package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.utils.VersionUtil;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/FurniturePacketProcessor;", "", "<init>", "()V", "executor", "Ljava/util/concurrent/ExecutorService;", "sendAsync", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "action", "Lkotlin/Function0;", "", "com.nexomc:commons"}
)
public final class FurniturePacketProcessor {
   @NotNull
   public static final FurniturePacketProcessor INSTANCE = new FurniturePacketProcessor();
   @NotNull
   private static final ExecutorService executor;

   private FurniturePacketProcessor() {
   }

   @NotNull
   public final CompletableFuture<Void> sendAsync(@NotNull Function0<Unit> action) {
      if (VersionUtil.INSTANCE.isFoliaServer()) {
         action.invoke();
         CompletableFuture completableFuture = CompletableFuture.completedFuture((Object)null);
         return completableFuture;
      } else {
         CompletableFuture completableFuture2 = CompletableFuture.runAsync((() -> FurniturePacketProcessor.sendAsync$lambda$0(action)), (Executor)executor);
         return completableFuture2;
      }
   }

   private static final void sendAsync$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   static {
      ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
      executor = newSingleThreadExecutor;
   }
}
