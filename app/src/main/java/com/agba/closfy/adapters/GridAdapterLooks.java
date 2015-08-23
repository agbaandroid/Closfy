package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.modelo.Look;

public class GridAdapterLooks extends BaseAdapter {
	private Context context;
	ArrayList<Look> listaLooks = new ArrayList<Look>();
	int estilo;

	public GridAdapterLooks(Context c, ArrayList<Look> listLooks, int estiloAux) {
		context = c;
		listaLooks = listLooks;
		estilo = estiloAux;
	}

	public int getCount() {
		return listaLooks.size();
	}

	public Object getItem(int position) {
		return listaLooks.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Look look = listaLooks.get(position);
		View v;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.mis_looks_adapter_nuevo, parent,
					false);
		} else {
			v = (View) convertView;
		}

		ImageView imagenLook = (ImageView) v.findViewById(R.id.imagenLook);
		LinearLayout layoutImage = (LinearLayout) v.findViewById(R.id.layoutImageLook);
		LinearLayout layoutText = (LinearLayout) v.findViewById(R.id.layoutTextLook);
		if (look.getFoto() != null) {			
			imagenLook.setBackgroundDrawable(look.getFoto());
			layoutImage.setVisibility(View.VISIBLE);
			layoutText.setVisibility(View.GONE);
		}else{
			layoutImage.setVisibility(View.GONE);
			layoutText.setVisibility(View.VISIBLE);
		}

		return v;
	}

}