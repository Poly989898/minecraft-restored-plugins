package net.Indyuce.mmocore.api.quest;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.experience.Profession;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Quest implements PreloadedObject {
   private final String id;
   private final String name;
   private final List<Quest> parents = new ArrayList<>();
   private final List<Objective> objectives = new ArrayList<>();
   private final List<String> lore;
   private final int mainLevelRestriction;
   private final Map<Profession, Integer> levelRestrictions = new HashMap<>();
   private final long cooldown;
   private final PostLoadAction postLoadAction = new PostLoadAction(configx -> {
      if (configx.contains("parent")) {
         for (String parent : configx.getStringList("parent")) {
            this.parents.add(MMOCore.plugin.questManager.getOrThrow(parent.toLowerCase().replace(" ", "-").replace("_", "-")));
         }
      }
   });

   public Quest(String id, ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = id.toLowerCase().replace("_", "-").replace(" ", "-");
      this.cooldown = (long)(config.contains("delay") ? config.getDouble("delay") * 60.0 * 60.0 * 1000.0 : -1.0);
      this.name = config.getString("name");
      this.lore = config.getStringList("lore");
      this.mainLevelRestriction = config.getInt("level-req.main");
      if (config.contains("level-req")) {
         for (String key : config.getConfigurationSection("level-req").getKeys(false)) {
            if (!key.equals("main")) {
               try {
                  String id1 = key.toLowerCase().replace("_", "-");
                  Validate.isTrue(MMOCore.plugin.professionManager.has(id1), "Could not find profession called '" + id1 + "'");
                  this.levelRestrictions.put(MMOCore.plugin.professionManager.get(id1), config.getInt("level-req." + key));
               } catch (IllegalArgumentException exception) {
                  MMOCore.plugin
                     .getLogger()
                     .log(Level.WARNING, "Could not load level requirement '" + key + "' from quest '" + id + "': " + exception.getMessage());
               }
            }
         }
      }

      for (String key : config.getConfigurationSection("objectives").getKeys(false)) {
         try {
            ConfigurationSection section = config.getConfigurationSection("objectives." + key);
            Validate.notNull(section, "Could not find config section");
            String format = section.getString("type");
            Validate.notNull(format, "Objective is missing format");
            this.objectives.add(MMOCore.plugin.loadManager.loadObjective(new MMOLineConfig(format), section));
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load objective '" + key + "' from quest '" + id + "': " + exception.getMessage());
         }
      }
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public boolean hasParent() {
      return this.parents.size() > 0;
   }

   public List<Quest> getParents() {
      return this.parents;
   }

   public boolean isRedoable() {
      return this.cooldown >= 0L;
   }

   public long getDelayMillis() {
      return this.cooldown;
   }

   public List<Objective> getObjectives() {
      return this.objectives;
   }

   public Set<Profession> getLevelRestrictions() {
      return this.levelRestrictions.keySet();
   }

   public int countLevelRestrictions() {
      return this.levelRestrictions.size() + (this.mainLevelRestriction > 0 ? 1 : 0);
   }

   public int getLevelRestriction(Profession profession) {
      return profession == null ? this.mainLevelRestriction : this.levelRestrictions.getOrDefault(profession, 0);
   }

   public QuestProgress generateNewProgress(PlayerData player) {
      return this.generateNewProgress(player, 0);
   }

   public QuestProgress generateNewProgress(PlayerData player, int objective) {
      return new QuestProgress(this, player, objective);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Quest && ((Quest)obj).id.equals(this.id);
   }
}
