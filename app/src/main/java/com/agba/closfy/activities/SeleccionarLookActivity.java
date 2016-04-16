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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.adapters.ListAdapterSpinner;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class SeleccionarLookActivity extends AppCompatActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	int idLook = -1;

	static final int MENSAJE_CONFIRMAR_ELIMINAR = 1;

	ArrayList<Look> listLooks = new ArrayList<Look>();
	int cuentaSeleccionada;

	int estilo;

	Look lookSeleccionado = new Look();
	String fecha;
	int hora;

	private ImageView checkFavoritos;
	int favorito = 0;

	ProgressDialog progDailog;

	GridView gridview;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	private Spinner spinnerUtilidades;
	int posiUtilidad = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.seleccionar_looks);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		AdView adView;
		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(
				getResources().getString(R.string.seleccionarLook));


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
				if (idLook != -1) {
					boolean ok = false;
					boolean diaLibre = false;
					db = openOrCreateDatabase(BD_NOMBRE, 1, null);
					Look lookSeleccionado = gestion.getLookById(db, idLook);
					if (db != null) {
						diaLibre = gestion.isDiaLibreHora(db, fecha, hora,
								cuentaSeleccionada);
						if (diaLibre) {
							ok = gestion.insertarLookCalendario(db, hora,
									fecha, lookSeleccionado.getIdLook(),
									cuentaSeleccionada);
						}
					}
					db.close();

					finish();
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

		gridview = (GridView) findViewById(R.id.gridLooks);
		spinnerUtilidades = (Spinner) findViewById(R.id.spinnerTipoPrendaArmario);

		checkFavoritos = (ImageView) findViewById(R.id.checkFavoritos);

		Bundle extras = getIntent().getExtras();
		fecha = extras.getString("fecha");
		hora = extras.getInt("hora");

		registerForContextMenu(gridview);

		// Rellenamos el spinner tipo
		obtenerUtilidades();

		/*botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		botonAceptar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (idLook == -1) {

				} else {
					boolean ok = false;
					boolean diaLibre = false;
					db = openOrCreateDatabase(BD_NOMBRE, 1, null);
					Look lookSeleccionado = gestion.getLookById(db, idLook);
					if (db != null) {
						diaLibre = gestion.isDiaLibreHora(db, fecha, hora,
								cuentaSeleccionada);
						if (diaLibre) {
							ok = gestion.insertarLookCalendario(db, hora,
									fecha, lookSeleccionado.getIdLook(),
									cuentaSeleccionada);
						}
					}
					db.close();

					finish();
				}
			}
		});

		/*spinnerUtilidades
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						posiUtilidad = position;
						new CargarLooksTask().execute();
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		checkFavoritos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favorito == 0) {
					favorito = 1;
					if (estilo == 1) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_on);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_on);
					}
				} else {
					favorito = 0;
					if (estilo == 1) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_off);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_off);
					}
				}
				new CargarLooksTask().execute();
			}
		});*/
		new CargarLooksTask().execute();

	}

	public void obtenerUtilidades() {
		ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			// Recuperamos el listado del spinner Categorias
			listUtilidades = (ArrayList<Utilidad>) gestion.getUtilidades(db);
		}
		db.close();

		// Creamos el adaptador
		ListAdapterSpinner spinner_adapterCat = new ListAdapterSpinner(this,
				android.R.layout.simple_spinner_item, listUtilidades);

		//spinnerUtilidades.setAdapter(spinner_adapterCat);
	}

	public void obtenerLooks() {
		ArrayList<Utilidad> utilidades = new ArrayList<Utilidad>();

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		utilidades = gestion.getUtilidades(db);

		Utilidad utilidad = utilidades.get(posiUtilidad);
		if (favorito == 1) {
			listLooks = gestion.getLooksFavoritos(db, cuentaSeleccionada);
		} else {
			listLooks = gestion.getLooks(db, cuentaSeleccionada);
		}
		db.close();

		if (utilidad.getIdUtilidad() != -1) {
			listLooks = Util.filtrarLooksUtilidad(listLooks,
					utilidad.getIdUtilidad());
		}

		Util.obtenerImagenLook(this, listLooks, 4);
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		switch (id) {
		case MENSAJE_CONFIRMAR_ELIMINAR:
			builder.setTitle(getResources().getString(R.string.atencion));
			builder.setMessage(getResources().getString(
					R.string.msnEliminarLook));
			builder.setIcon(R.drawable.ic_delete);
			builder.setCancelable(false);
			builder.setPositiveButton(
					getResources().getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							boolean ok = false;
							db = openOrCreateDatabase(BD_NOMBRE, 1, null);

							if (db != null) {
								ok = gestion.eliminarLook(db,
										lookSeleccionado.getIdLook(),
										lookSeleccionado.getIdFoto());
							}
							db.close();

							if (ok) {
								new CargarLooksTask().execute();

								Context context = getApplicationContext();
								CharSequence text = getResources().getString(
										R.string.deleteLookOk);
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, text,
										duration);
								toast.show();
							} else {
								Context context = getApplicationContext();
								CharSequence text = getResources().getString(
										R.string.deleteLookError);
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, text,
										duration);
								toast.show();
							}
							dialog.cancel();
						}
					}).setNegativeButton(
					getResources().getString(R.string.cancelar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			break;

		}
		return null;
	}

	public class GridAdapterSeleccionarLooks extends BaseAdapter implements
			OnClickListener {
		private Context context;
		ArrayList<Look> listaLooks = new ArrayList<Look>();
		View viewAnterior;

		public GridAdapterSeleccionarLooks(Context c, ArrayList<Look> listLooks) {
			context = c;
			listaLooks = listLooks;
		}

		public int getCount() {
			return listaLooks.size();
		}

		public Object getItem(int position) {
			return listaLooks.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Look look = listaLooks.get(position);

			View v;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.seleccionar_looks_adapter,
						parent, false);
			} else {
				v = (View) convertView;
			}

			ImageView imagenLook = (ImageView) v.findViewById(R.id.imagenLook);
			LinearLayout layoutImage = (LinearLayout) v
					.findViewById(R.id.layoutImageLook);
			LinearLayout layoutText = (LinearLayout) v
					.findViewById(R.id.layoutTextLook);
			LinearLayout layoutLook = (LinearLayout) v
					.findViewById(R.id.layoutLook);
			layoutLook.setOnClickListener(this);

			if (look.getFoto() != null) {
				imagenLook.setBackgroundDrawable(look.getFoto());
				layoutImage.setVisibility(View.VISIBLE);
				layoutText.setVisibility(View.GONE);
			} else {
				layoutImage.setVisibility(View.GONE);
				layoutText.setVisibility(View.VISIBLE);
			}

			ImageView imagenCheck = (ImageView) v.findViewById(R.id.imagecheck);

			if (idLook == listaLooks.get(position).getIdLook()) {
				imagenCheck.setBackgroundResource(R.drawable.tic);
			} else {
				imagenCheck.setBackgroundResource(android.R.color.transparent);
			}

			v.setTag(look.getIdLook());

			return v;
		}

		// Al pulsar un d�a del calendario
		@Override
		public void onClick(View view) {
			int position = (Integer) view.getTag();

			if (position == idLook) {
				idLook = -1;
			} else {
				idLook = position;
			}

			notifyDataSetChanged();
		}
	}

	public void mostrarMensaje(String texto) {
		Context context = getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public class CargarLooksTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(SeleccionarLookActivity.this);
			progDailog.setIndeterminate(false);
			progDailog.setMessage("Cargando ...");
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		// Decode image in background.
		@Override
		protected Void doInBackground(Integer... params) {

			// Recuperamos las prendas
			obtenerLooks();
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			final GridAdapterSeleccionarLooks gridadapter = new GridAdapterSeleccionarLooks(
					SeleccionarLookActivity.this, listLooks);
			gridview.setAdapter(gridadapter);
			progDailog.dismiss();
		}
	}

}
