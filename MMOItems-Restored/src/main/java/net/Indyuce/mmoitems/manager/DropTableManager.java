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
      FileConfiguration config = new ConfigFile("drops").getConfig();
      if (config.contains("monsters")) {
         for (String key : config.getConfigurationSection("monsters").getKeys(false)) {
            try {
               EntityType entityType = EntityType.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.monsters.put(entityType, new DropTable(config.getConfigurationSection("monsters." + key)));
            } catch (IllegalArgumentException exception) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with mob type '" + key + "': " + exception.getMessage());
            }
         }
      }

      if (config.contains("blocks")) {
         for (String key : config.getConfigurationSection("blocks").getKeys(false)) {
            try {
               Material material = Material.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.blocks.put(material, new DropTable(config.getConfigurationSection("blocks." + key)));
            } catch (IllegalArgumentException exception) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with material '" + key + "': " + exception.getMessage());
            }
         }
      }

      if (config.contains("customblocks")) {
         for (String key : config.getConfigurationSection("customblocks").getKeys(false)) {
            try {
               int count = Integer.parseInt(key);
               this.customBlocks.put(count, new DropTable(config.getConfigurationSection("customblocks." + key)));
            } catch (IllegalArgumentException exception) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not read drop table with custom block '" + key + "': " + exception.getMessage());
            }
         }
      }
   }

   @EventHandler
   public void entityDrops(EntityDeathEvent event) {
      LivingEntity entity = event.getEntity();
      Player killer = entity.getKiller();
      if (killer == null || !killer.hasMetadata("NPC")) {
         if (this.monsters.containsKey(entity.getType())) {
            List list = this.monsters.get(entity.getType()).read(killer != null ? PlayerData.get(killer) : null, false);
            ItemDropEvent event2 = new ItemDropEvent(killer, list, entity);
            Bukkit.getPluginManager().callEvent(event2);
            if (event2.isCancelled()) {
               return;
            }

            event.getDrops().addAll(list);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void blockDrops(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (player != null && player.getGameMode() != GameMode.CREATIVE) {
         Block block = event.getBlock();
         Optional optional = MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData());
         if (optional.isPresent()) {
            CustomBlock customBlock = (CustomBlock)optional.get();
            if (this.customBlocks.containsKey(customBlock.getId()) && MMOUtils.getPickaxePower(player) >= customBlock.getRequiredPower()) {
               PlayerData playerData = PlayerData.get(player);
               List<ItemStack> items = this.customBlocks.get(customBlock.getId()).read(playerData, this.hasSilkTouchTool(player));
               ItemDropEvent event2 = new ItemDropEvent(player, items, customBlock);
               Bukkit.getPluginManager().callEvent(event2);
               if (event2.isCancelled()) {
                  return;
               }

               Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> {
                  for (ItemStack var3x : items) {
                     UtilityMethods.dropItemNaturally(block.getLocation(), var3x);
                  }
               }, 2L);
            }
         } else if (this.blocks.containsKey(block.getType())) {
            Material type = block.getType();
            List<ItemStack> items = this.blocks.get(type).read(PlayerData.get(player), this.hasSilkTouchTool(player));
            ItemDropEvent event2 = new ItemDropEvent(player, items, block);
            Bukkit.getPluginManager().callEvent(event2);
            if (event2.isCancelled()) {
               return;
            }

            Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> {
               for (ItemStack var3x : items) {
                  UtilityMethods.dropItemNaturally(block.getLocation(), var3x);
               }
            }, 2L);
         }
      }
   }

   public boolean hasSilkTouchTool(Player player) {
      ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      return itemInMainHand != null && itemInMainHand.getType() != Material.AIR && itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
   }
}
