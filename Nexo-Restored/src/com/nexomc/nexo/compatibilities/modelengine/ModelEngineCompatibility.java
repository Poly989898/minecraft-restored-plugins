package com.nexomc.nexo.compatibilities.modelengine;

import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.logs.Logs;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \t2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\tB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u0006*\u00020\u0007H\u0007b\u0002\b\b¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/modelengine/ModelEngineCompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lcom/ticxo/modelengine/api/ModelEngineAPI;", "<init>", "()V", "onMegReload", "", "Lcom/ticxo/modelengine/api/events/ModelRegistrationEvent;", "Lorg/bukkit/event/EventHandler;", "Companion", "com.nexomc:core"}
)
public final class ModelEngineCompatibility extends CompatibilityProvider<ModelEngineAPI> {
   @NotNull
   public static final Companion Companion = new Companion();
   private static final long TIMEOUT_SECONDS = 300L;
   @NotNull
   private static CompletableFuture<Void> modelEngineFuture = new CompletableFuture();

   @EventHandler
   public final void onMegReload(@NotNull ModelRegistrationEvent modelRegistrationEvent) {
      ModelGenerator.Phase phase = modelRegistrationEvent.getPhase();
      switch (phase == null ? -1 : ModelEngineCompatibility.WhenMappings.$EnumSwitchMapping$0[phase.ordinal()]) {
         case 1:
            if (modelEngineFuture.isDone()) {
               modelEngineFuture = new CompletableFuture();
            }

            Logs.logInfo("Awaiting ModelEngine ResourcePack...");
            break;
         case 2:
            modelEngineFuture.complete(null);
            Logs.logInfo("ModelEngine ResourcePack is ready.");
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/compatibilities/modelengine/ModelEngineCompatibility$Companion;", "", "<init>", "()V", "TIMEOUT_SECONDS", "", "modelEngineFuture", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final CompletableFuture<Void> modelEngineFuture() {
         if (PluginUtils.INSTANCE.isModelEngineEnabled() && Settings.PACK_IMPORT_MODEL_ENGINE.toBool()) {
            CompletableFuture future = ModelEngineCompatibility.modelEngineFuture;
            if (future.isDone()) {
               return future;
            } else {
               CompletableFuture.delayedExecutor(300L, TimeUnit.SECONDS).execute((() -> Companion.modelEngineFuture$lambda$0(future)));
               return future;
            }
         } else {
            CompletableFuture completableFuture = CompletableFuture.completedFuture((Object)null);
            return completableFuture;
         }
      }

      private static final void modelEngineFuture$lambda$0(CompletableFuture future) {
         if (!future.isDone() && future.complete((Object)null)) {
            Logs.logWarn("ModelEngine ResourcePack did not finish within 300s, continuing without it...", false);
         }

      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.ticxo.modelengine.api.generator.ModelGenerator.Phase.values().length];
         try { a_0[com.ticxo.modelengine.api.generator.ModelGenerator.Phase.PRE_IMPORT.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.ticxo.modelengine.api.generator.ModelGenerator.Phase.FINISHED.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
