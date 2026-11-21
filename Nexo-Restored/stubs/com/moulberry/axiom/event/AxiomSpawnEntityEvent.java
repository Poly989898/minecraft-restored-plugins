package com.moulberry.axiom.event;

/** Compile-only stub of the Axiom API event (not packaged). */
public class AxiomSpawnEntityEvent extends org.bukkit.event.Event {
   private static final org.bukkit.event.HandlerList HANDLERS = new org.bukkit.event.HandlerList();

   public org.bukkit.entity.Entity getEntity() { return null; }

   @Override
   public org.bukkit.event.HandlerList getHandlers() { return HANDLERS; }

   public static org.bukkit.event.HandlerList getHandlerList() { return HANDLERS; }
}
