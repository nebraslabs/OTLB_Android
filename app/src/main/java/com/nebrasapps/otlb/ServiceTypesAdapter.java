package com.nebrasapps.otlb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nebrasapps.otlb.pojo.Services;

import java.util.ArrayList;
import java.util.List;


/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class ServiceTypesAdapter extends RecyclerView.Adapter<ServiceTypesAdapter.ViewHolder>{

    private List<Services> serviceTypesList=new ArrayList<Services>();
    public int selectedItem=-1;
    public boolean selected=false;
    private Context context;
    private OnItemClickListener mAdapterListener;

    public ServiceTypesAdapter(Context ctx, List<Services> items) {
        this.context=ctx;
        this.serviceTypesList = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row_item, parent, false);

        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Services item = serviceTypesList.get(position);

        holder.type.setText(item.getTypeName());

        if(selectedItem==position)
        {
            holder.typeImg.setImageResource(item.getSelectedImg());
            holder.typeImg.setAlpha(1f);
        }else
        {
            holder.typeImg.setImageResource(item.getImage());
            holder.typeImg.setAlpha(0.4f);

        }
        holder.mainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selected) {
                    if (selectedItem == position) {
                        selectedItem = -1;
                    } else {
                        selectedItem = position;
                    }
                    notifyDataSetChanged();
                    if (mAdapterListener != null) {
                        mAdapterListener.OnSelecetd(selectedItem, item.getTypeName());
                    }
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return serviceTypesList.size();
    }



    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        ImageView typeImg;
        View mainLay;
        public ViewHolder(View rowView) {
            super(rowView);
            type = (TextView) itemView.findViewById(R.id.type);
            typeImg = (ImageView) itemView.findViewById(R.id.type_img);
            mainLay = (View) itemView.findViewById(R.id.main_lay);
        }
    }
    public void setOnItemClickListener( OnItemClickListener ctx) {
        mAdapterListener = ctx;
    }
    public interface OnItemClickListener {
        void OnSelecetd(int pos, String type);
    }
}