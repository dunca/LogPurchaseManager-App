package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

/**
 * Corresponds to a acquirer / user of the mobile application
 */
@Data
@DatabaseTable
public final class Acquirer {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String username;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String lastName;

    @DatabaseField(canBeNull = false)
    private String password;

    public Acquirer(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    Acquirer() {

    }
}
