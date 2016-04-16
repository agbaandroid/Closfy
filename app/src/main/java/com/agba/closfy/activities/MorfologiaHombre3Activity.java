package com.agba.closfy.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.agba.closfy.R;

public class MorfologiaHombre3Activity extends AppCompatActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.morfologia_hombre3);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		setSupportActionBar(toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.morfologia));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	// Aadiendo funcionalidad a las opciones de men
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
