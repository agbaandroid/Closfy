package com.agba.closfy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.ComoFuncionaActivity;
import com.agba.closfy.activities.QueMePongoActivity;
import com.agba.closfy.activities.TarifasActivity;
import com.agba.closfy.activities.TerminosCondicionesActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Asesoramiento;
import com.agba.closfy.util.Util;

import java.util.ArrayList;

public class QueMePongoInicialFragment extends Fragment {
	private static final String KEY_CONTENT = "QueMePongoInicialFragment:Content";
	private String mContent = "???";

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private final int MENSAJE_ERROR_ASESORAMIENTOS = 0;

	int cuentaSeleccionada;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	LinearLayout botonAsesoramiento;
	LinearLayout botonTarifas;
	LinearLayout botonComoFunciona;
	LinearLayout botonTerminosCondiciones;

	TextView nAsesoramientos;

	int estilo;
	ArrayList<Asesoramiento> listAsesoramientos;

	boolean isSinPublicidad;

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

		return inflater.inflate(R.layout.quemepongo_inicial, container, false);
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

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}
		db.close();

		botonAsesoramiento = (LinearLayout) getView().findViewById(
				R.id.layoutAsesoramiento);
		botonTarifas = (LinearLayout) getView()
				.findViewById(R.id.layoutTarifas);
		botonComoFunciona = (LinearLayout) getView().findViewById(
				R.id.layoutComoFunciona);
		botonTerminosCondiciones = (LinearLayout) getView().findViewById(
				R.id.layoutTerminosCondiciones);
		nAsesoramientos = (TextView) getView().findViewById(
				R.id.nAsesoramientos);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listAsesoramientos = gestion.getAsesoramientos(db);
		}
		db.close();

		nAsesoramientos.setText(String.valueOf(listAsesoramientos.size()));

		botonTarifas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), TarifasActivity.class);
				intent.putExtra("isSinPublicidad", isSinPublicidad);
				startActivity(intent);
			}
		});

		botonComoFunciona.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ComoFuncionaActivity.class);
				startActivity(intent);
			}
		});

		botonTerminosCondiciones.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						TerminosCondicionesActivity.class);
				startActivity(intent);
			}
		});

		botonAsesoramiento.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listAsesoramientos.size() > 0) {
					Intent intent = new Intent(getActivity(),
							QueMePongoActivity.class);

					Bundle bundle = new Bundle();
					bundle.putInt("idAsesoramiento", listAsesoramientos.get(0)
							.getIdAsesoramiento());
					bundle.putString("idOrder", listAsesoramientos.get(0)
							.getIdOrder());
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					onCreateDialog(MENSAJE_ERROR_ASESORAMIENTOS);
				}
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listAsesoramientos = gestion.getAsesoramientos(db);
		}
		db.close();
		
		nAsesoramientos.setText(String.valueOf(listAsesoramientos.size()));
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog alert;
		switch (id) {
		case MENSAJE_ERROR_ASESORAMIENTOS:
			builder.setMessage(
					getResources().getString(R.string.sinAsesoramientos))
					.setTitle(getResources().getString(R.string.informacion))
					.setIcon(R.drawable.ic_info_azul)
					.setNegativeButton(getResources().getString(R.string.masTarde),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setPositiveButton(getResources().getString(R.string.comprarAhora),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(getActivity(),
											TarifasActivity.class);
									intent.putExtra("isSinPublicidad", isSinPublicidad);
									startActivity(intent);
									dialog.cancel();
								}
							});
			alert = builder.create();
			alert.show();
			break;
		}
		return null;
	}

}
