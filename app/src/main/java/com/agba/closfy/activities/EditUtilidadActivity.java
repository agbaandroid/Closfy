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
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;

public class EditUtilidadActivity extends Activity implements OnClickListener {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	static final int MENSAJE_OK = 1;
	static final int MENSAJE_ERROR = 2;
	private GestionBBDD gestion = new GestionBBDD();
	private EditText editUti;
	private int id;
	private String textEdit;
	LinearLayout layoutEditUti;
	
	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_utilidad);
		// Abrimos la base de datos si no esta abierta
		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		gestion = new GestionBBDD();
		editUti = (EditText) findViewById(R.id.cajaNombreUtilidad);
		layoutEditUti = (LinearLayout) findViewById(R.id.layoutEditUti);

		View botonGuardar = findViewById(R.id.botonGuardar);
		botonGuardar.setOnClickListener(this);

		View botonCancelar = findViewById(R.id.botonCancelar);
		botonCancelar.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getInt("id");
			textEdit = extras.getString("textEdit");
		}

		editUti.setText(textEdit);
		
		prefs = this.getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);
		
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.botonGuardar:
			if (!"".equals(editUti.getText().toString())) {
				String text = "";

				for (int i = 0; i < editUti.getText().length(); i++) {
					if (i == 0) {
						text = text
								+ editUti.getText().toString().toUpperCase()
										.charAt(i);
					} else {
						text = text
								+ editUti.getText().toString().toLowerCase()
										.charAt(i);
					}
				}

				boolean ok = gestion.editUtilidad(db, text.trim(), String.valueOf(id));
				if (ok) {
					Context context = getApplicationContext();
					CharSequence textMsg = getResources().getString(
							R.string.editUtilidadOK);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg, duration);
					toast.show();
				} else {
					Context context = getApplicationContext();
					CharSequence textMsg = getResources().getString(
							R.string.editUtilidadKO);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg, duration);
					toast.show();
				}
				finish();
			}
			break;
		case R.id.botonCancelar:
			finish();
			break;
		}
	}
	
	public void cambiarEstiloHombre (){
		layoutEditUti.setBackgroundResource(R.color.azul);
		editUti.setBackgroundResource(R.drawable.botton_azul);
	}
}
