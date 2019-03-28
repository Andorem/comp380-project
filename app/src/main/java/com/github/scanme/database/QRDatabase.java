/* This class represents the Room database that acts as a layer on top of an SQL database.
   It uses the Dao object to issue query calls and automatically performs them asynchronously.
 */

package com.github.scanme.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {QR.class}, version = 1, exportSchema = false)
abstract class QRDatabase extends RoomDatabase {

    // Dao getter for issuing queries
    public abstract QRDao qrDao();

    /* This ensures the database is a singleton:
          Instantiating a Room database takes a lot of resources.
          This makes sure there is only one instance of the database at a time.
     */
    private static volatile QRDatabase INSTANCE;
    static QRDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QRDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), QRDatabase.class, "qrDatabase").build();
                }
            }
        }
        return INSTANCE;
    }
}
