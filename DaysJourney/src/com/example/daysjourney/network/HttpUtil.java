package com.example.daysjourney.network;

import java.util.Map;

import com.example.daysjourney.core.App;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * HTTP method 기능을 구현.
 * AsyncClient 사용.
 * 허니컴 버전부터는 메인쓰레드에서 Http요청을 할 경우 에러로 간주한다. 그래서 메인쓰레드에서 네트웍 작업을 하려면 비동기로 처리해야 한다. 
 * TODO 이게 무엇이고, 어떻게 사용하는 지 정리.
 * @author munkyusin
 *
 */
public class HttpUtil {
	
	private static AsyncHttpClient client;
	
	private static AsyncHttpClient getClient() {
		if(client == null){
			client = new AsyncHttpClient();
		}
		
		return client;
	}
	
	public static void get(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
		setHeaders(headers);
		App.log("GET :" + url);
		getClient().get(url, params, handler);
	}
	
	public static void post(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
		setHeaders(headers);
		App.log("POST : "+ url + "?" + params);
		getClient().post(url, params, handler);
	}
	
	public static void put(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
		setHeaders(headers);
		getClient().put(url, params, handler);
	}
	
	public static void delete(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
		setHeaders(headers);
		getClient().delete(null, url, null, params, handler);
	}
	
	public static void setHeaders(Map<String, String> headers) {
		if(headers == null){
			return;
		}
		
		for(Map.Entry<String, String> entry : headers.entrySet()) {
			getClient().addHeader(entry.getKey(), entry.getValue());
		}
	}
}
