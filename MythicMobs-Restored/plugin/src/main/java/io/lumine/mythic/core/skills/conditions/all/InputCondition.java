package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.triggers.meta.InputState;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "input",
   aliases = {"hasinput", "playerinput"},
   description = "Checks if a player is pressing a specific input key"
)
public class InputCondition extends SkillCondition implements IEntityCondition {
   private final Set<String> requiredKeys;

   public InputCondition(String line, MythicLineConfig config) {
      super(line);
      String rawKeys = config.getString(new String[]{"input", "key", "keys", "k"}, this.conditionVar, "forward");
      this.requiredKeys = Set.copyOf(parseKeys(rawKeys));
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof Player player) {
         try {
            InputState state = InputState.from(player.getCurrentInput());

            for (String key : this.requiredKeys) {
               if (!state.isPressed(key)) {
                  return false;
               }
            }

            return true;
         } catch (UnsupportedOperationException | NoSuchMethodError e) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Set<String> parseKeys(String rawKeys) {
      HashSet<String> keys = new HashSet<>();
      if (rawKeys == null) {
         keys.add("forward");
         return keys;
      } else {
         for (String part : rawKeys.split(",")) {
            String trimmed = part.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty()) {
               keys.add(trimmed);
            }
         }

         if (keys.isEmpty()) {
            keys.add("forward");
         }

         return keys;
      }
   }
}
