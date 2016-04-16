package com.agba.closfy.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class EditUtilidadActivity extends AppCompatActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();
	private EditText editUti;
	private int id;
	private String textEdit;
	LinearLayout layoutEditUti;
	
	int estilo;
	private int cuentaSeleccionada;
	SharedPreferences prefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nuevo_add_utilidad);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setContentInsetsAbsolute(0, 0);
		setSupportActionBar(toolbar);

		AdView adView;
		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		editUti = (EditText) findViewById(R.id.cajaNombreUtilidad);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getInt("id");
			textEdit = extras.getString("textEdit");
		}

		editUti.setText(textEdit);

		// Inflate the custom view and add click handlers for the buttons
		View actionBarButtons = getLayoutInflater().inflate(R.layout.edit_delete_actionbar,
				new LinearLayout(this), false);

		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean ok = gestion.deleteUtilidad(db, String.valueOf(id));
				if (ok) {
					Context context = getApplicationContext();
					CharSequence textMsg = getResources()
							.getString(R.string.deleteUtilidadOK);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg,
							duration);
					toast.show();
					finish();
				} else {
					Context context = getApplicationContext();
					CharSequence textMsg = getResources()
							.getString(R.string.deleteUtilidadKO);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, textMsg,
							duration);
					toast.show();
					finish();
				}
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

					boolean ok = gestion.editUtilidad(db, text.trim(), String.valueOf(id));
					if (ok) {
						Context context = getApplicationContext();
						CharSequence textMsg = getResources().getString(
								R.string.editUtilidadOK);
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, textMsg, duration);
						toast.show();
					} else {
						Context context = getApplicationContext();
						CharSequence textMsg = getResources().getString(
								R.string.editUtilidadKO);
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, textMsg, duration);
						toast.show();
					}
					finish();
				}
			}
		});

		// Hide the icon, title and home/up button
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		// Set the custom view and allow the bar to show it
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
		getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

	}

	// Anadiendo funcionalidad a las opciones de menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
