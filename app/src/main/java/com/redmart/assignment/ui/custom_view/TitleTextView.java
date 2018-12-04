package com.redmart.assignment.ui.custom_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class TitleTextView extends TextView {

    private static final String TAG = TitleTextView.class.getName();

    public TitleTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        try{
            Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
            setTypeface(customFont);
        }catch (Exception e){
            Log.d(TAG,"Exception while setting custom textview # "+e.getMessage());
        }

    }
}
