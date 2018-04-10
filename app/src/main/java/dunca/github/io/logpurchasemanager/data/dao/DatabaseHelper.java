package dunca.github.io.logpurchasemanager.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import dunca.github.io.logpurchasemanager.data.StaticDataGenerator;
import dunca.github.io.logpurchasemanager.data.model.BuyerModel;

public final class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    private static final int LOCAL_DB_VERSION = 1;
    private static final String LOCAL_DB_NAME = "lpm_database.db";

    // Dao instances
    private RuntimeExceptionDao<BuyerModel, Integer> mBuyerModelDao;

    public DatabaseHelper(Context context) {
        super(context, LOCAL_DB_NAME, null, LOCAL_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            createDatabaseTables(connectionSource);
            Log.i(TAG, "Database tables created successfully");
        } catch (SQLException e) {
            final String message = "Cannot create database tables: " + e.getMessage();
            Log.e(TAG, message);

            throw new RuntimeException(message);
        }

        StaticDataGenerator staticDataGenerator = new StaticDataGenerator(this);
        staticDataGenerator.createBuyerEntries();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        // TODO
    }

    /**
     * Gets the {@link RuntimeExceptionDao} instance associated with the {@link BuyerModel} table
     *
     * @return the {@link RuntimeExceptionDao} instance associated with the {@link BuyerModel} table
     */
    public RuntimeExceptionDao<BuyerModel, Integer> getBuyerModelDao() {
        if (mBuyerModelDao == null) {
            mBuyerModelDao = getRuntimeExceptionDao(BuyerModel.class);
        }

        return mBuyerModelDao;
    }

    /**
     * Creates tables based on model classes
     *
     * @param connectionSource the {@link ConnectionSource} object associated with the local db
     * @throws SQLException if an underlying SQL related error occurs
     */
    private void createDatabaseTables(ConnectionSource connectionSource)
            throws java.sql.SQLException {

        TableUtils.createTable(connectionSource, BuyerModel.class);
    }
}
