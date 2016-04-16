package com.agba.closfy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Locale;

public class CuentasActivity extends AppCompatActivity {

    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();
    private SQLiteDatabase db;

    static final int MENSAJE_CONFIRMACION = 1;

    private final int CUENTAS = 1;

    ListView listaCuentas;
    private RelativeLayout layoutPubli;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    // Productos que posee el usuario
    boolean isPremium = false;
    boolean isSinPublicidad = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_cuentas);

        prefs = getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        AdView adView;
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isPremium = extras.getBoolean("isPremium", false);
            isSinPublicidad = extras.getBoolean("isSinPublicidad", false);
        }

        getSupportActionBar().setTitle(
                getResources().getString(R.string.cuentasList));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutPubli = (RelativeLayout) findViewById(R.id.layoutPubli);

        listaCuentas = (ListView) findViewById(R.id.listaCuentas);
        obtenerCuentas();
    }

    public void obtenerCuentas() {
        ArrayList<Cuenta> listCuentas = new ArrayList<Cuenta>();
        db = openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            // Recuperamos el listado del spinner Categorias
            listCuentas = (ArrayList<Cuenta>) gestion.getCuentas(db);
        }
        db.close();

        listaCuentas.setAdapter(new ListAdapterCuentas(this, listCuentas));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting_mas, menu);
        return true;
    }

    // Aadiendo funcionalidad a las opciones de men
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(CuentasActivity.this, AddCuentaActivity.class);
                intent.putExtra("isPremium", isPremium);
                intent.putExtra("isSinPublicidad", isSinPublicidad);
                startActivityForResult(intent, CUENTAS);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ListAdapterCuentas extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Cuenta> listaCuenta = new ArrayList<Cuenta>();
        Locale locale = Locale.getDefault();
        String languaje = locale.getLanguage();
        Context context;

        public ListAdapterCuentas(Context context, ArrayList<Cuenta> lista) {
            listaCuenta = lista;
            mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listaCuenta.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listaCuenta.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtNombre;
            ImageView iconCuenta;
            LinearLayout layoutCuenta;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.lista_cuentas, null);
            }

            txtNombre = (TextView) convertView.findViewById(R.id.textCuenta);
            iconCuenta = (ImageView) convertView.findViewById(R.id.iconCuenta);
            layoutCuenta = (LinearLayout) convertView.findViewById(R.id.layoutCuenta);

            Cuenta cuenta = listaCuenta.get(position);
            txtNombre.setText(cuenta.getDescCuenta());
            iconCuenta.setBackgroundResource(Util.obtenerIconoUser(cuenta.getIdIcon()));

            layoutCuenta.setTag(position);

            layoutCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posiSel = (int) v.getTag();
                    Cuenta cuenta = listaCuenta.get(posiSel);
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(context, NuevoEditCuentaActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idCuenta", cuenta.getIdCuenta());
                    intent.putExtras(bundle);
                    intent.putExtra("isPremium", isPremium);
                    intent.putExtra("isSinPublicidad", isSinPublicidad);
                    startActivityForResult(intent, CUENTAS);
                }
            });

            return convertView;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        obtenerCuentas();
    }
}
