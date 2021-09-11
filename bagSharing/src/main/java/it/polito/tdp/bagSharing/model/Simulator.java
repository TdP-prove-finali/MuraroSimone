package it.polito.tdp.bagSharing.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import it.polito.tdp.bagSharing.model.Event.EventType;


public class Simulator {

	
		
		// Eventi
		private PriorityQueue<Event> queue ;
		
		// Parametri di simulazione
		
		private double costoSacchetti;   	   //costo sacchetti fabbrica/comprati dall'azienda bag-sharing
		private double costoSacchettiCliente;  //costo sacchetti venduti al cliente
		private double costoAffittoSacchetti;  //costo affitto per un scchetto per un giorno
		private double costoLavaggio;		   //costo lavaggio a sacchetto
		
		private int tIn;						//intervallo tra un cliente nuovo e quello successivo
		
		private LocalTime oraApertura = LocalTime.of(8, 0);
		private LocalTime oraChiusura = LocalTime.of(20, 0);
		private LocalDate giornoInizio;
		private LocalDate giornoFine;
		private LocalDateTime inizio;
		private LocalDateTime fine;

		
		
		// Stato del sistema
		private int nBag ; 				//Bag attualmente presenti
		private int nBagLavare ; 		//Bag da lavare
		
		// Misure in uscita
		private double guadagno;
		private double costo;
		private int nBagTot;			//numero di nuove borse utilizzate
		
		
		// Impostazione dei parametri iniziali
		
		public void init(double costoSacchetti, double costoSacchettiCliente, double costoAffittoSacchetti,double costoLavaggio,int durata,int intervallo) {
			
			this.costoSacchetti=costoSacchetti;
			this.costoSacchettiCliente=costoSacchettiCliente;
			this.costoAffittoSacchetti=costoAffittoSacchetti;
			this.costoLavaggio=costoLavaggio;
			this.giornoInizio=LocalDate.now();
			this.giornoFine=giornoInizio.plus(durata, ChronoUnit.DAYS);
			this.inizio=LocalDateTime.of(giornoInizio, oraApertura);
			this.fine=LocalDateTime.of(giornoFine, oraChiusura);
			this.tIn=intervallo;
		}
		
		
		
		// Simulazione
		public void run() {
			this.queue = new PriorityQueue<Event>() ;

			// Stato iniziale
			this.nBag = 0 ;
			this.nBagLavare=0;
			this.costo=0.00;
			this.guadagno=0.00;
			
			// Eventi iniziali
			LocalDateTime momento = this.inizio ;
			while(momento.isBefore(this.fine)) {     	
				
				if(momento.getHour()>=this.oraApertura.getHour() && momento.getHour()<this.oraChiusura.getHour()) {  
					this.queue.add(new Event(momento, EventType.NUOVO_CLIENTE,this.getNCasualeBag(),0)) ;
					momento = momento.plus(this.tIn, ChronoUnit.MINUTES) ;
				}
				 else {
					this.queue.add(new Event(momento.plus(5, ChronoUnit.MINUTES), EventType.LAVAGGIO,0,0)) ;
					momento= momento.plus(12, ChronoUnit.HOURS);
				}
			}
			
			// Ciclo di simulazione
			while(!this.queue.isEmpty()) {
				Event e = this.queue.poll();
				processEvent(e) ;	
			}
		}
		
		
		 // processEvent
		private void processEvent(Event e) {
			switch(e.getType()) {
			
			case NUOVO_CLIENTE:
					double num = Math.random()*2 ;  // [0, 2)
					if (num<1.0) {					//clienti che accettano di far parte del progetto bagSharing (50% dei nuovi clienti)
						this.iscrittoBagSharing(e);		
						}							// restanti 50% non accettano bagSharing
				break ;	
				
			case CLIENTE_ABITUALE:
				this.iscrittoBagSharing(e);		//tutti i clienti abituali sono iscritti al progetto BagSharing
				break ;	
				
			case RESTITUZIONE:
				double costoClienteAffitto=0.00;
				costoClienteAffitto=e.getnGG()*this.costoAffittoSacchetti;
				if(costoClienteAffitto< this.costoSacchettiCliente) {  //se il costo dell'affitto non supera il costo del sacchetto
					double costoDaSostenere=this.costoSacchettiCliente-costoClienteAffitto;
					costo+= costoDaSostenere*e.getnBagPreso();
					this.nBagLavare+= e.getnBagPreso();
					this.queue.add(new Event(e.getTime().plus(5,ChronoUnit.MINUTES), EventType.CLIENTE_ABITUALE, this.getNCasualeBag(), 0));  //nuovo affitto
				}     // altrimenti il cliente si tiene il sacchetto senza fare il reso
				break ;	
				
			case LAVAGGIO:
				this.nBagLavare-=this.nBagLavare*0.02;		//il 2% delle buste restituite sono logore e vanno buttate
				costo+=this.nBagLavare*this.costoLavaggio;
				this.nBag+=this.nBagLavare;    				//si aggiungono tutte le bag lavate alle bag disponibili
				this.nBagLavare=0;							//alla fine del lavaggio non ci sono più bag da lavare
				break ;
			}
		}
		
		
	
		private void vendiSacchetto(int numeroSacchettiPreso) {
			
			if(this.nBag>=numeroSacchettiPreso) {  						// se disponibilità bag sufficiente non devo comprare una nuova bag
				 guadagno=guadagno+(costoSacchettiCliente*numeroSacchettiPreso);   //ho solo guadagno e tolgo una bag da quelle disponibili
				 nBag--;

			 } else {
				 if(this.nBag==0) {   // controllo disponibilità bag, se =0, utilizzo una busta nuova 
				 costo=costo+(costoSacchetti*numeroSacchettiPreso); 
				 guadagno=guadagno+(costoSacchettiCliente*numeroSacchettiPreso);
				 nBagTot+= numeroSacchettiPreso;      //aumento il numero di nuove borse utilizzate
				 	} else {  												//alcune bag disponibili/alcune bag da comprare
				 		int numeroSacchettiNonDisponibile=numeroSacchettiPreso-nBag;
				 		costo=costo+(costoSacchetti*numeroSacchettiNonDisponibile); 
						 guadagno=guadagno+(costoSacchettiCliente*numeroSacchettiPreso);
						 nBagTot+= numeroSacchettiNonDisponibile;
						 nBag=0;
				 	}
				 }
			
		}
		
		
		private void iscrittoBagSharing(Event e) {
			
			double numSi = Math.random()*2 ;
			
			if(numSi<1.0) {									//50% dei clienti compra la busta e non la vuole affittare
				this.vendiSacchetto(e.getnBagPreso());    
			} else {										//50% dei clienti affitta bag 
				double numGG = Math.random()*100 ;
				
				if(numGG<10) {								//10% la restituisce entro 2 giorni
					
					this.vendiSacchetto(e.getnBagPreso());
					if(e.getTime().plus(2, ChronoUnit.DAYS).isBefore(fine))
					this.queue.add(new Event(e.getTime().plus(2, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),2)); 
				
				} else {
					if(numGG<25) {							//15% la restituisce entro 3 giorni
						this.vendiSacchetto(e.getnBagPreso());
						if(e.getTime().plus(3, ChronoUnit.DAYS).isBefore(fine))
						this.queue.add(new Event(e.getTime().plus(3, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),3));
					} else {
						if(numGG<45) {						//20% la restituisce dopo 4 giorni
							this.vendiSacchetto(e.getnBagPreso());
							if(e.getTime().plus(4, ChronoUnit.DAYS).isBefore(fine))
							this.queue.add(new Event(e.getTime().plus(4, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),4));
						} else {
							if(numGG<65) {
								this.vendiSacchetto(e.getnBagPreso());
								if(e.getTime().plus(5, ChronoUnit.DAYS).isBefore(fine))
								this.queue.add(new Event(e.getTime().plus(5, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),5));
							} else {
								if(numGG<80) {
									this.vendiSacchetto(e.getnBagPreso());
									if(e.getTime().plus(6, ChronoUnit.DAYS).isBefore(fine))
									this.queue.add(new Event(e.getTime().plus(6, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),6));
								} else {
									if(numGG<90) {
										this.vendiSacchetto(e.getnBagPreso());
										if(e.getTime().plus(7, ChronoUnit.DAYS).isBefore(fine))
										this.queue.add(new Event(e.getTime().plus(7, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),7));
									} else {
										if(numGG<95) {
											this.vendiSacchetto(e.getnBagPreso());
											if(e.getTime().plus(8, ChronoUnit.DAYS).isBefore(fine))
											this.queue.add(new Event(e.getTime().plus(8, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),8));
										} else {
											if(numGG<100) {
												this.vendiSacchetto(e.getnBagPreso());
												if(e.getTime().plus(9, ChronoUnit.DAYS).isBefore(fine))
												this.queue.add(new Event(e.getTime().plus(9, ChronoUnit.DAYS),EventType.RESTITUZIONE,e.getnBagPreso(),9));
											} else {
												
												}
											}
										}
									}
								}
							}
						}
				}
			}
		}
		
		
		
		public int getNCasualeBag() {    		//Per trovare un numero casuale tra [0,4]
			return (int)(Math.random()*4)+1 ;	//solitamente i clienti prendono massimo 4 borse
		}
		
		//metodi get per passare i valori al model
		
		public double getCostoStruttura() {
			//int num = (int)(Math.random()*3)+1;
			return this.costo;
		}
		
		public double getGuadagnoStruttura() {
			//int num = (int)(Math.random()*3)+1;
			return this.guadagno;
		}

		public int getnBagTotaliStruttura() {
			//int num = (int)(Math.random()*3)+1;
			return this.nBagTot ;
		}
		
			
	
}
