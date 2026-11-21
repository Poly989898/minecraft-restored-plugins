package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import lombok.Getter;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class EffectRemoveMetadata extends SkillTriggerMetadata {
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
   private final int nextLevel;
   private final int nextDuration;
   private final String sourceType;
   private final boolean cleared;

   public EffectRemoveMetadata(EntityPotionEffectEvent event, AbstractEntity entity) {
      this.event = event;
      this.entity = entity;
      PotionEffect oldEffect = event.getOldEffect();
      PotionEffect newEffect = event.getNewEffect();
      PotionEffectType effectType = oldEffect != null ? oldEffect.getType() : (newEffect != null ? newEffect.getType() : null);
      this.effectId = EffectMetadataSupport.resolveEffectId(effectType);
      this.level = EffectMetadataSupport.resolveLevel(oldEffect);
      this.duration = EffectMetadataSupport.resolveDuration(oldEffect);
      this.ambient = oldEffect != null && oldEffect.isAmbient();
      this.particles = oldEffect != null && oldEffect.hasParticles();
      this.icon = oldEffect != null && oldEffect.hasIcon();
      this.cause = event.getCause().name();
      this.action = event.getAction().name();
      this.nextLevel = EffectMetadataSupport.resolveLevel(newEffect);
      this.nextDuration = EffectMetadataSupport.resolveDuration(newEffect);
      this.sourceType = this.cause;
      this.cleared = event.getAction() == Action.CLEARED;
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
      EffectMetadataSupport.putInt(vars, "effect_next_level", this.nextLevel);
      EffectMetadataSupport.putInt(vars, "effect_next_duration", this.nextDuration);
      EffectMetadataSupport.putString(vars, "effect_source_type", this.sourceType);
      EffectMetadataSupport.putString(vars, "effect_cleared", String.valueOf(this.cleared));
   }
}
