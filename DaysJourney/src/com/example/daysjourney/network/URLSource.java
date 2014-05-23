package com.example.daysjourney.network;

import com.example.daysjourney.core.App;



public class URLSource {
	public static final String BASE_URL_DEVELOPMENT = "http://172.20.10.5:3000";
	//TODO 나중에 실서버에 올리면...
	public static final String BASE_URL_PRODUCTION = "";
	
	public static final String getBaseUrl() {
		if(App.SERVER_TARGET == App.SERVER_TEST) {
			return BASE_URL_DEVELOPMENT;
		} else {
			return BASE_URL_PRODUCTION;
		}
	}
	
	public static final String SIGN_UP = getBaseUrl() +"/users";
	public static final String SIGN_IN = getBaseUrl() + "/users/sign_in";
	public static final String PATHS = getBaseUrl() + "/users/%s/paths";
	public static final String DESTINATIONS = getBaseUrl() + "/paths/%s/destinations";
	public static final String DESTINATIONS_INFO = getBaseUrl() + "/destinations/%s";
	public static final String HOME_INFO = getBaseUrl() + "/destinations/%s/home";
	public static final String HOME_UPDATE = getBaseUrl() + "/destinations/%s";
/**
	기본 API를 기본으로 작성. 
    public static final String SIGN_UP = getBaseUrl() + "/users";
	public static final String SIGN_IN = getBaseUrl() + "/tokens";
	public static final String VOBBLES = getBaseUrl() + "/vobbles";
    public static final String VOBBLES_COUNT = getBaseUrl() + "/vobbles/count";
	public static final String VOBBLES_CREATE = getBaseUrl() + "/users/%s/vobbles";
    public static final String USER_INFO = getBaseUrl() + "/users/%s";
    public static final String USER_VOBBLES = getBaseUrl() + "/users/%s/vobbles";
    public static final String USER_VOBBLES_COUNT = getBaseUrl() + "/users/%s/vobbles/count";
	public static final String USER_VOBBLES_DELETE = getBaseUrl() + "/users/%s/vobbles/%s/delete";

    public static final String EVENTS = getBaseUrl() + "/events";
    public static final String MORE_APPS = "http://teamnexters.com/apps";
 */
	
}
