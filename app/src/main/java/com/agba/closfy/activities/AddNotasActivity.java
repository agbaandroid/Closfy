package com.agba.closfy.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;

public class AddNotasActivity extends Activity implements OnClickListener {
	private EditText editNota;
	private LinearLayout layoutAddNotas;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	int cuentaSeleccionada;
	int estilo;

	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_notas);

		editNota = (EditText) findViewById(R.id.cajaNotas);
		layoutAddNotas = (LinearLayout) findViewById(R.id.layoutAddNotas);

		View botonAdd = findViewById(R.id.botonGuardar);
		botonAdd.setOnClickListener(this);
		View botonCancelar = findViewById(R.id.botonCancelar);
		botonCancelar.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		String notas = extras.getString("notas");
		boolean isEdicion = extras.getBoolean("isEdicion");
		if (notas != null) {
			editNota.setText(notas);
		}

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (!isEdicion) {
			editNota.setEnabled(false);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.botonGuardar:
			String text = "";
			if (editNota.getText().toString() != null) {
				text = editNota.getText().toString();
			}

			getIntent().putExtra("notas", text);
			setResult(RESULT_OK, getIntent());
			finish();

			break;
		case R.id.botonCancelar:
			finish();
			break;
		}
	}

	public void cambiarEstiloHombre() {
		editNota.setBackgroundResource(R.drawable.botton_azul);
		layoutAddNotas.setBackgroundResource(R.color.azul);
	}
}
