package com.agba.closfy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.activities.PrendaBasicaActivity;
import com.agba.closfy.activities.SelectTemporadaActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

import eu.janmuller.android.simplecropimage.CropImage;

public class NuevaPrendaFragment extends Fragment {
	private static final String KEY_CONTENT = "NuevaPrendaFragment:Content";
	private String mContent = "???";

	Logger log;

	String path = Environment.getExternalStorageDirectory() + "/Closfy/Prendas";

	private Uri mImageCaptureUri;
	private String urlAux = "";
	private Uri tmpImgUri;
	private LinearLayout layoutImagen;
	private ImageView imagenSeleccionada;
	private Spinner spinnerTipo;
	private LinearLayout botonGuardar;
	private ImageView checkFavoritos;
	private ListView listUtilidadesView;
	private LinearLayout botonCambiarTemp;
	TextView textTemporada;
	int estilo;
	private TextView textoCambiar;

	ListAdapterUtilidad adapterUtilidad;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int cuentaSeleccionada;

	int idRadioTemporada;
	int favorito = 0;
	int idTipo = 0;
	int prendaBasica = 0;
	int idPrendaBasica;
	String utilidades = "";
	ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	String idFoto = "";

	private static final int PICK_FROM_CAMERA = 1;
	private static final int REQUEST_CODE_CROP_IMAGE = 2;
	private static final int PICK_FROM_FILE = 3;
	private static final int ADD_UTILIDAD = 4;
	private static final int CHANGE_TEMP = 5;
	private static final int PRENDA_BASICA = 6;
	private static final int MENSAJE_ERROR_FOTO = 1;
	private static final int MENSAJE_ERROR_TIPO = 2;
	private static final int MENSAJE_ERROR_TIPO_BASICA = 3;

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case PICK_FROM_CAMERA:
				// doCrop();
				try {
					if (mImageCaptureUri == null) {
						urlAux = prefs.getString("urlImagen", "");

						mImageCaptureUri = Uri.fromFile(new File(urlAux));
					}
					runCropImage(mImageCaptureUri.getPath());
				} catch (Exception e) {
					e.printStackTrace();
					Context context = getActivity().getApplicationContext();
					CharSequence text = (getActivity().getResources()
							.getString(R.string.errorFoto));
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				break;

			case PICK_FROM_FILE:
				try {

					InputStream inputStream = getActivity()
							.getContentResolver().openInputStream(
									data.getData());
					FileOutputStream fileOutputStream = new FileOutputStream(
							mImageCaptureUri.getPath());
					copyStream(inputStream, fileOutputStream);
					fileOutputStream.close();
					inputStream.close();

					runCropImage(mImageCaptureUri.getPath());

					prendaBasica = 0;

				} catch (Exception e) {
					Log.e("ERROR", "Error while creating temp file", e);
					Context context = getActivity().getApplicationContext();
					CharSequence text = (getActivity().getResources()
							.getString(R.string.errorFoto));
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				break;
			case REQUEST_CODE_CROP_IMAGE:
				String path = data.getStringExtra(CropImage.IMAGE_PATH);
				// if nothing received
				if (path == null) {
					return;
				}
				// cropped bitmap
				Bitmap original = BitmapFactory.decodeFile(path);
				// imagenSeleccionada.setImageBitmap(bitmapCrop);

				Matrix matrix = new Matrix();
				matrix.postRotate(-3.0f);

				Bitmap rotateFoto = Bitmap
						.createBitmap(original, 0, 0, original.getWidth(),
								original.getHeight(), matrix, true);

				imagenSeleccionada.setImageBitmap(rotateFoto);

				File dbFile = new File(
						Environment.getExternalStorageDirectory(),
						"/Closfy/Prendas");

				crearDirectorio(dbFile);

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				original.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

				// you can create a new file name "test.jpg" in sdcard
				// folder.
				idFoto = "prenda_" + String.valueOf(System.currentTimeMillis())
						+ ".jpg";
				File file = new File(dbFile, idFoto);
				try {
					file.createNewFile();
					// write the bytes in file
					FileOutputStream fo = new FileOutputStream(file);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				tmpImgUri = Uri.fromFile(new File(dbFile, idFoto));

				// Borramos la foto original
				if (mImageCaptureUri != null) {
					File fileOriginal = new File(mImageCaptureUri.getPath());
					if (fileOriginal.exists()) {
						fileOriginal.delete();
					}
				}

				editor = prefs.edit();
				editor.putString("urlImagen", "");
				editor.commit();

				prendaBasica = 0;
				break;
			case ADD_UTILIDAD:
				ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

				db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
				if (db != null) {
					listUtilidades = gestion.getUtilidades(db);
				}
				db.close();

				ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(
						getActivity(), listUtilidades);
				listUtilidadesView.setAdapter(adapterUtilidad);
				break;
			case CHANGE_TEMP:
				int temp = data.getExtras().getInt("Temporada");
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
					textTemporada.setText(getResources().getString(
							R.string.todoAno));
					idRadioTemporada = 2;
					break;
				default:
					break;
				}
				break;
			case PRENDA_BASICA:
				int prendaBasicaSeleccionada = data.getExtras().getInt(
						"prendaBasicaSeleccionada");

				Bitmap bitmap = Util.obtenerImagenPrendaBasica(getActivity(),
						idTipo, prendaBasicaSeleccionada, 2, estilo);

				Matrix matrix2 = new Matrix();
				matrix2.postRotate(-3.0f);

				Bitmap imagenGirada = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix2, true);

				imagenSeleccionada.setImageBitmap(imagenGirada);

				// bitmap.recycle();
				// imagenGirada.recycle();

				prendaBasica = 1;
				idPrendaBasica = prendaBasicaSeleccionada;
				break;
			}
		} else if (resultCode == 99) {
			Context context = getActivity().getApplicationContext();
			CharSequence text = (getActivity().getResources()
					.getString(R.string.errorFoto));
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	public void runCropImage(String path) {
		Intent intent = new Intent(getActivity(), CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, path);
		intent.putExtra(CropImage.SCALE, false);
		intent.putExtra(CropImage.ASPECT_X, 0);// change ration here via intent
		intent.putExtra(CropImage.ASPECT_Y, 0);
		intent.putExtra(CropImage.SCALE_UP_IF_NEEDED, true);

		try {
			/*
			 * Depending on the camera app it might be possible to get the
			 * orientation of the image from its EXIF data. Pass the level of
			 * rotation to the crop activity so it can be shown the right way
			 * up.
			 */
			ExifInterface exif = new ExifInterface(path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				intent.putExtra(CropImage.ROTATION_IN_DEGREES, 0f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				intent.putExtra(CropImage.ROTATION_IN_DEGREES, 90f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				intent.putExtra(CropImage.ROTATION_IN_DEGREES, 180f);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				intent.putExtra(CropImage.ROTATION_IN_DEGREES, -90f);
				break;
			}

			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);// final
																	// static
			// int 1
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.nueva_prenda, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		final String[] items = new String[] {
				getResources().getString(R.string.hacerFoto),
				getResources().getString(R.string.seleccionaGaleria),
				getResources().getString(R.string.prendaBasica) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		idRadioTemporada = 2;

		listIdsUtilidad.add(-1);

		builder.setTitle("Seleccionar imagen");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					File dbFileTmp = new File(Environment
							.getExternalStorageDirectory(), "/Closfy/Tmp");

					crearDirectorio(dbFileTmp);

					mImageCaptureUri = Uri.fromFile(new File(
							dbFileTmp,
							"prenda_"
									+ String.valueOf(System.currentTimeMillis())
									+ ".jpg"));

					urlAux = mImageCaptureUri.getPath();

					editor = prefs.edit();
					editor.putString("urlImagen", urlAux);
					editor.commit();

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else if (item == 2) {
					if (idTipo == 0) {
						onCreateDialog(MENSAJE_ERROR_TIPO_BASICA);
					} else {
						Intent intent = new Intent(getActivity(),
								PrendaBasicaActivity.class);
						intent.putExtra("tipoPrenda", idTipo);
						startActivityForResult(intent, PRENDA_BASICA);
					}
				} else { // pick from file
					File dbFileTmp = new File(Environment
							.getExternalStorageDirectory(), "/Closfy/Tmp");

					crearDirectorio(dbFileTmp);

					mImageCaptureUri = Uri.fromFile(new File(
							dbFileTmp,
							"prenda_"
									+ String.valueOf(System.currentTimeMillis())
									+ ".jpg"));

					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, PICK_FROM_FILE);
				}
			}
		});

		final AlertDialog dialog = builder.create();
		layoutImagen = (LinearLayout) this.getView().findViewById(
				R.id.layoutImagen);

		Matrix matrix = new Matrix();
		matrix.postRotate(-3.0f);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;

		Bitmap marco = BitmapFactory.decodeResource(getResources(),
				R.drawable.marco, options);

		Bitmap rotateFotoMarco = Bitmap.createBitmap(marco, 0, 0,
				marco.getWidth(), marco.getHeight(), matrix, true);

		Drawable marcoDrawable = new BitmapDrawable(getResources(),
				rotateFotoMarco);

		layoutImagen.setBackgroundDrawable(marcoDrawable);

		imagenSeleccionada = (ImageView) this.getView().findViewById(
				R.id.marcoIcon);

		botonCambiarTemp = (LinearLayout) this.getView().findViewById(
				R.id.botonCambiarTemp);

		checkFavoritos = (ImageView) this.getView().findViewById(
				R.id.checkFavoritos);

		botonGuardar = (LinearLayout) this.getView().findViewById(
				R.id.botonGuardar);

		spinnerTipo = (Spinner) this.getView().findViewById(
				R.id.spinnerTipoPrenda);

		listUtilidadesView = (ListView) this.getView().findViewById(
				R.id.listUtilidades);

		textTemporada = (TextView) this.getView().findViewById(
				R.id.textTemporada);

		textoCambiar = (TextView) this.getView()
				.findViewById(R.id.textoCambiar);

		ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listUtilidades = gestion.getUtilidades(db);
		}
		db.close();

		adapterUtilidad = new ListAdapterUtilidad(getActivity(), listUtilidades);
		listUtilidadesView.setAdapter(adapterUtilidad);

		obtenerTiposPrenda();

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

		layoutImagen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});

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

		botonCambiarTemp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(getActivity(),
						SelectTemporadaActivity.class);
				in.putExtra("Temporada", idRadioTemporada);
				startActivityForResult(in, CHANGE_TEMP);
			}
		});

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

		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
				boolean ok = false;
				boolean errorFoto = false;
				boolean errorTipo = false;

				// Comprobamos que todos los datos estn informados
				if (idRadioTemporada == -1 || idTipo == 0
						|| (idFoto.equals("") && prendaBasica == 0)) {
					if (idFoto.equals("")) {
						errorFoto = true;
					} else {
						errorTipo = true;
					}
				}

				// obtenemos la cadena de utilidades
				utilidades = Util.obtenerCadenaUtilidades(listIdsUtilidad);

				// Si no hay error insertamos la prenda
				if (!errorFoto && !errorTipo) {
					if (db != null) {
						ok = gestion.insertarPrenda(db, idTipo, prendaBasica,
								idPrendaBasica, idRadioTemporada, favorito,
								idFoto, utilidades, cuentaSeleccionada);
					}
					db.close();

					if (ok) {
						inicializar();
						mostrarMensaje(getResources().getString(
								R.string.prendaOK));
					} else {
						mostrarMensaje(getResources().getString(
								R.string.prendaKO));
					}
				} else {
					if (errorFoto) {
						onCreateDialog(MENSAJE_ERROR_FOTO);
					} else {
						onCreateDialog(MENSAJE_ERROR_TIPO);
					}
				}
			}
		});

	}

	public void obtenerTiposPrenda() {
		ArrayAdapter<CharSequence> adapterList;
		// rellenamos el spinner tipo prenda
		if (estilo == 1) {
			adapterList = ArrayAdapter.createFromResource(getActivity(),
					R.array.tiposPrendaHombre,
					android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			spinnerTipo.setAdapter(adapterList);
		} else {
			adapterList = ArrayAdapter.createFromResource(getActivity(),
					R.array.tiposPrenda, android.R.layout.simple_spinner_item);
			adapterList.setDropDownViewResource(R.layout.spinner);
			spinnerTipo.setAdapter(adapterList);
		}
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
		Context context = getActivity().getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		case MENSAJE_ERROR_TIPO_BASICA:
			builder.setMessage(
					getResources().getString(R.string.tipoObligatorioBasica))
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

	public void inicializar() {
		tmpImgUri = null;

		imagenSeleccionada.setImageResource(android.R.color.transparent);
		spinnerTipo.setSelection(0);
		idFoto = "";
		listIdsUtilidad.clear();
		listIdsUtilidad.add(-1);

		adapterUtilidad.notifyDataSetChanged();

		textTemporada.setText(getResources().getString(R.string.todoAno));
		idRadioTemporada = 2;

		favorito = 0;
		prendaBasica = 0;

		if (estilo == 0) {
			checkFavoritos.setBackgroundResource(R.drawable.check_corazon_off);
		} else {
			checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// Eliminamos la foto temporal
		if (tmpImgUri != null) {
			File fileTemporal = new File(tmpImgUri.getPath());
			if (fileTemporal.exists()) {
				fileTemporal.delete();
			}
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

		// Al pulsar un da del calendario
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

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	public void cambiarEstiloHombre() {

		spinnerTipo.setBackgroundResource(R.drawable.spinner_azul);
		checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
		textoCambiar.setBackgroundResource(R.color.azul);

	}
}
