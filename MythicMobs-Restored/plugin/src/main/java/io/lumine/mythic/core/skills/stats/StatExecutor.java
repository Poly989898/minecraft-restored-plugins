package io.lumine.mythic.core.skills.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.stats.StatManager;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import io.lumine.mythic.bukkit.events.MythicStatsRegistrationEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.stats.types.CompositeStat;
import io.lumine.mythic.core.skills.stats.types.DamageBonusStat;
import io.lumine.mythic.core.skills.stats.types.DamageModifierStat;
import io.lumine.mythic.core.skills.stats.types.MutatorStat;
import io.lumine.mythic.core.skills.stats.types.ProcStat;
import io.lumine.mythic.core.skills.stats.types.StaticStat;
import io.lumine.mythic.core.skills.stats.types.VanillaAttributeStat;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;

@Getter
public class StatExecutor extends ReloadableModule<MythicBukkit> implements StatManager {
   private static final Map<String, Class<? extends StatType>> HARDCODED_STATS = new ConcurrentHashMap<>();
   private static final NodeListProp STATS = Property.NodeList(Scope.STATS, "");
   private static final StringProp STAT_TYPE = Property.String(Scope.STATS, "Type", "STATIC");
   private final Map<String, StatType> stats = Maps.newConcurrentMap();
   private final Collection<StatType> nonZeroBaseStats = Lists.newArrayList();

   public StatExecutor(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
      this.registerPrimaryStat(Stats.DODGE_NEGATION);
      this.registerPrimaryStat(Stats.BONUS_DAMAGE);
      this.registerPrimaryStat(Stats.CRITICAL_STRIKE_CHANCE);
      this.registerPrimaryStat(Stats.CRITICAL_STRIKE_DAMAGE);
      this.registerPrimaryStat(Stats.CRITICAL_STRIKE_RESILIENCE);
      this.registerPrimaryStat(Stats.PARRY_NEGATION);
      this.registerPrimaryStat(Stats.PERCENT_DAMAGE);
      this.registerPrimaryStat(Stats.DAMAGE_REDUCTION);
      this.registerPrimaryStat(Stats.DEFENSE);
      this.registerPrimaryStat(Stats.DODGE_CHANCE);
      this.registerPrimaryStat(Stats.HEALTH_REGENERATION);
      this.registerPrimaryStat(Stats.LIFESTEAL_CHANCE);
      this.registerPrimaryStat(Stats.LIFESTEAL_POWER);
      this.registerPrimaryStat(Stats.PARRY_CHANCE);
      this.registerPrimaryStat(Stats.PARRY_POWER);
      this.registerPrimaryStat(Stats.PARRY_COUNTERATTACK);
      this.registerPrimaryStat(Stats.SKILL_POWER);
      this.registerPrimaryStat(Stats.LOOT_BIAS);
      this.registerPrimaryStat(Stats.ATTACK_DAMAGE);
      this.registerPrimaryStat(Stats.ATTACK_SPEED);
      this.registerPrimaryStat(Stats.HEALTH);
      this.registerPrimaryStat(Stats.MOVEMENT_SPEED);
      this.registerPrimaryStat(Stats.SCALE);
      this.registerPrimaryStat(Stats.STEP_HEIGHT);
      this.registerPrimaryStat(Stats.ARMOR);
      this.registerPrimaryStat(Stats.ARMOR_TOUGHNESS);
      this.registerPrimaryStat(Stats.BURNING_TIME);
      this.registerPrimaryStat(Stats.EXPLOSION_KNOCKBACK_RESISTANCE);
      this.registerPrimaryStat(Stats.FALL_DAMAGE_MULTIPLIER);
      this.registerPrimaryStat(Stats.GRAVITY);
      this.registerPrimaryStat(Stats.JUMP_STRENGTH);
      this.registerPrimaryStat(Stats.KNOCKBACK_RESISTANCE);
      this.registerPrimaryStat(Stats.MOVEMENT_EFFICIENCY);
      this.registerPrimaryStat(Stats.OXYGEN_BONUS);
      this.registerPrimaryStat(Stats.SAFE_FALL_DISTANCE);
      this.registerPrimaryStat(Stats.SNEAKING_SPEED);
      this.registerPrimaryStat(Stats.WATER_MOVEMENT_EFFICIENCY);
      this.registerPrimaryStat(Stats.BLOCK_BREAK_SPEED);
      this.registerPrimaryStat(Stats.BLOCK_INTERACTION_RANGE);
      this.registerPrimaryStat(Stats.ENTITY_INTERACTION_RANGE);
      Events.call(new MythicStatsRegistrationEvent(this));

      for (Attribute attribute : Registry.ATTRIBUTE) {
         this.registerVanillaAttributeStat(attribute);
      }

      for (PackExecutor.ConfigFile configFile : this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.STAT)) {
         for (String node : STATS.fget(configFile.file())) {
            this.registerStat(configFile.file(), node);
         }
      }

      Events.subscribe(MythicStatChangeEvent.class).handler(event -> {
         if (event.getStat() instanceof AttributeMappedStat attributeMappedStat) {
            AbstractEntity entity = event.getCaster().getEntity();
            if (entity == null) {
               return;
            }

            double newValue = event.getNewValue();
            Entity bukkitEntity = entity.getBukkitEntity();
            if (bukkitEntity == null) {
               attributeMappedStat.applyAttribute(entity, newValue);
            } else {
               Schedulers.ensureSync(bukkitEntity, () -> attributeMappedStat.applyAttribute(entity, newValue));
            }
         }
      }).bindWith(this);
      this.loadStats();
   }

   @Override
   public void unload() {
      for (StatType stat : this.stats.values()) {
         stat.terminate();
      }

      this.stats.clear();
      this.nonZeroBaseStats.clear();
   }

   public void registerPrimaryStat(StatType type) {
      if (!this.stats.containsKey(type.getPropertyNode())) {
         this.stats.put(type.getPropertyNode(), type);
      }
   }

   private void registerVanillaAttributeStat(Attribute attribute) {
      VanillaAttributeStat stat = new VanillaAttributeStat(attribute);
      String key = stat.getPropertyNode();
      if (!this.stats.containsKey(key) && !this.isAttributeMapped(key)) {
         if (stat.getConfig().getString("Type", "STATIC").equalsIgnoreCase("STATIC")) {
            this.stats.put(key, stat);
         }
      }
   }

   private boolean isAttributeMapped(String key) {
      BukkitAttribute descriptor = BukkitAttribute.get(key);
      if (descriptor == null) {
         return false;
      } else {
         for (String existing : this.stats.keySet()) {
            if (BukkitAttribute.get(existing) == descriptor) {
               return true;
            }
         }

         return false;
      }
   }

   private void registerStat(File file, String key) {
      if (!this.stats.containsKey(key)) {
         String type = STAT_TYPE.fget(file, key);
         StatType stat = null;
         String toUpperCase = type.toUpperCase();

         stat = (switch (toUpperCase) {
            case "COMPOSITE" -> new CompositeStat(file, key);
            case "DAMAGE_MODIFIER" -> new DamageModifierStat(file, key);
            case "DAMAGE_BONUS" -> new DamageBonusStat(file, key);
            case "PROC" -> new ProcStat(file, key);
            case "MUTATOR" -> new MutatorStat(file, key);
            default -> new StaticStat(file, key);
         });
         if (stat != null) {
            this.stats.put(key, stat);
         }
      }
   }

   private void loadStats() {
      this.stats.values().forEach(stat -> {
         stat.initialize(this);
         if (stat.getBaseValue() != 0.0 || stat.isAlwaysActive()) {
            this.nonZeroBaseStats.add(stat);
         }
      });
   }

   @Override
   public Optional<StatType> getStat(String statName) {
      return Optional.ofNullable(this.stats.getOrDefault(statName, null));
   }

   public StatType getStatOrRegisterStatic(String statName) {
      return this.stats.compute(statName, (key, stat) -> (stat == null ? new StaticStat(key) : stat));
   }

   @Override
   public Optional<StatRegistry> getStatRegistry(AbstractEntity entity) {
      if (entity == null) {
         return Optional.empty();
      } else if (entity.isPlayer()) {
         PlayerData profile = this.getPlugin().getPlayerManager().getProfile(entity.asPlayer());
         if (profile == null) {
            return Optional.empty();
         } else {
            StatRegistry registry = profile.getStatRegistry();
            return Optional.of(registry);
         }
      } else {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(entity);
         return am == null ? Optional.empty() : Optional.of(am.getStatRegistry());
      }
   }

   public Optional<StatSnapshot> getStatRegistryAsSnapshot(AbstractEntity entity) {
      if (entity == null) {
         return Optional.empty();
      } else if (entity.isPlayer()) {
         PlayerData profile = this.getPlugin().getPlayerManager().getProfile(entity.asPlayer());
         if (profile == null) {
            return Optional.empty();
         } else {
            StatRegistry registry = profile.getStatRegistry();
            return Optional.of(registry);
         }
      } else {
         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(entity);
         return am == null ? Optional.empty() : Optional.of(am.getStatRegistry());
      }
   }
}
