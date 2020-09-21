package com.journey.okcollectandemo.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.journey.okcollectandemo.R;
import com.journey.okcollectandemo.activities.MainActivity;
import com.journey.okcollectandemo.datamodel.AddressItem;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ListViewHolder> {
    private static final String TAG = "AddressListAdapter";
    private final List<AddressItem> data;
    private MainActivity context;

    public AddressListAdapter(MainActivity context, List<AddressItem> rowItem) {
        this.context = context;
        this.data = rowItem;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.addressitem, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder viewHolder, final int position) {
        final AddressItem row_pos = data.get(position);
        try {
            viewHolder.displaytitleTV.setText(displayText(row_pos));
            /*
            String created = row_pos.getCreatedAt().toString();
            try {
                if (row_pos.getString("status") != null) {
                    viewHolder.statusTV.setText("Status: " + row_pos.getString("status"));
                } else {
                    viewHolder.statusTV.setText("Status: ");
                }
            } catch (Exception e) {
                displayLog("status error " + e.toString());
            }
            try {
                viewHolder.scoreTV.setText("Score: " + row_pos.getNumber("score").toString());
            } catch (Exception e) {
                viewHolder.scoreTV.setText("Score: ");
            }
            viewHolder.createdTV.setText("Created: " + created.substring(0, 20));

            try {
                Date date1 = row_pos.getCreatedAt();
                Date date2 = new Date(System.currentTimeMillis());
                long diffInMillisec = date2.getTime() - date1.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec);
                long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillisec);
                long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
                long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
                String duration = diffInDays + " days " + (diffInHours % 24) + " hours " + (diffInMin % 60) + " mins";
                viewHolder.durationTV.setText(duration);
            } catch (Exception e) {
                displayLog("duration error " + e.toString());
            }
             */
        } catch (Exception e) {
            displayLog("error getting title and text " + e.toString());
        }

        viewHolder.runListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                displayLog("ualid "+row_pos.getString("okhiId"));
                Intent intent = new Intent(context, GeofenceHistoryActivity.class);
                intent.putExtra("ualId", row_pos.getString("okhiId"));
                intent.putExtra("title", row_pos.getString("title"));
                context.startActivity(intent);
                */
            }
        });
    }

    private String displayText(AddressItem data){
        String displaytext = null;
        String propertyName = data.getPropname();
        String propertyNumber = data.getPropnumber();
        String streetName = data.getStreetName();
        String directions = data.getDirection();
        String title = data.getTitle();
        String subtitle = data.getSubtitle();
        displayLog(data.getUalid()+" propertyname "+propertyName+" propertynumber "+
                propertyNumber+" streetname "+streetName+" directions "+directions+" title "+title+
                " subtitle "+subtitle);
        if(title != null){
            if(title.length() > 0){
                displaytext = title;
            }
        }
        if(displaytext == null){
            if(subtitle != null){
                if(subtitle.length() > 0){
                    displaytext = subtitle;
                }
            }
        }
        if(displaytext == null){
            if(propertyName != null){
                if(propertyName.length() > 0){
                    displaytext = propertyName;
                }
            }
        }
        if(displaytext == null){
            if(streetName != null){
                if(streetName.length() > 0){
                    displaytext = streetName;
                }
            }
        }
        if(displaytext == null){
            if(directions != null){
                if(directions.length() > 0){
                    displaytext = directions;
                }
            }
        }
        return displaytext;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }

    public static final class ListViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout runListItem;
        private TextView displaytitleTV;
        private TextView createdTV;

        public ListViewHolder(View itemView) {
            super(itemView);
            displaytitleTV = itemView.findViewById(R.id.displaytitleTV);
            createdTV = itemView.findViewById(R.id.createdTV);
            runListItem = itemView.findViewById(R.id.addressitem);
        }
    }
}
