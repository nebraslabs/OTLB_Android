package com.nebrasapps.otlb.historyRecyclerView;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.pojo.DateItem;
import com.nebrasapps.otlb.pojo.GeneralItem;
import com.nebrasapps.otlb.pojo.ListItem;
import com.nebrasapps.otlb.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */





public class HistoryRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<ListItem> consolidatedList = new ArrayList<>();
    private List<ListItem> filterList = new ArrayList<>();
    public HistoryRecyclerAdapter(Activity context, List<ListItem> consolidatedList) {
        this.context = context;
        this.consolidatedList = consolidatedList;
        this.filterList.addAll(consolidatedList);
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            // section header for items inside date
            case ListItem.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.item_history, parent,
                        false);
                viewHolder = new GeneralViewHolder(v1);
                break;
            // section header for date
            case ListItem.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.date_layout, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,
                                 int position) {

        switch (viewHolder.getItemViewType()) {

            case ListItem.TYPE_GENERAL:
                GeneralItem generalItem
                        = (GeneralItem) consolidatedList.get(position);
                GeneralViewHolder generalViewHolder
                        = (GeneralViewHolder) viewHolder;
                final HistoryObject item=generalItem.getPojoOfJsonArray();
                generalViewHolder.dropTxt.setText(item.getDrop());
                generalViewHolder.pickup.setText(item.getPickup());
                generalViewHolder.statusTxt.setText(item.getStatus());
                if(item.getStatus().equalsIgnoreCase("Rejected"))
                {
                    generalViewHolder.statusTxt.setText(context.getResources().getString(R.string.rejected));
                    generalViewHolder.statusTxt.setTextColor(ContextCompat.getColor(context,R.color.error_clr));
                } else if(item.getStatus().equalsIgnoreCase("Cancelled"))
            {
                generalViewHolder.statusTxt.setText(context.getResources().getString(R.string.cancelled));
                generalViewHolder.statusTxt.setTextColor(ContextCompat.getColor(context,R.color.cancelled_clr));
            }else
                {
                    generalViewHolder.statusTxt.setText(context.getResources().getString(R.string.accepted));
                    generalViewHolder.statusTxt.setTextColor(ContextCompat.getColor(context,R.color.accept_clr));

                }
                if(item.getTime()!=null){
                    generalViewHolder.time.setText(item.getDatetime());
                }
                //Making view gone if no destination available
                if(item.getDrop().length()<=0)
                {
                    generalViewHolder.dropLay.setVisibility(View.GONE);

                }else
                {
                    generalViewHolder.dropLay.setVisibility(View.VISIBLE);

                }

                //SETTING IMAGE DEPENDING ON SERVICE
                if(item.getService().equalsIgnoreCase("1"))
                {
                   generalViewHolder.service.setImageResource(R.drawable.taxi);
                }else if(item.getService().equalsIgnoreCase("4"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.power);
                }else if(item.getService().equalsIgnoreCase("5"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.plumbing);
                }else if(item.getService().equalsIgnoreCase("8"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.wheel);
                }else if(item.getService().equalsIgnoreCase("Maintenance"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.home_maintenance);
                }else if(item.getService().equalsIgnoreCase("7"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.petrol);
                }else if(item.getService().equalsIgnoreCase("2"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.delivery);
                }else if(item.getService().equalsIgnoreCase("14"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.water_trucks);
                }else if(item.getService().equalsIgnoreCase("3"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.two_trucks);
                }else if(item.getService().equalsIgnoreCase("11"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.car_battery);
                }else if(item.getService().equalsIgnoreCase("13"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.car_repair);
                }else if(item.getService().equalsIgnoreCase("10"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.car_cleaning);
                }else if(item.getService().equalsIgnoreCase("12"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.car_unlock);
                }else if(item.getService().equalsIgnoreCase("6"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.housekeeper);
                }else if(item.getService().equalsIgnoreCase("9"))
                {
                    generalViewHolder.service.setImageResource(R.drawable.oil_change);
                }

                break;

            case ListItem.TYPE_DATE:
                DateItem dateItem
                        = (DateItem) consolidatedList.get(position);
                DateViewHolder dateViewHolder
                        = (DateViewHolder) viewHolder;
                dateViewHolder.title.setText(DateTimeUtil.getFormattedDate(dateItem.getDate()));
                // Populate date item data here

                break;
        }
    }


}

// ViewHolder for date row item
class DateViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    public DateViewHolder(View v) {
        super(v);
        title = (TextView) itemView.findViewById(R.id.section_text);

    }
}

// View holder for general row item
class GeneralViewHolder extends RecyclerView.ViewHolder {
    public  LinearLayout mainLay;
    public TextView rideId;
    public TextView pickup;
    public TextView dropTxt;
    public TextView statusTxt;
    public ImageView service;
    public TextView time;
    public RelativeLayout dropLay;
    public GeneralViewHolder(View v) {
        super(v);
        pickup = (TextView) itemView.findViewById(R.id.pickup);
        dropTxt = (TextView) itemView.findViewById(R.id.drop);
        mainLay=(LinearLayout)itemView.findViewById(R.id.main_lay);
        dropLay = (RelativeLayout) itemView.findViewById(R.id.droplay);
        time = (TextView) itemView.findViewById(R.id.time);
        service = (ImageView) itemView.findViewById(R.id.service);
        statusTxt = (TextView) itemView.findViewById(R.id.status);
    }


}