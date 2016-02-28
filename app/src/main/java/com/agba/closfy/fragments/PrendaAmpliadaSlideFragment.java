package com.agba.closfy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;

public class PrendaAmpliadaSlideFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private int[] listaPrendas;
    private ImageView imagenAmpliada;
    private TextView page;

    SharedPreferences prefs;
    int estilo;
    int cuentaSeleccionada;

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    public static PrendaAmpliadaSlideFragment create(int pageNumber, int[] listIdsPrendas) {
        PrendaAmpliadaSlideFragment fragment = new PrendaAmpliadaSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putIntArray("listPrendas", listIdsPrendas);
        fragment.setArguments(args);
        return fragment;
    }

    public PrendaAmpliadaSlideFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prenda_ampliada_slide, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        listaPrendas = getArguments().getIntArray("listPrendas");

        page = (TextView) getView().findViewById(R.id.page);
        page.setText(String.valueOf(mPageNumber + 1) + "/" + listaPrendas.length);
        imagenAmpliada = (ImageView) getView().findViewById(R.id.prendaAmpliada);

        // Cuenta seleccionada
        prefs = getActivity().getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        Prenda prenda = new Prenda();
        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            prenda = gestion.getPrendaById(db, listaPrendas[mPageNumber]);
        }
        db.close();

        prenda = Util.obtenerImagenesPrendas(getActivity(), prenda, 0, estilo);

        imagenAmpliada.setImageDrawable(prenda.getFoto());
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
