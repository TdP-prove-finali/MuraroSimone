package it.polito.tdp.bagSharing.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.bagSharing.db.BagSharingDao;


public class Model {
	
	BagSharingDao dao = new BagSharingDao();

	
	private List<Struttura> strutture;   //lista di strutture che costituiranno i vertici del grafo per la provincia
	List<Struttura> centriProvinciali;	//lista di strutture che costituiranno i vertici del grafo regionale
	
	private Graph<Struttura,DefaultWeightedEdge> grafo;
	
	
	private List<Struttura> camminoMinimo;    //lista di strutture per salvare percorso costo minimo
	private double pesoMinimo;				  // peso del cammino minimo
	
	
	//SIMULAZIONE
	private double costoProvincia;
	private double guadagnoProvincia;
	private int nBagTotProvincia;

	
	public void creaGrafo(String database,String provincia, boolean CC,boolean supAlim) {     //grafo per le provincie
		
		if(provincia.equals("INTERA REGIONE")) {
			this.creaGrafoRegione(database, provincia, CC, supAlim);
			} else {
				this.creaGrafoProvincia(database, provincia, CC, supAlim);
		}
		
	}
	
	public void creaGrafoRegione(String database,String provincia, boolean CC,boolean supAlim) {    //grafo per la regione
		
		centriProvinciali= new ArrayList<Struttura>();         //lista contenente struttura di partenza nel cammino migliore
		
		for(String s: getAllProvincie()) {					   //Per ogni provincia 
			
			 creaGrafo(database,s,CC,supAlim);				   // creo il grafo
			 List<Struttura> tmp=camminoMinimo(null);		   // cerco cammino minimo
			
			 if(tmp.size()>0)								  //se trova un cammino minimo
				 centriProvinciali.add(tmp.get(0));			  //aggiungo la strttura di partenza del cammino minimo
		}
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, centriProvinciali);
		
			for(Struttura s1: centriProvinciali) {
				for(Struttura s2: centriProvinciali) {
					if(!s1.equals(s2)) {
						Graphs.addEdge(grafo, s1, s2, distance(s1.getLat(),s2.getLat(),s1.getLng(),s2.getLng()));
					}
				}
			}
	}
	
	
	public void creaGrafoProvincia(String database,String provincia, boolean CC,boolean supAlim) {     //grafo per le provincie
		
		if(database.equals("GRANDI STRUTTURE"))									//2 metodi diversi per interrogare il database poichè non corrispndevano alcunni parametri
			this.strutture= dao.getAllVerticiGrandi(provincia,CC,supAlim);
		
		if(database.equals("MEDIE STRUTTURE"))
			this.strutture= dao.getAllVerticiMedie(provincia,CC,supAlim);
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, strutture);   								//creazione grafo--> vertici presi dalla lista Strutture ottenuta interrogando il database
		
	
		for(Struttura s1: strutture) {											//creazione archi grafo
			for(Struttura s2: strutture) {
				if(!s1.equals(s2)) {											// peso dell'arco ottenuto tramite il metodo distance(lat1,lat2,lng1,lng2)
					Graphs.addEdge(grafo, s1, s2, distance(s1.getLat(),s2.getLat(),s1.getLng(),s2.getLng()));  
				}
			}
		}
		
	}
	
    public static double distance(double lat1,double lat2, double lon1, double lon2) {

    	// The math module contains a function
    	// named toRadians which converts from
    	// degrees to radians.
    	lon1 = Math.toRadians(lon1);
    	lon2 = Math.toRadians(lon2);
    	lat1 = Math.toRadians(lat1);
    	lat2 = Math.toRadians(lat2);

    	// Haversine formula
    	double dlon = lon2 - lon1;
    	double dlat = lat2 - lat1;
    	double a = Math.pow(Math.sin(dlat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.pow(Math.sin(dlon / 2),2);
    
    	double c = 2 * Math.asin(Math.sqrt(a));

    	// Radius of earth in kilometers. Use 3956
    	// for miles
    	double r = 6371;

    	// calculate the result
    	return(c * r);
    }
	
	
	
	
	public List<Struttura> camminoMinimo(Struttura partenza){  
		
		this.camminoMinimo = new ArrayList<Struttura>();
		this.pesoMinimo = 1000000.00;
		
		
		List<Struttura> parziale = new ArrayList<Struttura>();
									//Inserimento Struttura
		if(partenza==(null)) {		//provo tutte le strutture come partenza	
			for(Struttura s: grafo.vertexSet()) {   
				parziale.add(s);
				recursive(parziale,0.00);
			
				parziale.clear();
			}
		} else {					//seleziono struttura di partenza
				parziale.add(partenza);
				recursive(parziale,0);
		}
		
		
		return this.camminoMinimo;
		
	}
	
	
	private void recursive(List<Struttura> parziale, double pesoParziale) {
		
		if(parziale.size()==grafo.vertexSet().size()) {     	//caso terminale
			if(pesoParziale < this.pesoMinimo) {
			
				this.pesoMinimo = pesoParziale;
				this.camminoMinimo = new ArrayList<Struttura>(parziale);
			
			}
		}
		
		     //caso Intermedio
		for (Struttura s:Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			
			double pesoAggiuntivo = this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), s));
			
			if (!parziale.contains(s)) {
		
				pesoParziale += pesoAggiuntivo;
				parziale.add(s);
				recursive(parziale,pesoParziale);
				
				pesoParziale -= pesoAggiuntivo;
				parziale.remove(s);								
			}									
		}			
		
	}
	
	public void simula(String costoSacchetti, String costoSacchettiCliente, String costoAffittoSacchetti,String costoLavaggio,String durata,String intervallo) {
		
		Simulator sim=new Simulator();
		this.costoProvincia=0.00;
		this.guadagnoProvincia=0.00;
		this.nBagTotProvincia=0;
		double costoTrasporto=0.00;
		costoTrasporto= (this.pesoCammino()*.10);
		
		try {
			 double cS=Double.parseDouble(costoSacchetti);
			 double cSC= Double.parseDouble(costoSacchettiCliente);
			 double cAS= Double.parseDouble(costoAffittoSacchetti);
			 double cL=Double.parseDouble(costoLavaggio);
			 int duration=Integer.parseInt(durata);
			 int interval= Integer.parseInt(intervallo);
			 
			 for(int i=0;i<=grafo.vertexSet().size();i++) {
					sim.init(cS,cSC,cAS,cL,duration, interval);
					sim.run();
					
					this.costoProvincia+=sim.getCostoStruttura();
					this.guadagnoProvincia+=sim.getGuadagnoStruttura();
					this.nBagTotProvincia+= sim.getnBagTotaliStruttura();
				}
				
					this.costoProvincia+=(costoTrasporto*duration*2);  // costo una tratta*giorniSimulazione*2 viaggi al giorno
					
		} catch(Exception e) {
            System.out.println("Exception: " + e);
        }
		
	}

	public List<Struttura> getStrutture() {
		return strutture;
	}
	
	public List<Struttura> getCentriProvinciali() {
		List<Struttura> tmp=new ArrayList<Struttura>();
		for(Struttura s: grafo.vertexSet())
			tmp.add(s);
		return tmp;
	}


	public int numeroVertici() {
		// TODO Auto-generated method stub
		return grafo.vertexSet().size();
	}


	public int numeroArchi() {
		// TODO Auto-generated method stub
		return grafo.edgeSet().size();
	}

	public double pesoCammino() {
		// TODO Auto-generated method stub
		return this.pesoMinimo;
	}

	public List<String> getAllProvincie() {
		// TODO Auto-generated method stub
		return dao.getAllProvincie() ;
	}
	
	
	
	public double getCostoProvincia() {
		return this.costoProvincia;
	}
	
	public double getGuadagnoProvincia() {
		return this.guadagnoProvincia;
	}
	
	public Graph<Struttura,DefaultWeightedEdge> getGrafo() {
		return this.grafo;
	}
	
	public List<Struttura> getCammino(){
		return this.camminoMinimo;
	}

	public int getnBagTotaliProvincia() {
		//int num = (int)(Math.random()*3)+1;
		return this.nBagTotProvincia;
	}
	
	
	
}