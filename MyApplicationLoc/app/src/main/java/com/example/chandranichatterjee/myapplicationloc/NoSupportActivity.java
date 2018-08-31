package com.example.chandranichatterjee.myapplicationloc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sqisland.tutorial.recipes.R;

import org.w3c.dom.Text;

public class NoSupportActivity extends AppCompatActivity {
    ImageView no_support;
    TextView tv_ok, error_title;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_no_support);

        error_title = (TextView) findViewById(R.id.error_title);

        Bundle b = getIntent().getExtras();
        if (b!= null){
            title = b.getString("error_detail");
        }


        error_title.setText(""+title);

        no_support = (ImageView)findViewById(R.id.img_no_support);
        no_support.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_no_support_24dp));

        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

    }
}
