package com.example.com.hdl.first_tutorial;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


@SuppressLint("DrawAllocation")
public class customCanvas extends View {
	

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
	int good= Color.rgb(51,148,46);
	int medium=Color.rgb(144,148,46);
	int bad=Color.rgb(148, 51,46);
	float thres_GOOD=3f;
	float thres_MEDIUM= 1f;
	drawingLoi draw;
	public customCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(8);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		draw=new drawingLoi(paint);
		

	}
	public void refreshBackground(){
		background=openPicture();
		this.postInvalidate();
	}
	Bitmap openPicture(){
		Bitmap toDisk = null;
		File file = new File(Environment.getExternalStorageDirectory().toString(),
				"arun1.txt");
		float[] metricArray=new float[MainActivity.MAX_ANGLE];
		MainActivity.resetMetric(metricArray);
		if(file.exists())      
		{
			BufferedReader br;

			try {
				br = new BufferedReader(new FileReader(file));
				String line;   
				while ((line = br.readLine()) != null) {
					String[] parts = line.split(" ");
					metricArray[Integer.parseInt(parts[0])]=Float.parseFloat(parts[1]);


				}
				br.close() ;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  

			if (1==1)
				try {

					for (int i=0; i<MainActivity.MAX_ANGLE-1;i++)
						if (metricArray[i]<MainActivity.NON){
							int j;
							for (j=i+1; j<MainActivity.MAX_ANGLE;j++)
								if (metricArray[j]<MainActivity.NON)
									break;
							if (j!=MainActivity.MAX_ANGLE)
								for (int k=i+1; k<j;k++)
									metricArray[k]=metricArray[i];
								
							i=j-1;
									
								
						}
					values.clear();
					for (int i=0; i<MainActivity.MAX_ANGLE;i++)
						if (metricArray[i]<MainActivity.NON)
							values.add(new cArc(
									180-getAngleFromRaw(i/100.0f), 
									metricArray[i]));

					int w=400;
					int h=w/2;
					toDisk = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
					Canvas a=new Canvas(toDisk);
					drawArcGrad(a, new cPoint(w,h), new cPoint(0.5f,0f), h, values);
					//			drawArcGradMean(a, new cPoint(w,h), new cPoint(0.5f,0f), h, values);
					//			String path = Environment.getExternalStorageDirectory().toString();
					//			File file = new File(path, "arun1.jpg"); // the File to save to
					//			toDisk.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

				} catch (Exception ex) {
					Log.d("Asd", "asd");

				}
		}

		return toDisk;
	}

	
	
	void beep(){
		ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,  
				(int)(ToneGenerator.MAX_VOLUME * 0.7));
		toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 200); 
	}
	void drawArcGrad(Canvas canvas,cPoint dimen, cPoint center, float radius, ArrayList<cArc> value ){
		for (cArc point: value)
		{
			cPoint bat=new cPoint(center.x*dimen.x, center.y*dimen.y);
			cPoint end=calculateEndPoint(bat, radius, point.angle);
			Paint paint=draw.getPaintColor( getColor(point.value));
			paint.setStrokeWidth(8);
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
	void drawArcGradMean(Canvas canvas,cPoint dimen, cPoint center, 
			float radius, ArrayList<cArc> value ){
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
	
	float angle=30f;
	int frameRate=60;
	float velocity=0.1f;
	float raw=0f;
	float force=0f;
	float metric;
	float getAngleFromRaw(float raw){
		float minAngle=55;
		float maxAngle=180;
		return (maxAngle-minAngle)*raw+minAngle;
	}
	public float updateAngleAndForce(float angle1, float force){

		this.raw=angle1;
		angle=getAngleFromRaw(angle1);
		this.force=force;
		this.postInvalidate();

		metric=(1-force)/MainActivity.clamp(raw, 0.1f, 1);
		return metric;
	}
	cPoint calculateEndPoint(cPoint base, float length, float angle){
		double x_, y_;
		double ang_rad= angle*Math.PI/180;
		x_=length*Math.cos(ang_rad);
		y_=length*Math.sin(ang_rad);
		return new cPoint((float) (base.x+x_), 
				(float) (base.y+y_));
	}
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);

		w = (float) getWidth();
		h = (float) getHeight();

		draw.setAll(canvas, w, h);
		cPoint mot= new cPoint(0.05f,0.25f);
		cPoint hai= new cPoint(0.2f,0.25f);

		float width=0.55f;
		cPoint origin= new cPoint(hai.x-width, hai.y);
		cPoint endpoint=new cPoint(hai.x+width, hai.y+width);
		if (background!=null) 
			canvas.drawBitmap(background,null,draw.convertRect(origin, endpoint),null);

		Paint lightP=new Paint();
		lightP.setColor(Color.rgb(255,204,204));
		Paint darkP=new Paint();
		darkP.setColor(Color.BLACK);
		darkP.setTextSize(30);

		draw.drawCircle(mot, 10f);
		draw.drawCircle( hai, 10f);
		draw.drawLine(mot,hai);
		cPoint ba=  calculateEndPoint(hai, 0.65f, 180-angle);
		cPoint bon= new cPoint(ba.x+0.3f,ba.y+0.05f);
		//		angle+=velocity;
		//		if (angle>90 || angle<0) velocity=-velocity;
		//		if (Math.abs(angle-70)<=10)
		//			beep();

		//		int color=bad;
		//		if (angle>thres_GOOD) color=good;
		//		else if (angle>thres_MEDIUM) color=medium;


		draw.drawLine(hai,ba);
		draw.drawRect(ba,bon, draw.getPaintColor(getColor(raw)));
		draw.drawText(String.format("   %2$.3f",
				force,raw) , ba.x, bon.y, darkP);
		//small force, but can move big angle.

		//		this.postInvalidateDelayed(1000/frameRate);
	}
}
