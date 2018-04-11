package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import lombok.Data;

@Data
@DatabaseTable
public final class Supplier implements Model {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false, defaultValue = "")
    private String street;

    @DatabaseField(canBeNull = false, defaultValue = "")
    private String country;

    @DatabaseField(canBeNull = false, defaultValue = "")
    private String address;

    @DatabaseField(canBeNull = false, defaultValue = "")
    private String code;

    public Supplier(String name, String street, String country, String address, String code) {
        this.name = name;
        this.street = street;
        this.country = country;
        this.address = address;
        this.code = code;
    }

    Supplier() {

    }

    @Override
    public String toString() {
        return name;
    }
}
