package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitParticle;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@MythicMechanic(
   author = "Ashijin",
   name = "summonAreaEffectCloud",
   aliases = {"summonCloud"},
   description = "Summons an Area Effect Cloud"
)
public class SummonAreaEffectCloudMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected String strParticle;
   protected BukkitParticle particle;
   protected Function<PlaceholderMeta, Object> particleData = null;
   protected PlaceholderString potionEffect;
   protected PlaceholderDuration potionDuration;
   protected PlaceholderInt potionLevel;
   protected PlaceholderDuration duration;
   protected PlaceholderDuration durationReductionOnUse;
   protected PlaceholderInt radius;
   protected PlaceholderFloat radiusReductionOnUse;
   protected PlaceholderFloat radiusReductionOnTick;

   public SummonAreaEffectCloudMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.strParticle = mlc.getString(new String[]{"particle", "p"}, "reddust");
      this.particle = BukkitParticle.get(this.strParticle);
      if (this.particle.requiresData()) {
         this.particleData = this.particle.parseDataOptions(mlc);
      }

      this.potionEffect = mlc.getPlaceholderString(new String[]{"type", "effect", "t"}, "SLOW");
      this.potionDuration = mlc.getPlaceholderDuration(new String[]{"potionduration", "pd"}, DurationUnit.TICKS, "100");
      this.potionLevel = mlc.getPlaceholderInteger(new String[]{"level", "lvl", "l"}, 1);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d", "cloudduration"}, DurationUnit.TICKS, "200");
      this.durationReductionOnUse = mlc.getPlaceholderDuration(new String[]{"durationreudctiononuse", "drou"}, DurationUnit.TICKS, "0");
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 2);
      this.radiusReductionOnUse = mlc.getPlaceholderFloat(new String[]{"radiusreductiononuse", "rrou"}, 0.0F);
      this.radiusReductionOnTick = mlc.getPlaceholderFloat(new String[]{"radiusreductionontick", "rrot"}, 0.0F);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      Location location = BukkitAdapter.adapt(context.location());
      AreaEffectCloud cloud = (AreaEffectCloud)location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
      if (this.particleData != null) {
         cloud.setParticle(this.particle.toBukkitParticle(), this.particleData.apply(context.meta()));
      } else {
         cloud.setParticle(this.particle.toBukkitParticle());
      }

      String potion = this.potionEffect.get(context);
      int pduration = this.potionDuration.get(context).getTicks();
      int lvl = this.potionLevel.get(context);
      PotionEffectType type = PotionEffectType.getByName(potion);

      PotionEffect effect;
      try {
         effect = new PotionEffect(type, pduration, lvl);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, this.config, "The 'type' attribute must be a valid potion type.");
         return SkillResult.INVALID_CONFIG;
      }

      cloud.addCustomEffect(effect, true);
      cloud.setDuration(this.duration.get(context).getTicks());
      cloud.setDurationOnUse(this.durationReductionOnUse.get(context).getTicks());
      cloud.setRadius(this.radius.get(context));
      cloud.setRadiusOnUse(this.radiusReductionOnUse.get(context));
      cloud.setRadiusPerTick(this.radiusReductionOnTick.get(context));
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.cast(PlaceholderContext.builder().meta(data).entity(target).build());
      return SkillResult.SUCCESS;
   }
}
