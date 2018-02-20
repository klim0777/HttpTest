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

    private Button   mGetButton;
    private ListView mListView;
    private Spinner mSpinner;

    private static List<Item> mArray = new ArrayList<>();
    private String[] mImageLinks;

    private Adapter mAdapter;

    private GetDataTask mGetDataTask;
    private GetBitmapArrayTask mGetBitmapArrayTask;

    private ProgressDialog mDialog;

    private String[] mYears = {"2006","2007","2008","2009","2010","2011",
                              "2012","2013","2014","2015","2016","2017",
                              "2018"};

    private String mUrl = "https://api.spacexdata.com/v2/launches?launch_year=2017";
    private String mUrlBase = "https://api.spacexdata.com/v2/launches?launch_year=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGetButton = (Button) findViewById(R.id.getButton);
        mListView = (ListView) findViewById(R.id.listView);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        // adapter for spinner
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mYears);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setPrompt("select year");
        mSpinner.setSelection(0);

        // changes url according to selected year
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // selected item position + 2006 == selected year
                String urlAdd = String.valueOf(2006 + position);
                mUrl = mUrlBase + urlAdd;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setTitle("Wait please");
                mDialog.setMessage("Downloading data");
                mDialog.show();

                mArray.clear();

                mGetDataTask = new GetDataTask();
                mGetDataTask.execute(mUrl);

            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String linkToOpen = mArray.get(position).getArticleLink();
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
        protected JSONArray doInBackground(String... link) {

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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                // make JsonArray from response
                response = new JSONArray(finalJson);

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
            mImageLinks = new String[response.length()];

            for(int i = 0; i < response.length(); i++) {
                try {
                    JSONObject launch = response.getJSONObject(i);

                    // get details
                    String details = launch.getString("details");

                    // links JSONObject contains imageLink and articleLink
                    JSONObject links = launch.getJSONObject("links");
                    String imageLink = links.getString("mission_patch");
                    String articleLink = links.getString("article_link");

                    // rocket JSONOnbject contains rocket_name
                    JSONObject rocket = launch.getJSONObject("rocket");
                    String rocketName = rocket.getString("rocket_name");

                    // get launchDate and convert to long
                    String launchDateUnix = launch.getString("launch_date_unix");
                    long launchTime = Long.parseLong(launchDateUnix);

                    // convert launchTime to ms
                    final Date date = new Date(launchTime * 1000L);

                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // Set time-zone
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                    // formatting date
                    String formattedLaunchDate = dateFormat.format(date);

                    // save loaded data to item
                    Item item  = new Item();
                    item.setRocketName(rocketName);
                    item.setLaunchDate(formattedLaunchDate);
                    item.setDetails(details);
                    item.setImage(imageLink);
                    item.setArticleLink(articleLink);
                    // save imageLink separately to mImageLinks array
                    // just for sending array as parameter to mGetBitmapArrayTask
                    mImageLinks[i] = imageLink;

                    mArray.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mGetBitmapArrayTask = new GetBitmapArrayTask();
            mGetBitmapArrayTask.execute(mImageLinks);

        }// onPostExecute

    }//GetData

    /***
     * in method onPostExecute creates adapter and sets it to listView
     */
    public class  GetBitmapArrayTask extends AsyncTask<String, Void, Bitmap[]> {
        private Bitmap[] mbitmaps;
        
        @Override
        protected Bitmap[] doInBackground(String[] url) {
            mbitmaps = new Bitmap[mImageLinks.length];

            for (int i = 0; i < mImageLinks.length; i++) {
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

            for(int i = 0; i < mArray.size(); i++) {
                mArray.get(i).setBitmap(mbitmaps[i]);
            }

            mDialog.cancel();

            Activity activity = (Activity) MainActivity.this;

            mAdapter = new Adapter(activity, mArray);
            mListView.setAdapter(mAdapter);
        }
    }

}


