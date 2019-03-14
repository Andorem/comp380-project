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


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QR {

    /* Table Columns */
    @PrimaryKey
    @NonNull
    public String ID;

    public String title;
    public String description;
    public String fromLoc;
    public String toLoc;
    public String imagePath;

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

    public QR(String ID, String title, String description, String imagePath) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
    }

    /* Getters and Setters */

    public String getId() {
        return ID;
    }
    public void setId(String id) {
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

    public String getFrom() {
        return fromLoc;
    }
    public void setFrom(String fromLoc) {
        this.fromLoc = fromLoc;
    }

    public String getTo() {
        return toLoc;
    }
    public void setTo(String toLoc) { this.toLoc = toLoc; }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
