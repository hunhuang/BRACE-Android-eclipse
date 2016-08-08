package com.example.com.hdl.first_tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class heightBar extends View{
 	public void setVal(float val){
 		if (val>max) val=max;
 		if (val<min) val=min;
 		currentVal=val;
 	}
 	drawingLoi draw;
 	Paint paint;
    public heightBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(8);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		draw=new drawingLoi(paint);
    	
    }
    public int height=500;
    public int width=400;
    public int start_x=20;
    public int start_y=20;
    public float percentOut=0.2f;
    float wR=0.4f;
    float wBar=0.2f;
    public int howDec=3;
    float margin=0.01f;
    float heightIndicator=0.3f;
    float percentW=0.5f;
    public float currentVal=56f;
    public float currentMax=80f;
    public float max=500f;
    public float min=0f;
    public int fontSize=15;
    cPoint mot=new cPoint(0,0);
    cPoint hai=new cPoint(1,1);
    @Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);

		draw.setAll(canvas, (float) getWidth(), (float) getHeight());
		draw.drawRect(mot,hai, draw.getPaintColor(Color.GREEN));
//		
//        g.setColor(back);
//        fillRect(g,wR,0,wBar,1);
//        
//        g.setColor(end);
//        fillRect(g,wR,0,wBar,percentOut);
//        fillRect(g,wR,1-percentOut,wBar,percentOut);
//        
//        
//        g.setColor(Color.BLACK);
//        drawRect(g,wR, 0,wBar,1);
//        //draw current position
//        float cur=1.0f*(currentVal-min)/(max-min);
//        float curMax=1.0f*(currentMax-min)/(max-min);
//        
//        g.setColor(panel);
////        drawAndFill(g,margin,cur-heightIndicator/2, percentW*(wR-2*margin),heightIndicator);
////        drawLine(g, wR-margin-percentW*(wR-2*margin), cur-heightIndicator/2, wR-margin, cur);
////        drawLine(g, wR-margin-percentW*(wR-2*margin), cur+heightIndicator/2, wR-margin, cur);
//        
//        int n=5;
//        float[] x={margin,
//        		margin+percentW*(wR-2*margin),
//        		wR-margin,
//        			margin+percentW*(wR-2*margin),
//        			margin};
//        float[] y={cur-heightIndicator/2, 
//        		cur-heightIndicator/2,
//        		cur,
//        		cur+heightIndicator/2,
//        		cur+heightIndicator/2};
//        float[] xp=new float[n];
//        float[] yp=new float[n];
//        
//        for (int i=0;i<n;i++){
//        	xp[i]=wR+wBar/2+(wR+wBar/2-x[i]);
//        	yp[i]=y[i]-cur+curMax;
//        }
//        
//        drawPolygon(g,Color.BLACK,x, y,n);
//        
//        drawPolygon(g,Color.BLACK,xp, yp,n);
//        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize)); 
//        g.setColor(Color.BLACK);
//        drawString(g,String.format(" Max: %."+howDec+"f",currentVal), (xp[2]+xp[1])/2,curMax );
//        drawString(g,String.format("   %."+howDec+"f",currentMax), x[0],cur );
	}
//    void drawPolygon(Graphics g,Color color, float[] x, float[] y, int n){
//    	int[] ax=new int[n];
//    	int[] ay=new int[n];
//    	for (int i=0;i<n;i++){
//    		if (x[i]>1) x[i]=1;
//    		if (x[i]<0) x[i]=0;
//    		if (y[i]>1) y[i]=1;
//    		if (y[i]<0) y[i]=0;
//    		ax[i]=(int) (x[i]*width+start_x);
//    		ay[i]=(int)(y[i]*height+start_y);
//    	}
//    	g.fillPolygon(ax,ay,n);
//    	Color back=g.getColor();
//    	g.setColor(color);
//    	Graphics2D g2 = (Graphics2D) g;
//    	    g2.setStroke(new BasicStroke(3));
//    	g2.setStroke(new BasicStroke(5));
//    	g2.drawPolygon(ax,ay,n);
//    	g.setColor(back);
//    }
//    Color back=new Color(220,220,220);
//    Color end=new Color(255,255,224);
//    Color panel=end;
//  
	
}
