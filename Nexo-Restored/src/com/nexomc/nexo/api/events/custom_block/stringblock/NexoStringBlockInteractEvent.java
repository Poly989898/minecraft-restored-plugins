package com.nexomc.nexo.api.events.custom_block.stringblock;

import com.nexomc.nexo.api.events.custom_block.NexoBlockInteractEvent;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockInteractEvent;", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockInteractEvent;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "block", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "action", "Lorg/bukkit/event/block/Action;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;Lorg/bukkit/block/Block;Lorg/bukkit/block/BlockFace;Lorg/bukkit/event/block/Action;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "com.nexomc:core"}
)
public final class NexoStringBlockInteractEvent extends NexoBlockInteractEvent {
   @NotNull
   private final StringBlockMechanic mechanic;

   public NexoStringBlockInteractEvent(@NotNull StringBlockMechanic mechanic, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @NotNull Block block, @NotNull BlockFace blockFace, @NotNull Action action) {
      super(mechanic, player, itemInHand, hand, block, blockFace, action);
      this.mechanic = mechanic;
   }

   @NotNull
   public StringBlockMechanic getMechanic() {
      return this.mechanic;
   }
}
