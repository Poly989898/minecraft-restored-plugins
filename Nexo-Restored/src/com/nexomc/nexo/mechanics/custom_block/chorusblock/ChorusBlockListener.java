package com.nexomc.nexo.mechanics.custom_block.chorusblock;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J&\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0018\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\f\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onFallingBlockLandOnCarpet", "", "Lcom/destroystokyo/paper/event/entity/EntityRemoveFromWorldEvent;", "Lorg/bukkit/event/EventHandler;", "onFlow", "Lorg/bukkit/event/block/BlockFromToEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "com.nexomc:core"}
)
public final class ChorusBlockListener implements Listener {
   @EventHandler
   public final void onFallingBlockLandOnCarpet(@NotNull EntityRemoveFromWorldEvent entityRemoveFromWorldEvent) {
      Entity entity = entityRemoveFromWorldEvent.getEntity();
      FallingBlock fallingBlock2 = entity instanceof FallingBlock ? (FallingBlock)entity : null;
      if ((entity instanceof FallingBlock ? (FallingBlock)entity : null) != null) {
         FallingBlock fallingBlock = fallingBlock2;
         ChorusBlockMechanic chorusBlockMechanic = NexoBlocks.chorusBlockMechanic(fallingBlock.getBlockData());
         if (chorusBlockMechanic != null) {
            ChorusBlockMechanic mechanic = chorusBlockMechanic;
            Location location = fallingBlock.getLocation();
            if (!Intrinsics.areEqual(NexoBlocks.customBlockMechanic(location), mechanic)) {
               ItemBuilder itemBuilder = NexoItems.itemFromId(mechanic.getItemID());
               ItemStack itemStack = itemBuilder.build();
               fallingBlock.setDropItem(false);
               fallingBlock.getWorld().dropItemNaturally(fallingBlock.getLocation().toCenterLocation().subtract((double)0.0F, (double)0.25F, (double)0.0F), itemStack);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onFlow(@NotNull BlockFromToEvent blockFromToEvent) {
      Block toBlock = blockFromToEvent.getToBlock();
      if (NexoBlocks.isNexoChorusBlock(toBlock)) {
         blockFromToEvent.setCancelled(true);
      }

   }
}
