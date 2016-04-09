package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;

public class AddTipoActivity extends AppCompatActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();
	private EditText editTipo;
	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;
	private Spinner spinnerTipo;
	private Spinner spinnerSexo;

	private static final int MENSAJE_ERROR_TIPO = 0;

	int idTipo;
	int idSexo;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_tipos);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setContentInsetsAbsolute(0, 0);
		setSupportActionBar(toolbar);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		editTipo = (EditText) findViewById(R.id.cajaNombreTipo);
		spinnerTipo = (Spinner) findViewById(R.id.spinnerTipoPrenda);
		spinnerSexo = (Spinner) findViewById(R.id.spinnerSexo);

		// Inflate the custom view and add click handlers for the buttons
		View actionBarButtons = getLayoutInflater().inflate(R.layout.accept_cancel_actionbar,
				new LinearLayout(this), false);

		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!"".equals(editTipo.getText().toString())) {
					if(idTipo == 0){
						onCreateDialog(MENSAJE_ERROR_TIPO);
					}else{
						String text = "";

						for (int i = 0; i < editTipo.getText().length(); i++) {
							if (i == 0) {
								text = text
										+ editTipo.getText().toString().toUpperCase()
										.charAt(i);
							} else {
								text = text
										+ editTipo.getText().toString().toLowerCase()
										.charAt(i);
							}
						}

						int sex = spinnerSexo.getSelectedItemPosition();

						boolean ok = false;
						db = openOrCreateDatabase(BD_NOMBRE, 1, null);
						if (db != null) {
							ok = gestion.addSubtipo(db, idTipo, sex, text.trim());
						}
						db.close();
						if (ok) {
							Context context = getApplicationContext();
							CharSequence textMsg = getResources().getString(
									R.string.addTipoOK);
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, textMsg, duration);
							toast.show();
							setResult(RESULT_OK, getIntent());
							finish();
						} else {
							Context context = getApplicationContext();
							CharSequence textMsg = getResources().getString(
									R.string.addTipoKO);
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, textMsg, duration);
							toast.show();

							finish();
						}
					}

				}
			}
		});

		// Hide the icon, title and home/up button
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		// Set the custom view and allow the bar to show it
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
		getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

		obtenerTiposPrenda();
		obtenerSexo();

		spinnerTipo
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
											   View view, int position, long id) {

						if (estilo == 1 && position > 2) {
							idTipo = position + 1;
						} else {
							idTipo = position;
						}

					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		spinnerSexo
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
											   View view, int position, long id) {

						idSexo = position;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
	}

	public void obtenerTiposPrenda() {
		ArrayAdapter<CharSequence> adapterList;
		// rellenamos el spinner tipo prenda
		if (estilo == 1) {
			adapterList = ArrayAdapter.createFromResource(this,
					R.array.tiposPrendaHombre,
					android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			spinnerTipo.setAdapter(adapterList);
		} else {
			adapterList = ArrayAdapter.createFromResource(this,
					R.array.tiposPrenda, android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			spinnerTipo.setAdapter(adapterList);
		}
	}

	public void obtenerSexo(){
		ArrayAdapter<CharSequence> adapterList;
		adapterList = ArrayAdapter.createFromResource(this,
				R.array.arraySexoTipos,
				android.R.layout.simple_spinner_item);
		adapterList.setDropDownViewResource(R.layout.spinner);
		spinnerSexo.setAdapter(adapterList);

		spinnerSexo.setSelection(estilo);
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		switch (id) {
			case MENSAJE_ERROR_TIPO:
				builder.setMessage(
						getResources().getString(R.string.tipoObligatorio))
						.setTitle(getResources().getString(R.string.atencion))
						.setIcon(R.drawable.ic_alert)
						.setPositiveButton(
								getResources().getString(R.string.aceptar),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {
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
