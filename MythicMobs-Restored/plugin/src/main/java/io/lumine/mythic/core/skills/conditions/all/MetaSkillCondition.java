package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.mechanics.MetaSkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

@MythicCondition(
   author = "Lxlp",
   name = "skillCondition",
   aliases = {"metaSkillCondition"},
   description = "Casts a Metaskill that will determine if the condition should check or not"
)
public class MetaSkillCondition extends SkillCondition implements ISkillMetaComparisonCondition, ISkillMetaLocationComparisonCondition, SkillHolder {
   protected final MetaSkillMechanic metaSkillMechanic;
   protected final String internalName;
   public static final String DETERMINATION_KEY = "condition-outcome";

   public MetaSkillCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.internalName = mlc.getKey() + "@" + mlc.getFileName() + ":" + mlc.getLine();
      this.metaSkillMechanic = new MetaSkillMechanic(getPlugin().getSkillManager(), null, line, mlc);
   }

   @Override
   public String getInternalName() {
      return this.internalName;
   }

   @Override
   public Pack getPack() {
      return null;
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      return this.executeCheckLogic(meta, target);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractLocation target) {
      return this.executeCheckLogic(meta, target);
   }

   public boolean executeCheckLogic(SkillMetadata meta, Object target) {
      if (!this.metaSkillMechanic.hasSkill()) {
         return false;
      } else {
         SkillMetadata finalMeta = createConditionMetadata(meta);
         if (target instanceof AbstractEntity entity) {
            finalMeta.setEntityTarget(entity);
         } else if (target instanceof AbstractLocation location) {
            finalMeta.setLocationTarget(location);
         }

         MetaSkillMechanic.SkillCastOutput result = this.metaSkillMechanic.castWithOutput(finalMeta);
         if (result != null && result.result() == SkillResult.SUCCESS) {
            Optional<Object> outcome = result.dataOut().getMetadata("condition-outcome");
            return outcome.isPresent() && outcome.get() instanceof Boolean returnBoolean ? returnBoolean : false;
         } else {
            return false;
         }
      }
   }

   static SkillMetadata createConditionMetadata(SkillMetadata meta) {
      SkillMetadata clone = meta.deepClone();
      if (clone instanceof SkillMetadataImpl impl) {
         impl.setMetadata(new MetaSkillCondition.ConditionMetadataMap(impl.getMetadata()));
      } else {
         clone.setMetadata("condition-outcome", null);
      }

      return clone;
   }

   private static final class ConditionMetadataMap extends AbstractMap<String, Object> {
      private final Map<String, Object> shared;
      private final Map<String, Object> outcome = Collections.synchronizedMap(new HashMap<>(1));

      private ConditionMetadataMap(Map<String, Object> source) {
         this.shared = source instanceof MetaSkillCondition.ConditionMetadataMap scoped
            ? scoped.shared
            : Maps.filterKeys(source, key -> !"condition-outcome".equals(key));
      }

      private Map<String, Object> mapFor(Object key) {
         return "condition-outcome".equals(key) ? this.outcome : this.shared;
      }

      @Override
      public Object get(Object key) {
         return this.mapFor(key).get(key);
      }

      @Override
      public boolean containsKey(Object key) {
         return this.mapFor(key).containsKey(key);
      }

      public Object put(String key, Object value) {
         return this.mapFor(key).put(key, value);
      }

      @Override
      public Object remove(Object key) {
         return this.mapFor(key).remove(key);
      }

      @Override
      public void clear() {
         this.shared.clear();
         this.outcome.clear();
      }

      @Override
      public Set<Entry<String, Object>> entrySet() {
         return new AbstractSet<Entry<String, Object>>() {
            @Override
            public Iterator<Entry<String, Object>> iterator() {
               List<String> keys = new ArrayList<>(ConditionMetadataMap.this.shared.keySet());
               if (ConditionMetadataMap.this.outcome.containsKey("condition-outcome")) {
                  keys.add("condition-outcome");
               }

               final Iterator<String> iterator = keys.iterator();
               return new Iterator<Entry<String, Object>>() {
                  private String current;
                  private boolean canRemove;

                  @Override
                  public boolean hasNext() {
                     return iterator.hasNext();
                  }

                  public Entry<String, Object> next() {
                     this.current = iterator.next();
                     this.canRemove = true;
                     return new SimpleEntry<String, Object>(this.current, ConditionMetadataMap.this.get(this.current)) {
                        @Override
                        public Object setValue(Object value) {
                           Object previous = ConditionMetadataMap.this.put(this.getKey(), value);
                           super.setValue(value);
                           return previous;
                        }
                     };
                  }

                  @Override
                  public void remove() {
                     if (!this.canRemove) {
                        throw new IllegalStateException();
                     } else {
                        ConditionMetadataMap.this.remove(this.current);
                        this.canRemove = false;
                     }
                  }
               };
            }

            @Override
            public int size() {
               return ConditionMetadataMap.this.shared.size() + ConditionMetadataMap.this.outcome.size();
            }

            @Override
            public void clear() {
               ConditionMetadataMap.this.clear();
            }
         };
      }
   }
}
