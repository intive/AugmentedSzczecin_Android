package com.blstream.as.data.rest.service;

import android.database.Cursor;
import android.net.Uri;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;

/**
 * Created by Rafal Soudani on 2015-05-30.
 */
public class MyContentProvider extends ContentProvider {
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Class<? extends Model> type = getModelType(uri);
        final Cursor cursor = Cache.openDatabase().query(
                Cache.getTableName(type),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
}
