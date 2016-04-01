package com.agba.closfy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.CrearLookFragment;
import com.agba.closfy.fragments.CrearLookFragmentHombre;
import com.agba.closfy.fragments.CrearLookInicioFragment;
import com.agba.closfy.util.Util;

import java.util.ArrayList;

public class CrearLookPrincipalActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    View cancelActionView;
    View doneActionView;
    View actionBarButtons;
    int paso = 1;
    Fragment fragment;
    public int estilo;
    public ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();
    public int favorito = 0;
    public int idRadioTemporada;
    public ArrayList<Integer> listaPrendasSeleccionadas = new ArrayList<Integer>();
    private static final int CREAR_LOOK = 1;

    SharedPreferences prefs;
    int cuentaSeleccionada;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_look_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        // Cuenta seleccionada
        prefs = getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(this, prefs);

        db = openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        // Inflate the custom view and add click handlers for the buttons
        actionBarButtons = getLayoutInflater().inflate(R.layout.next_cancel_actionbar,
                new LinearLayout(this), false);

        cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
        doneActionView = actionBarButtons.findViewById(R.id.action_done);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // Set the custom view and allow the bar to show it
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
        getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

        cancelActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paso == 1) {
                    if (estilo == 0) {
                        fragment = new CrearLookFragmentHombre(idRadioTemporada,
                                listIdsUtilidad, favorito);
                    } else {
                        fragment = new CrearLookFragment(idRadioTemporada,
                                listIdsUtilidad, favorito);
                    }

                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.crearlookFragment, fragment).commit();

                    }
                }else{
                    String prendas = obtenerCadenaPrendas();

                    // obtenemos la cadena de utilidades
                    String utilidades = Util
                            .obtenerCadenaUtilidades(listIdsUtilidad);

                    Intent intent = new Intent(CrearLookPrincipalActivity.this,
                            ResumenLookMainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cadenaPrendas", prendas);
                    bundle.putString("utilidades", utilidades);
                    bundle.putInt("temporada", idRadioTemporada);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CREAR_LOOK);
                    //inicializar();
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

        fragment = new CrearLookInicioFragment();
        if (fragment != null) {
            FragmentManager fragmentManager = this
                    .getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.crearlookFragment, fragment).commit();

        }
    }

    public String obtenerCadenaPrendas() {
        String cadena = "";

        for (int i = 0; i < listaPrendasSeleccionadas.size(); i++) {
            cadena = cadena + String.valueOf(listaPrendasSeleccionadas.get(i)) + ";";
        }

        if(!cadena.equals("")){
            cadena.substring(0, cadena.length() - 1);
        }
        return cadena;
    }

    public void cambiarActionBar(int pasoAux){
        paso = pasoAux;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case CREAR_LOOK:
                finish();
        }

    }
}
