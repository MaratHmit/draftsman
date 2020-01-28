package ru.etalon5.draftsman;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONObject;

public abstract class GraphicsItem {
	
	final static double EPS = 0.0001;
	
	public boolean isVisible = true;
	protected int mScale = 100;
	protected boolean mIsDrawingMode = true;
	protected boolean mIsMovingMode;

	public abstract void draw(Canvas canvas, Paint paint);

	public void draw(Canvas canvas, Paint paint, int scale) {
		mScale = scale;
		if (mScale <= 0)
			mScale = 100;
		draw(canvas, paint);
	}

	public abstract void setStartPoint(PointF point);
	public abstract void addPoint(PointF point);
	public abstract void setFromJSONObject(JSONObject json);
	public abstract boolean isCrosses(RectF rect);
	public abstract boolean isCrosses(PointF point);
	public abstract boolean isEmpty();
	public abstract void increment(float dX, float dY);
	public abstract GraphicsItem cloneInstanse();
	public abstract JSONObject getAsJSONObject();
	
	public GraphicsItem() {
		
	}	
	
	public GraphicsItem(JSONObject json) {
		setFromJSONObject(json);
		mIsDrawingMode = false;
	}
	
	public void setDrawingMode() {
		mIsDrawingMode = true;
	}
	public void setFixedMode() {
		mIsDrawingMode = false;
		mIsMovingMode = false;
	}
	
	public void show() {
		isVisible = true;
	}
	
	public void hide() {
		isVisible = false;
	}	
	
	public void setMovingMode() {
		mIsMovingMode = true;
	}	
	
	public PointF getNearPoint(PointF point) {
		return point;
	}
	
	public PointF getNearPointByDistanse(PointF point, float distanse) {
		return point;
	}

	public void straighten(int distanse) {
		
	}
	
	static public boolean isLineCross(PointF pointA1, PointF pointA2, PointF pointB1, PointF pointB2) {
		return (((pointB1.x - pointA1.x) * (pointA2.y - pointA1.y) - 
				(pointB1.y - pointA1.y) * (pointA2.x - pointA1.x)) * 
				((pointB2.x - pointA1.x) * (pointA2.y - pointA1.y) - 
						(pointB2.y - pointA1.y) * (pointA2.x - pointA1.x)) <= 0) &&
						(((pointA1.x - pointB1.x) * (pointB2.y - pointB1.y) - 
								(pointA1.y - pointB1.y) * (pointB2.x - pointB1.x)) * 
								((pointA2.x - pointB1.x) * (pointB2.y - pointB1.y) - 
										(pointA2.y - pointB1.y) * (pointB2.x - pointB1.x)) <= 0);		
	}
}
