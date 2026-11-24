package io.lumine.mythic.lib.entity;

import io.lumine.mythic.lib.skill.trigger.TriggerType;

public enum ProjectileType {
   ARROW,
   TRIDENT;

   private final TriggerType tick = new TriggerType(this.name() + "_TICK");
   private final TriggerType hit = new TriggerType(this.name() + "_HIT");
   private final TriggerType land = new TriggerType(this.name() + "_LAND");

   public TriggerType getTickTrigger() {
      return this.tick;
   }

   public TriggerType getHitTrigger() {
      return this.hit;
   }

   public TriggerType getLandTrigger() {
      return this.land;
   }
}
