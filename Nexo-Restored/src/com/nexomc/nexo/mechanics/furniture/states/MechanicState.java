package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Iterator;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u000fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/MechanicState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "doUnsetState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "doSetState", "FurnitureStateMechanic", "com.nexomc:core"}
)
public final class MechanicState extends FurnitureState {
   @NotNull
   private final FurnitureMechanic mechanic;

   public MechanicState(@NotNull ConfigurationSection section) {
      super(section);
      MechanicState mechanicState = this;
      int value = 0;
      ConfigurationSection configurationSection = NexoYamlKt.section(NexoYamlKt.getRootSection(section), "Mechanics.furniture");
      ConfigurationSection parentClone = NexoYamlKt.remove(NexoYamlKt.clone(configurationSection), "states");
      ConfigurationSection configurationSection2 = NexoYamlKt.copyFrom(parentClone, section);
      String name = section.getName();
      this.mechanic = new FurnitureStateMechanic(NexoYamlKt.copyWithParent(configurationSection2, name));
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   protected void doUnsetState(@NotNull ItemDisplay baseEntity) {
      String str = (String)baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING);
      if (str != null) {
         String id = str;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(id);
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            IFurniturePacketManager.Companion.updateBaseEntity(baseEntity, mechanic);
         }
      }
   }

   protected void doSetState(@NotNull ItemDisplay baseEntity) {
      IFurniturePacketManager.Companion.updateBaseEntity(baseEntity, this.mechanic);
      FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.refreshItem(baseEntity);
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/MechanicState$FurnitureStateMechanic;", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "stateSection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "com.nexomc:core"}
   )
   public static final class FurnitureStateMechanic extends FurnitureMechanic {
      public FurnitureStateMechanic(@NotNull ConfigurationSection stateSection) {
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

         super(mechanicFactory, stateSection);
      }
   }
}
