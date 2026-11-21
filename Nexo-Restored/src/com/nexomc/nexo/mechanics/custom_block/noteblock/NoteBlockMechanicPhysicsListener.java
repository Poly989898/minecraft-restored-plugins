package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u000e\u001a\u00020\u0005*\u00020\u000fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u0010\u001a\u00020\u0005*\u00020\u0011H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ\u0010\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u0002¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicPhysicsListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onPistonPush", "", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onPistonPull", "Lorg/bukkit/event/block/BlockPistonRetractEvent;", "onBlockPhysics", "Lorg/bukkit/event/block/BlockPhysicsEvent;", "onNoteblockPowered", "Lorg/bukkit/event/world/GenericGameEvent;", "updateBlockAbove", "block", "Lorg/bukkit/block/Block;", "com.nexomc:core"}
)
public final class NoteBlockMechanicPhysicsListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPush(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
      boolean flag;
      if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
         flag = false;
      } else {
         block: {
            for(Object element : blocks) {
               Block it = (Block)element;
               int value = 0;
               if (it.getType() == Material.NOTE_BLOCK) {
                  flag = true;
                  break block;
               }
            }

            flag = false;
         }
      }

      if (flag) {
         blockPistonExtendEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPull(@NotNull BlockPistonRetractEvent blockPistonRetractEvent) {
      Iterable blocks = (Iterable)blockPistonRetractEvent.getBlocks();
      boolean flag;
      if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
         flag = false;
      } else {
         block: {
            for(Object element : blocks) {
               Block it = (Block)element;
               int value = 0;
               if (it.getType() == Material.NOTE_BLOCK) {
                  flag = true;
                  break block;
               }
            }

            flag = false;
         }
      }

      if (flag) {
         blockPistonRetractEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBlockPhysics(@NotNull BlockPhysicsEvent blockPhysicsEvent) {
      Pair pair = TuplesKt.to(blockPhysicsEvent.getBlock().getRelative(BlockFace.UP), blockPhysicsEvent.getBlock().getRelative(BlockFace.DOWN));
      Object component1 = pair.component1();
      Block aboveBlock = (Block)component1;
      component1 = pair.component2();
      Block belowBlock = (Block)component1;
      if (belowBlock.getType() == Material.NOTE_BLOCK) {
         blockPhysicsEvent.setCancelled(true);
         this.updateBlockAbove(belowBlock);
      } else if (aboveBlock.getType() == Material.NOTE_BLOCK) {
         blockPhysicsEvent.setCancelled(true);
         this.updateBlockAbove(aboveBlock);
      }

      if (blockPhysicsEvent.getBlock().getType() == Material.NOTE_BLOCK) {
         blockPhysicsEvent.setCancelled(true);
         Block block = blockPhysicsEvent.getBlock();
         this.updateBlockAbove(block);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onNoteblockPowered(@NotNull GenericGameEvent genericGameEvent) {
      Block block2 = genericGameEvent.getLocation().getBlock();
      Block block = block2;
      BlockData blockData = block.getBlockData().clone();
      NoteBlock noteBlock = blockData instanceof NoteBlock ? (NoteBlock)blockData : null;
      if ((blockData instanceof NoteBlock ? (NoteBlock)blockData : null) != null) {
         NoteBlock data = noteBlock;
         BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
         Location location = genericGameEvent.getLocation();
         if (blockHelpers.isLoaded(location) && !genericGameEvent.isAsynchronous() && genericGameEvent.getEvent() == GameEvent.NOTE_BLOCK_PLAY) {
            SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
            location = genericGameEvent.getLocation();
            schedulerUtils.runAtLocationLater(location, 1L, (a0_ -> NoteBlockMechanicPhysicsListener.onNoteblockPowered$lambda$0(block, data, (Location)a0_)));
         }
      }
   }

   private final void updateBlockAbove(Block block) {
      Block it = block.getRelative(BlockFace.UP);
      int value = 0;
      Pair pair = TuplesKt.to(it, it.getRelative(BlockFace.UP));
      Object component1 = pair.component1();
      Block blockAbove = (Block)component1;
      component1 = pair.component2();
      it = (Block)component1;
      if (blockAbove.getType() == Material.NOTE_BLOCK) {
         blockAbove.getState().update(true, true);
      }

      if (it.getType() == Material.NOTE_BLOCK) {
         this.updateBlockAbove(blockAbove);
      }

   }

   private static final Unit onNoteblockPowered$lambda$0(Block block, NoteBlock data, Location it) {
      block.setBlockData((BlockData)data, false);
      return Unit.INSTANCE;
   }
}
