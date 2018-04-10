package dunca.github.io.logpurchasemanager.data;

import java.util.Arrays;

import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.BuyerModel;

public final class StaticDataGenerator {
    private DatabaseHelper mDatabaseHelper;

    public StaticDataGenerator(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    public void createBuyerEntries() {
        BuyerModel buyer1 = new BuyerModel("BE", "Eduard", "Banu", "1");
        BuyerModel buyer2 = new BuyerModel("MT", "Teodor", "Moldovan", "1");

        mDatabaseHelper.getBuyerModelDao().create(Arrays.asList(buyer1, buyer2));
    }
}
