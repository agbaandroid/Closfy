package com.agba.closfy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Icon;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.modelo.PrendaLook;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    final static GestionBBDD gestion = new GestionBBDD();
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/*public static ArrayList<Prenda> obtenerImagenesPrendas(Context context,
            ArrayList<Prenda> listaPrendas, int tamanioImagen, int estilo) {
		ArrayList<Prenda> listaPrendaDefinitiva = new ArrayList<Prenda>();

		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = tamanioImagen;

		for (Prenda prenda : listaPrendas) {
			if (prenda.getIdFoto() != null && !prenda.getIdFoto().equals("")) {
				String filePath = Environment.getExternalStorageDirectory()
						+ "/Closfy/Prendas/" + prenda.getIdFoto();

				if (tamanioImagen == 2) {
					options.inSampleSize = 6;
				}

				bitmap = BitmapFactory.decodeFile(filePath, options);
				if (bitmap != null) {
					Drawable drawable = new BitmapDrawable(
							context.getResources(), bitmap);
					prenda.setFoto(drawable);
					listaPrendaDefinitiva.add(prenda);
				}
			} else if (prenda.getPrendaBasica() == 1) {
				bitmap = Util.obtenerImagenPrendaBasica(context,
						prenda.getIdTipo(), prenda.getIdPrendaBasica(),
						tamanioImagen, estilo);
				Drawable drawable = new BitmapDrawable(context.getResources(),
						bitmap);
				prenda.setFoto(drawable);
				listaPrendaDefinitiva.add(prenda);
			}

			// if(bitmap != null){
			// bitmap.recycle();
			// bitmap = null;
			// }
		}
		return listaPrendaDefinitiva;
	}*/

    public static void obtenerImagenLook(Context context,
                                         ArrayList<Look> listaLooks, int tamanioImagen) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = tamanioImagen;

        Bitmap bitmap = null;
        Drawable foto = null;

        for (Look look : listaLooks) {
            if (look.getIdFoto() != null && !look.getIdFoto().equals("")) {
                String filePath = Environment.getExternalStorageDirectory()
                        + "/Closfy/Looks/" + look.getIdFoto();
                bitmap = BitmapFactory.decodeFile(filePath, options);
                if (bitmap != null) {
                    foto = new BitmapDrawable(context.getResources(), bitmap);
                    look.setFoto(foto);
                }
            }
        }
    }

	/*public static Prenda obtenerImagenesPrendas(Context context, Prenda prenda,
            int tamanioImagen, int estilo) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = tamanioImagen;

		if (prenda.getIdFoto() != null && !prenda.getIdFoto().equals("")) {
			String filePath = Environment.getExternalStorageDirectory()
					+ "/Closfy/Prendas/" + prenda.getIdFoto();
			
			if (tamanioImagen == 2) {
				options.inSampleSize = 6;
			}
			
			bitmap = BitmapFactory.decodeFile(filePath, options);
			if (bitmap != null) {
				Drawable drawable = new BitmapDrawable(context.getResources(),
						bitmap);
				prenda.setFoto(drawable);
			}
		} else if (prenda.getPrendaBasica() == 1) {
			bitmap = Util.obtenerImagenPrendaBasica(context,
					prenda.getIdTipo(), prenda.getIdPrendaBasica(),
					tamanioImagen, estilo);
			Drawable drawable = new BitmapDrawable(context.getResources(),
					bitmap);
			prenda.setFoto(drawable);
		}

		// if(bitmap != null){
		// bitmap.recycle();
		// bitmap = null;
		// }

		return prenda;
	}*/

    public static Bitmap obtenerPrendaBitmap(Context context, Prenda prenda,
                                             int tamanioImagen, int estilo) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = tamanioImagen;

        if (prenda.getIdFoto() != null && !prenda.getIdFoto().equals("")) {
            String filePath = Environment.getExternalStorageDirectory()
                    + "/Closfy/Prendas/" + prenda.getIdFoto();

            if (tamanioImagen == 2) {
                options.inSampleSize = 6;
            }

            bitmap = BitmapFactory.decodeFile(filePath, options);
        } else if (prenda.getPrendaBasica() == 1) {
            int image = Util.obtenerImagenPrendaBasica(context,
                    prenda.getIdTipo(), prenda.getIdPrendaBasica(),
                    tamanioImagen, estilo);

            bitmap = BitmapFactory.decodeResource(
                    context.getResources(),
                    image, options);
        }

        return bitmap;
    }

	public static Bitmap obtenerPrendaLookBitmap(Context context,
			PrendaLook prenda, int tamanioImagen, int estilo) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = tamanioImagen;

		if (prenda.getIdFoto() != null && !prenda.getIdFoto().equals("")) {
			String filePath = Environment.getExternalStorageDirectory()
					+ "/Closfy/Prendas/" + prenda.getIdFoto();
			
			if (tamanioImagen == 2) {
				options.inSampleSize = 6;
			}
			
			bitmap = BitmapFactory.decodeFile(filePath, options);
		} else if (prenda.getPrendaBasica() == 1) {
			int image = Util.obtenerImagenPrendaBasica(context,
					prenda.getIdTipo(), prenda.getIdPrendaBasica(),
					tamanioImagen, estilo);

            bitmap = BitmapFactory.decodeResource(
                    context.getResources(),
                    image, options);
		}

		return bitmap;
	}

	/*public static void obtenerImagenesLooks(Context context, SQLiteDatabase db,
			ArrayList<Look> listaLooks, int tamanioImagen, int estilo) {
		String BD_NOMBRE = "BDClosfy";
		String[] idPrendas = new String[7];

		for (Look look : listaLooks) {
			if (look.getIdFoto() != null && !look.getIdFoto().equals("")) {
				break;
			} else {
				StringTokenizer st = new StringTokenizer(
						look.getCadenaPrendas(), ";");

				int cont = 0;
				while (st.hasMoreTokens()) {
					String idPrenda = st.nextToken();
					idPrendas[cont] = idPrenda;
					cont++;
				}

				db = context.openOrCreateDatabase(BD_NOMBRE, 1, null);

				if (!idPrendas[0].equals("-1")) {
					Prenda prendaSup = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[0]));
					prendaSup = Util.obtenerImagenesPrendas(context, prendaSup,
							tamanioImagen, estilo);
					look.setPrendaSup(prendaSup);
				}
				if (!idPrendas[1].equals("-1")) {
					Prenda prendaInf = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[1]));
					prendaInf = Util.obtenerImagenesPrendas(context, prendaInf,
							tamanioImagen, estilo);
					look.setPrendaInf(prendaInf);
				}
				if (!idPrendas[2].equals("-1")) {
					Prenda prendaCuerpo = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[2]));
					prendaCuerpo = Util.obtenerImagenesPrendas(context,
							prendaCuerpo, tamanioImagen, estilo);
					look.setPrendaCuerpo(prendaCuerpo);
				}
				if (!idPrendas[3].equals("-1")) {
					Prenda prendaAbrigo = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[3]));
					prendaAbrigo = Util.obtenerImagenesPrendas(context,
							prendaAbrigo, tamanioImagen, estilo);
					look.setPrendaAbrigo(prendaAbrigo);
				}
				if (!idPrendas[4].equals("-1")) {
					Prenda prendaCalzado = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[4]));
					prendaCalzado = Util.obtenerImagenesPrendas(context,
							prendaCalzado, tamanioImagen, estilo);
					look.setPrendaCalzado(prendaCalzado);
				}
				if (!idPrendas[5].equals("-1")) {
					Prenda prendaCompl1 = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[5]));
					prendaCompl1 = Util.obtenerImagenesPrendas(context,
							prendaCompl1, tamanioImagen, estilo);
					look.setPrendaCompl1(prendaCompl1);
				}
				if (!idPrendas[6].equals("-1")) {
					Prenda prendaCompl2 = gestion.getPrendaById(db,
							Integer.parseInt(idPrendas[6]));
					prendaCompl2 = Util.obtenerImagenesPrendas(context,
							prendaCompl2, tamanioImagen, estilo);
					look.setPrendaCompl2(prendaCompl2);
				}
			}

			db.close();
		}
	}*/

    public static int cuentaSeleccionada(Context context,
                                         SharedPreferences prefs) {
        prefs = context.getSharedPreferences("ficheroConf",
                Context.MODE_PRIVATE);

        int idCuenta = prefs.getInt("cuenta", 0);
        return idCuenta;
    }

    public static String obtenerCadenaUtilidades(
            ArrayList<Integer> listaUtilidades) {
        String cadena = "";
        for (int i = 0; i < listaUtilidades.size(); i++) {
            cadena = cadena + String.valueOf(listaUtilidades.get(i)) + ";";
        }
        cadena = cadena.substring(0, cadena.length() - 1);
        return cadena;
    }

    public static ArrayList<Integer> obtenerListaUtilidades(String lista) {
        ArrayList<Integer> listIdUtilidades = new ArrayList<Integer>();
        String[] listAux = lista.split(";");

        for (int i = 0; i < listAux.length; i++) {
            listIdUtilidades.add(Integer.parseInt(listAux[i]));
        }
        return listIdUtilidades;
    }

    // Filtramos las prendas por temporadas
    public static ArrayList<Prenda> filtrarPrendas(
            ArrayList<Prenda> listaPrendas, int idRadioTemporada,
            ArrayList<Integer> listIdsUtilidad) {
        ArrayList<Prenda> listPrendasTemporadas = new ArrayList<>();
        ArrayList<Prenda> listPrendasDefinitiva = new ArrayList<>();

        for (Prenda prenda : listaPrendas) {
            if (idRadioTemporada == 2) {
                listPrendasTemporadas.add(prenda);
            } else if (prenda.getIdTemporada() == 2
                    || prenda.getIdTemporada() == idRadioTemporada) {
                listPrendasTemporadas.add(prenda);
            }
        }

        // Filtramos las prendas por utilidades
        for (Prenda prenda : listPrendasTemporadas) {
            String[] utilidades = prenda.getUtilidades().split(";");
            for (int j = 0; j < utilidades.length; j++) {
                if (utilidades != null
                        && utilidades.length > 0
                        && (Integer.parseInt(utilidades[j]) == -1
                        || listIdsUtilidad.contains(-1) || listIdsUtilidad
                        .contains(Integer.parseInt(utilidades[j])))) {
                    listPrendasDefinitiva.add(prenda);
                    break;
                }
            }
        }

        return listPrendasDefinitiva;
    }

    public static ArrayList<Look> filtrarLooksUtilidad(
            ArrayList<Look> listaLooks, int utilidad) {
        ArrayList<Look> listLooksDefinitiva = new ArrayList<Look>();

        for (Look look : listaLooks) {
            String[] utilidadesLook = look.getUtilidades().split(";");
            for (int j = 0; j < utilidadesLook.length; j++) {
                if (Integer.parseInt(utilidadesLook[j]) == -1
                        || Integer.parseInt(utilidadesLook[j]) == utilidad) {
                    listLooksDefinitiva.add(look);
                    break;
                }
            }

        }
        return listLooksDefinitiva;
    }

    public static ArrayList<Prenda> filtrarPrendasUtilidad(
            ArrayList<Prenda> listaPrendas, int utilidad) {
        ArrayList<Prenda> listPrendasDefinitiva = new ArrayList<Prenda>();

        for (Prenda prenda : listaPrendas) {
            String[] utilidadesPrenda = prenda.getUtilidades().split(";");
            for (int j = 0; j < utilidadesPrenda.length; j++) {
                if (Integer.parseInt(utilidadesPrenda[j]) == -1
                        || Integer.parseInt(utilidadesPrenda[j]) == utilidad) {
                    listPrendasDefinitiva.add(prenda);
                    break;
                }
            }

        }
        return listPrendasDefinitiva;
    }

    public static ArrayList<Prenda> obtenerPrendasBasicasAbrigo(
            Context context, int tipoPrenda, int estilo) {

        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo_hombre9));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.abrigo7));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
        }

        return listPrendas;
    }

    public static ArrayList<Prenda> obtenerPrendasBasicasCuerpoEntero(
            Context context, int tipoPrenda, int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();
        Prenda prenda = new Prenda();
        prenda.setIdPrenda(0);
        prenda.setFoto(context.getResources().getDrawable(R.drawable.cuerpo1));

        Prenda prenda2 = new Prenda();
        prenda2.setIdPrenda(1);
        prenda2.setFoto(context.getResources().getDrawable(R.drawable.cuerpo2));

        Prenda prenda3 = new Prenda();
        prenda3.setIdPrenda(2);
        prenda3.setFoto(context.getResources().getDrawable(R.drawable.cuerpo3));

        Prenda prenda4 = new Prenda();
        prenda4.setIdPrenda(3);
        prenda4.setFoto(context.getResources().getDrawable(R.drawable.cuerpo4));

        Prenda prenda5 = new Prenda();
        prenda5.setIdPrenda(4);
        prenda5.setFoto(context.getResources().getDrawable(R.drawable.cuerpo5));

        listPrendas.add(prenda);
        listPrendas.add(prenda2);
        listPrendas.add(prenda3);
        listPrendas.add(prenda4);
        listPrendas.add(prenda5);

        return listPrendas;
    }

    public static ArrayList<Prenda> obtenerPrendasBasicasSuperior(
            Context context, int tipoPrenda, int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre11));

            Prenda prenda12 = new Prenda();
            prenda12.setIdPrenda(11);
            prenda12.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre12));

            Prenda prenda13 = new Prenda();
            prenda13.setIdPrenda(12);
            prenda13.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre13));

            Prenda prenda14 = new Prenda();
            prenda14.setIdPrenda(13);
            prenda14.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre14));

            Prenda prenda15 = new Prenda();
            prenda15.setIdPrenda(14);
            prenda15.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre15));

            Prenda prenda16 = new Prenda();
            prenda16.setIdPrenda(15);
            prenda16.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre16));

            Prenda prenda17 = new Prenda();
            prenda17.setIdPrenda(16);
            prenda17.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre17));

            Prenda prenda18 = new Prenda();
            prenda18.setIdPrenda(17);
            prenda18.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre18));

            Prenda prenda19 = new Prenda();
            prenda19.setIdPrenda(18);
            prenda19.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre19));

            Prenda prenda20 = new Prenda();
            prenda20.setIdPrenda(19);
            prenda20.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre20));

            Prenda prenda21 = new Prenda();
            prenda21.setIdPrenda(20);
            prenda21.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre21));

            Prenda prenda22 = new Prenda();
            prenda22.setIdPrenda(21);
            prenda22.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre22));

            Prenda prenda23 = new Prenda();
            prenda23.setIdPrenda(22);
            prenda23.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre23));

            Prenda prenda24 = new Prenda();
            prenda24.setIdPrenda(23);
            prenda24.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre24));

            Prenda prenda25 = new Prenda();
            prenda25.setIdPrenda(24);
            prenda25.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre25));

            Prenda prenda26 = new Prenda();
            prenda26.setIdPrenda(25);
            prenda26.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre26));

            Prenda prenda27 = new Prenda();
            prenda27.setIdPrenda(26);
            prenda27.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre27));

            Prenda prenda28 = new Prenda();
            prenda28.setIdPrenda(27);
            prenda28.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre28));

            Prenda prenda29 = new Prenda();
            prenda29.setIdPrenda(28);
            prenda29.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre29));

            Prenda prenda30 = new Prenda();
            prenda30.setIdPrenda(29);
            prenda30.setFoto(context.getResources().getDrawable(
                    R.drawable.superior_hombre30));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
            listPrendas.add(prenda12);
            listPrendas.add(prenda13);
            listPrendas.add(prenda14);
            listPrendas.add(prenda15);
            listPrendas.add(prenda16);
            listPrendas.add(prenda17);
            listPrendas.add(prenda18);
            listPrendas.add(prenda19);
            listPrendas.add(prenda20);
            listPrendas.add(prenda21);
            listPrendas.add(prenda22);
            listPrendas.add(prenda23);
            listPrendas.add(prenda24);
            listPrendas.add(prenda25);
            listPrendas.add(prenda26);
            listPrendas.add(prenda27);
            listPrendas.add(prenda28);
            listPrendas.add(prenda29);
            listPrendas.add(prenda30);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.superior1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.superior2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.superior3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.superior4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.superior5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.superior6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.superior7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.superior8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.superior9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.superior10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.superior11));

            Prenda prenda12 = new Prenda();
            prenda12.setIdPrenda(11);
            prenda12.setFoto(context.getResources().getDrawable(
                    R.drawable.superior12));

            Prenda prenda13 = new Prenda();
            prenda13.setIdPrenda(12);
            prenda13.setFoto(context.getResources().getDrawable(
                    R.drawable.superior13));

            Prenda prenda14 = new Prenda();
            prenda14.setIdPrenda(13);
            prenda14.setFoto(context.getResources().getDrawable(
                    R.drawable.superior14));

            Prenda prenda15 = new Prenda();
            prenda15.setIdPrenda(14);
            prenda15.setFoto(context.getResources().getDrawable(
                    R.drawable.superior15));

            Prenda prenda16 = new Prenda();
            prenda16.setIdPrenda(15);
            prenda16.setFoto(context.getResources().getDrawable(
                    R.drawable.superior16));

            Prenda prenda17 = new Prenda();
            prenda17.setIdPrenda(16);
            prenda17.setFoto(context.getResources().getDrawable(
                    R.drawable.superior17));

            Prenda prenda18 = new Prenda();
            prenda18.setIdPrenda(17);
            prenda18.setFoto(context.getResources().getDrawable(
                    R.drawable.superior18));

            Prenda prenda19 = new Prenda();
            prenda19.setIdPrenda(18);
            prenda19.setFoto(context.getResources().getDrawable(
                    R.drawable.superior19));

            Prenda prenda20 = new Prenda();
            prenda20.setIdPrenda(19);
            prenda20.setFoto(context.getResources().getDrawable(
                    R.drawable.superior20));

            Prenda prenda21 = new Prenda();
            prenda21.setIdPrenda(20);
            prenda21.setFoto(context.getResources().getDrawable(
                    R.drawable.superior21));

            Prenda prenda22 = new Prenda();
            prenda22.setIdPrenda(21);
            prenda22.setFoto(context.getResources().getDrawable(
                    R.drawable.superior22));

            Prenda prenda23 = new Prenda();
            prenda23.setIdPrenda(22);
            prenda23.setFoto(context.getResources().getDrawable(
                    R.drawable.superior23));

            Prenda prenda24 = new Prenda();
            prenda24.setIdPrenda(23);
            prenda24.setFoto(context.getResources().getDrawable(
                    R.drawable.superior24));

            Prenda prenda25 = new Prenda();
            prenda25.setIdPrenda(24);
            prenda25.setFoto(context.getResources().getDrawable(
                    R.drawable.superior25));

            Prenda prenda26 = new Prenda();
            prenda26.setIdPrenda(25);
            prenda26.setFoto(context.getResources().getDrawable(
                    R.drawable.superior26));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
            listPrendas.add(prenda12);
            listPrendas.add(prenda13);
            listPrendas.add(prenda14);
            listPrendas.add(prenda15);
            listPrendas.add(prenda16);
            listPrendas.add(prenda17);
            listPrendas.add(prenda18);
            listPrendas.add(prenda19);
            listPrendas.add(prenda20);
            listPrendas.add(prenda21);
            listPrendas.add(prenda22);
            listPrendas.add(prenda23);
            listPrendas.add(prenda24);
            listPrendas.add(prenda25);
            listPrendas.add(prenda26);
        }

        return listPrendas;
    }

    public static ArrayList<Prenda> obtenerPrendasBasicasInferior(
            Context context, int tipoPrenda, int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior_hombre10));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior11));

            Prenda prenda12 = new Prenda();
            prenda12.setIdPrenda(11);
            prenda12.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior12));

            Prenda prenda13 = new Prenda();
            prenda13.setIdPrenda(12);
            prenda13.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior13));

            Prenda prenda14 = new Prenda();
            prenda14.setIdPrenda(13);
            prenda14.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior14));

            Prenda prenda15 = new Prenda();
            prenda15.setIdPrenda(14);
            prenda15.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior15));

            Prenda prenda16 = new Prenda();
            prenda16.setIdPrenda(15);
            prenda16.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior16));

            Prenda prenda17 = new Prenda();
            prenda17.setIdPrenda(16);
            prenda17.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior17));

            Prenda prenda18 = new Prenda();
            prenda18.setIdPrenda(17);
            prenda18.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior18));

            Prenda prenda19 = new Prenda();
            prenda19.setIdPrenda(18);
            prenda19.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior19));

            Prenda prenda20 = new Prenda();
            prenda20.setIdPrenda(19);
            prenda20.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior20));

            Prenda prenda21 = new Prenda();
            prenda21.setIdPrenda(20);
            prenda21.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior21));

            Prenda prenda22 = new Prenda();
            prenda22.setIdPrenda(21);
            prenda22.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior22));

            Prenda prenda23 = new Prenda();
            prenda23.setIdPrenda(22);
            prenda23.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior23));

            Prenda prenda24 = new Prenda();
            prenda24.setIdPrenda(23);
            prenda24.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior24));

            Prenda prenda25 = new Prenda();
            prenda25.setIdPrenda(24);
            prenda25.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior25));

            Prenda prenda26 = new Prenda();
            prenda26.setIdPrenda(25);
            prenda26.setFoto(context.getResources().getDrawable(
                    R.drawable.inferior26));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
            listPrendas.add(prenda12);
            listPrendas.add(prenda13);
            listPrendas.add(prenda14);
            listPrendas.add(prenda15);
            listPrendas.add(prenda16);
            listPrendas.add(prenda17);
            listPrendas.add(prenda18);
            listPrendas.add(prenda19);
            listPrendas.add(prenda20);
            listPrendas.add(prenda21);
            listPrendas.add(prenda22);
            listPrendas.add(prenda23);
            listPrendas.add(prenda24);
            listPrendas.add(prenda25);
            listPrendas.add(prenda26);
        }

        return listPrendas;

    }

    public static ArrayList<Prenda> obtenerPrendasBasicasComplemento(
            Context context, int tipoPrenda, int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento_hombre11));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento11));

            Prenda prenda12 = new Prenda();
            prenda12.setIdPrenda(11);
            prenda12.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento12));

            Prenda prenda13 = new Prenda();
            prenda13.setIdPrenda(12);
            prenda13.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento13));

            Prenda prenda14 = new Prenda();
            prenda14.setIdPrenda(13);
            prenda14.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento14));

            Prenda prenda15 = new Prenda();
            prenda15.setIdPrenda(14);
            prenda15.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento15));

            Prenda prenda16 = new Prenda();
            prenda16.setIdPrenda(15);
            prenda16.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento16));

            Prenda prenda17 = new Prenda();
            prenda17.setIdPrenda(16);
            prenda17.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento17));

            Prenda prenda18 = new Prenda();
            prenda18.setIdPrenda(17);
            prenda18.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento18));

            Prenda prenda19 = new Prenda();
            prenda19.setIdPrenda(18);
            prenda19.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento19));

            Prenda prenda20 = new Prenda();
            prenda20.setIdPrenda(19);
            prenda20.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento20));

            Prenda prenda21 = new Prenda();
            prenda21.setIdPrenda(20);
            prenda21.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento21));

            Prenda prenda22 = new Prenda();
            prenda22.setIdPrenda(21);
            prenda22.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento22));

            Prenda prenda23 = new Prenda();
            prenda23.setIdPrenda(22);
            prenda23.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento23));

            Prenda prenda24 = new Prenda();
            prenda24.setIdPrenda(23);
            prenda24.setFoto(context.getResources().getDrawable(
                    R.drawable.complemento24));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
            listPrendas.add(prenda12);
            listPrendas.add(prenda13);
            listPrendas.add(prenda14);
            listPrendas.add(prenda15);
            listPrendas.add(prenda16);
            listPrendas.add(prenda17);
            listPrendas.add(prenda18);
            listPrendas.add(prenda19);
            listPrendas.add(prenda20);
            listPrendas.add(prenda21);
            listPrendas.add(prenda22);
            listPrendas.add(prenda23);
            listPrendas.add(prenda24);
        }

        return listPrendas;
    }

    public static ArrayList<Prenda> obtenerPrendasBasicasCalzado(
            Context context, int tipoPrenda, int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado_hombre11));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado1));

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado2));

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado3));

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado4));

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado5));

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado6));

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado7));

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado8));

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado9));

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado10));

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado11));

            Prenda prenda12 = new Prenda();
            prenda12.setIdPrenda(11);
            prenda12.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado12));

            Prenda prenda13 = new Prenda();
            prenda13.setIdPrenda(12);
            prenda13.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado13));

            Prenda prenda14 = new Prenda();
            prenda14.setIdPrenda(13);
            prenda14.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado14));

            Prenda prenda15 = new Prenda();
            prenda15.setIdPrenda(14);
            prenda15.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado15));

            Prenda prenda16 = new Prenda();
            prenda16.setIdPrenda(15);
            prenda16.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado16));

            Prenda prenda17 = new Prenda();
            prenda17.setIdPrenda(16);
            prenda17.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado17));

            Prenda prenda18 = new Prenda();
            prenda18.setIdPrenda(17);
            prenda18.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado18));

            Prenda prenda19 = new Prenda();
            prenda19.setIdPrenda(18);
            prenda19.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado19));

            Prenda prenda20 = new Prenda();
            prenda20.setIdPrenda(19);
            prenda20.setFoto(context.getResources().getDrawable(
                    R.drawable.calzado20));

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
            listPrendas.add(prenda12);
            listPrendas.add(prenda13);
            listPrendas.add(prenda14);
            listPrendas.add(prenda15);
            listPrendas.add(prenda16);
            listPrendas.add(prenda17);
            listPrendas.add(prenda18);
            listPrendas.add(prenda19);
            listPrendas.add(prenda20);
        }

        return listPrendas;
    }

    public static int obtenerImagenPrendaBasica(Context context,
                                                int tipoPrenda, int prendaSeleccionada, int tamanioImagen,
                                                int estilo) {
        int imagen = 0;
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = tamanioImagen;

        if (tipoPrenda == 1) {
            if (estilo == 1) {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.superior_hombre1;
                        break;
                    case 1:
                        imagen = R.drawable.superior_hombre2;
                        break;
                    case 2:
                        imagen = R.drawable.superior_hombre3;
                        break;
                    case 3:
                        imagen = R.drawable.superior_hombre4;
                        break;
                    case 4:
                        imagen = R.drawable.superior_hombre5;
                        break;
                    case 5:
                        imagen = R.drawable.superior_hombre6;
                        break;
                    case 6:
                        imagen = R.drawable.superior_hombre7;
                        break;
                    case 7:
                        imagen = R.drawable.superior_hombre8;
                        break;
                    case 8:
                        imagen = R.drawable.superior_hombre9;
                        break;
                    case 9:
                        imagen = R.drawable.superior_hombre10;
                        break;
                    case 10:
                        imagen = R.drawable.superior_hombre11;
                        break;
                    case 11:
                        imagen = R.drawable.superior_hombre12;
                        break;
                    case 12:
                        imagen = R.drawable.superior_hombre13;
                        break;
                    case 13:
                        imagen = R.drawable.superior_hombre14;
                        break;
                    case 14:
                        imagen = R.drawable.superior_hombre15;
                        break;
                    case 15:
                        imagen = R.drawable.superior_hombre16;
                        break;
                    case 16:
                        imagen = R.drawable.superior_hombre17;
                        break;
                    case 17:
                        imagen = R.drawable.superior_hombre18;
                        break;
                    case 18:
                        imagen = R.drawable.superior_hombre19;
                        break;
                    case 19:
                        imagen = R.drawable.superior_hombre20;
                        break;
                    case 20:
                        imagen = R.drawable.superior_hombre21;
                        break;
                    case 21:
                        imagen = R.drawable.superior_hombre22;
                        break;
                    case 22:
                        imagen = R.drawable.superior_hombre23;
                        break;
                    case 23:
                        imagen = R.drawable.superior_hombre24;
                        break;
                    case 24:
                        imagen = R.drawable.superior_hombre25;
                        break;
                    case 25:
                        imagen = R.drawable.superior_hombre26;
                        break;
                    case 26:
                        imagen = R.drawable.superior_hombre27;
                        break;
                    case 27:
                        imagen = R.drawable.superior_hombre28;
                        break;
                    case 28:
                        imagen = R.drawable.superior_hombre29;
                        break;
                    case 29:
                        imagen = R.drawable.superior_hombre30;
                        break;
                    default:
                        break;
                }
            } else {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.superior1;
                        break;
                    case 1:
                        imagen = R.drawable.superior2;
                        break;
                    case 2:
                        imagen = R.drawable.superior3;
                        break;
                    case 3:
                        imagen = R.drawable.superior4;
                        break;
                    case 4:
                        imagen = R.drawable.superior5;
                        break;
                    case 5:
                        imagen = R.drawable.superior6;
                        break;
                    case 6:
                        imagen = R.drawable.superior7;
                        break;
                    case 7:
                        imagen = R.drawable.superior8;
                        break;
                    case 8:
                        imagen = R.drawable.superior9;
                        break;
                    case 9:
                        imagen = R.drawable.superior10;
                        break;
                    case 10:
                        imagen = R.drawable.superior11;
                        break;
                    case 11:
                        imagen = R.drawable.superior12;
                        break;
                    case 12:
                        imagen = R.drawable.superior13;
                        break;
                    case 13:
                        imagen = R.drawable.superior14;
                        break;
                    case 14:
                        imagen = R.drawable.superior15;
                        break;
                    case 15:
                        imagen = R.drawable.superior16;
                        break;
                    case 16:
                        imagen = R.drawable.superior17;
                        break;
                    case 17:
                        imagen = R.drawable.superior18;
                        break;
                    case 18:
                        imagen = R.drawable.superior19;
                        break;
                    case 19:
                        imagen = R.drawable.superior20;
                        break;
                    case 20:
                        imagen = R.drawable.superior21;
                        break;
                    case 21:
                        imagen = R.drawable.superior22;
                        break;
                    case 22:
                        imagen = R.drawable.superior23;
                        break;
                    case 23:
                        imagen = R.drawable.superior24;
                        break;
                    case 24:
                        imagen = R.drawable.superior25;
                        break;
                    case 25:
                        imagen = R.drawable.superior26;
                        break;
                    default:
                        break;
                }
            }

        } else if (tipoPrenda == 2) {
            if (estilo == 1) {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.inferior_hombre1;
                        break;
                    case 1:
                        imagen = R.drawable.inferior_hombre2;
                        break;
                    case 2:
                        imagen = R.drawable.inferior_hombre3;
                        break;
                    case 3:
                        imagen = R.drawable.inferior_hombre4;
                        break;
                    case 4:
                        imagen = R.drawable.inferior_hombre5;
                        break;
                    case 5:
                        imagen = R.drawable.inferior_hombre6;
                        break;
                    case 6:
                        imagen = R.drawable.inferior_hombre7;
                        break;
                    case 7:
                        imagen = R.drawable.inferior_hombre8;
                        break;
                    case 8:
                        imagen = R.drawable.inferior_hombre9;
                        break;
                    case 9:
                        imagen = R.drawable.inferior_hombre10;
                        break;
                    default:
                        break;
                }
            } else {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.inferior1;
                        break;
                    case 1:
                        imagen = R.drawable.inferior2;
                        break;
                    case 2:
                        imagen = R.drawable.inferior3;
                        break;
                    case 3:
                        imagen = R.drawable.inferior4;
                        break;
                    case 4:
                        imagen = R.drawable.inferior5;
                        break;
                    case 5:
                        imagen = R.drawable.inferior6;
                        break;
                    case 6:
                        imagen = R.drawable.inferior7;
                        break;
                    case 7:
                        imagen = R.drawable.inferior8;
                        break;
                    case 8:
                        imagen = R.drawable.inferior9;
                        break;
                    case 9:
                        imagen = R.drawable.inferior10;
                        break;
                    case 10:
                        imagen = R.drawable.inferior11;
                        break;
                    case 11:
                        imagen = R.drawable.inferior12;
                        break;
                    case 12:
                        imagen = R.drawable.inferior13;
                        break;
                    case 13:
                        imagen = R.drawable.inferior14;
                        break;
                    case 14:
                        imagen = R.drawable.inferior15;
                        break;
                    case 15:
                        imagen = R.drawable.inferior16;
                        break;
                    case 16:
                        imagen = R.drawable.inferior17;
                        break;
                    case 17:
                        imagen = R.drawable.inferior18;
                        break;
                    case 18:
                        imagen = R.drawable.inferior19;
                        break;
                    case 19:
                        imagen = R.drawable.inferior20;
                        break;
                    case 20:
                        imagen = R.drawable.inferior21;
                        break;
                    case 21:
                        imagen = R.drawable.inferior22;
                        break;
                    case 22:
                        imagen = R.drawable.inferior23;
                        break;
                    case 23:
                        imagen = R.drawable.inferior24;
                        break;
                    case 24:
                        imagen = R.drawable.inferior25;
                        break;
                    case 25:
                        imagen = R.drawable.inferior26;
                        break;
                    default:
                        break;
                }
            }
        } else if (tipoPrenda == 5) {
            if (estilo == 1) {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.calzado_hombre1;
                        break;
                    case 1:
                        imagen = R.drawable.calzado_hombre2;
                        break;
                    case 2:
                        imagen = R.drawable.calzado_hombre3;
                        break;
                    case 3:
                        imagen = R.drawable.calzado_hombre4;
                        break;
                    case 4:
                        imagen = R.drawable.calzado_hombre5;
                        break;
                    case 5:
                        imagen = R.drawable.calzado_hombre6;
                        break;
                    case 6:
                        imagen = R.drawable.calzado_hombre7;
                        break;
                    case 7:
                        imagen = R.drawable.calzado_hombre8;
                        break;
                    case 8:
                        imagen = R.drawable.calzado_hombre9;
                        break;
                    case 9:
                        imagen = R.drawable.calzado_hombre10;
                        break;
                    case 10:
                        imagen = R.drawable.calzado_hombre11;
                        break;
                    case 11:
                        imagen = R.drawable.calzado_hombre12;
                        break;
                    case 12:
                        imagen = R.drawable.calzado_hombre13;
                        break;
                    default:
                        break;
                }
            } else {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.calzado1;
                        break;
                    case 1:
                        imagen = R.drawable.calzado2;
                        break;
                    case 2:
                        imagen = R.drawable.calzado3;
                        break;
                    case 3:
                        imagen = R.drawable.calzado4;
                        break;
                    case 4:
                        imagen = R.drawable.calzado5;
                        break;
                    case 5:
                        imagen = R.drawable.calzado6;
                        break;
                    case 6:
                        imagen = R.drawable.calzado7;
                        break;
                    case 7:
                        imagen = R.drawable.calzado8;
                        break;
                    case 8:
                        imagen = R.drawable.calzado9;
                        break;
                    case 9:
                        imagen = R.drawable.calzado10;
                        break;
                    case 10:
                        imagen = R.drawable.calzado11;
                        break;
                    case 11:
                        imagen = R.drawable.calzado12;
                        break;
                    case 12:
                        imagen = R.drawable.calzado13;
                        break;
                    case 13:
                        imagen = R.drawable.calzado14;
                        break;
                    case 14:
                        imagen = R.drawable.calzado15;
                        break;
                    case 15:
                        imagen = R.drawable.calzado16;
                        break;
                    case 16:
                        imagen = R.drawable.calzado17;
                        break;
                    case 17:
                        imagen = R.drawable.calzado18;
                        break;
                    case 18:
                        imagen = R.drawable.calzado19;
                        break;
                    case 19:
                        imagen = R.drawable.calzado20;
                        break;
                    default:
                        break;
                }
            }
        } else if (tipoPrenda == 4) {
            if (estilo == 1) {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.abrigo_hombre1;
                        break;
                    case 1:
                        imagen = R.drawable.abrigo_hombre2;
                        break;
                    case 2:
                        imagen = R.drawable.abrigo_hombre3;
                        break;
                    case 3:
                        imagen = R.drawable.abrigo_hombre4;
                        break;
                    case 4:
                        imagen = R.drawable.abrigo_hombre5;
                        break;
                    case 5:
                        imagen = R.drawable.abrigo_hombre6;
                        break;
                    case 6:
                        imagen = R.drawable.abrigo_hombre7;
                        break;
                    case 7:
                        imagen = R.drawable.abrigo_hombre8;
                        break;
                    case 8:
                        imagen = R.drawable.abrigo_hombre9;
                        break;

                    default:
                        break;
                }
            } else {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.abrigo1;
                        break;
                    case 1:
                        imagen = R.drawable.abrigo2;
                        break;
                    case 2:
                        imagen = R.drawable.abrigo3;
                        break;
                    case 3:
                        imagen = R.drawable.abrigo4;
                        break;
                    case 4:
                        imagen = R.drawable.abrigo5;
                        break;
                    case 5:
                        imagen = R.drawable.abrigo6;
                        break;
                    case 6:
                        imagen = R.drawable.abrigo7;
                        break;

                    default:
                        break;
                }
            }
        } else if (tipoPrenda == 3) {
            switch (prendaSeleccionada) {
                case 0:
                    imagen = R.drawable.cuerpo1;
                    break;
                case 1:
                    imagen = R.drawable.cuerpo2;
                    break;
                case 2:
                    imagen = R.drawable.cuerpo3;
                    break;
                case 3:
                    imagen = R.drawable.cuerpo4;
                    break;
                case 4:
                    imagen = R.drawable.cuerpo5;
                    break;
                default:
                    break;
            }
        } else if (tipoPrenda == 6) {
            if (estilo == 1) {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.complemento_hombre1;
                        break;
                    case 1:
                        imagen = R.drawable.complemento_hombre2;
                        break;
                    case 2:
                        imagen = R.drawable.complemento_hombre3;
                        break;
                    case 3:
                        imagen = R.drawable.complemento_hombre4;
                        break;
                    case 4:
                        imagen = R.drawable.complemento_hombre5;
                        break;
                    case 5:
                        imagen = R.drawable.complemento_hombre6;
                        break;
                    case 6:
                        imagen = R.drawable.complemento_hombre7;
                        break;
                    case 7:
                        imagen = R.drawable.complemento_hombre8;
                        break;
                    case 8:
                        imagen = R.drawable.complemento_hombre9;
                        break;
                    case 9:
                        imagen = R.drawable.complemento_hombre10;
                        break;
                    case 10:
                        imagen = R.drawable.complemento_hombre11;
                        break;
                    default:
                        break;
                }
            } else {
                switch (prendaSeleccionada) {
                    case 0:
                        imagen = R.drawable.complemento1;
                        break;
                    case 1:
                        imagen = R.drawable.complemento2;
                        break;
                    case 2:
                        imagen = R.drawable.complemento3;
                        break;
                    case 3:
                        imagen = R.drawable.complemento4;
                        break;
                    case 4:
                        imagen = R.drawable.complemento5;
                        break;
                    case 5:
                        imagen = R.drawable.complemento6;
                        break;
                    case 6:
                        imagen = R.drawable.complemento7;
                        break;
                    case 7:
                        imagen = R.drawable.complemento8;
                        break;
                    case 8:
                        imagen = R.drawable.complemento9;
                        break;
                    case 9:
                        imagen = R.drawable.complemento10;
                        break;
                    case 10:
                        imagen = R.drawable.complemento11;
                        break;
                    case 11:
                        imagen = R.drawable.complemento12;
                        break;
                    case 12:
                        imagen = R.drawable.complemento13;
                        break;
                    case 13:
                        imagen = R.drawable.complemento14;
                        break;
                    case 14:
                        imagen = R.drawable.complemento15;
                        break;
                    case 15:
                        imagen = R.drawable.complemento16;
                        break;
                    case 16:
                        imagen = R.drawable.complemento17;
                        break;
                    case 17:
                        imagen = R.drawable.complemento18;
                        break;
                    case 18:
                        imagen = R.drawable.complemento19;
                        break;
                    case 19:
                        imagen = R.drawable.complemento20;
                        break;
                    case 20:
                        imagen = R.drawable.complemento21;
                        break;
                    case 21:
                        imagen = R.drawable.complemento22;
                        break;
                    case 22:
                        imagen = R.drawable.complemento23;
                        break;
                    case 23:
                        imagen = R.drawable.complemento24;
                        break;
                    default:
                        break;
                }
            }
        }
        return imagen;
    }

    public static ArrayList<Prenda> obtenerInspiraciones(Context context,
                                                         int estilo) {
        ArrayList<Prenda> listPrendas = new ArrayList<Prenda>();

        if (estilo == 1) {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setIdDrawable(R.drawable.inspiracion_hombre1);

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setIdDrawable(R.drawable.inspiracion_hombre2);

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setIdDrawable(R.drawable.inspiracion_hombre3);

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setIdDrawable(R.drawable.inspiracion_hombre4);

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setIdDrawable(R.drawable.inspiracion_hombre5);

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setIdDrawable(R.drawable.inspiracion_hombre6);

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setIdDrawable(R.drawable.inspiracion_hombre7);

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setIdDrawable(R.drawable.inspiracion_hombre8);

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setIdDrawable(R.drawable.inspiracion_hombre9);

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setIdDrawable(R.drawable.inspiracion_hombre10);

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
        } else {
            Prenda prenda = new Prenda();
            prenda.setIdPrenda(0);
            prenda.setIdDrawable(R.drawable.inspiraciones1);

            Prenda prenda2 = new Prenda();
            prenda2.setIdPrenda(1);
            prenda2.setIdDrawable(R.drawable.inspiraciones2);

            Prenda prenda3 = new Prenda();
            prenda3.setIdPrenda(2);
            prenda3.setIdDrawable(R.drawable.inspiraciones3);

            Prenda prenda4 = new Prenda();
            prenda4.setIdPrenda(3);
            prenda4.setIdDrawable(R.drawable.inspiraciones4);

            Prenda prenda5 = new Prenda();
            prenda5.setIdPrenda(4);
            prenda5.setIdDrawable(R.drawable.inspiraciones5);

            Prenda prenda6 = new Prenda();
            prenda6.setIdPrenda(5);
            prenda6.setIdDrawable(R.drawable.inspiraciones6);

            Prenda prenda7 = new Prenda();
            prenda7.setIdPrenda(6);
            prenda7.setIdDrawable(R.drawable.inspiraciones7);

            Prenda prenda8 = new Prenda();
            prenda8.setIdPrenda(7);
            prenda8.setIdDrawable(R.drawable.inspiraciones8);

            Prenda prenda9 = new Prenda();
            prenda9.setIdPrenda(8);
            prenda9.setIdDrawable(R.drawable.inspiraciones9);

            Prenda prenda10 = new Prenda();
            prenda10.setIdPrenda(9);
            prenda10.setIdDrawable(R.drawable.inspiraciones10);

            Prenda prenda11 = new Prenda();
            prenda11.setIdPrenda(10);
            prenda11.setIdDrawable(R.drawable.inspiraciones11);

            listPrendas.add(prenda);
            listPrendas.add(prenda2);
            listPrendas.add(prenda3);
            listPrendas.add(prenda4);
            listPrendas.add(prenda5);
            listPrendas.add(prenda6);
            listPrendas.add(prenda7);
            listPrendas.add(prenda8);
            listPrendas.add(prenda9);
            listPrendas.add(prenda10);
            listPrendas.add(prenda11);
        }
        return listPrendas;
    }

    public static int obtenerImagenInspiracion(Context context,
                                               int prendaSeleccionada, int estilo) {
        int imagen = 0;

        if (estilo == 1) {
            switch (prendaSeleccionada) {
                case 0:
                    imagen = R.drawable.inspiracion_hombre1;
                    break;
                case 1:
                    imagen = R.drawable.inspiracion_hombre2;
                    break;
                case 2:
                    imagen = R.drawable.inspiracion_hombre3;
                    break;
                case 3:
                    imagen = R.drawable.inspiracion_hombre4;
                    break;
                case 4:
                    imagen = R.drawable.inspiracion_hombre5;
                    break;
                case 5:
                    imagen = R.drawable.inspiracion_hombre6;
                    break;
                case 6:
                    imagen = R.drawable.inspiracion_hombre7;
                    break;
                case 7:
                    imagen = R.drawable.inspiracion_hombre8;
                    break;
                case 8:
                    imagen = R.drawable.inspiracion_hombre9;
                    break;
                case 9:
                    imagen = R.drawable.inspiracion_hombre10;
                    break;
            }
        } else {
            switch (prendaSeleccionada) {
                case 0:
                    imagen = R.drawable.inspiraciones1;
                    break;
                case 1:
                    imagen = R.drawable.inspiraciones2;
                    break;
                case 2:
                    imagen = R.drawable.inspiraciones3;
                    break;
                case 3:
                    imagen = R.drawable.inspiraciones4;
                    break;
                case 4:
                    imagen = R.drawable.inspiraciones5;
                    break;
                case 5:
                    imagen = R.drawable.inspiraciones6;
                    break;
                case 6:
                    imagen = R.drawable.inspiraciones7;
                    break;
                case 7:
                    imagen = R.drawable.inspiraciones8;
                    break;
                case 8:
                    imagen = R.drawable.inspiraciones9;
                    break;
                case 9:
                    imagen = R.drawable.inspiraciones10;
                    break;
                case 10:
                    imagen = R.drawable.inspiraciones11;
                    break;
            }
        }
        return imagen;
    }

    /**
     * Validate given email with regular expression.
     *
     * @param email email for validation
     * @return true valid email, otherwise false
     */
    public static boolean validarEmail(String email) {

        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static String getClassId(String idClass) {
        idClass = idClass.replace("#", "");
        idClass = idClass.replace("9", "");
        idClass = idClass.replace("$", "");
        idClass = idClass.replace("&", "");

        return idClass;
    }

    public static int obtenerIconoUser(int idIcon) {
        int icon = 0;

        switch (idIcon) {
            case 0:
                icon = R.drawable.user1;
                break;
            case 1:
                icon = R.drawable.user2;
                break;
            case 2:
                icon = R.drawable.user3;
                break;
            case 3:
                icon = R.drawable.user4;
                break;
            case 4:
                icon = R.drawable.user5;
                break;
            default:
                icon = R.drawable.user1;
                break;
        }
        return icon;
    }

    public static ArrayList<Icon> obtenerIconosCuenta() {
        ArrayList<Icon> listIcon = new ArrayList<Icon>();

        for (int i = 0; i < 5; i++) {
            Icon icon = new Icon();
            icon.setId(i);
            listIcon.add(icon);
        }

        return listIcon;
    }
}
