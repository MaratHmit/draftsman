package ru.etalon5.draftsman;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RegActivity extends Activity {

	private EditText mEditTextAccount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		initVariables();
		initListeners();
	};

	private void initVariables() {		
		mEditTextAccount = (EditText)findViewById(R.id.editTextNameRegEmail);
	}
	
	private void initListeners() {		
		
		mEditTextAccount.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {					 
					if(!mEditTextAccount.getText().toString().isEmpty()){
						onClickReg();
					}
					return true;
				}
				return false;
			}
		});		
		
		Button buttonOk = 
				(Button)findViewById(R.id.buttonRegEnter);		
		buttonOk.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				onClickReg();				
			}
		});	
	}
	
	private void onClickReg() {
		String account = mEditTextAccount.getText().toString().trim();		
		if (account.isEmpty())
			return;
		
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(account).matches()) {
			 Config.showError(this, "Регистрация\", \"Введён некорректный email адрес!");
			 mEditTextAccount.setFocusable(true);
			 return;
		}			
		
		Config.user.setAccount(account);
		Config.user.setLogin(account);		
		Config.setError("");
		
		if (Config.isOnline(this)) {
			Button buttonOk = 
					(Button)findViewById(R.id.buttonRegEnter);		
			buttonOk.setEnabled(false);		
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarReg);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			new Registration().execute();
		}
		else Config.showError(this, "Регистрация", "Отсутствует соединение с интернетом!");
	}	
	
	private class Registration extends AsyncTask<Void, Void, Boolean> {	    	    
	    protected Boolean doInBackground(Void... params) {	    	
	    	return Config.user.reg();
	    }	   	    
	    protected void onPostExecute(Boolean result) {
	    	Button buttonOk = 
					(Button)findViewById(R.id.buttonRegEnter);		
			buttonOk.setEnabled(true);			
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarReg);
			progressBar.setVisibility(ProgressBar.INVISIBLE);
	    	if (result){ 	    		
	    		setResult(RESULT_OK);	        	
	    		finish();
	    	}
	    	else showError();	    	
	    }
	}
	
	void showError() {
		Config.showError(this, "Регистрация", Config.getError());
	}
}
