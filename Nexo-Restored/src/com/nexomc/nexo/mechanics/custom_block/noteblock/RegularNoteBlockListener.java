package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.destroystokyo.paper.MaterialSetTag;
import com.nexomc.nexo.api.NexoBlocks;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u000e\u001a\u00020\u0005*\u00020\u000fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/RegularNoteBlockListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onNotePlay", "", "Lorg/bukkit/event/block/NotePlayEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onNoteBlockPower", "Lorg/bukkit/event/block/BlockPhysicsEvent;", "onRightClickNoteBlock", "Lorg/bukkit/event/player/PlayerInteractEvent;", "com.nexomc:core"}
)
public final class RegularNoteBlockListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onNotePlay(@NotNull NotePlayEvent notePlayEvent) {
      notePlayEvent.setCancelled(true);
      Block block = notePlayEvent.getBlock();
      if (!NexoBlocks.isNexoNoteBlock(block)) {
         Block block2 = notePlayEvent.getBlock();
         RegularNoteBlock regularNoteBlock = new RegularNoteBlock(block2, (Player)null);
         regularNoteBlock.runClickAction(Action.LEFT_CLICK_BLOCK);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onNoteBlockPower(@NotNull BlockPhysicsEvent blockPhysicsEvent) {
      if (blockPhysicsEvent.getBlock().getType() == Material.NOTE_BLOCK) {
         Block block = blockPhysicsEvent.getBlock();
         if (!NexoBlocks.isNexoNoteBlock(block)) {
            Block block2 = blockPhysicsEvent.getBlock();
            RegularNoteBlock regularNoteBlock = new RegularNoteBlock(block2, (Player)null);
            if (!blockPhysicsEvent.getBlock().isBlockIndirectlyPowered()) {
               regularNoteBlock.setPowered(false);
            } else if (!regularNoteBlock.isPowered()) {
               regularNoteBlock.playSoundNaturally();
               regularNoteBlock.setPowered(true);
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onRightClickNoteBlock(@NotNull PlayerInteractEvent playerInteractEvent) {
      if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Block clickedBlock = playerInteractEvent.getClickedBlock();
         if (clickedBlock != null) {
            Block it = clickedBlock;
            int value = 0;
            clickedBlock = it.getType() == Material.NOTE_BLOCK ? it : null;
            if (clickedBlock != null) {
               Block it_2 = clickedBlock;
               NexoBlocks nexoBlocks = NexoBlocks.INSTANCE;
               int value2 = 0;
               Block block2 = !NexoBlocks.isNexoNoteBlock(it_2) ? it_2 : null;
               if (block2 != null) {
                  Block block = block2;
                  PlayerInventory it_1 = playerInteractEvent.getPlayer().getInventory();
                  int value3 = 0;
                  Pair pair = TuplesKt.to(it_1.getItemInMainHand(), it_1.getItemInOffHand());
                  Object component1 = pair.component1();
                  ItemStack mainHandItem = (ItemStack)component1;
                  component1 = pair.component2();
                  ItemStack offHandItem = (ItemStack)component1;
                  if (MaterialSetTag.ITEMS_SKULLS.getValues().contains(mainHandItem.getType()) && playerInteractEvent.getBlockFace() == BlockFace.UP) {
                     return;
                  }

                  RegularNoteBlock regularNoteBlock = new RegularNoteBlock(block, playerInteractEvent.getPlayer());
                  if (!playerInteractEvent.getPlayer().isSneaking() || mainHandItem.isEmpty() && offHandItem.isEmpty()) {
                     playerInteractEvent.setUseInteractedBlock(Result.DENY);
                     regularNoteBlock.runClickAction(Action.RIGHT_CLICK_BLOCK);
                     return;
                  }

                  return;
               }
            }
         }

      }
   }
}
