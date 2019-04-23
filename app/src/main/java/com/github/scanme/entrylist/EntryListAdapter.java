package com.github.scanme.entrylist;

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

import com.github.scanme.BitmapHandler;
import com.github.scanme.MainActivity;
import com.github.scanme.R;
import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;

public class EntryListAdapter extends RecyclerView.Adapter<EntryViewHolder> {
    Context activityContext;
    List<QR> entriesData;

    List<Boolean> checkboxSelections = new ArrayList<>();
    List<QR> selectedQRs = new ArrayList<>();
    boolean selectMode = false;

    QR deletedItem;
    int deletedPos;

    private QRRepository qrRepo;

    public EntryListAdapter(Context activityContext, List<QR> entriesData) {
        //super(activityContext, 0, entriesData);
        this.activityContext = activityContext;
        this.entriesData = entriesData;
        this.qrRepo = new QRRepository(((MainActivity) activityContext).getApplication());
    }
    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View entryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_row, parent, false);
        return new EntryViewHolder(activityContext, entryView);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, final int position) {
        QR qr = getItem(position);
        holder.setItem(qr);

       final CheckBox checkbox = holder.getCheckbox();
       checkbox.setChecked(checkboxSelections.get(position));
       holder.getCheckbox().setVisibility(selectMode ? View.VISIBLE : View.GONE);

       holder.getView().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (selectMode) { // allows selection by clicking entire row (instead of just checkbox)
                   checkbox.setChecked(!checkbox.isChecked());
                   setSelected(position, checkbox.isChecked());
               } else {
                   ((MainActivity) activityContext).openViewEntryActivity(getItem(position));
               }
           }

       });
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
    public int getItemCount() {
        return entriesData.size();
    }

    public QR getItem(int position) {
        return entriesData.get(position);
    }

    public void deleteItem(int position) {
        deletedItem = entriesData.get(position);
        deletedPos = position;
        qrRepo.delete(deletedItem);
       // showUndo();
    }

    private void showUndo() {
        View view = ((MainActivity) activityContext).findViewById(R.id.wrapper);
        Snackbar snackbar = Snackbar.make(view, "UNDO", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });
        snackbar.show();
    }

    private void undo() {
        qrRepo.insert(deletedItem);
    }

    public boolean isEmpty() {
        return entriesData.isEmpty();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        // notifyDataSetChanged();
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

    public Context getContext() {
        return this.activityContext;
    }

}
