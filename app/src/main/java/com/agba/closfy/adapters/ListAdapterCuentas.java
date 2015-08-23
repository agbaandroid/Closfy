package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.modelo.Cuenta;

public class ListAdapterCuentas extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<Cuenta> listaCuenta = new ArrayList<Cuenta>();

	public ListAdapterCuentas(Context context, ArrayList<Cuenta> lista) {
		listaCuenta = lista;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listaCuenta.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listaCuenta.get(position);
	}

	public int getPositionById(String id) {
		int posi = 0;
		for (int i = 0; i < listaCuenta.size(); i++) {
			Cuenta cuenta = listaCuenta.get(i);
			if (cuenta.getIdCuenta().equals(id)) {
				posi = i;
				break;
			}
		}
		return posi;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lista_cuenta, null);
		}

		text = (TextView) convertView.findViewById(R.id.textListaCuentas);
		text.setText(listaCuenta.get(position).getDescCuenta());
		return convertView;
	}

}
