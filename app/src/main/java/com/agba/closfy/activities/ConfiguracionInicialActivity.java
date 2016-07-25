package com.agba.closfy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;

public class ConfiguracionInicialActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	LinearLayout layoutHombre;
	LinearLayout layoutMujer;
	int cuenta = 0;
	ProgressDialog progDailog;
	boolean isSinPublicidad;
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.configuracion_inicial);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isSinPublicidad = extras.getBoolean("isSinPublicidad", false);
		}

		layoutHombre = (LinearLayout) findViewById(R.id.layoutHombre);
		layoutMujer = (LinearLayout) findViewById(R.id.layoutMujer);

		// Asignamos el tipo de fuente
		Typeface miPropiaTypeFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/Pacifico.ttf");

		TextView txtSelec = (TextView) findViewById(R.id.textSelect);
		txtSelec.setTypeface(miPropiaTypeFace);
		TextView txtHombre = (TextView) findViewById(
				R.id.textHombre);
		txtHombre.setTypeface(miPropiaTypeFace);
		TextView txtMujer = (TextView) findViewById(
				R.id.textMujer);
		txtMujer.setTypeface(miPropiaTypeFace);

		layoutHombre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cuenta = 1;			
				new MyLoadingAsyncTask().execute();
			}
		});

		layoutMujer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cuenta = 0;
				new MyLoadingAsyncTask().execute();
			}
		});

	}

	public void crearCuentaPrincipal(int sexo) {
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			gestion.crearCuentaPrincipal(db, sexo);
		}
		db.close();		
	}
	
	public class MyLoadingAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ConfiguracionInicialActivity.this);
			progDailog.setIndeterminate(false);
			progDailog.setMessage(getResources().getString(R.string.cargando));
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(false);
			progDailog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			crearCuentaPrincipal(cuenta);
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			Intent intent;

			prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
			boolean tutorialShowed = prefs.getBoolean("tutorialShowed", false);

			if(tutorialShowed){
				intent = new Intent(ConfiguracionInicialActivity.this, ClosfyActivity.class);
			}else{
				intent = new Intent(ConfiguracionInicialActivity.this, TutorialActivity.class);
			}

			intent.putExtra("isSinPublicidad", isSinPublicidad);
			startActivity(intent);
			finish();
		}
	}

}
