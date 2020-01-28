package ru.etalon5.draftsman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
	private Menu menu;	
	static final private int RESULT_AUTH_OK = 0;
	static final private int RESULT_ADD_OK = 1;	
		
	
	private void initDefaultSettings() {
		Config.isCompressedRequest = true;		
	}
	
	private void loadAuth() {		
		SharedPreferences reader = PreferenceManager.getDefaultSharedPreferences(this);
		Config.user.setAccount(reader.getString("account", Consts.LOCAL_ACCOUNT));
		Config.user.setLogin(reader.getString("login", Consts.LOCAL_ACCOUNT));
		Config.user.setPassword(reader.getString("password", Consts.LOCAL_PASSWORD));
		Config.isFirstStart = reader.getBoolean("isFirstStart", true);
		if (Config.isFirstStart)
			Config.setDBName(reader.getString("dbName", "dbMain"));
		else Config.setDBName(reader.getString("dbName", Config.md5(Config.user.getAccount().trim())));
    	Config.isLocalMode = reader.getBoolean("isLocalMode", true);
    	Config.user.setIsRoot(true);    	
    	Config.setSecret(reader.getString("secret", ""));    	
    	initTitle();
	}
	
	private void saveAuth() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("dbName", Config.getDatabaseName());
    	editor.putString("account", Config.user.getAccount());    	
    	editor.putString("login", Config.user.getLogin());    	
    	editor.putString("password", Config.user.getPassword());
    	editor.putString("secret", Config.getSecret());
    	editor.putBoolean("isLocalMode", Config.isLocalMode);    	
    	editor.putBoolean("isFirstStart", false);
    	editor.commit();  
	}		
	
	private void startAuth() {
		Intent intent = new Intent(this, AuthActivity.class);
		startActivityForResult(intent, RESULT_AUTH_OK);		
	}
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (savedInstanceState == null) {
			Config.user = new DataUser();
			initDefaultSettings();	
			loadAuth();			
			StoreMeasures.initVariables();
		}		
		StoreMeasures.initDBHelper(this);
		if (savedInstanceState == null)
			getFragmentManager().beginTransaction().add(R.id.fragmentContainer, new DataListFragment()).commit();
	    loadSettings();
	}
	
	private void initMenu() {		
		if (Config.isLocalMode) {
			menu.getItem(1).setVisible(false);
			menu.getItem(3).setVisible(false);
			menu.getItem(2).setVisible(true);
		}
		else {
			menu.getItem(1).setVisible(true);
			menu.getItem(2).setVisible(false);
			menu.getItem(3).setVisible(true);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		saveAuth();
		saveSettings();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private DataListFragment getFragmentMeasures() {
		DataListFragment fragment = (DataListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
		return fragment;
	}  

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
    	initMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        
    	DataListFragment fragment = getFragmentMeasures();
    	
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
        	if (fragment != null)
        		fragment.synchronizeData();
            return true;
        }
        if (id == R.id.action_auth) {
        	startAuth();
            return true;
        }
        if (id == R.id.action_logout) {
        	logout();
            return true;
        }
        if (id == R.id.action_addMeasure) {
        	addMeasure();
            return true;
        }
        if (id == R.id.action_deleteAll) {
        	deleteAllMeasurements();
            return true;
        }
		if (id == R.id.action_tempaltesDraft) {
			Intent i = new Intent(this, TemplatesActivity.class);
			i.putExtra("isSelected", false);
			startActivity(i);
			return true;
		}
        return super.onOptionsItemSelected(item);        
    }
    
    private void loadSettings() {
    	CheckBox checkComplete = 
				(CheckBox)findViewById(R.id.checkBoxOutstanding);
		checkComplete.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				DataListFragment fragment = getFragmentMeasures();
				if (fragment != null)
					fragment.loadData();					
			}
		});
    	SharedPreferences reader = PreferenceManager.getDefaultSharedPreferences(this);    	
    	checkComplete.setChecked(reader.getBoolean("statusMeasures", false));
    	
    }
    
    private void saveSettings() {
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    	CheckBox checkComplete = 
				(CheckBox)findViewById(R.id.checkBoxOutstanding);
    	editor.putBoolean("statusMeasures", checkComplete.isChecked());    	
    	editor.commit();    	
    }    
    
    private void logout() {    	
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    	editor.remove("isLocalMode");
    	editor.commit();
    	loadAuth();
    	initMenu();
    	initTitle();
	}
    
    private void addMeasure() {
    	Intent intent = new Intent(this, CardMeasure.class);    	
		startActivityForResult(intent, RESULT_ADD_OK);
    }
    
    private void deleteAllMeasurements() {
    	DataListFragment fragment = getFragmentMeasures();
		if (fragment != null)
			fragment.deleteAllMeasurements();    	
    }
    
    private void initTitle() {
		if (!Config.isLocalMode)
			setTitle("Журнал замеров [" + Config.user.getAccount() + "]");
		else setTitle("Журнал замеров");
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {      
      if (requestCode == RESULT_AUTH_OK) {
    	  if (resultCode == RESULT_OK) {
			  Toast toast = Toast.makeText(this,
					  "Вход в аккаунт " + Config.user.getAccount() + " выполнен! Теперь Вы можете синхронизировать данные с сервером!",
					  Toast.LENGTH_SHORT);
			  toast.show();
    		  saveAuth();
    		  initMenu();    	      	  
    		  initTitle();
    	  } else loadAuth();
      }
      if (requestCode == RESULT_ADD_OK && resultCode == RESULT_OK) {
    	  DataListFragment fragment = getFragmentMeasures();
    	  if (fragment != null)
    		  fragment.loadData();      
      }
    }    
    
}
