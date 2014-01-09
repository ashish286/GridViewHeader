package com.munix.gridviewheader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {
	
	Context mContext;
	String[] arr;
	
	public TestAdapter( Context mContext, String[] arr )
	{
		this.mContext = mContext;
		this.arr = arr;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ViewHolder
	{
		public TextView text;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if ( convertView == null )
		{
			//Utilities.log("convert null " + position);
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.adapter_item, null);
			holder.text = (TextView)convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		}else{
			//Utilities.log("convert not null " + position);
			holder = (ViewHolder)convertView.getTag();
		}
		holder.text.setText( arr[position] );

		return convertView;
	}

}
