package com.example.daysjourney.entity;

import org.json.JSONObject;

/**
 * user_id 는 db 의 _id 값.
 * 
 * 
 * 확실하지 않은 부분. => 암호화된 비밀번호가 토큰일까?
 * @author munkyusin
 *
 */
public class User {
	
	public static final String USER_ID = "user_id"; 
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String USER_NAME = "user_name";
	public static final String ENCRYPTED_PASSWORD = "encrypted_passeword";
	
	private int userId;
	private String email;
	private String username;
	private String encryptedPassword;
	
	public static User build(JSONObject json) {
		if (json == null){
			return null;
		}
		
		User user = new User();
		user.userId = json.optInt(USER_ID);
		user.email = json.optString(EMAIL);
		user.username = json.optString(USER_NAME);
		user.encryptedPassword = json.optString(ENCRYPTED_PASSWORD);
		return user;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public String getEmail() {
        return email;
    }
	
	public String getUsername() {
		return username;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
}
