package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.LootBag;
import org.bukkit.inventory.ItemStack;

public class MythicDropsDrop extends Drop implements IMultiDrop {
   public MythicDropsDrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   @Override
   public LootBag get(DropMetadata meta) {
      LootBag loot = new LootBag(meta);
      if (CompatibilityManager.MythicDrops != null) {
         for (int i = 0; i < this.getAmount(); i++) {
            ItemStack md;
            if (this.dropVar != null) {
               md = CompatibilityManager.MythicDrops.getTierLoot(this.dropVar);
            } else {
               md = CompatibilityManager.MythicDrops.getLoot();
            }

            if (md != null) {
               loot.add(meta, new VanillaItemDrop(this.getLine(), this.getConfig(), BukkitAdapter.adapt(md)));
            }
         }
      }

      return loot;
   }
}
