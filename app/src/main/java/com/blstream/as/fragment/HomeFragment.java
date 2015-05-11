package com.blstream.as.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blstream.as.R;

public class HomeFragment extends Fragment {
    public final static String TAG = HomeFragment.class.getSimpleName();

    private final static int NUM_IMAGES = 5;

    private TextView nearbyPoiButton;
    private TextView ownPlacesButton;
    private TextView addPoiButton;
    private TextView settingsButton;
    private TextView logoutButton;

    private Callbacks activityConnector;

    private View rootView;

    private int[] images;

    public interface Callbacks {
        boolean isUserLogged();
        void switchToPoiAdd();
        void switchToMap();
        void switchToPoiList();
        void switchToLogout();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.home_screen_fragment, container, false);
        }
        setButtons();
        setButtonsListeners();

        setImages();
        PoiImageSlider viewPagerAdapter = new PoiImageSlider(getActivity());
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(viewPagerAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            activityConnector = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement HomeFragment.Callbacks");
        }
    }

    private void setButtons() {
        nearbyPoiButton = (TextView) rootView.findViewById(R.id.nearby);
        addPoiButton = (TextView) rootView.findViewById(R.id.add_poi);
        settingsButton = (TextView) rootView.findViewById(R.id.settings);
        ownPlacesButton = (TextView) rootView.findViewById(R.id.own_places);
        logoutButton = (TextView) rootView.findViewById(R.id.logout);
        if (!activityConnector.isUserLogged()){
            addPoiButton.setVisibility(View.GONE);
            ownPlacesButton.setVisibility(View.GONE);
            logoutButton.setText(getString(R.string.home_screen_exit));
        }
    }

    private void setButtonsListeners() {
        setNearbyPoiListener();
        setAddPoiListener();
        setSettingsListener();
        setOwnPlacesListener();
        setLogoutListener();
    }

    //Only for testing
    private void setImages() {
        images = new int[NUM_IMAGES];
        images[0] = R.drawable.splash;
        images[1] = R.drawable.splash;
        images[2] = R.drawable.splash;
        images[3] = R.drawable.splash;
        images[4] = R.drawable.splash;
    }

    private void setNearbyPoiListener() {
        nearbyPoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityConnector.switchToMap();
            }
        });
    }

    private void setAddPoiListener() {
        addPoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityConnector.switchToPoiAdd();
            }
        });
    }

    private void setSettingsListener() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO settings tab
            }
        });
    }

    private void setOwnPlacesListener() {
        ownPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityConnector.switchToPoiList();
            }
        });
    }

    void setLogoutListener(){
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activityConnector.switchToLogout();
            }
        });
    }

    private class PoiImageSlider extends PagerAdapter {

        final Context context;
        final LayoutInflater layoutInflater;

        public PoiImageSlider(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.view_pager_image, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setImageResource(images[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

}
