package com.agba.closfy.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;

public class SelectTemporadaActivity extends Activity {
	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();

	TextView botonCancelar;
	TextView botonAceptar;
	LinearLayout radioOtoInv;
	LinearLayout radioPrimVer;
	LinearLayout radioTodoAno;
	ImageView imgOtoInv;
	ImageView imgPrimVer;
	ImageView imgTodoAno;

	LinearLayout layoutSelectTemp;

	int temporada;

	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temporada);

		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);

		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		botonAceptar = (TextView) this.findViewById(R.id.botonAceptar);
		botonCancelar = (TextView) this.findViewById(R.id.botonCancelar);
		radioOtoInv = (LinearLayout) this.findViewById(R.id.radioOtoInv);
		radioPrimVer = (LinearLayout) this.findViewById(R.id.radioPrimVer);
		radioTodoAno = (LinearLayout) this.findViewById(R.id.radioAll);
		imgOtoInv = (ImageView) this.findViewById(R.id.imgOtoInv);
		imgPrimVer = (ImageView) this.findViewById(R.id.imgPrimVer);
		imgTodoAno = (ImageView) this.findViewById(R.id.imgAll);

		layoutSelectTemp = (LinearLayout) this
				.findViewById(R.id.layoutSelectTemp);

		Bundle extras = getIntent().getExtras();
		temporada = extras.getInt("Temporada");

		switch (temporada) {
		case 0:
			imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_on);
			imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_off);
			imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_off);
			break;
		case 1:
			imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_off);
			imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_on);
			imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_off);
			break;
		case 2:
			imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_off);
			imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_off);
			imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_on);
			break;
		default:
			break;
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		radioOtoInv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgOtoInv.setBackgroundResource(R.drawable.radio_azul_on);
					imgPrimVer.setBackgroundResource(R.drawable.radio_azul_off);
					imgTodoAno.setBackgroundResource(R.drawable.radio_azul_off);
				} else {
					imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_on);
					imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_off);
					imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_off);
				}
				temporada = 0;
			}
		});

		radioPrimVer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgOtoInv.setBackgroundResource(R.drawable.radio_azul_off);
					imgPrimVer.setBackgroundResource(R.drawable.radio_azul_on);
					imgTodoAno.setBackgroundResource(R.drawable.radio_azul_off);
				} else {
					imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_off);
					imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_on);
					imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_off);
				}
				temporada = 1;
			}
		});

		radioTodoAno.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgOtoInv.setBackgroundResource(R.drawable.radio_azul_off);
					imgPrimVer.setBackgroundResource(R.drawable.radio_azul_off);
					imgTodoAno.setBackgroundResource(R.drawable.radio_azul_on);
				} else {
					imgOtoInv.setBackgroundResource(R.drawable.radio_rosa_off);
					imgPrimVer.setBackgroundResource(R.drawable.radio_rosa_off);
					imgTodoAno.setBackgroundResource(R.drawable.radio_rosa_on);
				}
				temporada = 2;
			}
		});

		botonAceptar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getIntent().putExtra("Temporada", temporada);
				setResult(RESULT_OK, getIntent());
				finish();
			}
		});

		botonCancelar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void cambiarEstiloHombre() {
		switch (temporada) {
		case 0:
			imgOtoInv.setBackgroundResource(R.drawable.radio_azul_on);
			imgPrimVer.setBackgroundResource(R.drawable.radio_azul_off);
			imgTodoAno.setBackgroundResource(R.drawable.radio_azul_off);
			break;
		case 1:
			imgOtoInv.setBackgroundResource(R.drawable.radio_azul_off);
			imgPrimVer.setBackgroundResource(R.drawable.radio_azul_on);
			imgTodoAno.setBackgroundResource(R.drawable.radio_azul_off);
			break;
		case 2:
			imgOtoInv.setBackgroundResource(R.drawable.radio_azul_off);
			imgPrimVer.setBackgroundResource(R.drawable.radio_azul_off);
			imgTodoAno.setBackgroundResource(R.drawable.radio_azul_on);
			break;
		default:
			break;
		}

		layoutSelectTemp.setBackgroundResource(R.color.azul);
	}

}
