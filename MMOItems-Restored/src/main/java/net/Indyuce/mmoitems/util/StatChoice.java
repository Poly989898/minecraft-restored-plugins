package net.Indyuce.mmoitems.util;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class StatChoice {
   private final String id;
   private final String hint;

   public StatChoice(String var1) {
      this(var1, null);
   }

   public StatChoice(String var1, @Nullable String var2) {
      this.id = var1;
      this.hint = var2;
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
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         StatChoice var2 = (StatChoice)var1;
         return this.id.equals(var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
