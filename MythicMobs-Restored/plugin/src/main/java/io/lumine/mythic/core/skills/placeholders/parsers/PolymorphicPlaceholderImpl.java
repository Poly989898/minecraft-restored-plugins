package io.lumine.mythic.core.skills.placeholders.parsers;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.placeholders.IPlaceholder;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.types.ListVariable;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class PolymorphicPlaceholderImpl implements PolymorphicPlaceholder {
   protected final String value;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderInt> intGetter;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderFloat> floatGetter;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderLong> longGetter;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderDouble> doubleGetter;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderBoolean> booleanGetter;
   private static final PolymorphicPlaceholderImpl.PlaceholderGetter<PlaceholderVector> vectorGetter;
   private final Map<VariableType, IPlaceholder> cache = new ConcurrentHashMap<>();
   private static final Map<VariableType, PolymorphicPlaceholderImpl.PlaceholderGetter<IPlaceholder>> resolutionMap;

   public PolymorphicPlaceholderImpl(String input) {
      this.value = input;
   }

   @Override
   public boolean isStatic() {
      return false;
   }

   @Override
   public boolean isPresent() {
      return this.value != null && !this.value.isEmpty();
   }

   @Override
   public String serialize() {
      return this.value;
   }

   public PolymorphicPlaceholderImpl cache(VariableType type) {
      PolymorphicPlaceholderImpl.PlaceholderGetter<IPlaceholder> resolver = resolutionMap.get(type);
      if (resolver == null) {
         return this;
      } else {
         this.cache.put(type, resolver.get(this.value));
         return this;
      }
   }

   @Override
   public String originalValue() {
      return this.value;
   }

   private <T extends IPlaceholder> T getOrCompute(VariableType type, Supplier<T> factory) {
      IPlaceholder cached = this.cache.get(type);
      if (cached == null) {
         this.cache.putIfAbsent(type, factory.get());
         cached = this.cache.get(type);
      }

      return (T)cached;
   }

   @Override
   public String getString(PlaceholderContext context) {
      return this.getOrCompute(VariableType.STRING, () -> PlaceholderString.of(this.value)).get(context);
   }

   @Override
   public int getInt(PlaceholderContext context) {
      return this.getOrCompute(VariableType.INTEGER, () -> intGetter.get(this.value)).get(context);
   }

   @Override
   public float getFloat(PlaceholderContext context) {
      return this.getOrCompute(VariableType.FLOAT, () -> floatGetter.get(this.value)).get(context);
   }

   @Override
   public double getDouble(PlaceholderContext context) {
      return this.getOrCompute(VariableType.DOUBLE, () -> doubleGetter.get(this.value)).get(context);
   }

   @Override
   public Long getLong(PlaceholderContext context) {
      return this.getOrCompute(VariableType.LONG, () -> longGetter.get(this.value)).get(context);
   }

   @Override
   public boolean getBoolean(PlaceholderContext context) {
      return this.getOrCompute(VariableType.BOOLEAN, () -> booleanGetter.get(this.value)).get(context);
   }

   @Override
   public AbstractVector getVector(PlaceholderContext context) {
      return this.getOrCompute(VariableType.VECTOR, () -> vectorGetter.get(this.value)).get(context);
   }

   @Override
   public Instant getTime(PlaceholderContext context) {
      return this.getOrCompute(VariableType.TIME, () -> PlaceholderTime.of(this.value)).get(context);
   }

   @Override
   public List<String> getList(PlaceholderContext context) {
      return ListVariable.fromString(this.getOrCompute(VariableType.STRING, () -> PlaceholderString.of(this.value)).get(context));
   }

   @Override
   public Map<String, String> getMap(PlaceholderContext context) {
      return MapVariable.fromString(this.getOrCompute(VariableType.STRING, () -> PlaceholderString.of(this.value)).get(context));
   }

   static {
      if (MythicBukkit.isVolatile()) {
         intGetter = PlaceholderInt::of;
         floatGetter = PlaceholderFloat::of;
         doubleGetter = PlaceholderDouble::of;
         longGetter = PlaceholderLong::of;
         booleanGetter = PlaceholderBoolean::of;
         vectorGetter = PlaceholderVector::of;
      } else {
         intGetter = PlaceholderIntImpl::new;
         floatGetter = PlaceholderFloatImpl::new;
         longGetter = PlaceholderLongImpl::new;
         doubleGetter = PlaceholderDoubleImpl::new;
         booleanGetter = PlaceholderBooleanImpl::new;
         vectorGetter = PlaceholderVectorImpl::new;
      }

      resolutionMap = new ConcurrentHashMap<>();
      resolutionMap.put(VariableType.INTEGER, intGetter::get);
      resolutionMap.put(VariableType.FLOAT, floatGetter::get);
      resolutionMap.put(VariableType.DOUBLE, doubleGetter::get);
      resolutionMap.put(VariableType.LONG, longGetter::get);
      resolutionMap.put(VariableType.BOOLEAN, booleanGetter::get);
      resolutionMap.put(VariableType.VECTOR, vectorGetter::get);
      resolutionMap.put(VariableType.STRING, PlaceholderString::of);
      resolutionMap.put(VariableType.TIME, PlaceholderTime::of);
   }

   @FunctionalInterface
   private interface PlaceholderGetter<T> {
      T get(String str);
   }
}
