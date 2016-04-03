package com.agba.closfy.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.fragments.LookAmpliadoSlideFragment;
import com.agba.closfy.modelo.Look;

public class NuevoAmpliarLookActivity extends AppCompatActivity {
    private static final String KEY_CONTENT = "NuevoAmpliarLookActivity:Content";
    private String mContent = "???";

    int idLook;
    int[] looks;
    int posi;

    private static final int EDIT_LOOK = 1;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.look_ampliado);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the icon, title and home/up button
        getSupportActionBar().setTitle(R.string.prendas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Bundle extras = getIntent().getExtras();
        idLook = extras.getInt("idLook");
        looks = extras.getIntArray("looks");
        posi = extras.getInt("posicion");

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(posi);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
                posi = position;
            }
        });

    }

    // Anadiendo las opciones de menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting_edit, menu);
        return true;
    }

    // Aadiendo funcionalidad a las opciones de men
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Llamar activity EditarPrenda

                Look lookSeleccionado = new Look();
                db = openOrCreateDatabase(BD_NOMBRE, 1, null);
                if (db != null) {
                    lookSeleccionado = gestion.getLookById(db, looks[posi]);
                }
                db.close();

                Intent intent2 = new Intent(this,
                        EditarLookActivity.class);
                intent2.putExtra("idLook",
                        lookSeleccionado.getIdLook());
                intent2.putExtra("temporada",
                        lookSeleccionado.getIdTemporada());
                intent2.putExtra("utilidades",
                        lookSeleccionado.getUtilidades());
                intent2.putExtra("favorito",
                        lookSeleccionado.getFavorito());
                startActivityForResult(intent2, EDIT_LOOK);

                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return LookAmpliadoSlideFragment.create(position, looks);
        }

        @Override
        public int getCount() {
            return looks.length;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_LOOK:
                    finish();
            }
        }
    }
}
