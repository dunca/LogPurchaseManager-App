package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public final class WoodCertification implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int listPriority;

    public WoodCertification(String name, int listPriority) {
        this.name = name;
        this.listPriority = listPriority;
    }

    public WoodCertification(String name) {
        this(name, 0);
    }

    WoodCertification() {

    }

    @Override
    public String toString() {
        return name;
    }
}
