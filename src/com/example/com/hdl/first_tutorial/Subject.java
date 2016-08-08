package com.example.com.hdl.first_tutorial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import java.io.Serializable;
public class Subject implements Serializable  {
	String name;
	int age;
	char gender;
	Date lastVisit;
	List<Float> angles;
	List<Float> forces;
	public Subject(String name, int age, char gender){
		this.name=name;
		this.age=age;
		this.gender=gender;
		
	}
	public void loadExperiment(Cursor data){
		angles=new ArrayList<Float>();
		forces=new ArrayList<Float>();
		while (data.isAfterLast()==false){
			angles.add(data.getFloat(1));
			forces.add(data.getFloat(2));
			
		}
		
	}
}

