package io.lumine.mythic.bukkit.compatibility;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsAPI;
import com.codisimus.plugins.phatloots.loot.LootBundle;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.VolatileEnchantment;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class PhatLootsSupport {
   PhatLoots phatloots;
   public boolean enabled = false;
   private LootBundle lastBundle = null;

   public PhatLootsSupport() {
      this.phatloots = (PhatLoots)Bukkit.getPluginManager().getPlugin("PhatLoots");
      if (this.phatloots != null) {
         this.enabled = true;
      } else {
         this.enabled = false;
      }
   }

   public boolean generateBundle(AbstractEntity trigger, String phatLootTable) {
      if (!this.enabled) {
         return false;
      } else {
         PhatLoot phatloot = PhatLootsAPI.getPhatLoot(phatLootTable);
         LootBundle lootBundle;
         if (trigger != null && trigger.isPlayer()) {
            AbstractPlayer player = trigger.asPlayer();
            double lootingBonusPerLvl = PhatLoot.lootingBonusPerLvl;
            int lootLevel = player.getEnchantmentLevelHeld(VolatileEnchantment.LOOTING.getName());
            double lootingBonus = lootingBonusPerLvl * lootLevel;
            lootBundle = phatloot.rollForLoot(lootingBonus);
         } else {
            lootBundle = phatloot.rollForLoot();
         }

         if (lootBundle != null) {
            this.lastBundle = lootBundle;
            return true;
         } else {
            return false;
         }
      }
   }

   public List<ItemStack> getLootBundleItems() {
      return !this.enabled ? null : this.lastBundle.getItemList();
   }

   public int getLootBundleXP() {
      return !this.enabled ? 0 : this.lastBundle.getExp();
   }

   public double getLootBundleMoney() {
      return !this.enabled ? 0.0 : this.lastBundle.getMoney();
   }
}
