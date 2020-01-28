package ru.etalon5.draftsman;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class DataPerson extends Data {
	
	private String mSurname;
	private String mPatronymic;

	public DataPerson() {		
	}

	public DataPerson(Cursor c) {
		super(c);		
	}

	public DataPerson(JSONObject json) {
		super(json);		
	}

	public String getSurname() {
		return mSurname;
	}

	public void setSurname(String surname) {
		mSurname = surname;
	}

	public String getPatronymic() {
		return mPatronymic;
	}

	public void setPatronymic(String patronymic) {
		mPatronymic = patronymic;
	}
	
	public void setFromJSONObject(JSONObject json) throws JSONException {	
		super.setFromJSONObject(json);	
		if (json.has("surname"))
			mSurname = json.getString("surname");		
		if (json.has("patronymic"))
			mPatronymic = json.getString("patronymic");				
	}
	
	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = super.getAsJSONObject();
		try {
			obj.put("surname", mSurname);
			obj.put("patronymic", mPatronymic);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}		

}
