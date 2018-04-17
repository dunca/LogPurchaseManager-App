package dunca.github.io.logpurchasemanager.fragments.events;

import lombok.Getter;

@Getter
public class AcquisitionIdEvent {
    private int acquisitionId;

    public AcquisitionIdEvent(int acquisitionId) {
        this.acquisitionId = acquisitionId;
    }
}
