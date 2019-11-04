package com.example.thewordbooks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class wordsDBHelper extends SQLiteOpenHelper {
//    private final static String DATABASE_NAME="dictionary_db";
//    private final static int DATABASE_VERSION=1;
        private String SQL_CREATE_DATABASE="create table words ("+
        "_id integer primary key autoincrement,"+
        "word TEXT,"+
        "pronunciation TEXT,"
        + "meaning TEXT,"+
        "sample TEXT)";
    private Context mContext;


    public wordsDBHelper(Context context){
        super(context,"dictionary_db",null,1);
        mContext=context;

    }
    @Override

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
            sqLiteDatabase.execSQL("CREATE TABLE englishwords (word TEXT, pronunciation TEXT,  meaning TEXT);");
//             Log.d("#DB","Words");
//            try {
//                BufferedReader br = null;
//                String sql="";
//                br = new BufferedReader(new InputStreamReader(mContext.getAssets().open("words")));
//                while((sql=br.readLine())!=null){
//                    sqLiteDatabase.execSQL(sql,null);
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
