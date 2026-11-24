package io.lumine.mythic.lib.api.util.ui;

import io.lumine.mythic.lib.MythicLib;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FriendlyFeedbackProvider {
   @NotNull
   FriendlyFeedbackPalette ffPalette;
   @NotNull
   HashMap<FriendlyFeedbackCategory, ArrayList<FriendlyFeedbackMessage>> feedback = new HashMap<>();
   @NotNull
   FriendlyFeedbackMessage prefixSample = new FriendlyFeedbackMessage("");

   public FriendlyFeedbackPalette getPalette() {
      return this.ffPalette;
   }

   public FriendlyFeedbackProvider(@NotNull FriendlyFeedbackPalette palette) {
      this.ffPalette = palette;
   }

   @NotNull
   public ArrayList<FriendlyFeedbackMessage> getFeedbackOf(@NotNull FriendlyFeedbackCategory category) {
      return this.feedback.computeIfAbsent(category, k -> new ArrayList<>());
   }

   public void clearFeedbackOf(@NotNull FriendlyFeedbackCategory category) {
      this.feedback.put(category, new ArrayList<>());
   }

   public void clearFeedback() {
      this.feedback.clear();
   }

   public void log(@NotNull FriendlyFeedbackCategory category, @Nullable String message, String... replaces) {
      if (message != null) {
         if (!message.isEmpty()) {
            this.getFeedbackOf(category).add(this.getMessage(message, replaces));
         }
      }
   }

   @NotNull
   public FriendlyFeedbackMessage getMessage(@NotNull String message, String... replaces) {
      return generateMessage(this.prefixSample, message, replaces);
   }

   public static void log(@Nullable FriendlyFeedbackProvider ffp, @NotNull FriendlyFeedbackCategory category, @Nullable String message, String... replaces) {
      if (ffp != null) {
         ffp.log(category, message, replaces);
      }
   }

   public void activatePrefix(boolean usePrefix, @Nullable String subdivision) {
      this.prefixSample.togglePrefix(usePrefix);
      this.prefixSample.setSubdivision(subdivision);
   }

   public int messagesTotal(@NotNull FriendlyFeedbackCategory ofCategory) {
      return this.getFeedbackOf(ofCategory).size();
   }

   public int messagesTotal() {
      int t = 0;

      for (FriendlyFeedbackCategory cat : FriendlyFeedbackCategory.values()) {
         t += this.messagesTotal(cat);
      }

      return t;
   }

   public void sendAllTo(@NotNull Player player, @NotNull ConsoleCommandSender console) {
      for (FriendlyFeedbackCategory cat : this.feedback.keySet()) {
         this.sendTo(cat, player);
         this.sendTo(cat, console);
      }
   }

   public void sendTo(@NotNull FriendlyFeedbackCategory category, @NotNull Player player, @NotNull ConsoleCommandSender console) {
      this.sendTo(category, player);
      this.sendTo(category, console);
   }

   public void sendAllTo(@NotNull Player player) {
      for (FriendlyFeedbackCategory cat : this.feedback.keySet()) {
         this.sendTo(cat, player);
      }
   }

   public void sendTo(@NotNull FriendlyFeedbackCategory category, @NotNull Player player) {
      for (FriendlyFeedbackMessage msg : this.getFeedbackOf(category)) {
         player.sendMessage(MythicLib.plugin.parseColors(msg.forPlayer(this.getPalette())));
      }
   }

   public void sendAllTo(@NotNull ConsoleCommandSender console) {
      this.sendAllTo((CommandSender) console);
   }

   public void sendTo(@NotNull FriendlyFeedbackCategory category, @NotNull ConsoleCommandSender console) {
      this.sendTo(category, (CommandSender)console);
   }

   public void sendAllTo(@NotNull CommandSender sender) {
      for (FriendlyFeedbackCategory cat : this.feedback.keySet()) {
         this.sendTo(cat, sender);
      }
   }

   public void sendTo(@NotNull FriendlyFeedbackCategory category, @NotNull CommandSender sender) {
      for (FriendlyFeedbackMessage msg : this.getFeedbackOf(category)) {
         sender.sendMessage(MythicLib.plugin.parseColors(msg.forConsole(this.getPalette())));
      }
   }

   @NotNull
   public static FriendlyFeedbackMessage generateMessage(@NotNull String message, String... replaces) {
      return generateMessage(null, message, replaces);
   }

   @NotNull
   public static FriendlyFeedbackMessage generateMessage(@Nullable FriendlyFeedbackMessage prefixTemplate, @NotNull String message, String... replaces) {
      if (prefixTemplate == null) {
         prefixTemplate = new FriendlyFeedbackMessage("");
      }

      for (int i = 0; i < replaces.length; i++) {
         String rep = replaces[i];
         if (rep == null) {
            rep = "";
         }

         message = message.replace("{" + i + "}", rep);
      }

      FriendlyFeedbackMessage msg = prefixTemplate.clone();
      msg.setMessage("$b" + message);
      return msg;
   }

   @NotNull
   public static String quickForConsole(@NotNull FriendlyFeedbackPalette palette, @NotNull String message, String... replaces) {
      FriendlyFeedbackMessage msg = generateMessage(null, message, replaces);
      return msg.forConsole(palette);
   }

   @NotNull
   public static String quickForPlayer(@NotNull FriendlyFeedbackPalette palette, @NotNull String message, String... replaces) {
      if (MythicLib.plugin.getVersion().isUnder(1, 16)) {
         return quickForConsole(palette, message, replaces);
      }

      FriendlyFeedbackMessage msg = generateMessage(null, message, replaces);
      return msg.forPlayer(palette);
   }
}
