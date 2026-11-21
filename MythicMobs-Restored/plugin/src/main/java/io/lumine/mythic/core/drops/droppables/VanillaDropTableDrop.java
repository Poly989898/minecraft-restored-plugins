package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.LootBag;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.LootContext.Builder;

@Getter
public class VanillaDropTableDrop extends Drop implements IMultiDrop {
   private LootTable lootTable;

   public VanillaDropTableDrop(String line, MythicLineConfig config) {
      super(line, config);
      String item = this.getDropVar();
      if (!item.contains(":")) {
         item = "minecraft:" + item;
      }

      LootTables lootTables = (LootTables)Registry.LOOT_TABLES.get(NamespacedKey.fromString(item.toLowerCase()));
      if (lootTables != null) {
         this.lootTable = lootTables.getLootTable();
      }
   }

   @Override
   public LootBag get(DropMetadata meta) {
      LootBag loot = new LootBag(meta);
      if (this.lootTable == null) {
         return loot;
      } else {
         AbstractLocation location;
         if (meta.getCaster() != null) {
            location = meta.getCaster().getLocation();
         } else if (meta.getCause().isPresent()) {
            location = meta.getCause().get().getLocation();
         } else if (meta.getTrigger() != null) {
            location = meta.getTrigger().getLocation();
         } else {
            location = new AbstractLocation(((World)Bukkit.getWorlds().get(0)).getName(), 0.0, 0.0, 0.0);
         }

         Builder context = new Builder(BukkitAdapter.adapt(location));
         Entity lootedEntity;
         if (meta.getDropper().isPresent()) {
            lootedEntity = meta.getDropper().get().getEntity().getBukkitEntity();
            context.lootedEntity(lootedEntity);
         } else {
            lootedEntity = null;
         }

         HumanEntity killer = this.getKiller(meta, lootedEntity);
         if (killer != null) {
            context.killer(killer);
         }

         for (ItemStack item : this.lootTable.populateLoot(ThreadLocalRandom.current(), context.build())) {
            loot.add(meta, new VanillaItemDrop(this.getLine(), this.getConfig(), BukkitAdapter.adapt(item)));
         }

         return loot;
      }
   }

   private HumanEntity getKiller(DropMetadata meta, Entity lootedEntity) {
      if (meta.getCause().isPresent() && meta.getCause().get().getBukkitEntity() instanceof HumanEntity killer) {
         return killer;
      } else {
         return lootedEntity instanceof LivingEntity livingEntity ? livingEntity.getKiller() : null;
      }
   }
}
