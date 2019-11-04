package com.example.thewordbooks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class word_agoDBHelper extends SQLiteOpenHelper {
        private String CREATE_AGO_TABLE="create table words_ago(_id integer primary key autoincrement,word_ago varchar(200))";
        public  word_agoDBHelper(Context context){


            super(context,"record_db",null,2);
        }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_AGO_TABLE);
        Log.d("#DB","WordAgo");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
