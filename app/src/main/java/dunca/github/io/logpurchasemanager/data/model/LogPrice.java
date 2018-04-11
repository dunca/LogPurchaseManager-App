package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public class LogPrice implements Model {
    // TODO: switch to id if this is synced with the service
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquisition acquisition;

    @DatabaseField(canBeNull = false, foreign = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false, foreign = true)
    private TreeSpecies treeSpecies;

    @DatabaseField(canBeNull = false, foreign = true)
    private LogQualityClass logQualityClass;

    @DatabaseField(canBeNull = false, foreign = true)
    private LogDiameterClass logDiameterClass;

    @DatabaseField(canBeNull = false)
    private double price;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int quantity;

    @DatabaseField(canBeNull = false)
    private boolean isSynced;

    public LogPrice(Acquisition acquisition, Acquirer acquirer, TreeSpecies treeSpecies,
                    LogQualityClass logQualityClass, LogDiameterClass logDiameterClass,
                    double price, int quantity, boolean isSynced) {
        this.acquisition = acquisition;
        this.acquirer = acquirer;
        this.treeSpecies = treeSpecies;
        this.logQualityClass = logQualityClass;
        this.logDiameterClass = logDiameterClass;
        this.price = price;
        this.quantity = quantity;
        this.isSynced = isSynced;
    }
}
