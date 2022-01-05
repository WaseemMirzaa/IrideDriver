package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buzzware.iridedriver.Models.settings.Price;
import com.buzzware.iridedriver.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;

    ArrayList<Price> types;

    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, ArrayList<Price> list) {

        this.context = applicationContext;

        this.types = list;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_item, null);
        TextView name = (TextView) view.findViewById(R.id.titleTV);

        name.setText(types.get(i).name);
        return view;
    }
}