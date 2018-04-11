package dunca.github.io.logpurchasemanager.data;

import java.util.Arrays;

import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionStatus;
import dunca.github.io.logpurchasemanager.data.model.LogQualityClass;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.TreeSpecies;
import dunca.github.io.logpurchasemanager.data.model.WoodCertification;
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

    public void createTreeSpecies() {
        TreeSpecies treeSpecies1 = new TreeSpecies("bc", "Beech");
        TreeSpecies treeSpecies2 = new TreeSpecies("sp", "Spruce");

        mDatabaseHelper.getWoodSpeciesDao().create(Arrays.asList(treeSpecies1, treeSpecies2));
    }

    public void createWoodCertifications() {
        WoodCertification wc1 = new WoodCertification("None");
        WoodCertification wc2 = new WoodCertification("PEFC");

        mDatabaseHelper.getWoodCertificationDao().create(Arrays.asList(wc1, wc2));
    }

    public void createLogQualityClasses() {
        LogQualityClass lc1 = new LogQualityClass("HQ", "High Quality");
        LogQualityClass lc2 = new LogQualityClass("LQ", "Low Quality");

        mDatabaseHelper.getLogQualityClassDao().create(Arrays.asList(lc1, lc2));
    }

    public void createAcquisitionStatuses() {
        AcquisitionStatus as1 = new AcquisitionStatus("Accepted");

        mDatabaseHelper.getAcquisitionStatusDao().create(as1);
    }

    public void createSuppliers() {
        Supplier supplier1 = new Supplier("SuperWood", "Wood's street", "Germany", "Berlin",
                "1234");
        Supplier supplier2 = new Supplier("UltraWood", "Spruce's street", "Austria", "Vienna",
                "4567");

        mDatabaseHelper.getSupplierDao().create(Arrays.asList(supplier1, supplier2));
    }
}
