package com.blstream.as.data.rest.service;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.blstream.as.data.rest.model.Address;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;

/**
 * Created by Rafal Soudani on 2015-05-30.
 */
public class MyContentProvider extends com.activeandroid.content.ContentProvider {

    public static final String AUTHORITY = "com.blstream.as";
    public static final String POI_PATH = Poi.TABLE_NAME.toLowerCase();
    public static final Uri POI_URI = Uri.parse("content://" + AUTHORITY + "/" + POI_PATH);
    public static final int POI_ID = 100;

    private static final UriMatcher MY_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MY_URI_MATCHER.addURI(AUTHORITY, POI_PATH, POI_ID);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uriType = MY_URI_MATCHER.match(uri);
        Cursor cursor = null;

        switch (uriType) {
            case POI_ID:
                From query = new Select(Poi.TABLE_NAME + ".*", Location.TABLE_NAME + ".*", Address.TABLE_NAME + ".*")
                        .from(Poi.class).as(Poi.TABLE_NAME)
                        .leftJoin(Location.class).as(Location.TABLE_NAME)
                        .on(Poi.TABLE_NAME + "." + Poi.LOCATION_ID + " = " + Location.TABLE_NAME + "." + BaseColumns._ID)
                        .leftJoin(Address.class).as(Address.TABLE_NAME)
                        .on(Poi.TABLE_NAME + "." + Poi.ADDRESS_ID + " = " + Address.TABLE_NAME + "." + BaseColumns._ID);

                cursor = Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            default:
                cursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return cursor;
    }

    public static Uri createUri(Class<? extends Model> type, Long id) {
        if (type == Poi.class) {
                return POI_URI;
        } else {
            return com.activeandroid.content.ContentProvider.createUri(type, id);
        }
    }
}