package net.Indyuce.mmoitems.util;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class StatChoice {
   private final String id;
   private final String hint;

   public StatChoice(String text) {
      this(text, null);
   }

   public StatChoice(String text, @Nullable String text2) {
      this.id = text;
      this.hint = text2;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getHint() {
      return this.hint;
   }

   @Override
   public String toString() {
      return this.getId();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         StatChoice statChoice = (StatChoice)obj;
         return this.id.equals(statChoice.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
