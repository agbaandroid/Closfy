package com.agba.closfy.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DropBoxFragment extends Fragment {
    private static final String KEY_CONTENT = "DropBoxFragment:Content";
    private String mContent = "???";

    final static private String APP_KEY = "yty5ua0nmg6nv9a";
    final static private String APP_SECRET = "sn345hty9ytw7v3";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    SharedPreferences prefs;
    ProgressDialog progDailog;

    TextView fecha;
    TextView nPrendas;
    TextView nLooks;
    TextView crear;
    TextView restaurar;
    TextView actualizar;

    LinearLayout layoutBackup;
    LinearLayout layoutNoBackup;

    String fechaString;

    int numPrendas;
    int numLooks;

    boolean hayBackup;
    boolean isSinPublicidad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isSinPublicidad = bundle.getBoolean("isSinPublicidad");
        }

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dropbox, container, false);
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

        fecha = (TextView) getView().findViewById(R.id.fecha);
        nPrendas = (TextView) getView().findViewById(R.id.nPrendas);
        nLooks = (TextView) getView().findViewById(R.id.nLooks);
        crear = (TextView) getView().findViewById(R.id.botonCrear);
        restaurar = (TextView) getView().findViewById(R.id.botonRestaurar);
        actualizar = (TextView) getView().findViewById(R.id.botonActualizar);
        layoutBackup = (LinearLayout) getView().findViewById(R.id.layoutBackup);
        layoutNoBackup = (LinearLayout) getView().findViewById(R.id.layoutNoBackup);

        RelativeLayout layoutPubli = (RelativeLayout) getView().findViewById(R.id.layoutPubli);
        if (isSinPublicidad) {
            layoutPubli.setVisibility(View.GONE);
        } else {
            AdView adView = (AdView) getActivity().findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        prefs = getActivity().getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
        String token = prefs.getString("dropboxToken", null);

        //cerrar sesion DropBox
        //mDBApi.getSession().unlink();

        if (token != null) {
            mDBApi.getSession().setOAuth2AccessToken(token);

            if (mDBApi.getSession().isLinked()) {
                new CargarInfoTask().execute();
            }
        }

    }

    public void cargarInfo() {
        String path = "";

        try {
            DropboxAPI.Entry entries = mDBApi.metadata(path, 0, null, true, null);

            if (entries.contents.size() != 0) {
                hayBackup = true;

                for (DropboxAPI.Entry e : entries.contents) {
                    if (!e.isDeleted) {
                        fechaString = e.modified;
                        String pathDir = "/" + e.fileName() + "/";
                        DropboxAPI.Entry entriesDir = mDBApi.metadata(pathDir, 0, null, true, null);

                        if (e.fileName().equals("Prendas")) {
                            numPrendas = entriesDir.contents.size();
                        } else if(e.fileName().equals("Looks")) {
                            numLooks = entriesDir.contents.size();
                        }
                    }
                }
            }

        } catch (DropboxServerException dse) {
            if (dse.error != DropboxServerException._404_NOT_FOUND) {
                Log.d("Error", "Error");
            }
        } catch (DropboxException e) {
            Log.d("Error", "Error");
        }
    }

    public class CargarInfoTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(getActivity());
            progDailog.setIndeterminate(false);
            progDailog.setMessage("Cargando...");
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Integer... params) {

            // Recuperamos las prendas
            //subirFichero();
            //subirFoto();
            cargarInfo();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {

            if (hayBackup) {
                nPrendas.setText(String.valueOf(numPrendas));
                nLooks.setText(String.valueOf(numLooks));

                try {
                    SimpleDateFormat dfDb = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
                    Date dateDb = dfDb.parse(fechaString);

                    SimpleDateFormat toYours = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaFormateada = toYours.format(dateDb);
                    fecha.setText(fechaFormateada);
                } catch (Exception e) {
                    Log.d("Error", "Error al formatear fecha");
                }

                layoutBackup.setVisibility(View.VISIBLE);
                layoutNoBackup.setVisibility(View.GONE);
            } else {
                layoutBackup.setVisibility(View.GONE);
                layoutNoBackup.setVisibility(View.VISIBLE);
            }

            progDailog.dismiss();
        }
    }
}
