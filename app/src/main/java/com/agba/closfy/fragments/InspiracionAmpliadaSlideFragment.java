package com.agba.closfy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.util.Util;

import java.util.ArrayList;

public class InspiracionAmpliadaSlideFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private int[] listaInspiraciones;
    private ImageView imagenAmpliada;
    private TextView page;

    SharedPreferences prefs;
    int estilo;
    int cuentaSeleccionada;

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    public static InspiracionAmpliadaSlideFragment create(int pageNumber, int[] listIdsLooks) {
        InspiracionAmpliadaSlideFragment fragment = new InspiracionAmpliadaSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putIntArray("listInspiraciones", listIdsLooks);
        fragment.setArguments(args);
        return fragment;
    }

    public InspiracionAmpliadaSlideFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.look_ampliado_slide, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        listaInspiraciones = getArguments().getIntArray("listInspiraciones");

        page = (TextView) getView().findViewById(R.id.page);
        page.setText(String.valueOf(mPageNumber + 1) + "/" + listaInspiraciones.length);
        imagenAmpliada = (ImageView) getView().findViewById(R.id.imagenLook);

        // Cuenta seleccionada
        prefs = getActivity().getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        Bitmap inspiracion = Util.obtenerImagenInspiracion(getActivity(), getPageNumber(), estilo);

        Bitmap prenda = Util.obtenerImagenInspiracion(getActivity(), mPageNumber, estilo);
        imagenAmpliada.setImageBitmap(prenda);
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
