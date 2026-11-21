package io.lumine.mythic.core.utils;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class MythicUtil {
   public static final float DEGTORAD = (float) (Math.PI / 180.0);
   public static final float RADTODEG = (float) (180.0 / Math.PI);
   private static final int CHAR_PIXEL_LENGTH = 6;
   private static final int SPACE_PIXEL_LENGTH = 4;
   private List<Character> singlePixels = new ArrayList<>(List.of(' ', ' ', ' ', ' '));

   public static boolean matchNumber(String s, double i) {
      if (s == null) {
         return false;
      } else {
         try {
            s = s.replace(" ", "");
            if (s.contains("=")) {
               double d = Double.parseDouble(s.substring(1));
               if (i == d) {
                  return true;
               }
            } else if (s.contains(">")) {
               double d = Double.parseDouble(s.substring(1));
               if (i > d) {
                  return true;
               }
            } else if (s.contains("<")) {
               double d = Double.parseDouble(s.substring(1));
               if (i < d) {
                  return true;
               }
            } else if (s.contains("to")) {
               String[] split = s.split("to");
               double d1 = Double.parseDouble(split[0]);
               double d2 = Double.parseDouble(split[1]);
               if (i >= d1 && i <= d2) {
                  return true;
               }
            } else if (s.contains("-")) {
               String[] split = s.split("-");
               double d1 = Double.parseDouble(split[0]);
               double d2 = Double.parseDouble(split[1]);
               if (i >= d1 && i <= d2) {
                  return true;
               }
            } else {
               double d = Double.parseDouble(s);
               if (i == d) {
                  return true;
               }
            }

            return false;
         } catch (Exception e) {
            return false;
         }
      }
   }

   public static Object getPrivateField(String fieldName, Class clazz, Object object) {
      Object o = null;

      try {
         Field field = clazz.getDeclaredField(fieldName);
         field.setAccessible(true);
         o = field.get(object);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalAccessException ex) {
         ex.printStackTrace();
      }

      return o;
   }

   public static void setPrivateField(String fieldName, Class clazz, Object object, Object value) {
      try {
         Field field = clazz.getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(object, value);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalAccessException ex) {
         ex.printStackTrace();
      }
   }

   public static LivingEntity getTargetedEntity(Player player) {
      int range = 32;
      List<Entity> ne;
      if (ServerVersion.isFolia() ? !FoliaHelper.isOwnedByCurrentRegion(player) : !Bukkit.isPrimaryThread()) {
         try {
            ne = Promise.<List<Entity>>supplyingSync(() -> player.getNearbyEntities(range, range, range)).get();
         } catch (ExecutionException | InterruptedException ex2) {
            ex2.printStackTrace();
            return null;
         }
      } else {
         ne = player.getNearbyEntities(range, range, range);
      }

      List<LivingEntity> entities = new ArrayList<>();

      for (Entity en : ne) {
         if (en instanceof LivingEntity) {
            entities.add((LivingEntity)en);
         }
      }

      LivingEntity target = null;

      BlockIterator bi;
      try {
         bi = new BlockIterator(player, range);
      } catch (IllegalStateException ex3) {
         return null;
      }

      while (bi.hasNext()) {
         Block b = bi.next();
         int bx = b.getX();
         int by = b.getY();
         int bz = b.getZ();
         Material material = b.getType();
         if (material != Material.BARRIER && (material.isOccluding() || material.isSolid())) {
            break;
         }

         for (LivingEntity e : entities) {
            Location l = e.getLocation();
            double ex = l.getX();
            double ey = l.getY();
            double ez = l.getZ();
            if (bx - 0.75 <= ex && ex <= bx + 1.75 && bz - 0.75 <= ez && ez <= bz + 1.75 && by - 1 <= ey && ey <= by + 2.5) {
               if (e == null || !(e instanceof Player) || ((Player)e).getGameMode() != GameMode.CREATIVE) {
                  return e;
               }

               target = null;
            }
         }
      }

      return null;
   }

   public static void rotateVector(Vector v, float degrees) {
      double rad = Math.toRadians(degrees);
      double sin = Math.sin(rad);
      double cos = Math.cos(rad);
      double x = v.getX() * cos - v.getZ() * sin;
      double z = v.getX() * sin + v.getZ() * cos;
      v.setX(x);
      v.setZ(z);
   }

   public static Double calculateLaunchAngle(Location from, Location to, double v, double elevation, double g) {
      Vector vector = from.clone().subtract(to).toVector();
      Double distance = Math.sqrt(Math.pow(vector.getX(), 2.0) + Math.pow(vector.getZ(), 2.0));
      double v2 = Math.pow(v, 2.0);
      double v4 = Math.pow(v, 4.0);
      double check = g * (g * Math.pow(distance, 2.0) + 2.0 * elevation * v2);
      return v4 < check ? null : Math.atan((v2 - Math.sqrt(v4 - check)) / (g * distance));
   }

   public static double calculateHangtime(double launchAngle, double v, double elev, double g) {
      double a = v * Math.sin(launchAngle);
      double b = -2.0 * g * elev;
      return Math.pow(a, 2.0) + b < 0.0 ? 0.0 : (a + Math.sqrt(Math.pow(a, 2.0) + b)) / g;
   }

   public static Vector normalizeVector(Vector victor) {
      double mag = Math.sqrt(Math.pow(victor.getX(), 2.0) + Math.pow(victor.getY(), 2.0) + Math.pow(victor.getZ(), 2.0));
      return mag != 0.0 ? victor.multiply(1.0 / mag) : victor.multiply(0);
   }

   public static Location moveBukkit(Location loc, Vector offset) {
      return moveBukkit(loc, offset.getX(), offset.getY(), offset.getZ());
   }

   public static Location moveBukkit(Location loc, double dx, double dy, double dz) {
      Vector off = rotateBukkit(loc.getYaw(), loc.getPitch(), dx, dy, dz);
      double x = loc.getX() + off.getX();
      double y = loc.getY() + off.getY();
      double z = loc.getZ() + off.getZ();
      return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
   }

   public static Vector rotateBukkit(float yaw, float pitch, Vector value) {
      return rotateBukkit(yaw, pitch, value.getX(), value.getY(), value.getZ());
   }

   public static Vector rotateBukkit(float yaw, float pitch, double x, double y, double z) {
      return new Vector(x, y, z).rotateAroundX(pitch * (float) (Math.PI / 180.0)).rotateAroundY(yaw * (float) (Math.PI / 180.0));
   }

   public static AbstractLocation move(AbstractLocation loc, AbstractVector offset) {
      return move(loc, offset.getX(), offset.getY(), offset.getZ());
   }

   public static AbstractLocation move(AbstractLocation loc, double dx, double dy, double dz) {
      AbstractVector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
      double x = loc.getX() + off.getX();
      double y = loc.getY() + off.getY();
      double z = loc.getZ() + off.getZ();
      return new AbstractLocation(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
   }

   public static AbstractVector rotate(float yaw, float pitch, AbstractVector value) {
      return rotate(yaw, pitch, value.getX(), value.getY(), value.getZ());
   }

   public static AbstractVector rotate(float yaw, float pitch, double x, double y, double z) {
      return new AbstractVector(-z, y, -x).rotateAroundX(pitch * (float) (Math.PI / 180.0)).rotateAroundY(-yaw * (float) (Math.PI / 180.0));
   }

   @Deprecated
   public static AbstractLocation move(boolean faulty, AbstractLocation loc, double dx, double dy, double dz) {
      return faulty ? faultyMove(loc, dx, dy, dz) : move(loc, dx, dy, dz);
   }

   @Deprecated
   private static AbstractLocation faultyMove(AbstractLocation loc, double dx, double dy, double dz) {
      AbstractVector off = faultyRotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
      double x = loc.getX() + off.getX();
      double y = loc.getY() + off.getY();
      double z = loc.getZ() + off.getZ();
      return new AbstractLocation(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
   }

   @Deprecated
   private static AbstractVector faultyRotate(float yaw, float pitch, double x, double y, double z) {
      float angle = yaw * (float) (Math.PI / 180.0);
      double sinyaw = Math.sin(angle);
      double cosyaw = Math.cos(angle);
      angle = pitch * (float) (Math.PI / 180.0);
      double sinpitch = Math.sin(angle);
      double cospitch = Math.cos(angle);
      double newx = 0.0;
      double newy = 0.0;
      double newz = 0.0;
      newz -= x * cosyaw;
      newz -= y * sinyaw * sinpitch;
      newz -= z * sinyaw * cospitch;
      newx += x * sinyaw;
      newx -= y * cosyaw * sinpitch;
      newx -= z * cosyaw * cospitch;
      newy += y * cospitch;
      newy -= z * sinpitch;
      return new AbstractVector(newx, newy, newz);
   }

   public static UUID getUUIDFromString(String s) {
      String md5 = getMD5(s);
      String uuid = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-" + md5.substring(16, 20) + "-" + md5.substring(20);
      return UUID.fromString(uuid);
   }

   public static String getMD5(String input) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] messageDigest = md.digest(input.getBytes());
         BigInteger number = new BigInteger(1, messageDigest);
         String hashtext = number.toString(16);

         while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
         }

         return hashtext;
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   public static String[] wrapString(String text, int maxLineLength) {
      StringBuilder result = new StringBuilder();
      text = text.replace("\\n", "\n");
      String[] lines = text.split("\n", -1);

      for (String line : lines) {
         StringBuilder lineBuilder = new StringBuilder();
         String[] words = line.split(" ", -1);
         int currentLength = 0;

         for (String word : words) {
            if (currentLength + word.length() > maxLineLength) {
               if (lineBuilder.length() > 0) {
                  result.append(lineBuilder.toString().trim()).append("\n");
                  lineBuilder = new StringBuilder();
               }

               currentLength = 0;
            }

            lineBuilder.append(word).append(" ");
            currentLength += word.length() + 1;
         }

         if (lineBuilder.length() > 0) {
            result.append(lineBuilder.toString().trim());
            if (!line.equals(lines[lines.length - 1])) {
               result.append("\n");
            }
         }
      }

      return result.toString().split("\n");
   }

   public static List<String> splitIgnoringParentheses(String input) {
      List<String> splitList = new ArrayList<>();
      int openParentheses = 0;
      StringBuilder segment = new StringBuilder();
      int len = input.length();

      for (int i = 0; i < len; i++) {
         char c = input.charAt(i);
         if (c == '(') {
            openParentheses++;
         } else if (c == ')') {
            openParentheses--;
         }

         if (c == ' ' && openParentheses == 0) {
            splitList.add(segment.toString());
            segment.setLength(0);
         } else {
            segment.append(c);
         }
      }

      if (segment.length() > 0) {
         splitList.add(segment.toString());
      }

      return splitList;
   }

   public static String getCenteredText(String line, int pixelWidth, boolean bold, List<Character> singlePixels) {
      int inputWidth = getPixelLength(line, bold);
      pixelWidth -= inputWidth / 2;
      if (pixelWidth <= 0) {
         return line;
      } else {
         int whiteSpaceAmount = pixelWidth / 4;
         int singlePixelAmount = pixelWidth % 4;
         String whiteSpaces = " ".repeat(whiteSpaceAmount);
         if (singlePixelAmount > 0) {
            whiteSpaces = whiteSpaces + singlePixels.get(singlePixelAmount - 1);
         }

         return "<white>" + whiteSpaces + line;
      }
   }

   public static int getPixelLength(String line, boolean bold) {
      int len = line.length();
      int base = bold ? 7 : 6;
      int width = 0;

      for (int i = 0; i < len; i++) {
         char c = line.charAt(i);
         if (c == '<') {
            int end = line.indexOf(62, i + 1);
            if (end >= 0) {
               i = end;
               continue;
            }
         }

         width += base;
         switch (c) {
            case ' ':
            case '(':
            case ')':
            case '/':
            case 'I':
            case '[':
            case '\\':
            case ']':
            case 't':
               width -= 2;
               break;
            case '!':
            case ',':
            case '.':
            case ':':
            case 'i':
               width -= 4;
               break;
            case '\'':
            case 'l':
               width -= 3;
               break;
            case '1':
            case 'f':
               width--;
         }
      }

      return width;
   }
}
