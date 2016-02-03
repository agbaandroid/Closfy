package com.agba.closfy.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.agba.closfy.modelo.Asesoramiento;
import com.agba.closfy.modelo.Cuenta;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.modelo.PrendaLook;
import com.agba.closfy.modelo.Utilidad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestionBBDD {

	private final String INFO = "INFO";
	String sqlCreatePrendas = "CREATE TABLE IF NOT EXISTS [Prendas] ( "
			+ "[idPrenda] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[tipoPrenda] VARCHAR(1) NOT NULL, [prendaBasica] INTEGER NULL, "
			+ "[idPrendaBasica] INTEGER NULL, "
			+ "[idTemporada] VARCHAR(1) NOT NULL, "
			+ "[favorito] INTEGER  NULL, [idFoto] TEXT NOT NULL, [utilidades] TEXT,"
			+ "[idCuenta] INTEGER NULL)";

	String sqlCreateLooks = "CREATE TABLE IF NOT EXISTS [Looks] ( "
			+ "[idLook] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[idTemporada] VARCHAR(1) NOT NULL, [prendas] TEXT NOT NULL, "
			+ "[utilidades] TEXT NOT NULL, [favorito] INTEGER  NULL,  [notas] TEXT NULL, [idCuenta] INTEGER NULL, "
			+ "[idFoto] TEXT NOT NULL, " + "[colorFondo] TEXT NOT NULL)";

	String sqlCreateLookPrendas = "CREATE TABLE IF NOT EXISTS [Look_Prendas] ( "
			+ "[idLookPrenda] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[idLook] INTEGER  NOT NULL, "
			+ "[idPrenda] INTEGER  NOT NULL, "
			+ "[posiX] FLOAT  NOT NULL, [posiY] FLOAT NOT NULL,"
			+ "[ancho] FLOAT NOT NULL, [alto] FLOAT NOT NULL, [posicion] INTEGER NOT NULL)";

	String sqlCreateUtilidades = "CREATE TABLE IF NOT EXISTS [Utilidades] ( "
			+ "[idUtilidad] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[descripcion] TEXT NOT NULL)";

	String sqlCreateCalendario = "CREATE TABLE IF NOT EXISTS [Calendario] ( "
			+ "[idCalendario] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[fecha] TEXT NOT NULL, [hora] INTEGER NOT NULL, "
			+ "[idLook] INTEGER NOT NULL, [idCuenta] INTEGER NULL)";

	String sqlCreateCuentas = "CREATE TABLE IF NOT EXISTS [Cuentas] ( "
			+ "[idCuenta] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[nombre] TEXT NOT NULL, [sexo] INTEGER NOT NULL, "
			+ "[idIcon] INTEGER NOT NULL)";

	String sqlCreateAsesoramientos = "CREATE TABLE IF NOT EXISTS [Asesoramientos] ( "
			+ "[idAsesoramiento] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "[idCompra] TEXT NOT NULL, [Consumido] INTEGER NOT NULL)";

	Locale locale = Locale.getDefault();
	String languaje = locale.getLanguage();

	public void createTables(SQLiteDatabase db) {

		Log.d(INFO, "Se crear� la estructura basica de base de"
				+ " datos si no existe");

		// Creamos las tablas si no existen
		db.execSQL(sqlCreatePrendas);
		db.execSQL(sqlCreateLooks);
		db.execSQL(sqlCreateLookPrendas);
		db.execSQL(sqlCreateUtilidades);
		db.execSQL(sqlCreateCuentas);
		db.execSQL(sqlCreateCalendario);
		db.execSQL(sqlCreateAsesoramientos);
		// db.execSQL(sqlCreateTarjetas);
		// db.execSQL(sqlCreateRecibos);
		// db.execSQL(sqlCreateCuentas);

		// Comprobamos si las tabla Utilidades est� vacia
		// para a�adirle los campos por defecto
		Cursor c1 = db.query("Utilidades", new String[] { "idUtilidad" }, null,
				null, null, null, null);

		if (!c1.moveToFirst()) {
			if (languaje.equals("es") || languaje.equals("es-rUS")
					|| languaje.equals("ca")) {
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Trabajo')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Diario')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Fiesta')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Deporte')");
			} else {
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Work')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Casual')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Party')");
				db.execSQL("INSERT INTO Utilidades VALUES(null, 'Workout')");
			}
		}

		// Comprobamos si las tablas Categorias y Subcategorias estan vacias
		// para a�adirle los campos por defecto
	}

	public void crearCuentaPrincipal(SQLiteDatabase db, int sexo) {
		Cursor cCuenta = db.query("Cuentas", new String[] { "idCuenta" }, null,
				null, null, null, null);

		if (!cCuenta.moveToFirst()) {
			if (languaje.equals("es") || languaje.equals("es-rUS")
					|| languaje.equals("ca")) {
				db.execSQL("INSERT INTO Cuentas VALUES(0, 'Cuenta principal', '"
						+ sexo + "', 1 )");
			} else {
				db.execSQL("INSERT INTO Cuentas VALUES(0, 'Main account', '"
						+ sexo + "', 1 )");
			}

		}
	}

	public boolean hayCuenta(SQLiteDatabase db) {
		boolean hayCuenta = false;
		Cursor cCuenta = db.query("Cuentas", new String[] { "idCuenta" }, null,
				null, null, null, null);

		if (cCuenta.moveToFirst()) {
			hayCuenta = true;
		}

		return hayCuenta;
	}

	public boolean insertarPrenda(SQLiteDatabase db, int idTipo,
			int prendaBasica, int idPrendaBasica, int idTemporada,
			int favorito, String idFoto, String utilidades, int idCuenta) {

		try {
			String sql = "INSERT INTO Prendas VALUES(" + null + "," + idTipo
					+ ", " + prendaBasica + ", " + idPrendaBasica + ", "
					+ idTemporada + ", " + "'" + favorito + "'," + "'" + idFoto
					+ "', " + "'" + utilidades + "', '" + idCuenta + "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return false;
		}
	}

	public boolean editarPrenda(SQLiteDatabase db, String id, int idTemporada,
			int favorito, String idFoto, String utilidades, int idCuenta) {
		try {
			ContentValues values = new ContentValues();
			values.put("idTemporada", idTemporada);
			values.put("favorito", favorito);
			values.put("idFoto", idFoto);
			values.put("utilidades", utilidades);
			values.put("idCuenta", idCuenta);
			db.update("Prendas", values, "idPrenda=?", new String[] { id });
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public ArrayList<Prenda> getPrendas(SQLiteDatabase db, int idCuenta) {
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
		try {
			Cursor c1 = db.rawQuery("select * from Prendas where idCuenta='"
					+ idCuenta + "'", null);
			if (c1.moveToFirst()) {
				do {
					Prenda prenda = new Prenda();
					prenda.setIdPrenda(c1.getInt(0));
					prenda.setIdTipo(c1.getInt(1));
					prenda.setPrendaBasica(c1.getInt(2));
					prenda.setIdPrendaBasica(c1.getInt(3));
					prenda.setIdTemporada(c1.getInt(4));
					prenda.setFavorito(c1.getInt(5));
					prenda.setIdFoto(c1.getString(6));
					prenda.setUtilidades(c1.getString(7));
					listaPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return listaPrendas;
		}
		return listaPrendas;
	}

	public ArrayList<Prenda> getPrendasFavoritas(SQLiteDatabase db, int idCuenta) {
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
		try {
			Cursor c1 = db.rawQuery("select * from Prendas where idCuenta='"
					+ idCuenta + "' and favorito='1'", null);
			if (c1.moveToFirst()) {
				do {
					Prenda prenda = new Prenda();
					prenda.setIdPrenda(c1.getInt(0));
					prenda.setIdTipo(c1.getInt(1));
					prenda.setPrendaBasica(c1.getInt(2));
					prenda.setIdPrendaBasica(c1.getInt(3));
					prenda.setIdTemporada(c1.getInt(4));
					prenda.setFavorito(c1.getInt(5));
					prenda.setIdFoto(c1.getString(6));
					prenda.setUtilidades(c1.getString(7));
					listaPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return listaPrendas;
		}
		return listaPrendas;
	}

	public ArrayList<Prenda> getPrendasByIdTipo(SQLiteDatabase db, int idTipo,
			int idCuenta) {
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
		try {
			Cursor c1 = db.rawQuery(
					"select * from Prendas where tipoPrenda = '" + idTipo
							+ "' and idCuenta = '" + idCuenta + "'", null);
			if (c1.moveToFirst()) {
				do {
					Prenda prenda = new Prenda();
					prenda.setIdPrenda(c1.getInt(0));
					prenda.setIdTipo(c1.getInt(1));
					prenda.setPrendaBasica(c1.getInt(2));
					prenda.setIdPrendaBasica(c1.getInt(3));
					prenda.setIdTemporada(c1.getInt(4));
					prenda.setFavorito(c1.getInt(5));
					prenda.setIdFoto(c1.getString(6));
					prenda.setUtilidades(c1.getString(7));
					listaPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return listaPrendas;
		}
		return listaPrendas;
	}

	public ArrayList<Prenda> getPrendasFiltros(SQLiteDatabase db, int idTipo,
			int idTemporada, int favorito, int idCuenta) {
				ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
				try {
					StringBuffer sql = new StringBuffer(
							"select * from Prendas where idCuenta = '" + idCuenta + "'");

					if (idTipo != 0) {
						sql.append(" and tipoPrenda = '" + idTipo + "'");
					}
					if (idTemporada != 2) {
						sql.append(" and (idTemporada = '" + idTemporada
								+ "' or idTemporada = '2')");
					}
					if (favorito != 0) {
						sql.append(" and favorito = '" + favorito + "'");
					}

					Cursor c1 = db.rawQuery(sql.toString(), null);
			if (c1.moveToFirst()) {
				do {
					Prenda prenda = new Prenda();
					prenda.setIdPrenda(c1.getInt(0));
					prenda.setIdTipo(c1.getInt(1));
					prenda.setPrendaBasica(c1.getInt(2));
					prenda.setIdPrendaBasica(c1.getInt(3));
					prenda.setIdTemporada(c1.getInt(4));
					prenda.setFavorito(c1.getInt(5));
					prenda.setIdFoto(c1.getString(6));
					prenda.setUtilidades(c1.getString(7));
					listaPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return listaPrendas;
		}
		return listaPrendas;
	}

	public ArrayList<Prenda> getPrendasFavoritasByIdTipo(SQLiteDatabase db,
			int idTipo, int idCuenta) {
		ArrayList<Prenda> listaPrendas = new ArrayList<Prenda>();
		try {
			Cursor c1 = db.rawQuery(
					"select * from Prendas where tipoPrenda = '" + idTipo
							+ "' and idCuenta = '" + idCuenta
							+ "' and favorito='1'", null);
			if (c1.moveToFirst()) {
				do {
					Prenda prenda = new Prenda();
					prenda.setIdPrenda(c1.getInt(0));
					prenda.setIdTipo(c1.getInt(1));
					prenda.setPrendaBasica(c1.getInt(2));
					prenda.setIdPrendaBasica(c1.getInt(3));
					prenda.setIdTemporada(c1.getInt(4));
					prenda.setFavorito(c1.getInt(5));
					prenda.setIdFoto(c1.getString(6));
					prenda.setUtilidades(c1.getString(7));
					listaPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return listaPrendas;
		}
		return listaPrendas;
	}

	public Prenda getPrendaById(SQLiteDatabase db, int idPrenda) {
		Prenda prenda = new Prenda();
		try {
			Cursor c1 = db.rawQuery("select * from Prendas where idPrenda = '"
					+ idPrenda + "'", null);
			if (c1.moveToFirst()) {
				prenda.setIdPrenda(c1.getInt(0));
				prenda.setIdTipo(c1.getInt(1));
				prenda.setPrendaBasica(c1.getInt(2));
				prenda.setIdPrendaBasica(c1.getInt(3));
				prenda.setIdTemporada(c1.getInt(4));
				prenda.setFavorito(c1.getInt(5));
				prenda.setIdFoto(c1.getString(6));
				prenda.setUtilidades(c1.getString(7));
			}
		} catch (Exception e) {
			Log.d("Error", "Error al insertar registro en BBDD");
			return prenda;
		}
		return prenda;
	}

	public boolean eliminarPrenda(SQLiteDatabase db, int idPrenda, String idFoto) {
		try {
			db.delete("Prendas", "idPrenda=?",
					new String[] { String.valueOf(idPrenda) });

			// Borramos la foto del direcctorio
			File dbFile = new File(Environment.getExternalStorageDirectory(),
					"/Closfy/Prendas");

			Uri tmpImgUri = Uri.fromFile(new File(dbFile, idFoto));
			File fileFoto = new File(tmpImgUri.getPath());
			if (fileFoto.exists()) {
				fileFoto.delete();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean insertarLook(SQLiteDatabase db, int idTemporada,
			String prendas, String utilidades, int favorito, String notas,
			int idCuenta, String idFoto, String colorFondo) {

		try {
			String sql = "INSERT INTO Looks VALUES(" + null + "," + idTemporada
					+ ", " + "'" + prendas + "', '" + utilidades + "', '"
					+ favorito + "', '" + notas + "', '" + idCuenta + "', '"
					+ idFoto + "', '" + colorFondo + "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.d("Error", "Error al insertar look en BBDD");
			return false;
		}
	}

	public boolean insertarLookPrendas(SQLiteDatabase db, int idLook,
			int idPrenda, float posiX, float posiY, float ancho, float alto,
			int pos) {

		try {
			String sql = "INSERT INTO Look_Prendas VALUES(" + null + ","
					+ idLook + ", " + "'" + idPrenda + "', '" + posiX + "', '"
					+ posiY + "', '" + ancho + "', '" + alto + "' , '" + pos
					+ "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.d("Error", "Error al insertar look en BBDD");
			return false;
		}
	}

	public boolean editarLook(SQLiteDatabase db, String id, int idTemporada,
			String utilidades, int favorito, String notas) {
		try {
			ContentValues values = new ContentValues();
			values.put("idTemporada", idTemporada);
			values.put("favorito", favorito);
			values.put("utilidades", utilidades);
			values.put("notas", notas);
			db.update("Looks", values, "idLook=?", new String[] { id });
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public boolean editarLook(SQLiteDatabase db, String id, String url) {
		try {
			ContentValues values = new ContentValues();
			values.put("idFoto", url);
			db.update("Looks", values, "idLook=?", new String[] { id });
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public ArrayList<Look> getLooks(SQLiteDatabase db, int idCuenta) {
		ArrayList<Look> listaLooks = new ArrayList<Look>();
		try {
			Cursor c1 = db.rawQuery("select * from Looks where idCuenta = '"
					+ idCuenta + "'", null);
			if (c1.moveToFirst()) {
				do {
					Look look = new Look();
					look.setIdLook(c1.getInt(0));
					look.setIdTemporada(c1.getInt(1));
					look.setCadenaPrendas(c1.getString(2));
					look.setUtilidades(c1.getString(3));
					look.setFavorito(c1.getInt(4));
					look.setNotas(c1.getString(5));
					look.setIdFoto(c1.getString(7));
					look.setColorFondo(c1.getString(8));
					listaLooks.add(look);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener looks en BBDD");
			return listaLooks;
		}
		return listaLooks;
	}

	public ArrayList<Look> getLooksFiltros(SQLiteDatabase db, int idCuenta,
			int idTemporada, int favorito) {
		ArrayList<Look> listaLooks = new ArrayList<Look>();
		try {
			StringBuffer sql = new StringBuffer(
					"select * from Looks where idCuenta = '" + idCuenta + "'");
			if (idTemporada != 2) {
				sql.append(" and (idTemporada = '" + idTemporada
						+ "' or idTemporada = '2')");
			}
			if (favorito != 0) {
				sql.append(" and favorito = '" + favorito + "'");
			}

			Cursor c1 = db.rawQuery(sql.toString(), null);
			if (c1.moveToFirst()) {
				do {
					Look look = new Look();
					look.setIdLook(c1.getInt(0));
					look.setIdTemporada(c1.getInt(1));
					look.setCadenaPrendas(c1.getString(2));
					look.setUtilidades(c1.getString(3));
					look.setFavorito(c1.getInt(4));
					look.setNotas(c1.getString(5));
					look.setIdFoto(c1.getString(7));
					look.setColorFondo(c1.getString(8));
					listaLooks.add(look);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener looks en BBDD");
			return listaLooks;
		}
		return listaLooks;
	}

	public ArrayList<Look> getLooksFavoritos(SQLiteDatabase db, int idCuenta) {
		ArrayList<Look> listaLooks = new ArrayList<Look>();
		try {
			Cursor c1 = db.rawQuery("select * from Looks where idCuenta = '"
					+ idCuenta + "' and favorito='1'", null);
			if (c1.moveToFirst()) {
				do {
					Look look = new Look();
					look.setIdLook(c1.getInt(0));
					look.setIdTemporada(c1.getInt(1));
					look.setCadenaPrendas(c1.getString(2));
					look.setUtilidades(c1.getString(3));
					look.setFavorito(c1.getInt(4));
					look.setNotas(c1.getString(5));
					look.setIdFoto(c1.getString(7));
					look.setColorFondo(c1.getString(8));
					listaLooks.add(look);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener looks en BBDD");
			return listaLooks;
		}
		return listaLooks;
	}

	public Look getLookById(SQLiteDatabase db, int idLook) {
		Look look = new Look();
		try {
			Cursor c1 = db.rawQuery("select * from Looks where idLook = '"
					+ idLook + "'", null);
			if (c1.moveToFirst()) {
				look.setIdLook(c1.getInt(0));
				look.setIdTemporada(c1.getInt(1));
				look.setCadenaPrendas(c1.getString(2));
				look.setUtilidades(c1.getString(3));
				look.setFavorito(c1.getInt(4));
				look.setNotas(c1.getString(5));
				look.setIdFoto(c1.getString(7));
				look.setColorFondo(c1.getString(8));
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener un look en BBDD");
			return look;
		}
		return look;
	}

	public ArrayList<PrendaLook> getPrendasLook(SQLiteDatabase db, int idLook) {
		ArrayList<PrendaLook> listPrendas = new ArrayList<PrendaLook>();
		try {
			Cursor c1 = db
					.rawQuery(
							"select LP.idLook, LP.idPrenda, P.tipoPrenda, P.idFoto, P.prendaBasica, P.idPrendaBasica, LP.posiX, "
									+ "LP.posiY, LP.ancho, LP.alto, LP.posicion from Looks L, Look_Prendas LP, Prendas P "
									+ "where L.idLook = LP.idLook and LP.idPrenda = P.idPrenda and L.idLook ='"
									+ idLook + "'", null);
			if (c1.moveToFirst()) {
				do {
					PrendaLook prenda = new PrendaLook();
					prenda.setIdLook(c1.getInt(0));
					prenda.setIdPrenda(c1.getInt(1));
					prenda.setIdTipo(c1.getInt(2));
					prenda.setIdFoto(c1.getString(3));
					prenda.setPrendaBasica(c1.getInt(4));
					prenda.setIdPrendaBasica(c1.getInt(5));
					prenda.setPosiX(c1.getFloat(6));
					prenda.setPosiY(c1.getFloat(7));
					prenda.setAncho(c1.getFloat(8));
					prenda.setAlto(c1.getFloat(9));
					prenda.setPos(c1.getInt(10));
					listPrendas.add(prenda);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener un look en BBDD");
			return listPrendas;
		}
		return listPrendas;
	}

	public Look getUltimoLook(SQLiteDatabase db) {
		Look look = new Look();
		try {
			Cursor c1 = db.rawQuery(
					"SELECT * FROM Looks ORDER BY idLook DESC LIMIT 1", null);
			if (c1.moveToFirst()) {
				look.setIdLook(c1.getInt(0));
				// look.setIdTemporada(c1.getInt(1));
				// look.setCadenaPrendas(c1.getString(2));
				// look.setUtilidades(c1.getString(3));
				// look.setFavorito(c1.getInt(4));
				// look.setNotas(c1.getString(5));
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener un look en BBDD");
			return look;
		}
		return look;
	}

	public boolean eliminarLook(SQLiteDatabase db, int idLook, String idFoto) {
		try {
			db.delete("Looks", "idLook=?",
					new String[] { String.valueOf(idLook) });

			db.delete("Look_Prendas", "idLook=?",
					new String[] { String.valueOf(idLook) });

			// Borramos la foto del direcctorio
			File dbFile = new File(Environment.getExternalStorageDirectory(),
					"/Closfy/Looks");

			Uri tmpImgUri = Uri.fromFile(new File(dbFile, idFoto));
			File fileFoto = new File(tmpImgUri.getPath());
			if (fileFoto.exists()) {
				fileFoto.delete();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean eliminarPrendaLook(SQLiteDatabase db, int idLook) {
		try {
			db.delete("Look_Prendas", "idLook=?",
					new String[] { String.valueOf(idLook) });
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public ArrayList<Utilidad> getUtilidades(SQLiteDatabase db) {
		ArrayList<Utilidad> listaUtilidades = new ArrayList<Utilidad>();
		try {
			String todos = "";

			if (languaje.equals("es") || languaje.equals("es-rUS")
					|| languaje.equals("ca")) {
				todos = "Todos";
			} else {
				todos = "All";
			}

			Utilidad utilidadTodas = new Utilidad();
			utilidadTodas.setIdUtilidad(-1);
			utilidadTodas.setNombre(todos);
			listaUtilidades.add(utilidadTodas);

			Cursor c1 = db.rawQuery("select * from Utilidades", null);
			if (c1.moveToFirst()) {
				do {
					Utilidad utilidad = new Utilidad();
					utilidad.setIdUtilidad(c1.getInt(0));
					utilidad.setNombre(c1.getString(1));
					listaUtilidades.add(utilidad);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener las utilidades en BBDD");
			return listaUtilidades;
		}
		return listaUtilidades;
	}

	public ArrayList<Utilidad> getUtilidadesFiltro(SQLiteDatabase db) {
		ArrayList<Utilidad> listaUtilidades = new ArrayList<Utilidad>();
		try {
			String todos = "";

			if (languaje.equals("es") || languaje.equals("es-rUS")
					|| languaje.equals("ca")) {
				todos = "Utilidades...";
			} else {
				todos = "Utilities...";
			}

			Utilidad utilidadTodas = new Utilidad();
			utilidadTodas.setIdUtilidad(-1);
			utilidadTodas.setNombre(todos);
			listaUtilidades.add(utilidadTodas);

			Cursor c1 = db.rawQuery("select * from Utilidades", null);
			if (c1.moveToFirst()) {
				do {
					Utilidad utilidad = new Utilidad();
					utilidad.setIdUtilidad(c1.getInt(0));
					utilidad.setNombre(c1.getString(1));
					listaUtilidades.add(utilidad);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener las utilidades en BBDD");
			return listaUtilidades;
		}
		return listaUtilidades;
	}

	public ArrayList<Utilidad> getUtilidadesLista(SQLiteDatabase db) {
		ArrayList<Utilidad> listaUtilidades = new ArrayList<Utilidad>();
		try {
			Cursor c1 = db.rawQuery("select * from Utilidades", null);
			if (c1.moveToFirst()) {
				do {
					Utilidad utilidad = new Utilidad();
					utilidad.setIdUtilidad(c1.getInt(0));
					utilidad.setNombre(c1.getString(1));
					listaUtilidades.add(utilidad);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al obtener las utilidades en BBDD");
			return listaUtilidades;
		}
		return listaUtilidades;
	}

	public boolean addUtilidad(SQLiteDatabase db, String utilidad) {
		try {
			String sql = "INSERT INTO Utilidades VALUES(" + null + ", '"
					+ utilidad + "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.d("Error", "Error al insertar una utilidad en BBDD");
			return false;
		}
	}

	public boolean editUtilidad(SQLiteDatabase db, String descripcion, String id) {
		try {
			ContentValues values = new ContentValues();
			values.put("descripcion", descripcion);
			db.update("Utilidades", values, "idUtilidad=?", new String[] { id });

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteUtilidad(SQLiteDatabase db, String id) {
		try {
			db.delete("Utilidades", "idUtilidad=?", new String[] { id });
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDiaLibre(SQLiteDatabase db, String fecha, int cuenta) {
		// consultamos si existe una nomina del mes actual
		try {
			Cursor calendar = db.rawQuery(
					"select * from Calendario where fecha = '" + fecha
							+ "'  and idCuenta = '" + cuenta + "'", null);
			if (calendar.moveToFirst()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDiaLibreHora(SQLiteDatabase db, String fecha, int hora,
			int cuenta) {
		// consultamos si existe una nomina del mes actual
		try {
			Cursor calendar = db.rawQuery(
					"select * from Calendario where fecha = '" + fecha
							+ "' and hora = '" + hora + "' and idCuenta = '"
							+ cuenta + "'", null);
			if (calendar.moveToFirst()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean insertarLookCalendario(SQLiteDatabase db, int hora,
			String fecha, int idLook, int cuenta) {

		try {
			String sql = "INSERT INTO Calendario VALUES(" + null + ", '"
					+ fecha + "' , " + "'" + hora + "', '" + idLook + "' , '"
					+ cuenta + "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.d("Error", "Error al insertar look en el calendario");
			return false;
		}
	}

	public int getLookCalendario(SQLiteDatabase db, String fecha, int hora,
			int cuenta) {
		// consultamos si existe una nomina del mes actual
		try {
			Cursor calendar = db.rawQuery(
					"select idLook from Calendario where fecha = '" + fecha
							+ "' and hora = '" + hora + "' and idCuenta = '"
							+ cuenta + "'", null);
			if (calendar.moveToFirst()) {
				return calendar.getInt(0);
			}

			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	public void deleteLookCalendario(SQLiteDatabase db, String fechaString,
			int hora, int cuentaSeleccionada) {

		String where = "fecha = ?" + " AND hora = ?" + " AND idCuenta = ?";
		String[] whereArgs = { fechaString, String.valueOf(hora),
				String.valueOf(cuentaSeleccionada) };

		db.delete("Calendario", where, whereArgs);

	}

	public Cuenta getCuentaSeleccionada(SQLiteDatabase db, int idCuenta) {

		Cuenta cuenta = new Cuenta();

		Cursor c1 = db.rawQuery("select * from Cuentas where idCuenta="
				+ idCuenta, null);

		if (c1.moveToFirst()) {
			cuenta.setIdCuenta(c1.getString(0));
			cuenta.setDescCuenta(c1.getString(1));
			cuenta.setSexo(c1.getInt(2));
			cuenta.setIdIcon(c1.getInt(3));
		}
		return cuenta;
	}

	public int getEstiloCuenta(SQLiteDatabase db, int idCuenta) {

		int estilo = 0;

		Cursor c1 = db.rawQuery("select * from Cuentas where idCuenta="
				+ idCuenta, null);

		if (c1.moveToFirst()) {
			estilo = c1.getInt(2);
		}
		return estilo;
	}

	public List<Cuenta> getCuentas(SQLiteDatabase db) {
		List<Cuenta> listaCuentas = new ArrayList<Cuenta>();
		Cursor c1 = db.rawQuery("select * from Cuentas", null);

		if (c1.moveToFirst()) {
			do {
				Cuenta cuenta = new Cuenta();
				cuenta.setIdCuenta(c1.getString(0));
				cuenta.setDescCuenta(c1.getString(1));
				cuenta.setSexo(c1.getInt(2));
				cuenta.setIdIcon(c1.getInt(3));

				listaCuentas.add(cuenta);
			} while (c1.moveToNext());
		}
		return listaCuentas;
	}

	public boolean editCuenta(SQLiteDatabase db, String descripcion,
							  String idCuenta, int idIcon) {
		try {
			ContentValues values = new ContentValues();
			values.put("nombre", descripcion);
			values.put("idIcon", idIcon);
			db.update("Cuentas", values, "idCuenta=?",
					new String[]{idCuenta});

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addCuenta(SQLiteDatabase db, String cuenta, int sexo, int idIcon) {
		try {
			db.execSQL("INSERT INTO Cuentas VALUES(null, '" + cuenta + "', '"
					+ sexo + "', '" + idIcon + "')");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteCuenta(SQLiteDatabase db, String idCuenta) {
		try {
			db.delete("Cuentas", "idCuenta=?", new String[] { idCuenta });

			ArrayList<Prenda> listPrendas = getPrendas(db,
					Integer.parseInt(idCuenta));

			// Borramos la foto del direcctorio
			File dbFile = new File(Environment.getExternalStorageDirectory(),
					"/Closfy/Prendas");

			for (Prenda prenda : listPrendas) {
				if (prenda.getIdFoto() != null) {
					Uri tmpImgUri = Uri.fromFile(new File(dbFile, prenda
							.getIdFoto()));
					File fileFoto = new File(tmpImgUri.getPath());
					if (fileFoto.exists()) {
						fileFoto.delete();
					}
				}
			}

			db.delete("Prendas", "idCuenta=?", new String[] { idCuenta });
			db.delete("Looks", "idCuenta=?", new String[] { idCuenta });
			db.delete("Calendario", "idCuenta=?", new String[] { idCuenta });

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean comprobarTablasVersionInicial(SQLiteDatabase db) {
		// consultamos si existe una nomina del mes actual
		try {
			Cursor prendas = db.rawQuery("select * from Prendas", null);
			if (prendas.moveToFirst()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public boolean comprobarTablaLookPrendasCreada(SQLiteDatabase db) {
		// consultamos si existe una nomina del mes actual
		try {
			Cursor tabla = db.rawQuery("SELECT * FROM Look_Prendas", null);

			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public boolean comprobarTablaAsesoramientosCreada(SQLiteDatabase db) {
		try {
			Cursor tabla = db.rawQuery("SELECT * FROM Asesoramientos", null);

			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public void actualizarBDVersion2(SQLiteDatabase db) {
		ArrayList<Look> listaLooks = new ArrayList<Look>();

		db.execSQL(sqlCreateLookPrendas);
		db.execSQL("ALTER TABLE Looks ADD COLUMN idFoto TEXT NULL");
		db.execSQL("ALTER TABLE Looks ADD COLUMN colorFondo TEXT NULL");

		// Cursor c1 = db.rawQuery("select * from Looks", null);
		//
		// if (c1.moveToFirst()) {
		// do {
		// Look look = new Look();
		// look.setIdLook(c1.getInt(0));
		// look.setCadenaPrendas(c1.getString(2));
		// listaLooks.add(look);
		// } while (c1.moveToNext());
		// }

		// for(int i=0;i<listaLooks.size();i++){
		// Look look = listaLooks.get(i);
		// String[] prendas = look.getCadenaPrendas().split(";");
		// for(int j=0;j<prendas.length;j++){
		// if(!prendas[j].equals("-1")){
		// //Prenda prenda = getPrendaById(db, Integer.parseInt(prendas[j]));
		//
		// }
		// }
		//
		// db.execSQL("UPDATE Movimientos SET colorFondo=0");
		// }

	}

	public void crearTablaAsesoramientos(SQLiteDatabase db) {
		db.execSQL(sqlCreateAsesoramientos);
	}

	public boolean insertarAsesoramientos(SQLiteDatabase db,
			int nAsesoramientos, String orderId) {

		try {
			for (int i = 0; i < nAsesoramientos; i++) {
				String sql = "INSERT INTO Asesoramientos VALUES(" + null
						+ ", '" + orderId + "', " + 0 + ")";
				db.execSQL(sql);
			}
			return true;
		} catch (Exception e) {
			Log.d("Error",
					"Error al insertar los asesoramientos adquiridos en BBDD");
			return false;
		}
	}

	public ArrayList<Asesoramiento> getAsesoramientos(SQLiteDatabase db) {
		ArrayList<Asesoramiento> listaAsesoramientos = new ArrayList<Asesoramiento>();
		try {
			Cursor c1 = db.rawQuery(
					"select * from Asesoramientos where consumido='" + 0 + "'",
					null);
			if (c1.moveToFirst()) {
				do {
					Asesoramiento asesoramiento = new Asesoramiento();
					asesoramiento.setIdAsesoramiento(c1.getInt(0));
					asesoramiento.setIdOrder(c1.getString(1));
					asesoramiento.setConsumido(c1.getInt(2));
					listaAsesoramientos.add(asesoramiento);
				} while (c1.moveToNext());
			}
		} catch (Exception e) {
			Log.d("Error", "Error al recuperar asesoramientos en BBDD");
			return listaAsesoramientos;
		}
		return listaAsesoramientos;
	}

	public boolean consumirAsesoramiento(SQLiteDatabase db,
			String idAsesoramiento) {
		try {
			ContentValues values = new ContentValues();
			values.put("consumido", 1);
			db.update("Asesoramientos", values, "idAsesoramiento=?",
					new String[] { idAsesoramiento });
		} catch (Exception e) {
			return false;
		}
		return true;

	}
}
