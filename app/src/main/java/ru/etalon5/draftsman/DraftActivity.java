package ru.etalon5.draftsman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class DraftActivity extends AppCompatActivity
		implements NameTemplateDialogFragment.DialogCommunicator, DrawSettingsDialogFragment.DialogCommunicator {
	
	public static final String EXTRA_KEY_MEASURE = "KeyMeasure";
    private static final int RESULT_GET_TEMPLATE_OK = 0;
	private DataMeasure mMeasure;
	private ViewFlipper mFlipper;	
	private ToggleButton mButtonArrow;
	private ToggleButton mButtonPen;
	private ToggleButton mButtonLine;
	private ToggleButton mButtonRect;
	private ToggleButton mButtonText;
	private ToggleButton mButtonVText;
	private ToggleButton mButtonDelete;
	private ToggleButton mButtonMove;
	private Button mButtonPressed;
	private Button mButtonUndo;
	private Button mButtonNote;
	private Button mButtonClearAll;
	private ArrayList<ToggleButton> mToolButtons;
	private EditText mEditTextName;
	private EditText mEditTextEditor;
	private TextView mTextViewNumDraft;
	private Consts.Tool mCurTool = Consts.Tool.Arrow;
	private boolean mIsTemplateMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_draft);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		String key = getIntent().getStringExtra(EXTRA_KEY_MEASURE);
		mEditTextName = (EditText) findViewById(R.id.editTextNameDraft);
		if (key != null) {
			mIsTemplateMode = false;
			mMeasure = StoreMeasures.getMeasure(key);
			if (mMeasure != null)
				setTitle(getTitle() + " - " + mMeasure.getAddress());
			mTextViewNumDraft = (TextView) findViewById(R.id.textViewNumDraft);
			mButtonNote = (Button) findViewById(R.id.buttonDraftNote);
		} else {
			mIsTemplateMode = true;
			mMeasure = new DataMeasure();
			setTitle(getResources().getString(R.string.new_template));
			findViewById(R.id.textViewNumDraft).setVisibility(View.GONE);
			findViewById(R.id.buttonDraftNote).setVisibility(View.GONE);

		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		initListenersTools();
		if (!mIsTemplateMode) {
			initListenerTextName();
			initListenerNote();
		}
		initTextEditor();
		initDrawScenes();		
	};	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mIsTemplateMode)
			getMenuInflater().inflate(R.menu.draft_template, menu);
		else getMenuInflater().inflate(R.menu.draft, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!mIsTemplateMode)
			save();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {        
		int id = item.getItemId();
		if (id == R.id.action_addDraft) {
			addDraft();
			return true;
		}    
		if (id == R.id.action_cloneDraft) {
			onCloneDraft();
			return true;
		}
        if (id == R.id.action_tempaltesDraft) {
            addDraftFromTemplates();
            return true;
        }
		if (id == R.id.action_deleteDraft) {
			deleteDraft();
			return true;
		}
		if (id == R.id.action_settingsDraft) {
			settingsDraft();
			return true;
		}
		if (id == R.id.action_saveAsTemplate) {
			saveAsTemplates();
			return true;
		}
		if (id == R.id.action_select) {
			onSetDraftForTemplate();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onSetDraftForTemplate() {

		String name = mEditTextName.getText().toString();
		if (name.isEmpty()) {
			Config.showError(this, getResources().getString(R.string.creation_template),
					getString(R.string.enter_the_field_name));
			mEditTextName.setFocusable(true);
			return;
		}

		DataDraft dataDraft = ((DrawScene) mFlipper.getCurrentView()).getDataDraft();
		dataDraft.setName(mEditTextName.getText().toString());
		dataDraft.refresfDraftFromGraphicsObjects();
		Intent intent = new Intent();
		intent.putExtra("draft", dataDraft.getDraft());
		intent.putExtra("draftName", dataDraft.getName());
		setResult(RESULT_OK, intent);
		finish();
	}

	private void addDraftFromTemplates() {
        Intent i = new Intent(this, TemplatesActivity.class);
        startActivityForResult(i, RESULT_GET_TEMPLATE_OK);
    }

    public class CustomClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
        	mEditTextName.clearFocus();
        	
        	if (v == mButtonUndo) {
        		((DrawScene) mFlipper.getCurrentView()).undoDraw();
        		return;
        	}

			if (v == mButtonClearAll) {
				onClearDraft();
				return;
			}
        	
        	if (v == mButtonPressed)
        		return;

        	if (mEditTextEditor.getVisibility() == View.VISIBLE) {        	    
        		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        		imm.hideSoftInputFromWindow(mEditTextEditor.getWindowToken(), 0);
        		mEditTextEditor.setVisibility(View.INVISIBLE);
        	}
			mButtonPressed = (Button) v;
        	mCurTool = Consts.Tool.Arrow;

			for (int i = 0; i < mToolButtons.size(); i++)
				if (mToolButtons.get(i) != v)
					mToolButtons.get(i).setChecked(false);

			String msg = "Инструменты отключены";

			if (v == mButtonPen) {
				msg = "Выбран инструмент: КАРАНДАШ";
				mCurTool = Consts.Tool.Pen;
			}
			if (v == mButtonLine) {
				msg = "Выбран инструмент: ЛИНИЯ";
				mCurTool = Consts.Tool.Line;
			}
			if (v == mButtonRect) {
				msg = "Выбран инструмент: ПРЯМОУГОЛЬНИК";
				mCurTool = Consts.Tool.Rect;
			}
			if (v == mButtonText) {
				msg = "Выбран инструмент: ГОРИЗОНТАЛЬНЫЙ ТЕКСТ";
				mCurTool = Consts.Tool.Text;
			}
			if (v == mButtonVText) {
				msg = "Выбран инструмент: ВЕРТИКАЛЬНЫЙ ТЕКСТ";
				mCurTool = Consts.Tool.VText;
			}
			if (v == mButtonDelete) {
				msg = "Выбран инструмент: УДАЛЕНИЕ ЭЛЕМЕНТА";
				mCurTool = Consts.Tool.Delete;
			}
			if (v == mButtonMove) {
				msg = "Выбран инструмент: ПЕРЕМЕСТИТЬ ОБЪЕКТ";
				mCurTool = Consts.Tool.Move;
			}

			int n = mFlipper.getChildCount();
    		for (int i = 0; i < n; i++) {
    			DrawScene scene = (DrawScene) mFlipper.getChildAt(i);
    			scene.setTool(mCurTool);
    		}
    		
        	Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        	toast.show(); 
        }
    }
	
	private void initListenersTools() {		
		
		mToolButtons = new ArrayList<ToggleButton>();
		mButtonArrow = (ToggleButton) findViewById(R.id.buttonDrawArrow);
		mToolButtons.add(mButtonArrow);
		mButtonPressed = mButtonArrow;
		mButtonArrow.setOnClickListener(new CustomClickListener());
	
		mButtonPen = (ToggleButton) findViewById(R.id.buttonDrawPen);
		mButtonPen.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonPen);		

		mButtonLine = (ToggleButton) findViewById(R.id.buttonDrawLine);
		mButtonLine.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonLine);
		
		mButtonRect = (ToggleButton) findViewById(R.id.buttonDrawRect);
		mButtonRect.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonRect);
		
		mButtonText = (ToggleButton) findViewById(R.id.buttonDrawText);
		mButtonText.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonText);
		
		mButtonVText = (ToggleButton) findViewById(R.id.buttonDrawVText);
		mButtonVText.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonVText);		
		
		mButtonDelete = (ToggleButton) findViewById(R.id.buttonDrawDelete);
		mButtonDelete.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonDelete);
		
		mButtonMove = (ToggleButton) findViewById(R.id.buttonObjectMove);
		mButtonMove.setOnClickListener(new CustomClickListener());
		mToolButtons.add(mButtonMove);
		
		mButtonUndo = (Button) findViewById(R.id.buttonUndo);
		mButtonUndo.setOnClickListener(new CustomClickListener());

		mButtonClearAll = (Button) findViewById(R.id.buttonClearAll);
		mButtonClearAll.setOnClickListener(new CustomClickListener());

	}
	
	private void initListenerTextName() {
		mEditTextName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				DrawScene curScene = (DrawScene) mFlipper.getCurrentView();
				curScene.getDataDraft().setName(s.toString());
			}
		});
	}

	private void initListenerNote() {
		mButtonNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setNoteDraft();
			}
		});
	}
	
	private void setNoteDraft() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Примечание к чертежу");
		alert.setMessage("Примечание:");
		
		final DataDraft draft = ((DrawScene) mFlipper.getCurrentView()).getDataDraft();
		
		final EditText input = new EditText(this);
		input.setText(draft.getNote());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				draft.setNote(input.getText().toString());
			}
		});

		alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}
	
	private void initTextEditor() {
		mEditTextEditor = new EditText(getApplication());
		mEditTextEditor.setBackgroundColor(Color.BLACK);		
		mEditTextEditor.setVisibility(View.INVISIBLE);
		ViewGroup.MarginLayoutParams etParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		addContentView(mEditTextEditor, etParams);
	}
	
	private void initDrawScenes() {
		if (mMeasure == null)
			return;
		
		mFlipper = (ViewFlipper)findViewById(R.id.viewflipperDrafts);		
		if (mMeasure.getListDrafts().size() == 0) {
			DataDraft draft = new DataDraft();
			mMeasure.getListDrafts().add(draft);	
		}
		for (int i = 0; i < mMeasure.getListDrafts().size(); i++)
			addDrawScene(mMeasure.getListDrafts().get(i));
		if (mEditTextName != null)
			mEditTextName.setText(mMeasure.getListDrafts().get(0).getName());
		for (int i = 0; i < mMeasure.getListDrafts().size(); i++) 				
			mMeasure.getListDrafts().get(i).setIsUnModified();
	}
	
	private DrawScene addDrawScene(DataDraft dataDraft) {
		DrawScene scene = new DrawScene(this, dataDraft);		
		scene.setEditior(mEditTextEditor);
		scene.setEditTextName(mEditTextName);
		scene.setTextViewNumDraft(mTextViewNumDraft);
		scene.setFlipper(mFlipper);
		mFlipper.addView(scene);
		scene.setNumDraft();
		return scene;
	}
	
	private void addDraft() {
		DataDraft draft = new DataDraft();		
		mMeasure.getListDrafts().add(draft);	
		DrawScene newScene = addDrawScene(draft);
		while (mFlipper.getCurrentView() != newScene)
			mFlipper.showNext();		
		mEditTextName.setText("");
		mButtonArrow.callOnClick();
		newScene.setNumDraft();		
	}
	
	private void onCloneDraft() {
		AlertDialog.Builder ad;
		String title = getString(R.string.applyClone);
		String message = getString(R.string.isCreateClone);
		String buttonYesString = getString(R.string.yes);
		String buttonNoString = getString(R.string.no);

		ad = new AlertDialog.Builder(this);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				DataDraft draft = new DataDraft();
				mMeasure.getListDrafts().add(draft);
				DrawScene newScene = addDrawScene(draft);
				DrawScene curScene = (DrawScene) mFlipper.getCurrentView();
				newScene.copyObjects(curScene.getObjects());
				mFlipper.showNext();
				mButtonArrow.callOnClick();
				newScene.setNumDraft();
			}
		});
		ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
		ad.setCancelable(false);
		ad.show();
	}
	
	private void onClearDraft() {
		AlertDialog.Builder ad;
		String title = "Подтверждение очистки";
		String message = "Очистить чертеж?";
		String buttonYesString = "Да";
		String buttonNoString = "Нет";
	        
	    ad = new AlertDialog.Builder(this);
	    ad.setTitle(title);  
	    ad.setMessage(message);
	    ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				((DrawScene) mFlipper.getCurrentView()).clearDraft();
			}
		});
	    ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {

			}
		});
	    ad.setCancelable(false);
	    ad.show();	   
	}
	
	private void deleteDraft() {
		AlertDialog.Builder ad;
		String title = "Подтверждение удаления";
		String message = "Удалить чертеж?";
		String buttonYesString = "Да";
		String buttonNoString = "Нет";
	        
	    ad = new AlertDialog.Builder(this);
	    ad.setTitle(title);  
	    ad.setMessage(message);
	    ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int arg1) {
	    		if (mFlipper.getChildCount() == 1) 
	    			((DrawScene) mFlipper.getCurrentView()).clearDraft();	    		
	    		else {
	    			DataDraft draft = ((DrawScene) mFlipper.getCurrentView()).getDataDraft();
	    			draft.setIsDeleted();
	    			mFlipper.removeView(mFlipper.getCurrentView());
	    			mButtonArrow.callOnClick();
	    			((DrawScene) mFlipper.getCurrentView()).setNumDraft();
	    		}	    	
	    	}		
	    });
	    ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int arg1) {
	            
	            }
	        });
	    ad.setCancelable(false);
	    ad.show();	  		
	}
	
	private boolean save() {
		boolean result = StoreMeasures.saveMeasureDraftsInCache(mMeasure);
		if (result) {
			Toast toast = Toast.makeText(getApplicationContext(), "Сохранено!", Toast.LENGTH_SHORT);
			toast.show();
		}
		return result;
	}
	
	private void onSaveDraft() {
		save();				
	}

	private void settingsDraft() {
		DrawSettingsDialogFragment dialog = new DrawSettingsDialogFragment();
		dialog.show(getFragmentManager(), "fragment_draft_settings");
	}

	@Override
	public void dialogFinish() {	
		((DrawScene) mFlipper.getCurrentView()).repaint();
	}

	private void saveAsTemplates() {
		NameTemplateDialogFragment dialog = new NameTemplateDialogFragment();
		dialog.show(getFragmentManager(), "fragment_name_template");
	}

	public void saveTemplate(String name) {
		DataDraft draft = ((DrawScene) mFlipper.getCurrentView()).getDataDraft();
		draft.refresfDraftFromGraphicsObjects();
		boolean result = StoreMeasures.saveDraftAsTemplate(draft.getDraft(), name);
		if (result) {
			Toast toast = Toast.makeText(getApplicationContext(), "Сохранено как шаблон!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_GET_TEMPLATE_OK) {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra("draft"))
					((DrawScene) mFlipper.getCurrentView()).resetDraft(data.getStringExtra("draft"));

			}
		}
	}

}

