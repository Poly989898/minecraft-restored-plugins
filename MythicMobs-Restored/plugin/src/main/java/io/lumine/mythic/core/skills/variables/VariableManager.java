package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.serialize.GsonStorageHandler;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.io.File;
import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class VariableManager extends ReloadableModule<MythicBukkit> implements Terminable {
   private final GsonStorageHandler<VariableRegistry> globalRegistryReader;
   @Getter
   private VariableRegistry globalRegistry;
   private boolean loaded = false;

   public VariableManager(MythicBukkit plugin) {
      super(plugin, false);
      File dataFolder = new File(plugin.getDataFolder(), "data");
      this.globalRegistryReader = new GsonStorageHandler<>("global-data", ".json", dataFolder, VariableRegistry.class);
      this.load(plugin);
   }

   public void load(MythicBukkit plugin) {
      Optional<VariableRegistry> maybeRegistry = this.globalRegistryReader.load();
      if (maybeRegistry.isPresent()) {
         this.globalRegistry = maybeRegistry.get();
      } else {
         this.globalRegistry = new VariableRegistry();
      }

      this.loaded = true;
   }

   @Override
   public synchronized void unload() {
      this.globalRegistryReader.save(this.globalRegistry);
   }

   public void save() {
      if (this.loaded) {
         if (this.getPlugin().isShuttingDown()) {
            this.unload();
         } else {
            Schedulers.ensureAsync(this::unload);
         }
      }
   }

   public VariableRegistry getRegistry(VariableScope scope, @NotNull PlaceholderContext context) {
      if (context.meta() != null) {
         if (context.entity() != null) {
            return this.getRegistry(scope, context.meta(), context.entity());
         }

         if (context.meta() instanceof SkillMetadata skillMeta) {
            return this.getRegistry(scope, skillMeta);
         }
      }

      if (context.entity() != null) {
         return this.getRegistry(scope, context.entity());
      } else {
         return context.location() != null ? this.getRegistry(scope, context.location()) : null;
      }
   }

   public VariableRegistry getRegistry(VariableScope scope, PlaceholderMeta meta) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Getting VariableRegistry in scope {0}", scope);
      switch (scope) {
         case CASTER:
            SkillCaster caster = meta.getCaster();
            if (caster instanceof VariableHolder holder) {
               if (caster instanceof ActiveMob) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster mob registry...");
               } else if (caster.getEntity().isPlayer()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster player registry...");
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster registry...");
               }

               return holder.getVariables();
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "! Can't get registry for null caster");
            return null;
         case SKILL:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting skill registry...");
            if (meta instanceof SkillMetadata skillMeta) {
               return skillMeta.getVariables();
            }

            return null;
         case TARGET:
            return null;
         case WORLD:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting world registry...");
            SkillCaster worldCaster = meta.getCaster();
            if (worldCaster != null) {
               return this.getPlugin().getWorldManager().getWorldData(worldCaster.getEntity().getWorld()).getVariables();
            }

            return null;
         case GLOBAL:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting global registry...");
            return this.globalRegistry;
         default:
            return null;
      }
   }

   public VariableRegistry getRegistry(VariableScope scope, PlaceholderMeta meta, AbstractEntity entity) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Getting VariableRegistry in scope {0}", scope);
      switch (scope) {
         case CASTER:
            SkillCaster casterx = meta.getCaster();
            if (casterx instanceof VariableHolder holder) {
               if (casterx instanceof ActiveMob) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster mob registry...");
               } else if (casterx.getEntity().isPlayer()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster player registry...");
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting caster registry...");
               }

               return holder.getVariables();
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "! Can't get registry for null caster");
            return null;
         case SKILL:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting skill registry...");
            if (meta instanceof SkillMetadata skillMetadata) {
               return skillMetadata.getVariables();
            }

            return null;
         case TARGET:
            SkillCaster targetCaster = this.getPlugin().getSkillManager().getCasterNullable(entity);
            if (targetCaster instanceof VariableHolder holder) {
               if (targetCaster instanceof ActiveMob) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target mob registry...");
               } else if (targetCaster.getEntity().isPlayer()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target player registry...");
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target entity registry...");
               }

               return holder.getVariables();
            }
         default:
            return null;
         case WORLD:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting world registry...");
            SkillCaster caster = meta.getCaster();
            if (caster != null) {
               return this.getPlugin().getWorldManager().getWorldData(caster.getEntity().getWorld()).getVariables();
            } else {
               if (entity != null) {
                  return this.getPlugin().getWorldManager().getWorldData(entity.getWorld()).getVariables();
               }

               return null;
            }
         case GLOBAL:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting global registry...");
            return this.globalRegistry;
      }
   }

   public VariableRegistry getRegistry(VariableScope scope, AbstractEntity entity) {
      if (entity == null) {
         return scope == VariableScope.GLOBAL ? this.globalRegistry : null;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Getting VariableRegistry for entity {0} in scope {1}", entity.getName(), scope);
         switch (scope) {
            case CASTER:
               SkillCaster casterx = this.getPlugin().getSkillManager().getCaster(entity);
               if (casterx instanceof VariableHolder holder) {
                  if (casterx instanceof ActiveMob) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target mob registry...");
                  } else if (casterx.getEntity().isPlayer()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target player registry...");
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target registry...");
                  }

                  return holder.getVariables();
               }
            case SKILL:
            default:
               break;
            case TARGET:
               SkillCaster caster = this.getPlugin().getSkillManager().getCaster(entity);
               if (caster instanceof VariableHolder holder) {
                  if (caster instanceof ActiveMob) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target mob registry...");
                  } else if (caster.getEntity().isPlayer()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target player registry...");
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Getting target registry...");
                  }

                  return holder.getVariables();
               }
               break;
            case WORLD:
               return this.getPlugin().getWorldManager().getWorldData(entity.getWorld()).getVariables();
            case GLOBAL:
               return this.globalRegistry;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "! Can't get caster registry in this context");
         return null;
      }
   }

   public VariableRegistry getRegistry(ActiveMob am) {
      return am.getVariables();
   }

   public VariableRegistry getRegistry(VariableScope scope, AbstractLocation target) {
      switch (scope) {
         case CASTER:
            return null;
         case SKILL:
         case TARGET:
         default:
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "! Can't get caster registry in this context");
            return null;
         case WORLD:
            return this.getPlugin().getWorldManager().getWorldData(target.getWorld()).getVariables();
         case GLOBAL:
            return this.globalRegistry;
      }
   }
}
