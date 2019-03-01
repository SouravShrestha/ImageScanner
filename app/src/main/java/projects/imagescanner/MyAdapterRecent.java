package projects.imagescanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyAdapterRecent extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<RecentItems> list;

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
        return list.size();
    }
}
