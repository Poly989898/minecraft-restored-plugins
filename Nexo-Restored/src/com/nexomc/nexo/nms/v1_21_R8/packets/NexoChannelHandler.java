package com.nexomc.nexo.nms.v1_21_R8.packets;

import com.mojang.authlib.GameProfile;
import com.nexomc.nexo.api.ComponentProjections;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.placeholderapi.PapiTag;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.dialog.NexoDialogs;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.ShiftTag;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.nms.commons.packets.HashedComponentRewriter;
import com.nexomc.nexo.nms.commons.packets.NexoCommonsHandler;
import com.nexomc.nexo.nms.v1_21_R8.ItemUtils;
import com.nexomc.nexo.nms.v1_21_R8.packets.transformers.ComponentTransformer;
import com.nexomc.nexo.nms.v1_21_R8.packets.transformers.DialogTransformer;
import com.nexomc.nexo.nms.v1_21_R8.packets.transformers.ItemTransformer;
import com.nexomc.nexo.nms.v1_21_R8.packets.transformers.MerchantTransformer;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.PaperDialog;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointered;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.HashedStack;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 82\u00020\u0001:\u00018B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001c\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010'2\b\u0010(\u001a\u0004\u0018\u00010)H\u0002J\u0016\u0010*\u001a\u00020+*\u00020+2\b\b\u0002\u0010,\u001a\u00020-H\u0002J\u0016\u0010*\u001a\u00020.*\u00020.2\b\u0010/\u001a\u0004\u0018\u000100H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010\n\u001a\u0004\u0018\u00010\t2\b\u0010\b\u001a\u0004\u0018\u00010\t8V@RX\u0096\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\u00020\u0015X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u0019X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u001dX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010 \u001a\u00020!¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R'\u00101\u001a\u000e\u0012\u0004\u0012\u000200\u0012\u0004\u0012\u000203028BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b6\u00107\u001a\u0004\b4\u00105¨\u00069"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R8/packets/NexoChannelHandler;", "Lcom/nexomc/nexo/nms/commons/packets/NexoCommonsHandler;", "uuid", "Ljava/util/UUID;", "locale", "Ljava/util/Locale;", "<init>", "(Ljava/util/UUID;Ljava/util/Locale;)V", "value", "Lorg/bukkit/entity/Player;", "player", "getPlayer", "()Lorg/bukkit/entity/Player;", "protocolInfo", "Lnet/minecraft/network/ProtocolInfo;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "getProtocolInfo", "()Lnet/minecraft/network/ProtocolInfo;", "hashRewriter", "Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;", "componentTransformer", "Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/ComponentTransformer;", "getComponentTransformer", "()Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/ComponentTransformer;", "itemTransformer", "Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/ItemTransformer;", "getItemTransformer", "()Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/ItemTransformer;", "merchantTransformer", "Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/MerchantTransformer;", "getMerchantTransformer", "()Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/MerchantTransformer;", "dialogTransformer", "Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/DialogTransformer;", "getDialogTransformer", "()Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/DialogTransformer;", "handlePickItem", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "item", "Lorg/bukkit/inventory/ItemStack;", "transform", "Lnet/minecraft/network/chat/Component;", "formatPlainContents", "", "Lnet/minecraft/world/item/ItemStack;", "originalName", "", "soundMapping", "Ljava/util/concurrent/ConcurrentHashMap;", "Lnet/minecraft/resources/ResourceLocation;", "getSoundMapping", "()Ljava/util/concurrent/ConcurrentHashMap;", "soundMapping$delegate", "Lkotlin/Lazy;", "Companion", "com.nexomc:v1_21_R8"}
)
public final class NexoChannelHandler extends NexoCommonsHandler {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final UUID uuid;
   @Nullable
   private Player player;
   @NotNull
   private final HashedComponentRewriter hashRewriter;
   @NotNull
   private final ComponentTransformer componentTransformer;
   @NotNull
   private final ItemTransformer itemTransformer;
   @NotNull
   private final MerchantTransformer merchantTransformer;
   @NotNull
   private final DialogTransformer dialogTransformer;
   @NotNull
   private final Lazy soundMapping$delegate;
   private static final ProtocolInfo<ClientGamePacketListener> protInfo;

   public NexoChannelHandler(@NotNull UUID uuid, @NotNull Locale locale) {
      Intrinsics.checkNotNullParameter(uuid, "uuid");
      Intrinsics.checkNotNullParameter(locale, "locale");
      super();
      this.uuid = uuid;
      this.hashRewriter = new HashedComponentRewriter();
      this.componentTransformer = new ComponentTransformer(NexoChannelHandler::componentTransformer$lambda$0, locale);
      this.itemTransformer = new ItemTransformer(NexoChannelHandler::itemTransformer$lambda$0, locale, this.getComponentTransformer(), this.hashRewriter);
      this.merchantTransformer = new MerchantTransformer(this.getItemTransformer());
      this.dialogTransformer = new DialogTransformer(this.getComponentTransformer(), this.getItemTransformer());
      NexoCommonsHandler $this$iv = this;
      boolean condition$iv = true;
      $this$iv.getPacketTransformers().put(ClientboundEntityPositionSyncPacket.class, NexoChannelHandler$special$$inlined$registerIgnored$default$1.INSTANCE);
      $this$iv = this;
      Function1 reader$iv = NexoChannelHandler::_init_$lambda$0;
      condition$iv = true;
      $this$iv.getPacketTransformers().put(ServerboundClientInformationPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$1(reader$iv));
      $this$iv = this;
      condition$iv = Settings.FORMAT_SCOREBOARD.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$1;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundSetPlayerTeamPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$1(reader$iv));
      }

      $this$iv = this;
      condition$iv = Settings.FORMAT_PLAYER_CHAT.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$2;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundPlayerChatPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$2(reader$iv));
      }

      $this$iv = this;
      condition$iv = Settings.FORMAT_TABLIST.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$3;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundPlayerInfoUpdatePacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$3(reader$iv));
      }

      $this$iv = this;
      reader$iv = NexoChannelHandler::_init_$lambda$4;
      condition$iv = true;
      $this$iv.getPacketTransformers().put(ClientboundShowDialogPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$default$1(reader$iv));
      $this$iv = this;
      condition$iv = Settings.FORMAT_ITEMS.toBool() || Settings.FORMAT_CURSOR.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$5;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ServerboundContainerClickPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$4(reader$iv));
      }

      $this$iv = this;
      condition$iv = Settings.FORMAT_ITEMS.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$6;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundContainerSetContentPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$5(reader$iv));
      }

      $this$iv = this;
      condition$iv = Settings.FORMAT_CURSOR.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$7;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundSetCursorItemPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$6(reader$iv));
      }

      $this$iv = this;
      condition$iv = Settings.FORMAT_VILLAGERS.toBool();
      reader$iv = NexoChannelHandler::_init_$lambda$8;
      if (condition$iv) {
         $this$iv.getPacketTransformers().put(ClientboundMerchantOffersPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$7(reader$iv));
      }

      MechanicsManager this_$iv = MechanicsManager.INSTANCE;
      condition$iv = false;
      Iterator reader$iv = ((Iterable)this_$iv.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory var10000;
      while(true) {
         if (reader$iv.hasNext()) {
            MechanicFactory it$iv = (MechanicFactory)reader$iv.next();
            int var7 = 0;
            var10000 = it$iv;
            if (!(it$iv instanceof FurnitureFactory)) {
               var10000 = null;
            }

            it$iv = (FurnitureFactory)var10000;
            if (it$iv == null) {
               continue;
            }

            var10000 = it$iv;
            break;
         }

         var10000 = null;
         break;
      }

      if (var10000 != null) {
         NexoCommonsHandler $this$iv = this;
         Function1 reader$iv = NexoChannelHandler::_init_$lambda$9;
         condition$iv = true;
         $this$iv.getPacketTransformers().put(ServerboundPickItemFromBlockPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$2(reader$iv));
         $this$iv = this;
         reader$iv = NexoChannelHandler::_init_$lambda$10;
         condition$iv = true;
         $this$iv.getPacketTransformers().put(ServerboundPickItemFromEntityPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$3(reader$iv));
      }

      MechanicsManager this_$iv = MechanicsManager.INSTANCE;
      Iterator $i$f$registerTransformer = ((Iterable)this_$iv.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if ($i$f$registerTransformer.hasNext()) {
            MechanicFactory it$iv = (MechanicFactory)$i$f$registerTransformer.next();
            int var9 = 0;
            var10000 = it$iv;
            if (!(it$iv instanceof FurnitureFactory)) {
               var10000 = null;
            }

            it$iv = (FurnitureFactory)var10000;
            if (it$iv == null) {
               continue;
            }

            var10000 = it$iv;
            break;
         }

         var10000 = null;
         break;
      }

      block: {
         if (!((FurnitureFactory)var10000 != null ? ((FurnitureFactory)var10000).getCustomSounds() : false)) {
            this_$iv = MechanicsManager.INSTANCE;

            block2: {
               for(MechanicFactory it$iv : (Iterable)this_$iv.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int var71 = 0;
                  var10000 = it$iv;
                  if (!(it$iv instanceof CustomBlockFactory)) {
                     var10000 = null;
                  }

                  it$iv = (CustomBlockFactory)var10000;
                  if (it$iv != null) {
                     var10000 = it$iv;
                     break block2;
                  }
               }

               var10000 = null;
            }

            block2: {
               var10000 = (CustomBlockFactory)var10000;
               if (var10000 != null) {
                  CustomBlockFactory.CustomBlockSounds var78 = var10000.getCustomSounds();
                  if (var78 != null) {
                     var79 = var78.getEnabled();
                     break block2;
                  }
               }

               var79 = false;
            }

            if (!var79) {
               var80 = false;
               break block;
            }
         }

         var80 = true;
      }

      boolean customSounds = var80;
      NexoCommonsHandler this_$iv = this;
      Function1 transformer$iv = NexoChannelHandler::_init_$lambda$11;
      int $i$f$registerTransformer = 0;
      if (customSounds) {
         this_$iv.getPacketTransformers().put(ClientboundSoundPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$8(transformer$iv));
      }

      this.soundMapping$delegate = LazyKt.lazy(NexoChannelHandler::soundMapping_delegate$lambda$0);
   }

   @Nullable
   public Player getPlayer() {
      if (this.player == null) {
         this.player = Bukkit.getPlayer(this.uuid);
      }

      return this.player;
   }

   @NotNull
   public ProtocolInfo<ClientGamePacketListener> getProtocolInfo() {
      ProtocolInfo var10000 = protInfo;
      Intrinsics.checkNotNullExpressionValue(var10000, "protInfo");
      return var10000;
   }

   @NotNull
   public ComponentTransformer getComponentTransformer() {
      return this.componentTransformer;
   }

   @NotNull
   public ItemTransformer getItemTransformer() {
      return this.itemTransformer;
   }

   @NotNull
   public MerchantTransformer getMerchantTransformer() {
      return this.merchantTransformer;
   }

   @NotNull
   public final DialogTransformer getDialogTransformer() {
      return this.dialogTransformer;
   }

   private final void handlePickItem(FurnitureMechanic mechanic, ItemStack item) {
      if (mechanic != null && item != null) {
         Player var10000 = this.getPlayer();
         if (var10000 != null) {
            Player player = var10000;
            PlayerInventory var8 = player.getInventory();
            Intrinsics.checkNotNullExpressionValue(var8, "getInventory(...)");
            PlayerInventory inventory = var8;
            int currentHeld = inventory.getHeldItemSlot();

            for(int i = 0; i < 9; ++i) {
               if (Intrinsics.areEqual(NexoItems.idFromItem(inventory.getItem(i)), mechanic.getItemID())) {
                  inventory.setHeldItemSlot(i);
                  return;
               }
            }

            if (player.getGameMode() == GameMode.CREATIVE) {
               ItemStack var9 = inventory.getItem(currentHeld);
               if (!(var9 != null ? !var9.isEmpty() : false)) {
                  inventory.setItem(currentHeld, item);
               } else {
                  for(int i = 0; i < 9; ++i) {
                     var9 = inventory.getItem(i);
                     if (!(var9 != null ? !var9.isEmpty() : false)) {
                        inventory.setItem(i, item);
                        inventory.setHeldItemSlot(i);
                        return;
                     }
                  }

               }
            }
         }
      }
   }

   private final Component transform(Component $this$transform, boolean formatPlainContents) {
      return this.getComponentTransformer().transform($this$transform, formatPlainContents, true);
   }

   private final net.minecraft.world.item.ItemStack transform(net.minecraft.world.item.ItemStack $this$transform, String originalName) {
      return this.getItemTransformer().transform($this$transform, originalName);
   }

   private final ConcurrentHashMap<String, ResourceLocation> getSoundMapping() {
      Lazy var1 = this.soundMapping$delegate;
      return (ConcurrentHashMap)var1.getValue();
   }

   private static final Player componentTransformer$lambda$0(NexoChannelHandler this$0) {
      return this$0.getPlayer();
   }

   private static final Player itemTransformer$lambda$0(NexoChannelHandler this$0) {
      return this$0.getPlayer();
   }

   private static final Unit _init_$lambda$0(NexoChannelHandler this$0, ServerboundClientInformationPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      ComponentTransformer var10000 = this$0.getComponentTransformer();
      String var10001 = packet.information().language();
      Intrinsics.checkNotNullExpressionValue(var10001, "language(...)");
      Locale var3 = Locale.forLanguageTag(StringsKt.replace(var10001, "_", "-", false));
      Intrinsics.checkNotNullExpressionValue(var3, "forLanguageTag(...)");
      var10000.setLocale$com_nexomc_v1_21_R8(var3);
      ItemTransformer var2 = this$0.getItemTransformer();
      String var4 = packet.information().language();
      Intrinsics.checkNotNullExpressionValue(var4, "language(...)");
      Locale var5 = Locale.forLanguageTag(StringsKt.replace(var4, "_", "-", false));
      Intrinsics.checkNotNullExpressionValue(var5, "forLanguageTag(...)");
      var2.setLocale$com_nexomc_v1_21_R8(var5);
      return Unit.INSTANCE;
   }

   private static final void lambda$1$0(Function2 $tmp0, Object p0, String p1) {
      $tmp0.invoke(p0, p1);
   }

   private static final Packet _init_$lambda$1(NexoChannelHandler this$0, ClientboundSetPlayerTeamPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      Optional var10000 = packet.getParameters();
      if (var10000 != null) {
         ClientboundSetPlayerTeamPacket.Parameters var11 = (ClientboundSetPlayerTeamPacket.Parameters)OptionalsKt.getOrNull(var10000);
         if (var11 != null) {
            ClientboundSetPlayerTeamPacket.Parameters parameters = var11;
            ClientboundSetPlayerTeamPacket.Action var12 = packet.getTeamAction();
            int var13;
            if (var12 != null) {
               var13 = var12.ordinal();
            } else {
               ClientboundSetPlayerTeamPacket.Action var14 = packet.getPlayerAction();
               var13 = var14 != null ? var14.ordinal() : 2;
            }

            int method = var13;
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), NexoCommonsHandler.Companion.getRegistryAccess());
            boolean shouldHavePlayerList = method == 0 || method == 3 || method == 4;
            boolean shouldHaveParameters = method == 0 || method == 2;

            Packet var7;
            try {
               buf.writeUtf(packet.getName());
               buf.writeByte(method);
               if (shouldHaveParameters) {
                  StreamCodec var15 = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  Component var10003 = parameters.getDisplayName();
                  Intrinsics.checkNotNullExpressionValue(var10003, "getDisplayName(...)");
                  var15.encode(buf, this$0.transform(var10003));
                  buf.writeByte(parameters.getOptions());
                  var15 = Visibility.STREAM_CODEC;
                  Team.Visibility var10002 = parameters.getNametagVisibility();
                  if (var10002 == null) {
                     var10002 = Visibility.ALWAYS;
                  }

                  var15.encode(buf, var10002);
                  var15 = CollisionRule.STREAM_CODEC;
                  Team.CollisionRule var20 = parameters.getCollisionRule();
                  if (var20 == null) {
                     var20 = CollisionRule.ALWAYS;
                  }

                  var15.encode(buf, var20);
                  buf.writeEnum((Enum)parameters.getColor());
                  var15 = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  var10003 = parameters.getPlayerPrefix();
                  Intrinsics.checkNotNullExpressionValue(var10003, "getPlayerPrefix(...)");
                  var15.encode(buf, this$0.transform(var10003));
                  var15 = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  var10003 = parameters.getPlayerSuffix();
                  Intrinsics.checkNotNullExpressionValue(var10003, "getPlayerSuffix(...)");
                  var15.encode(buf, this$0.transform(var10003));
               }

               if (shouldHavePlayerList) {
                  buf.writeCollection(packet.getPlayers(), NexoChannelHandler::lambda$1$0);
               }

               var7 = (Packet)ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(buf);
            } finally {
               buf.release();
            }

            return var7;
         }
      }

      return (Packet)packet;
   }

   private static final Packet _init_$lambda$2(NexoChannelHandler this$0, ClientboundPlayerChatPacket packet) {
      Component var9;
      block: {
         Intrinsics.checkNotNullParameter(packet, "packet");
         Player sender = Bukkit.getPlayer(packet.sender());
         var9 = packet.unsignedContent();
         if (var9 != null) {
            var9 = this$0.transform(var9, false);
            if (var9 != null) {
               var9 = this$0.unescapeGlyphs(var9);
               if (var9 != null) {
                  break block;
               }
            }
         }

         net.kyori.adventure.text.Component var14;
         Player var10001;
         block2: {
            String content = packet.body().content();
            int var6 = 0;
            var10 = ComponentProjections.INSTANCE;
            var10001 = this$0.getPlayer();
            if (Settings.FORMAT_CHAT_STANDARD_TAGS.toBool()) {
               String var10002 = packet.body().content();
               Intrinsics.checkNotNullExpressionValue(var10002, "content(...)");
               if (StringsKt.contains((CharSequence)var10002, (CharSequence)"<", false)) {
                  var10002 = packet.body().content();
                  Intrinsics.checkNotNullExpressionValue(var10002, "content(...)");
                  var14 = AdventureUtilsKt.deserialize(var10002, (Pointered)sender);
                  break block2;
               }
            }

            Glyph.Companion var13 = Glyph.Companion;
            Intrinsics.checkNotNull(content);
            if (!var13.containsTagOrPlaceholder(content) && !ShiftTag.INSTANCE.containsTag(content) && !PapiTag.INSTANCE.containsTag(content)) {
               return (Packet)packet;
            }

            net.kyori.adventure.text.Component var7 = sender != null ? AdventureUtils.INSTANCE.getNEXO_SERIALIZER().deserialize(packet.body().content(), (Pointered)sender) : AdventureUtils.INSTANCE.getNEXO_SERIALIZER().deserialize(packet.body().content());
            Intrinsics.checkNotNull(var7);
            var14 = var7;
         }

         var9 = PaperAdventure.asVanilla(var10.apply(var10001, var14));
      }

      Component component = var9;
      ResourceKey var11 = (ResourceKey)packet.chatType().chatType().unwrapKey().get();
      RegistryAccess var12 = NexoCommonsHandler.Companion.getRegistryAccess();
      Component var10003 = packet.chatType().name();
      Intrinsics.checkNotNullExpressionValue(var10003, "name(...)");
      ChatType.Bound chatType = ChatType.bind(var11, var12, this$0.transform(var10003));
      return (Packet)(new ClientboundPlayerChatPacket(packet.globalIndex(), packet.sender(), packet.index(), packet.signature(), packet.body(), component, packet.filterMask(), chatType));
   }

   private static final Packet _init_$lambda$3(NexoChannelHandler this$0, ClientboundPlayerInfoUpdatePacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      EnumSet var10000 = packet.actions();
      List var10001 = packet.entries();
      Intrinsics.checkNotNullExpressionValue(var10001, "entries(...)");
      Iterable $this$map$iv = (Iterable)var10001;
      EnumSet var11 = var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         ClientboundPlayerInfoUpdatePacket.Entry it = (ClientboundPlayerInfoUpdatePacket.Entry)item$iv$iv;
         int var10 = 0;
         UUID var10002 = it.profileId();
         GameProfile var10003 = it.profile();
         boolean var10004 = it.listed();
         int var10005 = it.latency();
         GameType var10006 = it.gameMode();
         Component var10007 = it.displayName();
         destination$iv$iv.add(new ClientboundPlayerInfoUpdatePacket.Entry(var10002, var10003, var10004, var10005, var10006, var10007 != null ? this$0.transform(var10007) : null, it.showHat(), it.listOrder(), it.chatSession()));
      }

      List var13 = (List)destination$iv$iv;
      return (Packet)(new ClientboundPlayerInfoUpdatePacket(var11, var13));
   }

   private static final Packet _init_$lambda$4(NexoChannelHandler this$0, ClientboundShowDialogPacket packet) {
      Holder var8;
      block: {
         Intrinsics.checkNotNullParameter(packet, "packet");
         Dialog var10000 = PaperDialog.minecraftToBukkit((net.minecraft.server.dialog.Dialog)packet.dialog().value());
         Intrinsics.checkNotNullExpressionValue(var10000, "minecraftToBukkit(...)");
         Dialog paperDialog = var10000;
         NamespacedKey key = NexoDialogs.INSTANCE.getDialogRegistry().getKey((Keyed)paperDialog);
         var10000 = NexoDialogs.INSTANCE.reloadedDialog((Key)key);
         if (var10000 != null) {
            Dialog p0 = var10000;
            int var6 = 0;
            var8 = PaperDialog.bukkitToMinecraftHolder(p0);
            if (var8 != null) {
               break block;
            }
         }

         var8 = packet.dialog();
      }

      Holder dialog = var8;
      DialogTransformer var10002 = this$0.dialogTransformer;
      Intrinsics.checkNotNull(dialog);
      return (Packet)(new ClientboundShowDialogPacket(var10002.transform(dialog)));
   }

   private static final Packet _init_$lambda$5(NexoChannelHandler this$0, ServerboundContainerClickPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      boolean changed = false;
      Int2ObjectOpenHashMap changedSlots = new Int2ObjectOpenHashMap(packet.changedSlots().size());
      ObjectSet var10000 = packet.changedSlots().int2ObjectEntrySet();
      Intrinsics.checkNotNullExpressionValue(var10000, "int2ObjectEntrySet(...)");
      Iterable $this$forEach$iv = (Iterable)var10000;

      for(Object element$iv : $this$forEach$iv) {
         Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)element$iv;
         int var9 = 0;
         HashedComponentRewriter var12 = this$0.hashRewriter;
         Object var10001 = entry.getValue();
         Intrinsics.checkNotNullExpressionValue(var10001, "<get-value>(...)");
         HashedStack rewritten = var12.rewrite((HashedStack)var10001);
         if (rewritten != entry.getValue()) {
            changed = true;
         }

         changedSlots.put(entry.getIntKey(), rewritten);
      }

      HashedComponentRewriter var13 = this$0.hashRewriter;
      HashedStack var14 = packet.carriedItem();
      Intrinsics.checkNotNullExpressionValue(var14, "carriedItem(...)");
      HashedStack carriedItem = var13.rewrite(var14);
      if (!changed && carriedItem == packet.carriedItem()) {
         return (Packet)packet;
      } else {
         return (Packet)(new ServerboundContainerClickPacket(packet.containerId(), packet.stateId(), packet.slotNum(), packet.buttonNum(), packet.clickType(), (Int2ObjectMap)changedSlots, carriedItem));
      }
   }

   private static final Packet _init_$lambda$6(NexoChannelHandler this$0, ClientboundContainerSetContentPacket packet) {
      Inventory var19;
      block: {
         Intrinsics.checkNotNullParameter(packet, "packet");
         Player var10000 = this$0.getPlayer();
         if (var10000 != null) {
            InventoryView var18 = var10000.getOpenInventory();
            if (var18 != null) {
               var19 = var18.getTopInventory();
               break block;
            }
         }

         var19 = null;
      }

      block: {
         Inventory var4 = var19;
         AnvilInventory var20 = var4 instanceof AnvilInventory ? (AnvilInventory)var4 : null;
         if ((var4 instanceof AnvilInventory ? (AnvilInventory)var4 : null) != null) {
            ItemStack var21 = var20.getFirstItem();
            if (var21 != null) {
               CraftItemStack var22 = ItemUtils.Companion.toCraftStack(var21);
               if (var22 != null) {
                  var23 = var22.handle;
                  break block;
               }
            }
         }

         var23 = null;
      }

      net.minecraft.world.item.ItemStack firstItem = var23;
      int var24 = packet.containerId();
      int var10001 = packet.stateId();
      List var10002 = packet.items();
      Intrinsics.checkNotNullExpressionValue(var10002, "items(...)");
      Iterable $this$mapTo$iv = (Iterable)var10002;
      NonNullList var26 = NonNullList.create();
      Intrinsics.checkNotNullExpressionValue(var26, "create(...)");
      Collection var17 = (Collection)var26;
      int var11 = var10001;
      int var10 = var24;

      for(Object item$iv : $this$mapTo$iv) {
         net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack)item$iv;
         int var9 = 0;
         net.minecraft.world.item.ItemStack var25;
         if (!item.equals(firstItem)) {
            Intrinsics.checkNotNull(item);
            var25 = this$0.transform(item);
         } else {
            block: {
               Intrinsics.checkNotNull(item);
               CustomData var27 = (CustomData)item.get(DataComponents.CUSTOM_DATA);
               if (var27 != null) {
                  CompoundTag var28 = var27.getUnsafe();
                  if (var28 != null) {
                     Optional var29 = var28.getString(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING());
                     if (var29 != null) {
                        var30 = (String)OptionalsKt.getOrNull(var29);
                        break block;
                     }
                  }
               }

               var30 = null;
            }

            if (var30 == null) {
               var30 = "";
            }

            var25 = this$0.transform(item, var30);
         }

         var17.add(var25);
      }

      List var31 = (List)var17;
      net.minecraft.world.item.ItemStack var10004 = packet.carriedItem();
      Intrinsics.checkNotNullExpressionValue(var10004, "carriedItem(...)");
      net.minecraft.world.item.ItemStack var13 = this$0.transform(var10004);
      List var14 = var31;
      return (Packet)(new ClientboundContainerSetContentPacket(var10, var11, var14, var13));
   }

   private static final Packet _init_$lambda$7(NexoChannelHandler this$0, ClientboundSetCursorItemPacket it) {
      Intrinsics.checkNotNullParameter(it, "it");
      net.minecraft.world.item.ItemStack var10003 = it.contents();
      Intrinsics.checkNotNullExpressionValue(var10003, "contents(...)");
      return (Packet)(new ClientboundSetCursorItemPacket(this$0.transform(var10003)));
   }

   private static final Packet _init_$lambda$8(NexoChannelHandler this$0, ClientboundMerchantOffersPacket it) {
      Intrinsics.checkNotNullParameter(it, "it");
      int var10002 = it.getContainerId();
      MerchantOffers var10004 = it.getOffers();
      Intrinsics.checkNotNullExpressionValue(var10004, "getOffers(...)");
      return (Packet)(new ClientboundMerchantOffersPacket(var10002, this$0.transform(var10004), it.getVillagerLevel(), it.getVillagerXp(), it.showProgress(), it.canRestock()));
   }

   private static final Unit _init_$lambda$9(NexoChannelHandler this$0, ServerboundPickItemFromBlockPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      BlockLocation location = new BlockLocation(packet.pos().getX(), packet.pos().getY(), packet.pos().getZ());
      FurnitureMechanic mechanic = IFurniturePacketManager.Companion.mechanicFromHitbox(location);
      ItemBuilder var10000 = NexoItems.itemFromId(mechanic != null ? mechanic.getItemID() : null);
      ItemStack item = var10000 != null ? var10000.build() : null;
      this$0.handlePickItem(mechanic, item);
      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$10(NexoChannelHandler this$0, ServerboundPickItemFromEntityPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      FurnitureMechanic mechanic = IFurniturePacketManager.Companion.mechanicFromHitbox(packet.id());
      ItemBuilder var10000 = NexoItems.itemFromId(mechanic != null ? mechanic.getItemID() : null);
      ItemStack item = var10000 != null ? var10000.build() : null;
      this$0.handlePickItem(mechanic, item);
      return Unit.INSTANCE;
   }

   private static final Packet _init_$lambda$11(NexoChannelHandler this$0, ClientboundSoundPacket packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      Optional var10000 = packet.getSound().unwrap().right();
      Intrinsics.checkNotNullExpressionValue(var10000, "right(...)");
      SoundEvent var7 = (SoundEvent)OptionalsKt.getOrNull(var10000);
      if (var7 == null) {
         return (Packet)packet;
      } else {
         SoundEvent event = var7;
         ResourceLocation var8 = (ResourceLocation)this$0.getSoundMapping().get(event.location().toString());
         if (var8 == null) {
            return (Packet)packet;
         } else {
            block: {
               ResourceLocation newSound = var8;
               Optional var9 = event.fixedRange();
               Intrinsics.checkNotNullExpressionValue(var9, "fixedRange(...)");
               Float var10 = (Float)OptionalsKt.getOrNull(var9);
               if (var10 != null) {
                  float it = ((Number)var10).floatValue();
                  int var6 = 0;
                  var11 = SoundEvent.createFixedRangeEvent(newSound, it);
                  if (var11 != null) {
                     break block;
                  }
               }

               var11 = SoundEvent.createVariableRangeEvent(newSound);
            }

            SoundEvent newEvent = var11;
            return (Packet)(new ClientboundSoundPacket(Holder.direct(newEvent), packet.getSource(), packet.getX(), packet.getY(), packet.getZ(), packet.getVolume(), packet.getPitch(), packet.getSeed()));
         }
      }
   }

   private static final ConcurrentHashMap soundMapping_delegate$lambda$0() {
      ConcurrentHashMap var0 = new ConcurrentHashMap();
      ConcurrentHashMap $this$soundMapping_delegate_u24lambda_u240_u240 = var0;
      int var2 = 0;
      Iterable $this$forEachIndexed$iv = (Iterable)BlockSounds.Companion.getVANILLA_WOOD_SOUNDS();
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int i = index$iv++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String vanilla = (String)item$iv;
         int var11 = 0;
         String var24 = (String)CollectionsKt.getOrNull(BlockSounds.Companion.getNEXO_WOOD_SOUNDS(), i);
         if (var24 != null) {
            String it = var24;
            int var13 = 0;
            $this$soundMapping_delegate_u24lambda_u240_u240.put(vanilla, ResourceLocation.parse(it));
         }
      }

      $this$forEachIndexed$iv = (Iterable)BlockSounds.Companion.getVANILLA_STONE_SOUNDS();
      index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int i = index$iv++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String vanilla = (String)item$iv;
         int var21 = 0;
         String var26 = (String)CollectionsKt.getOrNull(BlockSounds.Companion.getNEXO_STONE_SOUNDS(), i);
         if (var26 != null) {
            String it = var26;
            int var23 = 0;
            $this$soundMapping_delegate_u24lambda_u240_u240.put(vanilla, ResourceLocation.parse(it));
         }
      }

      return var0;
   }

   static {
      protInfo = GameProtocols.CLIENTBOUND_TEMPLATE.bind(NexoCommonsHandler.Companion.getDecorator());
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R2\u0010\u0004\u001a&\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006 \u0007*\u0012\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R8/packets/NexoChannelHandler$Companion;", "", "<init>", "()V", "protInfo", "Lnet/minecraft/network/ProtocolInfo;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "kotlin.jvm.PlatformType", "com.nexomc:v1_21_R8"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
