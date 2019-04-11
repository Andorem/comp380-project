package com.github.scanme;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.scanme.database.QR;


public class EntriesListAdapter extends ArrayAdapter<QR> {
    Context activityContext;
    List<QR> entriesData;

    List<Boolean> checkboxSelections = new ArrayList<>();
    List<QR> selectedQRs = new ArrayList<>();
    boolean selectMode = false;

    private static class EntryViewHolder {
        private TextView title;
        private TextView description;
        private ImageView thumbnail;
        private CheckBox checkbox;
    }

    EntriesListAdapter(Context activityContext, List<QR> entriesData) {
        super(activityContext, 0, entriesData);
        this.activityContext = activityContext;
        this.entriesData = entriesData;
    }

    // Dynamically create item in list for each entry on save/edit
    @Override
    public View getView(final int position, View entryView, ViewGroup parent) {
        EntryViewHolder holder;
        if (entryView == null) {
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            entryView = inflater.inflate(R.layout.entry_row, null); // custom list item layout
            holder = new EntryViewHolder();
            holder.title = entryView.findViewById(R.id.title);
            holder.description = entryView.findViewById(R.id.description);
            holder.thumbnail = entryView.findViewById(R.id.thumbnail);
            holder.checkbox = entryView.findViewById(R.id.checkbox);
            entryView.setTag(holder);
        }
        else {
            holder = (EntryViewHolder) entryView.getTag();
        }

        final QR qr = getItem(position);
        holder.title.setText(qr.getTitle());
        holder.description.setText(qr.getDescription());
        Bitmap image = BitmapHandler.decodeAsThumbnail(getContext(), qr.getImagePath(), 50, 50);
        holder.thumbnail.setImageBitmap(image);
        holder.checkbox.setChecked(checkboxSelections.get(position));
        holder.checkbox.setVisibility(selectMode ? View.VISIBLE : View.GONE);

        ((ListView) parent).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if (selectMode) { // allows selection by clicking entire row (instead of just checkbox)
                    CheckBox checkbox = view.findViewById(R.id.checkbox);
                    checkbox.setChecked(!checkbox.isChecked());
                    setSelected(position, checkbox.isChecked());
                }
                else {
                    ((MainActivity) activityContext).openViewEntryActivity(getItem(position));
                }
            }

        });

        if (selectMode) {
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkbox = (CheckBox) v;
                    setSelected(position, checkbox.isChecked());
                }
            });
        }

        return entryView;
    }

    public void updateEntries(List<QR> entries) {
        entriesData = entries;

        // Populate checkbox selections with default values for new rows
        for (int i = 0; i < entriesData.size(); i++) {
            if (checkboxSelections.contains(i) == false) checkboxSelections.add(i, false);
        }
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

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        //notifyDataSetChanged();
    }

    public boolean getSelectMode() {
        return this.selectMode;
    }

    public void toggleSelectMode() {
        this.selectMode = !this.selectMode;
        Collections.fill(checkboxSelections, Boolean.FALSE);
        notifyDataSetChanged();
    }

    public void setSelected(int position, boolean selected) {
        QR selectedQR = getItem(position);
        checkboxSelections.set(position, selected);
        if (selected) selectedQRs.add(selectedQR);
        else selectedQRs.remove(selectedQR);
    }

    public List<QR> getSelectedQRs() {
        return selectedQRs;
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
