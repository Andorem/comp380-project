/* This class defines a Repository for accessing the entire database.
   The repository acts as the gateway for the rest of the app to interact with the database.
   It handles all the business logic (setting up the Dao, data operations) and provides a
   clean result.
 */

package com.github.scanme.database;

import android.app.Application;
import android.os.AsyncTask;

import java.io.File;
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

    public LiveData<List<QR>> getQRs(String search) {
        return qrDao.getQRs(search);
    }

    public void insert(QR qr) {
        new insertAsync(qrDao).execute(qr);
    }

    public void delete(QR qr) {
        new deleteAsync(qrDao).execute(qr);
    }

    public void update(QR qr) { new updateAsync(qrDao).execute(qr); }

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

    private class deleteAsync extends AsyncTask<QR, Void, Void> {
        private QRDao asyncDao;

        deleteAsync(QRDao nonAsyncDao) {
            asyncDao = nonAsyncDao;
        }

        @Override
        protected Void doInBackground(final QR... params) {
            QR qr = params[0];
            asyncDao.delete(qr);
            deleteFile(qr.getImagePath());
            deleteFile(qr.getQrPath());
            return null;
        }
    }

    private class updateAsync extends AsyncTask<QR, Void, Void> {
        private QRDao asyncDao;

        updateAsync(QRDao nonAsyncDao) {
            asyncDao = nonAsyncDao;
        }

        @Override
        protected Void doInBackground(final QR... params) {
            asyncDao.update(params[0]);
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

    /*HELPER METHODS*/
    private void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }
}
