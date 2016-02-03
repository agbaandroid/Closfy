package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class InspiracionesActivity extends ActionBarActivity {

	static final int MENSAJE_CONFIRMAR_ELIMINAR = 1;
	static final int EDIT_PRENDA = 0;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	int cuentaSeleccionada;
	int estilo;

	ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();
	GridView gridview;
	AdView adView;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	ProgressDialog progDailog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.inspiraciones);

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

		getSupportActionBar().setTitle(
				getResources().getString(R.string.inspiraciones));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);		

		gridview = (GridView) findViewById(R.id.gridInspiraciones);
		adView = (AdView) findViewById(R.id.adView);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}
		
		new CargarInspiracionesTask().execute();
		
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

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

	public void obtenerPrendas() {

		if (estilo == 1) {
			listPrendas = Util.obtenerInspiraciones(this, estilo);
		} else {
			listPrendas = Util.obtenerInspiraciones(this, estilo);
		}
	}

	public class CargarInspiracionesTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(InspiracionesActivity.this);
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
			obtenerPrendas();
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			final GridAdapterInspiraciones gridadapter = new GridAdapterInspiraciones(
					InspiracionesActivity.this, listPrendas);
			gridview.setAdapter(gridadapter);			
			progDailog.dismiss();
		}
	}

	public class GridAdapterInspiraciones extends BaseAdapter implements
			OnClickListener {
		private Context context;
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();

		public GridAdapterInspiraciones(Context c, ArrayList<Prenda> listPrendas) {
			context = c;
			listaPrendas = listPrendas;
		}

		public int getCount() {
			return listaPrendas.size();
		}

		public Object getItem(int position) {
			return listaPrendas.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.mis_looks_adapter_nuevo, parent, false);
			} else {
				v = (View) convertView;
			}

			ImageView imagenPrenda = (ImageView) v
					.findViewById(R.id.imagenLook);

			imagenPrenda.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imagenPrenda.setPadding(5, 5, 5, 5);

			imagenPrenda.setOnClickListener(this);
			imagenPrenda.setImageDrawable(listaPrendas.get(position).getFoto());

			imagenPrenda.setTag(listaPrendas.get(position).getIdPrenda());
			v.setTag(listaPrendas.get(position).getIdPrenda());

			return v;
		}

		// Al pulsar un d�a del calendario
		@Override
		public void onClick(View view) {
			int position = (Integer) view.getTag();
			Intent intent = new Intent(InspiracionesActivity.this, AmpliarInspiracionActivity.class);
			intent.putExtra("idPrenda", position);
			startActivity(intent);
		}

	}

}
