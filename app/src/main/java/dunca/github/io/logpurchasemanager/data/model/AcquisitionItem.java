package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

@Data
@DatabaseTable
public final class AcquisitionItem {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquisition acquisition;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false)
    private String logBarCode;

    @DatabaseField(canBeNull = false)
    private double netDiameter;

    @DatabaseField(canBeNull = false)
    private double grossDiameter;

    @DatabaseField(canBeNull = false)
    private double netLength;

    @DatabaseField(canBeNull = false)
    private double grossLength;

    @DatabaseField(canBeNull = false)
    private double netVolume;

    @DatabaseField(canBeNull = false)
    private double grossVolume;

    @DatabaseField(canBeNull = false, foreign = true)
    private LogQualityClass logQualityClass;

    @DatabaseField(canBeNull = false, foreign = true)
    private LogDiameterClass logDiameterClass;

    @DatabaseField(canBeNull = false, foreign = true)
    private WoodSpecies woodSpecies;

    @DatabaseField(canBeNull = false)
    private String observations;

    @DatabaseField(canBeNull = false)
    private double price;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private boolean isSpecialPrice;

    @DatabaseField(canBeNull = false)
    private boolean isSynced;
}
