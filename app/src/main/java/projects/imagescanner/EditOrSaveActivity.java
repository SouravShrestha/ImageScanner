package projects.imagescanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;

public class EditOrSaveActivity extends AppCompatActivity {

    EditText title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_save);
        title = findViewById(R.id.editDocTitle);
        body = findViewById(R.id.editDoc);

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            ArrayList<String> gotItems = extras.getStringArrayList("passedValues");
            title.setText(gotItems.get(0));
            body.setText(gotItems.get(2));
        }
        title.setSelection(title.getText().length());
        body.setSelection(body.getText().length());

    }
}
