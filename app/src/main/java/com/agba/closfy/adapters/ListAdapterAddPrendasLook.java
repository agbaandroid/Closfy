package com.agba.closfy.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.database.GestionBBDD;
import com.agba.closfy.modelo.Prenda;
import com.agba.closfy.util.Util;
import com.bumptech.glide.Glide;

public class ListAdapterAddPrendasLook extends BaseAdapter {
    private LayoutInflater mInflater;
    private int mSelectedItem;
    private ArrayList<Prenda> listaPrendas;
    Locale locale = Locale.getDefault();
    String languaje = locale.getLanguage();
    Context contextAux;
    int estiloAux;

    public ListAdapterAddPrendasLook(Context context, ArrayList<Prenda> lista, int estilo) {
        listaPrendas = lista;
        mInflater = LayoutInflater.from(context);
        contextAux = context;
        estiloAux = estilo;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listaPrendas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listaPrendas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView prenda;
        RelativeLayout layoutNavigator;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lista_add_prendas, null);
        }

        prenda = (ImageView) convertView.findViewById(R.id.prenda);
        layoutNavigator = (RelativeLayout) convertView
                .findViewById(R.id.layoutNavigator);
        //

        if (listaPrendas.get(position).getIdFoto() != null && !listaPrendas.get(position).getIdFoto().equals("")) {
            String filePath = Environment.getExternalStorageDirectory()
                    + "/Closfy/Prendas/" + listaPrendas.get(position).getIdFoto();
            Glide.with(contextAux).load(filePath).fitCenter().into(prenda);
        } else if (listaPrendas.get(position).getPrendaBasica() == 1) {
            int drawable = Util.obtenerImagenPrendaBasica(contextAux,
                    listaPrendas.get(position).getIdTipo(), listaPrendas.get(position).getIdPrendaBasica(),
                    2, estiloAux);

            Glide.with(contextAux).load(drawable).fitCenter().into(prenda);
        }
        //prenda.setBackgroundDrawable(listaPrendas.get(position).getFoto());

        return convertView;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
    }

}
