package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.EmailSender;
import com.agba.closfy.util.Util;

public class QueMePongoActivity extends ActionBarActivity {

	private String idClass = "c#99l#o&&s#9fy1999#$23#94$59#967#989";

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private static final int MENSAJE_ERROR_EMAIL = 1;
	private static final int MENSAJE_ERROR_MENSAJE = 2;
	private static final int MENSAJE_AVISO = 3;
	private static final int MENSAJE_OK = 4;
	private static final int MENSAJE_ERROR = 5;

	private EditText editEmail;
	private EditText editMensaje;
	private LinearLayout botonEnviar;

	LinearLayout radioEvento;
	LinearLayout radioCasual;
	ImageView imgEvento;
	ImageView imgCasual;

	int tipoAsesoramiento = 0;

	int cuentaSeleccionada;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	String idAsesoramiento;
	String idOrder;

	int estilo;

	ProgressDialog progDailog;

	boolean emailEnviado;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quemepongo);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
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

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.queMePongo));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		editEmail = (EditText) findViewById(R.id.cajaEmail);
		editMensaje = (EditText) findViewById(R.id.cajaMensaje);
		botonEnviar = (LinearLayout) findViewById(R.id.botonEnviar);

		radioEvento = (LinearLayout) findViewById(R.id.radioEvento);
		radioCasual = (LinearLayout) findViewById(R.id.radioCasual);
		imgEvento = (ImageView) findViewById(R.id.imgEvento);
		imgCasual = (ImageView) findViewById(R.id.imgCasual);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}
		db.close();

		idClass = Util.getClassId(idClass);

		switch (tipoAsesoramiento) {
		case 0:
			imgEvento.setBackgroundResource(R.drawable.radio_rosa_on);
			imgCasual.setBackgroundResource(R.drawable.radio_rosa_off);
			break;
		case 1:
			imgEvento.setBackgroundResource(R.drawable.radio_rosa_off);
			imgCasual.setBackgroundResource(R.drawable.radio_rosa_on);
			break;
		default:
			break;
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		Bundle extras = getIntent().getExtras();
		idAsesoramiento = String.valueOf(extras.getInt("idAsesoramiento"));
		idOrder = extras.getString("idOrder");

		radioEvento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgEvento.setBackgroundResource(R.drawable.radio_azul_on);
					imgCasual.setBackgroundResource(R.drawable.radio_azul_off);
				} else {
					imgEvento.setBackgroundResource(R.drawable.radio_rosa_on);
					imgCasual.setBackgroundResource(R.drawable.radio_rosa_off);
				}
				tipoAsesoramiento = 0;
			}
		});

		radioCasual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (estilo == 1) {
					imgCasual.setBackgroundResource(R.drawable.radio_azul_on);
					imgEvento.setBackgroundResource(R.drawable.radio_azul_off);
				} else {
					imgCasual.setBackgroundResource(R.drawable.radio_rosa_on);
					imgEvento.setBackgroundResource(R.drawable.radio_rosa_off);
				}
				tipoAsesoramiento = 1;
			}
		});

		botonEnviar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean errorEmail = false;
				boolean errorMensaje = false;

				if (editEmail.getText().toString().equals("")
						|| !Util.validarEmail(editEmail.getText().toString())) {
					errorEmail = true;
				}

				if (editMensaje.getText().toString().equals("")) {
					errorMensaje = true;
				}

				if (!errorMensaje && !errorEmail) {
					onCreateDialog(MENSAJE_AVISO);
				} else {
					if (errorEmail) {
						onCreateDialog(MENSAJE_ERROR_EMAIL);
					} else {
						onCreateDialog(MENSAJE_ERROR_MENSAJE);
					}
				}

			}
		});
	}

	public void enviarEmail() {
		try {
			emailEnviado = false;

			String asunto = getResources().getString(R.string.queMePongo);

			EmailSender sender = new EmailSender("closfy.send@yahoo.es",
					idClass);
			emailEnviado = sender.sendMail(asunto, editMensaje.getText()
					.toString().trim(), "closfy.send@yahoo.es",
					"info@closfy.com", tipoAsesoramiento, idOrder, editEmail
							.getText().toString().trim());

		} catch (Exception e) {
			Log.e("SendMail", e.getMessage(), e);
			emailEnviado = false;
		}
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		switch (id) {
		case MENSAJE_AVISO:
			builder.setMessage(
					getResources()
							.getString(R.string.confirmacionAsesoramiento))
					.setTitle(getResources().getString(R.string.atencion))
					.setIcon(R.drawable.ic_alert)
					.setNegativeButton(
							getResources().getString(R.string.cancelar),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setPositiveButton(
							getResources().getString(R.string.aceptar),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new EnviarEmailAsyncTask().execute();
									dialog.cancel();
								}
							});
			alert = builder.create();
			alert.show();
			break;
		case MENSAJE_OK:
			builder.setMessage(getResources().getString(R.string.emailOK))
					.setTitle(getResources().getString(R.string.informacion))
					.setIcon(R.drawable.ic_info_azul)
					.setPositiveButton(
							getResources().getString(R.string.aceptar),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Actualizar asesoramiento
									db = openOrCreateDatabase(BD_NOMBRE, 1,
											null);
									if (db != null) {
										gestion.consumirAsesoramiento(db,
												idAsesoramiento);
									}
									db.close();
									finish();
								}
							});
			alert = builder.create();
			alert.show();
			break;
		case MENSAJE_ERROR:
			builder.setMessage(getResources().getString(R.string.emailError))
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
		case MENSAJE_ERROR_EMAIL:
			builder.setMessage(getResources().getString(R.string.errorEmail))
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
		case MENSAJE_ERROR_MENSAJE:
			builder.setMessage(getResources().getString(R.string.ErrorMensaje))
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

	public class EnviarEmailAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(QueMePongoActivity.this);
			progDailog.setIndeterminate(false);
			progDailog.setMessage(getResources().getString(R.string.cargando));
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			enviarEmail();
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			progDailog.dismiss();
			if (emailEnviado) {
				onCreateDialog(MENSAJE_OK);
			} else {
				onCreateDialog(MENSAJE_ERROR);
			}
		}
	}

	public void cambiarEstiloHombre() {
		switch (tipoAsesoramiento) {
		case 0:
			imgEvento.setBackgroundResource(R.drawable.radio_azul_on);
			imgCasual.setBackgroundResource(R.drawable.radio_azul_off);
			break;
		case 1:
			imgCasual.setBackgroundResource(R.drawable.radio_azul_on);
			imgEvento.setBackgroundResource(R.drawable.radio_azul_off);
			break;
		default:
			break;
		}
	}

}
