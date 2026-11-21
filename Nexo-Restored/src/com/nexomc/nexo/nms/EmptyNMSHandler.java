package com.nexomc.nexo.nms;

import com.nexomc.nexo.utils.InteractionResult;
import com.nexomc.nexo.mods.INexoCreativeInventory;
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
   d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0001\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0017H\u0016J\b\u0010\u0019\u001a\u00020\u0017H\u0016J2\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%H\u0016J\u0010\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020#H\u0016J\r\u0010)\u001a\u00070*¢\u0006\u0002\b+H\u0016J\b\u0010,\u001a\u00020-H\u0016R\u0014\u0010\u000e\u001a\u00020\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u0013X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/nms/EmptyNMSHandler;", "Lcom/nexomc/nexo/nms/NMSHandler;", "<init>", "()V", "packetHandler", "Lcom/nexomc/nexo/nms/IPacketHandler;", "itemUtils", "Lcom/nexomc/nexo/nms/IItemUtils;", "blockUtils", "Lcom/nexomc/nexo/nms/IBlockUtils;", "playerUtils", "Lcom/nexomc/nexo/nms/IPlayerUtils;", "creativeInventory", "Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "pluginConverter", "Lcom/nexomc/nexo/nms/IPluginConverter;", "getPluginConverter", "()Lcom/nexomc/nexo/nms/IPluginConverter;", "packDispatchListener", "Lorg/bukkit/event/Listener;", "getPackDispatchListener", "()Lorg/bukkit/event/Listener;", "noteblockUpdatesDisabled", "", "tripwireUpdatesDisabled", "chorusplantUpdateDisabled", "correctBlockStates", "", "player", "Lorg/bukkit/entity/Player;", "slot", "Lorg/bukkit/inventory/EquipmentSlot;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "target", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "noteBlockInstrument", "", "block", "resourcepackFormat", "Lteam/unnamed/creative/metadata/pack/FormatVersion;", "Lorg/jetbrains/annotations/NotNull;", "datapackFormat", "", "com.nexomc:core"}
)
public final class EmptyNMSHandler implements NMSHandler {
   @NotNull
   private final IPluginConverter pluginConverter = new IPluginConverter.EmptyPluginConverter();
   @NotNull
   private final Listener packDispatchListener = new Listener() {
   };

   @NotNull
   public IPacketHandler packetHandler() {
      return new IPacketHandler.EmptyPacketHandler();
   }

   @NotNull
   public IItemUtils itemUtils() {
      return new IItemUtils.EmptyItemUtils();
   }

   @NotNull
   public IBlockUtils blockUtils() {
      return new IBlockUtils.DefaultBlockUtils();
   }

   @NotNull
   public IPlayerUtils playerUtils() {
      return new IPlayerUtils.EmptyPlayerUtils();
   }

   @NotNull
   public INexoCreativeInventory creativeInventory() {
      return new INexoCreativeInventory.EmptyCreativeInventory();
   }

   @NotNull
   public IPluginConverter getPluginConverter() {
      return this.pluginConverter;
   }

   @NotNull
   public Listener getPackDispatchListener() {
      return this.packDispatchListener;
   }

   public boolean noteblockUpdatesDisabled() {
      return false;
   }

   public boolean tripwireUpdatesDisabled() {
      return false;
   }

   public boolean chorusplantUpdateDisabled() {
      return false;
   }

   @Nullable
   public InteractionResult correctBlockStates(@NotNull Player player, @NotNull EquipmentSlot slot, @NotNull ItemStack itemStack, @NotNull Block target, @NotNull BlockFace blockFace) {
      return null;
   }

   @NotNull
   public String noteBlockInstrument(@NotNull Block block) {
      return "block.note_block.harp";
   }

   @NotNull
   public FormatVersion resourcepackFormat() {
      FormatVersion formatVersion = FormatVersion.of(55);
      return formatVersion;
   }

   public int datapackFormat() {
      return 71;
   }
}
