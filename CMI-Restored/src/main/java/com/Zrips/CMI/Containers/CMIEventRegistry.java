package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.function.BiConsumer;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CMIEventRegistry {
  static Plugin plugin = CMI.getInstance();
  static PluginManager pm = CMI.getInstance().getServer().getPluginManager();

  public static <T extends Event> void registerEvent(
      EventPriority eventPriority,
      boolean flag,
      Class<T> eventClass,
      BiConsumer<Listener, T> biConsumer) {
    registerEvent(eventPriority, flag, true, eventClass, biConsumer, null, null);
  }

  public static <T extends Event> void registerEvent(
      EventPriority eventPriority, Class<T> eventClass, BiConsumer<Listener, T> biConsumer) {
    registerEvent(eventPriority, false, true, eventClass, biConsumer, null, null);
  }

  public static <T extends Event> void registerEvent(
      Class<T> eventClass, BiConsumer<Listener, T> biConsumer) {
    registerEvent(EventPriority.NORMAL, false, true, eventClass, biConsumer, null, null);
  }

  public static <T1 extends Event, T2 extends Event> void registerEvent(
      EventPriority eventPriority,
      boolean flag,
      Class<T1> eventClass,
      BiConsumer<Listener, T1> biConsumer,
      Class<T2> secondEventClass,
      BiConsumer<Listener, T2> secondConsumer) {
    registerEvent(
        eventPriority, false, flag, eventClass, biConsumer, secondEventClass, secondConsumer);
  }

  public static <T1 extends Event, T2 extends Event> void registerEvent(
      EventPriority eventPriority,
      boolean flag,
      boolean useFirstEvent,
      Class<T1> eventClass,
      BiConsumer<Listener, T1> biConsumer,
      Class<T2> secondEventClass,
      BiConsumer<Listener, T2> secondConsumer) {
    Listener listener = new Listener() {};
    if (useFirstEvent) {
      pm.registerEvent(
          eventClass,
          listener,
          eventPriority,
          (listenerArg, event) -> biConsumer.accept(listenerArg, eventClass.cast(event)),
          plugin,
          flag);
    } else {
      pm.registerEvent(
          secondEventClass,
          listener,
          eventPriority,
          (secondListenerArg, secondEvent) ->
              secondConsumer.accept(secondListenerArg, secondEventClass.cast(secondEvent)),
          plugin,
          flag);
    }
  }
}
