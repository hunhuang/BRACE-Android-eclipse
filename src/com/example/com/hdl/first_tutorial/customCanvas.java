package com.example.com.hdl.first_tutorial;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


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
	int good= Color.GREEN;
	int medium=Color.YELLOW;
	int bad=Color.RED;
	float thres_GOOD=70f;
	float thres_MEDIUM= 40f;
	
	public customCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(8);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		for (float i=0; i<90; i+=0.1){
			values.add(new cArc(i, i));///180f
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

//			Paint r=new Paint();
			
			drawArcGradMean(a, new cPoint(w,h), new cPoint(0.5f,0f), h, values);
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
	float average(List<cArc> a){
		float sum=0;
		for (cArc b:a){
			sum+=b.value;
		}
		return sum/a.size();
	}
	int getColor(float value){
		int color=bad;
		if (value>thres_GOOD) color=good;
		else if (value>thres_MEDIUM) color=medium;
		return color;
	}
	//draw arc with clear separation
	void drawArcGradMean(Canvas canvas,cPoint dimen, cPoint center, float radius, ArrayList<cArc> value ){
		int length=value.size();
		int start1= length/3;
		int start2= length*2/3;
		
		List<cArc> portion1= value.subList(0, start1);
		List<cArc> portion2= value.subList(start1+1, start2);
		List<cArc> portion3= value.subList(start2, length-1);
		List<List<cArc>> mang=Arrays.asList(portion1, portion2, portion3);
		
		int[] color={getColor(average(portion1)),
					getColor(average(portion2)),
					getColor(average(portion3))};
		for (int i=0;i<3;i++){
			List<cArc> portion= mang.get(i);
			for (cArc point: portion){
				cPoint bat=new cPoint(center.x*dimen.x, center.y*dimen.y);
				cPoint end=calculateEndPoint(bat, radius, point.angle);
				Paint paint=new Paint();
				paint.setColor(color[i]);
				canvas.drawLine(bat.x,bat.y,end.x,end.y, paint);
			}
		}
		
	}
	RectF convertRect(cPoint p1, cPoint p2){
		return new RectF(w*p1.x, h*p1.y, w*p2.x, h*p2.y);
	}
	Paint getPaintColor(int color){
		Paint p=new Paint();
		p.setColor(color);;
		return p;
	}
	float angle=30f;
	int frameRate=60;
	float velocity=0.1f;
	float raw=0f;
	public void updateAngle(float raw){
		this.raw=raw;
		angle=(raw/5f)*180;
		this.postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
		w = (float) getWidth();
		h = (float) getHeight();


		cPoint mot= new cPoint(0.05f,0.25f);
		cPoint hai= new cPoint(0.2f,0.25f);
		
		float width=0.3f;
		cPoint origin= new cPoint(hai.x-width, hai.y);
		cPoint endpoint=new cPoint(hai.x+width, hai.y+width);
		canvas.drawBitmap(background,null,convertRect(origin, endpoint),null);
		
		Paint lightP=new Paint();
		lightP.setColor(Color.rgb(255,204,204));
		Paint darkP=new Paint();
		darkP.setColor(Color.BLACK);
		darkP.setTextSize(30);

		drawCircle(canvas, mot, 10f);
		drawCircle(canvas, hai, 10f);
		drawLine(canvas,mot,hai);
		cPoint ba=  calculateEndPoint(hai, 0.5f, angle);
		cPoint bon= new cPoint(ba.x+0.4f,ba.y+0.05f);
//		angle+=velocity;
//		if (angle>90 || angle<0) velocity=-velocity;
//		if (Math.abs(angle-70)<=10)
//			beep();
		
		int color=bad;
		if (angle>thres_GOOD) color=good;
		else if (angle>thres_MEDIUM) color=medium;
		
		drawLine(canvas,hai,ba);
		drawRect(canvas,ba,bon, getPaintColor(color));
		drawText(canvas,String.format("   %1$.0f",
				angle), ba.x, bon.y, darkP);
		
		
//		this.postInvalidateDelayed(1000/frameRate);
	}
}
