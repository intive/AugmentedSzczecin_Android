package com.blstream.as.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;

import com.activeandroid.Cache;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.blstream.as.R;
import com.blstream.as.data.rest.model.Address;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.SearchResult;
import com.blstream.as.data.rest.model.SearchResults;
import com.blstream.as.data.rest.service.MyContentProvider;
import com.blstream.as.data.rest.service.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PoiSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Button searchButton;
    private Button cancelButton;

    private SimpleCursorAdapter nameAdapter;
    private String name = null;
    private AutoCompleteTextView nameEditText;

    private List<String> tagList;
    private EditText tagsEditText;

    private SimpleCursorAdapter streetAdapter;
    private String street = null;
    private AutoCompleteTextView streetEditText;

    private String category = null;
    private Spinner categorySpinner;

    private String subcategory = null;
    private Spinner subcategorySpinner;

    private Boolean paid = null;
    private Spinner paidSpinner;

    private Boolean open = null;
    private CheckBox openCheckBox;

    private FragmentManager fragmentManager;

    private PoiSearchListener activityConnector;

    public static final String TAG = PoiSearchFragment.class.getSimpleName();

    private static final String CATEGORY_PLACES = "Miejsca publiczne";
    private static final String CATEGORY_COMMERCIALS = "Firmy i usługi";
    private static final String CATEGORY_PEOPLE = "Znajomi";
    private static final String CATEGORY_EVENTS = "Wydarzenia";
    private static final String SUBCATEGORY_SCHOOL = "Szkoła";
    private static final String SUBCATEGORY_HOSPITAL = "Szpital";
    private static final String SUBCATEGORY_PARK = "Park";
    private static final String SUBCATEGORY_MONUMENT = "Zabytek";
    private static final String SUBCATEGORY_MUSEUM = "Muzeum";
    private static final String SUBCATEGORY_OFFICE = "Urząd";
    private static final String SUBCATEGORY_BUS_STATION = "Dworzec autobusowy";
    private static final String SUBCATEGORY_TRAIN_STATION = "Dworzec kolejowy";
    private static final String SUBCATEGORY_POST_OFFICE = "Poczta";
    private static final String SUBCATEGORY_CHURCH = "Kościół";
    private static final String PAID = "Płatne";
    private static final String FREE = "Bezpłatne";
    private static final String SEARCH_LONGITUDE = "14.5530200";
    private static final String SEARCH_LATITUDE = "53.4289400";
    private static final String SEARCH_RADIUS = "10000000";


    public static PoiSearchFragment newInstance() {
        PoiSearchFragment poiSearchFragment = new PoiSearchFragment();
        return poiSearchFragment;
    }

    public PoiSearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTheme(R.style.Search);
        View poiSearchView = inflater.inflate(R.layout.poi_search_fragment, container, false);

        getLoaderManager().initLoader(0, null, this);

        searchButton = (Button) poiSearchView.findViewById(R.id.searchOkButton);
        cancelButton = (Button) poiSearchView.findViewById(R.id.searchCancelButton);

        nameEditText = (AutoCompleteTextView) poiSearchView.findViewById(R.id.nameEditText);
        setNameCursorAdapter();

        tagsEditText = (EditText) poiSearchView.findViewById(R.id.tagsEditText);

        streetEditText = (AutoCompleteTextView) poiSearchView.findViewById(R.id.streetEditText);
        setStreetCursorAdapter();

        categorySpinner = (Spinner) poiSearchView.findViewById(R.id.categorySpinner);
        setCategorySpinnerAdapter();
        setPlaceListener();

        subcategorySpinner = (Spinner) poiSearchView.findViewById(R.id.subcategorySpinner);
        setSubcategorySpinnerAdapter();

        paidSpinner = (Spinner) poiSearchView.findViewById(R.id.paidSpinner);
        setPaidSpinnerAdapter();

        openCheckBox = (CheckBox) poiSearchView.findViewById(R.id.openCheckBox);

        setCancelListener();
        setSearchListener();

        return poiSearchView;
    }

    public void setCategorySpinnerAdapter(){
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category_array, R.layout.spinner_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    public void setSubcategorySpinnerAdapter(){
        ArrayAdapter<CharSequence> subcategoryAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.subcategory_array, R.layout.spinner_item);
        subcategorySpinner.setAdapter(subcategoryAdapter);
        subcategorySpinner.setVisibility(View.GONE);
    }

    public void setPaidSpinnerAdapter(){
        ArrayAdapter<CharSequence> paidAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.paid_array, R.layout.spinner_item);
        paidSpinner.setAdapter(paidAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MyContentProvider.createUri(Poi.class, null),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        nameAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameAdapter.swapCursor(null);
    }

    private void setNameCursorAdapter() {
        nameAdapter = new SimpleCursorAdapter(getActivity(), R.layout.autocomplete_textview_item,
                null, new String[] { Poi.NAME },
                new int[] {R.id.autocompleteTextView},
                0);
        nameEditText.setAdapter(nameAdapter);

        nameAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return getNameCursor(str);
            }
        });

        nameAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(Poi.NAME);
                return cur.getString(index);
            }
        });
    }

    public Cursor getNameCursor(CharSequence str) {
        From query = new Select(Poi.TABLE_NAME + ".*")
                .from(Poi.class).as(Poi.TABLE_NAME)
                .where(Poi.NAME + " LIKE ?", "%" + str + "%")
                .groupBy(Poi.NAME);
        return Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
    }

    private void setStreetCursorAdapter() {
        streetAdapter = new SimpleCursorAdapter(getActivity(), R.layout.autocomplete_textview_item,
                null, new String[] {Address.STREET },
                new int[] {R.id.autocompleteTextView},
                0);
        streetEditText.setAdapter(streetAdapter);

        streetAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return getStreetCursor(str);
            }
        });

        streetAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(Address.STREET);
                return cur.getString(index);
            }
        });
    }

    public Cursor getStreetCursor(CharSequence str) {
        From query = new Select(Address.TABLE_NAME + ".*")
                .distinct()
                .from(Address.class).as(Address.TABLE_NAME)
                .where(Address.STREET + " LIKE ?", "%"+str+"%")
                .groupBy(Address.STREET);
        return Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
    }

    public void setCancelListener(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    public void setPlaceListener(){
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (categorySpinner.getSelectedItem().equals(CATEGORY_PLACES)) {
                    subcategorySpinner.setVisibility(View.VISIBLE);
                }
                else {
                    subcategorySpinner.setVisibility(View.GONE);
                    subcategorySpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setSearchListener(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNameValue();
                setTagsValue();
                setStreetValue();
                setCategoryValue();
                setSubcategoryValue();
                setOpenValue();
                setPaidValue();
                if (isFormEmpty()){
                    showEmptyFormDialog();
                }
                else {
                    getSearchResults(category, name, tagList, street, subcategory, open, paid);
                }
            }
        });
    }

    public Boolean isFormEmpty(){
        return(category==null && name==null && tagList.isEmpty() && street==null && subcategory==null
                && open==null && paid==null);
    }

    public void setNameValue(){
        if (nameEditText.getText().toString().trim().length() > 0){
            name = nameEditText.getText().toString();
        }
        else {
            name = null;
        }
    }

    public void setTagsValue(){
        tagList = new ArrayList<>();
        if (tagsEditText.getText().toString().trim().length() > 0){
            tagList = Arrays.asList(tagsEditText.getText().toString().split("\\s*,\\s*"));
        }
        else {
            tagList.clear();
        }
    }

    public void setStreetValue(){
        if (streetEditText.getText().toString().trim().length() > 0){
            street = streetEditText.getText().toString();
        }
        else {
            street = null;
        }
    }

    public void setOpenValue(){
        if (openCheckBox.isChecked()) {
            open = true;
        }
        else {
            open = null;
        }
    }

    public void setCategoryValue(){
        String categoryValue = (String) categorySpinner.getSelectedItem();
        switch (categoryValue) {
            case CATEGORY_PLACES:
                category = getString(R.string.server_cat_place);
                break;
            case CATEGORY_COMMERCIALS:
                category = getString(R.string.server_cat_commercial);
                break;
            case CATEGORY_PEOPLE:
                category = getString(R.string.server_cat_person);
                break;
            case CATEGORY_EVENTS:
                category = getString(R.string.server_cat_event);
                break;
            default:
                category = null;
                break;
        }
    }

    public void setSubcategoryValue(){
        String subcategoryValue = (String) subcategorySpinner.getSelectedItem();
        switch (subcategoryValue) {
            case SUBCATEGORY_SCHOOL:
                subcategory = getString(R.string.server_subcat_school);
                break;
            case SUBCATEGORY_HOSPITAL:
                subcategory = getString(R.string.server_subcat_hospital);
                break;
            case SUBCATEGORY_PARK:
                subcategory = getString(R.string.server_subcat_park);
                break;
            case SUBCATEGORY_MONUMENT:
                subcategory = getString(R.string.server_subcat_monument);
                break;
            case SUBCATEGORY_MUSEUM:
                subcategory = getString(R.string.server_subcat_museum);
                break;
            case SUBCATEGORY_OFFICE:
                subcategory = getString(R.string.server_subcat_office);
                break;
            case SUBCATEGORY_BUS_STATION:
                subcategory = getString(R.string.server_subcat_bus_station);
                break;
            case SUBCATEGORY_TRAIN_STATION:
                subcategory = getString(R.string.server_subcat_train_station);
                break;
            case SUBCATEGORY_POST_OFFICE:
                subcategory = getString(R.string.server_subcat_post_office);
                break;
            case SUBCATEGORY_CHURCH:
                subcategory = getString(R.string.server_subcat_church);
                break;
            default:
                subcategory = null;
                break;
        }
    }

    public void setPaidValue(){
        String paidValue = (String) paidSpinner.getSelectedItem();
        switch (paidValue) {
            case PAID:
                paid = true;
                break;
            case FREE:
                paid = false;
                break;
            default:
                paid = null;
                break;
        }
    }

    public void getSearchResults(String category, String name,
                                 List<String> tagList, String street,
                                 String subcategory, Boolean open, Boolean paid){
        Server.search(SEARCH_LONGITUDE, SEARCH_LATITUDE, SEARCH_RADIUS, category, name, tagList,
                street, subcategory, open, paid, new Callback<SearchResults>() {
                    @Override
                    public void success(SearchResults searchResults, final Response response) {
                        if (noResultsFound(searchResults)) {
                            showNoPoisFoundDialog();
                        } else {
                            ArrayList<SearchResult> resultList = putResultToList(searchResults);
                            activityConnector.showSearchResults(resultList);
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        showErrorDialog();
                    }
                });
    }

    public ArrayList<SearchResult> putResultToList(SearchResults searchResults){
        ArrayList<SearchResult> resultList = new ArrayList<>();
        if (searchResults.getPlacesList()!= null){
            resultList.addAll(searchResults.getPlacesList());
        }
        if (searchResults.getCommercialsList()!= null){
            resultList.addAll(searchResults.getCommercialsList());
        }
        if (searchResults.getPeopleList()!= null){
            resultList.addAll(searchResults.getPeopleList());
        }
        if (searchResults.getEventsList()!= null){
            resultList.addAll(searchResults.getEventsList());
        }
        return resultList;
    }

    public Boolean noResultsFound(SearchResults searchResults){
        return (searchResults==null ||
                (searchResults.getPlacesList()==null && searchResults.getCommercialsList()==null
                        && searchResults.getEventsList()==null && searchResults.getPeopleList()==null));
    }

    public void showEmptyFormDialog(){
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setMessage(R.string.search_empty_form)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showNoPoisFoundDialog(){
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(R.string.no_pois_found_title)
                .setMessage(R.string.no_pois_found_content)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showErrorDialog(){
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setMessage(R.string.search_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof PoiSearchListener))
            throw new ClassCastException(activity.toString() + " must implement PoiSearchListener");
        activityConnector = (PoiSearchListener) activity;
    }

    public interface PoiSearchListener {
        void showSearchResults(ArrayList<SearchResult> results);
    }

}
