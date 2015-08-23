package com.agba.closfy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.adapters.ListAdapterNavigator;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.CalendarioFragment;
import com.agba.closfy.fragments.CrearLookPrincipalFragment;
import com.agba.closfy.fragments.CuentasFragment;
import com.agba.closfy.fragments.MiArmarioFragment;
import com.agba.closfy.fragments.MisLooksFragment;
import com.agba.closfy.fragments.MorfologiaFragment;
import com.agba.closfy.fragments.MorfologiaHombreFragment;
import com.agba.closfy.fragments.NuevaPrendaFragment;
import com.agba.closfy.fragments.QueMePongoInicialFragment;
import com.agba.closfy.fragments.TestColoridoFragment;
import com.agba.closfy.fragments.UtilidadesFragment;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.util.Util;

public class ClosfyActivity extends ActionBarActivity {
	// Menu navegacion
	private String[] titlesMenu;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ListView navList;
	private DrawerLayout navDrawerLayout;
	private ActionBarDrawerToggle actionBarDrawer;
	private ListAdapterNavigator mAdapter;
	TextView textoHeader;

	private final String BD_NOMBRE = "BDClosfy";
	private SQLiteDatabase db;
	final GestionBBDD gestion = new GestionBBDD();

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;
	Toolbar toolbar;

	private long mLastPress = 0; // Cuando se pulsa atras por ultima vez
	private long mTimeLimit = 3000; // Limite de tiempo entre pulsaciones, en ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);

		View header = getLayoutInflater().inflate(R.layout.header, null);
		View footer = getLayoutInflater().inflate(R.layout.footer, null);

		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navList = (ListView) findViewById(R.id.left_drawer);

		navList.addHeaderView(header);
		navList.addFooterView(footer);

		textoHeader = (TextView) findViewById(R.id.txtHeader);

		int cuen = Util.cuentaSeleccionada(this, prefs);
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuen);
			Cuenta cuenta = gestion.getCuentaSeleccionada(db, cuen);
			textoHeader.setText(cuenta.getDescCuenta());
		}
		db.close();

		toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (estilo == 1) {
			toolbar.setBackgroundResource(R.color.azul);
		}
		setSupportActionBar(toolbar);

		mTitle = mDrawerTitle = getTitle();

		actionBarDrawer = new ActionBarDrawerToggle(this, navDrawerLayout,
				R.string.aceptar, R.string.cancelar) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
			}

			public void onDrawerOpened(View view) {
				super.onDrawerOpened(view);
				getSupportActionBar().setTitle(mDrawerTitle);
			}
		};

		navDrawerLayout.setDrawerListener(actionBarDrawer);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().show();

		titlesMenu = getResources().getStringArray(R.array.titles);

		// Set previous array as adapter of the list
		mAdapter = new ListAdapterNavigator(this, titlesMenu);
		navList.setAdapter(mAdapter);
		navList.setOnItemClickListener(new DrawerItemClickListener());

		ImageView moneyControl = (ImageView) findViewById(R.id.moneyControlIcon);
		// ImageView daysCounter = (ImageView)
		// findViewById(R.id.daysCounterIcon);
		// ImageView twitter = (ImageView) findViewById(R.id.twitterIcon);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			selectItem(1);
		}

		moneyControl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent1 = null;
				intent1 = new Intent(
						"android.intent.action.VIEW",
						Uri.parse("https://play.google.com/store/apps/details?id=com.agudoApp.salaryApp"));
				startActivity(intent1);
			}
		});

		// daysCounter.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Intent intent1 = null;
		// intent1 = new Intent(
		// "android.intent.action.VIEW",
		// Uri.parse("https://play.google.com/store/apps/details?id=com.agba.dayscounter"));
		// startActivity(intent1);
		// }
		// });
		//
		// twitter.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
		// .parse("https://twitter.com/#!/AGBAAndroidApps"));
		// startActivity(browserIntent);
		// }
		// });
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawer.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawer.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (actionBarDrawer.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// Opciones del menu de navegacion
	private void selectItem(int position) {

		Fragment fragment = null;

		switch (position - 1) {
		case -1:
			fragment = new CuentasFragment();
			break;
		case 0:
			fragment = new NuevaPrendaFragment();
			// isCategoriaPremium);
			break;
		case 1:
			fragment = new CrearLookPrincipalFragment();
			break;
		case 2:
			fragment = new MiArmarioFragment();
			break;
		case 3:
			fragment = new MisLooksFragment();
			break;
		case 4:
			fragment = new UtilidadesFragment();
			break;
		case 5:
			fragment = new QueMePongoInicialFragment();
			break;
		case 6:
			fragment = new CalendarioFragment();
			break;
		case 7:
			fragment = new TestColoridoFragment();
			break;
		case 8:
			if (estilo == 1) {
				fragment = new MorfologiaHombreFragment();
			} else {
				fragment = new MorfologiaFragment();
			}
			break;		
		case 9:
			Intent intent1 = new Intent(
					"android.intent.action.VIEW",
					Uri.parse("https://play.google.com/store/apps/details?id=com.agba.closfy"));
			startActivity(intent1);
			break;
		// case 9:
		// Intent intent = new Intent(this, Preferences.class);
		// startActivity(intent);
		// break;
		// case 10:
		// Intent intent1 = null;
		// intent1 = new Intent(
		// "android.intent.action.VIEW",
		// Uri.parse("https://play.google.com/store/apps/details?id=com.agudoApp.salaryApp"));
		// startActivity(intent1);
		// break;
		// case 11:
		// fragment = new TiendaFragment(isPremium, isSinPublicidad,
		// isCategoriaPremium);
		// break;
		// default:
		// break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			navList.setItemChecked(position, true);
			textoHeader = (TextView) findViewById(R.id.txtHeader);

			if (position != 0) {
				setTitle(titlesMenu[position - 1]);
				mAdapter.setSelectedItem(position - 1);
				textoHeader.setTextColor(Color.BLACK);
			} else {
				setTitle(getResources().getString(R.string.cuentas));
				mAdapter.setSelectedItem(99);
				if (estilo == 1) {
					textoHeader.setTextColor(getResources().getColor(
							R.color.azul));
				} else {
					textoHeader.setTextColor(getResources().getColor(
							R.color.actionBarColor));
				}
			}
			navDrawerLayout.closeDrawer(navList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onResume() {
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		boolean actualizar = prefs.getBoolean("actualizaCuenta", false);

		if (actualizar) {
			textoHeader = (TextView) findViewById(R.id.txtHeader);
			int cuen = Util.cuentaSeleccionada(this, prefs);
			db = openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				Cuenta cuenta = gestion.getCuentaSeleccionada(db, cuen);
				textoHeader.setText(cuenta.getDescCuenta());

				estilo = gestion.getEstiloCuenta(db, cuen);
			}
			db.close();

			if (estilo == 1) {
				toolbar.setBackgroundResource(R.color.azul);
				textoHeader.setTextColor(getResources().getColor(R.color.azul));
			} else {
				toolbar.setBackgroundResource(R.color.actionBarColor);
				textoHeader.setTextColor(getResources().getColor(
						R.color.actionBarColor));
			}

			editor = prefs.edit();
			editor.putBoolean("actualizaCuenta", false);
			editor.commit();
		}

		super.onResume();
	}

	@Override
	public void onBackPressed() {
		Toast onBackPressedToast = Toast.makeText(this, R.string.pulseDosVeces,
				Toast.LENGTH_SHORT);
		long currentTime = System.currentTimeMillis();

		if (currentTime - mLastPress > mTimeLimit) {
			onBackPressedToast.show();
			mLastPress = currentTime;
		} else {
			onBackPressedToast.cancel();
			super.onBackPressed();
		}
	}
}