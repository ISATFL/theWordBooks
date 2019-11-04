package com.example.thewordbooks.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thewordbooks.HttpUtil;
import com.example.thewordbooks.JinshanParseUtil;
import com.example.thewordbooks.R;

import java.io.IOException;



import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
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
public class Right_fragment extends Fragment implements  TextToSpeech.OnInitListener{
    String search_word="";
    String sample="";
    TextView  sample_result;
    private TextToSpeech tts;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.right_fragmemt,container,false);
        tts=new TextToSpeech(getActivity(),this);
        sample_result=view.findViewById(R.id.sample_view);
        sample_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(sample_result.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
//        sample_result=view.findViewById(R.id.sample);
//        Bundle bundle=getArguments();
//        if(bundle!=null) {
//            Log.d("11",sample);
//            sample = bundle.getString("sample");
//
//            sample_result.setText(sample);
//
//        }
        return view;
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

}
