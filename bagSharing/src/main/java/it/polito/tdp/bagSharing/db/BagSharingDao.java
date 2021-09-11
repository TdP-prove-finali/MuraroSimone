package it.polito.tdp.bagSharing.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import it.polito.tdp.bagSharing.model.Struttura;


public class BagSharingDao {
	
	
	public List<String> getAllProvincie(){
		String sql = "SELECT DISTINCT Provincia FROM grandi_strutture";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

			
				result.add(res.getString("Provincia"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Struttura> getAllVerticiGrandi(String provincia,boolean CC, boolean supAlim){
		
		String sql = "SELECT DISTINCT ID_PDV,Provincia,Comune,Indirizzo,location FROM grandi_strutture WHERE Provincia=?";
		if(CC && supAlim) {
			 sql = "SELECT DISTINCT ID_PDV,Provincia,Comune,Indirizzo,location FROM grandi_strutture WHERE Provincia=? AND CC=? AND SupAlim=? ";
		} else { if(CC) {
				 sql = "SELECT DISTINCT ID_PDV,Provincia,Comune,Indirizzo,location FROM grandi_strutture WHERE Provincia=? AND CC=? ";
			} else { if(supAlim) {
					 sql = "SELECT DISTINCT ID_PDV,Provincia,Comune,Indirizzo,location FROM grandi_strutture WHERE Provincia=? AND SupAlim=?";
				}
			}
		}
		List<Struttura> result = new ArrayList<Struttura>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, provincia);

				if(CC  && supAlim) {
					st.setString(2, "SI");
					st.setString(3, "0.00");
				} else if(CC) 
						st.setString(2, "SI");
						 else if(supAlim)
							st.setString(2, "0.00");
						
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(new Struttura(res.getString("ID_PDV"),res.getString("Provincia"),res.getString("Comune"),res.getString("Indirizzo"),res.getString("location")));
			}
			res.close();
			st.close();
			conn.close();
			return result;	
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Struttura> getAllVerticiMedie(String provincia,boolean CC, boolean supAlim){
		String sql = "SELECT DISTINCT ID,Provincia,Comune,Indirizzo,location FROM medie_strutture WHERE Provincia=?";
		
		if(CC  && supAlim) {
			 sql = "SELECT DISTINCT ID,Provincia,Comune,Indirizzo,location FROM medie_strutture WHERE Provincia=? AND CC=? AND SupAlim=? ";
		} else {
			if(CC) {
				 sql = "SELECT DISTINCT ID,Provincia,Comune,Indirizzo,location FROM medie_strutture WHERE Provincia=? AND CC=? ";
			} else {
				if(supAlim) {
					 sql = "SELECT DISTINCT ID,Provincia,Comune,Indirizzo,location FROM medie_strutture WHERE Provincia=? AND SupAlim=?";
				}
			}
		}
		
		List<Struttura> result = new ArrayList<Struttura>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, provincia);
			
			if(CC  && supAlim) {
				st.setString(2, "SI");
				st.setString(3, "0.00");
			} else if(CC) 
					st.setString(2, "SI");
					 else if(supAlim)
						st.setString(2, "0.00");
			
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(new Struttura(res.getString("ID"),res.getString("Provincia"),res.getString("Comune"),res.getString("Indirizzo"),res.getString("location")));
			
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
}
