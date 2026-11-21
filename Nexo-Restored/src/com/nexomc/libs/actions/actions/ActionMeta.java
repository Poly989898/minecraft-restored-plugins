package com.nexomc.libs.actions.actions;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import com.nexomc.libs.actions.components.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionMeta<T> {
   private static final long DEFAULT_DELAY = 0L;
   private static final double DEFAULT_CHANCE = (double)0.0F;
   private final Map<String, String> properties;
   private final String rawData;
   private final List<Component<T, ?>> components;
   private final long delay;
   private final double chance;

   public ActionMeta(@NotNull Map<String, String> properties, @Nullable String rawData, @NotNull List<Component<T, ?>> components) {
      this.properties = properties;
      this.rawData = rawData == null ? "" : rawData;
      this.components = components;
      this.delay = (Long)this.getProperty("delay", 0L, Longs::tryParse);
      this.chance = (Double)this.getProperty("chance", (double)0.0F, Doubles::tryParse);
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public <R> @NotNull R getProperty(@NotNull String key, @NotNull R def, @NotNull Function<@NotNull String, @Nullable R> transformer) {
      String value = (String)this.properties.get(key);
      if (value == null) {
         return def;
      } else {
         R result = (R)transformer.apply(value);
         return (R)(result == null ? def : result);
      }
   }

   public List<Component<T, ?>> getComponents() {
      return this.components;
   }

   public String getRawData() {
      return this.rawData;
   }

   public @NotNull String getParsedData(@NotNull T t, @NotNull Context<T> context) {
      StringBuilder stringBuilder = new StringBuilder(this.rawData.length());

      for(Component<T, ?> component : this.components) {
         stringBuilder.append(component.parse(t, context));
      }

      return stringBuilder.toString();
   }

   public long getDelay() {
      return this.delay;
   }

   public boolean hasDelay() {
      return this.getDelay() != 0L;
   }

   public double getChance() {
      return this.chance;
   }

   public boolean hasChance() {
      return this.getChance() != (double)0.0F;
   }
}
