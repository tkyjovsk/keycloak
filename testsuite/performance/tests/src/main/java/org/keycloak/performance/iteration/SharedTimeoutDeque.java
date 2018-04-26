package org.keycloak.performance.iteration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.logging.Logger;

/**
 * {@code SharedTimeoutDeque} allows to share a common timeout between multiple 
 * threads using {@code LinkedBlockingDeque.poll(timeout, timeUnit)}, 
 * so that their individual timeouts don't add up.
 * 
 * <p>Also allows to add a list of items at the head of the queue.</p>
 *
 * @author tkyjovsk
 * @param <T> item type
 */
public class SharedTimeoutDeque<T> extends LinkedBlockingDeque<T> {

    private final Logger logger = Logger.getLogger(SharedTimeoutDeque.class);

    private final long sharedPollTimeoutMillis;
    private final AtomicLong sharedPollTimeoutTime;

    /**
     *
     * @param sharedPollTimeout
     * @param timeUnit
     */
    public SharedTimeoutDeque(long sharedPollTimeout, TimeUnit timeUnit) {
        this.sharedPollTimeoutMillis = MILLISECONDS.convert(
                sharedPollTimeout, timeUnit);
        this.sharedPollTimeoutTime = new AtomicLong(Long.MIN_VALUE); // MIN_VALUE means "unset"
    }

    public SharedTimeoutDeque() {
        this(5, SECONDS);
    }

    /**
     * Runs {@code addFirst(item)} for each item in the {@code list}.
     * Note that this will add the items at the head of the queue in a <b>reversed order</b>.
     * 
     * @param list A list of items to add at the head of the quque.
     */
    public void addFirstForEach(List<? extends T> list) {
        list.stream().forEachOrdered((e) -> addFirst(e));
    }

    /**
     * Runs {@code addFirst(item)} for each item in the {@code list} in its reversed order.
     * Note that this will add the items at the head of the queue their <b>original order</b>.
     * 
     * @param list A list of items to add at the head of the quque.
     */
    public void addFirstForEachReversed(List<? extends T> list) {
        addFirstForEach(new ReverseList<>(list));
    }

    /**
     * Polls the queue with the timeout remaining to the sharedTimeoutTime.
     *
     * @return Result of {@code poll(remainingPollTimeout, timeUnit)} where the
     * {@code remainingPollTimeout} is the time remaining to
     * {@code sharedPollTimeoutTime}.
     *
     * @throws InterruptedException
     */
    public synchronized T pollWithSharedTimeout() throws InterruptedException {
        long pollStartTime = new Date().getTime();
        if (sharedPollTimeoutTime.get() == Long.MIN_VALUE) {
            sharedPollTimeoutTime.set(pollStartTime + sharedPollTimeoutMillis);
        }
        long remainingPollTimeout = Long.max(0, sharedPollTimeoutTime.get() - pollStartTime);
        logger.trace(String.format("remainingPollTimeout: %s", remainingPollTimeout));
        T item = poll(remainingPollTimeout, MILLISECONDS);
        if (item != null) {
            sharedPollTimeoutTime.set(Long.MIN_VALUE); // successful poll unsets sharedTimeoutTime
        }
        return item;
    }

}
