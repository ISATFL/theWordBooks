package com.example.thewordbooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class land_activity extends AppCompatActivity {
    EditText input_word;
    TextView find;
    TextView ago;
    TextView clear;
    List_view_ago list_view;
    wordsDBHelper wordsDBHelper;
    word_agoDBHelper word_agoDBHelper;
    SQLiteDatabase dictionary_db;
    SQLiteDatabase ago_db;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    TextView meaning_result;
    String del_word="";
    String change_word="";
    String sample="";
    private TextToSpeech tts;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_word:
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                final View add_view= LayoutInflater.from(land_activity.this).inflate(R.layout.add_dialog,null);
                dialog.setTitle("添加单词");
                dialog.setView(add_view);
                dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText word_text=add_view.findViewById(R.id.word);
                        EditText pronunciation_text=add_view.findViewById(R.id.pronunciation);
                        EditText meaning_text=add_view.findViewById(R.id.meaning);
                        EditText sample_text=add_view.findViewById(R.id.sample);
                        String word=word_text.getText().toString();
                        String pronunciation=pronunciation_text.getText().toString();
                        String meaning=meaning_text.getText().toString();
                        String sample=sample_text.getText().toString();

                        dictionary_db=wordsDBHelper.getWritableDatabase();
                        dictionary_db.execSQL("insert into words values (null,?,?,?,?)",new String[]{word,pronunciation,meaning,sample});
                        dictionary_db.close();
                        Toast.makeText(land_activity.this,"添加成功",Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.setNegativeButton("取消",null);
                dialog.show();
                break;
            case R.id.help:
                Toast.makeText(this,"这是帮助",Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("友情提示");
                builder.setMessage("您确定要退出吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                //Toast.makeText(this,"退出",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_land);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            Intent intent=new Intent();
            intent.setClass(land_activity.this,port_activity.class);
            startActivity(intent);
            land_activity.this.finish();
        }
    }
}
