package com.nexomc.libs.folialib.impl;

import com.nexomc.libs.folialib.FoliaLib;
import com.nexomc.libs.folialib.util.ImplementationTestsUtil;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PaperImplementation extends SpigotImplementation {
   private final FoliaLib foliaLib;
   private Method teleportAsyncMethod;

   public PaperImplementation(FoliaLib foliaLib) {
      super(foliaLib);
      this.foliaLib = foliaLib;
      if (ImplementationTestsUtil.isAsyncTeleportSupported()) {
         try {
            this.teleportAsyncMethod = Entity.class.getMethod("teleportAsync", Location.class, PlayerTeleportEvent.TeleportCause.class);
         } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize PaperImplementation", e);
         }
      }

   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
      return this.teleportAsync(entity, location, TeleportCause.PLUGIN);
   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
      if (!ImplementationTestsUtil.isAsyncTeleportSupported()) {
         return super.teleportAsync(entity, location, cause);
      } else {
         try {
            return (CompletableFuture)this.teleportAsyncMethod.invoke(entity, location, cause);
         } catch (Exception e) {
            e.printStackTrace();
            return super.teleportAsync(entity, location, cause);
         }
      }
   }
}
