package com.ranull.dualwield.event;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
public class OffHandAttackEvent extends org.bukkit.event.entity.EntityDamageByEntityEvent {
    public OffHandAttackEvent() { super(null, null, DamageCause.ENTITY_ATTACK, 0.0); }

}
