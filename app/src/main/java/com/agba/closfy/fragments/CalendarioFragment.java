package com.agba.closfy.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.SeleccionarLookActivity;
import com.agba.closfy.activities.VerLookActivity;
import com.agba.closfy.adapters.GridAdapterCalendar;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Look;
import com.agba.closfy.util.Util;

public class CalendarioFragment extends Fragment implements OnClickListener {
	private static final String tag = "MyCalendarActivity";

	private TextView currentMonth;
	private TextView fecha;

	int cuentaSeleccionada;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int estilo;

	boolean manianaLibre = false;
	boolean tardeLibre = false;
	boolean nocheLibre = false;

	private TextView addManiana;
	private TextView addTarde;
	private TextView addNoche;
	private LinearLayout editManiana;
	private LinearLayout editTarde;
	private LinearLayout editNoche;

	private ImageView prevMonth;
	private ImageView nextMonth;
	private LinearLayout layoutPrev;
	private LinearLayout layoutNext;
	private GridView calendarView;
	private GridView diasSemView;
	private GridCellAdapter adapter;
	private GridAdapterCalendar adapterDiasSem;
	private Calendar _calendar;
	@SuppressLint("NewApi")
	private int day, month, year, monthActual, yearActual;

	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private final DateFormat dateFormatter = new DateFormat();
	private final String[] monthsText = { "Enero", "Febrero", "Marzo", "Abril",
			"Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre",
			"Noviemvbre", "Diciembre" };

	private String fechaString;

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	final GestionBBDD gestion = new GestionBBDD();

	public View viewAnterior;
	private static final String dateTemplate = "MMMM yyyy";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.my_calendar_view, container, false);
	}

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		// Cuenta seleccionada
		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);
		cuentaSeleccionada = Util.cuentaSeleccionada(getActivity(), prefs);

		_calendar = Calendar.getInstance(Locale.getDefault());
		day = _calendar.get(Calendar.DAY_OF_MONTH);
		month = _calendar.get(Calendar.MONTH) + 1;
		monthActual = _calendar.get(Calendar.MONTH);
		year = _calendar.get(Calendar.YEAR);
		yearActual = _calendar.get(Calendar.YEAR);

		addManiana = (TextView) this.getView().findViewById(
				R.id.botonAddManiana);
		addTarde = (TextView) this.getView().findViewById(R.id.botonAddTarde);
		addNoche = (TextView) this.getView().findViewById(R.id.botonAddNoche);
		editManiana = (LinearLayout) this.getView().findViewById(
				R.id.botonEditManiana);
		editTarde = (LinearLayout) this.getView().findViewById(
				R.id.botonEditTarde);
		editNoche = (LinearLayout) this.getView().findViewById(
				R.id.botonEditNoche);

		addManiana.setOnClickListener(this);
		addTarde.setOnClickListener(this);
		addNoche.setOnClickListener(this);
		editManiana.setOnClickListener(this);
		editTarde.setOnClickListener(this);
		editNoche.setOnClickListener(this);

		registerForContextMenu(editManiana);
		registerForContextMenu(editTarde);
		registerForContextMenu(editNoche);

		Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
				+ year);

		fecha = (TextView) this.getView().findViewById(R.id.fecha);
		rellenarFecha(day, month, year);

		prevMonth = (ImageView) this.getView().findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		layoutPrev = (LinearLayout) this.getView()
				.findViewById(R.id.layoutPrev);
		layoutPrev.setOnClickListener(this);

		currentMonth = (TextView) this.getView()
				.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		nextMonth = (ImageView) this.getView().findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		layoutNext = (LinearLayout) this.getView()
				.findViewById(R.id.layoutNext);
		layoutNext.setOnClickListener(this);

		calendarView = (GridView) this.getView().findViewById(R.id.calendar);
		diasSemView = (GridView) this.getView().findViewById(R.id.diasSem);

		// Initialised
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

		// Initialised
		adapterDiasSem = new GridAdapterCalendar(getActivity());
		adapterDiasSem.notifyDataSetChanged();
		diasSemView.setAdapter(adapterDiasSem);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			estilo = gestion.getEstiloCuenta(db, cuentaSeleccionada);
		}

		if (estilo == 1) {
			cambiarEstiloHombre();
		}

	}

	/** * * @param month * @param year */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth || v == layoutPrev) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);

			if (month == (monthActual + 1) && year == yearActual) {
				rellenarFecha(day, month, year);
			} else {
				rellenarFecha(1, month, year);
			}
		}
		if (v == nextMonth || v == layoutNext) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
			Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);

			if (month == (monthActual + 1) && year == yearActual) {
				rellenarFecha(day, month, year);
			} else {
				rellenarFecha(1, month, year);
			}
		}
		if (v == addManiana) {
			Intent intent = new Intent(getActivity(),
					SeleccionarLookActivity.class);
			intent.putExtra("fecha", fechaString);
			intent.putExtra("hora", 0);
			this.startActivity(intent);
		}

		if (v == addTarde) {
			Intent intent = new Intent(getActivity(),
					SeleccionarLookActivity.class);
			intent.putExtra("fecha", fechaString);
			intent.putExtra("hora", 1);
			this.startActivity(intent);
		}

		if (v == addNoche) {
			Intent intent = new Intent(getActivity(),
					SeleccionarLookActivity.class);
			intent.putExtra("fecha", fechaString);
			intent.putExtra("hora", 2);
			this.startActivity(intent);
		}

		if (v == editManiana) {
			prefs = getActivity().getSharedPreferences("ficheroConf",
					Context.MODE_PRIVATE);
			editor = prefs.edit();
			editor.putInt("horaSeleccionada", 0);
			editor.commit();

			getActivity().openContextMenu(v);
		}

		if (v == editTarde) {
			prefs = getActivity().getSharedPreferences("ficheroConf",
					Context.MODE_PRIVATE);
			editor = prefs.edit();
			editor.putInt("horaSeleccionada", 1);
			editor.commit();

			getActivity().openContextMenu(v);
		}

		if (v == editNoche) {
			prefs = getActivity().getSharedPreferences("ficheroConf",
					Context.MODE_PRIVATE);
			editor = prefs.edit();
			editor.putInt("horaSeleccionada", 2);
			editor.commit();

			getActivity().openContextMenu(v);
		}
	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}

	public void rellenarFecha(int dia, int mes, int anio) {
		String day = String.valueOf(dia);
		String month = String.valueOf(mes);
		if (dia < 10) {
			day = "0" + day;
		}
		if (mes < 10) {
			month = "0" + month;
		}
		fecha.setText(day + " - " + monthsText[mes - 1] + " - "
				+ String.valueOf(anio));
		fechaString = day + "/" + month + "/" + String.valueOf(anio);

		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			manianaLibre = gestion.isDiaLibreHora(db, fechaString, 0,
					cuentaSeleccionada);
			tardeLibre = gestion.isDiaLibreHora(db, fechaString, 1,
					cuentaSeleccionada);
			nocheLibre = gestion.isDiaLibreHora(db, fechaString, 2,
					cuentaSeleccionada);
		}
		db.close();

		if (!manianaLibre) {
			addManiana.setVisibility(8);
			editManiana.setVisibility(0);
		} else {
			addManiana.setVisibility(0);
			editManiana.setVisibility(8);
		}

		if (!tardeLibre) {
			addTarde.setVisibility(8);
			editTarde.setVisibility(0);
		} else {
			addTarde.setVisibility(0);
			editTarde.setVisibility(8);
		}

		if (!nocheLibre) {
			addNoche.setVisibility(8);
			editNoche.setVisibility(0);
		} else {
			addNoche.setVisibility(0);
			editNoche.setVisibility(8);
		}
	}

	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] months = { "Enero", "Febrero", "Marzo", "Abril",
				"Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre",
				"Noviemvbre", "Diciembre" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;

		// Days in Current Month public
		GridCellAdapter(Context context, int textViewResourceId, int month,
				int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			setCurrentMonth(month);
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());
			// Print
			// Month
			printMonth(month, year);
			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * * Prints Month * * @param mm * @param yy
		 */
		private void printMonth(int mm, int yy) {
			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;
			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);
			Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
					+ daysInMonth + " days.");
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());
			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 2;
			if (currentWeekDay == -1) {
				trailingSpaces = 6;
			} else {
				trailingSpaces = currentWeekDay;
			}
			// Log.d(tag, "Week Day:" + currentWeekDay + " is "
			// + getWeekDayAsString(currentWeekDay));
			Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);
			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;
			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				Log.d(tag,
						"PREV MONTH:= "
								+ prevMonth
								+ " => "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
										- trailingSpaces + DAY_OFFSET)
										+ i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY" + "-" + (prevMonth + 1) + "-" + prevYear);
			}
			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()
						&& (monthActual == currentMonth)
						&& (yearActual == year)) {
					list.add(String.valueOf(i) + "-RED" + "-"
							+ (currentMonth + 1) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ (currentMonth + 1) + "-" + yy);
				}
			}
			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ (nextMonth + 1) + "-" + nextYear);
			}
		}

		/**
		 * * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH,
		 * retrieve * ALL entries from a SQLite database for that month. Iterate
		 * over the * List of All entries, and get the dateCreated, which is
		 * converted into * day. * * @param year *
		 * 
		 * @param month
		 *            *
		 * @return
		 */
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
				int month) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}
			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);
			// ACCOUNT FOR SPACING
			Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

			String dayAux = theday;
			if (Integer.parseInt(dayAux) < 10) {
				dayAux = 0 + dayAux;
			}

			String monthAux = themonth;
			if (Integer.parseInt(monthAux) < 10) {
				monthAux = 0 + monthAux;
			}

			String fechaAux = dayAux + "/" + monthAux + "/" + theyear;

			boolean diaLibre = false;
			db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				diaLibre = gestion.isDiaLibre(db, fechaAux, cuentaSeleccionada);
			}
			db.close();

			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}
			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
					+ theyear);

			boolean isGrey = false;
			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));
				if (diaLibre) {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar);
				} else {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_prenda);
				}
				isGrey = true;
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(R.color.black));
				if (diaLibre) {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar);
				} else {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_prenda);
				}
				isGrey = false;
			}
			if (day_color[1].equals("RED")) {
				gridcell.setTextColor(getResources().getColor(R.color.orrange));
				if (diaLibre) {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_marcado);
				} else {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_marcado_prenda);
				}
				viewAnterior = gridcell;
				isGrey = false;
			}

			Calendar c = Calendar.getInstance();
			int mes = c.get(Calendar.MONTH) + 1;
			if (Integer.parseInt(themonth) != mes
					&& Integer.parseInt(theday) == 1 && !isGrey) {
				if (diaLibre) {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_marcado);
				} else {
					gridcell.setBackgroundResource(R.drawable.fondo_day_calendar_marcado_prenda);
				}
				viewAnterior = gridcell;
			}

			return row;
		}

		// Al pulsar un dia del calendario
		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
			Log.e("Selected date", date_month_year);
			try {

				String fecha[] = date_month_year.split("-");
				int dia = Integer.parseInt(fecha[0]);
				int mes = Integer.parseInt(fecha[1]);
				int anio = Integer.parseInt(fecha[2]);

				String dayAux = fecha[0];
				if (Integer.parseInt(dayAux) < 10) {
					dayAux = 0 + dayAux;
				}

				String monthAux = fecha[1];
				if (Integer.parseInt(monthAux) < 10) {
					monthAux = 0 + monthAux;
				}

				String fechaAux = dayAux + "/" + monthAux + "/" + fecha[2];

				boolean diaLibre = false;
				db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
				if (db != null) {
					diaLibre = gestion.isDiaLibre(db, fechaAux,
							cuentaSeleccionada);
				}
				db.close();

				// Cambiamos el fondo del dia que estaba seleccionado
				// anteriormente
				if (viewAnterior != null) {
					String tag = (String) viewAnterior.getTag();
					String[] fechaAnterior = tag.split("-");

					String dayAuxAnterior = fechaAnterior[0];
					if (Integer.parseInt(dayAuxAnterior) < 10) {
						dayAuxAnterior = 0 + dayAuxAnterior;
					}
					String monthAuxAnterior = fechaAnterior[1];
					if (Integer.parseInt(monthAuxAnterior) < 10) {
						monthAuxAnterior = 0 + monthAuxAnterior;
					}
					String fechaAuxAnterior = dayAuxAnterior + "/"
							+ monthAuxAnterior + "/" + fechaAnterior[2];

					boolean diaAnteriorLibre = false;
					db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
					if (db != null) {
						diaAnteriorLibre = gestion.isDiaLibre(db,
								fechaAuxAnterior, cuentaSeleccionada);
					}
					db.close();

					if (diaAnteriorLibre) {
						viewAnterior
								.setBackgroundResource(R.drawable.fondo_day_calendar);
					} else {
						viewAnterior
								.setBackgroundResource(R.drawable.fondo_day_calendar_prenda);
					}
				}

				if (diaLibre) {
					view.setBackgroundResource(R.drawable.fondo_day_calendar_marcado);
				} else {
					view.setBackgroundResource(R.drawable.fondo_day_calendar_marcado_prenda);
				}
				viewAnterior = view;

				rellenarFecha(dia, mes, anio);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}

		public int getCurrentMonth() {
			return currentMonth;
		}

		public void setCurrentMonth(int currentMonth) {
			this.currentMonth = currentMonth;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();

		comprobarHorasDia();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		MenuInflater inflater = (getActivity()).getMenuInflater();
		inflater.inflate(R.menu.menu_pulsacion_calendario, menu);
		menu.setHeaderTitle(getActivity().getResources().getString(
				R.string.elijaOpcion));

	}

	public boolean onContextItemSelected(MenuItem aItem) {

		prefs = getActivity().getSharedPreferences("ficheroConf",
				Context.MODE_PRIVATE);

		int hora = prefs.getInt("horaSeleccionada", 0);

		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case R.id.opc1:
			Intent intent = new Intent(getActivity(), VerLookActivity.class);
			int idLook = -1;
			Look look = new Look();

			db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				idLook = gestion.getLookCalendario(db, fechaString, hora,
						cuentaSeleccionada);
				look = gestion.getLookById(db, idLook);
			}
			db.close();

			intent.putExtra("idLook", look.getIdLook());
			intent.putExtra("temporada", look.getIdTemporada());
			intent.putExtra("utilidades", look.getUtilidades());
			intent.putExtra("favorito", look.getFavorito());

			this.startActivity(intent);
			return true;
		case R.id.opc2:
			db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
			if (db != null) {
				gestion.deleteLookCalendario(db, fechaString, hora,
						cuentaSeleccionada);
			}
			db.close();
			adapter.notifyDataSetChanged();
			comprobarHorasDia();
			return true;
		}
		return false;
	}

	public void comprobarHorasDia() {
		db = getActivity().openOrCreateDatabase(BD_NOMBRE, 1, null);
		if (db != null) {
			manianaLibre = gestion.isDiaLibreHora(db, fechaString, 0,
					cuentaSeleccionada);
			tardeLibre = gestion.isDiaLibreHora(db, fechaString, 1,
					cuentaSeleccionada);
			nocheLibre = gestion.isDiaLibreHora(db, fechaString, 2,
					cuentaSeleccionada);
		}
		db.close();

		if (!manianaLibre) {
			addManiana.setVisibility(8);
			editManiana.setVisibility(0);
		} else {
			addManiana.setVisibility(0);
			editManiana.setVisibility(8);
		}

		if (!tardeLibre) {
			addTarde.setVisibility(8);
			editTarde.setVisibility(0);
		} else {
			addTarde.setVisibility(0);
			editTarde.setVisibility(8);
		}

		if (!nocheLibre) {
			addNoche.setVisibility(8);
			editNoche.setVisibility(0);
		} else {
			addNoche.setVisibility(0);
			editNoche.setVisibility(8);
		}

		rellenarFecha(day, month, year);
	}

	public void cambiarEstiloHombre() {
		addManiana.setBackgroundResource(R.color.azul);
		addTarde.setBackgroundResource(R.color.azul);
		addNoche.setBackgroundResource(R.color.azul);
		editManiana.setBackgroundResource(R.color.azul);
		editTarde.setBackgroundResource(R.color.azul);
		editNoche.setBackgroundResource(R.color.azul);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_setting, menu);
	}

	// Anadiendo funcionalidad a las opciones de menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View view = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog alert;
		switch (item.getItemId()) {
		case R.id.btInfo:
			view = li.inflate(R.layout.info, null);
			builder.setView(view);
			builder.setTitle(getResources().getString(R.string.informacion));
			builder.setIcon(R.drawable.ic_info_azul);
			builder.setCancelable(false);
			builder.setPositiveButton(getResources()
					.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			return true;
		case R.id.btAcerca:
			view = li.inflate(R.layout.acerca, null);
			builder.setView(view);
			builder.setTitle(getResources().getString(R.string.app_name));
			builder.setIcon(R.drawable.icon_app);
			builder.setCancelable(false);
			builder.setPositiveButton(getResources()
					.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}