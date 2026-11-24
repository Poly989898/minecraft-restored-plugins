package net.Indyuce.mmocore.api.player.profess;

import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.version.Attributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.player.ClassDataContainer;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import org.bukkit.configuration.ConfigurationSection;

public class SavedClassInformation implements ClassDataContainer {
   private final int level;
   private final int skillPoints;
   private final int attributePoints;
   private final int attributeReallocationPoints;
   private final int skillTreeReallocationPoints;
   private final int skillReallocationPoints;
   private final double experience;
   private final double health;
   private final double mana;
   private final double stellium;
   private final double stamina;
   private final Map<String, Integer> attributeLevels = new HashMap<>();
   private final Map<String, Integer> skillLevels = new HashMap<>();
   private final Map<String, Integer> skillTreePoints = new HashMap<>();
   private final Map<String, Integer> nodeLevels = new HashMap<>();
   private final Map<String, Integer> nodeTimesClaimed = new HashMap<>();
   private final Map<Integer, String> boundSkills = new HashMap<>();
   private final Set<String> unlockedItems = new HashSet<>();

   public SavedClassInformation(ConfigurationSection config) {
      this.level = config.getInt("level");
      this.experience = config.getDouble("experience");
      this.skillPoints = config.getInt("skill-points");
      this.attributePoints = config.getInt("attribute-points");
      this.attributeReallocationPoints = config.getInt("attribute-realloc-points");
      this.skillReallocationPoints = config.getInt("skill-reallocation-points");
      this.skillTreeReallocationPoints = config.getInt("skill-tree-reallocation-points");
      this.health = config.getDouble("health", 20.0);
      this.mana = config.getDouble("mana", 0.0);
      this.stamina = config.getDouble("stamina", 0.0);
      this.stellium = config.getDouble("stellium", 0.0);
      if (config.contains("attribute")) {
         config.getConfigurationSection("attribute").getKeys(false).forEach(key -> this.attributeLevels.put(key, config.getInt("attribute." + key)));
      }

      if (config.contains("skill")) {
         config.getConfigurationSection("skill").getKeys(false).forEach(key -> this.skillLevels.put(key, config.getInt("skill." + key)));
      }

      if (config.contains("skill-tree-points")) {
         config.getConfigurationSection("skill-tree-points")
            .getKeys(false)
            .forEach(key -> this.skillTreePoints.put(key, config.getInt("skill-tree-points." + key)));
      }

      if (config.contains("node-levels")) {
         config.getConfigurationSection("node-levels").getKeys(false).forEach(key -> this.nodeLevels.put(key, config.getInt("node-levels." + key)));
      }

      if (config.contains("node-times-claimed")) {
         config.getConfigurationSection("node-times-claimed")
            .getKeys(false)
            .forEach(key -> this.nodeTimesClaimed.put(key, config.getInt("node-times-claimed." + key)));
      }

      if (config.isConfigurationSection("bound-skills")) {
         config.getConfigurationSection("bound-skills")
            .getKeys(false)
            .forEach(key -> this.boundSkills.put(Integer.parseInt(key), config.getString("bound-skills." + key)));
      }

      this.unlockedItems.addAll(config.getStringList("unlocked-items"));
   }

   public SavedClassInformation(JsonObject json) {
      this.level = json.get("level").getAsInt();
      this.experience = json.get("experience").getAsDouble();
      this.skillPoints = json.get("skill-points").getAsInt();
      this.attributePoints = json.get("attribute-points").getAsInt();
      this.attributeReallocationPoints = json.get("attribute-realloc-points").getAsInt();
      this.skillReallocationPoints = json.get("skill-reallocation-points").getAsInt();
      this.skillTreeReallocationPoints = json.get("skill-tree-reallocation-points").getAsInt();
      this.health = json.has("health") ? json.get("health").getAsDouble() : 20.0;
      this.mana = json.has("mana") ? json.get("mana").getAsDouble() : 0.0;
      this.stamina = json.has("stamina") ? json.get("stamina").getAsDouble() : 0.0;
      this.stellium = json.has("stellium") ? json.get("stellium").getAsDouble() : 0.0;
      if (json.has("attribute")) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("attribute").entrySet()) {
            this.attributeLevels.put(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (json.has("skill")) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("skill").entrySet()) {
            this.skillLevels.put(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (json.has("skill-tree-points")) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("skill-tree-points").entrySet()) {
            this.skillTreePoints.put(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (json.has("node-levels")) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("node-levels").entrySet()) {
            this.nodeLevels.put(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (json.has("node-times-claimed")) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("node-times-claimed").entrySet()) {
            this.nodeTimesClaimed.put(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (json.has("bound-skills") && json.get("bound-skills").isJsonObject()) {
         for (Entry<String, JsonElement> entry : json.getAsJsonObject("bound-skills").entrySet()) {
            this.boundSkills.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
         }
      }

      if (json.has("unlocked-items")) {
         for (JsonElement unlockedItem : json.get("unlocked-items").getAsJsonArray()) {
            this.unlockedItems.add(unlockedItem.getAsString());
         }
      }
   }

   public SavedClassInformation(ClassDataContainer data) {
      this.level = data.getLevel();
      this.skillPoints = data.getSkillPoints();
      this.attributePoints = data.getAttributePoints();
      this.attributeReallocationPoints = data.getAttributeReallocationPoints();
      this.skillTreeReallocationPoints = data.getSkillTreeReallocationPoints();
      this.skillReallocationPoints = data.getSkillReallocationPoints();
      this.experience = data.getExperience();
      this.health = data.getHealth();
      this.mana = data.getMana();
      this.stellium = data.getStellium();
      this.stamina = data.getStamina();
      this.attributeLevels.putAll(data.mapAttributeLevels());
      this.skillLevels.putAll(data.mapSkillLevels());
      this.skillTreePoints.putAll(data.mapSkillTreePoints());
      this.nodeLevels.putAll(data.getNodeLevels());
      this.nodeTimesClaimed.putAll(data.getNodeTimesClaimed());
      this.boundSkills.putAll(data.mapBoundSkills());
      this.unlockedItems.addAll(data.getUnlockedItems());
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   @Override
   public double getExperience() {
      return this.experience;
   }

   @Override
   public int getSkillPoints() {
      return this.skillPoints;
   }

   @Override
   public int getAttributePoints() {
      return this.attributePoints;
   }

   @Override
   public int getAttributeReallocationPoints() {
      return this.attributeReallocationPoints;
   }

   @Override
   public double getHealth() {
      return this.health;
   }

   @Override
   public double getMana() {
      return this.mana;
   }

   @Override
   public double getStellium() {
      return this.stellium;
   }

   @Override
   public double getStamina() {
      return this.stamina;
   }

   public Set<String> getSkillKeys() {
      return this.skillLevels.keySet();
   }

   public int getSkillLevel(RegisteredSkill skill) {
      return this.getSkillLevel(skill.getHandler().getId());
   }

   public int getSkillLevel(String id) {
      return this.skillLevels.get(id);
   }

   public void registerSkillLevel(RegisteredSkill skill, int level) {
      this.registerSkillLevel(skill.getHandler().getId(), level);
   }

   @Override
   public Map<String, Integer> mapSkillLevels() {
      return this.skillLevels;
   }

   @Override
   public Map<String, Integer> mapSkillTreePoints() {
      return this.skillTreePoints;
   }

   @Deprecated
   public Map<Integer, String> getBoundSkills() {
      return this.mapBoundSkills();
   }

   @Override
   public int getSkillTreeReallocationPoints() {
      return this.skillTreeReallocationPoints;
   }

   @Override
   public int getSkillReallocationPoints() {
      return this.skillReallocationPoints;
   }

   public void registerSkillLevel(String attribute, int level) {
      this.skillLevels.put(attribute, level);
   }

   public Set<String> getNodeKeys() {
      return this.nodeLevels.keySet();
   }

   public int getNodeLevel(String node) {
      return this.nodeLevels.get(node);
   }

   @Override
   public Map<String, Integer> mapAttributeLevels() {
      return this.attributeLevels;
   }

   @Override
   public Map<Integer, String> mapBoundSkills() {
      return this.boundSkills;
   }

   @Override
   public Map<String, Integer> getNodeLevels() {
      return this.nodeLevels;
   }

   @Override
   public Map<String, Integer> getNodeTimesClaimed() {
      return this.nodeTimesClaimed;
   }

   public Set<String> getSkillTreePointsKeys() {
      return this.skillTreePoints.keySet();
   }

   public int getSkillTreePoints(String skillTreeId) {
      return this.skillTreePoints.get(skillTreeId);
   }

   public Set<String> getAttributeKeys() {
      return this.attributeLevels.keySet();
   }

   public int getAttributeLevel(String id) {
      return this.attributeLevels.getOrDefault(id, 0);
   }

   public void registerAttributeLevel(PlayerAttribute attribute, int level) {
      this.registerAttributeLevel(attribute.getId(), level);
   }

   public void registerAttributeLevel(String attribute, int level) {
      this.attributeLevels.put(attribute, level);
   }

   @Override
   public Set<String> getUnlockedItems() {
      return this.unlockedItems;
   }

   public void load(PlayerClass profess, PlayerData player) {
      if (!player.getProfess().hasOption(ClassOption.DEFAULT) || MMOCore.plugin.configManager.saveDefaultClassInfo) {
         player.applyClassInfo(player.getProfess(), new SavedClassInformation(player));
      }

      player.getProfess().resetAdvancement(player, false);
      player.mapSkillLevels().forEach((skill, level) -> player.resetSkillLevel(skill));

      for (PlayerAttribute attribute : MMOCore.plugin.attributeManager.getAll()) {
         attribute.resetAdvancement(player, false);
         player.getAttributes().getInstance(attribute).setBase(0);
      }

      player.resetSkillTrees();
      player.setLevel(this.level);
      player.setExperience(this.experience);
      player.setSkillPoints(this.skillPoints);
      player.setAttributePoints(this.attributePoints);
      player.setAttributeReallocationPoints(this.attributeReallocationPoints);
      player.setSkillTreeReallocationPoints(this.skillTreeReallocationPoints);
      player.setSkillReallocationPoints(this.skillReallocationPoints);
      player.setUnlockedItems(this.unlockedItems);
      player.setClass(profess);

      for (int slot : this.boundSkills.keySet()) {
         player.bindSkill(slot, profess.getSkill(this.boundSkills.get(slot)));
      }

      this.skillLevels.forEach(player::setSkillLevel);
      this.attributeLevels.forEach((id, pts) -> player.getAttributes().getInstance(id).setBase(pts));
      player.setSkillTreePoints("global", this.skillTreePoints.getOrDefault("global", 0));

      for (SkillTree skillTree : profess.getSkillTrees()) {
         player.setSkillTreePoints(skillTree.getId(), this.skillTreePoints.getOrDefault(skillTree.getId(), 0));

         for (SkillTreeNode node : skillTree.getNodes()) {
            player.setNodeLevel(node, this.nodeLevels.getOrDefault(node.getFullId(), 0));
         }

         skillTree.setupNodeStates(player);
      }

      this.nodeTimesClaimed.forEach((str, val) -> player.setClaims(str, val));
      player.unloadClassInfo(profess);
      player.getPlayer().setHealth(MMOCoreUtils.fixResource(this.health, player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue()));
      player.setHealth(this.health);
      player.setMana(this.mana);
      player.setStellium(this.stellium);
      player.setStamina(this.stamina);
      player.applyTemporaryTriggers();
      player.getStats().updateStats();
   }
}
