package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.customblockdata.CustomBlockData;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.BlockHelpers;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.text.StringsKt;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 J\u0006\u0010!\u001a\u00020\u001eJ\u0006\u0010\"\u001a\u00020\u001eJ\u000e\u0010#\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\u000fJ\u0006\u0010%\u001a\u00020\u001eJ\f\u0010'\u001a\u00020\u001e*\u00020\u0003H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0016¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u001a¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010&\u001a\u00020\u000f8F¢\u0006\u0006\u001a\u0004\b&\u0010\u0010¨\u0006("},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/RegularNoteBlock;", "", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;)V", "blockAbove", "noteKey", "Lorg/bukkit/NamespacedKey;", "poweredKey", "container", "Lorg/bukkit/persistence/PersistentDataContainer;", "isPowered", "", "()Z", "note", "", "getNote", "()B", "pitch", "", "getPitch", "()F", "sound", "", "getSound", "()Ljava/lang/String;", "runClickAction", "", "action", "Lorg/bukkit/event/block/Action;", "playSoundNaturally", "increaseNote", "setPowered", "powered", "removeData", "isMobSound", "updateSurroundingBlocks", "com.nexomc:core"}
)
public final class RegularNoteBlock {
   @NotNull
   private final Block block;
   @Nullable
   private final Player player;
   @NotNull
   private final Block blockAbove;
   @NotNull
   private final NamespacedKey noteKey;
   @NotNull
   private final NamespacedKey poweredKey;
   @NotNull
   private final PersistentDataContainer container;
   private final boolean isPowered;
   private final byte note;
   private final float pitch;
   @Nullable
   private final String sound;

   public RegularNoteBlock(@NotNull Block block, @Nullable Player player) {
      super();
      this.block = block;
      this.player = player;
      Block relative = this.block.getRelative(BlockFace.UP);
      this.blockAbove = relative;
      this.noteKey = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "note");
      this.poweredKey = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "powered");
      this.container = new CustomBlockData(this.block, (Plugin)NexoPlugin.Companion.instance());
      Object orDefault = this.container.getOrDefault(this.poweredKey, PersistentDataType.BOOLEAN, false);
      this.isPowered = (Boolean)orDefault;
      orDefault = this.container.getOrDefault(this.noteKey, PersistentDataType.BYTE, (byte)0);
      this.note = ((Number)orDefault).byteValue();
      this.pitch = (float)Math.pow((double)2.0F, (double)(((float)this.note - 12.0F) / 12.0F));
      Material material = this.blockAbove.getType();
      Material type = material;
      String str;
      switch (RegularNoteBlock.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            str = type.name().toLowerCase(Locale.ROOT);
            str = "block.note_block.imitate." + StringsKt.substringBeforeLast(str, "_", str);
            break;
         case 6:
            str = "block.note_block.imitate.ender_dragon";
            break;
         case 7:
            BlockState state = this.blockAbove.getState();
            NamespacedKey noteBlockSound = ((Skull)state).getNoteBlockSound();
            str = noteBlockSound != null ? noteBlockSound.value() : null;
            break;
         default:
            block: {
               Block block2 = this.block.getRelative(BlockFace.DOWN);
               Block blockBelow = block2;
               NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(blockBelow);
               if (noteBlockMechanic != null) {
                  str = noteBlockMechanic.getInstrument();
                  if (str != null) {
                     break block;
                  }
               }

               str = "block.note_block." + NMSHandlers.handler().noteBlockInstrument(blockBelow);
            }

            str = str.toLowerCase(Locale.ROOT);
      }

      this.sound = str;
   }

   public final boolean isPowered() {
      return this.isPowered;
   }

   public final byte getNote() {
      return this.note;
   }

   public final float getPitch() {
      return this.pitch;
   }

   @Nullable
   public final String getSound() {
      return this.sound;
   }

   public final void runClickAction(@NotNull Action action) {
      this.playSoundNaturally();
      if (action == Action.RIGHT_CLICK_BLOCK) {
         this.increaseNote();
      }

   }

   public final void playSoundNaturally() {
      boolean mobSound = this.isMobSound();
      String str = this.sound;
      int value = 0;
      String str2 = this.blockAbove.isEmpty() || mobSound ? str : null;
      if (str2 != null) {
         String sound = str2;
         Location location = this.block.getLocation();
         Location loc = BlockHelpers.toCenterBlockLocation(location);
         double particleColor = (double)this.note / (double)24.0F;
         this.block.getWorld().playSound(loc, sound, 1.0F, mobSound ? 1.0F : this.pitch);
         if (!mobSound) {
            this.block.getWorld().spawnParticle(Particle.NOTE, loc.clone().add((double)0.0F, 1.2, (double)0.0F), 0, particleColor, (double)0.0F, (double)0.0F, (double)1.0F);
         }

         this.block.getWorld().sendGameEvent((Entity)this.player, GameEvent.NOTE_BLOCK_PLAY, loc.toVector());
      }
   }

   public final void increaseNote() {
      this.container.set(this.noteKey, PersistentDataType.BYTE, (byte)((this.note + 1) % 25));
      this.updateSurroundingBlocks(this.block);
   }

   public final void setPowered(boolean powered) {
      if (powered != this.isPowered) {
         this.updateSurroundingBlocks(this.block);
      }

      if (powered) {
         this.container.set(this.poweredKey, PersistentDataType.BOOLEAN, true);
      } else {
         this.container.remove(this.poweredKey);
      }

   }

   public final void removeData() {
      this.container.remove(this.noteKey);
      this.container.remove(this.poweredKey);
   }

   public final boolean isMobSound() {
      boolean flag;
      switch (RegularNoteBlock.WhenMappings.$EnumSwitchMapping$0[this.blockAbove.getType().ordinal()]) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            flag = true;
            break;
         default:
            flag = false;
      }

      return flag;
   }

   private final void updateSurroundingBlocks(Block block2) {
      BlockData blockData = block2.getBlockData().clone();
      BlockData oldData = blockData;
      block2.setType(Material.BARRIER);
      block2.setBlockData(oldData, false);
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.Material.values().length];
         try { a_0[org.bukkit.Material.SKELETON_SKULL.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.WITHER_SKELETON_SKULL.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.PIGLIN_HEAD.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.ZOMBIE_HEAD.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.CREEPER_HEAD.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.DRAGON_HEAD.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.PLAYER_HEAD.ordinal()] = 7; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
