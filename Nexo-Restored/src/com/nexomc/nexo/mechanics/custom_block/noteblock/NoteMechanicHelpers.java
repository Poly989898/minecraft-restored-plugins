package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.nms.IBlockUtils;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.drops.Drop;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\bJ\u0010\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\r\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0015\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000f\u001a\u00020\n¢\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteMechanicHelpers;", "", "<init>", "()V", "checkNoteBlockAbove", "", "loc", "Lorg/bukkit/Location;", "Lkotlin/jvm/JvmStatic;", "legacyBlockData", "Lorg/bukkit/block/data/type/NoteBlock;", "customVariation", "", "modernBlockData", "modernCustomVariation", "noteBlock", "(Lorg/bukkit/block/data/type/NoteBlock;)Ljava/lang/Integer;", "handleFallingNexoBlockAbove", "block", "Lorg/bukkit/block/Block;", "com.nexomc:core"}
)
public final class NoteMechanicHelpers {
   @NotNull
   public static final NoteMechanicHelpers INSTANCE = new NoteMechanicHelpers();

   private NoteMechanicHelpers() {
   }

   @JvmStatic
   public static final void checkNoteBlockAbove(@NotNull Location loc) {
      Block relative = loc.getBlock().getRelative(BlockFace.UP);
      Block block = relative;
      if (block.getType() == Material.NOTE_BLOCK) {
         block.getState().update(true, true);
      }

      relative = loc.getBlock().getRelative(BlockFace.UP, 2);
      Block nextBlock = relative;
      if (nextBlock.getType() == Material.NOTE_BLOCK) {
         Location location = block.getLocation();
         checkNoteBlockAbove(location);
      }

   }

   @Nullable
   public final NoteBlock legacyBlockData(int customVariation) {
      BlockData createBlockData = Material.NOTE_BLOCK.createBlockData();
      NoteBlock it = (NoteBlock)createBlockData;
      int value = 0;
      Instrument instrument = NMSHandlers.handler().blockUtils().instrumentForVariation((customVariation + 26) % 400 * 2);
      if (instrument == null) {
         return null;
      } else {
         it.setInstrument(instrument);
         it.setNote(new Note(customVariation % 25));
         it.setPowered(customVariation >= 400);
         return it;
      }
   }

   @NotNull
   public final NoteBlock modernBlockData(int customVariation) {
      IBlockUtils blockUtils = NMSHandlers.handler().blockUtils();
      BlockData createBlockData = Material.NOTE_BLOCK.createBlockData();
      return blockUtils.encodeToBlockData(customVariation, (NoteBlock)createBlockData);
   }

   @Nullable
   public final Integer modernCustomVariation(@NotNull NoteBlock noteBlock) {
      IBlockUtils blockUtils = NMSHandlers.handler().blockUtils();
      Instrument instrument = noteBlock.getInstrument();
      Integer number = blockUtils.indexForInstrument(instrument);
      if (number != null) {
         int instrumentIndex = number;
         return instrumentIndex * 50 + noteBlock.getNote().getId() + (noteBlock.isPowered() ? 25 : 0);
      } else {
         return null;
      }
   }

   public final void handleFallingNexoBlockAbove(@NotNull Block block) {
      Block relative = block.getRelative(BlockFace.UP);
      Block blockAbove = relative;
      NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(blockAbove);
      if (noteBlockMechanic != null) {
         NoteBlockMechanic it = noteBlockMechanic;
         int value = 0;
         noteBlockMechanic = it.isFalling() ? it : null;
         if (noteBlockMechanic != null) {
            NoteBlockMechanic mechanic = noteBlockMechanic;
            Location location = blockAbove.getLocation();
            Location fallingLocation = BlockHelpers.toCenterBlockLocation(location);
            BlockData blockData = NexoBlocks.blockData(mechanic.getItemID());
            if (blockData == null) {
               return;
            }

            BlockData fallingData = blockData;
            Location location2 = blockAbove.getLocation();
            NexoBlocks.remove(location2, (Player)null, Drop.Companion.emptyDrop());
            Entity entity = blockAbove.getWorld().spawn(fallingLocation, FallingBlock.class);
            FallingBlock falling = (FallingBlock)entity;
            falling.setBlockData(fallingData);
            falling.getPersistentDataContainer().set(NoteBlockMechanic.Companion.getFALLING_KEY(), PersistentDataType.BYTE, (byte)1);
            this.handleFallingNexoBlockAbove(blockAbove);
            return;
         }
      }

   }
}
