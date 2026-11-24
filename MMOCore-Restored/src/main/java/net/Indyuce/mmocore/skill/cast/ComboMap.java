package net.Indyuce.mmocore.skill.cast;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ComboMap {
   private final Map<KeyCombo, Integer> combos = new HashMap<>();
   private final Set<PlayerKey> firstKeys = new HashSet<>();
   private final Set<PlayerKey> keys = new HashSet<>();
   private final int longestCombo;

   public ComboMap(ConfigurationSection config) {
      int currentLongestCombo = 0;

      for (String key : config.getKeys(false)) {
         try {
            int spellSlot = Integer.valueOf(key);
            Validate.isTrue(spellSlot >= 0, "Spell slot must be at least 0");
            Validate.isTrue(!this.combos.values().contains(spellSlot), "There is already a key combo with the same skill slot");
            KeyCombo combo = new KeyCombo();

            for (String str : config.getStringList(key)) {
               PlayerKey newKey = PlayerKey.valueOf(UtilityMethods.enumName(str));
               this.keys.add(newKey);
               combo.registerKey(newKey);
            }

            this.combos.put(combo, spellSlot);
            this.firstKeys.add(combo.getAt(0));
            currentLongestCombo = Math.max(currentLongestCombo, combo.countKeys());
         } catch (RuntimeException exception) {
            throw new RuntimeException("Could not loading key combo '" + key + "': " + exception.getMessage());
         }
      }

      this.longestCombo = currentLongestCombo;
   }

   @NotNull
   public Map<KeyCombo, Integer> getCombos() {
      return this.combos;
   }

   public int getLongest() {
      return this.longestCombo;
   }

   public boolean isComboKey(PlayerKey key) {
      return this.keys.contains(key);
   }

   public boolean isComboStart(PlayerKey key) {
      return this.firstKeys.contains(key);
   }
}
