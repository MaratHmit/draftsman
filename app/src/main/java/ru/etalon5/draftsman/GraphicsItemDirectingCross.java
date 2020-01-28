package ru.etalon5.draftsman;

import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class GraphicsItemDirectingCross extends GraphicsItem {
	
	public GraphicsItemDirectingCross() {
		super();
	}
	
	public GraphicsItemDirectingCross(JSONObject json) {
		super(json);
		// TODO Auto-generated constructor stub
	}

	private float mX;
	private float mY;
	private float mW;
	private float mH;

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (!isVisible)
			return;
		
		canvas.drawLine(0, mY, mW, mY, paint);
		canvas.drawLine(mX, 0, mX, mH, paint);	
	}

	@Override
	public void addPoint(PointF point) {
		mX = point.x;
		mY = point.y;
	}

	@Override
	public void setStartPoint(PointF point) {
		mX = point.x;
		mY = point.y;		
	}
	
	public void setSize(float width, float height) {
		mW = width;
		mH = height;
	}

	@Override
	public void setFromJSONObject(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCrosses(RectF rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCrosses(PointF point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void increment(float dX, float dY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphicsItem cloneInstanse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getAsJSONObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
