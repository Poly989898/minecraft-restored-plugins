package io.lumine.mythic.lib.script;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import io.lumine.mythic.lib.util.configobject.ConfigSectionObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Script implements PreloadedObject {
   private final String id;
   private final boolean publik;
   private final List<Condition> conditions = new ArrayList<>();
   private final List<Mechanic> mechanics = new ArrayList<>();
   private final PostLoadAction postLoadAction = new PostLoadAction(configx -> {
      if (configx.isConfigurationSection("conditions")) {
         for (String str : configx.getConfigurationSection("conditions").getKeys(false)) {
            this.registerCondition(str, () -> new ConfigSectionObject(configx.getConfigurationSection("conditions." + str)));
         }
      } else if (configx.isList("conditions")) {
         for (Object obj : configx.getList("conditions")) {
            this.registerCondition(String.valueOf(obj), () -> new MMOLineConfig(String.valueOf(obj)));
         }
      }

      if (configx.isConfigurationSection("mechanics")) {
         for (String str : configx.getConfigurationSection("mechanics").getKeys(false)) {
            this.registerMechanic(str, () -> new ConfigSectionObject(configx.getConfigurationSection("mechanics." + str)));
         }
      } else if (configx.isList("mechanics")) {
         for (Object obj : configx.getList("mechanics")) {
            this.registerMechanic(String.valueOf(obj), () -> new MMOLineConfig(String.valueOf(obj)));
         }
      }
   });

   public Script(@NotNull ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = config.getName();
      this.publik = config.getBoolean("public", false);
   }

   public Script(String id, boolean publik) {
      this.id = id;
      this.publik = publik;
   }

   @NotNull
   @Override
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   private void registerCondition(String key, Supplier<ConfigObject> config) {
      try {
         this.conditions.add(MythicLib.plugin.getSkills().loadCondition(config.get()));
      } catch (RuntimeException exception) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load condition '" + key + "' from script '" + this.id + "': " + exception.getMessage());
      }
   }

   private void registerMechanic(String key, Supplier<ConfigObject> config) {
      try {
         this.mechanics.add(MythicLib.plugin.getSkills().loadMechanic(config.get()));
      } catch (RuntimeException exception) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load mechanic '" + key + "' from script '" + this.id + "': " + exception.getMessage());
      }
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   public boolean isPublic() {
      return this.publik;
   }

   @NotNull
   public List<Mechanic> getMechanics() {
      return this.mechanics;
   }

   @NotNull
   public List<Condition> getConditions() {
      return this.conditions;
   }

   public boolean cast(SkillMetadata meta) {
      int conditionCounter = 0;

      for (Condition condition : this.conditions) {
         try {
            conditionCounter++;
            if (!condition.checkIfMet(meta)) {
               return false;
            }
         } catch (RuntimeException exception) {
            MythicLib.plugin
               .getLogger()
               .log(Level.WARNING, "Could not check condition n" + conditionCounter + " from script '" + this.id + "': " + exception.getMessage());
            return false;
         }
      }

      new MechanicQueue(meta, this).next();
      return true;
   }
}
