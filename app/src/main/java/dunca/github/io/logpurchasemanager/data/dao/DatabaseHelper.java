package dunca.github.io.logpurchasemanager.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import io.github.dunca.logpurchasemanager.shared.model.Acquirer;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;
import io.github.dunca.logpurchasemanager.shared.model.AcquisitionItem;
import io.github.dunca.logpurchasemanager.shared.model.LogPrice;
import io.github.dunca.logpurchasemanager.shared.model.LogQualityClass;
import io.github.dunca.logpurchasemanager.shared.model.Supplier;
import io.github.dunca.logpurchasemanager.shared.model.TreeSpecies;
import io.github.dunca.logpurchasemanager.shared.model.WoodCertification;
import io.github.dunca.logpurchasemanager.shared.model.WoodRegion;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;
import io.github.dunca.logpurchasemanager.shared.model.custom.AcquisitionData;
import io.github.dunca.logpurchasemanager.shared.model.custom.StaticData;
import io.github.dunca.logpurchasemanager.shared.model.interfaces.Model;

public final class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    private static final int LOCAL_DB_VERSION = 1;
    private static final String LOCAL_DB_NAME = "data.db";

    private static DatabaseHelper sLatestInstance;

    // Dao instances
    private RuntimeExceptionDao<Acquirer, Integer> mAcquirerDao;
    private RuntimeExceptionDao<WoodRegion, Integer> mWoodRegionDao;
    private RuntimeExceptionDao<TreeSpecies, Integer> mWoodSpeciesDao;
    private RuntimeExceptionDao<WoodCertification, Integer> mWoodCertificationDao;
    private RuntimeExceptionDao<LogQualityClass, Integer> mLogQualityClassDao;
    private RuntimeExceptionDao<Supplier, Integer> mSupplierDao;
    private RuntimeExceptionDao<Acquisition, Integer> mAcquisitionDao;
    private RuntimeExceptionDao<AcquisitionItem, Integer> mAcquisitionItemDao;
    private RuntimeExceptionDao<LogPrice, Integer> mLogPriceDao;

    public DatabaseHelper(Context context) {
        super(context, LOCAL_DB_NAME, null, LOCAL_DB_VERSION);
        sLatestInstance = this;
    }

    public static DatabaseHelper getLatestInstance() {
        if (sLatestInstance == null) {
            throw new IllegalStateException(DatabaseHelper.class.getSimpleName() + " has not been initialized");
        }

        return sLatestInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            createDatabaseTables();
            Log.i(TAG, "Database tables created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create database tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {

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

    public RuntimeExceptionDao<TreeSpecies, Integer> getTreeSpeciesDao() {
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

    private void createDatabaseTables() throws SQLException {
        createTable(Acquirer.class);
        createTable(Acquisition.class);
        createTable(AcquisitionItem.class);
        createTable(LogPrice.class);
        createTable(LogQualityClass.class);
        createTable(Supplier.class);
        createTable(TreeSpecies.class);
        createTable(WoodCertification.class);
        createTable(WoodRegion.class);
    }

    private <T extends Model> void createTable(Class<T> modelClass) throws SQLException {
        TableUtils.createTable(getConnectionSource(), modelClass);
    }

    private <T extends Model> void clearTable(Class<T> modelClass) throws SQLException {
        TableUtils.dropTable(getConnectionSource(), modelClass, true);
        createTable(modelClass);
    }

    public void markAcquisitionDataAsSynced(AcquisitionData acquisitionData) {
        for (Acquisition acquisition : acquisitionData.getAcquisitionList()) {
            acquisition.setId(acquisition.getAppAllocatedId());
            acquisition.setSynced(true);

            getAcquisitionDao().update(acquisition);
        }

        for (AcquisitionItem acquisitionItem : acquisitionData.getAcquisitionItemList()) {
            acquisitionItem.setId(acquisitionItem.getAppAllocatedId());
            acquisitionItem.setSynced(true);

            getAcquisitionItemDao().update(acquisitionItem);
        }

        for (LogPrice logPrice : acquisitionData.getLogPriceList()) {
            logPrice.setId(logPrice.getAppAllocatedId());
            logPrice.setSynced(true);

            getLogPriceDao().update(logPrice);
        }
    }

    public AcquisitionData getUnsyncedAcquisitionData() throws SQLException {
        List<Acquisition> acquisitionList = getNotSyncedWhereClause(getAcquisitionDao()).query();

        List<AcquisitionItem> acquisitionItemList = getNotSyncedWhereClause(getAcquisitionItemDao()).query();

        List<LogPrice> logPriceList = getNotSyncedWhereClause(getLogPriceDao()).query();

        return new AcquisitionData(acquisitionList, acquisitionItemList, logPriceList);
    }

    private <T extends Model> Where<T, Integer> getNotSyncedWhereClause(RuntimeExceptionDao<T, Integer> dao) throws SQLException {
        return dao.queryBuilder().where().eq(CommonFieldNames.IS_SYNCED, false);
    }

    public void replaceStaticData(StaticData newStaticData) throws SQLException {
        clearStaticDataTables();

        getAcquirerDao().create(newStaticData.getAcquirers());
        getLogQualityClassDao().create(newStaticData.getLogQualityClasses());
        getSupplierDao().create(newStaticData.getSuppliers());
        getTreeSpeciesDao().create(newStaticData.getTreeSpecies());
        getWoodCertificationDao().create(newStaticData.getWoodCertifications());
        getWoodRegionDao().create(newStaticData.getWoodRegions());

        Log.i(TAG, "Replaced static data");
    }

    private void clearStaticDataTables() throws SQLException {
        clearTable(Acquirer.class);
        clearTable(LogQualityClass.class);
        clearTable(Supplier.class);
        clearTable(TreeSpecies.class);
        clearTable(WoodCertification.class);
        clearTable(WoodRegion.class);

        Log.i(TAG, "Cleared static data tables");
    }
}
