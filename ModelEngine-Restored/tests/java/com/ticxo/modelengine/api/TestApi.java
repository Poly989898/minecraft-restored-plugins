package com.ticxo.modelengine.api;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.utils.config.ConfigManager;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Minimal stand-in for the running plugin so classes with static initialisers that touch
 * {@code ModelEngineAPI.getAPI()} (for example TMath) can be loaded without a server.
 * Objects are created with Unsafe, so no Bukkit plugin machinery is started.
 */
public final class TestApi {
   private TestApi() {
   }

   private static boolean installed;

   public static synchronized void install() {
      if (installed) {
         return;
      }

      try {
         Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
         f.setAccessible(true);
         sun.misc.Unsafe unsafe = (sun.misc.Unsafe)f.get(null);

         ConfigManager config = (ConfigManager)unsafe.allocateInstance(NoopConfigManager.class);
         StubApi api = (StubApi)unsafe.allocateInstance(StubApi.class);
         api.configManager = config;
         ModelEngineAPI.API = api;
         installed = true;
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   /** Config manager that keeps property defaults: reference updaters are accepted but never run. */
   static class NoopConfigManager extends ConfigManager {
      NoopConfigManager() {
         super(null);
      }

      @Override
      public void registerReferenceUpdate(Runnable runnable) {
      }
   }

   static class StubApi extends ModelEngineAPI {
      @Override
      public ModeledEntity createModeledEntityImpl(BaseEntity<?> baseEntity, Consumer<ModeledEntity> consumer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ActiveModel createActiveModelImpl(
         ModelBlueprint modelBlueprint, Function<ActiveModel, ModelRenderer> function, Function<ActiveModel, AnimationHandler> function2
      ) {
         throw new UnsupportedOperationException();
      }

      @Override
      public VFX createVFXImpl(BaseEntity<?> baseEntity, Function<VFX, VFXRenderer> function, Consumer<VFX> consumer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public UUID getDisguiseRelayOrDefault(UUID uuid) {
         return uuid;
      }

      @Override
      public AnimationHandler getPriorityHandler(ActiveModel activeModel) {
         throw new UnsupportedOperationException();
      }

      @Override
      public AnimationHandler getStateMachineHandler(ActiveModel activeModel) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int playerProtocolVersion(UUID uuid) {
         return 0;
      }
   }
}
