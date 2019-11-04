package com.example.thewordbooks.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thewordbooks.List_view_ago;
import com.example.thewordbooks.R;
import com.example.thewordbooks.*;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class Left_fragment extends Fragment  implements TextToSpeech.OnInitListener {
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
    String sample=null;
    View view;
    String word_sample="";
    private TextToSpeech tts;

    //private TextToSpeech tts;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.left_fragment,container,false);
       // Log.d("HHH","fff");
        tts=new TextToSpeech(getActivity(),this);
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
        return view;
    }

    private void initView() {//
        input_word = view.findViewById(R.id.word);
        find = view.findViewById(R.id.find);
        ago = view.findViewById(R.id.ago);
        clear = view.findViewById(R.id.clear);
        list_view = view.findViewById(R.id.listView);
        meaning_result=view.findViewById(R.id.meaning);
        pron_result=view.findViewById(R.id.pron_result);


    }
    private void initData()  {//获取历史记录
        wordsDBHelper = new wordsDBHelper(this.getActivity());
        word_agoDBHelper = new word_agoDBHelper(this.getActivity());

        //initializeData();
        cursor = word_agoDBHelper.getReadableDatabase().rawQuery("select * from words_ago ", null);
        adapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list_view.setAdapter(adapter);

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
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                insertAgo(input_word.getText().toString().trim());
            }
        });//搜索时保存搜索记录
        input_word.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                    adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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
                //setData(search_word);
                cursor_result=wordsDBHelper.getReadableDatabase().rawQuery("select * from words where word=?",new String[]{search_word});
                if(cursor_result.getCount()!=0) {

                    cursor_result.moveToNext();
                    String meaning = "";
                    String pron="";
                   // word=search_word;
                    meaning = cursor_result.getString(cursor_result.getColumnIndex("meaning"));
                    pron=cursor_result.getString(cursor_result.getColumnIndex("pronunciation"));



                    //tts.speak("hello",TextToSpeech.QUEUE_FLUSH,null);
                    //tts.speak(meaning,TextToSpeech.QUEUE_FLUSH,null);
                    pron_result.setText("["+pron+"]");
                    pron_result.setVisibility(View.VISIBLE);

                    meaning_result.setText(meaning);
                }



                String utlxml="http://dict-co.iciba.com/api/dictionary.php?w="+search_word+"&key=C2FC88A32BD1979D0535E18BE84219D9";
                HttpUtil.sendHttpRequest(utlxml, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(),"获取例句失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result=response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
                                JinshanParseUtil.parseJinshanEnglishToChineseXMLWithPull(result);
                                SharedPreferences pref=getActivity().getSharedPreferences("JinshanEnglishToChinese",MODE_PRIVATE);

                                sample=pref.getString("exampleText","空");
                                //Toast.makeText(getActivity(),sample,Toast.LENGTH_SHORT).show();



                            }
                        });

                    }
                });
                pron_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.speak(change_word,TextToSpeech.QUEUE_FLUSH,null);
                    }
                });
                TextView sample_View=getActivity().findViewById(R.id.sample_view);
                sample_View.setText(sample);
                sample_View.setVisibility(View.VISIBLE);





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
        adapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_1,cursor, new String[]{"word"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list_view.setAdapter(adapter);
        //refresh();
    }
    private void deleteAgo() {
        ago_db = word_agoDBHelper.getWritableDatabase();
        ago_db.execSQL("delete from words_ago");
        cursor = word_agoDBHelper.getReadableDatabase().rawQuery("select * from words_ago", null);


        if (input_word.getText().toString().equals("")) {
            adapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_1,cursor, new String[]{"word_ago"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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

    public void onInit(int i) {
        if(i==TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.US);
            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
            }
            else{
                tts.setLanguage(Locale.CHINESE);
            }
        }
    }

    @Override
    public  void onDestroy() {
        super.onDestroy();
        if(ago_db!=null){
            ago_db.close();
        }
        if(dictionary_db!=null){
            dictionary_db.close();
        }
    }
}
