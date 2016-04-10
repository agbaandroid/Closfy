package com.agba.closfy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.activities.CrearLookPrincipalActivity;
import com.agba.closfy.activities.NuevoAmpliarLookActivity;
import com.agba.closfy.adapters.ListAdapterSpinner;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Utilidad;
import com.agba.closfy.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class NuevoMisLooksFragment extends Fragment {
    private static final String KEY_CONTENT = "MisLooksFragment:Content";
    private String mContent = "???";

    private SQLiteDatabase db;
    private final String BD_NOMBRE = "BDClosfy";
    final GestionBBDD gestion = new GestionBBDD();

    static final int MENSAJE_CONFIRMAR_ELIMINAR = 1;
    static final int EDIT_LOOK = 0;
    static final int AMPLIAR_LOOK = 0;
    private final int LOOK = 1;

    ArrayList<Look> listLooks = new ArrayList<Look>();
    int cuentaSeleccionada;

    private LinearLayout filtros;
    private DrawerLayout drawer;

    private ImageView checkFavoritos;
    int favorito = 0;

    int estilo;

    Look lookSeleccionado = new Look();

    GridView gridview;
    AdView adView;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

	/*private Spinner spinnerUtilidades;
    private Spinner spinnerTemporada;
	int idTemporada = 2;*/

    boolean cargaInicialTemporada = true;
    boolean cargaInicialUtilidad = true;
    boolean cargado = false;

    int posiUtilidad = 0;
    int[] looks;

    ProgressDialog progDailog;

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

        return inflater.inflate(R.layout.nuevo_mis_looks, container, false);
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

        // Look up the AdView as a resource and load a request.
        adView = (AdView) this.getView().findViewById(R.id.adView);

        gridview = (GridView) getView().findViewById(R.id.gridLooks);
        filtros = (LinearLayout) getActivity().findViewById(R.id.right_drawer_looks);
        drawer = (DrawerLayout) getActivity().findViewById(
                R.id.drawer_layout);
		/*spinnerUtilidades = (Spinner) this.getView().findViewById(
				R.id.spinnerUtilidadesLook);

		spinnerTemporada = (Spinner) this.getView().findViewById(
				R.id.spinnerTemporadaLook);

		// Rellenamos el spinner tipo
		obtenerSpinners();
*/
        registerForContextMenu(gridview);

        // Cuenta seleccionada
        prefs = getActivity().getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);
        cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

        checkFavoritos = (ImageView) this.getView().findViewById(
                R.id.checkFavoritos);

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
        }

        if (estilo == 1) {
            cambiarEstiloHombre();
        }

		/*spinnerUtilidades
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						posiUtilidad = position;
						if (!cargaInicialUtilidad) {
							new CargarLooksTask().execute();
						} else {
							cargaInicialUtilidad = false;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		spinnerTemporada
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						idTemporada = position;
						if (!cargaInicialTemporada) {
							new CargarLooksTask().execute();
						} else {
							cargaInicialTemporada = false;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		checkFavoritos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favorito == 0) {
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
				new CargarLooksTask().execute();
			}
		});*/
    }

    public void obtenerSpinners() {
        ArrayList<Utilidad> listUtilidades = new ArrayList<Utilidad>();
        ArrayAdapter<CharSequence> adapterListTemp;

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            // Recuperamos el listado del spinner Categorias
            listUtilidades = (ArrayList<Utilidad>) gestion.getUtilidades(db);
        }
        db.close();

        // Creamos el adaptador
        ListAdapterSpinner spinner_adapterCat = new ListAdapterSpinner(
                getActivity(), android.R.layout.simple_spinner_item,
                listUtilidades);

		/*spinnerUtilidades.setAdapter(spinner_adapterCat);

		adapterListTemp = ArrayAdapter.createFromResource(getActivity(),
				R.array.tiposTemporada, android.R.layout.simple_spinner_item);
		adapterListTemp.setDropDownViewResource(R.layout.spinner);
		spinnerTemporada.setAdapter(adapterListTemp);

		spinnerTemporada.setSelection(2);*/
    }

    public void obtenerLooks() {
        ArrayList<Utilidad> utilidades = new ArrayList<Utilidad>();

        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            utilidades = gestion.getUtilidades(db);

            Utilidad utilidad = utilidades.get(posiUtilidad);

            listLooks = gestion.getLooksFiltros(db, cuentaSeleccionada,
                    2, favorito);

            if (utilidad.getIdUtilidad() != -1) {
                listLooks = Util.filtrarLooksUtilidad(listLooks,
                        utilidad.getIdUtilidad());
            }

        }
        db.close();

        Util.obtenerImagenLook(getActivity(), listLooks, 4);

    }

    public void guardarLookSeleccionado(String idLook) {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        prefs = getActivity().getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("idLook", idLook);
        editor.putBoolean("mostrarMenu", true);
        editor.commit();
    }

    public Look obtenerLookSeleccionado() {
        Look look = new Look();
        SharedPreferences prefs;
        prefs = getActivity().getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);

        String idLook = prefs.getString("idLook", "0");
        db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
        if (db != null) {
            look = gestion.getLookById(db, Integer.parseInt(idLook));
        }
        db.close();
        return look;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                db = getActivity().openOrCreateDatabase(BD_NOMBRE,
                                        1, null);

                                if (db != null) {
                                    ok = gestion.eliminarLook(db,
                                            lookSeleccionado.getIdLook(),
                                            lookSeleccionado.getIdFoto());
                                }
                                db.close();

                                if (ok) {
                                    Context context = getActivity()
                                            .getApplicationContext();
                                    CharSequence text = getResources().getString(
                                            R.string.deleteLookOk);
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text,
                                            duration);
                                    toast.show();

                                    new CargarLooksTask().execute();
                                } else {
                                    Context context = getActivity()
                                            .getApplicationContext();
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

    public class CargarLooksTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(getActivity());
            progDailog.setIndeterminate(false);
            progDailog.setMessage(getResources().getString(R.string.cargando));
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Integer... params) {

            // Recuperamos las prendas
            obtenerLooks();
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            final GridAdapterLooks gridadapter = new GridAdapterLooks(
                    getActivity(), listLooks, estilo);
            gridview.setAdapter(gridadapter);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            progDailog.dismiss();
        }
    }

    public void cambiarEstiloHombre() {
        // spinnerUtilidades.setBackgroundResource(R.drawable.spinner_azul);
        //checkFavoritos.setBackgroundResource(R.drawable.check_estrella_off);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        new CargarLooksTask().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_setting, menu);
    }

    // Aadiendo funcionalidad a las opciones de men
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), CrearLookPrincipalActivity.class);
                startActivityForResult(intent, LOOK);
                return true;
            case R.id.action_filter:
                drawer.openDrawer(filtros);
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GridAdapterLooks extends BaseAdapter implements
            OnClickListener {
        private Context context;
        ArrayList<Look> listaLooks = new ArrayList<Look>();
        int estilo;

        public GridAdapterLooks(Context c, ArrayList<Look> listLooks,
                                int estiloAux) {
            context = c;
            listaLooks = listLooks;
            estilo = estiloAux;
        }

        public int getCount() {
            return listaLooks.size();
        }

        public Object getItem(int position) {
            return listaLooks.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Look look = listaLooks.get(position);
            View v;
            if (convertView == null) { // if it's not recycled, initialize some
                // attributes
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.mis_looks_adapter_nuevo, parent,
                        false);
            } else {
                v = (View) convertView;
            }

            //ImageView opcionesPrenda = (ImageView) v
            //		.findViewById(R.id.opcionesLook);
            //opcionesPrenda.setOnClickListener(this);
            //opcionesPrenda.setTag(position);

            LinearLayout layoutImagenLook = (LinearLayout) v
                    .findViewById(R.id.layoutImagenLook);
            layoutImagenLook.setOnClickListener(this);
            layoutImagenLook.setTag(position);

            ImageView imagenLook = (ImageView) v.findViewById(R.id.imagenLook);
            LinearLayout layoutImage = (LinearLayout) v
                    .findViewById(R.id.layoutImageLook);
            LinearLayout layoutText = (LinearLayout) v
                    .findViewById(R.id.layoutTextLook);
            if (look.getFoto() != null) {
                imagenLook.setBackgroundDrawable(look.getFoto());
                layoutImage.setVisibility(View.VISIBLE);
                layoutText.setVisibility(View.GONE);
            } else {
                layoutImage.setVisibility(View.GONE);
                layoutText.setVisibility(View.VISIBLE);
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            Look look = listLooks.get(position);
            guardarLookSeleccionado(String.valueOf(look.getIdLook()));

            switch (v.getId()) {
                case R.id.layoutImagenLook:
                    Intent intent = new Intent(getActivity(),
                            NuevoAmpliarLookActivity.class);
                    intent.putExtra("idLook", look.getIdLook());
                    intent.putExtra("looks", obtenerCadenaLooks());
                    intent.putExtra("posicion", position);
                    startActivityForResult(intent, AMPLIAR_LOOK);
                    break;
            }
        }
    }

    public int[] obtenerCadenaLooks() {
        looks = new int[listLooks.size()];

        for (int i = 0; i < listLooks.size(); i++) {
            Look look = listLooks.get(i);
            looks[i] = look.getIdLook();
        }

        return looks;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (!cargado) {
            cargado = true;
            new CargarLooksTask().execute();
        }
    }
}
