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
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionItem;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionStatus;
import dunca.github.io.logpurchasemanager.data.model.LogDiameterClass;
import dunca.github.io.logpurchasemanager.data.model.LogPrice;
import dunca.github.io.logpurchasemanager.data.model.LogQualityClass;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.TreeSpecies;
import dunca.github.io.logpurchasemanager.data.model.WoodCertification;
import dunca.github.io.logpurchasemanager.data.model.WoodRegion;
import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;

public final class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    private static final int LOCAL_DB_VERSION = 1;
    private static final String LOCAL_DB_NAME = "lpm_database.db";

    private ConnectionSource mConnectionSource;

    private static DatabaseHelper sLatestInstance;

    // Dao instances
    private RuntimeExceptionDao<Acquirer, Integer> mAcquirerDao;
    private RuntimeExceptionDao<WoodRegion, Integer> mWoodRegionDao;
    private RuntimeExceptionDao<TreeSpecies, Integer> mWoodSpeciesDao;
    private RuntimeExceptionDao<WoodCertification, Integer> mWoodCertificationDao;
    private RuntimeExceptionDao<LogQualityClass, Integer> mLogQualityClassDao;
    private RuntimeExceptionDao<AcquisitionStatus, Integer> mAcquisitionStatusDao;
    private RuntimeExceptionDao<Supplier, Integer> mSupplierDao;
    private RuntimeExceptionDao<Acquisition, Integer> mAcquisitionDao;
    private RuntimeExceptionDao<AcquisitionItem, Integer> mAcquisitionItemDao;
    private RuntimeExceptionDao<LogPrice, Integer> mLogPriceDao;

    public DatabaseHelper(Context context) {
        super(context, LOCAL_DB_NAME, null, LOCAL_DB_VERSION);

        sLatestInstance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        mConnectionSource = connectionSource;

        try {
            createDatabaseTables();
            Log.i(TAG, "Database tables created successfully");
        } catch (SQLException e) {
            final String message = "Cannot create database tables: " + e.getMessage();
            Log.e(TAG, message);

            throw new RuntimeException(message);
        }

        StaticDataGenerator staticDataGenerator = new StaticDataGenerator(this);

        staticDataGenerator.createAcquirers();

        staticDataGenerator.createWoodRegions();
        staticDataGenerator.createTreeSpecies();
        staticDataGenerator.createWoodCertifications();

        staticDataGenerator.createLogQualityClasses();

        staticDataGenerator.createAcquisitionStatuses();
        staticDataGenerator.createSuppliers();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        // TODO
    }

    public RuntimeExceptionDao<Acquirer, Integer> getAcquirerDao() {
        if (mAcquirerDao == null) {
            mAcquirerDao = getRuntimeExceptionDao(Acquirer.class);
        }

        return mAcquirerDao;
    }

    public RuntimeExceptionDao<WoodRegion, Integer> getWoodRegionDao() {
        if (mWoodRegionDao == null) {
            mWoodRegionDao = getRuntimeExceptionDao(WoodRegion.class);
        }

        return mWoodRegionDao;
    }

    public RuntimeExceptionDao<TreeSpecies, Integer> getWoodSpeciesDao() {
        if (mWoodSpeciesDao == null) {
            mWoodSpeciesDao = getRuntimeExceptionDao(TreeSpecies.class);
        }

        return mWoodSpeciesDao;
    }

    public RuntimeExceptionDao<WoodCertification, Integer> getWoodCertificationDao() {
        if (mWoodCertificationDao == null) {
            mWoodCertificationDao = getRuntimeExceptionDao(WoodCertification.class);
        }

        return mWoodCertificationDao;
    }

    public RuntimeExceptionDao<LogQualityClass, Integer> getLogQualityClassDao() {
        if (mLogQualityClassDao == null) {
            mLogQualityClassDao = getRuntimeExceptionDao(LogQualityClass.class);
        }

        return mLogQualityClassDao;
    }

    public RuntimeExceptionDao<AcquisitionStatus, Integer> getAcquisitionStatusDao() {
        if (mAcquisitionStatusDao == null) {
            mAcquisitionStatusDao = getRuntimeExceptionDao(AcquisitionStatus.class);
        }

        return mAcquisitionStatusDao;
    }

    public RuntimeExceptionDao<Supplier, Integer> getSupplierDao() {
        if (mSupplierDao == null) {
            mSupplierDao = getRuntimeExceptionDao(Supplier.class);
        }

        return mSupplierDao;
    }

    public RuntimeExceptionDao<Acquisition, Integer> getAcquisitionDao() {
        if (mAcquisitionDao == null) {
            mAcquisitionDao = getRuntimeExceptionDao(Acquisition.class);
        }

        return mAcquisitionDao;
    }

    public RuntimeExceptionDao<AcquisitionItem, Integer> getAcquisitionItemDao() {
        if (mAcquisitionItemDao == null) {
            mAcquisitionItemDao = getRuntimeExceptionDao(AcquisitionItem.class);
        }

        return mAcquisitionItemDao;
    }

    public RuntimeExceptionDao<LogPrice, Integer> getLogPriceDao() {
        if (mLogPriceDao == null) {
            mLogPriceDao = getRuntimeExceptionDao(LogPrice.class);
        }

        return mLogPriceDao;
    }

    public static DatabaseHelper getLatestInstance() {
        if (sLatestInstance == null) {
            throw new IllegalStateException(DatabaseHelper.class.getSimpleName() +
                    " has not been initialized");
        }

        return sLatestInstance;
    }

    /**
     * Creates tables based on model classes
     *
     * @throws SQLException if an underlying SQL related error occurs
     */
    private void createDatabaseTables() throws java.sql.SQLException {
        createDatabaseTable(Acquirer.class);
        createDatabaseTable(Acquisition.class);
        createDatabaseTable(AcquisitionItem.class);
        createDatabaseTable(AcquisitionStatus.class);
        createDatabaseTable(LogDiameterClass.class);
        createDatabaseTable(LogPrice.class);
        createDatabaseTable(LogQualityClass.class);
        createDatabaseTable(Supplier.class);
        createDatabaseTable(TreeSpecies.class);
        createDatabaseTable(WoodCertification.class);
        createDatabaseTable(WoodRegion.class);
    }

    private <T extends Model> void createDatabaseTable(Class<T> modelClass) throws SQLException {
        TableUtils.createTable(mConnectionSource, modelClass);
    }
}
