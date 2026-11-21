package com.nexomc.nexo.nms.v1_21_R11.furniture.bed;

import com.mojang.datafixers.util.Pair;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBedListener;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedEntity;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent.Cause;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u0000 *2\u00020\u0001:\u0001*B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010\"\u001a\u00020#H\u0016J\b\u0010$\u001a\u00020#H\u0016J\b\u0010%\u001a\u00020#H\u0016J\b\u0010&\u001a\u00020#H\u0002J\b\u0010'\u001a\u00020#H\u0002J\u0010\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020\u001bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\u001c\u001a\u00020\u001d8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b \u0010!\u001a\u0004\b\u001e\u0010\u001f¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/furniture/bed/FurnitureBedMannequin;", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bedPlayer", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "<init>", "(Lorg/bukkit/entity/Player;Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;)V", "serverPlayer", "Lnet/minecraft/server/level/ServerPlayer;", "serverLevel", "Lnet/minecraft/server/level/ServerLevel;", "blockLocation", "Lorg/bukkit/Location;", "bedBlock", "Lorg/bukkit/block/Block;", "bedPos", "Lnet/minecraft/core/BlockPos;", "mannequin", "Lcom/nexomc/nexo/nms/v1_21_R11/furniture/bed/FurnitureBedMannequinEntity;", "listener", "Lorg/bukkit/event/Listener;", "update", "", "setBedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBlockUpdatePacket;", "getSetBedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBlockUpdatePacket;", "setBedPacket$delegate", "Lkotlin/Lazy;", "spawn", "", "refresh", "remove", "hideRealEquipment", "restoreRealEquipment", "sendRealEquipment", "visible", "Companion", "com.nexomc:v1_21_R11"}
)
public final class FurnitureBedMannequin implements IFurnitureBedPacket {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Player bedPlayer;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureBed bed;
   @NotNull
   private final ServerPlayer serverPlayer;
   @NotNull
   private final ServerLevel serverLevel;
   @NotNull
   private final Location blockLocation;
   @NotNull
   private final Block bedBlock;
   @NotNull
   private final BlockPos bedPos;
   @NotNull
   private final FurnitureBedMannequinEntity mannequin;
   @NotNull
   private final Listener listener;
   private boolean update;
   @NotNull
   private final Lazy setBedPacket$delegate;
   @NotNull
   private static final List<EquipmentSlot> HUMANOID_SLOTS;

   public FurnitureBedMannequin(@NotNull Player bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull FurnitureBed bed) {
      Intrinsics.checkNotNullParameter(bedPlayer, "bedPlayer");
      Intrinsics.checkNotNullParameter(baseEntity, "baseEntity");
      Intrinsics.checkNotNullParameter(bedEntity, "bedEntity");
      Intrinsics.checkNotNullParameter(bed, "bed");
      super();
      this.bedPlayer = bedPlayer;
      this.baseEntity = baseEntity;
      this.bed = bed;
      Player var10001 = this.bedPlayer;
      Intrinsics.checkNotNull(var10001, "null cannot be cast to non-null type org.bukkit.craftbukkit.entity.CraftPlayer");
      ServerPlayer var5 = ((CraftPlayer)var10001).getHandle();
      Intrinsics.checkNotNullExpressionValue(var5, "getHandle(...)");
      this.serverPlayer = var5;
      World var6 = this.baseEntity.getWorld();
      Intrinsics.checkNotNull(var6, "null cannot be cast to non-null type org.bukkit.craftbukkit.CraftWorld");
      ServerLevel var7 = ((CraftWorld)var6).getHandle();
      Intrinsics.checkNotNullExpressionValue(var7, "getHandle(...)");
      this.serverLevel = var7;
      this.blockLocation = new Location(this.baseEntity.getWorld(), bedEntity.getX(), (double)this.baseEntity.getWorld().getMinHeight(), bedEntity.getZ());
      Block var8 = this.blockLocation.getBlock();
      Intrinsics.checkNotNullExpressionValue(var8, "getBlock(...)");
      this.bedBlock = var8;
      this.bedPos = new BlockPos(this.blockLocation.getBlockX(), this.blockLocation.getBlockY(), this.blockLocation.getBlockZ());
      this.mannequin = new FurnitureBedMannequinEntity(this.serverPlayer, this.baseEntity, bedEntity, this.serverLevel);
      this.listener = new FurnitureBedListener(this.bedPlayer, this.bed);
      this.setBedPacket$delegate = LazyKt.lazy(FurnitureBedMannequin::setBedPacket_delegate$lambda$0);
   }

   private final ClientboundBlockUpdatePacket getSetBedPacket() {
      Lazy var1 = this.setBedPacket$delegate;
      return (ClientboundBlockUpdatePacket)var1.getValue();
   }

   public void spawn() {
      this.mannequin.spawn(this.bedPos);
      this.serverPlayer.setSharedFlag(5, true);
      this.hideRealEquipment();
      Set var10000 = this.baseEntity.getTrackedBy();
      Intrinsics.checkNotNullExpressionValue(var10000, "getTrackedBy(...)");
      Iterable $this$forEach$iv = (Iterable)var10000;

      for(Object element$iv : $this$forEach$iv) {
         Player it = (Player)element$iv;
         int var6 = 0;
         Intrinsics.checkNotNull(it, "null cannot be cast to non-null type org.bukkit.craftbukkit.entity.CraftPlayer");
         ((CraftPlayer)it).getHandle().connection.send((Packet)this.getSetBedPacket());
      }

      if (this.bed.getSkipNight()) {
         this.bedPlayer.setSleepingIgnored(true);
      }

      if (this.bed.getResetPhantoms()) {
         this.bedPlayer.setStatistic(Statistic.TIME_SINCE_REST, 0);
      }

      MechanicsManager this_$iv = MechanicsManager.INSTANCE;
      Iterator var10 = ((Iterable)this_$iv.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (var10.hasNext()) {
            MechanicFactory it$iv = (MechanicFactory)var10.next();
            int var13 = 0;
            MechanicFactory var15 = it$iv;
            if (!(it$iv instanceof FurnitureFactory)) {
               var15 = null;
            }

            it$iv = (FurnitureFactory)var15;
            if (it$iv == null) {
               continue;
            }

            var14 = it$iv;
            break;
         }

         var14 = null;
         break;
      }

      FurnitureFactory var16 = (FurnitureFactory)var14;
      if (var16 != null) {
         Listener[] var8 = new Listener[]{this.listener};
         var16.registerListeners(var8);
      }

      this.update = true;
   }

   public void refresh() {
      if (this.update) {
         ClientboundTeleportEntityPacket positionSyncPacket = this.mannequin.positionSyncPacket();
         Set var10000 = this.baseEntity.getTrackedBy();
         Intrinsics.checkNotNullExpressionValue(var10000, "getTrackedBy(...)");
         Iterable $this$forEach$iv = (Iterable)var10000;

         for(Object element$iv : $this$forEach$iv) {
            Player it = (Player)element$iv;
            int var7 = 0;
            Intrinsics.checkNotNull(it, "null cannot be cast to non-null type org.bukkit.craftbukkit.entity.CraftPlayer");
            ServerGamePacketListenerImpl var9 = ((CraftPlayer)it).getHandle().connection;
            Intrinsics.checkNotNullExpressionValue(var9, "connection");
            ServerGamePacketListenerImpl connection = var9;
            connection.send((Packet)this.getSetBedPacket());
            connection.send((Packet)positionSyncPacket);
         }

         this.mannequin.updateHeadRotation();
         this.mannequin.syncPlayerState();
         this.serverPlayer.setSharedFlag(5, true);
         this.hideRealEquipment();
      }
   }

   public void remove() {
      this.update = false;
      HandlerList.unregisterAll(this.listener);
      this.mannequin.discard(Cause.DESPAWN);
      Set var10000 = this.baseEntity.getTrackedBy();
      Intrinsics.checkNotNullExpressionValue(var10000, "getTrackedBy(...)");
      Iterable $this$forEach$iv = (Iterable)var10000;

      for(Object element$iv : $this$forEach$iv) {
         Player it = (Player)element$iv;
         int var6 = 0;
         it.sendBlockChange(this.blockLocation, this.bedBlock.getBlockData());
      }

      if (this.bed.getSkipNight()) {
         this.bedPlayer.setSleepingIgnored(false);
      }

      this.serverPlayer.setSharedFlag(5, this.serverPlayer.persistentInvisibility || this.bedPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY));
      this.restoreRealEquipment();
      this.serverPlayer.setGlowingTag(this.mannequin.hasGlowingTag());
      SchedulerUtils.INSTANCE.syncDelayedTask(1L, FurnitureBedMannequin::remove$lambda$1);
   }

   private final void hideRealEquipment() {
      this.sendRealEquipment(false);
   }

   private final void restoreRealEquipment() {
      this.sendRealEquipment(true);
   }

   private final void sendRealEquipment(boolean visible) {
      int var10000 = this.serverPlayer.getId();
      Iterable $this$forEach$iv = (Iterable)HUMANOID_SLOTS;
      int var12 = var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$forEach$iv, 10)));

      for(Object item$iv$iv : $this$forEach$iv) {
         EquipmentSlot it = (EquipmentSlot)item$iv$iv;
         int var11 = 0;
         destination$iv$iv.add(Pair.of(it, visible ? this.serverPlayer.getItemBySlot(it) : ItemStack.EMPTY));
      }

      List var13 = (List)destination$iv$iv;
      ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(var12, var13);
      Set var21 = this.baseEntity.getTrackedBy();
      Intrinsics.checkNotNullExpressionValue(var21, "getTrackedBy(...)");
      $this$forEach$iv = (Iterable)var21;

      for(Object element$iv : $this$forEach$iv) {
         Player it = (Player)element$iv;
         int var20 = 0;
         Intrinsics.checkNotNull(it, "null cannot be cast to non-null type org.bukkit.craftbukkit.entity.CraftPlayer");
         ((CraftPlayer)it).getHandle().connection.send((Packet)packet);
      }

   }

   private static final ClientboundBlockUpdatePacket setBedPacket_delegate$lambda$0(FurnitureBedMannequin this$0) {
      Object var10000 = ((BlockState)EntityUtilsKt.getEntityUtils().whiteBed().defaultBlockState().setValue((Property)BedBlock.FACING, (Comparable)this$0.mannequin.getHeadFacing().getOpposite())).setValue((Property)BedBlock.PART, (Comparable)BedPart.HEAD);
      Intrinsics.checkNotNullExpressionValue(var10000, "setValue(...)");
      BlockState bed = (BlockState)var10000;
      return new ClientboundBlockUpdatePacket(this$0.bedPos, bed);
   }

   private static final Unit remove$lambda$1(FurnitureBedMannequin this$0) {
      this$0.bedPlayer.updateInventory();
      return Unit.INSTANCE;
   }

   static {
      EquipmentSlot[] var0 = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
      HUMANOID_SLOTS = CollectionsKt.listOf(var0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/furniture/bed/FurnitureBedMannequin$Companion;", "", "<init>", "()V", "HUMANOID_SLOTS", "", "Lnet/minecraft/world/entity/EquipmentSlot;", "com.nexomc:v1_21_R11"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
