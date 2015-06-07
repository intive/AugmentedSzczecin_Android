package com.blstream.as.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.blstream.as.data.rest.model.SearchResult;
import java.util.ArrayList;


public class SearchResultsFragment extends ListFragment {

    public static final String TAG = SearchResultsFragment.class.getName();

    private ArrayList<SearchResult> results = new ArrayList<>();
    private static final String ARGUMENT_RESULTS_NAME = "results";

    private OnPoiSelectedListener activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            results = (ArrayList<SearchResult>) getArguments().getSerializable(ARGUMENT_RESULTS_NAME);
        }
        SearchResultsAdapter adapter = new SearchResultsAdapter(getActivity(), results);
        setListAdapter(adapter);
    }

    public static SearchResultsFragment newInstance(ArrayList<SearchResult> results) {
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_RESULTS_NAME, results);
        searchResultsFragment.setArguments(args);
        return searchResultsFragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SearchResult result = (SearchResult) getListAdapter().getItem(position);
        String poiId = result.getPoiId();
        activity.goToMarker(poiId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnPoiSelectedListener))
            throw new ClassCastException(activity.toString() + " must implement OnPoiSelectedListener");
        this.activity = (OnPoiSelectedListener) activity;
    }

    public interface OnPoiSelectedListener {
        void goToMarker(String poiId);
    }
}
