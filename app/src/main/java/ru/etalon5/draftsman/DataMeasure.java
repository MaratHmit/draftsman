package ru.etalon5.draftsman;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class DataMeasure extends Data {
	 private int mNum;
	 private int mYearDoc;
	 private String mDateDoc;
	 private String mKeyCustomer;
	 private String mCustomer;
	 private String mAddress;
	 private String mPhones;
	 private String mObjectWork;
	 private String mDateWork;
	 private String mTimeWork;
	 private String mDesirableTime;	 
	 private String mKeyCreatedUser;
	 private String mKeyUpdatedUser;
	 private String mOffice;	 
	 private int mCountDrafts;
	 private ArrayList<DataDraft> mListDrafts;
	

	public DataMeasure() {
		initialization(); 
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
		DataMeasure result = (DataMeasure) super.clone();
    	return result;
    }
	
	public DataMeasure(JSONObject json) {		
		initialization();
		try {
			setFromJSONObject(json);
		} catch (JSONException e) {		
			e.printStackTrace();
		}
	}
	
	private void initialization() {
		mListDrafts = new ArrayList<DataDraft>();
		mNum = 0;
		mYearDoc = 0;
		mDateDoc = "";
		mKeyCustomer = "";
		mCustomer = "";
		mAddress = "";
		mPhones = "";
		mObjectWork = "";
		mDateWork = "";
		mTimeWork = "";
		mDesirableTime = "";	 
		mKeyCreatedUser = "";
		mKeyUpdatedUser = "";
		mOffice = "";	 
	}
	
	public DataMeasure(Cursor cursor) {
		super(cursor);
		mListDrafts = new ArrayList<DataDraft>(); 
		int i;
		i = cursor.getColumnIndex("num");
		if (i >= 0)		
			mNum = cursor.getInt(i);
		i = cursor.getColumnIndex("yearDoc");
		if (i >= 0)
			mYearDoc = cursor.getInt(i);
		i = cursor.getColumnIndex("dateDoc");
		if (i >= 0)
			mDateDoc = cursor.getString(i);
		i = cursor.getColumnIndex("customer");
		if (i >= 0)
			mCustomer = cursor.getString(i);
		i = cursor.getColumnIndex("address");
		if (i >= 0)
			mAddress = cursor.getString(i);		
		i = cursor.getColumnIndex("phones");
		if (i >= 0)
			mPhones = cursor.getString(i);		
		i = cursor.getColumnIndex("objectWork");
		if (i >= 0)
			mObjectWork = cursor.getString(i);
		i = cursor.getColumnIndex("desirableTime");
		if (i >= 0)
			mDesirableTime = cursor.getString(i);			
		i = cursor.getColumnIndex("dateWork");
		if (i >= 0)
			mDateWork = cursor.getString(i);		
		i = cursor.getColumnIndex("timeWork");
		if (i >= 0)
			mTimeWork = cursor.getString(i);
		i = cursor.getColumnIndex("keyCreatedUser");
		if (i >= 0)
			mKeyCreatedUser = cursor.getString(i);			
		i = cursor.getColumnIndex("keyUpdatedUser");
		if (i >= 0)
			mKeyUpdatedUser = cursor.getString(i);
		i = cursor.getColumnIndex("countDrafts");
		if (i >= 0)
			mCountDrafts = cursor.getInt(i);		
		setIsUnModified();
	}
	
	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		super.setFromJSONObject(json);		
		if (json.has("num"))
			mNum = json.getInt("num");
		if (json.has("yearDoc"))
			mYearDoc = json.getInt("yearDoc");
		if (json.has("dateDoc") && (!json.getString("dateDoc").equals("null")))
			mDateDoc = json.getString("dateDoc");
		if (json.has("keyCustomer") && (!json.getString("keyCustomer").equals("null")))
			mKeyCustomer = json.getString("keyCustomer");
		if (json.has("customer") && (!json.getString("customer").equals("null")))
			mCustomer = json.getString("customer");		
		if (json.has("address") && (!json.getString("address").equals("null")))
			mAddress = json.getString("address");
		if (json.has("phones") && (!json.getString("phones").equals("null")))
			mPhones = json.getString("phones");
		if (json.has("objectWork") && (!json.getString("objectWork").equals("null")))
			mObjectWork = json.getString("objectWork");		
		if (json.has("desirableTime") && (!json.getString("desirableTime").equals("null")))
			mDesirableTime = json.getString("desirableTime");		
		if (json.has("dateWork") && (!json.getString("dateWork").equals("null")) && (!json.getString("dateWork").equals("30.11.-0001")))
			mDateWork = json.getString("dateWork");
		if (json.has("timeWork") && (!json.getString("timeWork").equals("null")))
			mTimeWork = json.getString("timeWork");				
		if (json.has("keyCreatedUser") && (!json.getString("keyCreatedUser").equals("null")))
			mKeyCreatedUser = json.getString("keyCreatedUser");
		if (json.has("keyUpdatedUser") && (!json.getString("keyUpdatedUser").equals("null")))
			mKeyUpdatedUser = json.getString("keyUpdatedUser");		
		if (json.has("office") && (!json.getString("office").equals("null")))
			mOffice = json.getString("office");
		mListDrafts.clear();		
		if (json.has("drawings")) {
			JSONArray draftsArray = json.getJSONArray("drawings");			
			for (int i = 0; i < draftsArray.length(); i++) {
				DataDraft draft = new DataDraft(draftsArray.getJSONObject(i));
				draft.setKeyMeasurement(getKey());
				mListDrafts.add(draft);
			}
		}	
	}
	
	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = super.getAsJSONObject();
		try {
			obj.put("num", mNum);
			obj.put("yearDoc", mYearDoc);
			obj.put("dateDoc", mDateDoc);
			if (mKeyCustomer != null && !mKeyCustomer.isEmpty())
				obj.put("keyCustomer", mKeyCustomer);
			obj.put("customer", mCustomer);
			obj.put("address", mAddress);
			obj.put("phones", mPhones);
			obj.put("objectWork", mObjectWork);
			obj.put("desirableTime", mDesirableTime);
			obj.put("dateWork", mDateWork);
			if (mKeyCreatedUser != null && !mKeyCreatedUser.isEmpty() && !mKeyCreatedUser.contentEquals(Consts.LOCAL_ACCOUNT))
				obj.put("keyCreatedUser", mKeyCreatedUser);
			if (mKeyUpdatedUser != null && !mKeyUpdatedUser.isEmpty() && !mKeyUpdatedUser.contentEquals(Consts.LOCAL_ACCOUNT))
				obj.put("keyUpdatedUser", mKeyUpdatedUser);		
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return obj;
	}		

	public int getNum() {
		return mNum;
	}

	public void setNum(int num) {
		mNum = num;
		setIsModified();
	}

	public int getYearDoc() {
		return mYearDoc;
	}

	public void setYearDoc(int yearDoc) {
		mYearDoc = yearDoc;
		setIsModified();
	}

	public String getDateDoc() {
		return mDateDoc;
	}

	public void setDateDoc(String dateDoc) {
		mDateDoc = dateDoc;
		setIsModified();
	}

	public String getKeyCustomer() {
		return mKeyCustomer;
	}

	public void setKeyCustomer(String keyCustomer) {
		mKeyCustomer = keyCustomer;
		setIsModified();
	}

	public String getCustomer() {
		return mCustomer;
	}

	public void setCustomer(String customer) {
		mCustomer = customer;
		setIsModified();
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		mAddress = address;
		setIsModified();
	}

	public String getPhones() {
		return mPhones;
	}

	public void setPhones(String phones) {
		mPhones = phones;
		setIsModified();
	}

	public String getObjectWork() {		 
		return mObjectWork;
	}

	public void setObjectWork(String object) {
		mObjectWork = object;
		setIsModified();
	}

	public String getDateWork() {		
		return mDateWork;
	}

	public void setDateWork(String dateWork) {
		mDateWork = dateWork;
		setIsModified();
	}

	public String getTimeWork() {
		return mTimeWork;
	}

	public void setTimeWork(String timeWork) {
		mTimeWork = timeWork;
		setIsModified();
	}

	public String getDesirableTime() {
		return mDesirableTime;
	}

	public void setDesirableTime(String desirableTime) {
		mDesirableTime = desirableTime;
		setIsModified();
	}

	public ArrayList<DataDraft> getListDrafts() {
		return mListDrafts;
	}

	public void setListDrafts(ArrayList<DataDraft> listDrafts) {
		mListDrafts = listDrafts;
	}

	public String title() {
		return "Замер № " + mNum + " от " + mDateDoc + ". Дата, время: " +
				mDateWork + " - " + mDesirableTime;
	}

	public String shortTitle() {
		return "Замер № " + mNum + " от " + mDateDoc;
	}

	public String adrworkTitle() {
		return "Адрес: " + mAddress + ". - ЗАМЕР: " + mObjectWork;
	}

	public String customerTitle() {
		return "тел. " + mPhones + ". Заказчик: " + mCustomer;
	}

	public String noteTitle() {
		return "Примечание: " + getNote();
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

	public String getOffice() {
		return mOffice;
	}

	public void setOffice(String office) {
		mOffice = office;
	}
	
	public int getCountDrafts() {
		return mCountDrafts;
	}
	
	public void setCountDrafts(int count) {
		mCountDrafts = count;
	}
}
