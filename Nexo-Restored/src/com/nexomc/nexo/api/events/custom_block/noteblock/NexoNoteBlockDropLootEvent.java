package com.nexomc.nexo.api.events.custom_block.noteblock;

import com.nexomc.nexo.api.events.custom_block.NexoCustomBlockDropLootEvent;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.utils.drops.DroppedLoot;
import java.util.List;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0004\b\u000b\u0010\fR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/noteblock/NexoNoteBlockDropLootEvent;", "Lcom/nexomc/nexo/api/events/custom_block/NexoCustomBlockDropLootEvent;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "loots", "", "Lcom/nexomc/nexo/utils/drops/DroppedLoot;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Ljava/util/List;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "com.nexomc:core"}
)
public final class NexoNoteBlockDropLootEvent extends NexoCustomBlockDropLootEvent {
   @NotNull
   private final NoteBlockMechanic mechanic;

   public NexoNoteBlockDropLootEvent(@NotNull NoteBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull List<DroppedLoot> loots) {
      super(mechanic, block, player, loots);
      this.mechanic = mechanic;
   }

   @NotNull
   public NoteBlockMechanic getMechanic() {
      return this.mechanic;
   }
}
