package com.example.com.hdl.first_tutorial;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ToneGenerator;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

@SuppressLint("DrawAllocation")
public class customCanvas extends View {
	class cPoint{
		public float x,y;
		public cPoint(float xx,float yy){
			x=xx;
			y=yy;
		}
	}
	class cArc{
		public float angle, value;
		public cArc(float angle, float value){
			this.angle=angle;
			this.value=value;
		}
	}
	Paint paint;
	float w,h;
	ArrayList<cArc> values=new ArrayList<cArc>();
	Bitmap background=null;
	public customCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(8);
		for (float i=0; i<180; i+=0.1){
			values.add(new cArc(i, i/180f));
		}
		background=savePicture();

	}
	Bitmap savePicture(){
		Bitmap toDisk = null;
		try {
			int w=400;
			int h=w/2;
			toDisk = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
			
			Canvas a=new Canvas(toDisk);
			//            canvas.setBitmap(toDisk);
			//            a.drawCircle(w/2,0,h,paint);
			Paint r=new Paint();
			r.setColor(Color.BLACK);
			a.drawRect(new RectF(0,0,w,h), r);
			drawArcGrad(a, new cPoint(w,h), new cPoint(0.5f,0f), h, values);
			//            String path = Environment.getExternalStorageDirectory().toString();

			//            File file = new File(path, "arun1.jpg"); // the File to save to
			//            toDisk.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

		} catch (Exception ex) {
			Log.d("Asd", "asd");

		}
		return toDisk;
	}

	void drawRect(Canvas canvas, cPoint p1, cPoint p2, Paint paint){

		RectF a=new RectF(w*p1.x, h*p1.y, w*p2.x, h*p2.y);
		canvas.drawRect(a, paint);
	}
	void drawCircle(Canvas canvas, cPoint point, float radius){
		drawCircle(canvas, point, radius, paint);
	}
	void drawCircle(Canvas canvas, cPoint point, float radius, Paint paint){
		canvas.drawCircle(point.x*w,point.y*h, radius, paint);
	}
	void drawLine(Canvas canvas, cPoint mot,cPoint hai){
		drawLine(canvas, mot, hai, paint);
	}
	void drawLine(Canvas canvas, cPoint mot,cPoint hai, Paint paint){
		canvas.drawLine(mot.x*w,mot.y*h,hai.x*w,hai.y*h, paint);
	}
	void drawArc(Canvas canvas, cPoint p, float radius1, float start, float end, Paint paint){
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
	cPoint calculateEndPoint(cPoint base, float length, float angle){
		double x_, y_;
		double ang_rad= angle*Math.PI/180;
		x_=length*Math.cos(ang_rad);
		y_=length*Math.sin(ang_rad);
		return new cPoint((float) (base.x+x_), 
				(float) (base.y+y_));
	}

	void drawText(Canvas canvas,String text, float x, float y, Paint paint){
		canvas.drawText(text, x*w, y*h, paint);

	}
	void beep(){
		ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,  
				(int)(ToneGenerator.MAX_VOLUME * 0.7));
		toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 200); 
	}
	void drawArcGrad(Canvas canvas,cPoint dimen, cPoint center, float radius, ArrayList<cArc> value ){
		for (cArc point: value){
			cPoint bat=new cPoint(center.x*dimen.x, center.y*dimen.y);
			cPoint end=calculateEndPoint(bat, radius, point.angle);
			Paint paint=new Paint();
			int tu=(int) (255*point.value);
			paint.setColor(Color.rgb(0,255-tu,tu));

			canvas.drawLine(bat.x,bat.y,end.x,end.y, paint);
		}
	}
	RectF convertRect(cPoint p1, cPoint p2){
		return new RectF(w*p1.x, h*p1.y, w*p2.x, h*p2.y);
	}
	float angle=30f;
	int frameRate=60;
	float velocity=0.1f;
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		angle+=velocity;
		if (angle>90 || angle<0) velocity=-velocity;
		if (Math.abs(angle-70)<=10)
			beep();
		w = (float) getWidth();
		h = (float) getHeight();


		cPoint mot= new cPoint(0.05f,0.25f);
		cPoint hai= new cPoint(0.2f,0.25f);
		cPoint ba1=new cPoint(0.4f, 0.5f);
		cPoint ba=  calculateEndPoint(hai, 0.5f, angle);
		cPoint bon= new cPoint(ba.x+0.4f,ba.y+0.05f);

		Paint lightP=new Paint();
		lightP.setColor(Color.rgb(255,204,204));
		Paint darkP=new Paint();
		darkP.setColor(Color.BLACK);
		darkP.setTextSize(30);

		//		drawArcGrad(canvas, hai, 0.5f, values);
		//		drawArc(canvas, hai, 0.5f, 0,180, lightP);

		drawCircle(canvas, mot, 10f);
		drawCircle(canvas, hai, 10f);
		drawLine(canvas,mot,hai);
		drawLine(canvas,hai,ba);
		drawRect(canvas,ba,bon, lightP);
		drawText(canvas,String.format("   %1$.3f",
				180-angle), ba.x, bon.y, darkP);
		Paint paintBit=new Paint();
		paintBit.setAntiAlias(true);;
		paintBit.setFilterBitmap(true);
		paintBit.setDither(true);
//		convertRect(hai,ba1)
//		drawRect(canvas, hai, ba1, lightP);
//		canvas.drawRect(new RectF(0,0,background.getWidth(),background.getHeight()), paint);
		canvas.drawRect(new Rect(0,0,400,200), lightP);
		canvas.drawBitmap(background,0,0,null);
//				Bitmap.createScaledBitmap( background,300,150,true),null,new Rect(0,0,300,150),null);
		
		this.postInvalidateDelayed(1000/frameRate);
	}
}
