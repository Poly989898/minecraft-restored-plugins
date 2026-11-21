package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.items.CustomModelDataValue;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.UtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 !2\u00020\u0001:\u0001!B)\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\b\u0010\tB\u0011\b\u0016\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\b\u0010\fJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0016\u001a\u00020\u0017J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0017J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0015\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J/\u0010\u001b\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u00122\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001e\u001a\u00020\u001fHÖ\u0081\u0004J\n\u0010 \u001a\u00020\u0004HÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001d\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStates;", "", "conditions", "", "", "states", "", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "<init>", "(Ljava/util/List;Ljava/util/Map;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getConditions", "()Ljava/util/List;", "getStates", "()Ljava/util/Map;", "canCycle", "", "player", "Lorg/bukkit/entity/Player;", "currentState", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "nextState", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class FurnitureStates {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<String> conditions;
   @NotNull
   private final Map<String, FurnitureState> states;
   @NotNull
   private static final SpelExpressionParser PARSER = new SpelExpressionParser();
   @NotNull
   private static final NamespacedKey STATE_KEY;

   public FurnitureStates(@NotNull List<String> conditions, @NotNull Map<String, FurnitureState> states) {
      super();
      this.conditions = conditions;
      this.states = states;
   }

   @NotNull
   public final List<String> getConditions() {
      return this.conditions;
   }

   @NotNull
   public final Map<String, FurnitureState> getStates() {
      return this.states;
   }

   public FurnitureStates(@NotNull ConfigurationSection section) {
      this(NexoYamlKt.stringListOrSingle(section, "condition", (String)null), Companion.buildStates(section));
   }

   public final boolean canCycle(@NotNull Player player) {
      return this.conditions.isEmpty() || Companion.evaluateConditions$com_nexomc_core(this.conditions, player);
   }

   @Nullable
   public final FurnitureState currentState(@NotNull ItemDisplay baseEntity) {
      String str = baseEntity.getPersistentDataContainer().get(STATE_KEY, PersistentDataType.STRING);
      if (str == null) {
         str = "";
      }

      String stateId = str;
      FurnitureState furnitureState = this.states.get(stateId);
      if (furnitureState == null) {
         furnitureState = (FurnitureState)CollectionsKt.firstOrNull((Iterable)this.states.values());
         if (furnitureState != null) {
            FurnitureState furnitureState2 = furnitureState;
            int value = 0;
            baseEntity.getPersistentDataContainer().set(STATE_KEY, PersistentDataType.STRING, furnitureState2.getId());
            furnitureState = furnitureState2;
         } else {
            furnitureState = null;
         }
      }

      return furnitureState;
   }

   @Nullable
   public final FurnitureState nextState(@NotNull Player player, @NotNull ItemDisplay baseEntity) {
      int value;
      com.nexomc.nexo.mechanics.furniture.states.FurnitureState furnitureState;
      Set states = this.states.entrySet();
      String str = baseEntity.getPersistentDataContainer().get(STATE_KEY, PersistentDataType.STRING);
      if (str == null) {
         str = "";
      }

      String currentStateId = str;
      Iterable iterable = (Iterable)states;
      int value5 = 0;
      int value6 = 0;
      Iterator iterator = iterable.iterator();

      while(true) {
         if (!iterator.hasNext()) {
            value = -1;
            break;
         }

         Object item = iterator.next();
         if (value6 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Map.Entry it = (Map.Entry)item;
         int value2 = 0;
         if (Intrinsics.areEqual(it.getKey(), currentStateId)) {
            value = value6;
            break;
         }

         ++value6;
      }

      int currentIndex = value;
      Iterator iterator2 = ((Iterable)(new IntRange(1, states.size()))).iterator();

      while(true) {
         if (iterator2.hasNext()) {
            value6 = ((IntIterator)iterator2).nextInt();
            int value3 = 0;
            int index = (currentIndex + value6) % states.size();
            Object obj = ((Map.Entry)CollectionsKt.elementAt((Iterable)states, index)).getValue();
            FurnitureState it = (FurnitureState)obj;
            int value4 = 0;
            FurnitureState furnitureState2 = (FurnitureState)(it.canRun(player) ? obj : null);
            if (furnitureState2 == null) {
               continue;
            }

            furnitureState = furnitureState2;
            break;
         }

         furnitureState = null;
         break;
      }

      return furnitureState;
   }

   @NotNull
   public final List<String> component1() {
      return this.conditions;
   }

   @NotNull
   public final Map<String, FurnitureState> component2() {
      return this.states;
   }

   @NotNull
   public final FurnitureStates copy(@NotNull List<String> conditions, @NotNull Map<String, FurnitureState> states) {
      return new FurnitureStates(conditions, states);
   }

   @NotNull
   public String toString() {
      return "FurnitureStates(conditions=" + this.conditions + ", states=" + this.states + ")";
   }

   public int hashCode() {
      int result = this.conditions.hashCode();
      result = result * 31 + this.states.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FurnitureStates)) {
         return false;
      } else {
         FurnitureStates furnitureStates = (FurnitureStates)other;
         if (!Intrinsics.areEqual(this.conditions, furnitureStates.conditions)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.states, furnitureStates.states);
         }
      }
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:furniture_state");
      STATE_KEY = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J#\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0000¢\u0006\u0002\b\u0017J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u001e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u001d\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0000¢\u0006\u0002\b J\u001d\u0010!\u001a\u00020\u001e2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0000¢\u0006\u0002\b\"J \u0010#\u001a\u00020\u001e2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010$\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\u00070\u0007¢\u0006\u0002\b\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006&"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStates$Companion;", "", "<init>", "()V", "PARSER", "Lorg/springframework/expression/spel/standard/SpelExpressionParser;", "STATE_KEY", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getSTATE_KEY", "()Lorg/bukkit/NamespacedKey;", "buildStates", "", "", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "evaluateConditions", "", "conditions", "", "player", "Lorg/bukkit/entity/Player;", "evaluateConditions$com_nexomc_core", "currentState", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "cycleState", "", "cycleNext", "cycleNext$com_nexomc_core", "resetToEmpty", "resetToEmpty$com_nexomc_core", "applyTransition", "from", "to", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getSTATE_KEY() {
         return FurnitureStates.STATE_KEY;
      }

      private final Map<String, FurnitureState> buildStates(ConfigurationSection section) {
         Pair pair = TuplesKt.to(NexoYamlKt.stringOrNull(section, "reset_after"), NexoYamlKt.stringOrNull(section, "next_after"));
         String parentReset = (String)pair.component1();
         String parentNext = (String)pair.component2();
         Map map3 = NexoYamlKt.childSections(section);
         Map destination = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map3.size())));
         Iterable entrySet = (Iterable)map3.entrySet();
         Map map4 = destination;

         for(Object element : entrySet) {
            Map.Entry entry2 = (Map.Entry)element;
            int value = 0;
            Object key = entry2.getKey();
            Map.Entry it = (Map.Entry)element;
            Object obj = key;
            int value2 = 0;
            ConfigurationSection configurationSection = NexoYamlKt.clone((ConfigurationSection)it.getValue());
            map4.put(obj, configurationSection);
         }

         int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(map4.size()), 16);
         Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map4.entrySet()) {
            Object obj2 = entry.getKey();
            Object obj3 = entry.getValue();
            Map map = (Map)result;
            ConfigurationSection configurationSection2 = (ConfigurationSection)obj3;
            String str = (String)obj2;
            Map map2 = map;
            int value3 = 0;
            if (!configurationSection2.contains("reset_after")) {
               configurationSection2.set("reset_after", parentReset);
            }

            if (!configurationSection2.contains("next_after")) {
               configurationSection2.set("next_after", parentNext);
            }

            String str2 = "type";
            Enum enumValue2 = EnumUtils.getEnum(FurnitureStateType.class, NexoYamlKt.stringOrNull(configurationSection2, str2));
            FurnitureState furnitureState;
            switch ((FurnitureStateType)enumValue2) {
               case ITEM_MODEL -> furnitureState = new ItemModelState(configurationSection2);
               case FURNITURE -> furnitureState = new MechanicState(configurationSection2);
               case CUSTOM_MODEL_DATA -> furnitureState = new CustomModelDataState(configurationSection2);
               case MODELENGINE -> furnitureState = new ModelEngineState(configurationSection2);
               case ANIMATION -> furnitureState = new AnimationState(configurationSection2);
               case MODELENGINE_ANIM -> furnitureState = new ModelEngineAnimState(configurationSection2);
               case DEFAULT -> furnitureState = new EmptyState(configurationSection2);
               default -> throw new NoWhenBranchMatchedException();
            }

            FurnitureState furnitureState2 = furnitureState;
            map2.put(obj2, furnitureState2);
         }

         capacity = 0;
         Collection collection = result.values();
         if (!(CollectionsKt.firstOrNull((Iterable)collection) instanceof EmptyState)) {
            ((Map)result).put("", new EmptyState(parentReset != null ? DurationUtilsKt.toDuration(parentReset, (DurationUnit)null) : null, parentNext != null ? DurationUtilsKt.toDuration(parentNext, (DurationUnit)null) : null));
         }

         return (Map)result;
      }

      public final boolean evaluateConditions$com_nexomc_core(@NotNull List<String> conditions, @NotNull Player player) {
         StandardEvaluationContext context = new StandardEvaluationContext(player);
         context.setVariable("player", player);
         context.setVariable("server", Bukkit.getServer());
         Iterable iterable = (Iterable)conditions;
         boolean flag;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = true;
         } else {
            for(Object element : iterable) {
               String condition = (String)element;
               int value = 0;
               Companion companion = FurnitureStates.Companion;

               Object obj2;
               try {
                  int value2 = 0;
                  Boolean flag2 = FurnitureStates.PARSER.parseExpression(condition).getValue((EvaluationContext)context, Boolean.TYPE);
                  if (flag2 == null) {
                     return false;
                  }

                  obj2 = ResultShim.ok(flag2);
               } catch (Throwable e) {
                  obj2 = ResultShim.ok(ResultKt.createFailure(e));
               }

               Object obj = UtilsKt.printOnFailure(obj2, true);
               Boolean flag3 = true;
               if (!(Boolean)(ResultShim.isFailure(obj) ? flag3 : obj)) {
                  flag = false;
                  return flag;
               }
            }

            flag = true;
         }

         return flag;
      }

      @Nullable
      public final FurnitureState currentState(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         FurnitureStates furnitureStates = mechanic.getStates();
         Map map = furnitureStates != null ? furnitureStates.getStates() : null;
         if (map == null || map.isEmpty()) {
            return null;
         } else {
            String str = baseEntity.getPersistentDataContainer().get(this.getSTATE_KEY(), PersistentDataType.STRING);
            if (str == null) {
               str = "";
            }

            String stateId = str;
            FurnitureState furnitureState = mechanic.getStates().getStates().get(stateId);
            if (furnitureState == null) {
               furnitureState = (FurnitureState)CollectionsKt.firstOrNull((Iterable)mechanic.getStates().getStates().values());
               if (furnitureState != null) {
                  FurnitureState furnitureState2 = furnitureState;
                  int value = 0;
                  baseEntity.getPersistentDataContainer().set(FurnitureStates.Companion.getSTATE_KEY(), PersistentDataType.STRING, furnitureState2.getId());
                  furnitureState = furnitureState2;
               } else {
                  furnitureState = null;
               }
            }

            return furnitureState;
         }
      }

      public final void cycleState(@NotNull Player player, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         int value;
         FurnitureStates furnitureStates = mechanic.getStates();
         if (furnitureStates != null) {
            FurnitureStates it = furnitureStates;
            int value2 = 0;
            furnitureStates = it.canCycle(player) ? it : null;
            if (furnitureStates != null) {
               Map map = furnitureStates.getStates();
               if (map != null) {
                  Set entrySet = map.entrySet();
                  if (entrySet != null) {
                     Collection collection = (Collection)entrySet;
                     Collection collection2;
                     if (collection.isEmpty()) {
                        int value3 = 0;
                        collection2 = null;
                     } else {
                        collection2 = collection;
                     }

                     Set set = (Set)collection2;
                     if ((Set)collection2 != null) {
                        Set states = set;
                        String str = baseEntity.getPersistentDataContainer().get(this.getSTATE_KEY(), PersistentDataType.STRING);
                        if (str == null) {
                           str = "";
                        }

                        String currentStateId = str;
                        Iterable iterable = (Iterable)states;
                        value2 = 0;
                        int value7 = 0;
                        Iterator iterator = iterable.iterator();

                        while(true) {
                           if (!iterator.hasNext()) {
                              value = -1;
                              break;
                           }

                           Object item = iterator.next();
                           if (value7 < 0) {
                              CollectionsKt.throwIndexOverflow();
                           }

                           Map.Entry it_5 = (Map.Entry)item;
                           int value4 = 0;
                           if (Intrinsics.areEqual(it_5.getKey(), currentStateId)) {
                              value = value7;
                              break;
                           }

                           ++value7;
                        }

                        Integer number2 = value;
                        value2 = ((Number)number2).intValue();
                        value7 = 0;
                        Integer number = value2 >= 0 ? number2 : null;
                        if ((value2 >= 0 ? number2 : null) == null) {
                           return;
                        }

                        int currentIndex = number;
                        FurnitureState it_3 = (FurnitureState)((Map.Entry)CollectionsKt.elementAt((Iterable)states, currentIndex)).getValue();
                        iterator = ((Iterable)(new IntRange(1, states.size()))).iterator();

                        FurnitureState furnitureState;
                        while(true) {
                           if (!iterator.hasNext()) {
                              furnitureState = null;
                              break;
                           }

                           FurnitureState furnitureState2;
                           block: {
                              int offset = ((IntIterator)iterator).nextInt();
                              int value5 = 0;
                              int index = (currentIndex + offset) % states.size();
                              Map.Entry entry = (Map.Entry)CollectionsKt.elementAtOrNull((Iterable)states, index);
                              if (entry != null) {
                                 FurnitureState furnitureState3 = (FurnitureState)entry.getValue();
                                 if (furnitureState3 != null) {
                                    FurnitureState it_2 = furnitureState3;
                                    int value6 = 0;
                                    furnitureState2 = it_2.canRun(player) ? it_2 : null;
                                    break block;
                                 }
                              }

                              furnitureState2 = null;
                           }

                           FurnitureState furnitureState4 = furnitureState2;
                           if (furnitureState4 != null) {
                              furnitureState = furnitureState4;
                              break;
                           }
                        }

                        if (furnitureState == null) {
                           return;
                        }

                        FurnitureState nextState = furnitureState;
                        this.applyTransition(baseEntity, it_3, nextState);
                        return;
                     }
                  }
               }
            }
         }

      }

      public final void cycleNext$com_nexomc_core(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         int value;
         FurnitureStates furnitureStates = mechanic.getStates();
         if (furnitureStates != null) {
            Map map = furnitureStates.getStates();
            if (map != null) {
               Set entrySet = map.entrySet();
               if (entrySet != null) {
                  Set it = entrySet;
                  int value2 = 0;
                  entrySet = !((Collection)it).isEmpty() ? it : null;
                  if (entrySet != null) {
                     Set states = entrySet;
                     String str = baseEntity.getPersistentDataContainer().get(this.getSTATE_KEY(), PersistentDataType.STRING);
                     if (str == null) {
                        str = "";
                     }

                     String currentStateId = str;
                     Iterable iterable = (Iterable)states;
                     int value4 = 0;
                     int index = 0;
                     Iterator iterator = iterable.iterator();

                     while(true) {
                        if (!iterator.hasNext()) {
                           value = -1;
                           break;
                        }

                        Object item = iterator.next();
                        if (index < 0) {
                           CollectionsKt.throwIndexOverflow();
                        }

                        Map.Entry it_1 = (Map.Entry)item;
                        int value3 = 0;
                        if (Intrinsics.areEqual(it_1.getKey(), currentStateId)) {
                           value = index;
                           break;
                        }

                        ++index;
                     }

                     Integer nextState = value;
                     value4 = ((Number)nextState).intValue();
                     index = 0;
                     Integer number = value4 >= 0 ? nextState : null;
                     if ((value4 >= 0 ? nextState : null) != null) {
                        int currentIndex = number;
                        FurnitureState currentState = (FurnitureState)((Map.Entry)CollectionsKt.elementAt((Iterable)states, currentIndex)).getValue();
                        FurnitureState furnitureState = (FurnitureState)((Map.Entry)CollectionsKt.elementAt((Iterable)states, (currentIndex + 1) % states.size())).getValue();
                        this.applyTransition(baseEntity, currentState, furnitureState);
                        return;
                     }

                     return;
                  }
               }
            }
         }

      }

      public final void resetToEmpty$com_nexomc_core(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         FurnitureStates furnitureStates = mechanic.getStates();
         if (furnitureStates != null) {
            Map map = furnitureStates.getStates();
            if (map != null) {
               Map states = map;
               String str = baseEntity.getPersistentDataContainer().get(this.getSTATE_KEY(), PersistentDataType.STRING);
               if (str == null) {
                  return;
               }

               String currentStateId = str;
               if (Intrinsics.areEqual(currentStateId, "")) {
                  return;
               }

               FurnitureState furnitureState = (FurnitureState)states.get(currentStateId);
               if (furnitureState == null) {
                  return;
               }

               FurnitureState currentState = furnitureState;
               furnitureState = (FurnitureState)states.get("");
               if (furnitureState == null) {
                  return;
               }

               FurnitureState emptyState = furnitureState;
               this.applyTransition(baseEntity, currentState, emptyState);
               return;
            }
         }

      }

      private final void applyTransition(ItemDisplay baseEntity, FurnitureState from, FurnitureState to) {
         baseEntity.getPersistentDataContainer().set(this.getSTATE_KEY(), PersistentDataType.STRING, to.getId());
         from.unsetState(baseEntity);
         to.setState(baseEntity);
      }
   }
}
