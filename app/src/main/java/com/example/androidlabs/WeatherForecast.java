package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class WeatherForecast extends AppCompatActivity {

    private ImageView weather_image;
    private TextView current_temperature;
    private TextView min_temperature;
    private TextView max_temperature;
    private TextView uv_rating;
    private ProgressBar progressBar;
    private static final String ACTIVITY_NAME = "WeatherForecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        //all the widgets from weather forecast activity
        weather_image = findViewById(R.id.current_weather);
        current_temperature = findViewById(R.id.current_temperature);
        min_temperature = findViewById(R.id.min_temperature);
        max_temperature = findViewById(R.id.max_temperature);
        uv_rating = findViewById(R.id.uv_rating);
        progressBar = findViewById(R.id.progress_bar);

        //setting progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        //my urls
        String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
        String UvUrl = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";

        //calling forecastQuery to get values from web page
        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute( weatherUrl, UvUrl);

    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String UV;
        private String min;
        private String max;
        private String current_temp;
        private Bitmap image;
        private String iconName; //code for weather_pic


        protected String doInBackground(String... args) {
            String returnString = null;

            try {
                //url object of server to contact
                URL weatherUrl = new URL(args[0]);
                //open connection
                HttpURLConnection urlConnection = (HttpURLConnection) weatherUrl.openConnection();
                //wait for data
                InputStream inputStreamWeather = urlConnection.getInputStream();

                // create parser for the XML
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput(inputStreamWeather, "UTF-8");

                int EVENT_TYPE;
                while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) { // loop until end of xml doc
                    if (EVENT_TYPE == START_TAG) {
                        String tagName = xpp.getName();
                        switch (tagName) {
                            case "temperature":
                                current_temp = xpp.getAttributeValue(null, "value");
                                publishProgress(25);
                                min = xpp.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = xpp.getAttributeValue(null, "max");
                                publishProgress(75);
                                break;
                            case "weather":
                                iconName = xpp.getAttributeValue(null, "icon");
                        }
                    } xpp.next(); //go to next element
                }


                String fileName = iconName + ".png";

                // check if file is in local storage
                if (!fileExistance(fileName)) {
                    Log.i(ACTIVITY_NAME, "Downloading image from server");
                    image = null;
                    URL url = new URL("http://openweathermap.org/img/w/" + fileName);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());
                    }

                    //saving image to local storage
                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i(ACTIVITY_NAME, "Image saved to local storage");
                } else {
                    Log.i(ACTIVITY_NAME, "Downloading from local storage");
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    image = BitmapFactory.decodeStream(fis);
                }
                publishProgress(100);

                // connects to the UV index server
                URL uvUrl = new URL(args[1]);
                HttpURLConnection uvUrlConnection = (HttpURLConnection) uvUrl.openConnection();
                InputStream inStreamUv = uvUrlConnection.getInputStream();

                BufferedReader jsonReader = new BufferedReader(new InputStreamReader(inStreamUv, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder(100);
                String line;
                while ((line = jsonReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jObj = new JSONObject(result);
                UV = String.valueOf(jObj.getDouble("value"));

            } catch (Exception e) {
                returnString = "error";
            }

            return returnString;
        }

        //method to check if image file already in local storage
        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        @Override
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            char celsiusSymbol = 0x2103; //celsius symbol
            //update values
            weather_image.setImageBitmap(image);
            current_temperature.setText(String.format("Current Temp: %s%c", current_temp, celsiusSymbol));
            max_temperature.setText(String.format("High: %s%c", max, celsiusSymbol));
            min_temperature.setText(String.format("Low: %s%c", min, celsiusSymbol));
            uv_rating.setText(String.format("UV Index: %s", UV));
            //hide progress bar
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

    }
}
