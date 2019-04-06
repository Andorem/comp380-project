package com.github.scanme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.github.scanme.database.QR;

public class EntriesListAdapter extends ArrayAdapter<QR> {
    Context activityContext;
    List<QR> entriesData;

    EntriesListAdapter(Context activityContext, List<QR> entriesData) {
        super(activityContext, 0, entriesData);
        this.activityContext = activityContext;
        this.entriesData = entriesData;
    }

    // Dynamically create item in list for each entry on save/edit
    @Override
    public View getView(int position, View entryView, ViewGroup parent) {
        QR entry = entriesData.get(position);
        if(entryView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            entryView = inflater.inflate(R.layout.entry_row, null); // custom list item layout

            TextView entryTitle = entryView.findViewById(R.id.title);
            TextView entryDescription = entryView.findViewById(R.id.description);
            ImageView entryImage = entryView.findViewById(R.id.thumbnail);

            entryTitle.setText(entry.getTitle());
            entryDescription.setText(entry.getDescription());
            Bitmap image = BitmapHandler.decodeAsThumbnail(getContext(), entry.getImagePath(), 200,200);
            entryImage.setImageBitmap(image);
        }
        return entryView;
    }

    public void updateEntries(List<QR> entries) {
        entriesData = entries;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entriesData.size();
    }

    @Override
    public QR getItem(int position) {
        return entriesData.get(position);
    }

    @Override
    public boolean isEmpty() {
        return entriesData.isEmpty();
    }

   /* @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
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
    }*/

}
