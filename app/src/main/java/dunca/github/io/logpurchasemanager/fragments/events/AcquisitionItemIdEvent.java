package dunca.github.io.logpurchasemanager.fragments.events;

import lombok.Getter;

@Getter
public final class AcquisitionItemIdEvent {
    private int acquisitionItemId;

    public AcquisitionItemIdEvent(int acquisitionItemId) {
        this.acquisitionItemId = acquisitionItemId;
    }
}
