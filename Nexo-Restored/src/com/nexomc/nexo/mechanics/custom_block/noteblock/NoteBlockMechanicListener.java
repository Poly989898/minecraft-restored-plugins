package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockHelpers;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.nms.IBlockUtils;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.EventUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000eJ\u001c\u0010\u000f\u001a\u00020\u0005*\u00020\u0010H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ\u0010\u0010\u0011\u001a\u00020\u0005*\u00020\u0012H\u0007b\u0002\b\u0007J&\u0010\u0013\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000e\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0016\u001a\u00020\u0005*\u00020\u0017H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u001a\u001a\u00020\u0005*\u00020\u001bH\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onPlaceAgainstNoteBlock", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onNotePlayed", "Lorg/bukkit/event/block/NotePlayEvent;", "NORMAL", "onFallingNexoBlock", "Lorg/bukkit/event/entity/EntityChangeBlockEvent;", "onBreakBeneathFallingNexoBlock", "Lorg/bukkit/event/block/BlockBreakEvent;", "onSetFire", "onCatchFire", "Lorg/bukkit/event/block/BlockIgniteEvent;", "onPistonPush", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "onPistonPull", "Lorg/bukkit/event/block/BlockPistonRetractEvent;", "onFallingBlockLandOnCarpet", "Lcom/destroystokyo/paper/event/entity/EntityRemoveFromWorldEvent;", "com.nexomc:core"}
)
public final class NoteBlockMechanicListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPlaceAgainstNoteBlock(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block it = clickedBlock;
         int value = 0;
         clickedBlock = it.getType() == Material.NOTE_BLOCK ? it : null;
         if (clickedBlock != null) {
            EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
            if (equipmentSlot == null) {
               return;
            }

            Pair pair = TuplesKt.to(clickedBlock, equipmentSlot);
            Block block = (Block)pair.component1();
            EquipmentSlot hand = (EquipmentSlot)pair.component2();
            if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
               return;
            }

            if (!playerInteractEvent.getPlayer().isSneaking() && BlockHelpers.isInteractable(block, (Player)null)) {
               return;
            }

            if (playerInteractEvent.useInteractedBlock() != Result.DENY && NexoBlocks.isNexoNoteBlock(block)) {
               ItemStack item = playerInteractEvent.getItem();
               if (item != null) {
                  Material material = item.getType();
                  if (material != null) {
                     Material it_4 = material;
                     int value2 = 0;
                     material = !it_4.isAir() && !NexoBlocks.isNexoNoteBlock(playerInteractEvent.getItem()) ? it_4 : null;
                     if (material != null) {
                        Material type = material;
                        playerInteractEvent.setUseInteractedBlock(Result.DENY);
                        BlockData newData = type.isBlock() ? type.createBlockData() : null;
                        CustomBlockHelpers customBlockHelpers = CustomBlockHelpers.INSTANCE;
                        Player player = playerInteractEvent.getPlayer();
                        ItemStack itemStack = playerInteractEvent.getItem();
                        BlockFace blockFace = playerInteractEvent.getBlockFace();
                        customBlockHelpers.makePlayerPlaceBlock(player, hand, itemStack, block, blockFace, (CustomBlockMechanic)null, newData);
                        return;
                     }
                  }
               }

               return;
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public final void onNotePlayed(@NotNull NotePlayEvent notePlayEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Block block = notePlayEvent.getBlock();
      if (NexoBlocks.isNexoNoteBlock(block)) {
         notePlayEvent.setCancelled(true);
      } else {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof NoteBlockMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (NoteBlockMechanicFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         if ((NoteBlockMechanicFactory)mechanicFactory != null ? ((NoteBlockMechanicFactory)mechanicFactory).getReimplementNoteblockFeatures() : false) {
            IBlockUtils blockUtils = NMSHandlers.handler().blockUtils();
            Block relative = notePlayEvent.getBlock().getRelative(BlockFace.DOWN);
            Instrument instrument = blockUtils.instrumentForBlock(relative);
            if (instrument == null) {
               instrument = Instrument.PIANO;
            }

            notePlayEvent.setInstrument(instrument);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onFallingNexoBlock(@NotNull EntityChangeBlockEvent entityChangeBlockEvent) {
      Entity entity = entityChangeBlockEvent.getEntity();
      FallingBlock fallingBlock2 = entity instanceof FallingBlock ? (FallingBlock)entity : null;
      if ((entity instanceof FallingBlock ? (FallingBlock)entity : null) != null) {
         FallingBlock fallingBlock = fallingBlock2;
         NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(fallingBlock.getBlockData());
         if (noteBlockMechanic != null) {
            NoteBlockMechanic mechanic = noteBlockMechanic;
            String itemID = mechanic.getItemID();
            Location location = entityChangeBlockEvent.getBlock().getLocation();
            NexoBlocks.place(itemID, location);
            fallingBlock.setDropItem(false);
         }
      }
   }

   @EventHandler
   public final void onBreakBeneathFallingNexoBlock(@NotNull BlockBreakEvent blockBreakEvent) {
      NoteMechanicHelpers noteMechanicHelpers = NoteMechanicHelpers.INSTANCE;
      Block block = blockBreakEvent.getBlock();
      noteMechanicHelpers.handleFallingNexoBlockAbove(block);
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onSetFire(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block it = clickedBlock;
         int value = 0;
         clickedBlock = it.getType() == Material.NOTE_BLOCK ? it : null;
         if (clickedBlock != null) {
            ItemStack itemStack = playerInteractEvent.getItem();
            if (itemStack == null) {
               return;
            }

            Pair pair = TuplesKt.to(clickedBlock, itemStack);
            Block block = (Block)pair.component1();
            ItemStack item = (ItemStack)pair.component2();
            if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getBlockFace() == BlockFace.UP) {
               NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(block);
               if (noteBlockMechanic == null) {
                  return;
               }

               NoteBlockMechanic mechanic = noteBlockMechanic;
               if (!mechanic.canIgnite()) {
                  return;
               }

               if (item.getType() != Material.FLINT_AND_STEEL && item.getType() != Material.FIRE_CHARGE) {
                  return;
               }

               EventUtils.INSTANCE.call((Event)(new BlockIgniteEvent(block, IgniteCause.FLINT_AND_STEEL, (Entity)playerInteractEvent.getPlayer())));
               return;
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onCatchFire(@NotNull BlockIgniteEvent blockIgniteEvent) {
      Block block = blockIgniteEvent.getBlock();
      NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(block);
      if (noteBlockMechanic != null) {
         NoteBlockMechanic mechanic = noteBlockMechanic;
         if (!mechanic.canIgnite()) {
            blockIgniteEvent.setCancelled(true);
         } else {
            blockIgniteEvent.getBlock().getWorld().playSound(blockIgniteEvent.getBlock().getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
            blockIgniteEvent.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPush(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks, 10)));

      for(Object item : blocks) {
         Block p0 = (Block)item;
         int value = 0;
         destination.add(p0.getLocation());
      }

      blocks = (Iterable)((List)destination);
      NoteMechanicHelpers noteMechanicHelpers = NoteMechanicHelpers.INSTANCE;

      for(Object element : blocks) {
         Location p0 = (Location)element;
         int value2 = 0;
         NoteMechanicHelpers.checkNoteBlockAbove(p0);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPull(@NotNull BlockPistonRetractEvent blockPistonRetractEvent) {
      Iterable blocks = (Iterable)blockPistonRetractEvent.getBlocks();
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks, 10)));

      for(Object item : blocks) {
         Block it = (Block)item;
         int value = 0;
         destination.add(it.getRelative(blockPistonRetractEvent.getDirection()).getLocation());
      }

      blocks = (Iterable)((List)destination);
      NoteMechanicHelpers noteMechanicHelpers = NoteMechanicHelpers.INSTANCE;

      for(Object element : blocks) {
         Location p0 = (Location)element;
         int value2 = 0;
         NoteMechanicHelpers.checkNoteBlockAbove(p0);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onFallingBlockLandOnCarpet(@NotNull EntityRemoveFromWorldEvent entityRemoveFromWorldEvent) {
      Entity entity = entityRemoveFromWorldEvent.getEntity();
      FallingBlock fallingBlock2 = entity instanceof FallingBlock ? (FallingBlock)entity : null;
      if ((entity instanceof FallingBlock ? (FallingBlock)entity : null) != null) {
         FallingBlock it = fallingBlock2;
         FallingBlock it_3 = it;
         fallingBlock2 = it_3.getPersistentDataContainer().has(NoteBlockMechanic.Companion.getFALLING_KEY()) ? it_3 : null;
         if (fallingBlock2 != null) {
            FallingBlock fallingBlock = fallingBlock2;
            NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(fallingBlock.getBlockData());
            if (noteBlockMechanic != null) {
               block: {
                  NoteBlockMechanic it_2 = noteBlockMechanic;
                  int value = 0;
                  DirectionalBlock directional = it_2.getDirectional();
                  if (directional != null) {
                     noteBlockMechanic = directional.getParentMechanic();
                     if (noteBlockMechanic != null) {
                        break block;
                     }
                  }

                  noteBlockMechanic = it_2;
               }

               if (noteBlockMechanic != null) {
                  NoteBlockMechanic it_1 = noteBlockMechanic;
                  int value2 = 0;
                  noteBlockMechanic = it_1.isFalling() ? it_1 : null;
                  if (noteBlockMechanic != null) {
                     NoteBlockMechanic mechanic = noteBlockMechanic;
                     Location location2 = fallingBlock.getLocation();
                     Location location = location2;
                     Block block = location.getBlock();
                     if (Intrinsics.areEqual(NexoBlocks.noteBlockMechanic(block), mechanic)) {
                        return;
                     }

                     ItemBuilder itemBuilder = NexoItems.itemFromId(mechanic.getItemID());
                     ItemStack itemStack = itemBuilder.build();
                     fallingBlock.setDropItem(false);
                     fallingBlock.setCancelDrop(true);
                     fallingBlock.getWorld().dropItemNaturally(location.toCenterLocation().subtract((double)0.0F, (double)0.25F, (double)0.0F), itemStack);
                     return;
                  }
               }
            }

            return;
         }
      }

   }
}
