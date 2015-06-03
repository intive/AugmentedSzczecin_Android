package com.blstream.as.data.rest.service;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.blstream.as.data.rest.model.Address;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;

import java.util.HashMap;

/**
 * Created by Rafal Soudani on 2015-05-30.
 */
public class MyContentProvider extends com.activeandroid.content.ContentProvider {

    public static final String AUTHORITY = "com.blstream.as";
    public static final String POI_PATH = Poi.TABLE_NAME.toLowerCase();
    public static final Uri POI_URI = Uri.parse("content://" + AUTHORITY + "/" + POI_PATH);
    public static final int POI_ALL = 100;
    public static final int POI_SINGLE = 110;

    private static final UriMatcher MY_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MY_URI_MATCHER.addURI(AUTHORITY, POI_PATH, POI_ALL);
        MY_URI_MATCHER.addURI(AUTHORITY, POI_PATH + "/#", POI_SINGLE);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String table ="";

        StringBuilder sb = new StringBuilder();
        sb.append(Poi.TABLE_NAME);
        sb.append(" LEFT OUTER JOIN ");
        sb.append(Location.TABLE_NAME);
        sb.append(" ON (");
        sb.append(Poi.TABLE_NAME + "." + Poi.LOCATION_ID);
        sb.append(" = ");
        sb.append(Location.TABLE_NAME + "." + BaseColumns._ID);
        sb.append(") LEFT OUTER JOIN ");
        sb.append(Address.TABLE_NAME);
        sb.append(" ON (");
        sb.append(Poi.TABLE_NAME + "." + Poi.ADDRESS_ID);
        sb.append(" = ");
        sb.append(Address.TABLE_NAME + "." + BaseColumns._ID);
        sb.append(")");
        table = sb.toString();

        queryBuilder.setTables(table);
        int uriType = MY_URI_MATCHER.match(uri);
        switch (uriType) {
            case POI_SINGLE:
                String id = Poi.TABLE_NAME + "." + BaseColumns._ID;
                queryBuilder.appendWhere(id + "=" + uri.getLastPathSegment());
                break;
            case POI_ALL:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(Cache.openDatabase(), projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
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