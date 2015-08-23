package com.agba.closfy.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;

public class ListAdapterAddPrendasLook extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mSelectedItem;
	private ArrayList<Prenda> listaPrendas;
	Locale locale = Locale.getDefault();
	String languaje = locale.getLanguage();
	Context contextAux;

	public ListAdapterAddPrendasLook(Context context, ArrayList<Prenda> lista) {
		listaPrendas = lista;
		mInflater = LayoutInflater.from(context);
		contextAux = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listaPrendas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listaPrendas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView prenda;
		RelativeLayout layoutNavigator;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lista_add_prendas, null);
		}

		prenda = (ImageView) convertView.findViewById(R.id.prenda);
		layoutNavigator = (RelativeLayout) convertView
				.findViewById(R.id.layoutNavigator);
		//
		prenda.setBackgroundDrawable(listaPrendas.get(position).getFoto());

		return convertView;
	}

	public int getSelectedItem() {
		return mSelectedItem;
	}

	public void setSelectedItem(int selectedItem) {
		mSelectedItem = selectedItem;
	}

}
