package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.utils.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class CommandHelper {
   public static final Component COMMAND_HEADER = Text.parse(
      "<yellow><strikethrough>-------------<gold>========</strikethrough> <bold><gradient:#20DAE9:#2815EC>Mythic</gradient></bold> <strikethrough><gold>========<yellow>-------------</strikethrough>"
   );
   public static final Component COMMAND_FOOTER = Text.parse(
      "<yellow><strikethrough>-----------------</strikethrough> <gradient:#20DAE9:#2815EC>www.mythiccraft.io</gradient> <yellow><strikethrough>-----------------</strikethrough>"
   );
   public static final Component COMMAND_PREFIX = Text.parse("<bold><gray>[<gradient:#20DAE9:#2815EC>Mythic</gradient><gray>]</bold> ");

   public static void sendHeader(CommandSender sender) {
      Text.sendMessage(sender, COMMAND_HEADER);
   }

   public static void sendFooter(CommandSender sender) {
      Text.sendMessage(sender, COMMAND_FOOTER);
   }

   public static void send(CommandSender sender, String message) {
      Text.sendMessage(sender, Text.parse("<white>" + message));
   }

   public static void sendSuccess(CommandSender sender, String message) {
      Text.sendMessage(sender, COMMAND_PREFIX.append(Text.parse("<green>" + message)));
   }

   public static void sendError(CommandSender sender, String message) {
      Text.sendMessage(sender, COMMAND_PREFIX.append(Text.parse("<red>" + message)));
   }

   public static void sendCommandMessage(CommandSender player, String[]... args) {
      sendHeader(player);
      player.sendMessage(" ");

      for (String[] s : args) {
         for (String ss : s) {
            Text.sendMessage(player, ss);
         }
      }

      player.sendMessage(" ");
      sendFooter(player);
   }

   public static void sendCommandMessage(CommandSender player, Component[]... args) {
      sendHeader(player);
      player.sendMessage(" ");

      for (Component[] s : args) {
         for (Component ss : s) {
            Text.sendMessage(player, ss);
         }
      }

      player.sendMessage(" ");
      sendFooter(player);
   }

   public static void sendEditorMessage(CommandSender player, String... args) {
      sendHeader(player);

      for (String ss : args) {
         Text.sendMessage(player, COMMAND_PREFIX.append(Text.parse("<white>" + ss)));
      }
   }

   private CommandHelper() {
   }
}
