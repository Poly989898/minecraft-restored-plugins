package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Chat.CMIChatBubble;
import com.Zrips.CMI.events.CMIPlayerSitEvent;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMIBlock.Bisect;
import net.Zrips.CMILib.Container.CMIBlock.StairShape;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.SlabType;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Steerable;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AnimationManager {
  private CMI plugin;
  CMITask task = null;
  public static final String CMIArmorStandForSit = "CMIArmorStandForSit";
  public static final String CMIRainbowArmor = "CMIRainbowArmor";
  public static final String CMISoulBound = "CMISoulBound";
  public static final String CMIFakeSlime = "CMIFakeSlime";
  HashMap<UUID, Long> doubleClick = new HashMap<>();
  HashMap<UUID, LeatherAnimation> leatherArmor = new HashMap<>();
  HashMap<UUID, AnimationManager.Chair> playerSittingMap = new HashMap<>();
  HashMap<String, UUID> chairLoc = new HashMap<>();
  private HashMap<UUID, UUID> riding = new HashMap<>();
  private ConcurrentHashMap<UUID, Location> ridingNonSteerable = new ConcurrentHashMap<>();
  private HashMap<UUID, UUID> beingRidden = new HashMap<>();
  private boolean SitOnStairs = true;
  private boolean StairsAsChairs = true;
  private boolean SlabsAsChairs = true;
  private boolean CarpetsAsChairs = true;
  private boolean RemoveFromChairOnDamage = true;
  private boolean DoubleClick = true;
  private int DoubleClickDelay = 200;
  private int chairRange = 200;
  List<CMIPlayerInventory.CMIInventorySlot> armorTypes =
      new ArrayList<>(
          Arrays.asList(
              CMIPlayerInventory.CMIInventorySlot.Helmet,
              CMIPlayerInventory.CMIInventorySlot.ChestPlate,
              CMIPlayerInventory.CMIInventorySlot.Pants,
              CMIPlayerInventory.CMIInventorySlot.Boots));
  private CMITask autoTimerBukkitId = null;
  private Runnable autoTimer =
      new Runnable() {
        @Override
        public void run() {
          try {
            AnimationManager.this.checkLeatherArmors();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      };
  private static Method method = null;
  private static Field field = null;
  CMITask ridingTask = null;

  public AnimationManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void clearCache(UUID uuid) {
    this.doubleClick.remove(uuid);
    this.leatherArmor.remove(uuid);
    UUID ridingUuid = this.riding.remove(uuid);
    if (ridingUuid != null) {
      this.beingRidden.remove(ridingUuid);
    }

    AnimationManager.Chair chair = this.playerSittingMap.remove(uuid);
    if (chair != null && chair.getChairLoc() != null) {
      this.chairLoc.remove(
          this.plugin.getUtilManager().convertLocToStringShort(chair.getChairLoc()));
    }
  }

  public void addLeatherArmor(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      LeatherAnimation leatherAnimation = this.leatherArmor.remove(player.getUniqueId());
      if (leatherAnimation == null) {
        leatherAnimation = new LeatherAnimation();
      }

      leatherAnimation.getMap().clear();

      for (CMIPlayerInventory.CMIInventorySlot cmiInventorySlot : this.armorTypes) {
        ItemStack itemStack = cmiUser.getInventory().getItem(cmiInventorySlot);
        if (itemStack != null && CMIMaterial.isLeatherArmor(itemStack.getType())) {
          Integer integer = new CMINBT(itemStack).getInt("CMIRainbowArmor");
          if (integer != null) {
            String string = new CMINBT(itemStack).getString("CMISoulBound");
            if (string == null || string.equals(player.getUniqueId().toString())) {
              leatherAnimation.setAnimation(
                  cmiInventorySlot, AnimationManager.LeatherAnimationType.getById(integer));
            }
          }
        }
      }

      if (!leatherAnimation.getMap().isEmpty()) {
        this.leatherArmor.put(player.getUniqueId(), leatherAnimation);
      }

      if (this.autoTimerBukkitId == null) {
        this.autoTimerBukkitId =
            CMIScheduler.scheduleSyncRepeatingTask(this.plugin, this.autoTimer, 4L, 4L);
      }

      if (this.leatherArmor.isEmpty()) {
        this.stopLeatherUpdate();
      }
    }
  }

  public void stopLeatherUpdate() {
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
      this.autoTimerBukkitId = null;
    }
  }

  private void checkLeatherArmors() {
    for (var entry : new HashMap<>(this.leatherArmor).entrySet()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser((UUID) entry.getKey());
      if (!cmiUser.isOnline()) {
        this.leatherArmor.remove(entry.getKey());
      } else {
        CMIScheduler.runTaskAsynchronously(
            this.plugin,
            () -> {
              HashMap<com.Zrips.CMI.Modules.Animations.AnimationManager.LeatherAnimationType, List>
                  hashMap = new HashMap();

              for (CMIPlayerInventory.CMIInventorySlot cmiInventorySlot : this.armorTypes) {
                List list =
                    (List)
                        hashMap.get(
                            ((LeatherAnimation) entry.getValue()).getAnimation(cmiInventorySlot));
                if (list == null) {
                  list = new ArrayList();
                }

                list.add(cmiInventorySlot);
                hashMap.put(
                    ((LeatherAnimation) entry.getValue()).getAnimation(cmiInventorySlot), list);
              }

              for (Entry animationEntry : hashMap.entrySet()) {
                setColor(
                    cmiUser,
                    (AnimationManager.LeatherAnimationType) animationEntry.getKey(),
                    (List<CMIPlayerInventory.CMIInventorySlot>) animationEntry.getValue(),
                    (LeatherAnimation) entry.getValue());
              }
            });
      }
    }
  }

  private static void setColor(
      CMIUser cmiUser,
      AnimationManager.LeatherAnimationType leatherAnimationType,
      List<CMIPlayerInventory.CMIInventorySlot> list,
      LeatherAnimation leatherAnimation) {
    if (leatherAnimationType != null) {
      switch (leatherAnimationType) {
        case Rainbow:
          Float colorValue = leatherAnimation.getColorValue();
          Float saturationValue = leatherAnimation.getSaturationValue();
          float floatValue = 2.0F;
          if (saturationValue == null) {
            Random random = new Random(System.nanoTime());
            saturationValue = (float) random.nextDouble();
          }

          if (leatherAnimation.isSascending()) {
            saturationValue = saturationValue + 0.001F * floatValue;
          } else {
            saturationValue = saturationValue - 0.001F * floatValue;
          }

          if (saturationValue >= 1.0F) {
            leatherAnimation.setSascending(false);
            saturationValue = saturationValue - 0.001F * floatValue;
          }

          if (saturationValue.floatValue() <= 0.5) {
            saturationValue = 0.5F;
            leatherAnimation.setSascending(true);
            saturationValue = saturationValue + 0.001F * floatValue;
          }

          if (colorValue == null) {
            Random rnd = new Random(System.nanoTime());
            colorValue = (float) rnd.nextDouble();
          }

          if (leatherAnimation.isAscending()) {
            colorValue = colorValue + 0.0075F * floatValue;
          } else {
            colorValue = colorValue - 0.0075F * floatValue;
          }

          if (colorValue >= 1.0F) {
            leatherAnimation.setAscending(false);
            colorValue = colorValue - 0.0075F * floatValue;
          }

          if (colorValue <= 0.0F) {
            colorValue = 0.0F;
            leatherAnimation.setAscending(true);
            colorValue = colorValue + 0.0075F * floatValue;
          }

          leatherAnimation.setColorValue(colorValue);
          leatherAnimation.setSaturationValue(saturationValue);
          Color color = Color.getHSBColor(colorValue, saturationValue, saturationValue);
          org.bukkit.Color leatherColor =
              org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());

          for (CMIPlayerInventory.CMIInventorySlot cmiInventorySlot : list) {
            ItemStack itemStack = cmiUser.getInventory().getItem(cmiInventorySlot);
            if (itemStack != null && itemStack.getItemMeta() instanceof LeatherArmorMeta) {
              LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
              leatherArmorMeta.setColor(leatherColor);
              itemStack.setItemMeta(leatherArmorMeta);
            }
          }
          break;
        case Health:
          int value =
              (int)
                  (cmiUser.getPlayer().getHealth()
                      / CMIPlayer.getMaxHealth(cmiUser.getPlayer())
                      * 100.0);
          value = CMINumber.clamp(value, 0, 100);
          int inverse = 255 * (100 - value) / 100;
          int direct = 255 * value / 100;
          org.bukkit.Color fadeColor =
              org.bukkit.Color.fromRGB(
                  CMINumber.clamp(inverse, 0, 255), CMINumber.clamp(direct, 0, 255), 0);

          for (CMIPlayerInventory.CMIInventorySlot slot : list) {
            ItemStack armorItem = cmiUser.getInventory().getItem(slot);
            if (armorItem != null && armorItem.getItemMeta() instanceof LeatherArmorMeta) {
              LeatherArmorMeta armorMeta = (LeatherArmorMeta) armorItem.getItemMeta();
              armorMeta.setColor(fadeColor);
              armorItem.setItemMeta(armorMeta);
            }
          }
          break;
        case Biome:
          String string =
              CMIBiome.getBiomeName(cmiUser.getPlayer().getLocation().getBlock().getBiome());
          CMIBiome cmiBiome = CMIBiome.get(string);
          if (cmiBiome != null) {
            Color biomeColor = cmiBiome.getColor();
            org.bukkit.Color armorColor =
                org.bukkit.Color.fromRGB(
                    biomeColor.getRed(), biomeColor.getGreen(), biomeColor.getBlue());

            for (CMIPlayerInventory.CMIInventorySlot slot : list) {
              ItemStack armorItem = cmiUser.getInventory().getItem(slot);
              if (armorItem != null
                  && armorItem.hasItemMeta()
                  && armorItem.getItemMeta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta armorMeta = (LeatherArmorMeta) armorItem.getItemMeta();
                armorMeta.setColor(armorColor);
                armorItem.setItemMeta(armorMeta);
              }
            }
          }
          break;
        case Day:
          byte b = cmiUser.getPlayer().getLocation().getBlock().getLightLevel();
          int gray = b * 255 / 15;
          org.bukkit.Color grayColor = org.bukkit.Color.fromRGB(gray, gray, gray);

          for (CMIPlayerInventory.CMIInventorySlot slot : list) {
            ItemStack armorItem = cmiUser.getInventory().getItem(slot);
            if (armorItem != null && armorItem.getItemMeta() instanceof LeatherArmorMeta) {
              LeatherArmorMeta armorMeta = (LeatherArmorMeta) armorItem.getItemMeta();
              armorMeta.setColor(grayColor);
              armorItem.setItemMeta(armorMeta);
            }
          }
      }
    }
  }

  public static int getIntFromColor(int value, int green, int blue) {
    value = value << 16 & 0xFF0000;
    green = green << 8 & 0xFF00;
    blue &= 255;
    return 0xFF000000 | value | green | blue;
  }

  public boolean isDoubleClickWait(Player player) {
    if (!this.DoubleClick) {
      return false;
    }

    Long lastClick = this.doubleClick.get(player.getUniqueId());
    if (lastClick != null && System.currentTimeMillis() - lastClick <= this.DoubleClickDelay) {
      return false;
    }

    this.doubleClick.put(player.getUniqueId(), System.currentTimeMillis());
    return true;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Animations.SitOnStairs",
        new String[] {
          "Enable sitting on stair block by clicking on them with empty hand or by looking and"
              + " using command",
          "Requires cmi.command.sit.stairs"
        });
    this.SitOnStairs = configReader.get("Animations.SitOnStairs", true);
    this.StairsAsChairs = configReader.get("Animations.StairsAsChairs", true);
    this.SlabsAsChairs = configReader.get("Animations.SlabsAsChairs", true);
    this.CarpetsAsChairs = configReader.get("Animations.CarpetsAsChairs", false);
    this.RemoveFromChairOnDamage = configReader.get("Animations.RemoveFromChairOnDamage", true);
    configReader.addComment(
        "Animations.DoubleClick",
        new String[] {"Player will sit on chair only after rapid double click"});
    this.DoubleClick = configReader.get("Animations.DoubleClick", false);
    configReader.addComment(
        "Animations.DoubleClickDelay",
        new String[] {
          "Delay in milliseconds between clicks to sit on chair when double click is enabled"
        });
    this.DoubleClickDelay = configReader.get("Animations.DoubleClickDelay", 200);
    configReader.addComment(
        "Animations.ChairRange",
        new String[] {"Range in blocks from player to look up for valid chair block"});
    this.chairRange = configReader.get("Animations.ChairRange", 4);
  }

  public boolean isValidChairBlock(Block block) {
    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    if (!cmiMaterial.isSlab() && !cmiMaterial.isCarpet() && !cmiMaterial.isStairs()) {
      return false;
    }

    if ((!cmiMaterial.isStairs() || this.plugin.getAnimationManager().isStairsAsChairs())
        && (!cmiMaterial.isSlab() || this.plugin.getAnimationManager().isSlabsAsChairs())) {
      if (cmiMaterial.isCarpet() && !this.CarpetsAsChairs) {
        return false;
      }

      if (CMIMaterial.isSlab(block.getType())) {
        SlabType slabType = CMIMaterial.getSlabType(block);
        switch (slabType) {
          case TOP:
          case DOUBLE:
          case NOTSLAB:
            return false;
          case BOTTOM:
        }
      }

      if (!this.plugin
          .getTeleportHandler()
          .isEmpty(
              CMIMaterial.get(block.getLocation().clone().add(0.0, 1.0, 0.0).getBlock()), false)) {
        return false;
      }

      CMIBlock cmiBlock = new CMIBlock(block);
      Bisect bisect = cmiBlock.getBisect();
      if (bisect != null && bisect == Bisect.TOP) {
        return false;
      }

      BlockFace blockFace = cmiBlock.getFacing();
      if (blockFace != null && CMIMaterial.isStairs(block.getType())) {
        String string;
        switch (string = blockFace.toString()) {
          case "EAST":
          case "WEST":
          case "NORTH":
          case "SOUTH":
            return true;
        }

        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public void sit(Player player) {
    this.sit(player, false);
  }

  public void sit(Player player, boolean flag) {
    this.sit(player, player.getLocation(), flag);
  }

  public void sit(Player player, Location location, boolean flag) {
    Location copyLocation = location.clone();
    copyLocation.add(0.0, -1.0, 0.0);
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      copyLocation.add(0.0, -0.2, 0.0);
    }

    AnimationManager.Chair chair =
        new AnimationManager.Chair()
            .setArmorStandLoc(copyLocation.clone().add(0.0, -0.7, 0.0))
            .setChairLoc(location);
    chair.setPersistent(flag);
    if (this.sit(player, chair)) {
      this.chairLoc.put(
          this.plugin.getUtilManager().convertLocToStringShort(location), player.getUniqueId());
    }
  }

  public boolean isSomeOneSittingHere(Block block) {
    UUID uuid =
        this.chairLoc.get(
            this.plugin.getUtilManager().convertLocToStringShort(block.getLocation().clone()));
    return uuid != null;
  }

  private static Vector getStairLedgeDirection(Block block) {
    CMIBlock cmiBlock = new CMIBlock(block);
    BlockFace blockFace = cmiBlock.getFacing();
    if (blockFace == null) {
      return new Vector();
    }

    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    double number = 0.0;
    double angle = 0.0;
    if (!cmiMaterial.isStairs()) {
      return new Vector(number, 0.0, angle);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      StairShape stairShape = cmiBlock.getStairShape();
      BlockFace chairFace = cmiBlock.getFacing();
      switch (chairFace) {
        case NORTH:
          number += 0.5;
          angle += 0.75;
          switch (stairShape) {
            case INNER_LEFT:
            case OUTER_LEFT:
              number += 0.3;
              return new Vector(number, 0.0, angle);
            case INNER_RIGHT:
            case OUTER_RIGHT:
              number += -0.3;
              return new Vector(number, 0.0, angle);
            case STRAIGHT:
            default:
              return new Vector(number, 0.0, angle);
          }
        case EAST:
          number += 0.25;
          angle += 0.5;
          switch (stairShape) {
            case INNER_LEFT:
            case OUTER_LEFT:
              angle += 0.3;
              return new Vector(number, 0.0, angle);
            case INNER_RIGHT:
            case OUTER_RIGHT:
              angle += -0.3;
              return new Vector(number, 0.0, angle);
            case STRAIGHT:
            default:
              return new Vector(number, 0.0, angle);
          }
        case SOUTH:
          number += 0.5;
          angle += 0.25;
          switch (stairShape) {
            case INNER_LEFT:
            case OUTER_LEFT:
              number += -0.3;
              return new Vector(number, 0.0, angle);
            case INNER_RIGHT:
            case OUTER_RIGHT:
              number += 0.3;
              return new Vector(number, 0.0, angle);
            case STRAIGHT:
            default:
              return new Vector(number, 0.0, angle);
          }
        case WEST:
          number += 0.75;
          angle += 0.5;
          switch (stairShape) {
            case INNER_LEFT:
            case OUTER_LEFT:
              angle += -0.3;
              break;
            case INNER_RIGHT:
            case OUTER_RIGHT:
              angle += 0.3;
            case STRAIGHT:
            default:
              break;
          }
      }
    } else {
      switch (block.getData()) {
        case 0:
        case 1:
        case 2:
        case 3:
          switch (cmiBlock.getDirection()) {
            case upWest:
              number += 0.25;
              angle += 0.5;
              break;
            case upEast:
              number += 0.75;
              angle += 0.5;
              break;
            case upNorth:
              number += 0.5;
              angle += 0.25;
              break;
            case upSouth:
              number += 0.5;
              angle += 0.75;
            default:
              break;
          }
      }
    }

    return new Vector(number, 0.0, angle);
  }

  public void sit(Player player, Block block) {
    this.sit(player, block, false);
  }

  public void sit(Player player, Block block, boolean flag) {
    Location location = block.getLocation().clone();
    location.setYaw(player.getLocation().getYaw());
    CMIMaterial cmiMaterial = CMIMaterial.get(block);
    if (cmiMaterial.isStairs()) {
      Vector vector = getStairLedgeDirection(block);
      location.add(vector.getX(), -1.2, vector.getZ());
    }

    if (cmiMaterial.isSlab()) {
      location.add(0.5, -1.2, 0.5);
    }

    if (cmiMaterial.isCarpet()) {
      location.add(0.5, -1.6, 0.5);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      location.add(0.0, -0.2, 0.0);
    }

    AnimationManager.Chair chair = new AnimationManager.Chair();
    chair.setChairLoc(block.getLocation()).setArmorStandLoc(location);
    chair.setPersistent(flag);
    if (this.sit(player, chair)) {
      this.chairLoc.put(
          this.plugin.getUtilManager().convertLocToStringShort(block.getLocation()),
          player.getUniqueId());
    }
  }

  public boolean isSitting(Player player) {
    return player == null ? false : this.playerSittingMap.containsKey(player.getUniqueId());
  }

  public Entity getChair(Player player) {
    if (this.playerSittingMap.isEmpty()) {
      return null;
    }

    AnimationManager.Chair chair = this.playerSittingMap.get(player.getUniqueId());
    return chair == null ? null : chair.getEnt();
  }

  public void sitOnPlayer(Player player, Player target) {
    AnimationManager.Chair chair =
        new AnimationManager.Chair()
            .setChairLoc(target.getLocation())
            .setArmorStandLoc(target.getLocation());
    ArmorStand armorStand = null;
    Location location = chair.getArmorStandLoc();
    armorStand = (ArmorStand) location.getWorld().spawn(location, ArmorStand.class);
    armorStand.setBasePlate(false);
    armorStand.setVisible(false);
    armorStand.setGravity(false);
    armorStand.setSmall(true);
    armorStand.setCustomName("CMIArmorStandForSit");

    try {
      armorStand.setMetadata("CMIArmorStandForSit", new FixedMetadataValue(this.plugin, true));
    } catch (Exception exception) {
    }

    armorStand.setCustomNameVisible(false);
    armorStand.getLocation().setDirection(location.getDirection());
    armorStand.addPotionEffect(
        new PotionEffect(PotionEffectType.ABSORPTION, 99999999, 2, false, false));
    boolean flag = false;
    if (Version.isCurrentLower(Version.v1_11_R1)) {
      armorStand.setPassenger(player);
      flag = target.setPassenger(armorStand);
    } else {
      armorStand.addPassenger(player);
      flag = target.addPassenger(armorStand);
    }

    chair.setEnt(armorStand);
    this.playerSittingMap.put(player.getUniqueId(), chair);
    if (!flag) {
      this.removePlayerFromChair(player);
    } else {
      this.updateSitTask();
    }
  }

  private boolean sit(Player player, AnimationManager.Chair chair) {
    if (!player.isSneaking() && !this.plugin.getPlayerManager().getUser(player).isCuffed()) {
      CMIPlayerSitEvent cmiPlayerSitEvent = new CMIPlayerSitEvent(player, chair);
      CMI.getInstance().getServer().getPluginManager().callEvent(cmiPlayerSitEvent);
      if (cmiPlayerSitEvent.isCancelled()) {
        return false;
      }

      Location location = chair.getArmorStandLoc().clone();
      location.add(0.0, 1.7, 0.0);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      Location seatLocation = chair.getArmorStandLoc();
      CMIScheduler.runAtLocation(
          this.plugin,
          seatLocation,
          () -> {
            try {
              ArmorStand armorStand = null;
              if (Version.isCurrentEqualOrHigher(Version.v1_20_2)) {
                try {
                  armorStand =
                      (ArmorStand)
                          seatLocation
                              .getWorld()
                              .spawn(
                                  seatLocation,
                                  ArmorStand.class,
                                  stand -> {
                                    stand.setVisible(false);
                                    stand.setGravity(false);
                                  });
                } catch (Throwable throwable) {
                  throwable.printStackTrace();
                  armorStand =
                      (ArmorStand) seatLocation.getWorld().spawn(seatLocation, ArmorStand.class);
                  armorStand.setVisible(false);
                  armorStand.setGravity(false);
                }
              } else {
                armorStand =
                    (ArmorStand) seatLocation.getWorld().spawn(seatLocation, ArmorStand.class);
                armorStand.setVisible(false);
                armorStand.setGravity(false);
              }

              armorStand.setBasePlate(false);
              armorStand.setCustomName("CMIArmorStandForSit");
              armorStand.setCustomNameVisible(false);
              armorStand.getLocation().setDirection(seatLocation.getDirection());
              armorStand.addPotionEffect(
                  new PotionEffect(PotionEffectType.ABSORPTION, 99999999, 2, false, false));
              chair.setEnt(armorStand);
              boolean flag = false;
              if (Version.isCurrentLower(Version.v1_11_R1)) {
                flag = armorStand.setPassenger(player);
              } else {
                flag = armorStand.addPassenger(player);
              }

              this.playerSittingMap.put(player.getUniqueId(), chair);
              if (!flag) {
                this.removePlayerFromChair(player);
              } else {
                this.updateSitTask();
              }
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }
          });
      return true;
    } else {
      return false;
    }
  }

  @Deprecated
  public void removePlayer(UUID uuid) {
    Player player = CMIUser.getOnlinePlayer(uuid);
    if (player != null) {
      this.removePlayer(player);
    }
  }

  @Deprecated
  public void removePlayer(Player player) {
    this.removePlayer(player, true);
  }

  @Deprecated
  public void removePlayer(Player player, boolean flag) {
    this.removePlayerFromChair(player, flag, false);
  }

  public void removePlayerFromChair(UUID uuid) {
    Player player = CMIUser.getOnlinePlayer(uuid);
    if (player != null) {
      this.removePlayerFromChair(player);
    }
  }

  public void removePlayerFromChair(Player player) {
    this.removePlayerFromChair(player, true, false, true);
  }

  public void removePlayerFromChair(Player player, boolean flag) {
    this.removePlayerFromChair(player, flag, false, true);
  }

  public void removePlayerFromChair(Player player, boolean flag, boolean silent) {
    this.removePlayerFromChair(player, flag, silent, true);
  }

  public void removePlayerFromChair(
      Player player, boolean flag, boolean silent, boolean keepRiding) {
    this.updateSitTask();
    AnimationManager.Chair chair = this.playerSittingMap.remove(player.getUniqueId());
    if (chair != null) {
      if (chair.getChairLoc() != null) {
        this.chairLoc.remove(
            this.plugin.getUtilManager().convertLocToStringShort(chair.getChairLoc()));
      }

      Entity entity = chair.getEnt();
      if (entity != null) {
        CMIScheduler.runAtLocation(this.plugin, entity.getLocation(), entity::remove);
        if (player.getWorld().equals(chair.getArmorStandLoc().getWorld())
            && !(player
                    .getLocation()
                    .toVector()
                    .distance(chair.getArmorStandLoc().clone().add(0.0, 1.95, 0.0).toVector())
                > 1.0)) {
          CMILC.info("sit", player, "onStandUp");
          if (keepRiding) {
            if (flag) {
              CMIScheduler.runTask(this.plugin, () -> this.tpPlayer(player, chair, silent));
            } else {
              this.tpPlayer(player, chair, silent);
            }
          }
        }
      }
    }
  }

  private void tpPlayer(Player player, AnimationManager.Chair chair, boolean flag) {
    if (player.isOnline()) {
      Location location = chair.getArmorStandLoc().clone().add(0.0, 1.95, 0.0);
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        location.add(0.0, 0.2, 0.0);
      }

      location.setPitch(player.getLocation().getPitch());
      location.setYaw(player.getLocation().getYaw());
      CMIScheduler.runAtLocation(
          this.plugin,
          location,
          () -> {
            if (player.getWorld().equals(location.getWorld())) {
              if (CMIMaterial.isStairs(chair.getChairLoc().getBlock().getType())) {
                Vector vector = getStairLedgeDirection(chair.getChairLoc().getBlock());
                double number = (int) (vector.getX() * 10.0) / 10.0;
                double roundedZ = (int) (vector.getZ() * 10.0) / 10.0;
                if (number >= 0.7) {
                  number = 0.8;
                }

                if (roundedZ >= 0.7) {
                  roundedZ = 0.8;
                }

                location.setX(location.getBlockX() + number);
                location.setY(location.getY() + 0.0);
                location.setZ(location.getBlockZ() + roundedZ);
              }

              if (flag) {
                location.setX(location.getBlockX() + 0.5);
                location.setZ(location.getBlockZ() + 0.5);
              }

              if (!(player.getLocation().distance(location) > 100.0)) {
                this.plugin
                    .getTeleportHandler()
                    .teleportPlayer(player, location, TeleportCause.PLUGIN, false);
              }
            }
          });
    }
  }

  private void updateSitTask() {
    if (this.task == null && !this.playerSittingMap.isEmpty()) {
      this.task =
          CMIScheduler.runTimerAsync(
              this.plugin,
              () -> {
                var hashMap = new HashMap<>(this.playerSittingMap);

                for (Entry entry : hashMap.entrySet()) {
                  if (((AnimationManager.Chair) entry.getValue()).getEnt() instanceof ArmorStand) {
                    AnimationManager.Chair chair = (AnimationManager.Chair) entry.getValue();
                    if (!chair.isPersistent()
                        && chair.getLastCheck() + 3000L < System.currentTimeMillis()) {
                      if (this.plugin
                              .getUtilManager()
                              .getDistance(chair.getEnt().getLocation(), chair.getArmorStandLoc())
                          > 0.6) {
                        CMIScheduler.runTask(
                            this.plugin, () -> this.removePlayerFromChair((UUID) entry.getKey()));
                        return;
                      }

                      chair.setLastCheck(System.currentTimeMillis());
                    }

                    modifyArmorStandDirection(
                        (ArmorStand) ((AnimationManager.Chair) entry.getValue()).getEnt());
                  }
                }
              },
              0L,
              1L);
    } else if (this.task != null && this.playerSittingMap.isEmpty()) {
      this.task.cancel();
      this.task = null;
    }
  }

  private static void modifyArmorStandDirection(ArmorStand armorStand) {
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        armorStand.getLocation(),
        () -> {
          try {
            armorStand.setRotation(
                armorStand.getPassenger().getLocation().getYaw(),
                armorStand.getLocation().getPitch());
          } catch (Throwable throwable) {
            try {
              if (method == null) {
                method = armorStand.getClass().getMethod("getHandle");
              }

              Object object = method.invoke(armorStand);
              if (field == null) {
                if (Version.isCurrentLower(Version.v1_17_R1)) {
                  field = object.getClass().getField("yaw");
                } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
                  field = object.getClass().getField("aX");
                } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
                  field = object.getClass().getField("aA");
                } else {
                  field = object.getClass().getField("ay");
                }
              }

              if (armorStand.getPassenger() == null) {
                return;
              }

              field.set(object, armorStand.getPassenger().getLocation().getYaw());
            } catch (Throwable throwable2) {
            }
          }
        });
  }

  public boolean isSitOnStairs() {
    return this.SitOnStairs;
  }

  public void setSitOnStairs(boolean flag) {
    this.SitOnStairs = flag;
  }

  public boolean isSlabsAsChairs() {
    return this.SlabsAsChairs;
  }

  public boolean isStairsAsChairs() {
    return this.StairsAsChairs;
  }

  public UUID removeRiding(UUID uuid) {
    UUID ridingUuid = this.riding.remove(uuid);
    this.ridingNonSteerable.remove(uuid);
    if (ridingUuid != null) {
      this.beingRidden.remove(ridingUuid);
    }

    return ridingUuid;
  }

  public CompletableFuture<List<Entity>> removePassengers(Player player, String string) {
    ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();
    ArrayList passengers = new ArrayList();
    if (Version.isCurrentLower(Version.v1_11_R1)) {
      if (player.getPassenger() != null) {
        Entity entity = player.getPassenger();
        if (entity instanceof Slime
            && entity.getCustomName() != null
            && entity.getCustomName().equalsIgnoreCase("CMIFakeSlime")) {
          if (entity.getPassenger() != null) {
            passengers.add(entity.getPassenger());
          }

          entity.leaveVehicle();
          entity.remove();
        } else {
          passengers.add(player.getPassenger());
          player.getPassenger().leaveVehicle();
        }
      }
    } else {
      for (Entity passenger : player.getPassengers()) {
        if (passenger != null) {
          if (passenger instanceof Slime
              && passenger.getCustomName() != null
              && passenger.getCustomName().equalsIgnoreCase("CMIFakeSlime")) {
            if (passenger.getPassenger() != null) {
              passengers.add(passenger.getPassenger());
            }

            arrayList.add(
                CMIScheduler.runAtLocation(
                    this.plugin,
                    passenger.getLocation(),
                    () -> {
                      passenger.leaveVehicle();
                      passenger.remove();
                    }));
          } else {
            if (!Version.isCurrentEqualOrHigher(Version.v1_21_R1)
                || !passenger.getType().equals(EntityType.TEXT_DISPLAY)) {
              passengers.add(passenger);
            }

            arrayList.add(
                CMIScheduler.runAtLocation(
                    this.plugin, passenger.getLocation(), passenger::leaveVehicle));
          }
        }
      }

      if (string != null && player.isInsideVehicle()) {
        passengers.add(player);
        player.leaveVehicle();
      }
    }

    CMIChatBubble cmiChatBubble =
        this.plugin.getChatBubbleManager().getBubble(player.getUniqueId());
    if (cmiChatBubble != null) {
      arrayList.add(cmiChatBubble.delete());
    }

    return CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]))
        .thenApply(value -> passengers);
  }

  public boolean isRiding(UUID uuid) {
    return this.riding.containsKey(uuid);
  }

  public HashMap<UUID, UUID> getRidingList() {
    return this.riding;
  }

  public boolean isBeingRiden(UUID uuid) {
    return this.beingRidden.containsKey(uuid);
  }

  public boolean isRidingNonSteerable(UUID uuid) {
    return this.ridingNonSteerable.containsKey(uuid);
  }

  public UUID getBeingRiden(UUID uuid) {
    return this.beingRidden.get(uuid);
  }

  @Deprecated
  public void addRiding(UUID uuid, UUID passengerUuid) {
    this.riding.put(uuid, passengerUuid);
    this.beingRidden.put(passengerUuid, uuid);
  }

  public void addRiding(Player player, Entity entity) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !(entity instanceof Steerable)) {
      this.ridingNonSteerable.put(player.getUniqueId(), player.getLocation());
      this.runRidingTasker();
    }

    this.riding.put(player.getUniqueId(), entity.getUniqueId());
    this.beingRidden.put(entity.getUniqueId(), player.getUniqueId());
  }

  private void runRidingTasker() {
    if (this.ridingTask == null) {
      this.ridingTask =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (this.ridingNonSteerable.isEmpty()) {
                  this.ridingTask.cancel();
                  this.ridingTask = null;
                } else {
                  for (var entry : this.ridingNonSteerable.entrySet()) {
                    Location location = (Location) entry.getValue();
                    Player player = CMIUser.getOnlinePlayer((UUID) entry.getKey());
                    if (player == null) {
                      this.removeRiding((UUID) entry.getKey());
                    } else {
                      Location playerLocation = player.getLocation();
                      if (!location.toVector().equals(playerLocation.toVector())) {
                        this.ridingNonSteerable.put((UUID) entry.getKey(), playerLocation);
                        PlayerMoveEvent playerMoveEvent =
                            new PlayerMoveEvent(player, location, playerLocation);
                        Bukkit.getServer().getPluginManager().callEvent(playerMoveEvent);
                      }
                    }
                  }
                }
              },
              5L,
              5L);
    }
  }

  public boolean isDoubleClick() {
    return this.DoubleClick;
  }

  public int getChairRange() {
    return this.chairRange;
  }

  public boolean isRemoveFromChairOnDamage() {
    return this.RemoveFromChairOnDamage;
  }

  public class Chair {
    private Entity ent = null;
    private Location ChairBlockLoc = null;
    private Location armorStandLoc = null;
    private long lastCheck = 0L;
    private boolean persistent = false;

    public Entity getEnt() {
      return this.ent;
    }

    public AnimationManager.Chair setEnt(Entity entity) {
      this.ent = entity;
      return this;
    }

    public Location getChairLoc() {
      return this.ChairBlockLoc;
    }

    public AnimationManager.Chair setChairLoc(Location location) {
      this.ChairBlockLoc = location;
      return this;
    }

    public Location getArmorStandLoc() {
      return this.armorStandLoc;
    }

    public AnimationManager.Chair setArmorStandLoc(Location location) {
      this.armorStandLoc = location;
      return this;
    }

    public long getLastCheck() {
      return this.lastCheck;
    }

    public void setLastCheck(long longValue) {
      this.lastCheck = longValue;
    }

    public boolean isPersistent() {
      return this.persistent;
    }

    public void setPersistent(boolean flag) {
      this.persistent = flag;
    }
  }

  public enum LeatherAnimationType {
    Rainbow(1),
    Health(2),
    Biome(3),
    Day(4);

    private Integer id;

    LeatherAnimationType(Integer integer) {
      this.id = integer;
    }

    public Integer getId() {
      return this.id;
    }

    public void setId(Integer integer) {
      this.id = integer;
    }

    public static AnimationManager.LeatherAnimationType getById(int value) {
      AnimationManager.LeatherAnimationType[] leatherAnimationTypes;
      int typeCount = (leatherAnimationTypes = values()).length;

      for (int typeIndex = 0; typeIndex < typeCount; typeIndex++) {
        AnimationManager.LeatherAnimationType leatherAnimationType =
            leatherAnimationTypes[typeIndex];
        if (leatherAnimationType.getId() == value) {
          return leatherAnimationType;
        }
      }

      return null;
    }

    public static AnimationManager.LeatherAnimationType getByName(String string) {
      AnimationManager.LeatherAnimationType[] leatherAnimationTypes;
      int value = (leatherAnimationTypes = values()).length;

      for (int index = 0; index < value; index++) {
        AnimationManager.LeatherAnimationType leatherAnimationType = leatherAnimationTypes[index];
        if (leatherAnimationType.name().equalsIgnoreCase(string)) {
          return leatherAnimationType;
        }
      }

      return null;
    }
  }
}
