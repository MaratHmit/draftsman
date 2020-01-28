package ru.etalon5.draftsman;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Api {
	static HttpURLConnection HttpConnect;
	
	static public boolean userReg(DataUser user) {		
		try {			
			String answer = execute(Consts.API_USERS, Consts.APIMETHOD_REG, user.getAsJSONString());			
			if (answer.isEmpty())
				return false;
			
			JSONObject json = new JSONObject(answer);			
			if (json.has("status") && json.getString("status").equals("ok")) {	
				json = json.getJSONObject("data");
				Config.setSecret(json.getString("secret"));				
				return true;
			} else Config.setError(json.getString("error"));
			
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return false;		
	}
	
	static public boolean userAuth(DataUser user) {
		try {
			String answer = execute(Consts.API_USERS, Consts.APIMETHOD_AUTH, user.getAsJSONString());			
			if (answer.isEmpty())
				return false;
			
			JSONObject json = new JSONObject(answer);			
			if (json.has("status") && json.getString("status").equals("ok")) {
				json = json.getJSONObject("data");
				Config.setSecret(json.getString("secret"));
				user.setFromJSONObject(json.getJSONObject("item"));
				return true;
			} else Config.setError(json.getString("error"));			
			
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return false;
	}

	static public boolean fetchMeasurements(Request request, ArrayList<DataMeasure> measures) {
		String answer = null;
		try {
			answer = execute(Consts.API_MEASUREMENTS, Consts.APIMETHOD_FETCH, "", request);
			if (!answer.isEmpty()) {
				JSONObject json = new JSONObject(answer);
				if (json.has("status") && json.getString("status").equals("ok")) {					 
					json = json.getJSONObject("data");					
					Config.countRecs = json.getInt("count");
					JSONArray items = json.getJSONArray("items");
					measures.clear();
					for (int i = 0; i < items.length(); i++) 						
						measures.add(new DataMeasure(items.getJSONObject(i)));					
					return true;
				} else Config.setError(json.getString("error"));				
			}
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
		return false;
	}	
	
	static public boolean fetchDrafts(Request request, ArrayList<DataDraft> drafts) {
		String answer = null;
		try {
			answer = execute(Consts.API_DRAFTS, Consts.APIMETHOD_FETCH, "", request);
			if (!answer.isEmpty()) {
				JSONObject json = new JSONObject(answer);
				if (json.has("status") && json.getString("status").equals("ok")) {
					json = json.getJSONObject("data");					
					JSONArray items = json.getJSONArray("items");
					drafts.clear();
					for (int i = 0; i < items.length(); i++) 						
						drafts.add(new DataDraft(items.getJSONObject(i)));					
					return true;
				} else Config.setError(json.getString("error"));				
			}
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
		return false;
	}
	
	static public boolean saveDraft(DataDraft draft) {
    	try {			
			return execute(Consts.API_DRAFTS, Consts.APIMETHOD_SAVE, draft.getAsJSONString()) != "";
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		}
		return false;
    }
	
	static public boolean saveMeasurement(DataMeasure measure) {
    	try {			
			return execute(Consts.API_MEASUREMENTS, Consts.APIMETHOD_SAVE, measure.getAsJSONString()) != "";
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		}
		return false;
    }	
	
	static public boolean deleteMeasurement(DataMeasure measure) {
    	try {			
			String answer = execute(Consts.API_MEASUREMENTS, Consts.APIMETHOD_DELETE, measure.getAsJSONString());
			if (!answer.isEmpty()) {
				JSONObject json = new JSONObject(answer);
				if (json.has("status") && json.getString("status").equals("ok")) 				
					return true;								
			}			
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		return false;
    }	
		
	
	static private String execute(String apiName, String apiMethod,
			String stringJSON) throws MalformedURLException, IOException {
    	return execute(apiName, apiMethod, stringJSON, null);
    }
    
	static private String execute(String apiName, String apiMethod,
			String stringJSON, Request request) throws MalformedURLException, IOException {
		
		String answer = null;			
		String apiScript = Config.getApiController(apiName, apiMethod);		
		if (request != null)
			apiScript = apiScript + request.getURLString();	

		URL url = new URL(apiScript);				
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-type", "application/json");
		
		try {			
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream());
			wr.write(stringJSON.getBytes("UTF8"));			
			wr.flush();
			wr.close();
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			InputStream in = connection.getInputStream();						

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {				
				return answer;
			}

			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();		
			
			if (Config.isCompressedRequest) {
				Compressor compressor = new Compressor();
				answer = compressor.decompressToString(out.toByteArray());						
			} else answer = new String(out.toByteArray());
		}
		catch (Exception e) {
			answer = "";
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}		
		return answer;
	}	
}
