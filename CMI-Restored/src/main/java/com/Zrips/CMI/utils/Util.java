package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMICollision;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIMessageReplies;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Containers.CMIRay;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.itemInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.google.common.base.Charsets;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.CMIPotionEffectType;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.Yaml;

public class Util {
  public boolean Debug = true;
  private HashMap<UUID, String> worldCache = new HashMap<>();
  static Random random = new Random(System.nanoTime());
  private CMI plugin;
  Integer range = null;
  private static boolean updatedFile = false;

  public Util(CMI cmi) {
    this.plugin = cmi;

    for (World world : Bukkit.getWorlds()) {
      this.worldCache.put(world.getUID(), world.getName());
    }
  }

  public static boolean isOnGround(Player player) {
    Location location = player.getLocation();
    Block block = location.clone().add(0.0, -0.05, 0.0).getBlock();
    return !CMIMaterial.isAir(block.getType()) && player.isOnGround();
  }

  public static Player getAttacker(EntityDamageEvent entityDamageEvent) {
    if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)) {
      return null;
    }

    EntityDamageByEntityEvent entityDamageByEntityEvent =
        (EntityDamageByEntityEvent) entityDamageEvent;
    Player player = null;
    Entity entity = entityDamageByEntityEvent.getDamager();
    if (entity instanceof Player) {
      player = (Player) entity;
    } else if (entity instanceof Projectile) {
      Projectile projectile = (Projectile) entity;
      if (projectile.getShooter() instanceof Player) {
        player = (Player) projectile.getShooter();
      }
    }

    return player;
  }

  public static Entity getDamager(EntityDamageEvent entityDamageEvent) {
    if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)) {
      return null;
    }

    EntityDamageByEntityEvent entityDamageByEntityEvent =
        (EntityDamageByEntityEvent) entityDamageEvent;
    Object object = entityDamageByEntityEvent.getDamager();
    if (object instanceof Projectile) {
      Projectile projectile = (Projectile) object;
      if (projectile.getShooter() instanceof Player) {
        object = (Player) projectile.getShooter();
      }
    }

    return (Entity) object;
  }

  public static Util.SignSide getSignSide(Block block, Location location) {
    float floatValue =
        (float)
            Math.toDegrees(
                Math.atan2(
                    location.getX() - block.getX() - 0.5, location.getZ() - block.getZ() - 0.5));
    floatValue = floatValue < 0.0F ? floatValue + 360.0F : floatValue;
    BlockFace blockFace = new CMIBlock(block).getFacing();
    double facingDegrees = Math.toDegrees(Math.atan2(blockFace.getModX(), blockFace.getModZ()));
    double angleDelta = Math.abs(facingDegrees - floatValue) % 360.0;
    return (angleDelta > 180.0 ? 360.0 - angleDelta : angleDelta) < 90.0
        ? Util.SignSide.FRONT
        : Util.SignSide.BACK;
  }

  public String serializePotionEffect(PotionEffect potionEffect) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(potionEffect.getType().getName());
    stringBuilder.append(":");
    stringBuilder.append(potionEffect.getDuration() / 20);
    if (potionEffect.getAmplifier() > 0) {
      stringBuilder.append(":");
      stringBuilder.append(potionEffect.getAmplifier() + 1);
    }

    if (potionEffect.hasParticles()) {
      stringBuilder.append(":v");
    }

    return stringBuilder.toString();
  }

  public PotionEffect deserializePotionEffect(String string) {
    return this.deserializePotionEffect(string, null);
  }

  public PotionEffect deserializePotionEffect(String string, Integer integer) {
    if (string == null) {
      return null;
    }

    string = string.replace(" ", "_");
    String[] strings = string.split(":");
    PotionEffectType potionEffectType = null;
    int value = integer == null ? 0 : integer;
    int amplifier = 0;
    boolean flag = false;
    String[] effectParts = strings;
    int partCount = strings.length;

    for (int partIndex = 0; partIndex < partCount; partIndex++) {
      String part = effectParts[partIndex];
      if (potionEffectType == null) {
        potionEffectType = CMIPotionEffectType.get(part);
      } else if (part.equalsIgnoreCase("v")) {
        flag = true;
      } else {
        if (value == 0) {
          try {
            value = Integer.parseInt(part);
            continue;
          } catch (NumberFormatException numberFormatException) {
          }
        }

        if (value != 0 && amplifier == 0) {
          try {
            amplifier = Integer.parseInt(part);
          } catch (NumberFormatException numberFormatException2) {
          }
        }
      }
    }

    if (potionEffectType == null) {
      return null;
    }

    if (value == 0) {
      value = 15;
    }

    value *= 20;
    if (--amplifier < 0) {
      amplifier = 0;
    }

    return new PotionEffect(potionEffectType, value, amplifier, true, flag);
  }

  @Deprecated
  public boolean isNPC(Player player) {
    return CMINPCChecker.isNPC(player);
  }

  public UUID generateUUID(String string) {
    return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(Charsets.UTF_8));
  }

  public int getPlayerTrackingRange() {
    return this.getPlayerTrackingRange(null);
  }

  public boolean lookingAt(
      Player player, Vector vector, double yawTolerance, double pitchTolerance) {
    Location location = player.getEyeLocation();
    Vector toTarget = vector.clone().subtract(location.toVector()).normalize();
    double targetYaw = Math.toDegrees(Math.atan2(-toTarget.getX(), toTarget.getZ()));
    double targetPitch = Math.toDegrees(Math.asin(-toTarget.getY()));
    double yawDelta = (targetYaw - location.getYaw()) % 360.0;
    if (yawDelta > 180.0) {
      yawDelta -= 360.0;
    }

    if (yawDelta < -180.0) {
      yawDelta += 360.0;
    }

    double pitchDelta = targetPitch - location.getPitch();
    return Math.abs(yawDelta) <= yawTolerance && Math.abs(pitchDelta) <= pitchTolerance;
  }

  public int getPlayerTrackingRange(World world) {
    if (this.range == null) {
      this.range = 64;

      try {
        Object object = null;
        if (world != null) {
          object =
              Bukkit.spigot()
                  .getConfig()
                  .get("world-settings." + world.getName() + ".entity-tracking-range.players");
        }

        if (object == null) {
          object =
              Bukkit.spigot()
                  .getConfig()
                  .get("world-settings.default.entity-tracking-range.players");
        }

        if (object != null) {
          this.range = (Integer) object;
        }
      } catch (Exception | Error value) {
      }
    }

    return this.range;
  }

  @Deprecated
  public void addMessageReplayTo(String string, String replyTarget) {
    this.addMessageReplyTo(string, replyTarget);
  }

  @Deprecated
  public void addMessageReplyTo(String string, String replyTarget) {
    CMIMessageReplies.addMessageReplyTo(string, replyTarget);
  }

  @Deprecated
  public void addMessageReplyToBySender(String string, String replyTarget) {
    CMIMessageReplies.addMessageReplyToBySender(string, replyTarget);
  }

  @Deprecated
  public void removeMessageReplayTo(String string) {
    this.removeMessageReplyTo(string);
  }

  @Deprecated
  public void removeMessageReplyTo(String string) {
    CMIMessageReplies.removeMessageReplyTo(string);
  }

  @Deprecated
  public String getMessageReplayTo(String string) {
    return this.getMessageReplyTo(string);
  }

  @Deprecated
  public String getMessageReplyTo(String string) {
    return CMIMessageReplies.getMessageReplyTo(string);
  }

  public double getDistance(Location location, Location otherLocation) {
    if (location != null
        && otherLocation != null
        && location.getWorld() == otherLocation.getWorld()) {
      try {
        return location.distance(otherLocation);
      } catch (Throwable throwable) {
        return 2.147483647E9;
      }
    } else {
      return 2.147483647E9;
    }
  }

  public double getDistance(Vector vector, Vector otherVector) {
    if (vector != null && otherVector != null) {
      try {
        return vector.distance(otherVector);
      } catch (Throwable throwable) {
        return 2.147483647E9;
      }
    } else {
      return 2.147483647E9;
    }
  }

  public double getDistanceSquared(Vector vector, Vector otherVector) {
    if (vector != null && otherVector != null) {
      try {
        return vector.distanceSquared(otherVector);
      } catch (Throwable throwable) {
        return 2.147483647E9;
      }
    } else {
      return 2.147483647E9;
    }
  }

  public int getMaxWorldHeight(World world) {
    if (world == null) {
      return 256;
    }

    Integer integer = CMIWorldLimits.getFlyAboveRoof(world.getName());
    if (integer != null && integer > 0) {
      return integer;
    }

    if (world.getEnvironment() == null) {
      return 256;
    }

    try {
      switch (world.getEnvironment()) {
        case NORMAL:
        case THE_END:
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            return world.getMaxHeight();
          }

          return 256;
        case NETHER:
          return 128;
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return 256;
  }

  public Vector getVector(Location location, Location otherLocation) {
    double dx = location.getX() - otherLocation.getX();
    double dy = location.getY() - otherLocation.getY();
    double dz = location.getZ() - otherLocation.getZ();
    double yawAngle = Math.atan2(dz, dx);
    double pitchAngle = Math.atan2(Math.sqrt(dz * dz + dx * dx), dy) + Math.PI;
    double dirX = Math.sin(pitchAngle) * Math.cos(yawAngle);
    double dirZ = Math.sin(pitchAngle) * Math.sin(yawAngle);
    double dirY = Math.cos(pitchAngle);
    return new Vector(dirX, dirY, dirZ);
  }

  public double getYaw(Location location, Location otherLocation) {
    double dx = location.getX() - otherLocation.getX();
    double dz = location.getZ() - otherLocation.getZ();
    return Math.toDegrees(Math.atan2(dz, dx));
  }

  public double getPitch(Location location, Location otherLocation) {
    double dx = location.getX() - otherLocation.getX();
    double dy = location.getY() - otherLocation.getY();
    double dz = location.getZ() - otherLocation.getZ();
    return Math.toDegrees(Math.atan2(Math.sqrt(dz * dz + dx * dx), dy) + Math.PI);
  }

  public String createTextProbgressBar(int value, float floatValue, float fillAmount) {
    if (fillAmount < 0.0F) {
      fillAmount = 0.0F;
    }

    if (floatValue < 1.0F) {
      floatValue = 1.0F;
    }

    if (fillAmount > floatValue) {
      fillAmount = floatValue;
    }

    fillAmount = fillAmount * value / floatValue;
    floatValue = value;
    String fillPart = LC.info_ProgressBarFill.getLocale(new Object[0]);
    String emptyPart = LC.info_ProgressBarEmpty.getLocale(new Object[0]);
    String filledBar = "";

    for (int filledIndex = 0; filledIndex < fillAmount; filledIndex++) {
      if (filledBar.isEmpty()) {
        filledBar = filledBar + fillPart;
      } else {
        filledBar = filledBar + CMIChatColor.stripColor(fillPart);
      }
    }

    String emptyBar = "";

    for (float emptyIndex = fillAmount; emptyIndex < floatValue; emptyIndex++) {
      if (emptyBar.isEmpty()) {
        emptyBar = emptyBar + emptyPart;
      } else {
        emptyBar = emptyBar + CMIChatColor.stripColor(emptyPart);
      }
    }

    return filledBar + emptyBar;
  }

  public Block getHighestBlockAt(Location location, boolean flag) {
    for (int value = location.getWorld().getMaxHeight();
        value > CMIWorld.getMinHeight(location.getWorld());
        value--) {
      Block block =
          location.getWorld().getBlockAt(location.getBlockX(), value, location.getBlockZ());
      if (block != null
          && !block.getType().equals(Material.AIR)
          && (!flag || block.getType().isSolid())) {
        return block;
      }
    }

    return null;
  }

  public TreeMap<Double, Entity> getClosestEntities(
      Location location, int value, double minDistance, EntityType entityType) {
    HashSet<Entity> hashSet = new HashSet<>();

    for (int chunkDx = -1; chunkDx < 2; chunkDx++) {
      for (int chunkDz = -1; chunkDz < 2; chunkDz++) {
        hashSet.addAll(
            Arrays.asList(
                location
                    .getWorld()
                    .getChunkAt(
                        location.getChunk().getX() + chunkDx, location.getChunk().getZ() + chunkDz)
                    .getEntities()));
      }
    }

    double maxDistance = value * 2;
    TreeMap<Double, Entity> treeMap = new TreeMap();
    Entity entity = this.getClosestEntity(location, value, entityType);
    if (entity == null) {
      return treeMap;
    }

    for (Entity candidate : hashSet) {
      if ((entityType == null || candidate.getType().equals(entityType))
          && (candidate.getCustomName() == null
              || !candidate.getCustomName().equalsIgnoreCase("CMIArmorStandForSit"))) {
        double locationDistance = location.distance(candidate.getLocation());
        if (!(maxDistance < locationDistance)) {
          double anchorDistance = entity.getLocation().distance(candidate.getLocation());
          if (!(minDistance < anchorDistance)) {
            treeMap.put(locationDistance, candidate);
          }
        }
      }
    }

    return treeMap;
  }

  public Entity getClosestEntity(Location location, int value, EntityType entityType) {
    HashSet<Entity> hashSet = new HashSet<>();

    for (int chunkDx = -1; chunkDx < 2; chunkDx++) {
      for (int chunkDz = -1; chunkDz < 2; chunkDz++) {
        hashSet.addAll(
            Arrays.asList(
                location
                    .getWorld()
                    .getChunkAt(
                        location.getChunk().getX() + chunkDx, location.getChunk().getZ() + chunkDz)
                    .getEntities()));
      }
    }

    double closestDistance = value * 2;
    Entity entity = null;

    for (Entity candidate : hashSet) {
      if ((entityType == null || candidate.getType().equals(entityType))
          && (candidate.getCustomName() == null
              || !candidate.getCustomName().equalsIgnoreCase("CMIArmorStandForSit"))) {
        double candidateDistance = location.distance(candidate.getLocation());
        if (!(closestDistance < candidateDistance)) {
          closestDistance = candidateDistance;
          entity = candidate;
        }
      }
    }

    return entity;
  }

  public Entity getTargetEntity(Player player) {
    return this.getTargetEntity(player, 0.95, false, true);
  }

  public Entity getTargetEntity(Player player, double number) {
    return this.getTargetEntity(player, number, false, true);
  }

  public Entity getTargetEntity(Player player, boolean flag, boolean includeHidden) {
    return this.getTargetEntity(player, 0.95, flag, includeHidden);
  }

  public Entity getTargetEntity(Player player, double number, boolean flag, boolean includeHidden) {
    byte b = 30;
    var list =
        player.getNearbyEntities(b, b, b).stream()
            .filter(player::hasLineOfSight)
            .collect(Collectors.toList());
    CMIRay cmiRay = new CMIRay(player);
    double bestHitDistance = -1.0;
    Entity entity = null;

    for (Entity candidate : list) {
      if ((!flag || !includeHidden) && candidate instanceof Player) {
        Player targetPlayer = (Player) candidate;
        if (!flag && targetPlayer.getGameMode().name().equalsIgnoreCase("SPECTATOR")
            || !includeHidden && !player.canSee(targetPlayer)) {
          continue;
        }
      }

      CMIHitBox cmiHitBox = new CMIHitBox(candidate);
      double hitDistance = new CMICollision(cmiHitBox).collidesNearest(cmiRay, 0.0, b);
      if (hitDistance != -1.0 && (hitDistance < bestHitDistance || bestHitDistance == -1.0)) {
        bestHitDistance = hitDistance;
        entity = candidate;
      }
    }

    return entity;
  }

  public Block getTargetBlock(Player player, int value, boolean flag) {
    return this.getTargetBlock(player, null, value, flag);
  }

  public Block getTargetBlock(Player player, int value) {
    return this.getTargetBlock(player, null, value, false);
  }

  public Block getTargetBlock(Player player, Material material, int value) {
    return this.getTargetBlock(player, material, value, false);
  }

  public Block getTargetBlock(Player player, Material material, int value, boolean flag) {
    var arrayList = this.getLastTwoTargetBlock(player, material, value, flag);
    return arrayList.isEmpty() ? null : (Block) arrayList.get(arrayList.size() - 1);
  }

  public ArrayList<Block> getLastTwoTargetBlock(Player player, int value) {
    return this.getLastTwoTargetBlock(player, null, value, false);
  }

  public ArrayList<Block> getLastTwoTargetBlock(
      Player player, Material material, int value, boolean flag) {
    if (value > 240) {
      value = 240;
    }

    if (value < 1) {
      value = 1;
    }

    Block block = this.plugin.getNMS().getBlockLookingAt(player, value, flag);
    BlockIterator blockIterator = new BlockIterator(player, value);
    LinkedHashSet<Block> linkedHashSet = new LinkedHashSet();
    if (block == null) {
      return new ArrayList<>(linkedHashSet);
    }

    Block blockBeforeTarget = null;

    while (blockIterator.hasNext()) {
      Block pathBlock = (Block) blockIterator.next();
      if (blockBeforeTarget == null && !pathBlock.equals(block)) {
        blockBeforeTarget = pathBlock;
      }

      if (pathBlock.equals(block)) {
        linkedHashSet.add(blockBeforeTarget);
        break;
      }
    }

    linkedHashSet.add(block);
    return new ArrayList<>(linkedHashSet);
  }

  public itemInfo getItemInfo(String string) {
    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(string);
    return cmiItemStack != null
        ? new itemInfo(cmiItemStack.getType())
        : new itemInfo(CMIMaterial.get(string));
  }

  public int getItemData(ItemStack itemStack) {
    if (itemStack.getType().toString().contains("_EGG")) {
      return CMILib.getInstance().getReflectionManager().getEggId(itemStack);
    } else if (itemStack.getType().equals(CMIMaterial.SPAWNER.getMaterial())) {
      return new CMIItemStack(itemStack).getEntityType().getTypeId();
    } else {
      return Version.isCurrentEqualOrHigher(Version.v1_13_R1) ? 0 : itemStack.getData().getData();
    }
  }

  public String translateDamageCause(String string) {
    if (CMILib.getInstance().getLM().containsKey("info.DamageCause." + string.toLowerCase())) {
      string =
          CMILib.getInstance()
              .getLM()
              .getMessage("info.DamageCause." + string.toLowerCase(), new Object[0]);
    }

    return string;
  }

  public static int getOnlinePlayersCount() {
    if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
      return Bukkit.getOnlinePlayers().size();
    }

    int value = 0;

    for (Player player : Bukkit.getOnlinePlayers()) {
      value++;
    }

    return value;
  }

  public int getOnlinePlayerCount() {
    return this.getOnlinePlayerCount(null);
  }

  public int getOnlinePlayerCount(World world) {
    int value = 0;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (world == null || player.getWorld().equals(world)) {
        value++;
      }
    }

    return value;
  }

  public String getOnlinePlayersAsString() {
    return this.getOnlinePlayersAsString(null);
  }

  public String getOnlinePlayersAsString(String string) {
    String result = "";

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (string == null || !player.getName().equalsIgnoreCase(string)) {
        if (!result.isEmpty()) {
          result = result + ",";
        }

        result = result + player.getName();
      }
    }

    return result;
  }

  @Deprecated
  public List<Player> getPlayersFromRange(Location location, int value, boolean flag) {
    return this.getPlayersFromRange(location, value);
  }

  public List<Player> getPlayersFromRange(Location location, int value) {
    return this.getPlayersFromRange(null, location, value);
  }

  public List<Player> getPlayersFromRange(Player player, Location location, int value) {
    ArrayList<Player> arrayList = new ArrayList();

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      Location onlineLocation = onlinePlayer.getLocation();
      if (value < 0) {
        arrayList.add(onlinePlayer);
      } else if (onlineLocation.getWorld() == location.getWorld()
          && onlineLocation.getBlockX() <= location.getBlockX() + value
          && onlineLocation.getBlockX() >= location.getBlockX() - value
          && onlineLocation.getBlockY() <= location.getBlockY() + value
          && onlineLocation.getBlockY() >= location.getBlockY() - value
          && onlineLocation.getBlockZ() <= location.getBlockZ() + value
          && onlineLocation.getBlockZ() >= location.getBlockZ() - value
          && (player == null || onlinePlayer.canSee(player))) {
        arrayList.add(onlinePlayer);
      }
    }

    return arrayList;
  }

  public List<Player> getPlayersFromRangeForCounter(Location location, int value, boolean flag) {
    ArrayList<Player> arrayList = new ArrayList();

    for (Player player : Bukkit.getOnlinePlayers()) {
      Location playerLocation = player.getLocation();
      if (playerLocation.getWorld() == location.getWorld()
          && playerLocation.getBlockX() <= location.getBlockX() + value
          && playerLocation.getBlockX() >= location.getBlockX() - value
          && playerLocation.getBlockY() <= location.getBlockY() + value
          && playerLocation.getBlockY() >= location.getBlockY() - value
          && playerLocation.getBlockZ() <= location.getBlockZ() + value
          && playerLocation.getBlockZ() >= location.getBlockZ() - value) {
        if (!flag) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (!cmiUser.isJoinedCounter()
              && cmiUser.hasPermission(PermissionsManager.CMIPerm.command_counter_autojoin)) {
            cmiUser.setJoinedCounter(true);
          }

          if (!cmiUser.isJoinedCounter()) {
            continue;
          }
        }

        arrayList.add(player);
      }
    }

    return arrayList;
  }

  @Deprecated
  public World getWorld(String string) {
    return CMIWorld.getWorld(string);
  }

  public List<String> getWorldList() {
    ArrayList<String> arrayList = new ArrayList();

    for (World world : Bukkit.getWorlds()) {
      if (world != null && world.getName() != null) {
        arrayList.add(world.getName());
      }
    }

    return arrayList;
  }

  public void resendBlockInfo(Player player, Block block) {
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> player.sendBlockChange(block.getLocation(), block.getType(), (byte) 0),
        10L);
  }

  public boolean isFullInv(ItemStack[] itemStacks, List<ItemStack> list) {
    int value = 36;
    ItemStack[] slots = itemStacks;
    int slotCount = itemStacks.length;

    for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
      ItemStack itemStack = slots[slotIndex];
      if (itemStack != null) {
        value--;
      }
    }

    return value < list.size();
  }

  public List<ItemStack> ConvertInvToList(Inventory inventory) {
    ItemStack[] itemStacks = inventory.getContents();
    ArrayList<ItemStack> arrayList = new ArrayList();
    ItemStack[] contentsCopy = itemStacks;
    int value = itemStacks.length;

    for (int slotIndex = 0; slotIndex < value; slotIndex++) {
      ItemStack itemStack = contentsCopy[slotIndex];
      if (itemStack != null) {
        arrayList.add(itemStack);
      }
    }

    return arrayList;
  }

  public boolean canRepair(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    } else {
      Material material = Material.getMaterial(itemStack.getType().name());
      if (!material.isBlock()
          && material.getMaxDurability() >= 1
          && itemStack.getDurability() != 0) {
        Integer integer = getCustomModelData(itemStack);
        return integer != null
                && this.plugin.getRepairManager().getBlockedCustomDataID().contains(integer)
            ? false
            : !isUnbreakable(itemStack);
      } else {
        return false;
      }
    }
  }

  public boolean needRepair(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (!itemStack.hasItemMeta()) {
        return false;
      }

      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof Damageable)) {
        return false;
      }
    }

    Material material = Material.getMaterial(itemStack.getType().name());
    return !material.isBlock()
        && material.getMaxDurability() >= 1
        && itemStack.getDurability() != 0;
  }

  public ItemStack repairItem(ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (!itemStack.hasItemMeta()) {
        return null;
      }

      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof Damageable)) {
        return null;
      }

      Damageable damageable = (Damageable) itemMeta;
      damageable.setDamage(0);
      itemStack.setItemMeta(damageable);
      return itemStack;
    } else {
      Material material = Material.getMaterial(itemStack.getType().name());
      if (!material.isBlock()
          && material.getMaxDurability() >= 1
          && itemStack.getDurability() != 0) {
        itemStack.setDurability((short) 0);
        return itemStack;
      } else {
        return null;
      }
    }
  }

  public static Integer getCustomModelData(ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    } else if (!Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      Integer integer = new CMINBT(itemStack).getInt("CustomModelData");
      return integer == null ? null : integer;
    } else {
      ItemMeta itemMeta = itemStack.getItemMeta();
      return itemMeta != null && itemMeta.hasCustomModelData()
          ? itemMeta.getCustomModelData()
          : null;
    }
  }

  public static ItemStack setCustomModelData(ItemStack itemStack, int value) {
    if (itemStack == null) {
      return itemStack;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setCustomModelData(value);
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    } else {
      return (ItemStack) new CMINBT(itemStack).setInt("CustomModelData", value);
    }
  }

  public static boolean isUnbreakable(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      return itemStack.getItemMeta().isUnbreakable();
    }

    CMINBT cminbt = new CMINBT(itemStack);
    Byte unbreakableTag = cminbt.getByte("Unbreakable");
    return unbreakableTag != null && unbreakableTag == 1;
  }

  public static ItemStack setUnbreakable(ItemStack itemStack, boolean flag) {
    if (itemStack == null) {
      return itemStack;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setUnbreakable(flag);
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    } else {
      return (ItemStack) new CMINBT(itemStack).setByte("Unbreakable", (byte) (flag ? 1 : 0));
    }
  }

  public boolean hasSilkTouch(ItemStack itemStack, int value) {
    if (itemStack == null) {
      return false;
    }

    for (var entry : itemStack.getEnchantments().entrySet()) {
      if (((Enchantment) entry.getKey()).getName().equalsIgnoreCase("SILK_TOUCH")
          && (Integer) entry.getValue() >= value) {
        return true;
      }
    }

    return false;
  }

  @Deprecated
  public ItemStack setEntityType(ItemStack itemStack, EntityType entityType) {
    return CMIEntity.setEntityType(itemStack, entityType);
  }

  public boolean haveBlackListedItems(Player player, TeleportManager.TpAction tpAction) {
    if (!this.plugin.getTeleportManager().getBlockedItems().isEmpty()
        && !PermissionsManager.CMIPerm.teleport_bypassblacklist.hasPermission(player)) {
      if (this.plugin.getTeleportManager().isBlackListedItemsEnabledFor(tpAction)) {
        var hashMap = this.plugin.getUtilManager().getBlackListedItems(player);
        if (!hashMap.isEmpty()) {
          this.showBlackListedItemList(player, hashMap);
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  private void showBlackListedItemList(Player player, HashMap<Material, Integer> hashMap) {
    RawMessage rawMessage = new RawMessage();
    Material material = null;
    int value = 1;
    String string = "";

    for (var entry : hashMap.entrySet()) {
      if (material == null) {
        material = (Material) entry.getKey();
        value = (Integer) entry.getValue();
      }

      if (!string.isEmpty()) {
        string = string + " \n";
      }

      string =
          string
              + CMILC.info_BlackList.getLocale(
                  "[material]",
                  ((Material) entry.getKey()).name().toLowerCase(),
                  "[amount]",
                  entry.getValue(),
                  "[max]",
                  this.plugin.getTeleportManager().getBlockedItems().get(entry.getKey()));
    }

    if (material != null) {
      Integer integer = this.plugin.getTeleportManager().getBlockedItems().get(material);
      if (integer != null) {
        rawMessage
            .addText(
                CMILC.info_CantTeleport.getLocale(
                    "[item]", material.name().toLowerCase(), "[amount]", value, "[max]", integer))
            .addHover(string);
        rawMessage.show(player);
      }
    }
  }

  public HashMap<Material, Integer> getBlackListedItems(Player player) {
    HashMap<Material, Integer> hashMap = new HashMap();
    if (this.plugin.getTeleportManager().getBlockedItems().isEmpty()) {
      return hashMap;
    }

    PlayerInventory playerInventory = player.getInventory();
    HashMap<Material, Integer> allItems = new HashMap<>();
    allItems = this.getAllItemsFromInv(playerInventory, allItems);

    for (Entry entry : allItems.entrySet()) {
      Integer integer = this.plugin.getTeleportManager().getBlockedItems().get(entry.getKey());
      if (integer != null && (Integer) entry.getValue() > integer) {
        hashMap.put((Material) entry.getKey(), (Integer) entry.getValue());
      }
    }

    return hashMap;
  }

  private HashMap<Material, Integer> getAllItemsFromInv(
      Inventory inventory, HashMap<Material, Integer> hashMap) {
    ItemStack[] itemStacks;
    int value = (itemStacks = inventory.getContents()).length;

    for (int slotIndex = 0; slotIndex < value; slotIndex++) {
      ItemStack itemStack = itemStacks[slotIndex];
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        Inventory shulkerInventory = CMIShulkerBox.getShulkerInv(itemStack);
        if (shulkerInventory != null) {
          hashMap = this.getAllItemsFromInv(shulkerInventory, hashMap);
        }

        Integer integer = (Integer) hashMap.get(itemStack.getType());
        if (integer == null) {
          integer = 0;
        }

        hashMap.put(itemStack.getType(), integer + itemStack.getAmount());
      }
    }

    return hashMap;
  }

  public String convertLocToStringShort(Location location) {
    String string = "";
    if (location != null && location.getWorld() != null) {
      string = string + location.getWorld().getName();
      string = string + ";" + location.getBlockX();
      string = string + ";" + location.getBlockY();
      string = string + ";" + location.getBlockZ();
    }

    return string;
  }

  public boolean validName(String string) {
    return this.validName("[^a-zA-Z0-9\\-\\_]", string);
  }

  public boolean validName(String string, String input) {
    if (!input.contains(":") && !input.contains(".") && !input.contains("|")) {
      try {
        String stripped = input.replaceAll(string, "");
        return stripped.isEmpty() ? false : input.equals(stripped);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        String fallbackStripped = input.replaceAll("[^a-zA-Z0-9\\-\\_]", "");
        return fallbackStripped.isEmpty() ? false : input.equals(fallbackStripped);
      }
    } else {
      return false;
    }
  }

  public String getWorldName(UUID uuid) {
    World world = Bukkit.getWorld(uuid);
    if (world != null) {
      return world.getName();
    }

    try {
      String string = CMIWorld.getWorldName(uuid);
      if (string != null) {
        return string;
      }
    } catch (Throwable throwable) {
    }

    return this.worldCache.get(uuid);
  }

  public CMIChatColor getTpsColor(Double tps) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      tps = CMINumber.clamp(tps, 17.0, 20);
      float floatValue = (float) (tps - 17.0) / 3.0F;
      int red;
      int green;
      if (floatValue < 0.5F) {
        red = 255;
        green = (int) (floatValue * 2.0F * 255.0F);
      } else {
        red = (int) ((1.0F - (floatValue - 0.5F) * 2.0F) * 255.0F);
        green = (int) (255.0 - (floatValue - 0.5F) * 255.0F * 0.9);
      }

      return new CMIChatColor(String.format("#%02X%02X00", red, green));
    } else if (tps < 17.0) {
      return CMIChatColor.DARK_RED;
    } else if (tps < 18.3) {
      return CMIChatColor.RED;
    } else if (tps < 18.8) {
      return CMIChatColor.GOLD;
    } else if (tps < 19.2) {
      return CMIChatColor.YELLOW;
    } else {
      return tps < 19.7 ? CMIChatColor.GREEN : CMIChatColor.DARK_GREEN;
    }
  }

  public static File getFile(Player player) {
    return getFile(player.getUniqueId());
  }

  public static File getFile(UUID uuid) {
    return new File(
        ((World) Bukkit.getWorlds().get(0)).getWorldFolder(),
        "playerdata" + File.separator + uuid + ".dat");
  }

  public Player getRandomOnlinePlayer() {
    return this.getRandomOnlinePlayer(null);
  }

  public Player getRandomOnlinePlayer(Set<Player> set) {
    ArrayList<Player> arrayList = new ArrayList();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (set == null || !set.contains(player)) {
        arrayList.add(player);
      }
    }

    Collections.shuffle(arrayList, random);
    Player randomPlayer = null;
    if (!arrayList.isEmpty()) {
      randomPlayer = (Player) arrayList.get(0);
    }

    return randomPlayer;
  }

  public boolean blockedItemFromRenaming(
      CommandSender commandSender, ItemStack itemStack, ItemStack renamedItem, String string) {
    if (!(commandSender instanceof Player)) {
      return false;
    }

    var hashMap = this.plugin.getConfigManager().getItemRenamingPreventMap();
    if (!hashMap.isEmpty()
        && !PermissionsManager.CMIPerm.anvil_itemrename_bypass.hasPermission(commandSender)) {
      if (Config.ItemRenamingGlobalDisable) {
        return true;
      }

      for (Entry entry : hashMap.entrySet()) {
        if (((CMIItemStack) entry.getKey()).getType().equals(renamedItem.getType())) {
          if (((String) entry.getValue()).isEmpty()) {
            return true;
          }

          if (Config.ItemRenamingCheckSource) {
            ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : null;
            if (itemMeta != null && itemMeta.hasDisplayName()) {
              String sourceDisplayName = itemMeta.getDisplayName();
              Pattern pattern = Pattern.compile((String) entry.getValue());
              Matcher matcher = pattern.matcher(CMIChatColor.stripColor(sourceDisplayName));
              if (matcher.find()) {
                return true;
              }
            }
          }

          Pattern blockPattern = Pattern.compile((String) entry.getValue());
          Matcher nameMatcher = blockPattern.matcher(CMIChatColor.stripColor(string));
          if (nameMatcher.find()) {
            return true;
          }
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public String getLineSplitter(String string) {
    string = LC.info_SpliterMiddle.getLocale(new Object[] {"[textWidth]", string});
    short shortValue = 320;
    String spacer = LC.info_singleSpliter.getLocale(new Object[0]);
    String spacerColor = CMIChatColor.getLastColors(spacer);
    spacer = CMIChatColor.stripColor(spacer);
    StringBuilder stringBuilder = new StringBuilder();
    AffineTransform affineTransform = new AffineTransform();
    FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, true);
    Font font = new Font("Tahoma", 0, 12);
    int textWidth =
        (int) font.getStringBounds(CMIChatColor.stripColor(string), fontRenderContext).getWidth();
    int spacerCount = (shortValue - textWidth) / 12;
    stringBuilder.append(spacerColor);

    for (int leftIndex = 0; leftIndex < spacerCount; leftIndex++) {
      stringBuilder.append(spacer);
    }

    stringBuilder.append(string);

    for (int rightIndex = 0; rightIndex < spacerCount; rightIndex++) {
      stringBuilder.append(spacer);
    }

    return stringBuilder.toString();
  }

  private static Map<String, Object> replaceInternalWithCustom(Map<String, Object> map) {
    if (map == null) {
      return map;
    }

    HashMap<String, Object> hashMap = new HashMap();

    for (var entry : map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        Map nestedMap = (Map) entry.getValue();
        replaceInternalWithCustom(nestedMap);
      } else if (((String) entry.getKey()).equals("internal")
          && entry.getValue() instanceof String
          && map.get("blockMaterial") == null) {
        hashMap.put((String) entry.getKey(), entry.getValue());
        updatedFile = true;
      }
    }

    for (Entry replacedEntry : hashMap.entrySet()) {
      map.remove(replacedEntry.getKey());
      map.put("custom", replacedEntry.getValue());
    }

    return map;
  }

  public static void fixItemStackFile(File file) {
    if (!Version.isCurrentEqualOrLower(Version.v1_20_R3)) {
      FileInputStream fileInputStream = null;

      try {
        updatedFile = false;
        fileInputStream = new FileInputStream(file);
        Yaml yaml = new Yaml();
        Map map = (Map) yaml.load(fileInputStream);
        fileInputStream.close();
        if (map == null) {
          return;
        }

        Map updatedMap = replaceInternalWithCustom(map);
        if (updatedFile) {
          FileWriter fileWriter = new FileWriter(file);
          yaml.dump(updatedMap, fileWriter);
          fileWriter.close();
          return;
        }
      } catch (Throwable throwable) {
        return;
      } finally {
        if (fileInputStream != null) {
          try {
            fileInputStream.close();
          } catch (Throwable throwable2) {
          }
        }
      }
    }
  }

  public static void fixHeadNameSection(ConfigurationSection configurationSection) {
    if (configurationSection != null
        && !Version.isCurrentEqualOrLower(Version.v1_20_R3)
        && configurationSection.contains("meta")
        && configurationSection.get("meta") instanceof SkullMeta) {
      SkullMeta skullMeta = (SkullMeta) configurationSection.get("meta");
      if (skullMeta.getOwner() != null && skullMeta.getOwner().contains(" ")) {
        PlayerProfile playerProfile =
            Bukkit.createPlayerProfile(
                skullMeta.getOwnerProfile().getUniqueId(),
                skullMeta.getOwnerProfile().getName().replace(" ", ""));
        playerProfile.setTextures(skullMeta.getOwnerProfile().getTextures());
        skullMeta.setOwnerProfile(playerProfile);
      }
    }
  }

  public static String simplifyGradientColors(String string) {
    if (!string.contains("{#")) {
      return string;
    }

    String result = string;
    Matcher matcher = CMIChatColor.post2GradientPattern.matcher(result);
    Pattern pattern =
        Pattern.compile(
            "((\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\}))(.)");

    while (matcher.find()) {
      String gradientSegment = matcher.group();
      CMIChatColor cmiChatColor = null;
      String previousTag = null;
      String previousSuffix = "";
      double previousDistance = Double.MAX_VALUE;
      double lastDelta = Double.MAX_VALUE;
      Matcher segmentMatcher = pattern.matcher(gradientSegment);
      StringBuilder stringBuilder = new StringBuilder();

      while (segmentMatcher.find()) {
        String colorTag = segmentMatcher.group(1);
        CMIChatColor currentColor = CMIChatColor.getColor(colorTag);
        String currentSuffix = segmentMatcher.group(8);
        if (previousTag != null && cmiChatColor != null) {
          Color currentRgb = null;

          try {
            currentRgb =
                new Color(
                    Integer.valueOf(currentColor.getHex().substring(0, 2), 16),
                    Integer.valueOf(currentColor.getHex().substring(2, 4), 16),
                    Integer.valueOf(currentColor.getHex().substring(4, 6), 16));
          } catch (Throwable throwable) {
            break;
          }

          Color previousRgb = null;

          try {
            previousRgb =
                new Color(
                    Integer.valueOf(cmiChatColor.getHex().substring(0, 2), 16),
                    Integer.valueOf(cmiChatColor.getHex().substring(2, 4), 16),
                    Integer.valueOf(cmiChatColor.getHex().substring(4, 6), 16));
          } catch (Throwable throwable2) {
            break;
          }

          int currentRed = currentRgb.getRed();
          int previousRed = previousRgb.getRed();
          int redMean = currentRed + previousRed >> 1;
          int redDiff = currentRed - previousRed;
          int greenDiff = currentRgb.getGreen() - previousRgb.getGreen();
          int blueDiff = currentRgb.getBlue() - previousRgb.getBlue();
          double colorDistance =
              Math.sqrt(
                  ((512 + redMean) * redDiff * redDiff >> 8)
                      + 4 * greenDiff * greenDiff
                      + ((767 - redMean) * blueDiff * blueDiff >> 8));
          double distanceDelta = previousDistance - colorDistance;
          if (distanceDelta < 0.0) {
            double absDelta = -distanceDelta;
          }

          if (previousDistance == 0.0) {
            stringBuilder.append(previousSuffix);
          } else if (colorDistance == 0.0) {
            String sameColorTag = previousTag.substring(0, previousTag.length() - 1) + "<>}";
            stringBuilder.append(previousSuffix + sameColorTag);
          } else if (previousDistance == Double.MAX_VALUE) {
            String openEndTag = previousTag.substring(0, previousTag.length() - 1) + ">}";
            stringBuilder.append(openEndTag + previousSuffix);
          } else if (previousDistance - colorDistance <= 3.0
              && previousDistance - colorDistance >= -3.0) {
            stringBuilder.append(previousSuffix);
          } else {
            stringBuilder.append(previousTag + previousSuffix);
          }

          lastDelta = previousDistance - colorDistance;
          previousDistance = colorDistance;
        }

        cmiChatColor = currentColor;
        previousTag = colorTag;
        previousSuffix = currentSuffix;
      }

      if (previousTag != null) {
        if (lastDelta <= 3.0 && lastDelta >= -3.0) {
          stringBuilder.append(
              previousSuffix + previousTag.substring(0, previousTag.length() - 1) + "<}");
        } else {
          stringBuilder.append(
              previousTag.substring(0, previousTag.length() - 1) + "}" + previousSuffix);
        }
      }

      result = result.replace(gradientSegment, stringBuilder.toString());
    }

    return result;
  }

  @Deprecated
  public static ItemStack getItemFromObject(Object object) {
    return CMIPaperItemSerializator.getItem(object);
  }

  public enum SignSide {
    FRONT,
    BACK;
  }
}
