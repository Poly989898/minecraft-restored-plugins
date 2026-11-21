package com.nexomc.nexo.mechanics.custom_block.noteblock.directional;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.blockstate.Variant;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u0012\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\tH\u0016J\u0018\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0016J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\u0013\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010%\u001a\u00020\u001b2\b\u0010&\u001a\u0004\u0018\u00010'HÖ\u0083\u0004J\n\u0010(\u001a\u00020)HÖ\u0081\u0004J\n\u0010*\u001a\u00020\tHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\f\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u001c\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0011X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalLog;", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "xBlock", "", "getXBlock", "()Ljava/lang/String;", "yBlock", "getYBlock", "zBlock", "getZBlock", "blocks", "", "getBlocks", "()Ljava/util/List;", "directionMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "face", "Lorg/bukkit/block/BlockFace;", "player", "Lorg/bukkit/entity/Player;", "anyMatch", "", "itemId", "parseVariantBuilder", "", "variantBuilder", "Lteam/unnamed/creative/blockstate/Variant$Builder;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "component1", "copy", "equals", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class DirectionalLog extends DirectionalBlock {
   @NotNull
   private final ConfigurationSection section;
   @Nullable
   private final String xBlock;
   @Nullable
   private final String yBlock;
   @Nullable
   private final String zBlock;
   @NotNull
   private final List<String> blocks;

   public DirectionalLog(@NotNull ConfigurationSection section) {
      super(section);
      this.section = section;
      this.xBlock = this.childIdOrCreate$com_nexomc_core(this.section, "x");
      this.yBlock = this.childIdOrCreate$com_nexomc_core(this.section, "y");
      this.zBlock = this.childIdOrCreate$com_nexomc_core(this.section, "z");
      String[] strs = new String[]{this.xBlock, this.yBlock, this.zBlock};
      this.blocks = CollectionsKt.listOf(strs);
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   @Nullable
   public final String getXBlock() {
      return this.xBlock;
   }

   @Nullable
   public final String getYBlock() {
      return this.yBlock;
   }

   @Nullable
   public final String getZBlock() {
      return this.zBlock;
   }

   @NotNull
   public List<String> getBlocks() {
      return this.blocks;
   }

   @Nullable
   public NoteBlockMechanic directionMechanic(@NotNull BlockFace face, @Nullable Player player) {
      String str;
      switch (DirectionalLog.WhenMappings.$EnumSwitchMapping$0[face.ordinal()]) {
         case 1:
         case 2:
            str = this.xBlock;
            break;
         case 3:
         case 4:
            str = this.zBlock;
            break;
         case 5:
         case 6:
            str = this.yBlock;
            break;
         default:
            str = null;
      }

      return NexoBlocks.noteBlockMechanic(str);
   }

   public boolean anyMatch(@Nullable String itemId) {
      return Intrinsics.areEqual(itemId, this.yBlock) || Intrinsics.areEqual(itemId, this.xBlock) || Intrinsics.areEqual(itemId, this.zBlock) || super.anyMatch(itemId);
   }

   public void parseVariantBuilder(@NotNull Variant.Builder variantBuilder, @NotNull CustomBlockMechanic mechanic) {
      String itemID = mechanic.getItemID();
      if (!Intrinsics.areEqual(itemID, this.yBlock)) {
         if (Intrinsics.areEqual(itemID, this.xBlock)) {
            variantBuilder.x(90);
         } else if (Intrinsics.areEqual(itemID, this.zBlock)) {
            variantBuilder.x(90).y(90);
         } else {
            super.parseVariantBuilder(variantBuilder, mechanic);
         }
      }

   }

   @NotNull
   public final ConfigurationSection component1() {
      return this.section;
   }

   @NotNull
   public final DirectionalLog copy(@NotNull ConfigurationSection section) {
      return new DirectionalLog(section);
   }

   @NotNull
   public String toString() {
      return "DirectionalLog(section=" + this.section + ")";
   }

   public int hashCode() {
      return this.section.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof DirectionalLog)) {
         return false;
      } else {
         DirectionalLog directionalLog = (DirectionalLog)other;
         return Intrinsics.areEqual(this.section, directionalLog.section);
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.block.BlockFace.values().length];
         try { a_0[org.bukkit.block.BlockFace.NORTH.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.SOUTH.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.EAST.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.WEST.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.UP.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.DOWN.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
