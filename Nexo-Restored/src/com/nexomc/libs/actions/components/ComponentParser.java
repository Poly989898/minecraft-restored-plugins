package com.nexomc.libs.actions.components;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.nexomc.libs.actions.components.implementations.MathComponent;
import com.nexomc.libs.actions.components.implementations.RandomDoubleComponent;
import com.nexomc.libs.actions.components.implementations.RandomElementComponent;
import com.nexomc.libs.actions.components.implementations.RandomLongComponent;
import com.nexomc.libs.actions.components.implementations.StringComponent;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentParser {
   private static final Pattern COMPONENT_PATTERN = Pattern.compile("~?(?<id>[a-zA-Z]+):\\{(?<data>[^}]+)}");
   private final Table<String, Class<?>, Component.Supplier<?, ?>> registeredComponents = HashBasedTable.create();
   private final PlaceholderManager placeholderManager;

   public ComponentParser(PlaceholderManager placeholderManager) {
      this.placeholderManager = placeholderManager;
   }

   private <T> @Nullable Component<T, ?> createComponent(@NotNull Class<T> clazz, @NotNull String id, @NotNull String value) {
      Component.Supplier<?, ?> supplier = (Component.Supplier)this.registeredComponents.get(id.toLowerCase(), clazz);
      return supplier == null ? null : supplier.run(value, this.placeholderManager);
   }

   public <T, R> void register(@NotNull Class<T> clazz, @NotNull String id, Component.@NotNull Supplier<T, R> supplier) {
      this.registeredComponents.put(id.toLowerCase(), clazz, supplier);
   }

   public <T> void registerDefaults(@NotNull Class<T> clazz) {
      this.register(clazz, "math", MathComponent::new);
      this.register(clazz, "random", RandomDoubleComponent::new);
      this.register(clazz, "randomL", RandomLongComponent::new);
      this.register(clazz, "randomE", RandomElementComponent::new);
   }

   public <T> @NotNull List<Component<T, ?>> parseComponents(@NotNull Class<T> clazz, @NotNull String data) {
      Matcher matcher = COMPONENT_PATTERN.matcher(data);
      List<Component<T, ?>> foundComponents = new ArrayList();
      int lastMatchEnd = 0;

      while(matcher.find()) {
         int matchStart = matcher.start();
         String id = matcher.group("id").toLowerCase();
         Component<T, ?> node = this.createComponent(clazz, id, matcher.group("data"));
         if (node == null) {
            foundComponents.add(new StringComponent(data.substring(lastMatchEnd, matcher.end()), this.placeholderManager));
            lastMatchEnd = matcher.end();
         } else {
            if (matchStart != 0 && matchStart != lastMatchEnd) {
               foundComponents.add(new StringComponent(data.substring(lastMatchEnd, matchStart), this.placeholderManager));
            }

            foundComponents.add(node);
            lastMatchEnd = matcher.end();
         }
      }

      if (lastMatchEnd != data.length()) {
         foundComponents.add(new StringComponent(data.substring(lastMatchEnd), this.placeholderManager));
      }

      return foundComponents;
   }
}
