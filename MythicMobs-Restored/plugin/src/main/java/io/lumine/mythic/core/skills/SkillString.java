package io.lumine.mythic.core.skills;

import javax.annotation.Nullable;
import org.bukkit.ChatColor;

public class SkillString {
   @Nullable
   public static String parseMessageSpecialChars(@Nullable String s) {
      if (s == null) {
         return null;
      } else if (s.indexOf(60) == -1) {
         return ChatColor.translateAlternateColorCodes('&', s);
      } else {
         int len = s.length();
         StringBuilder sb = new StringBuilder(len);

         for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '<' && i + 3 < len) {
               char c1 = s.charAt(i + 1);
               if (c1 == '&' && i + 4 < len) {
                  char c2 = s.charAt(i + 2);
                  char c3 = s.charAt(i + 3);
                  if (i + 4 < len && s.charAt(i + 4) == '>') {
                     String rep = lookupTwoCharCode(c2, c3);
                     if (rep != null) {
                        sb.append(rep);
                        i += 4;
                        continue;
                     }
                  }

                  if (i + 7 < len && s.charAt(i + 7) == '>') {
                     if (c2 == 's' && s.regionMatches(i + 2, "skull>", 0, 6)) {
                        sb.append('\u2620');
                        i += 7;
                        continue;
                     }

                     if (c2 == 'h' && s.regionMatches(i + 2, "heart>", 0, 6)) {
                        sb.append('\u2764');
                        i += 7;
                        continue;
                     }
                  }
               } else if (c1 == '^' && i + 5 < len && s.charAt(i + 2) == 'd' && s.charAt(i + 3) == 'o' && s.charAt(i + 4) == 't') {
                  if (s.charAt(i + 5) == '>') {
                     sb.append('.');
                     i += 5;
                     continue;
                  }

                  if (i + 6 < len && s.charAt(i + 5) == '2' && s.charAt(i + 6) == '>') {
                     sb.append("<^dot>");
                     i += 6;
                     continue;
                  }
               }

               sb.append(c);
            } else {
               sb.append(c);
            }
         }

         return ChatColor.translateAlternateColorCodes('&', sb.toString());
      }
   }

   private static String lookupTwoCharCode(char c1, char c2) {
      return switch (c1) {
         case 'b' -> c2 == 's' ? "\\" : null;
         case 'c' -> c2 == 'o' ? ":" : (c2 == 'm' ? "," : null);
         case 'd' -> c2 == 'a' ? "-" : (c2 == 'q' ? "\"" : null);
         case 'e' -> c2 == 'q' ? "=" : null;
         case 'f' -> c2 == 's' ? "/" : null;
         case 'g' -> c2 == 't' ? ">" : null;
         default -> null;
         case 'l' -> c2 == 'b' ? "[" : (c2 == 'c' ? "{" : (c2 == 't' ? "<" : null));
         case 'n' -> c2 == 'l' ? "\n" : (c2 == 'm' ? "#" : null);
         case 'r' -> c2 == 'b' ? "]" : (c2 == 'c' ? "}" : null);
         case 's' -> c2 == 'q' ? "'" : (c2 == 'p' ? " " : (c2 == 'c' ? ";" : null));
      };
   }

   public static String unparseMessageSpecialChars(String s) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         StringBuilder sb = null;

         for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            String rep = switch (c) {
               case ' ' -> "<&sp>";
               case '\'' -> "<&sq>";
               case ',' -> "<&cm>";
               case '-' -> "<&da>";
               case '/' -> "<&fs>";
               case ';' -> "<&sc>";
               case '=' -> "<&eq>";
               case '[' -> "<&lb>";
               case '\\' -> "<&bs>";
               case ']' -> "<&rb>";
               case '{' -> "<&lc>";
               case '}' -> "<&rc>";
               default -> null;
            };
            if (rep != null) {
               if (sb == null) {
                  sb = new StringBuilder(len + 32);
                  sb.append(s, 0, i);
               }

               sb.append(rep);
            } else if (sb != null) {
               sb.append(c);
            }
         }

         return sb != null ? sb.toString() : s;
      }
   }

   @Nullable
   public static String convertLegacyVariables(@Nullable String s) {
      if (s == null) {
         return null;
      } else if (s.indexOf(60) == -1) {
         return s;
      } else {
         s = s.replace("<mob", "<caster");
         s = s.replace("<dropper", "<caster");
         s = s.replace("<player", "<caster");
         return s.replace("<killer", "<trigger");
      }
   }
}
