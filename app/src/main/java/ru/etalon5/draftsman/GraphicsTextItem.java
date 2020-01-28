package ru.etalon5.draftsman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphicsTextItem extends GraphicsItem {
	private String mText;
	private float mX;
	private float mY;
	private float mWidth = 0;
	private float mHeight = Consts.TEXT_SIZE;
	private boolean mIsVertical;
	
	public GraphicsTextItem() {
		
	}
	
	public GraphicsTextItem(JSONObject json) {
		super(json);		
	}
	
	public GraphicsTextItem(PointF point, boolean isVertical) {
		mIsVertical = isVertical;
		mX = point.x;
		mY = point.y;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {		
		if (!isVisible || mText == null)
			return;

		int textSize = Consts.TEXT_SIZE * mScale / 100;
		float xT = mX * mScale / 100;
		float yT = mY * mScale / 100;

		Paint paintText = new Paint();
		paintText.setColor(paint.getColor());
		paintText.setStrokeWidth(1);		
		paintText.setTextSize(textSize);
		paintText.setAntiAlias(true);
		if (mIsMovingMode)
			paintText.setColor(Color.MAGENTA);
		String[] strList = mText.split("\n");
		
		float y = yT + Consts.TEXT_SIZE;
		if (mIsVertical) {
			canvas.save();
			canvas.rotate(270, xT, y);
		}
		for (int i = 0; i < strList.length; i++) {
			mWidth = Math.max(mWidth, paint.measureText(strList[i]));
			canvas.drawText(strList[i] , xT, y, paintText);
			y += Consts.TEXT_SIZE;
		}
		mHeight = strList.length * Consts.TEXT_SIZE;
		if (mIsVertical)
			canvas.restore();
	}

	@Override
	public void setStartPoint(PointF point) {
		mX = point.x;
		mY = point.y;
	}

	@Override
	public void addPoint(PointF point) {
	
	}
	
	public String getText() {
		if (mText == null)
			mText = "";
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}

	public PointF getStartPoint() {
		return new PointF(mX, mY);
	}
	
	@Override
	public void setFromJSONObject(JSONObject json) {
		try {
			mX = (float) json.getDouble("x");
			mY = (float) json.getDouble("y");
			mText = json.getString("value");
			if (json.has("rotation")) {
				mIsVertical = json.getDouble("rotation") > 0;
			}
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
		
	}
	
	@Override
	public boolean isCrosses(PointF point) {
		if (mWidth == 0)
			return false;
		
		float x1 = mX;
		float x2 = mX + mWidth;
		if (mIsVertical)
			x2 = mX + mHeight;		
		float y1 = mY;
		float y2 = mY + mHeight;
		if (mIsVertical)
			y2 = mY + mWidth;
		
		return (point.x >= x1) && (point.x <= x2) && (point.y >= y1) && (point.y <= y2);
	}	

	@Override
	public boolean isCrosses(RectF rect) {		
		return isCrosses(new PointF(rect.left + (rect.right - rect.left) / 2, 
					rect.top + (rect.bottom - rect.top) / 2)) ||
				isCrosses(new PointF(rect.left, rect.top)) || isCrosses(new PointF(rect.right, rect.bottom)) ||
				isCrosses(new PointF(rect.left, rect.bottom)) || isCrosses(new PointF(rect.right, rect.top));
	}

	@Override
	public boolean isEmpty() {		
		return mText.isEmpty();
	}

	@Override
	public void increment(float dX, float dY) {
		mX += dX;
		mY += dY;		
	}

	@Override
	public GraphicsItem cloneInstanse() {
		GraphicsTextItem item = new GraphicsTextItem();
		item.mX = mX;
		item.mY = mY;
		item.mText = mText;
		item.setFixedMode();
		return item;
	}

	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = new JSONObject();
		try {					
			obj.put("x", mX);
			obj.put("y", mY);
			if (mIsVertical)
				obj.put("rotation", 270);
			else obj.put("rotation", 0);
			obj.put("value", mText);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}
