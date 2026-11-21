package com.nexomc.customblockdata.events;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\t¨\u0006\n"},
   d2 = {"Lcom/nexomc/customblockdata/events/CustomBlockDataRemoveEvent;", "Lcom/nexomc/customblockdata/events/CustomBlockDataEvent;", "plugin", "Lorg/bukkit/plugin/Plugin;", "block", "Lorg/bukkit/block/Block;", "bukkitEvent", "Lorg/bukkit/event/Event;", "<init>", "(Lorg/bukkit/plugin/Plugin;Lorg/bukkit/block/Block;Lorg/bukkit/event/Event;)V", "custom-block-data"}
)
public final class CustomBlockDataRemoveEvent extends CustomBlockDataEvent {
   public CustomBlockDataRemoveEvent(@NotNull Plugin plugin, @NotNull Block block, @Nullable Event bukkitEvent) {
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      Intrinsics.checkNotNullParameter(block, "block");
      Intrinsics.checkNotNull(bukkitEvent);
      super(plugin, block, bukkitEvent);
   }
}
