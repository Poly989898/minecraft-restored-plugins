package io.lumine.mythic.api.volatilecode;

import java.util.concurrent.CompletableFuture;

public interface AsyncPathNavigation<P> {
   CompletableFuture<P> getPathFuture(P p);
}
