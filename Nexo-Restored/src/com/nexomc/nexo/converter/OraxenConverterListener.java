package com.nexomc.nexo.converter;

import com.nexomc.customblockdata.CustomBlockData;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\b\u001a\u00020\t*\u00020\nH\u0007b\u0002\b\u000bJ\u0010\u0010\f\u001a\u00020\t*\u00020\rH\u0007b\u0002\b\u000bJ\f\u0010\u000e\u001a\u00020\t*\u00020\u000fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/converter/OraxenConverterListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "FURNITURE_KEY", "Lorg/bukkit/NamespacedKey;", "BASE_ENTITY_KEY", "SEAT_KEY", "onChunkLoad", "", "Lorg/bukkit/event/world/ChunkLoadEvent;", "Lorg/bukkit/event/EventHandler;", "onEntitiesLoad", "Lorg/bukkit/event/world/EntitiesLoadEvent;", "convertFurniture", "Lorg/bukkit/entity/Entity;", "com.nexomc:core"}
)
public final class OraxenConverterListener implements Listener {
   @NotNull
   private final NamespacedKey FURNITURE_KEY;
   @NotNull
   private final NamespacedKey BASE_ENTITY_KEY;
   @NotNull
   private final NamespacedKey SEAT_KEY;

   public OraxenConverterListener() {
      NamespacedKey namespacedKey = NamespacedKey.fromString("oraxen:furniture");
      this.FURNITURE_KEY = namespacedKey;
      namespacedKey = NamespacedKey.fromString("oraxen:base_entity");
      this.BASE_ENTITY_KEY = namespacedKey;
      namespacedKey = NamespacedKey.fromString("oraxen:seat");
      this.SEAT_KEY = namespacedKey;
      SchedulerUtils.INSTANCE._runAtWorldEntities((a0_ -> OraxenConverterListener._init_$lambda$0(this, (Entity)a0_)));
   }

   @EventHandler
   public final void onChunkLoad(@NotNull ChunkLoadEvent chunkLoadEvent) {
      CustomBlockData.Companion companion = CustomBlockData.Companion;
      Chunk chunk = chunkLoadEvent.getChunk();

      for(Block block : companion.getBlocksWithCustomData("oraxen", chunk)) {
         if (block.getType() == Material.BARRIER) {
            CustomBlockData pdc = new CustomBlockData(block, "oraxen");
            if (pdc.getKeys().contains(this.BASE_ENTITY_KEY)) {
               block.setType(Material.AIR);
               pdc.clear();
            }
         }
      }

   }

   @EventHandler
   public final void onEntitiesLoad(@NotNull EntitiesLoadEvent entitiesLoadEvent) {
      Iterable entities = (Iterable)entitiesLoadEvent.getEntities();

      for(Object element : entities) {
         Entity it = (Entity)element;
         int value = 0;
         this.convertFurniture(it);
      }

   }

   private final void convertFurniture(Entity entity) {
      PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
      PersistentDataContainer pdc = persistentDataContainer;
      if (entity instanceof Interaction i) {
         int value = 0;
         Interaction interaction = (Interaction)(pdc.has(this.BASE_ENTITY_KEY) && !pdc.has(this.SEAT_KEY) ? entity : null);
         if (interaction != null) {
            interaction.remove();
         }
      } else if (entity instanceof ArmorStand it) {
         int value2 = 0;
         ArmorStand armorStand = (ArmorStand)(pdc.has(this.FURNITURE_KEY) ? entity : null);
         if (armorStand != null) {
            armorStand.remove();
         }
      } else if (entity instanceof ItemFrame it) {
         int value3 = 0;
         ItemFrame itemFrame = (ItemFrame)(pdc.has(this.FURNITURE_KEY) ? entity : null);
         if (itemFrame != null) {
            ItemFrame it_1 = itemFrame;
            value3 = 0;
            Object obj = pdc.get(this.FURNITURE_KEY, PersistentDataType.STRING);
            Location location = it_1.getLocation();
            Logs.logError("Found legacy Oraxen-Furniture " + obj + " using ItemFrame at " + ConfigHelpersKt.fineString(location) + "...", false);
            Logs.logWarn("Nexo only supports ItemDisplay-Furniture, we suggest manually replacing these", false);
         }
      } else if (entity instanceof ItemDisplay) {
         if (!NexoFurniture.isFurniture(entity)) {
            NMSHandlers.handler().getPluginConverter().convertOraxen(entity);
         } else {
            Color color2 = NexoFurniture.furnitureDye((ItemDisplay)entity);
            if (color2 == null) {
               ItemStack itemStack = ((ItemDisplay)entity).getItemStack();
               Colorable colorable = ColorableKt.asColorable(itemStack);
               color2 = colorable != null ? colorable.getColor() : null;
               if (color2 == null) {
                  return;
               }
            }

            Color color = color2;
            NexoFurniture.furnitureDye((ItemDisplay)entity, color);
         }
      }

   }

   private static final Unit _init_$lambda$0(OraxenConverterListener oraxenConverterListener, Entity entity) {
      oraxenConverterListener.convertFurniture(entity);
      return Unit.INSTANCE;
   }
}
