package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.drops.droppables.ExperienceDrop;
import io.lumine.mythic.core.drops.droppables.VaultDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMobLootDropEvent extends Event {
   private final ActiveMob mob;
   private final LivingEntity killer;
   private final LootBag drops;
   private static final HandlerList handlers = new HandlerList();

   public MythicMobLootDropEvent(ActiveMob am, LivingEntity killer, LootBag drops) {
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

   public LootBag getDrops() {
      return this.drops;
   }

   public Collection<Drop> getPhysicalDrops() {
      return this.drops.getLootTable();
   }

   public Collection<Drop> getIntangibleDrops() {
      return this.drops.getLootTableIntangible().values();
   }

   public Optional<Drop> getIntangibleDrop(Class<? extends IIntangibleDrop> clazz) {
      return Optional.ofNullable(this.drops.getLootTableIntangible().getOrDefault(clazz, null));
   }

   public int getExp() {
      Drop drop = this.drops.getLootTableIntangible().getOrDefault(ExperienceDrop.class, null);
      return drop == null ? 0 : (int)drop.getAmount();
   }

   public void setExp(int amount) {
      Drop drop = this.drops.getLootTableIntangible().getOrDefault(ExperienceDrop.class, null);
      if (drop == null) {
         this.drops.getLootTableIntangible().put(ExperienceDrop.class, new ExperienceDrop("API", null, amount));
      } else {
         drop.setAmount(amount);
      }
   }

   public int getMoney() {
      Drop drop = this.drops.getLootTableIntangible().getOrDefault(VaultDrop.class, null);
      return drop == null ? 0 : (int)drop.getAmount();
   }

   public void setMoney(int amount) {
      Drop drop = this.drops.getLootTableIntangible().getOrDefault(VaultDrop.class, null);
      if (drop == null) {
         this.drops.getLootTableIntangible().put(VaultDrop.class, new VaultDrop("API", null, amount));
      } else {
         drop.setAmount(amount);
      }
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
