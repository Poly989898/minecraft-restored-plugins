package io.lumine.mythic.lib.api.util;

import io.lumine.mythic.lib.MythicLib;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TemporaryListener implements Listener {
   private final HandlerList[] handlerLists;
   private boolean closed;
   @Nullable
   private BukkitRunnable runnable;

   public TemporaryListener(@NotNull HandlerList... handlerLists) {
      this(MythicLib.plugin, handlerLists);
   }

   public TemporaryListener(@NotNull JavaPlugin plugin, @NotNull HandlerList... handlerLists) {
      this.handlerLists = handlerLists.length == 0 ? inferHandlerLists(this.getClass()) : handlerLists;
      Bukkit.getPluginManager().registerEvents(this, plugin);
   }

   public void close(long duration) {
      Bukkit.getScheduler().runTaskLater(MythicLib.plugin, () -> this.close(), duration);
   }

   @NotNull
   public BukkitRunnable getRunnable() {
      return Objects.requireNonNull(this.runnable, "No runnable registered");
   }

   public void registerRunnable(@NotNull BukkitRunnable runnable, Consumer<BukkitRunnable> action) {
      Validate.notNull(runnable, "Runnable cannot be null");
      Validate.isTrue(this.runnable == null, "Runnable already registered");
      this.runnable = runnable;
      action.accept(runnable);
   }

   public boolean close() {
      if (this.closed) {
         return false;
      }

      this.closed = true;
      this.whenClosed();
      if (this.runnable != null && !this.runnable.isCancelled()) {
         this.runnable.cancel();
      }

      for (HandlerList list : this.handlerLists) {
         list.unregister(this);
      }

      return true;
   }

   public void whenClosed() {
   }

   public static HandlerList[] inferHandlerLists(@NotNull Class<?> clazz) {
      List<HandlerList> lists = new ArrayList<>();

      for (Method method : clazz.getDeclaredMethods()) {
         try {
            EventHandler annot = method.getAnnotation(EventHandler.class);
            if (annot != null) {
               Validate.isTrue(method.getParameterCount() == 1, "Wrong param count for event handler");
               Class<?> paramType = method.getParameters()[0].getType();
               Validate.isTrue(isEventClass(paramType), "Param of event handler is not an event class");
               HandlerList handlerList = (HandlerList)paramType.getMethod("getHandlerList").invoke(null);
               lists.add(handlerList);
            }
         } catch (Throwable any) {
            throw new RuntimeException("Could not infer events of temporary listener", any);
         }
      }

      return lists.toArray(new HandlerList[0]);
   }

   private static boolean isEventClass(@NotNull Class<?> clazz) {
      Class<?> superclass;
      return clazz == Event.class || (superclass = clazz.getSuperclass()) != null && isEventClass(superclass);
   }
}
