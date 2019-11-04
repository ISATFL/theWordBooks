package com.example.thewordbooks;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final int WORDS_DIR=0;
    public static final int WORDS_ITEM=1;
//    public static final int CATEGORY_DIR=2;
//    public static final int CATEGORY_ITEM=3;
    public static final  String AUTHORITY="com.example.thewordbooks.provider";
    private static UriMatcher uriMatcher;
    private  wordsDBHelper dbHelper;
    static{
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"words",WORDS_DIR);
        uriMatcher.addURI(AUTHORITY,"words/#",WORDS_ITEM);

    }
    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int deleteRows=0;
        switch (uriMatcher.match(uri)){
            case WORDS_DIR:
                deleteRows=db.delete("words",selection,selectionArgs);
                break;
            case WORDS_ITEM:
                String wordId=uri.getPathSegments().get(1);
                deleteRows=db.delete("words","_id=?",new String[]{wordId});
                break;
                default:
                    break;
        }
        return deleteRows;
        // Implement this to handle requests to delete one or more rows.

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case WORDS_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.thewordbooks.provider.words";
            case WORDS_ITEM:
                return "vnd.android.cursor.dir/vnd.com.example.thewordbooks.provider.words";
        }
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        return null;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Uri uriReturn=null;
        switch (uriMatcher.match(uri)){
            case WORDS_DIR:
            case WORDS_ITEM:
                long newWordId=db.insert("words",null,values);
                uriReturn=Uri.parse("content://"+AUTHORITY+"/words/"+newWordId);
                break;
                default:
                    break;
        }
        // TODO: Implement this to handle requests to insert a new row.
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        dbHelper=new wordsDBHelper(getContext());
        return true;
        // TODO: Implement this to initialize your content provider on startup.

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (uriMatcher.match(uri)){

            case WORDS_DIR:
                cursor=db.query("words",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case WORDS_ITEM:
                String wordId=uri.getPathSegments().get(449741);
                cursor=db.query("words",projection,"_id=?",new String[]{wordId},null,null,sortOrder);
                break;
                default:
                    break;
        }
        return cursor;
        // TODO: Implement this to handle query requests from clients.

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int updateRows=0;
        switch (uriMatcher.match(uri)){
            case WORDS_DIR:
                updateRows=db.update("words",values,selection,selectionArgs);
                break;
            case WORDS_ITEM:
                String wordId=uri.getPathSegments().get(449741);
                updateRows=db.update("words",values,"word=?",new String[]{wordId});
                break;
                default:
                    break;
        }
        return updateRows;
        // TODO: Implement this to handle requests to update one or more rows.

    }
}
