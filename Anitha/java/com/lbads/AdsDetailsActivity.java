package com.lbads;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;

public class AdsDetailsActivity extends AppCompatActivity {
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("type")+" Details");
        //Toast.makeText(getApplicationContext(),getIntent().getStringExtra("imgs"),Toast.LENGTH_SHORT).show();
        TextView tvName=(TextView)findViewById(R.id.tvName);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tvName.setText("Name : "+getIntent().getStringExtra("title"));
        TextView tvType=(TextView)findViewById(R.id.tvType);
        tvType.setText("Type : "+getIntent().getStringExtra("type"));
        TextView tvDes=(TextView)findViewById(R.id.tvDes);
        tvDes.setText("Description : "+getIntent().getStringExtra("des"));
        TextView tvLocation=(TextView)findViewById(R.id.tvLocation);
        tvLocation.setText("Location : "+getIntent().getStringExtra("location"));

        TextView tvOffer=(TextView)findViewById(R.id.tvOffer);
        tvOffer.setText("Offer : "+getIntent().getStringExtra("offer"));

        TextView tvTimings=(TextView)findViewById(R.id.tvTimings);
        tvTimings.setText("Timings : "+getIntent().getStringExtra("tim"));

        TextView tvPhno=(TextView)findViewById(R.id.tvPhno);
        tvPhno.setText("PhoneNumber : "+getIntent().getStringExtra("phno"));

        TextView tvFromDate=(TextView)findViewById(R.id.tvFromDate);
        tvFromDate.setText("From Date : "+getIntent().getStringExtra("from_date"));

        TextView tvToDate=(TextView)findViewById(R.id.tvToDate);
        tvToDate.setText("To Date : "+getIntent().getStringExtra("to_date"));

        List<String> urls= Arrays.asList(getIntent().getStringExtra("imgs").split(","));
        MyPager pager=new MyPager(AdsDetailsActivity.this,urls);
        viewPager.setAdapter(pager);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
