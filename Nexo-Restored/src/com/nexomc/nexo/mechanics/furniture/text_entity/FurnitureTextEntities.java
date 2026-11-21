package com.nexomc.nexo.mechanics.furniture.text_entity;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006B\u0011\b\u0016\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\u0005\u0010\tJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/text_entity/FurnitureTextEntities;", "", "textEntities", "", "Lcom/nexomc/nexo/mechanics/furniture/text_entity/TextEntityMechanic;", "<init>", "(Ljava/util/List;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getTextEntities", "()Ljava/util/List;", "refreshTextEntities", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "com.nexomc:core"}
)
public final class FurnitureTextEntities {
   @NotNull
   private final List<TextEntityMechanic> textEntities;

   public FurnitureTextEntities(@NotNull List<TextEntityMechanic> textEntities) {
      super();
      this.textEntities = textEntities;
   }

   @NotNull
   public final List<TextEntityMechanic> getTextEntities() {
      return this.textEntities;
   }

   public FurnitureTextEntities(@NotNull ConfigurationSection section) {
      /* this-alias removed */
      Collection collection = (Collection)NexoYamlKt.sectionListOrSingle(section, "text_entity");
      Object obj;
      if (collection.isEmpty()) {
         int value = 0;
         obj = NexoYamlKt.sectionListOrSingle(section, "text_entities");
         /* this-alias removed */
      } else {
         obj = collection;
      }

      Iterable iterable = (Iterable)obj;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         ConfigurationSection p0 = (ConfigurationSection)item;
         int value2 = 0;
         destination.add(new TextEntityMechanic(p0));
      }

      this((List)destination);
   }

   public final void refreshTextEntities(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
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
            packetManager.removeTextEntityMechanicPacket(baseEntity, mechanic);
            packetManager.sendTextEntityMechanicPacket(baseEntity, mechanic);
            return;
         }
      }

   }
}
