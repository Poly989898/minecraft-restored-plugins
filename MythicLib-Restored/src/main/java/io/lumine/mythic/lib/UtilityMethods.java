package io.lumine.mythic.lib;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.condition.RegionCondition;
import io.lumine.mythic.lib.api.condition.type.MMOCondition;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.util.Tasks;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VersionUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class UtilityMethods {
   private static final Listener PRIVATE_LISTENER = new Listener() {};
   private static final char[] DELAY_CHARACTERS = new char[]{'m', 'h', 'd', 'm', 'y'};
   private static final long[] DELAY_AMOUNTS = new long[]{60L, 3600L, 86400L, 2592000L, 946080000L};
   private static final int PTS_PER_BLOCK = 10;
   private static final double BOUNDING_BOX_EXPANSION = 0.2;
   private static final String[] PREVIOUS_ATTRIBUTE_MODIFIER_NAMES = new String[]{"mmolib.", "mmoitems.", "mythiclib."};
   private static final Random RANDOM = new Random();
   private static final Map<String, String> DEBUG_COLOR_PREFIX = new HashMap<>();
   private static final int[] NEGATIVE_SPACE_AMOUNTS = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 16, 32, 64, 128, 256, 512, 1024};

   public static Location readLocation(@NotNull ConfigObject config) {
      return new Location(
         Bukkit.getWorld(config.getString("world")),
         config.getDouble("x"),
         config.getDouble("y"),
         config.getDouble("z"),
         (float)config.getDouble("yaw"),
         (float)config.getDouble("pitch")
      );
   }

   public static Vector safeNormalize(@NotNull Vector vec) {
      return safeNormalize(vec, vec);
   }

   public static Vector safeNormalize(@NotNull Vector vec, @NotNull Vector defaultValue) {
      double normSquared = vec.lengthSquared();
      return normSquared == 0.0 ? defaultValue : vec.multiply(1.0 / Math.sqrt(normSquared));
   }

   public static void forcePotionEffect(LivingEntity entity, PotionEffectType type, double duration, int amplifier) {
      entity.removePotionEffect(type);
      entity.addPotionEffect(new PotionEffect(type, (int)(duration * 20.0), amplifier));
   }

   @NotNull
   public static Pattern internalPlaceholderPattern(char start, char end) {
      return Pattern.compile(start + "[^&|!=" + start + end + "]*?" + end);
   }

   public static <T extends Event> void registerEvent(@NotNull Class<T> eventClass, @NotNull EventPriority priority, @NotNull Consumer<T> executor) {
      registerEvent(eventClass, PRIVATE_LISTENER, priority, executor, MythicLib.plugin, false);
   }

   public static <T extends Event> void registerEvent(
      @NotNull Class<T> eventClass,
      @NotNull Listener listener,
      @NotNull EventPriority priority,
      @NotNull Consumer<T> executor,
      @NotNull Plugin plugin,
      boolean ignoreCancelled
   ) {
      Bukkit.getPluginManager().registerEvent(eventClass, listener, priority, (listener2, event) -> executor.accept((T)event), plugin, ignoreCancelled);
   }

   public static MMOCondition getCondition(String input) {
      MMOLineConfig config = new MMOLineConfig(input);
      String key = config.getKey().toLowerCase();
      switch (key) {
         case "region":
            if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
               return null;
            }

            return new RegionCondition(config);
         default:
            return null;
      }
   }

   public static boolean isInvalidated(@NotNull PlayerMetadata caster) {
      return isInvalidated(caster.getData());
   }

   public static boolean isInvalidated(@NotNull MMOPlayerData playerData) {
      return !playerData.isOnline() || isInvalidated(playerData.getPlayer());
   }

   public static boolean isInvalidated(@NotNull Player player) {
      return !player.isOnline() || player.isDead();
   }

   @Nullable
   public static ItemStack getHandItem(@NotNull LivingEntity entity, @NotNull EquipmentSlot hand) {
      switch (hand) {
         case MAIN_HAND:
            return entity.getEquipment().getItemInMainHand();
         case OFF_HAND:
            return entity.getEquipment().getItemInOffHand();
         default:
            throw new IllegalArgumentException("Must provide a hand slot");
      }
   }

   @Nullable
   public static ItemStack getHandItem(@NotNull LivingEntity entity, @NotNull org.bukkit.inventory.EquipmentSlot hand) {
      switch (hand) {
         case HAND:
            return entity.getEquipment().getItemInMainHand();
         case OFF_HAND:
            return entity.getEquipment().getItemInOffHand();
         default:
            throw new IllegalArgumentException("Must provide a hand slot");
      }
   }

   @Deprecated
   public static void setTextureValue(@NotNull ItemMeta meta, @NotNull String textureValue) {
      if (meta instanceof SkullMeta) {
         setTextureValue((SkullMeta)meta, textureValue, UUID.randomUUID());
      }
   }

   public static void setTextureValue(@NotNull SkullMeta meta, @NotNull String textureValue) {
      setTextureValue(meta, textureValue, UUID.randomUUID());
   }

   public static void setTextureValue(@NotNull SkullMeta meta, @NotNull String textureValue, @NotNull UUID uniqueId) {
      Object profile = MythicLib.plugin.getVersion().getWrapper().newProfile(uniqueId, textureValue);
      MythicLib.plugin.getVersion().getWrapper().setProfile(meta, profile);
   }

   @Deprecated
   public static boolean isFakeEvent(@NotNull EntityDamageEvent event) {
      return isFake(event);
   }

   public static boolean isFake(@NotNull Event event) {
      return MythicLib.plugin.getFakeEvents().isFake(event);
   }

   @Deprecated
   @NotNull
   public static Runnable serverThreadCatch(@NotNull Plugin plugin, @NotNull Runnable runnable) {
      return () -> {
         try {
            runnable.run();
         } catch (Throwable throwable) {
            Bukkit.getScheduler().runTask(plugin, () -> throwable.printStackTrace());
         }
      };
   }

   public static boolean isAir(@Nullable ItemStack item) {
      return item == null || item.getType() == Material.AIR;
   }

   public static String formatDelay(long millis) {
      if (millis < 60000L) {
         return "1m";
      }

      String format = "";

      for (int j = DELAY_CHARACTERS.length - 1; j >= 0; j--) {
         long divisor = DELAY_AMOUNTS[j] * 1000L;
         if (millis >= divisor) {
            format = millis / divisor + DELAY_CHARACTERS[j] + " " + format;
            millis %= divisor;
         }
      }

      return format;
   }

   public static void drawVector(Vector vec, Location source, Color color) {
      double step = 0.1 / vec.length();

      for (double d = 0.0; d < 1.0; d += step) {
         Location inter = source.clone().add(vec.clone().multiply(d));
         inter.getWorld().spawnParticle(VParticle.REDSTONE.get(), inter, 0, new DustOptions(color, 0.6F));
      }
   }

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

   public static boolean canTarget(Player source, Entity target) {
      return canTarget(source, null, target, InteractionType.OFFENSE_SKILL);
   }

   public static boolean canTarget(Player source, Location loc, Entity target) {
      return canTarget(source, loc, target, InteractionType.OFFENSE_SKILL);
   }

   public static boolean canTarget(Player source, Entity target, InteractionType interaction) {
      return canTarget(source, null, target, interaction);
   }

   public static boolean canTarget(@Nullable Player source, @Nullable Location loc, @NotNull Entity target, @NotNull InteractionType interaction) {
      return loc != null && !target.getBoundingBox().expand(0.2).contains(loc.toVector())
         ? false
         : MythicLib.plugin.getEntities().canInteract(source, target, interaction);
   }

   @NotNull
   public static <T> T resolveEnumField(@NotNull Function<String, T> resolver, @NotNull String... candidates) {
      return resolveEnumField(resolver, null, candidates);
   }

   @NotNull
   public static <T> T resolveEnumField(@NotNull Function<String, T> resolver, @Nullable Supplier<T> defaultValue, @NotNull String... candidates) {
      for (String candidate : candidates) {
         try {
            return Objects.requireNonNull(resolver.apply(candidate));
         } catch (Throwable exception) {
         }
      }

      if (defaultValue != null) {
         return Objects.requireNonNull(defaultValue.get(), "Null supplied value");
      } else {
         throw new RuntimeException("Could not find enum field called '" + Arrays.<String>asList(candidates) + "'");
      }
   }

   public static double getPlayerDefaultBaseValue(@NotNull Attribute attribute, @Nullable AttributeInstance instance) {
      Double found = MythicLib.plugin.getStats().getPlayerDefaultBaseValue(attribute);
      return found != null ? found : (instance != null ? instance.getDefaultValue() : 0.0);
   }

   @NotNull
   public static String format(@NotNull String input, @NotNull Object... args) {
      return new Formatter().format(input, args).toString();
   }

   public static void flushOldModifiers(@NotNull Player player) {
      for (Attribute attribute : Attribute.values()) {
         AttributeInstance ins = player.getAttribute(attribute);
         if (ins != null) {
            for (AttributeModifier mod : ins.getModifiers()) {
               for (String prev : PREVIOUS_ATTRIBUTE_MODIFIER_NAMES) {
                  if (mod.getName().startsWith(prev)) {
                     ins.removeModifier(mod);
                  }
               }
            }
         }
      }
   }

   public static boolean isRealPlayer(Entity entity) {
      return entity instanceof Player && !entity.hasMetadata("NPC");
   }

   public static boolean isMetaItem(@Nullable ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
   }

   public static void heal(@NotNull LivingEntity player, double heal) {
      heal(player, heal, false);
   }

   public static void heal(@NotNull LivingEntity player, double heal, boolean allowNegatives) {
      if (heal > 0.0 || allowNegatives) {
         player.setHealth(Math.min(player.getAttribute(Attribute.MAX_HEALTH).getValue(), player.getHealth() + heal));
      }
   }

   public static void closeOpenViewsOfType(Class<?> inventoryHolderClass) {
      for (Player online : Bukkit.getOnlinePlayers()) {
         VInventoryView view = VersionUtils.getOpen(online);
         if (inventoryHolderClass.isInstance(view.getTopInventory().getHolder())) {
            view.close();
         }
      }
   }

   public static String caseOnWords(String input) {
      StringBuilder builder = new StringBuilder(input);
      boolean isLastSpace = true;

      for (int i = 0; i < builder.length(); i++) {
         char ch = builder.charAt(i);
         if (isLastSpace && ch >= 'a' && ch <= 'z') {
            builder.setCharAt(i, (char)(ch + -32));
            isLastSpace = false;
         } else {
            isLastSpace = ch == ' ';
         }
      }

      return builder.toString();
   }

   public static void dropItemNaturally(@NotNull Location loc, @NotNull ItemStack stack) {
      double xs = RANDOM.nextFloat() * 0.5F + 0.25;
      double ys = RANDOM.nextFloat() * 0.5F + 0.25;
      double zs = RANDOM.nextFloat() * 0.5F + 0.25;
      loc.getWorld().dropItem(loc.clone().add(xs, ys, zs), stack);
   }

   @Deprecated
   public static List<Player> getNearbyPlayers(Location loc) {
      List<Player> players = new ArrayList<>();
      int cx = loc.getChunk().getX();
      int cz = loc.getChunk().getZ();

      for (int x = -1; x < 2; x++) {
         for (int z = -1; z < 2; z++) {
            for (Entity target : loc.getWorld().getChunkAt(cx + x, cz + z).getEntities()) {
               if (target instanceof Player) {
                  players.add((Player)target);
               }
            }
         }
      }

      return players;
   }

   public static void loadDefaultFile(String path, String name) {
      String newPath = path.isEmpty() ? "" : "/" + path;
      File folder = new File(MythicLib.plugin.getDataFolder() + newPath);
      if (!folder.exists()) {
         folder.mkdir();
      }

      File file = new File(MythicLib.plugin.getDataFolder() + newPath, name);
      if (!file.exists()) {
         try {
            Files.copy(MythicLib.plugin.getResource("default/" + (path.isEmpty() ? "" : path + "/") + name), file.getAbsoluteFile().toPath());
         } catch (IOException exception) {
            exception.printStackTrace();
         }
      }
   }

   public static boolean isVanished(Player player) {
      for (MetadataValue meta : player.getMetadata("vanished")) {
         if (meta.asBoolean()) {
            return true;
         }
      }

      return false;
   }

   public static String enumName(String str) {
      return str.toUpperCase().replace("-", "_").replace(" ", "_");
   }

   @Deprecated
   public static <T> Consumer<T> sync(@NotNull Plugin plugin, @NotNull Consumer<T> syncTask) {
      return Tasks.sync(plugin, syncTask);
   }

   public static String ymlName(String str) {
      return str.toLowerCase().replace("_", "-").replace(" ", "-");
   }

   @Nullable
   public static Player getPlayerDamager(EntityDamageByEntityEvent event) {
      if (isRealPlayer(event.getDamager())) {
         return (Player)event.getDamager();
      }

      if (event.getDamager() instanceof Projectile) {
         ProjectileSource shooter = ((Projectile)event.getDamager()).getShooter();
         if (shooter instanceof Entity && isRealPlayer((Entity)shooter)) {
            return (Player)shooter;
         }
      }

      return null;
   }

   @NotNull
   public static Enchantment findEnchant(@NotNull String id) {
      id = id.toLowerCase().replace("-", "_");
      Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(id));
      if (enchant != null) {
         return enchant;
      } else {
         id = id.toUpperCase();
         enchant = Enchantment.getByName(id);
         if (enchant != null) {
            return enchant;
         } else {
            throw new IllegalArgumentException("Could not find enchant called '" + id + "'");
         }
      }
   }

   public static double[] getYawPitch(Vector axis) {
      double _2PI = Math.PI * 2;
      double x = axis.getX();
      double z = axis.getZ();
      if (x == 0.0 && z == 0.0) {
         return new double[]{0.0, axis.getY() > 0.0 ? -90.0 : 90.0};
      }

      double theta = Math.atan2(-x, z);
      double yaw = (float)Math.toDegrees((theta + (Math.PI * 2)) % (Math.PI * 2));
      double xz = Math.sqrt(x * x + z * z);
      double pitch = (float)Math.toDegrees(Math.atan(-axis.getY() / xz));
      return new double[]{yaw, pitch};
   }

   public static Vector rotate(Vector rotated, Vector axis) {
      double[] yawPitch = getYawPitch(axis);
      return rotate(rotated, Math.toRadians(yawPitch[0]), Math.toRadians(yawPitch[1]));
   }

   public static Vector rotate(Vector rotated, double yaw, double pitch) {
      return rotAxisY(rotAxisX(rotated, pitch), -yaw);
   }

   private static Vector rotAxisX(Vector rotated, double angle) {
      double y = rotated.getY() * Math.cos(angle) - rotated.getZ() * Math.sin(angle);
      double z = rotated.getY() * Math.sin(angle) + rotated.getZ() * Math.cos(angle);
      return rotated.setY(y).setZ(z);
   }

   private static Vector rotAxisY(Vector rotated, double angle) {
      double x = rotated.getX() * Math.cos(angle) + rotated.getZ() * Math.sin(angle);
      double z = rotated.getX() * -Math.sin(angle) + rotated.getZ() * Math.cos(angle);
      return rotated.setX(x).setZ(z);
   }

   public static double getAltitude(Entity entity) {
      return getAltitude(entity.getLocation());
   }

   public static double getAltitude(Location loc) {
      Location moving = loc.clone();

      while (!moving.getBlock().getType().isSolid()) {
         moving.add(0.0, -1.0, 0.0);
      }

      return loc.getY() - moving.getBlockY() - 1.0;
   }

   public static void debug(@NotNull JavaPlugin plugin, @Nullable String prefix, @NotNull String message) {
      Validate.notNull(plugin, "Plugin cannot be null");
      Validate.notNull(message, "Message cannot be null");
      String colorPrefix = DEBUG_COLOR_PREFIX.getOrDefault(plugin.getName(), "");
      if (MythicLib.plugin.getMMOConfig().debugMode) {
         plugin.getLogger().log(Level.INFO, colorPrefix + "[Debug" + (prefix == null ? "" : ": " + prefix) + "] " + message);
      }
   }

   @NotNull
   public static String getFontSpace(int size) {
      Validate.isTrue(size < 2048 && size > -2048, "Size must be between -2050 and 2050");
      if (size == 0) {
         return "";
      }

      int BASE_CHAR = size < 0 ? 63489 : 63521;
      if (size < 0) {
         size = -size;
      }

      StringBuilder built = new StringBuilder();

      for (int i = 0; i < NEGATIVE_SPACE_AMOUNTS.length; i++) {
         int index = NEGATIVE_SPACE_AMOUNTS.length - 1 - i;
         int providedSize = NEGATIVE_SPACE_AMOUNTS[index];
         if (size >= providedSize) {
            size -= providedSize;
            built.append((char)(BASE_CHAR + index));
         }
      }

      return built.toString();
   }

   static {
      DEBUG_COLOR_PREFIX.put("MythicLib", "§a");
      DEBUG_COLOR_PREFIX.put("MMOItems", "§c");
      DEBUG_COLOR_PREFIX.put("MMOCore", "§6");
      DEBUG_COLOR_PREFIX.put("RPGInventory", "§e");
   }

   /** Applies a parser to a string, turning any failure into an exception that uses the given message (with %s for the value). */
   public static <T> T prettyValueOf(java.util.function.Function<String, T> parser, String input, String errorFormat) {
      try {
         return parser.apply(input);
      } catch (RuntimeException exception) {
         throw new IllegalArgumentException(String.format(errorFormat, input), exception);
      }
   }

   public static <T> java.util.function.Consumer<T> dummyConsume() {
      return t -> {
      };
   }

   /** Holder of an inventory without creating a block state snapshot. */
   public static org.bukkit.inventory.InventoryHolder getHolder(org.bukkit.inventory.Inventory inventory) {
      return inventory.getHolder(false);
   }
}
