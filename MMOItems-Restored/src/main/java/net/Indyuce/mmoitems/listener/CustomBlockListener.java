package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Optional;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.inventory.EquipmentSlot;

public class CustomBlockListener implements Listener {
   public CustomBlockListener() {
      if (MMOItems.plugin.getLanguage().replaceMushroomDrops) {
         Bukkit.getPluginManager().registerEvents(new CustomBlockListener.MushroomReplacer(), MMOItems.plugin);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void a(BlockPhysicsEvent event) {
      if (MMOItems.plugin.getCustomBlocks().isMushroomBlock(event.getChangedType())) {
         event.setCancelled(true);
         event.getBlock().getState().update(true, false);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void b(BlockBreakEvent event) {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
         Optional optional = MMOItems.plugin.getCustomBlocks().getFromBlock(event.getBlock().getBlockData());
         if (optional.isPresent()) {
            CustomBlock customBlock = (CustomBlock)optional.get();
            int pickaxePower = MMOUtils.getPickaxePower(event.getPlayer());
            if (pickaxePower < customBlock.getRequiredPower()) {
               if (customBlock.requirePowerToBreak()) {
                  event.setCancelled(true);
               } else {
                  event.setDropItems(false);
                  event.setExpToDrop(0);
               }
            } else {
               event.setDropItems(false);
               event.setExpToDrop(event.getPlayer().getGameMode() == GameMode.CREATIVE ? 0 : customBlock.rollExperience());
            }
         }
      }
   }

   @Deprecated
   private static int getPickaxePower(Player player) {
      return MMOUtils.getPickaxePower(player);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void c(BlockPlaceEvent event) {
      if (!event.isCancelled() && !this.isMushroomBlock(event.getBlockPlaced().getType())) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getItemInHand());
         int mMOITEMSBLOCKID = nBTItem.getInteger("MMOITEMS_BLOCK_ID");
         if (mMOITEMSBLOCKID > 160 || mMOITEMSBLOCKID < 1 || mMOITEMSBLOCKID == 54) {
            return;
         }

         if (MMOItems.plugin.getCustomBlocks().getBlock(mMOITEMSBLOCKID) == null) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load custom block '" + mMOITEMSBLOCKID + "':  Block is not registered.");
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Try reloading the plugin to solve the issue.");
            event.setCancelled(true);
            return;
         }

         CustomBlock block = MMOItems.plugin.getCustomBlocks().getBlock(mMOITEMSBLOCKID);
         Block blockPlaced = event.getBlockPlaced();
         blockPlaced.setType(block.getState().getType(), false);
         blockPlaced.setBlockData(block.getState().getBlockData(), false);
         BlockPlaceEvent placeEvent = new BlockPlaceEvent(
            blockPlaced, blockPlaced.getState(), event.getBlockAgainst(), event.getItemInHand(), event.getPlayer(), true, EquipmentSlot.HAND
         );
         Bukkit.getServer().getPluginManager().callEvent(placeEvent);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void d(BlockIgniteEvent event) {
      if (event.getCause() == IgniteCause.LAVA || event.getCause() == IgniteCause.SPREAD) {
         BlockFace[] blockFaces = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

         for (BlockFace blockFace : blockFaces) {
            if (MMOItems.plugin.getCustomBlocks().getFromBlock(event.getBlock().getRelative(blockFace).getBlockData()).isPresent()) {
               event.setCancelled(true);
            }
         }
      }
   }

   private boolean isMushroomBlock(Material material) {
      return material == Material.BROWN_MUSHROOM_BLOCK || material == Material.MUSHROOM_STEM || material == Material.RED_MUSHROOM_BLOCK;
   }

   public static class MushroomReplacer implements Listener {
      @EventHandler(ignoreCancelled = true)
      public void d(BlockBreakEvent event) {
         if (MMOItems.plugin.getCustomBlocks().isMushroomBlock(event.getBlock().getType()) && MMOItems.plugin.getDropTables().hasSilkTouchTool(event.getPlayer())
            )
          {
            event.setDropItems(false);
         }
      }
   }
}
