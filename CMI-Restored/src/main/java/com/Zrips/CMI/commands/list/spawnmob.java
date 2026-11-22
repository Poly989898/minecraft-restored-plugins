package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.AllListeners.MobExpirationListener;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIVillagerProfession;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import io.papermc.paper.entity.Leashable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.CMIPotionEffectType;
import net.Zrips.CMILib.Items.CMIPotionType;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Goat;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class spawnmob implements Cmd {

  private static final Pattern ENTITY_ARGUMENT_PATTERN = Pattern.compile("^([^;:]+)([;:])");

  int spawned = 0;
  int spawnedPassangers = 0;

  /**
   * Applies one extra mob argument to a spawned entity. The constant key and the alternative names
   * are the same lookup keys the original enum used.
   */
  private enum SpawnMobValue {
    GLOW("glow", false),
    NAME("n", true, "name"),
    HIDE_NAME("hidename", false),
    SPEED("s", true, "speed"),
    SCALE("sc", true, "scale"),
    EFFECT("effect", true, "effects"),
    MAX_HEALTH("hp", true),
    PICKUP("pickup", false, "pickitems"),
    NO_PICKUP("nopickup", false),
    IMMORTAL("immortal", false),
    INVISIBLE("invisible", false),
    NO_GRAVITY("nograv", false, "nogravity"),
    BABY("baby", false),
    ADULT("adult", false),
    DERP("derp", false),
    DUMB("dumb", false, "noai"),
    EXPIRE("expire", true, "ex"),
    NOT_PERSISTENT("notpersistent", false),
    TARGET("t", true, "target"),
    LEASH("leash", true, "leash"),
    TAMED("tamed", false),
    SADDLE("saddle", false),
    CHEST("chest", false),
    ANGRY("angry", false),
    UPWARDS("upwards", false, "dinnerbone"),
    RAINBOW("rainbow", false, "jeb"),
    CHARGE("charge", false, "charged"),
    BOUNCE("bounce", false),
    SCREAMING("screaming", false),
    BROWN("brown", false),
    NONE("none", false),
    ONFIRE("onfire", false),
    SKULL("skull", true, "head"),
    HELMET("helmet", true),
    CHESTPLATE("chestplate", true, "breastplate"),
    LEGS("legs", true),
    BOOTS("boots", true),
    MAIN_HAND("mhand", true, "mainhand"),
    OFF_HAND("ohand", true, "offhand"),
    POTION("potion", true),
    PARTICLE("particle", true),
    COLOR("color", true),
    RADIUS("radius", true),
    DURATION("duration", true),
    RADIUS_TICK("radiustick", true),
    NO_PLATE("noplate", false),
    ARMS("arms", false),
    NO_ARMS("noarms", false),
    SMALL("small", false),
    WARM("warm", false),
    COLD("cold", false),
    TEMPERATE("temperate", false),
    INCENDIARY("incendiary", false);

    private static final Map<String, SpawnMobValue> BY_KEY = new HashMap<String, SpawnMobValue>();

    static {
      for (SpawnMobValue value : values()) {
        BY_KEY.put(value.key, value);
        for (String alternative : value.alternatives) {
          BY_KEY.put(alternative, value);
        }
      }
    }

    private final String key;
    private final boolean variable;
    private final String[] alternatives;

    SpawnMobValue(String key, boolean variable, String... alternatives) {
      this.key = key;
      this.variable = variable;
      this.alternatives = alternatives;
    }

    boolean isVariable() {
      return variable;
    }

    static SpawnMobValue getByVariable(String input) {
      if (input == null) {
        return NONE;
      }
      if (input.contains("{") && input.endsWith("}")) {
        return BY_KEY.getOrDefault(input.split("\\{", 2)[0].toLowerCase(), NONE);
      }
      return BY_KEY.getOrDefault(input.split("-", 2)[0].toLowerCase(), NONE);
    }
  }

  private interface EquipmentApplier {
    void apply(Entity entity, ItemStack item);
  }

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("wrongEntityName", "{gcp}Incorrect entity name");
    configReader.get("spawnedEntities", "{gcp}Spawned {gcs}[entities] {gcp}entities ");
    configReader.get("spawnedPasangers", "{gcp}and {gcs}[passengers] {gcp}passengers");
    configReader.get("nothingSpawned", "{gcp}No entities spawned");
    configReader.get("cantSpawn", "{gcp}Can't spawn this type of entity");
  }

  @Override
  @CAnnotation(
      priority = 190,
      info = "Spawns entity at your location",
      args = "[EntityType]",
      tab = {"EntityType"},
      explanation = {
        "Example: /cmi spawnmob sheep:adult:rainbow 3 sp:5",
        "<T>More information at"
            + " www.zrips.net/cmi/commands/spawnmob/</T><URL>https://www.zrips.net/cmi/commands/spawnmob/</URL>"
      },
      regVar = {-100},
      consoleVar = {-100},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    int amount = 1;
    EntityType entityType = null;
    double spread = 0.0;
    Location location = null;
    List<String> extraArgs = new ArrayList<String>();

    Matcher matcher = ENTITY_ARGUMENT_PATTERN.matcher(args[0]);
    String entityName;
    String separator = null;
    if (!matcher.find()) {
      entityName = args[0];
    } else {
      entityName = matcher.group(1);
      separator = matcher.group(2);
    }
    String remainder =
        args[0].substring(entityName.length() + (separator != null ? separator.length() : 0));
    if (!remainder.isEmpty()) {
      if (separator != null && remainder.contains(separator)) {
        int depth = 0;
        int segmentStart = 0;
        int index = 0;
        while (index < remainder.length()) {
          char c = remainder.charAt(index);
          if (c == '{') {
            depth++;
          } else if (c == '}') {
            depth--;
          } else if (c == separator.charAt(0) && depth == 0) {
            extraArgs.add(remainder.substring(segmentStart, index));
            segmentStart = index + 1;
          }
          index++;
        }
        extraArgs.add(remainder.substring(segmentStart));
      } else {
        extraArgs.add(remainder);
      }
    }

    String[] remainingArgs = CMIArray.removeFirst(args);

    CMIEntityType cmiEntityType = CMIEntityType.get(entityName);
    if (cmiEntityType != null) {
      entityType = cmiEntityType.getType();
    }
    if (entityType == null) {
      CMILC.info((Object) this, commandSender, "wrongEntityName", new Object[0]);
      return null;
    }
    if (!entityType.isSpawnable()) {
      CMILC.info((Object) this, commandSender, "cantSpawn", new Object[0]);
      return null;
    }

    LinkedHashMap<String, Integer> passengers = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> passengerAttributes = new LinkedHashMap<String, Integer>();
    for (String arg : remainingArgs) {
      String lower = arg.toLowerCase();
      if (lower.startsWith("sp:") || lower.startsWith("spread:")) {
        try {
          spread = Double.parseDouble(arg.split(":")[1]);
        } catch (NumberFormatException ignored) {
        }
      } else if (lower.startsWith("quantity:") || lower.startsWith("q:")) {
        try {
          amount = Integer.parseInt(arg.split(":")[1]);
        } catch (NumberFormatException ignored) {
        }
      } else if (startsWithAny(
          lower,
          "passenger:",
          "pass:",
          "ps:",
          "passenger1:",
          "pass1:",
          "ps1:",
          "passenger;",
          "pass;",
          "ps;",
          "passenger1;",
          "pass1;",
          "ps1;")) {
        putPassengerEntry(passengers, arg, cMI.getConfigManager().SpawnMobMaxPassengers);
      } else if (startsWithAny(
          lower, "passenger2:", "pass2:", "ps2:", "passenger2;", "pass2;", "ps2;")) {
        putPassengerEntry(passengerAttributes, arg, cMI.getConfigManager().SpawnMobMaxPassengers);
      } else if (lower.startsWith("loc:") || lower.startsWith("l:")) {
        location = resolveLocationArgument(arg, arg.split(":", 2)[1], commandSender, location);
      } else if (!arg.contains(":")) {
        Location parsedLocation = null;
        if (location == null && (parsedLocation = CMILocation.fromString(arg)) != null) {
          location = parsedLocation;
        } else {
          Player onlinePlayer = CMIUser.getOnlinePlayer(arg);
          if (onlinePlayer != null) {
            location = onlinePlayer.getLocation();
          } else {
            try {
              amount = Integer.parseInt(arg);
            } catch (Throwable ignored) {
            }
          }
        }
      }
    }

    int maxQuantity = cMI.getConfigManager().SpawnMobMaxQuantity;
    amount = amount > maxQuantity ? maxQuantity : (amount < 1 ? 1 : amount);

    if (location == null && commandSender instanceof Player) {
      Player player = (Player) commandSender;
      Block block = cMI.getUtilManager().getTargetBlock(player, null, 50, true);
      if (block != null) {
        location = block.getLocation().clone().add(0.5, 1.0, 0.5);
        location.setYaw(player.getLocation().getYaw());
      }
    }
    if (location == null) {
      LC.info_NoPlayer.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    this.spawnIn(
        commandSender,
        location,
        entityType,
        amount,
        spread,
        passengers,
        passengerAttributes,
        extraArgs);
    return true;
  }

  private static boolean startsWithAny(String text, String... prefixes) {
    for (String prefix : prefixes) {
      if (text.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Parses "passenger:type-count" style arguments. Each dash separated number is a count limit
   * (capped at the configured maximum); the rest is the entity spec.
   */
  private static void putPassengerEntry(
      Map<String, Integer> target, String arg, int maxPassengers) {
    String[] parts = arg.split("[:;]", 2);
    if (!arg.contains("-")) {
      target.put(parts[1], 1);
      return;
    }
    String spec = parts[1];
    int limit = 1;
    for (String token : spec.split("-")) {
      try {
        limit = Integer.parseInt(token);
        spec = spec.replace("-" + token, "");
        if (limit > maxPassengers) {
          limit = maxPassengers;
        }
      } catch (NumberFormatException ignored) {
      }
    }
    target.put(spec, limit);
  }

  private static Location resolveLocationArgument(
      String arg, String value, CommandSender commandSender, Location current) {
    CMILocation parsed = CMILocation.fromString(value);
    if (parsed != null) {
      return parsed;
    }
    if (!arg.contains("/")) {
      Player onlinePlayer = CMIUser.getOnlinePlayer(value);
      return onlinePlayer != null ? onlinePlayer.getLocation() : current;
    }
    List<String> parts = new LinkedList<String>(Arrays.asList(value.split("/")));
    if (parts.size() < 3) {
      return current;
    }
    if (parts.size() == 3 && commandSender instanceof Player) {
      parts.add(((Player) commandSender).getWorld().getName());
    }
    if (parts.size() != 4) {
      return current;
    }
    double x;
    double y;
    double z;
    try {
      x = Double.parseDouble(parts.get(0));
      y = Double.parseDouble(parts.get(1));
      z = Double.parseDouble(parts.get(2));
    } catch (NumberFormatException e) {
      return current;
    }
    World targetWorld = null;
    for (World candidate : Bukkit.getWorlds()) {
      if (candidate.getName().equalsIgnoreCase(parts.get(3))) {
        targetWorld = candidate;
        break;
      }
    }
    return targetWorld != null ? new Location(targetWorld, x, y, z) : current;
  }

  private void spawnIn(
      CommandSender commandSender,
      Location location,
      EntityType entityType,
      int amount,
      double spread,
      Map<String, Integer> passengers,
      Map<String, Integer> passengerAttributes,
      List<String> extraArgs) {
    this.spawned = 0;
    this.spawnedPassangers = 0;
    final Random random = new Random(System.nanoTime());
    final CommandSender sender = commandSender;
    final Location spawnLocation = location;
    final EntityType type = entityType;
    final int count = amount;
    final double radius = spread;
    final List<String> args = extraArgs;
    CMIScheduler.runAtLocation(
        (JavaPlugin) CMI.getInstance(),
        spawnLocation,
        () -> {
          for (int i = 0; i < count; i++) {
            double offsetX = 0.0;
            if (radius != 0.0) {
              offsetX = random.nextInt((int) (radius * 2.0 * 10.0));
            }
            double offsetZ = 0.0;
            if (radius != 0.0) {
              offsetZ = random.nextInt((int) (radius * 2.0 * 10.0));
            }
            offsetX = offsetX / 10.0 - radius;
            offsetZ = offsetZ / 10.0 - radius;
            Entity entity;
            try {
              entity =
                  spawnLocation
                      .getWorld()
                      .spawnEntity(spawnLocation.clone().add(offsetX, 0.0, offsetZ), type);
            } catch (Exception exception) {
              exception.printStackTrace();
              continue;
            }
            this.spawned++;
            proccessMob(sender instanceof Player ? (Player) sender : null, entity, args);
            for (Map.Entry<String, Integer> passenger : passengers.entrySet()) {
              this.processPassenger(
                  passenger.getKey(), passenger.getValue(), entity, spawnLocation, sender);
            }
            for (Map.Entry<String, Integer> attribute : passengerAttributes.entrySet()) {
              this.processPassenger(
                  attribute.getKey(), attribute.getValue(), entity, spawnLocation, sender);
            }
            if (entity.isDead()) {
              this.spawned--;
            }
          }
          String message = null;
          if (this.spawned > 0) {
            message = CMILC.getIM(this, "spawnedEntities", "[entities]", this.spawned);
          }
          if (this.spawnedPassangers > 0) {
            message =
                String.valueOf(message)
                    + CMILC.getIM(this, "spawnedPasangers", "[passengers]", this.spawnedPassangers);
          }
          if (message != null) {
            sender.sendMessage(message);
          } else {
            CMILC.info((Object) this, sender, "nothingSpawned", new Object[0]);
          }
        });
  }

  private void processPassenger(
      String spec, int count, Entity mount, Location location, CommandSender commandSender) {
    String[] tokens = spec.contains(":") ? spec.split(":") : spec.split(";");
    String typeName = tokens[0];
    List<String> extraArgs = new ArrayList<String>(Arrays.asList(tokens));
    if (!extraArgs.isEmpty()) {
      extraArgs.remove(0);
    }
    EntityType passengerType = null;
    for (EntityType candidate : EntityType.values()) {
      if (typeName.equalsIgnoreCase(candidate.name())) {
        passengerType = candidate;
        break;
      }
    }
    if (passengerType == null) {
      return;
    }
    for (int i = 0; i < count; i++) {
      Entity passenger;
      try {
        passenger = location.getWorld().spawnEntity(location, passengerType);
      } catch (Exception exception) {
        continue;
      }
      this.spawnedPassangers++;
      proccessMob(
          commandSender instanceof Player ? (Player) commandSender : null, passenger, extraArgs);
      if (Version.isCurrentLower(Version.v1_11_R1)) {
        mount.setPassenger(passenger);
      } else {
        mount.addPassenger(passenger);
      }
      mount = passenger;
    }
  }

  private static ItemStack getItem(String input, CMIAsyncHead asyncHead) {
    if (input == null) {
      return null;
    }
    Material material = null;
    String name = input;
    List<String> parts = new LinkedList<String>();
    if (input.contains("/")) {
      parts = new LinkedList<String>(Arrays.asList(input.split("/")));
      name = parts.remove(0);
    }
    CMIItemStack namedItem = CMILib.getInstance().getItemManager().getItem(name);
    CMIMaterial cmiMaterial = namedItem == null ? null : namedItem.getCMIType();
    if (cmiMaterial != null) {
      material = cmiMaterial.getMaterial();
    }
    CMIItemStack headItem = null;
    if (cmiMaterial != null && cmiMaterial.isPlayerHead()) {
      headItem = CMILib.getInstance().getItemManager().getItem(input.replace("/", ":"), asyncHead);
      if (!headItem.getCMIType().isValidAsItemStack()) {
        headItem = null;
      }
    }
    if (material == null) {
      for (Material candidate : Material.values()) {
        if (name.equalsIgnoreCase(candidate.name())) {
          material = candidate;
          break;
        }
      }
    }
    if (material == null) {
      return null;
    }
    ItemStack result = new ItemStack(material, 1);
    if (headItem != null && !headItem.getCMIType().isNone()) {
      result = headItem.getItemStack();
    }
    if (result.getItemMeta() instanceof LeatherArmorMeta) {
      LeatherArmorMeta meta = (LeatherArmorMeta) result.getItemMeta();
      if (parts.size() == 3) {
        try {
          int red = Integer.parseInt(parts.get(0));
          int green = Integer.parseInt(parts.get(1));
          int blue = Integer.parseInt(parts.get(2));
          red = Math.min(255, Math.max(0, red));
          green = Math.min(255, Math.max(0, green));
          blue = Math.min(255, Math.max(0, blue));
          meta.setColor(Color.fromRGB(red, green, blue));
        } catch (NumberFormatException ignored) {
        }
      } else if (parts.size() == 1) {
        CMIChatColor chatColor = CMIChatColor.getColor(parts.get(0));
        if (chatColor != null) {
          meta.setColor(chatColor.getRGBColor());
        } else {
          chatColor = CMIChatColor.getByCustomName(parts.get(0));
          if (chatColor != null) {
            meta.setColor(chatColor.getRGBColor());
          }
        }
      }
      result.setItemMeta(meta);
    }
    return result;
  }

  public static void proccessMob(CommandSender commandSender, Entity entity, String values) {
    List<String> list = values == null ? new ArrayList<String>() : Arrays.asList(values.split(":"));
    proccessMob(null, entity, list);
  }

  private static void proccessMob(Player player, final Entity entity, List<String> args) {
    for (String arg : args) {
      if (arg == null) {
        break;
      }
      SpawnMobValue value = SpawnMobValue.getByVariable(arg);
      String variableValue = null;
      if (value.isVariable()) {
        if (arg.contains("{") && arg.endsWith("}")) {
          variableValue = arg.split("\\{", 2)[1];
          variableValue = variableValue.substring(0, variableValue.length() - 1);
        } else if (arg.contains("-")) {
          variableValue = arg.split("-", 2)[1];
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_21_R2) && entity instanceof Salmon) {
        Salmon salmon = (Salmon) entity;
        switch (arg.toLowerCase()) {
          case "small":
            salmon.setVariant(Salmon.Variant.SMALL);
            break;
          case "medium":
            salmon.setVariant(Salmon.Variant.MEDIUM);
            break;
          case "large":
            salmon.setVariant(Salmon.Variant.LARGE);
            break;
          default:
            break;
        }
        continue;
      }

      switch (value) {
        case GLOW:
          try {
            entity.setGlowing(true);
          } catch (Exception ignored) {
          }
          break;
        case NAME:
          if (variableValue != null) {
            entity.setCustomName(
                CMIChatColor.translate(CMIText.replaceUnderScoreSpace(variableValue)));
            entity.setCustomNameVisible(true);
          }
          break;
        case HIDE_NAME:
          entity.setCustomNameVisible(false);
          break;
        case SPEED:
          if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            try {
              int amplifier = Integer.parseInt(variableValue);
              if (amplifier > 0) {
                living.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier));
              }
            } catch (Throwable ignored) {
            }
          }
          break;
        case SCALE:
          if (variableValue != null) {
            applyScaleArg(entity, variableValue);
          } else {
            applyFallbackValue(player, entity, arg, value, variableValue);
          }
          break;
        case EFFECT:
          if (entity instanceof LivingEntity && variableValue != null) {
            applyPotionEffects((LivingEntity) entity, variableValue);
          }
          break;
        case MAX_HEALTH:
          if (entity instanceof Damageable) {
            try {
              int health = CMINumber.clamp(Integer.parseInt(variableValue), 1, 2048);
              ((Damageable) entity).setMaxHealth(health);
              ((Damageable) entity).setHealth(health);
            } catch (Throwable ignored) {
            }
          }
          break;
        case PICKUP:
          if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setCanPickupItems(true);
          }
          break;
        case NO_PICKUP:
          if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setCanPickupItems(false);
          }
          break;
        case IMMORTAL:
          entity.setInvulnerable(true);
          if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
          }
          break;
        case INVISIBLE:
          if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setVisible(false);
          }
          break;
        case NO_GRAVITY:
          entity.setGravity(false);
          break;
        case BABY:
          if (entity instanceof Ageable) {
            try {
              ((Ageable) entity).setBaby();
              ((Ageable) entity).setAge(-2147483647);
            } catch (Exception ignored) {
            }
          }
          break;
        case ADULT:
          if (entity instanceof Ageable) {
            try {
              ((Ageable) entity).setAdult();
            } catch (Exception ignored) {
            }
          }
          break;
        case DERP:
          if (entity instanceof Snowman) {
            try {
              if (arg.toLowerCase().equals("derp")) {
                ((Snowman) entity).setDerp(true);
              }
            } catch (Throwable ignored) {
            }
          }
          break;
        case DUMB:
          if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(false);
          }
          break;
        case EXPIRE:
          if (variableValue != null) {
            try {
              MobExpirationListener.addMob(
                  entity, CMINumber.clamp(Double.parseDouble(variableValue), 0.05, 1200));
              entity.setPersistent(false);
            } catch (Throwable ignored) {
            }
          }
          break;
        case NOT_PERSISTENT:
          try {
            entity.setPersistent(false);
          } catch (Throwable ignored) {
          }
          break;
        case TARGET:
          applyTargetArg(entity, variableValue);
          break;
        case LEASH:
          applyLeashArg(entity, variableValue);
          break;
        case TAMED:
          if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            tameable.setTamed(true);
            if (player != null) {
              tameable.setOwner((AnimalTamer) player);
            }
          }
          break;
        case SADDLE:
          try {
            if (entity instanceof Steerable) {
              ((Steerable) entity).setSaddle(true);
            }
            if (entity instanceof Horse) {
              ((Horse) entity).getInventory().setSaddle(CMIMaterial.SADDLE.newItemStack());
            }
          } catch (Throwable ignored) {
          }
          break;
        case CHEST:
          applyChestArg(entity);
          break;
        case ANGRY:
          if (entity instanceof Wolf) {
            ((Wolf) entity).setAngry(true);
          }
          if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && entity instanceof Bee) {
            ((Bee) entity).setAnger(600);
          }
          if (Version.isCurrentEqualOrHigher(Version.v1_11_R1) && entity instanceof Vindicator) {
            ((Vindicator) entity).setJohnny(true);
          }
          break;
        case UPWARDS:
          entity.setCustomName("Dinnerbone");
          break;
        case RAINBOW:
          if (entity instanceof Sheep) {
            Sheep sheep = (Sheep) entity;
            sheep.setCustomName("jeb_");
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
              CMI.getInstance().getNMS().setEntityTag(entity, "CustomName", "{\"text\":\"jeb_\"}");
            }
          }
          break;
        case CHARGE:
          if (entity instanceof Creeper) {
            ((Creeper) entity).setPowered(true);
          }
          break;
        case BOUNCE:
          if (Version.isCurrentLower(Version.v1_10_R1) || !(entity instanceof Projectile)) {
            break;
          }
          ((Projectile) entity).setBounce(true);
          break;
        case SCREAMING:
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1) && entity instanceof Goat) {
            ((Goat) entity).setScreaming(true);
          }
          break;
        case BROWN:
          if (!Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
            break;
          }
          if (entity instanceof MushroomCow) {
            ((MushroomCow) entity).setVariant(MushroomCow.Variant.BROWN);
          } else {
            applyFallbackValue(player, entity, arg, value, variableValue);
          }
          break;
        case ONFIRE:
          try {
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              entity.setVisualFire(true);
            }
          } catch (Throwable ignored) {
          }
          break;
        case SKULL:
          if (entity instanceof LivingEntity) {
            ItemStack head =
                getItem(
                    variableValue,
                    asyncHead(
                        entity,
                        (target, item) -> ((LivingEntity) target).getEquipment().setHelmet(item)));
            if (head != null) {
              ((LivingEntity) entity).getEquipment().setHelmet(head);
            }
            if (entity instanceof Mob) {
              ((Mob) entity).getEquipment().setHelmetDropChance(0.0f);
            }
          }
          break;
        case HELMET:
          applyEquipmentArg(
              entity,
              variableValue,
              (target, item) -> ((LivingEntity) target).getEquipment().setHelmet(item));
          break;
        case CHESTPLATE:
          applyEquipmentArg(
              entity,
              variableValue,
              (target, item) -> ((LivingEntity) target).getEquipment().setChestplate(item));
          break;
        case LEGS:
          applyEquipmentArg(
              entity,
              variableValue,
              (target, item) -> ((LivingEntity) target).getEquipment().setLeggings(item));
          break;
        case BOOTS:
          applyEquipmentArg(
              entity,
              variableValue,
              (target, item) -> ((LivingEntity) target).getEquipment().setBoots(item));
          break;
        case MAIN_HAND:
          applyMainHandArg(entity, variableValue);
          break;
        case OFF_HAND:
          applyOffHandArg(entity, variableValue);
          break;
        case ARMS:
          if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setArms(true);
          }
          break;
        case NO_ARMS:
          if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setArms(false);
          }
          break;
        case NO_PLATE:
          if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setBasePlate(false);
          }
          break;
        case SMALL:
          if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setSmall(true);
          }
          break;
        case WARM:
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
            if (entity instanceof Pig) {
              ((Pig) entity).setVariant(Pig.Variant.WARM);
            } else if (entity instanceof Cow) {
              ((Cow) entity).setVariant(Cow.Variant.WARM);
            } else if (entity instanceof Chicken) {
              ((Chicken) entity).setVariant(Chicken.Variant.WARM);
            }
          }
          break;
        case COLD:
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
            if (entity instanceof Pig) {
              ((Pig) entity).setVariant(Pig.Variant.COLD);
            } else if (entity instanceof Cow) {
              ((Cow) entity).setVariant(Cow.Variant.COLD);
            } else if (entity instanceof Chicken) {
              ((Chicken) entity).setVariant(Chicken.Variant.COLD);
            }
          }
          break;
        case TEMPERATE:
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
            if (entity instanceof Pig) {
              ((Pig) entity).setVariant(Pig.Variant.TEMPERATE);
            } else if (entity instanceof Cow) {
              ((Cow) entity).setVariant(Cow.Variant.TEMPERATE);
            } else if (entity instanceof Chicken) {
              ((Chicken) entity).setVariant(Chicken.Variant.TEMPERATE);
            }
          }
          break;
        case INCENDIARY:
          if (entity instanceof TNTPrimed) {
            ((TNTPrimed) entity).setIsIncendiary(true);
          }
          break;
        default:
          applyFallbackValue(player, entity, arg, value, variableValue);
          break;
      }
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && entity instanceof Villager) {
      Villager villager = (Villager) entity;
      try {
        if (villager.getProfession() != Villager.Profession.NONE
            && villager.getVillagerLevel() < 2) {
          villager.setVillagerLevel(2);
        }
      } catch (Throwable ignored) {
      }
    }
    try {
      if (entity instanceof EnderDragon) {
        ((EnderDragon) entity).setPhase(EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET);
      }
    } catch (Throwable ignored) {
    }
  }

  private static void applyScaleArg(Entity entity, String value) {
    if (!(entity instanceof LivingEntity) || Version.isCurrentLower(Version.v1_20_R4)) {
      return;
    }
    try {
      double scale;
      if (value.contains("-")) {
        double min = Double.parseDouble(value.split("-", 2)[0]);
        double max = Double.parseDouble(value.split("-", 2)[1]);
        scale = Math.random() * (max - min) + min;
      } else {
        scale = Double.parseDouble(value);
      }
      if (!(scale > 0.0)) {
        return;
      }
      CMIAttribute.SCALE.setBaseValue(entity, scale);
    } catch (Throwable ignored) {
    }
  }

  private static void applyPotionEffects(LivingEntity living, String value) {
    String[] effects = value.contains("-") ? value.split("-") : value.split(",");
    for (String effect : effects) {
      if (!effect.contains("/")) {
        continue;
      }
      String[] fields = effect.split("/");
      if (fields.length != 3) {
        continue;
      }
      try {
        PotionEffectType type = CMIPotionEffectType.get(fields[0]);
        if (type != null) {
          living.addPotionEffect(
              new PotionEffect(
                  type, Integer.parseInt(fields[1]) * 20, Integer.parseInt(fields[2])));
        }
      } catch (NumberFormatException ignored) {
      }
    }
  }

  private static void applyTargetArg(Entity entity, String name) {
    Player target = CMIUser.getOnlinePlayer(name);
    if (target == null) {
      return;
    }
    try {
      if (entity instanceof ShulkerBullet) {
        ((ShulkerBullet) entity).setTarget(target);
        return;
      }
    } catch (Throwable ignored) {
    }
    if (!(entity instanceof Mob)) {
      return;
    }
    ((Mob) entity).setTarget(target);
    if (Version.isCurrentEqualOrHigher(Version.v1_15_R1) && entity instanceof Bee) {
      ((Bee) entity).setAnger(1200);
    }
    Vector direction =
        target.getLocation().toVector().clone().subtract(entity.getLocation().toVector());
    entity.teleport(entity.getLocation().clone().setDirection(direction));
  }

  private static void applyLeashArg(Entity entity, String name) {
    if (!Version.isPaperBranch()) {
      return;
    }
    try {
      CMIEntityType cmiEntityType = CMIEntityType.get(name);
      if (cmiEntityType == null) {
        return;
      }
      Entity holder = entity.getWorld().spawnEntity(entity.getLocation(), cmiEntityType.getType());
      if (!(holder instanceof Leashable)) {
        return;
      }
      ((Leashable) holder).setLeashHolder(entity);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private static void applyChestArg(Entity entity) {
    if (Version.isCurrentEqual(Version.v1_11_R1)) {
      try {
        if (entity instanceof Donkey) {
          ((Donkey) entity).setCarryingChest(true);
        } else if (entity instanceof Mule) {
          ((Mule) entity).setCarryingChest(true);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1) && entity instanceof ChestedHorse) {
      ((ChestedHorse) entity).setCarryingChest(true);
    }
  }

  private static CMIAsyncHead asyncHead(final Entity entity, final EquipmentApplier applier) {
    return new CMIAsyncHead() {
      @Override
      public void afterAsyncUpdate(final ItemStack itemStack) {
        CMIScheduler.runTask(
            (JavaPlugin) CMI.getInstance(),
            () -> {
              if (entity.isDead()) {
                return;
              }
              if (itemStack != null) {
                applier.apply(entity, itemStack);
              }
            });
      }
    };
  }

  private static void applyEquipmentArg(Entity entity, String value, EquipmentApplier applier) {
    if (!(entity instanceof LivingEntity)) {
      return;
    }
    ItemStack item = getItem(value, asyncHead(entity, applier));
    if (item == null) {
      return;
    }
    applier.apply(entity, item);
  }

  private static void applyMainHandArg(Entity entity, String value) {
    if (!(entity instanceof LivingEntity)) {
      return;
    }
    ItemStack item = getItem(value, asyncHead(entity, spawnmob::applyMainHandItem));
    if (item == null) {
      return;
    }
    LivingEntity living = (LivingEntity) entity;
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      if (entity instanceof Enderman) {
        Enderman enderman = (Enderman) entity;
        if (Version.isCurrentHigher(Version.v1_13_R1)) {
          if (item.getType().isBlock()) {
            enderman.setCarriedBlock(item.getType().createBlockData());
          }
        } else {
          enderman.setCarriedMaterial(item.getData());
        }
        return;
      }
      living.getEquipment().setItemInMainHand(item);
      if (entity instanceof Mob) {
        ((Mob) entity).getEquipment().setItemInMainHandDropChance(0.0f);
      }
    } else {
      living.getEquipment().setItemInHand(item);
      if (entity instanceof Mob) {
        ((Mob) entity).getEquipment().setItemInHandDropChance(0.0f);
      }
    }
  }

  private static void applyMainHandItem(Entity entity, ItemStack item) {
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      if (entity instanceof Enderman) {
        if (Version.isCurrentHigher(Version.v1_13_R1)) {
          if (item.getType().isBlock()) {
            ((Enderman) entity).setCarriedBlock(item.getType().createBlockData());
          }
        } else {
          ((Enderman) entity).setCarriedMaterial(item.getData());
        }
      } else {
        ((LivingEntity) entity).getEquipment().setItemInMainHand(item);
      }
    } else {
      ((LivingEntity) entity).getEquipment().setItemInHand(item);
    }
  }

  private static void applyOffHandArg(Entity entity, String value) {
    if (!(entity instanceof LivingEntity)) {
      return;
    }
    ItemStack item = getItem(value, asyncHead(entity, spawnmob::applyOffHandItem));
    if (item == null) {
      return;
    }
    LivingEntity living = (LivingEntity) entity;
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      living.getEquipment().setItemInOffHand(item);
      if (entity instanceof Mob) {
        ((Mob) entity).getEquipment().setItemInOffHandDropChance(0.0f);
      }
    } else {
      living.getEquipment().setItemInHand(item);
      if (entity instanceof Mob) {
        ((Mob) entity).getEquipment().setItemInHandDropChance(0.0f);
      }
    }
  }

  private static void applyOffHandItem(Entity entity, ItemStack item) {
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      ((LivingEntity) entity).getEquipment().setItemInOffHand(item);
    } else {
      ((LivingEntity) entity).getEquipment().setItemInHand(item);
    }
  }

  /**
   * Handles the values that are not in the main switch: the type specific arguments, then the area
   * effect cloud arguments, then the remaining entity type arguments. Matches the original
   * fall-through order.
   */
  private static void applyFallbackValue(
      Player player, Entity entity, String arg, SpawnMobValue value, String variableValue) {
    applyTypeSpecificArg(player, entity, arg);
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && entity instanceof AreaEffectCloud) {
      if (variableValue == null) {
        return;
      }
      if (applyAreaEffectCloudArg(entity, value, variableValue)) {
        return;
      }
    }
    applyEntityTypeSpecificArg(player, entity, arg);
  }

  private static void applyTypeSpecificArg(Player player, Entity entity, String arg) {
    if (entity instanceof Villager) {
      applyVillagerArg((Villager) entity, arg);
      return;
    }
    if (entity instanceof ExperienceOrb) {
      try {
        ((ExperienceOrb) entity).setExperience(Integer.parseInt(arg));
      } catch (Exception ignored) {
      }
      return;
    }
    if (entity instanceof TNTPrimed) {
      try {
        ((TNTPrimed) entity)
            .setFuseTicks(
                CMINumber.clamp(
                    (int) ((int) (Double.parseDouble(arg) * 20.0)), 1, Integer.MAX_VALUE));
      } catch (Exception ignored) {
      }
      return;
    }
    if (entity instanceof Ocelot) {
      Ocelot ocelot = (Ocelot) entity;
      switch (arg.toLowerCase()) {
        case "red":
          ocelot.setCatType(Ocelot.Type.RED_CAT);
          break;
        case "siamese":
          ocelot.setCatType(Ocelot.Type.SIAMESE_CAT);
          break;
        case "wild":
          ocelot.setCatType(Ocelot.Type.WILD_OCELOT);
          break;
        case "black":
          ocelot.setCatType(Ocelot.Type.BLACK_CAT);
          break;
        default:
          break;
      }
      return;
    }
    if (entity instanceof Sheep) {
      for (DyeColor color : DyeColor.values()) {
        if (color.toString().equalsIgnoreCase(arg)) {
          ((Sheep) entity).setColor(color);
        }
      }
      return;
    }
    if (entity instanceof Horse) {
      applyHorseArg(entity, arg);
      return;
    }
    if (entity instanceof Slime) {
      try {
        int size = Integer.parseInt(arg);
        if (size < 1) {
          size = 1;
        }
        ((Slime) entity).setSize(size);
      } catch (NumberFormatException ignored) {
      }
      return;
    }
    if (entity instanceof Item) {
      try {
        CMIItemStack stack = CMIItemStack.deserialize(arg);
        if (stack == null || stack.getItemStack() == null) {
          return;
        }
        ((Item) entity).setItemStack(stack.getItemStack());
      } catch (NumberFormatException ignored) {
      }
      return;
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_11_R1) && entity instanceof Llama) {
      for (Llama.Color color : Llama.Color.values()) {
        if (color.toString().equalsIgnoreCase(arg)) {
          ((Llama) entity).setColor(color);
          break;
        }
      }
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      if (entity instanceof Shulker) {
        for (DyeColor color : DyeColor.values()) {
          if (color.toString().equalsIgnoreCase(arg)) {
            ((Shulker) entity).setColor(color);
            break;
          }
        }
      } else if (entity instanceof Parrot) {
        for (Parrot.Variant variant : Parrot.Variant.values()) {
          if (variant.toString().equalsIgnoreCase(arg)) {
            ((Parrot) entity).setVariant(variant);
            break;
          }
        }
      }
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      if (entity instanceof Panda) {
        applyPandaArg((Panda) entity, arg);
      }
      if (entity instanceof Cat) {
        for (Cat.Type type : Cat.Type.values()) {
          if (type.name().replace("_", "").equalsIgnoreCase(arg)) {
            ((Cat) entity).setCatType(type);
            break;
          }
        }
      }
      if (entity instanceof Fox) {
        if (arg.equalsIgnoreCase("white")
            || arg.equalsIgnoreCase("arctic")
            || arg.equalsIgnoreCase("snow")) {
          ((Fox) entity).setFoxType(Fox.Type.SNOW);
        }
      }
    }
  }

  private static void applyPandaArg(Panda panda, String arg) {
    Panda.Gene mainGene = panda.getMainGene();
    Panda.Gene hiddenGene = panda.getHiddenGene();
    String mainName = arg;
    String hiddenName = null;
    if (arg.contains("-")) {
      mainName = arg.split("-")[0];
      hiddenName = arg.split("-")[1];
    } else if (arg.contains(",")) {
      mainName = arg.split(",")[0];
      hiddenName = arg.split(",")[1];
    }
    for (Panda.Gene gene : Panda.Gene.values()) {
      if (gene.toString().equalsIgnoreCase(mainName)) {
        mainGene = gene;
        break;
      }
    }
    if (hiddenName != null) {
      for (Panda.Gene gene : Panda.Gene.values()) {
        if (gene.toString().equalsIgnoreCase(hiddenName)) {
          hiddenGene = gene;
          break;
        }
      }
    }
    if (hiddenGene == null) {
      hiddenGene = mainGene;
    }
    if (hiddenGene != null) {
      panda.setHiddenGene(hiddenGene);
    }
    if (mainGene != null) {
      panda.setMainGene(mainGene);
    }
  }

  private static void applyVillagerArg(Villager villager, String arg) {
    for (Villager.Profession profession : Villager.Profession.values()) {
      if (CMIVillagerProfession.getName(profession).equalsIgnoreCase(arg)) {
        villager.setProfession(profession);
        break;
      }
    }
    try {
      int level = Integer.parseInt(arg);
      level = level < 1 ? 1 : (level > 5 ? 5 : level);
      villager.setVillagerLevel(level);
      return;
    } catch (Throwable notNumber) {
      try {
        for (Villager.Type type : Villager.Type.values()) {
          if (type.toString().equalsIgnoreCase(arg)) {
            villager.setVillagerType(type);
            break;
          }
        }
      } catch (Throwable ignored) {
      }
    }
  }

  private static void applyHorseArg(Entity entity, String arg) {
    Horse horse = (Horse) entity;
    boolean variantApplied = false;
    try {
      Horse.Variant variant = horseVariantFor(arg.toLowerCase());
      if (variant != null) {
        horse.setVariant(variant);
        variantApplied = true;
      }
    } catch (UnsupportedOperationException exception) {
      CMI.getInstance()
          .consoleMessage(
              "&4Using wrong entity type for horse?. &7Use new special horse types as of 1.12+"
                  + " version");
    }
    if (variantApplied) {
      return;
    }
    Horse.Color color = null;
    for (Horse.Color candidate : Horse.Color.values()) {
      if (candidate.toString().replace("_", "").equalsIgnoreCase(arg.replace("_", ""))) {
        color = candidate;
        break;
      }
    }
    if (color != null) {
      horse.setColor(color);
    }
  }

  private static Horse.Variant horseVariantFor(String lowerName) {
    switch (lowerName) {
      case "horse":
        return Horse.Variant.HORSE;
      case "donkey":
        return Horse.Variant.DONKEY;
      case "mule":
        return Horse.Variant.MULE;
      case "skeleton":
        return Horse.Variant.SKELETON_HORSE;
      case "undead":
      case "zombie":
      case "dead":
        return Horse.Variant.UNDEAD_HORSE;
      default:
        return null;
    }
  }

  /**
   * Area effect cloud arguments. Returns true when the value was handled (the original code either
   * applied it or skipped it), false when the value should fall through to the generic entity
   * handling.
   */
  private static boolean applyAreaEffectCloudArg(
      Entity entity, SpawnMobValue value, String variableValue) {
    AreaEffectCloud cloud = (AreaEffectCloud) entity;
    switch (value) {
      case POTION:
        {
          PotionType potionType = CMIPotionType.get(variableValue);
          if (potionType != null) {
            cloud.setBasePotionType(potionType);
          }
          return true;
        }
      case PARTICLE:
        {
          CMIEffect effect = CMIEffectManager.CMIParticle.getCMIEffect(variableValue);
          if (effect == null || effect.getParticle() == null) {
            return true;
          }
          try {
            cloud.setParticle(
                effect.getParticle().getParticle(),
                effect.getParticleParameter(entity.getLocation()));
          } catch (Throwable ignored) {
          }
          return true;
        }
      case COLOR:
        {
          CMIChatColor chatColor = CMIChatColor.getColor(variableValue);
          if (chatColor != null) {
            Color rgb = chatColor.getRGBColor();
            if (rgb != null) {
              cloud.setColor(rgb);
            }
          }
          return true;
        }
      case RADIUS:
        {
          try {
            float radius = Float.parseFloat(variableValue);
            if (radius > 0f) {
              cloud.setRadius(radius);
            }
          } catch (NumberFormatException ignored) {
          }
          return true;
        }
      case DURATION:
        {
          try {
            int ticks = (int) (Double.parseDouble(variableValue) * 20.0);
            if (ticks > 0 || ticks == -1) {
              cloud.setDuration(ticks);
            }
          } catch (NumberFormatException ignored) {
          }
          return true;
        }
      case RADIUS_TICK:
        {
          try {
            cloud.setRadiusPerTick(Float.parseFloat(variableValue));
          } catch (NumberFormatException ignored) {
          }
          return true;
        }
      default:
        return false;
    }
  }

  /**
   * Generic entity arguments that were checked after the type specific ones: axolotl, boat, item
   * display, wolf and happy ghast.
   */
  private static void applyEntityTypeSpecificArg(Player player, Entity entity, String arg) {
    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1) && entity instanceof Axolotl) {
      for (Axolotl.Variant variant : Axolotl.Variant.values()) {
        if (variant.name().replace("_", "").equalsIgnoreCase(arg)) {
          ((Axolotl) entity).setVariant(variant);
          break;
        }
      }
    }
    try {
      if (entity instanceof Boat) {
        Boat boat = (Boat) entity;
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
          Boat.Type boatType = Boat.Type.BIRCH;
          for (Boat.Type candidate : Boat.Type.values()) {
            if (candidate.toString().replace("_", "").equalsIgnoreCase(arg)) {
              boatType = candidate;
              break;
            }
          }
          boat.setBoatType(boatType);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_10_R1)) {
          TreeSpecies species = TreeSpecies.BIRCH;
          for (TreeSpecies candidate : TreeSpecies.values()) {
            if (candidate.toString().replace("_", "").equalsIgnoreCase(arg)) {
              species = candidate;
              break;
            }
          }
          boat.setWoodType(species);
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      if (entity instanceof ItemDisplay) {
        ItemDisplay display = (ItemDisplay) entity;
        CMIItemStack stack = CMIItemStack.deserialize(arg);
        if (stack != null
            && stack.getItemStack() != null
            && stack.getItemStack().getType().isItem()) {
          display.setItemStack(stack.getItemStack());
        } else {
          CMILC.info(spawnmob.class, player, "cantSpawn", new Object[0]);
          entity.remove();
        }
      } else if (entity instanceof BlockDisplay) {
        BlockDisplay display = (BlockDisplay) entity;
        CMIItemStack stack = CMIItemStack.deserialize(arg);
        if (stack != null
            && stack.getItemStack() != null
            && stack.getItemStack().getType().isBlock()) {
          display.setBlock(Bukkit.createBlockData(stack.getItemStack().getType()));
        } else {
          CMILC.info(spawnmob.class, player, "cantSpawn", new Object[0]);
          entity.remove();
        }
      }
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4) && entity instanceof Wolf) {
      Wolf wolf = (Wolf) entity;
      switch (arg.toLowerCase()) {
        case "spotted":
          wolf.setVariant(Wolf.Variant.SPOTTED);
          break;
        case "striped":
          wolf.setVariant(Wolf.Variant.STRIPED);
          break;
        case "pale":
          wolf.setVariant(Wolf.Variant.PALE);
          break;
        case "ashen":
          wolf.setVariant(Wolf.Variant.ASHEN);
          break;
        case "black":
          wolf.setVariant(Wolf.Variant.BLACK);
          break;
        case "rusty":
          wolf.setVariant(Wolf.Variant.RUSTY);
          break;
        case "snowy":
          wolf.setVariant(Wolf.Variant.SNOWY);
          break;
        case "woods":
          wolf.setVariant(Wolf.Variant.WOODS);
          break;
        case "chestnut":
          wolf.setVariant(Wolf.Variant.CHESTNUT);
          break;
        default:
          break;
      }
    }
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R5) && entity instanceof HappyGhast) {
      CMIMaterial harness = CMIMaterial.get(arg.toLowerCase() + "_harness");
      if (harness != null && !harness.isNone()) {
        ((HappyGhast) entity).getEquipment().setItem(EquipmentSlot.BODY, harness.newItemStack());
      }
    }
  }
}
