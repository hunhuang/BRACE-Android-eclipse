package com.example.com.hdl.first_tutorial;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UsersAdapter extends ArrayAdapter<Subject> {
    public UsersAdapter(Context context, ArrayList<Subject> users) {
       super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Subject user = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_subject, parent, false);
       }
       // Lookup view for data population
       TextView name = (TextView) convertView.findViewById(R.id.name);
       TextView age = (TextView) convertView.findViewById(R.id.age);
       // Populate the data into the template view using the data object
       name.setText(user.name);
       age.setText(user.age+"");
       // Return the completed view to render on screen
       return convertView;
   }
}
