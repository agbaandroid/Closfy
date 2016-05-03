package com.agba.closfy.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AddUtilidadActivity extends AppCompatActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();
	private EditText editUti;
	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;
	boolean isSinPublicidad;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nuevo_add_utilidad);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isSinPublicidad = extras.getBoolean("isSinPublicidad", false);
		}

		RelativeLayout layoutPubli = (RelativeLayout) findViewById(R.id.layoutPubli);
		if (isSinPublicidad) {
			layoutPubli.setVisibility(View.GONE);
		} else {
			AdView adView = (AdView) findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setContentInsetsAbsolute(0, 0);
		setSupportActionBar(toolbar);

		editUti = (EditText) findViewById(R.id.cajaNombreUtilidad);

		// Inflate the custom view and add click handlers for the buttons
		View actionBarButtons = getLayoutInflater().inflate(R.layout.accept_cancel_actionbar,
				new LinearLayout(this), false);

		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!"".equals(editUti.getText().toString())) {
					String text = "";

					for (int i = 0; i < editUti.getText().length(); i++) {
						if (i == 0) {
							text = text
									+ editUti.getText().toString().toUpperCase()
									.charAt(i);
						} else {
							text = text
									+ editUti.getText().toString().toLowerCase()
									.charAt(i);
						}
					}
					boolean ok = false;
					db = openOrCreateDatabase(BD_NOMBRE, 1, null);
					if (db != null) {
						ok = gestion.addUtilidad(db, text.trim());
					}
					db.close();
					if (ok) {
						Context context = getApplicationContext();
						CharSequence textMsg = getResources().getString(
								R.string.addUtilidadOK);
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, textMsg, duration);
						toast.show();
						setResult(RESULT_OK, getIntent());
						finish();
					} else {
						Context context = getApplicationContext();
						CharSequence textMsg = getResources().getString(
								R.string.addUtilidadKO);
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, textMsg, duration);
						toast.show();

						finish();
					}
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

		// Set the custom view and allow the bar to show it
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
		getSupportActionBar().setCustomView(actionBarButtons, layoutParams);
	}
}
