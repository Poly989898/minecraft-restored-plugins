package com.nexomc.nexo.nms.commons.furniture.bed;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBedListener;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.nms.commons.CommonNMSHandler;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.nms.commons.furniture.FurniturePacketProcessor;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000È\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000e\u0018\u0000 \u0085\u00012\u00020\u0001:\u0002\u0085\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010w\u001a\u00020xH\u0016J\b\u0010y\u001a\u00020xH\u0016J\b\u0010z\u001a\u00020xH\u0016J\u0010\u0010{\u001a\u00020x2\u0006\u0010|\u001a\u00020tH\u0002J\u0010\u0010}\u001a\u00020x2\u0006\u0010|\u001a\u00020tH\u0002J\b\u0010~\u001a\u00020\u0015H\u0002J\b\u0010\u007f\u001a\u00020+H\u0002J\u0012\u0010\u0080\u0001\u001a\u00020x2\u0007\u0010\u0081\u0001\u001a\u00020mH\u0002J\t\u0010\u0082\u0001\u001a\u00020xH\u0002J\t\u0010\u0083\u0001\u001a\u00020xH\u0002J\t\u0010\u0084\u0001\u001a\u00020xH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u001a\u0010\u001a\u001a\u00020\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u00020!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u0011\u0010&\u001a\u00020'¢\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u001a\u0010*\u001a\u00020+X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\u001b\u00100\u001a\u0002018FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b4\u00105\u001a\u0004\b2\u00103R\u001b\u00106\u001a\u0002078FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b:\u00105\u001a\u0004\b8\u00109R\u001b\u0010;\u001a\u00020<8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b?\u00105\u001a\u0004\b=\u0010>R\u001b\u0010@\u001a\u00020A8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bD\u00105\u001a\u0004\bB\u0010CR\u001b\u0010E\u001a\u00020F8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bI\u00105\u001a\u0004\bG\u0010HR\u001b\u0010J\u001a\u00020K8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bN\u00105\u001a\u0004\bL\u0010MR\u001b\u0010O\u001a\u00020P8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bS\u00105\u001a\u0004\bQ\u0010RR\u001b\u0010T\u001a\u00020U8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bX\u00105\u001a\u0004\bV\u0010WR\u001b\u0010Y\u001a\u00020Z8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b]\u00105\u001a\u0004\b[\u0010\\R\u0014\u0010^\u001a\b\u0012\u0004\u0012\u00020`0_X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010a\u001a\u00020`X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\bb\u0010c\"\u0004\bd\u0010eR\u001a\u0010f\u001a\u00020gX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bh\u0010i\"\u0004\bj\u0010kR\u001a\u0010l\u001a\u00020mX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bn\u0010o\"\u0004\bp\u0010qR\u0014\u0010r\u001a\b\u0012\u0004\u0012\u00020t0sX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010u\u001a\u00020vX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0086\u0001"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedPacket;", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bedPlayer", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "<init>", "(Lorg/bukkit/entity/Player;Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;)V", "getBedPlayer", "()Lorg/bukkit/entity/Player;", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getBedEntity", "()Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "getBed", "()Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "serverPlayer", "Lnet/minecraft/server/level/ServerPlayer;", "getServerPlayer", "()Lnet/minecraft/server/level/ServerPlayer;", "playerNpc", "getPlayerNpc", "blockLocation", "Lorg/bukkit/Location;", "getBlockLocation", "()Lorg/bukkit/Location;", "setBlockLocation", "(Lorg/bukkit/Location;)V", "bedBlock", "Lorg/bukkit/block/Block;", "getBedBlock", "()Lorg/bukkit/block/Block;", "setBedBlock", "(Lorg/bukkit/block/Block;)V", "bedPos", "Lnet/minecraft/core/BlockPos;", "getBedPos", "()Lnet/minecraft/core/BlockPos;", "headRot", "Lnet/minecraft/core/Direction;", "getHeadRot", "()Lnet/minecraft/core/Direction;", "setHeadRot", "(Lnet/minecraft/core/Direction;)V", "setBedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBlockUpdatePacket;", "getSetBedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBlockUpdatePacket;", "setBedPacket$delegate", "Lkotlin/Lazy;", "addNpcInfoPacket", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;", "getAddNpcInfoPacket", "()Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;", "addNpcInfoPacket$delegate", "removeNpcInfoPacket", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoRemovePacket;", "getRemoveNpcInfoPacket", "()Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoRemovePacket;", "removeNpcInfoPacket$delegate", "removeNpcPacket", "Lnet/minecraft/network/protocol/game/ClientboundRemoveEntitiesPacket;", "getRemoveNpcPacket", "()Lnet/minecraft/network/protocol/game/ClientboundRemoveEntitiesPacket;", "removeNpcPacket$delegate", "createNpcPacket", "Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;", "getCreateNpcPacket", "()Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;", "createNpcPacket$delegate", "teleportNpcPacket", "Lnet/minecraft/network/protocol/game/ClientboundTeleportEntityPacket;", "getTeleportNpcPacket", "()Lnet/minecraft/network/protocol/game/ClientboundTeleportEntityPacket;", "teleportNpcPacket$delegate", "attributeNpcPacket", "Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;", "getAttributeNpcPacket", "()Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;", "attributeNpcPacket$delegate", "metaNpcPacket", "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "getMetaNpcPacket", "()Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "metaNpcPacket$delegate", "bundle", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getBundle", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "bundle$delegate", "equipmentSlotCache", "Lnet/minecraft/core/NonNullList;", "Lnet/minecraft/world/item/ItemStack;", "mainSlotCache", "getMainSlotCache", "()Lnet/minecraft/world/item/ItemStack;", "setMainSlotCache", "(Lnet/minecraft/world/item/ItemStack;)V", "headRotCache", "", "getHeadRotCache", "()F", "setHeadRotCache", "(F)V", "update", "", "getUpdate", "()Z", "setUpdate", "(Z)V", "trackedPlayers", "", "Lnet/minecraft/network/Connection;", "listener", "Lorg/bukkit/event/Listener;", "spawn", "", "refresh", "remove", "addNpc", "player", "removeNpc", "createNpc", "headRotation", "setEquipmentVisibility", "visibility", "updateHeadRotation", "updateSkin", "updateEquipment", "Companion", "com.nexomc:commons"}
)
public final class FurnitureBedPacket implements IFurnitureBedPacket {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Player bedPlayer;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureBedEntity bedEntity;
   @NotNull
   private final FurnitureBed bed;
   @NotNull
   private final ServerPlayer serverPlayer;
   @NotNull
   private final ServerPlayer playerNpc;
   @NotNull
   private Location blockLocation;
   @NotNull
   private Block bedBlock;
   @NotNull
   private final BlockPos bedPos;
   @NotNull
   private Direction headRot;
   @NotNull
   private final Lazy setBedPacket$delegate;
   @NotNull
   private final Lazy addNpcInfoPacket$delegate;
   @NotNull
   private final Lazy removeNpcInfoPacket$delegate;
   @NotNull
   private final Lazy removeNpcPacket$delegate;
   @NotNull
   private final Lazy createNpcPacket$delegate;
   @NotNull
   private final Lazy teleportNpcPacket$delegate;
   @NotNull
   private final Lazy attributeNpcPacket$delegate;
   @NotNull
   private final Lazy metaNpcPacket$delegate;
   @NotNull
   private final Lazy bundle$delegate;
   @NotNull
   private NonNullList<ItemStack> equipmentSlotCache;
   public ItemStack mainSlotCache;
   private float headRotCache;
   private boolean update;
   @NotNull
   private final List<Connection> trackedPlayers;
   @NotNull
   private final Listener listener;
   private static final EntityDataAccessor<Pose> POSE_ACCESSOR;
   private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS_ACCESSOR;
   private static final EntityDataAccessor<Byte> BYTE_ACCESSOR_17;
   private static final EntityDataAccessor<Byte> BYTE_ACCESSOR_18;
   @NotNull
   private static final Lazy<CraftServer> craftServer$delegate;
   @NotNull
   private static final Lazy<CommonNMSHandler> nmsHandler$delegate;

   public FurnitureBedPacket(@NotNull Player bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull FurnitureBed bed) {
      super();
      this.bedPlayer = bedPlayer;
      this.baseEntity = baseEntity;
      this.bedEntity = bedEntity;
      this.bed = bed;
      Player player = this.bedPlayer;
      ServerPlayer handle = ((CraftPlayer)player).getHandle();
      this.serverPlayer = handle;
      this.playerNpc = this.createNpc();
      this.blockLocation = new Location(this.baseEntity.getWorld(), this.bedEntity.getX(), (double)this.baseEntity.getWorld().getMinHeight(), this.baseEntity.getZ());
      Block block = this.blockLocation.getBlock();
      this.bedBlock = block;
      this.bedPos = new BlockPos(this.blockLocation.getBlockX(), this.blockLocation.getBlockY(), this.blockLocation.getBlockZ());
      this.headRot = this.headRotation();
      this.setBedPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.setBedPacket_delegate$lambda$0(this)));
      this.addNpcInfoPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.addNpcInfoPacket_delegate$lambda$0(this)));
      this.removeNpcInfoPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.removeNpcInfoPacket_delegate$lambda$0(this)));
      this.removeNpcPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.removeNpcPacket_delegate$lambda$0(this)));
      this.createNpcPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.createNpcPacket_delegate$lambda$0(this)));
      this.teleportNpcPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.teleportNpcPacket_delegate$lambda$0(this)));
      this.attributeNpcPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.attributeNpcPacket_delegate$lambda$0(this)));
      this.metaNpcPacket$delegate = LazyKt.lazy((() -> FurnitureBedPacket.metaNpcPacket_delegate$lambda$0(this)));
      this.bundle$delegate = LazyKt.lazy((() -> FurnitureBedPacket.bundle_delegate$lambda$0(this)));
      NonNullList nonNullList = NonNullList.create();
      this.equipmentSlotCache = nonNullList;
      this.trackedPlayers = (List)(new ArrayList());
      this.listener = new FurnitureBedListener(this.bedPlayer, this.bed);
   }

   @NotNull
   public final Player getBedPlayer() {
      return this.bedPlayer;
   }

   @NotNull
   public final ItemDisplay getBaseEntity() {
      return this.baseEntity;
   }

   @NotNull
   public final FurnitureBedEntity getBedEntity() {
      return this.bedEntity;
   }

   @NotNull
   public final FurnitureBed getBed() {
      return this.bed;
   }

   @NotNull
   public final ServerPlayer getServerPlayer() {
      return this.serverPlayer;
   }

   @NotNull
   public final ServerPlayer getPlayerNpc() {
      return this.playerNpc;
   }

   @NotNull
   public final Location getBlockLocation() {
      return this.blockLocation;
   }

   public final void setBlockLocation(@NotNull Location location) {
      this.blockLocation = location;
   }

   @NotNull
   public final Block getBedBlock() {
      return this.bedBlock;
   }

   public final void setBedBlock(@NotNull Block block) {
      this.bedBlock = block;
   }

   @NotNull
   public final BlockPos getBedPos() {
      return this.bedPos;
   }

   @NotNull
   public final Direction getHeadRot() {
      return this.headRot;
   }

   public final void setHeadRot(@NotNull Direction direction) {
      this.headRot = direction;
   }

   @NotNull
   public final ClientboundBlockUpdatePacket getSetBedPacket() {
      Lazy lazy = this.setBedPacket$delegate;
      return (ClientboundBlockUpdatePacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundPlayerInfoUpdatePacket getAddNpcInfoPacket() {
      Lazy lazy = this.addNpcInfoPacket$delegate;
      return (ClientboundPlayerInfoUpdatePacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundPlayerInfoRemovePacket getRemoveNpcInfoPacket() {
      Lazy lazy = this.removeNpcInfoPacket$delegate;
      return (ClientboundPlayerInfoRemovePacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundRemoveEntitiesPacket getRemoveNpcPacket() {
      Lazy lazy = this.removeNpcPacket$delegate;
      return (ClientboundRemoveEntitiesPacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundAddEntityPacket getCreateNpcPacket() {
      Lazy lazy = this.createNpcPacket$delegate;
      return (ClientboundAddEntityPacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundTeleportEntityPacket getTeleportNpcPacket() {
      Lazy lazy = this.teleportNpcPacket$delegate;
      return (ClientboundTeleportEntityPacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundUpdateAttributesPacket getAttributeNpcPacket() {
      Lazy lazy = this.attributeNpcPacket$delegate;
      return (ClientboundUpdateAttributesPacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundSetEntityDataPacket getMetaNpcPacket() {
      Lazy lazy = this.metaNpcPacket$delegate;
      return (ClientboundSetEntityDataPacket)lazy.getValue();
   }

   @NotNull
   public final ClientboundBundlePacket getBundle() {
      Lazy lazy = this.bundle$delegate;
      return (ClientboundBundlePacket)lazy.getValue();
   }

   @NotNull
   public final ItemStack getMainSlotCache() {
      ItemStack itemStack = this.mainSlotCache;
      if (itemStack != null) {
         return itemStack;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("mainSlotCache");
         return null;
      }
   }

   public final void setMainSlotCache(@NotNull ItemStack itemStack) {
      this.mainSlotCache = itemStack;
   }

   public final float getHeadRotCache() {
      return this.headRotCache;
   }

   public final void setHeadRotCache(float value) {
      this.headRotCache = value;
   }

   public final boolean getUpdate() {
      return this.update;
   }

   public final void setUpdate(boolean flag) {
      this.update = flag;
   }

   public void spawn() {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      this.trackedPlayers.clear();
      Collection collection = (Collection)this.trackedPlayers;
      Set trackedBy = this.baseEntity.getTrackedBy();
      Iterable iterable = (Iterable)trackedBy;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Player it = (Player)item;
         int value = 0;
         destination.add(((CraftPlayer)it).getHandle().connection.connection);
      }

      iterable = (Iterable)((List)destination);
      CollectionsKt.addAll(collection, iterable);
      this.playerNpc.setGlowingTag(this.serverPlayer.hasGlowingTag());
      if (this.serverPlayer.hasGlowingTag()) {
         this.serverPlayer.setGlowingTag(false);
      }

      this.playerNpc.getEntityData().set(POSE_ACCESSOR, Pose.SLEEPING);
      this.playerNpc.getEntityData().set(BLOCK_POS_ACCESSOR, Optional.of(this.bedPos));
      this.playerNpc.getEntityData().set(BYTE_ACCESSOR_17, this.serverPlayer.getEntityData().get(BYTE_ACCESSOR_17));
      this.playerNpc.getEntityData().set(BYTE_ACCESSOR_18, this.serverPlayer.getEntityData().get(BYTE_ACCESSOR_18));
      this.serverPlayer.setSharedFlag(5, true);
      this.setEquipmentVisibility(false);
      if (this.bed.getSkipNight()) {
         this.bedPlayer.setSleepingIgnored(true);
      }

      if (this.bed.getResetPhantoms()) {
         this.bedPlayer.setStatistic(Statistic.TIME_SINCE_REST, 0);
      }

      Iterable iterable2 = (Iterable)this.trackedPlayers;

      for(Object element : iterable2) {
         Connection p0 = (Connection)element;
         this.addNpc(p0);
      }

      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory mechanicFactory3 = (MechanicFactory)iterator.next();
            MechanicFactory mechanicFactory2 = mechanicFactory3;
            if (!(mechanicFactory3 instanceof FurnitureFactory)) {
               mechanicFactory2 = null;
            }

            mechanicFactory3 = (FurnitureFactory)mechanicFactory2;
            if (mechanicFactory3 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory3;
            break;
         }

         mechanicFactory = null;
         break;
      }

      FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
      if (furnitureFactory != null) {
         Listener[] listeners = new Listener[]{this.listener};
         furnitureFactory.registerListeners(listeners);
      }

      this.update = true;
   }

   public void refresh() {
      if (this.update) {
         Set trackedBy = this.baseEntity.getTrackedBy();
         Iterable iterable = (Iterable)trackedBy;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            Player it = (Player)item;
            int value = 0;
            destination.add(((CraftPlayer)it).getHandle().connection.connection);
         }

         List players = (List)destination;

         for(Connection player : (java.lang.Iterable<Connection>)(java.lang.Iterable)players) {
            if (!this.trackedPlayers.contains(player)) {
               List list = this.trackedPlayers;
               list.add(player);
               this.addNpc(player);
            }
         }

         for(Connection player : this.trackedPlayers) {
            if (!players.contains(player)) {
               this.removeNpc(player);
            }
         }

         Collection collection = (Collection)this.trackedPlayers;
         Function1 function1 = new FurnitureBedPacket$refresh$$inlined$removeUnless$1(players);
         collection.removeIf(new FurnitureBedPacket$inlined$sam$i$java_util_function_Predicate$0(function1));
         this.updateHeadRotation();
         this.serverPlayer.setSharedFlag(5, true);
         this.updateEquipment();
         this.setEquipmentVisibility(false);
         this.updateSkin();
      }
   }

   public void remove() {
      this.update = false;
      HandlerList.unregisterAll(this.listener);
      Iterable iterable = (Iterable)this.trackedPlayers;

      for(Object element : iterable) {
         Connection p0 = (Connection)element;
         int value = 0;
         this.removeNpc(p0);
      }

      if (this.bed.getSkipNight()) {
         this.bedPlayer.setSleepingIgnored(false);
      }

      this.serverPlayer.persistentInvisibility = false;
      this.serverPlayer.setInvisible(false);
      this.setEquipmentVisibility(true);
      this.serverPlayer.setGlowingTag(this.playerNpc.hasGlowingTag());
   }

   private final void addNpc(Connection player) {
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.addNpc$lambda$0(player, this)));
      SchedulerUtils.INSTANCE.runTaskLater(1L, (() -> FurnitureBedPacket.addNpc$lambda$1(player, this)));
   }

   private final void removeNpc(Connection player) {
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.removeNpc$lambda$0(player, this)));
      ServerPlayer serverPlayer2 = player.getPlayer();
      if (serverPlayer2 != null) {
         CraftPlayer bukkitEntity = serverPlayer2.getBukkitEntity();
         if (bukkitEntity != null) {
            bukkitEntity.sendBlockChange(this.blockLocation, this.bedBlock.getBlockData());
         }
      }

   }

   private final ServerPlayer createNpc() {
      World world = this.baseEntity.getWorld();
      ServerLevel serverLevel = ((CraftWorld)world).getHandle();
      GameProfile gameProfile = Companion.getNmsHandler().playerUtils().copyGameProfile(this.serverPlayer);
      ClientInformation clientInformation = this.serverPlayer.clientInformation();
      ServerPlayer serverPlayer2 = new ServerPlayer((MinecraftServer)Companion.getCraftServer().getServer(), serverLevel, gameProfile, clientInformation);
      int value = 0;
      serverPlayer2.connection = this.serverPlayer.connection;
      serverPlayer2.setPos(this.bedEntity.getX(), this.bedEntity.getY() + 0.1125 * (double)this.serverPlayer.getScale(), this.bedEntity.getZ());
      serverPlayer2.setRot(0.0F, 0.0F);
      return serverPlayer2;
   }

   private final Direction headRotation() {
      float yaw = this.baseEntity.getYaw();
      return !(yaw >= 135.0F) && !(yaw < -135.0F) ? (yaw >= -135.0F && yaw < -45.0F ? Direction.EAST : (yaw >= -45.0F && yaw < 45.0F ? Direction.SOUTH : (yaw >= 45.0F ? Direction.WEST : Direction.NORTH))) : Direction.NORTH;
   }

   private final void setEquipmentVisibility(boolean visibility) {
      int id = this.serverPlayer.getId();
      Iterable entries$0 = (Iterable)FurnitureBedPacket.EntriesMappings.entries$0;
      int value = id;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(entries$0, 10)));

      for(Object item : entries$0) {
         EquipmentSlot it;
         ItemStack itemStack;
         block: {
            it = (EquipmentSlot)item;
            int value2 = 0;
            ServerPlayer serverPlayer2 = this.serverPlayer;
            int value3 = 0;
            ServerPlayer serverPlayer3 = Boolean.valueOf(visibility) ? serverPlayer2 : null;
            if (serverPlayer3 != null) {
               itemStack = serverPlayer3.getItemBySlot(it);
               if (itemStack != null) {
                  break block;
               }
            }

            itemStack = ItemStack.EMPTY;
         }

         destination.add(Pair.of(it, itemStack));
      }

      List list = (List)destination;
      ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(value, list);
      List players = CollectionsKt.toList((Iterable)this.trackedPlayers);
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.setEquipmentVisibility$lambda$1(players, packet)));
   }

   private final void updateHeadRotation() {
      Float p0 = this.bedPlayer.getYaw();
      float value = this.headRotCache;
      int value2 = 0;
      Float number = !Float.valueOf(value).equals(p0) ? p0 : null;
      if (number != null) {
         float playerYaw = number;
         this.headRotCache = playerYaw;
         float value3;
         switch (FurnitureBedPacket.WhenMappings.$EnumSwitchMapping$0[this.headRot.ordinal()]) {
            case 1 -> value3 = -90.0F;
            case 2 -> value3 = 90.0F;
            case 3 -> value3 = -180.0F;
            default -> value3 = 0.0F;
         }

         playerYaw += value3;
         playerYaw = (playerYaw < 0.0F ? 360.0F + playerYaw : playerYaw) % 360.0F;
         byte fixedRotation = (byte)((int)((playerYaw >= 315.0F ? playerYaw - (float)360 : (playerYaw <= 45.0F ? playerYaw : (playerYaw >= 180.0F ? -45.0F : 45.0F))) * (float)256 / (float)360));
         ClientboundRotateHeadPacket packet = new ClientboundRotateHeadPacket((Entity)this.playerNpc, fixedRotation);
         List players = CollectionsKt.toList((Iterable)this.trackedPlayers);
         FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.updateHeadRotation$lambda$0(players, packet)));
      }
   }

   private final void updateSkin() {
      this.playerNpc.setInvisible(this.serverPlayer.hasEffect(MobEffects.INVISIBILITY));
      SynchedEntityData entityData = this.playerNpc.getEntityData();
      entityData.set(BYTE_ACCESSOR_17, this.serverPlayer.getEntityData().get(BYTE_ACCESSOR_17));
      entityData.set(BYTE_ACCESSOR_18, this.serverPlayer.getEntityData().get(BYTE_ACCESSOR_18));
      ClientboundSetEntityDataPacket clientboundSetEntityDataPacket;
      int id = this.playerNpc.getId();
      List packDirty = entityData.packDirty();
      if (packDirty != null) {
         clientboundSetEntityDataPacket = new ClientboundSetEntityDataPacket(id, packDirty);
         ClientboundSetEntityDataPacket packet = clientboundSetEntityDataPacket;
         List players = CollectionsKt.toList((Iterable)this.trackedPlayers);
         FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.updateSkin$lambda$0(players, packet)));
      }
   }

   private final void updateEquipment() {
      ItemStack mainItemStack = this.serverPlayer.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!Intrinsics.areEqual(this.equipmentSlotCache, this.serverPlayer.getInventory().getContents()) || !Intrinsics.areEqual(this.getMainSlotCache(), mainItemStack)) {
         this.equipmentSlotCache.clear();
         this.equipmentSlotCache.addAll((Collection)this.serverPlayer.getInventory().getContents());
         this.setMainSlotCache(mainItemStack);
         Iterable entries$0 = (Iterable)FurnitureBedPacket.EntriesMappings.entries$0;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(entries$0, 10)));

         for(Object item : entries$0) {
            EquipmentSlot it = (EquipmentSlot)item;
            int value = 0;
            destination.add(Pair.of(it, this.serverPlayer.getItemBySlot(it)));
         }

         List equipmentList = (List)destination;
         ClientboundSetEquipmentPacket setEquipmentPacket = new ClientboundSetEquipmentPacket(this.playerNpc.getId(), equipmentList);
         List players = CollectionsKt.toList((Iterable)this.trackedPlayers);
         FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.updateEquipment$lambda$1(players, setEquipmentPacket)));
         this.serverPlayer.containerMenu.sendAllDataToRemote();
      }
   }

   private static final ClientboundBlockUpdatePacket setBedPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      return new ClientboundBlockUpdatePacket(furnitureBedPacket.bedPos, (BlockState)((BlockState)EntityUtilsKt.getEntityUtils().whiteBed().defaultBlockState().setValue((Property)BedBlock.FACING, (Comparable)furnitureBedPacket.headRot.getOpposite())).setValue((Property)BedBlock.PART, (Comparable)BedPart.HEAD));
   }

   private static final ClientboundPlayerInfoUpdatePacket addNpcInfoPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      EnumSet enumSet = EnumSet.of((Enum)Action.ADD_PLAYER, (Enum)Action.INITIALIZE_CHAT, (Enum)Action.UPDATE_GAME_MODE, (Enum)Action.UPDATE_LATENCY, (Enum)Action.UPDATE_DISPLAY_NAME);
      ServerPlayer[] serverPlayers = new ServerPlayer[]{furnitureBedPacket.playerNpc};
      return new ClientboundPlayerInfoUpdatePacket(enumSet, (Collection)CollectionsKt.mutableListOf(serverPlayers));
   }

   private static final ClientboundPlayerInfoRemovePacket removeNpcInfoPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      UUID[] uuids = new UUID[1];
      UUID uuid = furnitureBedPacket.playerNpc.getUUID();
      uuids[0] = uuid;
      return new ClientboundPlayerInfoRemovePacket(CollectionsKt.mutableListOf(uuids));
   }

   private static final ClientboundRemoveEntitiesPacket removeNpcPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      int[] ints = new int[]{furnitureBedPacket.playerNpc.getId()};
      return new ClientboundRemoveEntitiesPacket(ints);
   }

   private static final ClientboundAddEntityPacket createNpcPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      return new ClientboundAddEntityPacket(furnitureBedPacket.playerNpc.getId(), furnitureBedPacket.playerNpc.getUUID(), furnitureBedPacket.playerNpc.getX(), furnitureBedPacket.playerNpc.getY(), furnitureBedPacket.playerNpc.getZ(), furnitureBedPacket.playerNpc.getXRot(), furnitureBedPacket.playerNpc.getYRot(), furnitureBedPacket.playerNpc.getType(), 0, furnitureBedPacket.playerNpc.getDeltaMovement(), (double)furnitureBedPacket.playerNpc.yHeadRot);
   }

   private static final ClientboundTeleportEntityPacket teleportNpcPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      NMSHandler handler = NMSHandlers.handler();
      return ((CommonNMSHandler)handler).teleportNPCPacket(furnitureBedPacket.playerNpc);
   }

   private static final ClientboundUpdateAttributesPacket attributeNpcPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      return new ClientboundUpdateAttributesPacket(furnitureBedPacket.playerNpc.getId(), furnitureBedPacket.serverPlayer.getAttributes().getSyncableAttributes());
   }

   private static final ClientboundSetEntityDataPacket metaNpcPacket_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      int id = furnitureBedPacket.playerNpc.getId();
      SynchedEntityData it = furnitureBedPacket.playerNpc.getEntityData();
      int value = id;
      int value2 = 0;
      List list = it.isDirty() ? it.packDirty() : it.getNonDefaultValues();
      return new ClientboundSetEntityDataPacket(value, list);
   }

   private static final ClientboundBundlePacket bundle_delegate$lambda$0(FurnitureBedPacket furnitureBedPacket) {
      Packet[] packets = new Packet[]{furnitureBedPacket.getAddNpcInfoPacket(), furnitureBedPacket.getCreateNpcPacket(), furnitureBedPacket.getSetBedPacket(), furnitureBedPacket.getMetaNpcPacket(), furnitureBedPacket.getAttributeNpcPacket(), furnitureBedPacket.getTeleportNpcPacket()};
      return new ClientboundBundlePacket((Iterable)CollectionsKt.listOf(packets));
   }

   private static final Unit addNpc$lambda$0(Connection player, FurnitureBedPacket furnitureBedPacket) {
      player.send((Packet)furnitureBedPacket.getBundle());
      return Unit.INSTANCE;
   }

   private static final Unit addNpc$lambda$1$0(Connection player, FurnitureBedPacket furnitureBedPacket) {
      player.send((Packet)furnitureBedPacket.getTeleportNpcPacket());
      return Unit.INSTANCE;
   }

   private static final Unit addNpc$lambda$1$1$0(Connection player, FurnitureBedPacket furnitureBedPacket) {
      player.send((Packet)furnitureBedPacket.getTeleportNpcPacket());
      return Unit.INSTANCE;
   }

   private static final Unit addNpc$lambda$1$1(Connection player, FurnitureBedPacket furnitureBedPacket) {
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.addNpc$lambda$1$1$0(player, furnitureBedPacket)));
      return Unit.INSTANCE;
   }

   private static final Unit addNpc$lambda$1(Connection player, FurnitureBedPacket furnitureBedPacket) {
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurnitureBedPacket.addNpc$lambda$1$0(player, furnitureBedPacket)));
      SchedulerUtils.INSTANCE.runTaskLater(1L, (() -> FurnitureBedPacket.addNpc$lambda$1$1(player, furnitureBedPacket)));
      return Unit.INSTANCE;
   }

   private static final Unit removeNpc$lambda$0(Connection player, FurnitureBedPacket furnitureBedPacket) {
      player.send((Packet)furnitureBedPacket.getRemoveNpcInfoPacket());
      player.send((Packet)furnitureBedPacket.getRemoveNpcPacket());
      return Unit.INSTANCE;
   }

   private static final Unit setEquipmentVisibility$lambda$1(List players, ClientboundSetEquipmentPacket packet) {
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         Connection it = (Connection)element;
         int value = 0;
         it.send((Packet)packet);
      }

      return Unit.INSTANCE;
   }

   private static final Unit updateHeadRotation$lambda$0(List players, ClientboundRotateHeadPacket packet) {
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         Connection it = (Connection)element;
         int value = 0;
         it.send((Packet)packet);
      }

      return Unit.INSTANCE;
   }

   private static final Unit updateSkin$lambda$0(List players, ClientboundSetEntityDataPacket packet) {
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         Connection it = (Connection)element;
         int value = 0;
         it.send((Packet)packet);
      }

      return Unit.INSTANCE;
   }

   private static final Unit updateEquipment$lambda$1(List players, ClientboundSetEquipmentPacket setEquipmentPacket) {
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         Connection it = (Connection)element;
         int value = 0;
         it.send((Packet)setEquipmentPacket);
      }

      return Unit.INSTANCE;
   }

   private static final CraftServer craftServer_delegate$lambda$0() {
      Server server = Bukkit.getServer();
      return (CraftServer)server;
   }

   private static final CommonNMSHandler nmsHandler_delegate$lambda$0() {
      NMSHandler handler = NMSHandlers.handler();
      return (CommonNMSHandler)handler;
   }

   static {
      POSE_ACCESSOR = EntityDataSerializers.POSE.createAccessor(6);
      BLOCK_POS_ACCESSOR = EntityDataSerializers.OPTIONAL_BLOCK_POS.createAccessor(14);
      BYTE_ACCESSOR_17 = EntityDataSerializers.BYTE.createAccessor(17);
      BYTE_ACCESSOR_18 = EntityDataSerializers.BYTE.createAccessor(18);
      craftServer$delegate = LazyKt.lazy(FurnitureBedPacket::craftServer_delegate$lambda$0);
      nmsHandler$delegate = LazyKt.lazy(FurnitureBedPacket::nmsHandler_delegate$lambda$0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R2\u0010\u0004\u001a&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006 \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000Rj\u0010\b\u001a^\u0012(\u0012&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\n0\n \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\n0\n\u0018\u00010\t0\t \u0007*.\u0012(\u0012&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\n0\n \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\n0\n\u0018\u00010\t0\t\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R2\u0010\u000b\u001a&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\f0\f \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\f0\f\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R2\u0010\r\u001a&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\f0\f \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\f0\f\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0010\u0010\u0011R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0013\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedPacket$Companion;", "", "<init>", "()V", "POSE_ACCESSOR", "Lnet/minecraft/network/syncher/EntityDataAccessor;", "Lnet/minecraft/world/entity/Pose;", "kotlin.jvm.PlatformType", "BLOCK_POS_ACCESSOR", "Ljava/util/Optional;", "Lnet/minecraft/core/BlockPos;", "BYTE_ACCESSOR_17", "", "BYTE_ACCESSOR_18", "craftServer", "Lorg/bukkit/craftbukkit/CraftServer;", "getCraftServer", "()Lorg/bukkit/craftbukkit/CraftServer;", "craftServer$delegate", "Lkotlin/Lazy;", "nmsHandler", "Lcom/nexomc/nexo/nms/commons/CommonNMSHandler;", "getNmsHandler", "()Lcom/nexomc/nexo/nms/commons/CommonNMSHandler;", "nmsHandler$delegate", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final CraftServer getCraftServer() {
         Lazy lazy = FurnitureBedPacket.craftServer$delegate;
         return (CraftServer)lazy.getValue();
      }

      private final CommonNMSHandler getNmsHandler() {
         Lazy lazy = FurnitureBedPacket.nmsHandler$delegate;
         return (CommonNMSHandler)lazy.getValue();
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[net.minecraft.core.Direction.values().length];
         try { a_0[net.minecraft.core.Direction.WEST.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[net.minecraft.core.Direction.EAST.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[net.minecraft.core.Direction.NORTH.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(net.minecraft.world.entity.EquipmentSlot.values());
   }
}
