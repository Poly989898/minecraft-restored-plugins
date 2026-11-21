package io.lumine.mythic.bukkit.utils;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.argument.ArgumentParserRegistry;
import io.lumine.mythic.bukkit.utils.command.argument.SimpleParserRegistry;
import io.lumine.mythic.bukkit.utils.command.functional.FunctionalCommandBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.time.DurationParser;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@NonnullByDefault
public final class Commands {
   private static final ArgumentParserRegistry PARSER_REGISTRY = new SimpleParserRegistry();

   @Nonnull
   public static ArgumentParserRegistry parserRegistry() {
      return PARSER_REGISTRY;
   }

   public static FunctionalCommandBuilder<CommandSender> create() {
      return FunctionalCommandBuilder.newBuilder();
   }

   private Commands() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   static {
      PARSER_REGISTRY.register(String.class, Optional::of);
      PARSER_REGISTRY.register(Number.class, Numbers::parse);
      PARSER_REGISTRY.register(Integer.class, Numbers::parseIntegerOpt);
      PARSER_REGISTRY.register(Long.class, Numbers::parseLongOpt);
      PARSER_REGISTRY.register(Float.class, Numbers::parseFloatOpt);
      PARSER_REGISTRY.register(Double.class, Numbers::parseDoubleOpt);
      PARSER_REGISTRY.register(Byte.class, Numbers::parseByteOpt);
      PARSER_REGISTRY.register(
         Boolean.class, s -> s.equalsIgnoreCase("true") ? Optional.of(true) : (s.equalsIgnoreCase("false") ? Optional.of(false) : Optional.empty())
      );
      PARSER_REGISTRY.register(UUID.class, s -> {
         try {
            return Optional.of(UUID.fromString(s));
         } catch (IllegalArgumentException e) {
            return Optional.empty();
         }
      });
      PARSER_REGISTRY.register(Player.class, s -> {
         try {
            return Players.get(UUID.fromString(s));
         } catch (IllegalArgumentException ex) {
            return Players.get(s);
         }
      });
      PARSER_REGISTRY.register(OfflinePlayer.class, s -> {
         try {
            return Players.getOffline(UUID.fromString(s));
         } catch (IllegalArgumentException ex2) {
            return Players.getOffline(s);
         }
      });
      PARSER_REGISTRY.register(World.class, Worlds::get);
      PARSER_REGISTRY.register(Duration.class, DurationParser::parseSafely);
   }
}
