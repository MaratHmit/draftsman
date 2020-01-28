package ru.etalon5.draftsman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphicsRectItem extends GraphicsItem {
	
	private float mX1;
	private float mY1;
	private float mX2;
	private float mY2;
	
	public GraphicsRectItem() {
		
	}
	
	public GraphicsRectItem(JSONObject json) {
		super(json);
	}

	public GraphicsRectItem(PointF point) {
		mX1 = point.x;
		mY1 = point.y;
		mX2 = point.x;
		mY2 = point.y;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (!isVisible)
			return;
		
		Paint paintDraw = new Paint(paint);
		if (mIsDrawingMode || mIsMovingMode)
			paintDraw.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));	
		if (mIsMovingMode)
			paintDraw.setColor(Color.MAGENTA);
		float x1, y1, x2, y2;
		x1 = mX1 * mScale / 100;
		x2 = mX2 * mScale / 100;
		y1 = mY1 * mScale / 100;
		y2 = mY2 * mScale / 100;
		RectF r = new RectF(x1, y1, x2, y2);
		canvas.drawRect(r, paintDraw);
	}

	@Override
	public void setStartPoint(PointF point) {
		mX1 = point.x;
		mY1 = point.y;	
	}

	@Override
	public void addPoint(PointF point) {
		mX2 = point.x;
		mY2 = point.y;	
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

	public float getY2() {
		return mY2;
	}

	public void setY2(float y2) {
		mY2 = y2;
	}

	@Override
	public void setFromJSONObject(JSONObject json) {
		try {
			mX1 = (float) json.getDouble("x");
			mY1 = (float) json.getDouble("y");
			mX2 = mX1 + (float) json.getDouble("w");
			mY2 = mY1+ (float) json.getDouble("h");
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
	}

	@Override
	public boolean isCrosses(PointF point) {
		float x1 = Math.min(mX1, mX2);
		float x2 = Math.max(mX1, mX2);
		float y1 = Math.min(mY1, mY2);
		float y2 = Math.max(mY1, mY2);
		return (point.x >= x1) && (point.x <= x2) && (point.y >= y1) && (point.y <= y2);
	}
	
	@Override
	public boolean isCrosses(RectF rect) {			
		return isCrosses(new PointF(rect.left, rect.top)) || isCrosses(new PointF(rect.right, rect.bottom)) ||
				isCrosses(new PointF(rect.left, rect.bottom)) || isCrosses(new PointF(rect.right, rect.top));
	}

	@Override
	public boolean isEmpty() {		
		return (mX1 == mX2) || (mY1 == mY2);
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
		GraphicsRectItem item = new GraphicsRectItem();
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
			float x, y, h, w;
			x = Math.min(mX1, mX2);
			y = Math.min(mY1, mY2);
			w = Math.max(mX1, mX2) - x;
			h = Math.max(mY1, mY2) - y;		
			obj.put("x", x);
			obj.put("y", y);
			obj.put("h", h);
			obj.put("w", w);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public PointF getNearPoint(PointF point) {
		float d1 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d2 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d3 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d4 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d = Math.min(Math.min(Math.min(d1, d2), d3), d4);
		if (d1 == d)
			return new PointF(mX1, mY1);
		if (d2 == d)
			return new PointF(mX1, mY2);
		if (d3 == d)
			return new PointF(mX2, mY1);		
		if (d4 == d)
			return new PointF(mX2, mY2);		
		return point;
	}

	@Override
	public PointF getNearPointByDistanse(PointF point, float distanse) {
		float d1 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d2 = (float) Math.sqrt(Math.pow(mX1 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d3 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY1 - point.y, 2));
		float d4 = (float) Math.sqrt(Math.pow(mX2 - point.x, 2) + Math.pow(mY2 - point.y, 2));
		float d = Math.min(Math.min(Math.min(d1, d2), d3), d4);
		if (d1 == d && d <= distanse)
			return new PointF(mX1, mY1);
		if (d2 == d && d <= distanse)
			return new PointF(mX1, mY2);
		if (d3 == d && d <= distanse)
			return new PointF(mX2, mY1);		
		if (d4 == d && d <= distanse)
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
