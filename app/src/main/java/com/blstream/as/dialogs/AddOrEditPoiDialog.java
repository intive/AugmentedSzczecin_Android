package com.blstream.as.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.R;
import com.blstream.as.data.BuildConfig;
import com.blstream.as.data.rest.model.Category;
import com.blstream.as.data.rest.model.SubCategory;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 2015-03-26.
 * Edited by Rafal Soudani
 */
public class AddOrEditPoiDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    public static final String TAG = AddOrEditPoiDialog.class.getSimpleName();

    private EditText name, description, street, postalCode, city, streetNumber, houseNumber, tags;
    private TextView longitudeTextView, latitudeTextView, subCategoryTextView;
    private Spinner categorySpinner, subCategorySpinner;
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

        latitudeTextView = (TextView) view.findViewById(R.id.latitude);
        longitudeTextView = (TextView) view.findViewById(R.id.longitude);
        subCategoryTextView = (TextView) view.findViewById(R.id.subCategoryTextView);

        name = (EditText) view.findViewById(R.id.titleEditText);
        description = (EditText) view.findViewById(R.id.descriptionEditText);
        street = (EditText) view.findViewById(R.id.streetEditText);
        postalCode = (EditText) view.findViewById(R.id.postalCodeEditText);
        city = (EditText) view.findViewById(R.id.cityEditText);
        streetNumber = (EditText) view.findViewById(R.id.streetNumberEditText);
        houseNumber = (EditText) view.findViewById(R.id.houseNumberEditText);
        tags = (EditText) view.findViewById(R.id.tagsEditText);

        postalCode.addTextChangedListener(this);

        if (getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG) instanceof MapsFragment) {
            mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG);
        }
        latitudeTextView.setText(getLatitude(marker));
        longitudeTextView.setText(getLongitude(marker));

        Button okButton = (Button) view.findViewById(R.id.acceptAddPoi);
        Button cancelButton = (Button) view.findViewById(R.id.cancelAddPoi);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        subCategorySpinner = (Spinner) view.findViewById(R.id.subCategorySpinner);

        List<EnumWithName> categoriesWithNamesList = new ArrayList<>();
        for (Category cat : Category.values()) {
            categoriesWithNamesList.add(new EnumWithName<>(cat));
        }

        categorySpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, categoriesWithNamesList));

        List<EnumWithName> subCategoriesWithNamesList = new ArrayList<>();
        for (SubCategory sub : SubCategory.values()) {
            subCategoriesWithNamesList.add(new EnumWithName<>(sub));
        }

        categorySpinner.setOnItemSelectedListener(this);

        subCategorySpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, subCategoriesWithNamesList));

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
        return view;
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
                    Category category = (Category) (((EnumWithName) categorySpinner.getSelectedItem()).getEnum());
                    SubCategory subCategory;
                    if (category.equals(Category.PLACE)) {
                        subCategory = (SubCategory) ((EnumWithName) subCategorySpinner.getSelectedItem()).getEnum();
                    } else {
                        subCategory = null;
                    }

                    Server.addPoi(
                            stringValue(name),
                            stringValue(description),
                            stringValue(street),
                            stringValue(postalCode),
                            stringValue(city),
                            stringValue(streetNumber),
                            stringValue(houseNumber),
                            stringArrayValue(tags),
                            doubleValue(latitudeTextView),
                            doubleValue(longitudeTextView),
                            category,
                            subCategory
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
        if (stringValue(postalCode).length() == 0) {
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString();
        if (str.length() == 3 && count > before) {
            char lastChar = str.charAt(str.length() - 1);
            if (lastChar != '-') {
                str = str.substring(0, str.length() - 1);
                str += "-";
                str += lastChar;
                postalCode.setText(str);
                postalCode.setSelection(str.length());
            }
        } else if (str.length() == 3 && count < before) {
            str = str.substring(0, str.length() - 1);
            postalCode.setText(str);
            postalCode.setSelection(str.length());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

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

    private class EnumWithName<E extends Enum<E>> {

        private E e;

        public EnumWithName(E e) {
            this.e = e;
        }

        @Override
        public String toString() {
            if (e instanceof Category) {
                return context.getString(((Category) e).getIdStringResource());
            } else if (e instanceof SubCategory) {
                return context.getString(((SubCategory) e).getIdStringResource());
            } else {
                return super.toString();
            }
        }

        public E getEnum() {
            return e;
        }

    }
}
