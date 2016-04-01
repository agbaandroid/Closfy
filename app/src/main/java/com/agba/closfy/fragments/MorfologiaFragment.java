package com.agba.closfy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.agba.closfy.R;
import com.agba.closfy.activities.MorfologiaMujer1Activity;
import com.agba.closfy.activities.MorfologiaMujer2Activity;
import com.agba.closfy.activities.MorfologiaMujer3Activity;
import com.agba.closfy.activities.MorfologiaMujer4Activity;
import com.agba.closfy.activities.MorfologiaMujer5Activity;

public class MorfologiaFragment extends Fragment {
	private static final String KEY_CONTENT = "MorfologiaFragment:Content";
	private String mContent = "???";
	private ImageView morfologia1;
	private ImageView morfologia2;
	private ImageView morfologia3;
	private ImageView morfologia4;
	private ImageView morfologia5;

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

		return inflater.inflate(R.layout.morfologia, container, false);
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

		morfologia1 = (ImageView) getView().findViewById(R.id.morfologia1);
		morfologia2 = (ImageView) getView().findViewById(R.id.morfologia2);
		morfologia3 = (ImageView) getView().findViewById(R.id.morfologia3);
		morfologia4 = (ImageView) getView().findViewById(R.id.morfologia4);
		morfologia5 = (ImageView) getView().findViewById(R.id.morfologia5);

		morfologia1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MorfologiaMujer1Activity.class);
				startActivity(intent);
			}
		});

		morfologia2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MorfologiaMujer2Activity.class);
				startActivity(intent);
			}
		});

		morfologia3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MorfologiaMujer3Activity.class);
				startActivity(intent);
			}
		});

		morfologia4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MorfologiaMujer4Activity.class);
				startActivity(intent);
			}
		});

		morfologia5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MorfologiaMujer5Activity.class);
				startActivity(intent);
			}
		});

	}

}
