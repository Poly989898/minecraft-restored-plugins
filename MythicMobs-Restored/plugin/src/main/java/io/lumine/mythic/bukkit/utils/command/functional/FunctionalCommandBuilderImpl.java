package io.lumine.mythic.bukkit.utils.command.functional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.Command;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@NonnullByDefault
class FunctionalCommandBuilderImpl<T extends CommandSender> implements FunctionalCommandBuilder<T> {
   private final Builder<Predicate<CommandContext<?>>> predicates;
   @Nullable
   private FunctionalTabHandler tabHandler;
   @Nullable
   private String permission;
   @Nullable
   private String permissionMessage;
   @Nullable
   private String description;

   private FunctionalCommandBuilderImpl(
      Builder<Predicate<CommandContext<?>>> predicates,
      @Nullable FunctionalTabHandler tabHandler,
      @Nullable String permission,
      @Nullable String permissionMessage,
      @Nullable String description
   ) {
      this.predicates = predicates;
      this.tabHandler = tabHandler;
      this.permission = permission;
      this.permissionMessage = permissionMessage;
      this.description = description;
   }

   FunctionalCommandBuilderImpl() {
      this(ImmutableList.builder(), null, null, null, null);
   }

   @Override
   public FunctionalCommandBuilder<T> description(String description) {
      Objects.requireNonNull(description, "description");
      this.description = description;
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test, @Nullable String failureMessage) {
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         if (((Predicate)test).test(context)) {
            return true;
         } else {
            if (failureMessage != null) {
               context.reply(failureMessage);
            }

            return false;
         }
      });
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> assertPermission(String permission, @Nullable String failureMessage) {
      Objects.requireNonNull(permission, "permission");
      this.permission = permission;
      this.permissionMessage = failureMessage;
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> assertOp(String failureMessage) {
      Objects.requireNonNull(failureMessage, "failureMessage");
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         if (context.sender().isOp()) {
            return true;
         } else {
            context.reply(failureMessage);
            return false;
         }
      });
      return this;
   }

   @Override
   public FunctionalCommandBuilder<Player> assertPlayer(String failureMessage) {
      Objects.requireNonNull(failureMessage, "failureMessage");
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         if (context.sender() instanceof Player) {
            return true;
         } else {
            context.reply(failureMessage);
            return false;
         }
      });
      return new FunctionalCommandBuilderImpl(this.predicates, this.tabHandler, this.permission, this.permissionMessage, this.description);
   }

   @Override
   public FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(String failureMessage) {
      Objects.requireNonNull(failureMessage, "failureMessage");
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         if (context.sender() instanceof ConsoleCommandSender) {
            return true;
         } else {
            context.reply(failureMessage);
            return false;
         }
      });
      return new FunctionalCommandBuilderImpl(this.predicates, this.tabHandler, this.permission, this.permissionMessage, this.description);
   }

   @Override
   public FunctionalCommandBuilder<T> assertUsage(String usage, String failureMessage) {
      Objects.requireNonNull(usage, "usage");
      Objects.requireNonNull(failureMessage, "failureMessage");
      List<String> usageParts = Splitter.on(" ").splitToList(usage);
      int requiredArgs = 0;

      for (String usagePart : usageParts) {
         if (!usagePart.startsWith("[") && !usagePart.endsWith("]")) {
            requiredArgs++;
         }
      }

      int finalRequiredArgs = requiredArgs;
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         if (context.args().size() >= finalRequiredArgs) {
            return true;
         } else {
            context.reply(failureMessage.replace("{usage}", "/" + context.label() + " " + usage));
            return false;
         }
      });
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> assertArgument(int index, Predicate<String> test, String failureMessage) {
      Objects.requireNonNull(test, "test");
      Objects.requireNonNull(failureMessage, "failureMessage");
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         String arg = context.rawArg(index);
         if (test.test(arg)) {
            return true;
         } else {
            context.reply(failureMessage.replace("{arg}", arg).replace("{index}", Integer.toString(index)));
            return false;
         }
      });
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> assertSender(Predicate<T> test, String failureMessage) {
      Objects.requireNonNull(test, "test");
      Objects.requireNonNull(failureMessage, "failureMessage");
      this.predicates.add((Predicate<CommandContext<?>>)context -> {
         T sender = (T)context.sender();
         if (test.test(sender)) {
            return true;
         } else {
            context.reply(failureMessage);
            return false;
         }
      });
      return this;
   }

   @Override
   public FunctionalCommandBuilder<T> tabHandler(FunctionalTabHandler tabHandler) {
      this.tabHandler = tabHandler;
      return this;
   }

   @Override
   public Command handler(FunctionalCommandHandler handler) {
      Objects.requireNonNull(handler, "handler");
      return new FunctionalCommand(this.predicates.build(), handler, this.tabHandler, this.permission, this.permissionMessage, this.description);
   }
}
