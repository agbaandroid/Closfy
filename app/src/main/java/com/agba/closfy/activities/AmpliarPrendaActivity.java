package com.agba.closfy.activities;

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
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;

public class AmpliarPrendaActivity extends ActionBarActivity {
	private static final String KEY_CONTENT = "NuevaPrendaFragment:Content";
	private String mContent = "???";

	private ImageView imagenAmpliada;
	private LinearLayout botonVolver;
	int idPrenda;

	SharedPreferences prefs;
	int estilo;
	int cuentaSeleccionada;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.prenda_ampliada);

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

		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(
				getResources().getString(R.string.editarPrenda));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		idPrenda = extras.getInt("idPrenda");

		imagenAmpliada = (ImageView) this.findViewById(R.id.prendaAmpliada);
		botonVolver = (LinearLayout) this.findViewById(R.id.botonVolver);

		Prenda prenda = new Prenda();
		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			prenda = gestion.getPrendaById(db, idPrenda);
		}
		db.close();

		prenda = Util.obtenerImagenesPrendas(this, prenda, 0, estilo);

		imagenAmpliada.setImageDrawable(prenda.getFoto());

		botonVolver.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	// Anadiendo las opciones de menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_setting, menu);
		return true;
	}

	// Anadiendo funcionalidad a las opciones de menu
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
