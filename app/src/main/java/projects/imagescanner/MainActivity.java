package projects.imagescanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    MyAdapterRecent myAdapterRecent;
    RecyclerView recentList;
    ImageView digital,handwritten;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<RecentItems> recentItemsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recentList = findViewById(R.id.recentView);
        RecentItems recentItem = new RecentItems();
        recentItem.setTitle("First Document");
        recentItem.setImage(R.drawable.img_two);
        recentItem.setDate("28-02-2019");
        RecentItems recentItems = new RecentItems();
        recentItems.setTitle("Second Document");
        recentItems.setImage(R.drawable.img_one);
        recentItems.setDate("30-07-2018");
        recentItemsList.add(recentItem);
        recentItemsList.add(recentItems);
        recentList.setHasFixedSize(true);
        recentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myAdapterRecent = new MyAdapterRecent(recentItemsList);
        recentList.setAdapter(myAdapterRecent);
        digital = findViewById(R.id.digial);
        digital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
            }
        });

        handwritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                UploadPhoto(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void UploadPhoto(Uri resultUri) {
        Toast.makeText(MainActivity.this,resultUri.toString(),Toast.LENGTH_SHORT).show();
    }

    public void addItems(String title,int image,String date) {
        images.add(image);
        titles.add(title);
        dates.add(date);
    }
}
