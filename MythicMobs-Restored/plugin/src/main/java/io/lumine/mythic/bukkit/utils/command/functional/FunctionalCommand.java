package io.lumine.mythic.bukkit.utils.command.functional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.AbstractCommand;
import io.lumine.mythic.bukkit.utils.command.CommandInterruptException;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@NonnullByDefault
class FunctionalCommand extends AbstractCommand {
   private final ImmutableList<Predicate<CommandContext<?>>> predicates;
   private final FunctionalCommandHandler handler;
   @Nullable
   private final FunctionalTabHandler tabHandler;

   FunctionalCommand(
      ImmutableList<Predicate<CommandContext<?>>> predicates,
      FunctionalCommandHandler handler,
      @Nullable FunctionalTabHandler tabHandler,
      @Nullable String permission,
      @Nullable String permissionMessage,
      @Nullable String description
   ) {
      this.predicates = predicates;
      this.handler = handler;
      this.tabHandler = tabHandler;
      this.permission = permission;
      this.permissionMessage = permissionMessage;
      this.description = description;
   }

   @Override
   public void call(@Nonnull CommandContext<?> context) throws CommandInterruptException {
      UnmodifiableIterator unmodifiableIterator = this.predicates.iterator();

      while (unmodifiableIterator.hasNext()) {
         Predicate<CommandContext<?>> predicate = (Predicate<CommandContext<?>>)unmodifiableIterator.next();
         if (!predicate.test(context)) {
            return;
         }
      }

      this.handler.handle(context);
   }

   @Nullable
   @Override
   public List<String> callTabCompleter(@Nonnull CommandContext<?> context) throws CommandInterruptException {
      if (this.tabHandler == null) {
         return null;
      } else {
         UnmodifiableIterator unmodifiableIterator = this.predicates.iterator();

         while (unmodifiableIterator.hasNext()) {
            Predicate<CommandContext<?>> predicate = (Predicate<CommandContext<?>>)unmodifiableIterator.next();
            if (!predicate.test(context)) {
               return null;
            }
         }

         return this.tabHandler.handle(context);
      }
   }
}
