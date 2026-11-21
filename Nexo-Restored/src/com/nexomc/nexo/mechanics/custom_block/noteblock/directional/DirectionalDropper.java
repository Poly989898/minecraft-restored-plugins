package com.nexomc.nexo.mechanics.custom_block.noteblock.directional;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
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
   d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J\u0012\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\tH\u0016J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0016J\t\u0010!\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010#\u001a\u00020\u00192\b\u0010$\u001a\u0004\u0018\u00010%HÖ\u0083\u0004J\n\u0010&\u001a\u00020'HÖ\u0081\u0004J\n\u0010(\u001a\u00020\tHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\f\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u001c\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006)"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalDropper;", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalFurnace;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "upBlock", "", "getUpBlock", "()Ljava/lang/String;", "downBlock", "getDownBlock", "blocks", "", "getBlocks", "()Ljava/util/List;", "directionMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "face", "Lorg/bukkit/block/BlockFace;", "player", "Lorg/bukkit/entity/Player;", "anyMatch", "", "itemId", "parseVariantBuilder", "", "variantBuilder", "Lteam/unnamed/creative/blockstate/Variant$Builder;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "component1", "copy", "equals", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class DirectionalDropper extends DirectionalFurnace {
   @NotNull
   private final ConfigurationSection section;
   @Nullable
   private final String upBlock;
   @Nullable
   private final String downBlock;
   @NotNull
   private final List<String> blocks;

   public DirectionalDropper(@NotNull ConfigurationSection section) {
      super(section);
      this.section = section;
      this.upBlock = this.childIdOrCreate$com_nexomc_core(this.section, "up");
      this.downBlock = this.childIdOrCreate$com_nexomc_core(this.section, "down");
      String[] strs = new String[]{this.upBlock, this.downBlock, this.getNorthBlock(), this.getSouthBlock(), this.getWestBlock(), this.getEastBlock()};
      this.blocks = CollectionsKt.listOf(strs);
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   @Nullable
   public final String getUpBlock() {
      return this.upBlock;
   }

   @Nullable
   public final String getDownBlock() {
      return this.downBlock;
   }

   @NotNull
   public List<String> getBlocks() {
      return this.blocks;
   }

   @Nullable
   public NoteBlockMechanic directionMechanic(@NotNull BlockFace face, @Nullable Player player) {
      BlockFace blockFace;
      block: {
         if (player != null) {
            int value = 0;
            blockFace = this.relativeFacing$com_nexomc_core(player);
            if (blockFace != null) {
               break block;
            }
         }

         blockFace = face;
      }

      String str;
      switch (DirectionalDropper.WhenMappings.$EnumSwitchMapping$0[blockFace.ordinal()]) {
         case 1 -> str = this.getNorthBlock();
         case 2 -> str = this.getSouthBlock();
         case 3 -> str = this.getEastBlock();
         case 4 -> str = this.getWestBlock();
         case 5 -> str = this.upBlock;
         case 6 -> str = this.downBlock;
         default -> str = null;
      }

      return NexoBlocks.noteBlockMechanic(str);
   }

   public boolean anyMatch(@Nullable String itemId) {
      return Intrinsics.areEqual(itemId, this.upBlock) || Intrinsics.areEqual(itemId, this.downBlock) || super.anyMatch(itemId);
   }

   public void parseVariantBuilder(@NotNull Variant.Builder variantBuilder, @NotNull CustomBlockMechanic mechanic) {
      String itemID = mechanic.getItemID();
      if (Intrinsics.areEqual(itemID, this.upBlock)) {
         variantBuilder.x(270);
      } else if (Intrinsics.areEqual(itemID, this.downBlock)) {
         variantBuilder.x(90);
      } else {
         super.parseVariantBuilder(variantBuilder, mechanic);
         Unit unit = Unit.INSTANCE;
      }

   }

   @NotNull
   public final ConfigurationSection component1() {
      return this.section;
   }

   @NotNull
   public final DirectionalDropper copy(@NotNull ConfigurationSection section) {
      return new DirectionalDropper(section);
   }

   @NotNull
   public String toString() {
      return "DirectionalDropper(section=" + this.section + ")";
   }

   public int hashCode() {
      return this.section.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof DirectionalDropper)) {
         return false;
      } else {
         DirectionalDropper directionalDropper = (DirectionalDropper)other;
         return Intrinsics.areEqual(this.section, directionalDropper.section);
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
