package com.agba.closfy.activities;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;

public class AddCuentaActivity extends Activity implements OnClickListener {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();
	private EditText nombreCuenta;
	Locale locale = Locale.getDefault();
	String languaje = locale.getLanguage();

	LinearLayout radioMujer;
	LinearLayout radioHombre;
	ImageView imgMujer;
	ImageView imgHombre;
	LinearLayout layoutAddCuenta;

	int sexo;

	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_cuenta);
		gestion = new GestionBBDD();
		nombreCuenta = (EditText) findViewById(R.id.cajaNombreCuenta);
		layoutAddCuenta = (LinearLayout) findViewById(R.id.layoutAddCuenta);
		radioMujer = (LinearLayout) this.findViewById(R.id.radioMujer);
		radioHombre = (LinearLayout) this.findViewById(R.id.radioHombre);
		imgMujer = (ImageView) this.findViewById(R.id.imgMujer);
		imgHombre = (ImageView) this.findViewById(R.id.imgHombre);

		View botonAddSub = findViewById(R.id.botonGuardar);
		botonAddSub.setOnClickListener(this);
		View botonCancelar = findViewById(R.id.botonCancelar);
		botonCancelar.setOnClickListener(this);

		sexo = 0;

		imgMujer.setBackgroundResource(R.drawable.radio_rosa_on);
		imgHombre.setBackgroundResource(R.drawable.radio_rosa_off);

		prefs = this.getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);

		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		radioMujer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgMujer.setBackgroundResource(R.drawable.radio_azul_on);
					imgHombre.setBackgroundResource(R.drawable.radio_azul_off);
				} else {
					imgMujer.setBackgroundResource(R.drawable.radio_rosa_on);
					imgHombre.setBackgroundResource(R.drawable.radio_rosa_off);
				}
				sexo = 0;
			}
		});

		radioHombre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgMujer.setBackgroundResource(R.drawable.radio_azul_off);
					imgHombre.setBackgroundResource(R.drawable.radio_azul_on);
				} else {
					imgMujer.setBackgroundResource(R.drawable.radio_rosa_off);
					imgHombre.setBackgroundResource(R.drawable.radio_rosa_on);
				}
				sexo = 1;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.botonGuardar:
			if (!"".equals(nombreCuenta.getText().toString())) {
				String text = "";

				for (int i = 0; i < nombreCuenta.getText().length(); i++) {
					if (i == 0) {
						text = text
								+ nombreCuenta.getText().toString()
										.toUpperCase().charAt(i);
					} else {
						text = text
								+ nombreCuenta.getText().toString()
										.toLowerCase().charAt(i);
					}
				}
				boolean ok = false;
				db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
				if (db != null) {
					ok = gestion.addCuenta(db, text.trim(), sexo);
				}
				db.close();
				if (ok) {
					this.setResult(0);

					Context context = getApplicationContext();
					CharSequence textMsg = getResources().getString(
							R.string.addCuentaOK);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg, duration);
					toast.show();

					finish();
				} else {
					Context context = getApplicationContext();
					CharSequence textMsg = getResources().getString(
							R.string.addCuentaKO);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg, duration);
					toast.show();

					finish();
				}
			}
			break;
		case R.id.botonCancelar:
			finish();
			break;
		}
	}

	public void cambiarEstiloHombre() {
		nombreCuenta.setBackgroundResource(R.drawable.botton_azul);
		layoutAddCuenta.setBackgroundResource(R.color.azul);

		imgMujer.setBackgroundResource(R.drawable.radio_azul_on);
		imgHombre.setBackgroundResource(R.drawable.radio_azul_off);
	}
}
