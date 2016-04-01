package com.agba.closfy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.agba.closfy.R;
import com.agba.closfy.activities.InviernoActivity;
import com.agba.closfy.activities.OtonoActivity;
import com.agba.closfy.activities.PrimaveraActivity;
import com.agba.closfy.activities.VeranoActivity;

public class TestColoridoFragment extends Fragment {
	private static final String KEY_CONTENT = "TestColoridoFragment:Content";
	private String mContent = "???";

	LinearLayout layoutInvierno;
	LinearLayout layoutPrimavera;
	LinearLayout layoutOtono;
	LinearLayout layoutVerano;

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

		return inflater.inflate(R.layout.test_colorido, container, false);
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

		layoutInvierno = (LinearLayout) getView().findViewById(
				R.id.layoutInvierno);
		layoutOtono = (LinearLayout) getView().findViewById(R.id.layoutOtono);
		layoutVerano = (LinearLayout) getView().findViewById(R.id.layoutVerano);
		layoutPrimavera = (LinearLayout) getView().findViewById(
				R.id.layoutPrimavera);

		layoutInvierno.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						InviernoActivity.class);
				startActivity(intent);
			}
		});

		layoutOtono.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), OtonoActivity.class);
				startActivity(intent);
			}
		});

		layoutVerano.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), VeranoActivity.class);
				startActivity(intent);
			}
		});

		layoutPrimavera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						PrimaveraActivity.class);
				startActivity(intent);
			}
		});

	}

}
