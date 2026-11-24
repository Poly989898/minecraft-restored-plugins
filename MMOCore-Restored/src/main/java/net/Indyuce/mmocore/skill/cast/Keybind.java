package net.Indyuce.mmocore.skill.cast;

import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.mmocore.api.event.PlayerKeyPressEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Keybind {
   private final PlayerKey key;
   private final Boolean sneak;

   public Keybind(Object object) {
      if (object instanceof String) {
         this.key = PlayerKey.valueOf(UtilityMethods.enumName((String)object));
         this.sneak = null;
      } else {
         if (!(object instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("Keybind requires a string or config section");
         }

         ConfigurationSection config = (ConfigurationSection)object;
         this.key = PlayerKey.valueOf(UtilityMethods.enumName(config.getString("key", "NONE")));
         this.sneak = config.contains("sneak") ? config.getBoolean("sneak") : null;
      }
   }

   public Keybind(PlayerKey key, Boolean sneak) {
      this.key = key;
      this.sneak = sneak;
   }

   public boolean matches(@NotNull PlayerKeyPressEvent event) {
      return event.getPressed() == this.key && (this.sneak == null || event.getPlayer().isSneaking() == this.sneak);
   }

   @Nullable
   public static Keybind fromConfig(@Nullable Object object) {
      return object == null ? null : new Keybind(object);
   }
}
