package com.example.daysjourney.core;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.daysjourney.entity.Path;

/**
 * Today paths information need to maintain in whole Activities. 
 * @author munkyusin
 *
 */
public class PathManager {
	private static PathManager pathManager = new PathManager();
	
	private PathManager() {}
	
	public static PathManager getInstance() {
		return pathManager;
	}
	
	public void registerPath(Context context, Path path) {
		setPathId(context, path.getPathId());
		setCreatedAt(context, path.getCreatedAt());
	}
	
	public boolean isRegisteredPath(Context context) {
		return !TextUtils.isEmpty(getPathId(context));
	}
	
	public boolean isTodayPath(Context context) {
		//TODO 오늘 날짜와 createdAt 이 동일한지.
		boolean result = false;
		/**if(!TextUtils.isEmpty(getPathId(context)) && TextUtils.equals(getCreatedAt(context), b)) {
			result = true;
		}**/
		return result;
	}
	
	public String getPathId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(Path.PATH_ID, "");
	}
	
	public String getCreatedAt(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(Path.CREATED_AT, "");
	}
	
	private void setPathId(Context context, String pathId) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Path.PATH_ID, pathId).commit();
	}
	
	private void setCreatedAt(Context context, String createdAt) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Path.PATH_ID, createdAt).commit();
	}
}
