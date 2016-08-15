package com.agba.closfy.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;

public class CargandoActivity extends Activity {
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();
	private SQLiteDatabase db;

	static final String SKU_SIN_PUBLICIDAD = "sin_publicidad";

	// Productos que posee el usuario
	boolean isSinPublicidad = false;
	SharedPreferences prefs;

	IInAppBillingService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargando);

		Intent serviceIntent = new Intent(
				"com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

		// Asignamos el tipo de fuente
		Typeface miPropiaTypeFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/Pacifico.ttf");


		TextView txtCargando = (TextView) findViewById(R.id.textCargando);
		txtCargando.setTypeface(miPropiaTypeFace);
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

		ArrayList<String> skuList = new ArrayList<String>();
		skuList.add(SKU_SIN_PUBLICIDAD);

		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

		Bundle ownedItems;
		try {
			ownedItems = mService.getPurchases(3, getPackageName(), "inapp",
					null);
			int response = ownedItems.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList<String> ownedSkus = ownedItems
						.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				ArrayList<String> purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

				for (int i = 0; i < purchaseDataList.size(); ++i) {
					String sku = ownedSkus.get(i);

					if (sku.equals(SKU_SIN_PUBLICIDAD)) {
						isSinPublicidad = true;
					}
				}

			}
		} catch (RemoteException e1) {
			return;
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
			new MyLoadingAsyncTask().execute();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mServiceConn != null) {
			unbindService(mServiceConn);
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
				prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
				boolean tutorialShowed = prefs.getBoolean("tutorialShowed", false);
				if(tutorialShowed){
					intent = new Intent(CargandoActivity.this, ClosfyActivity.class);
				}else{
					intent = new Intent(CargandoActivity.this, TutorialActivity.class);
				}
			} else {
				intent = new Intent(CargandoActivity.this,
						ConfiguracionInicialActivity.class);
			}
			intent.putExtra("isSinPublicidad", isSinPublicidad);

			startActivity(intent);
			finish();

		}
	}
}
