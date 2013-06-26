package com.beboo.wifibackupandrestore;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.content.res.Resources;

public class NetworkListAdapter extends SimpleAdapter {
	private LayoutInflater	mInflater;
	
	private int[] colors = new int[] { 0x00808080, 0x00606060 };
	//private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

	public NetworkListAdapter (Context context, List<Map<String, String>> items, int resource, String[] from, int[] to) {
		    super(context, items, resource, from, to);

	}

	@Override
	public Object getItem (int position)
	{
		return super.getItem (position);
	}

	 @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      int colorPos = position % colors.length;

	  Resources res = parent.getResources();

	  int color = 0;
	  if (colorPos == 0) {
			color = res.getColor(R.color.even_line);
		}
		else {
			color = res.getColor(R.color.odd_line);
		}
	  	
      view.setBackgroundColor(color);
      return view;
    }
}
