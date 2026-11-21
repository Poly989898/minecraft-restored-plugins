package it.ytnoos.lpx;

import java.util.List;

public record ConfigDefaultUpdate(String I, String i, List<String> L) {
   public String path() {
      return this.I;
   }

   public String newDefault() {
      return this.i;
   }

   public List<String> oldDefaults() {
      return this.L;
   }
}
