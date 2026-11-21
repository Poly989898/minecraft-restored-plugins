package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.events.custom_block.NexoBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockPlaceEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockPlaceEvent;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteMechanicHelpers;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.InteractionResult;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.protectionlib.ProtectionLib;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.kyori.adventure.text.Component;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JB\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockHelpers;", "", "<init>", "()V", "makePlayerPlaceBlock", "", "player", "Lorg/bukkit/entity/Player;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "item", "Lorg/bukkit/inventory/ItemStack;", "placedAgainst", "Lorg/bukkit/block/Block;", "face", "Lorg/bukkit/block/BlockFace;", "newMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "newData", "Lorg/bukkit/block/data/BlockData;", "com.nexomc:core"}
)
public final class CustomBlockHelpers {
   @NotNull
   public static final CustomBlockHelpers INSTANCE = new CustomBlockHelpers();

   private CustomBlockHelpers() {
   }

   public final void makePlayerPlaceBlock(@NotNull Player player, @NotNull EquipmentSlot hand, @NotNull ItemStack item, @NotNull Block placedAgainst, @NotNull BlockFace face, @Nullable CustomBlockMechanic newMechanic, @Nullable BlockData newData) {
      Block target = null;
      Material type = item.getType();
      Material itemMaterial = type;
      World it = placedAgainst.getWorld();
      int value = 0;
      IntRange worldHeightRange = new IntRange(it.getMinHeight(), it.getMaxHeight());
      if (BlockHelpers.isReplaceable(placedAgainst, (UUID)null)) {
         target = placedAgainst;
      } else {
         Block relative = placedAgainst.getRelative(face);
         target = relative;
         if (newMechanic != null && !BlockHelpers.isReplaceable(target, (UUID)null)) {
            return;
         }

         if (Tag.DOORS.isTagged(target.getType())) {
            return;
         }
      }

      Pair pair = TuplesKt.to(target.getRelative(BlockFace.DOWN), target.getRelative(BlockFace.UP));
      Object component1 = pair.component1();
      Block blockBelow = (Block)component1;
      component1 = pair.component2();
      Block blockAbove = (Block)component1;
      BlockData blockData = target.getBlockData();
      BlockData oldData = blockData;
      InteractionResult result = null;
      if (newMechanic == null) {
         if (Tag.ITEMS_BOATS.isTagged(itemMaterial)) {
            return;
         }

         NMSHandler handler = NMSHandlers.handler();
         Block block = target.getRelative(face.getOppositeFace());
         result = handler.correctBlockStates(player, hand, item, block, face);
         BlockState sign = target.getState();
         Sign sign2 = sign instanceof Sign ? (Sign)sign : null;
         if ((sign instanceof Sign ? (Sign)sign : null) != null) {
            Sign sign_5 = sign2;
            int value2 = 0;
            if (sign_5.getType() != oldData.getMaterial()) {
               List list = sign_5.getSide(Side.FRONT).lines();
               Collection collection = (Collection)list;
               List list2 = sign_5.getSide(Side.BACK).lines();
               List lines = CollectionsKt.plus(collection, (Iterable)list2);
               Iterable iterable = (Iterable)lines;
               boolean flag;
               if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                  flag = false;
               } else {
                  block: {
                     for(Object element : iterable) {
                        Component it_4 = (Component)element;
                        int value3 = 0;
                        AdventureUtils adventureUtils = AdventureUtils.INSTANCE;
                        if (((CharSequence)adventureUtils.asFlatTextContent(it_4)).length() > 0) {
                           flag = true;
                           break block;
                        }
                     }

                     flag = false;
                  }
               }

               if (!flag) {
                  player.openSign(sign_5, Side.FRONT);
               }
            }
         }
      }

      if (newData != null) {
         if (result == null) {
            target.setBlockData(newData, false);
         } else {
            target.setBlockData(target.getBlockData(), false);
         }
      }

      BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(target, target.getState(), placedAgainst, item, player, true, hand);
      Location location = target.getLocation();
      if (!ProtectionLib.canBuild(player, location)) {
         blockPlaceEvent.setCancelled(true);
      }

      int sign = worldHeightRange.getFirst();
      int sign_3 = worldHeightRange.getLast();
      int it_2 = target.getY();
      if (!(sign_3 <= it_2 ? it_2 <= sign_3 : false)) {
         blockPlaceEvent.setCancelled(true);
      }

      if (newMechanic != null) {
         if (!(newMechanic instanceof StringBlockMechanic) && BlockHelpers.isStandingInside(player, target)) {
            blockPlaceEvent.setCancelled(true);
         }
      } else {
         if (!itemMaterial.isBlock() && itemMaterial != Material.FLINT_AND_STEEL && itemMaterial != Material.FIRE_CHARGE && itemMaterial != Material.STRING) {
            return;
         }

         if (Intrinsics.areEqual(target.getBlockData(), oldData)) {
            blockPlaceEvent.setCancelled(true);
         }

         if (result == null) {
            blockPlaceEvent.setCancelled(true);
         }
      }

      NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(placedAgainst.getBlockData());
      if (noteBlockMechanic != null) {
         NoteBlockMechanic it_1 = noteBlockMechanic;
         int value4 = 0;
         if (!player.isSneaking() && (it_1.isStorage() || it_1.hasClickActions())) {
            blockPlaceEvent.setCancelled(true);
         }
      }

      if (newMechanic instanceof StringBlockMechanic && ((StringBlockMechanic)newMechanic).getTall()) {
         if (!BlockHelpers.REPLACEABLE_BLOCKS.contains(blockAbove.getType())) {
            blockPlaceEvent.setCancelled(true);
         } else {
            sign_3 = worldHeightRange.getFirst();
            sign_3 = worldHeightRange.getLast();
            it_2 = blockAbove.getY();
            if (!(sign_3 <= it_2 ? it_2 <= sign_3 : false)) {
               blockPlaceEvent.setCancelled(true);
            } else {
               blockAbove.setType(Material.TRIPWIRE);
            }
         }
      }

      if (EventUtils.INSTANCE.call((Event)blockPlaceEvent) && blockPlaceEvent.canBuild()) {
         if (newMechanic != null) {
            String itemID = newMechanic.getItemID();
            location = target.getLocation();
            NexoBlocks.place(itemID, location);
            NexoBlockPlaceEvent customBlockPlaceEvent = newMechanic instanceof NoteBlockMechanic ? (NexoBlockPlaceEvent)(new NexoNoteBlockPlaceEvent((NoteBlockMechanic)newMechanic, target, player, item, hand)) : (newMechanic instanceof StringBlockMechanic ? (NexoBlockPlaceEvent)(new NexoStringBlockPlaceEvent((StringBlockMechanic)newMechanic, target, player, item, hand)) : (newMechanic instanceof ChorusBlockMechanic ? (NexoBlockPlaceEvent)(new NexoChorusBlockPlaceEvent((ChorusBlockMechanic)newMechanic, target, player, item, hand)) : new NexoBlockPlaceEvent(newMechanic, target, player, item, hand)));
            if (!EventUtils.INSTANCE.call(customBlockPlaceEvent)) {
               target.setBlockData(oldData, false);
               return;
            }

            if (newMechanic instanceof NoteBlockMechanic) {
               if (((NoteBlockMechanic)newMechanic).isFalling() && blockBelow.getType().isAir()) {
                  Location location2 = target.getLocation();
                  Location fallingLocation = BlockHelpers.toCenterBlockLocation(location2);
                  location2 = target.getLocation();
                  NexoBlocks.remove(location2, (Player)null, Drop.Companion.emptyDrop());
                  if (fallingLocation.getNearbyEntitiesByType(FallingBlock.class, (double)0.25F).isEmpty()) {
                     Entity entity = target.getWorld().spawn(fallingLocation, FallingBlock.class);
                     FallingBlock falling = (FallingBlock)entity;
                     falling.setBlockData(newData);
                     falling.getPersistentDataContainer().set(NoteBlockMechanic.Companion.getFALLING_KEY(), PersistentDataType.BYTE, (byte)1);
                  }

                  NoteMechanicHelpers.INSTANCE.handleFallingNexoBlockAbove(target);
               } else {
                  target.setType(Material.AIR);
                  target.setBlockData(newData, true);
               }
            } else if (newData != null) {
               target.setType(Material.AIR);
               target.setBlockData(newData, true);
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
               item.subtract();
            }

            player.swingHand(hand);
         }

         target.getWorld().sendGameEvent((Entity)player, GameEvent.BLOCK_PLACE, target.getLocation().toVector());
      } else {
         target.setBlockData(oldData, false);
      }
   }
}
