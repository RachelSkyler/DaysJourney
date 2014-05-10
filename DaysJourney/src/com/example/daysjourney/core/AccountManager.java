package com.example.daysjourney.core;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.daysjourney.entity.User;

/**
 * Use Singleton pattern.
 * 
 * This class manage the account of the App's user.
 * @author munkyusin
 *
 */
public class AccountManager {
	private static AccountManager accountManager = new AccountManager();
	
	private AccountManager() {}
	
	public static AccountManager getInstance() {
		return accountManager;
	}
	
	public void signIn(Context context, User user) {
		//TODO call the methods such as setUserId(context, user.getUserId()+""); , setUsername, setEncryptedPassword
		setUserId(context, user.getUserId() +"");
		setUsername(context, user.getUsername());
		setEncrytedPassword(context, user.getEncryptedPassword());
	}
	
	public void signOut(Context context) {
		//TODO call the methods such as setUserId(context, "");
		setUserId(context, "");
		setUsername(context, "");
		setEncrytedPassword(context, "");
	}
	
	/**
	 * There are many useful methods for String in the TextUtils class.
	 * @param context
	 * @return
	 */
	public boolean isSignedIn(Context context) {
		return !TextUtils.isEmpty(getEncrytedPassword(context));
	}
	/**
	 * Using constant value, like USER_ID in User class
	 * @param context
	 * @return
	 */
	public String getUserId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(User.USER_ID, "");
	}
	
	public String getEncrytedPassword(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(User.ENCRYPTED_PASSWORD, "");
	}
	
	private void setUserId(Context context, String userId) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(User.USER_ID, userId).commit();
	}
	
	private void setUsername(Context context, String username){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(User.USER_NAME, username).commit();
	}
	
	private void setEncrytedPassword(Context context, String encryptedPassword){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(User.ENCRYPTED_PASSWORD, encryptedPassword).commit();
	}
}
