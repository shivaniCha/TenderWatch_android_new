package com.tenderWatch.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.tenderWatch.Models.AllContractorTender;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.SharedPreference.SessionManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lcom47 on 4/1/18.
 */

public class ContractorTenderListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<AllContractorTender> tenderList;
    private ArrayList<AllContractorTender> filterList;
    private ItemFilters filters = new ItemFilters();

    public ArrayList<AllContractorTender> getList(){
        return tenderList;
    }

    public ContractorTenderListAdapter(Context context, ArrayList<AllContractorTender> tenderList) {
        this.context = context;
        this.tenderList = tenderList;
        this.filterList=tenderList;
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
        TextView tvTenderRead = convertView.findViewById(R.id.tv_tender_read);
        TextView tvTenderAmed = convertView.findViewById(R.id.tv_tender_amed);
        LinearLayout stampRemove = convertView.findViewById(R.id.stamp_remove);
        CircleImageView imgTrue = convertView.findViewById(R.id.tender_image3);
        String userId = new SessionManager(convertView.getContext()).getPreferencesObject(convertView.getContext()).getId();
        if (!tenderList.get(position).getTenderPhoto().equals("no image")) {
            Picasso.with(context).load(tenderList.get(position).getTenderPhoto()).resize(100, 100).into(tender_image);
        } else {
            tender_image.setImageDrawable(context.getResources().getDrawable(R.drawable.avtar));
        }

        if (isShowNew(position, userId)) {
            tvTenderRead.setVisibility(View.VISIBLE);
        } else {
            tvTenderRead.setVisibility(View.GONE);
        }

        if (isShowAmed(position, userId) && tenderList.get(position).getReadby().size()>0) {
            tvTenderAmed.setVisibility(View.VISIBLE);
        } else {
            tvTenderAmed.setVisibility(View.GONE);
        }


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


        txtTenderName.setText(tenderList.get(position).getTenderUploader().getEmail());
        txtTenderTitle.setText(tenderList.get(position).getTenderName());
        User user = new SessionManager(context).getPreferencesObject(context);
        if (tenderList.get(position).getInterested().size() > 0) {
            for (int i = 0; i < tenderList.get(position).getInterested().size(); i++) {
                String id = user.getId();
                if (tenderList.get(position).getInterested().contains(id)) {
                    imgTrue.setVisibility(View.VISIBLE);
                }
            }
        }
        if (tenderList.get(position).getAmendRead() != null) {

            if (tenderList.get(position).getAmendRead().size() > 0) {
                for (int i = 0; i < tenderList.get(position).getAmendRead().size(); i++) {
                    String id = user.getId();
                    if (!tenderList.get(position).getAmendRead().contains(id)) {
                        tender_image.setBorderColor(Color.RED);
                        tender_image.setBorderWidth(2);
                        tvTenderAmed.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (tenderList.get(position).getAmendRead().size() == 0) {
                tender_image.setBorderColor(Color.RED);
                tender_image.setBorderWidth(2);
            }
        }
        return convertView;
    }



    private boolean isShowNew(int position, String userId) {
        for (int i = 0; i < tenderList.get(position).getReadby().size(); i++) {
            if (tenderList.get(position).getReadby().get(i).equals(userId)) {
                return false;
            }
        }
        return true;
    }

    private boolean isShowAmed(int position, String userId) {
        if(tenderList.get(position).getAmendRead()!=null) {
            for (int i = 0; i < tenderList.get(position).getAmendRead().size(); i++) {
                if (tenderList.get(position).getAmendRead().get(i).equals(userId)) {
                    return false;
                }
            }
        }else{
            return false;
        }
        return true;
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
                final List<AllContractorTender> list = filterList;
                int count = list.size();
                final List<AllContractorTender> filterList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    AllContractorTender tender = list.get(i);
                    if (tender.getTenderName().toLowerCase().contains(filterString)) {
                        filterList.add(tender);
                    }
                }
                results.values = filterList;
                results.count = filterList.size();
            }else {
                results.values = filterList;
                results.count = filterList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tenderList = (ArrayList<AllContractorTender>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
