package com.agba.closfy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agba.closfy.R;
import com.agba.closfy.adapters.TutorialImagesAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    TextView done;
    TextView skip;
    ProgressDialog progDailog;
    Integer[] IMAGES = new Integer[6];
    boolean isSinPublicidad = false;
    boolean isMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isSinPublicidad = extras.getBoolean("isSinPublicidad", false);
            isMenu = extras.getBoolean("isMenu", false);
        }

        Locale locale = Locale.getDefault();
        String languaje = locale.getLanguage();

        if (languaje.equals("es") || languaje.equals("es-rUS")
                || languaje.equals("ca")) {
            IMAGES[0] = R.drawable.bienvenidoes;
            IMAGES[1] = R.drawable.tutorial1es;
            IMAGES[2] = R.drawable.tutorial2es;
            IMAGES[3] = R.drawable.tutorial3es;
            IMAGES[4] = R.drawable.tutorial4es;
            IMAGES[5] = R.drawable.tutorial5es;
        }else{
            IMAGES[0] = R.drawable.bienvenidoen1;
            IMAGES[1] = R.drawable.tutorial1en;
            IMAGES[2] = R.drawable.tutorial2en;
            IMAGES[3] = R.drawable.tutorial3en;
            IMAGES[4] = R.drawable.tutorial4en;
            IMAGES[5] = R.drawable.tutorial5en;
        }

        done = (TextView) findViewById(R.id.botonDone);
        skip = (TextView) findViewById(R.id.botonSkip);

        init();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IniciarTask().execute();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IniciarTask().execute();
            }
        });
    }

    private void init() {

        for (int i = 0; i < IMAGES.length; i++)
            ImagesArray.add(IMAGES[i]);

        mPager = (ViewPager) findViewById(R.id.pager);


        mPager.setAdapter(new TutorialImagesAdapter(TutorialActivity.this, ImagesArray));


        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        //indicator.setRadius(5 * density);

        NUM_PAGES = IMAGES.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        /*Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(Update);
			}
		}, 3000, 3000);*/

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

                if (position + 1 == IMAGES.length) {
                    skip.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.VISIBLE);
                } else {
                    skip.setVisibility(View.VISIBLE);
                    done.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public class IniciarTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(TutorialActivity.this);
            progDailog.setIndeterminate(false);
            progDailog.setMessage(getResources().getString(R.string.cargando));
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Integer... params) {
            if(!isMenu){
                Intent intent = new Intent(TutorialActivity.this, ClosfyActivity.class);
                intent.putExtra("isSinPublicidad", isSinPublicidad);
                startActivity(intent);
            }
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Void result) {
            SharedPreferences prefs = getSharedPreferences("ficheroConf", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("tutorialShowed", true);
            editor.commit();

            progDailog.dismiss();
            finish();
        }
    }


}