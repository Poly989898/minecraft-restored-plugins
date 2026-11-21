package com.ticxo.modelengine.core21.mythic.mechanics.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import java.util.UUID;

@MythicMechanic(name = "vfxbrightness", aliases = "vfxlight")
public class VFXBrightnessMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderInt blockLight;
   private final PlaceholderInt skyLight;

   public VFXBrightnessMechanic(MythicLineConfig mlc) {
      this.blockLight = mlc.getPlaceholderInteger(new String[]{"block", "b"}, -1, new String[0]);
      this.skyLight = mlc.getPlaceholderInteger(new String[]{"sky", "s"}, -1, new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      VFX vfx = ModelEngineAPI.getVFX(target.getBukkitEntity());
      if (vfx == null) {
         return SkillResult.INVALID_TARGET;
      }

      vfx.setBlockLight(this.blockLight.get(meta, target));
      vfx.setSkyLight(this.skyLight.get(meta, target));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      UUID uuid = MythicUtils.getVFXUniqueId(meta);
      VFX vfx = ModelEngineAPI.getAPI().getVFXUpdater().getVFX(uuid);
      if (vfx == null) {
         return SkillResult.INVALID_TARGET;
      }

      vfx.setBlockLight(this.blockLight.get(meta));
      vfx.setSkyLight(this.skyLight.get(meta));
      return SkillResult.SUCCESS;
   }
}
