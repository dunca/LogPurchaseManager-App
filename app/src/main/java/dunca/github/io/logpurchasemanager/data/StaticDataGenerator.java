package dunca.github.io.logpurchasemanager.data;

import java.util.Arrays;

import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;

public final class StaticDataGenerator {
    private DatabaseHelper mDatabaseHelper;

    public StaticDataGenerator(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    public void createBuyerEntries() {
        Acquirer acquirer1 = new Acquirer("BE", "Eduard", "Banu", "1");
        Acquirer acquirer2 = new Acquirer("MT", "Teodor", "Moldovan", "1");

        mDatabaseHelper.getBuyerModelDao().create(Arrays.asList(acquirer1, acquirer2));
    }
}
