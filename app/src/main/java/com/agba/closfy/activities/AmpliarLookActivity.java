package com.agba.closfy.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.util.Util;

import java.util.ArrayList;

public class AmpliarLookActivity extends AppCompatActivity {
	private static final String KEY_CONTENT = "NuevaPrendaFragment:Content";
	private String mContent = "???";

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

		setContentView(R.layout.look_ampliado_calendario);

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

		ArrayList<Look> listLooks = new ArrayList<Look>();
		listLooks.add(look);

		Util.obtenerImagenLook(this, listLooks, 0);

		Look lookSelec = listLooks.get(0);

		if (lookSelec.getFoto() != null) {
			imagenLook.setBackgroundDrawable(lookSelec.getFoto());
		}
	}

	// Aadiendo funcionalidad a las opciones de men
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
