package io.lumine.mythic.bukkit.utils.promise;

public class PromiseRetiredException extends IllegalStateException {
   public PromiseRetiredException() {
      super("Entity was retired before the task could be scheduled");
   }
}
