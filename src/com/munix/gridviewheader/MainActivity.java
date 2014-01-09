package com.munix.gridviewheader;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private GridView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list = (GridView)this.findViewById(R.id.list);
		list.setNumColumns(2);
		
		String strings[] = new String[100];
		for (int i = 0; i < strings.length; i++)
			strings[i] = new String("Pos " + i);
		
		View header = View.inflate( this, R.layout.item, null);
		list.addHeaderView(header);
		list.setNumColumns(2);
		
		TestAdapter adapter = new TestAdapter(this, strings);
		list.setAdapter(adapter);
	}
	
	public void fun( View v )
	{
		Toast.makeText(v.getContext(), "Click", Toast.LENGTH_SHORT).show();
		list.removeHeaderView();
	}
}
