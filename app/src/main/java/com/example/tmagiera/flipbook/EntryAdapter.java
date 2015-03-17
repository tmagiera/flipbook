package com.example.tmagiera.flipbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tmagiera.flipbook.KwejkXmlParser.Entry;

import java.util.List;

public class EntryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Entry> mEntries;
    private DrawableLoader drawableLoader = new DrawableLoader();
//
//    private static class ViewHolder {
//        public final ImageView image;
//        public final TextView title;
//
//        public ViewHolder(ImageView image, TextView title) {
//            this.image = image;
//            this.title = title;
//        }
//    }

    public EntryAdapter(Context context, List<Entry> entries) {
        mInflater = LayoutInflater.from(context);
        mEntries = entries;
    }

    @Override
    public int getCount() {
        return mEntries.size()-1;
    }

    @Override
    public Object getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        TextView title;

 //       if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_entry, parent, false);
            Log.d("EntryAdapter", convertView.toString());
            image = (ImageView)convertView.findViewById(R.id.entryImage);
            title = (TextView)convertView.findViewById(R.id.entryTitle);
//            convertView.setTag(new ViewHolder(image, title));
//        } else {
//            ViewHolder holder = (ViewHolder)convertView.getTag();
//            image = holder.image;
//            title = holder.title;
//        }

        Entry entry = mEntries.get(position);

        image.setMinimumHeight(entry.height);
        drawableLoader.fetchDrawableOnThread(entry.source, image);
        title.setText(entry.title);

        return convertView;
    }

}
