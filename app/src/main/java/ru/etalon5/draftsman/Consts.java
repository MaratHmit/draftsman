package ru.etalon5.draftsman;

public class Consts {
	static final String SALT_MD5 ="3E586ADEBCAE4E8FB4CA2DBDAA01A947";
	static final String API_MAIN = "http://etalon5.ru/api/1";
	static final String API_MAIN_TEST = "http://test.etalon5.ru/api/1";
	
	static final String API_MEASUREMENTS = "measurements";
	static final String API_DRAFTS = "measurements/drafts";
	static final String API_USERS = "users";	
	
	static final String LOCAL_ACCOUNT = "account";
	static final String LOCAL_PASSWORD = "password";
		
	static final String APIMETHOD_AUTH = "auth.api";
	static final String APIMETHOD_REG = "reg.api";
	static final String APIMETHOD_FETCH = "fetch.api";
	static final String APIMETHOD_SAVE = "save.api";
	static final String APIMETHOD_DELETE = "delete.api";
	
	static final int DB_VERSION = 2;
	
	static final int LIMIT_LOCAL = 100;
	static final int LIMIT_SERVER = 100;
	
	static final int CAGE_SIZE = 28;
	static final int DAGGER_SIZE = 7;
	
	static final int TEXT_SIZE = 16;
	static final int DEL_CROSS_SIZE = 40;
	
	public enum Tool {
		Arrow, Pen, Line, Rect, Text, VText, Delete, Move
	}

}
