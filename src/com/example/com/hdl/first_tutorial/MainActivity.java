package com.example.com.hdl.first_tutorial;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {
	Button connectButton;
	TextView mainView;
	LineChart chart ;
	OutputStream outputStream;
	InputStream inStream;
	BluetoothSocket socket;
	private void init() throws IOException {
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null) {
			if (blueAdapter.isEnabled()) {
				Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

				if(bondedDevices.size() > 0) {
					Object[] devices = (Object []) bondedDevices.toArray();
					BluetoothDevice device = (BluetoothDevice) devices[0];
					appendMessage(device.getName());
					ParcelUuid[] uuids = device.getUuids();
					socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
					socket.connect();
					outputStream = socket.getOutputStream();
					inStream = socket.getInputStream();
				}
				appendMessage("Connection done");
				Log.e("error", "No appropriate paired devices.");
			} else {
				Log.e("error", "Bluetooth is disabled.");
			}
		}
	}
	int countMess=0;
	void appendMessage(String a){
		countMess++;
		if (countMess>10){
			mainView.setText("");
			countMess=0;
		}
		mainView.append(a);
		
	}
	public void write(String s) throws IOException {
		outputStream.write(s.getBytes());
	}
	class Task implements Runnable {
		@Override
		public void run() {
			Scanner s=new Scanner(inStream);
			s.useDelimiter("\n");
			while (true && inStream != null) {
				//				bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
				final String tam=s.next();
				final String[] parts = tam.split(" ");
				
				//				String tam= new String(buffer,"UTF-8");
				runOnUiThread(new Runnable() {
				      @Override
				      public final void run() {
//				    	  appendMessage(tam);
				    	  addPoint(Float.parseFloat(parts[1]));
				      } 
				    });
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
	void run() {
		new Thread(new Task()).start();

		//		final int BUFFER_SIZE = 1024;
		//		byte[] buffer = new byte[BUFFER_SIZE];
		//		int bytes = 0;
		//		Scanner s=new Scanner(inStream);
		//		s.useDelimiter("\n");
		//		while (true) {
		//			//				bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
		//			String tam=s.next();
		//			//				String tam= new String(buffer,"UTF-8");
		//			appendMessage(tam +"\n");
		//		}
	}
	public void disconnectBluetooth(View v){
		if (inStream != null) {
			try {inStream.close();} catch (Exception e) {}
			inStream = null;
		}

		if (outputStream != null) {
			try {outputStream.close();} catch (Exception e) {}
			outputStream = null;
		}

		if (socket != null) {
			try {socket.close();} catch (Exception e) {}
			socket = null;
		}
		appendMessage("Disconnected\n");
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainView = (TextView) findViewById(R.id.text1);
		connectButton=(Button)findViewById(R.id.connect);
		chart = (LineChart) findViewById(R.id.chart);

		runChart();


	}
	public void ConnectClick(View v){
		appendMessage("Connecting to device...");

		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(),"Error Loi",Toast.LENGTH_SHORT).show();
		}
		//				try {
		////					write("asdlasd");
		//				} catch (IOException e) {
		//					// TODO Auto-generated catch block
		//					Toast.makeText(getApplicationContext(),"Error Loi2",Toast.LENGTH_SHORT).show();
		//				}


		//				Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		//				startActivityForResult(turnOn, 0); 
		//				Set<BluetoothDevice> pairedDevices;
		//				
		//				ArrayList list = new ArrayList();
		//				for(BluetoothDevice bt : pairedDevices){
		//					appendMessage(bt.getName()+"\n");
		//					list.add(bt.getName());}
		//
		//				ListView lv = (ListView)findViewById(R.id.listview);
		//				ArrayAdapter adapter = new ArrayAdapter(view,R.layout.simpletext, list);
		//				lv.setAdapter(adapter);
	}
	ArrayList<Entry> array_data=new ArrayList<Entry>();
	
	int posi=-1;
	void addPoint(float val){
		posi++;
		Entry mot = new Entry(val, posi);
		array_data.add(mot);
		if (posi>=numVisible){
			posi--;
			array_data.remove(0);
			for (Entry a:array_data){
				a.setXIndex(a.getXIndex()-1);
			}
			
		}
		
		
		chart.notifyDataSetChanged();
		chart.invalidate(); // refresh
	}
	int numVisible=25;
	
	
	void runChart(){
		LineDataSet setComp1 =new LineDataSet(array_data, "First Sensor");
		setComp1.setAxisDependency(AxisDependency.LEFT);

		ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
		dataSets.add(setComp1);

		YAxis leftAxis = chart.getAxisLeft();
		
		chart.getAxisRight().setEnabled(false);
		
		leftAxis.setAxisMaxValue(5);
		leftAxis.setAxisMinValue(0);
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i=0; i<numVisible;i++)
		{
			xVals.add(i+"p");
		}
		LineData data = new LineData(xVals, dataSets);
		data.setDrawValues(false);
		
		chart.setData(data);
		chart.setDescription("");
		
		for (int i=0;i<numVisible;i++)
			addPoint((float) Math.random());
		
	}
	public void runningStream(View v){
		appendMessage("Streaming");
		
		run();


	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			mainView.setText("Huynh Duc Loi");
		}
		return super.onOptionsItemSelected(item);
	}
}
