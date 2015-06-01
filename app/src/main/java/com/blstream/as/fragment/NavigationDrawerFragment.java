package com.blstream.as.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blstream.as.DrawerAdapter;
import com.blstream.as.DrawerItem;
import com.blstream.as.HomeActivity;
import com.blstream.as.LoginUtils;
import com.blstream.as.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks callbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle drawerToggle;

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private View fragmentContainerView;
    private View rootView;
    private SharedPreferences sharedPreferences;

    private int currentSelectedPosition = 0;
    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.navigation_drawer_fragment, container, false);
        if (rootView != null) {
            LinearLayout scrollView = (LinearLayout) rootView.findViewById(R.id.drawer_view);
            if (scrollView != null) {
                drawerListView = (ListView) scrollView.findViewById(R.id.drawer_list);

                Context context = getActivity();

                if (context != null) {
                    if (LoginUtils.isUserLogged(context)) {
                        setDrawerListView();
                        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                selectItem(position);
                            }
                        });
                    }
                    else {
                        setNotLoggedDrawerListView();
                        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                selectItemNotLogged(position);
                            }
                        });
                    }
                    TextView userName = (TextView) scrollView.findViewById(R.id.user_name);
                    userName.setText(LoginUtils.getUserName(context));
                    drawerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }
            }
        }
        return rootView;
    }

    public void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void setNotLoggedDrawerListView() {
        String[] drawerNames = getResources().getStringArray(R.array.drawer_items_not_logged);
        int[] drawerIcons = {
                R.drawable.nearby,
                R.drawable.settings,
                R.drawable.logout
        };

        int size = drawerIcons.length;

        DrawerItem[] drawerItems = new DrawerItem[size];
        for (int i = 0; i < size; i++) {
            drawerItems[i] = new DrawerItem(drawerIcons[i], drawerNames[i]);
        }

        DrawerAdapter drawerAdapter = new DrawerAdapter(getActivity(), R.layout.navigation_drawer_item, drawerItems);
        drawerListView.setAdapter(drawerAdapter);
    }

    private void setDrawerListView() {


        String[] drawerNames = getResources().getStringArray(R.array.drawer_items);
        int[] drawerIcons = {
                R.drawable.nearby,
                R.drawable.poi_list,
                R.drawable.add_poi,
                R.drawable.settings,
                R.drawable.logout
        };

        int size = drawerIcons.length;

        DrawerItem[] drawerItems = new DrawerItem[size];
        for (int i = 0; i < size; i++) {
            drawerItems[i] = new DrawerItem(drawerIcons[i], drawerNames[i]);
        }

        DrawerAdapter drawerAdapter = new DrawerAdapter(getActivity(), R.layout.navigation_drawer_item, drawerItems);
        drawerListView.setAdapter(drawerAdapter);

    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        if (drawerLayout != null) {
            this.drawerLayout.setScrimColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = callbacks.getActivityActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.toolbar_show);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                NavigationDrawerFragment.this.drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    sharedPreferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        if (!userLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(fragmentContainerView);
        }

        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(drawerToggle);
    }

    private void selectItem(int position) {
        currentSelectedPosition = position;
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, false);
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
        if (callbacks != null) {
            if (!isPositionValid(position)) {
                callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.MAP_2D);
            }
            else {
                callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.values()[position]);
            }
        }
    }

    private void selectItemNotLogged(int position) {
        currentSelectedPosition = position;
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, false);
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
        if (callbacks != null) {
            switch (position) {
                case 0:
                    callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.MAP_2D);
                    break;
                case 1:
                    callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.SETTINGS);
                    break;
                case 2:
                    callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.LOGOUT);
                    break;
                default:
                    callbacks.onNavigationDrawerItemSelected(HomeActivity.FragmentType.MAP_2D);
                    break;
            }
        }
    }

    private boolean isPositionValid(int position) {
        return (position >= 0 && position < HomeActivity.FragmentType.values().length);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = callbacks.getActivityActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setTitle(R.string.app_name);
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(HomeActivity.FragmentType fragmentType);
        ActionBar getActivityActionBar();
    }
}