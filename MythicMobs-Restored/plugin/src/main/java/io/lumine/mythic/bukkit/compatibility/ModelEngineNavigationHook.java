package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.MobType;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class ModelEngineNavigationHook implements Listener {
   private ModelEngineNavigationHook() {
   }

   public static void register(MythicBukkit plugin) {
      try {
         Plugin meg = Bukkit.getPluginManager().getPlugin("ModelEngine");
         ClassLoader loader = meg.getClass().getClassLoader();
         Class<? extends Event> eventType = loader.loadClass("com.ticxo.modelengine.api.events.AddModelEvent").asSubclass(Event.class);
         Method getTarget = eventType.getMethod("getTarget");
         Method getBase = loader.loadClass("com.ticxo.modelengine.api.model.ModeledEntity").getMethod("getBase");
         Method getOriginal = loader.loadClass("com.ticxo.modelengine.api.entity.BaseEntity").getMethod("getOriginal");
         Bukkit.getPluginManager().registerEvent(eventType, new ModelEngineNavigationHook(), EventPriority.MONITOR, (listener, event) -> {
            try {
               if (!(getOriginal.invoke(getBase.invoke(getTarget.invoke(event))) instanceof Entity entity && plugin.getVolatileCodeHandler() != null)) {
                  return;
               }

               plugin.getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(am -> {
                  if (am.getType() instanceof MobType type && type.isOptionAsyncPathfinding()) {
                     plugin.getVolatileCodeHandler().getAIHandler().enableAsyncPathfinding(am.getEntity());
                  }
               });
            } catch (ReflectiveOperationException e) {
               MythicLogger.handleMinorError(e);
            }
         }, plugin, true);
      } catch (RuntimeException | ReflectiveOperationException ex) {
         MythicLogger.error("Failed to hook ModelEngine model events for async pathfinding", ex);
      }
   }
}
