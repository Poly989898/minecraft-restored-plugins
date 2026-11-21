package com.nexomc.nexo.api.events.custom_block.stringblock;

import com.nexomc.nexo.api.events.custom_block.NexoBlockDamageEvent;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockDamageEvent;", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockDamageEvent;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanic;", "com.nexomc:core"}
)
public final class NexoStringBlockDamageEvent extends NexoBlockDamageEvent {
   @NotNull
   private final StringBlockMechanic mechanic;

   public NexoStringBlockDamageEvent(@NotNull StringBlockMechanic mechanic, @NotNull Block block, @NotNull Player player) {
      super(mechanic, block, player);
      this.mechanic = mechanic;
   }

   @NotNull
   public StringBlockMechanic getMechanic() {
      return this.mechanic;
   }
}
