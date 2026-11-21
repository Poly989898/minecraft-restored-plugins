package io.lumine.mythic.bukkit.utils.config;

import io.lumine.mythic.bukkit.utils.logging.ConsoleColor;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.ChatColor;

public class LineConfig {
   private String fileName = "Unknown";
   private String key;
   private String line;
   private HashMap<String, String> config = new HashMap<>();

   public static LineConfig of(String line) {
      return new LineConfig(line);
   }

   public LineConfig(String fileName, String line) {
      this(line);
      this.fileName = fileName;
   }

   public LineConfig(File file, String line) {
      this(line);
      this.fileName = file.getName();
   }

   public LineConfig(String s) {
      this.line = unparseBlock(s);
      s = parseBlock(s);
      if (s.contains("{") && s.contains("}")) {
         this.key = s.substring(0, s.indexOf("{"));
         int startPos = s.indexOf(123) + 1;
         int lastPos = s.lastIndexOf(125);
         int count = 0;
         s = s.substring(startPos, lastPos);

         for (char c : s.toCharArray()) {
            if (c == '{') {
               count++;
            }

            if (c == '}') {
               count--;
            }
         }

         if (count != 0) {
            Log.error(ConsoleColor.RED + "Error loading LineConfig: Unbalanced Braces" + ConsoleColor.WHITE);
            Log.error(ConsoleColor.RED + "[Line]: " + ConsoleColor.WHITE + s);
            return;
         }

         if (s.length() == 0) {
            return;
         }

         int start = 0;
         int pos = 0;
         int depth = 0;
         String lastKey = "";
         String lastVal = "";
         boolean inb = false;
         s = s + "}";

         for (char c : s.toCharArray()) {
            if (c == '{' || c == '[') {
               depth++;
            }

            if (c == '}' || c == ']') {
               depth--;
            }

            if (c == ';' && depth == 0 || c == '}' && depth < 0) {
               try {
                  String element = s.substring(start, pos);
                  if (pos - start > 0 && element.length() > 0) {
                     String key = element.substring(0, element.indexOf(61)).trim().toLowerCase();
                     String val = element.substring(element.indexOf(61) + 1).trim();
                     this.config.put(key, val);
                  }
               } catch (Exception e) {
               } finally {
                  start = pos + 1;
               }
            }

            pos++;
         }
      } else if (s.contains("[") && s.contains("]")) {
         try {
            String[] split = s.split("\\[");
            s = split[1];
            String[] split2 = s.split("\\]");
            s = split2[0];
         } catch (ArrayIndexOutOfBoundsException ex) {
            Log.error(ConsoleColor.RED + "Error loading LineConfig: Invalid Syntax" + ConsoleColor.WHITE);
            Log.error(ConsoleColor.RED + "[Line]: " + ConsoleColor.WHITE + s);
            return;
         }
      } else {
         this.key = s;
      }
   }

   public String getLine() {
      return this.line;
   }

   public int size() {
      return this.config.size();
   }

   public Set<Entry<String, String>> entrySet() {
      return this.config.entrySet();
   }

   public static String getKey(String s) {
      String key = null;
      if (s.contains("{")) {
         key = s.substring(0, s.indexOf("{"));
      } else if (s.contains("[")) {
         key = s.substring(0, s.indexOf("["));
      } else {
         key = s;
      }

      return key;
   }

   public boolean getBoolean(String key) {
      return this.getBoolean(key, false);
   }

   public boolean getBoolean(String key, boolean def) {
      key = key.toLowerCase();
      String s = this.config.get(key);
      if (s == null) {
         return def;
      } else {
         try {
            return Boolean.parseBoolean(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public boolean getBoolean(String[] key, boolean def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      if (s == null) {
         return def;
      } else {
         try {
            return Boolean.parseBoolean(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public String getString(String key) {
      return this.getString(key, null);
   }

   public String getString(String[] key) {
      return this.getString(key, null);
   }

   public String getString(String key, String def) {
      String s = this.config.get(key.toLowerCase());
      return s == null ? def : s;
   }

   public String getString(String[] key, String def, String... args) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            return s;
         }
      }

      for (String a : args) {
         if (a != null) {
            return a;
         }
      }

      return def;
   }

   public int getInteger(String key) {
      return this.getInteger(key, 0);
   }

   public int getInteger(String[] key) {
      return this.getInteger(key, 0);
   }

   public int getInteger(String key, int def) {
      String s = this.config.get(key.toLowerCase());
      if (s == null) {
         return def;
      } else {
         try {
            return Integer.parseInt(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public int getInteger(String[] key, int def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      if (s == null) {
         return def;
      } else {
         try {
            return Integer.parseInt(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public double getDouble(String key) {
      return this.getDouble(key, 0.0);
   }

   public double getDouble(String[] key) {
      return this.getDouble(key, 0.0);
   }

   public double getDouble(String key, double def) {
      String s = this.config.get(key.toLowerCase());
      if (s == null) {
         return def;
      } else {
         try {
            return Double.parseDouble(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public double getDouble(String[] key, double def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      if (s == null) {
         return def;
      } else {
         try {
            return Double.parseDouble(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public float getFloat(String key) {
      return this.getFloat(key, 0.0F);
   }

   public float getFloat(String[] key) {
      return this.getFloat(key, 0.0F);
   }

   public float getFloat(String key, float def) {
      String s = this.config.get(key.toLowerCase());
      if (s == null) {
         return def;
      } else {
         try {
            return Float.parseFloat(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public float getFloat(String[] key, float def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      if (s == null) {
         return def;
      } else {
         try {
            return Float.parseFloat(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public long getLong(String key) {
      return this.getLong(key, 0L);
   }

   public long getLong(String[] key) {
      return this.getLong(key, 0L);
   }

   public long getLong(String key, long def) {
      String s = this.config.get(key.toLowerCase());
      if (s == null) {
         return def;
      } else {
         try {
            return Long.parseLong(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public long getLong(String[] key, long def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      if (s == null) {
         return def;
      } else {
         try {
            return Long.parseLong(s);
         } catch (Exception e) {
            return def;
         }
      }
   }

   public Color getColor(String[] key, String def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            break;
         }
      }

      return this.getColor(s, def);
   }

   public Color getColor(String key, String def) {
      String c = key == null ? def : this.getString(key, def);
      if (c == null) {
         return null;
      } else if (c.startsWith("#") && c.length() == 7) {
         return Color.decode(c);
      } else {
         Log.error("Couldn't parse color '" + c + "': must be in Hex or R,G,B format");
         return Color.RED;
      }
   }

   public List<Locus> getLocationList(String[] key, List<Locus> def) {
      String s = null;

      for (String k : key) {
         s = this.config.get(k.toLowerCase());
         if (s != null) {
            return this.getLocationList(k, def);
         }
      }

      return def;
   }

   public List<Locus> getLocationList(String key, List<Locus> def) {
      List<Locus> result = new ArrayList<>();
      String internalName = this.getString(key);
      internalName = unparseBlock(internalName);
      if (internalName == null) {
         return def;
      } else {
         try {
            if (internalName.startsWith("[") && internalName.endsWith("]")) {
               internalName = internalName.substring(1, internalName.length() - 1);
               internalName = parseBlock(internalName);
               internalName = internalName.replace("- ", ";");
               String[] split = internalName.split(";");
               new ArrayList();

               for (String e : split) {
                  String[] sp = e.split(",");
                  double x = Double.valueOf(sp[0]);
                  double y = Double.valueOf(sp[1]);
                  double z = Double.valueOf(sp[2]);
                  result.add(Locus.of(x, y, z));
               }
            } else {
               String[] sp = internalName.split(",");
               double x = Double.valueOf(sp[0]);
               double y = Double.valueOf(sp[1]);
               double z = Double.valueOf(sp[2]);
               result.add(Locus.of(x, y, z));
            }
         } catch (Error | Exception ex) {
            Log.error("LocationList is improperly configured");
            ex.printStackTrace();
         }

         return result;
      }
   }

   public static String unparseBlock(String s) {
      if (s.contains("\"")) {
         String[] split = s.split("\"");
         int i = 0;
         String ns = "";

         for (String ss : split) {
            if (i % 2 == 1) {
               ns = ns.concat("\"" + unparseSpecialChars(ss) + "\"");
            } else {
               ns = ns.concat(ss);
            }

            i++;
         }

         s = ns;
      }

      if (s.contains("'")) {
         String[] split = s.split("'");
         int i = 0;
         String ns = "";

         for (String ss : split) {
            if (i % 2 == 1) {
               ns = ns.concat("'" + unparseSpecialChars(ss) + "'");
            } else {
               ns = ns.concat(ss);
            }

            i++;
         }

         s = ns;
      }

      int pos = 0;
      int count = 0;
      int ss = 0;
      int sc = 0;
      int ec = 0;
      String parsed = "";

      for (char c : s.toCharArray()) {
         if (c == '{') {
            if (count == 0) {
               sc = pos;
            }

            count++;
         }

         if (c == '}') {
            if (--count == 0) {
               String f = s.substring(ss, sc);
               String m = s.substring(sc, pos).replace(" ", "<&csp>").replace("-", "<&da>");
               String e = s.substring(pos);
               parsed = parsed + f + m;
               ss = pos;
            }
         }

         pos++;
      }

      return parsed + s.substring(ss, pos);
   }

   public static String parseBlock(String s) {
      return s.replace("<&csp>", " ").replace("<&da>", "-").trim();
   }

   public static String parseSpecialChars(String s) {
      if (s == null) {
         return null;
      } else {
         s = s.replace("<&co>", ":");
         s = s.replace("<&sq>", "'");
         s = s.replace("<&da>", "-");
         s = s.replace("<&bs>", "\\");
         s = s.replace("<&fs>", "/");
         s = s.replace("<&sp>", " ");
         s = s.replace("<&cm>", ",");
         s = s.replace("<&sc>", ";");
         s = s.replace("<&eq>", "=");
         s = s.replace("<&dq>", "\"");
         s = s.replace("<&rb>", "]");
         s = s.replace("<&lb>", "[");
         s = s.replace("<&rc>", "}");
         s = s.replace("<&lc>", "{");
         s = s.replace("<&nl>", "\n");
         s = s.replace("<&nm>", "#");
         s = s.replace("<&skull>", "\u2620");
         s = s.replace("<&heart>", "\u2764");
         return ChatColor.translateAlternateColorCodes('&', s);
      }
   }

   public static String unparseSpecialChars(String s) {
      if (s == null) {
         return null;
      } else {
         s = s.replace("-", "<&da>");
         s = s.replace("\\", "<&bs>");
         s = s.replace("/", "<&fs>");
         s = s.replace(" ", "<&sp>");
         s = s.replace(",", "<&cm>");
         s = s.replace(";", "<&sc>");
         s = s.replace("=", "<&eq>");
         s = s.replace("{", "<&lc>");
         s = s.replace("}", "<&rc>");
         s = s.replace("[", "<&lb>");
         s = s.replace("]", "<&rb>");
         return s.replace("'", "<&sq>");
      }
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getKey() {
      return this.key;
   }
}
