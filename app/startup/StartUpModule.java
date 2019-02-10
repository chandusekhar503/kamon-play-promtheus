package startup;

import com.google.inject.AbstractModule;


/**
 * Created by chandu on 23/11/18.
 */
public class StartUpModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SystemMetricsInitializer.class).asEagerSingleton();
    }
}
