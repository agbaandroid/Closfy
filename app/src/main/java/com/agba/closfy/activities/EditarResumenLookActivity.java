package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.modelo.PrendaLook;
import com.agba.closfy.modelo.ViewsVo;
import com.agba.closfy.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditarResumenLookActivity extends ActionBarActivity {
	String[] idPrendas;

	private TextView botonGuardar, botonCancelar;
	private Button m_btnSDeleteImage, m_btnZoomLado, m_btnZoomAlto, m_btnZoomZoom;
	private Context m_context;

	private LinearLayout m_llTopLayout;
	private ImageView m_ivImage, m_ivtmpImage;
	private Display m_screen;
	private int m_DisplayWidth, m_ImageCount, m_viewsAddedHeightEmotions = 0,
			m_height, m_absHeight = 0, m_AddedViewsHeightText = 0,
			m_deleteEditHeightwidth;
	private Dialog m_dialog;
	private OnTouchListener m_touchImagListener, m_strecthArrowListener, anchoListener, altoListener, zoomListener;
	private RelativeLayout m_RelativeLayout, m_RelativeLayoutAux,
			m_absTextlayout, m_absZoomlayout;
	private int m_widthDelete = 0, m_totalTextViewCount = 0;
	private float m_oldDist = 1f, m_scale, m_oldX = 0, m_oldY = 0, m_dX, m_dY,
			m_posX, m_posY, m_prevX = 0, m_prevY = 0, m_newX, m_newY;
	ViewTreeObserver m_vtoTree;
	private RelativeLayout.LayoutParams m_layoutparams, m_layoutparamsDelete,
			m_layoutParamsEdit, m_layoutParamsLado, m_layoutParamsAlto, m_layoutParamsZoom;
	private ArrayList<ViewsVo> m_arrSignObjects;
	ArrayList<PrendaLook> listaPrendas = new ArrayList<PrendaLook>();

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	private static final int NOTAS = 1;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;

	int cuentaSeleccionada;

	int idLook;
	Look look = new Look();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar_resumen_look);
		m_context = EditarResumenLookActivity.this;

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}
		db.close();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (estilo == 1) {
			toolbar.setBackgroundResource(R.color.azul);
		}
		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(
				getResources().getString(R.string.editar));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		idLook = extras.getInt("idLook");

		look = new Look();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			look = gestion.getLookById(db, idLook);
			listaPrendas = gestion.getPrendasLook(db, idLook);
		}
		db.close();

		idPrendas = look.getCadenaPrendas().split(";");

		botonGuardar = (TextView) findViewById(R.id.botonGuardar);
		botonCancelar = (TextView) findViewById(R.id.botonCancelar);
		m_ivImage = (ImageView) findViewById(R.id.ivCardView);
		m_RelativeLayout = (RelativeLayout) findViewById(R.id.relative1);
		m_llTopLayout = (LinearLayout) findViewById(R.id.llBottomLayout);
		m_arrSignObjects = new ArrayList<ViewsVo>();

		// Set the layout parameters to the Absolute layout for adding images.
		RelativeLayout.LayoutParams rl_pr = new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rl_pr.addRule(RelativeLayout.ABOVE, R.id.llBottomLayout);
		rl_pr.addRule(RelativeLayout.BELOW, R.id.toolbar);

		m_RelativeLayout.setLayoutParams(rl_pr);

		m_screen = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		m_DisplayWidth = m_screen.getWidth();
		m_AddedViewsHeightText = m_llTopLayout.getHeight();

		// Get the absoulte layout height according to the device screen density
		// to set the layout.
		m_vtoTree = m_RelativeLayout.getViewTreeObserver();
		m_vtoTree.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {

				m_absHeight = m_RelativeLayout.getHeight();
				m_RelativeLayout.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);

				int posiX = 0;
				int posiY = 0;
				db = openOrCreateDatabase(BD_NOMBRE, 1, null);

				// Aqui anadimos las prendas con su colocacion.

				if (listaPrendas.size() > 0) {
					for (int i = 0; i < listaPrendas.size(); i++) {
						PrendaLook prenda = listaPrendas.get(i);
						Bitmap m_bitmap = Util.obtenerPrendaLookBitmap(
								EditarResumenLookActivity.this, prenda, 0,
								estilo);
						if (m_bitmap != null) {
							getImageLayout(m_bitmap, null, prenda,
									(int) prenda.getPosiX(),
									(int) prenda.getPosiY());

							m_arrSignObjects.get(i)
									.setxValue(prenda.getPosiX());
							m_arrSignObjects.get(i)
									.setyValue(prenda.getPosiY());
						}
					}
				} else if (idPrendas.length > 0) {
					int posiArray = 0;
					for (int i = 0; i < idPrendas.length; i++) {
						if (!idPrendas[i].equals(String.valueOf(""))
								&& !idPrendas[i].equals(String.valueOf("-1"))) {
							Prenda prenda = gestion.getPrendaById(db,
									Integer.parseInt(idPrendas[i]));

							Bitmap m_bitmap = Util.obtenerPrendaBitmap(
									EditarResumenLookActivity.this, prenda, 0,
									estilo);
							if (m_bitmap != null) {
								getImageLayout(m_bitmap, prenda, null, posiX,
										posiY);

								m_arrSignObjects.get(posiArray)
										.setxValue(posiX);
								m_arrSignObjects.get(posiArray)
										.setyValue(posiY);

								posiArray++;

								if ((posiX + 610) < m_RelativeLayout.getWidth()) {
									posiX = posiX + 310;
								} else {
									if ((posiY + 610) < m_RelativeLayout
											.getHeight()) {
										posiX = 0;
										posiY = posiY + 310;
									} else {
										posiX = 0;
										posiY = 0;
									}
								}
							}
						}

					}
				}

				for (int i = 1; i < m_RelativeLayout.getChildCount(); i++) {
					RelativeLayout rel = (RelativeLayout) m_RelativeLayout
							.getChildAt(i);
					for (int j = 0; j < rel.getChildCount(); j++) {
						rel.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
					}
				}

				db.close();
			}
		});

		m_dialog = new Dialog(this, R.style.Dialog);
		m_dialog.setCancelable(true);

		botonGuardar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 1; i < m_RelativeLayout.getChildCount(); i++) {
					RelativeLayout rel = (RelativeLayout) m_RelativeLayout
							.getChildAt(i);
					for (int j = 0; j < rel.getChildCount(); j++) {
						rel.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
					}
				}

				m_RelativeLayout.setDrawingCacheEnabled(true);
				Bitmap b = m_RelativeLayout.getDrawingCache();

				// Carpeta donde guardamos la captura
				// En este caso, la ra�z de la SD Card
				File dbFile = new File(Environment
						.getExternalStorageDirectory(), "/Closfy/Looks");

				crearDirectorio(dbFile);

				// El archivo que contendra la captura
				String url = "look_"
						+ String.valueOf(System.currentTimeMillis()) + ".jpg";
				File f = new File(dbFile, url);

				try {
					if (dbFile.canWrite()) {
						f.createNewFile();
						OutputStream os = new FileOutputStream(f);
						b.compress(Bitmap.CompressFormat.JPEG, 90, os);
						os.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				m_RelativeLayout.setDrawingCacheEnabled(false);

				if (look.getIdFoto() != null) {
					// Borramos la foto del direcctorio
					File dbFileOld = new File(Environment
							.getExternalStorageDirectory(), "/Closfy/Looks");

					Uri tmpImgUri = Uri.fromFile(new File(dbFile, look
							.getIdFoto()));
					File fileFoto = new File(tmpImgUri.getPath());
					if (fileFoto.exists()) {
						fileFoto.delete();
					}
				}

				boolean ok = false;
				db = openOrCreateDatabase(BD_NOMBRE, 1, null);
				if (db != null) {
					ok = gestion.editarLook(db,
							String.valueOf(look.getIdLook()), url);
				}

				if (ok) {

					boolean ok2 = gestion.eliminarPrendaLook(db,
							look.getIdLook());

					for (int i = 0; i < m_arrSignObjects.size(); i++) {
						gestion.insertarLookPrendas(db, look.getIdLook(),
								m_arrSignObjects.get(i).getIdPrenda(),
								m_arrSignObjects.get(i).getxValue(),
								m_arrSignObjects.get(i).getyValue(),
								m_arrSignObjects.get(i).getAncho(),
								m_arrSignObjects.get(i).getAlto(),
								m_arrSignObjects.get(i).getPos());
					}
				}
				db.close();

				if (ok) {
					mostrarMensaje(getResources().getString(R.string.lookOK));
					setResult(RESULT_OK, getIntent());
					finish();
				} else {
					mostrarMensaje(getResources().getString(R.string.lookKO));
				}
			}
		});

		botonCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK, getIntent());
				finish();
			}
		});
	}

	/**
	 * Method to set the view's height dynamically according to screen size.
	 */
	private void setViewsHeightDynamically(PrendaLook prenda) {
		// if (m_absHeight <= 500) {
		// m_layoutparamsDelete = new RelativeLayout.LayoutParams(20, 20);
		// m_layoutparamsDelete.leftMargin = 110;
		// m_layoutparamsDelete.topMargin = 0;
		//
		// m_layoutParamsEdit = new RelativeLayout.LayoutParams(20, 20);
		// m_layoutParamsEdit.leftMargin = 110;
		// m_layoutParamsEdit.topMargin = 110;
		//
		// m_deleteEditHeightwidth = 20;
		// } else if (m_absHeight >= 900) {
		//
		// } else {
		// m_layoutparamsDelete = new RelativeLayout.LayoutParams(35, 35);
		// m_layoutparamsDelete.leftMargin = 140;
		// m_layoutparamsDelete.topMargin = 0;
		//
		// m_layoutParamsEdit = new RelativeLayout.LayoutParams(35, 35);
		// m_layoutParamsEdit.leftMargin = 120;
		// m_layoutParamsEdit.topMargin = 120;
		// m_deleteEditHeightwidth = 35;
		// }
		m_layoutparamsDelete = new RelativeLayout.LayoutParams(50, 50);
		m_layoutparamsDelete.leftMargin = 5;
		m_layoutparamsDelete.topMargin = 5;

		m_layoutParamsEdit = new RelativeLayout.LayoutParams(50, 50);
		m_layoutParamsLado = new RelativeLayout.LayoutParams(50, 50);
		m_layoutParamsAlto = new RelativeLayout.LayoutParams(50, 50);
		m_layoutParamsZoom = new RelativeLayout.LayoutParams(50, 50);
		if (prenda != null) {
			m_layoutParamsEdit.leftMargin = (int) prenda.getAncho() - 45;
			m_layoutParamsEdit.topMargin = (int) prenda.getAlto() - 45;

			m_layoutParamsLado.leftMargin = (int) prenda.getAncho() - 45;
			m_layoutParamsLado.topMargin = (int) prenda.getAlto() / 2;

			m_layoutParamsAlto.leftMargin = (int) prenda.getAncho() / 2;
			m_layoutParamsAlto.topMargin = (int) prenda.getAlto() - 45;

			m_layoutParamsZoom.leftMargin = (int) prenda.getAncho() - 45;
			m_layoutParamsZoom.topMargin = (int) prenda.getAlto() - 45;
		} else {
			m_layoutParamsEdit.leftMargin = 255;
			m_layoutParamsEdit.topMargin = 255;

			m_layoutParamsLado = new RelativeLayout.LayoutParams(50, 50);
			m_layoutParamsLado.leftMargin = 255;
			m_layoutParamsLado.topMargin = 127;

			m_layoutParamsAlto = new RelativeLayout.LayoutParams(50, 50);
			m_layoutParamsAlto.leftMargin = 127;
			m_layoutParamsAlto.topMargin = 255;

			m_layoutParamsZoom = new RelativeLayout.LayoutParams(50, 50);
			m_layoutParamsZoom.leftMargin = 255;
			m_layoutParamsZoom.topMargin = 255;
		}

		m_deleteEditHeightwidth = 50;
	}

	/**
	 * Method to add the image by setting and creating the views dynamically
	 * with delete and zoom option.
	 */
	@SuppressWarnings("deprecation")
	private void getImageLayout(Bitmap p_bitmap, Prenda prenda,
			PrendaLook prendaLook, int posiX, int posiY) {
		ViewsVo m_signVo;
		// Check for images count .Set the count for limiting the number of
		// images to add on screen.
		if (m_ImageCount < 1) {
			m_viewsAddedHeightEmotions = m_viewsAddedHeightEmotions + 90;
			m_ImageCount++;
		} /*
		 * else { Toast.makeText(m_context, "No enough space for images.",
		 * Toast.LENGTH_LONG).show(); }
		 */

		m_btnSDeleteImage = new Button(m_context);
		m_btnZoomLado = new Button(m_context);
		m_btnZoomAlto = new Button(m_context);
		m_btnZoomZoom = new Button(m_context);
		m_ivtmpImage = new ImageView(m_context);

		m_absTextlayout = new RelativeLayout(m_context);
		m_absZoomlayout = new RelativeLayout(m_context);

		if (prendaLook != null) {
			m_ivtmpImage.setImageBitmap(Bitmap.createScaledBitmap(p_bitmap,
					(int) prendaLook.getAncho(), (int) prendaLook.getAlto(),
					true));
		} else {
			m_ivtmpImage.setImageBitmap(Bitmap.createScaledBitmap(p_bitmap,
					300, 300, true));
		}

		RelativeLayout.LayoutParams laParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		if (prendaLook != null) {
			laParam.leftMargin = (int) prendaLook.getPosiX();
			laParam.topMargin = (int) prendaLook.getPosiY();
		} else {
			laParam.leftMargin = posiX;
			laParam.topMargin = posiY;
		}

		m_absTextlayout.setLayoutParams(laParam);

		RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		laParam2.leftMargin = 0;
		laParam2.topMargin = 0;

		m_absZoomlayout.setLayoutParams(laParam2);

		FrameLayout.LayoutParams frameParams;
		// if (m_absHeight >= 900)
		if (prendaLook != null) {
			frameParams = new FrameLayout.LayoutParams(
					(int) prendaLook.getAncho(), (int) prendaLook.getAlto());
		} else {
			frameParams = new FrameLayout.LayoutParams(300, 300);
		}
		frameParams.leftMargin = 0;
		frameParams.topMargin = 0;
		m_ivtmpImage.setLayoutParams(frameParams);
		// else
		// m_ivtmpImage
		// .setLayoutParams(new FrameLayout.LayoutParams(120, 120));

		m_ivtmpImage.setBackgroundColor(Color.TRANSPARENT);

		m_absZoomlayout.setPadding(5, 5, 5, 5);

		setViewsHeightDynamically(prendaLook);

		// System.err.println("Height of Layout------" + m_absHeight);
		m_btnSDeleteImage.setLayoutParams(m_layoutparamsDelete);
		m_btnSDeleteImage.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.close));
		m_btnSDeleteImage.setId(m_arrSignObjects.size());
		m_btnSDeleteImage.setOnClickListener(new ImageDeleteListener());

		m_btnZoomLado.setLayoutParams(m_layoutParamsLado);
		m_btnZoomLado.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.resize_width));
		m_btnZoomLado.setId(m_arrSignObjects.size());

		m_btnZoomAlto.setLayoutParams(m_layoutParamsAlto);
		m_btnZoomAlto.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.resize_alto));
		m_btnZoomAlto.setId(m_arrSignObjects.size());

		m_btnZoomZoom.setLayoutParams(m_layoutParamsZoom);
		m_btnZoomZoom.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.resize));
		m_btnZoomZoom.setId(m_arrSignObjects.size());

		m_absZoomlayout.setBackgroundResource(R.drawable.recuadro);
		m_absZoomlayout.addView(m_ivtmpImage);

		m_absTextlayout.addView(m_absZoomlayout);
		m_absTextlayout.addView(m_btnSDeleteImage);
		m_absTextlayout.addView(m_btnZoomLado);
		m_absTextlayout.addView(m_btnZoomAlto);
		m_absTextlayout.addView(m_btnZoomZoom);

		m_absTextlayout.setDrawingCacheEnabled(true);
		m_absTextlayout.setClickable(true);
		m_absTextlayout.setId(m_arrSignObjects.size());
		m_ivtmpImage.setId(m_arrSignObjects.size());

		m_vtoTree = m_absTextlayout.getViewTreeObserver();
		m_vtoTree.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				m_absTextlayout.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
			}
		});

		/**
		 * Add all the views into arraylist which are added into the screen for
		 * further to perform deletion of each views.
		 */
		m_signVo = new ViewsVo();
		if (prendaLook != null) {
			m_signVo.setIdPrenda(prendaLook.getIdPrenda());
			m_signVo.setxValue(prendaLook.getPosiX());
			m_signVo.setyValue(prendaLook.getPosiY());
			m_signVo.setAncho(prendaLook.getAncho());
			m_signVo.setAlto(prendaLook.getAlto());
		} else {
			m_signVo.setIdPrenda(prenda.getIdPrenda());
			m_signVo.setxValue(posiX);
			m_signVo.setyValue(posiY);
			m_signVo.setAncho(300);
			m_signVo.setAlto(300);
		}

		m_signVo.setPos(m_arrSignObjects.size());
		m_signVo.setViewId(m_arrSignObjects.size());
		m_arrSignObjects.add(m_signVo);
		m_RelativeLayout.addView(m_absTextlayout);

		// Image touch listener to move image onTouch event on screen.
		m_touchImagListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				m_RelativeLayoutAux = (RelativeLayout) v;

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

				case MotionEvent.ACTION_DOWN:
					m_oldX = event.getX();
					m_oldY = event.getY();
					v.bringToFront();
					int posicion = 0;
					int posiOld = 0;
					for (int counter = 0; counter < m_arrSignObjects.size(); counter++) {
						if (v.getId() == m_arrSignObjects.get(counter)
								.getViewId()) {
							posiOld = m_arrSignObjects.get(counter).getPos();
							m_arrSignObjects.get(counter).setPos(0);
							posicion = counter;

							break;
						}
					}

					// colocamos las posiciones
					for (int counter = 0; counter < m_arrSignObjects.size(); counter++) {
						if ((counter != posicion)
								&& (m_arrSignObjects.get(counter).getPos() <= posiOld)) {
							m_arrSignObjects.get(counter).setPos(
									m_arrSignObjects.get(counter).getPos() + 1);
						}
					}

					break;
				case MotionEvent.ACTION_CANCEL:
					break;
				case MotionEvent.ACTION_UP:
					ocultarBotones();
					break;
				case MotionEvent.ACTION_POINTER_UP:
					break;

				case MotionEvent.ACTION_MOVE:

					((RelativeLayout) v).getChildAt(1).setBackgroundResource(
							R.drawable.close);
					((RelativeLayout) v).getChildAt(0).setBackgroundResource(
							R.drawable.recuadro);
					((RelativeLayout) v).getChildAt(2).setBackgroundResource(
							R.drawable.resize);

					m_dX = event.getX() - m_oldX;
					m_dY = event.getY() - m_oldY;

					RelativeLayout.LayoutParams laParamAux = (RelativeLayout.LayoutParams) v
							.getLayoutParams();

					m_posX = laParamAux.leftMargin + m_dX;
					m_posY = laParamAux.topMargin + m_dY;

					if (m_posX > 0
							&& m_posY > 0
							&& (m_posX + v.getWidth()) < m_RelativeLayout
									.getWidth()
							&& (m_posY + v.getHeight()) < m_RelativeLayout
									.getHeight()) {

						RelativeLayout.LayoutParams laParam = new RelativeLayout.LayoutParams(
								v.getMeasuredWidth(), v.getMeasuredHeight());
						laParam.leftMargin = (int) m_posX;
						laParam.topMargin = (int) m_posY;

						v.setLayoutParams(laParam);

						for (int counter = 0; counter < m_arrSignObjects.size(); counter++) {
							if (v.getId() == m_arrSignObjects.get(counter)
									.getViewId()) {
								m_arrSignObjects.get(counter).setxValue(m_posX);
								m_arrSignObjects.get(counter).setyValue(m_posY);
							}
						}

						m_prevX = m_posX;
						m_prevY = m_posY;

					} else {
						if (m_posX < 0) {
							m_posX = 0;
						}
						if (m_posY < 0) {
							m_posY = 0;
						}
						if ((m_posX + v.getWidth()) > m_RelativeLayout
								.getWidth()) {
							m_posX = m_RelativeLayout.getWidth() - v.getWidth();
						}
						if ((m_posY + v.getHeight()) > m_RelativeLayout
								.getHeight()) {
							m_posY = m_RelativeLayout.getHeight()
									- v.getHeight();
						}

						RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
								v.getMeasuredWidth(), v.getMeasuredHeight());
						laParam2.leftMargin = (int) m_posX;
						laParam2.topMargin = (int) m_posY;

						v.setLayoutParams(laParam2);

						for (int counter = 0; counter < m_arrSignObjects.size(); counter++) {
							if (v.getId() == m_arrSignObjects.get(counter)
									.getViewId()) {
								m_arrSignObjects.get(counter).setxValue(m_posX);
								m_arrSignObjects.get(counter).setyValue(m_posY);
							}
						}

						m_prevX = m_posX;
						m_prevY = m_posY;
					}
					break;
				}
				return false;
			}
		};

		// Listener for the arrow ontouch of arrow ZoomIn and ZoomOut the image.
		anchoListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				View view;
				ViewsVo viewVo = new ViewsVo();
				// RemoveBorders();
				view = v;
				v.setClickable(true);
				v.setDrawingCacheEnabled(true);
				//v.bringToFront();
				RelativeLayout m_absLayout = null;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						m_oldX = event.getX();
						m_oldY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						ocultarBotones();
						break;
					case MotionEvent.ACTION_POINTER_UP:
						break;

					case MotionEvent.ACTION_MOVE:

						m_absLayout = (RelativeLayout) v.getParent();

						((RelativeLayout) m_absLayout).getChildAt(1).setBackgroundResource(
								R.drawable.close);
						((RelativeLayout) m_absLayout).getChildAt(0).setBackgroundResource(
								R.drawable.recuadro);
						((RelativeLayout) m_absLayout).getChildAt(2).setBackgroundResource(
								R.drawable.resize_width);
						((RelativeLayout) m_absLayout).getChildAt(3).setBackgroundResource(
								R.drawable.resize_alto);
						((RelativeLayout) m_absLayout).getChildAt(4).setBackgroundResource(
								R.drawable.resize);

						m_newX = event.getX();
						m_scale = 10;

						if (m_newX > m_oldX) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = viewVo.getActualBitmap().getHeight();
							int m_widthOfImage = (int) (viewVo.getActualBitmap().getWidth() + m_scale);

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if ((m_absLayout.getRight() + 20) <= (m_DisplayWidth)) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								(((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						} else if (m_newX < m_oldX) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = viewVo.getActualBitmap().getHeight();
							int m_widthOfImage = (int) (viewVo.getActualBitmap().getWidth() - m_scale);

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if (m_widthOfImage > 200) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								(((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						}
						break;
				}
				return false;
			}
		};

		// Listener for the arrow ontouch of arrow ZoomIn and ZoomOut the image.
		altoListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				View view;
				ViewsVo viewVo = new ViewsVo();
				// RemoveBorders();
				view = v;
				v.setClickable(true);
				v.setDrawingCacheEnabled(true);
				//v.bringToFront();
				RelativeLayout m_absLayout = null;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						m_oldX = event.getX();
						m_oldY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						ocultarBotones();
						break;
					case MotionEvent.ACTION_POINTER_UP:
						break;

					case MotionEvent.ACTION_MOVE:
						m_absLayout = (RelativeLayout) v.getParent();

						((RelativeLayout) m_absLayout).getChildAt(1).setBackgroundResource(
								R.drawable.close);
						((RelativeLayout) m_absLayout).getChildAt(0).setBackgroundResource(
								R.drawable.recuadro);
						((RelativeLayout) m_absLayout).getChildAt(2).setBackgroundResource(
								R.drawable.resize_width);
						((RelativeLayout) m_absLayout).getChildAt(3).setBackgroundResource(
								R.drawable.resize_alto);
						((RelativeLayout) m_absLayout).getChildAt(4).setBackgroundResource(
								R.drawable.resize);

						m_newY = event.getY();
						m_scale = 10;

						if (m_newY > m_oldY) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = (int) (viewVo.getActualBitmap().getHeight() + m_scale);
							int m_widthOfImage = viewVo.getActualBitmap().getWidth();

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if ((m_absLayout.getBottom() + 10) <= m_ivImage
									.getBottom()) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						} else if (m_newY < m_oldY) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = (int) (viewVo.getActualBitmap().getHeight() - m_scale);
							int m_widthOfImage = viewVo.getActualBitmap().getWidth();

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if (m_heightOfImage > 200) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						}
						break;
				}
				return false;
			}
		};

		// Listener for the arrow ontouch of arrow ZoomIn and ZoomOut the image.
		zoomListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				View view;
				ViewsVo viewVo = new ViewsVo();
				// RemoveBorders();
				view = v;
				v.setClickable(true);
				v.setDrawingCacheEnabled(true);
				//v.bringToFront();
				RelativeLayout m_absLayout = null;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						m_oldX = event.getX();
						m_oldY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						ocultarBotones();
						break;
					case MotionEvent.ACTION_POINTER_UP:
						break;

					case MotionEvent.ACTION_MOVE:
						m_absLayout = (RelativeLayout) v.getParent();

						((RelativeLayout) m_absLayout).getChildAt(1).setBackgroundResource(
								R.drawable.close);
						((RelativeLayout) m_absLayout).getChildAt(0).setBackgroundResource(
								R.drawable.recuadro);
						((RelativeLayout) m_absLayout).getChildAt(2).setBackgroundResource(
								R.drawable.resize_width);
						((RelativeLayout) m_absLayout).getChildAt(3).setBackgroundResource(
								R.drawable.resize_alto);
						((RelativeLayout) m_absLayout).getChildAt(4).setBackgroundResource(
								R.drawable.resize);

						m_newY = event.getY();
						m_newX = event.getX();
						m_scale = 10;

						if ((m_newY > m_oldY) && m_newX > m_oldX) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = (int) (viewVo.getActualBitmap().getHeight() + m_scale);
							int m_widthOfImage = (int) (viewVo.getActualBitmap().getWidth() + m_scale);

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if ((m_absLayout.getBottom() + 10)<= (m_ivImage
									.getBottom())
									&& (m_absLayout.getRight() + 20) <= (m_DisplayWidth)) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						} else if ((m_newY < m_oldY) && (m_newX < m_oldX)) {
							for (int counter = 0; counter < m_arrSignObjects
									.size(); counter++) {
								if (v.getId() == m_arrSignObjects.get(
										counter).getViewId()) {
									viewVo = m_arrSignObjects.get(
											counter);
									break;
								}
							}

							int m_heightOfImage = (int) (viewVo.getActualBitmap().getHeight() - m_scale);
							int m_widthOfImage = (int) (viewVo.getActualBitmap().getWidth() - m_scale);

							Bitmap orig = viewVo.getOriginalBitmap();
							Bitmap newBitmap = Bitmap.createScaledBitmap(orig, m_widthOfImage,
									m_heightOfImage, true);

							//if (newDist > 0.0f) {
							m_absLayout = (RelativeLayout) v.getParent();

							if (m_heightOfImage > 200) {

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);
								m_layoutparams.leftMargin = 0;
								m_layoutparams.topMargin = 0;

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setImageBitmap(newBitmap);

								((ImageView) ((RelativeLayout) m_absLayout
										.getChildAt(0)).getChildAt(0)).setLayoutParams(m_layoutparams);

								m_layoutparams = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.WRAP_CONTENT,
										RelativeLayout.LayoutParams.WRAP_CONTENT);

								m_layoutparams.leftMargin = m_absLayout
										.getLeft();
								m_layoutparams.topMargin = m_absLayout.getTop();
								m_absLayout.setLayoutParams(m_layoutparams);

								RelativeLayout.LayoutParams laParam2 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam2.leftMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth) / 2;

								laParam2.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(3))
										.setLayoutParams(laParam2);

								RelativeLayout.LayoutParams laParam3 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam3.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam3.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth) / 2;

								((Button) m_absLayout.getChildAt(2))
										.setLayoutParams(laParam3);

								RelativeLayout.LayoutParams laParam4 = new RelativeLayout.LayoutParams(
										m_deleteEditHeightwidth,
										m_deleteEditHeightwidth);
								laParam4.leftMargin = (int) (((RelativeLayout) m_absLayout
										.getChildAt(0)).getWidth())
										- m_deleteEditHeightwidth;

								laParam4.topMargin = (int) ((((RelativeLayout) m_absLayout
										.getChildAt(0)).getHeight())
										- m_deleteEditHeightwidth);

								((Button) m_absLayout.getChildAt(4))
										.setLayoutParams(laParam4);

								m_layoutparams = new RelativeLayout.LayoutParams(
										m_widthOfImage, m_heightOfImage);

								m_layoutparams.leftMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getLeft();
								m_layoutparams.topMargin = ((RelativeLayout) m_absLayout
										.getChildAt(0)).getTop();

								for (int counter = 0; counter < m_arrSignObjects
										.size(); counter++) {
									if (v.getId() == m_arrSignObjects.get(
											counter).getViewId()) {
										m_arrSignObjects.get(counter).setAncho(
												m_widthOfImage);
										m_arrSignObjects.get(counter).setAlto(
												m_heightOfImage);
										m_arrSignObjects.get(counter).setActualBitmap(
												newBitmap);
									}
								}
								((RelativeLayout) m_absLayout.getChildAt(0))
										.setLayoutParams(m_layoutparams);
							}
						}
						break;
				}
				return false;
			}
		};

		m_absTextlayout.setOnTouchListener(m_touchImagListener);
		m_btnZoomLado.setOnTouchListener(anchoListener);
		m_btnZoomAlto.setOnTouchListener(altoListener);
		m_btnZoomZoom.setOnTouchListener(zoomListener);
	}

	// Delete button listener to show the alert and confirmation for deleting
	// the items.
	private class ImageDeleteListener implements OnClickListener {
		@Override
		public void onClick(final View v) {

			m_ImageCount--;
			for (int counter = 0; counter < m_arrSignObjects.size(); counter++) {
				if (v.getId() == m_arrSignObjects.get(counter).getViewId()) {
					if (m_totalTextViewCount <= 0) {
						m_AddedViewsHeightText = m_AddedViewsHeightText
								- m_arrSignObjects.get(counter).getViewHeight();
					} else {
						m_totalTextViewCount--;
					}

					m_RelativeLayout.removeView((View) v.getParent());
					m_arrSignObjects.remove(counter);

					break;
				}
			}
		}

	}

	/*
	 * public void RemoveBorders() { for (int i = 0; i <
	 * m_RelativeLayout.getChildCount(); i++) {
	 * m_RelativeLayout.getChildAt(i).setBackgroundDrawable(null);
	 * m_RelativeLayout.getChildAt(i); } }
	 */
	public boolean ocultarBotones() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {

				for (int i = 1; i < m_RelativeLayout.getChildCount(); i++) {
					RelativeLayout rel = (RelativeLayout) m_RelativeLayout
							.getChildAt(i);
					for (int j = 0; j < rel.getChildCount(); j++) {
						rel.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
					}
				}

				// m_RelativeLayoutAux.getChildAt(0).setBackgroundColor(
				// Color.TRANSPARENT);
				// m_RelativeLayoutAux.getChildAt(1).setBackgroundColor(
				// Color.TRANSPARENT);
				// m_RelativeLayoutAux.getChildAt(2).setBackgroundColor(
				// Color.TRANSPARENT);
			}
		}, 1500);
		return true;
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
		Context context = getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent());
		finish();
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
}
