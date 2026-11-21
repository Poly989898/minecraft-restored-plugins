package io.lumine.mythic.core.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.PlaceholderAPISupport;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.all.MetaTargetPlaceholder;
import io.lumine.mythic.core.skills.placeholders.parsers.tokens.GenericPlaceholderParserToken;
import io.lumine.mythic.core.skills.placeholders.parsers.tokens.LegacyPlaceholderParserToken;
import io.lumine.mythic.core.skills.placeholders.parsers.tokens.PlaceholderParserToken;
import io.lumine.mythic.core.skills.placeholders.parsers.tokens.StringPlaceholderParserToken;
import io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ItemPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.LocationPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.SpawnerPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.TargetPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.WorldPlaceholder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

public class PlaceholderParser {
   @Nullable
   protected String strValue;
   @Nullable
   protected String strUnparsedValue;
   protected boolean papi;
   protected final PlaceholderParser.PlaceholderParserTokenArray tokens = new PlaceholderParser.PlaceholderParserTokenArray();
   @Getter @Setter
   protected boolean doMath = false;
   private static final AtomicReference<Object> placeholderAPI = new AtomicReference<>();

   public PlaceholderParser() {
   }

   static boolean containsMathOperator(@Nullable String string) {
      return string != null
         && (string.contains("+") || string.contains("-") || string.contains("*") || string.contains("/") || string.contains("^") || string.contains("%"));
   }

   public PlaceholderParser(@Nullable String string) {
      if (string == null) {
         this.strValue = null;
         this.strUnparsedValue = null;
         this.papi = false;
      } else {
         try {
            if (string.startsWith("\"") && string.endsWith("\"")) {
               string = string.substring(1, string.length() - 1);
            } else if (string.startsWith("'") && string.endsWith("'")) {
               string = string.substring(1, string.length() - 1);
            }
         } catch (Exception e) {
            MythicLogger.error("Message '" + string + "' is incorrectly configured.", e);
            string = "INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.";
         }

         this.strUnparsedValue = SkillString.convertLegacyVariables(string);
         this.strValue = SkillString.parseMessageSpecialChars(this.strUnparsedValue);
         if (containsMathOperator(this.strValue)) {
            this.doMath = true;
         }

         this.papi = MythicBukkit.inst().getCompatibility().getPlaceholderAPI().isPresent() && this.strUnparsedValue.contains("%");
         this.getPlaceholderManager().registerParser(this);
         this.tokenizeString();
      }
   }

   public void tokenizeString() {
      this.tokens.clear();
      if (this.strValue != null) {
         this.tokenize(this.strValue);
      }
   }

   private void tokenize(String str) {
      int depth = 0;
      int lastIndex = 0;

      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (ch == '<') {
            if (depth == 0) {
               if (i + 1 < str.length()) {
                  char nextCh = str.charAt(i + 1);
                  if (nextCh == ' ' || nextCh == '=' || nextCh == '<') {
                     continue;
                  }
               }

               if (lastIndex < i) {
                  this.tokens.add(new StringPlaceholderParserToken(str.substring(lastIndex, i)));
               }

               lastIndex = i;
            }

            depth++;
         } else if (ch == '>' && depth != 0) {
            if (--depth == 0) {
               String full = str.substring(lastIndex, i + 1);
               String inner = str.substring(lastIndex + 1, i);
               PlaceholderExecutor.PlaceholderEntry placeholderEntry = this.getPlaceholderManager().getPlaceholder(inner);
               if (placeholderEntry.placeholder() == null) {
                  if (inner.indexOf(60) >= 0) {
                     this.tokens.add(new StringPlaceholderParserToken("<"));
                     this.tokenize(inner);
                     this.tokens.add(new StringPlaceholderParserToken(">"));
                  } else {
                     this.tokens.add(new StringPlaceholderParserToken(full));
                  }
               } else if (placeholderEntry.placeholder() instanceof GenericPlaceholder<?> genericPlaceholder) {
                  this.tokens.add(new GenericPlaceholderParserToken(genericPlaceholder));
               } else {
                  PlaceholderParser.FoundPlaceholder foundPlaceholder = new PlaceholderParser.FoundPlaceholder(full, placeholderEntry);
                  this.tokens.add(new LegacyPlaceholderParserToken(foundPlaceholder));
               }

               lastIndex = i + 1;
            }
         }
      }

      if (lastIndex < str.length()) {
         this.tokens.add(new StringPlaceholderParserToken(str.substring(lastIndex)));
      }
   }

   @Deprecated
   private static String replaceFirstLiteral(String input, String token, String replacement) {
      if (replacement == null) {
         throw new NullPointerException();
      } else {
         int idx = input.indexOf(token);
         if (idx < 0) {
            return input;
         } else {
            StringBuilder sb = new StringBuilder(input.length() - token.length() + replacement.length());
            sb.append(input, 0, idx);
            sb.append(replacement);
            sb.append(input, idx + token.length(), input.length());
            return sb.toString();
         }
      }
   }

   protected String parse(PlaceholderContext context) {
      String string = this.parsePlaceholders(context);
      if (string != null) {
         string = this.parsePapi(context, string);
      }

      return string;
   }

   protected String parsePlaceholders(PlaceholderContext context) {
      if (!this.tokens.isStatic && this.strValue != null) {
         int size = this.tokens.size();
         if (size == 1) {
            return this.tokens.get(0).apply(context);
         } else {
            StringBuilder stringBuilder = new StringBuilder(this.strValue.length());

            for (int i = 0; i < size; i++) {
               stringBuilder.append(this.tokens.get(i).apply(context));
            }

            return stringBuilder.toString();
         }
      } else {
         return this.strValue;
      }
   }

   protected String parsePapi(PlaceholderContext context, String string) {
      if (this.papi) {
         AbstractEntity entity = context.entity();
         return entity != null && entity.isPlayer() ? getPlaceholderAPI().parse(string, entity.asPlayer()) : getPlaceholderAPI().parse(string);
      } else {
         return string;
      }
   }

   public boolean isStatic() {
      return this.tokens.isStatic && !this.doMath && !this.papi;
   }

   public boolean isPresent() {
      return this.strValue != null && !this.strValue.isEmpty();
   }

   protected PlaceholderExecutor getPlaceholderManager() {
      return MythicBukkit.inst().getPlaceholderManager();
   }

   public String serialize() {
      return this.strUnparsedValue;
   }

   @Generated
   public static PlaceholderAPISupport getPlaceholderAPI() {
      Object $value = placeholderAPI.get();
      if ($value == null) {
         synchronized (placeholderAPI) {
            $value = placeholderAPI.get();
            if ($value == null) {
               PlaceholderAPISupport actualValue = MythicBukkit.inst().getCompatibility().getPlaceholderAPI().get();
               $value = actualValue == null ? placeholderAPI : actualValue;
               placeholderAPI.set($value);
            }
         }
      }

      return (PlaceholderAPISupport)($value == placeholderAPI ? null : $value);
   }

   @Deprecated
   public static class FoundPlaceholder {
      public static final byte KIND_GENERAL = 1;
      public static final byte KIND_META = 2;
      public static final byte KIND_ENTITY = 3;
      public static final byte KIND_LOCATION = 4;
      public static final byte KIND_TARGET = 5;
      public static final byte KIND_METATARGET = 6;
      public static final byte KIND_ITEM = 7;
      public static final byte KIND_WORLD = 8;
      public static final byte KIND_SPAWNER = 9;
      public final String token;
      public final String arg;
      public final Placeholder placeholder;
      public final byte kind;
      protected int count;

      protected FoundPlaceholder(String key, PlaceholderExecutor.PlaceholderEntry entry) {
         this.token = "<" + key + ">";
         this.arg = entry.arg();
         Placeholder pl = entry.placeholder();
         this.placeholder = pl;
         if (pl instanceof GenericPlaceholder) {
            this.kind = 0;
         } else if (pl instanceof GeneralPlaceholder) {
            this.kind = 1;
         } else if (pl instanceof MetaPlaceholder) {
            this.kind = 2;
         } else if (pl instanceof EntityPlaceholder) {
            this.kind = 3;
         } else if (pl instanceof LocationPlaceholder) {
            this.kind = 4;
         } else if (pl instanceof TargetPlaceholder) {
            this.kind = 5;
         } else if (pl instanceof MetaTargetPlaceholder) {
            this.kind = 6;
         } else if (pl instanceof ItemPlaceholder) {
            this.kind = 7;
         } else if (pl instanceof WorldPlaceholder) {
            this.kind = 8;
         } else if (pl instanceof SpawnerPlaceholder) {
            this.kind = 9;
         } else {
            this.kind = 0;
         }
      }
   }

   @Getter
   protected static class PlaceholderParserTokenArray extends ArrayList<PlaceholderParserToken<?>> {
      private boolean isStatic = true;

      private void updateStatic(PlaceholderParserToken<?> token) {
         if (this.isStatic && !token.isStatic()) {
            this.isStatic = false;
         }
      }

      private void updateStatic(Collection<? extends PlaceholderParserToken<?>> tokens) {
         if (this.isStatic) {
            for (PlaceholderParserToken<?> token : tokens) {
               if (!token.isStatic()) {
                  this.isStatic = false;
                  return;
               }
            }
         }
      }

      public boolean add(PlaceholderParserToken<?> placeholderParserToken) {
         this.updateStatic(placeholderParserToken);
         return super.add(placeholderParserToken);
      }

      public void add(int index, PlaceholderParserToken<?> element) {
         this.updateStatic(element);
         super.add(index, element);
      }

      @Override
      public boolean addAll(Collection<? extends PlaceholderParserToken<?>> c) {
         this.updateStatic(c);
         return super.addAll(c);
      }

      @Override
      public boolean addAll(int index, Collection<? extends PlaceholderParserToken<?>> c) {
         this.updateStatic(c);
         return super.addAll(index, c);
      }

      public PlaceholderParserToken<?> set(int index, PlaceholderParserToken<?> element) {
         this.updateStatic(element);
         return super.set(index, element);
      }

      @Override
      public void clear() {
         this.isStatic = true;
         super.clear();
      }
   }
}
