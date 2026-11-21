package io.lumine.mythic.bukkit.utils.network.messaging.codec;

public class EncodingException extends RuntimeException {
   public EncodingException() {
   }

   public EncodingException(String message) {
      super(message);
   }

   public EncodingException(String message, Throwable cause) {
      super(message, cause);
   }

   public EncodingException(Throwable cause) {
      super(cause);
   }
}
