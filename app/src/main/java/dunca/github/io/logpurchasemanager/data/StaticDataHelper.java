package dunca.github.io.logpurchasemanager.data;

import java.sql.SQLException;

import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import io.github.dunca.logpurchasemanager.shared.model.Acquirer;
import io.github.dunca.logpurchasemanager.shared.model.LogQualityClass;
import io.github.dunca.logpurchasemanager.shared.model.Supplier;
import io.github.dunca.logpurchasemanager.shared.model.TreeSpecies;
import io.github.dunca.logpurchasemanager.shared.model.WoodCertification;
import io.github.dunca.logpurchasemanager.shared.model.WoodRegion;
import io.github.dunca.logpurchasemanager.shared.model.custom.StaticData;

public final class StaticDataHelper {
    private DatabaseHelper mDbHelper;

    public StaticDataHelper(DatabaseHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    public synchronized void replaceWith(StaticData staticData) throws SQLException {
        clearStaticTables();

        mDbHelper.getAcquirerDao().create(staticData.getAcquirers());
        mDbHelper.getLogQualityClassDao().create(staticData.getLogQualityClasses());
        mDbHelper.getSupplierDao().create(staticData.getSuppliers());
        mDbHelper.getTreeSpeciesDao().create(staticData.getTreeSpecies());
        mDbHelper.getWoodCertificationDao().create(staticData.getWoodCertifications());
        mDbHelper.getWoodRegionDao().create(staticData.getWoodRegions());
    }

    private void clearStaticTables() throws SQLException {
        mDbHelper.clearTable(Acquirer.class);
        mDbHelper.clearTable(LogQualityClass.class);
        mDbHelper.clearTable(Supplier.class);
        mDbHelper.clearTable(TreeSpecies.class);
        mDbHelper.clearTable(WoodCertification.class);
        mDbHelper.clearTable(WoodRegion.class);
    }
}
