package com.nexomc.nexo.mechanics.custom_block.stringblock;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import kotlin.Metadata;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/StringBlockMechanicPaperListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onEnteringTripwire", "", "Lio/papermc/paper/event/entity/EntityInsideBlockEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "MONITOR", "ignoreCancelled", "com.nexomc:core"}
)
public final class StringBlockMechanicPaperListener implements Listener {
   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onEnteringTripwire(@NotNull EntityInsideBlockEvent entityInsideBlockEvent) {
      if (entityInsideBlockEvent.getBlock().getType() == Material.TRIPWIRE) {
         entityInsideBlockEvent.setCancelled(true);
      }

   }
}
