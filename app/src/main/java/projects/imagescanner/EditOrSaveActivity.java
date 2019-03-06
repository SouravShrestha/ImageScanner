package projects.imagescanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class EditOrSaveActivity extends AppCompatActivity {

    EditText title, body;
    Button bSaveChanges, viewImage;
    int index = 0;
    Bitmap bitmap;
    SharedPreferences sharedPref;
    ArrayList<String> gotItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_save);
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        title = findViewById(R.id.editDocTitle);
        body = findViewById(R.id.editDoc);
        bSaveChanges = findViewById(R.id.bCopy);
        viewImage = findViewById(R.id.tViewImage);
        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            gotItems = extras.getStringArrayList("passedValues");
            title.setText(gotItems.get(0));
            body.setText(gotItems.get(2));
            bitmap = BitmapFactory.decodeByteArray(extras.getByteArray("img"), 0, extras.getByteArray("img").length);
            index = Integer.parseInt(gotItems.get(3));
        }
        title.setSelection(title.getText().length());
        body.setSelection(body.getText().length());

        bSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.recentItemsList.get(index).setBody(body.getText().toString());
                MainActivity.recentItemsList.get(index).setTitle(title.getText().toString());
                MainActivity.myAdapterRecent.notifyDataSetChanged();
                RecentItems tempRecentItems = new RecentItems();
                tempRecentItems.setTitle(gotItems.get(0));
                tempRecentItems.setDate(gotItems.get(1));
                tempRecentItems.setBody(gotItems.get(2));
                tempRecentItems.setImage(gotItems.get(4));
                RecentItems newRecentItems = new RecentItems();
                newRecentItems.setTitle(title.getText().toString());
                newRecentItems.setDate(gotItems.get(1));
                newRecentItems.setBody(body.getText().toString());
                newRecentItems.setImage(gotItems.get(4));
                Gson gson = new Gson();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(gson.toJson(tempRecentItems));
                editor.putString(gson.toJson(newRecentItems),"Anything");
                editor.commit();
                FancyToast.makeText(getApplicationContext(),"Changes Saved Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
            }
        });

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePopup imagePopup = new ImagePopup(EditOrSaveActivity.this);
                imagePopup.setHideCloseIcon(true);
                imagePopup.setImageOnClickClose(true);
                imagePopup.setFullScreen(true);
                imagePopup.setBackgroundColor(Color.parseColor("#00000000"));
                imagePopup.initiatePopup(new BitmapDrawable(getResources(), bitmap));
                imagePopup.viewPopup();
            }
        });

    }

}
