package it.polito.tdp.bagSharing.model;

public class Struttura {

	private String id;
	private String provincia;
	private String comune;
	private String indirizzo ;
	private String location;
	private double lat;
	private double lng;
	
	public Struttura(String id, String provincia, String comune, String indirizzo, String location) {
		super();
		this.id = id;
		this.provincia = provincia;
		this.comune = comune;
		this.indirizzo = indirizzo;
		this.location = location;
		String[] latlng = location.split(" ");
		this.lng=Double.parseDouble(latlng[0]);
		this.lat=Double.parseDouble(latlng[1]);
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLng() {
		return lng;
	}


	public void setLng(double lng) {
		this.lng = lng;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Struttura other = (Struttura) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Struttura [id=" + id + ", provincia=" + provincia + ", comune=" + comune + ", indirizzo=" + indirizzo + "]";
	}
}
