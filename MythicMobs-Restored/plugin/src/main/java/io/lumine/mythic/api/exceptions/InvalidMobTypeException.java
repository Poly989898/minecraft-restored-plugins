package io.lumine.mythic.api.exceptions;

public class InvalidMobTypeException extends Exception {
   private static final long serialVersionUID = 1L;

   public InvalidMobTypeException(String message) {
      super(message);
   }
}
