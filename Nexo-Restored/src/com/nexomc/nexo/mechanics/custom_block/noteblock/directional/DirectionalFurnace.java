package com.nexomc.nexo.mechanics.custom_block.noteblock.directional;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
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
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u0012\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0016J\u0015\u0010#\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0000¢\u0006\u0002\b$R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\tR\u001c\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0011X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalFurnace;", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "northBlock", "", "getNorthBlock", "()Ljava/lang/String;", "southBlock", "getSouthBlock", "eastBlock", "getEastBlock", "westBlock", "getWestBlock", "blocks", "", "getBlocks", "()Ljava/util/List;", "directionMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "face", "Lorg/bukkit/block/BlockFace;", "player", "Lorg/bukkit/entity/Player;", "anyMatch", "", "itemId", "parseVariantBuilder", "", "variantBuilder", "Lteam/unnamed/creative/blockstate/Variant$Builder;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "relativeFacing", "relativeFacing$com_nexomc_core", "com.nexomc:core"}
)
public class DirectionalFurnace extends DirectionalBlock {
   @Nullable
   private final String northBlock;
   @Nullable
   private final String southBlock;
   @Nullable
   private final String eastBlock;
   @Nullable
   private final String westBlock;
   @NotNull
   private final List<String> blocks;

   public DirectionalFurnace(@NotNull ConfigurationSection section) {
      super(section);
      this.northBlock = this.childIdOrCreate$com_nexomc_core(section, "north");
      this.southBlock = this.childIdOrCreate$com_nexomc_core(section, "south");
      this.eastBlock = this.childIdOrCreate$com_nexomc_core(section, "east");
      this.westBlock = this.childIdOrCreate$com_nexomc_core(section, "west");
      String[] strs = new String[]{this.northBlock, this.southBlock, this.westBlock, this.eastBlock};
      this.blocks = CollectionsKt.listOf(strs);
   }

   @Nullable
   public final String getNorthBlock() {
      return this.northBlock;
   }

   @Nullable
   public final String getSouthBlock() {
      return this.southBlock;
   }

   @Nullable
   public final String getEastBlock() {
      return this.eastBlock;
   }

   @Nullable
   public final String getWestBlock() {
      return this.westBlock;
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
      switch (DirectionalFurnace.WhenMappings.$EnumSwitchMapping$0[blockFace.ordinal()]) {
         case 1 -> str = this.northBlock;
         case 2 -> str = this.southBlock;
         case 3 -> str = this.eastBlock;
         case 4 -> str = this.westBlock;
         default -> str = null;
      }

      return NexoBlocks.noteBlockMechanic(str);
   }

   public boolean anyMatch(@Nullable String itemId) {
      return Intrinsics.areEqual(itemId, this.northBlock) || Intrinsics.areEqual(itemId, this.southBlock) || Intrinsics.areEqual(itemId, this.westBlock) || Intrinsics.areEqual(itemId, this.eastBlock) || super.anyMatch(itemId);
   }

   public void parseVariantBuilder(@NotNull Variant.Builder variantBuilder, @NotNull CustomBlockMechanic mechanic) {
      String itemID = mechanic.getItemID();
      if (Intrinsics.areEqual(itemID, this.northBlock)) {
         Unit unit = Unit.INSTANCE;
      } else if (Intrinsics.areEqual(itemID, this.southBlock)) {
         variantBuilder.y(180);
      } else if (Intrinsics.areEqual(itemID, this.westBlock)) {
         variantBuilder.y(270);
      } else if (Intrinsics.areEqual(itemID, this.eastBlock)) {
         variantBuilder.y(90);
      } else {
         super.parseVariantBuilder(variantBuilder, mechanic);
         Unit unit2 = Unit.INSTANCE;
      }

   }

   @NotNull
   public final BlockFace relativeFacing$com_nexomc_core(@NotNull Player player) {
      Location it = player.getLocation();
      int value = 0;
      Pair pair = TuplesKt.to((double)it.getYaw(), (double)it.getPitch());
      double yaw = ((Number)pair.component1()).doubleValue();
      double pitch = ((Number)pair.component2()).doubleValue();
      BlockFace face = !((double)0.0F <= yaw ? yaw <= (double)45.0F : false) && !(yaw >= (double)315.0F) && (!(yaw >= (double)-45.0F) || !(yaw <= (double)0.0F)) && !(yaw <= (double)-315.0F) ? (!((double)45.0F <= yaw ? yaw <= (double)135.0F : false) && !((double)-315.0F <= yaw ? yaw <= (double)-225.0F : false) ? (!((double)135.0F <= yaw ? yaw <= (double)225.0F : false) && !((double)-225.0F <= yaw ? yaw <= (double)-135.0F : false) ? (!((double)225.0F <= yaw ? yaw <= (double)315.0F : false) && !((double)-135.0F <= yaw ? yaw <= (double)-45.0F : false) ? BlockFace.SELF : BlockFace.WEST) : BlockFace.SOUTH) : BlockFace.EAST) : BlockFace.NORTH;
      return !(this instanceof DirectionalDropper) ? face : (pitch <= (double)-45.0F ? BlockFace.DOWN : (pitch >= (double)45.0F ? BlockFace.UP : face));
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.block.BlockFace.values().length];
         try { a_0[org.bukkit.block.BlockFace.NORTH.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.SOUTH.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.EAST.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.WEST.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
