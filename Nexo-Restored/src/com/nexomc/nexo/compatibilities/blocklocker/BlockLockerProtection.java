package com.nexomc.nexo.compatibilities.blocklocker;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import nl.rutgerkok.blocklocker.ProtectableBlocksSettings;
import nl.rutgerkok.blocklocker.ProtectionType;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerProtection;", "Lnl/rutgerkok/blocklocker/ProtectableBlocksSettings;", "<init>", "()V", "canProtect", "", "block", "Lorg/bukkit/block/Block;", "type", "Lnl/rutgerkok/blocklocker/ProtectionType;", "com.nexomc:core"}
)
public final class BlockLockerProtection implements ProtectableBlocksSettings {
   public boolean canProtect(@NotNull Block block) {
      BlockLockerMechanic var12;
      block: {
         Intrinsics.checkNotNullParameter(block, "block");
         CustomBlockMechanic var10000 = NexoBlocks.customBlockMechanic(block);
         if (var10000 != null) {
            var12 = var10000.getBlockLocker();
            if (var12 != null) {
               break block;
            }
         }

         block2: {
            int p0 = 0;
            Block var13 = block.isEmpty() ? block : null;
            if (var13 != null) {
               Block p0_4 = var13;
               int var6 = 0;
               BlockLocation p0_3 = new BlockLocation(p0_4);
               IFurniturePacketManager.Companion var11 = IFurniturePacketManager.Companion;
               int var8 = 0;
               FurnitureMechanic var14 = var11.mechanicFromHitbox(p0_3);
               if (var14 != null) {
                  var12 = var14.getBlockLocker();
                  break block2;
               }
            }

            var12 = null;
         }

         if (var12 == null) {
            return false;
         }
      }

      BlockLockerMechanic blocklocker = var12;
      return blocklocker.getCanProtect();
   }

   public boolean canProtect(@NotNull ProtectionType type, @NotNull Block block) {
      BlockLockerMechanic var13;
      block: {
         Intrinsics.checkNotNullParameter(type, "type");
         Intrinsics.checkNotNullParameter(block, "block");
         CustomBlockMechanic var10000 = NexoBlocks.customBlockMechanic(block);
         if (var10000 != null) {
            var13 = var10000.getBlockLocker();
            if (var13 != null) {
               break block;
            }
         }

         block2: {
            int p0 = 0;
            Block var14 = block.isEmpty() ? block : null;
            if (var14 != null) {
               Block p0_2 = var14;
               int var7 = 0;
               BlockLocation p0_1 = new BlockLocation(p0_2);
               IFurniturePacketManager.Companion var12 = IFurniturePacketManager.Companion;
               int var9 = 0;
               FurnitureMechanic var15 = var12.mechanicFromHitbox(p0_1);
               if (var15 != null) {
                  var13 = var15.getBlockLocker();
                  break block2;
               }
            }

            var13 = null;
         }

         if (var13 == null) {
            return false;
         }
      }

      BlockLockerMechanic blocklocker = var13;
      return blocklocker.getCanProtect() && blocklocker.getProtectionType() == type;
   }
}
