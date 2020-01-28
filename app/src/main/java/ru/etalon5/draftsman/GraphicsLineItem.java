package ru.etalon5.draftsman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphicsLineItem extends GraphicsItem {

	private float mX1;
	private float mY1;
	private float mX2;
	private float mY2;	
	
	public GraphicsLineItem() {
		
	}
	
	public GraphicsLineItem(JSONObject json) {
		super(json);
	}
	
	public GraphicsLineItem(GraphicsLineItem item) {
		mX1 = item.mX1;
		mX2 = item.mX2;
		mY1 = item.mY1;
		mY2 = item.mY2;
	}
	
	
	public GraphicsLineItem(PointF point) {
		mX1 = point.x;
		mY1 = point.y;
		mX2 = point.x;
		mY2 = point.y;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (!isVisible || mX1 == 0 || mY1 == 0)
			return;
		Paint paintLine = new Paint();
		paintLine.setColor(paint.getColor());
		paintLine.setStrokeWidth(paint.getStrokeWidth());		
		paintLine.setAntiAlias(true);
		if (mIsDrawingMode || mIsMovingMode)
			paintLine.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));
		else paintLine.setPathEffect(null) ;		
		if (mIsMovingMode)
			paintLine.setColor(Color.MAGENTA);

		float x1, y1, x2, y2;
		x1 = mX1 * mScale / 100;
		x2 = mX2 * mScale / 100;
		y1 = mY1 * mScale / 100;
		y2 = mY2 * mScale / 100;
		canvas.drawLine(x1, y1, x2, y2, paintLine);
	}	

	public float getX1() {
		return mX1;
	}

	public void setX1(float x1) {
		mX1 = x1;
	}

	public float getY1() {
		return mY1;
	}

	public void setY1(float y1) {
		mY1 = y1;
	}

	public float getX2() {
		return mX2;
	}

	public void setX2(float x2) {
		mX2 = x2;
	}
	
	public void setLine(PointF p1, PointF p2) {
		setX1(p1.x);
		setY1(p1.y);
		setX2(p2.x);
		setY2(p2.y);
	}

	public float getY2() {
		return mY2;
	}

	public void setY2(float y2) {
		mY2 = y2;
	}

	@Override
	public void addPoint(PointF point) {		
			mX2 = point.x;
			mY2 = point.y;		
	}

	@Override
	public void setStartPoint(PointF point) {
		mX1 = point.x;
		mY1 = point.y;		
	}

	@Override
	public void setFromJSONObject(JSONObject json) {
		try {
			mX1 = (float) json.getDouble("x1");
			mY1 = (float) json.getDouble("y1");
			mX2 = (float) json.getDouble("x2");
			mY2 = (float) json.getDouble("y2");
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
	}

	@Override
	public boolean isCrosses(RectF rect) {
		if (mX1 < rect.left && mX2 < rect.left)
			return false;
		if (mX1 > rect.right && mX2 > rect.right)
			return false;
		if (mY1 < rect.top && mY2 < rect.top)
			return false;
		if (mY1 > rect.bottom && mY2 > rect.bottom)
			return false;
		return GraphicsItem.isLineCross(new PointF(mX1, mY1), new PointF(mX2, mY2), 
				new PointF(rect.left, rect.top), new PointF(rect.right, rect.bottom)) ||
				GraphicsItem.isLineCross(new PointF(mX1, mY1), new PointF(mX2, mY2), 
						new PointF(rect.left, rect.bottom), new PointF(rect.right, rect.top));		
	}

	@Override
	public boolean isEmpty() {		
		return (mX1 == mX2) && (mY1 == mY2);
	}

	@Override
	public boolean isCrosses(PointF point) {
		
		return false;
	}

	@Override
	public void increment(float dX, float dY) {
		mX1 += dX;
		mX2 += dX;
		mY1 += dY;
		mY2 += dY;		
	}

	@Override
	public GraphicsItem cloneInstanse() {
		GraphicsLineItem item = new GraphicsLineItem();
		item.mX1 = mX1;
		item.mX2 = mX2;
		item.mY1 = mY1;
		item.mY2 = mY2;
		item.setFixedMode();
		return item;
	}

	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("x1", mX1);
			obj.put("y1", mY1);
			obj.put("x2", mX2);
			obj.put("y2", mY2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public PointF getNearPoint(PointF point) {
		float d1 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d2 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d = Math.min(d1, d2);
		if (d1 == d)
			return new PointF(mX1, mY1);				
		if (d2 == d)
			return new PointF(mX2, mY2);		
		return point;
	}

	@Override
	public PointF getNearPointByDistanse(PointF point, float distanse) {
		float d1 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d2 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d = Math.min(d1, d2);
		if (d1 == d && d <= distanse)
			return new PointF(mX1, mY1);				
		if (d2 == d && d <= distanse)
			return new PointF(mX2, mY2);		
		return point;
	}
	
	@Override
	public void straighten(int distanse) {		
		mX1 = (int) Math.round(mX1 / distanse) * distanse;
		mX2 = (int) Math.round(mX2 / distanse) * distanse;
		mY1 = (int) Math.round(mY1 / distanse) * distanse;
		mY2 = (int) Math.round(mY2 / distanse) * distanse;
	}	
}
