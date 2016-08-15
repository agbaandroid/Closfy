package com.agba.closfy.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.agba.closfy.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DropBoxFragment extends Fragment {
    private static final String KEY_CONTENT = "DropBoxFragment:Content";
    private String mContent = "???";

    final static private String APP_KEY = "9ohsxc6aw21nt47";
    final static private String APP_SECRET = "obcmc8t02hu3ao0";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    SharedPreferences prefs;
    ProgressDialog progDailog;

    TextView fecha;
    TextView nPrendas;
    TextView nLooks;
    TextView crear;
    TextView restaurar;
    TextView actualizar;
    TextView cerrarSesion;
    TextView cerrarSesion2;

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
        cerrarSesion = (TextView) getView().findViewById(R.id.botonCerrarSesion);
        cerrarSesion2 = (TextView) getView().findViewById(R.id.botonCerrarSesion2);
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

        if (token != null) {
            mDBApi.getSession().setOAuth2AccessToken(token);

            if (mDBApi.getSession().isLinked()) {
                new CargarInfoTask().execute();
            }
        }

        crear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new CrearCopiaSeguridadTask().execute();
            }
        });

        restaurar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new RestaurarCopiaSeguridadTask().execute();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ActualizarCopiaSeguridadTask().execute();
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new CerrarSesionTask().execute();
            }
        });

        cerrarSesion2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new CerrarSesionTask().execute();
            }
        });
    }

    public void cerrarSesion() {
        mDBApi.getSession().unlink();
    }

    public void crearDirectorios() {
        try {
            DropboxAPI.Entry response;

            //Creamos los directorios
            response = mDBApi.createFolder("Prendas");
            response = mDBApi.createFolder("Looks");
        } catch (Exception e) {
            Log.i("Error", e.getMessage());

            showToast("Se ha producido un error.Inténtelo más tarde.");
        }
    }

    public void subirPrendas() {
        try {
            DropboxAPI.Entry response;

            //Subimos las imagenes de prendas
            File dirPrendas = new File(Environment.getExternalStorageDirectory() + "/Closfy/Prendas/");
            //Creo el array de tipo File con el contenido de la carpeta
            File[] files = dirPrendas.listFiles();

            for (File file : files) {
                File image = new File(dirPrendas, file.getName());

                FileInputStream inputStream = new FileInputStream(file);
                response = mDBApi.putFileOverwrite("/Prendas/" + file.getName(), inputStream,
                        file.length(), null);
            }
        } catch (Exception e) {
            Log.i("Error", e.getMessage());

            showToast("Se ha producido un error.Inténtelo más tarde.");
        }
    }

    public void subirLooks() {
        try {
            DropboxAPI.Entry response;

            //Subimos las imagenes de looks
            File dirLooks = new File(Environment.getExternalStorageDirectory() + "/Closfy/Looks/");
            //Creo el array de tipo File con el contenido de la carpeta
            File[] files = dirLooks.listFiles();

            for (File file : files) {
                File image = new File(dirLooks, file.getName());

                FileInputStream inputStream = new FileInputStream(file);
                response = mDBApi.putFileOverwrite("/Looks/" + file.getName(), inputStream,
                        file.length(), null);
            }
        } catch (Exception e) {
            Log.i("Error", e.getMessage());

            showToast("Se ha producido un error.Inténtelo más tarde.");
        }
    }

    public void subirBD() {
        try {
            DropboxAPI.Entry response;

            //Subimos la base de datos
            File dbFile = new File(Environment.getDataDirectory()
                    + "/data/com.agba.closfy/databases/BDClosfy");

            FileInputStream inputStream = new FileInputStream(dbFile);
            response = mDBApi.putFileOverwrite(dbFile.getName(), inputStream,
                    dbFile.length(), null);

        } catch (Exception e) {
            Log.i("Error", e.getMessage());

            showToast("Se ha producido un error.Inténtelo más tarde.");
        }
    }

    public void crearCopia() {
        try {
            crearDirectorios();
            subirPrendas();
            subirLooks();
            subirBD();
        } catch (Exception e) {
            Log.i("Error", e.getMessage());
        }
    }

    public void actualizarCopia() {
        try {
            subirPrendas();
            subirLooks();
            subirBD();
        } catch (Exception e) {
            Log.i("Error", e.getMessage());
        }
    }

    public void restaurarCopia() {
        try {
            DropboxAPI.Entry response;

            //Se borran todas las imagenes del dispositivo
            File dirClosfy = new File(Environment.getExternalStorageDirectory() + "/Closfy/");

            if(!dirClosfy.exists()){
                dirClosfy.mkdir();
            }

            //Se borran todas las imagenes del dispositivo
            File dirPrendas = new File(Environment.getExternalStorageDirectory() + "/Closfy/Prendas/");

            if (dirPrendas.exists()) {
                //Creo el array de tipo File con el contenido de la carpeta
                File[] filesPrendas = dirPrendas.listFiles();

                for (File file : filesPrendas) {
                    File image = new File(dirPrendas, file.getName());

                    if (image.exists()) {
                        image.delete();
                    }
                }
            } else {
                dirPrendas.mkdir();
            }

            File dirLooks = new File(Environment.getExternalStorageDirectory() + "/Closfy/Looks/");

            if (dirLooks.exists()) {
                //Creo el array de tipo File con el contenido de la carpeta
                File[] filesLooks = dirPrendas.listFiles();

                for (File file : filesLooks) {
                    File image = new File(dirLooks, file.getName());

                    if (image.exists()) {
                        image.delete();
                    }
                }
            } else {
                dirLooks.mkdir();
            }
            //Se descargan todas las imágenes de Dropbox
            String path = "";

            path = "/Prendas/";
            DropboxAPI.Entry entries = mDBApi.metadata(path, 0, null, true, null);

            if (entries.contents.size() != 0) {
                for (DropboxAPI.Entry e : entries.contents) {
                    if (!e.isDeleted) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Closfy/Prendas/" + e.fileName());
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/Prendas/" + e.fileName(), null, out, null);
                    }
                }
            }

            path = "/Looks/";
            entries = mDBApi.metadata(path, 0, null, true, null);

            if (entries.contents.size() != 0) {
                for (DropboxAPI.Entry e : entries.contents) {
                    if (!e.isDeleted) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Closfy/Looks/" + e.fileName());
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/Looks/" + e.fileName(), null, out, null);
                    }
                }
            }

            path = "";
            entries = mDBApi.metadata(path, 0, null, true, null);

            if (entries.contents.size() != 0) {
                File dbFile = new File(Environment.getExternalStorageDirectory()
                        + "/Closfy/BDClosfyTmp");

                OutputStream out = new BufferedOutputStream(new FileOutputStream(dbFile));
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/BDClosfy", null, out, null);

                File dataBaseDir = new File(Environment.getDataDirectory()
                        + "/data/com.agba.closfy/databases/");

                File file = new File(dataBaseDir, "BDClosfy");

                file.createNewFile();
                copyFile(dbFile, file);

                dbFile.delete();
            }


        } catch (Exception e) {
            Log.i("Error", e.getMessage());

            showToast("Se ha producido un error.Inténtelo más tarde.");
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
                        } else if (e.fileName().equals("Looks")) {
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

    public class CrearCopiaSeguridadTask extends AsyncTask<Integer, Void, Void> {

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
            crearCopia();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            progDailog.dismiss();
            new CargarInfoTask().execute();
        }
    }

    public class ActualizarCopiaSeguridadTask extends AsyncTask<Integer, Void, Void> {

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
            actualizarCopia();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            progDailog.dismiss();
            new CargarInfoTask().execute();
        }
    }

    public class RestaurarCopiaSeguridadTask extends AsyncTask<Integer, Void, Void> {

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
            restaurarCopia();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            progDailog.dismiss();
            new CargarInfoTask().execute();
        }
    }

    public class CerrarSesionTask extends AsyncTask<Integer, Void, Void> {

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
            cerrarSesion();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("dropboxToken", null);
            editor.commit();
            Bundle bundle = new Bundle();

            bundle.putBoolean("isSinPublicidad", isSinPublicidad);

            Fragment fragment = new DropBoxInicioFragment();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            progDailog.dismiss();
        }
    }

    void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
        error.show();
    }
}
