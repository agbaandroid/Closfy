package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.modelo.Subtipo;
import com.agba.closfy.modelo.Utilidad;

public class ListAdapterSubtiposSpinner extends ArrayAdapter<Subtipo> {

    private Context contexto;
    private ArrayList<Subtipo> lista;

    public ListAdapterSubtiposSpinner(Context context, int textViewResourceId,
                              ArrayList<Subtipo> items) {
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
        Subtipo subtipo = lista.get(position);
        View fila = inflador.inflate(R.layout.spinner_sinimagen, parent, false);
        TextView texto = (TextView) fila.findViewById(R.id.textSpinner);

        texto.setText(subtipo.getNombre());

        return fila;
    }

    public View getListView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflador = LayoutInflater.from(contexto);
        Subtipo subtipo = lista.get(position);
        View fila = inflador.inflate(R.layout.spinner_sinimagen, parent, false);
        TextView texto = (TextView) fila.findViewById(R.id.textSpinner);
        texto.setText(subtipo.getNombre());
        return fila;
    }

}