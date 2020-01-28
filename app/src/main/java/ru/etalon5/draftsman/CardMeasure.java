package ru.etalon5.draftsman;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CardMeasure extends AppCompatActivity {
	public static final String EXTRA_KEY_MEASURE = "KeyMeasure";	
	private DataMeasure mMeasure;
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_measurement_card);
		mMeasure = null;
		Intent intent = getIntent();		
		if (intent != null) {
		  String key = intent.getStringExtra(EXTRA_KEY_MEASURE);
		  mMeasure = StoreMeasures.getMeasure(key);
		}
		if (mMeasure != null)
			fillViews();
		initListeners();
	}		

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    
        getMenuInflater().inflate(R.menu.card_measure, menu);
        return true;
    }	
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        
        int id = item.getItemId();
        if (id == R.id.action_cancelMeasure) {
        	finish();
            return true;
        }       
        if (id == R.id.action_saveMeasure) {
        	saveMeasure();
            return true;
        } 
        
        return super.onOptionsItemSelected(item);        
    }    
    
    private void initListeners() {
    	Button buttonDate = (Button) findViewById(R.id.buttonDateMeasure);
		buttonDate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				getDateMeasure();
			}
		});	
		
		((EditText) findViewById(R.id.editTextMeasureNote)).setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {					 
					saveMeasure();
					return true;
				}
				return false;
			}
		});
		
		Button buttonSave = (Button) findViewById(R.id.buttonSaveMeasure);
		buttonSave.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				saveMeasure();
			}
		});
    }        
    
    private void fillViews() {
    	setTitle(mMeasure.shortTitle());
    	
    	if (mMeasure.getCustomer() != null)
    		((EditText) findViewById(R.id.editTextCustomer)).setText(mMeasure.getCustomer());
    	if (mMeasure.getPhones() != null)
    		((EditText) findViewById(R.id.editTextCustomerPhones)).setText(mMeasure.getPhones());
    	if (mMeasure.getAddress() != null)
    		((EditText) findViewById(R.id.editTextMeasureAddress)).setText(mMeasure.getAddress());
    	if (mMeasure.getDateWork() != null)    	
    		((TextView) findViewById(R.id.textViewDateMeasureValue)).setText(mMeasure.getDateWork());
    	if (mMeasure.getDesirableTime() != null)    	
    		((EditText) findViewById(R.id.editTextDesirableTime)).setText(mMeasure.getDesirableTime());
    	if (mMeasure.getObjectWork() != null)    	
    		((EditText) findViewById(R.id.editTextMeasureObject)).setText(mMeasure.getObjectWork());
    	if (mMeasure.getNote() != null)
    		((EditText) findViewById(R.id.editTextMeasureNote)).setText(mMeasure.getNote());
    }
    
    private void getDateMeasure() {
    	 DialogFragment dateDialog = new DatePicker();
         dateDialog.show(getFragmentManager(), "datePicker");
    }
    
    private void saveMeasure() {
    	if (((EditText) findViewById(R.id.editTextCustomer)).getText().toString().isEmpty()) {
    		Config.showError(this, "Создание замера", "Поле <Заказчик> не заполнено!");
    		return;
    	}
    	
    	DataMeasure measure = new DataMeasure();    	 
    	if (mMeasure != null)
    		measure.setKey(mMeasure.getKey());
    	measure.setCustomer(((EditText) findViewById(R.id.editTextCustomer)).getText().toString());
    	measure.setPhones(((EditText) findViewById(R.id.editTextCustomerPhones)).getText().toString());
    	measure.setAddress(((EditText) findViewById(R.id.editTextMeasureAddress)).getText().toString());
    	measure.setDateWork(((TextView) findViewById(R.id.textViewDateMeasureValue)).getText().toString());
    	measure.setDesirableTime(((TextView) findViewById(R.id.editTextDesirableTime)).getText().toString());
    	measure.setObjectWork(((TextView) findViewById(R.id.editTextMeasureObject)).getText().toString());
    	measure.setNote(((TextView) findViewById(R.id.editTextMeasureNote)).getText().toString());
    	    	
    	StoreMeasures.saveMeasureInCache(measure);
    	setResult(RESULT_OK);
    	finish();
    }
}
