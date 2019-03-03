package projects.imagescanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapterRecent extends RecyclerView.Adapter<MyAdapterRecent.MyViewHolder> {

    private ArrayList<RecentItems> list;
    private ItemClickListener clickListener;

    public MyAdapterRecent(ArrayList<RecentItems> recentItems){
        list = recentItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.each_recent_image, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.image.setImageResource(list.get(i).getImage());
        myViewHolder.title.setText(list.get(i).getTitle());
        myViewHolder.date.setText(list.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title,date;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            date = itemView.findViewById(R.id.txtDate);
            image = itemView.findViewById(R.id.img_recent);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}
