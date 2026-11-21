package io.lumine.mythic.bukkit.compatibility;

import com.tealcube.minecraft.bukkit.mythicdrops.MythicDropsPlugin;
import com.tealcube.minecraft.bukkit.mythicdrops.api.MythicDropsApi;
import com.tealcube.minecraft.bukkit.mythicdrops.api.items.builders.DropBuilder;
import com.tealcube.minecraft.bukkit.mythicdrops.api.tiers.Tier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class MythicDropsSupport {
   private MythicDropsPlugin handle = (MythicDropsPlugin)Bukkit.getPluginManager().getPlugin("MythicDrops");

   public MythicDropsPlugin getMythicDrops() {
      return this.handle;
   }

   public ItemStack getLoot() {
      DropBuilder db = MythicDropsPlugin.getNewDropBuilder();
      return db.build();
   }

   public ItemStack getTierLoot(String t) {
      Tier tier = MythicDropsApi.getMythicDrops().getTierManager().getById(t);
      DropBuilder db = MythicDropsPlugin.getNewDropBuilder().withTier(tier);
      return db.build();
   }
}
