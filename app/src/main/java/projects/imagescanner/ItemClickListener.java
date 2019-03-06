package projects.imagescanner;

import android.view.View;
import android.widget.ImageView;

public interface ItemClickListener {
    public void onClick(View view, int position, ImageView image);
}