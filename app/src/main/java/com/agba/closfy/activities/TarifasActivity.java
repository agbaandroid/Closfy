package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.util.Util;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class TarifasActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	int nAsesoramientos = 1;

	boolean bloqueado = false;

	int cuentaSeleccionada;
	int estilo;
	SharedPreferences prefs;

	ProgressDialog progDailog;

	TextView textPrecio1Ase;
	TextView textPrecio3Ase;
	TextView textPrecio5Ase;

	TextView botonComprarAse1;
	TextView botonComprarAse3;
	TextView botonComprarAse5;

	// id de los productos
	static final String SKU_ASESORAMIENTO1 = "asesoramientox1";
	static final String SKU_ASESORAMIENTO3 = "asesoramientox3";
	static final String SKU_ASESORAMIENTO5 = "asesoramientox5";

	String precio1 = "";
	String precio3 = "";
	String precio5 = "";

	IInAppBillingService mService;

	private static final Random random = new Random();
	private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890!@#$";
	String tokenCompra;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tarifas);

		Intent serviceIntent = new Intent(
				"com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

		// Cuenta seleccionada
		prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (estilo == 1) {
			toolbar.setBackgroundResource(R.color.azul);
		}

		setSupportActionBar(toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(getResources().getString(R.string.tarifas));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		textPrecio1Ase = (TextView) findViewById(R.id.textPrecio1Ase);
		textPrecio3Ase = (TextView) findViewById(R.id.textPrecio3Ase);
		textPrecio5Ase = (TextView) findViewById(R.id.textPrecio5Ase);

		botonComprarAse1 = (TextView) findViewById(R.id.botonComprarAse1);
		botonComprarAse3 = (TextView) findViewById(R.id.botonComprarAse3);
		botonComprarAse5 = (TextView) findViewById(R.id.botonComprarAse5);

		botonComprarAse1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!bloqueado) {
					comprarAsesoramiento(SKU_ASESORAMIENTO1);
					bloqueado = true;
				}
			}
		});

		botonComprarAse3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!bloqueado) {
					comprarAsesoramiento(SKU_ASESORAMIENTO3);
					bloqueado = true;
				}
			}
		});

		botonComprarAse5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!bloqueado) {
					comprarAsesoramiento(SKU_ASESORAMIENTO5);
					bloqueado = true;
				}
			}
		});

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

	public class MyLoadingAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(TarifasActivity.this);
			progDailog.setIndeterminate(false);
			progDailog.setMessage(getResources().getString(R.string.cargando));
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(false);
			progDailog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			obtenerProductos();
			return null;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Void result) {
			textPrecio1Ase.setText(textPrecio1Ase.getText().toString()
					+ precio1);
			textPrecio3Ase.setText(textPrecio3Ase.getText().toString()
					+ precio3);
			textPrecio5Ase.setText(textPrecio5Ase.getText().toString()
					+ precio5);

			progDailog.dismiss();
		}
	}

	public class ConsumirProductosAsyncTask extends
			AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(TarifasActivity.this);
			progDailog.setIndeterminate(false);
			progDailog.setMessage(getResources().getString(R.string.cargando));
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(false);
			progDailog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			boolean ok = consumirProductos(params[0], params[1], params[2]);

			if (ok) {
				return true;
			} else {
				return false;
			}
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (nAsesoramientos == 1) {
					alert(getResources().getString(R.string.graciasCompra1singular) + " " +
							+ nAsesoramientos + " " 
							+ getResources().getString(R.string.graciasCompra2singular));
				} else {
					alert(getResources().getString(R.string.graciasCompra1) + " " +
							+ nAsesoramientos
							+ getResources().getString(R.string.graciasCompra2));
				}

			} else {
				alert(getResources().getString(R.string.errorCompra));
			}
			bloqueado = false;
			progDailog.dismiss();
		}
	}

	public void obtenerProductos() {
		ArrayList<String> skuList = new ArrayList<String>();
		skuList.add(SKU_ASESORAMIENTO1);
		skuList.add(SKU_ASESORAMIENTO3);
		skuList.add(SKU_ASESORAMIENTO5);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

		// Consulta de detalles del producto integrado
		try {
			String purchaseToken = "inapp:" + getPackageName()
					+ ":android.test.purchased";
			int responseAux = mService.consumePurchase(3, getPackageName(),
					purchaseToken);

			Bundle skuDetails = mService.getSkuDetails(3, getPackageName(),
					"inapp", querySkus);

			int response = skuDetails.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList<String> responseList = skuDetails
						.getStringArrayList("DETAILS_LIST");

				for (String thisResponse : responseList) {
					JSONObject object = new JSONObject(thisResponse);
					String sku = object.getString("productId");
					String precio = object.getString("price");

					if (sku.equals(SKU_ASESORAMIENTO1)) {
						precio1 = precio;
					}
					if (sku.equals(SKU_ASESORAMIENTO3)) {
						precio3 = precio;
					}
					if (sku.equals(SKU_ASESORAMIENTO5)) {
						precio5 = precio;
					}

					// if (sku.equals("premiumUpgrade")) mPremiumUpgradePrice =
					// price;
					// else if (sku.equals("gas")) mGasPrice = price;
				}
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return;
		}
	}

	public void comprarAsesoramiento(String sku) {
		try {
			//tokenCompra = getToken(5) + System.currentTimeMillis();
			// Test
			// Bundle buyIntentBundle = mService.getBuyIntent(3,
			// getPackageName(),
			// "android.test.purchased", "inapp",
			// /* "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ" */"");

			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					sku, "inapp",
					/* "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ" */"");

			int response = buyIntentBundle.getInt("RESPONSE_CODE", 0);
			if (response == 0) {
				PendingIntent pendingIntent = buyIntentBundle
						.getParcelable("BUY_INTENT");

				startIntentSenderForResult(pendingIntent.getIntentSender(),
						1001, new Intent(), Integer.valueOf(0),
						Integer.valueOf(0), Integer.valueOf(0));
			} else if (response == 7) {
				alert(getResources().getString(R.string.errorYaComprado));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1001) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

			if (resultCode == RESULT_OK) {
				try {
					JSONObject o = new JSONObject(purchaseData);
					String sku = o.getString("productId");
					String orderId = o.getString("orderId");
					String purchaseToken = o.getString("purchaseToken");

					if (SKU_ASESORAMIENTO1.equals(sku)) {
						nAsesoramientos = 1;
					} else if (SKU_ASESORAMIENTO3.equals(sku)) {
						nAsesoramientos = 3;
					} else if (SKU_ASESORAMIENTO5.equals(sku)) {
						nAsesoramientos = 5;
					}

					new ConsumirProductosAsyncTask().execute(
							String.valueOf(nAsesoramientos), orderId,
							purchaseToken);

				} catch (Exception e) {
					alert(getResources().getString(R.string.errorCompra));
				}
			} else {
				bloqueado = false;
			}
		}
	}

	public boolean consumirProductos(String nAsesoramientos, String orderId,
			String purchaseToken) {
		// Guardamos en base de datos los asesoramientos adquiridos
		boolean resultado = false;
		db = openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			resultado = gestion.insertarAsesoramientos(db,
					Integer.parseInt(nAsesoramientos), orderId);
		}
		db.close();

		if (resultado) {
			try {
				// Se consume el producto integrado
				int response;

				response = mService.consumePurchase(3, getPackageName(),
						purchaseToken);

				if (response == 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
		} else {
			return false;
		}
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton(getResources().getString(R.string.aceptar), null);
		bld.create().show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mServiceConn != null) {
			unbindService(mServiceConn);
		}
	}

//	public static String getToken(int length) {
//		StringBuilder token = new StringBuilder(length);
//		for (int i = 0; i < length; i++) {
//			token.append(CHARS.charAt(random.nextInt(CHARS.length())));
//		}
//		return token.toString();
//	}

}
