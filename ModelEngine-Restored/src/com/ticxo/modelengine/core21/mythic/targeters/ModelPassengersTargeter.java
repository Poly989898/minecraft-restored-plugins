package com.ticxo.modelengine.core21.mythic.targeters;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicTargeter;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Entity;

@MythicTargeter(name = "modelpassengers", aliases = {})
public class ModelPassengersTargeter implements IEntityTargeter {
   private final PlaceholderString modelId;
   private final PlaceholderString pbone;

   public ModelPassengersTargeter(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
   }

   public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      SkillCaster caster = skillMetadata.getCaster();
      ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getEntity().getUniqueId());
      if (model == null) {
         return targets;
      }

      String parts = MythicUtils.getOrNullLowercase(this.pbone, skillMetadata);
      if (parts == null) {
         return targets;
      }

      String[] seats = parts.split(",");
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, skillMetadata);
      model.getModel(modelId).ifPresentOrElse(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
         for (String seat : seats) {
            ((MountManager)mountManager).getSeat(seat).ifPresent(mount -> {
               for (Entity passenger : ((Mount)mount).getPassengers()) {
                  targets.add(BukkitAdapter.adapt(passenger));
               }
            });
         }
      }), () -> {
         for (ActiveModel activeModel : model.getModels().values()) {
            activeModel.getMountManager().ifPresent(mountManager -> {
               for (String seat : seats) {
                  ((MountManager)mountManager).getSeat(seat).ifPresent(mount -> {
                     for (Entity passenger : ((Mount)mount).getPassengers()) {
                        targets.add(BukkitAdapter.adapt(passenger));
                     }
                  });
               }
            });
         }
      });
      return targets;
   }
}
