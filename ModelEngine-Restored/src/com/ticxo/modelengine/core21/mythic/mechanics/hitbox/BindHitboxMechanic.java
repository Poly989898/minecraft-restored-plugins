package com.ticxo.modelengine.core21.mythic.mechanics.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.joml.Vector3f;

@MythicMechanic(name = "bindhitbox", aliases = {})
public class BindHitboxMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final String strType;
   private final boolean inheritFaction;
   private MythicMob mm;
   private BukkitEntityType me;

   public BindHitboxMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
      this.strType = mlc.getString(new String[]{"type", "t", "mob", "m"}, "SKELETON", new String[0]);
      this.inheritFaction = mlc.getBoolean(new String[]{"inheritfaction", "if"}, true);
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         this.mm = this.getPlugin().getMobManager().getMythicMob(this.strType).orElse(null);
         if (this.mm == null) {
            this.me = BukkitEntityType.getMythicEntity(this.strType);
            if (this.me == null) {
               MythicLogger.errorGenericConfig(mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
            }
         }
      });
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      model.getModel(modelId).ifPresent(activeModel -> {
         String pbone = MythicUtils.getOrNullLowercase(this.partId, meta, target);
         if (pbone != null) {
            List<String> hitboxes = List.of(pbone.split(","));
            ArrayList<SubHitbox> subHitboxes = new ArrayList<>();

            for (String hitboxId : hitboxes) {
               Optional<ModelBone> maybeBone = activeModel.getBone(hitboxId);
               if (!maybeBone.isEmpty()) {
                  ModelBone bone = maybeBone.get();
                  Optional maybeHitbox = bone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX);
                  if (!maybeHitbox.isEmpty()) {
                     subHitboxes.add((SubHitbox)maybeHitbox.get());
                  }
               }
            }

            this.createBoundEntity(meta.getCaster(), subHitboxes);
         }
      });
      return SkillResult.SUCCESS;
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   private void createBoundEntity(SkillCaster caster, List<SubHitbox> subHitboxes) {
      if (!subHitboxes.isEmpty()) {
         AbstractEntity abstractCaster = caster.getEntity();
         Vector3f vec = subHitboxes.get(0).getLocation();
         AbstractLocation l = new AbstractLocation(abstractCaster.getWorld(), vec.x, vec.y, vec.z);
         if (this.mm != null) {
            ActiveMob ams = this.mm.spawn(l, caster.getLevel(), SpawnReason.SUMMON, entity -> {
               for (SubHitbox hitbox : subHitboxes) {
                  hitbox.addBoundEntity(entity);
               }
            });
            ams.setParent(caster);
            ams.setOwnerUUID(abstractCaster.getUniqueId());
            if (this.inheritFaction && caster instanceof ActiveMob am) {
               ams.setFaction(am.getFaction());
            }
         } else if (this.me != null) {
            this.me.spawn(l, SpawnReason.SUMMON, entity -> {
               for (SubHitbox hitbox : subHitboxes) {
                  hitbox.addBoundEntity(entity);
               }
            });
         }
      }
   }
}
