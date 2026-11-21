package com.nexomc.nexo.mods;

import kotlin.Metadata;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\bf\u0018\u0000 \r2\u00020\u0001:\u0002\f\rJ\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0003H&R\u0014\u0010\u0006\u001a\u00020\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\t¨\u0006\u000eÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "", "reload", "", "initialize", "uninitialize", "HELLO_CHANNEL", "", "getHELLO_CHANNEL", "()Ljava/lang/String;", "REGISTRY_CHANNEL", "getREGISTRY_CHANNEL", "EmptyCreativeInventory", "Companion", "com.nexomc:core"}
)
public interface INexoCreativeInventory {
   @NotNull
   Companion Companion = INexoCreativeInventory.Companion.$$INSTANCE;

   void reload();

   void initialize();

   void uninitialize();

   @NotNull
   default String getHELLO_CHANNEL() {
      return "nexo:hello";
   }

   @NotNull
   default String getREGISTRY_CHANNEL() {
      return "nexo:registry";
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\b\u0010\u0007\u001a\u00020\u0005H\u0016¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mods/INexoCreativeInventory$EmptyCreativeInventory;", "Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "<init>", "()V", "reload", "", "initialize", "uninitialize", "com.nexomc:core"}
   )
   public static final class EmptyCreativeInventory implements INexoCreativeInventory {
      public void reload() {
      }

      public void initialize() {
      }

      public void uninitialize() {
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @NotNull
      public static String getHELLO_CHANNEL(@NotNull INexoCreativeInventory iNexoCreativeInventory) {
         return iNexoCreativeInventory.getHELLO_CHANNEL();
      }

      /** @deprecated */
      @Deprecated
      @NotNull
      public static String getREGISTRY_CHANNEL(@NotNull INexoCreativeInventory iNexoCreativeInventory) {
         return iNexoCreativeInventory.getREGISTRY_CHANNEL();
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mods/INexoCreativeInventory$Companion;", "", "<init>", "()V", "COOKIE_KEY", "Lorg/bukkit/NamespacedKey;", "getCOOKIE_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      public static final Companion $$INSTANCE = new Companion();
      @NotNull
      private static final NamespacedKey COOKIE_KEY = new NamespacedKey("nexo", "creative_inventory");

      private Companion() {
      }

      @NotNull
      public final NamespacedKey getCOOKIE_KEY() {
         return COOKIE_KEY;
      }
   }
}
