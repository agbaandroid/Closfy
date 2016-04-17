package com.agba.closfy.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;

public class CargandoActivity extends Activity {
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();
	boolean tablasCreadas = false;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargando);


		// Asignamos el tipo de fuente
		Typeface miPropiaTypeFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/Pacifico.ttf");


		TextView txtCargando = (TextView) findViewById(R.id.textCargando);
		txtCargando.setTypeface(miPropiaTypeFace);

		new MyLoadingAsyncTask().execute();
	}

	public void iniciarApp() {
		// Se crea o abre la BD
		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {

			// Comprobamos si tiene los cambios de la nueva version de la BD
			boolean tablasInicialesCreadas = gestion
					.comprobarTablasVersionInicial(db);
			boolean tablaLookPrendasCreada = gestion
					.comprobarTablaLookPrendasCreada(db);
			boolean tablaLookAsesoramientosCreada = gestion
					.comprobarTablaAsesoramientosCreada(db);

			if (!tablasInicialesCreadas) {
				gestion.createTables(db);
			} else if (!tablaLookPrendasCreada) {
				gestion.actualizarBDVersion2(db);
			}

			if(!tablaLookAsesoramientosCreada){
				gestion.crearTablaAsesoramientos(db);
			}

			boolean tablaSubtiposCreada = gestion
					.comprobarTablaSubtiposCreada(db);
			if(!tablaSubtiposCreada){
				gestion.actualizarVersion20(db);
			}
		}
		db.close();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class MyLoadingAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			iniciarApp();
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			boolean hayCuenta = false;

			db = openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				// Se crea la estructura de base de datos si no existe
				hayCuenta = gestion.hayCuenta(db);
			}
			db.close();

			Intent intent;

			if (hayCuenta) {
				intent = new Intent(CargandoActivity.this, ClosfyActivity.class);
			} else {
				intent = new Intent(CargandoActivity.this,
						ConfiguracionInicialActivity.class);
			}

			startActivity(intent);
			finish();

		}
	}
}
