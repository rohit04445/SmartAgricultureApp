package com.example.smartagricultureapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import pl.pawelkleczkowski.customgauge.CustomGauge;


public class MainActivity extends AppCompatActivity {

    Button btnUpdate;
    //  TextView temp,humidity,moisture;
    ProgressDialog pd;
    private CustomGauge tem;
    private CustomGauge hum;
    private CustomGauge mois;
    TextView temperature, humidity, moisture, motor_suggested;
    boolean isFirstTime;
    ToggleButton motor_button;
    boolean force = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
        btnUpdate = findViewById(R.id.button);
        motor_button = findViewById(R.id.motor_button);
        isFirstTime=true;
        motor_suggested = findViewById(R.id.motor_suggested);
        tem = findViewById(R.id.temp);
        hum = findViewById(R.id.humidity);
        mois = findViewById(R.id.moisture);
        temperature = findViewById(R.id.temperatureView);
        moisture = findViewById(R.id.moistureView);
        humidity = findViewById(R.id.humidityView);
        motor_button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isFirstTime)
            {
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.show();
               if (isChecked) {
                new Thread(() -> {
                    MotorOperation("https://project-iotdata.herokuapp.com/on");
                    pd.dismiss();

                }).start();
                Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                data.motor_status = "ON";
               } else {
                new Thread(() -> {
                    MotorOperation("https://project-iotdata.herokuapp.com/off");
                    pd.dismiss();

                }).start();
                Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                data.motor_status = "OFF";

                   // The toggle is disabled
            }
                update();
            }
            else
            {
                isFirstTime=false;
            }
        });
        btnUpdate.setOnClickListener(v -> {
            force = true;
            new JsonTask().execute(data.url);
        });
        force = true;
        setRepeatingAsyncTask();

    }

    public void MotorOperation(String arg) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(arg);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            case R.id.one: {
                Toast.makeText(getApplicationContext(), "Already Here", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.two: {
                Intent k = new Intent(MainActivity.this, WeatherForecast.class);
                startActivityForResult(k, 0);
                return true;
            }//do something
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 0) {
            update();
        }
    }
    private class JsonTask extends AsyncTask<String, String, JSONObject> {

        protected void onPreExecute() {
            super.onPreExecute();
            if (force) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.show();
                force = false;
            }
        }

        protected JSONObject doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");

                }
                data.allData = new JSONObject(buffer.toString());
                data.sensor_data = data.allData.getJSONObject("sensor_data");
                data.weather_data = data.allData.getJSONArray("weather_data");
                data.motor_status = data.allData.getString("motor_data");
                data.motor_suggested=data.allData.getString("prediction");
                return data.allData;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            update();
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
    }


    private void update() {
        try {
            tem.setValue(Integer.parseInt(data.sensor_data.getString("temperature")));
            hum.setValue(Integer.parseInt(data.sensor_data.getString("humidity")));
            mois.setValue(Integer.parseInt(data.sensor_data.getString("moisture")));
            temperature.setText(String.valueOf(tem.getValue()));
            moisture.setText(String.valueOf(mois.getValue()));
            humidity.setText(String.valueOf(hum.getValue()));
            motor_suggested.setText(data.motor_suggested);
            Log.e("da",data.motor_status);
            motor_button.setChecked(data.motor_status.equals("ON"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        JsonTask jsonTask = new JsonTask();
                        jsonTask.execute(data.url);
                    } catch (Exception e) {
                        // error, do something
                    }
                });
            }
        };

        timer.schedule(task, 0, data.time);  // interval of 2 minute

    }

}