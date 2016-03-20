package com.agba.closfy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.activities.AddPrendaActivity;
import com.agba.closfy.activities.AmpliarPrendaActivity;
import com.agba.closfy.adapters.ListAdapterSpinner;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class NuevoMiArmarioFragment extends Fragment {
	private static final String KEY_CONTENT = "MiArmarioFragment:Content";
	private String mContent = "???";

	static final int MENSAJE_CONFIRMAR_ELIMINAR = 1;
	static final int AMPLIAR_PRENDA = 0;
	private final int PRENDA = 1;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	int cuentaSeleccionada;
	int estilo;

	private ImageView checkFavoritos;
	private ImageView opcionesPrenda;
	//int favorito = 0;

	ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();
	GridView gridview;
	AdView adView;
	Prenda prendaSeleccionada = new Prenda();

	//private Spinner spinnerTipo;
	//private Spinner spinnerTemporada;
	//private Spinner spinnerUtilidad;
	int idTipo = 0;
	int posiUtilidad = 0;
	int idTemporada = 2;

	boolean cargaInicialTipo = true;
	boolean cargaInicialTemporada = true;
	boolean cargaInicialUtilidad = true;
	boolean cargado = false;
	int[] prendas;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	ProgressDialog progDailog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.nuevo_armario, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		new CargarPrendasTask().execute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		gridview = (GridView) getView().findViewById(R.id.gridArmario);

		/*spinnerTipo = (Spinner) this.getView().findViewById(
				R.id.spinnerTipoPrendaArmario);

		spinnerTemporada = (Spinner) this.getView().findViewById(
				R.id.spinnerTemporadaArmario);

		spinnerUtilidad = (Spinner) this.getView().findViewById(
				R.id.spinnerUtilidadArmario);

		checkFavoritos = (ImageView) this.getView().findViewById(
				R.id.checkFavoritos);*/
		adView = (AdView) this.getView().findViewById(R.id.adView);


		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		// Rellenamos el spinner tipo
		obtenerSpinners();

		// Recuperamos las prendas
		// obtenerPrendas();

		/*spinnerTipo
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (estilo == 1 && position > 2) {
							idTipo = position + 1;
						} else {
							idTipo = position;
						}
						if (!cargaInicialTipo) {
							new CargarPrendasTask().execute();
						} else {
							cargaInicialTipo = false;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		spinnerTemporada
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						idTemporada = position;
						if (!cargaInicialTemporada) {
							new CargarPrendasTask().execute();
						} else {
							cargaInicialTemporada = false;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		spinnerUtilidad
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						posiUtilidad = position;
						if (!cargaInicialUtilidad) {
							new CargarPrendasTask().execute();
						} else {
							cargaInicialUtilidad = false;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		checkFavoritos.setOnClickListener(new OnClickListener() {
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
				new CargarPrendasTask().execute();

			}
		});*/
	}

	public void obtenerSpinners() {
		// rellenamos el spinner tipo prenda
		ArrayAdapter<CharSequence> adapterList;
		ArrayAdapter<CharSequence> adapterListTemp;

		if (estilo == 1) {
			adapterList = ArrayAdapter.createFromResource(getActivity(),
					R.array.tiposPrendaArmarioHombre,
					android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			//spinnerTipo.setAdapter(adapterList);
		} else {
			adapterList = ArrayAdapter.createFromResource(getActivity(),
					R.array.tiposPrendaArmario,
					android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			//spinnerTipo.setAdapter(adapterList);
		}

		adapterListTemp = ArrayAdapter.createFromResource(getActivity(),
				R.array.tiposTemporada, android.R.layout.simple_spinner_item);
		adapterListTemp.setDropDownViewResource(R.layout.spinner);
		//spinnerTemporada.setAdapter(adapterListTemp);

		//spinnerTemporada.setSelection(2);

		ArrayList<Utilidad> listCategorias = new ArrayList<Utilidad>();
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			// Recuperamos el listado del spinner Categorias
			listCategorias = (ArrayList<Utilidad>) gestion
					.getUtilidadesFiltro(db);
		}
		db.close();

		// Creamos el adaptador
		ListAdapterSpinner spinner_adapterUti = new ListAdapterSpinner(
				getActivity(), android.R.layout.simple_spinner_item,
				listCategorias);

		//spinnerUtilidad.setAdapter(spinner_adapterUti);

	}

	public ArrayList<Prenda> obtenerPrendas() {
		ArrayList<Utilidad> utilidades = new ArrayList<Utilidad>();

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listPrendas = gestion.getPrendasFiltros(db, idTipo, idTemporada,
					0, cuentaSeleccionada);
		}
		utilidades = gestion.getUtilidades(db);
		db.close();

		Utilidad utilidad = utilidades.get(posiUtilidad);
		if (utilidad.getIdUtilidad() != -1) {
			listPrendas = Util.filtrarPrendasUtilidad(listPrendas,
					utilidad.getIdUtilidad());
		}

		listPrendas = Util.obtenerImagenesPrendas(getActivity(), listPrendas,
				2, estilo);

		return listPrendas;

	}

	public void guardarPrendaSeleccionado(String idPrenda) {
		SharedPreferences prefs;
		SharedPreferences.Editor editor;

		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.putString("idPrenda", idPrenda);
		editor.putBoolean("mostrarMenu", true);
		editor.commit();
	}

	public int[] obtenerCadenaPrendas(){
		prendas = new int[listPrendas.size()];

		for(int i=0;i<listPrendas.size();i++){
			Prenda prenda = listPrendas.get(i);
			prendas[i] = prenda.getIdPrenda();
		}

		return prendas;
	}

	public Prenda obtenerPrendaSeleccionada() {
		Prenda mov = new Prenda();
		SharedPreferences prefs;
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);

		String idPrenda = prefs.getString("idPrenda", "0");
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			mov = gestion.getPrendaById(db, Integer.parseInt(idPrenda));
		}
		db.close();
		return mov;
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog alert;
		switch (id) {
		case MENSAJE_CONFIRMAR_ELIMINAR:
			builder.setTitle(getResources().getString(R.string.atencion));
			builder.setMessage(getResources().getString(
					R.string.msnEliminarPrenda));
			builder.setIcon(R.drawable.ic_delete);
			builder.setCancelable(false);
			builder.setPositiveButton(
					getResources().getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							boolean ok = false;
							db = getActivity().openOrCreateDatabase(BD_NOMBRE,
									1, null);

							if (db != null) {
								ok = gestion.eliminarPrenda(db,
										prendaSeleccionada.getIdPrenda(),
										prendaSeleccionada.getIdFoto());
							}
							db.close();

							if (ok) {
								Context context = getActivity()
										.getApplicationContext();
								CharSequence text = getResources().getString(
										R.string.deletePrendaOk);
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, text,
										duration);
								toast.show();

								new CargarPrendasTask().execute();
							} else {
								Context context = getActivity()
										.getApplicationContext();
								CharSequence text = getResources().getString(
										R.string.deletePrendaError);
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

	public class CargarPrendasTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
			progDailog.setIndeterminate(false);
			progDailog.setMessage(getResources().getString(R.string.cargando));
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
			final GridAdapter gridadapter = new GridAdapter(getActivity(),
					listPrendas);
			gridview.setAdapter(gridadapter);

			// Look up the AdView as a resource and load a request.
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);

			progDailog.dismiss();
		}
	}

	public void cambiarEstiloHombre() {
		// spinnerTipo.setBackgroundResource(R.drawable.spinner_azul);
		//checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_setting_mas, menu);
	}

	// Aadiendo funcionalidad a las opciones de men
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				Intent intent = new Intent(getActivity(), AddPrendaActivity.class);
				startActivityForResult(intent, PRENDA);
				return true;
			case android.R.id.home:
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!cargado) {
			cargado = true;
			new CargarPrendasTask().execute();
		}
	}

	public class GridAdapter extends BaseAdapter implements OnClickListener {
		private Context context;
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();

		public GridAdapter(Context c, ArrayList<Prenda> listPrendas) {
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

		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.armario_adapter, parent, false);
			} else {
				v = convertView;
			}

			LinearLayout layoutImagenPrenda = (LinearLayout) v
					.findViewById(R.id.layoutImagenPrenda);
			layoutImagenPrenda.setOnClickListener(this);
			layoutImagenPrenda.setTag(position);

			ImageView imagenPrenda = (ImageView) v
					.findViewById(R.id.imagenPrenda);

			imagenPrenda.setImageDrawable(listaPrendas.get(position).getFoto());
			imagenPrenda.getLayoutParams().height = 350;
			imagenPrenda.getLayoutParams().width = 300;
			return v;
		}

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			Prenda prenda = listPrendas.get(position);
			guardarPrendaSeleccionado(String.valueOf(prenda.getIdPrenda()));

			switch (v.getId()) {
				case R.id.layoutImagenPrenda:
					Intent intent = new Intent(getActivity(),
							AmpliarPrendaActivity.class);
					intent.putExtra("idPrenda", prenda.getIdPrenda());
					intent.putExtra("prendas", obtenerCadenaPrendas());
					intent.putExtra("posicion", position);
					startActivityForResult(intent, AMPLIAR_PRENDA);
					break;
			}
		}
	}
}
