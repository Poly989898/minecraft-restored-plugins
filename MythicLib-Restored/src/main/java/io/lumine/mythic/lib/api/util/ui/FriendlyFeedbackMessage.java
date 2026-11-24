package io.lumine.mythic.lib.api.util.ui;

import io.lumine.mythic.lib.MythicLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FriendlyFeedbackMessage implements Cloneable {
   @NotNull
   String message;
   boolean withPrefix = false;
   @Nullable
   String withSubdivision = null;

   public FriendlyFeedbackMessage clone() {
      try {
         super.clone();
      } catch (CloneNotSupportedException exception) {
      }

      return new FriendlyFeedbackMessage(this.getMessage(), this.hasPrefix(), this.getSubdivision());
   }

   public void setMessage(@NotNull String message) {
      this.message = message;
   }

   public void togglePrefix(boolean usePrefix) {
      this.withPrefix = usePrefix;
   }

   public void setSubdivision(@Nullable String subdivision) {
      this.withSubdivision = subdivision;
   }

   @NotNull
   public String getMessage() {
      return this.message;
   }

   public boolean hasPrefix() {
      return this.withPrefix;
   }

   @Nullable
   public String getSubdivision() {
      return this.withSubdivision;
   }

   public FriendlyFeedbackMessage(@NotNull String message) {
      this.message = message;
   }

   public FriendlyFeedbackMessage(@NotNull String message, @Nullable String subdivision) {
      this.message = message;
      if (subdivision != null) {
         this.withPrefix = true;
         this.withSubdivision = subdivision;
      }
   }

   public FriendlyFeedbackMessage(@NotNull String message, boolean usePrefix) {
      this.message = message;
      this.withPrefix = usePrefix;
   }

   public FriendlyFeedbackMessage(@NotNull String message, boolean usePrefix, @Nullable String subdivision) {
      this.message = message;
      this.withPrefix = usePrefix;
      this.withSubdivision = subdivision;
   }

   @Override
   public String toString() {
      return this.getMessage();
   }

   @NotNull
   public String forPlayer(@NotNull FriendlyFeedbackPalette pal) {
      if (MythicLib.plugin.getVersion().isUnder(1, 16)) {
         return this.forConsole(pal);
      }

      StringBuilder actualMessage = new StringBuilder();
      if (this.hasPrefix()) {
         actualMessage.append(pal.parseForPlayer(pal.getPrefix(this.getSubdivision())));
      }

      actualMessage.append(pal.parseForPlayer(pal.parseForPlayer(this.getMessage())));
      return actualMessage.toString();
   }

   @NotNull
   public String forConsole(@NotNull FriendlyFeedbackPalette pal) {
      StringBuilder actualMessage = new StringBuilder();
      if (this.hasPrefix()) {
         actualMessage.append(pal.parseForConsole(pal.consolePrefix(this.getSubdivision())));
      }

      actualMessage.append(pal.parseForConsole(pal.parseForConsole(this.getMessage())));
      return actualMessage.toString();
   }
}
