package com.nexomc.nexo.mechanics.custom_block.noteblock.beacon;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/beacon/BeaconListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "beaconEffect", "", "Lcom/destroystokyo/paper/event/block/BeaconEffectEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "Companion", "com.nexomc:core"}
)
public final class BeaconListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void beaconEffect(@NotNull BeaconEffectEvent beaconEffectEvent) {
      Companion companion = Companion;
      Block block = beaconEffectEvent.getBlock();
      if (!companion.checkBeaconPyramid(block)) {
         beaconEffectEvent.setCancelled(true);
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/beacon/BeaconListener$Companion;", "", "<init>", "()V", "checkBeaconPyramid", "", "block", "Lorg/bukkit/block/Block;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final boolean checkBeaconPyramid(Block block) {
         BlockState state = block.getState();
         Beacon beacon2 = state instanceof Beacon ? (Beacon)state : null;
         if ((state instanceof Beacon ? (Beacon)state : null) == null) {
            return false;
         } else {
            Beacon beacon = beacon2;
            Pair pair = TuplesKt.to(block.getX(), block.getY());
            int x = ((Number)pair.component1()).intValue();
            int y = ((Number)pair.component2()).intValue();
            Pair pair2 = TuplesKt.to(block.getZ(), block.getWorld());
            int z = ((Number)pair2.component1()).intValue();
            Object component2 = pair2.component2();
            World world = (World)component2;
            boolean validPyramid = true;
            int tier = 1;
            int value = beacon.getTier();
            if (tier <= value) {
               while(true) {
                  int tierY = y - tier;
                  if (tierY < world.getMinHeight()) {
                     break;
                  }

                  boolean validTier = true;
                  int tierX = x - tier;
                  int value2 = x + tier;
                  if (tierX <= value2) {
                     while(true) {
                        int tierZ = z - tier;
                        int value3 = z + tier;
                        if (tierZ <= value3) {
                           while(true) {
                              Location tierBlockLoc = new Location(world, (double)tierX, (double)tierY, (double)tierZ);
                              BlockData blockData2 = world.getBlockData(tierBlockLoc);
                              BlockData blockData = blockData2;
                              if (!Tag.BEACON_BASE_BLOCKS.isTagged(blockData.getMaterial())) {
                                 validTier = false;
                                 break;
                              }

                              NoteBlockMechanic mechanic = NexoBlocks.noteBlockMechanic(blockData);
                              if (mechanic != null && !mechanic.isBeaconBaseBlock()) {
                                 validTier = false;
                                 break;
                              }

                              if (tierZ == value3) {
                                 break;
                              }

                              ++tierZ;
                           }
                        }

                        if (tierX == value2) {
                           break;
                        }

                        ++tierX;
                     }
                  }

                  if (!validTier) {
                     validPyramid = false;
                     break;
                  }

                  if (tier == value) {
                     break;
                  }

                  ++tier;
               }
            }

            return validPyramid;
         }
      }
   }
}
