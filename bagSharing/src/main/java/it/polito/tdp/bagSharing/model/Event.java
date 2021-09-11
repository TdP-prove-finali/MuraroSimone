package it.polito.tdp.bagSharing.model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event> {

	
	public enum EventType {
		NUOVO_CLIENTE,
		CLIENTE_ABITUALE,
		RESTITUZIONE,
		LAVAGGIO
	}
	
	private LocalDateTime time ;
	private EventType type ;
	private Integer nBagPreso;
	private Integer nGG;
	
	@Override
	public int compareTo(Event other) {
		return this.time.compareTo(other.time) ;
	}

	public Event(LocalDateTime time, EventType type,int nBagPreso,int nGG) {
		super();
		this.time = time;
		this.type = type;
		this.nBagPreso=nBagPreso;
		this.nGG=nGG;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Integer getnBagPreso() {
		return nBagPreso;
	}

	public void setnBagPreso(Integer nBagPreso) {
		this.nBagPreso = nBagPreso;
	}

	public Integer getnGG() {
		return nGG;
	}

	public void setnGG(Integer nGG) {
		this.nGG = nGG;
	}

	@Override
	public String toString() {
		return "Event [time=" + time + ", type=" + type + "]";
	}
	
}
