package com.nexomc.libs.actions.actions;

import com.nexomc.libs.actions.components.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionParser {
   private static final Pattern ACTION_PATTERN = Pattern.compile("(?:\\{(?<properties>.+)}[^\\[]*)?(?:\\[(?<id>\\w+)])\\s?(?<data>.*)?");
   private final ActionManager actionManager;

   public ActionParser(ActionManager actionManager) {
      this.actionManager = actionManager;
   }

   private @NotNull Map<String, String> parseProperties(@NotNull Matcher matcher) {
      String propertiesGroup = matcher.group("properties");
      if (propertiesGroup != null && !propertiesGroup.isEmpty()) {
         Map<String, String> properties = new HashMap();

         for(String part : propertiesGroup.split(" ")) {
            String[] property = part.split("=", 2);
            if (property.length == 2) {
               properties.put(property[0], property[1]);
            }
         }

         return properties;
      } else {
         return Collections.emptyMap();
      }
   }

   public <T> @Nullable Action<T> parseAction(@NotNull Class<T> clazz, @NotNull String input) {
      Matcher matcher = ACTION_PATTERN.matcher(input);
      if (!matcher.find()) {
         return null;
      } else {
         String id = matcher.group("id");
         if (!this.actionManager.isRegistered(clazz, id)) {
            return null;
         } else {
            String data = matcher.group("data");
            List<Component<T, ?>> nodes = data == null ? Collections.emptyList() : this.actionManager.getComponentParser().parseComponents(clazz, data);
            ActionMeta<T> meta = new ActionMeta<T>(this.parseProperties(matcher), data, nodes);
            return this.actionManager.<T>createAction(clazz, id, meta);
         }
      }
   }
}
