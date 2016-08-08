package com.example.com.hdl.first_tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class subjectManager extends ActionBarActivity {
	ListView listView;
	ArrayList<Subject> listSubject = new ArrayList<Subject>();
	MenuItem deleteMenu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subjectmenu, menu);
		getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		deleteMenu = menu.findItem(R.id.action_delete);
		deleteMenu.setVisible(false);
		return (super.onCreateOptionsMenu(menu));

	}

	public void notifyDataSet() {
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsubject);
		listView = (ListView) findViewById(R.id.list);

		UsersAdapter adapter = new UsersAdapter(this, listSubject);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
				final int selection = index;
				final Dialog tmpDialog = new Dialog(subjectManager.this);
				tmpDialog.setContentView(R.layout.subjectdetail);
				tmpDialog.setTitle(listSubject.get(index).name);
				tmpDialog.show();
				Button dialogButton = (Button) tmpDialog.findViewById(R.id.btnUpdate);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tmpDialog.dismiss();
					}
				});
				((Button) tmpDialog.findViewById(R.id.btnDelete)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listSubject.remove(selection);
						notifyDataSet();
						tmpDialog.dismiss();
					}
				});

				//
				// });

				return true;
			}
		});
		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.putExtra("subject", listSubject.get(position));
				startActivity(i);
			}

		});
		showAll();
	}

	String databaseName = "LoiStorage";
	SQLiteDatabase dataOpen;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case (R.id.action_create_db):

			SQLiteDatabase lite = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
			try {
				lite.execSQL("DROP TABLE SubjectInfo");
				lite.execSQL("DROP TABLE Experiment");
				// int affect = lite.delete("SubjectInfo", null, null);
				// lite.delete("Experiment", null, null);
			} catch (SQLiteException e) {
			}
			lite.execSQL("CREATE TABLE IF NOT EXISTS SubjectInfo(subjectID INT PRIMARY KEY,"
					+ "name VARCHAR,age INT, gender VARCHAR(1));");
			lite.execSQL("INSERT INTO SubjectInfo VALUES(1,'loihuynh2',23, 'M');");
			lite.execSQL("CREATE TABLE IF NOT EXISTS Experiment(SampleID INT PRIMARY KEY,"
					+ " angle REAL, force REAL, subjectID INT, FOREIGN KEY(subjectID) REFERENCES SubjectInfo(subjectID));");
			lite.execSQL("INSERT INTO Experiment VALUES(1,2.3,3.2,1);");
			Toast.makeText(getBaseContext(), "Done!", Toast.LENGTH_LONG).show();
			break;
		case (R.id.action_export_db):
			try {
				File sd = Environment.getExternalStorageDirectory();

				if (sd.canWrite()) {
					String backupDBPath = "/cong.db";
					File currentDB = getApplicationContext().getDatabasePath(databaseName);
					File backupDB = new File(sd, backupDBPath);

					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Toast.makeText(getBaseContext(), "Write to: " + backupDB.toString(), Toast.LENGTH_LONG).show();

				}
			} catch (Exception e) {

				Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

			}
			break;
		case (R.id.action_detail):
			showAll();

			break;
		// listSubject.add("Huynh Duc Loi");
		// notifyDataSet();
		}
		return super.onOptionsItemSelected(item);
	}

	void showAll() {
		try {
			dataOpen = SQLiteDatabase.openDatabase(getApplicationContext().getDatabasePath(databaseName).getPath(),
					null, SQLiteDatabase.OPEN_READWRITE);
			Cursor resultSet = dataOpen.rawQuery("Select * from SubjectInfo", null);
			listSubject.clear();
			resultSet.moveToFirst();
			while (!resultSet.isAfterLast()) {
				listSubject.add(
						new Subject(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3).charAt(0))); // name
				resultSet.moveToNext();
			}
			notifyDataSet();
		} catch (SQLiteException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}