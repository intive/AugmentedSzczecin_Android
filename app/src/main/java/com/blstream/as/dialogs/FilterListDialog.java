package com.blstream.as.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.blstream.as.R;
import com.blstream.as.SubcategoryFilterAdapter;
import com.blstream.as.data.rest.model.enums.SubCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 2015-06-05.
 */
public class FilterListDialog {
    private Context context;
    private PopupWindow popupWindow;
    private ListView listView;
    private List<Integer> selectedItems;

    public FilterListDialog(Context context) {
        this.context = context;
        selectedItems = new ArrayList<>();
        createPopup();
    }

    private void createPopup() {
        popupWindow = new PopupWindow(context);
        popupWindow.setFocusable(true);
        listView = new ListView(context);
        SubcategoryFilterAdapter subcategoryFilterAdapter = new SubcategoryFilterAdapter(context,R.layout.filter_popup_menu_item,SubCategory.values(),selectedItems);
        listView.setAdapter(subcategoryFilterAdapter);
        listView.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) context);
        listView.setBackgroundColor(context.getResources().getColor(R.color.white));
        listView.setSelector(R.drawable.filter_popup_item_selector);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        popupWindow.setContentView(listView);
    }
    public void setWidth(int sizeInDp, int screenDpi) {
        popupWindow.setWidth(sizeInDp *(screenDpi/160));
    }
    public void setHeight(int sizeInDp, int screenDpi) {
        popupWindow.setHeight(sizeInDp *(screenDpi/160));
    }
    public void show(View anchor) {
        popupWindow.showAsDropDown(anchor,0,-anchor.getHeight());
    }
    public void checkItem(int position, View view) {
        if(selectedItems.contains(position)) {
            selectedItems.remove((Integer)position);
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        else {
            selectedItems.add(position);
            view.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }
    }

    public List<Integer> getSelectedItems() {
        return selectedItems;
    }

}