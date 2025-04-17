package mylie.engine.util;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class LatchUtils {
    private LatchUtils() {
        throw new IllegalInstantiationException(LatchUtils.class);
    }

    public static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for latch", e);
        }
    }
}
