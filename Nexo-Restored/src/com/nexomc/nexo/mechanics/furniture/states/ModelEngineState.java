package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.PluginUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.jvm.optionals.OptionalsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/ModelEngineState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "blueprint", "", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)V", "getBlueprint", "()Ljava/lang/String;", "doUnsetState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "doSetState", "com.nexomc:core"}
)
public class ModelEngineState extends FurnitureState {
   @NotNull
   private final String blueprint;

   public ModelEngineState(@NotNull ConfigurationSection section) {
      this(section, NexoYamlKt.stringOrEmpty(section, "blueprint"));
   }

   public ModelEngineState(@NotNull ConfigurationSection section, @NotNull String blueprint) {
      super(section);
      this.blueprint = blueprint;
      if (!PluginUtils.INSTANCE.isModelEngineEnabled()) {
         int value = 0;
         String str = this.getType() + " is only allowed when ModelEngine is on the server";
         throw new IllegalArgumentException(str.toString());
      } else if (ModelEngineAPI.getBlueprint(this.getBlueprint()) == null) {
         int value2 = 0;
         String str2 = "There was no BluePrint with the following model: " + this.getBlueprint();
         throw new IllegalArgumentException(str2.toString());
      }
   }

   @NotNull
   public String getBlueprint() {
      return this.blueprint;
   }

   protected void doUnsetState(@NotNull ItemDisplay baseEntity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic mechanic = furnitureMechanic;
         ModeledEntity modelEntity = ModelEngineAPI.getOrCreateModeledEntity((Entity)baseEntity);
         Optional model = modelEntity.getModel(this.getBlueprint());
         if (model != null) {
            ActiveModel orNull = (ActiveModel)OptionalsKt.getOrNull(model);
            if (orNull != null) {
               orNull.destroy();
            }
         }

         modelEntity.removeModel(this.getBlueprint());
         if (mechanic.isModelEngine()) {
            modelEntity.addModel(ModelEngineAPI.createActiveModel(mechanic.getModelEngineID()), true);
         } else {
            ModelEngineAPI.removeModeledEntity((Entity)baseEntity);
         }

      }
   }

   protected void doSetState(@NotNull ItemDisplay baseEntity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic mechanic = furnitureMechanic;
         ModeledEntity modelEntity = ModelEngineAPI.getOrCreateModeledEntity((Entity)baseEntity);
         if (mechanic.isModelEngine()) {
            Optional model = modelEntity.getModel(mechanic.getModelEngineID());
            if (model != null) {
               ActiveModel orNull = (ActiveModel)OptionalsKt.getOrNull(model);
               if (orNull != null) {
                  orNull.destroy();
               }
            }

            modelEntity.removeModel(mechanic.getModelEngineID());
         }

         modelEntity.addModel(ModelEngineAPI.createActiveModel(this.getBlueprint()), true);
      }
   }
}
