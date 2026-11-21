package com.nexomc.nexo.api.events.custom_block.chorusblock;

import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.utils.drops.Drop;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tB)\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\b\u0010\fR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/chorusblock/NexoChorusBlockBreakEvent;", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockBreakEvent;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;)V", "drop", "Lcom/nexomc/nexo/utils/drops/Drop;", "(Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/utils/drops/Drop;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "com.nexomc:core"}
)
public final class NexoChorusBlockBreakEvent extends NexoBlockBreakEvent {
   @NotNull
   private final ChorusBlockMechanic mechanic;

   public NexoChorusBlockBreakEvent(@NotNull ChorusBlockMechanic mechanic, @NotNull Block block, @NotNull Player player) {
      super(mechanic, block, player);
      this.mechanic = mechanic;
      this.setDrop(this.getMechanic().getBreakable().getDrop());
   }

   @NotNull
   public ChorusBlockMechanic getMechanic() {
      return this.mechanic;
   }

   public NexoChorusBlockBreakEvent(@NotNull ChorusBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull Drop drop) {
      this(mechanic, block, player);
      this.setDrop(drop);
   }
}
