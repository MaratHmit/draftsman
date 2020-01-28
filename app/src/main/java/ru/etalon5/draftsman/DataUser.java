package ru.etalon5.draftsman;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class DataUser extends DataPerson {
	
	private String mAccount;
	private String mLogin;
	private String mPassword;
	private boolean mIsRoot;
	private boolean mIsActive;

	public DataUser() {		
		
	}

	public DataUser(Cursor c) {
		super(c);		
	}

	public DataUser(JSONObject json) {
		super(json);		
	}
	
	public boolean auth() {
		return Api.userAuth(this);
	}
	
	public boolean reg() {
		return Api.userReg(this);
	}
	
	public void setFromJSONObject(JSONObject json) throws JSONException {	
		super.setFromJSONObject(json);	
		if (json.has("account"))
			mAccount = json.getString("account");		
		if (json.has("login"))
			mLogin = json.getString("login");				
		if (json.has("password"))
			mPassword = json.getString("password");		
		if (json.has("isActive"))
			mIsActive = json.getBoolean("isActive");
		mIsRoot = mLogin.equals(Consts.LOCAL_ACCOUNT);			
	}
	
	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = super.getAsJSONObject();
		try {
			obj.put("account", mAccount);
			obj.put("login", mLogin);
			obj.put("password", mPassword);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}			

	public String getAccount() {
		System.out.println(mAccount);
		return mAccount;
	}

	public void setAccount(String account) {
		mAccount = account;
	}

	public String getLogin() {
		return mLogin;
	}

	public void setLogin(String login) {
		mLogin = login;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public boolean getIsRoot() {
		return mIsRoot;
	}

	public void setIsRoot(boolean isRoot) {
		mIsRoot = isRoot;
	}

	public boolean getIsActive() {
		return mIsActive;
	}

	public void setIsActive(boolean isActive) {
		mIsActive = isActive;
	}	

}
