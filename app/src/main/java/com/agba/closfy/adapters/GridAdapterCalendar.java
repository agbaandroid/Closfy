package com.agba.closfy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agba.closfy.R;

public class GridAdapterCalendar extends BaseAdapter {
	private Context context;
	String[] listaDias;

	public GridAdapterCalendar(Context c) {
		context = c;
		listaDias = c.getResources().getStringArray(R.array.listaDias);
	}

	public int getCount() {
		return listaDias.length;
	}

	public Object getItem(int position) {
		return listaDias[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater
					.inflate(R.layout.screen_gridcell_days, parent, false);
		}
		// Get a reference to the Day gridcell
		TextView gridcell = (TextView) row.findViewById(R.id.nameDay);

		// Set the Day GridCell
		gridcell.setText(listaDias[position]);

		// gridcell.setTextColor(context.getResources()
		// .getColor(R.color.lightgray));

		// gridcell.setTextColor(context.getResources().getColor(R.color.lightgray02));

		// gridcell.setTextColor(context.getResources().getColor(R.color.orrange));

		return row;
	}
}