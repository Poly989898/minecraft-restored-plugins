package com.nexomc.nexo.items;

import com.nexomc.nexo.decomp.ResultShim;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.nms.IItemUtils;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.UseRemainder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\b\u001a\u00020\t*\u00020\nH\u0007b\u0002\b\u000bJ\u0010\u0010\f\u001a\u00020\t*\u00020\rH\u0007b\u0002\b\u000bJ\u0010\u0010\u000e\u001a\u00020\t*\u00020\u000fH\u0007b\u0002\b\u000bJ\u0010\u0010\u0010\u001a\u00020\t*\u00020\u0011H\u0007b\u0002\b\u000bJ\u001c\u0010\u0012\u001a\u00020\t*\u00020\u0013H\u0007b\u000e\b\u000b\u0012\n\b\u0014\u0012\u0006\b\n0\u00158\u0016J\u001c\u0010\u0012\u001a\u00020\t*\u00020\u0017H\u0007b\u000e\b\u000b\u0012\n\b\u0014\u0012\u0006\b\n0\u00158\u0016J\u001a\u0010\u0018\u001a\u00020\t*\u00020\u0019H\u0007b\f\b\u000b\u0012\b\b\u001a\u0012\u0004\b\u0007\u0010\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/items/ItemUpdater;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "updateEntityContents", "", "updateTileEntityContents", "updateItems", "onEntityLoad", "", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "Lorg/bukkit/event/EventHandler;", "onChunkLoad", "Lorg/bukkit/event/world/ChunkLoadEvent;", "onLootGenerate", "Lorg/bukkit/event/world/LootGenerateEvent;", "onPlayerPickUp", "Lorg/bukkit/event/entity/EntityPickupItemEvent;", "onItemEnchant", "Lorg/bukkit/event/enchantment/PrepareItemEnchantEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "MONITOR", "Lorg/bukkit/event/inventory/PrepareAnvilEvent;", "onUseConvertedTo", "Lorg/bukkit/event/player/PlayerItemConsumeEvent;", "ignoreCancelled", "Companion", "com.nexomc:core"}
)
public final class ItemUpdater implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   private final boolean updateEntityContents;
   private final boolean updateTileEntityContents;
   private final boolean updateItems;
   @NotNull
   private static final NamespacedKey IF_UUID;
   @NotNull
   private static final NamespacedKey MF_GUI;
   @NotNull
   private static final Material[] TILE_ENTITIES;
   @NotNull
   private static final Lazy<List<DataComponentType>> componentsToCopy$delegate;

   public ItemUpdater() {
      this.updateEntityContents = Settings.UPDATE_ENTITY_CONTENTS.toBool();
      this.updateTileEntityContents = Settings.UPDATE_TILE_ENTITY_CONTENTS.toBool();
      this.updateItems = Settings.UPDATE_ITEMS.toBool();
      SchedulerUtils.INSTANCE.syncDelayedTask(2L, (() -> ItemUpdater._init_$lambda$0(this)));
   }

   @EventHandler
   public final void onEntityLoad(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      if (this.updateEntityContents) {
         SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
         Entity entity = entityAddToWorldEvent.getEntity();
         schedulerUtils.runAtEntityLater(entity, 2L, (a0_ -> ItemUpdater.onEntityLoad$lambda$0(entityAddToWorldEvent, (Entity)a0_)));
      }

   }

   @EventHandler
   public final void onChunkLoad(@NotNull ChunkLoadEvent chunkLoadEvent) {
      if (this.updateTileEntityContents && !chunkLoadEvent.isNewChunk()) {
         Collection tileEntities = chunkLoadEvent.getChunk().getTileEntities((Block a0_) -> ItemUpdater.onChunkLoad$lambda$0(a0_), false);
         Iterable iterable = (Iterable)tileEntities;

         for(Object element : iterable) {
            BlockState tileEntity = (BlockState)element;
            int value = 0;
            InventoryHolder inventoryHolder = tileEntity instanceof InventoryHolder ? (InventoryHolder)tileEntity : null;
            if ((tileEntity instanceof InventoryHolder ? (InventoryHolder)tileEntity : null) != null) {
               Inventory inventory2 = inventoryHolder.getInventory();
               if (inventory2 != null) {
                  Inventory inventory = inventory2;
                  ItemStack[] contents = inventory.getContents();
                  Object[] objs = contents;
                  int value5 = 0;
                  int value2 = 0;

                  for(int i = objs.length; value2 < i; ++value2) {
                     Object obj = objs[value2];
                     int value3 = value5++;
                     ItemStack item = (ItemStack)obj;
                     int index = value3;
                     int value4 = 0;
                     if (item != null) {
                        inventory.setItem(index, Companion.updateItem(item));
                     }
                  }
               }
            }
         }

      }
   }

   @EventHandler
   public final void onLootGenerate(@NotNull LootGenerateEvent lootGenerateEvent) {
      List loot = lootGenerateEvent.getLoot();
      Iterable iterable = (Iterable)loot;
      Companion companion = Companion;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         ItemStack p0 = (ItemStack)item;
         int value = 0;
         destination.add(companion.updateItem(p0));
      }

      lootGenerateEvent.setLoot((Collection)((List)destination));
   }

   @EventHandler
   public final void onPlayerPickUp(@NotNull EntityPickupItemEvent entityPickupItemEvent) {
      if (Settings.UPDATE_ITEMS.toBool() && entityPickupItemEvent.getEntity() instanceof Player) {
         ItemStack itemStack = entityPickupItemEvent.getItem().getItemStack();
         Companion companion = Companion;
         ItemStack itemStack2 = entityPickupItemEvent.getItem().getItemStack();
         Pair pair = TuplesKt.to(itemStack, companion.updateItem(itemStack2));
         Object component1 = pair.component1();
         ItemStack oldItem = (ItemStack)component1;
         ItemStack newItem = (ItemStack)pair.component2();
         if (!Intrinsics.areEqual(oldItem, newItem)) {
            entityPickupItemEvent.getItem().setItemStack(newItem);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void onItemEnchant(@NotNull PrepareItemEnchantEvent prepareItemEnchantEvent) {
      boolean flag;
      EventUtils eventUtils;
      Cancellable cancellable;
      block: {
         eventUtils = EventUtils.INSTANCE;
         cancellable = (Cancellable)prepareItemEnchantEvent;
         ItemBuilder itemBuilder = NexoItems.builderFromItem(prepareItemEnchantEvent.getItem());
         if (itemBuilder != null) {
            NexoMeta nexoMeta = itemBuilder.getNexoMeta();
            if (nexoMeta != null) {
               flag = nexoMeta.getDisableEnchanting();
               break block;
            }
         }

         flag = false;
      }

      eventUtils.cancelIf(cancellable, flag);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void onItemEnchant(@NotNull PrepareAnvilEvent prepareAnvilEvent) {
      boolean flag;
      ItemStack firstItem = prepareAnvilEvent.getInventory().getFirstItem();
      if (firstItem != null) {
         ItemStack item;
         block: {
            item = firstItem;
            ItemBuilder itemBuilder = NexoItems.builderFromItem(item);
            if (itemBuilder != null) {
               NexoMeta nexoMeta = itemBuilder.getNexoMeta();
               if (nexoMeta != null) {
                  flag = nexoMeta.getDisableEnchanting();
                  break block;
               }
            }

            flag = false;
         }

         if (flag) {
            firstItem = prepareAnvilEvent.getResult();
            if (!Intrinsics.areEqual(firstItem != null ? firstItem.getEnchantments() : null, item.getEnchantments())) {
               prepareAnvilEvent.setResult((ItemStack)null);
            }
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onUseConvertedTo(@NotNull PlayerItemConsumeEvent playerItemConsumeEvent) {
      UseRemainder data = (UseRemainder)playerItemConsumeEvent.getItem().getData(DataComponentTypes.USE_REMAINDER);
      if (data != null) {
         ItemStack transformInto = data.transformInto();
         if (transformInto != null) {
            ItemStack useRemainder = transformInto;
            if (!playerItemConsumeEvent.getItem().isSimilar(Companion.updateItem(useRemainder))) {
               return;
            }

            PlayerInventory playerInventory = playerItemConsumeEvent.getPlayer().getInventory();
            PlayerInventory inventory = playerInventory;
            if (inventory.firstEmpty() == -1) {
               playerItemConsumeEvent.setItem(playerItemConsumeEvent.getItem().add(useRemainder.getAmount()));
               Unit unit = Unit.INSTANCE;
            } else {
               Intrinsics.checkNotNullExpressionValue(SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity((Entity)playerItemConsumeEvent.getPlayer(), (a0_ -> ItemUpdater.onUseConvertedTo$lambda$0(inventory, playerItemConsumeEvent, (WrappedTask)a0_))), "runAtEntity(...)");
            }

            return;
         }
      }

   }

   private static final boolean lambda$0$0(Block it) {
      return ArraysKt.contains(TILE_ENTITIES, it.getType());
   }

   private static final Unit lambda$0$1(BlockState tileEntity) {
      InventoryHolder inventoryHolder = tileEntity instanceof InventoryHolder ? (InventoryHolder)tileEntity : null;
      if ((tileEntity instanceof InventoryHolder ? (InventoryHolder)tileEntity : null) != null) {
         Inventory inventory2 = inventoryHolder.getInventory();
         if (inventory2 != null) {
            Inventory inventory = inventory2;
            ItemStack[] contents = inventory.getContents();
            Object[] objs = contents;
            int index = 0;
            int value = 0;

            for(int j = objs.length; value < j; ++value) {
               Object obj = objs[value];
               int value2 = index++;
               ItemStack item = (ItemStack)obj;
               int i = value2;
               int value3 = 0;
               if (item != null) {
                  inventory.setItem(i, Companion.updateItem(item));
               }
            }

            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit _init_$lambda$0(ItemUpdater itemUpdater) {
      if (itemUpdater.updateEntityContents) {
         SchedulerUtils.INSTANCE._runAtWorldEntities((Entity p0) -> {
            Companion.updateEntityInventories(p0);
            return Unit.INSTANCE;
         });
      }

      if (itemUpdater.updateTileEntityContents) {
         SchedulerUtils.INSTANCE.runAtWorldTileStates(ItemUpdater::lambda$0$0, ItemUpdater::lambda$0$1);
      }

      return Unit.INSTANCE;
   }

   private static final Unit onEntityLoad$lambda$0(EntityAddToWorldEvent entityAddToWorldEvent, Entity it) {
      Companion companion = Companion;
      Entity entity = entityAddToWorldEvent.getEntity();
      companion.updateEntityInventories(entity);
      return Unit.INSTANCE;
   }

   private static final boolean onChunkLoad$lambda$0(Block it) {
      return ArraysKt.contains(TILE_ENTITIES, it.getType());
   }

   private static final boolean onChunkLoad$lambda$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final void onUseConvertedTo$lambda$0(PlayerInventory inventory, PlayerItemConsumeEvent playerItemConsumeEvent, WrappedTask it) {
      int i = 0;
      int size = inventory.getSize();
      if (i <= size) {
         while(true) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
               ItemStack oldItem = item;
               ItemStack p0 = Companion.updateItem(oldItem);
               item = playerItemConsumeEvent.getItem();
               ItemStack itemStack = item;
               int value = 0;
               item = itemStack.isSimilar(p0) ? p0 : null;
               if (item != null) {
                  ItemStack newItem = item;
                  inventory.setItem(i, (ItemStack)null);
                  ItemStack[] itemStacks = new ItemStack[]{newItem};
                  inventory.addItem(itemStacks);
               }
            }

            if (i == size) {
               break;
            }

            ++i;
         }
      }

   }

   private static final List componentsToCopy_delegate$lambda$0() {
      Companion companion = Companion;

      Object obj2;
      try {
         int value = 0;
         DataComponentType[] dataComponentTypes = new DataComponentType[12];
         DataComponentType.Valued valued = DataComponentTypes.DYED_COLOR;
         dataComponentTypes[0] = valued;
         valued = DataComponentTypes.MAP_DECORATIONS;
         dataComponentTypes[1] = valued;
         valued = DataComponentTypes.BASE_COLOR;
         dataComponentTypes[2] = valued;
         valued = DataComponentTypes.CONTAINER;
         dataComponentTypes[3] = valued;
         valued = DataComponentTypes.CONTAINER_LOOT;
         dataComponentTypes[4] = valued;
         valued = DataComponentTypes.BUNDLE_CONTENTS;
         dataComponentTypes[5] = valued;
         valued = DataComponentTypes.PAINTING_VARIANT;
         dataComponentTypes[6] = valued;
         valued = DataComponentTypes.WRITTEN_BOOK_CONTENT;
         dataComponentTypes[7] = valued;
         valued = DataComponentTypes.WRITABLE_BOOK_CONTENT;
         dataComponentTypes[8] = valued;
         valued = DataComponentTypes.CHARGED_PROJECTILES;
         dataComponentTypes[9] = valued;
         DataComponentType.NonValued nonValued = DataComponentTypes.INTANGIBLE_PROJECTILE;
         dataComponentTypes[10] = nonValued;
         DataComponentType.Valued valued2 = DataComponentTypes.STORED_ENCHANTMENTS;
         dataComponentTypes[11] = valued2;
         obj2 = ResultShim.ok(CollectionsKt.listOf(dataComponentTypes));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      return (List)(ResultShim.isFailure(obj) ? emptyList : obj);
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:if-uuid");
      IF_UUID = namespacedKey;
      namespacedKey = NamespacedKey.fromString("nexo:mf-gui");
      MF_GUI = namespacedKey;
      Material[] materials = new Material[]{Material.BARREL, Material.CHEST, Material.TRAPPED_CHEST, Material.CRAFTER, Material.FURNACE_MINECART, Material.DECORATED_POT, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.SMOKER, Material.FURNACE, Material.BLAST_FURNACE, Material.BREWING_STAND, Material.JUKEBOX};
      Set values = Tag.SHULKER_BOXES.getValues();
      TILE_ENTITIES = (Material[])ArraysKt.plus(materials, (Collection)values);
      componentsToCopy$delegate = LazyKt.lazy(ItemUpdater::componentsToCopy_delegate$lambda$0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013J(\u0010\u001c\u001a\u00020\u001d2\u000e\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u001f\u0018\u00010\u00162\u000e\u0010 \u001a\n\u0012\u0004\u0012\u00020\u001f\u0018\u00010\u0016H\u0002J\u0015\u0010!\u001a\u00070\"¢\u0006\u0002\b#2\u0006\u0010$\u001a\u00020\nH\u0002R\u0013\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0007\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR!\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00168BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0018\u0010\u0019¨\u0006%"},
      d2 = {"Lcom/nexomc/nexo/items/ItemUpdater$Companion;", "", "<init>", "()V", "IF_UUID", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "MF_GUI", "TILE_ENTITIES", "", "Lorg/bukkit/Material;", "getTILE_ENTITIES", "()[Lorg/bukkit/Material;", "[Lorg/bukkit/Material;", "updateEntityInventories", "", "entity", "Lorg/bukkit/entity/Entity;", "updateItem", "Lorg/bukkit/inventory/ItemStack;", "oldItem", "componentsToCopy", "", "Lio/papermc/paper/datacomponent/DataComponentType;", "getComponentsToCopy", "()Ljava/util/List;", "componentsToCopy$delegate", "Lkotlin/Lazy;", "loreContentEquals", "", "oldLore", "Lnet/kyori/adventure/text/Component;", "newLore", "translatable", "Lnet/kyori/adventure/text/TranslatableComponent;", "Lorg/jetbrains/annotations/NotNull;", "type", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Material[] getTILE_ENTITIES() {
         return ItemUpdater.TILE_ENTITIES;
      }

      public final void updateEntityInventories(@NotNull Entity entity) {
         ItemFrame itemFrame = entity instanceof ItemFrame ? (ItemFrame)entity : null;
         if ((entity instanceof ItemFrame ? (ItemFrame)entity : null) != null) {
            ItemStack item = ((ItemFrame)entity).getItem();
            itemFrame.setItem(this.updateItem(item), false);
         }

         ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
         if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
            ItemStack itemStack = ((ItemDisplay)entity).getItemStack();
            itemDisplay.setItemStack(this.updateItem(itemStack));
         }

         Item item2 = entity instanceof Item ? (Item)entity : null;
         if ((entity instanceof Item ? (Item)entity : null) != null) {
            ItemStack itemStack2 = ((Item)entity).getItemStack();
            item2.setItemStack(this.updateItem(itemStack2));
         }

         InventoryHolder inventoryHolder = entity instanceof InventoryHolder ? (InventoryHolder)entity : null;
         if ((entity instanceof InventoryHolder ? (InventoryHolder)entity : null) != null) {
            Inventory inventory = inventoryHolder.getInventory();
            if (inventory != null) {
               ItemStack[] contents = inventory.getContents();
               if (contents != null) {
                  Object[] objs = contents;
                  int index = 0;
                  int value = 0;

                  for(int j = objs.length; value < j; ++value) {
                     Object obj = objs[value];
                     int i = index++;
                     int value9 = 0;
                     if (obj != null) {
                        ((InventoryHolder)entity).getInventory().setItem(i, ItemUpdater.Companion.updateItem((ItemStack)obj));
                     }
                  }
               }
            }
         }

         Player player = entity instanceof Player ? (Player)entity : null;
         if ((entity instanceof Player ? (Player)entity : null) != null) {
            Inventory enderChest = player.getEnderChest();
            if (enderChest != null) {
               ItemStack[] itemStacks = enderChest.getContents();
               if (itemStacks != null) {
                  Object[] objs2 = itemStacks;
                  int value10 = 0;
                  int value2 = 0;

                  for(int k = objs2.length; value2 < k; ++value2) {
                     Object obj2 = objs2[value2];
                     int i = value10++;
                     int value3 = 0;
                     if (obj2 != null) {
                        ((Player)entity).getEnderChest().setItem(i, ItemUpdater.Companion.updateItem((ItemStack)obj2));
                     }
                  }
               }
            }
         }

         LivingEntity livingEntity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
         if ((entity instanceof LivingEntity ? (LivingEntity)entity : null) != null) {
            EntityEquipment entityEquipment = livingEntity.getEquipment();
            if (entityEquipment != null) {
               EntityEquipment entityEquipment2 = entityEquipment;
               EntityEquipment equipment = entityEquipment2;
               int value4 = 0;
               Iterable iterable = (Iterable)java.util.Arrays.asList(EquipmentSlot.values());

               for(Object element : iterable) {
                  EquipmentSlot slot = (EquipmentSlot)element;
                  int value5 = 0;
                  Companion companion = ItemUpdater.Companion;

                  Object obj3;
                  try {
                     int value6 = 0;
                     ItemStack p0 = equipment.getItem(slot);
                     int value7 = 0;
                     obj3 = ResultShim.ok(companion.updateItem(p0));
                  } catch (Throwable e) {
                     obj3 = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  if (ResultShim.isSuccess(obj3)) {
                     ItemStack it = (ItemStack)obj3;
                     int value8 = 0;
                     equipment.setItem(slot, it);
                  }
               }
            }
         }

      }

      @NotNull
      public final ItemStack updateItem(@NotNull ItemStack oldItem) {
         java.util.Map<org.bukkit.enchantments.Enchantment,java.lang.Integer> map;
         java.util.Map<org.bukkit.enchantments.Enchantment,java.lang.Integer> map2;
         if (NexoPlugin.Companion.instance().converter().getItemsadderConverter().getConvertItems()) {
            NMSHandlers.handler().getPluginConverter().convertItemsAdder(oldItem);
         }

         if (NexoPlugin.Companion.instance().converter().getOraxenConverter().getConvertItems()) {
            NMSHandlers.handler().getPluginConverter().convertOraxen(oldItem);
         }

         String str = NexoItems.idFromItem(oldItem);
         if (str == null) {
            return oldItem;
         } else {
            String id = str;
            Iterable iterable2 = (Iterable)NexoItems.INSTANCE.getUpdateCallbacks$com_nexomc_core().values();
            Object accumulator = oldItem;

            for(Object element : iterable2) {
               UpdateCallback callback = (UpdateCallback)element;
               int value = 0;
               ItemStack itemStack = callback.preUpdate((ItemStack)accumulator);
               if (itemStack == null) {
                  return (ItemStack)accumulator;
               }

               accumulator = itemStack;
            }

            ItemStack preUpdateItem = (ItemStack)accumulator;
            ((ItemStack)accumulator).editPersistentDataContainer((a0_ -> Companion.updateItem$lambda$1(preUpdateItem, (PersistentDataContainer)a0_)));
            IItemUtils itemUtils = NMSHandlers.handler().itemUtils();
            ItemBuilder itemBuilder = NexoItems.itemFromId(id);
            if (itemBuilder != null) {
               ItemBuilder it = itemBuilder;
               int value2 = 0;
               itemBuilder = !it.getNexoMeta().getNoUpdate() ? it : null;
               if (itemBuilder != null) {
                  ItemBuilder newItemBuilder; ItemStack newItem; java.util.Map<org.bukkit.enchantments.Enchantment,java.lang.Integer> oldEnchants;
                  block: {
                     newItemBuilder = itemBuilder;
                     ItemStack itemStack2 = newItemBuilder.build().asQuantity(((ItemStack)accumulator).getAmount());
                     newItem = itemUtils.copyItemNBTTags((ItemStack)accumulator, itemStack2);
                     newItem.editPersistentDataContainer((a0_ -> Companion.updateItem$lambda$3(preUpdateItem, (PersistentDataContainer)a0_)));
                     ItemEnchantments data = (ItemEnchantments)((ItemStack)accumulator).getData(DataComponentTypes.ENCHANTMENTS);
                     if (data != null) {
                        map = data.enchantments();
                        if (map != null) {
                           break block;
                        }
                     }

                     map = MapsKt.emptyMap();
                  }

                  block: {
                     oldEnchants = map;
                     ItemEnchantments itemEnchantments = (ItemEnchantments)newItem.getData(DataComponentTypes.ENCHANTMENTS);
                     if (itemEnchantments != null) {
                        map2 = itemEnchantments.enchantments();
                        if (map2 != null) {
                           break block;
                        }
                     }

                     map2 = MapsKt.emptyMap();
                  }

                  Map newEnchants = map2;
                  Map newLore = MapsKt.plus(newEnchants, oldEnchants);
                  int value3 = 0;
                  Map map3 = !newLore.isEmpty() ? newLore : null;
                  if (map3 != null) {
                     Map it_6 = map3;
                     value3 = 0;
                     newItem.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().addAll(it_6).build());
                  }

                  ItemLore itemLore = (ItemLore)preUpdateItem.getData(DataComponentTypes.LORE);
                  List oldLore = itemLore != null ? itemLore.lines() : null;
                  itemLore = (ItemLore)newItem.getData(DataComponentTypes.LORE);
                  List newLore_5 = itemLore != null ? itemLore.lines() : null;
                  Collection collection = (Collection)oldLore;
                  List chosenLore = collection == null || collection.isEmpty() ? newLore_5 : (Settings.OVERRIDE_ITEM_LORE.toBool() && !this.loreContentEquals(oldLore, newLore_5) ? newLore_5 : oldLore);
                  if (chosenLore != null) {
                     newItem.setData(DataComponentTypes.LORE, ItemLore.lore(chosenLore));
                  } else {
                     newItem.unsetData((DataComponentType)DataComponentTypes.LORE);
                  }

                  Integer number = (Integer)newItem.getData(DataComponentTypes.MAX_DAMAGE);
                  if (number != null) {
                     int maxDamage = ((Number)number).intValue();
                     int item = 0;
                     number = (Integer)preUpdateItem.getData(DataComponentTypes.DAMAGE);
                     int oldDamage = number != null ? number : 0;
                     newItem.setData(DataComponentTypes.DAMAGE, RangesKt.coerceAtMost(oldDamage, maxDamage));
                  }

                  if (preUpdateItem.getType() == Material.WRITTEN_BOOK && newItem.getType() == Material.WRITABLE_BOOK) {
                     newItem.setType(Material.WRITTEN_BOOK);
                  }

                  DataComponentType.Valued valued = DataComponentTypes.TRIM;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.FOOD;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.MAX_STACK_SIZE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.ITEM_NAME;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.CUSTOM_NAME;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.JUKEBOX_PLAYABLE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.EQUIPPABLE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.USE_COOLDOWN;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.ENCHANTABLE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.DAMAGE_RESISTANT;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.TOOLTIP_STYLE;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  valued = DataComponentTypes.USE_REMAINDER;
                  updateItem$copyOldIfAbsent(newItem, preUpdateItem, valued);
                  if (!newItem.hasData((DataComponentType)DataComponentTypes.GLIDER) && preUpdateItem.hasData((DataComponentType)DataComponentTypes.GLIDER)) {
                     newItem.setData(DataComponentTypes.GLIDER);
                  }

                  if (newItemBuilder.getAttributeModifiers().isNotEmpty()) {
                     newItem.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, newItemBuilder.getAttributeModifiers().build(newItem));
                  }

                  Colorable colorable = ColorableKt.asColorable(preUpdateItem);
                  if (colorable != null) {
                     Colorable oldColorable = colorable;
                     int value4 = 0;
                     colorable = ColorableKt.asColorable(newItem);
                     if (colorable != null) {
                        colorable.setColor(oldColorable.getColor());
                     }

                     colorable = ColorableKt.asColorable(newItem);
                     if (colorable != null) {
                        colorable.setPotionContentColor(oldColorable.getPotionContentColor());
                     }
                  }

                  Companion companion = this;

                  Object obj;
                  try {
                     Companion companion2 = companion;
                     int value5 = 0;
                     newItem.copyDataFrom(preUpdateItem, (DataComponentType a0_) -> ItemUpdater.componentsToCopy$delegate.getValue().contains(a0_));
                     obj = ResultShim.ok(Unit.INSTANCE);
                  } catch (Throwable e) {
                     obj = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  if (ResultShim.exceptionOrNull(obj) != null) {
                     int value6 = 0;
                     itemUtils.paintingVariant(newItem, itemUtils.paintingVariant(preUpdateItem));
                  }

                  itemUtils.customDataComponent(newItem, newItemBuilder.getCustomDataMap());
                  Iterable iterable = (Iterable)NexoItems.INSTANCE.getUpdateCallbacks$com_nexomc_core().values();
                  Object obj2 = newItem;

                  for(Object obj3 : iterable) {
                     UpdateCallback callback = (UpdateCallback)obj3;
                     int value7 = 0;
                     ItemStack itemStack3 = callback.postUpdate(id, (ItemStack)obj2, preUpdateItem);
                     obj2 = itemStack3;
                  }

                  return (ItemStack)obj2;
               }
            }

            return (ItemStack)accumulator;
         }
      }

      private final List<DataComponentType> getComponentsToCopy() {
         Lazy lazy = ItemUpdater.componentsToCopy$delegate;
         return (List)lazy.getValue();
      }

      private final boolean loreContentEquals(List<? extends Component> oldLore, List<? extends Component> newLore) {
         if (oldLore != null && newLore != null) {
            if (oldLore.size() != newLore.size()) {
               return false;
            } else {
               Iterable indices = (Iterable)CollectionsKt.getIndices((Collection)oldLore);
               boolean flag;
               if (indices instanceof Collection && ((Collection)indices).isEmpty()) {
                  flag = true;
               } else {
                  Iterator iterator = indices.iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        flag = true;
                        break;
                     }

                     int element = ((IntIterator)iterator).nextInt();
                     int value = 0;
                     if (!Intrinsics.areEqual(AdventureUtils.INSTANCE.getMINI_MESSAGE().serialize((Component)oldLore.get(element)), AdventureUtils.INSTANCE.getMINI_MESSAGE().serialize((Component)newLore.get(element)))) {
                        flag = false;
                        break;
                     }
                  }
               }

               return flag;
            }
         } else {
            return Intrinsics.areEqual(oldLore, newLore);
         }
      }

      private final TranslatableComponent translatable(Material type) {
         String str = type.isBlock() ? "block" : "item";
         String str2 = type.name().toLowerCase(Locale.ROOT);
         TranslatableComponent translatableComponent = Component.translatable(str + ".minecraft." + str2);
         return translatableComponent;
      }

      private static final <T> void updateItem$copyOldIfAbsent(ItemStack newItem, ItemStack preUpdateItem, DataComponentType.Valued<T> type) {
         if (!newItem.hasData((DataComponentType)type) || !newItem.isDataOverridden((DataComponentType)type)) {
            Object data = preUpdateItem.getData(type);
            if (data != null) {
               Object it = data;
               int value = 0;
               newItem.setData(type, (T)it);
            }
         }

      }

      private static final void updateItem$lambda$1(ItemStack preUpdateItem, PersistentDataContainer it) {
         it.remove(ItemUpdater.IF_UUID);
         it.remove(ItemUpdater.MF_GUI);
         String str = (String)it.get(Glyph.Companion.getORIGINAL_ITEM_RENAME(), PersistentDataType.STRING);
         if (str != null) {
            String originalName = str;
            int value = 0;
            it.remove(Glyph.Companion.getORIGINAL_ITEM_RENAME());
            Map data = MapsKt.mapOf(TuplesKt.to(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING(), originalName));
            NMSHandlers.handler().itemUtils().customDataComponent(preUpdateItem, data);
         }

      }

      private static final void updateItem$lambda$3(ItemStack preUpdateItem, PersistentDataContainer pdc) {
         preUpdateItem.getPersistentDataContainer().copyTo(pdc, true);
      }

      private static final boolean updateItem$lambda$9$contains(List list, DataComponentType p0) {
         return list.contains(p0);
      }
   }
}
