package dunca.github.io.logpurchasemanager.util;

import java.io.Serializable;

/**
 * A {@link Runnable} that extends the {@link Serializable} interface
 */
public interface SerializableRunnable extends Serializable {
    void run();
}
