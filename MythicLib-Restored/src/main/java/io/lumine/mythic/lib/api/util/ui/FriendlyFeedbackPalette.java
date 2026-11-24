package io.lumine.mythic.lib.api.util.ui;

import io.lumine.mythic.lib.MythicLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FriendlyFeedbackPalette {
   @NotNull
   public abstract String getBodyFormat();

   @NotNull
   public abstract String consoleBodyFormat();

   @NotNull
   public abstract String getExampleFormat();

   @NotNull
   public abstract String consoleExampleFormat();

   @NotNull
   public abstract String getInputFormat();

   @NotNull
   public abstract String consoleInputFormat();

   @NotNull
   public abstract String getResultFormat();

   @NotNull
   public abstract String consoleResultFormat();

   @NotNull
   public abstract String getSuccessFormat();

   @NotNull
   public abstract String consoleSuccessFormat();

   @NotNull
   public abstract String getFailureFormat();

   @NotNull
   public abstract String consoleFailureFormat();

   @NotNull
   public abstract String getRawPrefix();

   @NotNull
   String getPrefix(@Nullable String subdivision) {
      return subdivision != null ? this.getRawPrefix().replace("#s", " " + this.getSubdivisionFormat() + subdivision) : this.getRawPrefix().replace("#s", "");
   }

   @NotNull
   public abstract String getRawPrefixConsole();

   @NotNull
   String consolePrefix(@Nullable String subdivision) {
      return subdivision != null
         ? this.getRawPrefixConsole().replace("#s", " " + this.consoleSubdivisionFormat() + subdivision)
         : this.getRawPrefixConsole().replace("#s", "");
   }

   @NotNull
   public abstract String getSubdivisionFormat();

   @NotNull
   public abstract String consoleSubdivisionFormat();

   @NotNull
   public String parseForPlayer(@NotNull String message) {
      return MythicLib.plugin.getVersion().isUnder(1, 16)
         ? this.parseForConsole(message)
         : message.replace("$b", this.getBodyFormat())
            .replace("$e", this.getExampleFormat())
            .replace("$i", this.getInputFormat())
            .replace("$u", this.getInputFormat())
            .replace("$s", this.getSuccessFormat())
            .replace("$f", this.getFailureFormat())
            .replace("$r", this.getResultFormat());
   }

   @NotNull
   public String parseForConsole(@NotNull String messaeg) {
      return messaeg.replace("$b", this.consoleBodyFormat())
         .replace("$e", this.consoleExampleFormat())
         .replace("$i", this.consoleInputFormat())
         .replace("$u", this.consoleInputFormat())
         .replace("$s", this.consoleSuccessFormat())
         .replace("$f", this.consoleFailureFormat())
         .replace("$r", this.consoleResultFormat());
   }
}
