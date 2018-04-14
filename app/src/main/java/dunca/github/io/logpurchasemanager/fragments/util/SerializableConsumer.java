package dunca.github.io.logpurchasemanager.fragments.util;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that extends the {@link Serializable} interface
 *
 * @param <T> the type of the input parameter
 */
public interface SerializableConsumer<T> extends Serializable {
    void consume(T t);
}
