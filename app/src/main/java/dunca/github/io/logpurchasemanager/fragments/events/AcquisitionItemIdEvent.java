package dunca.github.io.logpurchasemanager.fragments.events;

public final class AcquisitionItemIdEvent {
    private int acquisitionItemId;

    public AcquisitionItemIdEvent(int acquisitionItemId) {
        this.acquisitionItemId = acquisitionItemId;
    }

    public int getAcquisitionItemId() {
        return acquisitionItemId;
    }
}
