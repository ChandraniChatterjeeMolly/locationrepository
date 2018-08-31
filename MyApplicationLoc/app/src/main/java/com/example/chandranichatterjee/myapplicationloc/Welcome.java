package com.example.chandranichatterjee.myapplicationloc;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqisland.tutorial.recipes.R;

public class Welcome extends AppCompatActivity {

    private MySharedPreferences pref;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip;
    private Button btnNext;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private TextView[] dots;
    private ImageView img1, img2, img3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[]{
                R.layout.welcome_slide_1,
                R.layout.welcome_slide_2,
                R.layout.welcome_slide_3};

        addBottomDots(0);
        init();
    }

    private void init() {

        pref = new MySharedPreferences(this);
        if (!pref.isFirstLogin()) {
            gotoHome();
            finish();
        } else {
            myViewPagerAdapter = new MyViewPagerAdapter();
            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoHome();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = getItem(+1);
                    if (current < viewPager.getChildCount()) {
                        viewPager.setCurrentItem(current);
                    } else {
                        gotoHome();
                    }
                }
            });
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void gotoHome() {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }


        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorsActive = getResources().getColor(R.color.dot_dark);
        int colorsInactive = getResources().getColor(R.color.dot_light);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    //Adapter
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);


            if (position==0){
                ImageView img1 = (ImageView) view.findViewById(R.id.img1);
                img1.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.slide1_24dp));

            }
            if (position == 1){
                ImageView img2 = (ImageView) view.findViewById(R.id.img2);
                img2.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.slide2_24dp));
            }
            if (position == 2){
                ImageView img3 = (ImageView) view.findViewById(R.id.img3);
                img3.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.slide3_24dp));
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
