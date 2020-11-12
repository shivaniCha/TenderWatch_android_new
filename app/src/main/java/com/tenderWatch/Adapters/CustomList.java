package com.tenderWatch.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tenderWatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomList extends BaseAdapter {
    private Context context;
    public ArrayList<String> countryNameList;
    public ArrayList<String> originalItem;
    private boolean isCategory;
    private List<Boolean> selected=new ArrayList<>();
    private String countryId;
    private List<String> categoryIds;

    public CustomList(Context context, ArrayList<String> countryNameList,boolean isCategory) {
        this.context = context;
        this.countryNameList = countryNameList;
        this.isCategory=isCategory;
        for (int i = 0; i < countryNameList.size(); i++) {
            selected.add(i,false);
        }
    }

    public CustomList(Context context, ArrayList<String> countryNameList,boolean isCategory, String countryId) {
        this.context = context;
        this.countryNameList = countryNameList;
        this.isCategory=isCategory;
        this.countryId=countryId;
        for (int i = 0; i < countryNameList.size(); i++) {
            selected.add(i,false);
        }
    }

    public CustomList(Context context, ArrayList<String> countryNameList,boolean isCategory, List<String> categoryIds) {
        this.context = context;
        this.countryNameList = countryNameList;
        this.isCategory=isCategory;
        this.categoryIds=categoryIds;
        for (int i = 0; i < countryNameList.size(); i++) {
            selected.add(i,false);
        }
    }

    @Override
    public int getCount() {
        return countryNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.country_name, parent, false);

        TextView txtCountryName = (TextView) convertView.findViewById(R.id.name);
        ImageView flag_img = (ImageView) convertView.findViewById(R.id.flag_img);
        final LinearLayout itemLayout=convertView.findViewById(R.id.itemlayout);
        final ImageView ivCheck=convertView.findViewById(R.id.imgtrue);

        txtCountryName.setText(countryNameList.get(position).split("~")[0]);

        Bitmap flag1 = StringToBitMap(countryNameList.get(position).split("~")[1]);
        flag_img.setImageBitmap(flag1);

        if(isCategory){
            if(selected.get(position)){
                selected.set(position,true);
                itemLayout.setBackgroundColor(Color.argb(255, 207, 207, 207));
            }else{
                selected.set(position,false);
                itemLayout.setBackgroundColor(Color.argb(255, 255, 255, 255));
            }



            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selected.get(position)){
                        ivCheck.setVisibility(View.GONE);
                        selected.set(position,false);
                        itemLayout.setBackgroundColor(Color.argb(255, 255, 255, 255));
                    }else{
                        ivCheck.setVisibility(View.VISIBLE);
                        selected.set(position,true);
                        itemLayout.setBackgroundColor(Color.argb(255, 207, 207, 207));
                    }
                }
            });
        }

        return convertView;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Boolean> getSelected(){
        return selected;
    }

    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                countryNameList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }

            @SuppressWarnings("null")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> filteredArrayList = new ArrayList<String>();


                if (originalItem == null || originalItem.size() == 0) {
                    originalItem = new ArrayList<String>(countryNameList);
                }

                    /*
                     * if constraint is null then return original value
                     * else return filtered value
                     */
                if (constraint == null || constraint.length() == 0) {
                    results.count = originalItem.size();
                    results.values = originalItem;
                } else {
                    constraint = constraint.toString().toLowerCase(Locale.ENGLISH);
                    for (int i = 0; i < originalItem.size(); i++) {
                        String title = originalItem.get(i).split("~")[0].toLowerCase(Locale.ENGLISH);
                        if (title.startsWith(constraint.toString())) {
                            filteredArrayList.add(originalItem.get(i));
                        }
                    }
                    results.count = filteredArrayList.size();
                    results.values = filteredArrayList;
                }

                return results;
            }
        };

        return filter;
    }
}

