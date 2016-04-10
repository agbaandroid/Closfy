package com.agba.closfy.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.activities.CrearLookPrincipalActivity;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class CrearLookFragmentHombre extends Fragment {
    private static final String KEY_CONTENT = "CrearLookFragment:Content";
    private String mContent = "???";

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();


    SharedPreferences prefs;

    int estilo;

    ProgressDialog progDailog;

    int idRadioTemporada;
    ArrayList<Integer> listIdsUtilidad;
    int favorito;

    int cuentaSeleccionada;

    ListAdapterLooks adapterSup;
    ListAdapterLooks adapterInf;
    ListAdapterLooks adapterCuerpo;
    ListAdapterLooks adapterAbrigo;
    ListAdapterLooks adapterCalzado;
    ListAdapterLooks adapterComplemento;

    ArrayList<Prenda> listPrendasSup = new ArrayList<Prenda>();
    ArrayList<Prenda> listPrendasInf = new ArrayList<Prenda>();
    ArrayList<Prenda> listPrendasCuerpo = new ArrayList<Prenda>();
    ArrayList<Prenda> listPrendasAbrigo = new ArrayList<Prenda>();
    ArrayList<Prenda> listPrendasCalzado = new ArrayList<Prenda>();
    ArrayList<Prenda> listPrendasComplemento = new ArrayList<Prenda>();

    static TwoWayView listPrendaSup = null;
    static TwoWayView listPrendaInf = null;
    static TwoWayView listPrendaCuerpo = null;
    static TwoWayView listPrendaAbrigo = null;
    static TwoWayView listPrendaCalzado = null;
    static TwoWayView listPrendaComplemento = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }

    }

    public CrearLookFragmentHombre(int temporada, ArrayList<Integer> listIds, int favoritoSelec) {
        idRadioTemporada = temporada;
        listIdsUtilidad = listIds;
        favorito = favoritoSelec;

    }

    public CrearLookFragmentHombre() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.crear_look_hombre, container, false);
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

        ((CrearLookPrincipalActivity) getActivity()).cambiarActionBar(2);

        // Cuenta seleccionada
        prefs = getActivity().getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        new CargarPrendasTask().execute();

    }

    public void mostrarMensaje(String texto) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = (texto);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void inicializar() {
        ((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.clear();

        adapterSup = new ListAdapterLooks(getActivity(), listPrendasSup, 0);
        listPrendaSup.setAdapter(adapterSup);

        adapterInf = new ListAdapterLooks(getActivity(), listPrendasInf, 1);
        listPrendaInf.setAdapter(adapterInf);

        adapterCuerpo = new ListAdapterLooks(getActivity(), listPrendasCuerpo,
                2);
        listPrendaCuerpo.setAdapter(adapterCuerpo);

        adapterAbrigo = new ListAdapterLooks(getActivity(), listPrendasAbrigo,
                3);
        listPrendaAbrigo.setAdapter(adapterAbrigo);

        adapterCalzado = new ListAdapterLooks(getActivity(),
                listPrendasCalzado, 4);
        listPrendaCalzado.setAdapter(adapterCalzado);

        adapterComplemento = new ListAdapterLooks(getActivity(),
                listPrendasComplemento, 5);
        listPrendaComplemento.setAdapter(adapterComplemento);
    }

    public void obtenerPrendas() {
        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {

            if (favorito == 1) {
                listPrendasSup = gestion.getPrendasFavoritasByIdTipo(db, 1,
                        cuentaSeleccionada);
                listPrendasInf = gestion.getPrendasFavoritasByIdTipo(db, 2,
                        cuentaSeleccionada);
                listPrendasCuerpo = gestion.getPrendasFavoritasByIdTipo(db, 3,
                        cuentaSeleccionada);
                listPrendasAbrigo = gestion.getPrendasFavoritasByIdTipo(db, 4,
                        cuentaSeleccionada);
                listPrendasCalzado = gestion.getPrendasFavoritasByIdTipo(db, 5,
                        cuentaSeleccionada);
                listPrendasComplemento = gestion.getPrendasFavoritasByIdTipo(
                        db, 6, cuentaSeleccionada);
            } else {
                listPrendasSup = gestion.getPrendasByIdTipo(db, 1,
                        cuentaSeleccionada);
                listPrendasInf = gestion.getPrendasByIdTipo(db, 2,
                        cuentaSeleccionada);
                listPrendasCuerpo = gestion.getPrendasByIdTipo(db, 3,
                        cuentaSeleccionada);
                listPrendasAbrigo = gestion.getPrendasByIdTipo(db, 4,
                        cuentaSeleccionada);
                listPrendasCalzado = gestion.getPrendasByIdTipo(db, 5,
                        cuentaSeleccionada);
                listPrendasComplemento = gestion.getPrendasByIdTipo(db, 6,
                        cuentaSeleccionada);
            }

            listPrendasSup = Util.filtrarPrendas(listPrendasSup,
                    idRadioTemporada, listIdsUtilidad);
            listPrendasInf = Util.filtrarPrendas(listPrendasInf,
                    idRadioTemporada, listIdsUtilidad);
            listPrendasAbrigo = Util.filtrarPrendas(listPrendasAbrigo,
                    idRadioTemporada, listIdsUtilidad);
            listPrendasCuerpo = Util.filtrarPrendas(listPrendasCuerpo,
                    idRadioTemporada, listIdsUtilidad);
            listPrendasComplemento = Util.filtrarPrendas(
                    listPrendasComplemento, idRadioTemporada, listIdsUtilidad);
            listPrendasCalzado = Util.filtrarPrendas(listPrendasCalzado,
                    idRadioTemporada, listIdsUtilidad);
        }

        db.close();

        listPrendasSup = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasSup, 2, estilo);
        listPrendasInf = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasInf, 2, estilo);
        listPrendasCuerpo = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasCuerpo, 2, estilo);
        listPrendasAbrigo = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasAbrigo, 2, estilo);
        listPrendasCalzado = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasCalzado, 2, estilo);
        listPrendasComplemento = Util.obtenerImagenesPrendas(getActivity(),
                listPrendasComplemento, 2, estilo);
    }

    public class ListAdapterLooks extends BaseAdapter implements
            OnClickListener {
        private LayoutInflater mInflater;
        private ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
        private LinearLayout layoutPrenda;
        View viewAnterior;
        int tipoPrenda;

        public ListAdapterLooks(Context context, ArrayList<Prenda> lista,
                                int tipoPrendaAux) {
            listaPrendas = lista;
            mInflater = LayoutInflater.from(context);
            tipoPrenda = tipoPrendaAux;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listaPrendas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listaPrendas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Prenda prenda = listaPrendas.get(position);
            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.lista_prendas_look, null);
            } else {
                v = convertView;
            }

            layoutPrenda = (LinearLayout) v.findViewById(R.id.layoutPrenda);
            layoutPrenda.setOnClickListener(this);

            ImageView imagenPrenda = (ImageView) v
                    .findViewById(R.id.imagePrendaLook);
            imagenPrenda.setImageDrawable(prenda.getFoto());

            ImageView imagenCheck = (ImageView) v.findViewById(R.id.imagecheck);

            if (((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.contains(prenda.getIdPrenda())) {
                if (estilo == 1) {
                    imagenCheck.setBackgroundResource(R.drawable.tic_azul);
                } else {
                    imagenCheck.setBackgroundResource(R.drawable.tic);
                }
            } else {
                imagenCheck.setBackgroundResource(android.R.color.transparent);
            }

            v.setTag(prenda.getIdPrenda());
            return v;
        }

        // Al pulsar un dia del calendario
        @Override
        public void onClick(View view) {
            int idPrenda = (Integer) view.getTag();

            if (((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.contains(idPrenda)) {
                int posi = ((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.indexOf(idPrenda);
                ((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.remove(posi);
            } else {
                ((CrearLookPrincipalActivity) getActivity()).listaPrendasSeleccionadas.add(idPrenda);
            }

            adapterSup.notifyDataSetChanged();
            adapterInf.notifyDataSetChanged();
            adapterCuerpo.notifyDataSetChanged();
            adapterAbrigo.notifyDataSetChanged();
            adapterCalzado.notifyDataSetChanged();
            adapterComplemento.notifyDataSetChanged();
        }

    }

    public class CargarPrendasTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(getActivity());
            progDailog.setIndeterminate(false);
            progDailog.setMessage("Cargando ...");
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Integer... params) {

            obtenerPrendas();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            adapterSup = new ListAdapterLooks(getActivity(), listPrendasSup, 0);
            adapterInf = new ListAdapterLooks(getActivity(), listPrendasInf, 1);
            adapterCuerpo = new ListAdapterLooks(getActivity(),
                    listPrendasCuerpo, 2);
            adapterAbrigo = new ListAdapterLooks(getActivity(),
                    listPrendasAbrigo, 3);
            adapterCalzado = new ListAdapterLooks(getActivity(),
                    listPrendasCalzado, 4);
            adapterComplemento = new ListAdapterLooks(getActivity(),
                    listPrendasComplemento, 5);

            listPrendaSup = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaSup);
            listPrendaSup.setAdapter(adapterSup);

            listPrendaInf = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaInf);
            listPrendaInf.setAdapter(adapterInf);

            listPrendaCuerpo = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaCuerpo);
            listPrendaCuerpo.setAdapter(adapterCuerpo);

            listPrendaAbrigo = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaAbrigo);
            listPrendaAbrigo.setAdapter(adapterAbrigo);

            listPrendaCalzado = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaCalzado);
            listPrendaCalzado.setAdapter(adapterCalzado);

            listPrendaComplemento = (TwoWayView) getView().findViewById(
                    R.id.listViewPrendaComp);
            listPrendaComplemento.setAdapter(adapterComplemento);

            progDailog.dismiss();
        }
    }
}
