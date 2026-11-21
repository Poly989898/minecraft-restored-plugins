package io.lumine.mythic.bukkit.utils.command.functional;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.Command;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@NonnullByDefault
public interface FunctionalCommandBuilder<T extends CommandSender> {
   String DEFAULT_NOT_OP_MESSAGE = "&cOnly server operators are able to use this command.";
   String DEFAULT_NOT_PLAYER_MESSAGE = "&cOnly players are able to use this command.";
   String DEFAULT_NOT_CONSOLE_MESSAGE = "&cThis command is only available through the server console.";
   String DEFAULT_INVALID_USAGE_MESSAGE = "&cInvalid usage. Try: {usage}.";
   String DEFAULT_INVALID_ARGUMENT_MESSAGE = "&cInvalid argument '{arg}' at index {index}.";
   String DEFAULT_INVALID_SENDER_MESSAGE = "&cYou are not able to use this command.";

   static FunctionalCommandBuilder<CommandSender> newBuilder() {
      return new FunctionalCommandBuilderImpl<>();
   }

   FunctionalCommandBuilder<T> description(String str);

   default FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test) {
      return this.assertFunction(test, null);
   }

   FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> predicate, @Nullable String str);

   default FunctionalCommandBuilder<T> assertPermission(String permission) {
      return this.assertPermission(permission, null);
   }

   FunctionalCommandBuilder<T> assertPermission(String str, @Nullable String str2);

   default FunctionalCommandBuilder<T> assertOp() {
      return this.assertOp("&cOnly server operators are able to use this command.");
   }

   FunctionalCommandBuilder<T> assertOp(String str);

   default FunctionalCommandBuilder<Player> assertPlayer() {
      return this.assertPlayer("&cOnly players are able to use this command.");
   }

   FunctionalCommandBuilder<Player> assertPlayer(String str);

   default FunctionalCommandBuilder<ConsoleCommandSender> assertConsole() {
      return this.assertConsole("&cThis command is only available through the server console.");
   }

   FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(String str);

   default FunctionalCommandBuilder<T> assertUsage(String usage) {
      return this.assertUsage(usage, "&cInvalid usage. Try: {usage}.");
   }

   FunctionalCommandBuilder<T> assertUsage(String str, String str2);

   default FunctionalCommandBuilder<T> assertArgument(int index, Predicate<String> test) {
      return this.assertArgument(index, test, "&cInvalid argument '{arg}' at index {index}.");
   }

   FunctionalCommandBuilder<T> assertArgument(int value, Predicate<String> predicate, String str);

   default FunctionalCommandBuilder<T> assertSender(Predicate<T> test) {
      return this.assertSender(test, "&cYou are not able to use this command.");
   }

   FunctionalCommandBuilder<T> assertSender(Predicate<T> predicate, String str);

   FunctionalCommandBuilder<T> tabHandler(FunctionalTabHandler<T> functionalTabHandler);

   Command handler(FunctionalCommandHandler<T> functionalCommandHandler);
}
