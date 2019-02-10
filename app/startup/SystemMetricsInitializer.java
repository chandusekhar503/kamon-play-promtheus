package startup;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.*;
import akka.actor.ActorSystem;
import kamon.executors.util.ContextAwareExecutorService;
import kamon.system.SystemMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.inject.ApplicationLifecycle;
import scala.concurrent.ExecutionContext;

@Singleton
public class SystemMetricsInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemMetricsInitializer.class);

    private final ApplicationLifecycle appLifecycle;
    public ExecutorService dbExecutorService;
    public ExecutorService futureExecutorService;
    private ActorSystem actorSystem;

    @Inject
    public SystemMetricsInitializer(ApplicationLifecycle appLifecycle, ActorSystem actorSystem) {
        this.appLifecycle = appLifecycle;
        LOG.info("Inside SystemMetricsInitializer......");
        System.out.println("Inside SystemMetricsInitializer......");
        SystemMetrics.startCollecting();



        LOG.info("Inside Database thread pool......");
        System.out.println("Inside Database thread pool.....");
        ExecutionContext dbExecutionContext = actorSystem.dispatchers().lookup("akka.actor.jpa-execution-context");
        dbExecutorService = new ContextAwareExecutorService(ExecutionContextExecutorServiceBridge.apply(dbExecutionContext));

      appLifecycle.addStopHook(() -> {
            SystemMetrics.stopCollecting();
            return CompletableFuture.completedFuture(null);
        });
    }


    static class CustomThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "custom-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
