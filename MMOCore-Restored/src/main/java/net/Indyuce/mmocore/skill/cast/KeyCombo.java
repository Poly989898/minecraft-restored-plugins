package net.Indyuce.mmocore.skill.cast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class KeyCombo {
   private final List<PlayerKey> keys = new ArrayList<>();

   public int countKeys() {
      return this.keys.size();
   }

   public void registerKey(@NotNull PlayerKey key) {
      this.keys.add(key);
   }

   public PlayerKey getAt(int index) {
      return this.keys.get(index);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         KeyCombo keyCombo = (KeyCombo)o;
         return Objects.equals(this.keys, keyCombo.keys);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.keys.hashCode();
   }
}
