package ru.etalon5.draftsman;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class AuthActivity extends Activity {
		
	private EditText mEditTextAccount;	
	private EditText mEditTextPassword;
	static final private int RESULT_REG_OK = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		initVariables();
		initListeners();		
	};
	
	private void initVariables() {		
		mEditTextAccount = (EditText)findViewById(R.id.editTextNameAccount);		
		mEditTextPassword = (EditText)findViewById(R.id.editTextPassword);		
		if (!Config.user.getAccount().isEmpty() && !Config.isLocalMode) {			
			mEditTextAccount.setText(Config.user.getAccount());			
		}
		if (!Consts.LOCAL_ACCOUNT.equals(Config.user.getAccount()))
			mEditTextAccount.setText(Config.user.getAccount());
	}
	
	private void initListeners() {		
		
		mEditTextPassword.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {					 
					if(!mEditTextPassword.getText().toString().isEmpty()){
						onEnter();
					}
					return true;
				}
				return false;
			}
		});		
		
		Button buttonOk = 
				(Button)findViewById(R.id.buttonEnter);		
		buttonOk.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				onEnter();				
			}
		});
		
		Button buttonReg =
				(Button)findViewById(R.id.buttonRegistation);		
		buttonReg.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				onRegistration();				
			}
		});
	}
	
	private void onEnter() {
		String account = mEditTextAccount.getText().toString().trim();		
		String password = mEditTextPassword.getText().toString().trim();		
		if (account.isEmpty() || password.isEmpty())
			return;		
				
		password = Config.md5(Consts.SALT_MD5 + password).toLowerCase(Locale.ENGLISH);
		Config.user.setAccount(account);
		Config.user.setLogin(account);
		Config.user.setPassword(password);
		Config.setError("");
		
		if (Config.isOnline(this)) {
			Button buttonOk = 
					(Button)findViewById(R.id.buttonEnter);		
			buttonOk.setEnabled(false);
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarAuth);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			new Auth().execute();
		}
		else Config.showError(this, "Авторизация", "Отсутствует соединение с интернетом!");
	}	
	
	private class Auth extends AsyncTask<Void, Void, Boolean> {	    	    
	    protected Boolean doInBackground(Void... params) {	    	
	    	return Config.user.auth();
	    }	   	    
	    protected void onPostExecute(Boolean result) {
	    	Button buttonOk = 
					(Button)findViewById(R.id.buttonEnter);		
			buttonOk.setEnabled(true);
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarAuth);
			progressBar.setVisibility(ProgressBar.INVISIBLE);
	    	if (result){ 
	    		Config.isLocalMode = false;
	    		setResult(RESULT_OK);	        	
	    		finish();
	    	}
	    	else showError();	    	
	    }
	}
	
	private void onRegistration() {
		Intent intent = new Intent(this, RegActivity.class);
		startActivityForResult(intent, RESULT_REG_OK);
	}
	
	private void showError() {
		Config.showError(this, "Авторизация", "Неверный аккаунт или пароль!");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {      
		if (requestCode == RESULT_REG_OK && resultCode == RESULT_OK) {
			Toast toast = Toast.makeText(this,
					"Аккаунт " + Config.user.getAccount() + " успешно создан!",
					Toast.LENGTH_SHORT);
			toast.show(); 	
			Config.isLocalMode = false;
    		setResult(RESULT_OK);	        	
    		finish();
		}	                  
	}    
}
