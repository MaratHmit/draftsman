package ru.etalon5.draftsman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Config {
	static private String mSecret = "";	
	static private String mError = "";	
	static private String mToast = "";
	static private String mDBName;
	static int countRecs = 0;
	static boolean isFirstStart = true;
	static boolean isLocalMode = true;	
	static boolean isTest = false ;
	static boolean isCompressedRequest = true;
	static DataUser user = null;
	
	static public String getApiController(String apiName, String apiMethod) {		
		String s=null;
		try {
			String api = Consts.API_MAIN;
			if (isTest)
				api = Consts.API_MAIN_TEST;
			s = api + "/" + apiName + "/" + apiMethod + "?secret=" + URLEncoder.encode(mSecret, "UTF-8");
			if (Config.isCompressedRequest)
				s = s + "&compressed=1"; 
			
		} catch (UnsupportedEncodingException e) {	
			e.printStackTrace();
		}
		return s;
	}	
	
	public static String getDatabaseName() {
		return mDBName;
	}
	
	public static String getSecret() {
		return mSecret;
	}

	public static void setSecret(String mSecret) {
		Config.mSecret = mSecret;
	}

	public static String getError() {
		return mError;
	}

	public static void setError(String mError) {
		Config.mError = mError;
	}
	
	public static void clearError() {
		Config.mError = "";
	}
	
	public static String getToast() {
		return mToast;
	}

	public static void setToast(String mToast) {
		Config.mToast = mToast;
	}
	
	public static void clearToast() {
		Config.mToast = "";
	}	
	
	public static boolean isOnline(Activity activity) {
		 ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo nInfo = cm.getActiveNetworkInfo();
		 if (nInfo != null && nInfo.isConnected()) 
			 return true;		 
		 else return false;		 
	}	
	
	public static String md5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void showError(Activity activity) {
		showError(activity, activity.getString(R.string.app_name), Config.getError());
	}
	
	public static void showError(Activity activity, String title) {
		showError(activity, title, Config.getError());
	}
	
	public static void showError(Activity activity, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.ic_error);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});	
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	

	public static boolean isLandscapeMode(Activity activity)
	{
		Point size = new Point();		    
	    activity.getWindowManager().getDefaultDisplay().getSize(size);
	    int width = size.x;
	    int height = size.y;
	    return width > height;
	}
	
	public static int getCurrentYear()
    {
        java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault(), java.util.Locale.getDefault());
        calendar.setTime(new java.util.Date());
        return calendar.get(java.util.Calendar.YEAR);
    }
	
	public static String getCurrentDateAsString() {
		Date now = new Date();

		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
		String s = formatter.format(now);
		return s;
	}
	
	public static void setDBName(String dbName) {
		Config.mDBName = dbName;
	}
}
