package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.modelo.Prenda;

public class GridAdapter extends BaseAdapter {
	private Context context;
	ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();

	public GridAdapter(Context c, ArrayList<Prenda> listPrendas) {
		context = c;
		listaPrendas = listPrendas;
	}

	public int getCount() {
		return listaPrendas.size();
	}

	public Object getItem(int position) {
		return listaPrendas.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(
					R.layout.armario_adapter, parent,
					false);
		} else {
			v = (View) convertView;
		}
		
		ImageView imagenPrenda = (ImageView) v
				.findViewById(R.id.imagenPrenda);

		imagenPrenda.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imagenPrenda.setPadding(5, 5, 5, 5);
		
		imagenPrenda.setImageDrawable(listaPrendas.get(position).getFoto());		
		return v;
	}
}