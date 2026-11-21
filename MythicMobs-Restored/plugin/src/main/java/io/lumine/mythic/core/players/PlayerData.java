package io.lumine.mythic.core.players;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.auras.AuraPersistenceData;
import io.lumine.mythic.core.skills.cooldowns.ChargeRestoreOrder;
import io.lumine.mythic.core.skills.cooldowns.CooldownRegistry;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.variables.PlayerVariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableHolder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

public class PlayerData implements Profile, SkillCaster, VariableHolder {
   @Getter
   private UUID uniqueId;
   @Getter
   private String name;
   @Getter
   private long timestamp = System.currentTimeMillis();
   @Getter
   private PlayerVariableRegistry variables = new PlayerVariableRegistry();
   @Getter
   private transient AbstractPlayer entity;
   @Getter @Setter
   private transient boolean usingDamageSkill = false;
   @Getter
   private transient StatRegistry statRegistry;
   private transient CooldownRegistry skillCooldowns = new CooldownRegistry();
   @Getter
   private transient Collection<AbstractEntity> children = Sets.newConcurrentHashSet();
   @Getter
   private HashMap<String, Double> pity = new HashMap<>();
   @Getter
   private Map<String, AuraPersistenceData> persistentAuras = new ConcurrentHashMap<>();

   public PlayerData() {
   }

   public PlayerData(UUID uniqueId, String name) {
      this.uniqueId = uniqueId;
      this.name = name;
   }

   public void initialize(AbstractPlayer player) {
      this.entity = player;
      this.statRegistry = new StatRegistry(MythicBukkit.inst().getStatManager(), this);
      this.variables.setPlayerData(this);
   }

   private CooldownRegistry getSkillCooldownRegistry() {
      if (this.skillCooldowns == null) {
         this.skillCooldowns = new CooldownRegistry();
      }

      return this.skillCooldowns;
   }

   @Override
   public void setSkillCooldownMillis(AbstractSkill skill, double millis) {
      if (millis > 0.0) {
         this.getSkillCooldownRegistry().setCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), (long)millis);
      } else {
         this.getSkillCooldownRegistry().clearCooldown(skill.getUuid());
      }
   }

   @Override
   public double getSkillCooldownRemainingMillis(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getRemainingCooldownMillis(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   @Override
   public boolean isSkillOnCooldown(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().isOnCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public boolean isSkillRecharging(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().isRecharging(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public int getAvailableSkillCharges(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getAvailableCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public int getOverflowSkillCharges(AbstractSkill skill) {
      return this.getSkillCooldownRegistry().getOverflowCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
   }

   public boolean consumeSkillCharge(AbstractSkill skill, long cooldownMillis) {
      return this.getSkillCooldownRegistry().consumeCharge(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), cooldownMillis);
   }

   public void addSkillCharges(AbstractSkill skill, int amount, ChargeRestoreOrder order, boolean overflow) {
      this.getSkillCooldownRegistry().addCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), amount, order, overflow);
   }

   public void clearSkillCooldowns() {
      this.getSkillCooldownRegistry().purgeExpired();
      this.skillCooldowns = new CooldownRegistry();
   }

   @Override
   public void addChild(AbstractEntity entity) {
      this.children.add(entity);
   }

   @Override
   public void removeChild(AbstractEntity entity) {
      this.children.remove(entity);
   }

   public void addPity(double amount, String category) {
      this.pity.put(category, this.getPity(category) + amount);
   }

   public void resetPity(String category) {
      this.pity.remove(category);
   }

   public void setPity(double amount, String category) {
      this.pity.put(category, amount);
   }

   public void removePity(double amount, String category) {
      this.pity.put(category, this.getPity(category) - amount);
   }

   public double getPity(String category) {
      return this.pity.getOrDefault(category, 0.0);
   }

   public void savePersistentAura(AuraPersistenceData data) {
      if (data != null && data.id != null && !data.id.isEmpty()) {
         this.persistentAuras.put(data.id, data);
      }
   }

   public void removePersistentAura(String id) {
      if (id != null && !id.isEmpty()) {
         this.persistentAuras.remove(id);
      }
   }
}
