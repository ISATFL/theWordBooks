package com.example.thewordbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import  java.io.*;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.io.InputStreamReader;
import java.util.Locale;
import android.app.Dialog;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class port_activity extends AppCompatActivity implements OnInitListener {
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
    TextView pron_result;
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
                final View add_view= LayoutInflater.from(port_activity.this).inflate(R.layout.add_dialog,null);
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
                        Toast.makeText(port_activity.this,"添加成功",Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onCreate (final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_port);

        //
        tts=new TextToSpeech(this,this);

        initView();
//      initializeData();
        initData();
        initListener();


        list_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("选择操作");
                contextMenu.add(0,0,0,"删除该条");
                contextMenu.add(0,1,0,"修改该条");
            }
        });
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            Intent intent=new Intent();
            intent.setClass(port_activity.this,land_activity.class);
            startActivity(intent);
            port_activity.this.finish();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // String id=String.valueOf(info.id);
        switch (item.getItemId()){
            case 0:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("友情提示");
                builder.setMessage("您确定要删除吗？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dictionary_db=wordsDBHelper.getWritableDatabase();
                        dictionary_db.execSQL("delete from words where word=?",new String[]{del_word});
                        dictionary_db.close();
                        //从数据库删除
                        cursor=wordsDBHelper.getReadableDatabase().rawQuery("select * from words where word=? limit 0,20",new String[]{del_word});
                        //Log.d("#123","breakpoint2");
                        adapter = new SimpleCursorAdapter(port_activity.this, android.R.layout.simple_list_item_1,cursor, new String[]{"word"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                        list_view.setAdapter(adapter);
                        Toast.makeText(port_activity.this,"已删除"+del_word,Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();

                return true;

            case 1:
                AlertDialog.Builder c_dialog=new AlertDialog.Builder(this);
                final View change_view= LayoutInflater.from(port_activity.this).inflate(R.layout.change_dialog,null);
                c_dialog.setTitle("修改单词");
                c_dialog.setView(change_view);
                final EditText word_text=((EditText) change_view.findViewById(R.id.change_word));
                final EditText pronunciation_text=change_view.findViewById(R.id.change_pronunciation);
                final EditText meaning_text=change_view.findViewById(R.id.change_meaning);
                final EditText sample_text=change_view.findViewById(R.id.change_sample);

                Cursor cursor_result;
                cursor_result=wordsDBHelper.getReadableDatabase().rawQuery("select * from words where word=?",new String[]{change_word});
                String word = "";
                String pronunciation="";
                String meaning="";
                String sample="";
                if(cursor_result.getCount()!=0) {

                    cursor_result.moveToNext();


                    word = cursor_result.getString(cursor_result.getColumnIndex("word"));
                    pronunciation = cursor_result.getString(cursor_result.getColumnIndex("pronunciation"));
                    meaning = cursor_result.getString(cursor_result.getColumnIndex("meaning"));
                    sample = cursor_result.getString(cursor_result.getColumnIndex("sample"));

                }

                word_text.setText(word);
                pronunciation_text.setText(pronunciation);
                meaning_text.setText(meaning);
                sample_text.setText(sample);
                c_dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        String f_word=word_text.getText().toString();
                        String f_pronunciation=pronunciation_text.getText().toString();
                        String f_meaning=meaning_text.getText().toString();
                        String f_sample=sample_text.getText().toString();

                        dictionary_db=wordsDBHelper.getWritableDatabase();
                        dictionary_db.execSQL("update words set word=?,pronunciation=?,meaning=?,sample=? where word=?",new String[]{f_word,f_pronunciation,f_meaning,f_sample,change_word});
                        dictionary_db.close();
                        Toast.makeText(port_activity.this,"修改成功",Toast.LENGTH_SHORT).show();

                    }
                });
                c_dialog.setNegativeButton("取消",null);
                c_dialog.show();

                //将当前的数据获取并将数据输出在窗口，用户可以进行修改；
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void initView() {//
        input_word = findViewById(R.id.word);
        find = findViewById(R.id.find);
        ago = findViewById(R.id.ago);
        clear = findViewById(R.id.clear);
        list_view = findViewById(R.id.listView);
        meaning_result=findViewById(R.id.meaning);
        pron_result=findViewById(R.id.pron_result);
    }

    private void initData()  {//获取历史记录
        wordsDBHelper = new wordsDBHelper(this);
        word_agoDBHelper = new word_agoDBHelper(this);

        //initializeData();
        cursor = word_agoDBHelper.getReadableDatabase().rawQuery("select * from words_ago ", null);
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list_view.setAdapter(adapter);

    }



    private void deleteData() {//防止记录重复
        dictionary_db = wordsDBHelper.getWritableDatabase();
        dictionary_db.execSQL("delete from words");
        dictionary_db.close();
    }

    private void initializeData()   {//初始化词典
        // deleteData();
        dictionary_db=wordsDBHelper.getWritableDatabase();
//        dictionary_db.execSQL("insert into words values (null,?,?,?,?)",new String[] {"his","his","他","his father"});
//        dictionary_db.execSQL("insert into englishwords values (?,?,?)",new String[] {"his","his","他"});

        try{
            InputStreamReader inputReader=new InputStreamReader(getResources().getAssets().open("words_1.txt"));
            BufferedReader bufferedReader=new BufferedReader(inputReader);
            String sql="";
            while((sql=bufferedReader.readLine())!=null){
                dictionary_db.execSQL(sql);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        //dictionary_db = wordsDBHelper.getWritableDatabase();
//        try{
//            InputStream is=getAssets().toString().
//        }

//       File file=new File("D:dic.txt");

//       try{
//
//           BufferedReader br=new BufferedReader(new FileReader(file));
//           meaning_result.setText("hahah");

//
//           //meaning_result.setText(s);
//           while((s=br.readLine())!=null){
//              dictionary_db.execSQL(s);
//
//           }br.close();
//       }catch(Exception e){
//           e.printStackTrace();
//       }
//
//        //dictionary_db.execSQL("create table words_ago(_id integer primary key autoincrement,word_ago varchar(200))");
//

        //dictionary_db.close();
    }

    private void initListener() {
        clear.setOnClickListener(new View.OnClickListener() {//清除历史记录
            @Override
            public void onClick(View view) {
                deleteAgo();
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                insertAgo(input_word.getText().toString().trim());
            }
        });//搜索时保存搜索记录
        input_word.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //保存搜索记录
                    insertAgo(input_word.getText().toString().trim());
                }
                return false;
            }
        });
        //实时搜索
        input_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(input_word.getText().toString().equals("")){
                    ago.setText("搜索历史");
                    clear.setVisibility(View.VISIBLE);
                    cursor=word_agoDBHelper.getReadableDatabase().rawQuery("select * from words_ago",null);
                    adapter = new SimpleCursorAdapter(port_activity.this, android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                    list_view.setAdapter(adapter);
                    meaning_result.setText("");
                    pron_result.setText("");
                    //refresh();
                }else{
                    ago.setText("搜索结果");
                    clear.setVisibility(View.GONE);
                    String search=input_word.getText().toString();

                    search_result(search);
                }
            }
        });
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String search_word=((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                del_word=search_word;
                change_word=search_word;
                Cursor cursor_result;
                cursor_result=wordsDBHelper.getReadableDatabase().rawQuery("select * from words where word=?",new String[]{search_word});
                if(cursor_result.getCount()!=0) {

                    cursor_result.moveToNext();
                    String meaning = "";
                    String pron="";

                    meaning = cursor_result.getString(cursor_result.getColumnIndex("meaning"));
                    pron=cursor_result.getString(cursor_result.getColumnIndex("pronunciation"));

                    tts.speak(search_word,TextToSpeech.QUEUE_FLUSH,null);
                    //tts.speak(meaning,TextToSpeech.QUEUE_FLUSH,null);
                    pron_result.setText("["+pron+"]");

                    meaning_result.setText(meaning);

                }



//                String utlxml="http://dict-co.iciba.com/api/dictionary.php?w="+search_word+"&key=C2FC88A32BD1979D0535E18BE84219D9";
//                HttpUtil.sendHttpRequest(utlxml, new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Toast.makeText(port_activity.this,"获取例句失败",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        final String result=response.body().string();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
//                                JinshanParseUtil.parseJinshanEnglishToChineseXMLWithPull(result);
//                                SharedPreferences pref=getSharedPreferences("JinshanEnglishToChinese",MODE_PRIVATE);
//
//                                sample=pref.getString("exampleText","空");
//
//                            }
//                        });
//                    }
//                });
//                meaning_result.setText(sample);



                //listView 点击事件
            }
        });
    }
    private boolean hasDataAgo(String records){
        Log.d("#123",records);
        cursor=word_agoDBHelper.getReadableDatabase().rawQuery("select _id,word_ago from words_ago where  word_ago=?",new String[]{records});
        Log.d("#123","breakpoint1");
        return cursor.moveToNext();
    }
    private  void search_result(String word){
        Log.d("#123",word);
        cursor=wordsDBHelper.getReadableDatabase().rawQuery("select * from words where word like '"+word+"%' limit 0,20",null);
        //Log.d("#123","breakpoint2");
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,cursor, new String[]{"word"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list_view.setAdapter(adapter);
        //refresh();
    }
    private void deleteAgo() {
        ago_db = word_agoDBHelper.getWritableDatabase();
        ago_db.execSQL("delete from words_ago");
        cursor = word_agoDBHelper.getReadableDatabase().rawQuery("select * from words_ago", null);


        if (input_word.getText().toString().equals("")) {
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            list_view.setAdapter(adapter);
        }

    }

    private void refresh() {

        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }//刷新列表记录

    private void insertAgo(String word) {
        if(!hasDataAgo(word)){
            ago_db = word_agoDBHelper.getWritableDatabase();
            ago_db.execSQL("insert into words_ago values(null,?)", new String[]{word});
            ago_db.close();
        }

    }
    @Override
    public void onInit(int i) {
        if(i==TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.US);
            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(port_activity.this, "错误", Toast.LENGTH_SHORT).show();
            }
            else{
                tts.setLanguage(Locale.CHINESE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ago_db!=null){
            ago_db.close();
        }
        if(dictionary_db!=null){
            dictionary_db.close();
        }
    }
}