package com.example.com.hdl.first_tutorial;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {
	Button connectButton;
	TextView mainView;
//	LineChart chart;
	OutputStream outputStream;
	InputStream inStream;
	BluetoothSocket socket;
	Scanner source;
	customCanvas angleChart;
	static public int MAX_ANGLE = 101;
	float[] metricArray = new float[MAX_ANGLE];
	static public int NON = 100;
	SeekBar seekbar;ProgressBar progressF;
	private void init() throws IOException {
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null) {
			if (blueAdapter.isEnabled()) {
				Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

				if (bondedDevices.size() > 0) {
					Object[] devices = (Object[]) bondedDevices.toArray();
					BluetoothDevice device = (BluetoothDevice) devices[0];
					appendMessage(device.getName());
					ParcelUuid[] uuids = device.getUuids();
					socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
					socket.connect();
					outputStream = socket.getOutputStream();
					inStream = socket.getInputStream();
				}
				// appendMessage("Connection done");
				Log.e("error", "No appropriate paired devices.");
			} else {
				Log.e("error", "Bluetooth is disabled.");
			}
		}
		resetMetric(metricArray);
	}

	public static void resetMetric(float[] array) {
		for (int i = 0; i < MAX_ANGLE; i++) {
			array[i] = NON;
		}
	}

	int countMess = 0;

	void appendMessage(String a) {
		// countMess++;
		// if (countMess>10){
		// mainView.setText("");
		// countMess=0;
		// }
		mainView.setText(a);

	}

	public void write(String s) throws IOException {
		outputStream.write(s.getBytes());
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	float minV = 2.94f;
	float maxV = 0f;
	float lastAngleRaw;
	float lastAvgF;
	class Task implements Runnable {
		@Override
		public void run() {
			source = new Scanner(inStream);
			source.useDelimiter("\n");
			while (true && inStream != null) {
				// bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
				try {
					final String tam = source.next();
					final String[] parts = tam.split(" ");

					if (parts.length == 4)
						runOnUiThread(new Runnable() {
							@Override
							public final void run() {
								// appendMessage(tam);
								float mot =Math.abs( Float.parseFloat(parts[1]));
								float hai =Math.abs(Float.parseFloat(parts[2]));
								float avgF = (mot+hai)/2;
								progressF.setProgress((int)avgF);
								lastAngleRaw = Float.parseFloat(parts[3]);
								appendMessage(String.format("%.3f lbs- %.3f- %.3f lbs", mot,hai, avgF));

								float minF = 0.64f;
								float maxF = 3.84f;

								if (forceThreshold>lastAvgF && avgF>forceThreshold){
									ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);             
									toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);  
								}
								lastAvgF=avgF;
								float angle = clamp((lastAngleRaw - minV) / (maxV - minV), 0, 1);

								float metric = angleChart.updateAngleAndForce(angle, avgF);
								addPoint(avgF);
								addTolist(angle, metric);

							}
						});
				} catch (Exception e) {
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void taringScale(View v) {
		try {
			write("+");
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Cannot tare", Toast.LENGTH_SHORT).show();
		}
	}

	public void resetAngle(View v) {
		maxV = lastAngleRaw;
	}

	void addTolist(float angle, float metric) {
		int index = (int) (angle * 100);
		if (metricArray[index] > metric)
			metricArray[index] = metric;
	}

	public void disconnectBluetooth() {
		if (inStream != null) {
			try {
				inStream.close();
			} catch (Exception e) {
			}
			inStream = null;
		}
		appendMessage("In stream gone\n");
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
			outputStream = null;
		}
		appendMessage("Out Stream gone\n");
		// if (source!=null){
		// try{ source.close();} catch (Exception e){}
		// }
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
			}
			socket = null;
		}
		appendMessage("Socket closed\n");
		appendMessage("Disconnected\n");

	}
	float forceThreshold;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView name = (TextView) findViewById(R.id.name1);
		Intent i = getIntent();
		if (i != null) {
			Subject sub = (Subject) i.getSerializableExtra("subject");
			name.setText(sub.name+"-"+sub.age);
		}
		mainView = (TextView) findViewById(R.id.text1);
//		connectButton = (Button) findViewById(R.id.connect);
////		chart = (LineChart) findViewById(R.id.chart);
//		angleChart = (customCanvas) findViewById(R.id.signature_canvas);
//		experText =(TextView) findViewById(R.id.exper1);
//		seekbar=(SeekBar) findViewById(R.id.seekBar);
//		progressF=(ProgressBar) findViewById(R.id.progressF);
//		final TextView forceThres=(TextView) findViewById(R.id.forceThres);
//		forceThreshold=40f;forceThres.setText(forceThreshold+" lbs");
//    	seekbar.setProgress((int)forceThreshold);
//		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
//
//		    @Override       
//		    public void onStopTrackingTouch(SeekBar seekBar) {      
//		        // TODO Auto-generated method stub      
//		    }       
//
//		    @Override       
//		    public void onStartTrackingTouch(SeekBar seekBar) {     
//		        // TODO Auto-generated method stub      
//		    }       
//
//		    @Override       
//		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
//		        // TODO Auto-generated method stub      
//
////		    	forceThres.setText(progress+" lbs");
//		    	forceThreshold=progress;
//
//		    }       
//		}); 
//		runChart();

	}

	public void ConnectClick(View v) {
		connectButton.setEnabled(false);
		if (connectButton.getText() == getResources().getString(R.string.str_connect)) {
			appendMessage("Connecting to device...");

			try {
				init();
				connectButton.setText(getResources().getString(R.string.str_disconnect));
				angleChart.refreshBackground();
				appendMessage("Streaming");
				new Thread(new Task()).start();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Error Loi", Toast.LENGTH_SHORT).show();
			}
		} else {
			connectButton.setText(getResources().getString(R.string.str_connect));

			try {
				FileWriter out = new FileWriter(
						new File(Environment.getExternalStorageDirectory().toString(), "arun1.txt"));
				for (int i = 0; i < MAX_ANGLE; i++) {
					out.write(String.format("%1$d %2$.2f\n", i, metricArray[i]));
				}
				out.close();
			} catch (IOException e) {

			}

			disconnectBluetooth();
		}
		connectButton.setEnabled(true);
		// try {
		//// write("asdlasd");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(getApplicationContext(),"Error
		// Loi2",Toast.LENGTH_SHORT).show();
		// }

		// Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// startActivityForResult(turnOn, 0);
		// Set<BluetoothDevice> pairedDevices;
		//
		// ArrayList list = new ArrayList();
		// for(BluetoothDevice bt : pairedDevices){
		// appendMessage(bt.getName()+"\n");
		// list.add(bt.getName());}
		//
		// ListView lv = (ListView)findViewById(R.id.listview);
		// ArrayAdapter adapter = new ArrayAdapter(view,R.layout.simpletext,
		// list);
		// lv.setAdapter(adapter);
	}

	ArrayList<Entry> array_data = new ArrayList<Entry>();

	int posi = -1;

	void addPoint(float val) {
		posi++;
		Entry mot = new Entry(val, posi);
		array_data.add(mot);
		if (posi >= numVisible) {
			posi--;
			array_data.remove(0);
			for (Entry a : array_data) {
				a.setXIndex(a.getXIndex() - 1);
			}

		}

//		chart.notifyDataSetChanged();
//		chart.invalidate(); // refresh
	}

	int numVisible = 25;

	void runChart() {
//		LineDataSet setComp1 = new LineDataSet(array_data, "First Sensor");
//		setComp1.setAxisDependency(AxisDependency.LEFT);
//
//		ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//		dataSets.add(setComp1);
//
//		YAxis leftAxis = chart.getAxisLeft();
//
//		chart.getAxisRight().setEnabled(false);
//
//		leftAxis.setAxisMaxValue(200);
//		leftAxis.setAxisMinValue(0);
//		ArrayList<String> xVals = new ArrayList<String>();
//		for (int i = 0; i < numVisible; i++) {
//			xVals.add(i + "p");
//		}
//		LineData data = new LineData(xVals, dataSets);
//		data.setDrawValues(false);
//
//		chart.setData(data);
//		chart.setDescription("");

		for (int i = 0; i < numVisible; i++)
			addPoint((float) Math.random());

	}

	public void runningStream(View v) {

	}
	int exper_num=0;
	TextView experText;
	public void left_click(View v) {
		if (exper_num>0) exper_num--;
		experText.setText(String.format("Experiment %d", exper_num));
	}
	public void right_click(View v) {
		exper_num++;
		experText.setText(String.format("Experiment %d", exper_num));
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
