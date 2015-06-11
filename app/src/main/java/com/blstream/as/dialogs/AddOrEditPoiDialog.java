package com.blstream.as.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.R;
import com.blstream.as.data.BuildConfig;
import com.blstream.as.data.rest.model.enums.Category;
import com.blstream.as.data.rest.model.enums.EnumWithoutName;
import com.blstream.as.data.rest.model.enums.Price;
import com.blstream.as.data.rest.model.simpleModel.SimpleAddress;
import com.blstream.as.data.rest.model.simpleModel.SimpleLocation;
import com.blstream.as.data.rest.model.simpleModel.SimpleOpening;
import com.blstream.as.data.rest.model.enums.SubCategory;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.debug.BuildType;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 2015-03-26.
 * Edited by Rafal Soudani
 */
public class AddOrEditPoiDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = AddOrEditPoiDialog.class.getSimpleName();
    private static final String WWW_PREFIX = "www.";
    private static final String MONDAY = "MONDAY";
    private static final String TUESDAY= "TUESDAY";
    private static final String WEDNESDAY = "WEDNESDAY";
    private static final String THURSDAY = "THURSDAY";
    private static final String FRIDAY = "FRIDAY";
    private static final String SATURDAY = "SATURDAY";
    private static final String SUNDAY = "SUNDAY";

    private EditText name, description, street, postalCode, city, streetNumber, houseNumber, tags;
    private EditText www, phone, wiki, fanpage, open, close;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TextView longitudeTextView, latitudeTextView, subCategoryTextView;
    private Spinner categorySpinner, subCategorySpinner, priceSpinner;
    private Button okButton, cancelButton;
    private MapsFragment mapsFragment;
    private Context context;


    private Marker marker;

    private boolean editingMode;

    private OnAddPoiListener activityConnector;

    public static AddOrEditPoiDialog newInstance(Marker marker, boolean editingMode, Context context) {
        AddOrEditPoiDialog addOrEditPoiDialog = new AddOrEditPoiDialog();
        addOrEditPoiDialog.setMarker(marker);
        addOrEditPoiDialog.setEditingMode(editingMode);
        addOrEditPoiDialog.context = context;
        return addOrEditPoiDialog;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_poi_dialog, container, false);

        bindVariablesWithViews(view);

        postalCode.addTextChangedListener(new GenericTextWatcher(postalCode));
        www.addTextChangedListener(new GenericTextWatcher(www));
        wiki.addTextChangedListener(new GenericTextWatcher(wiki));
        fanpage.addTextChangedListener(new GenericTextWatcher(fanpage));

        open.setFilters(new InputFilter[]{new TimeFilter(open)});
        close.setFilters(new InputFilter[] {new TimeFilter(close)});

        if (getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG) instanceof MapsFragment) {
            mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG);
        }
        latitudeTextView.setText(getLatitude(marker));
        longitudeTextView.setText(getLongitude(marker));

        populateSpinners();

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        if (isEditingMode()) {
            name.setText(marker.getTitle());
            //TODO: pobrac pozostale atrybuty
            okButton.setVisibility(View.GONE);

            Button editOkButton = (Button) view.findViewById(R.id.acceptEditPoi);
            editOkButton.setVisibility(View.VISIBLE);
            editOkButton.setOnClickListener(this);
        }

        setCancelable(true);

        fillDataForDebug();
        return view;
    }

    private void populateSpinners() {
        List<EnumWithName> categoriesWithNamesList = new ArrayList<>();
        for (Category cat : Category.values()) {
            categoriesWithNamesList.add(new EnumWithName<>(cat));
        }

        categorySpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, categoriesWithNamesList));

        List<EnumWithName> pricesWithNamesList = new ArrayList<>();
        for (Price price : Price.values()) {
            pricesWithNamesList.add(new EnumWithName<>(price));
        }

        priceSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, pricesWithNamesList));

        List<EnumWithName> subCategoriesWithNamesList = new ArrayList<>();
        for (SubCategory sub : SubCategory.values()) {
            subCategoriesWithNamesList.add(new EnumWithName<>(sub));
        }

        categorySpinner.setOnItemSelectedListener(this);

        subCategorySpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, subCategoriesWithNamesList));
    }


    private void fillDataForDebug() {
        if (com.blstream.as.BuildConfig.BUILD_TYPE.contains(BuildType.DEBUG.buildName)) {
            name.setText("tytul");
            description.setText("opis");
            street.setText("ulica");
            postalCode.setText("00-000");
            city.setText("miasto");
            streetNumber.setText("nr bd");
            houseNumber.setText("nr lokalu");
            tags.setText("Tag1, Tag2");

            //pola nieobowiazkowe
            www.setText("www.adreswww.pl");
            phone.setText("070072772");
            wiki.setText("www.wikisite.com");
            fanpage.setText("www.fanpage.co.uk");
        }
    }

    private void bindVariablesWithViews(View view) {
        latitudeTextView = (TextView) view.findViewById(R.id.latitude);
        longitudeTextView = (TextView) view.findViewById(R.id.longitude);

        name = (EditText) view.findViewById(R.id.titleEditText);
        description = (EditText) view.findViewById(R.id.descriptionEditText);
        street = (EditText) view.findViewById(R.id.streetEditText);
        postalCode = (EditText) view.findViewById(R.id.postalCodeEditText);
        city = (EditText) view.findViewById(R.id.cityEditText);
        streetNumber = (EditText) view.findViewById(R.id.streetNumberEditText);
        houseNumber = (EditText) view.findViewById(R.id.houseNumberEditText);
        tags = (EditText) view.findViewById(R.id.tagsEditText);
        www = (EditText) view.findViewById(R.id.wwwEditText);
        phone = (EditText) view.findViewById(R.id.phoneEditText);
        wiki = (EditText) view.findViewById(R.id.wikiEditText);
        fanpage = (EditText) view.findViewById(R.id.fanPageEditText);
        open = (EditText) view.findViewById(R.id.openEditText);
        close = (EditText) view.findViewById(R.id.closeEditText);

        monday = (CheckBox) view.findViewById(R.id.mondayCheckBox);
        tuesday = (CheckBox) view.findViewById(R.id.tuesdayCheckBox);
        wednesday = (CheckBox) view.findViewById(R.id.wednesdayCheckBox);
        thursday = (CheckBox) view.findViewById(R.id.thursdayCheckBox);
        friday = (CheckBox) view.findViewById(R.id.fridayCheckBox);
        saturday = (CheckBox) view.findViewById(R.id.saturdayCheckBox);
        sunday = (CheckBox) view.findViewById(R.id.sundayCheckBox);

        okButton = (Button) view.findViewById(R.id.acceptAddPoi);
        cancelButton = (Button) view.findViewById(R.id.cancelAddPoi);

        priceSpinner = (Spinner) view.findViewById(R.id.priceSpinner);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        subCategorySpinner = (Spinner) view.findViewById(R.id.subCategorySpinner);
        subCategoryTextView = (TextView) view.findViewById(R.id.subCategoryTextView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (BuildConfig.DEBUG && (!(activity instanceof OnAddPoiListener)))
            throw new AssertionError("Activity: " + activity.getClass().getSimpleName() + " must implement OnPoiSelectedListener");
        activityConnector = (OnAddPoiListener) activity;
    }

    private String getLongitude(Marker marker) {
        return String.valueOf(marker.getPosition().longitude);
    }

    private String getLatitude(Marker marker) {
        return String.valueOf(marker.getPosition().latitude);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acceptAddPoi:
                String wrongFields = getWrongFields();
                if (getWrongFields().length() > 0) {
                    activityConnector.showAddPoiResultMessage(false, wrongFields);
                } else {
                    Server.addPoi(
                            stringValue(name),
                            stringValue(description),
                            getSimpleAddress(),
                            stringArrayValue(tags),
                            getSimpleLocation(),
                            getCategory(),
                            getSubCategory(),
                            webValue(www),
                            stringValue(phone),
                            webValue(wiki),
                            webValue(fanpage),
                            getSimpleOpening(),
                            getPrice()
                    );

                    marker.remove();
                    if (mapsFragment != null) {
                        mapsFragment.setMarkerTarget(null);
                    }
                    activityConnector.showAddPoiResultMessage(true, null);
                    dismiss();
                }
                break;

            case R.id.acceptEditPoi:
                //TODO: edycja punktu, gdy serwer bedzie na to pozwalal
                Toast.makeText(getActivity(), "Funkcjonalnosc jeszcze nie zaimplementowana", Toast.LENGTH_SHORT).show();
                break;

            case R.id.cancelAddPoi:
                dismiss();
                break;
        }
    }

    private Boolean getPrice() {
        Price tmpPrice = (Price) ((EnumWithName) priceSpinner.getSelectedItem()).getEnum();
        switch (tmpPrice){
            case UNASSIGNED:
                return null;
            case PAID:
                return true;
            case FREE:
                return false;
        }
        return null;
    }

    private String webValue(EditText editText) {
        if (editText.getText().toString().length() > 0 && !editText.getText().toString().equals(WWW_PREFIX)){
            return editText.getText().toString();
        } else {
            return null;
        }
    }

    private SubCategory getSubCategory() {
        if (getCategory().equals(Category.PLACE)) {
            return (SubCategory) ((EnumWithName) subCategorySpinner.getSelectedItem()).getEnum();
        } else {
            return null;
        }
    }

    private Category getCategory() {
        return (Category) (((EnumWithName) categorySpinner.getSelectedItem()).getEnum());
    }

    private SimpleAddress getSimpleAddress() {
        return new SimpleAddress(
                stringValue(city),
                stringValue(street),
                stringValue(streetNumber),
                stringValue(houseNumber),
                stringValue(postalCode));
    }

    private SimpleLocation getSimpleLocation() {
        return new SimpleLocation(doubleValue(latitudeTextView), doubleValue(longitudeTextView));
    }

    private SimpleOpening[] getSimpleOpening() {
        List<SimpleOpening> simpleOpeningList = new ArrayList<>();
        if (monday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(MONDAY, open.getText().toString(), close.getText().toString()));
        }
        if (tuesday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(TUESDAY, open.getText().toString(), close.getText().toString()));
        }
        if (wednesday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(WEDNESDAY, open.getText().toString(), close.getText().toString()));
        }
        if (thursday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(THURSDAY, open.getText().toString(), close.getText().toString()));
        }
        if (friday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(FRIDAY, open.getText().toString(), close.getText().toString()));
        }
        if (saturday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(SATURDAY, open.getText().toString(), close.getText().toString()));
        }
        if (sunday.isChecked()){
            simpleOpeningList.add(new SimpleOpening(SUNDAY, open.getText().toString(), close.getText().toString()));
        }
        SimpleOpening[] simpleOpenings = new SimpleOpening[simpleOpeningList.size()];
        simpleOpenings = simpleOpeningList.toArray(simpleOpenings);
        return simpleOpenings;
    }

    private String getWrongFields() {
        List<String> wrongFields = new ArrayList<>();
        if (stringValue(name).length() == 0) {
            wrongFields.add(context.getString(R.string.title));
        }
        if (stringValue(description).length() == 0) {
            wrongFields.add(context.getString(R.string.description));
        }
        if (stringValue(street).length() == 0) {
            wrongFields.add(context.getString(R.string.street));
        }
        if (stringValue(postalCode).length() < 5) {
            wrongFields.add(context.getString(R.string.postal_code));
        }
        if (stringValue(city).length() == 0) {
            wrongFields.add(context.getString(R.string.city));
        }
        if (stringValue(streetNumber).length() == 0) {
            wrongFields.add(context.getString(R.string.street_number));
        }
        if (stringValue(houseNumber).length() == 0) {
            wrongFields.add(context.getString(R.string.house_number));
        }
        if (stringValue(tags).length() == 0) {
            wrongFields.add(context.getString(R.string.tags));
        }
        return TextUtils.join(", ", wrongFields);
    }

    private Double doubleValue(TextView textView) {
        return Double.parseDouble(String.valueOf(textView.getText()));
    }

    private String stringValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    private String[] stringArrayValue(EditText tags) {
        return stringValue(tags).split("[\\s],[,\\s]");

    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Category category = (Category) ((EnumWithName) adapterView.getItemAtPosition(i)).getEnum();
        if (category.equals(Category.PLACE)) {
            subCategoryTextView.setVisibility(View.VISIBLE);
            subCategorySpinner.setVisibility(View.VISIBLE);
        } else {
            subCategoryTextView.setVisibility(View.GONE);
            subCategorySpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnAddPoiListener {
        /**
         * @param state true if successful, false if failed
         */
        void showAddPoiResultMessage(Boolean state, String wrongFields);
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public boolean isEditingMode() {
        return editingMode;
    }

    public void setEditingMode(boolean editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Cant get context in module (data), so cant extract string from resource. This is
     * helper class which extracts string from method getIdStringResource in toString method.
     */
    private class EnumWithName<EnumInDataModule extends EnumWithoutName> {

        private EnumInDataModule enumInDataModule;

        public EnumWithName(EnumInDataModule enumInDataModule) {
            this.enumInDataModule = enumInDataModule;
        }

        @Override
        public String toString() {
            return context.getString(enumInDataModule.getIdStringResource());
        }

        public EnumInDataModule getEnum() {
            return enumInDataModule;
        }

    }

    private class GenericTextWatcher implements TextWatcher {

        private EditText editText;

        private GenericTextWatcher(EditText editText) {
            this.editText = editText;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (editText.getId() == postalCode.getId()) {
                String str = s.toString();
                if (str.length() == 3 && count > before) {
                    char lastChar = str.charAt(str.length() - 1);
                    if (lastChar != '-') {
                        str = str.substring(0, str.length() - 1);
                        str += "-";
                        str += lastChar;
                        editText.setText(str);
                        editText.setSelection(str.length());
                    }
                } else if (str.length() == 3 && count < before) {
                    str = str.substring(0, str.length() - 1);
                    editText.setText(str);
                    editText.setSelection(str.length());
                }
            }
        }

        public void afterTextChanged(Editable editable) {
            if (editText.getId() == www.getId() || editText.getId() == wiki.getId() || editText.getId() == fanpage.getId())
                if (!editable.toString().contains(WWW_PREFIX)) {
                    editText.setText(WWW_PREFIX);
                    Selection.setSelection(editText.getText(), editText.getText().length());
                }
        }
    }

    private class TimeFilter implements InputFilter {

        EditText editText;
        private boolean doneOnce = false;

        private TimeFilter (EditText editText){
            this.editText = editText;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 1 && doneOnce == false) {
                source = source.subSequence(source.length() - 1, source.length());
                if (source.charAt(0) >= '0' && source.charAt(0) <= '2') {
                    doneOnce = true;
                    return source;
                } else {
                    return "";
                }
            }


            if (source.length() == 0) {
                return null;// deleting, keep original editing
            }
            String result = "";
            result += dest.toString().substring(0, dstart);
            result += source.toString().substring(start, end);
            result += dest.toString().substring(dend, dest.length());

            if (result.length() > 5) {
                return "";// do not allow this edit
            }
            boolean allowEdit = true;
            char c;
            if (result.length() > 0) {
                c = result.charAt(0);
                allowEdit &= (c >= '0' && c <= '2');
            }
            if (result.length() > 1) {
                c = result.charAt(1);
                if (result.charAt(0) == '0' || result.charAt(0) == '1')
                    allowEdit &= (c >= '0' && c <= '9');
                else
                    allowEdit &= (c >= '0' && c <= '3');
            }
            if (result.length() > 2) {
                c = result.charAt(2);
                allowEdit &= (c == ':');
            }
            if (result.length() > 3) {
                c = result.charAt(3);
                allowEdit &= (c >= '0' && c <= '5');
            }
            if (result.length() > 4) {
                c = result.charAt(4);
                allowEdit &= (c >= '0' && c <= '9');
            }
            return allowEdit ? null : "";
        }
    }
}
