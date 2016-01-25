package com.agba.closfy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.CrearLookPrincipalActivity;
import com.agba.closfy.activities.InspiracionesActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

import java.util.ArrayList;
import java.util.Locale;

public class CrearLookInicioFragment extends Fragment {
	private static final String KEY_CONTENT = "CrearLookInicioFragment:Content";
	private String mContent = "???";

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private Spinner spinnerTemporada;
	int idTemporada;

	private ImageView checkFavoritos;

	TextView textCambiar;
	TextView textInspiraciones;

	SharedPreferences prefs;
	int cuentaSeleccionada;


	private static final int CHANGE_TEMP = 5;

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

		return inflater.inflate(R.layout.crear_look_inicio, container, false);
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

		((CrearLookPrincipalActivity) getActivity()).cambiarActionBar(1);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		ListView listUtilidadesView = (ListView) this.getView().findViewById(
				R.id.listUtilidades);

		ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listUtilidades = gestion.getUtilidades(db);
		}
		db.close();

		ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(
				getActivity(), listUtilidades);
		listUtilidadesView.setAdapter(adapterUtilidad);

		spinnerTemporada = (Spinner) this.getView().findViewById(
				R.id.spinnerTemporada);

		checkFavoritos = (ImageView) this.getView().findViewById(
				R.id.checkFavoritos);

		textCambiar = (TextView) getActivity().findViewById(R.id.textCambiar);
		textInspiraciones = (TextView) getActivity().findViewById(
				R.id.textInspiraciones);

		((CrearLookPrincipalActivity) getActivity()).idRadioTemporada = 2;

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			((CrearLookPrincipalActivity) getActivity()).estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (((CrearLookPrincipalActivity) getActivity()).estilo == 1) {
			cambiarEstiloHombre();
		}

		obtenerTemporadas();
		idTemporada = 2;
		spinnerTemporada.setSelection(2);

		((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.add(-1);

		spinnerTemporada
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
											   View view, int position, long id) {

						idTemporada = position;
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		checkFavoritos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CrearLookPrincipalActivity) getActivity()).favorito == 0) {
					((CrearLookPrincipalActivity) getActivity()).favorito = 1;
					if (((CrearLookPrincipalActivity) getActivity()).estilo == 1) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_on);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_on);
					}
				} else {
					((CrearLookPrincipalActivity) getActivity()).favorito = 0;
					if (((CrearLookPrincipalActivity) getActivity()).estilo == 1) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_off);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_off);
					}
				}
			}
		});

		textInspiraciones.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),
						InspiracionesActivity.class);

				startActivity(in);
			}
		});

	}

	public void obtenerTemporadas() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.tiposTemporada,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner);
		spinnerTemporada.setAdapter(adapter);

		spinnerTemporada.setSelection(0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != getActivity().RESULT_OK)
			return;

		switch (requestCode) {
		case CHANGE_TEMP:
			int temp = data.getExtras().getInt("Temporada");
			TextView textTemporada = (TextView) this.getView().findViewById(
					R.id.textTemporada);
			switch (temp) {
			case 0:
				textTemporada.setText(getResources().getString(
						R.string.otonoInvierno));
				((CrearLookPrincipalActivity) getActivity()).idRadioTemporada = 0;
				break;
			case 1:
				textTemporada.setText(getResources().getString(
						R.string.primaveraVerano));
				((CrearLookPrincipalActivity) getActivity()).idRadioTemporada = 1;
				break;
			case 2:
				textTemporada.setText(getResources()
						.getString(R.string.todoAno));
				((CrearLookPrincipalActivity) getActivity()).idRadioTemporada = 2;
				break;
			default:
				break;
			}

			break;
		}
	}

	public class ListAdapterUtilidad extends BaseAdapter implements
			OnClickListener {
		private LayoutInflater mInflater;
		private ArrayList<Utilidad> listaUtilidad = new ArrayList<Utilidad>();
		Locale locale = Locale.getDefault();
		String languaje = locale.getLanguage();
		ArrayList<View> listViews = new ArrayList<View>();

		public ListAdapterUtilidad(Context context, ArrayList<Utilidad> lista) {
			listaUtilidad = lista;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listaUtilidad.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listaUtilidad.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView tic;
			TextView textUtilidad;
			LinearLayout linearLayoutLista;

			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.lista_utilidades, null);
			}

			tic = (ImageView) convertView.findViewById(R.id.imgUtilidad);
			textUtilidad = (TextView) convertView
					.findViewById(R.id.textUtilidad);

			textUtilidad.setText(listaUtilidad.get(position).getNombre());

			linearLayoutLista = (LinearLayout) convertView
					.findViewById(R.id.linearLayoutLista);
			linearLayoutLista.setOnClickListener(this);

			convertView.setTag(listaUtilidad.get(position).getIdUtilidad());
			tic.setTag(listaUtilidad.get(position).getIdUtilidad());

			boolean encontrado = false;
			for (int i = 0; i < ((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.size(); i++) {
				if (listaUtilidad.get(position).getIdUtilidad() == ((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad
						.get(i)) {
					encontrado = true;
				}
			}

			if (encontrado) {
				if (((CrearLookPrincipalActivity) getActivity()).estilo == 1) {
					tic.setBackgroundResource(R.drawable.tic_azul);
				} else {
					tic.setBackgroundResource(R.drawable.tic);
				}
			} else {
				tic.setBackgroundColor(getResources().getColor(
						android.R.color.transparent));
			}

			return convertView;
		}

		// Al pulsar un dia del calendario
		@Override
		public void onClick(View view) {
			Integer id = (Integer) view.getTag();

			if (id == -1 & !((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.contains(id)) {
				((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.clear();
			}

			if (((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.contains(id)) {
				if (id != -1) {
					((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.remove(id);
				}
			} else {
				if (id != 0 & ((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.contains(-1)) {
					((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.remove(0);
				}
				((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.add(id);
			}

			if (((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.size() == 0) {
				((CrearLookPrincipalActivity) getActivity()).listIdsUtilidad.add(-1);
			}

			notifyDataSetChanged();

		}

	}

	public void cambiarEstiloHombre() {
		checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
	}

}
