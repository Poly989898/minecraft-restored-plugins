package team.unnamed.creative.resources;

import org.jetbrains.annotations.Nullable;

public class MergeException extends RuntimeException {
   private static final long serialVersionUID = -123823843829316772L;

   public MergeException() {
   }

   public MergeException(@Nullable String message) {
      super(message);
   }

   public MergeException(@Nullable String message, @Nullable Throwable cause) {
      super(message, cause);
   }

   public MergeException(@Nullable Throwable cause) {
      super(cause);
   }
}
