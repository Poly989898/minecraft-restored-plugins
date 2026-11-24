package net.Indyuce.mmocore.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.block.BlockInfo;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.chest.condition.ConditionInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockListener implements Listener {
   private static final BlockFace[] order = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onCustomBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE) {
         String savedData = event.getBlock().getBlockData().getAsString();
         Block block = event.getBlock();
         boolean customMine = MMOCore.plugin.mineManager.isEnabled(player, block.getLocation());
         if (customMine) {
            BlockInfo info = MMOCore.plugin.mineManager.getInfo(block);
            boolean temporaryBlock = MMOCore.plugin.mineManager.isTemporaryBlock(block);
            if ((!temporaryBlock || info != null) && (info == null || info.checkConditions(block))) {
               if (info == null) {
                  if (MMOCore.plugin.mineManager.shouldProtect()) {
                     event.setCancelled(true);
                  }
               } else if (!info.getBlock().breakRestrictions(block)) {
                  event.setCancelled(true);
               } else if (!UtilityMethods.isFake(event)) {
                  boolean canBreak = true;
                  ItemStack item = player.getInventory().getItemInMainHand();
                  if (!MMOCore.plugin.restrictionManager.checkPermissions(item, info.getBlock())) {
                     ConfigMessage.fromKey("cannot-break").send(player);
                     canBreak = false;
                  }

                  boolean conditionsMet = !info.hasDropTable() || info.getDropTable().areConditionsMet(new ConditionInstance(player));
                  List<ItemStack> drops = conditionsMet && info.hasDropTable()
                     ? info.getDropTable().collect(new LootBuilder(PlayerData.get(player), info.getDropTable().getCapacity()))
                     : new ArrayList<>();
                  CustomBlockMineEvent called = new CustomBlockMineEvent(PlayerData.get(player), block, info, drops, !canBreak);
                  Bukkit.getPluginManager().callEvent(called);
                  if (called.isCancelled()) {
                     event.setCancelled(true);
                  } else {
                     drops = called.getDrops();
                     if (!info.getOption(BlockInfo.BlockInfoOption.VANILLA_DROPS)) {
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                        MMOCoreUtils.decreaseDurability(player, EquipmentSlot.HAND, 1);
                     }

                     if (conditionsMet && info.hasTriggers() && !block.hasMetadata("player_placed")) {
                        PlayerData playerData = PlayerData.get(player);
                        info.getTriggers().forEach(trigger -> trigger.apply(playerData));
                     }

                     if (conditionsMet && info.hasDropTable()) {
                        Location dropLocation = this.getSafeDropLocation(
                           block, !block.getType().isSolid() || !info.regenerates() || !info.getRegenerationInfo().hasTemporaryBlock()
                        );

                        for (ItemStack drop : drops) {
                           if (drop.getType() != Material.AIR && drop.getAmount() > 0) {
                              UtilityMethods.dropItemNaturally(dropLocation, drop);
                           }
                        }
                     }

                     if (info.hasRegen()) {
                        Bukkit.getScheduler()
                           .runTaskLater(
                              MythicLib.plugin,
                              () -> MMOCore.plugin
                                 .mineManager
                                 .initialize(info.startRegeneration(Bukkit.createBlockData(savedData), block.getLocation()), !temporaryBlock),
                              1L
                           );
                     }
                  }
               }
            } else {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void unregisterPlayerPlacedBlocksTag(BlockBreakEvent event) {
      if (!UtilityMethods.isFake(event)) {
         event.getBlock().removeMetadata("player_placed", MMOCore.plugin);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void registerPlayerPlacedBlocksTag(BlockPlaceEvent event) {
      if (!event.getBlock().getType().name().startsWith("STRIPPED_") || !event.getItemInHand().getType().name().endsWith("_AXE")) {
         event.getBlock().setMetadata("player_placed", new FixedMetadataValue(MMOCore.plugin, true));
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void blockPistonExtend(BlockPistonExtendEvent event) {
      Block movedBlock = event.getBlock();
      if (movedBlock.hasMetadata("player_placed")) {
         BlockFace direction = event.getDirection();

         for (Block b : event.getBlocks()) {
            if (b.hasMetadata("player_placed")) {
               movedBlock = b.getRelative(direction);
               movedBlock.setMetadata("player_placed", new FixedMetadataValue(MMOCore.plugin, true));
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void blockPistonRetract(BlockPistonRetractEvent event) {
      BlockFace direction = event.getDirection();
      Block movedBlock = event.getBlock().getRelative(direction);
      movedBlock.setMetadata("player_placed", new FixedMetadataValue(MMOCore.plugin, true));

      for (Block block : event.getBlocks()) {
         movedBlock = block.getRelative(direction);
         movedBlock.setMetadata("player_placed", new FixedMetadataValue(MMOCore.plugin, true));
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void cobblestoneGeneratorHandling(BlockFormEvent event) {
      if (event.getBlock().hasMetadata("player_placed")) {
         event.getBlock().removeMetadata("player_placed", MMOCore.plugin);
      }

      if (!MMOCore.plugin.configManager.cobbleGeneratorXP) {
         if ((event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.LAVA)
            && MythicLib.plugin.getVersion().getWrapper().isGeneratorOutput(event.getNewState().getType())) {
            event.getNewState().setMetadata("player_placed", new FixedMetadataValue(MMOCore.plugin, true));
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void handlePlayerStatistics(CustomBlockMineEvent event) {
      if (event.getBlockInfo().getBlock() instanceof VanillaBlockType) {
         event.getPlayer().incrementStatistic(Statistic.MINE_BLOCK, ((VanillaBlockType)event.getBlockInfo().getBlock()).getType());
      }
   }

   private Location getSafeDropLocation(Block block, boolean self) {
      if (block.getType() == Material.AIR && self) {
         return block.getLocation();
      }

      for (BlockFace face : order) {
         Block relative = block.getRelative(face);
         if (!relative.getType().isSolid()) {
            return relative.getLocation().add(block.getLocation().subtract(relative.getLocation()).multiply(0.6));
         }
      }

      return block.getLocation();
   }
}
