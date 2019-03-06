package projects.imagescanner;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.derohimat.sweetalertdialog.SweetAlertDialog;
import com.google.gson.Gson;
import com.pd.chocobar.ChocoBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    public static MyAdapterRecent myAdapterRecent;
    RecyclerView recentList;
    ImageView digital,handwritten;
    public static ArrayList<RecentItems> recentItemsList = new ArrayList<>();
    SweetAlertDialog pDialog;
    SharedPreferences sharedPref;
    String randomString = "Document";
    String baseUrl = "http://192.168.43.135:5000/";
    String URL = "http://192.168.43.135/index.php";
    int curr = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        recentList = findViewById(R.id.recentView);
        recentList.setHasFixedSize(true);
        recentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myAdapterRecent = new MyAdapterRecent(recentItemsList);
        myAdapterRecent.setClickListener(MainActivity.this);
        recentList.setAdapter(myAdapterRecent);
        digital = findViewById(R.id.digial);
        handwritten = findViewById(R.id.hand);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#EB5757"));
        pDialog.setTitleText("Your File Is Being Processed.!");
        pDialog.setCancelable(false);
        loadSavedItems();
        digital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr = 0;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
                baseUrl = "http://192.168.43.135:90/";
            }
        });

        handwritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr = 1;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
                baseUrl = "http://192.168.43.135:5000/";
            }
        });
    }

    private void loadSavedItems() {

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Gson gson = new Gson();
            RecentItems obj = gson.fromJson(entry.getKey(), RecentItems.class);
            recentItemsList.add(obj);
        }
        myAdapterRecent.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    pDialog.show();
                    UploadImage(MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }

    @Override
    public void onClick(View view, int position, ImageView imageView) {
        RecentItems passingList = recentItemsList.get(position);
        String passingTitle = passingList.getTitle();
        String passingDate = passingList.getDate();
        String passingBody = passingList.getBody();
        String passingImage =passingList.getImage();
        ArrayList<String> listPassed = new ArrayList<>();
        listPassed.add(passingTitle);
        listPassed.add(passingDate);
        listPassed.add(passingBody);
        listPassed.add(String.valueOf(position));
        listPassed.add(passingImage);
        Intent goToEdit = new Intent(this, EditOrSaveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("passedValues",listPassed);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        bundle.putByteArray("img",imageInByte);
        goToEdit.putExtras(bundle);
        startActivity(goToEdit);
    }

    private void UploadImage(final Bitmap bitmap) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s = response.trim();
                if (!s.equalsIgnoreCase("Loi")) {
                    Log.d("finalResult","eq");
                    new CallPython(bitmap).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Failed To Upload !", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                final Snackbar chocoBar = ChocoBar.builder().setBackgroundColor(Color.parseColor("#F44336"))  .setActivity(MainActivity.this).setTextSize(18)
                        .setTextColor(Color.parseColor("#FFFFFF")).setText("Unable To Connect To Server. Please Check Your Connection!")
                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().setActionTextColor(Color.parseColor("#FFFFFF"));
                chocoBar.setAction("Dismiss",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chocoBar.dismiss();
                    }
                });
                chocoBar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String image = getStringImage(bitmap);
                Map<String, String> params = new HashMap<String, String>();
                params.put("IMG", image);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    class CallPython extends AsyncTask{

        Bitmap bitmap;
        String jsonResponse = "";

        public  CallPython(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            String url = Uri.parse(baseUrl).toString();
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else
                {
                    Log.e("someError",Integer.toString(urlConnection.getResponseCode()));
                }
            }catch (IOException e) {
                Log.e("someError","Exception occurred:",e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("finalResult",jsonResponse);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.hide();
            if(jsonResponse!=null && jsonResponse.length()>0) {
                FancyToast.makeText(getApplicationContext(),"Processed Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                RecentItems addItems = new RecentItems();
                randomString = "Document_" + getRandomString();
                addItems.setTitle(randomString);
                addItems.setImage(getStringImage(bitmap));
                addItems.setDate(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                addItems.setBody(jsonResponse);
                commitShared(addItems);
            }
            else {
                final Snackbar chocoBar = ChocoBar.builder().setBackgroundColor(Color.parseColor("#FF9800"))  .setActivity(MainActivity.this).setTextSize(18)
                        .setTextColor(Color.parseColor("#FFFFFF")).setText("There was a problem while processing the image. Please Try Again.")
                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().setActionTextColor(Color.parseColor("#FFFFFF"));
                chocoBar.setAction("Try Again",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pDialog.show();
                        new CallPython(bitmap).execute();
                    }
                });
                chocoBar.show();
            }
            super.onPostExecute(o);
        }
    }

    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private void commitShared(RecentItems recentItems){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(gson.toJson(recentItems),randomString);
        editor.commit();
        recentItemsList.add(recentItems);
        myAdapterRecent.notifyDataSetChanged();
    }

    protected String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 3) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
