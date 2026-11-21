package io.lumine.mythic.core.skills.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatManager;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StatRegistry implements StatSnapshot {
   private static final TreeSet<StatRegistry.StatMap> EMPTY_SET = Sets.newTreeSet();
   @Getter
   private final StatManager manager;
   @Getter
   private final SkillCaster holder;
   private final Map<StatType, StatRegistry.StatMap> stats = Maps.newConcurrentMap();
   @Getter
   private final Map<SkillTrigger, List<StatRegistry.StatMap>> statsByApplication = Maps.newConcurrentMap();
   @Getter
   private final Map<SkillTrigger, TreeSet<StatRegistry.StatMap>> preProcessedStats = Maps.newConcurrentMap();
   @Getter
   private final Map<SkillTrigger, TreeSet<StatRegistry.StatMap>> postProcessedStats = Maps.newConcurrentMap();

   public StatRegistry(StatManager manager, SkillCaster holder) {
      this.manager = manager;
      this.holder = holder;

      for (StatType stat : manager.getNonZeroBaseStats()) {
         this.compute(stat);
      }
   }

   private StatRegistry.StatMap compute(StatType type) {
      StatRegistry.StatMap stat = this.stats.get(type);
      if (stat == null) {
         stat = new StatRegistry.StatMap(this, type);
         this.stats.put(type, stat);
         stat.init();
         if (type.getExecutionPoint() == StatExecution.PRE) {
            for (SkillTrigger app : type.getApplicableTriggers()) {
               this.preProcessedStats.computeIfAbsent(app, c -> Sets.newTreeSet()).add(stat);
            }
         } else if (type.getExecutionPoint() == StatExecution.POST) {
            for (SkillTrigger app : type.getApplicableTriggers()) {
               this.postProcessedStats.computeIfAbsent(app, c -> Sets.newTreeSet()).add(stat);
            }
         }
      }

      return stat;
   }

   @Override
   public double get(StatType type) {
      return this.compute(type).get();
   }

   public void putBaseValue(StatType type, double value) {
      this.compute(type).setBaseValue(value);
   }

   public void putBaseValueSilently(StatType type, double value) {
      this.compute(type).setBaseValueSilently(value);
   }

   public void putValue(StatType type, StatSource source, StatModifierType operation, double value) {
      this.compute(type).put(source, operation, value, true);
   }

   public void putValueSilently(StatType type, StatSource source, StatModifierType operation, double value) {
      this.compute(type).put(source, operation, value, false);
   }

   public void removeValue(StatType type, StatSource source) {
      StatRegistry.StatMap stat = this.stats.get(type);
      if (stat != null) {
         stat.remove(source);
      }
   }

   public void removeValueSilently(StatType type, StatSource source) {
      StatRegistry.StatMap stat = this.stats.get(type);
      if (stat != null) {
         stat.remove(source, false);
      }
   }

   public Collection<StatType> getApplicableStats() {
      return Collections.unmodifiableCollection(this.stats.keySet());
   }

   public Map<StatType, StatRegistry.StatMap> getStatMaps() {
      return this.stats;
   }

   public Collection<StatType> getApplicableStats(SkillTrigger trigger) {
      return this.stats.keySet().stream().filter(stat -> stat.isApplicable(trigger)).collect(Collectors.toUnmodifiableSet());
   }

   public Collection<StatRegistry.StatMap> getPreProcessStats(SkillTrigger trigger) {
      return this.preProcessedStats.getOrDefault(trigger, EMPTY_SET);
   }

   public Collection<StatRegistry.StatMap> getPostProcessStats(SkillTrigger trigger) {
      return this.postProcessedStats.getOrDefault(trigger, EMPTY_SET);
   }

   public Optional<StatRegistry.StatMap> getStatData(StatType type) {
      return Optional.ofNullable(this.stats.getOrDefault(type, null));
   }

   public boolean isEmpty() {
      return this.stats.isEmpty();
   }

   public HashMap<String, Double> getBaseStats() {
      HashMap<String, Double> map = Maps.newHashMap();

      for (StatRegistry.StatMap statMap : this.stats.values()) {
         StatType type = statMap.getType();
         double baseValue = statMap.getBaseValue();
         map.put(type.getKey(), baseValue);
      }

      return map;
   }

   public void updateDirtyStats() {
      this.stats.values().forEach(StatRegistry.StatMap::updateIfDirty);
   }

   public void refresh() {
      Map<StatType, StatRegistry.StatMap> stats = Maps.newConcurrentMap();

      for (Entry<StatType, StatRegistry.StatMap> entry : this.stats.entrySet()) {
         String key = entry.getKey().getKey();
         StatRegistry.StatMap statMap = entry.getValue();
         Optional<StatType> maybeNewStat = this.manager.getStat(key);
         if (maybeNewStat.isEmpty()) {
            this.statsByApplication.forEach((skillTrigger, statMaps) -> {
               statMaps.remove(statMap);
               if (statMaps.isEmpty()) {
                  this.statsByApplication.remove(skillTrigger);
               }
            });
            this.preProcessedStats.forEach((skillTrigger, statMaps) -> {
               statMaps.remove(statMap);
               if (statMaps.isEmpty()) {
                  this.preProcessedStats.remove(skillTrigger);
               }
            });
            this.postProcessedStats.forEach((skillTrigger, statMaps) -> {
               statMaps.remove(statMap);
               if (statMaps.isEmpty()) {
                  this.postProcessedStats.remove(skillTrigger);
               }
            });
         } else {
            StatType newStat = maybeNewStat.get();
            statMap.setType(newStat);
            stats.put(newStat, statMap);
            statMap.handleReload();
            if (this.holder.getEntity() != null && this.holder.getEntity().isPlayer()) {
               statMap.setBaseValueSilently(newStat.getBaseValue(this));
            }
         }
      }

      this.stats.clear();
      this.stats.putAll(stats);
      this.updateDirtyStats();
   }

   public StatRegistrySnapshot getSnapshot() {
      return new StatRegistrySnapshot(this);
   }

   @Getter
   @ToString
   public class StatMap implements Comparable<StatRegistry.StatMap>, Supplier<Double> {
      @Setter
      private StatType type;
      @Setter
      private StatRegistry registry;
      private double baseValue = 1.0;
      private final Map<StatSource, Double> additives = Maps.newConcurrentMap();
      private final Map<StatSource, Double> additiveMultipliers = Maps.newConcurrentMap();
      private final Map<StatSource, Double> compoundMultipliers = Maps.newConcurrentMap();
      private final Map<StatSource, Double> setters = Maps.newConcurrentMap();
      @Setter
      private double cachedValue = 0.0;
      @Setter
      private boolean dirty = false;

      public StatMap(StatRegistry registry, StatType type) {
         this.type = type;
         this.registry = registry;
      }

      public void init() {
         double startingValue = this.type.getBaseValue(this.registry);
         this.setBaseValue(startingValue);
      }

      public void setBaseValue(double value) {
         this.baseValue = value;
         this.calculateCachedValue();
      }

      public void setBaseValueSilently(double value) {
         this.baseValue = value;
         this.dirty = true;
      }

      public void updateIfDirty() {
         if (this.dirty) {
            this.dirty = false;
            this.calculateCachedValue();
         }
      }

      public void calculateCachedValue() {
         double oldValue;
         double newValue;
         synchronized (this) {
            double value = this.additives.isEmpty()
               ? this.baseValue + 0.0
               : this.baseValue + this.additives.values().stream().mapToDouble(Double::doubleValue).sum();
            if (!this.additiveMultipliers.isEmpty()) {
               value *= 1.0 + this.additiveMultipliers.values().stream().mapToDouble(Double::doubleValue).sum();
            }

            if (!this.compoundMultipliers.isEmpty()) {
               for (Double v : this.compoundMultipliers.values()) {
                  value *= v;
               }
            }

            if (!this.setters.isEmpty()) {
               for (Double v : this.setters.values()) {
                  value = v;
               }
            }

            value = Numbers.clamp(value, this.type.getMinValue(), this.type.getMaxValue());
            if (value == this.cachedValue) {
               return;
            }

            oldValue = this.cachedValue;
            this.cachedValue = value;
            newValue = value;
         }

         try {
            Events.call(new MythicStatChangeEvent(StatRegistry.this.holder, this.registry, this.type, oldValue, newValue));
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }

      public void put(StatSource source, StatModifierType operation, double value) {
         this.put(source, operation, value, true);
      }

      public void put(StatSource source, StatModifierType operation, double value, boolean refresh) {
         switch (operation) {
            case ADDITIVE:
               this.additives.put(source, value);
               break;
            case ADDITIVE_MULTIPLIER:
               this.additiveMultipliers.put(source, value);
               break;
            case COMPOUND_MULTIPLIER:
               this.compoundMultipliers.put(source, value);
               break;
            case SETTER:
               this.setters.put(source, value);
         }

         if (refresh) {
            this.calculateCachedValue();
         } else {
            this.dirty = true;
         }
      }

      public void remove(StatSource source) {
         this.remove(source, true);
      }

      public void remove(StatSource source, boolean refresh) {
         this.additives.remove(source);
         this.additiveMultipliers.remove(source);
         this.compoundMultipliers.remove(source);
         this.setters.remove(source);
         if (refresh) {
            this.calculateCachedValue();
         } else {
            this.dirty = true;
         }
      }

      public Double get() {
         return this.cachedValue;
      }

      public void handleReload() {
         this.additives.keySet().removeIf(source -> source.removeOnReload());
         this.additiveMultipliers.keySet().removeIf(source -> source.removeOnReload());
         this.compoundMultipliers.keySet().removeIf(source -> source.removeOnReload());
         this.setters.keySet().removeIf(source -> source.removeOnReload());
      }

      public int compareTo(StatRegistry.StatMap other) {
         int priorityComparison = Integer.compare(this.type.getPriority(), other.type.getPriority());
         return priorityComparison == 0 ? this.type.getKey().compareTo(other.type.getKey()) : priorityComparison;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof StatRegistry.StatMap other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getBaseValue(), other.getBaseValue()) != 0) {
            return false;
         } else if (Double.compare(this.getCachedValue(), other.getCachedValue()) != 0) {
            return false;
         } else if (this.isDirty() != other.isDirty()) {
            return false;
         } else {
            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null ? other$type == null : this$type.equals(other$type)) {
               Object this$registry = this.getRegistry();
               Object other$registry = other.getRegistry();
               if (this$registry == null ? other$registry == null : this$registry.equals(other$registry)) {
                  Object this$additives = this.getAdditives();
                  Object other$additives = other.getAdditives();
                  if (this$additives == null ? other$additives == null : this$additives.equals(other$additives)) {
                     Object this$additiveMultipliers = this.getAdditiveMultipliers();
                     Object other$additiveMultipliers = other.getAdditiveMultipliers();
                     if (this$additiveMultipliers == null ? other$additiveMultipliers == null : this$additiveMultipliers.equals(other$additiveMultipliers)) {
                        Object this$compoundMultipliers = this.getCompoundMultipliers();
                        Object other$compoundMultipliers = other.getCompoundMultipliers();
                        if (this$compoundMultipliers == null ? other$compoundMultipliers == null : this$compoundMultipliers.equals(other$compoundMultipliers)) {
                           Object this$setters = this.getSetters();
                           Object other$setters = other.getSetters();
                           return this$setters == null ? other$setters == null : this$setters.equals(other$setters);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof StatRegistry.StatMap;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $baseValue = Double.doubleToLongBits(this.getBaseValue());
         result = result * 59 + (int)($baseValue >>> 32 ^ $baseValue);
         long $cachedValue = Double.doubleToLongBits(this.getCachedValue());
         result = result * 59 + (int)($cachedValue >>> 32 ^ $cachedValue);
         result = result * 59 + (this.isDirty() ? 79 : 97);
         Object $type = this.getType();
         result = result * 59 + ($type == null ? 43 : $type.hashCode());
         Object $registry = this.getRegistry();
         result = result * 59 + ($registry == null ? 43 : $registry.hashCode());
         Object $additives = this.getAdditives();
         result = result * 59 + ($additives == null ? 43 : $additives.hashCode());
         Object $additiveMultipliers = this.getAdditiveMultipliers();
         result = result * 59 + ($additiveMultipliers == null ? 43 : $additiveMultipliers.hashCode());
         Object $compoundMultipliers = this.getCompoundMultipliers();
         result = result * 59 + ($compoundMultipliers == null ? 43 : $compoundMultipliers.hashCode());
         Object $setters = this.getSetters();
         return result * 59 + ($setters == null ? 43 : $setters.hashCode());
      }
   }
}
