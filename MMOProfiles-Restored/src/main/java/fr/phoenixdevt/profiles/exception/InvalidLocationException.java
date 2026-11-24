package fr.phoenixdevt.profiles.exception;

import fr.phoenixdevt.profiles.util.WrappedLocation;

public class InvalidLocationException extends RuntimeException {
   public InvalidLocationException(WrappedLocation location) {
      super("World '" + location.getWorldName() + "' not found. Make sure you setup your server identifiers correctly");
   }
}
