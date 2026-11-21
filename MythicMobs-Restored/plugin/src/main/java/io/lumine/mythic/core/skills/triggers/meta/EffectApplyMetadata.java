package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import lombok.Getter;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class EffectApplyMetadata extends SkillTriggerMetadata implements CancellableTriggerMetadata {
   private final EntityPotionEffectEvent event;
   private final AbstractEntity entity;
   private final String effectId;
   private final int level;
   private final int duration;
   private final boolean ambient;
   private final boolean particles;
   private final boolean icon;
   private final String cause;
   private final String action;
   private final int previousLevel;
   private final int previousDuration;
   private final String sourceType;
   private final boolean isNew;
   private final boolean overwrite;

   public EffectApplyMetadata(EntityPotionEffectEvent event, AbstractEntity entity) {
      this.event = event;
      this.entity = entity;
      PotionEffect newEffect = event.getNewEffect();
      PotionEffect oldEffect = event.getOldEffect();
      PotionEffectType effectType = newEffect != null ? newEffect.getType() : (oldEffect != null ? oldEffect.getType() : null);
      this.effectId = EffectMetadataSupport.resolveEffectId(effectType);
      this.level = EffectMetadataSupport.resolveLevel(newEffect);
      this.duration = EffectMetadataSupport.resolveDuration(newEffect);
      this.ambient = newEffect != null && newEffect.isAmbient();
      this.particles = newEffect != null && newEffect.hasParticles();
      this.icon = newEffect != null && newEffect.hasIcon();
      this.cause = event.getCause().name();
      this.action = event.getAction().name();
      this.previousLevel = EffectMetadataSupport.resolveLevel(oldEffect);
      this.previousDuration = EffectMetadataSupport.resolveDuration(oldEffect);
      this.sourceType = this.cause;
      this.isNew = oldEffect == null;
      this.overwrite = event.isOverride();
   }

   @Override
   public boolean isCancelled() {
      return this.event.isCancelled();
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      VariableRegistry vars = skillMetadata.getVariables();
      EffectMetadataSupport.putString(vars, "effect_id", this.effectId);
      EffectMetadataSupport.putInt(vars, "effect_level", this.level);
      EffectMetadataSupport.putInt(vars, "effect_duration", this.duration);
      EffectMetadataSupport.putString(vars, "effect_ambient", String.valueOf(this.ambient));
      EffectMetadataSupport.putString(vars, "effect_particles", String.valueOf(this.particles));
      EffectMetadataSupport.putString(vars, "effect_icon", String.valueOf(this.icon));
      EffectMetadataSupport.putString(vars, "effect_cause", this.cause);
      EffectMetadataSupport.putString(vars, "effect_action", this.action);
      EffectMetadataSupport.putInt(vars, "effect_previous_level", this.previousLevel);
      EffectMetadataSupport.putInt(vars, "effect_previous_duration", this.previousDuration);
      EffectMetadataSupport.putString(vars, "effect_source_type", this.sourceType);
      EffectMetadataSupport.putString(vars, "effect_is_new", String.valueOf(this.isNew));
      EffectMetadataSupport.putString(vars, "effect_overwrite", String.valueOf(this.overwrite));
   }
}
