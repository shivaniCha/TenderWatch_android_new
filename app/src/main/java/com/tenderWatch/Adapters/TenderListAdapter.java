package com.tenderWatch.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.R;
import com.tenderWatch.SharedPreference.SessionManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lcom48 on 18/12/17.
 */

public class TenderListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Tender> tenderList;
    private ArrayList<Tender> orignalData;
    private ItemFilters filters = new ItemFilters();

    public TenderListAdapter(Context context, ArrayList<Tender> tenderList) {
        this.context = context;
        this.tenderList = tenderList;
        this.orignalData = tenderList;
    }

    public void updateReceiptsList(ArrayList<Tender> tenderList) {
        this.tenderList.clear();
        this.tenderList.addAll(tenderList);
        this.notifyDataSetChanged();
    }

    public ArrayList<Tender> getTender(){
        return tenderList;
    }

    @Override
    public int getCount() {
        return tenderList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.tender_list, parent, false);
        final CircleImageView tender_image = convertView.findViewById(R.id.tender_image2);
        TextView txtTenderName = convertView.findViewById(R.id.tender_name);
        TextView txtTenderTitle = convertView.findViewById(R.id.tender_title);
        TextView txtTenderExpDate = convertView.findViewById(R.id.tender_expdate);
        LinearLayout stampRemove = convertView.findViewById(R.id.stamp_remove);
        if (!tenderList.get(position).getTenderPhoto().equals("")) {
            Picasso.with(context)
                    .load(tenderList.get(position).getTenderPhoto())
                    .resize(100, 100)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.v("Main", String.valueOf(bitmap));
                            Bitmap main = bitmap;
                            tender_image.setImageBitmap(main);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.v("Main", "errrorrrr");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
            //  Log.i(TAG, profilePicUrl);
            //}
        }
        CircleImageView imgTrue = convertView.findViewById(R.id.tender_image3);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        Date startDateValue = null, endDateValue = null;
        try {
            startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate);
            //  startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tenderList.get(position).getCreatedAt().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tenderList.get(position).getExpiryDate().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Date endDateValue = new Date(allTender.get(position).getExpiryDate().split("T")[0]);
        long diff = endDateValue.getTime() - startDateValue.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24)+1;

        if (days < 0) {
            stampRemove.setVisibility(View.VISIBLE);
            txtTenderExpDate.setText("Expired");
        } else {
            txtTenderExpDate.setText(days + " days");
        }
        Log.d("days", "" + days);
        if (!TextUtils.isEmpty(tenderList.get(position).getEmail()))
            txtTenderName.setText(tenderList.get(position).getEmail());
        else
            txtTenderName.setText(new SessionManager(context).getPreferencesObject(context).getEmail());

        txtTenderTitle.setText(tenderList.get(position).getTenderName());

        if (tenderList.get(position).getFavorite().size() > 0) {
            for (int i = 0; i < tenderList.get(position).getFavorite().size(); i++) {
                String id = new SessionManager(context).getPreferencesObject(context).getId();
                if (tenderList.get(position).getFavorite().contains(id)) {
                    imgTrue.setVisibility(View.VISIBLE);
                }
            }
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

    @Override
    public Filter getFilter() {
        return filters;
    }

    private class ItemFilters extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String filterString = charSequence.toString().toLowerCase();
            FilterResults results = new FilterResults();
            if (filterString.length() > 0) {
                final List<Tender> list = orignalData;
                int count = list.size();
                final List<Tender> filterList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Tender tender = list.get(i);
                    if (tender.getTenderName().toLowerCase().contains(filterString)) {
                        filterList.add(tender);
                    }
                }
                results.values = filterList;
                results.count = filterList.size();
            }else {
                results.values = orignalData;
                results.count = orignalData.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tenderList = (ArrayList<Tender>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}


