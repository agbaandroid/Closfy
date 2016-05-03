package com.agba.closfy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.AddUtilidadActivity;
import com.agba.closfy.activities.EditUtilidadActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class UtilidadesFragment extends Fragment {
	private static final String KEY_CONTENT = "PestanaCategoriaFragment:Content";
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();
	private SQLiteDatabase db;
	protected ListView listUtiView;

	int estilo;

	SharedPreferences prefs;
	int cuentaSeleccionada;
	boolean isSinPublicidad;

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Bundle bundle = getArguments();
		if(bundle != null) {
			isSinPublicidad = bundle.getBoolean("isSinPublicidad");
		}

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.utilidades, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		listUtiView = (ListView) this.getView().findViewById(
				R.id.listaPestanaUtilidad);

		RelativeLayout layoutPubli = (RelativeLayout) getView().findViewById(R.id.layoutPubli);
		if (isSinPublicidad) {
			layoutPubli.setVisibility(View.GONE);
		} else {
			AdView adView = (AdView) getActivity().findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		rellenarListaUtilidades();

	}

	public void rellenarListaUtilidades() {

		ArrayList<Utilidad> utilidades = new ArrayList<Utilidad>();
		ArrayList<Object> opciones = new ArrayList<Object>();
		opciones.add("1");

		utilidades = obtenerUtilidades();

		ListAdapterUtilidades listAdapter = new ListAdapterUtilidades(
				getActivity(), utilidades);
		listUtiView.setAdapter(listAdapter);

	}

	public ArrayList<Utilidad> obtenerUtilidades() {
		ArrayList<Utilidad> listCategorias = new ArrayList<Utilidad>();
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			// Recuperamos el listado del spinner Categorias
			listCategorias = (ArrayList<Utilidad>) gestion
					.getUtilidadesLista(db);
		}

		return listCategorias;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		rellenarListaUtilidades();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_setting_mas, menu);
	}

	// Aadiendo funcionalidad a las opciones de men
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				Intent inUti = new Intent(getActivity(),
						AddUtilidadActivity.class);
				inUti.putExtra("isSinPublicidad", isSinPublicidad);
				getActivity().startActivityForResult(inUti, 0);
				return true;
			case android.R.id.home:
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class ListAdapterUtilidades extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<Utilidad> listaUti = new ArrayList<Utilidad>();
		private Context context;

		public ListAdapterUtilidades(Context context, ArrayList<Utilidad> lista) {
			listaUti = lista;
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listaUti.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listaUti.get(position);
		}

		public int getPositionById(String id) {
			int posi = 0;
			for (int i = 0; i < listaUti.size(); i++) {
				Utilidad uti = listaUti.get(i);
				if (String.valueOf(uti.getIdUtilidad()).equals(id)) {
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
			LinearLayout layoutUtilidad;

			Utilidad uti = listaUti.get(position);

			String headerTitle = uti.getNombre();

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lista_utilidad, null);
			}

			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.textUtilidades);
			lblListHeader.setText(headerTitle);

			layoutUtilidad = (LinearLayout) convertView.findViewById(R.id.layoutUtilidad);

			layoutUtilidad.setTag(position);

			layoutUtilidad.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					int posiSel = (int) v.getTag();
					Utilidad uti = listaUti.get(posiSel);

					Intent intent = new Intent(getActivity(), EditUtilidadActivity.class);
					intent.putExtra("id", uti.getIdUtilidad());
					intent.putExtra("textEdit", uti.getNombre());
					intent.putExtra("isSinPublicidad", isSinPublicidad);
					getActivity().startActivity(intent);
				}
			});

			return convertView;
		}

	}
}
