package ru.etalon5.draftsman;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DataListFragment extends ListFragment {		
	private int mLastPosition;
	private int mSelectedPosition;
	private boolean mIsLoaded;
	private Request mRequest;	
	
	private static final int IDM_DRAW = 101; 
	private static final int IDM_EDIT = 102; 
	private static final int IDM_DELETE = 103;
	
	static final private int RESULT_EDIT_OK = 0;
	static final private int RESULT_DRAW_OK = 1;
		
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {		
		savedInstanceState.putInt("lastPosition", mLastPosition);		
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRequest = new Request();
		if (savedInstanceState != null) 
			mLastPosition = savedInstanceState.getInt("lastPosition");
		mIsLoaded = false;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		setListAdapter(new MeasuresAdapter(StoreMeasures.getMeasures()));		
	}
		
	@Override
	public void onStart() {
		super.onStart();
		if (!mIsLoaded) 
			loadData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		ListView listView = (ListView)view.findViewById(android.R.id.list);		
		if (Config.user.getIsRoot()) {
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

	            @Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	            	mSelectedPosition = position;
	                return false;
	            }
	        });	    
			registerForContextMenu(listView);
		}
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);		
		if (mSelectedPosition >= 0) {
			DataMeasure measure = ((MeasuresAdapter)getListAdapter()).getItem(mSelectedPosition);
			if (measure != null) 
				menu.setHeaderTitle(measure.shortTitle());
		}
		menu.add(Menu.NONE, IDM_DRAW, Menu.NONE, getString(R.string.app_draft));
		menu.add(Menu.NONE, IDM_EDIT, Menu.NONE, getString(R.string.app_edit));
		menu.add(Menu.NONE, IDM_DELETE, Menu.NONE, getString(R.string.app_delete));
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
			mSelectedPosition = position;
			drawSelectedMeasure();			
	}	
		
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (mSelectedPosition < 0)
			return false;

		switch (item.getItemId()) {
		case IDM_DRAW: { 
			drawSelectedMeasure();
			return true;
		}
		case IDM_EDIT: { 
			editSelectedMeasure();
			return true;
		}
		case IDM_DELETE: { 
			deleteSelectedMeasure();
			return true;
		}			
		default: return super.onOptionsItemSelected(item);
		}
	}
	
	private void drawSelectedMeasure() {
		if (mSelectedPosition < 0)
			return;
		
		DataMeasure measure = ((MeasuresAdapter)getListAdapter()).getItem(mSelectedPosition);
		if (measure != null) {
			StoreMeasures.loadDrawings(measure);
	    	Intent i = new Intent(getActivity(), DraftActivity.class);
	    	i.putExtra(DraftActivity.EXTRA_KEY_MEASURE, measure.getKey());
	    	startActivityForResult(i, RESULT_DRAW_OK);	    		      		    	
		}
	}
	
	private void editSelectedMeasure() {
		if (mSelectedPosition < 0)
			return;
		
		DataMeasure measure = ((MeasuresAdapter)getListAdapter()).getItem(mSelectedPosition);
		if (measure != null) {
			Intent intent = new Intent(getActivity(), CardMeasure.class);
			intent.putExtra(CardMeasure.EXTRA_KEY_MEASURE, measure.getKey());
			startActivityForResult(intent, RESULT_EDIT_OK);	    		    	    		    	
		}
	}
	
	private void deleteSelectedMeasure() {
		AlertDialog.Builder ad;
		String title = "Подтверждение удаления";
		String message = "Удалить выбранный замер?";
		String buttonYesString = "Да";
		String buttonNoString = "Нет";
	        
	    ad = new AlertDialog.Builder(getActivity());
	    ad.setTitle(title);  
	    ad.setMessage(message);
	    ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int arg1) {
	    		if (mSelectedPosition < 0)
	    			return;
	    		
	    		DataMeasure measure = ((MeasuresAdapter)getListAdapter()).getItem(mSelectedPosition);
	    		StoreMeasures.deleteMeasureInCache(measure);
	    		loadData();
	    	}		
	    });
	    ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int arg1) {
	            
	            }
	        });
	    ad.setCancelable(false);
	    ad.show();	
	}
	
	public void deleteAllMeasurements() {
		AlertDialog.Builder ad;
		String title = "Подтверждение удаления";
		String message = "Удалить все замеры на данном устройстве?";
		String buttonYesString = "Да";
		String buttonNoString = "Нет";
	        
	    ad = new AlertDialog.Builder(getActivity());
	    ad.setTitle(title);  
	    ad.setMessage(message);
	    ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int arg1) {
	    		StoreMeasures.deleteAllMeasurementsInCache();
	    		loadData();
	    	}		
	    });
	    ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int arg1) {
	            
	            }
	        });
	    ad.setCancelable(false);
	    ad.show();	
	}	
	
	public void synchronizeData() {
		if (!Config.isOnline(getActivity())) {
			Toast toast = Toast.makeText(getActivity(),
					"Отсутствует соединение с интернетом!", Toast.LENGTH_SHORT);
			toast.show();
        	toast.show();
        	return;
		}
		setVisibleFragments(true);
		setListShown(false);
		new FetchMeasures().execute();		
	}
	
	public void loadData() {
		Config.countRecs = 0;		
		Config.clearToast();
		Config.clearError();
		setRequestParams();
		StoreMeasures.setRequest(mRequest);
		StoreMeasures.loadFromDB();
		refreshAdapter();				
	}
	
	private void setRequestParams() {
		CheckBox box = (CheckBox) getActivity().findViewById(R.id.checkBoxOutstanding);
		
		mRequest.setLimit(Consts.LIMIT_SERVER);
		mRequest.setIsOutstanding(box.isChecked());
	}
	
	private class MeasuresAdapter extends ArrayAdapter<DataMeasure> {
		
		public MeasuresAdapter(ArrayList<DataMeasure> docs) {			
			super(getActivity(), 0, docs);			
		}
		
		@SuppressLint("InflateParams") @Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_measure, null);
			}			
			
			DataMeasure measure = getItem(position);			
			TextView titleTextView =
					(TextView)convertView.findViewById(R.id.measure_list_item_titleTextView);
			titleTextView.setText(measure.title());			
			TextView customerTextView = 
					(TextView)convertView.findViewById(R.id.measure_list_item_customerTextView);
			customerTextView.setText(measure.customerTitle());			
			
			LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.measure_list_item_layout_main);
			
			TextView statusTextView = 
					(TextView)convertView.findViewById(R.id.measure_list_item_status);
			if (measure.getStatus() == 1) { 				
				layout.setBackgroundColor(Color.rgb(210, 252, 168));
				statusTextView.setText("выполнен [чертежей: " + measure.getCountDrafts() + "]");
			}
			else { 
				layout.setBackgroundColor(Color.rgb(255, 204, 209));
				statusTextView.setText("новый");
			}
			
			TextView adrWorkTextView = 
					(TextView)convertView.findViewById(R.id.measure_list_item_adrworkTextView);			
			adrWorkTextView.setText(measure.adrworkTitle());
			
			TextView noteTextView = 
					(TextView)convertView.findViewById(R.id.measure_list_item_noteTextView);
			noteTextView.setText(measure.noteTitle());			
			
			return convertView;
		}
	}	
	
	@TargetApi(Build.VERSION_CODES.KITKAT) 
	public void refreshAdapter() {	
		
		if (Config.getError().isEmpty()) {
			((MeasuresAdapter) getListAdapter()).notifyDataSetChanged();
			if (mLastPosition > 0)			
				setSelection(mLastPosition);
			
			getListView().setOnScrollListener(new OnScrollListener() {			
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {			 			
				}			
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {					
					mLastPosition = firstVisibleItem;
				}
			});		
			
			TextView countTextView = 
					(TextView) getActivity().findViewById(R.id.textViewInfoCountMeasures);
			String s = StoreMeasures.getMeasures().size() + "";
			if (Config.countRecs > 0)
				s = s + "/" + Config.countRecs;
			countTextView.setText(s);		
			setVisibleFragments(Config.countRecs > 0);			
			if (!Config.getToast().isEmpty()) {
				Toast toast = Toast.makeText(getActivity(), Config.getToast(), Toast.LENGTH_SHORT); 
				toast.show(); 
			}
		} else Config.showError(getActivity());				
	}
	
	private class FetchMeasures extends AsyncTask<Boolean, Void, Void> {	    	
	    @Override
	    protected Void doInBackground(Boolean... params) {	    
	    	if (Config.getSecret() == "" && !Config.isLocalMode && Config.isOnline(getActivity()))
	    		 Config.user.auth();
	    	Config.clearToast();
	    	Config.clearError();
	    	setRequestParams();
	    	StoreMeasures.setRequest(mRequest);
	    	StoreMeasures.synchronizeData();
	    	return null;
	    }	
	    	
	    @Override
	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);	    		    	
	    	StoreMeasures.loadFromDB();
			refreshAdapter();
			setListShown(true);
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	    super.onActivityResult(requestCode, resultCode, data);
	    
	    if (requestCode == RESULT_DRAW_OK || requestCode == RESULT_EDIT_OK)
	    	  loadData();    
	}
	
	private void setVisibleFragments(boolean isVisible) {
		if (isVisible) {
			LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.buttonsContainer);
			layout.setVisibility(LinearLayout.VISIBLE);
			layout = (LinearLayout) getActivity().findViewById(R.id.fragmentContainer);
			layout.setVisibility(LinearLayout.VISIBLE);
			layout = (LinearLayout) getActivity().findViewById(R.id.emptyContainer);
			layout.setVisibility(LinearLayout.GONE);
		} else {
			LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.buttonsContainer);
			layout.setVisibility(LinearLayout.GONE);
			layout = (LinearLayout) getActivity().findViewById(R.id.fragmentContainer);
			layout.setVisibility(LinearLayout.GONE);
			layout = (LinearLayout) getActivity().findViewById(R.id.emptyContainer);
			layout.setVisibility(LinearLayout.VISIBLE);
		}
	}
	
}
