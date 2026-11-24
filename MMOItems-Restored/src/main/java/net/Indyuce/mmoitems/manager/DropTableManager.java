package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.droptable.DropTable;
import net.Indyuce.mmoitems.api.event.ItemDropEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DropTableManager implements Listener, Reloadable {
   private final Map<EntityType, DropTable> monsters = new HashMap<>();
   private final Map<Material, DropTable> blocks = new HashMap<>();
   private final Map<Integer, DropTable> customBlocks = new HashMap<>();

   public DropTableManager() {
      this.reload();
   }

   @Override
   public void reload() {
      this.monsters.clear();
      this.blocks.clear();
      this.customBlocks.clear();
      FileConfiguration var1 = new ConfigFile("drops").getConfig();
      if (var1.contains("monsters")) {
         for (String var3 : var1.getConfigurationSection("monsters").getKeys(false)) {
            try {
               EntityType var4 = EntityType.valueOf(var3.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.monsters.put(var4, new DropTable(var1.getConfigurationSection("monsters." + var3)));
            } catch (IllegalArgumentException var7) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with mob type '" + var3 + "': " + var7.getMessage());
            }
         }
      }

      if (var1.contains("blocks")) {
         for (String var10 : var1.getConfigurationSection("blocks").getKeys(false)) {
            try {
               Material var12 = Material.valueOf(var10.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.blocks.put(var12, new DropTable(var1.getConfigurationSection("blocks." + var10)));
            } catch (IllegalArgumentException var6) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with material '" + var10 + "': " + var6.getMessage());
            }
         }
      }

      if (var1.contains("customblocks")) {
         for (String var11 : var1.getConfigurationSection("customblocks").getKeys(false)) {
            try {
               int var13 = Integer.parseInt(var11);
               this.customBlocks.put(var13, new DropTable(var1.getConfigurationSection("customblocks." + var11)));
            } catch (IllegalArgumentException var5) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with custom block '" + var11 + "': " + var5.getMessage());
            }
         }
      }
   }

   @EventHandler
   public void entityDrops(EntityDeathEvent var1) {
      LivingEntity var2 = var1.getEntity();
      Player var3 = var2.getKiller();
      if (var3 == null || !var3.hasMetadata("NPC")) {
         if (this.monsters.containsKey(var2.getType())) {
            List var4 = this.monsters.get(var2.getType()).read(var3 != null ? PlayerData.get(var3) : null, false);
            ItemDropEvent var5 = new ItemDropEvent(var3, var4, var2);
            Bukkit.getPluginManager().callEvent(var5);
            if (var5.isCancelled()) {
               return;
            }

            var1.getDrops().addAll(var4);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void blockDrops(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2 != null && var2.getGameMode() != GameMode.CREATIVE) {
         Block var3 = var1.getBlock();
         Optional var4 = MMOItems.plugin.getCustomBlocks().getFromBlock(var3.getBlockData());
         if (var4.isPresent()) {
            CustomBlock var5 = (CustomBlock)var4.get();
            if (this.customBlocks.containsKey(var5.getId()) && MMOUtils.getPickaxePower(var2) >= var5.getRequiredPower()) {
               PlayerData var6 = PlayerData.get(var2);
               List<ItemStack> var7 = this.customBlocks.get(var5.getId()).read(var6, this.hasSilkTouchTool(var2));
               ItemDropEvent var8 = new ItemDropEvent(var2, var7, var5);
               Bukkit.getPluginManager().callEvent(var8);
               if (var8.isCancelled()) {
                  return;
               }

               Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> {
                  for (ItemStack var3x : var7) {
                     UtilityMethods.dropItemNaturally(var3.getLocation(), var3x);
                  }
               }, 2L);
            }
         } else if (this.blocks.containsKey(var3.getType())) {
            Material var9 = var3.getType();
            List<ItemStack> var10 = this.blocks.get(var9).read(PlayerData.get(var2), this.hasSilkTouchTool(var2));
            ItemDropEvent var11 = new ItemDropEvent(var2, var10, var3);
            Bukkit.getPluginManager().callEvent(var11);
            if (var11.isCancelled()) {
               return;
            }

            Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> {
               for (ItemStack var3x : var10) {
                  UtilityMethods.dropItemNaturally(var3.getLocation(), var3x);
               }
            }, 2L);
         }
      }
   }

   public boolean hasSilkTouchTool(Player var1) {
      ItemStack var2 = var1.getInventory().getItemInMainHand();
      return var2 != null && var2.getType() != Material.AIR && var2.hasItemMeta() && var2.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
   }
}
