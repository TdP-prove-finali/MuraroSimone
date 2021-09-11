package it.polito.tdp.bagSharing;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.bagSharing.model.Model;
import it.polito.tdp.bagSharing.model.Struttura;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnRicerca;

    @FXML
    private ComboBox<String> boxProvincia;

    @FXML
    private ComboBox<String> boxDatabase;

    @FXML
    private CheckBox checkCC;

    @FXML
    private CheckBox checkAlim;

    @FXML
    private ComboBox<Struttura> boxStruttura;

    @FXML
    private TextField txtCostoFabbrica;

    @FXML
    private TextField txtCostoCliente;

    @FXML
    private TextField txtCostoGG;

    @FXML
    private TextField txtCostoLavaggio;

    @FXML
    private TextField txtDurata;
    
    @FXML
    private TextField txtIntervallo;

    @FXML
    private Button btnSimulazione;

    @FXML
    private TextArea txtResult;


    @FXML
    void doGrafo(ActionEvent event) {
    	
    	this.boxStruttura.getItems().clear();

    	if(this.boxDatabase.getValue()!= null && this.boxProvincia.getValue()!= null) {
   
    		model.creaGrafo(this.boxDatabase.getValue(),this.boxProvincia.getValue(),this.checkCC.isSelected(),this.checkAlim.isSelected());
    		this.txtResult.setText("GRAFO CREATO\nVertici: " + model.numeroVertici() + "\nArchi: " + model.numeroArchi()+"\n");
			this.boxStruttura.getItems().addAll(model.getCentriProvinciali());
    		
    		} else if(this.boxDatabase.getValue()== null) {
    			txtResult.setText("Selezionare dimensioni strutture \n");
    		} else if(this.boxProvincia.getValue()== null) {
    			txtResult.setText("Selezionare provincia in cui si vole effettuare la ricerca \n");
    		}      
    }
    
    @FXML
    void doRicerca(ActionEvent event) {
    	

    	if(model.getGrafo()!=null) {
    		
    		List <Struttura> res = model.camminoMinimo(this.boxStruttura.getValue());
    		
    		if (res.size()>0) {
    		
    		txtResult.appendText("Cammino trovato (peso = " + model.pesoCammino() + "):\n");
    		
    		for (Struttura s:res) 
    			txtResult.appendText(s + "\n");
    		
    		} else {
    			txtResult.setText("Cammino non trovato.");
    		}
    		
    	} else {
    		txtResult.setText("Scegliere l'area e creare il grafo prima di clacolare il percorso minimo");
    	}
    	
    }
    
    @FXML
    void doSimulazione(ActionEvent event) {
    	
    	if(!this.txtCostoFabbrica.getText().isEmpty() && !this.txtCostoCliente.getText().isEmpty() &&  !this.txtCostoGG.getText().isEmpty() && 
    			!this.txtCostoLavaggio.getText().isEmpty() && !this.txtDurata.getText().isEmpty() && !this.txtIntervallo.getText().isEmpty()) {
    		
    		if(model.getGrafo()!=null) {

    			model.simula(this.txtCostoFabbrica.getText(), this.txtCostoCliente.getText(), this.txtCostoGG.getText(), 
    							this.txtCostoLavaggio.getText(), this.txtDurata.getText(), this.txtIntervallo.getText());
    	
    			if(model.getCammino()!=null) {
    				
    				this.txtResult.setText("Costi "+model.getCostoProvincia()+"\nGuadagno: "+model.getGuadagnoProvincia()+
    						"\nNumero totale di sacchetti nuovi utilizzati: "+model.getnBagTotaliProvincia()+"\nTenendo conto dei costi di trasporto");
    				
    			} else {
    					
    				this.txtResult.setText("Costi "+model.getCostoProvincia()+"\nGuadagno: "+model.getGuadagnoProvincia()
    					+"\nNumero totale di sacchetti nuovi utilizzati"+model.getnBagTotaliProvincia()+"\nSenza costi costi di trasporto");
    				
    				}
    			} else {
    				this.txtResult.setText("Scegliere l'area e creare il grafo prima di procedere con la simulazione");
    			}
    		} else {
    			this.txtResult.setText("Inserire tutti i parametri della simulazione");
    	}
    }

    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnRicerca != null : "fx:id=\"btnRicerca\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxProvincia != null : "fx:id=\"boxProvincia\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxDatabase != null : "fx:id=\"boxDatabase\" was not injected: check your FXML file 'Scene.fxml'.";
        assert checkCC != null : "fx:id=\"checkCC\" was not injected: check your FXML file 'Scene.fxml'.";
        assert checkAlim != null : "fx:id=\"checkAlim\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxStruttura != null : "fx:id=\"boxStruttura\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtCostoFabbrica != null : "fx:id=\"txtCostoFabbrica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtCostoCliente != null : "fx:id=\"txtCostoCliente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtCostoGG != null : "fx:id=\"txtCostoGG\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtCostoLavaggio != null : "fx:id=\"txtCostoLavaggio\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtDurata != null : "fx:id=\"txtDurata\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtIntervallo != null : "fx:id=\"txtIntervallo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }



	public void setModel(Model model) {
		this.model = model;
		
		this.boxDatabase.getItems().add("GRANDI STRUTTURE");
		this.boxDatabase.getItems().add("MEDIE STRUTTURE");
		this.boxProvincia.getItems().addAll(model.getAllProvincie());
		this.boxProvincia.getItems().add("INTERA REGIONE");
	}
}
