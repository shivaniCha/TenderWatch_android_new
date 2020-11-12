package com.tenderWatch.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.ResponseNotifications;
import com.tenderWatch.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lcom47 on 1/1/18.
 */

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ResponseNotifications> countryNameList;
    private String test;
    private ArrayList<String> selectedItem = new ArrayList<String>();
    private boolean isCheckItem = false;

    public NotificationAdapter(Context context, ArrayList<ResponseNotifications> countryNameList, String test) {
        this.context = context;
        this.countryNameList = countryNameList;
        this.test = test;
    }


    public ArrayList<String> getCheckedItem() {
        return selectedItem;
    }

    @Override
    public int getCount() {
        return countryNameList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_notification, null, true);


            holder.txtCountryName = convertView.findViewById(R.id.notification);
            holder.txtTime = convertView.findViewById(R.id.time);
            holder.spanText = new SpannableString(countryNameList.get(position).getMessage());


            holder.flag_img = convertView.findViewById(R.id.not_tender_image2);
            holder.imgChecked = convertView.findViewById(R.id.round_checked);
            holder.imgUnchecked = convertView.findViewById(R.id.round);
            holder.rowID = convertView.findViewById(R.id.rowID);

            convertView.setTag(holder);
        }
        else {
            // The getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        convertView = inflater.inflate(R.layout.layout_notification, parent, false);



        if (!countryNameList.get(position).getRead()) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.unread_notification));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }

        if (countryNameList.get(position).getSender() != null) {

            String email = countryNameList.get(position).getSender().getEmail();
            if (countryNameList.get(position).getMessage().contains(email)) {
                //Spannable spanText = new SpannableString(countryNameList.get(position).getMessage());

                // spanText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, changeString.length(), 0);
                try {
                    holder.spanText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), countryNameList.get(position).getMessage().indexOf(email), email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        String s1 = "";
        try {
            if (countryNameList.get(position).getMessage().split("\"").length > 0)
                s1 = countryNameList.get(position).getMessage().split("\"")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s2 = "";
        try {
            if (countryNameList.get(position).getMessage().split("\"").length > 1)
                s2 = countryNameList.get(position).getMessage().split("\"")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        int y = s2.length();
        int x = s1.length();
        int p = x + y + 1;
        String s3 = "";
        try {
            if (countryNameList.get(position).getMessage().split("\"").length > 2)
                s3 = countryNameList.get(position).getMessage().split("\"")[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        int z = s3.length();
        // spanText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, changeString.length(), 0);
        try {
            if (countryNameList.get(position).getMessage().split("\"").length > 0)
                holder.spanText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), x + 1, p, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (countryNameList.get(position).getOthers() != null) {
                if (countryNameList.get(position).getOthers().getCountry() != null) {
                    for (int i = 0; i < countryNameList.get(position).getOthers().getCountry().size(); i++) {
                        try {
                            if (countryNameList.get(position).getMessage().contains(countryNameList.get(position).getOthers().getCountry().get(i))) {
                                int from = countryNameList.get(position).getMessage().indexOf(countryNameList.get(position).getOthers().getCountry().get(i));
                                int to = from + countryNameList.get(position).getOthers().getCountry().get(i).length();
                                holder.spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), from, to, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!TextUtils.isEmpty(countryNameList.get(position).getOthers().getReference())) {
                    holder.spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), countryNameList.get(position).getMessage().indexOf(countryNameList.get(position).getOthers().getReference()), countryNameList.get(position).getOthers().getReference().length() + countryNameList.get(position).getMessage().indexOf(countryNameList.get(position).getOthers().getReference()), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.txtCountryName.setText(holder.spanText);
        holder.txtTime.setText(countryNameList.get(position).getCreatedAt().split("T")[0]);
        try {
            if (countryNameList.get(position).getSender() != null)

                if (countryNameList.get(position).getSender().getProfilePhoto().equalsIgnoreCase("result")){
                    Picasso.with(context).load(countryNameList.get(position).getSender().getProfilePhoto()).into(holder.flag_img);
                }
//                    Picasso.with(context).load(countryNameList.get(position).getSender().getProfilePhoto()).placeholder(context.getResources().getDrawable(R.drawable.avtar)).error(context.getResources().getDrawable(R.drawable.avtar)).into(holder.flag_img);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (context instanceof MainDrawer) {
            isCheckItem = !MainDrawer.editMenu.getTitle().equals("Edit");
        } else if (context instanceof ClientDrawer) {
            isCheckItem = !ClientDrawer.editMenu.getTitle().equals("Edit");
        }

        if (isCheckItem) {
            if (selectedItem.contains(countryNameList.get(position).getId())) {
                holder.imgUnchecked.setVisibility(View.GONE);
                holder.imgChecked.setVisibility(View.VISIBLE);
            } else {
                holder.imgUnchecked.setVisibility(View.VISIBLE);
                holder.imgChecked.setVisibility(View.GONE);
            }
        } else {
            holder.imgChecked.setVisibility(View.GONE);
            holder.imgUnchecked.setVisibility(View.GONE);
        }

        holder.imgChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgUnchecked.setVisibility(View.VISIBLE);
                holder.imgChecked.setVisibility(View.GONE);
                setCheckedItem(countryNameList.get(position).getId());
            }
        });

        holder.imgUnchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgChecked.setVisibility(View.VISIBLE);
                holder.imgUnchecked.setVisibility(View.GONE);
                setCheckedItem(countryNameList.get(position).getId());
            }
        });

        return convertView;
    }

    public void setCheckedItem(String i) {
        if (!selectedItem.contains(i)) {
            selectedItem.add(i);
        } else {
            selectedItem.remove(i);
        }
    }

    private class ViewHolder {

        TextView txtCountryName;
        TextView txtTime;
        Spannable spanText;
        CircleImageView flag_img ;
        ImageView imgChecked ;
        ImageView imgUnchecked;
        TextView rowID;
    }


    public void setSelectionAll() {
        for (ResponseNotifications nameList :countryNameList) {
            setCheckedItem(nameList.getId());
        }
    }
}
