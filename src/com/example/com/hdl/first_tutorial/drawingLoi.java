package com.example.com.hdl.first_tutorial;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class drawingLoi{
	
	Canvas canvas;
	float w; float h; Paint defaultPaint;
	
	public drawingLoi(Paint defaultPaint){
		
		this.defaultPaint=defaultPaint;
	}
	public void setAll(Canvas canvas,  float w, float h){
		this.w=w;this.h=h;
		this.canvas=canvas;
	}
	public void drawRect(cPoint p1, cPoint p2, Paint paint){

		RectF a=new RectF(w*p1.x, h*p1.y, w*p2.x, h*p2.y);
		canvas.drawRect(a, paint);
	}
	public  void drawCircle( cPoint point, float radius){
		drawCircle(point, radius, defaultPaint);
	}
	public  void drawCircle( cPoint point, float radius, Paint paint){
		canvas.drawCircle(point.x*w,point.y*h, radius, paint);
	}
	public  void drawLine( cPoint mot,cPoint hai){
		drawLine( mot, hai, defaultPaint);
	}
	public  void drawLine( cPoint mot,cPoint hai, Paint paint){
		canvas.drawLine(mot.x*w,mot.y*h,hai.x*w,hai.y*h, paint);
	}
	public  void drawArc( cPoint p, float radius1, float start, float end, Paint paint){
		float center_x, center_y;
		center_x = p.x*w;
		center_y = p.y*h;
		float radius=radius1*w;
		final RectF oval = new RectF();
		oval.set(center_x - radius, 
				center_y - radius, 
				center_x + radius, 
				center_y + radius);

		canvas.drawArc(oval, start, end, true, paint);
	}
	void drawText(String text, float x, float y, Paint paint){
		canvas.drawText(text, x*w, y*h, paint);

	}
	
	
	public RectF convertRect(cPoint p1, cPoint p2){
		return new RectF(w*p1.x, h*p1.y, w*p2.x, h*p2.y);
	}
	public Paint getPaintColor(int color){
		Paint p=new Paint();
		p.setColor(color);;
		return p;
	}
}

