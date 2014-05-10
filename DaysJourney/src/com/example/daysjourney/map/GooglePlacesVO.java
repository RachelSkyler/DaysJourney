package com.example.daysjourney.map;

import java.io.Serializable;

public class GooglePlacesVO implements Serializable{
	
	private static final long serialVersionUID = 682329310825100721L;
	
	private String description;
	private String id;
	private String reference;
	private String name;
	private String address;
	private Double longitude;
	private Double latitude;
	
	public GooglePlacesVO() {
		super();
	}
	public GooglePlacesVO(String description, String id, String reference) {
		super();
		this.description = description;
		this.id = id;
		this.reference = reference;
	}
	public GooglePlacesVO(String reference, String name, String address,
			Double longitude, Double latitude) {
		super();
		this.reference = reference;
		this.name = name;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	@Override
	public String toString() {
		return "GooglePlacesVO [description=" + description + ", id=" + id
				+ ", reference=" + reference + ", name=" + name + ", address="
				+ address + ", longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
}
