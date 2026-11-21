package com.nexomc.nexo.api.events.custom_block.stringblock;

import com.nexomc.nexo.api.events.custom_block.NexoBlockPlaceEvent;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockPlaceEvent;", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockPlaceEvent;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "com.nexomc:core"}
)
public final class NexoStringBlockPlaceEvent extends NexoBlockPlaceEvent {
   @NotNull
   private final StringBlockMechanic mechanic;

   public NexoStringBlockPlaceEvent(@NotNull StringBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull ItemStack itemInHand, @NotNull EquipmentSlot hand) {
      super(mechanic, block, player, itemInHand, hand);
      this.mechanic = mechanic;
   }

   @NotNull
   public StringBlockMechanic getMechanic() {
      return this.mechanic;
   }
}
