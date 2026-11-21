package com.nexomc.nexo.nms;

import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0001\u0007J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\bÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/IPlayerUtils;", "", "applyMiningEffect", "", "player", "Lorg/bukkit/entity/Player;", "removeMiningEffect", "EmptyPlayerUtils", "com.nexomc:core"}
)
public interface IPlayerUtils {
   default void applyMiningEffect(@NotNull Player player) {
   }

   default void removeMiningEffect(@NotNull Player player) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      public static void applyMiningEffect(@NotNull IPlayerUtils iPlayerUtils, @NotNull Player player) {
         iPlayerUtils.applyMiningEffect(player);
      }

      /** @deprecated */
      @Deprecated
      public static void removeMiningEffect(@NotNull IPlayerUtils iPlayerUtils, @NotNull Player player) {
         iPlayerUtils.removeMiningEffect(player);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/nms/IPlayerUtils$EmptyPlayerUtils;", "Lcom/nexomc/nexo/nms/IPlayerUtils;", "<init>", "()V", "applyMiningEffect", "", "player", "Lorg/bukkit/entity/Player;", "removeMiningEffect", "com.nexomc:core"}
   )
   public static final class EmptyPlayerUtils implements IPlayerUtils {
      public void applyMiningEffect(@NotNull Player player) {
      }

      public void removeMiningEffect(@NotNull Player player) {
      }
   }
}
