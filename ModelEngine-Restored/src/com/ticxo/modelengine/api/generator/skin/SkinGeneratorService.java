package com.ticxo.modelengine.api.generator.skin;

import java.util.concurrent.CompletableFuture;

public interface SkinGeneratorService {
   boolean isEnabled();

   void onReload();

   CompletableFuture<String> generate(byte[] rawImage);
}
