package ru.etalon5.draftsman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GraphicsPathItem extends GraphicsItem {
	private Path mPath;
	private ArrayList<PointF> mPoints;
	
	public GraphicsPathItem() {
		mPoints = new ArrayList<PointF>();
		mPath = new Path();		
	}
	
	public GraphicsPathItem(JSONObject json) {		
		mPoints = new ArrayList<PointF>();
		mPath = new Path();
		setFromJSONObject(json);
		setFixedMode();
	}

	public GraphicsPathItem(PointF point) {
		mPoints = new ArrayList<PointF>();
		setStartPoint(point);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {		
		if (!isVisible)
			return;
		
		Paint paintDraw = new Paint(paint);
		if (mIsDrawingMode || mIsMovingMode)
			paintDraw.setPathEffect(new DashPathEffect(new float[] {10, 5}, 0));	
		if (mIsMovingMode)
			paintDraw.setColor(Color.MAGENTA);
		if (mScale == 100)
			canvas.drawPath(mPath, paintDraw);
		else {
			Path path = new Path();
			for (int i = 0; i < mPoints.size(); ++i) {
				if (i == 0)
					path.moveTo(mPoints.get(i).x * mScale / 100,	mPoints.get(i).y * mScale / 100);
				else path.lineTo(mPoints.get(i).x * mScale / 100,	mPoints.get(i).y * mScale / 100);
			}
			canvas.drawPath(path, paintDraw);
		}
	}
	
	@Override
	public void addPoint(PointF point) {
		if (mPath == null)
			setStartPoint(point);
		else {
			mPath.lineTo(point.x, point.y);
			mPoints.add(point);
		}
	}

	@Override
	public void setStartPoint(PointF point) {
		mPoints.clear();
		mPath = new Path();
		mPath.moveTo(point.x, point.y);
		mPoints.add(point);
	}

	@Override
	public void setFromJSONObject(JSONObject json) {
		try {
			JSONArray points = json.getJSONArray("xy");
			int n = (int) points.length();
			for (int i = 0; i < n; i = i + 2) {
				PointF point = new PointF((float) points.getDouble(i), (float) points.getDouble(i + 1));				
				if (i == 0) 
					mPath.moveTo(point.x, point.y);
				else mPath.lineTo(point.x, point.y);
				mPoints.add(point);
			}			
		} catch (JSONException e) {		
			e.printStackTrace();
		}		
	}
	
	private boolean isCrossesLine(PointF pointA, PointF pointB, RectF rect) {
		if (pointA.x < rect.left && pointB.x < rect.left)
			return false;
		if (pointA.x > rect.right && pointB.x > rect.right)
			return false;
		if (pointA.y < rect.top && pointB.y < rect.top)
			return false;
		if (pointA.y > rect.bottom && pointB.y > rect.bottom)
			return false;
		return GraphicsItem.isLineCross(new PointF(pointA.x, pointA.y), new PointF(pointB.x, pointB.y), 
				new PointF(rect.left, rect.top), new PointF(rect.right, rect.bottom)) ||
				GraphicsItem.isLineCross(new PointF(pointA.x, pointA.y), new PointF(pointB.x, pointB.y), 
						new PointF(rect.left, rect.bottom), new PointF(rect.right, rect.top));		
	}

	@Override
	public boolean isCrosses(RectF rect) {
		for (int i = 0; i < mPoints.size() - 1; i++) 
			if (isCrossesLine(mPoints.get(i), mPoints.get(i + 1), rect))
				return true;		
		
		return false;
	}

	@Override
	public boolean isEmpty() {		
		return mPoints.size() < 2;
	}

	@Override
	public boolean isCrosses(PointF point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void increment(float dX, float dY) {
		mPath = new Path();
		for (int i = 0; i < mPoints.size(); i++) {
			mPoints.get(i).x += dX;
			mPoints.get(i).y += dY;
			if (i == 0)
				mPath.moveTo(mPoints.get(i).x, mPoints.get(i).y);
			else mPath.lineTo(mPoints.get(i).x, mPoints.get(i).y);
		}		
	}

	@Override
	public GraphicsItem cloneInstanse() {
		GraphicsPathItem item = new GraphicsPathItem();
		for (int i = 0; i < mPoints.size(); i++)
			item.mPoints.add(new PointF(mPoints.get(i).x, mPoints.get(i).y));
		item.mPath = new Path(mPath);
		item.setFixedMode();
		return item;
	}

	@Override
	public JSONObject getAsJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			JSONArray points = new JSONArray();
			for (int i = 0; i < mPoints.size(); i++) {
				points.put(Math.rint(1000 * mPoints.get(i).x) / 1000);
				points.put(Math.rint(1000 * mPoints.get(i).y) / 1000);
			}
			obj.put("xy", points);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
}
