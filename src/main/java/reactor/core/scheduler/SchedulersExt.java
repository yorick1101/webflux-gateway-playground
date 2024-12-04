package reactor.core.scheduler;

import org.slf4j.MDC;
import reactor.util.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public final class SchedulersExt {

    static final String SPORTY = "sporty"; // IO stuff

    static AtomicReference<Schedulers.CachedScheduler> CACHED_SPORTY_ELASTIC = new AtomicReference<>();
    static final Supplier<Scheduler> SPORTY_SUPPLIER = () -> newElastic(SPORTY, BoundedElasticScheduler.DEFAULT_TTL_SECONDS, true);

    public static Scheduler sporty() {
        return Schedulers.cache(CACHED_SPORTY_ELASTIC, SPORTY, SPORTY_SUPPLIER);
    }

    static Scheduler newElastic(String name, int ttlSeconds, boolean daemon) {
        return newElastic(ttlSeconds, new ReactorThreadFactory(name, BoundedElasticScheduler.COUNTER, daemon, false, Schedulers::defaultUncaughtException));
    }

    static Scheduler newElastic(int ttlSeconds, ThreadFactory threadFactory) {
        return Schedulers.newBoundedElastic(Integer.MAX_VALUE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, threadFactory, ttlSeconds);
    }

    static class ReactorThreadFactory implements ThreadFactory, Supplier<String>, Thread.UncaughtExceptionHandler {

        final private String name;
        final private AtomicLong counterReference;
        final private boolean daemon;
        final private boolean rejectBlocking;

        @Nullable
        final private BiConsumer<Thread, Throwable> uncaughtExceptionHandler;

        ReactorThreadFactory(String name,
                             AtomicLong counterReference,
                             boolean daemon,
                             boolean rejectBlocking,
                             @Nullable BiConsumer<Thread, Throwable> uncaughtExceptionHandler) {
            this.name = name;
            this.counterReference = counterReference;
            this.daemon = daemon;
            this.rejectBlocking = rejectBlocking;
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        @Override
        public final Thread newThread(Runnable runnable) {
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            //Map<String, Object> localContext = LocalContext.copyContext();
            String newThreadName = name + "-" + counterReference.incrementAndGet();
            Runnable r = () -> {
                //LocalContext.putAll(localContext);
                MDC.setContextMap(copyOfContextMap);
                runnable.run();
            };
            Thread t = rejectBlocking
                    ? new reactor.core.scheduler.ReactorThreadFactory.NonBlockingThread(r, newThreadName)
                    : Thread.ofVirtual().name(newThreadName).unstarted(r);
            if (daemon) {
                t.setDaemon(true);
            }
            if (uncaughtExceptionHandler != null) {
                t.setUncaughtExceptionHandler(this);
            }
            return t;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if (uncaughtExceptionHandler == null) {
                return;
            }
            uncaughtExceptionHandler.accept(t, e);
        }

        /**
         * Get the prefix used for new {@link Thread Threads} created by this {@link ThreadFactory}.
         * The factory can also be seen as a {@link Supplier Supplier&lt;String&gt;}.
         *
         * @return the thread name prefix
         */
        @Override
        public final String get() {
            return name;
        }

    }

}
