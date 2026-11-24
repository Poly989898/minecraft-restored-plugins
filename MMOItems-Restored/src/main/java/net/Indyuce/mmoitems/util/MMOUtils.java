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

   public static boolean isColorable(@NotNull Particle var0) {
      return var0.getDataType() == DustOptions.class;
   }

   public static String[] trimString(int var0, @NotNull String... var1) {
      ArrayList<String> var2 = new ArrayList<>();

      for (String var6 : var1) {
         if (var6.length() <= var0) {
            var2.add(var6);
         } else {
            StringBuilder var7 = new StringBuilder();

            for (String var11 : var6.split(" ")) {
               if (!var7.isEmpty()) {
                  var7.append(" ");
               }

               var7.append(var11);
               if (var7.length() > var0 || var11.endsWith("\n")) {
                  var2.add(var7.toString());
                  var7.setLength(0);
               }
            }

            if (!var7.isEmpty()) {
               var2.add(var7.toString());
            }
         }
      }

      return var2.toArray(new String[0]);
   }

   @NotNull
   public static ItemStack readIcon(@NotNull String var0) {
      Validate.notNull(var0, "Input must not be null");
      String[] var1 = var0.split(":");
      ItemStack var2 = new ItemStack(Material.valueOf(UtilityMethods.enumName(var1[0])));
      if (var1.length > 1) {
         ItemMeta var3 = var2.getItemMeta();
         var3.setCustomModelData(Integer.parseInt(var1[1]));
         var2.setItemMeta(var3);
      }

      return var2;
   }

   public static double getForce(@NotNull EntityShootBowEvent var0) {
      double var1 = var0.getForce();
      return MythicLib.plugin.getVersion().isUnder(new int[]{1, 21}) ? var1 : var1 / 3.0;
   }

   public static boolean isSoulboundTo(@NotNull NBTItem var0, @NotNull Player var1) {
      String var2 = var0.getString("MMOITEMS_SOULBOUND");
      return var2 != null && var2.contains(var1.getUniqueId().toString());
   }

   public static boolean hasBeenRemoved(@NotNull NBTItem var0) {
      if (!var0.hasType()) {
         return false;
      }

      String var1 = var0.getType();
      return isNonEmpty(var1) && (!Type.isValid(var1) || !MMOItems.plugin.getTemplates().hasTemplate(Type.get(var1), var0.getString("MMOITEMS_ITEM_ID")));
   }

   public static boolean isNonEmpty(@Nullable String var0) {
      return var0 != null && !var0.isEmpty();
   }

   @NotNull
   public static String requireNonEmptyElse(@Nullable String var0, @NotNull String var1) {
      return isNonEmpty(var0) ? var0 : Objects.requireNonNull(var1);
   }

   public static boolean checkReference(@Nullable String var0, @Nullable String var1) {
      if (var0 != null && var0.equals("all")) {
         return true;
      } else {
         return var1 != null && var1.equals("all") ? true : Objects.equals(var0, var1);
      }
   }

   @NotNull
   public static Color toRGB(ChatColor var0) {
      return Objects.requireNonNull(COLOR_MAPPINGS.get(var0), "Not a color");
   }

   public static int getPickaxePower(Player var0) {
      ItemStack var1 = var0.getInventory().getItemInMainHand();
      if (var1 != null && var1.getType() != Material.AIR) {
         NBTItem var2 = NBTItem.get(var1);
         if (var2.hasTag("MMOITEMS_PICKAXE_POWER")) {
            return var2.getInteger("MMOITEMS_PICKAXE_POWER");
         }

         switch (var1.getType().name()) {
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
   public static TriggerType backwardsCompatibleTriggerType(@NotNull String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Trigger cannot be null");
      }

      switch (var0) {
         case "ON_HIT":
            return TriggerType.ATTACK;
         case "WHEN_HIT":
            return TriggerType.DAMAGED;
         default:
            return TriggerType.valueOf(var0);
      }
   }

   public static boolean isMMOItem(@Nullable ItemStack var0, @NotNull String var1, @NotNull String var2) {
      if (var0 == null) {
         return false;
      } else {
         NBTItem var3 = NBTItem.get(var0);
         String var4 = getID(var3);
         if (var4 == null) {
            return false;
         } else {
            return !var4.equals(var2) ? false : var3.getType().equals(var1);
         }
      }
   }

   @Nullable
   public static Type getType(@Nullable NBTItem var0) {
      return var0 != null && var0.hasType() ? MMOItems.plugin.getTypes().get(var0.getType()) : null;
   }

   @Nullable
   public static String getID(@Nullable NBTItem var0) {
      if (var0 != null && var0.hasType()) {
         ItemTag var1 = ItemTag.getTagAtPath("MMOITEMS_ITEM_ID", var0, SupportedNBTTagValues.STRING);
         return var1 == null ? null : (String)var1.getValue();
      } else {
         return null;
      }
   }

   public static Vector normalize(Vector var0) {
      return var0.getX() == 0.0 && var0.getY() == 0.0 ? var0 : var0.normalize();
   }

   public static double parseDouble(String var0) {
      try {
         return Double.parseDouble(var0);
      } catch (IllegalArgumentException var2) {
         throw new IllegalArgumentException("Could not read number from '" + var0 + "'");
      }
   }

   @Nullable
   public static UUID UUIDFromString(@org.jetbrains.annotations.Nullable String var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}") ? UUID.fromString(var0) : null;
      }
   }

   public static LivingEntity getDamager(EntityDamageByEntityEvent var0) {
      if (var0.getDamager() instanceof LivingEntity) {
         return (LivingEntity)var0.getDamager();
      }

      if (var0.getDamager() instanceof Projectile) {
         Projectile var1 = (Projectile)var0.getDamager();
         if (var1.getShooter() instanceof LivingEntity) {
            return (LivingEntity)var1.getShooter();
         }
      }

      return null;
   }

   public static int getLevel(@NotNull ItemStack var0, @NotNull Enchantment var1) {
      return var0.hasItemMeta() ? var0.getItemMeta().getEnchantLevel(var1) : 0;
   }

   public static int getEffectDuration(PotionEffectType var0) {
      return !var0.equals(PotionEffectType.NIGHT_VISION) && !var0.equals(VPotionEffectType.NAUSEA.get())
         ? (var0.equals(PotionEffectType.BLINDNESS) ? 140 : 100)
         : 260;
   }

   @NotNull
   public static String getDisplayName(@Nullable ItemStack var0) {
      if (var0 == null) {
         return "null";
      } else {
         return var0.hasItemMeta() && var0.getItemMeta().hasDisplayName()
            ? var0.getItemMeta().getDisplayName()
            : caseOnWords(var0.getType().name().toLowerCase().replace("_", " "));
      }
   }

   @Deprecated
   public static String caseOnWords(String var0) {
      return UtilityMethods.caseOnWords(var0);
   }

   public static boolean isMetaItem(ItemStack var0, boolean var1) {
      return var0 != null
         && var0.getType() != Material.AIR
         && var0.getItemMeta() != null
         && var0.getItemMeta().getDisplayName() != null
         && (!var1 || var0.getItemMeta().getLore() != null);
   }

   public static void saturate(@NotNull Player var0, double var1) {
      saturate(var0, var1, true);
   }

   public static void saturate(@NotNull Player var0, double var1, boolean var3) {
      if (var1 > 0.0 || var3) {
         var0.setSaturation(Math.max(0.0F, Math.min(20.0F, var0.getSaturation() + (float)var1)));
      }
   }

   public static void feed(@NotNull Player var0, int var1) {
      feed(var0, var1, true);
   }

   public static void feed(@NotNull Player var0, int var1, boolean var2) {
      if (var1 > 0 || var2) {
         var0.setFoodLevel(Math.max(Math.min(20, var0.getFoodLevel() + var1), 0));
      }
   }

   public static void heal(@NotNull LivingEntity var0, double var1) {
      heal(var0, var1, true);
   }

   public static void heal(@NotNull LivingEntity var0, double var1, boolean var3) {
      if (var1 != 0.0) {
         if (!var0.isDead() && !(var0.getHealth() <= 0.0)) {
            if (!(var1 < 0.0) || var3) {
               double var4 = var0.getAttribute(Attribute.MAX_HEALTH).getValue();
               var0.setHealth(Math.min(var4, var0.getHealth() + var1));
            }
         }
      }
   }

   public static String intToRoman(int var0) {
      if (var0 >= 1 && var0 <= 3999) {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < romanChars.length; var2++) {
            while (var0 >= romanValues[var2]) {
               var1.append(romanChars[var2]);
               var0 -= romanValues[var2];
            }
         }

         return var1.toString();
      } else {
         throw new IllegalArgumentException("Input must be between 1 and 3999");
      }
   }

   public static double truncation(double var0, int var2) {
      double var3 = Math.pow(10.0, var2);
      return Math.floor(var0 * var3) / var3;
   }

   public static Vector rotateFunc(Vector var0, Location var1) {
      double var2 = var1.getYaw() / 180.0F * Math.PI;
      double var4 = var1.getPitch() / 180.0F * Math.PI;
      var0 = rotAxisX(var0, var4);
      return rotAxisY(var0, -var2);
   }

   private static Vector rotAxisX(Vector var0, double var1) {
      double var3 = var0.getY() * Math.cos(var1) - var0.getZ() * Math.sin(var1);
      double var5 = var0.getY() * Math.sin(var1) + var0.getZ() * Math.cos(var1);
      return var0.setY(var3).setZ(var5);
   }

   private static Vector rotAxisY(Vector var0, double var1) {
      double var3 = var0.getX() * Math.cos(var1) + var0.getZ() * Math.sin(var1);
      double var5 = var0.getX() * -Math.sin(var1) + var0.getZ() * Math.cos(var1);
      return var0.setX(var3).setZ(var5);
   }

   private static Vector rotAxisZ(Vector var0, double var1) {
      double var3 = var0.getX() * Math.cos(var1) - var0.getY() * Math.sin(var1);
      double var5 = var0.getX() * Math.sin(var1) + var0.getY() * Math.cos(var1);
      return var0.setX(var3).setY(var5);
   }

   public static List<Entity> getNearbyChunkEntities(Location var0) {
      ArrayList var1 = new ArrayList();
      int var2 = var0.getChunk().getX();
      int var3 = var0.getChunk().getZ();

      for (int var4 = -1; var4 < 2; var4++) {
         for (int var5 = -1; var5 < 2; var5++) {
            var1.addAll(Arrays.asList(var0.getWorld().getChunkAt(var2 + var4, var3 + var5).getEntities()));
         }
      }

      return var1;
   }
}
