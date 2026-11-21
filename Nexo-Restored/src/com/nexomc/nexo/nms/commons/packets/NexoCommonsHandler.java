package com.nexomc.nexo.nms.commons.packets;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.ComponentProjections;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.nms.GlyphHandlers;
import com.nexomc.nexo.nms.Transformer;
import com.nexomc.nexo.nms.commons.furniture.ClientboundEntityHitboxPacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundFurnitureBasePacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundFurnitureTextEntityPacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundRemoveBarrierHitboxPacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundRemoveFurnitureLightsPacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundSendBarrierHitboxPacket;
import com.nexomc.nexo.nms.commons.furniture.ClientboundSendFurnitureLightsPacket;
import com.nexomc.nexo.utils.Quadruple;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.logs.Logs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.world.BossEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u0000 <2\u00020\u0001:\u0001<B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\u001c\u0010 \u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016JB\u0010!\u001a\u00020\u0019\"\u000e\b\u0000\u0010\"\u0018\u0001*\u0006\u0012\u0002\b\u00030#2\b\b\u0002\u0010$\u001a\u00020%2\u001a\b\b\u0010&\u001a\u0014\u0012\u0004\u0012\u0002H\"\u0012\n\u0012\b\u0012\u0002\b\u0003\u0018\u00010#0'H\u0086\bø\u0001\u0000J<\u0010(\u001a\u00020\u0019\"\u000e\b\u0000\u0010\"\u0018\u0001*\u0006\u0012\u0002\b\u00030#2\b\b\u0002\u0010$\u001a\u00020%2\u0014\b\b\u0010)\u001a\u000e\u0012\u0004\u0012\u0002H\"\u0012\u0004\u0012\u00020\u00190'H\u0086\bø\u0001\u0000J#\u0010*\u001a\u00020\u0019\"\u000e\b\u0000\u0010\"\u0018\u0001*\u0006\u0012\u0002\b\u00030#2\b\b\u0002\u0010$\u001a\u00020%H\u0086\bJ\u0018\u0010+\u001a\b\u0012\u0002\b\u0003\u0018\u00010#2\n\u0010,\u001a\u0006\u0012\u0002\b\u00030#J$\u00102\u001a\b\u0012\u0004\u0012\u0002H\"03\"\b\b\u0000\u0010\"*\u00020\u001d2\f\u00104\u001a\b\u0012\u0004\u0012\u0002H\"03J\u0016\u00105\u001a\u00020\u00192\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u000209J\n\u0010:\u001a\u00020\u000f*\u00020\u000fJ\n\u0010;\u001a\u00020\u000f*\u00020\u000fJ\n\u0010:\u001a\u00020\u0013*\u00020\u0013J\n\u0010:\u001a\u00020\u0016*\u00020\u0016R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0018\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0018\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000eX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0011R\u0018\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u000eX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0011R?\u0010-\u001a0\u0012\u0010\u0012\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030#0/\u0012\u001a\u0012\u0018\u0012\b\u0012\u0006\u0012\u0002\b\u00030#\u0012\n\u0012\b\u0012\u0002\b\u0003\u0018\u00010#0'0.¢\u0006\b\n\u0000\u001a\u0004\b0\u00101\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006="},
   d2 = {"Lcom/nexomc/nexo/nms/commons/packets/NexoCommonsHandler;", "Lio/netty/channel/ChannelDuplexHandler;", "<init>", "()V", "player", "Lorg/bukkit/entity/Player;", "getPlayer", "()Lorg/bukkit/entity/Player;", "protocolInfo", "Lnet/minecraft/network/ProtocolInfo;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "getProtocolInfo", "()Lnet/minecraft/network/ProtocolInfo;", "componentTransformer", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/network/chat/Component;", "getComponentTransformer", "()Lcom/nexomc/nexo/nms/Transformer;", "itemTransformer", "Lnet/minecraft/world/item/ItemStack;", "getItemTransformer", "merchantTransformer", "Lnet/minecraft/world/item/trading/MerchantOffers;", "getMerchantTransformer", "write", "", "ctx", "Lio/netty/channel/ChannelHandlerContext;", "msg", "", "promise", "Lio/netty/channel/ChannelPromise;", "channelRead", "registerTransformer", "T", "Lnet/minecraft/network/protocol/Packet;", "condition", "", "transformer", "Lkotlin/Function1;", "registerReader", "reader", "registerIgnored", "transformPacket", "packet", "packetTransformers", "", "Ljava/lang/Class;", "getPacketTransformers", "()Ljava/util/Map;", "transformSynchedData", "Lnet/minecraft/network/syncher/SynchedEntityData$DataValue;", "data", "handleChunkUpdate", "chunkKey", "", "packetManager", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "transform", "unescapeGlyphs", "Companion", "com.nexomc:commons"}
)
public abstract class NexoCommonsHandler extends ChannelDuplexHandler {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Map<Class<? extends Packet<?>>, Function1<Packet<?>, Packet<?>>> packetTransformers = (Map)(new Object2ObjectOpenHashMap());
   @NotNull
   private static final RegistryAccess registryAccess;
   private static final Function<ByteBuf, RegistryFriendlyByteBuf> decorator;
   private static final boolean handlePacketEvents;
   public static final int EMPTY_TEAM_ACTION = 2;
   /** Transformer for packets that are registered only to be left untouched. */
   private static final Function1<Packet<?>, Packet<?>> PASS_THROUGH = packet -> packet;
   @Nullable
   private static final Class<? extends Enum<?>> bossbarOperationTypeClass;

   public NexoCommonsHandler() {
      boolean condition = true;
      this.getPacketTransformers().put(ClientboundSetEntityMotionPacket.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundRotateHeadPacket.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundMoveEntityPacket.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundMoveEntityPacket.Pos.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundMoveEntityPacket.Rot.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundMoveEntityPacket.PosRot.class, PASS_THROUGH);
      condition = true;
      this.getPacketTransformers().put(ClientboundSetTimePacket.class, PASS_THROUGH);
      condition = !Settings.FORMAT_SCOREBOARD.toBool();
      if (condition) {
         this.getPacketTransformers().put(ClientboundSetPlayerTeamPacket.class, PASS_THROUGH);
      }

      condition = !Settings.FORMAT_TABLIST.toBool();
      if (condition) {
         this.getPacketTransformers().put(ClientboundTabListPacket.class, PASS_THROUGH);
      }

      Function1 transformer = (a0_ -> NexoCommonsHandler._init_$lambda$0(this, (ClientboundBundlePacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundBundlePacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$1(transformer));
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$1(this, (ClientboundPlayerCombatKillPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundPlayerCombatKillPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$2(transformer));
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$2(this, (ClientboundServerLinksPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundServerLinksPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$3(transformer));
      condition = Settings.FORMAT_SCOREBOARD.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$3(this, (ClientboundSetScorePacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundSetScorePacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$1(transformer));
      }

      condition = Settings.FORMAT_SCOREBOARD.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$4(this, (ClientboundSetObjectivePacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundSetObjectivePacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$2(transformer));
      }

      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$5(this, (ClientboundServerDataPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundServerDataPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$4(transformer));
      condition = Settings.FORMAT_SYSTEM_CHAT.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$6(this, (ClientboundDisguisedChatPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundDisguisedChatPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$3(transformer));
      }

      condition = Settings.FORMAT_SYSTEM_CHAT.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$7(this, (ClientboundSystemChatPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundSystemChatPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$4(transformer));
      }

      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$8(this, (ClientboundSetTitleTextPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundSetTitleTextPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$5(transformer));
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$9(this, (ClientboundSetSubtitleTextPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundSetSubtitleTextPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$6(transformer));
      condition = Settings.FORMAT_ACTION_BAR.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$10(this, (ClientboundSetActionBarTextPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundSetActionBarTextPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$5(transformer));
      }

      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$11(this, (ClientboundOpenScreenPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundOpenScreenPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$7(transformer));
      condition = Settings.FORMAT_TABLIST.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$12(this, (ClientboundTabListPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundTabListPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$6(transformer));
      }

      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$13(this, (ClientboundResourcePackPushPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundResourcePackPushPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$8(transformer));
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$14(this, (ClientboundDisconnectPacket)a0_));
      condition = true;
      this.getPacketTransformers().put(ClientboundDisconnectPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$9(transformer));
      condition = Settings.FORMAT_ENTITY_DATA.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$15(this, (ClientboundSetEntityDataPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundSetEntityDataPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$7(transformer));
      }

      condition = Settings.FORMAT_BOSSBAR.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$16(this, (ClientboundBossEventPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundBossEventPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$8(transformer));
      }

      condition = Settings.FORMAT_ITEMS.toBool();
      transformer = (a0_ -> NexoCommonsHandler._init_$lambda$17(this, (ClientboundContainerSetSlotPacket)a0_));
      if (condition) {
         this.getPacketTransformers().put(ClientboundContainerSetSlotPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$9(transformer));
      }

      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      condition = false;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      if (mechanicFactory != null) {
         Function1 function1 = (Function1)NexoCommonsHandler$19.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundFurnitureBasePacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$10(function1));
         function1 = (Function1)NexoCommonsHandler$20.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundEntityHitboxPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$11(function1));
         function1 = (Function1)NexoCommonsHandler$21.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundFurnitureTextEntityPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$12(function1));
         function1 = (Function1)NexoCommonsHandler$22.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundSendBarrierHitboxPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$13(function1));
         function1 = (Function1)NexoCommonsHandler$23.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundRemoveBarrierHitboxPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$14(function1));
         function1 = (Function1)NexoCommonsHandler$24.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundSendFurnitureLightsPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$15(function1));
         function1 = (Function1)NexoCommonsHandler$25.INSTANCE;
         condition = true;
         this.getPacketTransformers().put(ClientboundRemoveFurnitureLightsPacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$16(function1));
         function1 = (a0_ -> NexoCommonsHandler._init_$lambda$18(this, (ClientboundBlockUpdatePacket)a0_));
         condition = true;
         this.getPacketTransformers().put(ClientboundBlockUpdatePacket.class, new NexoCommonsHandler$special$$inlined$registerTransformer$default$17(function1));
         function1 = (a0_ -> NexoCommonsHandler._init_$lambda$19(this, (ClientboundSectionBlocksUpdatePacket)a0_));
         condition = true;
         this.getPacketTransformers().put(ClientboundSectionBlocksUpdatePacket.class, new NexoCommonsHandler$special$$inlined$registerReader$default$1(function1));
         function1 = (a0_ -> NexoCommonsHandler._init_$lambda$20(this, (ClientboundLevelChunkWithLightPacket)a0_));
         condition = true;
         this.getPacketTransformers().put(ClientboundLevelChunkWithLightPacket.class, new NexoCommonsHandler$special$$inlined$registerReader$default$2(function1));
         function1 = (a0_ -> NexoCommonsHandler._init_$lambda$21(this, (ClientboundForgetLevelChunkPacket)a0_));
         condition = true;
         this.getPacketTransformers().put(ClientboundForgetLevelChunkPacket.class, new NexoCommonsHandler$special$$inlined$registerReader$default$3(function1));
      }

   }

   @Nullable
   public abstract Player getPlayer();

   @NotNull
   public abstract ProtocolInfo<ClientGamePacketListener> getProtocolInfo();

   @NotNull
   public abstract Transformer<Component> getComponentTransformer();

   @NotNull
   public abstract Transformer<ItemStack> getItemTransformer();

   @NotNull
   public abstract Transformer<MerchantOffers> getMerchantTransformer();

   public void write(@NotNull ChannelHandlerContext ctx, @NotNull Object msg, @Nullable ChannelPromise promise) throws Exception {
      NexoCommonsHandler nexoCommonsHandler = this;
      ChannelHandlerContext channelHandlerContext = ctx;
      Object obj;
      if (msg instanceof Packet) {
         obj = this.transformPacket((Packet)msg);
         if (obj == null) {
            return;
         }
      } else if (msg instanceof ByteBuf && handlePacketEvents) {
         ByteBuf copy = ((ByteBuf)msg).retainedDuplicate();
         NexoCommonsHandler nexoCommonsHandler2 = this;

         Object obj3;
         try {
            NexoCommonsHandler nexoCommonsHandler3 = nexoCommonsHandler2;
            int value = 0;
            RegistryFriendlyByteBuf decodeBuf = new RegistryFriendlyByteBuf(copy, registryAccess);
            Packet decoded = (Packet)nexoCommonsHandler3.getProtocolInfo().codec().decode(decodeBuf);
            Packet packet = nexoCommonsHandler3.transformPacket(decoded);
            if (packet == null) {
               int value2 = 0;
               ReferenceCountUtil.release(msg);
               return;
            }

            obj3 = ResultShim.ok(packet);
         } catch (Throwable e) {
            obj3 = ResultShim.ok(ResultKt.createFailure(e));
         }

         nexoCommonsHandler = this;
         channelHandlerContext = ctx;
         Object obj2 = obj3;
         if (ResultShim.isSuccess(obj3)) {
            Packet packet2 = (Packet)obj3;
            int value3 = 0;
            ReferenceCountUtil.release(msg);
            Unit unit = Unit.INSTANCE;
            nexoCommonsHandler = this;
            channelHandlerContext = ctx;
         }

         Object transformed = ResultShim.isFailure(obj2) ? msg : obj2;
         ReferenceCountUtil.release(copy);
         obj = transformed;
      } else {
         obj = msg;
      }

      super.write(channelHandlerContext, obj, promise);
   }

   public void channelRead(@Nullable ChannelHandlerContext ctx, @Nullable Object msg) throws Exception {
      if (!(msg instanceof Packet)) {
         super.channelRead(ctx, msg);
      } else {
         Packet packet = this.transformPacket((Packet)msg);
         if (packet != null) {
            super.channelRead(ctx, packet);
         }
      }
   }

   @Nullable
   public final Packet<?> transformPacket(@NotNull Packet<?> packet) {
      Function1 function1 = (Function1)this.packetTransformers.get(packet.getClass());
      if (function1 == null) {
         return packet;
      } else {
         Function1 entry = function1;
         NexoCommonsHandler nexoCommonsHandler = this;

         Object obj2;
         try {
            NexoCommonsHandler nexoCommonsHandler2 = nexoCommonsHandler;
            int value = 0;
            obj2 = ResultShim.ok((Packet)entry.invoke(packet));
         } catch (Throwable ex) {
            obj2 = ResultShim.ok(ResultKt.createFailure(ex));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj2);
         Object obj;
         if (throwable == null) {
            obj = obj2;
         } else {
            Throwable e = throwable;
            int value2 = 0;
            if (Settings.DEBUG.toBool()) {
               String simpleName = packet.getClass().getSimpleName();
               Logs.logWarn("Failed to transform " + simpleName + ": " + e.getClass().getName() + ": " + e.getMessage(), false);
            }

            obj = packet;
         }

         return (Packet)obj;
      }
   }

   @NotNull
   public final Map<Class<? extends Packet<?>>, Function1<Packet<?>, Packet<?>>> getPacketTransformers() {
      return this.packetTransformers;
   }

   @NotNull
   public final <T> SynchedEntityData.DataValue<T> transformSynchedData(@NotNull SynchedEntityData.DataValue<T> data) {
      Object value = data.value();
      Object obj;
      if (value instanceof AdventureComponent) {
         SynchedEntityData.DataValue dataValue;
         int value2 = data.id();
         Object serializer = data.serializer();
         if (serializer == null) {
            throw new NullPointerException("null cannot be cast to non-null type net.minecraft.network.syncher.EntityDataSerializer<io.papermc.paper.adventure.AdventureComponent>");
         }

         ComponentProjections componentProjections = ComponentProjections.INSTANCE;
         Player player = this.getPlayer();
         net.kyori.adventure.text.Component component2 = ((AdventureComponent)value).adventure$component();
         dataValue = new SynchedEntityData.DataValue(value2, (EntityDataSerializer)serializer, new AdventureComponent(componentProjections.apply(player, GlyphHandlers.transformGlyphs(component2, (Locale)null))));
         SynchedEntityData.DataValue dataValue3 = dataValue;
         obj = dataValue3;
      } else if (value instanceof Component) {
         Object obj2 = new SynchedEntityData.DataValue(data.id(), EntityDataSerializers.COMPONENT, this.transform((Component)value));
         obj = obj2;
      } else if (value instanceof Optional) {
         Object comp = OptionalsKt.getOrNull((Optional)value);
         if (comp instanceof AdventureComponent) {
            SynchedEntityData.DataValue dataValue2;
            int value3 = data.id();
            Object obj3 = data.serializer();
            if (obj3 == null) {
               throw new NullPointerException("null cannot be cast to non-null type net.minecraft.network.syncher.EntityDataSerializer<java.util.Optional<io.papermc.paper.adventure.AdventureComponent>>");
            }

            ComponentProjections componentProjections2 = ComponentProjections.INSTANCE;
            Player player2 = this.getPlayer();
            net.kyori.adventure.text.Component component = ((AdventureComponent)comp).adventure$component();
            dataValue2 = new SynchedEntityData.DataValue(value3, (EntityDataSerializer)obj3, Optional.of(new AdventureComponent(componentProjections2.apply(player2, GlyphHandlers.transformGlyphs(component, (Locale)null)))));
            SynchedEntityData.DataValue dataValue4 = dataValue2;
            obj = dataValue4;
         } else if (comp instanceof Component) {
            Object obj4 = new SynchedEntityData.DataValue(data.id(), EntityDataSerializers.OPTIONAL_COMPONENT, Optional.of(this.transform((Component)comp)));
            obj = obj4;
         } else {
            obj = data;
         }
      } else {
         obj = data;
      }

      return (SynchedEntityData.DataValue<T>)obj;
   }

   public final void handleChunkUpdate(long chunkKey, @NotNull IFurniturePacketManager packetManager) {
      java.util.Set set;
      Player player2 = this.getPlayer();
      if (player2 != null) {
         Player player;
         UUID world;
         block: {
            player = player2;
            UUID uid = player.getWorld().getUID();
            world = uid;
            Map map = (Map)IFurniturePacketManager.Companion.getBarrierChunkMap().get(world);
            if (map != null) {
               set = (Set)map.get(chunkKey);
               if (set != null) {
                  break block;
               }
            }

            set = SetsKt.emptySet();
         }

         Iterable iterable;
         block: {
            Map map2 = (Map)IFurniturePacketManager.Companion.getLightChunkMap().get(world);
            if (map2 != null) {
               Set set2 = (Set)map2.get(chunkKey);
               if (set2 != null) {
                  iterable = (Iterable)set2;
                  break block;
               }
            }

            iterable = (Iterable)SetsKt.emptySet();
         }

         Set baseUuids = SetsKt.plus(set, iterable);
         if (!((Collection)baseUuids).isEmpty()) {
            SchedulerUtils.INSTANCE.syncDelayedTask(1L, (() -> NexoCommonsHandler.handleChunkUpdate$lambda$0(baseUuids, packetManager, player)));
         }

      }
   }

   @NotNull
   public final Component transform(@NotNull Component component) {
      return (Component)this.getComponentTransformer().transform(component);
   }

   @NotNull
   public final Component unescapeGlyphs(@NotNull Component component3) {
      GlyphHandlers glyphHandlers = GlyphHandlers.INSTANCE;
      net.kyori.adventure.text.Component component = PaperAdventure.asAdventure(component3);
      Component component2 = PaperAdventure.asVanilla(glyphHandlers.unescapeGlyphs(component));
      return component2;
   }

   @NotNull
   public final ItemStack transform(@NotNull ItemStack itemStack) {
      return (ItemStack)this.getItemTransformer().transform(itemStack);
   }

   @NotNull
   public final MerchantOffers transform(@NotNull MerchantOffers merchantOffers) {
      return (MerchantOffers)this.getMerchantTransformer().transform(merchantOffers);
   }

   private static final Packet _init_$lambda$0(NexoCommonsHandler nexoCommonsHandler, ClientboundBundlePacket packet) {
      Iterable subPackets = packet.subPackets();
      Iterable iterable2 = subPackets;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

      for(Object item : iterable2) {
         Packet p0 = (Packet)item;
         int value = 0;
         destination.add(nexoCommonsHandler.transformPacket(p0));
      }

      List list = (List)destination;
      Iterable iterable = TypeIntrinsics.asMutableIterable(list);
      return (Packet)(new ClientboundBundlePacket(iterable));
   }

   private static final Packet _init_$lambda$1(NexoCommonsHandler nexoCommonsHandler, ClientboundPlayerCombatKillPacket packet) {
      int playerId = packet.playerId();
      Component message = packet.message();
      return (Packet)(new ClientboundPlayerCombatKillPacket(playerId, nexoCommonsHandler.transform(message)));
   }

   private static final Component lambda$2$0$0(NexoCommonsHandler nexoCommonsHandler, Component it) {
      return nexoCommonsHandler.transform(it);
   }

   private static final Component lambda$2$0$1(Function1 tmp0, Object p0) {
      return (Component)tmp0.invoke(p0);
   }

   private static final Packet _init_$lambda$2(NexoCommonsHandler nexoCommonsHandler, ClientboundServerLinksPacket packet) {
      List links = packet.links();
      Iterable iterable = (Iterable)links;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         ServerLinks.UntrustedEntry it = (ServerLinks.UntrustedEntry)item;
         int value = 0;
         destination.add(new ServerLinks.UntrustedEntry(it.type().mapRight(component -> nexoCommonsHandler.transform(component)), it.link()));
      }

      List list = (List)destination;
      return (Packet)(new ClientboundServerLinksPacket(list));
   }

   private static final Component lambda$3$0(NexoCommonsHandler nexoCommonsHandler, Component it) {
      return nexoCommonsHandler.transform(it);
   }

   private static final Component lambda$3$1(Function1 tmp0, Object p0) {
      return (Component)tmp0.invoke(p0);
   }

   private static final Packet _init_$lambda$3(NexoCommonsHandler nexoCommonsHandler, ClientboundSetScorePacket packet) {
      Optional format = Settings.HIDE_SCOREBOARD_NUMBERS.toBool() ? Optional.of(BlankFormat.INSTANCE) : packet.numberFormat();
      Optional display = packet.display().map(component -> nexoCommonsHandler.transform(component));
      return (Packet)(new ClientboundSetScorePacket(packet.owner(), packet.objectiveName(), packet.score(), display, format));
   }

   private static final Packet _init_$lambda$4(NexoCommonsHandler nexoCommonsHandler, ClientboundSetObjectivePacket packet) {
      if (packet.getMethod() != 0 && packet.getMethod() != 2) {
         return (Packet)packet;
      } else {
         Component component = packet.getDisplayName();
         if (component != null) {
            component = nexoCommonsHandler.transform(component);
            if (component != null) {
               Component displayName = component;
               RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);

               Packet packet2;
               try {
                  buf.writeUtf(packet.getObjectiveName());
                  buf.writeByte(packet.getMethod());
                  ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, displayName);
                  buf.writeEnum((Enum)packet.getRenderType());
                  Optional format = Settings.HIDE_SCOREBOARD_NUMBERS.toBool() ? Optional.of(BlankFormat.INSTANCE) : packet.getNumberFormat();
                  NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, format);
                  packet2 = (Packet)ClientboundSetObjectivePacket.STREAM_CODEC.decode(buf);
               } finally {
                  buf.release();
               }

               return packet2;
            }
         }

         return (Packet)packet;
      }
   }

   private static final Packet _init_$lambda$5(NexoCommonsHandler nexoCommonsHandler, ClientboundServerDataPacket packet) {
      Component motd = packet.motd();
      return (Packet)(new ClientboundServerDataPacket(nexoCommonsHandler.transform(motd), packet.iconBytes()));
   }

   private static final Packet _init_$lambda$6(NexoCommonsHandler nexoCommonsHandler, ClientboundDisguisedChatPacket packet) {
      Component message = packet.message();
      return (Packet)(new ClientboundDisguisedChatPacket(nexoCommonsHandler.unescapeGlyphs(nexoCommonsHandler.transform(message)), packet.chatType()));
   }

   private static final Packet _init_$lambda$7(NexoCommonsHandler nexoCommonsHandler, ClientboundSystemChatPacket packet) {
      Component content = packet.content();
      return (Packet)(new ClientboundSystemChatPacket(nexoCommonsHandler.unescapeGlyphs(nexoCommonsHandler.transform(content)), packet.overlay()));
   }

   private static final Packet _init_$lambda$8(NexoCommonsHandler nexoCommonsHandler, ClientboundSetTitleTextPacket packet) {
      Component text = packet.text();
      return (Packet)(new ClientboundSetTitleTextPacket(nexoCommonsHandler.transform(text)));
   }

   private static final Packet _init_$lambda$9(NexoCommonsHandler nexoCommonsHandler, ClientboundSetSubtitleTextPacket packet) {
      Component text = packet.text();
      return (Packet)(new ClientboundSetSubtitleTextPacket(nexoCommonsHandler.transform(text)));
   }

   private static final Packet _init_$lambda$10(NexoCommonsHandler nexoCommonsHandler, ClientboundSetActionBarTextPacket packet) {
      Component text = packet.text();
      return (Packet)(new ClientboundSetActionBarTextPacket(nexoCommonsHandler.transform(text)));
   }

   private static final Packet _init_$lambda$11(NexoCommonsHandler nexoCommonsHandler, ClientboundOpenScreenPacket packet) {
      int containerId = packet.getContainerId();
      MenuType type = packet.getType();
      Component title = packet.getTitle();
      return (Packet)(new ClientboundOpenScreenPacket(containerId, type, nexoCommonsHandler.transform(title)));
   }

   private static final Packet _init_$lambda$12(NexoCommonsHandler nexoCommonsHandler, ClientboundTabListPacket packet) {
      Component header = packet.header();
      Component component = nexoCommonsHandler.transform(header);
      Component footer = packet.footer();
      return (Packet)(new ClientboundTabListPacket(component, nexoCommonsHandler.transform(footer)));
   }

   private static final Component lambda$13$0(NexoCommonsHandler nexoCommonsHandler, Component it) {
      return nexoCommonsHandler.transform(it);
   }

   private static final Component lambda$13$1(Function1 tmp0, Object p0) {
      return (Component)tmp0.invoke(p0);
   }

   private static final Packet _init_$lambda$13(NexoCommonsHandler nexoCommonsHandler, ClientboundResourcePackPushPacket packet) {
      Optional prompt = packet.prompt().map(component -> nexoCommonsHandler.transform(component));
      return (Packet)(new ClientboundResourcePackPushPacket(packet.id(), packet.url(), packet.hash(), packet.required(), prompt));
   }

   private static final Packet _init_$lambda$14(NexoCommonsHandler nexoCommonsHandler, ClientboundDisconnectPacket packet) {
      Component reason = packet.reason();
      return (Packet)(new ClientboundDisconnectPacket(nexoCommonsHandler.transform(reason)));
   }

   private static final Packet _init_$lambda$15(NexoCommonsHandler nexoCommonsHandler, ClientboundSetEntityDataPacket packet) {
      int value = packet.id();
      List packedItems = packet.packedItems();
      Iterable iterable = (Iterable)packedItems;
      int value2 = value;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         SynchedEntityData.DataValue it = (SynchedEntityData.DataValue)item;
         int value3 = 0;
         destination.add(nexoCommonsHandler.transformSynchedData(it));
      }

      List list = (List)destination;
      return (Packet)(new ClientboundSetEntityDataPacket(value2, list));
   }

   private static final Packet _init_$lambda$16(NexoCommonsHandler nexoCommonsHandler, ClientboundBossEventPacket packet) {
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);

      Packet packet2;
      try {
         ClientboundBossEventPacket.STREAM_CODEC.encode(buf, packet);
         UUID uuid = buf.readUUID();
         Class clazz = bossbarOperationTypeClass;
         if (clazz != null) {
            Enum type = buf.readEnum(clazz);
            String str = type.name();
            ClientboundBossEventPacket clientboundBossEventPacket;
            if (!Intrinsics.areEqual(str, "ADD") && !Intrinsics.areEqual(str, "UPDATE_NAME")) {
               clientboundBossEventPacket = packet;
            } else {
               Component displayName = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf);
               Quadruple quadruple;
               if (Intrinsics.areEqual(type.name(), "ADD")) {
                  float progress = buf.readFloat();
                  BossEvent.BossBarColor color = (BossEvent.BossBarColor)buf.readEnum(BossEvent.BossBarColor.class);
                  BossEvent.BossBarOverlay overlay = (BossEvent.BossBarOverlay)buf.readEnum(BossEvent.BossBarOverlay.class);
                  int flags = buf.readByte();
                  quadruple = new Quadruple(progress, color, overlay, flags);
               } else {
                  quadruple = null;
               }

               Quadruple addData = quadruple;
               buf.clear();
               buf.writeUUID(uuid);
               buf.writeEnum(type);
               StreamCodec streamCodec = ComponentSerialization.TRUSTED_STREAM_CODEC;
               streamCodec.encode(buf, nexoCommonsHandler.transform(displayName));
               if (Intrinsics.areEqual(type.name(), "ADD") && addData != null) {
                  buf.writeFloat(((Number)addData.getFirst()).floatValue());
                  buf.writeEnum((Enum)addData.getSecond());
                  buf.writeEnum((Enum)addData.getThird());
                  buf.writeByte(((Number)addData.getFourth()).intValue());
               }

               clientboundBossEventPacket = (ClientboundBossEventPacket)ClientboundBossEventPacket.STREAM_CODEC.decode(buf);
            }

            Packet uuid_1 = (Packet)clientboundBossEventPacket;
            return uuid_1;
         }

         packet2 = (Packet)packet;
      } finally {
         buf.release();
      }

      return packet2;
   }

   private static final Packet _init_$lambda$17(NexoCommonsHandler nexoCommonsHandler, ClientboundContainerSetSlotPacket it) {
      int containerId = it.getContainerId();
      int stateId = it.getStateId();
      int slot = it.getSlot();
      ItemStack item = it.getItem();
      return (Packet)(new ClientboundContainerSetSlotPacket(containerId, stateId, slot, nexoCommonsHandler.transform(item)));
   }

   private static final Packet _init_$lambda$18(NexoCommonsHandler nexoCommonsHandler, ClientboundBlockUpdatePacket packet) {
      if (!packet.getBlockState().isAir()) {
         return (Packet)packet;
      } else {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Vector vector = new Vector(packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());
         Player player = nexoCommonsHandler.getPlayer();
         if (player != null) {
            World world = player.getWorld();
            if (world != null) {
               if (companion.blockIsHitbox(vector, world, (UUID)null)) {
                  return null;
               }

               companion = IFurniturePacketManager.Companion;
               vector = new Vector(packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());
               Player player2 = nexoCommonsHandler.getPlayer();
               if (player2 != null) {
                  World world2 = player2.getWorld();
                  if (world2 != null) {
                     if (companion.blockIsLight(vector, world2, (UUID)null)) {
                        return null;
                     }

                     return (Packet)packet;
                  }
               }

               return null;
            }
         }

         return null;
      }
   }

   private static final void lambda$19$0(Set modifiedPositions, BlockPos pos, BlockState blockState) {
      if (blockState.isAir()) {
         modifiedPositions.add(new BlockLocation(pos.getX(), pos.getY(), pos.getZ()));
      }

   }

   private static final void lambda$19$1$0$0(ItemDisplay baseEntity, IFurniturePacketManager packetManager, WrappedTask it) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic mechanic = furnitureMechanic;
         packetManager.sendBarrierHitboxPacket(baseEntity, mechanic);
         packetManager.sendLightMechanicPacket(baseEntity, mechanic);
      }
   }

   private static final Unit lambda$19$1(Set modifiedPositions, World world, IFurniturePacketManager packetManager) {
      Iterable iterable = (Iterable)modifiedPositions;

      for(Object element : iterable) {
         BlockLocation blockLoc = (BlockLocation)element;
         int value = 0;
         ItemDisplay itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(blockLoc, world);
         if (itemDisplay == null) {
            itemDisplay = IFurniturePacketManager.Companion.baseEntityFromLight(blockLoc, world);
            if (itemDisplay == null) {
               continue;
            }
         }

         ItemDisplay baseEntity = itemDisplay;
         SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity((Entity)baseEntity, (WrappedTask task) -> NexoCommonsHandler.lambda$19$1$0$0(baseEntity, packetManager, task));
      }

      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$19(NexoCommonsHandler nexoCommonsHandler, ClientboundSectionBlocksUpdatePacket packet) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            Player player = nexoCommonsHandler.getPlayer();
            if (player != null) {
               World world2 = player.getWorld();
               if (world2 != null) {
                  World world = world2;
                  Set modifiedPositions = (Set)(new LinkedHashSet());
                  packet.runUpdates((BlockPos pos, BlockState state) -> NexoCommonsHandler.lambda$19$0(modifiedPositions, pos, state));
                  if (!((Collection)modifiedPositions).isEmpty()) {
                     SchedulerUtils.INSTANCE.syncDelayedTask(2L, () -> NexoCommonsHandler.lambda$19$1(modifiedPositions, world, packetManager));
                  }

                  return Unit.INSTANCE;
               }
            }

            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$20(NexoCommonsHandler nexoCommonsHandler, ClientboundLevelChunkWithLightPacket packet) {
      boolean flag;
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager;
            block: {
               packetManager = iFurniturePacketManager;
               Player player = nexoCommonsHandler.getPlayer();
               if (player != null) {
                  World world = player.getWorld();
                  if (world != null) {
                     flag = world.isChunkLoaded(packet.x(), packet.z());
                     break block;
                  }
               }

               flag = false;
            }

            if (!flag) {
               return Unit.INSTANCE;
            }

            nexoCommonsHandler.handleChunkUpdate(IFurniturePacketManager.Companion.packChunkPos(packet.x(), packet.z()), packetManager);
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$21(NexoCommonsHandler nexoCommonsHandler, ClientboundForgetLevelChunkPacket packet) {
      boolean flag;
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager;
            block: {
               packetManager = iFurniturePacketManager;
               Player player = nexoCommonsHandler.getPlayer();
               if (player != null) {
                  World world = player.getWorld();
                  if (world != null) {
                     flag = world.isChunkLoaded(packet.pos().x(), packet.pos().z());
                     break block;
                  }
               }

               flag = false;
            }

            if (!flag) {
               return Unit.INSTANCE;
            }

            nexoCommonsHandler.handleChunkUpdate(IFurniturePacketManager.Companion.packChunkPos(packet.pos().x(), packet.pos().z()), packetManager);
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final void handleChunkUpdate$lambda$0$0$0(ItemDisplay baseEntity, IFurniturePacketManager packetManager, Player player, WrappedTask it) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic mechanic = furnitureMechanic;
         packetManager.sendBarrierHitboxPacket(baseEntity, mechanic, player);
         packetManager.sendLightMechanicPacket(baseEntity, mechanic, player);
         packetManager.sendTextEntityMechanicPacket(baseEntity, mechanic, player);
      }
   }

   private static final Unit handleChunkUpdate$lambda$0(Set baseUuids, IFurniturePacketManager packetManager, Player player) {
      Iterable iterable = (Iterable)baseUuids;

      for(Object element : iterable) {
         UUID it = (UUID)element;
         int value = 0;
         Entity entity = Bukkit.getEntity(it);
         ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
         if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
            ItemDisplay baseEntity = itemDisplay;
            SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity((Entity)baseEntity, (a0_ -> NexoCommonsHandler.handleChunkUpdate$lambda$0$0$0(baseEntity, packetManager, player, (WrappedTask)a0_)));
         }
      }

      return Unit.INSTANCE;
   }

   static {
      RegistryAccess.Frozen frozen = MinecraftServer.getServer().registryAccess();
      registryAccess = (RegistryAccess)frozen;
      decorator = RegistryFriendlyByteBuf.decorator(registryAccess);
      handlePacketEvents = Settings.FORMAT_PACKET_EVENTS.toBool();
      Companion companion = Companion;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket$OperationType").asSubclass(Enum.class));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      bossbarOperationTypeClass = (Class)(ResultShim.isFailure(obj) ? null : obj);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007RQ\u0010\b\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\n0\n\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\f0\f \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\n0\n\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\f0\f\u0018\u00010\t0\t¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0086T¢\u0006\u0002\n\u0000R\u001f\u0010\u0015\u001a\u0010\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u0017\u0018\u00010\u0016¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019¨\u0006\u001a"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/packets/NexoCommonsHandler$Companion;", "", "<init>", "()V", "registryAccess", "Lnet/minecraft/core/RegistryAccess;", "getRegistryAccess", "()Lnet/minecraft/core/RegistryAccess;", "decorator", "Ljava/util/function/Function;", "Lio/netty/buffer/ByteBuf;", "kotlin.jvm.PlatformType", "Lnet/minecraft/network/RegistryFriendlyByteBuf;", "getDecorator", "()Ljava/util/function/Function;", "handlePacketEvents", "", "getHandlePacketEvents", "()Z", "EMPTY_TEAM_ACTION", "", "bossbarOperationTypeClass", "Ljava/lang/Class;", "", "getBossbarOperationTypeClass", "()Ljava/lang/Class;", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final RegistryAccess getRegistryAccess() {
         return NexoCommonsHandler.registryAccess;
      }

      public final Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
         return NexoCommonsHandler.decorator;
      }

      public final boolean getHandlePacketEvents() {
         return NexoCommonsHandler.handlePacketEvents;
      }

      @Nullable
      public final Class<? extends Enum<?>> getBossbarOperationTypeClass() {
         return NexoCommonsHandler.bossbarOperationTypeClass;
      }
   }
}
