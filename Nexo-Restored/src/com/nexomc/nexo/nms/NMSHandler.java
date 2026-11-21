package com.nexomc.nexo.nms;

import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mods.INexoCreativeInventory;
import com.nexomc.nexo.utils.InteractionResult;
import kotlin.Metadata;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.metadata.pack.FormatVersion;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH&J\b\u0010\u0010\u001a\u00020\u0011H&J\b\u0010\u0012\u001a\u00020\u0013H&J\b\u0010\u0014\u001a\u00020\u0015H&J\b\u0010\u0016\u001a\u00020\u0017H&J\b\u0010\u0018\u001a\u00020\u0019H&J\b\u0010\u001a\u001a\u00020\u0019H&J\b\u0010\u001b\u001a\u00020\u0019H&J\b\u0010\u001c\u001a\u00020\u001dH&J\b\u0010\u001e\u001a\u00020\u001fH&J2\u0010 \u001a\u0004\u0018\u00010!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+H&J\u0010\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020)H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\t¨\u0006/À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/NMSHandler;", "", "packDispatchListener", "Lorg/bukkit/event/Listener;", "getPackDispatchListener", "()Lorg/bukkit/event/Listener;", "pluginConverter", "Lcom/nexomc/nexo/nms/IPluginConverter;", "getPluginConverter", "()Lcom/nexomc/nexo/nms/IPluginConverter;", "customEntityHandler", "Lcom/nexomc/nexo/nms/ICustomEntityHandler;", "furniturePacketManager", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "packetHandler", "Lcom/nexomc/nexo/nms/IPacketHandler;", "creativeInventory", "Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "itemUtils", "Lcom/nexomc/nexo/nms/IItemUtils;", "blockUtils", "Lcom/nexomc/nexo/nms/IBlockUtils;", "playerUtils", "Lcom/nexomc/nexo/nms/IPlayerUtils;", "noteblockUpdatesDisabled", "", "tripwireUpdatesDisabled", "chorusplantUpdateDisabled", "resourcepackFormat", "Lteam/unnamed/creative/metadata/pack/FormatVersion;", "datapackFormat", "", "correctBlockStates", "Lcom/nexomc/nexo/utils/InteractionResult;", "player", "Lorg/bukkit/entity/Player;", "slot", "Lorg/bukkit/inventory/EquipmentSlot;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "target", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "noteBlockInstrument", "", "block", "com.nexomc:core"}
)
public interface NMSHandler {
   @NotNull
   Listener getPackDispatchListener();

   @NotNull
   IPluginConverter getPluginConverter();

   @NotNull
   default ICustomEntityHandler customEntityHandler() {
      return new EmptyCustomEntityHandler();
   }

   @NotNull
   default IFurniturePacketManager furniturePacketManager() {
      return new EmptyFurniturePacketManager();
   }

   @NotNull
   IPacketHandler packetHandler();

   @NotNull
   INexoCreativeInventory creativeInventory();

   @NotNull
   IItemUtils itemUtils();

   @NotNull
   IBlockUtils blockUtils();

   @NotNull
   IPlayerUtils playerUtils();

   boolean noteblockUpdatesDisabled();

   boolean tripwireUpdatesDisabled();

   boolean chorusplantUpdateDisabled();

   @NotNull
   FormatVersion resourcepackFormat();

   int datapackFormat();

   @Nullable
   InteractionResult correctBlockStates(@NotNull Player player, @NotNull EquipmentSlot equipmentSlot, @NotNull ItemStack itemStack, @NotNull Block block, @NotNull BlockFace blockFace);

   @NotNull
   String noteBlockInstrument(@NotNull Block block);

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @NotNull
      public static ICustomEntityHandler customEntityHandler(@NotNull NMSHandler nmsHandler) {
         return nmsHandler.customEntityHandler();
      }

      /** @deprecated */
      @Deprecated
      @NotNull
      public static IFurniturePacketManager furniturePacketManager(@NotNull NMSHandler nmsHandler) {
         return nmsHandler.furniturePacketManager();
      }
   }
}
