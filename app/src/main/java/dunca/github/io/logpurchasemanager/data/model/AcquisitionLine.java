package dunca.github.io.logpurchasemanager.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

@Data
@DatabaseTable
public final class AcquisitionLine {
    @DatabaseField(generatedId = true)
    private int id;

    private Acquisition acquisition;


}
