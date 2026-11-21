package com.nexomc.nexo.api;
import com.nexomc.nexo.api.NexoItems;
import java.util.Comparator;
import kotlin.comparisons.ComparisonsKt;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.converter.Converter;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.mechanics.light.LightMechanic;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.FurnitureHelpers;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Color;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007b\u0002\b\b¢\u0006\u0002\u0010\u0007J\u0014\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007b\u0002\b\bJ\u0016\u0010\t\u001a\u00020\n2\b\u0010\r\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0016\u0010\t\u001a\u00020\n2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007b\u0002\b\bJ\u0016\u0010\t\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0007b\u0002\b\bJ\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0007b\u0002\b\bJ\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0007b\u0002\b\bJ\u0016\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0016\u001a\u00020\u0017H\u0007b\u0002\b\bJ0\u0010\u0018\u001a\u0004\u0018\u00010\u00132\b\u0010\r\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0007b\u0002\b\bJ0\u0010\u0018\u001a\u0004\u0018\u00010\u00132\b\u0010\r\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001b\u001a\u00020\u001cH\u0007b\u0002\b\bJ0\u0010\u001f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#H\u0007b\u0002\b$b\u0002\b\bJ0\u0010\u001f\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u00112\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#H\u0007b\u0002\b$b\u0002\b\bJ\u0018\u0010%\u001a\u0004\u0018\u00010&2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0007b\u0002\b\bJ\u0018\u0010%\u001a\u0004\u0018\u00010&2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0007b\u0002\b\bJ\u0018\u0010%\u001a\u0004\u0018\u00010&2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0007b\u0002\b\bJ\u0018\u0010%\u001a\u0004\u0018\u00010&2\b\u0010\r\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b\bJ\u0018\u0010%\u001a\u0004\u0018\u00010&2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007b\u0002\b\bJ\u0016\u0010'\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007b\u0002\b\bJ\u001c\u0010'\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000e\u001a\u00020\u000fH\u0007b\u0002\b\bJ\u001b\u0010)\u001a\t\u0018\u00010*¢\u0006\u0002\b+2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007b\u0002\b\bJ\u001e\u0010)\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010,\u001a\u0004\u0018\u00010*H\u0007b\u0002\b\bJ\u0014\u0010-\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007b\u0002\b\bJ5\u0010.\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\n2\n\b\u0002\u00100\u001a\u0004\u0018\u00010&H\u0007b\u0002\b$b\u0002\b\b¢\u0006\u0002\u00101J\u0014\u00102\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007b\u0002\b\bJ\u0014\u00103\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007b\u0002\b\bJ\u0016\u00104\u001a\u0004\u0018\u00010\u00132\u0006\u0010 \u001a\u00020!H\u0007b\u0002\b\b¨\u00065"},
   d2 = {"Lcom/nexomc/nexo/api/NexoFurniture;", "", "<init>", "()V", "furnitureIDs", "", "", "()[Ljava/lang/String;", "Lkotlin/jvm/JvmStatic;", "isFurniture", "", "location", "Lorg/bukkit/Location;", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "entity", "Lorg/bukkit/entity/Entity;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "block", "Lorg/bukkit/block/Block;", "interactionId", "", "place", "rotation", "Lorg/bukkit/Rotation;", "blockFace", "Lorg/bukkit/block/BlockFace;", "yaw", "", "remove", "player", "Lorg/bukkit/entity/Player;", "drop", "Lcom/nexomc/nexo/utils/drops/Drop;", "Lkotlin/jvm/JvmOverloads;", "furnitureMechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "furnitureItem", "", "furnitureDye", "Lorg/bukkit/Color;", "Lorg/jetbrains/annotations/NotNull;", "dyeColor", "lightState", "toggleLight", "state", "mechanic", "(Lorg/bukkit/entity/ItemDisplay;Ljava/lang/Boolean;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)Z", "updateFurniture", "convertFurniture", "findTargetFurniture", "com.nexomc:core"}
)
public final class NexoFurniture {
   @NotNull
   public static final NexoFurniture INSTANCE = new NexoFurniture();

   private NexoFurniture() {
   }

   @JvmStatic
   @NotNull
   public static final String[] furnitureIDs() {
      Iterable itemNames = (Iterable)NexoItems.itemNames();
      NexoFurniture nexoFurniture = INSTANCE;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : itemNames) {
         String p0 = (String)element;
         int value = 0;
         if (isFurniture(p0)) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   /** Returns true if furniture is at the location. If no mechanic is found through the hitbox, ItemDisplay entities near the location are also checked. */
   @JvmStatic
   public static final boolean isFurniture(@NotNull Location location) {
      boolean flag;
      if (furnitureMechanic(location) == null) {
         Collection nearbyEntitiesByType = location.getWorld().getNearbyEntitiesByType(ItemDisplay.class, location.toCenterLocation(), (double)0.5F);
         Iterable iterable = (Iterable)nearbyEntitiesByType;
         NexoFurniture nexoFurniture = INSTANCE;
         boolean flag2;
         if (((Collection)iterable).isEmpty()) {
            flag2 = false;
         } else {
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag2 = false;
                  break;
               }

               Object element = iterator.next();
               Entity p0 = (Entity)element;
               int value = 0;
               if (isFurniture(p0)) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (!flag2) {
            flag = false;
            return flag;
         }
      }

      flag = true;
      return flag;
   }

   @JvmStatic
   public static final boolean isFurniture(@Nullable String itemID) {
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

      return (FurnitureFactory)mechanicFactory != null ? !((FurnitureFactory)mechanicFactory).isNotImplementedIn(itemID) : false;
   }

   @JvmStatic
   public static final boolean isFurniture(@Nullable ItemStack itemStack) {
      return isFurniture(NexoItems.idFromItem(itemStack));
   }

   @JvmStatic
   public static final boolean isFurniture(@Nullable Entity entity) {
      return (entity != null ? entity.getType() : null) == EntityType.ITEM_DISPLAY && furnitureMechanic(entity) != null;
   }

   @JvmStatic
   @Nullable
   public static final ItemDisplay baseEntity(@Nullable Block block) {
      return FurnitureMechanic.Companion.baseEntity(block);
   }

   @JvmStatic
   @Nullable
   public static final ItemDisplay baseEntity(@Nullable Location location) {
      return FurnitureMechanic.Companion.baseEntity(location);
   }

   @JvmStatic
   @Nullable
   public static final ItemDisplay baseEntity(int interactionId) {
      return FurnitureMechanic.Companion.baseEntity(interactionId);
   }

   /** Places furniture facing the given rotation. See {@link #place(String, Location, float, BlockFace)}. */
   @JvmStatic
   @Nullable
   public static final ItemDisplay place(@Nullable String itemID, @NotNull Location location, @NotNull Rotation rotation, @NotNull BlockFace blockFace) {
      return place(itemID, location, FurnitureHelpers.rotationToYaw(rotation), blockFace);
   }

   /** Places the furniture with this item id and returns its base ItemDisplay, or null if the id is not a furniture item. */
   @JvmStatic
   @Nullable
   public static final ItemDisplay place(@Nullable String itemID, @NotNull Location location, float yaw, @NotNull BlockFace blockFace) {
      FurnitureMechanic furnitureMechanic2 = furnitureMechanic(itemID);
      if (furnitureMechanic2 == null) {
         return null;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic2;
         return mechanic.place(location, yaw, blockFace);
      }
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location, @Nullable Player player, @Nullable Drop drop) {
      org.bukkit.inventory.ItemStack itemStack2;
      if (FurnitureFactory.Companion.isEnabled() && BlockHelpers.INSTANCE.isLoaded(location)) {
         FurnitureMechanic furnitureMechanic2 = furnitureMechanic(location);
         if (furnitureMechanic2 == null) {
            Collection nearbyEntitiesByType = location.getWorld().getNearbyEntitiesByType(ItemDisplay.class, location, (double)0.5F);
            Iterable iterable = (Iterable)nearbyEntitiesByType;
            NexoFurniture nexoFurniture = INSTANCE;
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  furnitureMechanic2 = null;
                  break;
               }

               Entity p0 = (Entity)iterator.next();
               int value = 0;
               FurnitureMechanic it = furnitureMechanic(p0);
               if (it != null) {
                  furnitureMechanic2 = it;
                  break;
               }
            }

            if (furnitureMechanic2 == null) {
               return false;
            }
         }

         FurnitureMechanic mechanic;
         block: {
            mechanic = furnitureMechanic2;
            if (player != null) {
               PlayerInventory inventory = player.getInventory();
               if (inventory != null) {
                  itemStack2 = inventory.getItemInMainHand();
                  if (itemStack2 != null) {
                     break block;
                  }
               }
            }

            itemStack2 = ItemStack.of(Material.AIR);
         }

         ItemStack itemStack = itemStack2;
         ItemDisplay itemDisplay = FurnitureMechanic.Companion.baseEntity(location);
         if (itemDisplay == null) {
            return false;
         } else {
            ItemDisplay baseEntity = itemDisplay;
            if (player != null) {
               Drop drop2 = drop;
               if (drop == null) {
                  drop2 = mechanic.getBreakable().getDrop();
               }

               Drop drop3 = drop2;
               int value2 = 0;
               StorageMechanic storage = mechanic.getStorage();
               Drop drop_7 = !(storage != null ? storage.isShulker() : false) ? drop3 : null;
               if (player.getGameMode() != GameMode.CREATIVE) {
                  if (drop_7 != null) {
                     drop_7.furnitureSpawns(baseEntity, itemStack);
                  }
               }

               storage = mechanic.getStorage();
               if (storage != null) {
                  StorageMechanic it = storage;
                  int value3 = 0;
                  storage = it.isStorage() || it.isShulker() ? it : null;
                  if (storage != null) {
                     storage.dropStorageContent(mechanic, baseEntity);
                  }
               }

               baseEntity.getWorld().sendGameEvent((Entity)player, GameEvent.BLOCK_DESTROY, baseEntity.getLocation().toVector());
            }

            mechanic.removeBaseEntity(baseEntity);
            return true;
         }
      } else {
         return false;
      }
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Entity baseEntity, @Nullable Player player2, @Nullable Drop drop) {
        Entity entity;
        Intrinsics.checkNotNullParameter((Object)baseEntity, "baseEntity");
        Entity it = entity = baseEntity;
        boolean bl = false;
        Location location = it.getLocation();
        Intrinsics.checkNotNullExpressionValue((Object)location, "getLocation(...)");
        Entity entity2 = BlockHelpers.INSTANCE.isLoaded(location) ? entity : null;
        ItemDisplay itemDisplay = entity2 instanceof ItemDisplay ? (ItemDisplay)entity2 : null;
        if (itemDisplay == null) {
            return false;
        }
        ItemDisplay baseEntity2 = itemDisplay;
        FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity2);
        if (furnitureMechanic == null && (furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(baseEntity2)) == null) {
            return false;
        }
        FurnitureMechanic mechanic = furnitureMechanic;
        if (player2 != null) {
            StorageMechanic storageMechanic;
            Drop drop2;
            Drop drop3;
            ItemStack itemStack = player2.getInventory().getItemInMainHand();
            Intrinsics.checkNotNullExpressionValue((Object)itemStack, "getItemInMainHand(...)");
            ItemStack itemStack2 = itemStack;
            Drop drop4 = drop;
            if (drop4 == null) {
                drop4 = mechanic.getBreakable().getDrop();
            }
            Drop it2 = drop3 = drop4;
            boolean bl2 = false;
            StorageMechanic storageMechanic2 = mechanic.getStorage();
            Drop drop5 = drop2 = !(storageMechanic2 != null ? storageMechanic2.isShulker() : false) ? drop3 : null;
            if (player2.getGameMode() != GameMode.CREATIVE) {
                Drop drop6 = drop2;
                if (drop6 != null) {
                    drop6.furnitureSpawns(baseEntity2, itemStack2);
                }
            }
            if ((storageMechanic = mechanic.getStorage()) != null) {
                StorageMechanic storageMechanic3;
                StorageMechanic it3 = storageMechanic3 = storageMechanic;
                boolean bl3 = false;
                storageMechanic = it3.isStorage() || it3.isShulker() ? storageMechanic3 : null;
                if (storageMechanic != null) {
                    storageMechanic.dropStorageContent(mechanic, baseEntity2);
                }
            }
            baseEntity2.getWorld().sendGameEvent((Entity)player2, GameEvent.BLOCK_DESTROY, baseEntity2.getLocation().toVector());
        }
        mechanic.removeBaseEntity(baseEntity2);
        return true;
    }

   @JvmStatic
   @Nullable
   public static final FurnitureMechanic furnitureMechanic(@Nullable Block block) {
      return furnitureMechanic(block != null ? block.getLocation() : null);
   }

   /** Finds the furniture mechanic at the location through its hitbox. Returns null if furniture is disabled or nothing is found. */
   @JvmStatic
   @Nullable
   public static final FurnitureMechanic furnitureMechanic(@Nullable Location location) {
      if (FurnitureFactory.Companion.isEnabled() && location != null) {
         ItemDisplay itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(location);
         FurnitureMechanic furnitureMechanic2;
         if (itemDisplay != null) {
            ItemDisplay itemDisplay2 = itemDisplay;
            NexoFurniture nexoFurniture = INSTANCE;
            Entity p0 = (Entity)itemDisplay2;
            int value = 0;
            furnitureMechanic2 = furnitureMechanic(p0);
            if (furnitureMechanic2 != null) {
               return furnitureMechanic2;
            }
         }

         NexoFurniture it = INSTANCE;
         int value2 = 0;
         Block block2 = location.getBlock();
         Block block = block2;
         Location centerLoc = BlockHelpers.toCenterBlockLocation(location);
         Collection nearbyEntitiesByType = centerLoc.getWorld().getNearbyEntitiesByType(ItemDisplay.class, centerLoc, (double)2.0F);
         Iterable iterable = (Iterable)nearbyEntitiesByType;
         iterable = (Iterable)CollectionsKt.sortedWith(iterable, new NexoFurniture$furnitureMechanic$lambda$0$$inlined$sortedBy$1(centerLoc));
         Iterator iterator = iterable.iterator();

         ItemDisplay itemDisplay3;
         while(true) {
            if (!iterator.hasNext()) {
               itemDisplay3 = null;
               break;
            }

            Object element = iterator.next();
            ItemDisplay it_2 = (ItemDisplay)element;
            int value3 = 0;
            if (IFurniturePacketManager.Companion.blockIsHitbox(block, (UUID)null, false)) {
               itemDisplay3 = (ItemDisplay)element;
               break;
            }
         }

         ItemDisplay itemDisplay4 = itemDisplay3;
         if (itemDisplay4 != null) {
            NexoFurniture nexoFurniture2 = INSTANCE;
            Entity p0 = (Entity)itemDisplay4;
            int value4 = 0;
            furnitureMechanic2 = furnitureMechanic(p0);
         } else {
            furnitureMechanic2 = null;
         }

         return furnitureMechanic2;
      } else {
         return null;
      }
   }

   @JvmStatic
   @Nullable
   public static final FurnitureMechanic furnitureMechanic(@Nullable Entity baseEntity) {
      if (FurnitureFactory.Companion.isEnabled() && baseEntity != null && baseEntity.getType() == EntityType.ITEM_DISPLAY && (baseEntity.isValid() || baseEntity.isInWorld())) {
         FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
         if (furnitureBaseEntity != null) {
            FurnitureMechanic it = furnitureBaseEntity.getMechanic$com_nexomc_core();
            if (it != null) {
               return it;
            }
         }

         String itemID = baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING);
         if (NexoItems.exists(itemID) && !FurnitureSeat.Companion.isSeat(baseEntity) && !FurnitureBed.Companion.isBed(baseEntity)) {
            if (baseEntity instanceof Interaction) {
               return null;
            } else {
               MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
               Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

               MechanicFactory mechanicFactory;
               while(true) {
                  if (iterator.hasNext()) {
                     MechanicFactory mechanicFactory2 = (MechanicFactory)iterator.next();
                     int value = 0;
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

               mechanicFactory = mechanicFactory;
               if (mechanicFactory != null) {
                  FurnitureMechanic furnitureMechanic = (FurnitureMechanic)(mechanicFactory.getMechanic(itemID));
                  if (furnitureMechanic != null) {
                     FurnitureMechanic mechanic = furnitureMechanic;
                     return mechanic.placementMechanic((ItemDisplay)baseEntity);
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @JvmStatic
   @Nullable
   public static final FurnitureMechanic furnitureMechanic(@Nullable String itemID) {
      if (FurnitureFactory.Companion.isEnabled() && NexoItems.exists(itemID)) {
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

         return (FurnitureFactory)mechanicFactory != null ? ((FurnitureFactory)mechanicFactory).getMechanic(itemID) : null;
      } else {
         return null;
      }
   }

   @JvmStatic
   @Nullable
   public static final FurnitureMechanic furnitureMechanic(@Nullable ItemStack itemStack) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (!FurnitureFactory.Companion.isEnabled()) {
         return null;
      } else {
         String str = NexoItems.idFromItem(itemStack);
         if (str == null) {
            return null;
         } else {
            String itemId = str;
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            while(true) {
               if (iterator.hasNext()) {
                  MechanicFactory it = (MechanicFactory)iterator.next();
                  int value = 0;
                  MechanicFactory mechanicFactory2 = it;
                  if (!(it instanceof FurnitureFactory)) {
                     mechanicFactory2 = null;
                  }

                  it = (FurnitureFactory)mechanicFactory2;
                  if (it == null) {
                     continue;
                  }

                  mechanicFactory = it;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            return (FurnitureFactory)mechanicFactory != null ? ((FurnitureFactory)mechanicFactory).getMechanic(itemId) : null;
         }
      }
   }

   @JvmStatic
   @Nullable
   public static final ItemStack furnitureItem(@NotNull ItemDisplay baseEntity) {
      FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      return furnitureBaseEntity != null ? furnitureBaseEntity.getItemStack() : null;
   }

   @JvmStatic
   public static final void furnitureItem(@NotNull ItemDisplay baseEntity, @NotNull ItemStack itemStack) {
      FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.itemStack(itemStack, baseEntity);
      }

   }

   @JvmStatic
   @Nullable
   public static final Color furnitureDye(@NotNull ItemDisplay baseEntity) {
      Integer number = baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY(), PersistentDataType.INTEGER);
      Color color;
      if (number != null) {
         int p0 = ((Number)number).intValue();
         int value = 0;
         color = Color.fromRGB(p0);
      } else {
         color = null;
      }

      return color;
   }

   @JvmStatic
   public static final void furnitureDye(@NotNull ItemDisplay baseEntity, @Nullable Color dyeColor) {
      if (dyeColor == null) {
         baseEntity.getPersistentDataContainer().remove(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY());
      } else {
         baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY(), PersistentDataType.INTEGER, dyeColor.asRGB());
      }

      FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.refreshItem(baseEntity);
      }

   }

   @JvmStatic
   public static final boolean lightState(@NotNull ItemDisplay baseEntity) {
      boolean flag;
      block: {
         FurnitureMechanic furnitureMechanic2 = furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic2 != null) {
            LightMechanic light = furnitureMechanic2.getLight();
            if (light != null) {
               flag = !light.isEmpty();
               break block;
            }
         }

         flag = false;
      }

      if (!flag) {
         return false;
      } else {
         Object orDefault = baseEntity.getPersistentDataContainer().getOrDefault(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, true);
         return (Boolean)orDefault;
      }
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity, @Nullable Boolean state, @Nullable FurnitureMechanic mechanic) {
      if (mechanic != null) {
         int value = 0;
         FurnitureMechanic furnitureMechanic = !mechanic.getLight().isEmpty() ? mechanic : null;
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic_1 = furnitureMechanic;
            boolean newState = state != null ? state : !baseEntity.getPersistentDataContainer().getOrDefault(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, true);
            if (!mechanic_1.getLight().getToggleable()) {
               return true;
            }

            baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, state != null ? state : newState);
            FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
            if (furnitureBaseEntity != null) {
               furnitureBaseEntity.refreshItem(baseEntity);
            }

            mechanic_1.getLight().refreshLights(baseEntity, mechanic_1);
            return newState;
         }
      }

      return false;
   }

   @JvmStatic
   public static final void updateFurniture(@NotNull ItemDisplay baseEntity) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (FurnitureFactory.Companion.isEnabled()) {
         BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
         Location location = baseEntity.getLocation();
         if (blockHelpers.isLoaded(location)) {
            convertFurniture(baseEntity);
            FurnitureMechanic furnitureMechanic2 = furnitureMechanic((Entity)baseEntity);
            if (furnitureMechanic2 != null) {
               FurnitureMechanic furnitureMechanic3 = furnitureMechanic2;
               int value = 0;
               furnitureMechanic2 = !FurnitureSeat.Companion.isSeat((Entity)baseEntity) && !FurnitureBed.Companion.isBed((Entity)baseEntity) ? furnitureMechanic3 : null;
               if (furnitureMechanic2 != null) {
                  FurnitureMechanic mechanic = furnitureMechanic2;
                  FurnitureSeat.Companion.updateSeats(baseEntity, mechanic);
                  FurnitureBed.Companion.updateBeds(baseEntity, mechanic);
                  if (mechanic.isModelEngine()) {
                     mechanic.spawnModelEngineFurniture(baseEntity);
                  }

                  MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
                  Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

                  while(true) {
                     if (iterator.hasNext()) {
                        MechanicFactory it = (MechanicFactory)iterator.next();
                        int value2 = 0;
                        MechanicFactory mechanicFactory2 = it;
                        if (!(it instanceof FurnitureFactory)) {
                           mechanicFactory2 = null;
                        }

                        it = (FurnitureFactory)mechanicFactory2;
                        if (it == null) {
                           continue;
                        }

                        mechanicFactory = it;
                        break;
                     }

                     mechanicFactory = null;
                     break;
                  }

                  FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
                  if (furnitureFactory != null) {
                     IFurniturePacketManager iFurniturePacketManager = furnitureFactory.packetManager();
                     if (iFurniturePacketManager != null) {
                        IFurniturePacketManager packetManager = iFurniturePacketManager;
                        IFurniturePacketManager.Companion.getFurnitureBaseMap().remove(baseEntity.getUniqueId());
                        packetManager.removeLightMechanicPacket(baseEntity, mechanic);
                        packetManager.removeHitboxEntityPacket(baseEntity, mechanic);
                        packetManager.removeBarrierHitboxPacket(baseEntity, mechanic);
                        packetManager.removeTextEntityMechanicPacket(baseEntity, mechanic);
                        SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity((Entity)baseEntity, (a0_ -> NexoFurniture.updateFurniture$lambda$1(packetManager, baseEntity, mechanic, a0_)));
                        return;
                     }
                  }

                  return;
               }
            }

            return;
         }
      }

   }

   @JvmStatic
   public static final void convertFurniture(@NotNull ItemDisplay baseEntity) {
      String str = baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING);
      if (str != null) {
         String furnitureId = str;
         Converter.NexoConverter converter = NexoPlugin.Companion.instance().converter().getNexoConverter();
         if (converter.getFurnitureConverter().containsKey(furnitureId)) {
            str = converter.getFurnitureConverter().get(furnitureId);
            if (str != null) {
               String newId = str;
               if (((CharSequence)newId).length() == 0 && baseEntity.isValid()) {
                  baseEntity.remove();
               } else {
                  baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING, newId);
               }

            }
         }
      }
   }

   @JvmStatic
   @Nullable
   public static final ItemDisplay findTargetFurniture(@NotNull Player player) {
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
      ItemDisplay itemDisplay;
      if (mechanicFactory != null) {
         IFurniturePacketManager packetManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (packetManager != null) {
            itemDisplay = packetManager.findTargetFurnitureHitbox(player);
            return itemDisplay;
         }
      }

      itemDisplay = null;
      return itemDisplay;
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location, @Nullable Player player) {
      return remove(location, player, (Drop)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Location location) {
      return remove(location, (Player)null, (Drop)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Entity baseEntity, @Nullable Player player) {
      return remove(baseEntity, player, (Drop)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean remove(@NotNull Entity baseEntity) {
      return remove(baseEntity, (Player)null, (Drop)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity, @Nullable Boolean state) {
      return toggleLight(baseEntity, state, (FurnitureMechanic)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity) {
      return toggleLight(baseEntity, (Boolean)null, (FurnitureMechanic)null);
   }

   private static final void updateFurniture$lambda$1(IFurniturePacketManager packetManager, ItemDisplay baseEntity, FurnitureMechanic mechanic, WrappedTask it) {
      packetManager.sendFurnitureMetadataPacket(baseEntity, mechanic);
      packetManager.sendHitboxEntityPacket(baseEntity, mechanic);
      packetManager.sendBarrierHitboxPacket(baseEntity, mechanic);
      packetManager.sendLightMechanicPacket(baseEntity, mechanic);
      packetManager.sendTextEntityMechanicPacket(baseEntity, mechanic);
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static boolean remove$default(org.bukkit.entity.Entity entity, org.bukkit.entity.Player player, com.nexomc.nexo.utils.drops.Drop drop, int mask, Object marker) {
      if ((mask & 2) != 0) player = null;
      if ((mask & 4) != 0) drop = null;
      return remove(entity, player, drop);
   }
}
