package net.Indyuce.mmocore.api.util;

import com.google.common.collect.MultimapBuilder;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.version.Attributes;
import io.lumine.mythic.lib.version.VEnchantment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.util.Icon;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class MMOCoreUtils {
   private static final String[] romanChars = new String[]{"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
   private static final int[] romanQuantities = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
   private static final Random RANDOM = new Random();

   public static boolean pluginItem(ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
   }

   public static boolean isInvalid(OfflinePlayer player) {
      return player.getName() == null;
   }

   @Deprecated
   public static String displayName(ItemStack item) {
      return item.hasItemMeta() && item.getItemMeta().hasDisplayName()
         ? item.getItemMeta().getDisplayName()
         : UtilityMethods.caseOnWords(item.getType().name().replace("_", " "));
   }

   public static double fixResource(double current, double maxStat) {
      return current == 0.0 ? maxStat : Math.max(0.0, Math.min(current, maxStat));
   }

   @Deprecated
   public static String caseOnWords(String s) {
      StringBuilder builder = new StringBuilder(s);
      boolean isLastSpace = true;

      for (int item = 0; item < builder.length(); item++) {
         char ch = builder.charAt(item);
         if (isLastSpace && ch >= 'a' && ch <= 'z') {
            builder.setCharAt(item, (char)(ch + -32));
            isLastSpace = false;
         } else {
            isLastSpace = ch == ' ';
         }
      }

      return builder.toString();
   }

   public static String ymlName(String str) {
      return str.toLowerCase().replace("_", "-").replace(" ", "-");
   }

   @Deprecated
   public static String toRomanNumerals(int value) {
      return intToRoman(value);
   }

   private static String repeat(String s, int n) {
      if (s == null) {
         return null;
      }

      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < n; i++) {
         sb.append(s);
      }

      return sb.toString();
   }

   public static void displayIndicator(Location loc, String message) {
      Hologram holo = Hologram.create(loc, MythicLib.plugin.parseColors(Collections.singletonList(message)));
      Bukkit.getScheduler().runTaskLater(MMOCore.plugin, holo::despawn, 20L);
   }

   public static boolean isPlayerHead(Material material) {
      return material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD;
   }

   public static void addAllItemFlags(@NotNull ItemMeta meta) {
      meta.addItemFlags(ItemFlag.values());

      try {
         meta.setAttributeModifiers(MultimapBuilder.hashKeys(0).hashSetValues(0).build());
      } catch (Exception var2) {
      }
   }

   @Deprecated
   public static ItemStack readIcon(String string) {
      return Icon.from(string).toItem();
   }

   public static int getWorth(ItemStack[] items) {
      int t = 0;

      for (ItemStack item : items) {
         if (item != null && item.getType() != Material.AIR) {
            t += MythicLib.plugin.getVersion().getWrapper().getNBTItem(item).getInteger("RpgWorth") * item.getAmount();
         }
      }

      return t;
   }

   public static String toBase64(ItemStack[] items) {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
         dataOutput.writeInt(items.length);

         for (ItemStack item : items) {
            dataOutput.writeObject(item);
         }

         dataOutput.close();
         return Base64Coder.encodeLines(outputStream.toByteArray());
      } catch (Throwable throwable) {
         throw new RuntimeException("Could not serialize inventory", throwable);
      }
   }

   public static ItemStack[] itemStackArrayFromBase64(String data) {
      try {
         ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
         BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
         ItemStack[] items = new ItemStack[dataInput.readInt()];

         for (int i = 0; i < items.length; i++) {
            items[i] = (ItemStack)dataInput.readObject();
         }

         dataInput.close();
         return items;
      } catch (Throwable throwable) {
         throw new RuntimeException("Could not deserialize inventory", throwable);
      }
   }

   public static String intToRoman(int input) {
      if (input < 1) {
         return "<1";
      }

      if (input > 3999) {
         return ">3999";
      }

      StringBuilder result = new StringBuilder();

      for (int j = 0; j < romanChars.length; j++) {
         int i = romanChars.length - j - 1;
         int q = romanQuantities[i];
         String c = romanChars[i];

         while (input >= q) {
            result.append(c);
            input -= q;
         }
      }

      return result.toString();
   }

   public static Collection<String> jsonArrayToList(String json) {
      return new ArrayList<>(Arrays.asList((String[])MythicLib.plugin.getJson().parse(json, String[].class)));
   }

   public static String arrayToJsonString(Collection<String> array) {
      JsonArray object = new JsonArray();

      for (String str : array) {
         object.add(str);
      }

      return object.toString();
   }

   public static String entrySetToJsonString(Set<Entry<String, Integer>> entrySet) {
      JsonObject object = new JsonObject();

      for (Entry<String, Integer> entry : entrySet) {
         object.addProperty(entry.getKey(), entry.getValue());
      }

      return object.toString();
   }

   @Deprecated
   public static List<Entity> getNearbyChunkEntities(Location loc) {
      List<Entity> entities = new ArrayList<>();
      int cx = loc.getChunk().getX();
      int cz = loc.getChunk().getZ();

      for (int x = -1; x < 2; x++) {
         for (int z = -1; z < 2; z++) {
            entities.addAll(Arrays.asList(loc.getWorld().getChunkAt(cx + x, cz + z).getEntities()));
         }
      }

      return entities;
   }

   @Deprecated
   public static void heal(LivingEntity target, double value) {
      double max = target.getAttribute(Attributes.MAX_HEALTH).getValue();
      double gain = Math.min(max, target.getHealth() + value) - target.getHealth();
      EntityRegainHealthEvent event = new EntityRegainHealthEvent(target, gain, RegainReason.CUSTOM);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         target.setHealth(target.getHealth() + gain);
      }
   }

   public static void decreaseDurability(Player player, EquipmentSlot slot, int damage) {
      ItemStack item = UtilityMethods.getHandItem(player, slot);
      if (item != null && item.getType().getMaxDurability() != 0 && !item.getItemMeta().isUnbreakable()) {
         ItemMeta meta = item.getItemMeta();
         int unbreakingLevel = meta.getEnchantLevel(VEnchantment.UNBREAKING.get());
         if (unbreakingLevel <= 0 || RANDOM.nextInt(unbreakingLevel + 1) == 0) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, item, damage);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               int newDamage = event.getDamage() + ((Damageable)meta).getDamage();
               if (newDamage >= item.getType().getMaxDurability()) {
                  player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                  player.getInventory().setItem(slot, null);
               } else {
                  ((Damageable)meta).setDamage(newDamage);
                  item.setItemMeta(meta);
               }
            }
         }
      }
   }

   @NotNull
   public static Location readLocation(String string) {
      String[] split = string.split(" ");
      World world = Bukkit.getWorld(split[0]);
      Validate.notNull(world, "Could not find world with name '" + split[0] + "'");
      double x = Double.parseDouble(split[1]);
      double y = Double.parseDouble(split[2]);
      double z = Double.parseDouble(split[3]);
      float yaw = split.length > 4 ? (float)Double.parseDouble(split[4]) : 0.0F;
      float pitch = split.length > 5 ? (float)Double.parseDouble(split[5]) : 0.0F;
      return new Location(world, x, y, z, yaw, pitch);
   }

   public static Location getCenterLocation(Entity entity) {
      return entity.getBoundingBox().getCenter().toLocation(entity.getWorld());
   }

   public static void debug(String message) {
      message = ChatColor.YELLOW + "Debug> " + ChatColor.WHITE + message;

      for (Player player : Bukkit.getOnlinePlayers()) {
         player.sendMessage(message);
      }

      Bukkit.getConsoleSender().sendMessage(message);
   }
}
