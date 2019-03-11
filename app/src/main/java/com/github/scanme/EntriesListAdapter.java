package com.github.scanme;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.github.scanme.QREntry;

public class EntriesListAdapter implements ListAdapter {
    Context activityContext;
    ArrayList<QREntry> entriesData;

    EntriesListAdapter(Context activityContext, ArrayList<QREntry> entriesData) {
        this.activityContext = activityContext;
        this.entriesData = entriesData;
    }

    // Dynamically create item in list for each entry on save/edit
    @Override
    public View getView(int position, View entryView, ViewGroup parent) {
        QREntry entry = entriesData.get(position);
        if(entryView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            entryView = inflater.inflate(R.layout.entry_row, null); // custom list item layout
            entryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go to specific Entry screen
                }
            });
            TextView entryTitle = entryView.findViewById(R.id.title);
            TextView entryDescription = entryView.findViewById(R.id.description);
            ImageView entryImage = entryView.findViewById(R.id.thumbnail);

            entryTitle.setText(entry.title);
            entryDescription.setText(entry.description);
            Bitmap image = BitmapFactory.decodeFile(entry.imagePath);
            entryImage.setImageBitmap(image);
        }
        return entryView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public Object getItem(int position) {
        return entriesData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        return entriesData.size();
    }

    @Override
    public boolean isEmpty() {
        return entriesData.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

}
