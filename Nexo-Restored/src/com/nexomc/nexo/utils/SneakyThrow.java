package com.nexomc.nexo.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * Kotlin has no checked exceptions; the original code let Brigadier's {@link CommandSyntaxException}
 * propagate out of command lambdas. This helper keeps that behaviour in Java.
 */
public final class SneakyThrow {
   private SneakyThrow() {}

   public static List<Player> resolve(PlayerSelectorArgumentResolver resolver, CommandSourceStack source) {
      try {
         return resolver.resolve(source);
      } catch (CommandSyntaxException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   /** Rethrows any throwable without declaring it. Use as {@code throw SneakyThrow.sneaky(e);}. */
   public static RuntimeException sneaky(Throwable t) {
      throw SneakyThrow.<RuntimeException>rethrow(t);
   }

   @SuppressWarnings("unchecked")
   private static <T extends Throwable> T rethrow(Throwable t) throws T {
      throw (T)t;
   }
}
