package io.lumine.mythic.lib.api.placeholders;

import io.lumine.mythic.lib.comp.placeholder.MythicPlaceholderAPIHook;
import java.util.ArrayList;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicPlaceholders {
   @NotNull
   static final ArrayList<MythicPlaceholder> objectParsers = new ArrayList<>();

   @NotNull
   public static String parse(@NotNull String str, @NotNull Object... objects) {
      if (!str.contains("%")) {
         return str;
      }

      for (Object o : objects) {
         if (o != null) {
            ArrayList<MythicPlaceholder> parsers = getParserFor(o);
            if (!parsers.isEmpty()) {
               for (MythicPlaceholder p : parsers) {
                  if (p != null) {
                     String result = parseWithPlaceholder(str, o, p);
                     if (result != null) {
                        str = result;
                     }
                  }
               }
            }
         }
      }

      return str;
   }

   @Nullable
   public static String parseWithPlaceholder(@NotNull String str, @NotNull Object o, @NotNull MythicPlaceholder parser) {
      if (!str.contains("%")) {
         return null;
      }

      if (!parser.forUseWith(o)) {
         return null;
      }

      if (parser instanceof MythicPlaceholderAPIHook && o instanceof OfflinePlayer) {
         return parser.parse(str, o);
      }

      String builtIdentifier = "%" + parser.getMythicIdentifier() + "_";
      String[] args = str.split(builtIdentifier);
      StringBuilder built = new StringBuilder();
      boolean within = false;

      for (String arg : args) {
         if (!within) {
            within = true;
            built.append(arg);
         }

         if (arg != null) {
            int percentEnd = arg.indexOf("%");
            if (percentEnd < 0) {
               built.append(builtIdentifier).append(arg);
            } else {
               String parsing = arg.substring(0, percentEnd);
               String parsed = parser.parse(parsing, o);
               if (parsed == null) {
                  built.append(builtIdentifier).append(arg);
               } else {
                  String rem = arg.substring(percentEnd + 1);
                  built.append(parsed).append(rem);
               }
            }
         }
      }

      return built.toString();
   }

   @NotNull
   public static ArrayList<MythicPlaceholder> getParserFor(@NotNull Object obj) {
      ArrayList<MythicPlaceholder> ret = new ArrayList<>();

      for (MythicPlaceholder mp : objectParsers) {
         if (mp != null && mp.forUseWith(obj)) {
            ret.add(mp);
         }
      }

      return ret;
   }

   public static void registerPlaceholder(@NotNull MythicPlaceholder placeholder) {
      for (MythicPlaceholder mp : objectParsers) {
         if (mp.getMythicIdentifier().equals(placeholder.getMythicIdentifier())) {
            return;
         }
      }

      objectParsers.add(placeholder);
   }
}
