package com.agba.closfy.adapters;

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
import com.agba.closfy.util.Util;

public class ListAdapterNavigator extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mSelectedItem;
	private String[] listaOpciones;
	Locale locale = Locale.getDefault();
	String languaje = locale.getLanguage();
	Context contextAux;
	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();

	public ListAdapterNavigator(Context context, String[] lista) {
		listaOpciones = lista;
		mInflater = LayoutInflater.from(context);
		contextAux = context;
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
		RelativeLayout layoutNavigator;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lista_navigator, null);
		}

		cuentaSeleccionada = Util.cuentaSeleccionada(contextAux, prefs);

		db = contextAux.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		text = (TextView) convertView.findViewById(R.id.textNavigator);
		icon = (ImageView) convertView.findViewById(R.id.iconNavigator);
		layoutNavigator = (RelativeLayout) convertView
				.findViewById(R.id.layoutNavigator);
		text.setText(listaOpciones[position]);

		layoutNavigator.setBackgroundResource(R.color.blanco);

		if (position == mSelectedItem) {
			if (estilo == 1) {
				text.setTextColor(contextAux.getResources().getColor(
						R.color.azul));
			} else {
				text.setTextColor(contextAux.getResources().getColor(
						R.color.actionBarColor));
			}
			layoutNavigator.setBackgroundResource(R.color.fondodrawable);
		} else {
			text.setTextColor(Color.BLACK);
		}

		switch (position) {
		case 0:
			icon.setBackgroundResource(R.drawable.add);
			break;
		case 1:
			icon.setBackgroundResource(R.drawable.look);
			break;
		case 2:
			icon.setBackgroundResource(R.drawable.armario);
			break;
		case 3:
			icon.setBackgroundResource(R.drawable.mislooks);
			break;
		case 4:
			icon.setBackgroundResource(R.drawable.utilidades);
			break;
		case 5:
			icon.setBackgroundResource(R.drawable.percha);
			break;
		case 6:
			icon.setBackgroundResource(R.drawable.calendar);
			break;
//		case 6:
//			icon.setBackgroundResource(R.drawable.database);
//			break;
		case 7:
			icon.setBackgroundResource(R.drawable.test);
			break;
		case 8:
			icon.setBackgroundResource(R.drawable.morfologia);
			break;		
		case 9:
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
