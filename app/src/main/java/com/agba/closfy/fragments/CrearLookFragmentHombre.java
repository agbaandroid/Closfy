package com.agba.closfy.fragments;

import java.util.ArrayList;

import org.lucasr.twowayview.TwoWayView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.activities.ResumenLookMainActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;

public class CrearLookFragmentHombre extends Fragment {
	private static final String KEY_CONTENT = "CrearLookFragment:Content";
	private String mContent = "???";

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;

	ProgressDialog progDailog;

	private static final int CREAR_LOOK = 1;

	int idRadioTemporada;
	boolean mosCompl;
	boolean mosAbrigo;
	boolean mosCalzado;
	ArrayList<Integer> listIdsUtilidad;
	int favorito;

	int cuentaSeleccionada;

	ListAdapterLooks adapterSup;
	ListAdapterLooks adapterInf;
	ListAdapterLooks adapterCuerpo;
	ListAdapterLooks adapterAbrigo;
	ListAdapterLooks adapterCalzado;
	ListAdapterLooks adapterComplemento;

	ArrayList<Prenda> listPrendasSup = new ArrayList<Prenda>();
	ArrayList<Prenda> listPrendasInf = new ArrayList<Prenda>();
	ArrayList<Prenda> listPrendasCuerpo = new ArrayList<Prenda>();
	ArrayList<Prenda> listPrendasAbrigo = new ArrayList<Prenda>();
	ArrayList<Prenda> listPrendasCalzado = new ArrayList<Prenda>();
	ArrayList<Prenda> listPrendasComplemento = new ArrayList<Prenda>();

	private ArrayList<Integer> listaPrendasSeleccionadas = new ArrayList<Integer>();

	TextView botonGuardar;
	TextView botonCancelar;

	static TwoWayView listPrendaSup = null;
	static TwoWayView listPrendaInf = null;
	static TwoWayView listPrendaCuerpo = null;
	static TwoWayView listPrendaAbrigo = null;
	static TwoWayView listPrendaCalzado = null;
	static TwoWayView listPrendaComplemento = null;

	private LinearLayout layoutPrendaAbrigo;
	private LinearLayout layoutPrendaCalzado;
	private LinearLayout layoutPrendaCompl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

	}

	public CrearLookFragmentHombre(int temporada, boolean mostrarCompl,
			boolean mostrarAbrigo, boolean mostrarCalzado,
			ArrayList<Integer> listIds, int favoritoSelec) {
		idRadioTemporada = temporada;
		mosCompl = mostrarCompl;
		mosAbrigo = mostrarAbrigo;
		mosCalzado = mostrarCalzado;
		listIdsUtilidad = listIds;
		favorito = favoritoSelec;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.crear_look_hombre, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		botonGuardar = (TextView) this.getView().findViewById(R.id.botonCrear);

		botonCancelar = (TextView) getView().findViewById(R.id.botonCancelar);

		layoutPrendaAbrigo = (LinearLayout) this.getView().findViewById(
				R.id.layoutPrendaAbrigo);

		layoutPrendaCalzado = (LinearLayout) this.getView().findViewById(
				R.id.layoutPrendaCalzado);

		layoutPrendaCompl = (LinearLayout) this.getView().findViewById(
				R.id.layoutPrendaCompl);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (!mosAbrigo) {
			layoutPrendaAbrigo.setVisibility(8);
		}

		if (!mosCalzado) {
			layoutPrendaCalzado.setVisibility(8);
		}

		if (!mosCompl) {
			layoutPrendaCompl.setVisibility(8);
		}

		new CargarPrendasTask().execute();

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String prendas = obtenerCadenaPrendas();

				// obtenemos la cadena de utilidades
				String utilidades = Util
						.obtenerCadenaUtilidades(listIdsUtilidad);

				Intent intent = new Intent(getActivity(),
						ResumenLookMainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("cadenaPrendas", prendas);
				bundle.putString("utilidades", utilidades);
				bundle.putInt("temporada", idRadioTemporada);
				intent.putExtras(bundle);
				startActivityForResult(intent, CREAR_LOOK);
				inicializar();
			}
		});

		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Fragment fragment = new CrearLookInicioFragment();
				if (fragment != null) {
					FragmentManager fragmentManager = getActivity()
							.getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.crearlookFragment, fragment).commit();

				}
			}
		});

	}

	public String obtenerCadenaPrendas() {
		String cadena = "";

		for (int i = 0; i < listaPrendasSeleccionadas.size(); i++) {
			cadena = cadena + String.valueOf(listaPrendasSeleccionadas.get(i)) + ";";
		}
		
		if(!cadena.equals("")){
			cadena.substring(0, cadena.length() - 1);
		}
		return cadena;
	}

	public void mostrarMensaje(String texto) {
		Context context = getActivity().getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public void inicializar() {
		listaPrendasSeleccionadas.clear();

		adapterSup = new ListAdapterLooks(getActivity(), listPrendasSup, 0);
		listPrendaSup.setAdapter(adapterSup);

		adapterInf = new ListAdapterLooks(getActivity(), listPrendasInf, 1);
		listPrendaInf.setAdapter(adapterInf);

		adapterCuerpo = new ListAdapterLooks(getActivity(), listPrendasCuerpo,
				2);
		listPrendaCuerpo.setAdapter(adapterCuerpo);

		adapterAbrigo = new ListAdapterLooks(getActivity(), listPrendasAbrigo,
				3);
		listPrendaAbrigo.setAdapter(adapterAbrigo);

		adapterCalzado = new ListAdapterLooks(getActivity(),
				listPrendasCalzado, 4);
		listPrendaCalzado.setAdapter(adapterCalzado);

		adapterComplemento = new ListAdapterLooks(getActivity(),
				listPrendasComplemento, 5);
		listPrendaComplemento.setAdapter(adapterComplemento);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != getActivity().RESULT_OK)
			return;

		switch (requestCode) {
		case CREAR_LOOK:
			Fragment fragment = new CrearLookInicioFragment();
			if (fragment != null) {
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.crearlookFragment, fragment).commit();

			}
			break;
		}

	}

	public void obtenerPrendas() {
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);

		if (db != null) {

			if (favorito == 1) {
				listPrendasSup = gestion.getPrendasFavoritasByIdTipo(db, 1,
						cuentaSeleccionada);
				listPrendasInf = gestion.getPrendasFavoritasByIdTipo(db, 2,
						cuentaSeleccionada);
				listPrendasCuerpo = gestion.getPrendasFavoritasByIdTipo(db, 3,
						cuentaSeleccionada);
				listPrendasAbrigo = gestion.getPrendasFavoritasByIdTipo(db, 4,
						cuentaSeleccionada);
				listPrendasCalzado = gestion.getPrendasFavoritasByIdTipo(db, 5,
						cuentaSeleccionada);
				listPrendasComplemento = gestion.getPrendasFavoritasByIdTipo(
						db, 6, cuentaSeleccionada);
			} else {
				listPrendasSup = gestion.getPrendasByIdTipo(db, 1,
						cuentaSeleccionada);
				listPrendasInf = gestion.getPrendasByIdTipo(db, 2,
						cuentaSeleccionada);
				listPrendasCuerpo = gestion.getPrendasByIdTipo(db, 3,
						cuentaSeleccionada);
				listPrendasAbrigo = gestion.getPrendasByIdTipo(db, 4,
						cuentaSeleccionada);
				listPrendasCalzado = gestion.getPrendasByIdTipo(db, 5,
						cuentaSeleccionada);
				listPrendasComplemento = gestion.getPrendasByIdTipo(db, 6,
						cuentaSeleccionada);
			}

			listPrendasSup = Util.filtrarPrendas(listPrendasSup,
					idRadioTemporada, listIdsUtilidad);
			listPrendasInf = Util.filtrarPrendas(listPrendasInf,
					idRadioTemporada, listIdsUtilidad);
			listPrendasAbrigo = Util.filtrarPrendas(listPrendasAbrigo,
					idRadioTemporada, listIdsUtilidad);
			listPrendasCuerpo = Util.filtrarPrendas(listPrendasCuerpo,
					idRadioTemporada, listIdsUtilidad);
			listPrendasComplemento = Util.filtrarPrendas(
					listPrendasComplemento, idRadioTemporada, listIdsUtilidad);
			listPrendasCalzado = Util.filtrarPrendas(listPrendasCalzado,
					idRadioTemporada, listIdsUtilidad);
		}

		db.close();

		listPrendasSup = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasSup, 2, estilo);
		listPrendasInf = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasInf, 2, estilo);
		listPrendasCuerpo = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasCuerpo, 2, estilo);
		listPrendasAbrigo = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasAbrigo, 2, estilo);
		listPrendasCalzado = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasCalzado, 2, estilo);
		listPrendasComplemento = Util.obtenerImagenesPrendas(getActivity(),
				listPrendasComplemento, 2, estilo);
	}

	public class ListAdapterLooks extends BaseAdapter implements
			OnClickListener {
		private LayoutInflater mInflater;
		private ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
		private LinearLayout layoutPrenda;
		View viewAnterior;
		int tipoPrenda;

		public ListAdapterLooks(Context context, ArrayList<Prenda> lista,
				int tipoPrendaAux) {
			listaPrendas = lista;
			mInflater = LayoutInflater.from(context);
			tipoPrenda = tipoPrendaAux;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listaPrendas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listaPrendas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Prenda prenda = listaPrendas.get(position);
			View v;
			if (convertView == null) {
				v = mInflater.inflate(R.layout.lista_prendas_look, null);
			} else {
				v = convertView;
			}

			layoutPrenda = (LinearLayout) v.findViewById(R.id.layoutPrenda);
			layoutPrenda.setOnClickListener(this);

			ImageView imagenPrenda = (ImageView) v
					.findViewById(R.id.imagePrendaLook);
			imagenPrenda.setImageDrawable(prenda.getFoto());

			ImageView imagenCheck = (ImageView) v.findViewById(R.id.imagecheck);

			if (listaPrendasSeleccionadas.contains(prenda.getIdPrenda())) {
				if (estilo == 1) {
					imagenCheck.setBackgroundResource(R.drawable.tic_azul);
				} else {
					imagenCheck.setBackgroundResource(R.drawable.tic);
				}
			} else {
				imagenCheck.setBackgroundResource(android.R.color.transparent);
			}

			v.setTag(prenda.getIdPrenda());
			return v;
		}

		// Al pulsar un dia del calendario
		@Override
		public void onClick(View view) {
			int idPrenda = (Integer) view.getTag();

			if (listaPrendasSeleccionadas.contains(idPrenda)) {
				int posi = listaPrendasSeleccionadas.indexOf(idPrenda);
				listaPrendasSeleccionadas.remove(posi);
			}else{
				listaPrendasSeleccionadas.add(idPrenda);
			}

			adapterSup.notifyDataSetChanged();
			adapterInf.notifyDataSetChanged();
			adapterCuerpo.notifyDataSetChanged();
			adapterAbrigo.notifyDataSetChanged();
			adapterCalzado.notifyDataSetChanged();
			adapterComplemento.notifyDataSetChanged();
		}

	}

	public class CargarPrendasTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
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
			adapterSup = new ListAdapterLooks(getActivity(), listPrendasSup, 0);
			adapterInf = new ListAdapterLooks(getActivity(), listPrendasInf, 1);
			adapterCuerpo = new ListAdapterLooks(getActivity(),
					listPrendasCuerpo, 2);
			adapterAbrigo = new ListAdapterLooks(getActivity(),
					listPrendasAbrigo, 3);
			adapterCalzado = new ListAdapterLooks(getActivity(),
					listPrendasCalzado, 4);
			adapterComplemento = new ListAdapterLooks(getActivity(),
					listPrendasComplemento, 5);

			listPrendaSup = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaSup);
			listPrendaSup.setAdapter(adapterSup);

			listPrendaInf = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaInf);
			listPrendaInf.setAdapter(adapterInf);

			listPrendaCuerpo = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaCuerpo);
			listPrendaCuerpo.setAdapter(adapterCuerpo);

			listPrendaAbrigo = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaAbrigo);
			listPrendaAbrigo.setAdapter(adapterAbrigo);

			listPrendaCalzado = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaCalzado);
			listPrendaCalzado.setAdapter(adapterCalzado);

			listPrendaComplemento = (TwoWayView) getView().findViewById(
					R.id.listViewPrendaComp);
			listPrendaComplemento.setAdapter(adapterComplemento);

			progDailog.dismiss();
		}
	}
}
