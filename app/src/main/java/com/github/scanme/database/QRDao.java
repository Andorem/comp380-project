/* This interface is called a Data Access Object (Dao).
   It defines the SQL queries that are used to access and modify the database.
   It then associates these queries with empty methods that are easier to remember/read.
 */

package com.github.scanme.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
interface QRDao {
    @Query("SELECT * FROM qr")
    LiveData<List<QR>> getAll();

    @Query("SELECT * FROM qr WHERE id IN (:IDs)")
    LiveData<List<QR>> getAllOf(int[] IDs);

    @Query("SELECT * FROM qr WHERE id IN (:title)")
    LiveData<List<QR>> getAllByTitle(String title);

    @Query("SELECT * FROM qr WHERE fromLoc IN (:from)")
    LiveData<List<QR>> getAllFrom(String from);

    @Query("SELECT * FROM qr WHERE toLoc IN (:to)")
    LiveData<List<QR>> getAllTo(String to);

    @Query("SELECT * FROM qr WHERE id LIKE :id LIMIT 1")
    QR getByID(String id);

    @Insert
    void insert(QR... qrs);

    @Delete
    void delete(QR qr);

    @Update
    void update(QR qr);
}
