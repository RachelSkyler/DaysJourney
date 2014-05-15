package com.example.daysjourney.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Destination implements Serializable {
	private static final long serialVersionUID = 682329310825100721L;
	
	public static final String DESTINATION_ID = "destination_id";
	public static final String DESCRIPTION = "description";
	public static final String REFERENCE = "reference";
	public static final String NAME = "location_name";
	public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
	
	private String destinationId;
	private String pathId;
	private String description;
	private String reference;
	private String name;
	private String address;
	private double longitude;
	private double latitude;
	
	public static Destination build(JSONObject json) {
		if (json == null) {
			return null;
		}
		
		Destination destination = new Destination();
		JSONObject pOid, uOid,dOid;
		try {
			pOid = json.getJSONObject(Path.PATH_ID);
			destination.pathId = pOid.optString(User.OBJECT_ID);
			dOid = json.getJSONObject(DESTINATION_ID);
			destination.destinationId = dOid.optString(User.OBJECT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		destination.description = json.optString(DESCRIPTION);
		destination.reference = json.optString(REFERENCE);
		destination.name = json.optString(NAME);
		destination.latitude = json.optDouble(LATITUDE);
		destination.longitude = json.optDouble(LONGITUDE);
		return destination;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public String getPathId() {
		return pathId;
	}

	public String getDescription() {
		return description;
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getName() {
		return name;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
