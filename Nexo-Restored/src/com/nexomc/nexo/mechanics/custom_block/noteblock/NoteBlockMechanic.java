package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.nexo.mechanics.breakable.Breakable;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.noteblock.logstrip.LogStripping;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 -2\u00020\u0001:\u0001-B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\n\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J\b\u0010\u001f\u001a\u0004\u0018\u00010\u0011J\u0006\u0010\n\u001a\u00020\tJ\u0006\u0010\b\u001a\u00020\tJ\u0006\u0010 \u001a\u00020\tJ\b\u0010&\u001a\u00020\tH\u0016J\u0010\u0010+\u001a\u00020\t2\b\u0010,\u001a\u0004\u0018\u00010\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0018\u001a\u0004\u0018\u00010\u0017X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u001d\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010!\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001eR\u0014\u0010\"\u001a\u00020#X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0016\u0010'\u001a\u0004\u0018\u00010(8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b)\u0010*¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "factory", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "canIgnite", "", "isFalling", "beaconBaseBlock", "instrument", "", "getInstrument", "()Ljava/lang/String;", "logStripping", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/logstrip/LogStripping;", "directional", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "getDirectional", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "createBlockData", "Lorg/bukkit/block/data/type/NoteBlock;", "blockData", "getBlockData", "()Lorg/bukkit/block/data/type/NoteBlock;", "storage", "Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "isLog", "()Z", "log", "isBeaconBaseBlock", "isDirectional", "breakable", "Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "getBreakable", "()Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "hasLight", "blockSounds", "Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "getBlockSounds", "()Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "allowedSameVariation", "oldMechanic", "Companion", "com.nexomc:core"}
)
public final class NoteBlockMechanic extends CustomBlockMechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   private final boolean canIgnite;
   private final boolean isFalling;
   private final boolean beaconBaseBlock;
   @NotNull
   private final String instrument;
   @Nullable
   private final LogStripping logStripping;
   @Nullable
   private final DirectionalBlock directional;
   @Nullable
   private final NoteBlock blockData;
   private final boolean isLog;
   private final boolean isDirectional;
   @NotNull
   private final Breakable breakable;
   @NotNull
   private static final NamespacedKey FALLING_KEY;

   public NoteBlockMechanic(@NotNull NoteBlockMechanicFactory factory, @NotNull ConfigurationSection section) {
      com.nexomc.nexo.mechanics.breakable.Breakable breakable2;
      super(factory, section);
      this.canIgnite = section.getBoolean("can_ignite", false);
      this.isFalling = section.getBoolean("is_falling", false);
      this.beaconBaseBlock = section.getBoolean("beacon_base_block", false);
      String string = section.getString("instrument", "block.note_block.bass");
      this.instrument = string;
      NoteBlockMechanic noteBlockMechanic = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "log_strip");
      LogStripping logStripping2;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         logStripping2 = new LogStripping(p0);
         noteBlockMechanic = this;
      } else {
         logStripping2 = null;
      }

      this.logStripping = logStripping2;
      noteBlockMechanic = this;
      ConfigurationSection configurationSection2 = NexoYamlKt.section(section, "directional");
      DirectionalBlock directionalBlock;
      if (configurationSection2 != null) {
         ConfigurationSection configurationSection3 = configurationSection2;
         DirectionalBlock.Companion companion = DirectionalBlock.Companion;
         int value2 = 0;
         directionalBlock = companion.fromSection(configurationSection3);
         noteBlockMechanic = this;
      } else {
         directionalBlock = null;
      }

      block: {
         this.directional = directionalBlock;
         BlockData blockData2 = super.getBlockData();
         this.blockData = blockData2 instanceof NoteBlock ? (NoteBlock)blockData2 : null;
         this.isLog = this.log() != null;
         this.isDirectional = this.directional != null;
         directionalBlock = this.directional;
         if (directionalBlock != null) {
            NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
            if (parentMechanic != null) {
               breakable2 = parentMechanic.getBreakable();
               if (breakable2 != null) {
                  break block;
               }
            }
         }

         breakable2 = super.getBreakable();
      }

      this.breakable = breakable2;
   }

   @NotNull
   public final String getInstrument() {
      return this.instrument;
   }

   @Nullable
   public final DirectionalBlock getDirectional() {
      return this.directional;
   }

   @NotNull
   public NoteBlock createBlockData() {
      return NoteMechanicHelpers.INSTANCE.modernBlockData(this.getCustomVariation());
   }

   @Nullable
   public NoteBlock getBlockData() {
      return this.blockData;
   }

   @Nullable
   public StorageMechanic storage() {
      StorageMechanic storageMechanic = super.getStorage$com_nexomc_core();
      if (storageMechanic == null) {
         DirectionalBlock directionalBlock = this.directional;
         if (directionalBlock != null) {
            NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
            if (parentMechanic != null) {
               storageMechanic = parentMechanic.getStorage$com_nexomc_core();
               return storageMechanic;
            }
         }

         storageMechanic = null;
      }

      return storageMechanic;
   }

   public final boolean isLog() {
      return this.isLog;
   }

   @Nullable
   public final LogStripping log() {
      LogStripping logStripping2 = this.logStripping;
      if (logStripping2 == null) {
         DirectionalBlock directionalBlock = this.directional;
         if (directionalBlock != null) {
            NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
            if (parentMechanic != null) {
               logStripping2 = parentMechanic.logStripping;
               return logStripping2;
            }
         }

         logStripping2 = null;
      }

      return logStripping2;
   }

   public final boolean isFalling() {
      DirectionalBlock directionalBlock = this.directional;
      boolean flag;
      if (directionalBlock != null) {
         NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
         if (parentMechanic != null) {
            flag = parentMechanic.isFalling;
            return flag;
         }
      }

      flag = this.isFalling;
      return flag;
   }

   public final boolean canIgnite() {
      DirectionalBlock directionalBlock = this.directional;
      boolean flag;
      if (directionalBlock != null) {
         NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
         if (parentMechanic != null) {
            flag = parentMechanic.canIgnite;
            return flag;
         }
      }

      flag = this.canIgnite;
      return flag;
   }

   public final boolean isBeaconBaseBlock() {
      DirectionalBlock directionalBlock = this.directional;
      boolean flag;
      if (directionalBlock != null) {
         NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
         if (parentMechanic != null) {
            flag = parentMechanic.beaconBaseBlock;
            return flag;
         }
      }

      flag = this.beaconBaseBlock;
      return flag;
   }

   public final boolean isDirectional() {
      return this.isDirectional;
   }

   @NotNull
   public Breakable getBreakable() {
      return this.breakable;
   }

   public boolean hasLight() {
      DirectionalBlock directionalBlock = this.directional;
      boolean flag;
      if (directionalBlock != null) {
         NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
         if (parentMechanic != null) {
            flag = parentMechanic.hasLight();
            return flag;
         }
      }

      flag = super.hasLight();
      return flag;
   }

   @Nullable
   public BlockSounds getBlockSounds() {
      DirectionalBlock directionalBlock = this.directional;
      BlockSounds blockSounds;
      if (directionalBlock != null) {
         NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
         if (parentMechanic != null) {
            blockSounds = parentMechanic.getBlockSounds();
            if (blockSounds != null) {
               return blockSounds;
            }
         }
      }

      blockSounds = super.getBlockSounds();
      return blockSounds;
   }

   public final boolean allowedSameVariation(@Nullable NoteBlockMechanic oldMechanic) {
      if (oldMechanic != null && !Intrinsics.areEqual(this.getItemID(), oldMechanic.getItemID())) {
         if (this.isDirectional && oldMechanic.isDirectional) {
            DirectionalBlock directionalBlock = oldMechanic.directional;
            NoteBlockMechanic parentMechanic = directionalBlock.getParentMechanic();
            DirectionalBlock directionalBlock2 = this.directional;
            Pair pair = TuplesKt.to(parentMechanic, directionalBlock2.getParentMechanic());
            NoteBlockMechanic oldParent = (NoteBlockMechanic)pair.component1();
            NoteBlockMechanic currentParent = (NoteBlockMechanic)pair.component2();
            return Intrinsics.areEqual(this.getItemID(), oldParent != null ? oldParent.getItemID() : null) ? true : (Intrinsics.areEqual(oldMechanic.getItemID(), currentParent != null ? currentParent.getItemID() : null) ? true : this.directional.anyMatch(oldMechanic.getItemID()) || oldMechanic.directional.anyMatch(this.getItemID()));
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:falling_custom_block");
      FALLING_KEY = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0016\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic$Companion;", "", "<init>", "()V", "FALLING_KEY", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getFALLING_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getFALLING_KEY() {
         return NoteBlockMechanic.FALLING_KEY;
      }
   }
}
