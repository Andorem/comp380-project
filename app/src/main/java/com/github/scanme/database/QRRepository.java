/* This class defines a Repository for accessing the entire database.
   The repository acts as the gateway for the rest of the app to interact with the database.
   It handles all the business logic (setting up the Dao, data operations) and provides a
   clean result.
 */

package com.github.scanme.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
public class QRRepository {
    private QRDao qrDao;
    private LiveData<List<QR>> QRs;

    // Set up the database, DAO, and member entities
    public QRRepository(Application application) {
        QRDatabase database = QRDatabase.getDatabase(application);
        qrDao = database.qrDao();
        QRs = qrDao.getAll();
    }

    /* PUBLIC METHODS */
    public LiveData<List<QR>> getAllQRs() {
        return QRs;
    }

    public LiveData<QR> getQR(String id) {
        return qrDao.getByID(id);
    }

    public void insert(QR qr) {
        new insertAsync(qrDao).execute(qr);
    }

    /*PRIVATE ASYNC TASKS*/

    // The Room database requires any data insertion to be in a separate thread (async)
    // This avoids clogging up the main thread, otherwise the UI could be slowed down.
    private class insertAsync extends AsyncTask<QR, Void, Void> {
        private QRDao asyncDao;

        insertAsync(QRDao nonAsyncDao) {
            asyncDao = nonAsyncDao;
        }

        @Override
        protected Void doInBackground(final QR... params) {
            asyncDao.insert(params[0]);
            return null;
        }
    }

    /*private class getQRAsync extends AsyncTask<String, Void, Void> {
        private QRDao asyncDao;

        getQRAsync(QRDao nonAsyncDao) {
            asyncDao = nonAsyncDao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncDao.getByID(params[0]);
            return null;
        }
    }*/
}
