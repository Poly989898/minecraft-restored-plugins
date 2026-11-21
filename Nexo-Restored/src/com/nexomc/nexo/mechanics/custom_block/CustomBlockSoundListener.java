package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockPlaceEvent;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.TuplesKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.blocksounds.BlockSoundTask;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import com.nexomc.protectionlib.ProtectionLib;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
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
   d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 *2\u00020\u0001:\u0001*B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t*\u00020\nH\u0007b\u0002\b\u000bJ&\u0010\f\u001a\u00020\t*\u00020\rH\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0010\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u0012\u001a\u00020\t*\u00020\u0013H\u0007b\u000e\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0014J\u001c\u0010\u0015\u001a\u00020\t*\u00020\u0016H\u0007b\u000e\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0014J\u001c\u0010\u0017\u001a\u00020\t*\u00020\u0018H\u0007b\u000e\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0014J\u001c\u0010\u0019\u001a\u00020\t*\u00020\u001aH\u0007b\u000e\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001bJ&\u0010\u001c\u001a\u00020\t*\u00020\u001dH\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001f\u001a\u00020\t*\u00020 H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001c\u001a\u00020\t*\u00020!H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001f\u001a\u00020\t*\u00020\"H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010#\u001a\u00020\t*\u00020$H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010%\u001a\u00020\t*\u00020&H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001e\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010'\u001a\u00020\t*\u00020(H\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0014\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002J&\u0010)\u001a\u00020\t*\u00020\u001aH\u0007b\u0018\b\u000b\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u001b\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockSoundListener;", "Lorg/bukkit/event/Listener;", "customSounds", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;)V", "getCustomSounds", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockFactory$CustomBlockSounds;", "onWorldUnload", "", "Lorg/bukkit/event/world/WorldUnloadEvent;", "Lorg/bukkit/event/EventHandler;", "onPlacingWood", "Lorg/bukkit/event/block/BlockPlaceEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGH", "ignoreCancelled", "onBreakingWood", "Lorg/bukkit/event/block/BlockBreakEvent;", "HIGHEST", "onHitWood", "Lorg/bukkit/event/block/BlockDamageEvent;", "onStopHittingWood", "Lorg/bukkit/event/block/BlockDamageAbortEvent;", "onStepFall", "Lorg/bukkit/event/world/GenericGameEvent;", "LOWEST", "onPlacing", "Lcom/nexomc/nexo/api/events/custom_block/noteblock/NexoNoteBlockPlaceEvent;", "MONITOR", "onBreaking", "Lcom/nexomc/nexo/api/events/custom_block/noteblock/NexoNoteBlockBreakEvent;", "Lcom/nexomc/nexo/api/events/custom_block/chorusblock/NexoChorusBlockPlaceEvent;", "Lcom/nexomc/nexo/api/events/custom_block/chorusblock/NexoChorusBlockBreakEvent;", "onPlaceString", "Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockPlaceEvent;", "onBreakString", "Lcom/nexomc/nexo/api/events/custom_block/stringblock/NexoStringBlockBreakEvent;", "onPistonPush", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "onStepFallString", "Companion", "com.nexomc:core"}
)
public final class CustomBlockSoundListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final CustomBlockFactory.CustomBlockSounds customSounds;
   @NotNull
   private static final Map<Location, BlockSoundTask> breakerPlaySound;

   public CustomBlockSoundListener(@NotNull CustomBlockFactory.CustomBlockSounds customSounds) {
      super();
      this.customSounds = customSounds;
   }

   @NotNull
   public final CustomBlockFactory.CustomBlockSounds getCustomSounds() {
      return this.customSounds;
   }

   @EventHandler
   public final void onWorldUnload(@NotNull WorldUnloadEvent worldUnloadEvent) {
      Iterable entrySet = (Iterable)breakerPlaySound.entrySet();

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value = 0;
         Location loc = (Location)entry.getKey();
         BlockSoundTask task = (BlockSoundTask)entry.getValue();
         if (!BlockHelpers.INSTANCE.isLoaded(loc) && !task.isCancelled()) {
            task.cancel();
            breakerPlaySound.remove(loc);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onPlacingWood(@NotNull BlockPlaceEvent blockPlaceEvent) {
      if (Intrinsics.areEqual(blockPlaceEvent.getBlockPlaced().getBlockSoundGroup().getPlaceSound(), Sound.BLOCK_WOOD_PLACE)) {
         Block blockPlaced = blockPlaceEvent.getBlockPlaced();
         if (!NexoBlocks.isNexoNoteBlock(blockPlaced)) {
            blockPlaced = blockPlaceEvent.getBlockPlaced();
            if (!NexoBlocks.isNexoChorusBlock(blockPlaced)) {
               Location location = blockPlaceEvent.getBlockPlaced().getLocation();
               BlockHelpers.playCustomBlockSound(location, "nexo:block.wood.place", 1.0F, 0.8F);
               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onBreakingWood(@NotNull BlockBreakEvent blockBreakEvent) {
      Location location2 = blockBreakEvent.getBlock().getLocation();
      Location location = location2;
      BlockSoundTask blockSoundTask = (BlockSoundTask)breakerPlaySound.get(location);
      if (blockSoundTask != null) {
         blockSoundTask.cancel();
      }

      if (Intrinsics.areEqual(blockBreakEvent.getBlock().getBlockSoundGroup().getBreakSound(), Sound.BLOCK_WOOD_BREAK)) {
         Block block = blockBreakEvent.getBlock();
         if (!NexoBlocks.isNexoNoteBlock(block)) {
            block = blockBreakEvent.getBlock();
            if (!NexoBlocks.isNexoChorusBlock(block)) {
               if (!blockBreakEvent.isCancelled()) {
                  Player player = blockBreakEvent.getPlayer();
                  if (ProtectionLib.canBreak(player, location)) {
                     BlockHelpers.playCustomBlockSound(location, "nexo:block.wood.break", 1.0F, 0.8F);
                     return;
                  }
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onHitWood(@NotNull BlockDamageEvent blockDamageEvent) {
      java.lang.String str;
      if (Intrinsics.areEqual(blockDamageEvent.getBlock().getBlockSoundGroup().getHitSound(), Sound.BLOCK_WOOD_HIT)) {
         Location it = blockDamageEvent.getBlock().getLocation();
         int value = 0;
         Location location2 = !breakerPlaySound.containsKey(it) ? it : null;
         if (location2 != null) {
            Location location;
            BlockSounds blockSounds;
            block: {
               location = location2;
               Block block = blockDamageEvent.getBlock();
               CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(block);
               blockSounds = customBlockMechanic != null ? customBlockMechanic.getBlockSounds() : null;
               if (blockSounds != null) {
                  str = blockSounds.getHitSound();
                  if (str != null) {
                     break block;
                  }
               }

               str = "nexo:block.wood.hit";
            }

            String sound = str;
            float volume = blockSounds != null ? blockSounds.getHitVolume() : 0.25F;
            float pitch = blockSounds != null ? blockSounds.getHitPitch() : 0.5F;
            BlockSoundTask task = new BlockSoundTask(System.currentTimeMillis(), location, 2L, 4L);
            breakerPlaySound.put(location, task);
            task.runTask((() -> CustomBlockSoundListener.onHitWood$lambda$1(location, sound, volume, pitch)));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onStopHittingWood(@NotNull BlockDamageAbortEvent blockDamageAbortEvent) {
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
      Player player2 = entity instanceof Player ? (Player)entity : null;
      if ((entity instanceof Player ? (Player)entity : null) != null) {
         Player player3 = player2;
         int value = 0;
         player2 = !VersionUtil.INSTANCE.isFoliaServer() ? player3 : null;
         if (player2 != null) {
            Player player = player2;
            Attribute attribute = Attribute.SAFE_FALL_DISTANCE;
            if (attribute != null) {
               Attribute p0 = attribute;
               int value2 = 0;
               AttributeInstance attributeInstance = player.getAttribute(p0);
               if (attributeInstance != null) {
                  double fallDistance = attributeInstance.getValue();
                  BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
                  Location location = player.getLocation();
                  if (blockHelpers.isLoaded(location) && !genericGameEvent.isAsynchronous()) {
                     if (Intrinsics.areEqual(genericGameEvent.getEvent(), GameEvent.HIT_GROUND) && (double)player.getFallDistance() < fallDistance) {
                        return;
                     }

                     if (!Intrinsics.areEqual(genericGameEvent.getEvent(), GameEvent.STEP) || !player.isInWater() && !player.isSwimming() && !player.isSneaking() && !player.isInLava()) {
                        Block block = BlockHelpers.entityStandingOn((Entity)player);
                        if (block != null) {
                           Block sound = block;
                           int value3 = 0;
                           block = !sound.getType().isAir() ? sound : null;
                           if (block != null) {
                              Block blockStandingOn = block;
                              if (!Intrinsics.areEqual(blockStandingOn.getBlockSoundGroup().getStepSound(), Sound.BLOCK_WOOD_STEP)) {
                                 return;
                              }

                              CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(blockStandingOn);
                              Triple triple;
                              if (genericGameEvent.getEvent() == GameEvent.STEP) {
                                 block: {
                                    if (mechanic != null) {
                                       BlockSounds blockSounds = mechanic.getBlockSounds();
                                       if (blockSounds != null) {
                                          BlockSounds it = blockSounds;
                                          int value4 = 0;
                                          triple = TuplesKt.to(kotlin.TuplesKt.to(it.getStepSound(), it.getStepVolume()), it.getStepPitch());
                                          if (triple != null) {
                                             break block;
                                          }
                                       }
                                    }

                                    triple = TuplesKt.to(kotlin.TuplesKt.to("nexo:block.wood.step", 0.15F), 1.0F);
                                 }
                              } else {
                                 block: {
                                    if (!Intrinsics.areEqual(genericGameEvent.getEvent(), GameEvent.HIT_GROUND)) {
                                       return;
                                    }

                                    if (mechanic != null) {
                                       BlockSounds blockSounds2 = mechanic.getBlockSounds();
                                       if (blockSounds2 != null) {
                                          BlockSounds it = blockSounds2;
                                          int value5 = 0;
                                          triple = TuplesKt.to(kotlin.TuplesKt.to(it.getFallSound(), it.getFallVolume()), it.getFallPitch());
                                          if (triple != null) {
                                             break block;
                                          }
                                       }
                                    }

                                    triple = TuplesKt.to(kotlin.TuplesKt.to("nexo:block.wood.fall", 0.5F), 0.75F);
                                 }
                              }

                              Triple triple2 = triple;
                              String sound_1 = (String)triple2.component1();
                              float volume = ((Number)triple2.component2()).floatValue();
                              float pitch = ((Number)triple2.component3()).floatValue();
                              BlockHelpers.playCustomBlockSound(player.getLocation(), sound_1, SoundCategory.PLAYERS, volume, pitch);
                              return;
                           }
                        }

                        return;
                     }

                     return;
                  }

                  return;
               }
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPlacing(@NotNull NexoNoteBlockPlaceEvent nexoNoteBlockPlaceEvent) {
      BlockSounds blockSounds2 = nexoNoteBlockPlaceEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasPlaceSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoNoteBlockPlaceEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getPlaceSound(), blockSounds.getPlaceVolume(), blockSounds.getPlacePitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBreaking(@NotNull NexoNoteBlockBreakEvent nexoNoteBlockBreakEvent) {
      BlockSounds blockSounds2 = nexoNoteBlockBreakEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasBreakSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoNoteBlockBreakEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getBreakSound(), blockSounds.getBreakVolume(), blockSounds.getBreakPitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPlacing(@NotNull NexoChorusBlockPlaceEvent nexoChorusBlockPlaceEvent) {
      BlockSounds blockSounds2 = nexoChorusBlockPlaceEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasPlaceSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoChorusBlockPlaceEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getPlaceSound(), blockSounds.getPlaceVolume(), blockSounds.getPlacePitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBreaking(@NotNull NexoChorusBlockBreakEvent nexoChorusBlockBreakEvent) {
      BlockSounds blockSounds2 = nexoChorusBlockBreakEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasBreakSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoChorusBlockBreakEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getBreakSound(), blockSounds.getBreakVolume(), blockSounds.getBreakPitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPlaceString(@NotNull NexoStringBlockPlaceEvent nexoStringBlockPlaceEvent) {
      BlockSounds blockSounds2 = nexoStringBlockPlaceEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasPlaceSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoStringBlockPlaceEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getPlaceSound(), blockSounds.getPlaceVolume(), blockSounds.getPlacePitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBreakString(@NotNull NexoStringBlockBreakEvent nexoStringBlockBreakEvent) {
      BlockSounds blockSounds2 = nexoStringBlockBreakEvent.getMechanic().getBlockSounds();
      if (blockSounds2 != null) {
         BlockSounds p0 = blockSounds2;
         int value = 0;
         blockSounds2 = p0.hasBreakSound() ? p0 : null;
         if (blockSounds2 != null) {
            BlockSounds blockSounds = blockSounds2;
            Location location = nexoStringBlockBreakEvent.getBlock().getLocation();
            BlockHelpers.playCustomBlockSound(location, blockSounds.getBreakSound(), blockSounds.getBreakVolume(), blockSounds.getBreakPitch());
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonPush(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : blocks) {
         Block it = (Block)element;
         int value = 0;
         if (it.getType() == Material.TRIPWIRE) {
            destination.add(element);
         }
      }

      blocks = (Iterable)((List)destination);

      for(Object obj : blocks) {
         Block block = (Block)obj;
         int value2 = 0;
         block.setType(Material.AIR, false);
         StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(block);
         if (stringBlockMechanic != null) {
            StringBlockMechanic mechanic = stringBlockMechanic;
            BlockSounds blockSounds2 = mechanic.getBlockSounds();
            if (blockSounds2 != null) {
               BlockSounds p0 = blockSounds2;
               int value3 = 0;
               blockSounds2 = p0.hasBreakSound() ? p0 : null;
               if (blockSounds2 != null) {
                  BlockSounds blockSounds = blockSounds2;
                  Location location = block.getLocation();
                  BlockHelpers.playCustomBlockSound(location, blockSounds.getBreakSound(), blockSounds.getBreakVolume(), blockSounds.getBreakPitch());
               }
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onStepFallString(@NotNull GenericGameEvent genericGameEvent) {
      Entity entity = genericGameEvent.getEntity();
      Player player2 = entity instanceof Player ? (Player)entity : null;
      if ((entity instanceof Player ? (Player)entity : null) != null) {
         Player player3 = player2;
         int value = 0;
         player2 = !VersionUtil.INSTANCE.isFoliaServer() ? player3 : null;
         if (player2 != null) {
            Player it = player2;
            int value2 = 0;
            BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
            Location location = it.getLocation();
            Player player4 = blockHelpers.isLoaded(location) && !genericGameEvent.isAsynchronous() ? it : null;
            if (player4 != null) {
               Player player = player4;
               EntityDamageEvent lastDamageCause = player.getLastDamageCause();
               if ((lastDamageCause != null ? lastDamageCause.getCause() : null) == DamageCause.FALL && !Intrinsics.areEqual(genericGameEvent.getEvent(), GameEvent.HIT_GROUND)) {
                  Block block = player.getLocation().getBlock();
                  StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(block);
                  if (stringBlockMechanic != null) {
                     BlockSounds blockSounds2 = stringBlockMechanic.getBlockSounds();
                     if (blockSounds2 != null) {
                        BlockSounds blockSounds = blockSounds2;
                        GameEvent event = genericGameEvent.getEvent();
                        Triple triple;
                        if (Intrinsics.areEqual(event, genericGameEvent.getEvent()) && blockSounds.hasStepSound()) {
                           triple = TuplesKt.to(kotlin.TuplesKt.to(blockSounds.getStepSound(), blockSounds.getStepVolume()), blockSounds.getStepPitch());
                        } else {
                           if (!Intrinsics.areEqual(event, genericGameEvent.getEvent()) || !blockSounds.hasFallSound()) {
                              return;
                           }

                           triple = TuplesKt.to(kotlin.TuplesKt.to(blockSounds.getFallSound(), blockSounds.getFallVolume()), blockSounds.getFallPitch());
                        }

                        Triple triple2 = triple;
                        String sound = (String)triple2.component1();
                        float volume = ((Number)triple2.component2()).floatValue();
                        float pitch = ((Number)triple2.component3()).floatValue();
                        BlockHelpers.playCustomBlockSound(player.getLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
                        return;
                     }
                  }

                  return;
               }

               return;
            }
         }
      }

   }

   private static final Unit onHitWood$lambda$1(Location location, String sound, float volume, float pitch) {
      BlockHelpers.playCustomBlockSound(location, sound, volume, pitch);
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
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockSoundListener$Companion;", "", "<init>", "()V", "breakerPlaySound", "", "Lorg/bukkit/Location;", "Lcom/nexomc/nexo/utils/blocksounds/BlockSoundTask;", "reload", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void reload() {
         Map map = CustomBlockSoundListener.breakerPlaySound;
         int value = 0;

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            ((BlockSoundTask)element.getValue()).cancel();
         }

         map.clear();
      }
   }
}
