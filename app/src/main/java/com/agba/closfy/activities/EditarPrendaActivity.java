package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class EditarPrendaActivity extends ActionBarActivity {
	private static final String KEY_CONTENT = "NuevaPrendaFragment:Content";
	private String mContent = "???";

	private LinearLayout layoutImagen;
	private ImageView imagenSeleccionada;
	private TextView textTipo;
	private TextView botonGuardar;
	private TextView botonCancelar;
	private ImageView checkFavoritos;
	private ListView listUtilidadesView;
	private TextView textTemporada;
	private TextView textoCambiar;
	private LinearLayout botonCambiarTemp;

	int idPrenda;
	int tipo;
	int temporada;
	int favoritoSelec;
	String idFoto = "";

	int estilo;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int cuentaSeleccionada;

	int idRadioTemporada;
	int favorito = 0;
	int idTipo = 0;
	String utilidades = "";

	ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private static final int ADD_UTILIDAD = 4;
	private static final int CHANGE_TEMP = 5;
	private static final int MENSAJE_ERROR_FOTO = 1;
	private static final int MENSAJE_ERROR_TIPO = 2;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editar_prenda);

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
				getResources().getString(R.string.editarPrenda));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		idRadioTemporada = 2;

		layoutImagen = (LinearLayout) this.findViewById(R.id.layoutImagen);

		Matrix matrix = new Matrix();
		matrix.postRotate(-3.0f);

		Bitmap marco = BitmapFactory.decodeResource(getResources(),
				R.drawable.marco);

		Bitmap rotateFotoMarco = Bitmap.createBitmap(marco, 0, 0,
				marco.getWidth(), marco.getHeight(), matrix, true);

		Drawable marcoDrawable = new BitmapDrawable(getResources(),
				rotateFotoMarco);

		layoutImagen.setBackgroundDrawable(marcoDrawable);

		imagenSeleccionada = (ImageView) this.findViewById(R.id.marcoIcon);

		checkFavoritos = (ImageView) this.findViewById(R.id.checkFavoritos);

		textTipo = (TextView) this.findViewById(R.id.textTipoPrenda);

		botonCambiarTemp = (LinearLayout) this
				.findViewById(R.id.botonCambiarTemp);

		botonGuardar = (TextView) this.findViewById(R.id.botonGuardar);
		botonCancelar = (TextView) this.findViewById(R.id.botonCancelar);

		listUtilidadesView = (ListView) this.findViewById(R.id.listUtilidades);

		ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listUtilidades = gestion.getUtilidades(db);
		}
		db.close();

		ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(this,
				listUtilidades);
		listUtilidadesView.setAdapter(adapterUtilidad);

		textTemporada = (TextView) this.findViewById(R.id.textTemporada);
		textoCambiar = (TextView) this. findViewById(R.id.textCambiar);

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		obtenerDatos();

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

		layoutImagen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditarPrendaActivity.this,
						AmpliarPrendaActivity.class);
				intent.putExtra("idPrenda", idPrenda);
				startActivity(intent);
			}
		});

		botonCambiarTemp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(EditarPrendaActivity.this,
						SelectTemporadaActivity.class);
				in.putExtra("Temporada", idRadioTemporada);
				startActivityForResult(in, CHANGE_TEMP);
			}
		});

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db = openOrCreateDatabase(BD_NOMBRE, 1, null);
				boolean ok = false;
				boolean errorTipo = false;

				// Comprobamos que todos los datos estan informados
				if (idRadioTemporada == -1 || idTipo == 0) {
					errorTipo = true;
				}

				// obtenemos la cadena de utilidades
				utilidades = Util.obtenerCadenaUtilidades(listIdsUtilidad);

				// Si no hay error insertamos la prenda

				if (db != null) {
					ok = gestion.editarPrenda(db, String.valueOf(idPrenda),
							idRadioTemporada, favorito, idFoto, utilidades, 0, 0,
							cuentaSeleccionada);
				}
				db.close();

				if (ok) {
					mostrarMensaje(getResources().getString(
							R.string.editPrendaOK));
					finish();
				} else {
					mostrarMensaje(getResources().getString(
							R.string.editPrendaKO));
				}

			}
		});

		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void obtenerDatos() {
		Bundle extras = getIntent().getExtras();
		idPrenda = extras.getInt("idPrenda");
		tipo = extras.getInt("tipo");
		temporada = extras.getInt("temporada");
		listIdsUtilidad = Util.obtenerListaUtilidades(extras
				.getString("utilidades"));
		favoritoSelec = extras.getInt("favorito");

		rellenarDatos();
	}

	public void rellenarDatos() {
		String[] tiposPrendas = getResources().getStringArray(
				R.array.tiposPrenda);

		textTipo.setText(tiposPrendas[tipo]);

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

		Prenda prenda = new Prenda();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			prenda = gestion.getPrendaById(db, idPrenda);
		}
		db.close();

		Bitmap original = Util.obtenerPrendaBitmap(this, prenda, 0, estilo);

		Matrix matrix = new Matrix();
		matrix.postRotate(-3.0f);

		Bitmap rotateFoto = Bitmap.createBitmap(original, 0, 0,
				original.getWidth(), original.getHeight(), matrix, true);

		imagenSeleccionada.setImageBitmap(rotateFoto);

		idFoto = prenda.getIdFoto();

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
		}
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case ADD_UTILIDAD:
			ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

			db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				listUtilidades = gestion.getUtilidades(db);
			}
			db.close();

			ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(this,
					listUtilidades);
			listUtilidadesView.setAdapter(adapterUtilidad);
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
		textoCambiar.setBackgroundResource(R.color.azul);
	}
}
