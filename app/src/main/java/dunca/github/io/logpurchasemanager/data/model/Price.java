package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

@Data
@DatabaseTable
public class Price {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquisition acquisition;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false, foreign = true)
    private Species species;

    @DatabaseField(canBeNull = false, foreign = true)
    private QualityClass qualityClass;

    @DatabaseField(canBeNull = false, foreign = true)
    private DiameterClass diameterClass;

    @DatabaseField(canBeNull = false)
    private double price;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int quantity;

    @DatabaseField(canBeNull = false)
    private boolean synced;
}
