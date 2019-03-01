package projects.imagescanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView title,date;
    ImageView image;

    public MyViewHolder(@NonNull View itemView) {

        super(itemView);

        title = itemView.findViewById(R.id.txtTitle);
        date = itemView.findViewById(R.id.txtDate);
        image = itemView.findViewById(R.id.img_recent);
    }
}
