package ru.etalon5.draftsman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class StoreMeasures {
	private static ArrayList<DataMeasure> mMeasures;	
	private static ArrayList<DataMeasure> mServMeasures;
	private static ArrayList<DataDraft> mServDrafts;		
	private static DBHelper mDbHelper;
	private static SQLiteDatabase mDbWriter;	
	private static SQLiteDatabase mDbReader;		
	private static Request mRequest;
	private static Request mRequestDraft;

	public static void initVariables() {		
		mMeasures = new ArrayList<DataMeasure>();		
		mServMeasures = new ArrayList<DataMeasure>();		
		mServDrafts = new ArrayList<DataDraft>();		
		mRequest = new Request();		
		mRequestDraft = new Request();
	}
	
	public static void initDBHelper(Context c) {
		mDbHelper = new DBHelper(c);		
		mDbWriter = mDbHelper.getWritableDatabase();		
		mDbReader = mDbHelper.getReadableDatabase();
	}	
	
	public static ArrayList<DataMeasure> getMeasures() {		
		return mMeasures;
	}
	
	public static DataMeasure getMeasure(String key) {
		if (key == null)
			return null;
		
		for (Data d : mMeasures) {			
			if (d.getKey().contentEquals(key)) 				
				return (DataMeasure) d;			
		}
		return null;
	}	
		 
	private static void setSynhroTimeMain() {		
		Cursor c = mDbReader.rawQuery("select max(createdAt), max(updatedAt) from measurement", null);
		if (c.moveToFirst())				
			mRequest.setSynchronization(Math.max(c.getInt(0), c.getInt(1)));			
	}
	
	private static void setSynhroTimeObject() {		
		Cursor c = mDbReader.rawQuery("select max(createdAt), max(updatedAt) from measurement_object", null);
		if (c.moveToFirst())				
			mRequestDraft.setSynchronization(Math.max(c.getInt(0), c.getInt(1)));			
	}	
	
	public static void synchronizeData() {
		boolean result = true;
		Config.clearError();
		setSynhroTimeMain();
		setSynhroTimeObject();		
		result = synhroToServer() && synhroFromServer();
		if (result) {
			deleteInCache();
			Config.setToast("Синхронизация произведена успешно!");
		}				
		if (!result && Config.getError().isEmpty())
			Config.setError("Не удалось произвести синхронизацию!");
	}
	
	private static boolean synhroToServer() {		
		return synhroToServerMeasurements() && synhroToServerDrafts();
	}
	
	private static boolean synhroToServerMeasurements() {
		boolean result = true;
		String having = "createdAt=-1 or updatedAt=-1 or isDeleted=1";		
		Cursor c = mDbReader.query("measurement", null, null, null, "key", having, null);		
		if (c.moveToFirst()) {		        
			do { 			
				DataMeasure measure = new DataMeasure(c);
				if (measure.isDeleted()) {
					if (result &= Api.deleteMeasurement(measure))
						mDbWriter.delete("measurement", "key = ?", new String[] { measure.getKey() });	
				} else result &= Api.saveMeasurement(measure);
				if (!result)
					break;
			} while (c.moveToNext());
		}
		c.close();	
		return result;
	}
	
	public static boolean synhroToServerDrafts() {
		boolean result = true;
		String having = "createdAt=-1 or updatedAt=-1 or isDeleted=1";		
		Cursor c = mDbReader.query("measurement_object", null, null, null, "key", having, "sortIndex");		
		if (c.moveToFirst()) {		        
			do { 			
				result &= Api.saveDraft(new DataDraft(c));
				if (!result)
					break;
			} while (c.moveToNext());
		}
		c.close();		
		return result;
	}
	
	private static boolean synhroFromServer() {
		boolean result = false; 
		if (Api.fetchMeasurements(mRequest, mServMeasures)) {
			saveMeasuresInCache();
			result = true;
		} else return false;
		if (Api.fetchDrafts(mRequestDraft, mServDrafts)) {
			saveDraftsInCache();
			result &= true;
		} else return false;
		return result;
	}
	
	private static void deleteInCache() {
		String having = "isDeleted=1";
		Cursor c = mDbReader.query("measurement_object", null, null, null, "key", having, "sortIndex");		
		if (c.moveToFirst()) {		        
			do {
				mDbWriter.delete("measurement_object", "key = ?", new String[] { c.getString(0) });				
			} while (c.moveToNext());
		}
		c.close();		
	}
	
	public static void loadDrawings(DataMeasure measure) {
		if (measure == null || measure.getKey().isEmpty())
			return;
		
		String having = "keyMeasurement='" + measure.getKey() + "' and isDeleted<>1";
		Cursor c = mDbReader.query("measurement_object", null, null, null, "key", having, "sortIndex");
		measure.getListDrafts().clear();
		if (c.moveToFirst()) {		        
			do {
				DataDraft d = new DataDraft(c);
				measure.getListDrafts().add(d);
			} while (c.moveToNext());
		}
		c.close();	
	}
	
	public static void loadFromDB() {
		Cursor c = mDbReader.rawQuery("select count(*) from measurement", null);
		if (c.moveToFirst())				
			Config.countRecs = c.getInt(0);
		c.close();
		mMeasures.clear();
		String having = null;
		if (mRequest.getIsOutstanding())
			having = "measurement.status=0 and measurement.isDeleted<>1";
		else having = "measurement.isDeleted<>1";
		String columns[] = new String[] { "measurement.*, COUNT(measurement_object.key) AS countDrafts" };
		c = mDbReader.query("measurement LEFT JOIN measurement_object ON measurement.key = measurement_object.keyMeasurement", 
				columns, null, null, "measurement.key", having, "measurement.yearDoc desc, measurement.num desc", "" + Consts.LIMIT_LOCAL);		
		if (c.moveToFirst()) {		        
			do {
				DataMeasure m = new DataMeasure(c);
				mMeasures.add(m);
			} while (c.moveToNext());
		}
		c.close();		
	}

	public static void loadFromJSONString(String jsonStr) throws JSONException {		
		JSONObject obj = new JSONObject(jsonStr);		
		if (!obj.has("status"))
			return;
		
		if (obj.getString("status").equalsIgnoreCase("ok")) {
			mServMeasures.clear();
			JSONObject data;
			if (obj.has("data")) {
				data = obj.getJSONObject("data");    		
    			JSONArray items = data.getJSONArray("items");    		    		
    			for (int i = 0; i < items.length(); i++) {
    				DataMeasure m = new DataMeasure(items.getJSONObject(i));    			
    				mServMeasures.add(m);
    			}
    		}    		
    	}    		    	
	}
	
	private static int getMaxNumMeasure() {
		String s = "SELECT MAX(num) FROM measurement WHERE yearDoc = " + Config.getCurrentYear();
		Cursor c = mDbReader.rawQuery(s, null);
		if (c.moveToFirst()) {		        
			return c.getInt(0);
		}
		c.close();	
		return 0;
	}
	
	public static void deleteAllMeasurementsInCache() {
		mDbWriter.delete("measurement", "1", null);
		mDbWriter.delete("measurement_object", "1", null);	
	}
	
	public static void deleteMeasureInCache(DataMeasure measure) {
		if (measure.getCreatedAt() > 0) {
			ContentValues cv = new ContentValues();							
			cv.put("isDeleted", 1);			
			mDbWriter.update("measurement", cv, "key = ?", new String[] { measure.getKey() });		
		} else 
			mDbWriter.delete("measurement", "key = ?", new String[] { measure.getKey() });		
	}
		
	public static void saveMeasureInCache(DataMeasure m) {
		boolean isNew = m.getKey().isEmpty();
		
		ContentValues cv = new ContentValues();
		if (isNew) {
			UUID uuid = UUID.randomUUID();
			m.setKey(uuid.toString());
			cv.put("key", m.getKey());
			cv.put("num", getMaxNumMeasure() + 1);
			cv.put("yearDoc", Config.getCurrentYear());
			cv.put("dateDoc", Config.getCurrentDateAsString());
			cv.put("status", 0);
			cv.put("isDeleted", 0);
			cv.put("keyCreatedUser", Config.user.getKey());
			cv.put("createdAt", -1);
		} else {
			cv.put("keyUpdatedUser", Config.user.getKey());
			cv.put("updatedAt", -1);
		}
		cv.put("keyCustomer", "");
		cv.put("customer", m.getCustomer());
		cv.put("address", m.getAddress());
		cv.put("phones", m.getPhones());
		cv.put("objectWork", m.getObjectWork());
		cv.put("dateWork", m.getDateWork());
		cv.put("timeWork", m.getTimeWork());
		cv.put("desirableTime", m.getDesirableTime());
		cv.put("note", m.getNote());					
		cv.put("office", "");					
		if (isNew)
			mDbWriter.insert("measurement", null, cv);
		else mDbWriter.update("measurement", cv, "key = ?", new String[] { m.getKey() });
	}
	
	public static boolean saveMeasureDraftsInCache(DataMeasure m) {
		boolean isEmpty = false;
		boolean result = false;
		for (int i = 0; i < m.getListDrafts().size(); i++) {			
			DataDraft d = m.getListDrafts().get(i);
			if (d.isModified() || (d.isEmpty() && !d.getKey().isEmpty())) {
				isEmpty = isEmpty || d.isEmpty();
				d.setKeyMeasurement(m.getKey());	
				d.setSortIndex(i);
				d.refresfDraftFromGraphicsObjects();
				result = saveDraftInCache(d);
				if (result)
					d.setIsUnModified();
			}
		}
		ContentValues cv = new ContentValues();
		if (m.getListDrafts().size() > 0 && !isEmpty && Config.isLocalMode) { 
			cv.put("status", 1);		
			mDbWriter.update("measurement", cv, "key = ?", new String[] { m.getKey() });
		}
		return result;
	}
	
	public static boolean saveDraftInCache(DataDraft d) {
		if (!d.isModified())
			return false;

		boolean isNew = d.getKey().isEmpty();
		if (isNew && (d.isEmpty() || d.isDeleted()))
			return false;

		ContentValues cv = new ContentValues();
		if (isNew) {
			UUID uuid = UUID.randomUUID();
			d.setKey(uuid.toString());
			cv.put("key", d.getKey());
			cv.put("keyMeasurement", d.getKeyMeasurement());
			cv.put("createdAt", -1);
		} else cv.put("updatedAt", -1);

		cv.put("name", d.getName());
		cv.put("note", d.getNote());
		if (d.isDeleted() || d.isEmpty())
			cv.put("isDeleted", 1);
		else cv.put("isDeleted", 0);
		cv.put("draft", d.getDraft());
		cv.put("sortIndex", d.getSortIndex());

		if (isNew)
			mDbWriter.insert("measurement_object", null, cv);
		else mDbWriter.update("measurement_object", cv, "key = ?", new String[] { d.getKey() });
		if (!isNew && Config.isLocalMode && (d.isDeleted() || d.isEmpty()))
			mDbWriter.delete("measurement_object", "key = ?", new String[] { d.getKey() });
		return true;
	}

	public static boolean saveDraftAsTemplate(String draft, String name) {
		ContentValues cv = new ContentValues();
		UUID uuid = UUID.randomUUID();
		cv.put("key", uuid.toString());
		cv.put("name", name);
		cv.put("draft", draft);
		cv.put("createdAt", -1);
		mDbWriter.insert("drawing_template", null, cv);
		return true;
	}

	private static void saveMeasuresInCache() {
		if (mServMeasures.size() == 0)
			return;		
		
		for (int i = 0; i < mServMeasures.size(); i++) {
			DataMeasure m = mServMeasures.get(i);
			boolean isNew = mRequest.getSynchronization() < m.getCreatedAt();
			
			if (isNew) {
				String having = "createdAt=-1 and isDeleted<>1";		
				ArrayList<String> keys = new ArrayList<String>();
				Cursor c = mDbReader.query("measurement",  new String[] { "key" }, null, null, "key", having, null);		
				if (c.moveToFirst()) {		        
					do { 			
						keys.add(c.getString(0));					
					} while (c.moveToNext());
				}
				c.close();	
			
			    isNew = !keys.contains(m.getKey());
			}

			ContentValues cv = new ContentValues();
			if (isNew)
				cv.put("key", m.getKey());
			cv.put("num", m.getNum());
			cv.put("yearDoc", m.getYearDoc());
			cv.put("dateDoc", m.getDateDoc());
			cv.put("keyCustomer", m.getKeyCustomer());
			cv.put("customer", m.getCustomer());
			cv.put("address", m.getAddress());
			cv.put("phones", m.getPhones());
			cv.put("objectWork", m.getObjectWork());
			cv.put("dateWork", m.getDateWork());
			cv.put("timeWork", m.getTimeWork());
			cv.put("desirableTime", m.getDesirableTime());
			cv.put("note", m.getNote());
			cv.put("keyCreatedUser", m.getKeyCreatedUser());
			cv.put("keyUpdatedUser", m.getKeyUpdatedUser());			
			cv.put("office", m.getOffice());
			cv.put("status", m.getStatus());
			if (isNew)
				cv.put("isDeleted", 0);
			cv.put("updatedAt", m.getUpdatedAt());
			cv.put("createdAt", m.getCreatedAt());
			if (isNew)
				mDbWriter.insert("measurement", null, cv);
			else mDbWriter.update("measurement", cv, "key = ?", new String[] { m.getKey() });			
		}
	}
	
	private static void saveDraftsInCache() {
		if (mServDrafts.size() == 0)
			return;
		
		String having = "createdAt=-1 and isDeleted<>1";		
		ArrayList<String> keys = new ArrayList<>();
		Cursor c = mDbReader.query("measurement_object",  new String[] { "key" }, null, null, "key", having, null);		
		if (c.moveToFirst()) {		        
			do { 			
				keys.add(c.getString(0));					
			} while (c.moveToNext());
		}
		c.close();		
		
		for (int i = 0; i < mServDrafts.size(); i++) {
			DataDraft m = mServDrafts.get(i);		  
			boolean isNew = mRequestDraft.getSynchronization() < m.getCreatedAt();	
			
			if (isNew) 			
			    isNew = !keys.contains(m.getKey());						

			ContentValues cv = new ContentValues();
			if (isNew) 
				cv.put("key", m.getKey());
			cv.put("keyMeasurement", m.getKeyMeasurement());
			cv.put("name", m.getName());			
			cv.put("note", m.getNote());
			cv.put("draft", m.getDraft());
			cv.put("sortIndex", m.getSortIndex());
			cv.put("keyCreatedUser", m.getKeyCreatedUser());
			cv.put("keyUpdatedUser", m.getKeyUpdatedUser());		
			cv.put("isDeleted", 0);
			cv.put("updatedAt", m.getUpdatedAt());
			cv.put("createdAt", m.getCreatedAt());
			
			if (isNew)
				mDbWriter.insert("measurement_object", null, cv);
			else mDbWriter.update("measurement_object", cv, "key = ?", new String[] { m.getKey() });			
		}		
	}
	
	public static void setRequest(Request request) {
		mRequest.assign(request);
	}

	public static void fetchTemplatesDrafts(ArrayList<DataDraft> templatesDrafts) {
		if (templatesDrafts == null)
			return;

		templatesDrafts.clear();
		Cursor c = mDbReader.query("drawing_template", null, null, null, null, null, "name");
		int i = 0;
		if (c.moveToFirst()) {
			do {
				DataDraft m = new DataDraft(c);
				if (i == 0)
					m.setIsSelected(true);
				templatesDrafts.add(m);
				i++;
			} while (c.moveToNext());
		}
		c.close();
	}

	public static boolean renameTemplate(DataDraft dataDraft) {
		ContentValues cv = new ContentValues();
		cv.put("name", dataDraft.getName());
		return mDbWriter.update("drawing_template", cv, "key = ?", new String[]{dataDraft.getKey()}) == 1;
	}

    public static boolean removeTemplate(DataDraft dataDraft) {
        return mDbWriter.delete("drawing_template", "key = ?", new String[] { dataDraft.getKey() }) == 1;
    }
}
