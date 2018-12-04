package com.redmart.assignment.ui.custom_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class DescTextView extends TextView {

    private static final String TAG = DescTextView.class.getName();

    public DescTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public DescTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public DescTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        try{
            Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/desc_font.ttf");
            setTypeface(customFont);
        }catch (Exception e){
            Log.d(TAG,"Exception while setting custom textview # "+e.getMessage());
        }

    }
}
