package com.agba.closfy.fragments;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.InspiracionesActivity;
import com.agba.closfy.activities.SelectTemporadaActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

public class CrearLookInicioFragment extends Fragment {
	private static final String KEY_CONTENT = "CrearLookInicioFragment:Content";
	private String mContent = "???";

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private LinearLayout botonCambiarTemp;
	LinearLayout checkCompl;
	LinearLayout checkAbrigo;
	LinearLayout checkCalzado;

	int estilo;

	private ImageView checkFavoritos;

	ImageView imgCompl;
	ImageView imgAbrigo;
	ImageView imgCalzado;
	LinearLayout botonSiguiente;

	TextView textCambiar;
	TextView textInspiraciones;

	boolean mostrarCompl = true;
	boolean mostrarAbrigo = true;
	boolean mostrarCalzado = true;

	int favorito = 0;

	SharedPreferences prefs;
	int cuentaSeleccionada;

	int idRadioTemporada;
	ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();

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

		botonCambiarTemp = (LinearLayout) this.getView().findViewById(
				R.id.botonCambiarTemp);

		checkCompl = (LinearLayout) getActivity().findViewById(R.id.checkCompl);
		checkAbrigo = (LinearLayout) getActivity().findViewById(
				R.id.checkAbrigo);
		checkCalzado = (LinearLayout) getActivity().findViewById(
				R.id.checkCalzado);
		checkFavoritos = (ImageView) this.getView().findViewById(
				R.id.checkFavoritos);

		textCambiar = (TextView) getActivity().findViewById(R.id.textCambiar);
		textInspiraciones = (TextView) getActivity().findViewById(
				R.id.textInspiraciones);

		imgCompl = (ImageView) getActivity().findViewById(R.id.imgCompl);
		imgAbrigo = (ImageView) getActivity().findViewById(R.id.imgAbrigo);
		imgCalzado = (ImageView) getActivity().findViewById(R.id.imgCalzado);
		botonSiguiente = (LinearLayout) getActivity().findViewById(
				R.id.botonSiguiente);

		idRadioTemporada = 2;

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
//			imgCompl.setBackgroundResource(R.drawable.check_azul_on);
//			imgAbrigo.setBackgroundResource(R.drawable.check_azul_on);
//			imgCalzado.setBackgroundResource(R.drawable.check_azul_on);

			cambiarEstiloHombre();
		} else {
//			imgCompl.setBackgroundResource(R.drawable.check_rosa_on);
//			imgAbrigo.setBackgroundResource(R.drawable.check_rosa_on);
//			imgCalzado.setBackgroundResource(R.drawable.check_rosa_on);
		}

		listIdsUtilidad.add(-1);

		botonCambiarTemp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),
						SelectTemporadaActivity.class);
				in.putExtra("Temporada", idRadioTemporada);
				startActivityForResult(in, CHANGE_TEMP);
			}
		});

		checkCompl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mostrarCompl) {
					mostrarCompl = false;
					if (estilo == 1) {
						imgCompl.setBackgroundResource(R.drawable.check_azul_off);
					} else {
						imgCompl.setBackgroundResource(R.drawable.check_rosa_off);
					}
				} else {
					mostrarCompl = true;
					if (estilo == 1) {
						imgCompl.setBackgroundResource(R.drawable.check_azul_on);
					} else {
						imgCompl.setBackgroundResource(R.drawable.check_rosa_on);
					}
				}
			}
		});

		checkAbrigo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mostrarAbrigo) {
					mostrarAbrigo = false;
					if (estilo == 1) {
						imgAbrigo
								.setBackgroundResource(R.drawable.check_azul_off);
					} else {
						imgAbrigo
								.setBackgroundResource(R.drawable.check_rosa_off);
					}
				} else {
					mostrarAbrigo = true;
					if (estilo == 1) {
						imgAbrigo
								.setBackgroundResource(R.drawable.check_azul_on);
					} else {
						imgAbrigo
								.setBackgroundResource(R.drawable.check_rosa_on);
					}
				}
			}
		});

		checkCalzado.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mostrarCalzado) {
					mostrarCalzado = false;
					if (estilo == 1) {
						imgCalzado
								.setBackgroundResource(R.drawable.check_azul_off);
					} else {
						imgCalzado
								.setBackgroundResource(R.drawable.check_rosa_off);
					}
				} else {
					mostrarCalzado = true;
					if (estilo == 1) {
						imgCalzado
								.setBackgroundResource(R.drawable.check_azul_on);
					} else {
						imgCalzado
								.setBackgroundResource(R.drawable.check_rosa_on);
					}
				}
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
			}
		});

		botonSiguiente.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Fragment fragment;

				if (estilo == 1) {
					fragment = new CrearLookFragmentHombre(idRadioTemporada,
							mostrarCompl, mostrarAbrigo, mostrarCalzado,
							listIdsUtilidad, favorito);
				} else {
					fragment = new CrearLookFragment(idRadioTemporada,
							mostrarCompl, mostrarAbrigo, mostrarCalzado,
							listIdsUtilidad, favorito);
				}

				if (fragment != null) {
					FragmentManager fragmentManager = getActivity()
							.getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.crearlookFragment, fragment).commit();

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
				idRadioTemporada = 0;
				break;
			case 1:
				textTemporada.setText(getResources().getString(
						R.string.primaveraVerano));
				idRadioTemporada = 1;
				break;
			case 2:
				textTemporada.setText(getResources()
						.getString(R.string.todoAno));
				idRadioTemporada = 2;
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
			for (int i = 0; i < listIdsUtilidad.size(); i++) {
				if (listaUtilidad.get(position).getIdUtilidad() == listIdsUtilidad
						.get(i)) {
					encontrado = true;
				}
			}

			if (encontrado) {
				if (estilo == 1) {
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

			if (id == -1 & !listIdsUtilidad.contains(id)) {
				listIdsUtilidad.clear();
			}

			if (listIdsUtilidad.contains(id)) {
				if (id != -1) {
					listIdsUtilidad.remove(id);
				}
			} else {
				if (id != 0 & listIdsUtilidad.contains(-1)) {
					listIdsUtilidad.remove(0);
				}
				listIdsUtilidad.add(id);
			}

			if (listIdsUtilidad.size() == 0) {
				listIdsUtilidad.add(-1);
			}

			notifyDataSetChanged();

		}

	}

	public void cambiarEstiloHombre() {
		checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
		textCambiar.setBackgroundResource(R.color.azul);
		textInspiraciones.setBackgroundResource(R.color.azul);
	}

}
