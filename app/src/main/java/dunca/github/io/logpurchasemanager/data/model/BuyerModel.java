package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

/**
 * Corresponds to a buyer / user of the mobile application
 */
@Data
@DatabaseTable(tableName = "Achizitor")
public final class BuyerModel {
    @DatabaseField(generatedId = true, columnName = "achizitor_id")
    private int buyerId;

    @DatabaseField(columnName = "login")
    private String userName;

    @DatabaseField(columnName = "prenume")
    private String firstName;

    @DatabaseField(columnName = "nume")
    private String lastName;

    @DatabaseField(columnName = "parola")
    private String password;

    public BuyerModel(String userName, String firstName, String lastName, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    BuyerModel() {

    }
}
