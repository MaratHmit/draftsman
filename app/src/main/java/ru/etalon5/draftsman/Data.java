package ru.etalon5.draftsman;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class Data {	
	private String mKey;
	private String mName;	
	private String mNote;
	private boolean mIsModified;	
	private int mSortIndex;
    private int mStatus;
    private int mUpdatedAt;
    private int mCreatedAt;
    private boolean mIsDeleted;
	private boolean mIsSelected;
	
    public Data()
	{
		
	}
        
    public Data cloneInstanse() throws CloneNotSupportedException {
    	Data data = new Data();
    	copyInstance(data);
    	return data;
    }
    
    protected void copyInstance(Data data) {
    	data.mKey = mKey;
    	data.mName = mName;
    	data.mNote = mNote;
    	data.mIsModified = mIsModified;
    	data.mSortIndex = mSortIndex;
    	data.mStatus = mStatus;
    	data.mUpdatedAt = mUpdatedAt;
    	data.mCreatedAt = mCreatedAt;
    	data.mIsDeleted = mIsDeleted;    	
    }
    
	public Data(Cursor c) {
		int i;			
		i = c.getColumnIndex("key");
		if (i >= 0)
			mKey = c.getString(i);
		i = c.getColumnIndex("name");
		if (i >= 0)
			mName = c.getString(i);
		i = c.getColumnIndex("note");
		if (i >= 0)
			mNote = c.getString(i);		
		i = c.getColumnIndex("sortIndex");
		if (i >= 0)
			mSortIndex = c.getInt(i);
		i = c.getColumnIndex("status");
		if (i >= 0)
			mStatus = c.getInt(i);
		i = c.getColumnIndex("isDeleted");
		if (i >= 0)
			mIsDeleted = c.getInt(i) > 0;		
		i = c.getColumnIndex("updatedAt");
		if (i >= 0)
			mUpdatedAt = c.getInt(i);
		i = c.getColumnIndex("createdAt");
		if (i >= 0)
			mCreatedAt = c.getInt(i);		
	}
	
	public Data(JSONObject json) {
		try {
			setFromJSONObject(json);
		} catch (JSONException e) {		
			e.printStackTrace();
		}
	}
	
	public void setFromJSONObject(JSONObject json) throws JSONException {		
		if (json.has("key"))
			mKey = json.getString("key");		
		if (json.has("name"))
			mName = json.getString("name");
		if (json.has("note"))
			mNote = json.getString("note");
		if (json.has("sortIndex"))
			mSortIndex = json.getInt("sortIndex");		
		if (json.has("status"))
			mStatus = json.getInt("status");		
		if (json.has("updatedAt"))
			mUpdatedAt = json.getInt("updatedAt");		
		if (json.has("createdAt"))
			mCreatedAt = json.getInt("createdAt");		
	}	
	
	public boolean isEmpty() {
		return true;
	}
	
	public String getKey() {
		if (mKey == null)
			mKey = "";
		return mKey;
	}

	public void setKey(String key) {
		mKey = key;
		setIsModified();
	}

	public String getNote() {
		if (mNote == null || mNote =="null")
			mNote = "";
		return mNote;
	}

	public void setNote(String note) {
		setIsModified();
		mNote = note;
	}

	public boolean isModified() {		 
		return mIsModified;
	}

	public void setIsModified(boolean isModified) {
		mIsModified = isModified;
	}

	public void setIsModified() {
		mIsModified = true;
	}
	
	public void setIsUnModified() {
		mIsModified = false;
	}
	
	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		setIsModified();
		mSortIndex = sortIndex;
	}

	public String getName() {
		if (mName == null || mName == "null")
			mName = "";
		return mName;
	}

	public void setName(String name) {
		setIsModified();
		mName = name;
	}
	
	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		setIsModified();
		mStatus = status;
	}	
	
	public int getUpdatedAt() {		
		return mUpdatedAt;
	}

	public void setUpdatedAt(int updatedAt) {		
		mUpdatedAt = updatedAt;
	}
	
	public int getCreatedAt() {	
		return mCreatedAt;
	}

	public void setCreatedAt(int createdAt) {
		mUpdatedAt = createdAt;
	}	
	
	public boolean isDeleted() {
		return mIsDeleted;
	}
	
	public void setIsDeleted() {
		mIsDeleted = true;
		mIsModified = true;
	}
		
	public JSONObject getAsJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("key", mKey);
			obj.put("name", mName);
			obj.put("note", mNote);
			obj.put("sortIndex", mSortIndex);
			obj.put("status", mStatus);
			obj.put("isDeleted", mIsDeleted);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return obj;
	}
	
	public String getAsJSONString() {
		return getAsJSONObject().toString();
	}

	public boolean isSelected() {
		return mIsSelected;
	}

	public void setIsSelected(boolean isSelected) {
		mIsSelected = isSelected;
	}
}
