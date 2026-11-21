package com.nexomc.nexo.mechanics.custom_block.noteblock.logstrip;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalLog;
import com.nexomc.nexo.mechanics.misc.misc.MiscMechanic;
import com.nexomc.nexo.mechanics.misc.misc.MiscMechanicFactory;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/logstrip/LogStripListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onStrippingLog", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "canStripLog", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class LogStripListener implements Listener {
   @EventHandler
   public final void onStrippingLog(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock directionalBlock;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block block = clickedBlock;
         ItemStack itemInMainHand = playerInteractEvent.getPlayer().getInventory().getItemInMainHand();
         ItemStack item = itemInMainHand;
         if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.NOTE_BLOCK && this.canStripLog(item)) {
            NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(block);
            if (noteBlockMechanic != null) {
               NoteBlockMechanic mechanic = noteBlockMechanic;
               LogStripping logStripping = mechanic.log();
               if (logStripping != null) {
                  LogStripping it = logStripping;
                  int value = 0;
                  logStripping = it.canBeStripped() ? it : null;
                  if (logStripping != null) {
                     LogStripping log = logStripping;
                     if (log.hasStrippedDrop()) {
                        playerInteractEvent.getPlayer().getWorld().dropItemNaturally(block.getRelative(playerInteractEvent.getPlayer().getFacing().getOppositeFace()).getLocation().toCenterLocation(), log.getLogDrop());
                     }

                     if (log.getDecreaseAxeDurability() && playerInteractEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        item.damage(1, (LivingEntity)playerInteractEvent.getPlayer());
                     }

                     NoteBlockMechanic strip = log.getStripMechanic();
                     if (strip != null) {
                        block: {
                           int value2 = 0;
                           DirectionalBlock directionalBlock2 = mechanic.getDirectional();
                           if (directionalBlock2 != null) {
                              NoteBlockMechanic parentMechanic = directionalBlock2.getParentMechanic();
                              if (parentMechanic != null) {
                                 directionalBlock = parentMechanic.getDirectional();
                                 break block;
                              }
                           }

                           directionalBlock = null;
                        }

                        DirectionalBlock directionalBlock3 = directionalBlock;
                        DirectionalLog directionalLog = directionalBlock3 instanceof DirectionalLog ? (DirectionalLog)directionalBlock3 : null;
                        NoteBlockMechanic noteBlockMechanic2;
                        if ((directionalBlock3 instanceof DirectionalLog ? (DirectionalLog)directionalBlock3 : null) == null) {
                           noteBlockMechanic2 = strip;
                        } else {
                           DirectionalLog parent = directionalLog;
                           DirectionalBlock directionalBlock4 = strip.getDirectional();
                           DirectionalLog directionalLog2 = directionalBlock4 instanceof DirectionalLog ? (DirectionalLog)directionalBlock4 : null;
                           if ((directionalBlock4 instanceof DirectionalLog ? (DirectionalLog)directionalBlock4 : null) == null) {
                              noteBlockMechanic2 = strip;
                           } else {
                              DirectionalLog directional = directionalLog2;
                              String itemID = mechanic.getItemID();
                              if (Intrinsics.areEqual(itemID, parent.getXBlock())) {
                                 noteBlockMechanic2 = NexoBlocks.noteBlockMechanic(directional.getXBlock());
                                 if (noteBlockMechanic2 == null) {
                                    noteBlockMechanic2 = strip;
                                 }
                              } else if (Intrinsics.areEqual(itemID, parent.getYBlock())) {
                                 noteBlockMechanic2 = NexoBlocks.noteBlockMechanic(directional.getYBlock());
                                 if (noteBlockMechanic2 == null) {
                                    noteBlockMechanic2 = strip;
                                 }
                              } else if (Intrinsics.areEqual(itemID, parent.getZBlock())) {
                                 noteBlockMechanic2 = NexoBlocks.noteBlockMechanic(directional.getZBlock());
                                 if (noteBlockMechanic2 == null) {
                                    noteBlockMechanic2 = strip;
                                 }
                              } else {
                                 noteBlockMechanic2 = strip;
                              }
                           }
                        }

                        NoteBlockMechanic it_2 = noteBlockMechanic2;
                        if (it_2 != null) {
                           NoteBlock it_1 = it_2.getBlockData();
                           if (it_1 != null) {
                              block.setBlockData((BlockData)it_1);
                              playerInteractEvent.getPlayer().playSound(block.getLocation(), Sound.ITEM_AXE_STRIP, 1.0F, 0.8F);
                              return;
                           }
                        }
                     }

                     return;
                  }
               }

            }
         }
      }
   }

   private final boolean canStripLog(ItemStack itemStack) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      boolean flag;
      if (!StringsKt.endsWith(itemStack.getType().name(), "_AXE", false)) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof MiscMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (MiscMechanicFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         block: {
            MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
            if (miscMechanicFactory != null) {
               MiscMechanic mechanic = miscMechanicFactory.getMechanic(NexoItems.idFromItem(itemStack));
               if (mechanic != null) {
                  flag = mechanic.getCanStripLogs();
                  break block;
               }
            }

            flag = false;
         }

         if (!flag) {
            flag = false;
            return flag;
         }
      }

      flag = true;
      return flag;
   }
}
