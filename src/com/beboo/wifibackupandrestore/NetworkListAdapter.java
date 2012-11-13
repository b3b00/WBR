package com.beboo.wifibackupandrestore;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;

public class NetworkListAdapter extends SimpleAdapter {
	private LayoutInflater	mInflater;

	public NetworkListAdapter (Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to)
	{
		super (context, data, resource, from, to);
		mInflater = LayoutInflater.from (context);

	}

	@Override
	public Object getItem (int position)
	{
		return super.getItem (position);
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent)
	{
		//Ce test permet de ne pas reconstruire la vue si elle est déjà créée
		if (convertView == null)
		{

			
			convertView = mInflater.inflate (R.layout.onelinelist2, null);
			
			//convertView.setTag(??)

			//CheckBox cb = (CheckBox) convertView.findViewById (R.id.selected);

			//cb.setTag (position);
		}
		return super.getView (position, convertView, parent);
	}
}
