package com.example.daysjourney.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Path implements Serializable {
	
	private static final long serialVersionUID = -701683628527464593L;
	public static final String PATH_ID = "path_id";
	public static final String CREATED_AT = "created_at";
	
	private String pathId;
	private String userId;
	private String createdAt;
	
	public static Path build(JSONObject json) {
		if (json == null) {
			return null;
		}
		
		Path path = new Path();
		
		JSONObject pOid, uOid;
		try {
			pOid = json.getJSONObject(PATH_ID);
			path.pathId = pOid.optString(User.OBJECT_ID);
			uOid = json.getJSONObject(User.USER_ID);
			path.userId = uOid.optString(User.OBJECT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		path.createdAt = json.optString(CREATED_AT);
		//TODO : 어떤 형식으로 넘어오는지 ...
		System.out.println(path.createdAt);
		return path;
	}

	public String getPathId() {
		return pathId;
	}

	public String getUserId() {
		return userId;
	}

	public String getCreatedAt() {
		return createdAt;
	}
}
