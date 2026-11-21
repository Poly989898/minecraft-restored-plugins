package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import lombok.Generated;
import lombok.ToString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@ToString
public class MythicMobDeathEvent extends Event {
   private final ActiveMob mob;
   private final LivingEntity killer;
   private List<ItemStack> drops;
   private static final HandlerList handlers = new HandlerList();

   public MythicMobDeathEvent(ActiveMob am, LivingEntity killer, List<ItemStack> drops) {
      this.mob = am;
      this.killer = killer;
      this.drops = drops;
   }

   public ActiveMob getMob() {
      return this.mob;
   }

   public Entity getEntity() {
      return BukkitAdapter.adapt(this.mob.getEntity());
   }

   public MythicMob getMobType() {
      return this.mob.getType();
   }

   public double getMobLevel() {
      return this.mob.getLevel();
   }

   public LivingEntity getKiller() {
      return this.killer;
   }

   public List<ItemStack> getDrops() {
      return this.drops;
   }

   public void setDrops(List<ItemStack> list) {
      this.drops = list;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MythicMobDeathEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mob = this.getMob();
         Object other$mob = other.getMob();
         if (this$mob == null ? other$mob == null : this$mob.equals(other$mob)) {
            Object this$killer = this.getKiller();
            Object other$killer = other.getKiller();
            if (this$killer == null ? other$killer == null : this$killer.equals(other$killer)) {
               Object this$drops = this.getDrops();
               Object other$drops = other.getDrops();
               return this$drops == null ? other$drops == null : this$drops.equals(other$drops);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MythicMobDeathEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mob = this.getMob();
      result = result * 59 + ($mob == null ? 43 : $mob.hashCode());
      Object $killer = this.getKiller();
      result = result * 59 + ($killer == null ? 43 : $killer.hashCode());
      Object $drops = this.getDrops();
      return result * 59 + ($drops == null ? 43 : $drops.hashCode());
   }
}
