package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

/**
 * Corresponds to a buyer / user of the mobile application
 */
@Data
@DatabaseTable()
public final class BuyerModel {
    @DatabaseField(generatedId = true)
    private int buyerId;

    @DatabaseField
    private String username;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String lastName;

    @DatabaseField
    private String password;

    public BuyerModel(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    BuyerModel() {

    }
}
