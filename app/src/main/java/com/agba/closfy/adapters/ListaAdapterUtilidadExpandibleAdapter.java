package com.agba.closfy.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.activities.DeleteUtilidadActivity;
import com.agba.closfy.activities.EditUtilidadActivity;
import com.agba.closfy.modelo.Utilidad;

public class ListaAdapterUtilidadExpandibleAdapter extends
		BaseExpandableListAdapter {

	private Context _context;
	private ArrayList<Utilidad> parentItems;
	private Utilidad utilidad = new Utilidad();
	private int estilo;

	public ListaAdapterUtilidadExpandibleAdapter(Context context,
			ArrayList<Utilidad> parents, int estiloAux) {
		_context = context;
		this.parentItems = parents;
		estilo = estiloAux;

	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ImageView editIcon;
		ImageView deleteIcon;
		TextView editText;
		TextView deleteText;
		LinearLayout layoutEdit;
		LinearLayout layoutDelete;

		utilidad = (Utilidad) this.parentItems.get(groupPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.child_utilidad, null);
		}

		layoutEdit = (LinearLayout) convertView.findViewById(R.id.layoutEdit);
		layoutDelete = (LinearLayout) convertView
				.findViewById(R.id.layoutDelete);
		editIcon = (ImageView) convertView.findViewById(R.id.editIcon);
		deleteIcon = (ImageView) convertView.findViewById(R.id.deleteIcon);
		editText = (TextView) convertView.findViewById(R.id.editText);
		deleteText = (TextView) convertView.findViewById(R.id.deleteText);

		
		if(estilo == 1){
			editIcon.setBackgroundResource(R.drawable.edit_utilidad_azul);
			deleteIcon.setBackgroundResource(R.drawable.delete_utilidad_azul);
			editText.setTextColor(_context.getResources().getColor(R.color.azul));
			deleteText.setTextColor(_context.getResources().getColor(R.color.azul));
		}
		
		layoutEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context, EditUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				intent.putExtra("textEdit", utilidad.getNombre());
				_context.startActivity(intent);
			}
		});

		editIcon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context, EditUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				intent.putExtra("textEdit", utilidad.getNombre());
				_context.startActivity(intent);
			}

		});

		editText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context, EditUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				intent.putExtra("textEdit", utilidad.getNombre());
				_context.startActivity(intent);
			}

		});

		layoutDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context,
						DeleteUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				intent.putExtra("textEdit", utilidad.getNombre());
				_context.startActivity(intent);
			}
		});

		deleteIcon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context,
						DeleteUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				intent.putExtra("textEdit", utilidad.getNombre());
				_context.startActivity(intent);
			}

		});

		deleteText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context,
						DeleteUtilidadActivity.class);
				intent.putExtra("id", utilidad.getIdUtilidad());
				_context.startActivity(intent);
			}

		});

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		float total = 0;

		Utilidad uti = parentItems.get(groupPosition);

		String headerTitle = uti.getNombre();
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.lista_utilidad, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.textUtilidades);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}