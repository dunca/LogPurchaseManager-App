package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.Data;

@Data
@DatabaseTable
public final class Acquisition {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String serialNumber;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false, foreign = true)
    private Supplier supplier;

    @DatabaseField(canBeNull = false)
    private Date receptionDate;

    @DatabaseField(canBeNull = false, foreign = true)
    private AcquisitionStatus acquisitionStatus;

    @DatabaseField(canBeNull = false)
    private String regionZone;

    @DatabaseField(canBeNull = false, foreign = true)
    private WoodRegion woodRegion;

    @DatabaseField(canBeNull = false, foreign = true)
    private WoodCertification woodCertification;

    @DatabaseField(canBeNull = false)
    private String observations;

    @DatabaseField(canBeNull = false)
    private double totalValue;

    @DatabaseField(canBeNull = false)
    private double grossTotal;

    @DatabaseField(canBeNull = false)
    private double netTotal;

    @DatabaseField(canBeNull = false)
    private double discountPercentage;

    @DatabaseField(canBeNull = false)
    private double discountValue;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private boolean net;

    @DatabaseField(canBeNull = false)
    private boolean isSynced;
}
