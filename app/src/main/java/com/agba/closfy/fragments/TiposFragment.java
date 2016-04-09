package com.agba.closfy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.AddTipoActivity;
import com.agba.closfy.activities.EditTipoActivity;
import com.agba.closfy.activities.EditUtilidadActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Subtipo;
import com.agba.closfy.util.Util;

import java.util.ArrayList;

public class TiposFragment extends Fragment {
    private static final String KEY_CONTENT = "TiposFragment:Content";
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();
    private SQLiteDatabase db;
    protected ListView listTipoView;
    private Spinner spinnerTipo;
    int idTipo = 0;

    int estilo;

    SharedPreferences prefs;
    int cuentaSeleccionada;

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.tipos, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        listTipoView = (ListView) this.getView().findViewById(
                R.id.listaPestanaTipos);
        spinnerTipo = (Spinner) this.getView().findViewById(R.id.spinnerTipo);

        // Cuenta seleccionada
        prefs = getActivity().getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        obtenerTiposPrenda();
        rellenarListaSubtipos();

        spinnerTipo
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        if (estilo == 1 && position > 2) {
                            idTipo = position + 1;
                        } else {
                            idTipo = position;
                        }

                        rellenarListaSubtipos();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    public void rellenarListaSubtipos() {
        ArrayList<Subtipo> tipos = new ArrayList<Subtipo>();

        tipos = obtenerTipos();
        ListAdapterTipos listAdapter = new ListAdapterTipos(
                getActivity(), tipos);
        listTipoView.setAdapter(listAdapter);

    }

    public ArrayList<Subtipo> obtenerTipos() {
        ArrayList<Subtipo> listTipos = new ArrayList<Subtipo>();
        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            // Recuperamos el listado del spinner subtipos
            listTipos = (ArrayList<Subtipo>) gestion
                    .getAllSubtipos(db, idTipo, estilo);
        }

        return listTipos;
    }

    public void obtenerTiposPrenda() {
        ArrayAdapter<CharSequence> adapterList;
        // rellenamos el spinner tipo prenda
        if (estilo == 1) {
            adapterList = ArrayAdapter.createFromResource(getActivity(),
                    R.array.tiposPrendaArmarioHombre,
                    android.R.layout.simple_spinner_item);
            adapterList.setDropDownViewResource(R.layout.spinner);
            spinnerTipo.setAdapter(adapterList);
        } else {
            adapterList = ArrayAdapter.createFromResource(getActivity(),
                    R.array.tiposPrendaArmario, android.R.layout.simple_spinner_item);
            adapterList.setDropDownViewResource(R.layout.spinner);
            spinnerTipo.setAdapter(adapterList);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        rellenarListaSubtipos();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_setting_mas, menu);
    }

    // Aadiendo funcionalidad a las opciones de men
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent inUti = new Intent(getActivity(),
                        AddTipoActivity.class);
                getActivity().startActivityForResult(inUti, 0);
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ListAdapterTipos extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Subtipo> listaTipos = new ArrayList<Subtipo>();
        private Context context;

        public ListAdapterTipos(Context context, ArrayList<Subtipo> lista) {
            listaTipos = lista;
            mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listaTipos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listaTipos.get(position);
        }

        public int getPositionById(String id) {
            int posi = 0;
            for (int i = 0; i < listaTipos.size(); i++) {
                Subtipo sub = listaTipos.get(i);
                if (String.valueOf(sub.getId()).equals(id)) {
                    posi = i;
                    break;
                }
            }
            return posi;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layoutUtilidad;

            Subtipo sub = listaTipos.get(position);

            String headerTitle = sub.getNombre();

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.lista_tipo, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.textTipos);
            lblListHeader.setText(headerTitle);

            layoutUtilidad = (LinearLayout) convertView.findViewById(R.id.layoutTipo);

            layoutUtilidad.setTag(position);

            layoutUtilidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int posiSel = (int) v.getTag();
                    Subtipo sub = listaTipos.get(posiSel);

                    Intent intent = new Intent(getActivity(), EditTipoActivity.class);
                    intent.putExtra("id", sub.getId());
                    intent.putExtra("textEdit", sub.getNombre());
                    intent.putExtra("idTipo", sub.getIdTipo());
                    intent.putExtra("sexo", sub.getSexo());
                    getActivity().startActivity(intent);
                }
            });

            return convertView;
        }

    }
}
