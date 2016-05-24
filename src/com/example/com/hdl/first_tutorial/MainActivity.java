package com.example.com.hdl.first_tutorial;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
	private OutputStream outputStream;
	private InputStream inStream;

	private void init() throws IOException {
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null) {
			if (blueAdapter.isEnabled()) {
				Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

				if(bondedDevices.size() > 0) {
					Object[] devices = (Object []) bondedDevices.toArray();
					BluetoothDevice device = (BluetoothDevice) devices[0];
					ParcelUuid[] uuids = device.getUuids();
					BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
					socket.connect();
					outputStream = socket.getOutputStream();
					inStream = socket.getInputStream();
				}

				Log.e("error", "No appropriate paired devices.");
			} else {
				Log.e("error", "Bluetooth is disabled.");
			}
		}
	}

	public void write(String s) throws IOException {
		outputStream.write(s.getBytes());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainView = (TextView) findViewById(R.id.text1);
		connectButton=(Button)findViewById(R.id.connect);
		chart = (LineChart) findViewById(R.id.chart);
		connectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) 
			{
				try {
					init();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"Error Loi",Toast.LENGTH_SHORT).show();
				}
				try {
					write("asdlasd");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"Error Loi2",Toast.LENGTH_SHORT).show();
				}
				
				mainView.append("Connecting to device...");
				
				//				Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//				startActivityForResult(turnOn, 0); 
//				Set<BluetoothDevice> pairedDevices;
//				
//				ArrayList list = new ArrayList();
//				for(BluetoothDevice bt : pairedDevices){
//					mainView.append(bt.getName()+"\n");
//					list.add(bt.getName());}
//
//				ListView lv = (ListView)findViewById(R.id.listview);
				//				ArrayAdapter adapter = new ArrayAdapter(view,R.layout.simpletext, list);
				//				lv.setAdapter(adapter);
			}
		});
		


	}

	public void runningRefresh(View v){
		mainView.append("Refreshing");


		ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
		ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
		Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
		valsComp1.add(c1e1);
		Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
		valsComp1.add(c1e2);
		Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
		valsComp2.add(c2e1);
		Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
		valsComp2.add(c2e2);
		LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
		setComp1.setAxisDependency(AxisDependency.LEFT);
		LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
		setComp2.setAxisDependency(AxisDependency.LEFT);
		ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
		dataSets.add(setComp1);
		dataSets.add(setComp2);

		ArrayList<String> xVals = new ArrayList<String>();
		xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q"); 

		LineData data = new LineData(xVals, dataSets);
		chart.setData(data);
		chart.invalidate(); // refresh
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
