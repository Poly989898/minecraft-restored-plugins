package net.Indyuce.mmocore.player;

import io.lumine.mythic.lib.version.Attributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class DefaultPlayerData implements ClassDataContainer {
   private final int level;
   private final int classPoints;
   private final int skillPoints;
   private final int attributePoints;
   private final int attrReallocPoints;
   private final int skillReallocPoints;
   private final int skillTreeReallocPoints;
   private final double health;
   private final double mana;
   private final double stamina;
   private final double stellium;
   public static final DefaultPlayerData DEFAULT = new DefaultPlayerData(1, 0, 0, 0, 0, 0, 0, 20.0, 0.0, 0.0, 0.0);

   public DefaultPlayerData(ConfigurationSection config) {
      this.level = config.getInt("level", 1);
      this.classPoints = config.getInt("class-points");
      this.skillPoints = config.getInt("skill-points");
      this.attributePoints = config.getInt("attribute-points");
      this.attrReallocPoints = config.getInt("attribute-realloc-points");
      this.skillReallocPoints = config.getInt("skill-realloc-points", 0);
      this.skillTreeReallocPoints = config.getInt("skill-tree-realloc-points", 0);
      this.health = config.getDouble("health", 20.0);
      this.mana = config.getDouble("mana", 20.0);
      this.stamina = config.getDouble("stamina", 20.0);
      this.stellium = config.getDouble("stellium", 20.0);
   }

   public DefaultPlayerData(
      int level,
      int classPoints,
      int skillPoints,
      int attributePoints,
      int attrReallocPoints,
      int skillReallocPoints,
      int skillTreeReallocPoints,
      double health,
      double mana,
      double stamina,
      double stellium
   ) {
      this.level = level;
      this.classPoints = classPoints;
      this.skillPoints = skillPoints;
      this.attributePoints = attributePoints;
      this.attrReallocPoints = attrReallocPoints;
      this.skillReallocPoints = skillReallocPoints;
      this.skillTreeReallocPoints = skillTreeReallocPoints;
      this.health = health;
      this.mana = mana;
      this.stamina = stamina;
      this.stellium = stellium;
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   @Override
   public double getExperience() {
      return 0.0;
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
   public double getStamina() {
      return this.stamina;
   }

   @Override
   public double getStellium() {
      return this.stellium;
   }

   @Override
   public int getSkillPoints() {
      return this.skillPoints;
   }

   public int getClassPoints() {
      return this.classPoints;
   }

   @Override
   public int getAttributePoints() {
      return this.attributePoints;
   }

   @Override
   public int getAttributeReallocationPoints() {
      return this.attrReallocPoints;
   }

   @Override
   public int getSkillReallocationPoints() {
      return this.skillReallocPoints;
   }

   @Override
   public int getSkillTreeReallocationPoints() {
      return this.skillTreeReallocPoints;
   }

   @Override
   public Map<String, Integer> mapSkillLevels() {
      return new HashMap<>();
   }

   @Override
   public Map<String, Integer> mapSkillTreePoints() {
      return new HashMap<>();
   }

   @Override
   public Map<String, Integer> getNodeLevels() {
      return new HashMap<>();
   }

   @Override
   public Map<String, Integer> getNodeTimesClaimed() {
      return new HashMap<>();
   }

   @Override
   public Set<String> getUnlockedItems() {
      return new HashSet<>();
   }

   @Override
   public Map<String, Integer> mapAttributeLevels() {
      return new HashMap<>();
   }

   @Override
   public Map<Integer, String> mapBoundSkills() {
      return new HashMap<>();
   }

   public void apply(PlayerData player) {
      player.setLevel(this.level);
      player.setExperience(0.0);
      player.setClassPoints(this.classPoints);
      player.setSkillPoints(this.skillPoints);
      player.setAttributePoints(this.attributePoints);
      player.setAttributeReallocationPoints(this.attrReallocPoints);
      player.setSkillTreeReallocationPoints(this.skillTreeReallocPoints);
      player.setSkillReallocationPoints(this.skillReallocPoints);
      if (player.isOnline()) {
         player.getPlayer().setHealth(Math.min(this.health, player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue()));
      }

      player.setMana(this.mana);
      player.setStamina(this.stamina);
      player.setStellium(this.stellium);
   }
}
