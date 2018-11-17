package controllers;

import javax.inject.*;

import play.mvc.*;

import services.CountService;
import services.Counter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller demonstrates how to use dependency injection to
 * bind a component into a controller class. The class contains an
 * action that shows an incrementing count to users. The {@link Counter}
 * object is injected by the Guice dependency injection system.
 */
@Singleton
public class CountController extends Controller {

    private final Counter counter;

    private final CountService countService;

    @Inject
    public CountController(Counter counter, CountService countService) {
       this.counter = counter;
        this.countService = countService;
    }

    /**
     * An action that responds with the {@link Counter}'s current
     * count. The result is plain text. This action is mapped to
     * <code>GET</code> requests with a path of <code>/count</code>
     * requests by an entry in the <code>routes</code> config file.
     */
    public CompletionStage<Result> count() {

        return countService.getCount(counter.nextCount()).thenApply(response ->{
            return ok(Integer.toString(response));
        });

        //return ok(Integer.toString(counter.nextCount()));
    }

}
