package com.agba.closfy.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

public class EditarLookActivity extends ActionBarActivity {
	private static final String KEY_CONTENT = "VerLookActivity:Content";
	private String mContent = "???";

	private LinearLayout lookLayout;
	private TextView botonGuardar;
	private TextView botonCancelar;
	private ImageView checkFavoritos;
	private ListView listUtilidadesView;
	private LinearLayout botonCambiarTemp;
	private TextView textTemporada;
	private TextView textoCambiar;
	private ImageView imagenLook;
	private LinearLayout layoutImage;
	private LinearLayout layoutText;

	LinearLayout notas;
	TextView textNotas;
	String notasString = "";

	int estilo;

	int idLook;
	int tipo;
	int temporada;
	int favoritoSelec;
	String idFoto = "";

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int cuentaSeleccionada;

	int idRadioTemporada;
	int favorito = 0;
	int idTipo = 0;
	String utilidades = "";

	Look look;
	ArrayList<Look> listLooks;

	ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private static final int EDIT_LOOK = 3;
	private static final int NOTAS = 4;
	private static final int CHANGE_TEMP = 5;
	private static final int MENSAJE_ERROR_FOTO = 1;
	private static final int MENSAJE_ERROR_TIPO = 2;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		idLook = extras.getInt("idLook");
		temporada = extras.getInt("temporada");
		listIdsUtilidad = Util.obtenerListaUtilidades(extras
				.getString("utilidades"));
		favoritoSelec = extras.getInt("favorito");

		look = new Look();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			look = gestion.getLookById(db, idLook);
		}
		db.close();

		listLooks = new ArrayList<Look>();
		listLooks.add(look);

		setContentView(R.layout.editar_look);

		// Cuenta seleccionada
		prefs = this.getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
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
				getResources().getString(R.string.detalle));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		lookLayout = (LinearLayout) findViewById(R.id.lookLayout);

		imagenLook = (ImageView) findViewById(R.id.imagenLook);
		layoutText = (LinearLayout) this.findViewById(R.id.layoutTextLook);
		layoutImage = (LinearLayout) findViewById(R.id.layoutImageLook);

		checkFavoritos = (ImageView) this.findViewById(R.id.checkFavoritos);

		botonCambiarTemp = (LinearLayout) this
				.findViewById(R.id.botonCambiarTemp);

		textTemporada = (TextView) this.findViewById(R.id.textTemporada);
		textoCambiar = (TextView) this.findViewById(R.id.textCambiar);

		botonGuardar = (TextView) this.findViewById(R.id.botonGuardar);
		botonCancelar = (TextView) this.findViewById(R.id.botonCancelar);

		listUtilidadesView = (ListView) this.findViewById(R.id.listUtilidades);

		notas = (LinearLayout) findViewById(R.id.notas);
		textNotas = (TextView) findViewById(R.id.textNotas);

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listUtilidades = gestion.getUtilidades(db);
		}
		db.close();

		ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(this,
				listUtilidades);
		listUtilidadesView.setAdapter(adapterUtilidad);

		rellenarDatos();

		checkFavoritos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favorito == 0) {
					favorito = 1;
					if (estilo == 0) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_on);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_on);
					}
				} else {
					favorito = 0;
					if (estilo == 0) {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_corazon_off);
					} else {
						checkFavoritos
								.setBackgroundResource(R.drawable.check_estrella_off);
					}
				}
			}
		});

		lookLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditarLookActivity.this,
						EditarResumenLookMainActivity.class);
				intent.putExtra("idLook", idLook);
				startActivityForResult(intent, EDIT_LOOK);
			}
		});

		botonCambiarTemp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(EditarLookActivity.this,
						SelectTemporadaActivity.class);
				in.putExtra("Temporada", idRadioTemporada);
				startActivityForResult(in, CHANGE_TEMP);
			}
		});

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db = openOrCreateDatabase(BD_NOMBRE, 1, null);
				boolean ok = false;

				// obtenemos la cadena de utilidades
				utilidades = Util.obtenerCadenaUtilidades(listIdsUtilidad);

				// Si no hay error insertamos la prenda

				if (db != null) {
					ok = gestion.editarLook(db, String.valueOf(idLook),
							idRadioTemporada, utilidades, favorito, notasString);
				}
				db.close();

				if (ok) {
					mostrarMensaje(getResources()
							.getString(R.string.editLookOK));
					finish();
				} else {
					mostrarMensaje(getResources()
							.getString(R.string.editLookKO));
				}

			}
		});

		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		notas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent inNota = new Intent(EditarLookActivity.this,
						AddNotasActivity.class);
				inNota.putExtra("notas", notasString);
				inNota.putExtra("isEdicion", true);
				EditarLookActivity.this.startActivityForResult(inNota, NOTAS);
			}
		});
	}

	public void rellenarDatos() {

		if (favoritoSelec == 1) {
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

		switch (temporada) {
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
			textTemporada.setText(getResources().getString(R.string.todoAno));
			idRadioTemporada = 2;
			break;
		default:
			break;
		}

		Look look = new Look();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			look = gestion.getLookById(db, idLook);
		}
		db.close();

		ArrayList<Look> listLooks = new ArrayList<Look>();
		listLooks.add(look);

		if (look.getNotas() != null) {
			notasString = look.getNotas();
		}

		Util.obtenerImagenLook(this, listLooks, 4);

		if (look.getFoto() != null) {
			imagenLook.setBackgroundDrawable(look.getFoto());
		} else {
			layoutImage.setVisibility(View.GONE);
			layoutText.setVisibility(View.VISIBLE);
		}

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

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		switch (id) {
		case MENSAJE_ERROR_FOTO:
			builder.setMessage(
					getResources().getString(R.string.fotoObligatoria))
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case EDIT_LOOK:
			db = openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				look = gestion.getLookById(db, idLook);
			}
			db.close();
			
			listLooks.clear();
			listLooks.add(look);
			Util.obtenerImagenLook(this, listLooks, 4);

			if (look.getFoto() != null) {
				imagenLook.setBackgroundDrawable(look.getFoto());
				layoutImage.setVisibility(View.VISIBLE);
				layoutText.setVisibility(View.GONE);
			} else {
				layoutImage.setVisibility(View.GONE);
				layoutText.setVisibility(View.VISIBLE);
			}
			break;
		case NOTAS:
			notasString = data.getExtras().getString("notas");
			break;
		case CHANGE_TEMP:
			int temp = data.getExtras().getInt("Temporada");
			textTemporada = (TextView) this.findViewById(R.id.textTemporada);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	public void crearDirectorio(File dbFile) {
		if (!dbFile.exists()) {
			dbFile.mkdirs();

			File fileNoFile = new File(dbFile, ".nomedia");
			try {
				fileNoFile.createNewFile();
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(fileNoFile);
				fo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void mostrarMensaje(String texto) {
		Context context = this.getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
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

		// Al pulsar un d�a del calendario
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
		textNotas.setBackgroundResource(R.color.azul);
		textoCambiar.setBackgroundResource(R.color.azul);
	}
}
