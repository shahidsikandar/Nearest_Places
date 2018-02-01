package com.example.administrator.nearestplaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.nearestplaces.modal.NearestPlaces;

import java.util.ArrayList;

/**
 * Created by Administrator on 1/10/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NearestPlaces> nearestPlacesArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView placesView;
        public TextView placesAddress;
        public MyViewHolder(View itemView) {
            super(itemView);

            placesView = (TextView) itemView.findViewById(R.id.places_nearest);
            placesAddress = (TextView) itemView.findViewById(R.id.places_address);

        }

        public void initData(NearestPlaces myNearestPlaces){

            placesView.setText(myNearestPlaces.getPlaces());
            placesAddress.setText(myNearestPlaces.getVicinity());
        }
    }

    public RecyclerAdapter(Context context, ArrayList<NearestPlaces> nearestPlacesArrayList) {
        this.context = context;
        this.nearestPlacesArrayList = nearestPlacesArrayList;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<NearestPlaces> getNearestPlacesArrayList() {
        return nearestPlacesArrayList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setNearestPlacesArrayList(ArrayList<NearestPlaces> nearestPlacesArrayList) {
        this.nearestPlacesArrayList = nearestPlacesArrayList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.initData(nearestPlacesArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return nearestPlacesArrayList.size();
    }

}
