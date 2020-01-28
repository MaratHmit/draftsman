package ru.etalon5.draftsman;

import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class GraphicsItemDeleteCross extends GraphicsItem {
	private float mX;
	private float mY;
	private float mSize = Consts.DEL_CROSS_SIZE;
	

	public GraphicsItemDeleteCross() {
		isVisible = false;		
	}

	public GraphicsItemDeleteCross(JSONObject json) {
		super(json);
	
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (!isVisible)
			return;
		
		canvas.drawLine(mX - mSize / 2, mY - mSize / 2, mX + mSize / 2, mY + mSize / 2, paint);
		canvas.drawLine(mX - mSize / 2, mY + mSize / 2, mX + mSize / 2, mY - mSize / 2, paint);
	}

	@Override
	public void setStartPoint(PointF point) {
		mX = point.x;
		mY = point.y;
	}

	@Override
	public void addPoint(PointF point) {
		mX = point.x;
		mY = point.y;
	}

	@Override
	public void setFromJSONObject(JSONObject json) {
	

	}
	
	public void show(PointF point) {
		setStartPoint(point);
		super.show();
	}

	@Override
	public boolean isCrosses(RectF rect) {
		// TODO Auto-generated method stub
		return false;
	}

	public RectF getRect() {
		RectF rect = new RectF(mX - mSize / 2, mY - mSize / 2, mX + mSize / 2, mY + mSize / 2);
		return rect;
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
