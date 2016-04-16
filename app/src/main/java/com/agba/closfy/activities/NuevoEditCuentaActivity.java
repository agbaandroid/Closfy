package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.adapters.ListAdapterIconCuenta;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.modelo.Icon;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class NuevoEditCuentaActivity extends AppCompatActivity {

    static final int MENSAJE_CONFIRMACION = 1;
    static final int MENSAJE_ERROR_CUENTA_PRINCIPAL = 4;
    static final int IMPORTE = 1;
    private final String BD_NOMBRE = "BDClosfy";
    private SQLiteDatabase db;
    final GestionBBDD gestion = new GestionBBDD();

    EditText nombre;
    Spinner spinnerIconUser;
    Spinner spinnerSexo;
    private RelativeLayout layoutPubli;

    String idCuenta;
    Cuenta cuenta = new Cuenta();

    // Productos que posee el usuario
    boolean isPremium = false;
    boolean isSinPublicidad = false;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_edit_cuenta);

        prefs = getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        AdView adView;
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        nombre = (EditText) findViewById(R.id.nombre);
        spinnerIconUser = (Spinner) findViewById(R.id.spinnerIconCuenta);
        spinnerSexo = (Spinner) findViewById(R.id.spinnerSexoCuenta);

        cargarSpinners();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idCuenta = extras.getString("idCuenta");
            isPremium = extras.getBoolean("isPremium", false);
            isSinPublicidad = extras.getBoolean("isSinPublicidad", false);
        }

        db = openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            cuenta = gestion.getCuentaSeleccionada(db, Integer.parseInt(idCuenta));
        }
        db.close();

        nombre.setText(cuenta.getDescCuenta());
        spinnerIconUser.setSelection(cuenta.getIdIcon());
        spinnerSexo.setSelection(cuenta.getSexo());

        // Inflate the custom view and add click handlers for the buttons
        View actionBarButtons = getLayoutInflater().inflate(R.layout.edit_delete_actionbar,
                new LinearLayout(this), false);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        View deleteActionView = actionBarButtons.findViewById(R.id.action_cancel);
        deleteActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(MENSAJE_CONFIRMACION);
            }
        });

        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(nombre.getText().toString())) {
                    String text = "";

                    for (int i = 0; i < nombre.getText().length(); i++) {
                        if (i == 0) {
                            text = text
                                    + nombre.getText().toString().toUpperCase()
                                    .charAt(i);
                        } else {
                            text = text
                                    + nombre.getText().toString().toLowerCase()
                                    .charAt(i);
                        }
                    }

                    int idIcon = 0;
                    idIcon = spinnerIconUser.getSelectedItemPosition();

                    boolean ok = false;
                    db = openOrCreateDatabase(BD_NOMBRE, 1, null);
                    if (db != null) {
                        ok = gestion.editCuenta(db, text.trim(), idCuenta, idIcon);
                    }
                    db.close();

                    if (ok) {
                        Context context = getApplicationContext();
                        CharSequence textMsg = getResources().getString(
                                R.string.editCuentaOK);
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, textMsg, duration);
                        toast.show();

                        finish();
                    } else {
                        Context context = getApplicationContext();
                        CharSequence textMsg = getResources().getString(
                                R.string.editCuentaKO);
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Set the custom view and allow the bar to show it
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
        getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

        layoutPubli = (RelativeLayout) findViewById(R.id.layoutPubli);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        switch (id) {
            case MENSAJE_ERROR_CUENTA_PRINCIPAL:
                builder.setMessage(
                        getResources().getString(R.string.cuentaPrincipal))
                        .setTitle(getResources().getString(R.string.atencion))
                        .setIcon(R.drawable.ic_alert)
                        .setPositiveButton(
                                getResources().getString(R.string.aceptar),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        NuevoEditCuentaActivity.this.setResult(0);
                                        finish();
                                    }
                                });
                alert = builder.create();
                alert.show();
                break;
            case MENSAJE_CONFIRMACION:
                builder.setTitle(R.string.deleteCuenta);
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage(getResources().getString(
                        R.string.deleteCuentaMns));
                builder.setCancelable(false);
                builder.setPositiveButton(
                        getResources().getString(R.string.eliminar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (cuenta.getIdCuenta().equals("0")) {
                                    onCreateDialog(MENSAJE_ERROR_CUENTA_PRINCIPAL);
                                } else {
                                    boolean ok = false;
                                    db = openOrCreateDatabase(BD_NOMBRE, 1, null);
                                    if (db != null) {
                                        ok = gestion.deleteCuenta(db,
                                                cuenta.getIdCuenta());
                                    }
                                    db.close();
                                    if (ok) {
                                        cuentaDefecto();

                                        Context context = getApplicationContext();
                                        CharSequence textMsg = getResources()
                                                .getString(R.string.deleteCuentaOK);
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context,
                                                textMsg, duration);
                                        toast.show();

                                        finish();
                                    } else {
                                        Context context = getApplicationContext();
                                        CharSequence textMsg = getResources()
                                                .getString(R.string.deleteCuentaKO);
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context,
                                                textMsg, duration);
                                        toast.show();

                                        finish();
                                    }
                                }
                                dialog.cancel();
                            }
                        }).setNegativeButton(
                        getResources().getString(R.string.cancelar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                NuevoEditCuentaActivity.this.setResult(0);
                                finish();
                            }
                        });
                alert = builder.create();
                alert.show();
                break;
        }
        return null;
    }

    // Anadiendo funcionalidad a las opciones de menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        View view = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void cargarSpinners() {
        ArrayList<Icon> listIcon = Util.obtenerIconosCuenta();
        // Creamos el adaptador
        ListAdapterIconCuenta spinner_adapterIcont = new ListAdapterIconCuenta(
                this, listIcon);
        spinnerIconUser.setAdapter(spinner_adapterIcont);

        ArrayAdapter<CharSequence> adapterList;
        adapterList = ArrayAdapter.createFromResource(this,
                R.array.arraySexo,
                android.R.layout.simple_spinner_item);
        adapterList.setDropDownViewResource(R.layout.spinner);
        spinnerSexo.setAdapter(adapterList);
    }

    public void cuentaDefecto() {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);

        editor = prefs.edit();

        editor.putInt("cuenta", 0);
        editor.commit();
    }
}
