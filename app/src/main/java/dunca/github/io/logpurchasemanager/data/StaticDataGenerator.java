package dunca.github.io.logpurchasemanager.data;

import java.util.Arrays;

import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.WoodRegion;

public final class StaticDataGenerator {
    private DatabaseHelper mDatabaseHelper;

    public StaticDataGenerator(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    public void createAcquirers() {
        Acquirer acquirer1 = new Acquirer("BE", "Eduard", "Banu", "1");
        Acquirer acquirer2 = new Acquirer("MT", "Teodor", "Moldovan", "1");

        mDatabaseHelper.getAcquirerDao().create(Arrays.asList(acquirer1, acquirer2));
    }

    public void createWoodRegions() {
        WoodRegion woodRegion1 = new WoodRegion("East of Romania", "RO-E");
        WoodRegion woodRegion2 = new WoodRegion("Germany", "DE");

        mDatabaseHelper.getWoodRegionDao().create(Arrays.asList(woodRegion1, woodRegion2));
    }
}
