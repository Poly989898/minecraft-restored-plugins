package com.nexomc.nexo.mechanics.custom_block.stringblock;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockPlaceEvent;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockHelpers;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableBlock;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableChild;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.random.Random;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ&\u0010\u000b\u001a\u00020\u0005*\u00020\fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\r\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u000f\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010J&\u0010\u0011\u001a\u00020\u0005*\u00020\u0012H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0013\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0013\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0011\u001a\u00020\u0005*\u00020\u0016H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0013\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0017\u001a\u00020\u0005*\u00020\u0012H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0013\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0002\b\u0007J\u001c\u0010\u001a\u001a\u00020\u0005*\u00020\u001bH\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0013¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onPlacingString", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "onStackable", "Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockInteractEvent;", "NORMAL", "ignoreCancelled", "onPlaceableOnWater", "HIGH", "onBreaking", "Lorg/bukkit/event/block/BlockBreakEvent;", "HIGHEST", "onBlockBreaking", "Lio/papermc/paper/event/block/BlockBreakBlockEvent;", "Lorg/bukkit/event/block/LeavesDecayEvent;", "onBreakingTall", "onPlaceStringOnString", "Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockPlaceEvent;", "onWaterUpdate", "Lorg/bukkit/event/block/BlockFromToEvent;", "com.nexomc:core"}
)
public final class StringBlockMechanicListener implements Listener {
   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void onPlacingString(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      ItemStack item = playerInteractEvent.getItem();
      if ((item != null ? item.getType() : null) == Material.STRING) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof StringBlockMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (StringBlockMechanicFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         if (!((StringBlockMechanicFactory)mechanicFactory != null ? !((StringBlockMechanicFactory)mechanicFactory).getDisableVanillaString() : false)) {
            playerInteractEvent.setUseItemInHand(Result.DENY);
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onStackable(@NotNull NexoStringBlockInteractEvent nexoStringBlockInteractEvent) {
      if (nexoStringBlockInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || nexoStringBlockInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (nexoStringBlockInteractEvent.getItemInHand() != null) {
            Player player = nexoStringBlockInteractEvent.getPlayer();
            Location location = nexoStringBlockInteractEvent.getBlock().getLocation();
            if (ProtectionLib.canBuild(player, location)) {
               Block block = nexoStringBlockInteractEvent.getBlock();
               StackableBlock stackable = nexoStringBlockInteractEvent.getMechanic().stackable();
               if (stackable != null) {
                  StackableChild stackableChild = stackable.nextStackable(nexoStringBlockInteractEvent.getBlock(), nexoStringBlockInteractEvent.getMechanic());
                  if (stackableChild != null) {
                     Tripwire blockData = stackableChild.getBlockData();
                     if (blockData != null) {
                        block.setBlockData((BlockData)blockData);
                        if (nexoStringBlockInteractEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                           nexoStringBlockInteractEvent.getItemInHand().subtract();
                        }

                        nexoStringBlockInteractEvent.getPlayer().swingHand(nexoStringBlockInteractEvent.getHand());
                        nexoStringBlockInteractEvent.setCancelled(true);
                        return;
                     }
                  }
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void onPlaceableOnWater(@NotNull PlayerInteractEvent playerInteractEvent) {
      ItemStack itemStack = playerInteractEvent.getItem();
      if (itemStack != null) {
         ItemStack it = itemStack;
         int value = 0;
         itemStack = !it.isEmpty() ? it : null;
         if (itemStack != null) {
            EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
            if (equipmentSlot == null) {
               return;
            }

            Pair pair = TuplesKt.to(itemStack, equipmentSlot);
            ItemStack item = (ItemStack)pair.component1();
            EquipmentSlot hand = (EquipmentSlot)pair.component2();
            String str = NexoItems.idFromItem(item);
            if (str == null) {
               return;
            }

            String itemID = str;
            RayTraceResult rayTraceResult = playerInteractEvent.getPlayer().rayTraceBlocks((double)5.0F, FluidCollisionMode.SOURCE_ONLY);
            if (rayTraceResult != null) {
               Block hitBlock = rayTraceResult.getHitBlock();
               if (hitBlock != null) {
                  Block placedAgainst = hitBlock;
                  if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR && playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
                     return;
                  }

                  Block it_4 = placedAgainst.getRelative(BlockFace.UP);
                  Block it_3 = it_4;
                  hitBlock = it_3.getType() == Material.AIR ? it_3 : null;
                  if (hitBlock == null) {
                     return;
                  }

                  Block target = hitBlock;
                  StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(itemID);
                  if (stringBlockMechanic != null) {
                     StringBlockMechanic it_2 = stringBlockMechanic;
                     int value2 = 0;
                     stringBlockMechanic = it_2.getPlaceableOnWater() ? it_2 : null;
                     if (stringBlockMechanic != null) {
                        StringBlockMechanic it_1 = stringBlockMechanic;
                        int value3 = 0;
                        stringBlockMechanic = NexoBlocks.stringMechanic((String)CollectionsKt.randomOrNull((Collection)it_1.randomPlace(), (Random)Random.Default));
                        if (stringBlockMechanic == null) {
                           stringBlockMechanic = it_1;
                        }

                        if (stringBlockMechanic != null) {
                           StringBlockMechanic mechanic = stringBlockMechanic;
                           CustomBlockHelpers customBlockHelpers = CustomBlockHelpers.INSTANCE;
                           Player player = playerInteractEvent.getPlayer();
                           BlockFace blockFace = playerInteractEvent.getBlockFace();
                           customBlockHelpers.makePlayerPlaceBlock(player, hand, item, target, blockFace, mechanic, (BlockData)mechanic.getBlockData());
                           return;
                        }
                     }
                  }

                  return;
               }
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBreaking(@NotNull BlockBreakEvent blockBreakEvent) {
      Block relative = blockBreakEvent.getBlock().getRelative(BlockFace.UP);
      Block blockAbove = relative;
      StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(blockAbove);
      if (stringBlockMechanic != null) {
         StringBlockMechanic mechanic = stringBlockMechanic;
         if (mechanic.getRequireSupporting()) {
            Location location = blockAbove.getLocation();
            NexoBlocks.remove(location, (Player)null, mechanic.getBreakable().getDrop());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBlockBreaking(@NotNull BlockBreakBlockEvent blockBreakBlockEvent) {
      Block relative = blockBreakBlockEvent.getBlock().getRelative(BlockFace.UP);
      Block blockAbove = relative;
      StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(blockAbove);
      if (stringBlockMechanic != null) {
         StringBlockMechanic mechanic = stringBlockMechanic;
         if (mechanic.getRequireSupporting()) {
            Location location = blockAbove.getLocation();
            NexoBlocks.remove(location, (Player)null, mechanic.getBreakable().getDrop());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBreaking(@NotNull LeavesDecayEvent leavesDecayEvent) {
      Block relative = leavesDecayEvent.getBlock().getRelative(BlockFace.UP);
      Block blockAbove = relative;
      StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(blockAbove);
      if (stringBlockMechanic != null) {
         StringBlockMechanic mechanic = stringBlockMechanic;
         if (mechanic.getRequireSupporting()) {
            Location location = blockAbove.getLocation();
            NexoBlocks.remove(location, (Player)null, mechanic.getBreakable().getDrop());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBreakingTall(@NotNull BlockBreakEvent blockBreakEvent) {
      Block relative = blockBreakEvent.getBlock().getRelative(BlockFace.DOWN);
      Block blockBelow = relative;
      relative = blockBreakEvent.getBlock();
      if (!NexoBlocks.isNexoStringBlock(relative)) {
         StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(blockBelow);
         if (stringBlockMechanic != null ? stringBlockMechanic.getTall() : false) {
            blockBreakEvent.setDropItems(false);
            Location location = blockBelow.getLocation();
            NexoBlocks.remove(location, blockBreakEvent.getPlayer(), (Drop)null);
         }
      }
   }

   @EventHandler
   public final void onPlaceStringOnString(@NotNull NexoStringBlockPlaceEvent nexoStringBlockPlaceEvent) {
      Block relative = nexoStringBlockPlaceEvent.getBlock().getRelative(BlockFace.DOWN);
      Block below = relative;
      if (NexoBlocks.isNexoStringBlock(below)) {
         nexoStringBlockPlaceEvent.setCancelled(true);
      }

      if (below.getType() == Material.TRIPWIRE) {
         relative = below.getRelative(BlockFace.DOWN);
         if (NexoBlocks.isNexoStringBlock(relative)) {
            nexoStringBlockPlaceEvent.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onWaterUpdate(@NotNull BlockFromToEvent blockFromToEvent) {
      Block toBlock = blockFromToEvent.getToBlock();
      Block changed = toBlock;
      toBlock = changed.getRelative(BlockFace.DOWN);
      Block changedBelow = toBlock;
      if (blockFromToEvent.getBlock().isLiquid() && changed.getType() == Material.TRIPWIRE) {
         blockFromToEvent.setCancelled(true);
         StringBlockMechanic mechanicBelow = NexoBlocks.stringMechanic(changedBelow);
         if (NexoBlocks.isNexoStringBlock(changed)) {
            Location location = changed.getLocation();
            NexoBlocks.remove(location, (Player)null, true);
         } else if (mechanicBelow != null && mechanicBelow.getTall()) {
            Location location2 = changedBelow.getLocation();
            NexoBlocks.remove(location2, (Player)null, true);
         }

      }
   }
}
