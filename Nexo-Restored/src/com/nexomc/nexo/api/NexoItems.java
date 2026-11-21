package com.nexomc.nexo.api;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.items.CustomModelData;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.ItemParser;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.items.UpdateCallback;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.persistence.PersistentDataContainerView;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000¦\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010(\u001a\u00020)H\u0007b\u0002\b*J\u000e\u0010/\u001a\u00020)2\u0006\u00100\u001a\u00020\rJ\u001c\u00101\u001a\u00020)2\u0006\u00102\u001a\u00020$2\u0006\u00103\u001a\u00020%H\u0007b\u0002\b*JD\u00101\u001a\u00020)2\u0006\u00102\u001a\u00020$2\u0014\u00104\u001a\u0010\u0012\u0004\u0012\u000206\u0012\u0006\u0012\u0004\u0018\u000106052\u001e\u00107\u001a\u001a\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u00020608J]\u00109\u001a\u00020)2\u0006\u00102\u001a\u00020$2\u0014\u00104\u001a\u0010\u0012\u0004\u0012\u000206\u0012\u0006\u0012\u0004\u0018\u0001060:2\u001e\u00107\u001a\u001a\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u0002060;H\u0007b\u0002\b*b\f\b<\u0012\b\b=\u0012\u0004\b\b(1¢\u0006\u0002\b1J\u0014\u0010>\u001a\u00020)2\u0006\u00102\u001a\u00020$H\u0007b\u0002\b*J\u0014\u0010?\u001a\u0002062\u0006\u0010@\u001a\u000206H\u0007b\u0002\b*J\u0016\u0010A\u001a\u0004\u0018\u00010\r2\u0006\u0010@\u001a\u00020\u000eH\u0007b\u0002\b*J\u0018\u0010A\u001a\u0004\u0018\u00010\r2\b\u0010@\u001a\u0004\u0018\u000106H\u0007b\u0002\b*J\u0016\u0010B\u001a\u00020C2\b\u00100\u001a\u0004\u0018\u00010\rH\u0007b\u0002\b*J\u0016\u0010B\u001a\u00020C2\b\u0010D\u001a\u0004\u0018\u000106H\u0007b\u0002\b*J \u0010E\u001a\u00020C2\b\u0010F\u001a\u0004\u0018\u0001062\b\u0010G\u001a\u0004\u0018\u000106H\u0007b\u0002\b*J\u0018\u0010H\u001a\u0004\u0018\u00010\u000e2\b\u0010I\u001a\u0004\u0018\u00010\rH\u0007b\u0002\b*J\u001c\u0010J\u001a\b\u0012\u0004\u0012\u00020\u000e0K2\b\u0010I\u001a\u0004\u0018\u00010\rH\u0007b\u0002\b*J\u0018\u0010L\u001a\u0004\u0018\u00010\u000e2\b\u0010@\u001a\u0004\u0018\u000106H\u0007b\u0002\b*J\u001a\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000e0M2\u0006\u0010N\u001a\u00020\fH\u0007b\u0002\b*J \u0010O\u001a\u00020C2\b\u0010P\u001a\u0004\u0018\u00010\r2\b\u0010Q\u001a\u0004\u0018\u00010\rH\u0007b\u0002\b*J$\u0010\n\u001a\u001a\u0012\u0004\u0012\u00020\f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0R0RH\u0007b\u0002\b*J\u0018\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0RH\u0007b\u0002\b*J\u0012\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000e0SH\u0007b\u0002\b*J\u0012\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0SH\u0007b\u0002\b*J\u0017\u0010T\u001a\b\u0012\u0004\u0012\u00020\r0UH\u0007b\u0002\b*¢\u0006\u0002\u0010VR\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R2\u0010\n\u001a\u001a\u0012\u0004\u0012\u00020\f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\u000b0\u000bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R \u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R \u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0016\"\u0004\b\u001b\u0010\u0018R&\u0010\u001c\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u001d0\u000bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0010R&\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\u000bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0010\"\u0004\b!\u0010\u0012R \u0010\"\u001a\u000e\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020%0#X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R+\u0010+\u001a\u001c\u0012\u0004\u0012\u00020\r\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020-\u0018\u00010,0#¢\u0006\b\n\u0000\u001a\u0004\b.\u0010'¨\u0006W"},
   d2 = {"Lcom/nexomc/nexo/api/NexoItems;", "", "<init>", "()V", "ITEM_ID", "Lorg/bukkit/NamespacedKey;", "getITEM_ID", "()Lorg/bukkit/NamespacedKey;", "ITEM_ID$delegate", "Lkotlin/Lazy;", "itemMap", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "Ljava/io/File;", "", "Lcom/nexomc/nexo/items/ItemBuilder;", "getItemMap$com_nexomc_core", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "setItemMap$com_nexomc_core", "(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V", "itemNames", "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;", "getItemNames$com_nexomc_core", "()Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;", "setItemNames$com_nexomc_core", "(Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;)V", "items", "getItems$com_nexomc_core", "setItems$com_nexomc_core", "unexcludedItems", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "getUnexcludedItems$com_nexomc_core", "entries", "getEntries$com_nexomc_core", "setEntries$com_nexomc_core", "updateCallbacks", "", "Lnet/kyori/adventure/key/Key;", "Lcom/nexomc/nexo/items/UpdateCallback;", "getUpdateCallbacks$com_nexomc_core", "()Ljava/util/Map;", "loadItems", "", "Lkotlin/jvm/JvmStatic;", "itemConfigCache", "Lkotlin/Pair;", "Lorg/bukkit/configuration/ConfigurationSection;", "getItemConfigCache", "reloadItem", "itemId", "registerUpdateCallback", "key", "callback", "preUpdate", "Lkotlin/Function1;", "Lorg/bukkit/inventory/ItemStack;", "postUpdate", "Lkotlin/Function3;", "registerUpdateCallbackJava", "Ljava/util/function/Function;", "Lorg/apache/commons/lang3/function/TriFunction;", "Lkotlin/jvm/JvmName;", "name", "unregisterUpdateCallback", "updateItem", "item", "idFromItem", "exists", "", "itemStack", "isSameId", "firstStack", "secondStack", "itemFromId", "id", "optionalItemFromId", "Ljava/util/Optional;", "builderFromItem", "", "file", "hasMechanic", "itemID", "mechanicID", "", "", "unexcludedItemNames", "", "()[Ljava/lang/String;", "com.nexomc:core"}
)
public final class NexoItems {
   @NotNull
   public static final NexoItems INSTANCE = new NexoItems();
   @NotNull
   private static final Lazy ITEM_ID$delegate = LazyKt.lazy(NexoItems::ITEM_ID_delegate$lambda$0);
   @NotNull
   private static Object2ObjectLinkedOpenHashMap<File, Object2ObjectLinkedOpenHashMap<String, ItemBuilder>> itemMap = new Object2ObjectLinkedOpenHashMap();
   @NotNull
   private static ObjectLinkedOpenHashSet<String> itemNames = new ObjectLinkedOpenHashSet();
   @NotNull
   private static ObjectLinkedOpenHashSet<ItemBuilder> items = new ObjectLinkedOpenHashSet();
   @NotNull
   private static final Object2ObjectLinkedOpenHashMap<File, ObjectArrayList<ItemBuilder>> unexcludedItems = new Object2ObjectLinkedOpenHashMap();
   @NotNull
   private static Object2ObjectLinkedOpenHashMap<String, ItemBuilder> entries = new Object2ObjectLinkedOpenHashMap();
   @NotNull
   private static final Map<Key, UpdateCallback> updateCallbacks = (Map)(new LinkedHashMap());
   @NotNull
   private static final Map<String, Pair<File, ConfigurationSection>> itemConfigCache = (Map)(new Object2ObjectLinkedOpenHashMap());

   private NexoItems() {
   }

   @NotNull
   public final NamespacedKey getITEM_ID() {
      Lazy lazy = ITEM_ID$delegate;
      return (NamespacedKey)lazy.getValue();
   }

   @NotNull
   public final Object2ObjectLinkedOpenHashMap<File, Object2ObjectLinkedOpenHashMap<String, ItemBuilder>> getItemMap$com_nexomc_core() {
      return itemMap;
   }

   public final void setItemMap$com_nexomc_core(@NotNull Object2ObjectLinkedOpenHashMap<File, Object2ObjectLinkedOpenHashMap<String, ItemBuilder>> object2ObjectLinkedOpenHashMap) {
      itemMap = object2ObjectLinkedOpenHashMap;
   }

   @NotNull
   public final ObjectLinkedOpenHashSet<String> getItemNames$com_nexomc_core() {
      return itemNames;
   }

   public final void setItemNames$com_nexomc_core(@NotNull ObjectLinkedOpenHashSet<String> objectLinkedOpenHashSet) {
      itemNames = objectLinkedOpenHashSet;
   }

   @NotNull
   public final ObjectLinkedOpenHashSet<ItemBuilder> getItems$com_nexomc_core() {
      return items;
   }

   public final void setItems$com_nexomc_core(@NotNull ObjectLinkedOpenHashSet<ItemBuilder> objectLinkedOpenHashSet) {
      items = objectLinkedOpenHashSet;
   }

   @NotNull
   public final Object2ObjectLinkedOpenHashMap<File, ObjectArrayList<ItemBuilder>> getUnexcludedItems$com_nexomc_core() {
      return unexcludedItems;
   }

   @NotNull
   public final Object2ObjectLinkedOpenHashMap<String, ItemBuilder> getEntries$com_nexomc_core() {
      return entries;
   }

   public final void setEntries$com_nexomc_core(@NotNull Object2ObjectLinkedOpenHashMap<String, ItemBuilder> object2ObjectLinkedOpenHashMap) {
      entries = object2ObjectLinkedOpenHashMap;
   }

   @NotNull
   public final Map<Key, UpdateCallback> getUpdateCallbacks$com_nexomc_core() {
      return updateCallbacks;
   }

   @JvmStatic
   public static final void loadItems() {
      ItemParser.Companion.getCUSTOM_MODEL_DATAS_BY_ID().clear();
      CustomModelData.Companion.getDATAS().clear();
      CustomBlockRegistry.INSTANCE.getDATAS().clear();
      NexoPlugin.Companion.instance().configsManager().getItemConfigs$com_nexomc_core().clear();
      NexoPlugin.Companion.instance().configsManager().parseAllItemTemplates();
      NexoPlugin.Companion.instance().configsManager().assignAllUsedCustomModelDatas();
      NexoPlugin.Companion.instance().configsManager().assignAllUsedCustomVariations();
      NexoItems nexoItems = INSTANCE;
      itemMap.clear();
      nexoItems = INSTANCE;
      ((Map)itemMap).putAll((Map)NexoPlugin.Companion.instance().configsManager().parseItemConfig$com_nexomc_core());
      nexoItems = INSTANCE;
      itemNames.clear();
      nexoItems = INSTANCE;
      Collection collection = (Collection)itemNames;
      nexoItems = INSTANCE;
      Collection collection2 = itemMap.values();
      CollectionsKt.addAll(collection, SequencesKt.flatMap(CollectionsKt.asSequence((Iterable)collection2), NexoItems::loadItems$lambda$0));
      NexoItems nexoItems2 = INSTANCE;
      items.clear();
      nexoItems2 = INSTANCE;
      collection = (Collection)items;
      nexoItems2 = INSTANCE;
      Collection collection3 = itemMap.values();
      CollectionsKt.addAll(collection, SequencesKt.flatMap(CollectionsKt.asSequence((Iterable)collection3), NexoItems::loadItems$lambda$1));
      NexoItems nexoItems3 = INSTANCE;
      entries.clear();
      nexoItems3 = INSTANCE;
      Map map = (Map)entries;
      nexoItems3 = INSTANCE;
      Collection collection4 = itemMap.values();
      Sequence sequence = SequencesKt.flatMap(CollectionsKt.asSequence((Iterable)collection4), NexoItems::loadItems$lambda$2);
      Map destination = (Map)(new LinkedHashMap());

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         Map.Entry it = (Map.Entry)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getKey(), it.getValue());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      map.putAll(destination);
      NexoItems nexoItems4 = INSTANCE;
      unexcludedItems.clear();
      nexoItems4 = INSTANCE;
      Map map2 = (Map)unexcludedItems;
      NexoItems nexoItems5 = INSTANCE;
      MapsKt.putAll(map2, SequencesKt.mapNotNull(MapsKt.asSequence((Map)itemMap), NexoItems::loadItems$lambda$4));
      NexoItems nexoItems6 = INSTANCE;
      updateCallbacks.clear();
      EventUtils.INSTANCE.call(new NexoItemsLoadedEvent());
   }

   @NotNull
   public final Map<String, Pair<File, ConfigurationSection>> getItemConfigCache() {
      return itemConfigCache;
   }

   /** Reloads the definition of one item, using its cached config section or searching the loaded item files. */
   public final void reloadItem(@NotNull String itemId) {
      Map map = itemConfigCache;
      Object obj4 = map.get(itemId);
      Object obj;
      if (obj4 == null) {
         int value = 0;
         NexoItems nexoItems = INSTANCE;
         Set entrySet = itemMap.entrySet();
         Iterable iterable = (Iterable)entrySet;
         Iterator iterator = iterable.iterator();

         Map.Entry entry;
         while(true) {
            if (!iterator.hasNext()) {
               entry = null;
               break;
            }

            Object obj2 = iterator.next();
            Map.Entry it = (Map.Entry)obj2;
            int value2 = 0;
            if (((Object2ObjectLinkedOpenHashMap)it.getValue()).containsKey(itemId)) {
               entry = (Map.Entry)obj2;
               break;
            }
         }

         Map.Entry entry2 = entry;
         Pair pair;
         if (entry2 != null) {
            Map.Entry it = entry2;
            int value3 = 0;
            Object key = it.getKey();
            NexoYaml.Companion companion = NexoYaml.Companion;
            Object obj3 = it.getKey();
            ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)companion.loadConfiguration((File)obj3), itemId);
            pair = TuplesKt.to(key, configurationSection);
         } else {
            pair = null;
         }

         Object answer = pair;
         map.put(itemId, answer);
         obj = answer;
      } else {
         obj = obj4;
      }

      Pair pair2 = (Pair)obj;
      if (pair2 != null) {
         Pair pair3 = pair2;
         File file = (File)pair3.component1();
         ConfigurationSection config = (ConfigurationSection)pair3.component2();
         Object2ObjectLinkedOpenHashMap object2ObjectLinkedOpenHashMap = (Object2ObjectLinkedOpenHashMap)itemMap.get(file);
         if (object2ObjectLinkedOpenHashMap != null) {
            ItemBuilder itemBuilder = (ItemBuilder)object2ObjectLinkedOpenHashMap.put(itemId, (new ItemParser(config)).buildItem());
         }

      }
   }

   @JvmStatic
   public static final void registerUpdateCallback(@NotNull Key key, @NotNull UpdateCallback callback) {
      NexoItems nexoItems = INSTANCE;
      updateCallbacks.put(key, callback);
   }

   @JvmStatic
   @JvmName(
      name = "registerUpdateCallback"
   )
   public static final void registerUpdateCallback(@NotNull Key key, @NotNull final Function<ItemStack, ItemStack> preUpdate, @NotNull final TriFunction<String, ItemStack, ItemStack, ItemStack> postUpdate) {
      registerUpdateCallback(key, new UpdateCallback() {
         public ItemStack preUpdate(ItemStack itemStack) {
            return preUpdate.apply(itemStack);
         }

         public ItemStack postUpdate(String itemId, ItemStack itemStack, ItemStack preUpdateItemStack) {
            return postUpdate.apply(itemId, itemStack, preUpdateItemStack);
         }
      });
   }

   /** Removes the update callback registered for the key. */
   @JvmStatic
   public static final void unregisterUpdateCallback(@NotNull Key key) {
      NexoItems nexoItems = INSTANCE;
      updateCallbacks.remove(key);
   }

   /** Runs the item updater on the stack and returns the updated stack. */
   @JvmStatic
   @NotNull
   public static final ItemStack updateItem(@NotNull ItemStack item) {
      return ItemUpdater.Companion.updateItem(item);
   }

   /** Returns the Nexo item id stored in the builder's custom tag, or null if it has none. */
   @JvmStatic
   @Nullable
   public static final String idFromItem(@NotNull ItemBuilder item) {
      NamespacedKey iteM_ID = INSTANCE.getITEM_ID();
      PersistentDataType persistentDataType = PersistentDataType.STRING;
      return (String)item.customTag(iteM_ID, persistentDataType);
   }

   /** Returns the Nexo item id stored in the stack's persistent data, or null if the stack is null or not a Nexo item. */
   @JvmStatic
   @Nullable
   public static final String idFromItem(@Nullable ItemStack item) {
      String str;
      if (item != null) {
         PersistentDataContainerView persistentDataContainer = item.getPersistentDataContainer();
         if (persistentDataContainer != null) {
            str = persistentDataContainer.get(INSTANCE.getITEM_ID(), PersistentDataType.STRING);
            return str;
         }
      }

      str = null;
      return str;
   }

   /** Returns true if an item with this id is loaded. */
   @JvmStatic
   public static final boolean exists(@Nullable String itemId) {
      NexoItems nexoItems = INSTANCE;
      return itemNames.contains(itemId);
   }

   /** Returns true if the stack carries the id of a loaded Nexo item. */
   @JvmStatic
   public static final boolean exists(@Nullable ItemStack itemStack) {
      NexoItems nexoItems = INSTANCE;
      return itemNames.contains(idFromItem(itemStack));
   }

   /** Returns true if both stacks carry the same Nexo item id. Two stacks that are not Nexo items both have a null id and therefore count as equal. */
   @JvmStatic
   public static final boolean isSameId(@Nullable ItemStack firstStack, @Nullable ItemStack secondStack) {
      String firstId = idFromItem(firstStack);
      String secondId = idFromItem(secondStack);
      return Intrinsics.areEqual(firstId, secondId);
   }

   /** Returns the builder registered for the item id, or null if there is none. */
   @JvmStatic
   @Nullable
   public static final ItemBuilder itemFromId(@Nullable String id) {
      NexoItems nexoItems = INSTANCE;
      return entries.get(id);
   }

   /** Same as {@link #itemFromId(String)} but wrapped in an Optional. */
   @JvmStatic
   @NotNull
   public static final Optional<ItemBuilder> optionalItemFromId(@Nullable String id) {
      Optional optional = Optional.ofNullable(itemFromId(id));
      return optional;
   }

   /** Returns the builder for the Nexo item the stack represents, or null if it is not a registered Nexo item. */
   @JvmStatic
   @Nullable
   public static final ItemBuilder builderFromItem(@Nullable ItemStack item) {
      return itemFromId(idFromItem(item));
   }

   @JvmStatic
   @NotNull
   public static final List<ItemBuilder> unexcludedItems(@NotNull File file) {
      NexoItems nexoItems = INSTANCE;
      ObjectArrayList objectArrayList = (ObjectArrayList)unexcludedItems.get(file);
      return objectArrayList != null ? (List)objectArrayList : CollectionsKt.emptyList();
   }

   @JvmStatic
   public static final boolean hasMechanic(@Nullable String itemID, @Nullable String mechanicID) {
      MechanicFactory mechanicFactory = MechanicsManager.INSTANCE.mechanicFactory(mechanicID);
      return (mechanicFactory != null ? mechanicFactory.getMechanic(itemID) : null) != null;
   }

   @JvmStatic
   @NotNull
   public static final Map<File, Map<String, ItemBuilder>> itemMap() {
      NexoItems nexoItems = INSTANCE;
      return (Map)itemMap;
   }

   @JvmStatic
   @NotNull
   public static final Map<String, ItemBuilder> entries() {
      NexoItems nexoItems = INSTANCE;
      return (Map)entries;
   }

   @JvmStatic
   @NotNull
   public static final Set<ItemBuilder> items() {
      NexoItems nexoItems = INSTANCE;
      return (Set)items;
   }

   @JvmStatic
   @NotNull
   public static final Set<String> itemNames() {
      NexoItems nexoItems = INSTANCE;
      return (Set)itemNames;
   }

   @JvmStatic
   @NotNull
   public static final String[] unexcludedItemNames() {
      NexoItems nexoItems = INSTANCE;
      Collection collection = itemMap.values();
      Iterable iterable = (Iterable)collection;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         Object2ObjectLinkedOpenHashMap it = (Object2ObjectLinkedOpenHashMap)element;
         int value = 0;
         Map map = (Map)it;
         Map map2 = (Map)(new Object2ObjectOpenHashMap());

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            if (((ItemBuilder)entry.getValue()).getNexoMeta().getExcludedFromCommands().isNotTrue()) {
               map2.put(entry.getKey(), entry.getValue());
            }
         }

         Set keySet = ((Object2ObjectOpenHashMap)map2).keySet();
         Iterable list = (Iterable)keySet;
         CollectionsKt.addAll(destination, list);
      }

      Collection collection2 = (Collection)((ObjectArrayList)destination);
      return (String[])collection2.toArray(new String[0]);
   }

   private static final NamespacedKey ITEM_ID_delegate$lambda$0() {
      return new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "id");
   }

   private static final Sequence loadItems$lambda$0(Object2ObjectLinkedOpenHashMap it) {
      Set keySet = it.keySet();
      return CollectionsKt.asSequence((Iterable)keySet);
   }

   private static final Sequence loadItems$lambda$1(Object2ObjectLinkedOpenHashMap it) {
      Collection collection = it.values();
      return CollectionsKt.asSequence((Iterable)collection);
   }

   private static final Sequence loadItems$lambda$2(Object2ObjectLinkedOpenHashMap it) {
      Set entrySet = it.entrySet();
      return CollectionsKt.asSequence((Iterable)entrySet);
   }

   private static final Pair loadItems$lambda$4(Map.Entry entry) {
      File file = (File)entry.getKey();
      Object2ObjectLinkedOpenHashMap map = (Object2ObjectLinkedOpenHashMap)entry.getValue();
      Collection collection = map.values();
      Iterable iterable = (Iterable)collection;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         ItemBuilder it = (ItemBuilder)element;
         int value = 0;
         if (it.getNexoMeta().getExcludedFromInventory().isNotTrue()) {
            destination.add(element);
         }
      }

      ObjectArrayList filtered = (ObjectArrayList)destination;
      return !((Collection)filtered).isEmpty() ? TuplesKt.to(file, filtered) : null;
   }
}
