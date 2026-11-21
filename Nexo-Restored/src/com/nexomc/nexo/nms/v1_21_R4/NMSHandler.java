package com.nexomc.nexo.nms.v1_21_R4;

import com.nexomc.nexo.nms.IBlockUtils;
import com.nexomc.nexo.nms.IPacketHandler;
import com.nexomc.nexo.nms.commons.CommonNMSHandler;
import com.nexomc.nexo.nms.commons.items.ITridentWrapper;
import com.nexomc.nexo.nms.v1_21_R4.items.TridentWrapper;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Trident;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import team.unnamed.creative.metadata.pack.FormatVersion;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B9\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016J\r\u0010\u001f\u001a\u00070 ¢\u0006\u0002\b!H\u0016J\b\u0010\"\u001a\u00020#H\u0016J\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'H\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\u00020\fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006("},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R4/NMSHandler;", "Lcom/nexomc/nexo/nms/NMSHandler;", "Lcom/nexomc/nexo/nms/commons/CommonNMSHandler;", "packDispatchListener", "Lorg/bukkit/event/Listener;", "packetHandler", "Lcom/nexomc/nexo/nms/IPacketHandler;", "itemUtils", "Lcom/nexomc/nexo/nms/v1_21_R4/ItemUtils;", "blockUtils", "Lcom/nexomc/nexo/nms/IBlockUtils;", "pluginConverter", "Lcom/nexomc/nexo/nms/v1_21_R4/PluginConverter;", "<init>", "(Lorg/bukkit/event/Listener;Lcom/nexomc/nexo/nms/IPacketHandler;Lcom/nexomc/nexo/nms/v1_21_R4/ItemUtils;Lcom/nexomc/nexo/nms/IBlockUtils;Lcom/nexomc/nexo/nms/v1_21_R4/PluginConverter;)V", "getPackDispatchListener", "()Lorg/bukkit/event/Listener;", "getPluginConverter", "()Lcom/nexomc/nexo/nms/v1_21_R4/PluginConverter;", "tridentWrapper", "Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "trident", "Lorg/bukkit/entity/Trident;", "nearestDirection", "Lnet/minecraft/core/Direction;", "vector3f", "Lorg/joml/Vector3f;", "teleportNPCPacket", "Lnet/minecraft/network/protocol/game/ClientboundTeleportEntityPacket;", "playerNpc", "Lnet/minecraft/server/level/ServerPlayer;", "resourcepackFormat", "Lteam/unnamed/creative/metadata/pack/FormatVersion;", "Lorg/jetbrains/annotations/NotNull;", "datapackFormat", "", "noteBlockInstrument", "", "block", "Lorg/bukkit/block/Block;", "com.nexomc:v1_21_R4"}
)
public final class NMSHandler extends CommonNMSHandler implements com.nexomc.nexo.nms.NMSHandler {
   @NotNull
   private final Listener packDispatchListener;
   @NotNull
   private final IPacketHandler packetHandler;
   @NotNull
   private final ItemUtils itemUtils;
   @NotNull
   private final IBlockUtils blockUtils;
   @NotNull
   private final PluginConverter pluginConverter;

   public NMSHandler(@NotNull Listener packDispatchListener, @NotNull IPacketHandler packetHandler, @NotNull ItemUtils itemUtils, @NotNull IBlockUtils blockUtils, @NotNull PluginConverter pluginConverter) {
      Intrinsics.checkNotNullParameter(packDispatchListener, "packDispatchListener");
      Intrinsics.checkNotNullParameter(packetHandler, "packetHandler");
      Intrinsics.checkNotNullParameter(itemUtils, "itemUtils");
      Intrinsics.checkNotNullParameter(blockUtils, "blockUtils");
      Intrinsics.checkNotNullParameter(pluginConverter, "pluginConverter");
      super();
      this.packDispatchListener = packDispatchListener;
      this.packetHandler = packetHandler;
      this.itemUtils = itemUtils;
      this.blockUtils = blockUtils;
      this.pluginConverter = pluginConverter;
   }

   @NotNull
   public Listener getPackDispatchListener() {
      return this.packDispatchListener;
   }

   @NotNull
   public PluginConverter getPluginConverter() {
      return this.pluginConverter;
   }

   @NotNull
   public ITridentWrapper tridentWrapper(@NotNull Trident trident) {
      Intrinsics.checkNotNullParameter(trident, "trident");
      return new TridentWrapper(trident);
   }

   @NotNull
   public Direction nearestDirection(@NotNull Vector3f vector3f) {
      Intrinsics.checkNotNullParameter(vector3f, "vector3f");
      Direction var10000 = Direction.getApproximateNearest(vector3f.x, vector3f.y, vector3f.z);
      Intrinsics.checkNotNullExpressionValue(var10000, "getApproximateNearest(...)");
      return var10000;
   }

   @NotNull
   public ClientboundTeleportEntityPacket teleportNPCPacket(@NotNull ServerPlayer playerNpc) {
      Intrinsics.checkNotNullParameter(playerNpc, "playerNpc");
      return new ClientboundTeleportEntityPacket(playerNpc.getId(), PositionMoveRotation.of((Entity)playerNpc), (Set)(new LinkedHashSet()), false);
   }

   @NotNull
   public IPacketHandler packetHandler() {
      return this.packetHandler;
   }

   @NotNull
   public ItemUtils itemUtils() {
      return this.itemUtils;
   }

   @NotNull
   public IBlockUtils blockUtils() {
      return this.blockUtils;
   }

   @NotNull
   public FormatVersion resourcepackFormat() {
      FormatVersion var10000 = FormatVersion.of(46);
      Intrinsics.checkNotNullExpressionValue(var10000, "of(...)");
      return var10000;
   }

   public int datapackFormat() {
      return 61;
   }

   @NotNull
   public String noteBlockInstrument(@NotNull Block block) {
      Intrinsics.checkNotNullParameter(block, "block");
      return ((CraftBlock)block).getNMS().instrument().toString();
   }

   public NMSHandler() {
      this((Listener)null, (IPacketHandler)null, (ItemUtils)null, (IBlockUtils)null, (PluginConverter)null, 31);
   }
}
