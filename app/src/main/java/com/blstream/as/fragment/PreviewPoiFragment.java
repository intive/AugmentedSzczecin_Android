package com.blstream.as.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blstream.as.R;

/**
 * Created by Damian on 2015-04-26.
 */
public class PreviewPoiFragment extends Fragment {
    private String name;
    private String category;
    private String description;


    public static PreviewPoiFragment newInstance() {
        return new PreviewPoiFragment();
    }
    public PreviewPoiFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_preview_poi, container, false);
        ImageView poiImageView = (ImageView) fragmentView.findViewById(R.id.poiImage);
        poiImageView.setImageResource(R.drawable.splash);
        TextView categoryTextView = (TextView) fragmentView.findViewById(R.id.categoryTextView);
        categoryTextView.setText("KAWIARNIA");
        TextView nameTextView = (TextView) fragmentView.findViewById(R.id.nameTextView);
        nameTextView.setText("Cafe Baranakan");
        TextView descriptionTextView = (TextView) fragmentView.findViewById(R.id.descriptionTextView);
        nameTextView.setText("Bardzo dlugi opis miejsca publicznego typu kawiarnia w ktorej chetnie napilbym sie kawy choc jej nie lubie.");
        return fragmentView;
    }
}
