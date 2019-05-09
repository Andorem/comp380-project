package com.github.scanme.entrylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.scanme.BitmapHandler;
import com.github.scanme.MainActivity;
import com.github.scanme.R;
import com.github.scanme.database.QR;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.RecyclerView;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView description;
    private ImageView thumbnail;
    private CheckBox checkbox;
    private FloatingActionButton icon;
    private QR item;
    private Context context;

    EntryViewHolder(Context context, View view) {
        super(view);
        this.title = view.findViewById(R.id.title);
        this.description = view.findViewById(R.id.description);
        this.thumbnail = view.findViewById(R.id.thumbnail);
        this.icon = view.findViewById(R.id.locationIcon);
        this.checkbox = view.findViewById(R.id.checkbox);
        this.context = context;
    }

    void setItem(QR qr) {
        this.item = qr;

        title.setText(qr.getTitle());
        description.setText(qr.getDescription());
        setImage();
        icon = qr.getLocationButton(icon, true);
    }

    CheckBox getCheckbox() {
        return this.checkbox;
    }

    View getView() {
        return this.itemView;
    }

    private void setImage() {
            Bitmap image = BitmapHandler.decodeAsThumbnail(context, item.getImagePath(), 75, 75);
            thumbnail.setImageBitmap(image);
    }


}