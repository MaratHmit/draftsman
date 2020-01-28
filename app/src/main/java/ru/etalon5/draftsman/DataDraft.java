package ru.etalon5.draftsman;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataDraft extends Data {
	private String mKeyMeasurement;	
	private String mDraft;	 
	private ArrayList<GraphicsItem> mObjects;
	private String mKeyCreatedUser;
	private String mKeyUpdatedUser;	
	
	public DataDraft() {
		
	}
	
	public DataDraft cloneInstanse() throws CloneNotSupportedException {
		DataDraft data = new DataDraft();
		copyInstance(data);
		return data;
	}
	
	protected void copyIntanse(DataDraft data) {
		super.copyInstance(data);
		data.mKeyMeasurement = mKeyMeasurement;
		data.mDraft = mDraft;
	}
	
	public DataDraft(Cursor cursor) {
		super(cursor);		
		int i;
		i = cursor.getColumnIndex("keyMeasurement");
		if (i >= 0)		
			mKeyMeasurement = cursor.getString(i);				
		i = cursor.getColumnIndex("draft");
		if (i >= 0)		
			mDraft = cursor.getString(i);
		setIsUnModified();
	}
	
	public DataDraft(JSONObject json) {
		super(json);		
	}
	 
	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		super.setFromJSONObject(json);
		if (json.has("keyMeasurement"))
			mKeyMeasurement = json.getString("keyMeasurement");
		if (json.has("draft") && (!json.getString("draft").equals("null")))
			mDraft = json.getString("draft");
		if (json.has("keyCreatedUser") && (!json.getString("keyCreatedUser").equals("null")))
			mKeyCreatedUser = json.getString("keyCreatedUser");
		if (json.has("keyUpdatedUser") && (!json.getString("keyUpdatedUser").equals("null")))
			mKeyUpdatedUser = json.getString("keyUpdatedUser");			
		setIsUnModified();
	}	
	
	public boolean isEmpty() {
		return (mObjects.size() == 0);
	}

	public String getKeyMeasurement() {
		return mKeyMeasurement;
	}

	public void setKeyMeasurement(String keyMeasurement) {
		mKeyMeasurement = keyMeasurement;
		setIsModified();
	}
	
	public String getDraft() {
		if (mDraft == null || mDraft == "null")
			mDraft = "";
		return mDraft;
	}

	public void setDraft(String draft) {
		setIsModified();
		mDraft = draft;
	}	
	
	public ArrayList<GraphicsItem> getGraphicsObjectsFromDraft() {
		if (mObjects == null)
			mObjects = new ArrayList<GraphicsItem>();
		
		mObjects.clear();
		if (mDraft == null || mDraft.isEmpty())
			return mObjects;
		
		try {
			JSONObject obj = new JSONObject(mDraft);
			if (obj.has("rects")) {
				JSONArray rects = obj.getJSONArray("rects");
				for (int i = 0; i < rects.length(); i++) {
					GraphicsRectItem item = new GraphicsRectItem(rects.getJSONObject(i));
					mObjects.add(item);
				}
			}
			if (obj.has("lines")) {
				JSONArray lines = obj.getJSONArray("lines");
				for (int i = 0; i < lines.length(); i++) {
					GraphicsLineItem item = new GraphicsLineItem(lines.getJSONObject(i));
					mObjects.add(item);
				}				
			}
			if (obj.has("texts")) {
				JSONArray texts = obj.getJSONArray("texts");
				for (int i = 0; i < texts.length(); i++) {
					GraphicsTextItem item = new GraphicsTextItem(texts.getJSONObject(i));
					mObjects.add(item);
				}				
			}
			if (obj.has("pathes")) {
				JSONArray paths = obj.getJSONArray("pathes");
				for (int i = 0; i < paths.length(); i++) {
					GraphicsPathItem item = new GraphicsPathItem(paths.getJSONObject(i));
					mObjects.add(item);
				}				
			}					
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return mObjects;
	}	
	
	public void refresfDraftFromGraphicsObjects() {
		JSONArray lines = new JSONArray();
		JSONArray rects = new JSONArray();
		JSONArray texts = new JSONArray();
		JSONArray pathes = new JSONArray();
		
		for (int i = 0; i < mObjects.size(); i++) 
			if (mObjects.get(i).isVisible) {
				if (mObjects.get(i) instanceof GraphicsLineItem)
					lines.put(mObjects.get(i).getAsJSONObject());
				if (mObjects.get(i) instanceof GraphicsRectItem)
					rects.put(mObjects.get(i).getAsJSONObject());
				if (mObjects.get(i) instanceof GraphicsTextItem)
					texts.put(mObjects.get(i).getAsJSONObject());
				if (mObjects.get(i) instanceof GraphicsPathItem)
					pathes.put(mObjects.get(i).getAsJSONObject());
			}		
		
		JSONObject obj = new JSONObject();
		try {			
			obj.put("lines", lines);
			obj.put("rects", rects);			
			obj.put("texts", texts);
			obj.put("pathes", pathes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		mDraft = obj.toString();		
	}
	
	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = super.getAsJSONObject();
		try {
			obj.put("keyMeasurement", mKeyMeasurement);
			obj.put("draft", mDraft);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return obj;
	}	
	
	public String getKeyCreatedUser() {
		return mKeyCreatedUser;
	}

	public void setKeyCreatedUser(String keyCreatedUser) {
		mKeyCreatedUser = keyCreatedUser;
	}

	public String getKeyUpdatedUser() {
		return mKeyUpdatedUser;
	}

	public void setKeyUpdatedUser(String keyUpdatedUser) {
		mKeyUpdatedUser = keyUpdatedUser;
	}
	
	
}
