package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public final class LogDiameterClass implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String symbol;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private double minimum;

    @DatabaseField
    private double maximum;

    public LogDiameterClass(String symbol, String name, double minimum, double maximum) {
        this.symbol = symbol;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    LogDiameterClass() {

    }
}
