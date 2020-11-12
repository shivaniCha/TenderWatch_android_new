package com.tenderWatch.Drawer;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.tenderWatch.R;
import com.tenderWatch.utils.NonSwipeableViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionContainer extends Fragment {


    public SubscriptionContainer() {
        // Required empty public constructor
    }

    private NonSwipeableViewPager subscriptionPager;
    private TabLayout tlSubscription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription_container, container, false);
        subscriptionPager = view.findViewById(R.id.vp_subscription);
        tlSubscription = view.findViewById(R.id.tl_subscription);
        getActivity().setTitle("Subscription Detail");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            subscriptionPager.setAdapter(new SubscriptionContainerAdapter(getChildFragmentManager()));
            tlSubscription.setupWithViewPager(subscriptionPager);
            ((MainDrawer)getActivity()).setOptionMenu(false,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SubscriptionContainerAdapter extends FragmentPagerAdapter {

        public SubscriptionContainerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Subscription();
                case 1:
                    return new PaymentHistoryFragment();
                default:
                    return new Subscription();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Subscription's";
                case 1:
                    return "Pending";
                default:
                    return "Subscription's";

            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
