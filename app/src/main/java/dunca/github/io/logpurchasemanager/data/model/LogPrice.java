package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public class LogPrice implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Acquisition acquisition;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Acquirer acquirer;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private TreeSpecies treeSpecies;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private LogQualityClass logQualityClass;

    @DatabaseField(canBeNull = false)
    private double price;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int quantity;

    @DatabaseField(canBeNull = false)
    private boolean isSynced;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int serverAllocatedId;

    public LogPrice(Acquisition acquisition, Acquirer acquirer, TreeSpecies treeSpecies,
                    LogQualityClass logQualityClass, double price, int quantity, boolean isSynced) {
        this.acquisition = acquisition;
        this.acquirer = acquirer;
        this.treeSpecies = treeSpecies;
        this.logQualityClass = logQualityClass;
        this.price = price;
        this.quantity = quantity;
        this.isSynced = isSynced;
    }

    LogPrice() {

    }
}
