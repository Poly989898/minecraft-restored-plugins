package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class PhatLootsDrop extends Drop implements IMultiDrop {
   private String lootString;

   public PhatLootsDrop(String line, MythicLineConfig config) {
      super(line, config);
      this.lootString = config.getString(new String[]{"type", "t"}, this.dropVar);
   }

   @Override
   public LootBag get(DropMetadata meta) {
      LootBag loot = new LootBag(meta);
      if (CompatibilityManager.PhatLoots == null) {
         MythicLogger.error("Phatloots isn't loaded!");
         return loot;
      } else {
         if (this.lootString != null && CompatibilityManager.PhatLoots != null) {
            List<ItemStack> dropsList = new ArrayList<>();
            int exp = 0;
            int money = 0;

            for (int i = 0; i < this.getAmount(); i++) {
               if (CompatibilityManager.PhatLoots.generateBundle(meta.getTrigger(), this.lootString)) {
                  dropsList.addAll(CompatibilityManager.PhatLoots.getLootBundleItems());
                  exp += CompatibilityManager.PhatLoots.getLootBundleXP();
                  money = (int)(money + CompatibilityManager.PhatLoots.getLootBundleMoney());
               }
            }

            for (ItemStack item : dropsList) {
               loot.add(meta, new VanillaItemDrop(this.getLine(), this.getConfig(), BukkitAdapter.adapt(item)));
            }

            if (exp > 0) {
               loot.add(meta, new ExperienceDrop(this.getLine(), this.getConfig(), exp));
            }

            if (money > 0) {
               loot.add(meta, new VaultDrop(this.getLine(), this.getConfig(), money));
            }
         }

         return loot;
      }
   }
}
