package com.nebrasapps.otlb.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class DialogList
{
    AlertDialog.Builder builder;
    protected final Activity baseActivity;
    protected List<String> strings;
    private ListView listView;
    private int colorID;
    AlertDialog dialog;

    public DialogList(Activity baseActivity)
    {
        builder = new AlertDialog.Builder(baseActivity);
        this.baseActivity = baseActivity;
        listView = new ListView(baseActivity);
        SimpleListAdapter simpleListAdapter = new SimpleListAdapter();
        listView.setAdapter(simpleListAdapter);

        colorID = Color.BLACK;

        builder.setView(listView);

    }

    public void setList(List<String> modelList)
    {
        strings = modelList;
    }

    public void show()
    {
        dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER_VERTICAL;
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    public void dismiss()
    {
        dialog.dismiss();
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener)
    {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0,+ text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(listener == null)
        {
            listener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();
                }
            };
        }

        builder.setPositiveButton(spannableString, listener);
    }

    /**
     * the defaultim color is black
     * @param color
     */
    public void setListItemColor(int color)
    {
        colorID = color;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        listView.setOnItemClickListener(listener);
    }

    //region the list adapter used for the dialog
    protected class SimpleListAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            if (strings != null)
                return strings.size();
            return 0;
        }

        @Override
        public String getItem(int i)
        {
            return strings.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            TextView textView = new TextView(baseActivity);
            textView.setPadding(10, 10, 10, 10);
            textView.setTextColor(colorID);
            textView.setText(strings.get(i));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);



            return textView;
        }
    }
    //endregion
}
