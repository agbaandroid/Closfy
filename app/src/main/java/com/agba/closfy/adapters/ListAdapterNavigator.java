package com.agba.closfy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;

import java.util.Locale;

public class ListAdapterNavigator extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mSelectedItem;
	private String[] listaOpciones;
	Locale locale = Locale.getDefault();
	String languaje = locale.getLanguage();

	public ListAdapterNavigator(Context context, String[] lista) {
		listaOpciones = lista;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listaOpciones.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listaOpciones[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text;
		ImageView icon;
		LinearLayout layoutNavigator;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lista_navigator, null);
		}

		text = (TextView) convertView.findViewById(R.id.textNavigator);
		icon = (ImageView) convertView.findViewById(R.id.iconNavigator);
		layoutNavigator = (LinearLayout) convertView.findViewById(R.id.layoutNavigator);
		text.setText(listaOpciones[position]);

		layoutNavigator.setBackgroundResource(R.color.blanco);

		if (position == mSelectedItem) {
			text.setTextColor(Color.argb(255, 19, 192, 172));
			//layoutNavigator.setBackgroundResource(R.color.fondodrawable);
		} else {
			text.setTextColor(Color.GRAY);
		}

		switch (position) {
			case 0:
				icon.setBackgroundResource(R.drawable.armario);
				break;
			case 1:
				icon.setBackgroundResource(R.drawable.mislooks);
				break;
			case 2:
				icon.setBackgroundResource(R.drawable.tipos);
				break;
			case 3:
				icon.setBackgroundResource(R.drawable.utilidades);
				break;
			case 4:
				icon.setBackgroundResource(R.drawable.percha);
				break;
			case 5:
				icon.setBackgroundResource(R.drawable.calendar);
				break;
			case 6:
				icon.setBackgroundResource(R.drawable.test);
				break;
			case 7:
				icon.setBackgroundResource(R.drawable.morfologia);
				break;
			case 8:
				icon.setBackgroundResource(R.drawable.tutorial);
				break;
			case 9:
				icon.setBackgroundResource(R.drawable.tienda);
				break;
			case 10:
				icon.setBackgroundResource(R.drawable.valorar);
				break;
		}
		return convertView;
	}

	public int getSelectedItem() {
		return mSelectedItem;
	}

	public void setSelectedItem(int selectedItem) {
		mSelectedItem = selectedItem;
	}

}
