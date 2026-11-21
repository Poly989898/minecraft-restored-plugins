package com.nexomc.nexo.mechanics.storage;

import com.nexomc.libs.gui.builder.gui.StorageBuilder;
import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.libs.gui.guis.StorageGui;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 92\u00020\u0001:\u00019B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001b\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u00192\u0006\u0010\u001b\u001a\u00020\u001c¢\u0006\u0002\u0010\u001dJ\u001b\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u00192\u0006\u0010\u001e\u001a\u00020\u001f¢\u0006\u0002\u0010 J \u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ \u0010'\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\u0016\u0010(\u001a\u00020\"2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020$J\u0016\u0010(\u001a\u00020\"2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010#\u001a\u00020$J\u001c\u0010)\u001a\u00020\"2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010*\u001a\u0004\u0018\u00010\u0011H\u0002J\u000e\u0010+\u001a\u00020\"2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0016\u0010+\u001a\u00020\"2\u0006\u0010,\u001a\u00020-2\u0006\u0010\u001e\u001a\u00020\u001fJ\u001a\u00105\u001a\u0002062\u0006\u0010%\u001a\u00020&2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0002J\u001a\u00107\u001a\u0002062\u0006\u0010#\u001a\u00020$2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0002J\u0010\u00108\u001a\u0002062\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0010\u00108\u001a\u0002062\u0006\u0010\u001e\u001a\u00020\u001fH\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010.\u001a\u00020/8F¢\u0006\u0006\u001a\u0004\b.\u00100R\u0011\u00101\u001a\u00020/8F¢\u0006\u0006\u001a\u0004\b1\u00100R\u0011\u00102\u001a\u00020/8F¢\u0006\u0006\u001a\u0004\b2\u00100R\u0011\u00103\u001a\u00020/8F¢\u0006\u0006\u001a\u0004\b3\u00100R\u0011\u00104\u001a\u00020/8F¢\u0006\u0006\u001a\u0004\b4\u00100¨\u0006:"},
   d2 = {"Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "rows", "", "title", "Lnet/kyori/adventure/text/Component;", "getTitle", "()Lnet/kyori/adventure/text/Component;", "storageType", "Lcom/nexomc/nexo/mechanics/storage/StorageType;", "getStorageType", "()Lcom/nexomc/nexo/mechanics/storage/StorageType;", "openSound", "", "closeSound", "openAnimation", "closeAnimation", "volume", "", "pitch", "storageContent", "", "Lorg/bukkit/inventory/ItemStack;", "block", "Lorg/bukkit/block/Block;", "(Lorg/bukkit/block/Block;)[Lorg/bukkit/inventory/ItemStack;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "(Lorg/bukkit/entity/ItemDisplay;)[Lorg/bukkit/inventory/ItemStack;", "openPersonalStorage", "", "player", "Lorg/bukkit/entity/Player;", "location", "Lorg/bukkit/Location;", "openDisposal", "openStorage", "playOpenAnimation", "animation", "dropStorageContent", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "isStorage", "", "()Z", "isPersonal", "isEnderchest", "isDisposal", "isShulker", "createDisposalGui", "Lcom/nexomc/libs/gui/guis/StorageGui;", "createPersonalGui", "createGui", "Companion", "com.nexomc:core"}
)
public final class StorageMechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   private final int rows;
   @NotNull
   private final Component title;
   @NotNull
   private final StorageType storageType;
   @NotNull
   private final String openSound;
   @NotNull
   private final String closeSound;
   @Nullable
   private final String openAnimation;
   @Nullable
   private final String closeAnimation;
   private final float volume;
   private final float pitch;
   @NotNull
   private static ObjectOpenHashSet<Player> playerStorages = new ObjectOpenHashSet();
   @NotNull
   private static Object2ObjectOpenHashMap<Block, StorageGui> blockStorages = new Object2ObjectOpenHashMap();
   @NotNull
   private static Object2ObjectOpenHashMap<ItemDisplay, StorageGui> displayStorages = new Object2ObjectOpenHashMap();
   @NotNull
   private static final NamespacedKey STORAGE_KEY;
   @NotNull
   private static final NamespacedKey PERSONAL_STORAGE_KEY;

   public StorageMechanic(@NotNull ConfigurationSection section) {
      Component component;
      block: {
         /* implicit super() */
         this.rows = section.getInt("rows", 6);
         String string = section.getString("title", "Storage");
         if (string != null) {
            component = AdventureUtilsKt.deserialize(string);
            if (component != null) {
               break block;
            }
         }

         TextComponent textComponent = Component.empty();
         component = (Component)textComponent;
      }

      this.title = component;
      String str = section.getString("type", "STORAGE");
      this.storageType = StorageType.valueOf(str);
      str = section.getString("open_sound", "minecraft:block.chest.open");
      this.openSound = str;
      str = section.getString("close_sound", "minecraft:block.chest.close");
      this.closeSound = str;
      this.openAnimation = section.getString("open_animation", (String)null);
      this.closeAnimation = section.getString("close_animation", (String)null);
      this.volume = (float)section.getDouble("volume", (double)0.5F);
      this.pitch = (float)section.getDouble("pitch", 0.95);
   }

   @NotNull
   public final Component getTitle() {
      return this.title;
   }

   @NotNull
   public final StorageType getStorageType() {
      return this.storageType;
   }

   @NotNull
   public final ItemStack[] storageContent(@NotNull Block block) {
      StorageGui storageGui = blockStorages.get(block);
      ItemStack[] itemStacks;
      if (storageGui != null) {
         Inventory inventory = storageGui.getInventory();
         if (inventory != null) {
            itemStacks = inventory.getContents();
            if (itemStacks != null) {
               return itemStacks;
            }
         }
      }

      Object orDefault = BlockHelpers.INSTANCE.getPersistentDataContainer(block).getOrDefault(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
      itemStacks = (ItemStack[])orDefault;
      return itemStacks;
   }

   @NotNull
   public final ItemStack[] storageContent(@NotNull ItemDisplay baseEntity) {
      StorageGui storageGui = displayStorages.get(baseEntity);
      ItemStack[] itemStacks;
      if (storageGui != null) {
         Inventory inventory = storageGui.getInventory();
         if (inventory != null) {
            itemStacks = inventory.getContents();
            if (itemStacks != null) {
               return itemStacks;
            }
         }
      }

      Object orDefault = baseEntity.getPersistentDataContainer().getOrDefault(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
      itemStacks = (ItemStack[])orDefault;
      return itemStacks;
   }

   public final void openPersonalStorage(@NotNull Player player, @NotNull Location location, @Nullable ItemDisplay baseEntity) {
      if (this.storageType == StorageType.PERSONAL) {
         this.createPersonalGui(player, baseEntity).open((HumanEntity)player);
         if (baseEntity != null) {
            this.playOpenAnimation(baseEntity, this.openAnimation);
         }

         if (location.isWorldLoaded()) {
            location.getWorld().playSound(location, this.openSound, this.volume, this.pitch);
         }

      }
   }

   public final void openDisposal(@NotNull Player player, @NotNull Location location, @Nullable ItemDisplay baseEntity) {
      if (this.storageType == StorageType.DISPOSAL) {
         this.createDisposalGui(location, baseEntity).open((HumanEntity)player);
         if (baseEntity != null) {
            this.playOpenAnimation(baseEntity, this.openAnimation);
         }

         if (location.isWorldLoaded()) {
            location.getWorld().playSound(location, this.openSound, this.volume, this.pitch);
         }

      }
   }

   public final void openStorage(@NotNull Block block, @NotNull Player player) {
      if (block.getType() == Material.NOTE_BLOCK) {
         StorageGui storageGui = blockStorages.get(block);
         if (storageGui == null) {
            StorageGui storageGui2 = this.createGui(block);
            int value = 0;
            ((Map)blockStorages).put(block, storageGui2);
            storageGui = storageGui2;
         }

         storageGui.open((HumanEntity)player);
         if (block.getLocation().isWorldLoaded()) {
            block.getWorld().playSound(block.getLocation(), this.openSound, this.volume, this.pitch);
         }

      }
   }

   public final void openStorage(@NotNull ItemDisplay baseEntity, @NotNull Player player) {
      StorageGui storageGui = displayStorages.get(baseEntity);
      if (storageGui == null) {
         StorageGui storageGui2 = this.createGui(baseEntity);
         int value = 0;
         ((Map)displayStorages).put(baseEntity, storageGui2);
         storageGui = storageGui2;
      }

      storageGui.open((HumanEntity)player);
      this.playOpenAnimation(baseEntity, this.openAnimation);
      if (baseEntity.getLocation().isWorldLoaded()) {
         baseEntity.getWorld().playSound(baseEntity.getLocation(), this.openSound, this.volume, this.pitch);
      }

   }

   private final void playOpenAnimation(ItemDisplay baseEntity, String animation) {
      if (baseEntity != null && animation != null) {
         UUID uuid2 = baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getMODELENGINE_KEY(), DataType.UUID);
         if (uuid2 != null) {
            UUID uuid = uuid2;
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(uuid);
            if (modeledEntity != null) {
               Map models = modeledEntity.getModels();
               if (models != null) {
                  Collection collection = models.values();
                  if (collection != null) {
                     Iterable iterable = (Iterable)collection;

                     for(Object element : iterable) {
                        ActiveModel model = (ActiveModel)element;
                        int value = 0;
                        model.getAnimationHandler().forceStopAllAnimations();
                        model.getAnimationHandler().playAnimation(animation, (double)0.0F, (double)0.0F, (double)1.0F, true);
                     }
                  }
               }
            }

         }
      }
   }

   public final void dropStorageContent(@NotNull Block block) {
      StorageGui gui = blockStorages.get(block);
      PersistentDataContainer pdc = BlockHelpers.INSTANCE.getPersistentDataContainer(block);
      ItemStack[] items = ((Map)blockStorages).containsKey(block) && gui != null ? gui.getInventory().getContents() : pdc.getOrDefault(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
      Location toCenterLocation = block.getLocation().toCenterLocation();
      Location loc = toCenterLocation;
      if (this.isShulker()) {
         NoteBlockMechanic noteBlockMechanic = NexoBlocks.noteBlockMechanic(block);
         if (noteBlockMechanic == null) {
            return;
         }

         NoteBlockMechanic mechanic = noteBlockMechanic;
         ItemBuilder itemBuilder = NexoItems.itemFromId(mechanic.getItemID());
         if (itemBuilder == null) {
            return;
         }

         ItemStack itemStack = itemBuilder.build();
         if (itemStack == null) {
            return;
         }

         ItemStack shulker = itemStack;
         shulker.editPersistentDataContainer((a0_ -> StorageMechanic.dropStorageContent$lambda$0(items, a0_)));
         Item item = block.getWorld().dropItemNaturally(loc, shulker);
      } else {
         Iterable iterable = (Iterable)ArraysKt.filterNotNull(items);

         for(Object element : iterable) {
            ItemStack it = (ItemStack)element;
            int value = 0;
            block.getWorld().dropItemNaturally(loc, it);
         }
      }

      if (gui != null) {
         Inventory inventory = gui.getInventory();
         if (inventory != null) {
            List viewers = inventory.getViewers();
            if (viewers != null) {
               Iterable iterable2 = (Iterable)viewers;
               Collection destination = (Collection)(new ArrayList());

               for(Object obj : iterable2) {
                  if (obj instanceof Player) {
                     destination.add(obj);
                  }
               }

               iterable2 = (Iterable)((List)destination);
               StorageGui storageGui = gui;

               for(Object obj2 : iterable2) {
                  HumanEntity p0 = (HumanEntity)obj2;
                  int value2 = 0;
                  storageGui.close(p0);
               }
            }
         }
      }

      pdc.remove(STORAGE_KEY);
      blockStorages.remove(block);
   }

   public final void dropStorageContent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity) {
      StorageGui gui = displayStorages.get(baseEntity);
      PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
      PersistentDataContainer pdc = persistentDataContainer;
      ItemStack[] items = ((Map)displayStorages).containsKey(baseEntity) && gui != null ? gui.getInventory().getContents() : pdc.getOrDefault(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
      Location toCenterLocation = baseEntity.getLocation().toCenterLocation();
      Location loc = toCenterLocation;
      if (this.isShulker()) {
         ItemBuilder defaultItem = NexoItems.itemFromId(mechanic.getItemID());
         ItemStack itemStack = NexoFurniture.furnitureItem(baseEntity);
         if (itemStack == null) {
            return;
         }

         ItemStack shulker;
         block: {
            shulker = itemStack;
            shulker.editPersistentDataContainer((a0_ -> StorageMechanic.dropStorageContent$lambda$2(items, a0_)));
            if (defaultItem != null) {
               Component component = defaultItem.getCustomName();
               if (component != null) {
                  Component customName = component;
                  int value = 0;
                  shulker.setData(DataComponentTypes.CUSTOM_NAME, customName);
                  break block;
               }
            }

            shulker.unsetData((DataComponentType)DataComponentTypes.CUSTOM_NAME);
         }

         Item item = baseEntity.getWorld().dropItemNaturally(loc, shulker);
      } else {
         Iterable iterable = (Iterable)ArraysKt.filterNotNull(items);

         for(Object element : iterable) {
            ItemStack it = (ItemStack)element;
            int value2 = 0;
            baseEntity.getWorld().dropItemNaturally(loc, it);
         }
      }

      if (gui != null) {
         Inventory inventory = gui.getInventory();
         if (inventory != null) {
            List viewers = inventory.getViewers();
            if (viewers != null) {
               Collection collection = (Collection)viewers;
               HumanEntity[] humanEntities = (HumanEntity[])collection.toArray(new HumanEntity[0]);
               if (humanEntities != null) {
                  HumanEntity[] humanEntities2 = humanEntities;
                  StorageGui storageGui = gui;
                  int value3 = 0;

                  for(int i = humanEntities2.length; value3 < i; ++value3) {
                     Object obj = humanEntities2[value3];
                     int value4 = 0;
                     storageGui.close((HumanEntity)obj);
                  }
               }
            }
         }
      }

      pdc.remove(STORAGE_KEY);
      displayStorages.remove(baseEntity);
   }

   public final boolean isStorage() {
      return this.storageType == StorageType.STORAGE;
   }

   public final boolean isPersonal() {
      return this.storageType == StorageType.PERSONAL;
   }

   public final boolean isEnderchest() {
      return this.storageType == StorageType.ENDERCHEST;
   }

   public final boolean isDisposal() {
      return this.storageType == StorageType.DISPOSAL;
   }

   public final boolean isShulker() {
      return this.storageType == StorageType.SHULKER;
   }

   private final StorageGui createDisposalGui(Location location, ItemDisplay baseEntity) {
      StorageGui storageGui = (((Gui.storage().title(this.title)).rows(this.rows)).inventory(StorageMechanic::createDisposalGui$lambda$0)).create();
      StorageGui gui = storageGui;
      gui.setOpenGuiAction((a0_ -> StorageMechanic.createDisposalGui$lambda$1(gui, a0_)));
      gui.setCloseGuiAction((a0_ -> StorageMechanic.createDisposalGui$lambda$2(gui, location, this, baseEntity, a0_)));
      return gui;
   }

   private final StorageGui createPersonalGui(Player player, ItemDisplay baseEntity) {
      PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
      PersistentDataContainer storagePDC = persistentDataContainer;
      StorageGui storageGui = (((Gui.storage().title(this.title)).rows(this.rows)).inventory(StorageMechanic::createPersonalGui$lambda$0)).create();
      StorageGui gui = storageGui;
      gui.setDefaultClickAction((a0_ -> StorageMechanic.createPersonalGui$lambda$1(player, storagePDC, gui, a0_)));
      gui.setOpenGuiAction((a0_ -> StorageMechanic.createPersonalGui$lambda$2(player, storagePDC, gui, a0_)));
      gui.setCloseGuiAction((a0_ -> StorageMechanic.createPersonalGui$lambda$3(player, storagePDC, gui, this, baseEntity, a0_)));
      return gui;
   }

   private final StorageGui createGui(Block block) {
      Location location2 = block.getLocation();
      Location location = location2;
      PersistentDataContainer storagePDC = BlockHelpers.INSTANCE.getPersistentDataContainer(block);
      StorageGui storageGui = (((Gui.storage().title(this.title)).rows(this.rows)).inventory(StorageMechanic::createGui$lambda$0)).create();
      StorageGui gui = storageGui;
      gui.setDefaultClickAction((a0_ -> StorageMechanic.createGui$lambda$1(block, storagePDC, gui, a0_)));
      gui.setOpenGuiAction((a0_ -> StorageMechanic.createGui$lambda$2(storagePDC, gui, a0_)));
      gui.setCloseGuiAction((a0_ -> StorageMechanic.createGui$lambda$3(storagePDC, gui, block, location, this, a0_)));
      return gui;
   }

   private final StorageGui createGui(ItemDisplay baseEntity) {
      Location location2 = baseEntity.getLocation();
      Location location = location2;
      PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
      PersistentDataContainer storagePDC = persistentDataContainer;
      StorageGui storageGui = (((Gui.storage().title(this.title)).rows(this.rows)).inventory(StorageMechanic::createGui$lambda$4)).create();
      StorageGui gui = storageGui;
      gui.setDefaultClickAction((a0_ -> StorageMechanic.createGui$lambda$5(baseEntity, storagePDC, gui, a0_)));
      gui.setOpenGuiAction((a0_ -> StorageMechanic.createGui$lambda$6(gui, storagePDC, a0_)));
      gui.setCloseGuiAction((a0_ -> StorageMechanic.createGui$lambda$7(gui, storagePDC, baseEntity, location, this, a0_)));
      return gui;
   }

   private static final void dropStorageContent$lambda$0(ItemStack[] items, PersistentDataContainer it) {
      it.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, items);
   }

   private static final void dropStorageContent$lambda$2(ItemStack[] items, PersistentDataContainer it) {
      it.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, items);
   }

   private static final Inventory createDisposalGui$lambda$0(Component t, InventoryHolder o, int r) {
      return Bukkit.createInventory(o, r, t);
   }

   private static final void createDisposalGui$lambda$1(StorageGui gui, InventoryOpenEvent it) {
      gui.getInventory().clear();
   }

   private static final void createDisposalGui$lambda$2(StorageGui gui, Location location, StorageMechanic storageMechanic, ItemDisplay baseEntity, InventoryCloseEvent it) {
      gui.getInventory().clear();
      if (location.isWorldLoaded()) {
         location.getWorld().playSound(location, storageMechanic.closeSound, storageMechanic.volume, storageMechanic.pitch);
      }

      if (baseEntity != null) {
         storageMechanic.playOpenAnimation(baseEntity, storageMechanic.closeAnimation);
      }

   }

   private static final Inventory createPersonalGui$lambda$0(Component t, InventoryHolder o, int r) {
      return Bukkit.createInventory(o, r, t);
   }

   private static final Unit createPersonalGui$lambda$1$0(PersistentDataContainer storagePDC, StorageGui gui, Entity it) {
      storagePDC.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      return Unit.INSTANCE;
   }

   private static final void createPersonalGui$lambda$1(Player player, PersistentDataContainer storagePDC, StorageGui gui, InventoryClickEvent event) {
      if (event.getCursor().getType() != Material.AIR || event.getCurrentItem() != null) {
         SchedulerUtils.INSTANCE.runAtEntityLater((Entity)player, 3L, (a0_ -> StorageMechanic.createPersonalGui$lambda$1$0(storagePDC, gui, a0_)));
      }

   }

   private static final void createPersonalGui$lambda$2(Player player, PersistentDataContainer storagePDC, StorageGui gui, InventoryOpenEvent it) {
      playerStorages.add(player);
      if (storagePDC.has(PERSONAL_STORAGE_KEY, DataType.ITEM_STACK_ARRAY)) {
         gui.getInventory().setContents(Objects.requireNonNull(storagePDC.get(PERSONAL_STORAGE_KEY, DataType.ITEM_STACK_ARRAY)));
      }

   }

   private static final void createPersonalGui$lambda$3(Player player, PersistentDataContainer storagePDC, StorageGui gui, StorageMechanic storageMechanic, ItemDisplay baseEntity, InventoryCloseEvent it) {
      playerStorages.remove(player);
      storagePDC.set(PERSONAL_STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      if (player.getLocation().isWorldLoaded()) {
         player.getLocation().getWorld().playSound(player.getLocation(), storageMechanic.closeSound, storageMechanic.volume, storageMechanic.pitch);
      }

      if (baseEntity != null) {
         storageMechanic.playOpenAnimation(baseEntity, storageMechanic.closeAnimation);
      }

   }

   private static final Inventory createGui$lambda$0(Component t, InventoryHolder o, int r) {
      return Bukkit.createInventory(o, r, t);
   }

   private static final Unit createGui$lambda$1$0(PersistentDataContainer storagePDC, StorageGui gui, Location it) {
      storagePDC.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      return Unit.INSTANCE;
   }

   private static final void createGui$lambda$1(Block block, PersistentDataContainer storagePDC, StorageGui gui, InventoryClickEvent event) {
      if (event.getCursor().isEmpty()) {
         ItemStack currentItem = event.getCurrentItem();
         if (!(currentItem != null ? !currentItem.isEmpty() : false)) {
            return;
         }
      }

      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Location location = block.getLocation();
      schedulerUtils.runAtLocationLater(location, 3L, (a0_ -> StorageMechanic.createGui$lambda$1$0(storagePDC, gui, a0_)));
   }

   private static final void createGui$lambda$2(PersistentDataContainer storagePDC, StorageGui gui, InventoryOpenEvent it) {
      if (storagePDC.has(STORAGE_KEY, DataType.ITEM_STACK_ARRAY)) {
         Inventory inventory = gui.getInventory();
         ItemStack[] itemStacks = storagePDC.get(STORAGE_KEY, DataType.ITEM_STACK_ARRAY);
         if (itemStacks == null) {
            itemStacks = new ItemStack[0];
         }

         inventory.setContents(itemStacks);
      }

   }

   private static final void createGui$lambda$3(PersistentDataContainer storagePDC, StorageGui gui, Block block, Location location2, StorageMechanic storageMechanic, InventoryCloseEvent it) {
      storagePDC.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
      Location location = block.getLocation();
      if (blockHelpers.isLoaded(location)) {
         location2.getWorld().playSound(location2, storageMechanic.closeSound, storageMechanic.volume, storageMechanic.pitch);
      }

   }

   private static final Inventory createGui$lambda$4(Component t, InventoryHolder o, int r) {
      return Bukkit.createInventory(o, r, t);
   }

   private static final Unit createGui$lambda$5$0(PersistentDataContainer storagePDC, StorageGui gui, Entity it) {
      storagePDC.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      return Unit.INSTANCE;
   }

   private static final void createGui$lambda$5(ItemDisplay baseEntity, PersistentDataContainer storagePDC, StorageGui gui, InventoryClickEvent event) {
      if (event.getCursor().getType() != Material.AIR || event.getCurrentItem() != null) {
         SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, 3L, (a0_ -> StorageMechanic.createGui$lambda$5$0(storagePDC, gui, a0_)));
      }

   }

   private static final void createGui$lambda$6(StorageGui gui, PersistentDataContainer storagePDC, InventoryOpenEvent it) {
      Inventory inventory = gui.getInventory();
      ItemStack[] itemStacks = storagePDC.get(STORAGE_KEY, DataType.ITEM_STACK_ARRAY);
      if (itemStacks == null) {
         itemStacks = new ItemStack[0];
      }

      inventory.setContents(itemStacks);
   }

   private static final void createGui$lambda$7(StorageGui gui, PersistentDataContainer storagePDC, ItemDisplay baseEntity, Location location2, StorageMechanic storageMechanic, InventoryCloseEvent it) {
      if (gui.getInventory().getViewers().size() <= 1) {
         storagePDC.set(STORAGE_KEY, DataType.ITEM_STACK_ARRAY, gui.getInventory().getContents());
      }

      BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
      Location location = baseEntity.getLocation();
      if (blockHelpers.isLoaded(location)) {
         location2.getWorld().playSound(location2, storageMechanic.closeSound, storageMechanic.volume, storageMechanic.pitch);
      }

      storageMechanic.playOpenAnimation(baseEntity, storageMechanic.closeAnimation);
   }

   static {
      STORAGE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "storage");
      PERSONAL_STORAGE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "personal_storage");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R \u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR&\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R&\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u000e0\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u0011\u0010\u0017\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u001b\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001a¨\u0006\u001d"},
      d2 = {"Lcom/nexomc/nexo/mechanics/storage/StorageMechanic$Companion;", "", "<init>", "()V", "playerStorages", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lorg/bukkit/entity/Player;", "getPlayerStorages", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "setPlayerStorages", "(Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;)V", "blockStorages", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lorg/bukkit/block/Block;", "Lcom/nexomc/libs/gui/guis/StorageGui;", "getBlockStorages", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "setBlockStorages", "(Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;)V", "displayStorages", "Lorg/bukkit/entity/ItemDisplay;", "getDisplayStorages", "setDisplayStorages", "STORAGE_KEY", "Lorg/bukkit/NamespacedKey;", "getSTORAGE_KEY", "()Lorg/bukkit/NamespacedKey;", "PERSONAL_STORAGE_KEY", "getPERSONAL_STORAGE_KEY", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ObjectOpenHashSet<Player> getPlayerStorages() {
         return StorageMechanic.playerStorages;
      }

      public final void setPlayerStorages(@NotNull ObjectOpenHashSet<Player> objectOpenHashSet) {
         StorageMechanic.playerStorages = objectOpenHashSet;
      }

      @NotNull
      public final Object2ObjectOpenHashMap<Block, StorageGui> getBlockStorages() {
         return StorageMechanic.blockStorages;
      }

      public final void setBlockStorages(@NotNull Object2ObjectOpenHashMap<Block, StorageGui> object2ObjectOpenHashMap) {
         StorageMechanic.blockStorages = object2ObjectOpenHashMap;
      }

      @NotNull
      public final Object2ObjectOpenHashMap<ItemDisplay, StorageGui> getDisplayStorages() {
         return StorageMechanic.displayStorages;
      }

      public final void setDisplayStorages(@NotNull Object2ObjectOpenHashMap<ItemDisplay, StorageGui> object2ObjectOpenHashMap) {
         StorageMechanic.displayStorages = object2ObjectOpenHashMap;
      }

      @NotNull
      public final NamespacedKey getSTORAGE_KEY() {
         return StorageMechanic.STORAGE_KEY;
      }

      @NotNull
      public final NamespacedKey getPERSONAL_STORAGE_KEY() {
         return StorageMechanic.PERSONAL_STORAGE_KEY;
      }
   }
}
