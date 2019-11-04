package com.example.thewordbooks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static android.content.Context.MODE_PRIVATE;

public class JinshanParseUtil {
    private final static String TAG="解析工具";
    public static void parseJinshanEnglishToChineseXMLWithPull(String result){
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(result));
            int eventType = xmlPullParser.getEventType();

            String queryText = "";      //查询文本
            String voiceText = "";      //发音信息
            String voiceUrlText = "";   //发音地址信息
            String meanText = "";       //基本释义信息
            String exampleText = "";    //例句信息

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //开始解析
                    case XmlPullParser.START_TAG: {
                        switch (nodeName) {
                            case "key":
                                queryText += xmlPullParser.nextText();
                                break;
                            case "ps":
                                voiceText += xmlPullParser.nextText() + "|";
                                break;
                            case "pron":
                                voiceUrlText += xmlPullParser.nextText() + "|";
                                break;
                            case "pos":
                                meanText += xmlPullParser.nextText() + "  ";
                                break;
                            case "acceptation":
                                meanText += xmlPullParser.nextText();
                                break;
                            case "orig":
                                exampleText += xmlPullParser.nextText();
                                exampleText = exampleText.substring(0,exampleText.length()-1);
                                break;
                            case "trans":
                                exampleText += xmlPullParser.nextText();
                                break;
                            default:
                                break;
                        }
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

            String[] voiceArray = voiceText.split("\\|");
            String[] voiceUrlArray = voiceUrlText.split("\\|");

            meanText = meanText.substring(0,meanText.length()-1);
            exampleText = exampleText.substring(1,exampleText.length());

            //创建SharedPreferences.Editor对象，指定文件名为
            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("JinshanEnglishToChinese",MODE_PRIVATE).edit();

            editor.clear();

            editor.putString("queryText",queryText);
            editor.putString("voiceEnText","["+voiceArray[0]+"]");
            editor.putString("voiceEnUrlText",voiceUrlArray[0]);
            editor.putString("voiceAmText","["+voiceArray[1]+"]");
            editor.putString("voiceAmUrlText",voiceUrlArray[1]);
            editor.putString("meanText",meanText);
            editor.putString("exampleText",exampleText);

            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG,"解析过程出错"+e.getMessage());
        }
    }
}
