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
   public void a(BlockPhysicsEvent var1) {
      if (MMOItems.plugin.getCustomBlocks().isMushroomBlock(var1.getChangedType())) {
         var1.setCancelled(true);
         var1.getBlock().getState().update(true, false);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void b(BlockBreakEvent var1) {
      if (var1.getPlayer().getGameMode() != GameMode.CREATIVE) {
         Optional var2 = MMOItems.plugin.getCustomBlocks().getFromBlock(var1.getBlock().getBlockData());
         if (var2.isPresent()) {
            CustomBlock var3 = (CustomBlock)var2.get();
            int var4 = MMOUtils.getPickaxePower(var1.getPlayer());
            if (var4 < var3.getRequiredPower()) {
               if (var3.requirePowerToBreak()) {
                  var1.setCancelled(true);
               } else {
                  var1.setDropItems(false);
                  var1.setExpToDrop(0);
               }
            } else {
               var1.setDropItems(false);
               var1.setExpToDrop(var1.getPlayer().getGameMode() == GameMode.CREATIVE ? 0 : var3.rollExperience());
            }
         }
      }
   }

   @Deprecated
   private static int getPickaxePower(Player var0) {
      return MMOUtils.getPickaxePower(var0);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void c(BlockPlaceEvent var1) {
      if (!var1.isCancelled() && !this.isMushroomBlock(var1.getBlockPlaced().getType())) {
         NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItemInHand());
         int var3 = var2.getInteger("MMOITEMS_BLOCK_ID");
         if (var3 > 160 || var3 < 1 || var3 == 54) {
            return;
         }

         if (MMOItems.plugin.getCustomBlocks().getBlock(var3) == null) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load custom block '" + var3 + "':  Block is not registered.");
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Try reloading the plugin to solve the issue.");
            var1.setCancelled(true);
            return;
         }

         CustomBlock var4 = MMOItems.plugin.getCustomBlocks().getBlock(var3);
         Block var5 = var1.getBlockPlaced();
         var5.setType(var4.getState().getType(), false);
         var5.setBlockData(var4.getState().getBlockData(), false);
         BlockPlaceEvent var6 = new BlockPlaceEvent(
            var5, var5.getState(), var1.getBlockAgainst(), var1.getItemInHand(), var1.getPlayer(), true, EquipmentSlot.HAND
         );
         Bukkit.getServer().getPluginManager().callEvent(var6);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void d(BlockIgniteEvent var1) {
      if (var1.getCause() == IgniteCause.LAVA || var1.getCause() == IgniteCause.SPREAD) {
         BlockFace[] var2 = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

         for (BlockFace var6 : var2) {
            if (MMOItems.plugin.getCustomBlocks().getFromBlock(var1.getBlock().getRelative(var6).getBlockData()).isPresent()) {
               var1.setCancelled(true);
            }
         }
      }
   }

   private boolean isMushroomBlock(Material var1) {
      return var1 == Material.BROWN_MUSHROOM_BLOCK || var1 == Material.MUSHROOM_STEM || var1 == Material.RED_MUSHROOM_BLOCK;
   }

   public static class MushroomReplacer implements Listener {
      @EventHandler(ignoreCancelled = true)
      public void d(BlockBreakEvent var1) {
         if (MMOItems.plugin.getCustomBlocks().isMushroomBlock(var1.getBlock().getType()) && MMOItems.plugin.getDropTables().hasSilkTouchTool(var1.getPlayer())
            )
          {
            var1.setDropItems(false);
         }
      }
   }
}
