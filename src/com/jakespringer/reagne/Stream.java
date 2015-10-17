package com.jakespringer.reagne;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.jakespringer.reagne.util.ImmutableTuple2;

public class Stream<T> {
    public static final Object DEFAULT_STREAM_OBJECT = new Object();

    // list of listeners for the stream to output to
    private final Queue<Consumer<T>> listeners;

    // current data of this stream
    private T data;

    // list of things to remove me from on ..#remove()
    private final List<ImmutableTuple2<Stream<?>, Consumer<?>>> removeMeFrom;
    
    private Stream<T> parent;

    /**
     * Creates a stream and initializes it with a value.
     * 
     * @param item
     *            the initial value of the stream
     * @see com.jakespringer.reagne.Stream
     */
    public Stream(final T item) {
        checkNull(item);
        data = item;
        parent = null;

        removeMeFrom = new ArrayList<>();
        listeners = new ConcurrentLinkedQueue<Consumer<T>>();
    }

    /**
     * Sends an item to the stream and alerts all listeners that an item has
     * been sent.
     * 
     * @param item
     *            the item to send
     */
    public void send(T item) {
        checkNull(item);
        data = item;
        listeners.stream().forEach(alertable -> {if (alertable != null) alertable.accept(item);});
        
        Stream<T> current = parent;
        while (current != null) {
            current.data = item;
            current = current.parent;
        }
    }

    /**
     * Returns the last value sent to this stream. If no value has been sent
     * will return the initial value of the stream.
     * 
     * @return the last value of the stream
     */
    public T get() {
        return data;
    }

    /**
     * Cleans up this stream and stops it from receiving from super streams.
     * <b>Should only be called to clean up a stream, can have impacts on other
     * streams.</b>
     */
    public void remove() {
        removeMeFrom.stream().forEach(pair -> pair.left.listeners.remove(pair.right));
        removeMeFrom.clear();
    }

    /**
     * Creates an returns a new stream that will activate the supplied Consumer
     * whenever an item is sent to the stream. The stream will pass on the item
     * sent to the consumer.
     * 
     * @param consumer
     *            the consumer that will be notified
     * @return the new stream
     */
    public Stream<T> forEach(final Consumer<T> consumer) {
        final Stream<T> newStream = cloneAndRegister();
        newStream.listeners.add(consumer);
        return newStream;
    }

    /**
     * Creates an returns a new stream that will update IF AND ONLY IF the
     * signal supplied is 'true'.
     * 
     * @param signal
     *            the signal with which to filter the stream
     * @return the new stream
     */
    public Stream<T> filter(final Stream<Boolean> signal) {
        final Stream<T> stream = cloneAndDontRegister();

        final Consumer<T> consumer = x -> {
            if (signal.get()) {
                stream.send(x);
            }
        };

        forEach(consumer);
        stream.removeMeFrom.add(new ImmutableTuple2<>(this, consumer));

        return stream;
    }

    /**
     * Creates and returns a new stream that filters everything sent while the
     * predicate is false.
     * 
     * @param predicate
     *            the predicate to check
     * @return the new stream
     */
    public Stream<T> filter(final Predicate<T> predicate) {
        final Stream<T> stream = cloneAndDontRegister();

        final Consumer<T> consumer = x -> {
            if (predicate.test(x)) {
                stream.send(x);
            }
        };

        forEach(consumer);
        stream.removeMeFrom.add(new ImmutableTuple2<>(this, consumer));

        return stream;
    }

    /**
     * Creates and returns a new stream that combines two streams and fires
     * whenever both streams fire.
     * 
     * @param stream
     *            the stream to combine with this
     * @return the new stream
     */
    public Stream<T> combine(final Stream<T> stream) {
        final Stream<T> newStream = cloneAndRegister();
        final Consumer<T> consumer = x -> newStream.send(x);
        stream.forEach(consumer);
        newStream.removeMeFrom.add(new ImmutableTuple2<>(stream, consumer));
        return newStream;
    }

    /**
     * Sends the result of the supplier when the stream given receives a value.
     * The supplier is fed the value of the stream given.
     * 
     * @param stream
     *            the stream to monitor
     * @param supplier
     *            the value to send to this stream
     * @return the new stream
     */
    public <S> Stream<T> sendOn(final Stream<S> stream, final BiFunction<S, T, T> supplier) {
        final Stream<T> newStream = cloneAndRegister();
        final Consumer<S> consumer = x -> newStream.send(supplier.apply(stream.get(), newStream.get()));
        stream.forEach(consumer);
        newStream.removeMeFrom.add(new ImmutableTuple2<>(stream, consumer));
        return newStream;
    }
    
    /**
     * Sends the payload when the stream given receives a value.
     * The supplier is fed the value of the stream given.
     * 
     * @param stream
     *            the stream to monitor
     * @param payload
     *            the value to send to this stream
     * @return the new stream
     */
    public <S> Stream<T> sendOn(final Stream<S> stream, final T payload) {
        final Stream<T> newStream = cloneAndRegister();
        final Consumer<S> consumer = x -> newStream.send(payload);
        stream.forEach(consumer);
        newStream.removeMeFrom.add(new ImmutableTuple2<>(stream, consumer));
        return newStream;
    }

    /**
     * Returns a stream that will not be removed when a substream is removed.
     * 
     * @return the root stream
     */
    public Stream<T> asRoot() {
        Stream<T> newStream = cloneAndRegister();
        newStream.removeMeFrom.clear();
        return newStream;
    }

    ///
    /// PRIVATE HELPER METHODS BELOW
    ///

    private Stream<T> cloneAndRegister() {
        final Stream<T> cloned = new Stream<>(data);
        final Consumer<T> consumer = x -> cloned.send(x);
        listeners.add(consumer);
        cloned.removeMeFrom.addAll(removeMeFrom);
        cloned.removeMeFrom.add(new ImmutableTuple2<>(this, consumer));
        cloned.parent = this;
        return cloned;
    }

    private Stream<T> cloneAndDontRegister() {
        // no parent, parent is null
        final Stream<T> cloned = new Stream<>(data);
        return cloned;
    }

    private void checkNull(T item) {
        if (item == null) {
            throw new NullPointerException("You cannot send a null item.");
        }
    }
}
