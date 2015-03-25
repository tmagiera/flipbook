package com.example.tmagiera.flipbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tmagiera.flipbook.KwejkXmlParser.Entry;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.List;

public class EntryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Entry> mEntries;
    private AnimatedGifLoader animatedGifLoader = new AnimatedGifLoader();
    DisplayImageOptions options;


    private static class ViewHolder {
        public final ImageView image;
        public final AnimatedGifImageView animatedGifImageView;
        public final TextView title;

        public ViewHolder(ImageView image, AnimatedGifImageView animatedGifImageView, TextView title) {
            this.image = image;
            this.animatedGifImageView = animatedGifImageView;
            this.title = title;
        }
    }

    public EntryAdapter(Context context, List<Entry> entries) {
        mInflater = LayoutInflater.from(context);
        mEntries = entries;
        mContext = context;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageOnFail(R.drawable.ic_error_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
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
        AnimatedGifImageView animatedgif;
        TextView title;

        //if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_entry, parent, false);
//            Log.d("EntryAdapter", convertView.toString());
            image = (ImageView)convertView.findViewById(R.id.entryImage);
            animatedgif = (AnimatedGifImageView)convertView.findViewById(R.id.entryAnimatedGif);
            title = (TextView)convertView.findViewById(R.id.entryTitle);
//            convertView.setTag(new ViewHolder(image, animatedgif, title));
//        } else {
//            ViewHolder holder = (ViewHolder)convertView.getTag();
//            image = holder.image;
//            animatedgif = holder.animatedGifImageView;
//            title = holder.title;
//        }

        Entry entry = mEntries.get(position);

        if (entry.source.endsWith(".gif")) {
            image.setEnabled(false);
            animatedGifLoader.fetchAnimatedGifOnThread(entry.source, animatedgif);
        } else {
            animatedgif.setEnabled(false);
            //image.setEnabled(false);
            //image.setMinimumHeight(convertView.getMeasuredWidth()/entry.width * entry.height);
            ImageLoader.getInstance().displayImage(entry.source, image, options);
       }
        title.setText(entry.pageNumber + "@" + entry.height + ":" + entry.title);

        return convertView;
    }
}
