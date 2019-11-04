package com.example.thewordbooks;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.jar.Attributes;

public class List_view_ago extends ListView {
    public List_view_ago(Context context){
        super(context);

    }
    public List_view_ago(Context context, AttributeSet attr){
        super(context,attr);
    }
    public List_view_ago(Context context, AttributeSet attr,int def){
        super(context,attr,def);
    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
