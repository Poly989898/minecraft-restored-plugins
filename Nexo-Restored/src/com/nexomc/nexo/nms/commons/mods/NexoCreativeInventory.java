package com.nexomc.nexo.nms.commons.mods;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mods.INexoCreativeInventory;
import com.nexomc.nexo.mods.NexoCreativeInventoryGroups;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.TuplesKt;
import com.nexomc.nexo.utils.inventories.NexoInventory;
import com.nexomc.nexo.utils.logs.Logs;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ItemLike;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 =2\u00020\u00012\u00020\u0002:\u0003;<=B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\rH\u0016J\b\u0010\u000f\u001a\u00020\rH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0002J\b\u0010\u0011\u001a\u00020\rH\u0002J\b\u0010\u0012\u001a\u00020\rH\u0002J \u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0018\u0010\u001a\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J2\u0010\u001d\u001a\u0012\u0012\u000e\u0012\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001e0\u001e2\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0010\u0010!\u001a\u00020\u001c2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\"\u001a\u00020#2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002JJ\u0010$\u001a\u00020\u00192\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020 2\u0006\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\u00152\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001e2\u0006\u0010*\u001a\u00020 2\u0006\u0010+\u001a\u00020 H\u0002J2\u0010,\u001a\u00020\u00192\u0006\u0010'\u001a\u00020 2\u0006\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\u00152\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001eH\u0002JB\u0010-\u001a\u00020\u00192\u0006\u0010'\u001a\u00020 2\u0006\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\u00152\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001e2\u0006\u0010*\u001a\u00020 2\u0006\u0010+\u001a\u00020 H\u0002J\"\u0010.\u001a\u00020\r2\u0006\u0010/\u001a\u0002002\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u001eH\u0002J\u0018\u00101\u001a\u000602j\u0002`32\n\u00104\u001a\u000602j\u0002`3H\u0002J4\u00105\u001a\u00020\r\"\u0004\b\u0000\u001062\n\u00107\u001a\u000602j\u0002`32\n\u00108\u001a\u000602j\u0002`32\f\u00109\u001a\b\u0012\u0004\u0012\u0002H60:H\u0002R\u0018\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0007j\u0002`\b0\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\t\u001a\u00070\n¢\u0006\u0002\b\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006>"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory;", "Lorg/bukkit/plugin/messaging/PluginMessageListener;", "Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "<init>", "()V", "groups", "", "Lcom/nexomc/nexo/mods/NexoCreativeInventoryGroups;", "Lcom/nexomc/nexo/nms/commons/mods/ItemGroupData;", "creativeInventoryKey", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "reload", "", "initialize", "uninitialize", "createConfigGroups", "createDefaultGroups", "createFileGroups", "onPluginMessageReceived", "channel", "", "player", "Lorg/bukkit/entity/Player;", "message", "", "sendPayload", "hello", "Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$HelloPayload;", "chunkGroups", "", "maxItems", "", "readHelloPayload", "negotiateProtocol", "Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$NegotiationResult;", "createRegistryPayload", "modernClient", "", "selectedProtocol", "status", "statusMessage", "batchIndex", "batchCount", "createLegacyRegistryPayload", "createModernRegistryPayload", "writeGroups", "buffer", "Lnet/minecraft/network/RegistryFriendlyByteBuf;", "sanitizeForClient", "Lnet/minecraft/world/item/ItemStack;", "Lcom/nexomc/nexo/nms/commons/mods/NMSItem;", "stack", "copyComponent", "T", "from", "to", "type", "Lnet/minecraft/core/component/DataComponentType;", "HelloPayload", "NegotiationResult", "Companion", "com.nexomc:commons"}
)
public final class NexoCreativeInventory implements PluginMessageListener, INexoCreativeInventory {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<NexoCreativeInventoryGroups> groups = (List)(new ArrayList());
   @NotNull
   private NamespacedKey creativeInventoryKey;
   @NotNull
   private static final String INVENTORY_PERMISSION = "nexo.command.inventory";
   @NotNull
   private static final Map<UUID, HelloPayload> helloPayloads = (Map)(new LinkedHashMap());
   private static final int HELLO_MAGIC = 5132360;
   private static final int REGISTRY_MAGIC = 5132370;
   private static final int SERVER_MIN_PROTOCOL = 1;
   private static final int SERVER_MAX_PROTOCOL = 3;
   @NotNull
   private static final String SERVER_IMPLEMENTATION_VERSION = "1.1.1";
   private static final int STATUS_OK = 0;
   private static final int STATUS_INCOMPATIBLE = 1;
   private static final int STATUS_NO_PERMISSION = 2;
   @NotNull
   private static final List<String> SERVER_FEATURES;
   @NotNull
   private static final List<DataComponentType<? extends Object>> SAFE_COMPONENTS;

   public NexoCreativeInventory() {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:creative_inventory");
      this.creativeInventoryKey = namespacedKey;
      this.initialize();
   }

   public void reload() {
      this.groups.clear();
      this.createFileGroups();
      this.createConfigGroups();
      Map map = helloPayloads;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value = 0;
         UUID uuid = (UUID)element.getKey();
         HelloPayload hello = (HelloPayload)element.getValue();
         Player player2 = Bukkit.getPlayer(uuid);
         if (player2 != null) {
            Player player = player2;
            int value2 = 0;
            this.sendPayload(player, hello);
         } else {
            helloPayloads.remove(uuid);
         }
      }

   }

   public void initialize() {
      NexoCreativeInventory nexoCreativeInventory = this;

      try {
         NexoCreativeInventory nexoCreativeInventory2 = nexoCreativeInventory;
         int value = 0;
         Messenger messenger = Bukkit.getServer().getMessenger();
         int value2 = 0;
         messenger.registerIncomingPluginChannel((Plugin)NexoPlugin.Companion.instance(), nexoCreativeInventory2.getHELLO_CHANNEL(), this);
         messenger.registerOutgoingPluginChannel((Plugin)NexoPlugin.Companion.instance(), nexoCreativeInventory2.getREGISTRY_CHANNEL());
         Object obj = ResultShim.ok(messenger);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   public void uninitialize() {
      NexoCreativeInventory nexoCreativeInventory = this;

      try {
         NexoCreativeInventory nexoCreativeInventory2 = nexoCreativeInventory;
         int value = 0;
         Messenger messenger = Bukkit.getServer().getMessenger();
         int value2 = 0;
         messenger.unregisterIncomingPluginChannel((Plugin)NexoPlugin.Companion.instance(), nexoCreativeInventory2.getHELLO_CHANNEL(), this);
         messenger.unregisterOutgoingPluginChannel((Plugin)NexoPlugin.Companion.instance(), nexoCreativeInventory2.getREGISTRY_CHANNEL());
         Object obj = ResultShim.ok(messenger);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   private final void createConfigGroups() {
      Collection collection;
      List list;
      block: {
         collection = (Collection)this.groups;
         ConfigurationSection toSection = NexoInventory.CREATIVE_LAYOUT.toSection();
         if (toSection != null) {
            Map map = NexoYamlKt.childSections(toSection);
            if (map != null) {
               Collection collection2 = map.values();
               if (collection2 != null) {
                  Iterable iterable2 = (Iterable)collection2;
                  Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

                  for(Object item : iterable2) {
                     ConfigurationSection p0 = (ConfigurationSection)item;
                     int value = 0;
                     destination.add(new NexoCreativeInventoryGroups(p0));
                  }

                  list = (List)destination;
                  break block;
               }
            }
         }

         list = null;
      }

      if (list == null) {
         list = CollectionsKt.emptyList();
      }

      Iterable iterable = (Iterable)list;
      CollectionsKt.addAll(collection, iterable);
   }

   private final void createDefaultGroups() {
      Object[] furnitureIDs = NexoFurniture.furnitureIDs();
      Collection destination = (Collection)(new ArrayList());
      Object[] objs = furnitureIDs;
      int value11 = 0;

      for(int j = furnitureIDs.length; value11 < j; ++value11) {
         ItemStack itemStack;
         block: {
            Object element = objs[value11];
            int value = 0;
            int value2 = 0;
            ItemBuilder itemBuilder = NexoItems.itemFromId((String)element);
            if (itemBuilder != null) {
               ItemBuilder i = itemBuilder;
               int value3 = 0;
               itemBuilder = i.getNexoMeta().getExcludedFromInventory().isNotTrue() ? i : null;
               if (itemBuilder != null) {
                  itemStack = itemBuilder.build();
                  break block;
               }
            }

            itemStack = null;
         }

         if (itemStack != null) {
            Object obj = itemStack;
            int value4 = 0;
            destination.add(obj);
         }
      }

      List furnitureItems = (List)destination;
      Object[] blockIDs = NexoBlocks.blockIDs();
      Collection collection4 = (Collection)(new ArrayList());
      Object[] objs2 = blockIDs;
      value11 = 0;
      int value5 = 0;

      for(int it = blockIDs.length; value5 < it; ++value5) {
         ItemStack itemStack2;
         block: {
            Object obj2 = objs2[value5];
            int it_7 = 0;
            int value6 = 0;
            ItemBuilder itemBuilder2 = NexoItems.itemFromId((String)obj2);
            if (itemBuilder2 != null) {
               ItemBuilder i = itemBuilder2;
               int value7 = 0;
               itemBuilder2 = i.getNexoMeta().getExcludedFromInventory().isNotTrue() ? i : null;
               if (itemBuilder2 != null) {
                  itemStack2 = itemBuilder2.build();
                  break block;
               }
            }

            itemStack2 = null;
         }

         if (itemStack2 != null) {
            Object obj3 = itemStack2;
            int value8 = 0;
            collection4.add(obj3);
         }
      }

      List customBlockItems = (List)collection4;
      Iterable items = (Iterable)NexoItems.items();
      Collection collection5 = (Collection)(new ArrayList());

      for(Object obj4 : items) {
         ItemBuilder it = (ItemBuilder)obj4;
         int value12 = 0;
         if (it.getNexoMeta().getExcludedFromInventory().isNotTrue() && (it.getConsumableComponent() != null || it.getFoodComponent() != null)) {
            collection5.add(obj4);
         }
      }

      items = (Iterable)((List)collection5);
      collection5 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10)));

      for(Object item : items) {
         ItemBuilder itemBuilder3 = (ItemBuilder)item;
         int value9 = 0;
         collection5.add(itemBuilder3.build());
      }

      List foodItems = (List)collection5;
      Iterable iterable = (Iterable)NexoItems.items();
      Collection collection6 = (Collection)(new ArrayList());
      value11 = 0;

      for(Object obj5 : iterable) {
         ItemBuilder it = (ItemBuilder)obj5;
         if (it.getNexoMeta().getExcludedFromInventory().isNotTrue()) {
            collection6.add(obj5);
         }
      }

      iterable = (Iterable)((List)collection6);
      collection6 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
      value11 = 0;

      for(Object obj6 : iterable) {
         ItemBuilder it = (ItemBuilder)obj6;
         int value10 = 0;
         collection6.add(it.build());
      }

      List allItems = (List)collection6;
      if (!((Collection)furnitureItems).isEmpty()) {
         Collection collection7 = (Collection)this.groups;
         Key key = Key.key("nexo:furniture");
         collection7.add(new NexoCreativeInventoryGroups(key, "Furniture", furnitureItems, ((org.bukkit.inventory.ItemStack)kotlin.collections.CollectionsKt.first((furnitureItems)))));
      }

      if (!((Collection)customBlockItems).isEmpty()) {
         Collection collection = (Collection)this.groups;
         Key key2 = Key.key("nexo:custom_blocks");
         collection.add(new NexoCreativeInventoryGroups(key2, "Custom Blocks", customBlockItems, ((org.bukkit.inventory.ItemStack)kotlin.collections.CollectionsKt.first((customBlockItems)))));
      }

      if (!((Collection)foodItems).isEmpty()) {
         Collection collection2 = (Collection)this.groups;
         Key key3 = Key.key("nexo:food");
         collection2.add(new NexoCreativeInventoryGroups(key3, "Food", foodItems, ((org.bukkit.inventory.ItemStack)kotlin.collections.CollectionsKt.first((foodItems)))));
      }

      if (!((Collection)allItems).isEmpty()) {
         Collection collection3 = (Collection)this.groups;
         Key key4 = Key.key("nexo:all");
         collection3.add(new NexoCreativeInventoryGroups(key4, "Items", allItems, ((org.bukkit.inventory.ItemStack)kotlin.collections.CollectionsKt.first((allItems)))));
      }

   }

   private final void createFileGroups() {
      Map itemMap = NexoItems.itemMap();

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)itemMap.entrySet()) {
         int value = 0;
         File file = (File)element.getKey();
         Map items = (Map)element.getValue();
         Iterable iterable = (Iterable)items.values();
         Collection destination = (Collection)(new ArrayList());

         for(Object obj : iterable) {
            int value2 = 0;
            ItemBuilder it = (ItemBuilder)obj;
            int value3 = 0;
            int value4 = 0;
            ItemBuilder itemBuilder = it.getNexoMeta().getExcludedFromInventory().isNotTrue() ? it : null;
            ItemStack itemStack = itemBuilder != null ? itemBuilder.build() : null;
            if (itemStack != null) {
               Object obj2 = itemStack;
               int value5 = 0;
               destination.add(obj2);
            }
         }

         List fileItems = (List)destination;
         if (!fileItems.isEmpty()) {
            String fileName = FilesKt.getNameWithoutExtension(file);
            CharSequence charSequence = (CharSequence)fileName;
            String[] strs = new String[]{"_", "-"};
            String title = CollectionsKt.joinToString((Iterable)StringsKt.split(charSequence, strs, false, 0), (CharSequence)" ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", NexoCreativeInventory::createFileGroups$lambda$0$1);
            ((Collection)this.groups).add(new NexoCreativeInventoryGroups(KeyUtils.parseKey("nexo:" + fileName, true), title, fileItems, ((org.bukkit.inventory.ItemStack)kotlin.collections.CollectionsKt.first((fileItems)))));
         }
      }

   }

   public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
      if (Intrinsics.areEqual(this.getHELLO_CHANNEL(), channel)) {
         HelloPayload hello = this.readHelloPayload(message);
         helloPayloads.put(player.getUniqueId(), hello);
         this.sendPayload(player, hello);
      }
   }

   private final void sendPayload(Player player, HelloPayload hello) {
      NegotiationResult negotiation = this.negotiateProtocol(hello);
      Triple triple;
      if (!negotiation.getCompatible()) {
         if (Settings.DEBUG.toBool()) {
            String name = player.getName();
            Logs.logInfo("Rejected client " + name + " with version " + hello.getClientVersion() + " and range " + hello.getMinProtocol() + ".." + hello.getMaxProtocol());
         }

         triple = TuplesKt.to(kotlin.TuplesKt.to(1, negotiation.getMessage()), CollectionsKt.emptyList());
      } else if (!player.hasPermission("nexo.command.inventory")) {
         if (Settings.DEBUG.toBool()) {
            Logs.logInfo("Denied inventory sync for " + player.getName() + " due to missing permission");
         }

         triple = TuplesKt.to(kotlin.TuplesKt.to(2, "Missing permission: nexo.command.inventory"), CollectionsKt.emptyList());
      } else {
         if (Settings.DEBUG.toBool()) {
            String str = player.getName();
            Logs.logInfo("Accepted client " + str + " with version " + hello.getClientVersion() + " using protocol " + negotiation.getSelectedProtocol());
         }

         triple = TuplesKt.to(kotlin.TuplesKt.to(0, "ok"), this.groups);
      }

      Triple triple2 = triple;
      int status = ((Number)triple2.component1()).intValue();
      String statusMessage = (String)triple2.component2();
      List groupsToSend = (List)triple2.component3();
      int packetSize = NexoInventory.CREATIVE_PACKET_SIZE.toInt();
      List chunkedGroupBatches = this.chunkGroups(groupsToSend, packetSize);
      int batchCount = RangesKt.coerceAtLeast(chunkedGroupBatches.size(), 1);
      Iterable iterable = (Iterable)chunkedGroupBatches;
      int index = 0;

      for(Object item : iterable) {
         int batchIndex = index++;
         if (batchIndex < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         List batch = (List)item;
         int value = 0;
         byte[] payload = this.createRegistryPayload(hello.getModern(), negotiation.getSelectedProtocol(), status, statusMessage, batch, batchIndex, batchCount);
         player.sendPluginMessage((Plugin)NexoPlugin.Companion.instance(), this.getREGISTRY_CHANNEL(), payload);
      }

   }

   private final List<List<NexoCreativeInventoryGroups>> chunkGroups(List<NexoCreativeInventoryGroups> groups, int maxItems) {
      if (!groups.isEmpty() && maxItems > 0) {
         List batches = (List)(new ArrayList());
         List currentBatch = (List)(new ArrayList());

         for(NexoCreativeInventoryGroups group : groups) {
            Iterable iterable = (Iterable)currentBatch;
            int value = 0;

            for(Object obj : iterable) {
               NexoCreativeInventoryGroups it = (NexoCreativeInventoryGroups)obj;
               int value2 = 0;
               int value3 = it.getItems().size();
               value += value3;
            }

            if (value + group.getItems().size() <= maxItems) {
               ((Collection)currentBatch).add(group);
            } else {
               if (!((Collection)currentBatch).isEmpty()) {
                  ((Collection)batches).add(CollectionsKt.toList((Iterable)currentBatch));
                  currentBatch.clear();
               }

               Collection collection = (Collection)batches;
               Iterable iterable2 = (Iterable)CollectionsKt.chunked((Iterable)group.getItems(), maxItems);
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

               for(Object item : iterable2) {
                  List it = (List)item;
                  int value4 = 0;
                  destination.add(group.copy((Key)null, (String)null, it, (ItemStack)null));
               }

               List list = (List)destination;
               collection.add(list);
            }
         }

         if (!((Collection)currentBatch).isEmpty()) {
            ((Collection)batches).add(CollectionsKt.toList((Iterable)currentBatch));
         }

         return batches;
      } else {
         return CollectionsKt.listOf(groups);
      }
   }

   private final HelloPayload readHelloPayload(byte[] message) {
      if (message.length == 0) {
         int fallbackProtocol = 1;
         return new HelloPayload(false, fallbackProtocol, fallbackProtocol, "legacy-empty", CollectionsKt.emptyList());
      } else {
         FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(message));

         HelloPayload helloPayload;
         try {
            int first = buffer.readVarInt();
            if (first != 5132360) {
               String clientVersion = buffer.readableBytes() > 0 ? buffer.readUtf(64) : "legacy";
               HelloPayload fallbackProtocol = new HelloPayload(false, first, first, clientVersion, CollectionsKt.emptyList());
               return fallbackProtocol;
            }

            int minProtocol = buffer.readVarInt();
            int maxProtocol = buffer.readVarInt();
            String clientVersion = buffer.readUtf(64);
            int featureCount = buffer.readVarInt();
            int value = Math.max(0, Math.min(featureCount, 32));
            ArrayList arrayList = new ArrayList(value);

            for(int i = 0; i < value; ++i) {
               int value2 = 0;
               arrayList.add(buffer.readUtf(64));
            }

            List features = (List)arrayList;
            helloPayload = new HelloPayload(true, minProtocol, maxProtocol, clientVersion, CollectionsKt.toList((Iterable)features));
         } catch (Exception e) {
            int fallbackProtocol = 1;
            HelloPayload clientVersion = new HelloPayload(false, fallbackProtocol, fallbackProtocol, "unknown", CollectionsKt.emptyList());
            return clientVersion;
         } finally {
            buffer.release();
         }

         return helloPayload;
      }
   }

   private final NegotiationResult negotiateProtocol(HelloPayload hello) {
      int clientMin = Math.min(hello.getMinProtocol(), hello.getMaxProtocol());
      int clientMax = Math.max(hello.getMinProtocol(), hello.getMaxProtocol());
      int commonMin = Math.max(1, clientMin);
      int commonMax = Math.min(3, clientMax);
      return commonMin > commonMax ? new NegotiationResult(false, 0, "No shared protocol (server 1..3, client " + clientMin + ".." + clientMax + ")") : new NegotiationResult(true, commonMax, "ok");
   }

   private final byte[] createRegistryPayload(boolean modernClient, int selectedProtocol, int status, String statusMessage, List<NexoCreativeInventoryGroups> groups, int batchIndex, int batchCount) {
      return modernClient ? this.createModernRegistryPayload(selectedProtocol, status, statusMessage, groups, batchIndex, batchCount) : this.createLegacyRegistryPayload(selectedProtocol, status, statusMessage, groups);
   }

   private final byte[] createLegacyRegistryPayload(int selectedProtocol, int status, String statusMessage, List<NexoCreativeInventoryGroups> groups) {
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), (RegistryAccess)MinecraftServer.getServer().registryAccess());

      byte[] bs;
      try {
         buffer.writeVarInt(selectedProtocol);
         buffer.writeVarInt(status);
         buffer.writeUtf(statusMessage, 256);
         if (status == 0) {
            this.writeGroups(buffer, groups);
         }

         byte[] payload = new byte[buffer.readableBytes()];
         buffer.getBytes(0, payload);
         bs = payload;
      } finally {
         buffer.release();
      }

      return bs;
   }

   private final byte[] createModernRegistryPayload(int selectedProtocol, int status, String statusMessage, List<NexoCreativeInventoryGroups> groups, int batchIndex, int batchCount) {
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), (RegistryAccess)MinecraftServer.getServer().registryAccess());

      byte[] bs;
      try {
         buffer.writeVarInt(5132370);
         buffer.writeVarInt(status);
         buffer.writeVarInt(selectedProtocol);
         buffer.writeUtf("1.1.1", 64);
         buffer.writeUtf(statusMessage, 256);
         buffer.writeVarInt(SERVER_FEATURES.size());

         for(String feature : SERVER_FEATURES) {
            buffer.writeUtf(feature, 64);
         }

         if (selectedProtocol >= 3) {
            buffer.writeVarInt(batchIndex);
            buffer.writeVarInt(batchCount);
         }

         if (status == 0) {
            this.writeGroups(buffer, groups);
         }

         byte[] payload = new byte[buffer.readableBytes()];
         buffer.getBytes(0, payload);
         bs = payload;
      } finally {
         buffer.release();
      }

      return bs;
   }

   private final void writeGroups(RegistryFriendlyByteBuf buffer, List<NexoCreativeInventoryGroups> groups) {
      buffer.writeVarInt(groups.size());

      for(NexoCreativeInventoryGroups group : groups) {
         buffer.writeUtf(group.getId().asString(), 256);
         buffer.writeUtf(group.getTitle(), 256);
         StreamCodec streamCodec = net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC;
         net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(group.getIcon());
         streamCodec.encode(buffer, this.sanitizeForClient(itemStack));
         buffer.writeVarInt(group.getItems().size());

         for(ItemStack bukkitStack : group.getItems()) {
            net.minecraft.world.item.ItemStack itemStack2 = CraftItemStack.asNMSCopy(bukkitStack);
            net.minecraft.world.item.ItemStack nmsStack = this.sanitizeForClient(itemStack2);
            net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, nmsStack);
         }
      }

   }

   private final net.minecraft.world.item.ItemStack sanitizeForClient(net.minecraft.world.item.ItemStack stack) {
      if (stack.isEmpty()) {
         net.minecraft.world.item.ItemStack itemStack = net.minecraft.world.item.ItemStack.EMPTY;
         return itemStack;
      } else {
         net.minecraft.world.item.ItemStack cleaned = new net.minecraft.world.item.ItemStack((ItemLike)stack.getItem(), stack.getCount());

         for(DataComponentType type : SAFE_COMPONENTS) {
            this.copyComponent(stack, cleaned, type);
         }

         return cleaned;
      }
   }

   private final <T> void copyComponent(net.minecraft.world.item.ItemStack from, net.minecraft.world.item.ItemStack to, DataComponentType<T> type) {
      Object obj = from.get(type);
      if (obj != null) {
         Object value = obj;
         to.set((DataComponentType)type, value);
      }
   }

   private static final CharSequence createFileGroups$lambda$0$1(String it) {
      String str;
      if (((CharSequence)it).length() > 0) {
         StringBuilder stringBuilder = new StringBuilder();
         char p0 = it.charAt(0);
         StringBuilder stringBuilder2 = stringBuilder;
         int value = 0;
         String str2 = String.valueOf(p0);
         str2 = str2.toUpperCase(Locale.ROOT);
         StringBuilder stringBuilder3 = stringBuilder2.append(str2);
         value = 1;
         String str3 = it.substring(value);
         str = stringBuilder3.append(str3).toString();
      } else {
         str = it;
      }

      return (CharSequence)str;
   }

   static {
      String[] strs = new String[]{"hello.range.v1", "registry.envelope.v1", "registry.groups.v1", "registry.batched.v1"};
      SERVER_FEATURES = CollectionsKt.listOf(strs);
      DataComponentType[] dataComponentTypes = new DataComponentType[30];
      Companion companion = Companion;
      byte b = 0;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(DataComponents.CUSTOM_DATA);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      dataComponentTypes[b] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b2 = 1;

      try {
         int value2 = 0;
         obj = ResultShim.ok(DataComponents.CUSTOM_NAME);
      } catch (Throwable ex) {
         obj = ResultShim.ok(ResultKt.createFailure(ex));
      }

      dataComponentTypes[b2] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b3 = 2;

      try {
         int value3 = 0;
         obj = ResultShim.ok(DataComponents.ITEM_NAME);
      } catch (Throwable ex2) {
         obj = ResultShim.ok(ResultKt.createFailure(ex2));
      }

      dataComponentTypes[b3] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b4 = 3;

      try {
         int value4 = 0;
         obj = ResultShim.ok(DataComponents.ITEM_MODEL);
      } catch (Throwable ex3) {
         obj = ResultShim.ok(ResultKt.createFailure(ex3));
      }

      dataComponentTypes[b4] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b5 = 4;

      try {
         int value5 = 0;
         obj = ResultShim.ok(DataComponents.LORE);
      } catch (Throwable ex4) {
         obj = ResultShim.ok(ResultKt.createFailure(ex4));
      }

      dataComponentTypes[b5] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b6 = 5;

      try {
         int value6 = 0;
         obj = ResultShim.ok(DataComponents.RARITY);
      } catch (Throwable ex5) {
         obj = ResultShim.ok(ResultKt.createFailure(ex5));
      }

      dataComponentTypes[b6] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b7 = 6;

      try {
         int value7 = 0;
         obj = ResultShim.ok(DataComponents.MAX_STACK_SIZE);
      } catch (Throwable ex6) {
         obj = ResultShim.ok(ResultKt.createFailure(ex6));
      }

      dataComponentTypes[b7] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b8 = 7;

      try {
         int value8 = 0;
         obj = ResultShim.ok(DataComponents.MAX_DAMAGE);
      } catch (Throwable ex7) {
         obj = ResultShim.ok(ResultKt.createFailure(ex7));
      }

      dataComponentTypes[b8] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b9 = 8;

      try {
         int value9 = 0;
         obj = ResultShim.ok(DataComponents.DAMAGE);
      } catch (Throwable ex8) {
         obj = ResultShim.ok(ResultKt.createFailure(ex8));
      }

      dataComponentTypes[b9] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b10 = 9;

      try {
         int value10 = 0;
         obj = ResultShim.ok(DataComponents.UNBREAKABLE);
      } catch (Throwable ex9) {
         obj = ResultShim.ok(ResultKt.createFailure(ex9));
      }

      dataComponentTypes[b10] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b11 = 10;

      try {
         int value11 = 0;
         obj = ResultShim.ok(DataComponents.ENCHANTMENTS);
      } catch (Throwable ex10) {
         obj = ResultShim.ok(ResultKt.createFailure(ex10));
      }

      dataComponentTypes[b11] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b12 = 11;

      try {
         int value12 = 0;
         obj = ResultShim.ok(DataComponents.STORED_ENCHANTMENTS);
      } catch (Throwable ex11) {
         obj = ResultShim.ok(ResultKt.createFailure(ex11));
      }

      dataComponentTypes[b12] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b13 = 12;

      try {
         int value13 = 0;
         obj = ResultShim.ok(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
      } catch (Throwable ex12) {
         obj = ResultShim.ok(ResultKt.createFailure(ex12));
      }

      dataComponentTypes[b13] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b14 = 13;

      try {
         int value14 = 0;
         obj = ResultShim.ok(DataComponents.CUSTOM_MODEL_DATA);
      } catch (Throwable ex13) {
         obj = ResultShim.ok(ResultKt.createFailure(ex13));
      }

      dataComponentTypes[b14] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b15 = 14;

      try {
         int value15 = 0;
         obj = ResultShim.ok(DataComponents.DYED_COLOR);
      } catch (Throwable ex14) {
         obj = ResultShim.ok(ResultKt.createFailure(ex14));
      }

      dataComponentTypes[b15] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b16 = 15;

      try {
         int value16 = 0;
         obj = ResultShim.ok(DataComponents.MAP_ID);
      } catch (Throwable ex15) {
         obj = ResultShim.ok(ResultKt.createFailure(ex15));
      }

      dataComponentTypes[b16] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b17 = 16;

      try {
         int value17 = 0;
         obj = ResultShim.ok(DataComponents.MAP_ID);
      } catch (Throwable ex16) {
         obj = ResultShim.ok(ResultKt.createFailure(ex16));
      }

      dataComponentTypes[b17] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b18 = 17;

      try {
         int value18 = 0;
         obj = ResultShim.ok(DataComponents.PROFILE);
      } catch (Throwable ex17) {
         obj = ResultShim.ok(ResultKt.createFailure(ex17));
      }

      dataComponentTypes[b18] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b19 = 18;

      try {
         int value19 = 0;
         obj = ResultShim.ok(DataComponents.TOOLTIP_DISPLAY);
      } catch (Throwable ex18) {
         obj = ResultShim.ok(ResultKt.createFailure(ex18));
      }

      dataComponentTypes[b19] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b20 = 19;

      try {
         int value20 = 0;
         obj = ResultShim.ok(DataComponents.REPAIR_COST);
      } catch (Throwable ex19) {
         obj = ResultShim.ok(ResultKt.createFailure(ex19));
      }

      dataComponentTypes[b20] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b21 = 20;

      try {
         int value21 = 0;
         obj = ResultShim.ok(DataComponents.POTION_CONTENTS);
      } catch (Throwable ex20) {
         obj = ResultShim.ok(ResultKt.createFailure(ex20));
      }

      dataComponentTypes[b21] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b22 = 21;

      try {
         int value22 = 0;
         obj = ResultShim.ok(DataComponents.FIREWORKS);
      } catch (Throwable ex21) {
         obj = ResultShim.ok(ResultKt.createFailure(ex21));
      }

      dataComponentTypes[b22] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b23 = 22;

      try {
         int value23 = 0;
         obj = ResultShim.ok(DataComponents.FIREWORK_EXPLOSION);
      } catch (Throwable ex22) {
         obj = ResultShim.ok(ResultKt.createFailure(ex22));
      }

      dataComponentTypes[b23] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b24 = 23;

      try {
         int value24 = 0;
         obj = ResultShim.ok(DataComponents.WRITTEN_BOOK_CONTENT);
      } catch (Throwable ex23) {
         obj = ResultShim.ok(ResultKt.createFailure(ex23));
      }

      dataComponentTypes[b24] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b25 = 24;

      try {
         int value25 = 0;
         obj = ResultShim.ok(DataComponents.BANNER_PATTERNS);
      } catch (Throwable ex24) {
         obj = ResultShim.ok(ResultKt.createFailure(ex24));
      }

      dataComponentTypes[b25] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b26 = 25;

      try {
         int value26 = 0;
         obj = ResultShim.ok(DataComponents.BASE_COLOR);
      } catch (Throwable ex25) {
         obj = ResultShim.ok(ResultKt.createFailure(ex25));
      }

      dataComponentTypes[b26] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b27 = 26;

      try {
         int value27 = 0;
         obj = ResultShim.ok(DataComponents.TRIM);
      } catch (Throwable ex26) {
         obj = ResultShim.ok(ResultKt.createFailure(ex26));
      }

      dataComponentTypes[b27] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b28 = 27;

      try {
         int value28 = 0;
         obj = ResultShim.ok(DataComponents.INSTRUMENT);
      } catch (Throwable ex27) {
         obj = ResultShim.ok(ResultKt.createFailure(ex27));
      }

      dataComponentTypes[b28] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b29 = 28;

      try {
         int value29 = 0;
         obj = ResultShim.ok(DataComponents.JUKEBOX_PLAYABLE);
      } catch (Throwable ex28) {
         obj = ResultShim.ok(ResultKt.createFailure(ex28));
      }

      dataComponentTypes[b29] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      companion = Companion;
      byte b30 = 29;

      try {
         int value30 = 0;
         obj = ResultShim.ok(DataComponents.NOTE_BLOCK_SOUND);
      } catch (Throwable ex29) {
         obj = ResultShim.ok(ResultKt.createFailure(ex29));
      }

      dataComponentTypes[b30] = (DataComponentType)(ResultShim.isFailure(obj) ? null : obj);
      SAFE_COMPONENTS = CollectionsKt.listOfNotNull(dataComponentTypes);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0016\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\bHÆ\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\b0\nHÆ\u0003JA\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\nHÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u00032\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001e\u001a\u00020\u0005HÖ\u0081\u0004J\n\u0010\u001f\u001a\u00020\bHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006 "},
      d2 = {"Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$HelloPayload;", "", "modern", "", "minProtocol", "", "maxProtocol", "clientVersion", "", "features", "", "<init>", "(ZIILjava/lang/String;Ljava/util/List;)V", "getModern", "()Z", "getMinProtocol", "()I", "getMaxProtocol", "getClientVersion", "()Ljava/lang/String;", "getFeatures", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "com.nexomc:commons"}
   )
   private static final class HelloPayload {
      private final boolean modern;
      private final int minProtocol;
      private final int maxProtocol;
      @NotNull
      private final String clientVersion;
      @NotNull
      private final List<String> features;

      public HelloPayload(boolean modern, int minProtocol, int maxProtocol, @NotNull String clientVersion, @NotNull List<String> features) {
         super();
         this.modern = modern;
         this.minProtocol = minProtocol;
         this.maxProtocol = maxProtocol;
         this.clientVersion = clientVersion;
         this.features = features;
      }

      public final boolean getModern() {
         return this.modern;
      }

      public final int getMinProtocol() {
         return this.minProtocol;
      }

      public final int getMaxProtocol() {
         return this.maxProtocol;
      }

      @NotNull
      public final String getClientVersion() {
         return this.clientVersion;
      }

      @NotNull
      public final List<String> getFeatures() {
         return this.features;
      }

      public final boolean component1() {
         return this.modern;
      }

      public final int component2() {
         return this.minProtocol;
      }

      public final int component3() {
         return this.maxProtocol;
      }

      @NotNull
      public final String component4() {
         return this.clientVersion;
      }

      @NotNull
      public final List<String> component5() {
         return this.features;
      }

      @NotNull
      public final HelloPayload copy(boolean modern, int minProtocol, int maxProtocol, @NotNull String clientVersion, @NotNull List<String> features) {
         return new HelloPayload(modern, minProtocol, maxProtocol, clientVersion, features);
      }

      @NotNull
      public String toString() {
         return "HelloPayload(modern=" + this.modern + ", minProtocol=" + this.minProtocol + ", maxProtocol=" + this.maxProtocol + ", clientVersion=" + this.clientVersion + ", features=" + this.features + ")";
      }

      public int hashCode() {
         int result = Boolean.hashCode(this.modern);
         result = result * 31 + Integer.hashCode(this.minProtocol);
         result = result * 31 + Integer.hashCode(this.maxProtocol);
         result = result * 31 + this.clientVersion.hashCode();
         result = result * 31 + this.features.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof HelloPayload)) {
            return false;
         } else {
            HelloPayload helloPayload = (HelloPayload)other;
            if (this.modern != helloPayload.modern) {
               return false;
            } else if (this.minProtocol != helloPayload.minProtocol) {
               return false;
            } else if (this.maxProtocol != helloPayload.maxProtocol) {
               return false;
            } else if (!Intrinsics.areEqual(this.clientVersion, helloPayload.clientVersion)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.features, helloPayload.features);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0014\u0010\u0014\u001a\u00020\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0016\u001a\u00020\u0005HÖ\u0081\u0004J\n\u0010\u0017\u001a\u00020\u0007HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0018"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$NegotiationResult;", "", "compatible", "", "selectedProtocol", "", "message", "", "<init>", "(ZILjava/lang/String;)V", "getCompatible", "()Z", "getSelectedProtocol", "()I", "getMessage", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "toString", "com.nexomc:commons"}
   )
   private static final class NegotiationResult {
      private final boolean compatible;
      private final int selectedProtocol;
      @NotNull
      private final String message;

      public NegotiationResult(boolean compatible, int selectedProtocol, @NotNull String message) {
         super();
         this.compatible = compatible;
         this.selectedProtocol = selectedProtocol;
         this.message = message;
      }

      public final boolean getCompatible() {
         return this.compatible;
      }

      public final int getSelectedProtocol() {
         return this.selectedProtocol;
      }

      @NotNull
      public final String getMessage() {
         return this.message;
      }

      public final boolean component1() {
         return this.compatible;
      }

      public final int component2() {
         return this.selectedProtocol;
      }

      @NotNull
      public final String component3() {
         return this.message;
      }

      @NotNull
      public final NegotiationResult copy(boolean compatible, int selectedProtocol, @NotNull String message) {
         return new NegotiationResult(compatible, selectedProtocol, message);
      }

      @NotNull
      public String toString() {
         return "NegotiationResult(compatible=" + this.compatible + ", selectedProtocol=" + this.selectedProtocol + ", message=" + this.message + ")";
      }

      public int hashCode() {
         int result = Boolean.hashCode(this.compatible);
         result = result * 31 + Integer.hashCode(this.selectedProtocol);
         result = result * 31 + this.message.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof NegotiationResult)) {
            return false;
         } else {
            NegotiationResult negotiationResult = (NegotiationResult)other;
            if (this.compatible != negotiationResult.compatible) {
               return false;
            } else if (this.selectedProtocol != negotiationResult.selectedProtocol) {
               return false;
            } else {
               return Intrinsics.areEqual(this.message, negotiationResult.message);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010\u0015\u001a\u0018\u0012\u0014\u0012\u0012\u0012\u000e\b\u0001\u0012\n \u0017*\u0004\u0018\u00010\u00010\u00010\u00160\u0014X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$Companion;", "", "<init>", "()V", "INVENTORY_PERMISSION", "", "helloPayloads", "", "Ljava/util/UUID;", "Lcom/nexomc/nexo/nms/commons/mods/NexoCreativeInventory$HelloPayload;", "HELLO_MAGIC", "", "REGISTRY_MAGIC", "SERVER_MIN_PROTOCOL", "SERVER_MAX_PROTOCOL", "SERVER_IMPLEMENTATION_VERSION", "STATUS_OK", "STATUS_INCOMPATIBLE", "STATUS_NO_PERMISSION", "SERVER_FEATURES", "", "SAFE_COMPONENTS", "Lnet/minecraft/core/component/DataComponentType;", "kotlin.jvm.PlatformType", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
