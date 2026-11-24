package net.Indyuce.mmoitems.util;

import com.google.common.collect.ImmutableMap;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.version.VPotionEffectType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MMOUtils {
   private static final String UNIVERSAL_REFERENCE = "all";
   private static final Map<ChatColor, Color> COLOR_MAPPINGS = ImmutableMap.<ChatColor, Color>builder()
      .put(ChatColor.BLACK, Color.fromRGB(0, 0, 0))
      .put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170))
      .put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0))
      .put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170))
      .put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0))
      .put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170))
      .put(ChatColor.GOLD, Color.fromRGB(255, 170, 0))
      .put(ChatColor.GRAY, Color.fromRGB(170, 170, 170))
      .put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85))
      .put(ChatColor.BLUE, Color.fromRGB(85, 85, 255))
      .put(ChatColor.GREEN, Color.fromRGB(85, 255, 85))
      .put(ChatColor.AQUA, Color.fromRGB(85, 255, 255))
      .put(ChatColor.RED, Color.fromRGB(255, 85, 85))
      .put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255))
      .put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85))
      .put(ChatColor.WHITE, Color.fromRGB(255, 255, 255))
      .build();
   private static final String[] romanChars = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
   private static final int[] romanValues = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

   public MMOUtils() {
      throw new IllegalArgumentException("This class cannot be instantiated.");
   }

   public static boolean isColorable(@NotNull Particle particle) {
      return particle.getDataType() == DustOptions.class;
   }

   public static String[] trimString(int count, @NotNull String... texts) {
      ArrayList<String> words = new ArrayList<>();

      for (String text : texts) {
         if (text.length() <= count) {
            words.add(text);
         } else {
            StringBuilder builder = new StringBuilder();

            for (String word : text.split(" ")) {
               if (!builder.isEmpty()) {
                  builder.append(" ");
               }

               builder.append(word);
               if (builder.length() > count || word.endsWith("\n")) {
                  words.add(builder.toString());
                  builder.setLength(0);
               }
            }

            if (!builder.isEmpty()) {
               words.add(builder.toString());
            }
         }
      }

      return words.toArray(new String[0]);
   }

   @NotNull
   public static ItemStack readIcon(@NotNull String text) {
      Validate.notNull(text, "Input must not be null");
      String[] texts = text.split(":");
      ItemStack item = new ItemStack(Material.valueOf(UtilityMethods.enumName(texts[0])));
      if (texts.length > 1) {
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setCustomModelData(Integer.parseInt(texts[1]));
         item.setItemMeta(itemMeta);
      }

      return item;
   }

   public static double getForce(@NotNull EntityShootBowEvent event) {
      double value = event.getForce();
      return MythicLib.plugin.getVersion().isUnder(new int[]{1, 21}) ? value : value / 3.0;
   }

   public static boolean isSoulboundTo(@NotNull NBTItem nbtItem, @NotNull Player player) {
      String mMOITEMSSOULBOUND = nbtItem.getString("MMOITEMS_SOULBOUND");
      return mMOITEMSSOULBOUND != null && mMOITEMSSOULBOUND.contains(player.getUniqueId().toString());
   }

   public static boolean hasBeenRemoved(@NotNull NBTItem nbtItem) {
      if (!nbtItem.hasType()) {
         return false;
      }

      String type = nbtItem.getType();
      return isNonEmpty(type) && (!Type.isValid(type) || !MMOItems.plugin.getTemplates().hasTemplate(Type.get(type), nbtItem.getString("MMOITEMS_ITEM_ID")));
   }

   public static boolean isNonEmpty(@Nullable String text) {
      return text != null && !text.isEmpty();
   }

   @NotNull
   public static String requireNonEmptyElse(@Nullable String text, @NotNull String fallback) {
      return isNonEmpty(text) ? text : Objects.requireNonNull(fallback);
   }

   public static boolean checkReference(@Nullable String text, @Nullable String otherReference) {
      if (text != null && text.equals("all")) {
         return true;
      } else {
         return otherReference != null && otherReference.equals("all") ? true : Objects.equals(text, otherReference);
      }
   }

   @NotNull
   public static Color toRGB(ChatColor chatColor) {
      return Objects.requireNonNull(COLOR_MAPPINGS.get(chatColor), "Not a color");
   }

   public static int getPickaxePower(Player player) {
      ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      if (itemInMainHand != null && itemInMainHand.getType() != Material.AIR) {
         NBTItem nbtItem = NBTItem.get(itemInMainHand);
         if (nbtItem.hasTag("MMOITEMS_PICKAXE_POWER")) {
            return nbtItem.getInteger("MMOITEMS_PICKAXE_POWER");
         }

         switch (itemInMainHand.getType().name()) {
            case "WOODEN_PICKAXE":
            case "WOOD_PICKAXE":
               return 5;
            case "STONE_PICKAXE":
               return 10;
            case "GOLDEN_PICKAXE":
            case "GOLD_PICKAXE":
               return 15;
            case "IRON_PICKAXE":
               return 20;
            case "DIAMOND_PICKAXE":
               return 25;
            case "NETHERITE_PICKAXE":
               return 30;
            default:
               return 0;
         }
      } else {
         return 0;
      }
   }

   @Deprecated
   @NotNull
   public static TriggerType backwardsCompatibleTriggerType(@NotNull String text) {
      if (text == null) {
         throw new IllegalArgumentException("Trigger cannot be null");
      }

      switch (text) {
         case "ON_HIT":
            return TriggerType.ATTACK;
         case "WHEN_HIT":
            return TriggerType.DAMAGED;
         default:
            return TriggerType.valueOf(text);
      }
   }

   public static boolean isMMOItem(@Nullable ItemStack item, @NotNull String text, @NotNull String id) {
      if (item == null) {
         return false;
      } else {
         NBTItem nbtItem = NBTItem.get(item);
         String iD = getID(nbtItem);
         if (iD == null) {
            return false;
         } else {
            return !iD.equals(id) ? false : nbtItem.getType().equals(text);
         }
      }
   }

   @Nullable
   public static Type getType(@Nullable NBTItem nbtItem) {
      return nbtItem != null && nbtItem.hasType() ? MMOItems.plugin.getTypes().get(nbtItem.getType()) : null;
   }

   @Nullable
   public static String getID(@Nullable NBTItem nbtItem) {
      if (nbtItem != null && nbtItem.hasType()) {
         ItemTag tagAtPath = ItemTag.getTagAtPath("MMOITEMS_ITEM_ID", nbtItem, SupportedNBTTagValues.STRING);
         return tagAtPath == null ? null : (String)tagAtPath.getValue();
      } else {
         return null;
      }
   }

   public static Vector normalize(Vector vector) {
      return vector.getX() == 0.0 && vector.getY() == 0.0 ? vector : vector.normalize();
   }

   public static double parseDouble(String text) {
      try {
         return Double.parseDouble(text);
      } catch (IllegalArgumentException exception) {
         throw new IllegalArgumentException("Could not read number from '" + text + "'");
      }
   }

   @Nullable
   public static UUID UUIDFromString(@org.jetbrains.annotations.Nullable String text) {
      if (text == null) {
         return null;
      } else {
         return text.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}") ? UUID.fromString(text) : null;
      }
   }

   public static LivingEntity getDamager(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof LivingEntity) {
         return (LivingEntity)event.getDamager();
      }

      if (event.getDamager() instanceof Projectile) {
         Projectile damager = (Projectile)event.getDamager();
         if (damager.getShooter() instanceof LivingEntity) {
            return (LivingEntity)damager.getShooter();
         }
      }

      return null;
   }

   public static int getLevel(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
      return item.hasItemMeta() ? item.getItemMeta().getEnchantLevel(enchantment) : 0;
   }

   public static int getEffectDuration(PotionEffectType potionEffectType) {
      return !potionEffectType.equals(PotionEffectType.NIGHT_VISION) && !potionEffectType.equals(VPotionEffectType.NAUSEA.get())
         ? (potionEffectType.equals(PotionEffectType.BLINDNESS) ? 140 : 100)
         : 260;
   }

   @NotNull
   public static String getDisplayName(@Nullable ItemStack item) {
      if (item == null) {
         return "null";
      } else {
         return item.hasItemMeta() && item.getItemMeta().hasDisplayName()
            ? item.getItemMeta().getDisplayName()
            : caseOnWords(item.getType().name().toLowerCase().replace("_", " "));
      }
   }

   @Deprecated
   public static String caseOnWords(String text) {
      return UtilityMethods.caseOnWords(text);
   }

   public static boolean isMetaItem(ItemStack item, boolean flag) {
      return item != null
         && item.getType() != Material.AIR
         && item.getItemMeta() != null
         && item.getItemMeta().getDisplayName() != null
         && (!flag || item.getItemMeta().getLore() != null);
   }

   public static void saturate(@NotNull Player player, double value) {
      saturate(player, value, true);
   }

   public static void saturate(@NotNull Player player, double value, boolean flag) {
      if (value > 0.0 || flag) {
         player.setSaturation(Math.max(0.0F, Math.min(20.0F, player.getSaturation() + (float)value)));
      }
   }

   public static void feed(@NotNull Player player, int count) {
      feed(player, count, true);
   }

   public static void feed(@NotNull Player player, int count, boolean flag) {
      if (count > 0 || flag) {
         player.setFoodLevel(Math.max(Math.min(20, player.getFoodLevel() + count), 0));
      }
   }

   public static void heal(@NotNull LivingEntity entity, double value) {
      heal(entity, value, true);
   }

   public static void heal(@NotNull LivingEntity entity, double value, boolean flag) {
      if (value != 0.0) {
         if (!entity.isDead() && !(entity.getHealth() <= 0.0)) {
            if (!(value < 0.0) || flag) {
               double maxHealth = entity.getAttribute(Attribute.MAX_HEALTH).getValue();
               entity.setHealth(Math.min(maxHealth, entity.getHealth() + value));
            }
         }
      }
   }

   public static String intToRoman(int count) {
      if (count >= 1 && count <= 3999) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < romanChars.length; i++) {
            while (count >= romanValues[i]) {
               builder.append(romanChars[i]);
               count -= romanValues[i];
            }
         }

         return builder.toString();
      } else {
         throw new IllegalArgumentException("Input must be between 1 and 3999");
      }
   }

   public static double truncation(double value, int count) {
      double factor = Math.pow(10.0, count);
      return Math.floor(value * factor) / factor;
   }

   public static Vector rotateFunc(Vector vector, Location location) {
      double value = location.getYaw() / 180.0F * Math.PI;
      double pitchRadians = location.getPitch() / 180.0F * Math.PI;
      vector = rotAxisX(vector, pitchRadians);
      return rotAxisY(vector, -value);
   }

   private static Vector rotAxisX(Vector vector, double value) {
      double rotatedY = vector.getY() * Math.cos(value) - vector.getZ() * Math.sin(value);
      double rotatedZ = vector.getY() * Math.sin(value) + vector.getZ() * Math.cos(value);
      return vector.setY(rotatedY).setZ(rotatedZ);
   }

   private static Vector rotAxisY(Vector vector, double value) {
      double rotatedX = vector.getX() * Math.cos(value) + vector.getZ() * Math.sin(value);
      double rotatedZ = vector.getX() * -Math.sin(value) + vector.getZ() * Math.cos(value);
      return vector.setX(rotatedX).setZ(rotatedZ);
   }

   private static Vector rotAxisZ(Vector vector, double value) {
      double rotatedX = vector.getX() * Math.cos(value) - vector.getY() * Math.sin(value);
      double rotatedY = vector.getX() * Math.sin(value) + vector.getY() * Math.cos(value);
      return vector.setX(rotatedX).setY(rotatedY);
   }

   public static List<Entity> getNearbyChunkEntities(Location location) {
      ArrayList list = new ArrayList();
      int x = location.getChunk().getX();
      int z = location.getChunk().getZ();

      for (int i = -1; i < 2; i++) {
         for (int j = -1; j < 2; j++) {
            list.addAll(Arrays.asList(location.getWorld().getChunkAt(x + i, z + j).getEntities()));
         }
      }

      return list;
   }
}
