package com.github.scanme.entrylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    List<QR> removedQRs = new ArrayList<>();
    boolean selectMode = false;

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

        final QR qr = getItem(position);
        holder.setItem(qr);

       if (removedQRs.contains(qr)) {
            holder.itemView.setVisibility(View.GONE);
        }
        else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

       final CheckBox checkbox = holder.getCheckbox();
       checkbox.setChecked(checkboxSelections.get(position));
       holder.getCheckbox().setVisibility(selectMode ? View.VISIBLE : View.GONE);

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(position, checkbox.isChecked());
                Log.d("LIST", "checkbox " + position + " (" + qr.getTitle() + ") = " + checkbox.isChecked());
                String temp = "";
                for (QR qr : getSelectedQRs()) temp += qr.getTitle() + ", ";
                Log.d("LIST", "adapter QRs: " + temp);
            }

        });

       holder.getView().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (selectMode) { // allows selection by clicking entire row (instead of just checkbox)
                   checkbox.setChecked(!checkbox.isChecked());
                   setSelected(position, checkbox.isChecked());
                   Log.d("LIST", "checkbox " + position + " (" + qr.getTitle() + ") = " + checkbox.isChecked());
                   String temp = "";
                   for (QR qr : getSelectedQRs()) temp += qr.getTitle() + ", ";
                   Log.d("LIST", "adapter QRs: " + temp);
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

    public void deleteItem(RecyclerView.ViewHolder holder, int position) {
        QR deletedItem = entriesData.remove(position);
        int deletedPos = position;
        removedQRs.add(deletedItem);
        notifyDataSetChanged();
        showUndo(holder, deletedItem, deletedPos);
    }

    private void showUndo(final RecyclerView.ViewHolder holder, final QR item, final int pos) {
       // notifyDataSetChanged();
        View view = ((MainActivity) activityContext).findViewById(R.id.wrapper);
        Snackbar snackbar = Snackbar.make(view, "Deleted QR", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo(item, pos);
            }
        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    for (QR item : removedQRs) {
                        qrRepo.delete(item);
                    }
                    removedQRs.clear();
                }
            }
        });
        snackbar.show();
    }

    private void undo(QR item, int pos) {
        if (entriesData.isEmpty()) entriesData.add(item);
        else entriesData.add(pos, item);
        removedQRs.remove(item);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return entriesData.isEmpty();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
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

    public boolean isSelectedQRsEmpty() {
        return selectedQRs.isEmpty();
    }

    public void clearSelectedQRs() {
        selectedQRs = new ArrayList<>();
    }

    public Context getContext() {
        return this.activityContext;
    }

}
