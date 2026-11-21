package io.lumine.mythic.core.skills.variables.types;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ListVariable extends Variable implements ContainingVariable {
   private final List<String> value = Lists.newCopyOnWriteArrayList();
   public static final VariableInfo<List> variableInfo = VariableInfo.<List>builder()
      .clazz(ListVariable.class)
      .type(VariableType.LIST)
      .typeName("List")
      .valueClass(List.class)
      .defaultValue(PolymorphicPlaceholder.of(""))
      .objectConstructor(ListVariable::ofObject)
      .polymorphicConstructor(ListVariable::of)
      .build();

   @Override
   public VariableInfo<List> getVariableInfo() {
      return variableInfo;
   }

   public static ListVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new ListVariable(data.getString(context), expirationTime);
   }

   public static String toString(Collection<String> value, String delimiter) {
      return String.join(delimiter, value);
   }

   public static String toString(Collection<String> value) {
      return String.join(",", value);
   }

   public static List<String> fromString(String data) {
      return Arrays.asList(data.split(","));
   }

   public static String formatted(Collection<String> value) {
      return toString(value, ", ");
   }

   public static ListVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof List list) {
         Object test = list.stream().map(String::valueOf).collect(Collectors.joining(","));
         return new ListVariable((String)test, expirationTime);
      } else {
         return new ListVariable(String.valueOf(o), expirationTime);
      }
   }

   public ListVariable(String value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   public ListVariable(String value) {
      this.build(value);
   }

   public void build(String value) {
      if (value != null && !value.isEmpty()) {
         this.value.addAll(fromString(value));
      }
   }

   public void append(@NotNull String value) {
      if (!value.isEmpty()) {
         if (value.contains(",")) {
            this.value.addAll(this.parse(value));
         } else {
            this.value.add(value);
         }
      }
   }

   public void remove(int value) {
      if (value >= 0 && value < this.value.size()) {
         this.value.remove(value);
      }
   }

   public String get(int value) {
      return value >= 0 && value < this.value.size() ? this.value.get(value) : null;
   }

   public static String getWithPipeDefault(List<String> list, String value) {
      if (value != null && !value.isEmpty()) {
         try {
            if (value.contains("|")) {
               String[] split = value.split("\\|");
               if (split.length > 1) {
                  String key = split[0];
                  String def = split[1];
                  return Objects.requireNonNullElse(list.get(Integer.parseInt(key)), def);
               }
            }

            return list.get(Integer.parseInt(value));
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static String reverse(List<String> list) {
      if (list.isEmpty()) {
         return "";
      } else {
         return list.size() == 1 ? list.get(0) : String.join(",", list.reversed());
      }
   }

   public static String sort(List<String> list) {
      if (list.isEmpty()) {
         return "";
      } else if (list.size() == 1) {
         return list.get(0);
      } else {
         List<String> sorted = new ArrayList<>(list);
         Collections.sort(sorted);
         return String.join(",", sorted);
      }
   }

   public String sortNumerically(List<String> list) {
      if (list.isEmpty()) {
         return "";
      } else {
         return list.size() == 1 ? list.get(0) : list.stream().map(Double::parseDouble).sorted().map(Object::toString).collect(Collectors.joining(","));
      }
   }

   private List<String> parse(@NotNull String value) {
      return Arrays.asList(Arrays.stream(value.split(",")).filter(s -> !s.isEmpty()).toArray(String[]::new));
   }

   public List<String> get() {
      return this.value;
   }

   public List<String> getValueClone() {
      return Lists.newArrayList(this.value);
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.append(data.getString(context));
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.remove(data.getInt(context));
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      List<String> newList = this.parse(data.getString(context));
      return this.value.equals(newList);
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      List<String> newList = this.parse(data.getString(context));
      return new HashSet<>(this.value).containsAll(new HashSet<>(newList));
   }

   @Override
   public String toString() {
      return toString(this.value);
   }

   @Override
   public String formatted() {
      return toString(this.value, ", ");
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ListVariable other)) {
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
      return other instanceof ListVariable;
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
