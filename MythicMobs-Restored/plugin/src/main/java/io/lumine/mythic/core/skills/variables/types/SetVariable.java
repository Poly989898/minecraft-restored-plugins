package io.lumine.mythic.core.skills.variables.types;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SetVariable extends Variable implements ContainingVariable {
   private Set<String> value = ConcurrentHashMap.newKeySet();
   public static final VariableInfo<Set> variableInfo = VariableInfo.<Set>builder()
      .clazz(SetVariable.class)
      .type(VariableType.SET)
      .typeName("Set")
      .valueClass(Set.class)
      .defaultValue(PolymorphicPlaceholder.of(""))
      .objectConstructor(SetVariable::ofObject)
      .polymorphicConstructor(SetVariable::of)
      .build();

   @Override
   public VariableInfo<Set> getVariableInfo() {
      return variableInfo;
   }

   public static SetVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new SetVariable(data.getString(context), expirationTime);
   }

   public static Set<String> fromString(String data) {
      return new HashSet<>(parse(data));
   }

   public static SetVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof Set set) {
         Object test = set.stream().map(String::valueOf).collect(Collectors.joining(","));
         return new SetVariable((String)test, expirationTime);
      } else {
         return new SetVariable(String.valueOf(o), expirationTime);
      }
   }

   public SetVariable(String value) {
      this.build(value);
   }

   public SetVariable(String value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   private void build(String value) {
      if (value != null && !value.isEmpty()) {
         this.add(value);
      }
   }

   public void add(@NotNull String value) {
      if (!value.isEmpty()) {
         if (value.contains(",")) {
            this.value.addAll(parse(value));
         } else {
            this.value.add(value);
         }
      }
   }

   public void remove(@NotNull String value) {
      if (value.contains(",")) {
         parse(value).forEach(this.value::remove);
      } else {
         this.value.remove(value);
      }
   }

   public boolean contains(@NotNull String value) {
      return value.contains(",") ? parse(value).stream().anyMatch(this.value::contains) : this.value.contains(value);
   }

   public static List<String> parse(@NotNull String value) {
      return Arrays.asList(Arrays.stream(value.split(",")).filter(s -> !s.isEmpty()).toArray(String[]::new));
   }

   public Set<String> get() {
      return this.value;
   }

   public Set<String> getValueClone() {
      return Sets.newHashSet(this.value);
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.add(data.getString(context));
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.remove(data.getString(context));
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      Set<String> newSet = new HashSet<>(parse(data.getString(context)));
      return this.value.equals(newSet);
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      Set<String> newSet = new HashSet<>(parse(data.getString(context)));
      return this.value.containsAll(newSet);
   }

   @Override
   public String toString() {
      return ListVariable.toString(this.value);
   }

   @Override
   public String formatted() {
      return ListVariable.toString(this.value, ", ");
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SetVariable other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$value = this.getValue();
         Object other$value = other.getValue();
         return this$value == null ? other$value == null : this$value.equals(other$value);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof SetVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $value = this.getValue();
      return result * 59 + ($value == null ? 43 : $value.hashCode());
   }
}
