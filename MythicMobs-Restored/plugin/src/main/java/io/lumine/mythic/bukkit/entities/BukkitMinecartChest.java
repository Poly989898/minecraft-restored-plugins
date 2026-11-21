package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.drops.droppables.VanillaItemDrop;
import java.util.Collection;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BukkitMinecartChest extends BukkitEntityType {
   private String dropTable;

   @Override
   public EntityType getBukkitType() {
      return EntityType.CHEST_MINECART;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.dropTable = mc.getString("Options.ChestContents");
   }

   @Override
   public Entity applyOptions(Entity entity) {
      StorageMinecart minecart = (StorageMinecart)entity;
      if (this.dropTable != null) {
         this.fillChest(minecart, minecart.getInventory());
      }

      return minecart;
   }

   private void fillChest(Entity cart, Inventory inventory) {
      AbstractEntity acart = BukkitAdapter.adapt(cart);
      DropMetadata meta = new DropMetadataImpl(new GenericCaster(acart), acart);
      Collection<ItemStack> items = Lists.newArrayList();
      MythicBukkit.inst().getDropManager().getDropTable(this.dropTable).get().generate(meta).getLootTable().forEach(type -> {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop) {
            if (((CustomDrop)type).getDrop().isEmpty()) {
               return;
            }

            drop = ((CustomDrop)type).getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof VanillaItemDrop) {
            items.add(BukkitAdapter.adapt(((VanillaItemDrop)drop).getDrop(meta, amount)));
         }
      });

      for (ItemStack itemStack : items) {
         int slot = -1;

         do {
            slot = (int)(Math.random() * inventory.getSize());
         } while (inventory.getItem(slot) != null);

         inventory.setItem(slot, itemStack);
      }
   }
}
