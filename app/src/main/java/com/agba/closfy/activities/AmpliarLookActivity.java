package com.agba.closfy.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.util.Util;

public class AmpliarLookActivity extends ActionBarActivity {
	private static final String KEY_CONTENT = "NuevaPrendaFragment:Content";
	private String mContent = "???";

	private LinearLayout botonVolver;
	int idLook;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	SharedPreferences prefs;
	int estilo;
	int cuentaSeleccionada;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		idLook = extras.getInt("idLook");

		Look look = new Look();
		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			look = gestion.getLookById(db, idLook);
		}
		db.close();

		setContentView(R.layout.look_ampliado);

		// Cuenta seleccionada
		prefs = this.getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (estilo == 1) {
			toolbar.setBackgroundResource(R.color.azul);
		}

		ImageView imagenLook = (ImageView) findViewById(R.id.imagenLook);
		LinearLayout layoutImage = (LinearLayout) findViewById(R.id.layoutImageLook);
		LinearLayout layoutText = (LinearLayout) findViewById(R.id.layoutTextLook);

		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(getResources().getString(R.string.look));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		botonVolver = (LinearLayout) this.findViewById(R.id.botonVolver);

		ArrayList<Look> listLooks = new ArrayList<Look>();
		listLooks.add(look);

		Util.obtenerImagenLook(this, listLooks, 0);

		Look lookSelec = listLooks.get(0);

		if (lookSelec.getFoto() != null) {
			imagenLook.setBackgroundDrawable(lookSelec.getFoto());
			layoutImage.setVisibility(View.VISIBLE);
			layoutText.setVisibility(View.GONE);
		} else {
			layoutImage.setVisibility(View.GONE);
			layoutText.setVisibility(View.VISIBLE);
		}

		botonVolver.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	// Aniadiendo las opciones de menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_setting, menu);
		return true;
	}

	// A�adiendo funcionalidad a las opciones de men�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		LayoutInflater li = LayoutInflater.from(this);
		View view = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
