package com.ticxo.modelengine.api.generator;

public interface ModelGenerator {
   default void importModels() {
      this.importModels(false);
   }

   boolean isInitialized();

   void importModels(boolean isStartup);

   void generateAssets(boolean isStartup);

   void zipResourcePack(boolean isStartup);

   void updateConfig();

   void queueTask(ModelGenerator.Phase post, Runnable task);

   enum Phase {
      PRE_IMPORT,
      POST_IMPORT,
      PRE_ASSETS,
      POST_ASSETS,
      PRE_ZIPPING,
      POST_ZIPPING,
      FINISHED;
   }
}
