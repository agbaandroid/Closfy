package com.agba.closfy.activities;

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
import com.agba.closfy.fragments.CrearLookFragmentHombre;
import com.agba.closfy.fragments.CrearLookInicioFragment;

import java.util.ArrayList;

public class CrearLookPrincipalActivity extends AppCompatActivity {

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



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_look_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

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

               /* if (estilo == 0){
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

                }*/
                fragment = new CrearLookFragmentHombre(idRadioTemporada,
                        listIdsUtilidad, favorito);

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.crearlookFragment, fragment).commit();
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

    public void cambiarActionBar(int pasoAux){
        paso = pasoAux;
    }
}
