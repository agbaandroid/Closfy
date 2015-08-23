package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.modelo.Utilidad;

public class ListAdapterSpinner extends ArrayAdapter<Utilidad> {

	private Context contexto;
	private ArrayList<Utilidad> lista;

	public ListAdapterSpinner(Context context, int textViewResourceId,
			ArrayList<Utilidad> items) {
		super(context, textViewResourceId, items);
		this.contexto = context;
		this.lista = items;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getListView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflador = LayoutInflater.from(contexto);
		Utilidad uti = lista.get(position);
		View fila = inflador.inflate(R.layout.spinner_sinimagen, parent, false);
		TextView texto = (TextView) fila.findViewById(R.id.textSpinner);

		texto.setText(uti.getNombre());

		return fila;
	}

	public View getListView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflador = LayoutInflater.from(contexto);
		Utilidad uti = lista.get(position);
		View fila = inflador.inflate(R.layout.spinner_sinimagen, parent, false);
		TextView texto = (TextView) fila.findViewById(R.id.textSpinner);
		texto.setText(uti.getNombre());
		return fila;
	}

}