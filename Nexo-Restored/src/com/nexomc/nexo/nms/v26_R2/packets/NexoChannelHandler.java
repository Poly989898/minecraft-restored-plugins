package com.nexomc.nexo.nms.v26_R2.packets;

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
import com.nexomc.nexo.nms.v26_R2.ItemUtils;
import com.nexomc.nexo.nms.v26_R2.packets.transformers.ComponentTransformer;
import com.nexomc.nexo.nms.v26_R2.packets.transformers.DialogTransformer;
import com.nexomc.nexo.nms.v26_R2.packets.transformers.ItemTransformer;
import com.nexomc.nexo.nms.v26_R2.packets.transformers.MerchantTransformer;
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
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 :2\u00020\u0001:\u0001:B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001c\u0010&\u001a\u00020'2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0002J\u0016\u0010,\u001a\u00020-*\u00020-2\b\b\u0002\u0010.\u001a\u00020/H\u0002J\u0016\u0010,\u001a\u000200*\u0002002\b\u00101\u001a\u0004\u0018\u000102H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R*\u0010\n\u001a\u0004\u0018\u00010\t2\b\u0010\b\u001a\u0004\u0018\u00010\t8V@RX\u0096\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00108VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\u00020\u0017X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u001bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u001fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\"\u001a\u00020#¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R'\u00103\u001a\u000e\u0012\u0004\u0012\u000202\u0012\u0004\u0012\u000205048BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b8\u00109\u001a\u0004\b6\u00107¨\u0006;"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/NexoChannelHandler;", "Lcom/nexomc/nexo/nms/commons/packets/NexoCommonsHandler;", "uuid", "Ljava/util/UUID;", "locale", "Ljava/util/Locale;", "<init>", "(Ljava/util/UUID;Ljava/util/Locale;)V", "value", "Lorg/bukkit/entity/Player;", "player", "getPlayer", "()Lorg/bukkit/entity/Player;", "setPlayer", "(Lorg/bukkit/entity/Player;)V", "protocolInfo", "Lnet/minecraft/network/ProtocolInfo;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "getProtocolInfo", "()Lnet/minecraft/network/ProtocolInfo;", "hashRewriter", "Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;", "componentTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;", "getComponentTransformer", "()Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;", "itemTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;", "getItemTransformer", "()Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;", "merchantTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/MerchantTransformer;", "getMerchantTransformer", "()Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/MerchantTransformer;", "dialogTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/DialogTransformer;", "getDialogTransformer", "()Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/DialogTransformer;", "handlePickItem", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "item", "Lorg/bukkit/inventory/ItemStack;", "transform", "Lnet/minecraft/network/chat/Component;", "formatPlainContents", "", "Lnet/minecraft/world/item/ItemStack;", "originalName", "", "soundMapping", "Ljava/util/concurrent/ConcurrentHashMap;", "Lnet/minecraft/resources/Identifier;", "getSoundMapping", "()Ljava/util/concurrent/ConcurrentHashMap;", "soundMapping$delegate", "Lkotlin/Lazy;", "Companion", "com.nexomc:v26_R2"}
)
public final class NexoChannelHandler extends NexoCommonsHandler {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final UUID uuid;
   @NotNull
   private Locale locale;
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
   @NotNull
   private static final ProtocolInfo<ClientGamePacketListener> protInfo;

   public NexoChannelHandler(@NotNull UUID uuid, @NotNull Locale locale) {
      boolean flag;
      boolean flag2;
      super();
      this.uuid = uuid;
      this.locale = locale;
      this.hashRewriter = new HashedComponentRewriter();
      this.componentTransformer = new ComponentTransformer((() -> NexoChannelHandler.componentTransformer$lambda$0(this)), this.locale);
      this.itemTransformer = new ItemTransformer((() -> NexoChannelHandler.itemTransformer$lambda$0(this)), this.locale, this.getComponentTransformer(), this.hashRewriter);
      this.merchantTransformer = new MerchantTransformer(this.getItemTransformer());
      this.dialogTransformer = new DialogTransformer(this.getComponentTransformer(), this.getItemTransformer());
      NexoCommonsHandler nexoCommonsHandler = this;
      boolean condition = true;
      nexoCommonsHandler.getPacketTransformers().put(ClientboundEntityPositionSyncPacket.class, NexoChannelHandler$special$$inlined$registerIgnored$default$1.INSTANCE);
      nexoCommonsHandler = this;
      Function1 reader = (a0_ -> NexoChannelHandler._init_$lambda$0(this, (ServerboundClientInformationPacket)a0_));
      condition = true;
      nexoCommonsHandler.getPacketTransformers().put(ServerboundClientInformationPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$1(reader));
      nexoCommonsHandler = this;
      condition = Settings.FORMAT_SCOREBOARD.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$1(this, (ClientboundSetPlayerTeamPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundSetPlayerTeamPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$1(reader));
      }

      nexoCommonsHandler = this;
      condition = Settings.FORMAT_PLAYER_CHAT.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$2(this, (ClientboundPlayerChatPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundPlayerChatPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$2(reader));
      }

      nexoCommonsHandler = this;
      condition = Settings.FORMAT_TABLIST.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$3(this, (ClientboundPlayerInfoUpdatePacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundPlayerInfoUpdatePacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$3(reader));
      }

      nexoCommonsHandler = this;
      reader = (a0_ -> NexoChannelHandler._init_$lambda$4(this, (ClientboundShowDialogPacket)a0_));
      condition = true;
      nexoCommonsHandler.getPacketTransformers().put(ClientboundShowDialogPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$default$1(reader));
      nexoCommonsHandler = this;
      condition = Settings.FORMAT_ITEMS.toBool() || Settings.FORMAT_CURSOR.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$5(this, (ServerboundContainerClickPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ServerboundContainerClickPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$4(reader));
      }

      nexoCommonsHandler = this;
      condition = Settings.FORMAT_ITEMS.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$6(this, (ClientboundContainerSetContentPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundContainerSetContentPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$5(reader));
      }

      nexoCommonsHandler = this;
      condition = Settings.FORMAT_CURSOR.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$7(this, (ClientboundSetCursorItemPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundSetCursorItemPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$6(reader));
      }

      nexoCommonsHandler = this;
      condition = Settings.FORMAT_VILLAGERS.toBool();
      reader = (a0_ -> NexoChannelHandler._init_$lambda$8(this, (ClientboundMerchantOffersPacket)a0_));
      if (condition) {
         nexoCommonsHandler.getPacketTransformers().put(ClientboundMerchantOffersPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$7(reader));
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
         NexoCommonsHandler nexoCommonsHandler2 = this;
         Function1 function1 = (a0_ -> NexoChannelHandler._init_$lambda$9(this, (ServerboundPickItemFromBlockPacket)a0_));
         condition = true;
         nexoCommonsHandler2.getPacketTransformers().put(ServerboundPickItemFromBlockPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$2(function1));
         nexoCommonsHandler2 = this;
         function1 = (a0_ -> NexoChannelHandler._init_$lambda$10(this, (ServerboundPickItemFromEntityPacket)a0_));
         condition = true;
         nexoCommonsHandler2.getPacketTransformers().put(ServerboundPickItemFromEntityPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$3(function1));
         nexoCommonsHandler2 = this;
         function1 = (a0_ -> NexoChannelHandler._init_$lambda$11(this, (ClientboundForgetLevelChunkPacket)a0_));
         condition = true;
         nexoCommonsHandler2.getPacketTransformers().put(ClientboundForgetLevelChunkPacket.class, new NexoChannelHandler$special$$inlined$registerReader$default$4(function1));
         nexoCommonsHandler2 = this;
         function1 = (a0_ -> NexoChannelHandler._init_$lambda$12(this, (ClientboundBlockUpdatePacket)a0_));
         condition = true;
         nexoCommonsHandler2.getPacketTransformers().put(ClientboundBlockUpdatePacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$default$2(function1));
      }

      MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;
      Iterator iterator2 = ((Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (iterator2.hasNext()) {
            MechanicFactory mechanicFactory2 = (MechanicFactory)iterator2.next();
            int value2 = 0;
            mechanicFactory = mechanicFactory2;
            if (!(mechanicFactory2 instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            mechanicFactory2 = (FurnitureFactory)mechanicFactory;
            if (mechanicFactory2 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory2;
            break;
         }

         mechanicFactory = null;
         break;
      }

      block: {
         if (!((FurnitureFactory)mechanicFactory != null ? ((FurnitureFactory)mechanicFactory).getCustomSounds() : false)) {
            mechanicsManager2 = MechanicsManager.INSTANCE;

            block2: {
               for(MechanicFactory mechanicFactory3 : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int value3 = 0;
                  mechanicFactory = mechanicFactory3;
                  if (!(mechanicFactory3 instanceof CustomBlockFactory)) {
                     mechanicFactory = null;
                  }

                  mechanicFactory3 = (CustomBlockFactory)mechanicFactory;
                  if (mechanicFactory3 != null) {
                     mechanicFactory = mechanicFactory3;
                     break block2;
                  }
               }

               mechanicFactory = null;
            }

            block2: {
               mechanicFactory = (CustomBlockFactory)mechanicFactory;
               if (mechanicFactory != null) {
                  CustomBlockFactory.CustomBlockSounds customBlockSounds = ((CustomBlockFactory)mechanicFactory).getCustomSounds();
                  if (customBlockSounds != null) {
                     flag = customBlockSounds.getEnabled();
                     break block2;
                  }
               }

               flag = false;
            }

            if (!flag) {
               flag2 = false;
               break block;
            }
         }

         flag2 = true;
      }

      boolean customSounds = flag2;
      NexoCommonsHandler nexoCommonsHandler3 = this;
      Function1 transformer = (a0_ -> NexoChannelHandler._init_$lambda$13(this, (ClientboundSoundPacket)a0_));
      if (customSounds) {
         nexoCommonsHandler3.getPacketTransformers().put(ClientboundSoundPacket.class, new NexoChannelHandler$special$$inlined$registerTransformer$8(transformer));
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

   private void setPlayer(Player value) {
      Locale locale2;
      block: {
         this.setPlayer(value);
         Player player2 = this.getPlayer();
         if (player2 != null) {
            locale2 = player2.locale();
            if (locale2 != null) {
               break block;
            }
         }

         locale2 = this.locale;
      }

      this.locale = locale2;
   }

   @NotNull
   public ProtocolInfo<ClientGamePacketListener> getProtocolInfo() {
      return protInfo;
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
         Player player2 = this.getPlayer();
         if (player2 != null) {
            Player player = player2;
            PlayerInventory playerInventory = player.getInventory();
            PlayerInventory inventory = playerInventory;
            int currentHeld = inventory.getHeldItemSlot();

            for(int i = 0; i < 9; ++i) {
               if (Intrinsics.areEqual(NexoItems.idFromItem(inventory.getItem(i)), mechanic.getItemID())) {
                  inventory.setHeldItemSlot(i);
                  return;
               }
            }

            if (player.getGameMode() == GameMode.CREATIVE) {
               ItemStack itemStack = inventory.getItem(currentHeld);
               if (!(itemStack != null ? !itemStack.isEmpty() : false)) {
                  inventory.setItem(currentHeld, item);
               } else {
                  for(int i = 0; i < 9; ++i) {
                     itemStack = inventory.getItem(i);
                     if (!(itemStack != null ? !itemStack.isEmpty() : false)) {
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

   private final Component transform(Component component, boolean formatPlainContents) {
      return this.getComponentTransformer().transform(component, formatPlainContents, true);
   }

   private final net.minecraft.world.item.ItemStack transform(net.minecraft.world.item.ItemStack itemStack, String originalName) {
      return this.getItemTransformer().transform(itemStack, originalName);
   }

   private final ConcurrentHashMap<String, Identifier> getSoundMapping() {
      Lazy lazy = this.soundMapping$delegate;
      return (ConcurrentHashMap)lazy.getValue();
   }

   private static final Player componentTransformer$lambda$0(NexoChannelHandler nexoChannelHandler) {
      return nexoChannelHandler.getPlayer();
   }

   private static final Player itemTransformer$lambda$0(NexoChannelHandler nexoChannelHandler) {
      return nexoChannelHandler.getPlayer();
   }

   private static final Unit _init_$lambda$0(NexoChannelHandler nexoChannelHandler, ServerboundClientInformationPacket packet) {
      ComponentTransformer componentTransformer2 = nexoChannelHandler.getComponentTransformer();
      String language = packet.information().language();
      Locale locale2 = Locale.forLanguageTag(StringsKt.replace(language, "_", "-", false));
      componentTransformer2.setLocale$com_nexomc_v26_R2(locale2);
      ItemTransformer itemTransformer2 = nexoChannelHandler.getItemTransformer();
      String str = packet.information().language();
      Locale locale3 = Locale.forLanguageTag(StringsKt.replace(str, "_", "-", false));
      itemTransformer2.setLocale$com_nexomc_v26_R2(locale3);
      return Unit.INSTANCE;
   }

   private static final void lambda$1$0(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   private static final Packet _init_$lambda$1(NexoChannelHandler nexoChannelHandler, ClientboundSetPlayerTeamPacket packet) {
      Optional optional = packet.getParameters();
      if (optional != null) {
         ClientboundSetPlayerTeamPacket.Parameters orNull = (ClientboundSetPlayerTeamPacket.Parameters)OptionalsKt.getOrNull(optional);
         if (orNull != null) {
            ClientboundSetPlayerTeamPacket.Parameters parameters = orNull;
            ClientboundSetPlayerTeamPacket.Action teamAction = packet.getTeamAction();
            int value;
            if (teamAction != null) {
               value = teamAction.ordinal();
            } else {
               ClientboundSetPlayerTeamPacket.Action playerAction = packet.getPlayerAction();
               value = playerAction != null ? playerAction.ordinal() : 2;
            }

            int method = value;
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), NexoCommonsHandler.Companion.getRegistryAccess());
            boolean shouldHavePlayerList = method == 0 || method == 3 || method == 4;
            boolean shouldHaveParameters = method == 0 || method == 2;

            Packet packet2;
            try {
               buf.writeUtf(packet.getName());
               buf.writeByte(method);
               if (shouldHaveParameters) {
                  StreamCodec streamCodec = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  Component displayName = parameters.displayName();
                  streamCodec.encode(buf, nexoChannelHandler.transform(displayName));
                  buf.writeByte(parameters.options());
                  streamCodec = Visibility.STREAM_CODEC;
                  Team.Visibility nameTagVisibility = parameters.nameTagVisibility();
                  if (nameTagVisibility == null) {
                     nameTagVisibility = Visibility.ALWAYS;
                  }

                  streamCodec.encode(buf, nameTagVisibility);
                  streamCodec = CollisionRule.STREAM_CODEC;
                  Team.CollisionRule collisionRule = parameters.collisionRule();
                  if (collisionRule == null) {
                     collisionRule = CollisionRule.ALWAYS;
                  }

                  streamCodec.encode(buf, collisionRule);
                  parameters.color().ifPresent(color -> buf.writeEnum((Enum)color));
                  streamCodec = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  displayName = parameters.playerPrefix();
                  streamCodec.encode(buf, nexoChannelHandler.transform(displayName));
                  streamCodec = ComponentSerialization.TRUSTED_STREAM_CODEC;
                  displayName = parameters.playerSuffix();
                  streamCodec.encode(buf, nexoChannelHandler.transform(displayName));
               }

               if (shouldHavePlayerList) {
                  buf.writeVarInt(packet.getPlayers().size());
                  for(String playerName : packet.getPlayers()) {
                     buf.writeUtf(playerName);
                  }
               }

               packet2 = (Packet)ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(buf);
            } finally {
               buf.release();
            }

            return packet2;
         }
      }

      return (Packet)packet;
   }

   private static final Packet _init_$lambda$2(NexoChannelHandler nexoChannelHandler, ClientboundPlayerChatPacket packet) {
      com.nexomc.nexo.api.ComponentProjections componentProjections;
      Component component2;
      block: {
         Player sender = Bukkit.getPlayer(packet.sender());
         component2 = packet.unsignedContent().orElse(null);
         if (component2 != null) {
            component2 = nexoChannelHandler.transform(component2, false);
            if (component2 != null) {
               component2 = nexoChannelHandler.unescapeGlyphs(component2);
               if (component2 != null) {
                  break block;
               }
            }
         }

         net.kyori.adventure.text.Component component3;
         Player player2;
         block2: {
            String content = packet.body().content();
            int value = 0;
            componentProjections = ComponentProjections.INSTANCE;
            player2 = nexoChannelHandler.getPlayer();
            if (Settings.FORMAT_CHAT_STANDARD_TAGS.toBool()) {
               String str = packet.body().content();
               if (StringsKt.contains((CharSequence)str, (CharSequence)"<", false)) {
                  str = packet.body().content();
                  component3 = AdventureUtilsKt.deserialize(str, (Pointered)sender);
                  break block2;
               }
            }

            Glyph.Companion companion = Glyph.Companion;
            if (!companion.containsTagOrPlaceholder(content) && !ShiftTag.INSTANCE.containsTag(content) && !PapiTag.INSTANCE.containsTag(content)) {
               return (Packet)packet;
            }

            net.kyori.adventure.text.Component component4 = sender != null ? AdventureUtils.INSTANCE.getNEXO_SERIALIZER().deserialize(packet.body().content(), (Pointered)sender) : AdventureUtils.INSTANCE.getNEXO_SERIALIZER().deserialize(packet.body().content());
            component3 = component4;
         }

         component2 = PaperAdventure.asVanilla(componentProjections.apply(player2, component3));
      }

      Component component = component2;
      ResourceKey resourceKey = (ResourceKey)packet.chatType().chatType().unwrapKey().get();
      RegistryAccess registryAccess = NexoCommonsHandler.Companion.getRegistryAccess();
      Component component5 = packet.chatType().name();
      ChatType.Bound bound = ChatType.bind(resourceKey, registryAccess, nexoChannelHandler.transform(component5));
      ChatType.Bound chatType = bound;
      return (Packet)(new ClientboundPlayerChatPacket(packet.globalIndex(), packet.sender(), packet.index(), packet.signature(), packet.body(), java.util.Optional.ofNullable(component), packet.filterMask(), chatType));
   }

   private static final Packet _init_$lambda$3(NexoChannelHandler nexoChannelHandler, ClientboundPlayerInfoUpdatePacket packet) {
      EnumSet actions = packet.actions();
      List entries = packet.entries();
      Iterable iterable = (Iterable)entries;
      EnumSet enumSet = actions;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         ClientboundPlayerInfoUpdatePacket.Entry it = (ClientboundPlayerInfoUpdatePacket.Entry)item;
         int value = 0;
         UUID profileId = it.profileId();
         GameProfile profile = it.profile();
         boolean listed = it.listed();
         int latency = it.latency();
         GameType gameMode = it.gameMode();
         Component displayName = it.displayName();
         destination.add(new ClientboundPlayerInfoUpdatePacket.Entry(profileId, profile, listed, latency, gameMode, displayName != null ? nexoChannelHandler.transform(displayName) : null, it.showHat(), it.listOrder(), it.chatSession()));
      }

      List<ClientboundPlayerInfoUpdatePacket.Entry> entries2 = (List<ClientboundPlayerInfoUpdatePacket.Entry>)destination;
      return (Packet)(new ClientboundPlayerInfoUpdatePacket(enumSet, entries2));
   }

   private static final Packet _init_$lambda$4(NexoChannelHandler nexoChannelHandler, ClientboundShowDialogPacket packet) {
      Holder holder;
      block: {
         Dialog dialog2 = PaperDialog.minecraftHolderToBukkit(packet.dialog());
         Dialog paperDialog = dialog2;
         NamespacedKey key = NexoDialogs.INSTANCE.getDialogRegistry().getKey(paperDialog);
         dialog2 = NexoDialogs.INSTANCE.reloadedDialog((Key)key);
         if (dialog2 != null) {
            Dialog p0 = dialog2;
            int value = 0;
            holder = PaperDialog.bukkitToMinecraftHolder(p0);
            if (holder != null) {
               break block;
            }
         }

         holder = packet.dialog();
      }

      Holder dialog = holder;
      return (Packet)(new ClientboundShowDialogPacket(nexoChannelHandler.dialogTransformer.transform(dialog)));
   }

   private static final Packet _init_$lambda$5(NexoChannelHandler nexoChannelHandler, ServerboundContainerClickPacket packet) {
      boolean changed = false;
      Int2ObjectOpenHashMap changedSlots = new Int2ObjectOpenHashMap(packet.changedSlots().size());
      ObjectSet int2ObjectEntrySet = packet.changedSlots().int2ObjectEntrySet();
      Iterable iterable = (Iterable)int2ObjectEntrySet;

      for(Object element : iterable) {
         Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)element;
         int value = 0;
         HashedComponentRewriter hashedComponentRewriter = nexoChannelHandler.hashRewriter;
         Object obj = entry.getValue();
         HashedStack rewritten = hashedComponentRewriter.rewrite((HashedStack)obj);
         if (rewritten != entry.getValue()) {
            changed = true;
         }

         changedSlots.put(entry.getIntKey(), rewritten);
      }

      HashedComponentRewriter hashedComponentRewriter2 = nexoChannelHandler.hashRewriter;
      HashedStack hashedStack = packet.carriedItem();
      HashedStack carriedItem = hashedComponentRewriter2.rewrite(hashedStack);
      if (!changed && carriedItem == packet.carriedItem()) {
         return (Packet)packet;
      } else {
         return (Packet)(new ServerboundContainerClickPacket(packet.containerId(), packet.stateId(), packet.slotNum(), packet.buttonNum(), packet.containerInput(), (Int2ObjectMap)changedSlots, carriedItem));
      }
   }

   private static final Packet _init_$lambda$6(NexoChannelHandler nexoChannelHandler, ClientboundContainerSetContentPacket packet) {
      net.minecraft.world.item.ItemStack itemStack;
      java.lang.String str;
      Inventory inventory;
      block: {
         Player player2 = nexoChannelHandler.getPlayer();
         if (player2 != null) {
            InventoryView openInventory = player2.getOpenInventory();
            if (openInventory != null) {
               inventory = openInventory.getTopInventory();
               break block;
            }
         }

         inventory = null;
      }

      block: {
         Inventory inventory2 = inventory;
         AnvilInventory anvilInventory = inventory2 instanceof AnvilInventory ? (AnvilInventory)inventory2 : null;
         if ((inventory2 instanceof AnvilInventory ? (AnvilInventory)inventory2 : null) != null) {
            ItemStack itemStack2 = anvilInventory.getFirstItem();
            if (itemStack2 != null) {
               CraftItemStack craftItemStack = ItemUtils.Companion.toCraftStack(itemStack2);
               if (craftItemStack != null) {
                  itemStack = craftItemStack.handle;
                  break block;
               }
            }
         }

         itemStack = null;
      }

      net.minecraft.world.item.ItemStack firstItem = itemStack;
      int containerId = packet.containerId();
      int stateId = packet.stateId();
      List items = packet.items();
      Iterable iterable = (Iterable)items;
      NonNullList nonNullList = NonNullList.create();
      Collection collection = (Collection)nonNullList;
      int value = stateId;
      int value2 = containerId;

      for(Object obj : iterable) {
         net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack)obj;
         int value3 = 0;
         net.minecraft.world.item.ItemStack itemStack3;
         if (!item.equals(firstItem)) {
            itemStack3 = nexoChannelHandler.transform(item);
         } else {
            block: {
               CustomData customData = (CustomData)item.get(DataComponents.CUSTOM_DATA);
               if (customData != null) {
                  CompoundTag unsafe = customData.getUnsafe();
                  if (unsafe != null) {
                     Optional string = unsafe.getString(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING());
                     if (string != null) {
                        str = (String)OptionalsKt.getOrNull(string);
                        break block;
                     }
                  }
               }

               str = null;
            }

            if (str == null) {
               str = "";
            }

            itemStack3 = nexoChannelHandler.transform(item, str);
         }

         collection.add(itemStack3);
      }

      List list = (List)collection;
      net.minecraft.world.item.ItemStack carriedItem = packet.carriedItem();
      net.minecraft.world.item.ItemStack itemStack4 = nexoChannelHandler.transform(carriedItem);
      List list2 = list;
      return (Packet)(new ClientboundContainerSetContentPacket(value2, value, list2, itemStack4));
   }

   private static final Packet _init_$lambda$7(NexoChannelHandler nexoChannelHandler, ClientboundSetCursorItemPacket it) {
      net.minecraft.world.item.ItemStack contents = it.contents();
      return (Packet)(new ClientboundSetCursorItemPacket(nexoChannelHandler.transform(contents)));
   }

   private static final Packet _init_$lambda$8(NexoChannelHandler nexoChannelHandler, ClientboundMerchantOffersPacket it) {
      int containerId = it.getContainerId();
      MerchantOffers offers = it.getOffers();
      return (Packet)(new ClientboundMerchantOffersPacket(containerId, nexoChannelHandler.transform(offers), it.getVillagerLevel(), it.getVillagerXp(), it.showProgress(), it.canRestock()));
   }

   private static final Unit _init_$lambda$9(NexoChannelHandler nexoChannelHandler, ServerboundPickItemFromBlockPacket packet) {
      BlockLocation location = new BlockLocation(packet.pos().getX(), packet.pos().getY(), packet.pos().getZ());
      FurnitureMechanic mechanic = IFurniturePacketManager.Companion.mechanicFromHitbox(location);
      ItemBuilder itemBuilder = NexoItems.itemFromId(mechanic != null ? mechanic.getItemID() : null);
      ItemStack item = itemBuilder != null ? itemBuilder.build() : null;
      nexoChannelHandler.handlePickItem(mechanic, item);
      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$10(NexoChannelHandler nexoChannelHandler, ServerboundPickItemFromEntityPacket packet) {
      FurnitureMechanic mechanic = IFurniturePacketManager.Companion.mechanicFromHitbox(packet.id());
      ItemBuilder itemBuilder = NexoItems.itemFromId(mechanic != null ? mechanic.getItemID() : null);
      ItemStack item = itemBuilder != null ? itemBuilder.build() : null;
      nexoChannelHandler.handlePickItem(mechanic, item);
      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$11(NexoChannelHandler nexoChannelHandler, ClientboundForgetLevelChunkPacket packet) {
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
               Player player2 = nexoChannelHandler.getPlayer();
               if (player2 != null) {
                  World world = player2.getWorld();
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

            nexoChannelHandler.handleChunkUpdate(IFurniturePacketManager.Companion.packChunkPos(packet.pos().x(), packet.pos().z()), packetManager);
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final Packet _init_$lambda$12(NexoChannelHandler nexoChannelHandler, ClientboundBlockUpdatePacket packet) {
      if (!packet.getBlockState().isAir()) {
         return (Packet)packet;
      } else {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Vector vector = new Vector(packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());
         Player player2 = nexoChannelHandler.getPlayer();
         if (player2 != null) {
            World world = player2.getWorld();
            if (world != null) {
               if (companion.blockIsHitbox(vector, world, (UUID)null)) {
                  return null;
               }

               companion = IFurniturePacketManager.Companion;
               vector = new Vector(packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());
               Player player3 = nexoChannelHandler.getPlayer();
               if (player3 != null) {
                  World world2 = player3.getWorld();
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

   private static final Packet _init_$lambda$13(NexoChannelHandler nexoChannelHandler, ClientboundSoundPacket packet) {
      net.minecraft.sounds.SoundEvent soundEvent;
      Optional right = packet.getSound().unwrap().right();
      SoundEvent orNull = (SoundEvent)OptionalsKt.getOrNull(right);
      if (orNull == null) {
         return (Packet)packet;
      } else {
         SoundEvent event = orNull;
         Identifier identifier = (Identifier)nexoChannelHandler.getSoundMapping().get(event.location().toString());
         if (identifier == null) {
            return (Packet)packet;
         } else {
            block: {
               Identifier newSound = identifier;
               Optional fixedRange = event.fixedRange();
               Float number = (Float)OptionalsKt.getOrNull(fixedRange);
               if (number != null) {
                  float it = ((Number)number).floatValue();
                  int value = 0;
                  soundEvent = SoundEvent.createFixedRangeEvent(newSound, it);
                  if (soundEvent != null) {
                     break block;
                  }
               }

               soundEvent = SoundEvent.createVariableRangeEvent(newSound);
            }

            SoundEvent newEvent = soundEvent;
            return (Packet)(new ClientboundSoundPacket(Holder.direct(newEvent), packet.getSource(), packet.getX(), packet.getY(), packet.getZ(), packet.getVolume(), packet.getPitch(), packet.getSeed()));
         }
      }
   }

   private static final ConcurrentHashMap soundMapping_delegate$lambda$0() {
      ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
      ConcurrentHashMap concurrentHashMap2 = concurrentHashMap;
      int value = 0;
      Iterable vanillA_WOOD_SOUNDS = (Iterable)BlockSounds.Companion.getVANILLA_WOOD_SOUNDS();
      int index = 0;

      for(Object item : vanillA_WOOD_SOUNDS) {
         int i = index++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String vanilla = (String)item;
         int value2 = 0;
         String orNull = (String)CollectionsKt.getOrNull(BlockSounds.Companion.getNEXO_WOOD_SOUNDS(), i);
         if (orNull != null) {
            String it = orNull;
            int value3 = 0;
            concurrentHashMap2.put(vanilla, Identifier.parse(it));
         }
      }

      vanillA_WOOD_SOUNDS = (Iterable)BlockSounds.Companion.getVANILLA_STONE_SOUNDS();
      index = 0;

      for(Object obj : vanillA_WOOD_SOUNDS) {
         int i = index++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String vanilla = (String)obj;
         int value4 = 0;
         String str = (String)CollectionsKt.getOrNull(BlockSounds.Companion.getNEXO_STONE_SOUNDS(), i);
         if (str != null) {
            String it = str;
            int value5 = 0;
            concurrentHashMap2.put(vanilla, Identifier.parse(it));
         }
      }

      return concurrentHashMap;
   }

   static {
      ProtocolInfo protocolInfo = GameProtocols.CLIENTBOUND_TEMPLATE.bind(NexoCommonsHandler.Companion.getDecorator());
      protInfo = protocolInfo;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0019\u0010\u0004\u001a\r\u0012\t\u0012\u00070\u0006¢\u0006\u0002\b\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/NexoChannelHandler$Companion;", "", "<init>", "()V", "protInfo", "Lnet/minecraft/network/ProtocolInfo;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "Lkotlin/jvm/internal/EnhancedNullability;", "com.nexomc:v26_R2"}
   )
   public static final class Companion {
      private Companion() {
      }
   }

   public static final void access$lambda$1$writeEnum(RegistryFriendlyByteBuf buf, Enum value) { buf.writeEnum(value); }
}
