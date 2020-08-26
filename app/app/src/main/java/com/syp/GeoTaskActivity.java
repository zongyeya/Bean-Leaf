package com.syp;


//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.syp.model.Singleton;
import com.syp.ui.CafeFragment;
import com.syp.ui.GeoTask;

import static com.syp.MainActivity.mainActivity;

/*Replace YOUR_API_KEY in String url with your API KEY obtained by registering your application with google.

 */


public class GeoTaskActivity extends AppCompatActivity implements GeoTask.Geo {
    EditText edttxt_from,edttxt_to;
    Button btn_get;
    String str_from,str_to;
    TextView tv_result1,tv_result2;
    public String timeInfo;
    public String distInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        Intent i = getIntent();
        str_from= i.getStringExtra("originQuery");
        str_to=i.getStringExtra("destQuery");
        Log.d("HiTag","Message showing nothing");
        Log.d("Hello Tag",str_from);
        Log.d("Hello Tag", str_to);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + str_from + "&destinations=" + str_to + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyBHnNZ__hAm9hTUERw2qMHPJB7rAaDLjMo";
        new GeoTask(GeoTaskActivity.this).execute(url);



//        btn_get.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                str_from=edttxt_from.getText().toString();
//                Intent i = getIntent();
//                str_from= i.getStringExtra("originQuery");
//                str_to=i.getStringExtra("destQuery");
//                Log.d("HiTag","Message showing nothing");
//                Log.d("Hello Tag",str_from);
//                Log.d("Hello Tag", str_to);
//                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + str_from + "&destinations=" + str_to + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyBHnNZ__hAm9hTUERw2qMHPJB7rAaDLjMo";
//                new GeoTask(GeoTaskActivity.this).execute(url);
//            }
//        });

    }



    @Override
    public void setDouble(String result) {
        String res[]=result.split(",");
        double min=Double.parseDouble(res[0])/60;
        double dist=Double.parseDouble(res[1])/1000;
        tv_result1.setText("Duration= " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins");
        tv_result2.setText("Distance= " + dist + " kilometers");

        Singleton.get(mainActivity).getDatabase()
                .child("users").child(Singleton.get(mainActivity).getUserId())
                .child("currentOrder")
                .child("distance").setValue(dist);

        Singleton.get(mainActivity).getDatabase()
                .child("users").child(Singleton.get(mainActivity).getUserId())
                .child("currentOrder")
                .child("travelTime").setValue((int) (min / 60) + " hr " + (int) (min % 60) + " mins");



        timeInfo = (int) (min / 60) + " hr " + (int) (min % 60) + " mins";
        distInfo = dist + " kilometers";
        finish();

    }

    public String getDurationData()
    {
        return timeInfo;
    }

    public String getDistInfo()
    {
        return distInfo;
    }

    public void initialize()
    {
        btn_get= (Button) findViewById(R.id.button_get);
        tv_result1= (TextView) findViewById(R.id.textView_result1);
        tv_result2=(TextView) findViewById(R.id.textView_result2);

    }
}
