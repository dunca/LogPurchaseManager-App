package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public final class TreeSpecies implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String symbol;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int listPriority;

    public TreeSpecies(String symbol, String name, int listPriority) {
        this.symbol = symbol;
        this.name = name;
        this.listPriority = listPriority;
    }

    public TreeSpecies(String symbol, String name) {
        this(symbol, name, 0);
    }

    TreeSpecies() {

    }

    public String toString() {
        return String.format("%s (%s)", symbol, name);
    }
}
