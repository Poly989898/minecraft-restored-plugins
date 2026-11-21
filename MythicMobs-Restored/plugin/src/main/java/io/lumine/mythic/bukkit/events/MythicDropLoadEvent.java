package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicDropLoadEvent extends Event {
   private final CustomDrop holder;
   private String name;
   private String argument;
   private MythicLineConfig config;
   private IDrop drop = null;
   private static final HandlerList handlers = new HandlerList();

   public MythicDropLoadEvent(CustomDrop holder, String name, String argument, MythicLineConfig mlc) {
      this.holder = holder;
      this.name = name;
      this.argument = argument;
      this.config = mlc;
   }

   public CustomDrop getContainer() {
      return this.holder;
   }

   public String getDropName() {
      return this.name;
   }

   public MythicLineConfig getConfig() {
      return this.config;
   }

   public String getArgument() {
      return this.argument;
   }

   public void register(IDrop drop) {
      this.drop = drop;
   }

   public Optional<IDrop> getDrop() {
      return Optional.ofNullable(this.drop);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
