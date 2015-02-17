package com.main.toledo.gymtrackr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kodie Glosser on 2/7/2015.
 */
public class CopyDatabase extends SQLiteOpenHelper{

    private static String DB_PATH = "/data/data/com.main.toledo.gymtrackr/databases/";
    private static String DB_NAME = "gymtrackr.db";
    private SQLiteDatabase myDatabase;

    private final Context myContext;

    public CopyDatabase (Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates an empty DB on the system it will rewrite if with my own DB
     * @throws IOException
     */
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();

        if(dbExist){
            // no need to do anything
        }
        else {
            this.getReadableDatabase();

            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error Copying database");
            }
        }
    }

    /**
     * Check if the DB already exists to avoid re-copying the file each time the application is opened
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            Log.e("Query Failure", e.getMessage());
            // DB doesnt exist yet
        }

        if(checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies the DB from the local assets-folder to the new empty database in the system folder.
     * Bytestream transfer.
     */
    private void copyDatabase() throws IOException {
        // open local DB as the input
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // path to the just created empty DB
        String outFileName = DB_PATH + DB_NAME;

        // open the empty DB as the output
        OutputStream myOutput = new FileOutputStream(outFileName);

        // xfers bytes from the input to the output
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer,0,length);
        }

        //close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

//    public void openDataBase() throws SQLException{} {
//        // open DB
//        String myPath = DB_PATH + DB_NAME;
//        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//    }


    @Override
    public synchronized void close() {
        if(myDatabase != null)
            myDatabase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
