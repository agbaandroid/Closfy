package com.agba.closfy.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.ResumenLookFragment;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.ViewsVo;
import com.agba.closfy.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ResumenLookMainActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;
	int cuentaSeleccionada;
	View cancelActionView;
	View doneActionView;
	View actionBarButtons;

	public RelativeLayout m_RelativeLayout;
	public ArrayList<ViewsVo> m_arrSignObjects;
	public String prendas = "";
	public String utilidades = "";
	public int temporada = 0;
	public int favorito = 0;

	public String notasString = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_resumen_look);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}
		db.close();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setContentInsetsAbsolute(0, 0);
		setSupportActionBar(toolbar);

		// Inflate the custom view and add click handlers for the buttons
		actionBarButtons = getLayoutInflater().inflate(R.layout.accept_cancel_actionbar,
				new LinearLayout(this), false);

		cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		doneActionView = actionBarButtons.findViewById(R.id.action_done);

		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		// Set the custom view and allow the bar to show it
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
		getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

		cancelActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		doneActionView.setOnClickListener(new View.OnClickListener() {
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

				// Carpeta dnde guardamos la captura
				// En este caso, la raz de la SD Card
				File dbFile = new File(Environment
						.getExternalStorageDirectory(), "/Closfy/Looks");

				crearDirectorio(dbFile);

				// El archivo que contendr la captura
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

				boolean ok = false;
				db = openOrCreateDatabase(BD_NOMBRE, 1, null);
				if (db != null) {
					ok = gestion.insertarLook(db, temporada, prendas,
							utilidades, favorito, notasString,
							cuentaSeleccionada, url, "255;255;255");
				}

				if (ok) {
					Look lastLook = gestion.getUltimoLook(db);

					for (int i = 0; i < m_arrSignObjects.size(); i++) {
						gestion.insertarLookPrendas(db, lastLook.getIdLook(),
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
					setResult(RESULT_OK,
							getIntent());
					finish();
				} else {
					mostrarMensaje(getResources().getString(R.string.lookKO));
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

		if (estilo == 1) {
			toolbar.setBackgroundResource(R.color.azul);
		}
		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(
				getResources().getString(R.string.crearLook));

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			selectItem(1);
		}
	}

	// Opciones del menu de navegacion
	private void selectItem(int position) {

		Fragment fragment = new ResumenLookFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}

	public void mostrarMensaje(String texto) {
		Context context = getApplicationContext();
		CharSequence text = (texto);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
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
}