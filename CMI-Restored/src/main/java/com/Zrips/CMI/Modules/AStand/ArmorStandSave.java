package com.Zrips.CMI.Modules.AStand;

import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.armorstand;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandSave {
  private Player player;
  private ArmorStand stand;
  private Set<ArmorStandManager.armorStandSaveOptions> save = new HashSet<>();
  private String name = "ArmorStand_" + new Random().nextInt();
  private String serialized = "";
  static String prefix = "{";
  static String suffix = "}";

  ArmorStandSave(
      Player targetPlayer,
      ArmorStand armorStand,
      Set<ArmorStandManager.armorStandSaveOptions> set) {
    this.player = targetPlayer;
    this.stand = armorStand;
    this.save = set;
  }

  ArmorStandSave(String string, String data) {
    this.name = string;
    this.serialized = data;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public ArmorStand getStand() {
    return this.stand;
  }

  public void setStand(ArmorStand armorStand) {
    this.stand = armorStand;
  }

  public Set<ArmorStandManager.armorStandSaveOptions> getSaveOptions() {
    return this.save;
  }

  private boolean valid(ItemStack itemStack) {
    return itemStack == null ? false : itemStack.getType() != Material.AIR;
  }

  public String serialize() {
    if (this.serialized != null && !this.serialized.isEmpty()) {
      return this.serialized;
    }

    if (this.stand == null) {
      return null;
    }

    StringBuilder stringBuilder = new StringBuilder();
    StringBuilder headBuilder = new StringBuilder();
    StringBuilder bodyBuilder = new StringBuilder();
    if (this.stand.getCustomName() != null && !this.stand.getCustomName().isEmpty()) {
      stringBuilder.append(";n" + prefix);
      stringBuilder.append(this.stand.getCustomName().replace("_", "__").replace(" ", "_"));
      stringBuilder.append(suffix);
    }

    for (ArmorStandManager.armorStandSaveOptions armorStandSaveOptions : this.getSaveOptions()) {
      switch (armorStandSaveOptions) {
        case helmet:
          if (this.valid(this.stand.getEquipment().getHelmet())) {
            stringBuilder.append(";helmet" + prefix);
            stringBuilder.append(CMIItemStack.serialize(this.stand.getEquipment().getHelmet()));
            stringBuilder.append(suffix);
          }
          break;
        case chest:
          if (this.valid(this.stand.getEquipment().getChestplate())) {
            stringBuilder.append(";chest" + prefix);
            stringBuilder.append(CMIItemStack.serialize(this.stand.getEquipment().getChestplate()));
            stringBuilder.append(suffix);
          }
          break;
        case offhand:
          if (this.valid(this.stand.getEquipment().getItemInOffHand())) {
            stringBuilder.append(";offhand" + prefix);
            stringBuilder.append(
                CMIItemStack.serialize(this.stand.getEquipment().getItemInOffHand()));
            stringBuilder.append(suffix);
          }
          break;
        case mainhand:
          if (this.valid(this.stand.getEquipment().getItemInMainHand())) {
            stringBuilder.append(";mainhand" + prefix);
            stringBuilder.append(
                CMIItemStack.serialize(this.stand.getEquipment().getItemInMainHand()));
            stringBuilder.append(suffix);
          }
          break;
        case leggings:
          if (this.valid(this.stand.getEquipment().getLeggings())) {
            stringBuilder.append(";leggings" + prefix);
            stringBuilder.append(CMIItemStack.serialize(this.stand.getEquipment().getLeggings()));
            stringBuilder.append(suffix);
          }
          break;
        case boots:
          if (this.valid(this.stand.getEquipment().getBoots())) {
            stringBuilder.append(";boots" + prefix);
            stringBuilder.append(CMIItemStack.serialize(this.stand.getEquipment().getBoots()));
            stringBuilder.append(suffix);
          }
          break;
        case name:
          if (this.stand.isCustomNameVisible()) {
            bodyBuilder.append(",name");
          }
          break;
        case body:
        case head:
        case torso:
        case leftArm:
        case rightArm:
        case leftLeg:
        case rightLeg:
          String string = getAngle(this.stand, armorStandSaveOptions);
          if (!string.isEmpty()) {
            headBuilder.append(";" + armorStandSaveOptions.toString() + "{" + string + "}");
          }
          break;
        case plate:
          if (!this.stand.hasBasePlate()) {
            bodyBuilder.append(",nobase");
          }
          break;
        case size:
          if (this.stand.isSmall()) {
            bodyBuilder.append(",small");
          }
          break;
        case visible:
          if (!this.stand.isVisible()) {
            bodyBuilder.append(",invisible");
          }
          break;
        case arms:
          if (this.stand.hasArms()) {
            bodyBuilder.append(",arms");
          }
          break;
        case gravity:
          if (!this.stand.hasGravity()) {
            bodyBuilder.append(",nogravity");
          }
          break;
        case glow:
          if (this.stand.isGlowing()) {
            bodyBuilder.append(",glowing");
          }
          break;
        case invulnerable:
          if (this.stand.isInvulnerable()) {
            bodyBuilder.append(",invulnerable");
          }
          break;
        case interactable:
          if (this.stand.isMarker()) {
            bodyBuilder.append(",noneinteractable");
          }
          break;
        case scale:
          if (!Version.isCurrentLower(Version.v1_20_R4)) {
            double number = CMIAttribute.SCALE.getBaseValue(this.stand);
            bodyBuilder.append(",scale:" + Double.toString(number).replace(",", "."));
          }
      }
    }

    String head = headBuilder.toString();
    if (head.startsWith(";")) {
      head = head.substring(1);
    }

    if (!head.isEmpty()) {
      head = ";" + head;
    }

    String body = bodyBuilder.toString();
    if (body.startsWith(",")) {
      body = body.substring(1);
    }

    if (!body.isEmpty()) {
      body = ";extra" + prefix + body + suffix;
    }

    String combined = stringBuilder.toString() + head + body;
    if (combined.startsWith(";")) {
      combined = combined.substring(1);
    }

    return combined;
  }

  private static String getAngle(
      ArmorStand armorStand, ArmorStandManager.armorStandSaveOptions armorStandSaveOptions) {
    EulerAngle eulerAngle = null;
    switch (armorStandSaveOptions) {
      case body:
        return String.valueOf(armorStand.getLocation().getYaw());
      case plate:
      case size:
      case visible:
      case arms:
      case gravity:
      case glow:
      case invulnerable:
      case interactable:
      case scale:
      default:
        break;
      case head:
        eulerAngle = armorStand.getHeadPose();
        break;
      case torso:
        eulerAngle = armorStand.getBodyPose();
        break;
      case leftArm:
        eulerAngle = armorStand.getLeftArmPose();
        break;
      case rightArm:
        eulerAngle = armorStand.getRightArmPose();
        break;
      case leftLeg:
        eulerAngle = armorStand.getLeftLegPose();
        break;
      case rightLeg:
        eulerAngle = armorStand.getRightLegPose();
    }

    if (eulerAngle != null) {
      int value = (int) Math.toDegrees(eulerAngle.getX());
      int yDegrees = (int) Math.toDegrees(eulerAngle.getY());
      int zDegrees = (int) Math.toDegrees(eulerAngle.getZ());
      return value == armorStandSaveOptions.getDefault().getBlockX()
              && yDegrees == armorStandSaveOptions.getDefault().getBlockY()
              && zDegrees == armorStandSaveOptions.getDefault().getBlockZ()
          ? ""
          : value + ":" + yDegrees + ":" + zDegrees;
    } else {
      return "";
    }
  }

  private static EulerAngle getAngle(
      ArmorStandManager.armorStandSaveOptions armorStandSaveOptions, String string) {
    try {
      if (!string.contains(":")) {
        return new EulerAngle(Double.parseDouble(string), 0.0, 0.0);
      } else {
        String[] strings = string.split(":");
        if (strings.length == 2 && strings[0].equalsIgnoreCase("r")) {
          double number = 5.0;
          int value = Integer.parseInt(strings[1]);
          Vector vector = armorStandSaveOptions.getMin();
          Vector max = armorStandSaveOptions.getMax();
          int sizeX = (int) Math.abs(vector.getX() - max.getX());
          int sizeY = (int) Math.abs(vector.getY() - max.getY());
          int sizeZ = (int) Math.abs(vector.getZ() - max.getZ());
          double ratioX = sizeX / number;
          double ratioY = sizeY / number;
          double ratioZ = sizeZ / number;
          sizeX = (int) (ratioX * value);
          sizeY = (int) (ratioY * value);
          sizeZ = (int) (ratioZ * value);
          double offsetX = (number - value) * (ratioX / 2.0);
          double offsetY = (number - value) * (ratioY / 2.0);
          double offsetZ = (number - value) * (ratioZ / 2.0);
          int randomZ = new Random().nextInt(sizeZ);
          double spawnX = vector.getX() + offsetX + new Random().nextInt(sizeX);
          double spawnZ = vector.getZ() + offsetZ + randomZ;
          return new EulerAngle(
              Math.toRadians(spawnX),
              Math.toRadians(vector.getY() + offsetY + new Random().nextInt(sizeY)),
              Math.toRadians(spawnZ));
        } else {
          return new EulerAngle(
              Math.toRadians(Double.parseDouble(strings[0])),
              Math.toRadians(Double.parseDouble(strings[1])),
              Math.toRadians(Double.parseDouble(strings[2])));
        }
      }
    } catch (Throwable throwable) {
      return null;
    }
  }

  public boolean apply(final ArmorStand armorStand) {
    armorStand.setArms(false);
    String[] strings = this.serialized.split(suffix + ";");

    for (int value = 0; value < strings.length; value++) {
      String string = strings[value];
      if (value == strings.length - 1) {
        string = string.substring(0, string.length() - 1);
      }

      String prefixless = string.split(Pattern.quote(prefix))[0];
      string = string.substring(prefixless.length() + 1, string.length());
      ArmorStandManager.armorStandSaveOptions armorStandSaveOptions =
          ArmorStandManager.armorStandSaveOptions.getByName(prefixless);
      if (armorStandSaveOptions == null) {
        if (prefixless.equalsIgnoreCase("extra")) {
          String[] parts;
          int partCount = (parts = string.split(",")).length;

          for (int partIndex = 0; partIndex < partCount; partIndex++) {
            String part = parts[partIndex];
            String partValue = part;
            switch (part.hashCode()) {
              case -1901805651:
                if (partValue.equals("invisible")) {
                  armorStand.setVisible(false);
                }
                break;
              case -1073976787:
                if (partValue.equals("invulnerable")) {
                  armorStand.setInvulnerable(true);
                }
                break;
              case -1040233454:
                if (partValue.equals("nobase")) {
                  armorStand.setBasePlate(false);
                }
                break;
              case -792553107:
                if (partValue.equals("nogravity")) {
                  armorStand.setGravity(false);
                }
                break;
              case 3002775:
                if (partValue.equals("arms")) {
                  armorStand.setArms(true);
                }
                break;
              case 3373707:
                if (partValue.equals("name")) {
                  armorStand.setCustomNameVisible(true);
                }
                break;
              case 109548807:
                if (partValue.equals("small")) {
                  armorStand.setSmall(true);
                }
                break;
              case 121707317:
                if (partValue.equals("glowing")) {
                  armorStand.setGlowing(true);
                }
                break;
              case 1496955848:
                if (partValue.equals("noneinteractable")) {
                  armorStand.setMarker(true);
                }
            }

            if (Version.isCurrentEqualOrHigher(Version.v1_20_R4) && part.startsWith("scale:")) {
              try {
                double number = Double.parseDouble(part.split(":")[1]);
                CMIAttribute.SCALE.setBaseValue(armorStand, number);
              } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
              }
            }
          }
        } else if (prefixless.equalsIgnoreCase("n")) {
          armorStand.setCustomName(CMIChatColor.translate(CMIText.replaceUnderScoreSpace(string)));
        }
      } else {
        switch (armorStandSaveOptions) {
          case helmet:
            CMIAsyncHead cmiAsyncHead =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack itemStack) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setHelmet(itemStack);
                    }
                  }
                };
            CMIItemStack cmiItemStack = CMIItemStack.deserialize(string, cmiAsyncHead);
            if (cmiItemStack != null
                && cmiItemStack.getItemStack() != null
                && !cmiAsyncHead.isAsyncHead()) {
              armorStand.getEquipment().setHelmet(cmiItemStack.getItemStack());
            }
            break;
          case chest:
            CMIAsyncHead itemTaskA =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack loadedItemA) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setChestplate(loadedItemA);
                    }
                  }
                };
            CMIItemStack itemA = CMIItemStack.deserialize(string, itemTaskA);
            if (itemA != null && itemA.getItemStack() != null && !itemTaskA.isAsyncHead()) {
              armorStand.getEquipment().setChestplate(itemA.getItemStack());
            }
            break;
          case offhand:
            CMIAsyncHead itemTaskB =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack loadedItemB) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setItemInOffHand(loadedItemB);
                    }
                  }
                };
            CMIItemStack itemB = CMIItemStack.deserialize(string, itemTaskB);
            if (itemB != null && itemB.getItemStack() != null && !itemTaskB.isAsyncHead()) {
              armorStand.getEquipment().setItemInOffHand(itemB.getItemStack());
            }
            break;
          case mainhand:
            CMIAsyncHead itemTaskC =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack loadedItemC) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setItemInHand(loadedItemC);
                    }
                  }
                };
            CMIItemStack itemC = CMIItemStack.deserialize(string, itemTaskC);
            if (itemC != null && itemC.getItemStack() != null && !itemTaskC.isAsyncHead()) {
              armorStand.getEquipment().setItemInHand(itemC.getItemStack());
            }
            break;
          case leggings:
            CMIAsyncHead itemTaskD =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack loadedItemD) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setLeggings(loadedItemD);
                    }
                  }
                };
            CMIItemStack itemD = CMIItemStack.deserialize(string, itemTaskD);
            if (itemD != null && itemD.getItemStack() != null && !itemTaskD.isAsyncHead()) {
              armorStand.getEquipment().setLeggings(itemD.getItemStack());
            }
            break;
          case boots:
            CMIAsyncHead itemTaskE =
                new CMIAsyncHead() {
                  public void afterAsyncUpdate(ItemStack loadedItemE) {
                    if (!armorStand.isDead()) {
                      armorStand.getEquipment().setBoots(loadedItemE);
                    }
                  }
                };
            CMIItemStack itemE = CMIItemStack.deserialize(string, itemTaskE);
            if (itemE != null && itemE.getItemStack() != null && !itemTaskE.isAsyncHead()) {
              armorStand.getEquipment().setBoots(itemE.getItemStack());
            }
          case name:
          case plate:
          case size:
          case visible:
          case arms:
          case gravity:
          case glow:
          case invulnerable:
          case interactable:
          case scale:
          default:
            break;
          case body:
            EulerAngle eulerAngle = getAngle(armorStandSaveOptions, string);
            if (eulerAngle != null) {
              Location location = armorStand.getLocation();
              location.setYaw((float) eulerAngle.getX());
              armorStand.teleport(location);
            }
            break;
          case head:
            EulerAngle angleA = getAngle(armorStandSaveOptions, string);
            if (angleA != null) {
              armorStand.setHeadPose(angleA);
            }
            break;
          case torso:
            EulerAngle angleB = getAngle(armorStandSaveOptions, string);
            if (angleB != null) {
              armorStand.setBodyPose(angleB);
            }
            break;
          case leftArm:
            EulerAngle angleC = getAngle(armorStandSaveOptions, string);
            if (angleC != null) {
              armorStand.setLeftArmPose(angleC);
            }
            break;
          case rightArm:
            EulerAngle angleD = getAngle(armorStandSaveOptions, string);
            if (angleD != null) {
              armorStand.setRightArmPose(angleD);
            }
            break;
          case leftLeg:
            EulerAngle angleE = getAngle(armorStandSaveOptions, string);
            if (angleE != null) {
              armorStand.setLeftLegPose(angleE);
            }
            break;
          case rightLeg:
            EulerAngle angleF = getAngle(armorStandSaveOptions, string);
            if (angleF != null) {
              armorStand.setRightLegPose(angleF);
            }
        }
      }
    }

    return true;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public String getSerialized() {
    return this.serialized;
  }

  public void setSerialized(String string) {
    this.serialized = string;
  }

  public ItemStack createItem() {
    CMIItemStack cmiItemStack = CMIMaterial.ARMOR_STAND.newCMIItemStack();
    cmiItemStack.setDisplayName(
        CMILC.getIM(armorstand.class, "armorStandItem", "[name]", this.getName()));
    CMINBT cminbt = new CMINBT(cmiItemStack.getItemStack());
    return (ItemStack)
        cminbt.setString("CMISavedArmorStand", CMIEncoder.encode(this.getName().toLowerCase()));
  }
}
