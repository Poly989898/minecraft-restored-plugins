package com.nexomc.nexo.converter;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/converter/ItemsAdderConverterListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onLoadFurniture", "", "Lorg/bukkit/event/world/EntitiesLoadEvent;", "Lorg/bukkit/event/EventHandler;", "Companion", "com.nexomc:core"}
)
public final class ItemsAdderConverterListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final NamespacedKey PLACEABLE_BEHAVIOUR_KEY;
   @NotNull
   private static final NamespacedKey PLACEABLE_ITEM_KEY;

   public ItemsAdderConverterListener() {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (Function1<Entity, Unit>)((Entity p0) -> {
(Companion).convertFurniture(p0);
return Unit.INSTANCE;
});
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new ItemsAdderConverterListener$special$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity entity = (Entity)obj2;
               int value4 = 0;
               task.invoke(entity);
            }
         }
      }

   }

   @EventHandler
   public final void onLoadFurniture(@NotNull EntitiesLoadEvent entitiesLoadEvent) {
      Iterable entities = (Iterable)entitiesLoadEvent.getEntities();
      Companion companion = Companion;

      for(Object element : entities) {
         Entity p0 = (Entity)element;
         int value = 0;
         companion.convertFurniture(p0);
      }

   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("itemsadder:placeable_behaviour_type");
      PLACEABLE_BEHAVIOUR_KEY = namespacedKey;
      namespacedKey = NamespacedKey.fromString("itemsadder:placeable_entity_item");
      PLACEABLE_ITEM_KEY = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0016\u0010\b\u001a\u00070\u0005¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007¨\u0006\u000f"},
      d2 = {"Lcom/nexomc/nexo/converter/ItemsAdderConverterListener$Companion;", "", "<init>", "()V", "PLACEABLE_BEHAVIOUR_KEY", "Lorg/bukkit/NamespacedKey;", "getPLACEABLE_BEHAVIOUR_KEY", "()Lorg/bukkit/NamespacedKey;", "PLACEABLE_ITEM_KEY", "Lorg/jetbrains/annotations/Nullable;", "getPLACEABLE_ITEM_KEY", "convertFurniture", "", "entity", "Lorg/bukkit/entity/Entity;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getPLACEABLE_BEHAVIOUR_KEY() {
         return ItemsAdderConverterListener.PLACEABLE_BEHAVIOUR_KEY;
      }

      @NotNull
      public final NamespacedKey getPLACEABLE_ITEM_KEY() {
         return ItemsAdderConverterListener.PLACEABLE_ITEM_KEY;
      }

      public final void convertFurniture(@NotNull Entity entity) {
         ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
         if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
            ItemDisplay baseEntity = itemDisplay;
            PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
            PersistentDataContainer pdc = persistentDataContainer;
            String str = pdc.get(this.getPLACEABLE_ITEM_KEY(), PersistentDataType.STRING);
            if (str != null) {
               CharSequence charSequence = (CharSequence)str;
               List list = StringsKt.split(charSequence, new String[]{":"}, false, 0);
               if (list != null) {
                  List list2 = list;
                  String namespace = (String)list2.get(0);
                  String id = (String)list2.get(1);
                  String str2 = NexoPlugin.Companion.instance().converter().getItemsadderConverter().getChangedItemIds().get(namespace + ":" + id);
                  if (str2 == null) {
                     str2 = id;
                  }

                  String itemId = str2;
                  if (NexoFurniture.furnitureMechanic(itemId) == null) {
                     return;
                  }

                  if (!Intrinsics.areEqual(pdc.get(this.getPLACEABLE_BEHAVIOUR_KEY(), PersistentDataType.STRING), "furniture")) {
                     return;
                  }

                  pdc.remove(this.getPLACEABLE_BEHAVIOUR_KEY());
                  pdc.remove(this.getPLACEABLE_ITEM_KEY());
                  pdc.set(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING, itemId);
                  ItemStack itemStack = baseEntity.getItemStack();
                  Colorable colorable = ColorableKt.asColorable(itemStack);
                  NexoFurniture.furnitureDye(baseEntity, colorable != null ? colorable.getColor() : null);
                  Component data = baseEntity.getItemStack().getData(DataComponentTypes.CUSTOM_NAME);
                  if (data != null) {
                     String str3 = AdventureUtilsKt.serialize(data);
                     if (str3 != null) {
                        String it = str3;
                        int value = 0;
                        pdc.set(FurnitureMechanic.Companion.getDISPLAY_NAME_KEY(), PersistentDataType.STRING, it);
                     }
                  }

                  baseEntity.setItemStack((ItemStack)null);
                  NexoFurniture.updateFurniture(baseEntity);
                  return;
               }
            }

         }
      }
   }
}
