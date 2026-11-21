package it.ytnoos.lpx;

import java.util.List;

public record ConfigVersionUpdates(int U, List<ConfigDefaultUpdate> z) {
   public int version() {
      return this.U;
   }

   public List<ConfigDefaultUpdate> updates() {
      return this.z;
   }
}
