package com.agba.closfy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class VerLookActivity extends AppCompatActivity {
    private static final String KEY_CONTENT = "VerLookActivity:Content";
    private String mContent = "???";

    private LinearLayout lookLayout;
    private ImageView checkFavoritos;
    private ListView listUtilidadesView;
    private ImageView imagenLook;
    private LinearLayout layoutImage;
    private LinearLayout layoutText;

    LinearLayout notas;
    TextView textNotas;
    TextView textTemporada;

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

    private static final int ADD_UTILIDAD = 4;
    private static final int CHANGE_TEMP = 5;
    private static final int MENSAJE_ERROR_FOTO = 1;
    private static final int MENSAJE_ERROR_TIPO = 2;

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

        setContentView(R.layout.ver_look);

        // Cuenta seleccionada
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
        checkFavoritos = (ImageView) this.findViewById(R.id.checkFavoritos);
        layoutImage = (LinearLayout) this.findViewById(R.id.layoutImageLook);
        layoutText = (LinearLayout) this.findViewById(R.id.layoutTextLook);
        listUtilidadesView = (ListView) this.findViewById(R.id.listUtilidades);
        textTemporada = (TextView) findViewById(R.id.textTemporada);

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

        rellenarDatos();

        lookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerLookActivity.this,
                        AmpliarLookActivity.class);
                intent.putExtra("idLook", idLook);
                startActivity(intent);
            }
        });
    }

    @SuppressWarnings("deprecation")
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

        switch (temporada) {
            case 0:
                textTemporada.setText(getResources().getString(R.string.otonoInvierno));
                break;
            case 1:
                textTemporada.setText(getResources().getString(R.string.primaveraVerano));
                break;
            case 2:
                textTemporada.setText(getResources().getString(R.string.todoAno));
                break;
        }

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

    public void mostrarMensaje(String texto) {
        Context context = this.getApplicationContext();
        CharSequence text = (texto);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public class ListAdapterUtilidad extends BaseAdapter {
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
