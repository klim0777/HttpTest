package klim.developer.com.httptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button   getButton;
    private ListView listView;
    private Spinner spinner;

    private static List<Item> array = new ArrayList<>();
    private String[] imageLinks;

    private Adapter adapter;

    private GetDataTask getDataTask;
    private GetBitmapArrayTask getBitmapArrayTask;

    private ProgressDialog dialog;

    private String[] years = {"2006","2007","2008","2009","2010","2011",
                              "2012","2013","2014","2015","2016","2017",
                              "2018"};

    private String url = "https://api.spacexdata.com/v2/launches?launch_year=2017";
    private String urlBase = "https://api.spacexdata.com/v2/launches?launch_year=";

    final Activity ac = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getButton = (Button) findViewById(R.id.getButton);
        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinner);

        // adapter for spinner
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setPrompt("select year");
        spinner.setSelection(0);

        // changes url according to selected year
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String urlAdd = String.valueOf(2006+position);
                url = urlBase + urlAdd;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Wait please");
                dialog.setMessage("Downloading data");
                dialog.show();

                array.clear();

                getDataTask = new GetDataTask();
                getDataTask.execute(url);

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String linkToOpen = array.get(position).getArticleLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(linkToOpen));
                startActivity(intent);
            }
        });
    }


    /***
     * onPostExecute creates GetBitMapArrayTask object
     * and calls .execute
     */
    public class GetDataTask extends AsyncTask<String, Void, JSONArray> {

        // get JSONArray
        @Override
        protected JSONArray doInBackground(String... link){

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            JSONArray response = new JSONArray();

            try {
                URL url = new URL(link[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                // make JsonArray from response
                response = new JSONArray(finalJson);
                Log.d("My log","array created");

                return response;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        // get data from loaded JSONArray
        @Override
        protected void onPostExecute(JSONArray response) {
            super.onPostExecute(response);
            imageLinks = new String[response.length()];

            for(int i = 0; i < response.length(); i++) {
                try {
                    JSONObject launch = response.getJSONObject(i);

                    String flight_number = launch.getString("flight_number");

                    // достаем details
                    String details = launch.getString("details");

                    // links, содержит в себе нужный нам mission_patch
                    JSONObject links = launch.getJSONObject("links");
                    String imageLink = links.getString("mission_patch");
                    String articleLink = links.getString("article_link");

                    // rocket, содержит в себе rocket_name
                    JSONObject rocket = launch.getJSONObject("rocket");
                    String rocketName = rocket.getString("rocket_name");

                    // достаем время запуска ракеты и конвертируем в long
                    String launchDateUnix = launch.getString("launch_date_unix");
                    long launchTime = Long.parseLong(launchDateUnix);

                    // конвертируем секунды launch_time в миллисекунды
                    final Date date = new Date(launchTime * 1000L);
                    // формат времени-даты
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // задание тайм-зоны
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                    // форматирование date
                    String formattedLaunchDate = dateFormat.format(date);

                    // сохранение загруженной информации
                    Item item  = new Item();
                    item.setRocketName(rocketName);
                    item.setLaunchDate(formattedLaunchDate);
                    item.setDetails(details);
                    item.setImage(imageLink);
                    item.setArticleLink(articleLink);
                    imageLinks[i] = imageLink;

                    array.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getBitmapArrayTask = new GetBitmapArrayTask();
            getBitmapArrayTask.execute(imageLinks);

        }// onPostExecute

    }//GetData

    /***
     * in method onPostExecute creates adapter and sets it to listView
     */
    public class  GetBitmapArrayTask extends AsyncTask<String, Void, Bitmap[]> {
        // ImageView imgV;
        private Bitmap[] mbitmaps;

        public GetBitmapArrayTask(){
        }

        @Override
        protected Bitmap[] doInBackground(String[] url) {
            mbitmaps = new Bitmap[imageLinks.length];

            for (int i = 0; i < imageLinks.length; i++) {
                String currentUrl = url[i];
                Bitmap bitmap = null;
                try {
                    InputStream inputStream = new java.net.URL(currentUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    mbitmaps[i] = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return mbitmaps;
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            super.onPostExecute(bitmaps);
            for(int i = 0; i < array.size(); i++) {
                array.get(i).setBitmap(mbitmaps[i]);
            }

            dialog.cancel();

            adapter = new Adapter(ac, array);
            listView.setAdapter(adapter);
        }
    }

}


