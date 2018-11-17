package services;

import com.google.inject.Singleton;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by chandu on 16/11/18.
 */
@Singleton
public class CountService {

    public CompletionStage<Integer> getCount(int count){
        return CompletableFuture.supplyAsync(() ->{
            return count;
        });
    }
}
