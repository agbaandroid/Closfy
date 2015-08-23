package com.agba.closfy.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.AddCuentaActivity;
import com.agba.closfy.activities.DeleteCuentaActivity;
import com.agba.closfy.activities.EditCuentaActivity;
import com.agba.closfy.activities.SelectCuentaActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.NuevaPrendaFragment.ListAdapterUtilidad;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

public class CuentasFragment extends Fragment {
	private static final String KEY_CONTENT = "CuentasFragment:Content";
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();
	private SQLiteDatabase db;

	private static final int PRENDA_BASICA = 88;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	private LinearLayout layoutAddCuenta;
	private LinearLayout layoutSelectCuenta;
	private LinearLayout layoutDeleteCuenta;
	private RelativeLayout layoutUser1;
	private TextView textCuenta;

	public static CuentasFragment newInstance(String content) {
		CuentasFragment fragment = new CuentasFragment();

		return fragment;
	}

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

		return inflater.inflate(R.layout.cuentas, container, false);
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

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);

		// Asignamos el tipo de fuente
		// Typeface miPropiaTypeFace = Typeface.createFromAsset(getActivity()
		// .getAssets(), "fonts/Berlin.ttf");

		layoutAddCuenta = (LinearLayout) this.getView().findViewById(
				R.id.layoutAddUser);
		layoutSelectCuenta = (LinearLayout) this.getView().findViewById(
				R.id.layoutEditUser);
		layoutDeleteCuenta = (LinearLayout) this.getView().findViewById(
				R.id.layoutDeleteUser);
		layoutUser1 = (RelativeLayout) this.getView().findViewById(
				R.id.layoutUser1);
		textCuenta = (TextView) this.getView().findViewById(
				R.id.txtCuentaSeleccionada);

		cargarCuenta();

		layoutAddCuenta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getActivity(), AddCuentaActivity.class);
				startActivity(in);
			}
		});

		layoutSelectCuenta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),
						SelectCuentaActivity.class);
				startActivityForResult(in, PRENDA_BASICA);
			}
		});

		layoutDeleteCuenta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),
						DeleteCuentaActivity.class);
				startActivityForResult(in, PRENDA_BASICA);
			}
		});

		layoutUser1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getActivity(), EditCuentaActivity.class);
				startActivityForResult(in, PRENDA_BASICA);
			}
		});
	}

	// Anadiendo las opciones de menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_setting, menu);
	}

	// Anadiendo funcionalidad a las opciones de menu
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

	public void cargarCuenta() {
		int cuent = cuentaSeleccionada();
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			Cuenta cuenta = gestion.getCuentaSeleccionada(db, cuent);
			textCuenta.setText(cuenta.getDescCuenta());
		}
		db.close();
	}

	@Override
	public void onResume() {
		cargarCuenta();
		super.onResume();
	}

	public int cuentaSeleccionada() {
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);

		int idCuenta = prefs.getInt("cuenta", 0);
		return idCuenta;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK){
			editor = prefs.edit();
			editor.putBoolean("actualizaCuenta", true);
			editor.commit();
		}

	}
}
