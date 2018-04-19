package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public final class Acquisition implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String serialNumber;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Supplier supplier;

    @DatabaseField(canBeNull = false)
    private Date receptionDate;

    @DatabaseField(canBeNull = false)
    private String regionZone;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private WoodRegion woodRegion;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private WoodCertification woodCertification;

    @DatabaseField(canBeNull = false)
    private String observations;

    @DatabaseField(canBeNull = false)
    private double totalValue;

    @DatabaseField(canBeNull = false)
    private double totalGrossVolume;

    @DatabaseField(canBeNull = false)
    private double totalNetVolume;

    @DatabaseField(canBeNull = false)
    private double discountPercentage;

    @DatabaseField(canBeNull = false)
    private double discountValue;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private boolean net;

    @DatabaseField(canBeNull = false)
    private boolean isSynced;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int serverAllocatedId;

    public Acquisition(String serialNumber, Acquirer acquirer, Supplier supplier,
                       Date receptionDate, String regionZone, WoodRegion woodRegion,
                       WoodCertification woodCertification, String observations, double totalValue,
                       double totalGrossVolume, double totalNetVolume, double discountPercentage,
                       double discountValue, boolean net, boolean isSynced) {
        this.serialNumber = serialNumber;
        this.acquirer = acquirer;
        this.supplier = supplier;
        this.receptionDate = receptionDate;
        this.regionZone = regionZone;
        this.woodRegion = woodRegion;
        this.woodCertification = woodCertification;
        this.observations = observations;
        this.totalValue = totalValue;
        this.totalGrossVolume = totalGrossVolume;
        this.totalNetVolume = totalNetVolume;
        this.discountPercentage = discountPercentage;
        this.discountValue = discountValue;
        this.net = net;
        this.isSynced = isSynced;
    }

    public Acquisition() {

    }
}
