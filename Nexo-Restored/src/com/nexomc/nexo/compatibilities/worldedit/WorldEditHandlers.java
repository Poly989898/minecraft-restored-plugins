package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.configs.Settings;
import com.sk89q.worldedit.EditSession.Stage;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/WorldEditHandlers;", "", "<init>", "()V", "onEditSession", "", "Lcom/sk89q/worldedit/event/extent/EditSessionEvent;", "Lcom/sk89q/worldedit/util/eventbus/Subscribe;", "com.nexomc:core"}
)
public final class WorldEditHandlers {
   @Subscribe
   public final void onEditSession(@NotNull EditSessionEvent $this$onEditSession) {
      Intrinsics.checkNotNullParameter($this$onEditSession, "<this>");
      if (Settings.WORLDEDIT_FURNITURE.toBool() || Settings.WORLDEDIT_CUSTOM_BLOCKS.toBool()) {
         World var10000 = $this$onEditSession.getWorld();
         if (var10000 != null) {
            org.bukkit.World var3 = BukkitAdapter.adapt(var10000);
            if (var3 != null) {
               org.bukkit.World world = var3;
               if ($this$onEditSession.getStage() == Stage.BEFORE_CHANGE) {
                  if (WrappedWorldEdit.INSTANCE.isFaweEnabled() && Settings.FAWE_HOOK.toBool()) {
                     $this$onEditSession.getExtent().addPostProcessor(new NexoCustomBlocksProcessor(world));
                  } else {
                     Extent var10003 = $this$onEditSession.getExtent();
                     Intrinsics.checkNotNullExpressionValue(var10003, "getExtent(...)");
                     $this$onEditSession.setExtent((Extent)(new NexoWorldEditExtent(var10003, world)));
                  }
               }

            }
         }
      }
   }
}
