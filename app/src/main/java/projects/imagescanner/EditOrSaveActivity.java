package projects.imagescanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class EditOrSaveActivity extends AppCompatActivity {

    EditText title, body;
    Button bSaveChanges;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_save);
        title = findViewById(R.id.editDocTitle);
        body = findViewById(R.id.editDoc);
        bSaveChanges = findViewById(R.id.bCopy);
        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            ArrayList<String> gotItems = extras.getStringArrayList("passedValues");
            title.setText(gotItems.get(0));
            body.setText(gotItems.get(2));
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
                FancyToast.makeText(getApplicationContext(),"Changes Saved Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
            }
        });

    }
}
