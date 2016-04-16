package com.agba.closfy.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class EditarLookActivity extends AppCompatActivity {
    private static final String KEY_CONTENT = "VerLookActivity:Content";
    private String mContent = "???";

    private LinearLayout lookLayout;
    private ImageView checkFavoritos;
    private ListView listUtilidadesView;
    private TextView textTemporada;
    private ImageView imagenLook;
    private LinearLayout layoutImage;
    private LinearLayout layoutText;
    private Spinner spinnerTemporada;

    LinearLayout notas;
    TextView textNotas;
    String notasString = "";

    int estilo;
    int idLook;
    int tipo;
    int temporada;
    int favoritoSelec;
    String idFoto = "";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    int cuentaSeleccionada;
    int favorito = 0;
    int idTipo = 0;
    String utilidades = "";

    Look look;
    ArrayList<Look> listLooks;
    ArrayList<Integer> listIdsUtilidad = new ArrayList<Integer>();

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    private static final int EDIT_LOOK = 3;
    private static final int MENSAJE_CONFIRMAR_ELIMINAR = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        idLook = extras.getInt("idLook");
        temporada = extras.getInt("temporada");
        listIdsUtilidad = Util.obtenerListaUtilidades(extras
                .getString("utilidades"));
        favoritoSelec = extras.getInt("favorito");

        look = new Look();
        db = openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            look = gestion.getLookById(db, idLook);
        }
        db.close();

        listLooks = new ArrayList<Look>();
        listLooks.add(look);

        setContentView(R.layout.nuevo_editar_look);

        AdView adView;
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Cuenta seleccionadaØ
        prefs = this.getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
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

        getSupportActionBar().setTitle(
                getResources().getString(R.string.detalle));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lookLayout = (LinearLayout) findViewById(R.id.lookLayout);

        imagenLook = (ImageView) findViewById(R.id.imagenLook);
        layoutText = (LinearLayout) this.findViewById(R.id.layoutTextLook);
        layoutImage = (LinearLayout) findViewById(R.id.layoutImageLook);

        spinnerTemporada = (Spinner) findViewById(R.id.spinnerTemporada);
        checkFavoritos = (ImageView) this.findViewById(R.id.checkFavoritos);

        textTemporada = (TextView) this.findViewById(R.id.textTemporada);

        listUtilidadesView = (ListView) this.findViewById(R.id.listUtilidades);

        notas = (LinearLayout) findViewById(R.id.notas);
        textNotas = (TextView) findViewById(R.id.textNotas);

        ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();

        db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            listUtilidades = gestion.getUtilidades(db);
        }
        db.close();

        ListAdapterUtilidad adapterUtilidad = new ListAdapterUtilidad(this,
                listUtilidades);
        listUtilidadesView.setAdapter(adapterUtilidad);

        // Inflate the custom view and add click handlers for the buttons
        View actionBarButtons = getLayoutInflater().inflate(R.layout.edit_delete_actionbar,
                new LinearLayout(this), false);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(MENSAJE_CONFIRMAR_ELIMINAR);
            }
        });

        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = openOrCreateDatabase(BD_NOMBRE, 1, null);
                boolean ok = false;

                // obtenemos la cadena de utilidades
                utilidades = Util.obtenerCadenaUtilidades(listIdsUtilidad);

                // Si no hay error insertamos la prenda
                if (db != null) {
                    ok = gestion.editarLook(db, String.valueOf(idLook),
                            temporada, utilidades, favorito, textNotas.getText().toString());
                }
                db.close();

                if (ok) {
                    mostrarMensaje(getResources()
                            .getString(R.string.editLookOK));
                    setResult(RESULT_OK, getIntent());
                    finish();
                } else {
                    mostrarMensaje(getResources()
                            .getString(R.string.editLookKO));
                }
            }
        });

        // Hide the icon, title and home/up button
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Set the custom view and allow the bar to show it
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
        getSupportActionBar().setCustomView(actionBarButtons, layoutParams);

        obtenerTemporadas();

        rellenarDatos();

        checkFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorito == 0) {
                    favorito = 1;
                    if (estilo == 0) {
                        checkFavoritos
                                .setBackgroundResource(R.drawable.check_corazon_on);
                    } else {
                        checkFavoritos
                                .setBackgroundResource(R.drawable.check_estrella_on);
                    }
                } else {
                    favorito = 0;
                    if (estilo == 0) {
                        checkFavoritos
                                .setBackgroundResource(R.drawable.check_corazon_off);
                    } else {
                        checkFavoritos
                                .setBackgroundResource(R.drawable.check_estrella_off);
                    }
                }
            }
        });

        lookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarLookActivity.this,
                        EditarResumenLookMainActivity.class);
                intent.putExtra("idLook", idLook);
                startActivityForResult(intent, EDIT_LOOK);
            }
        });

        spinnerTemporada
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        temporada = position;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        /*botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });*/

    }

    public void rellenarDatos() {

        if (favoritoSelec == 1) {
            favorito = 1;
            if (estilo == 1) {
                checkFavoritos
                        .setBackgroundResource(R.drawable.check_estrella_on);
            } else {
                checkFavoritos
                        .setBackgroundResource(R.drawable.check_corazon_on);
            }
        } else {
            favorito = 0;
            if (estilo == 1) {
                checkFavoritos
                        .setBackgroundResource(R.drawable.check_estrella_off);
            } else {
                checkFavoritos
                        .setBackgroundResource(R.drawable.check_corazon_off);
            }
        }

        spinnerTemporada.setSelection(temporada);

        Look look = new Look();
        db = openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            look = gestion.getLookById(db, idLook);
        }
        db.close();

        ArrayList<Look> listLooks = new ArrayList<Look>();
        listLooks.add(look);

        if (look.getNotas() != null) {
            textNotas.setText(look.getNotas());
        }

        Util.obtenerImagenLook(this, listLooks, 4);

        if (look.getFoto() != null) {
            imagenLook.setBackgroundDrawable(look.getFoto());
        } else {
            layoutImage.setVisibility(View.GONE);
            layoutText.setVisibility(View.VISIBLE);
        }

    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        switch (id) {
            case MENSAJE_CONFIRMAR_ELIMINAR:
                builder.setTitle(getResources().getString(R.string.atencion));
                builder.setMessage(getResources().getString(
                        R.string.msnEliminarLook));
                builder.setIcon(R.drawable.ic_delete);
                builder.setCancelable(false);
                builder.setPositiveButton(
                        getResources().getString(R.string.aceptar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean ok = false;
                                db = openOrCreateDatabase(BD_NOMBRE,
                                        1, null);


                                if (db != null) {
                                    look = gestion.getLookById(db, idLook);
                                }


                                if (db != null) {
                                    ok = gestion.eliminarLook(db,
                                            look.getIdLook(),
                                            look.getIdFoto());
                                }
                                db.close();

                                if (ok) {
                                    Context context = getApplicationContext();
                                    CharSequence text = getResources().getString(
                                            R.string.deleteLookOk);
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text,
                                            duration);
                                    toast.show();

                                    setResult(RESULT_OK, getIntent());
                                    finish();
                                } else {
                                    Context context = getApplicationContext();
                                    CharSequence text = getResources().getString(
                                            R.string.deleteLookError);
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text,
                                            duration);
                                    toast.show();
                                }
                                dialog.cancel();
                            }
                        }).setNegativeButton(
                        getResources().getString(R.string.cancelar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert = builder.create();
                alert.show();
                break;

        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case EDIT_LOOK:
                setResult(RESULT_OK, getIntent());
                finish();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    public void crearDirectorio(File dbFile) {
        if (!dbFile.exists()) {
            dbFile.mkdirs();

            File fileNoFile = new File(dbFile, ".nomedia");
            try {
                fileNoFile.createNewFile();
                // write the bytes in file
                FileOutputStream fo = new FileOutputStream(fileNoFile);
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void obtenerTemporadas() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tiposTemporada,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinnerTemporada.setAdapter(adapter);
    }

    public void mostrarMensaje(String texto) {
        Context context = this.getApplicationContext();
        CharSequence text = (texto);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public class ListAdapterUtilidad extends BaseAdapter implements
            OnClickListener {
        private LayoutInflater mInflater;
        private ArrayList<Utilidad> listaUtilidad = new ArrayList<Utilidad>();
        Locale locale = Locale.getDefault();
        String languaje = locale.getLanguage();
        ArrayList<View> listViews = new ArrayList<View>();

        public ListAdapterUtilidad(Context context, ArrayList<Utilidad> lista) {
            listaUtilidad = lista;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listaUtilidad.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listaUtilidad.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView tic;
            TextView textUtilidad;
            LinearLayout linearLayoutLista;

            if (convertView == null) {
                convertView = mInflater
                        .inflate(R.layout.lista_utilidades, null);
            }

            tic = (ImageView) convertView.findViewById(R.id.imgUtilidad);
            textUtilidad = (TextView) convertView
                    .findViewById(R.id.textUtilidad);

            textUtilidad.setText(listaUtilidad.get(position).getNombre());

            linearLayoutLista = (LinearLayout) convertView
                    .findViewById(R.id.linearLayoutLista);
            linearLayoutLista.setOnClickListener(this);

            convertView.setTag(listaUtilidad.get(position).getIdUtilidad());
            tic.setTag(listaUtilidad.get(position).getIdUtilidad());

            boolean encontrado = false;
            for (int i = 0; i < listIdsUtilidad.size(); i++) {
                if (listaUtilidad.get(position).getIdUtilidad() == listIdsUtilidad
                        .get(i)) {
                    encontrado = true;
                }
            }

            if (encontrado) {
                tic.setBackgroundResource(R.drawable.tic);
            } else {
                tic.setBackgroundColor(getResources().getColor(
                        android.R.color.transparent));
            }

            return convertView;
        }

        // Al pulsar un d�a del calendario
        @Override
        public void onClick(View view) {
            Integer id = (Integer) view.getTag();

            if (id == -1 & !listIdsUtilidad.contains(id)) {
                listIdsUtilidad.clear();
            }

            if (listIdsUtilidad.contains(id)) {
                if (id != -1) {
                    listIdsUtilidad.remove(id);
                }
            } else {
                if (id != 0 & listIdsUtilidad.contains(-1)) {
                    listIdsUtilidad.remove(0);
                }
                listIdsUtilidad.add(id);
            }

            if (listIdsUtilidad.size() == 0) {
                listIdsUtilidad.add(-1);
            }

            notifyDataSetChanged();

        }

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
