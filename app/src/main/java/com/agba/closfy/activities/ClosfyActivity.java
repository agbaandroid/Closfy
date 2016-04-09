package com.agba.closfy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.adapters.ListAdapterNavigator;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.CalendarioFragment;
import com.agba.closfy.fragments.MorfologiaFragment;
import com.agba.closfy.fragments.MorfologiaHombreFragment;
import com.agba.closfy.fragments.NuevoMiArmarioFragment;
import com.agba.closfy.fragments.NuevoMisLooksFragment;
import com.agba.closfy.fragments.QueMePongoInicialFragment;
import com.agba.closfy.fragments.TestColoridoFragment;
import com.agba.closfy.fragments.TiposFragment;
import com.agba.closfy.fragments.UtilidadesFragment;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ClosfyActivity extends AppCompatActivity {
	// Menu navegacion
	private String[] titlesMenu;
	private String[] titlesMenuMayusculas;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ListView navList, left_cuentas;
	private LinearLayout layoutCuentas, layoutGestionCuentas, layoutHeader, layoutHeader2;
	private LinearLayout left_drawer_cuentas;
	private DrawerLayout navDrawerLayout;
	private ActionBarDrawerToggle actionBarDrawer;
	private ListAdapterNavigator mAdapter;
	private ListAdapterCuentasNavigator mAdapterCuentas;
	TextView textoHeader;

	private InterstitialAd interstitial;

	private final String BD_NOMBRE = "BDClosfy";
	private SQLiteDatabase db;
	final GestionBBDD gestion = new GestionBBDD();

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;
	Toolbar toolbar;
	boolean listaCuentas;

	ArrayList<Cuenta> listCuentas;

	private long mLastPress = 0; // Cuando se pulsa atras por ultima vez
	private long mTimeLimit = 3000; // Limite de tiempo entre pulsaciones, en ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);

		View header = getLayoutInflater().inflate(R.layout.header, null);
		View headerCuentas = getLayoutInflater().inflate(R.layout.header_cuentas, null);
		View footer = getLayoutInflater().inflate(R.layout.footer, null);

		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navList = (ListView) findViewById(R.id.left_drawer);
		left_drawer_cuentas = (LinearLayout) findViewById(R.id.left_drawer_cuentas);
		left_cuentas = (ListView) findViewById(R.id.left_cuentas);
		layoutCuentas = (LinearLayout) findViewById(R.id.layoutCuentas);
		layoutGestionCuentas = (LinearLayout) findViewById(R.id.layoutGestionCuentas);
		layoutHeader = (LinearLayout) findViewById(R.id.layoutHeader);
		layoutHeader2 = (LinearLayout) findViewById(R.id.layoutHeader2);

		navList.addHeaderView(header);
		navList.addFooterView(footer);

		left_cuentas.addHeaderView(headerCuentas);

		mTitle = mDrawerTitle = getTitle();

		layoutCuentas.setVisibility(View.GONE);

		// Anuncio Inicial
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-2303483383476811/5599595681");

		AdRequest adRequestCompleto = new AdRequest.Builder().build();
		interstitial.loadAd(adRequestCompleto);

		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				displayInterstitial();
				super.onAdLoaded();
			}
		});

		textoHeader = (TextView) findViewById(R.id.nombreCuenta);

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
		titlesMenuMayusculas = getResources().getStringArray(R.array.titlesMayusculas);

		// Set previous array as adapter of the list
		mAdapter = new ListAdapterNavigator(this, titlesMenuMayusculas);
		navList.setAdapter(mAdapter);
		navList.setOnItemClickListener(new DrawerItemClickListener());
		left_cuentas.setOnItemClickListener(new DrawerItemClickListener());

		listCuentas = new ArrayList<Cuenta>();
		listCuentas = obtenerCuentas();

		mAdapterCuentas = new ListAdapterCuentasNavigator(this, listCuentas);
		left_cuentas.setAdapter(mAdapterCuentas);

		ImageView moneyControl = (ImageView) findViewById(R.id.moneyControlIcon);

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

		layoutGestionCuentas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ClosfyActivity.this, CuentasActivity.class);
				startActivityForResult(intent, 1);

				navList.setVisibility(View.VISIBLE);
				layoutCuentas.setVisibility(View.GONE);

				listaCuentas = false;

				navDrawerLayout.closeDrawer(left_drawer_cuentas);
			}
		});
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

				if (listaCuentas) {
					listaCuentas = false;
					navList.setVisibility(View.VISIBLE);
					layoutCuentas.setVisibility(View.GONE);
				} else {
					listaCuentas = true;
					navList.setVisibility(View.GONE);
					layoutCuentas.setVisibility(View.VISIBLE);
				}
				break;
		case 0:
			fragment = new NuevoMiArmarioFragment();
			break;
		case 1:
			fragment = new NuevoMisLooksFragment();
			break;
        case 2:
            fragment = new TiposFragment();
            break;
        case 3:
            fragment = new UtilidadesFragment();
            break;
		case 4:
			fragment = new QueMePongoInicialFragment();
			break;
		case 5:
			fragment = new CalendarioFragment();
			break;
		case 6:
			fragment = new TestColoridoFragment();
			break;
		case 7:
			if (estilo == 1) {
				fragment = new MorfologiaHombreFragment();
			} else {
				fragment = new MorfologiaFragment();
			}
			break;
		case 8:
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
			TextView textoHeader = (TextView) findViewById(R.id.nombreCuenta);


			if (position != 0) {
				setTitle(titlesMenu[position - 1]);
				mAdapter.setSelectedItem(position - 1);
				//textoHeader.setTextColor(Color.GRAY);
			}

			navDrawerLayout.closeDrawer(left_drawer_cuentas);
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
		TextView textoHeader = (TextView) findViewById(R.id.nombreCuenta);
		ImageView idIcon = (ImageView) findViewById(R.id.iconCuenta);

		TextView textoHeader2 = (TextView) findViewById(R.id.nombreCuenta2);
		ImageView idIcon2 = (ImageView) findViewById(R.id.iconCuenta2);

		int cuen = cuentaSeleccionada();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			Cuenta cuenta = gestion.getCuentaSeleccionada(db, cuen);
			textoHeader.setText(cuenta.getDescCuenta());
			idIcon.setBackgroundResource(Util.obtenerIconoUser(cuenta.getIdIcon()));
			textoHeader2.setText(cuenta.getDescCuenta());
			idIcon2.setBackgroundResource(Util.obtenerIconoUser(cuenta.getIdIcon()));
		}
		db.close();
		super.onResume();
	}

	// Comprobamos si debemos mostrar la publicidad o no
	public void displayInterstitial() {
		if(mostrarAnuncioCompleto()) {
			interstitial.show();
		}
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

	// Comprobamos si debemos mostrar el anuncio completo o no
	public boolean mostrarAnuncioCompleto() {
		boolean mostrarAnuncio = false;

		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		String fechaAct = mDay + "/" + (mMonth + 1) + "/" + mYear;
		String fechaAnuncio = prefs.getString("fechaAnuncio", "");
		int contador = prefs.getInt("contadorPubli", 0);
		contador = contador + 1;

		if (fechaAnuncio.equals(fechaAct) && contador == 2) {
			mostrarAnuncio = true;
			editor = prefs.edit();
			editor.putInt("contadorPubli", contador);
			editor.commit();
		} else {
			editor = prefs.edit();
			editor.putString("fechaAnuncio", fechaAct);
			editor.putInt("contadorPubli", contador);
			//editor.putInt("ultimoNumPubli", 0);
			editor.commit();
		}

		return mostrarAnuncio;
	}

	public ArrayList<Cuenta> obtenerCuentas() {
		ArrayList<Cuenta> listCuentas = new ArrayList<Cuenta>();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			listCuentas = (ArrayList) gestion.getCuentas(db);
		}
		return listCuentas;
	}

	public class ListAdapterCuentasNavigator extends BaseAdapter {
		private LayoutInflater mInflater;
		private int mSelectedItem;
		private ArrayList<Cuenta> lCuentas;
		Locale locale = Locale.getDefault();

		public ListAdapterCuentasNavigator(Context context, ArrayList<Cuenta> lista) {
			lCuentas = lista;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lCuentas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lCuentas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView text;
			ImageView icon;
			LinearLayout layoutNavigator;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lista_navigator_cuentas, null);
			}

			text = (TextView) convertView.findViewById(R.id.textNavigatorCuentas);
			icon = (ImageView) convertView.findViewById(R.id.iconNavigatorCuentas);
			layoutNavigator = (LinearLayout) convertView.findViewById(R.id.layoutNavigatorCuentas);
			text.setText(lCuentas.get(position).getDescCuenta());

			icon.setBackgroundResource(Util.obtenerIconoUser(lCuentas.get(position).getIdIcon()));

			layoutNavigator.setTag(position);

			layoutNavigator.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int posiSel = (int) v.getTag();
					Cuenta cuenta = listCuentas.get(posiSel);
					seleccionarCuenta(cuenta.getIdCuenta());

					Fragment fragment = new NuevoMiArmarioFragment();

					FragmentManager fragmentManager = getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, fragment).commit();

					navList.setItemChecked(1, true);
					setTitle(titlesMenu[0]);
					mAdapter.setSelectedItem(0);

					navList.setVisibility(View.VISIBLE);
					layoutCuentas.setVisibility(View.GONE);

					listaCuentas = false;

					navDrawerLayout.closeDrawer(left_drawer_cuentas);
				}
			});

			return convertView;
		}

		public int getSelectedItem() {
			return mSelectedItem;
		}

		public void setSelectedItem(int selectedItem) {
			mSelectedItem = selectedItem;
		}

	}

	public void seleccionarCuenta(String idCuenta) {
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.putInt("cuenta", Integer.parseInt(idCuenta));
		editor.commit();

		TextView textoHeader = (TextView) findViewById(R.id.nombreCuenta);
		ImageView idIcon = (ImageView) findViewById(R.id.iconCuenta);

		TextView textoHeader2 = (TextView) findViewById(R.id.nombreCuenta2);
		ImageView idIcon2 = (ImageView) findViewById(R.id.iconCuenta2);

		int cuen = cuentaSeleccionada();
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			Cuenta cuenta = gestion.getCuentaSeleccionada(db, cuen);
			textoHeader.setText(cuenta.getDescCuenta());
			idIcon.setBackgroundResource(Util.obtenerIconoUser(cuenta.getIdIcon()));
			textoHeader2.setText(cuenta.getDescCuenta());
			idIcon2.setBackgroundResource(Util.obtenerIconoUser(cuenta.getIdIcon()));
		}
		db.close();
	}

	public int cuentaSeleccionada() {
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);

		int idCuenta = prefs.getInt("cuenta", 0);
		return idCuenta;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		listCuentas = obtenerCuentas();

		mAdapterCuentas = new ListAdapterCuentasNavigator(this, listCuentas);
		left_cuentas.setAdapter(mAdapterCuentas);
	}
}