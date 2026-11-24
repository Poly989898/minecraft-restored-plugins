package io.lumine.mythic.lib.damage;

public enum DamageType {
   MAGIC,
   PHYSICAL,
   WEAPON,
   SKILL,
   PROJECTILE,
   UNARMED,
   ON_HIT,
   MINION,
   DOT;

   public String getPath() {
      return this.name().toLowerCase();
   }

   public String getOffenseStat() {
      return this.name() + "_DAMAGE";
   }
}
