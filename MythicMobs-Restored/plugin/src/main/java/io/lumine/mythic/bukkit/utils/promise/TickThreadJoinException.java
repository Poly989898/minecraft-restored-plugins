package io.lumine.mythic.bukkit.utils.promise;

public class TickThreadJoinException extends IllegalStateException {
   public TickThreadJoinException() {
      super("Refusing to block a Folia tick thread on an incomplete promise");
   }
}
