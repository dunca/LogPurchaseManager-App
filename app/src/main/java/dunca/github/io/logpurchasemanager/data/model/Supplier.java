package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

@Data
@DatabaseTable
public final class Supplier {
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
}
