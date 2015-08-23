package com.agba.closfy.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;

public class DeleteUtilidadActivity extends Activity {

	private SQLiteDatabase db;
	private final String BD_NOMBRE = "BDClosfy";
	private GestionBBDD gestion = new GestionBBDD();
	static final int MENSAJE_CONFIRMACION = 3;
	private int idUti;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.edit_delete);
		// Abrimos la base de datos si no esta abierta
		db = this.openOrCreateDatabase(BD_NOMBRE, 1, null);
		gestion = new GestionBBDD();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			idUti = extras.getInt("id");
		}

		onCreateDialog(MENSAJE_CONFIRMACION);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		switch (id) {
		case MENSAJE_CONFIRMACION:
			builder.setTitle(getResources()
					.getString(R.string.eliminarUtilidad));
			builder.setIcon(R.drawable.ic_delete);
			builder.setMessage(getResources().getString(R.string.msnEliminar));
			builder.setCancelable(false);
			builder.setPositiveButton(
					getResources().getString(R.string.eliminar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							boolean ok = gestion.deleteUtilidad(db, String.valueOf(idUti));
							if (ok) {
								Context context = getApplicationContext();
								CharSequence textMsg = getResources()
										.getString(R.string.deleteUtilidadOK);
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, textMsg,
										duration);
								toast.show();
								finish();
							} else {
								Context context = getApplicationContext();
								CharSequence textMsg = getResources()
										.getString(R.string.deleteUtilidadKO);
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, textMsg,
										duration);
								toast.show();
								finish();
							}
							dialog.cancel();
						}
					}).setNegativeButton(
					getResources().getString(R.string.cancelar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							DeleteUtilidadActivity.this.setResult(0);
							finish();
						}
					});
			alert = builder.create();
			alert.show();
			break;
		}
		return null;
	}
}