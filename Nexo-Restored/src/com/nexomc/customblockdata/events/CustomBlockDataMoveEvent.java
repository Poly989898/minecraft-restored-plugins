package com.nexomc.customblockdata.events;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\r"},
   d2 = {"Lcom/nexomc/customblockdata/events/CustomBlockDataMoveEvent;", "Lcom/nexomc/customblockdata/events/CustomBlockDataEvent;", "plugin", "Lorg/bukkit/plugin/Plugin;", "blockFrom", "Lorg/bukkit/block/Block;", "blockTo", "bukkitEvent", "Lorg/bukkit/event/Event;", "<init>", "(Lorg/bukkit/plugin/Plugin;Lorg/bukkit/block/Block;Lorg/bukkit/block/Block;Lorg/bukkit/event/Event;)V", "getBlockTo", "()Lorg/bukkit/block/Block;", "custom-block-data"}
)
public final class CustomBlockDataMoveEvent extends CustomBlockDataEvent {
   @NotNull
   private final Block blockTo;

   public CustomBlockDataMoveEvent(@NotNull Plugin plugin, @NotNull Block blockFrom, @NotNull Block blockTo, @NotNull Event bukkitEvent) {
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      Intrinsics.checkNotNullParameter(blockFrom, "blockFrom");
      Intrinsics.checkNotNullParameter(blockTo, "blockTo");
      Intrinsics.checkNotNullParameter(bukkitEvent, "bukkitEvent");
      super(plugin, blockFrom, bukkitEvent);
      this.blockTo = blockTo;
   }

   @NotNull
   public final Block getBlockTo() {
      return this.blockTo;
   }
}
