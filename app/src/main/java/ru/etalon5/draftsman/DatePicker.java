package ru.etalon5.draftsman;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
 
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) { 

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH); 
        
        Dialog picker = new DatePickerDialog(getActivity(), this, 
                year, month, day);
        picker.setTitle(getResources().getString(R.string.choose_date));
 
        return picker;
    }
    @Override
    public void onStart() {
        super.onStart();        
        Button nButton =  ((AlertDialog) getDialog())
                .getButton(DialogInterface.BUTTON_POSITIVE);
        nButton.setText(getResources().getString(R.string.ready));
 
    }
 
    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, 
            int month, int day) { 
    	 TextView textViewDateMeasure = (TextView) getActivity().findViewById(R.id.textViewDateMeasureValue);
    	 month++;
    	 String sMonth = month + "";
    	 if (sMonth.length() == 1)
    		 sMonth = "0" + sMonth;
    	 String sDay = day + "";
    	 if (sDay.length() == 1)
    		 sDay = "0" + sDay;    	 
    	 textViewDateMeasure.setText(sDay + "." + sMonth + "." + year);
    }
}