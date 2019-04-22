/* This class represents a table in the database, and its instances are called Entities.
   Each Entity represents a row in the table, and contains the data for each specific QR.
   The Entity's variables represent columns that have the same name as these fields.
   The database accesses this data through getters and setters.

   ==========================[QR TABLE]===========================
   | Primary Key (ID) | Title | Description | From | To |  Image |
   |    1st QR id     |       |             |           |        | <--- 1st QR Entity (row)
   |    ...           |       |             |           |        |
   ===============================================================
 */

package com.github.scanme.database;


import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QR implements Parcelable {

    /* Table Columns */
    @PrimaryKey
    @NonNull
    private String ID;

    private String title;
    private String description;
    private String fromLoc;
    private String toLoc;
    private String imagePath;
    private String qrPath;

    /*
    public QR(String ID, String title, String description, String fromLoc, String toLoc, String imagePath) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.imagePath = imagePath;
    }
    */

    public QR(String ID, String title, String description, String imagePath, String qrPath) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.qrPath = qrPath;
    }

    /* Getters and Setters */

    public String getID() {
        return ID;
    }
    public void setID(String id) {
        this.ID = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromLoc() {
        return fromLoc;
    }
    public void setFromLoc(String fromLoc) {
        this.fromLoc = fromLoc;
    }

    public String getToLoc() {
        return toLoc;
    }
    public void setToLoc(String toLoc) { this.toLoc = toLoc; }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getQrPath() {
        return qrPath;
    }
    public void setQrPath(String qrPath) {
        this.qrPath = qrPath;
    }

    /* The below methods allow QR to be parcelable, i.e. able to pass to activity with intents */

    @Override
    public int describeContents() {
        return 0;
    }

    // Create a Parcel containing all of QR's values
    @Override
    public void writeToParcel(Parcel out, int flags) {
        String[] vals = {ID, title, description, fromLoc, toLoc, imagePath, qrPath};
        for (int i = 0; i < vals.length; i++) {
            out.writeString(vals[i]);
        }
    }

    // Required CREATOR in order to parcelize/regenerate the object
    public static final Parcelable.Creator<QR> CREATOR = new Parcelable.Creator<QR>() {
        public QR createFromParcel(Parcel in) {
            return new QR(in);
        }

        public QR[] newArray(int size) {
            return new QR[size];
        }
    };

    // Convert Parcel to object (must read in order written in)
    private QR(Parcel in) {
        ID = in.readString();
        title = in.readString();
        description = in.readString();
        fromLoc = in.readString();
        toLoc = in.readString();
        imagePath = in.readString();
        qrPath = in.readString();
    }
}
