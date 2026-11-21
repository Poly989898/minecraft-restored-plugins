package com.nexomc.nexo.mechanics.furniture.listeners;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.blocksounds.BlockSoundTask;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import com.nexomc.protectionlib.ProtectionLib;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J&\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0018\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\f\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u000e\u001a\u00020\u0005*\u00020\u000fH\u0007b\u000e\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u0010J&\u0010\u0011\u001a\u00020\u0005*\u00020\u0012H\u0007b\u0018\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\f\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u0013\u001a\u00020\u0005*\u00020\u0014H\u0007b\u000e\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\fJ\u001c\u0010\u0015\u001a\u00020\u0005*\u00020\u0016H\u0007b\u000e\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u0017J&\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0018\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u001a\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001b\u001a\u00020\u0005*\u00020\u001cH\u0007b\u0018\b\u0007\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u001a\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureSoundListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onWorldUnload", "", "Lorg/bukkit/event/world/WorldUnloadEvent;", "Lorg/bukkit/event/EventHandler;", "onPlacingStone", "Lorg/bukkit/event/block/BlockPlaceEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onBreakingStone", "Lorg/bukkit/event/block/BlockBreakEvent;", "HIGH", "onHitStone", "Lorg/bukkit/event/block/BlockDamageEvent;", "onStopHittingStone", "Lorg/bukkit/event/block/BlockDamageAbortEvent;", "onStepFall", "Lorg/bukkit/event/world/GenericGameEvent;", "LOWEST", "onPlacingFurniture", "Lcom/nexomc/nexo/api/events/furniture/NexoFurniturePlaceEvent;", "MONITOR", "onBreakingFurniture", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureBreakEvent;", "Companion", "com.nexomc:core"}
)
public final class FurnitureSoundListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Map<Location, BlockSoundTask> breakerPlaySound;

   @EventHandler
   public final void onWorldUnload(@NotNull WorldUnloadEvent worldUnloadEvent) {

      for(Map.Entry entry : breakerPlaySound.entrySet()) {
         Location location = (Location)entry.getKey();
         BlockSoundTask task = (BlockSoundTask)entry.getValue();
         if (!BlockHelpers.INSTANCE.isLoaded(location) && !task.isCancelled()) {
            task.cancel();
            breakerPlaySound.remove(location);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPlacingStone(@NotNull BlockPlaceEvent blockPlaceEvent) {
      Block blockPlaced = blockPlaceEvent.getBlockPlaced();
      if (!NexoBlocks.isNexoStringBlock(blockPlaced) && !blockPlaceEvent.getBlockPlaced().isEmpty()) {
         if (Intrinsics.areEqual(blockPlaceEvent.getBlock().getBlockSoundGroup().getPlaceSound(), Sound.BLOCK_STONE_PLACE)) {
            Location location = blockPlaceEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, "nexo:block.stone.place", 1.0F, 0.8F);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void onBreakingStone(@NotNull BlockBreakEvent blockBreakEvent) {
      Location location2 = blockBreakEvent.getBlock().getLocation();
      Location location = location2;
      Block relative = blockBreakEvent.getBlock().getRelative(BlockFace.DOWN);
      StringBlockMechanic mechanicBelow = NexoBlocks.stringMechanic(relative);
      BlockSoundTask blockSoundTask = (BlockSoundTask)breakerPlaySound.remove(location);
      if (blockSoundTask != null) {
         blockSoundTask.cancel();
      }

      Block block = blockBreakEvent.getBlock();
      if (!NexoBlocks.isNexoStringBlock(block) && (blockBreakEvent.getBlock().getType() != Material.TRIPWIRE || mechanicBelow == null || !mechanicBelow.getTall())) {
         if (Intrinsics.areEqual(blockBreakEvent.getBlock().getBlockSoundGroup().getBreakSound(), Sound.BLOCK_STONE_BREAK)) {
            if (blockBreakEvent.getBlock().getType() != Material.BARRIER && !blockBreakEvent.getBlock().isEmpty() || !NexoFurniture.isFurniture(location)) {
               if (!blockBreakEvent.isCancelled()) {
                  Player player = blockBreakEvent.getPlayer();
                  if (ProtectionLib.canBreak(player, location)) {
                     BlockHelpers.playCustomBlockSound(location, "nexo:block.stone.break", 1.0F, 0.8F);
                     return;
                  }
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onHitStone(@NotNull BlockDamageEvent blockDamageEvent) {
      Location location2 = blockDamageEvent.getBlock().getLocation();
      Location location = location2;
      SoundGroup blockSoundGroup = blockDamageEvent.getBlock().getBlockSoundGroup();
      SoundGroup soundGroup = blockSoundGroup;
      if (!blockDamageEvent.getInstaBreak() && blockDamageEvent.getBlock().getType() != Material.BARRIER && Intrinsics.areEqual(soundGroup.getHitSound(), Sound.BLOCK_STONE_HIT)) {
         if (!breakerPlaySound.containsKey(location)) {
            BlockSoundTask task = new BlockSoundTask(System.currentTimeMillis(), location, 2L, 4L);
            breakerPlaySound.put(location, task);
            task.runTask((() -> FurnitureSoundListener.onHitStone$lambda$0(location)));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onStopHittingStone(@NotNull BlockDamageAbortEvent blockDamageAbortEvent) {
      BlockSoundTask blockSoundTask = (BlockSoundTask)breakerPlaySound.remove(blockDamageAbortEvent.getBlock().getLocation());
      if (blockSoundTask != null) {
         blockSoundTask.cancel();
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void onStepFall(@NotNull GenericGameEvent genericGameEvent) {
      Entity entity = genericGameEvent.getEntity();
      if (entity != null) {
         Entity it = entity;
         int value = 0;
         BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
         Location location = it.getLocation();
         entity = blockHelpers.isLoaded(location) && !genericGameEvent.isAsynchronous() && !VersionUtil.INSTANCE.isFoliaServer() ? it : null;
      } else {
         entity = null;
      }

      Entity entity2 = entity;
      Player player2 = entity2 instanceof Player ? (Player)entity2 : null;
      if ((entity2 instanceof Player ? (Player)entity2 : null) != null) {
         Player player = player2;
         Block block = BlockHelpers.entityStandingOn((Entity)player);
         if (block != null) {
            Block it = block;
            int value2 = 0;
            block = !it.isEmpty() ? it : null;
            if (block != null) {
               Block blockStandingOn = block;
               Pair pair = TuplesKt.to(player.getLastDamageCause(), blockStandingOn.getBlockSoundGroup());
               EntityDamageEvent cause = (EntityDamageEvent)pair.component1();
               Object component2 = pair.component2();
               SoundGroup soundGroup = (SoundGroup)component2;
               if (genericGameEvent.getEvent() == GameEvent.HIT_GROUND && cause != null && cause.getCause() != DamageCause.FALL) {
                  return;
               }

               if (blockStandingOn.getType() == Material.TRIPWIRE) {
                  return;
               }

               FurnitureMechanic mechanic = NexoFurniture.furnitureMechanic(blockStandingOn.getRelative(BlockFace.UP).getLocation());
               if (!Intrinsics.areEqual(soundGroup.getStepSound(), Sound.BLOCK_STONE_STEP) && mechanic == null) {
                  return;
               }

               if (mechanic != null && !IFurniturePacketManager.Companion.blockIsHitbox(blockStandingOn, (UUID)null, true)) {
                  return;
               }

               Triple triple;
               if (genericGameEvent.getEvent() == GameEvent.STEP) {
                  block: {
                     if (mechanic != null) {
                        BlockSounds blockSounds = mechanic.getBlockSounds();
                        if (blockSounds != null) {
                           BlockSounds it_2 = blockSounds;
                           int value3 = 0;
                           triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to(it_2.getStepSound(), it_2.getStepVolume()), it_2.getStepPitch());
                           if (triple != null) {
                              break block;
                           }
                        }
                     }

                     triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to("nexo:block.stone.step", 0.15F), 1.0F);
                  }
               } else {
                  block: {
                     if (!Intrinsics.areEqual(genericGameEvent.getEvent(), GameEvent.HIT_GROUND)) {
                        return;
                     }

                     if (mechanic != null) {
                        BlockSounds blockSounds2 = mechanic.getBlockSounds();
                        if (blockSounds2 != null) {
                           BlockSounds it_1 = blockSounds2;
                           int value4 = 0;
                           triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to(it_1.getFallSound(), it_1.getFallVolume()), it_1.getFallPitch());
                           if (triple != null) {
                              break block;
                           }
                        }
                     }

                     triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to("nexo:block.stone.fall", 0.5F), 0.75F);
                  }
               }

               Triple triple2 = triple;
               String sound = (String)triple2.component1();
               float volume = ((Number)triple2.component2()).floatValue();
               float pitch = ((Number)triple2.component3()).floatValue();
               BlockHelpers.playCustomBlockSound(player.getLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPlacingFurniture(@NotNull NexoFurniturePlaceEvent nexoFurniturePlaceEvent) {
      BlockSounds blockSounds2 = nexoFurniturePlaceEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasPlaceSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoFurniturePlaceEvent.getBaseEntity().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getPlaceSound(), blockSounds.getPlaceVolume(), blockSounds.getPlacePitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBreakingFurniture(@NotNull NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      BlockSounds blockSounds2 = nexoFurnitureBreakEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasBreakSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoFurnitureBreakEvent.getBaseEntity().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getBreakSound(), blockSounds.getBreakVolume(), blockSounds.getBreakPitch());
            return;
         }
      }

   }

   private static final Unit onHitStone$lambda$0(Location location) {
      BlockHelpers.playCustomBlockSound(location, "nexo:block.stone.hit", 0.25F, 0.5F);
      return Unit.INSTANCE;
   }

   static {
      breakerPlaySound = VersionUtil.INSTANCE.isFoliaServer() ? (Map)(new ConcurrentHashMap()) : (Map)(new Object2ObjectOpenHashMap());
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\b\u001a\u00020\tR\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureSoundListener$Companion;", "", "<init>", "()V", "breakerPlaySound", "", "Lorg/bukkit/Location;", "Lcom/nexomc/nexo/utils/blocksounds/BlockSoundTask;", "reload", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void reload() {
         Map map = FurnitureSoundListener.breakerPlaySound;
         int value = 0;

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            ((BlockSoundTask)element.getValue()).cancel();
         }

         map.clear();
      }
   }
}
