/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.provider.content.resolver;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.zhanghai.android.files.AppApplication;

public class Resolver {

    public static void checkExistence(@NonNull Uri uri) throws IOException {
        int rowCount;
        try (Cursor cursor = getContentResolver().query(uri, new String[0], null, null, null)) {
            if (cursor == null) {
                throw new IOException("query() returned null");
            }
            rowCount = cursor.getCount();
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (rowCount < 1) {
            throw new IOException("Row count is less than 1: " + rowCount);
        }
    }

    public static void delete(@NonNull Uri uri) throws IOException {
        int deletedRowCount;
        try {
            deletedRowCount = getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (deletedRowCount < 1) {
            throw new IOException("Deleted row count is less than 1: " + deletedRowCount);
        }
    }

    @Nullable
    public static String getDisplayName(@NonNull Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[] { OpenableColumns.DISPLAY_NAME }, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getSize(@NonNull Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, new String[] { OpenableColumns.SIZE },
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (columnIndex != -1) {
                    return cursor.getLong(columnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Nullable
    public static String getType(@NonNull Uri uri) {
        String type;
        try {
            type = getContentResolver().getType(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (type != null && type.isEmpty()) {
            type = null;
        }
        return type;
    }

    @NonNull
    public static InputStream openInputStream(@NonNull Uri uri, @NonNull String mode)
            throws IOException {
        try {
            AssetFileDescriptor descriptor = getContentResolver().openAssetFileDescriptor(uri,
                    mode);
            if (descriptor == null) {
                throw new FileNotFoundException("openAssetFileDescriptor() returned null");
            }
            return descriptor.createInputStream();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @NonNull
    public static OutputStream openOutputStream(@NonNull Uri uri, @NonNull String mode)
            throws IOException {
        try {
            AssetFileDescriptor descriptor = getContentResolver().openAssetFileDescriptor(uri,
                    mode);
            if (descriptor == null) {
                throw new FileNotFoundException("openAssetFileDescriptor() returned null");
            }
            return descriptor.createOutputStream();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @NonNull
    public static ParcelFileDescriptor openParcelFileDescriptor(@NonNull Uri uri,
                                                                @NonNull String mode)
            throws IOException {
        ParcelFileDescriptor descriptor;
        try {
            descriptor = getContentResolver().openFileDescriptor(uri, mode);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (descriptor == null) {
            throw new IOException("openFileDescriptor() returned null");
        }
        return descriptor;
    }

    @NonNull
    private static ContentResolver getContentResolver() {
        return AppApplication.getInstance().getContentResolver();
    }
}
