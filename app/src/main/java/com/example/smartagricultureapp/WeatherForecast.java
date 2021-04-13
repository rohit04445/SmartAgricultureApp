package com.example.smartagricultureapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class WeatherForecast extends AppCompatActivity {

    TextView temp,humidity,wind_speed,type_of_weather;
    LinearLayout horizontalScrollView;
    private CustomGauge tem;
    private CustomGauge hum;
    private CustomGauge wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_weather);
        tem=findViewById(R.id.tempgauge);
        hum=findViewById(R.id.humiditygauge);
        wind=findViewById(R.id.windgauge);
        temp = findViewById(R.id.temperatureViewWeathernew);
        humidity = findViewById(R.id.humidityViewWeathernew);
        type_of_weather= findViewById(R.id.type_of_weather);
        wind_speed = findViewById(R.id.windSpeedViewWeathernew);
        horizontalScrollView=findViewById(R.id.horizontalScrollBar);

        update();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.one:
            {
                this.finish();
                return true;
            }
            case R.id.two: {
                Toast.makeText(getApplicationContext(),"Already Here", Toast.LENGTH_SHORT).show();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void update() {
        try {
            JSONObject tmp=data.weather_data.getJSONObject(0);
            tem.setValue(Math.round(Float.parseFloat(tmp.getString("temp"))));
            hum.setValue(Math.round(Float.parseFloat(tmp.getString("humidity"))));
            wind.setValue(Math.round(Float.parseFloat(tmp.getString("wind_speed"))));
            temp.setText(String.valueOf(tem.getValue()));
            humidity.setText(String.valueOf(hum.getValue()));
            wind_speed.setText(String.valueOf(wind.getValue()));
            String s1=tmp.getString("type_of_weather");
            s1=s1.toUpperCase();
            type_of_weather.setText(s1);
            horizontalScrollView.removeAllViews();
            for (int i=1;i<15;i++)
            {
                LinearLayout l=new LinearLayout(WeatherForecast.this);
                l.setLayoutParams(new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
                l.setOrientation(LinearLayout.VERTICAL);
                JSONObject t=data.weather_data.getJSONObject(i);


                TextView temp=new TextView(WeatherForecast.this);
                temp.setText((t.getString("temp")+" (°C)"));

                TextView humid=new TextView(WeatherForecast.this);
                humid.setText((t.getString("humidity")+"% (H)"));

                TextView type_of_weather=new TextView(WeatherForecast.this);
                String s=t.getString("type_of_weather");
                s=s.toUpperCase();
                type_of_weather.setText(s);


                TextView wind=new TextView(WeatherForecast.this);
                wind.setText((t.getString("wind_speed")+" (m/s)"));

                TextView time=new TextView(WeatherForecast.this);
                time.setText(t.getString("timestamp"));


                l.addView(time);
                l.addView(temp);
                l.addView(humid);
                l.addView(wind);
                l.addView(type_of_weather);
                horizontalScrollView.addView(l);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
