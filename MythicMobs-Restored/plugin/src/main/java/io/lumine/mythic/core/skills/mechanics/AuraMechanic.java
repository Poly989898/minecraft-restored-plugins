package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.auras.components.AuraComponent;
import io.lumine.mythic.core.skills.auras.components.AuraComponentFactory;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@MythicMechanic(
   author = "Ashijin",
   name = "aura",
   aliases = {"buff", "debuff"},
   description = "Applies a generic aura to the target"
)
public class AuraMechanic extends Aura implements ITargetedEntitySkill, ITargetedLocationSkill {
   private final List<AuraComponentFactory> componentFactories = new ArrayList<>();

   public AuraMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      List<String> componentLines = mlc.getStringList(new String[]{"components", "component", "comp"}, null);
      if (componentLines != null && !componentLines.isEmpty()) {
         List<MythicLineConfig> componentConfigs = new ArrayList<>();

         for (String componentLine : componentLines) {
            if (componentLine != null && !componentLine.isEmpty()) {
               componentLine = MythicLineConfigImpl.unparseBlock(componentLine);
               String name = this.extractComponentName(componentLine);
               MythicLineConfig componentConfig = new MythicLineConfigImpl(componentLine);
               componentConfigs.add(componentConfig);
               AuraComponentFactory factory = this.getManager().getAuraComponentFactory(name, this, componentConfig);
               if (factory != null) {
                  this.componentFactories.add(factory);
               } else {
                  MythicLogger.errorMechanicConfig(this, componentConfig, "Unknown aura component: " + name);
               }
            }
         }

         this.applyComponentCancelOverrides(mlc, componentConfigs);
      }
   }

   private void applyComponentCancelOverrides(MythicLineConfig mlc, List<MythicLineConfig> componentConfigs) {
      if (!componentConfigs.isEmpty()) {
         this.cancelOnDeath = resolveCancelFlag(mlc, componentConfigs, this.cancelOnDeath, "cancelondeath", "cod");
         this.cancelOnCasterDeath = resolveCancelFlag(mlc, componentConfigs, this.cancelOnCasterDeath, "canceloncasterdeath", "cocd");
         this.cancelOnTeleport = resolveCancelFlag(mlc, componentConfigs, this.cancelOnTeleport, "cancelonteleport", "cot");
         this.cancelOnChangeWorld = resolveCancelFlag(mlc, componentConfigs, this.cancelOnChangeWorld, "cancelonchangeworld", "cocw");
         this.cancelOnGiveDamage = resolveCancelFlag(mlc, componentConfigs, this.cancelOnGiveDamage, "cancelongivedamage", "cogd");
         this.cancelOnTakeDamage = resolveCancelFlag(mlc, componentConfigs, this.cancelOnTakeDamage, "cancelontakedamage", "cotd");
      }
   }

   static boolean resolveCancelFlag(MythicLineConfig mlc, List<MythicLineConfig> componentConfigs, boolean current, String... keys) {
      if (mlc.getBooleanNullable(keys) != null) {
         return current;
      } else {
         for (MythicLineConfig componentConfig : componentConfigs) {
            Boolean value = componentConfig.getBooleanNullable(keys);
            if (value != null) {
               return value;
            }
         }

         return current;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new AuraMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.targetIsCaster) {
         SkillCaster caster = this.getManager().getCaster(target);
         data = data.deepClone();
         data.setCaster(caster);
      }

      new AuraMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public void setThreadSafetyLevel(ThreadSafetyLevel level) {
      this.threadSafetyLevel = level;
   }

   public void setInterval(PlaceholderInt interval) {
      this.interval = PlaceholderDuration.of(interval.toString(), DurationUnit.TICKS);
   }

   public void setInterval(PlaceholderDuration interval) {
      this.interval = interval;
   }

   public void addAuraTag(String tag) {
      if (tag != null && !tag.isEmpty()) {
         this.auraTags.add(PlaceholderString.of(tag));
      }
   }

   private String extractComponentName(String componentLine) {
      int braceIndex = componentLine.indexOf("{");
      return braceIndex < 0 ? componentLine.trim() : componentLine.substring(0, braceIndex).trim();
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      private final List<AuraComponent> components = new ArrayList<>();

      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.buildComponents();
         this.start();
      }

      public Tracker(SkillMetadata data, AbstractLocation location) {
         super(location, data);
         this.buildComponents();
         this.start();
      }

      private void buildComponents() {
         if (!AuraMechanic.this.componentFactories.isEmpty()) {
            for (AuraComponentFactory factory : AuraMechanic.this.componentFactories) {
               AuraComponent component = factory.create(this);
               if (component != null) {
                  this.components.add(component);
                  if (component instanceof Terminable terminable) {
                     this.registerAuraComponent(terminable);
                  }
               }
            }
         }
      }

      @Override
      public void auraStart() {
         super.auraStart();
         this.components.forEach(AuraComponent::auraStart);
      }

      @Override
      public void auraTick() {
         super.auraTick();
         this.components.forEach(AuraComponent::auraTick);
      }

      @Override
      public void auraStop() {
         this.components.forEach(AuraComponent::auraStop);
         super.auraStop();
      }
   }
}
