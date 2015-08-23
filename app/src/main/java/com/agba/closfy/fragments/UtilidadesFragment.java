package com.agba.closfy.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.activities.AddUtilidadActivity;
import com.agba.closfy.adapters.ListaAdapterUtilidadExpandibleAdapter;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

public class UtilidadesFragment extends Fragment {
	private static final String KEY_CONTENT = "PestanaCategoriaFragment:Content";
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();
	private SQLiteDatabase db;
	protected ExpandableListView listUtiView;
	private LinearLayout linearLayoutAdd;
	private ImageView imagenAdd;

	int estilo;

	SharedPreferences prefs;
	int cuentaSeleccionada;

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

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

		listUtiView = (ExpandableListView) this.getView().findViewById(
				R.id.listaPestanaUtilidad);
		linearLayoutAdd = (LinearLayout) this.getView().findViewById(
				R.id.layoutAddUtilidad);
		imagenAdd = (ImageView) this.getView().findViewById(R.id.imagenAdd);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		rellenarListaExpansibleCategorias();

		listUtiView.setOnGroupExpandListener(new OnGroupExpandListener() {
			int previousGroup = -1;

			@Override
			public void onGroupExpand(int groupPosition) {
				if (groupPosition != previousGroup)
					listUtiView.collapseGroup(previousGroup);
				previousGroup = groupPosition;
			}
		});

		linearLayoutAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent inUti = new Intent(getActivity(),
						AddUtilidadActivity.class);
				getActivity().startActivityForResult(inUti, 0);
			}
		});

		imagenAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent inUti = new Intent(getActivity(),
						AddUtilidadActivity.class);
				getActivity().startActivityForResult(inUti, 0);
			}
		});

	}

	public void rellenarListaExpansibleCategorias() {

		ArrayList<Utilidad> utilidades = new ArrayList<Utilidad>();
		ArrayList<Object> opciones = new ArrayList<Object>();
		opciones.add("1");

		utilidades = obtenerUtilidades();

		listUtiView.setDividerHeight(2);
		listUtiView.setGroupIndicator(null);
		listUtiView.setClickable(true);

		ListaAdapterUtilidadExpandibleAdapter listAdapter = new ListaAdapterUtilidadExpandibleAdapter(
				getActivity(), utilidades, estilo);
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
		rellenarListaExpansibleCategorias();
	}

	public void cambiarEstiloHombre() {
		imagenAdd.setBackgroundResource(R.drawable.nuevo_azul);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_setting, menu);
	}

	// Aadiendo funcionalidad a las opciones de men
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View view = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog alert;
		switch (item.getItemId()) {
		case R.id.btInfo:
			view = li.inflate(R.layout.info, null);
			builder.setView(view);
			builder.setTitle(getResources().getString(R.string.informacion));
			builder.setIcon(R.drawable.ic_info_azul);
			builder.setCancelable(false);
			builder.setPositiveButton(getResources()
					.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			return true;
		case R.id.btAcerca:
			view = li.inflate(R.layout.acerca, null);
			builder.setView(view);
			builder.setTitle(getResources().getString(R.string.app_name));
			builder.setIcon(R.drawable.icon_app);
			builder.setCancelable(false);
			builder.setPositiveButton(getResources()
					.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
