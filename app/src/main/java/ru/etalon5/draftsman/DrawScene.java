package ru.etalon5.draftsman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" }) 
public class DrawScene extends SurfaceView {
	
	private static final float MOVE_LENGTH = 100;		 
	private ArrayList<GraphicsItem> mObjects;
	private DataDraft mDataDraft;
	private Paint mPaintItems;
	private Paint mPaintDirectingCross;	
	private Paint mPaintDeletingCross;
	private boolean mIsEventDown;
	private Consts.Tool mActiveTool;
	private GraphicsItem mCurItem;
	private boolean mIsSnapGrid;
	private boolean mIsSnapObjects;
	private boolean mIsWhiteBackground;
	private GraphicsItemDirectingCross mDirectingCross;	
	private GraphicsItemDeleteCross mDeletingCross;
	private EditText mEditor;
	private EditText mEditTextName;
	private TextView mTextViewNumDraft;
	private ViewFlipper mParentFlipper;
	private PointF mFromPosition;
	private int mScale = 100;
	private boolean mIsViewMode;
	
	public DrawScene(Context context, DataDraft draft) {
		super(context);		
		init(draft);
	}

	public DrawScene(Context context, DataDraft draft, int scale, boolean isViewMode) {
		super(context);
		mScale = scale;
		mIsViewMode = isViewMode;
		init(draft);
	}

	public void init(DataDraft draft) {
		if (draft == null)
			return;

		mDataDraft = draft;
		loadDrawSettings();
		if (mIsViewMode) {
			mIsSnapGrid = false;
			mIsWhiteBackground = true;
		}
		mObjects = draft.getGraphicsObjectsFromDraft();
		initPaintItems();
		initPaintDirects();
		initPaintDelete();
		mActiveTool = Consts.Tool.Arrow;
		mDirectingCross = new GraphicsItemDirectingCross();
		mDeletingCross = new GraphicsItemDeleteCross();
		initCallback();
	}

	public void resetDraft(String draft) {
		mDataDraft.setDraft(draft);
		mObjects = mDataDraft.getGraphicsObjectsFromDraft();
	}

	public void setTool(Consts.Tool tool) {
		mActiveTool = tool;
	}
	
	public void setEditior(EditText editText) {
		mEditor = editText;
		if (mIsWhiteBackground) {
			mEditor.setBackgroundColor(Color.BLACK);
			mEditor.setTextColor(Color.WHITE);
		}
		else {
			mEditor.setBackgroundColor(Color.WHITE);
			mEditor.setTextColor(Color.BLACK);
		}
	}
	
	public void setEditTextName(EditText editText) {
		mEditTextName = editText;
	}
	
	public void setTextViewNumDraft(TextView textView) {
		mTextViewNumDraft = textView;
	}
	
	public void setFlipper(ViewFlipper flipper) {
		mParentFlipper = flipper;
	}
	
	public DataDraft getDataDraft() {
		return mDataDraft;
	}
	
	public int numPage() {
		return 1 + mParentFlipper.indexOfChild(mParentFlipper.getCurrentView());		
	}
	
	public void setNumDraft() {
		if (mTextViewNumDraft != null)
			mTextViewNumDraft.setText("[" + numPage() + "/" + mParentFlipper.getChildCount() + "]");
	}	
	
	public void undoDraw() {
		if (mObjects.size() == 0)
			return;
		
		GraphicsItem item = mObjects.get(mObjects.size() - 1);
		if (item.isVisible) 
			mObjects.remove(item);			
		else item.isVisible = true;
		draw();
	}
	
	public void clearDraft() {
		mObjects.clear();
		draw();
	}
	
	public void copyObjects(ArrayList<GraphicsItem>  objects) {
		mObjects.clear();
		for (int i = 0; i < objects.size(); i++) 
			if (objects.get(i).isVisible)
				mObjects.add(objects.get(i).cloneInstanse());		
	}
	
	public ArrayList<GraphicsItem> getObjects() {
		return mObjects;
	}
	
	public boolean onTouchEvent(MotionEvent event)
    {		
		PointF curr = new PointF(event.getX(), event.getY());

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:					
				onEventDown(curr);
				break;
			case MotionEvent.ACTION_MOVE:				
				onEventMove(curr);				
				break;
			case MotionEvent.ACTION_UP:						
				onEventUp(curr);
				break;
			case MotionEvent.ACTION_CANCEL:				
				break;				
		}
		if (mActiveTool != Consts.Tool.Arrow)
			draw();
		return true;
    }
	
	public void setIsSnapGrid(boolean isSnap) {
		mIsSnapGrid = isSnap;
	}
	
	public void setIsSnapObjects(boolean isSnap) {
		mIsSnapObjects = isSnap;
	}
	
	public void setIsWhiteBackground(boolean isWhiteBackground) {
		mIsWhiteBackground = isWhiteBackground;
	}
	
	private void initPaintItems() {
		mPaintItems = new Paint();
		if (mIsWhiteBackground)
			mPaintItems.setColor(Color.BLACK);
		else mPaintItems.setColor(Color.WHITE);
		if (mIsViewMode)
			mPaintItems.setColor(Color.parseColor("#FFC107"));
		mPaintItems.setStyle(Paint.Style.STROKE);
		mPaintItems.setStrokeWidth(2);		
	}
	
	public void loadDrawSettings() {
		SharedPreferences reader = PreferenceManager.getDefaultSharedPreferences(getContext());
		setIsSnapGrid(reader.getBoolean("draftIsSnapGrid", true));
		setIsSnapObjects(reader.getBoolean("draftIsSnapObjects", true));
		setIsWhiteBackground(reader.getBoolean("draftIsWhiteBackground", true));		
	}
	
	public void repaint() {
		loadDrawSettings();
		initPaintItems();
		initPaintDirects();
		initPaintDelete();		
		draw();
	}
	
	private void initPaintDirects() {
		mPaintDirectingCross = new Paint();
		mPaintDirectingCross.setColor(Color.RED);
		mPaintDirectingCross.setStyle(Paint.Style.STROKE);
		mPaintDirectingCross.setStrokeWidth(1);
		mPaintDirectingCross.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
	}
	
	private void initPaintDelete() {
		mPaintDeletingCross = new Paint();
		mPaintDeletingCross.setAntiAlias(true);
		mPaintDeletingCross.setColor(Color.RED);		
		mPaintDeletingCross.setStrokeWidth(3);		
	}
	
	private void onEventDown(PointF point) {
		if (mIsViewMode) {
			mFromPosition = point;
			return;
		}
		if (mActiveTool == Consts.Tool.Arrow) {
			if (mIsSnapGrid || mIsSnapObjects)
				point = recalcPoint(point);				
			mFromPosition = point;
			return;
		}			
		
		mIsEventDown = true;		
		mCurItem = null;
		
		if (mActiveTool == Consts.Tool.Move) {
			mCurItem = getItemByPoint(point);
			if (mCurItem != null) {
				mFromPosition = point;				
				mCurItem.setMovingMode();				
				return;
			}			
		}
		if (mActiveTool == Consts.Tool.Pen) {			
			mCurItem = new GraphicsPathItem(point);			
		}
		if (mActiveTool == Consts.Tool.Line) {			
			if (mIsSnapGrid || mIsSnapObjects) {
				point = recalcPoint(point);
				mDirectingCross.isVisible = true;
			}
			mCurItem = new GraphicsLineItem(point);			
		}			
		if (mActiveTool == Consts.Tool.Rect) {			
			if (mIsSnapGrid || mIsSnapObjects) 
				point = recalcPoint(point);			
			mCurItem = new GraphicsRectItem(point);			
		}				
		if (mActiveTool == Consts.Tool.Text || mActiveTool == Consts.Tool.VText) {		
			if (mEditor.getVisibility() == View.VISIBLE) {
				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        		imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
        		mEditor.setVisibility(View.INVISIBLE);
        		if (!mEditor.getText().toString().trim().isEmpty()) {
        			mFromPosition.y -= Consts.TEXT_SIZE;
        			if (!mEditor.getText().toString().isEmpty()) {
        				mCurItem = new GraphicsTextItem(mFromPosition, mActiveTool == Consts.Tool.VText);		
        				((GraphicsTextItem) mCurItem).setText(mEditor.getText().toString());
        			}
        		}
			} else {
				ViewGroup.MarginLayoutParams etParams = (ViewGroup.MarginLayoutParams) mEditor.getLayoutParams();			
				etParams.leftMargin = (int) point.x + mParentFlipper.getLeft() + 50;
				etParams.topMargin = (int) point.y + mParentFlipper.getTop();
				mEditor.setLayoutParams(etParams);
				mEditor.setText(null);
				mEditor.setVisibility(View.VISIBLE);    	
				mEditor.requestFocus();
				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEditor, InputMethodManager.SHOW_FORCED);
				mFromPosition = point;				
				RectF rect = new RectF(point.x - Consts.TEXT_SIZE, point.y - Consts.TEXT_SIZE, 
						point.x + Consts.TEXT_SIZE,	point.y + Consts.TEXT_SIZE);
				for (int i = 0; i < mObjects.size(); i++) 
					if (mObjects.get(i).isVisible && 
							(mObjects.get(i) instanceof GraphicsTextItem) &&
							mObjects.get(i).isCrosses(rect)) {
						mFromPosition = ((GraphicsTextItem)mObjects.get(i)).getStartPoint();
						mFromPosition.y += Consts.TEXT_SIZE;
						mEditor.setText(((GraphicsTextItem)mObjects.get(i)).getText());
						mEditor.setSelection(mEditor.getText().length(), mEditor.getText().length());
						mObjects.remove(i);
						break;
					}							
			}
		}		
		if (mActiveTool == Consts.Tool.Delete) {
			mDeletingCross.show(point);
		}
		
		if (mCurItem != null) {
			mDataDraft.setIsModified();
			mObjects.add(mCurItem);
		}
	}
	
	private void onEventUp(PointF point) {		
		if (mActiveTool == Consts.Tool.Arrow || mIsViewMode) {
			if ((point.x  + MOVE_LENGTH) < mFromPosition.x) {
				mParentFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.go_next_in));
				mParentFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.go_next_out));
				mParentFlipper.showNext();
				if (!mIsViewMode) {
					setNumDraft();
					mEditTextName.setText(((DrawScene) mParentFlipper.getCurrentView()).getDataDraft().getName());
				} else
					((TemplatesActivity) getContext()).setSelectedItem(((DrawScene) mParentFlipper.getCurrentView()).getDataDraft().getKey());
			} else if ((point.x  - MOVE_LENGTH) > mFromPosition.x) {//				
				mParentFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.go_prev_in));
				mParentFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.go_prev_out));
				mParentFlipper.showPrevious();
				if (!mIsViewMode) {
					setNumDraft();
					mEditTextName.setText(((DrawScene) mParentFlipper.getCurrentView()).getDataDraft().getName());
				} else
					((TemplatesActivity) getContext()).setSelectedItem(((DrawScene) mParentFlipper.getCurrentView()).getDataDraft().getKey());

			}			
			return;		
		}			
		
		if (mActiveTool == Consts.Tool.Delete) {
			mDataDraft.setIsModified();
			deleteGraphicsItem(point);
			mDeletingCross.hide();
		}
		
		if (mActiveTool == Consts.Tool.Move && mCurItem != null) {
			if (mIsSnapGrid)
				mCurItem.straighten(Consts.CAGE_SIZE);
		}
		
		if (mCurItem != null) {
			if ((mActiveTool == Consts.Tool.Line || mActiveTool == Consts.Tool.Rect)  && (mIsSnapGrid || mIsSnapObjects)) {
				point = recalcPoint(point);		
				mCurItem.addPoint(point);
			}
			if (mCurItem.isEmpty() && mObjects.contains(mCurItem))  
				mObjects.remove(mCurItem);			
			else mCurItem.setFixedMode();
		}
		mIsEventDown = false;
		mCurItem = null;
		mDirectingCross.isVisible = false;		
	}
	
	private void onEventMove(PointF point) {
		if (mActiveTool == Consts.Tool.Delete) {
			mDeletingCross.addPoint(point);
			deleteGraphicsItem(point);
		}
		
		if (!mIsEventDown || mCurItem == null)
			return;
		
		if (mActiveTool == Consts.Tool.Move) {
			if (mCurItem != null) 
				mCurItem.increment(point.x - mFromPosition.x, point.y - mFromPosition.y);			
			mFromPosition = point;
			return;
		}		

		mCurItem.addPoint(point);		
		if (mActiveTool == Consts.Tool.Line)
			mDirectingCross.addPoint(point);		
	}
	
	private void draw() {
		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			drawGrid(canvas);
			drawTempItems(canvas);
			drawObjects(canvas);
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
	
	private void drawGrid(Canvas canvas) {
		Paint paint = new Paint();
		float w, h, x = 0, y = 0;
		int n, m;
		
		if (mIsWhiteBackground)
			paint.setColor(Color.WHITE);
		else paint.setColor(Color.BLACK);
		if (mIsViewMode)
			paint.setColor(Color.parseColor("#0288D1"));
		canvas.drawPaint(paint);
		if (mIsWhiteBackground)
			paint.setColor(Color.GRAY);
		else paint.setColor(Color.BLUE);
		w = getWidth();
		h = getHeight();
		mDirectingCross.setSize(w, h);
		n = (int) (w / Consts.CAGE_SIZE);
		m = (int) (h / Consts.CAGE_SIZE);
		if (mIsSnapGrid)
			for (int i = 0; i <= n; i++) {
				x = i * Consts.CAGE_SIZE;		
				for (int j = 0; j <= m; j++) {				
					y = j * Consts.CAGE_SIZE;
					canvas.drawLine(x - Consts.DAGGER_SIZE / 2, y, x + Consts.DAGGER_SIZE / 2, y, paint);
					canvas.drawLine(x, y - Consts.DAGGER_SIZE / 2, x, y + Consts.DAGGER_SIZE / 2, paint);
				}
			}		
	}	

	private void drawObjects(Canvas canvas) {		
		for (GraphicsItem obj : mObjects) 			
			obj.draw(canvas, mPaintItems, mScale);
	}
	
	private void drawTempItems(Canvas canvas) {
		if (mActiveTool == Consts.Tool.Line)
			mDirectingCross.draw(canvas, mPaintDirectingCross);
		if (mActiveTool == Consts.Tool.Delete)
			mDeletingCross.draw(canvas, mPaintDeletingCross);
	}
	
	private void initCallback() {
		getHolder().addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Canvas canvas = getHolder().lockCanvas();
				drawGrid(canvas);
				drawObjects(canvas);
				getHolder().unlockCanvasAndPost(canvas);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}
	
	private PointF recalcPoint(PointF point) {		
		PointF resPoint = new PointF(point.x, point.y);
		
		if (mIsSnapObjects && mCurItem != null) {
			for (int i = 0; i < mObjects.size(); i++) 
				if ((mCurItem != mObjects.get(i)) && mObjects.get(i).isVisible)
					resPoint = mObjects.get(i).getNearPointByDistanse(resPoint, (float) 1.5 * Consts.CAGE_SIZE);					
		}
		
		if (mIsSnapGrid) {
			int d = Consts.CAGE_SIZE;
			resPoint.x = (int) Math.round(point.x / d) * d;
			resPoint.y = (int) Math.round(point.y / d) * d;	
		}	
		
		return resPoint;
	}

	private void deleteGraphicsItem(PointF point) {
		RectF rect = mDeletingCross.getRect();
		for (int i = 0; i < mObjects.size(); i++) {
			if (mObjects.get(i).isVisible && mObjects.get(i).isCrosses(rect))
				mObjects.get(i).hide();
		}
	}
	
	private GraphicsItem getItemByPoint(PointF point) {
		RectF rect = new RectF(point.x - 10, point.y - 10, point.x + 10, point.y + 10);
		for (int i = mObjects.size() - 1; i >= 0; i--) {
			if (mObjects.get(i).isVisible && mObjects.get(i).isCrosses(rect))
				return mObjects.get(i);
		}
		return null;
	}
}
