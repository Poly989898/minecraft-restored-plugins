package com.nexomc.nexo.mechanics.custom_block.stringblock;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ&\u0010\u000b\u001a\u00020\u0005*\u00020\fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\r\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u000f\u001a\u00020\u0005*\u00020\u0010H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\r\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicPhysicsListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "tripwireEvent", "", "Lorg/bukkit/event/block/BlockPhysicsEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "MONITOR", "onPistonPush", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "HIGHEST", "ignoreCancelled", "onBreak", "Lorg/bukkit/event/block/BlockBreakEvent;", "com.nexomc:core"}
)
public final class StringBlockMechanicPhysicsListener implements Listener {
   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void tripwireEvent(@NotNull BlockPhysicsEvent blockPhysicsEvent) {
      if (blockPhysicsEvent.getChangedType() == Material.TRIPWIRE) {
         if (blockPhysicsEvent.getSourceBlock() != blockPhysicsEvent.getBlock()) {
            blockPhysicsEvent.setCancelled(true);
            Iterable entries$0 = (Iterable)StringBlockMechanicPhysicsListener.EntriesMappings.entries$0;

            for(Object element : entries$0) {
               BlockFace f = (BlockFace)element;
               int value = 0;
               if (f.isCartesian() && f.getModY() == 0 && f != BlockFace.SELF) {
                  Block relative = blockPhysicsEvent.getBlock().getRelative(f);
                  Block changed = relative;
                  if (changed.getType() == Material.TRIPWIRE) {
                     BlockData blockData2 = changed.getBlockData().clone();
                     BlockData data = blockData2;
                     SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
                     Location location = changed.getLocation();
                     schedulerUtils.runAtLocationLater(location, 1L, (a0_ -> StringBlockMechanicPhysicsListener.tripwireEvent$lambda$0$0(changed, data, (Location)a0_)));
                  }
               }
            }

            BlockData blockData3 = blockPhysicsEvent.getBlock().getBlockData().clone();
            BlockData blockData = blockData3;
            SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocationLater(blockPhysicsEvent.getBlock().getLocation(), (WrappedTask task) -> StringBlockMechanicPhysicsListener.tripwireEvent$lambda$1(blockPhysicsEvent, blockData, task), 1L);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPush(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : blocks) {
         Block it = (Block)element;
         int value = 0;
         if (it.getType() == Material.TRIPWIRE) {
            destination.add(element);
         }
      }

      List tripwireList = (List)destination;
      blocks = (Iterable)tripwireList;

      for(Object obj : blocks) {
         Block block = (Block)obj;
         int value2 = 0;
         StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(block);
         if (stringBlockMechanic != null) {
            StringBlockMechanic mechanic = stringBlockMechanic;
            block.setType(Material.AIR, false);
            Drop drop = mechanic.getBreakable().getDrop();
            Location location = block.getLocation();
            ItemStack itemStack = ItemStack.of(Material.AIR);
            drop.spawns(location, itemStack);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBreak(@NotNull BlockBreakEvent blockBreakEvent) {
      Block relative = blockBreakEvent.getBlock().getRelative(BlockFace.UP);
      Block blockAbove = relative;

      for(BlockFace face : (java.lang.Iterable<BlockFace>)(java.lang.Iterable)StringBlockMechanicPhysicsListener.EntriesMappings.entries$0) {
         if (face != BlockFace.SELF || face.isCartesian()) {
            if (blockBreakEvent.getBlock().getType() == Material.TRIPWIRE || blockBreakEvent.getBlock().getType() == Material.NOTE_BLOCK) {
               break;
            }

            Location location = blockBreakEvent.getBlock().getLocation();
            if (NexoFurniture.isFurniture(location)) {
               break;
            }

            if (blockBreakEvent.getBlock().getRelative(face).getType() == Material.TRIPWIRE) {
               if (blockBreakEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                  blockBreakEvent.getBlock().breakNaturally(blockBreakEvent.getPlayer().getInventory().getItemInMainHand(), true);
               } else {
                  blockBreakEvent.getBlock().setType(Material.AIR);
               }

               Material type = blockAbove.getType();
               if (BlockHelpers.isReplaceable(type)) {
                  blockAbove.breakNaturally(true);
               }

               SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
               Location location2 = blockBreakEvent.getBlock().getLocation();
               schedulerUtils.runAtLocationLater(location2, 1L, (a0_ -> StringBlockMechanicPhysicsListener.onBreak$lambda$0(blockBreakEvent, (Location)a0_)));
            }
         }
      }

   }

   private static final Unit tripwireEvent$lambda$0$0(Block changed, BlockData data, Location it) {
      changed.setBlockData(data, false);
      return Unit.INSTANCE;
   }

   private static final void tripwireEvent$lambda$1(BlockPhysicsEvent blockPhysicsEvent, BlockData blockData, WrappedTask it) {
      if (!blockPhysicsEvent.getBlock().getType().isAir()) {
         blockPhysicsEvent.getBlock().setBlockData(blockData, false);
      }
   }

   private static final Unit onBreak$lambda$0(BlockBreakEvent blockBreakEvent, Location it) {
      StringMechanicHelpers stringMechanicHelpers = StringMechanicHelpers.INSTANCE;
      Location location = blockBreakEvent.getBlock().getLocation();
      stringMechanicHelpers.fixClientsideUpdate(location);
      return Unit.INSTANCE;
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.block.BlockFace.values());
   }
}
