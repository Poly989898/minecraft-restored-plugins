package com.Zrips.CMI.Modules.AStand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.util.Vector;

public class ArmorStandManager {
  private CMI plugin;
  private static ConcurrentHashMap<UUID, CMIArmorStandEdit> editByPlayer =
      new ConcurrentHashMap<>();
  private LinkedHashMap<String, ArmorStandSave> savedArmorStands = new LinkedHashMap<>();
  public static final String savedArmorStandLabel = "CMISavedArmorStand";
  public static boolean ArmorStandsCheckBlockPlace = true;
  public static boolean TemplatesSpecificPermission = true;
  private static double ArmorStandsMinScale = 0.5;
  private static double ArmorStandsMaxScale = 3.0;

  public ArmorStandManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig(ConfigReader configReader) {
    configReader.addComment(
        "Optimizations.ArmorStands.CheckBlockPlace",
        new String[] {
          "When set to true, plugin will check if player can manipulate armor stand by sending fake"
              + " block place event and checking if any plugin would want to prevent this action",
          "This can help out to minimize posibility for players to manipulate armor stands when"
              + " they don't have build permission in area where armorstand is standing"
        });
    ArmorStandsCheckBlockPlace =
        configReader.get("Optimizations.ArmorStands.CheckBlockPlace", false);
    configReader.addComment(
        "Optimizations.ArmorStands.MinScale",
        new String[] {
          "Minimal armorstand scale which can be set in armor stand editor",
          "This is base min scale, can be overriden with "
              + PermissionsManager.CMIPerm.command_armorstand_scalemin_$1
                  .getCleanPermissionForShow()
              + " permission node",
          "Keep in mind that min value will be taken from this setting and permission node if both"
              + " are set"
        });
    ArmorStandsMinScale =
        CMINumber.clamp(configReader.get("Optimizations.ArmorStands.MinScale", 0.5), 0.0625, 100.0);
    configReader.addComment(
        "Optimizations.ArmorStands.MaxScale",
        new String[] {
          "Max armorstand scale which can be set in armor stand editor",
          "This is base max scale, can be overriden with "
              + PermissionsManager.CMIPerm.command_armorstand_scalemax_$1
                  .getCleanPermissionForShow()
              + " permission node",
          "Keep in mind that max value will be taken from this setting and permission node if both"
              + " are set"
        });
    ArmorStandsMaxScale =
        CMINumber.clamp(configReader.get("Optimizations.ArmorStands.MaxScale", 3.0), 0.0625, 100.0);
    configReader.addComment(
        "Optimizations.ArmorStands.Templates.SpecificPermission",
        new String[] {
          "When set to true, the player will be required to have "
              + PermissionsManager.CMIPerm.command_armorstand_template_$1.getPermission(
                  "[templateName]")
              + " permission node to deploy saved template"
        });
    TemplatesSpecificPermission =
        configReader.get("Optimizations.ArmorStands.Templates.SpecificPermission", false);
  }

  public Entity getLast(Player player) {
    CMIArmorStandEdit cmiArmorStandEdit = this.getEditor(player);
    if (cmiArmorStandEdit == null) {
      return null;
    }

    Entity entity = cmiArmorStandEdit.getArmorStand();
    if (entity == null) {
      return null;
    }

    Entity entitySecond =
        this.plugin
            .getUtilManager()
            .getClosestEntity(entity.getLocation(), 10, EntityType.ARMOR_STAND);
    return entitySecond != null
            && entitySecond.getUniqueId().toString().equals(entity.getUniqueId().toString())
        ? entitySecond
        : null;
  }

  public void removeEditor(Player player) {
    if (player != null) {
      CMIUser.closeInventory(player);
      this.disableWithDelay(player.getUniqueId());
    }
  }

  public CMIArmorStandEdit getEditor(Player player) {
    return player == null ? null : editByPlayer.get(player.getUniqueId());
  }

  public CMIArmorStandEdit addEditor(Player player, Entity entity) {
    CMIArmorStandEdit cmiArmorStandEdit = this.getEditor(player);
    if (cmiArmorStandEdit == null) {
      cmiArmorStandEdit = new CMIArmorStandEdit(player.getUniqueId(), entity);
    } else {
      cmiArmorStandEdit.setArmorStand(entity);
    }

    cmiArmorStandEdit.enableEditMode();
    cmiArmorStandEdit.setCopy(null);
    this.addEditor(cmiArmorStandEdit);
    return cmiArmorStandEdit;
  }

  public void addEditor(CMIArmorStandEdit cmiArmorStandEdit) {
    if (cmiArmorStandEdit != null) {
      editByPlayer.put(cmiArmorStandEdit.getPlayerUUID(), cmiArmorStandEdit);
    }
  }

  public void removeEditorWithDelay(UUID uuid) {
    CMIScheduler.runTaskLater(this.plugin, () -> editByPlayer.remove(uuid), 2L);
  }

  public void disableWithDelay(UUID uuid) {
    CMIScheduler.runTaskLater(this.plugin, () -> this.disable(uuid), 2L);
  }

  private void disable(UUID uuid) {
    CMIArmorStandEdit cmiArmorStandEdit = editByPlayer.get(uuid);
    if (cmiArmorStandEdit != null) {
      cmiArmorStandEdit.disableEditMode();
    }
  }

  public boolean isBeingEditedBy(UUID uuid, UUID uUIDSecond) {
    UUID uUIDThird = this.getEditorOf(uUIDSecond);
    return uUIDThird != null && uuid.equals(uUIDThird);
  }

  public boolean isBeingEdited(UUID uuid) {
    return this.getEditorOf(uuid) != null;
  }

  public UUID getEditorOf(UUID uuid) {
    for (CMIArmorStandEdit cmiArmorStandEdit : editByPlayer.values()) {
      if (cmiArmorStandEdit.isEditing()
          && cmiArmorStandEdit.getArmorStand() != null
          && cmiArmorStandEdit.getArmorStand().getUniqueId().equals(uuid)) {
        UUID uUIDSecond = cmiArmorStandEdit.getPlayerUUID();
        if (uUIDSecond != null) {
          Player player = CMIUser.getOnlinePlayer(uUIDSecond);
          if (player == null) {
            this.disable(uUIDSecond);
            return uUIDSecond;
          }

          CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
          if (cmiGui == null && cmiArmorStandEdit.getCopy() == null) {
            this.disable(uUIDSecond);
            return uUIDSecond;
          }

          return uUIDSecond;
        }
      }
    }

    return null;
  }

  public boolean isTooFar(Player player, Entity entity) {
    Location location = player.getLocation();
    Location locationSecond = entity.getLocation();
    return !location.getWorld().equals(locationSecond.getWorld())
        ? true
        : location.distance(locationSecond) > 20.0;
  }

  @Deprecated
  public static void duplicate(
      ArmorStand armorStand,
      ArmorStand armorStandSecond,
      Set<ArmorStandManager.armorStandActions> set) {
    CMIArmorStandEditor.duplicate(armorStand, armorStandSecond, set);
  }

  @Deprecated
  public static void duplicateExtra(
      ArmorStand armorStand,
      ArmorStand armorStandSecond,
      Set<ArmorStandManager.armorStandExtraActions> set) {
    CMIArmorStandEditor.duplicateExtra(armorStand, armorStandSecond, set);
  }

  @Deprecated
  public boolean openEditor(Player player, Entity entity, boolean flag) {
    return CMIArmorStandEditor.openEditor(player, entity, flag);
  }

  @Deprecated
  public boolean openSaveWindow(Player player, Entity entity) {
    return CMIArmorStandEditor.openSaveWindow(player, entity);
  }

  @Deprecated
  public boolean openPositionEditor(Player player, Entity entity) {
    return CMIArmorStandEditor.openPositionEditor(player, entity);
  }

  @Deprecated
  public boolean isOk(CMIGui cmiGui) {
    return CMIArmorStandEditor.isOk(cmiGui);
  }

  public boolean canInteract(Player player, Entity entity, boolean flag) {
    PlayerAnimationEvent playerAnimationEvent = new PlayerAnimationEvent(player);
    Bukkit.getPluginManager().callEvent(playerAnimationEvent);
    if (flag
        && Version.isCurrentEqualOrHigher(Version.v1_8_R1)
        && !ArmorStandListener1_8.checkInteract(player, entity)) {
      return false;
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)
          && !ArmorStandListener1_8.checkManipulation(player, entity)) {
        return false;
      }
    } catch (Exception exception) {
    }

    if (ArmorStandsCheckBlockPlace) {
      try {
        BlockPlaceEvent blockPlaceEvent =
            CMIEvent.placeBlockEvent(entity.getLocation().getBlock(), player);
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) {
          return false;
        }

        blockPlaceEvent.setCancelled(true);
      } catch (Exception exception2) {
      }
    }

    return true;
  }

  @Deprecated
  public void changeAngle(
      CMIGui cmiGui,
      ArmorStandManager.armorStandActions armorStandActionsValue,
      ArmorStandManager.armorStandPoseC armorStandPoseCValue,
      int value) {
    CMIArmorStandEditor.changeAngle(cmiGui, armorStandActionsValue, armorStandPoseCValue, value);
  }

  @Deprecated
  public void changeAngle(
      ArmorStand armorStand,
      Player player,
      ArmorStandManager.armorStandActions armorStandActionsValue,
      ArmorStandManager.armorStandPoseC armorStandPoseCValue,
      int value) {
    CMIArmorStandEditor.changeAngle(
        armorStand, player, armorStandActionsValue, armorStandPoseCValue, value);
  }

  @Deprecated
  public void switchArmorStandArms(CMIGui cmiGui) {
    CMIArmorStandEditor.switchArmorStandArms(cmiGui);
  }

  @Deprecated
  public void updateArmorStandItems(CMIGui cmiGui) {
    CMIArmorStandEditor.updateArmorStandItems(cmiGui);
  }

  public ArmorStandSave getSavedArmorStand(String string) {
    return string == null ? null : this.savedArmorStands.get(string.toLowerCase());
  }

  public void save() {
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Saves" + File.separator + "armorStands.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      for (var entry : this.savedArmorStands.entrySet()) {
        configReader.get(
            ((ArmorStandSave) entry.getValue()).getName(),
            ((ArmorStandSave) entry.getValue()).serialize());
      }

      configReader.save();
    }
  }

  public void load() {
    Long time = System.currentTimeMillis();
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Saves" + File.separator + "armorStands.yml");
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    if (configReader != null) {
      var set = configReader.getC().getKeys(false);
      this.savedArmorStands.clear();

      for (String string : set) {
        String text = configReader.get(string, "");
        if (!text.isEmpty()) {
          this.savedArmorStands.put(string.toLowerCase(), new ArmorStandSave(string, text));
        }
      }

      this.plugin.loadMessage(
          this.savedArmorStands.size(), "ArmorStand templates", System.currentTimeMillis() - time);
    }
  }

  public HashMap<String, ArmorStandSave> getSavedArmorStands() {
    return this.savedArmorStands;
  }

  public void removeSavedArmorStand(String string) {
    this.savedArmorStands.remove(string.toLowerCase());
  }

  public void addSavedArmorStand(ArmorStandSave armorStandSave) {
    this.savedArmorStands.put(armorStandSave.getName().toLowerCase(), armorStandSave);
  }

  public static double getArmorStandsMinScale() {
    return ArmorStandsMinScale;
  }

  public static double getArmorStandsMaxScale() {
    return ArmorStandsMaxScale;
  }

  public enum armorStandActions {
    head,
    body,
    torso,
    leftArm,
    rightArm,
    leftLeg,
    rightLeg,
    pos;

    public static ArmorStandManager.armorStandActions getByName(String string) {
      ArmorStandManager.armorStandActions[] armorStandActionses;
      int value = (armorStandActionses = values()).length;

      for (int count = 0; count < value; count++) {
        ArmorStandManager.armorStandActions armorStandActionsValue = armorStandActionses[count];
        if (armorStandActionsValue.name().equalsIgnoreCase(string)) {
          return armorStandActionsValue;
        }
      }

      return null;
    }
  }

  public enum armorStandEditorSlots {
    helmet(11),
    chest(20),
    offhand(21),
    mainhand(19),
    leggings(29),
    boots(38);

    private int slot;

    armorStandEditorSlots(int value) {
      this.slot = value;
    }

    public int getSlot() {
      return this.slot;
    }

    public static ArmorStandManager.armorStandEditorSlots getBySlot(int value) {
      ArmorStandManager.armorStandEditorSlots[] armorStandEditorSlotses;
      int count = (armorStandEditorSlotses = values()).length;

      for (int countSecond = 0; countSecond < count; countSecond++) {
        ArmorStandManager.armorStandEditorSlots armorStandEditorSlotsValue =
            armorStandEditorSlotses[countSecond];
        if (armorStandEditorSlotsValue.getSlot() == value) {
          return armorStandEditorSlotsValue;
        }
      }

      return null;
    }
  }

  public enum armorStandExtraActions {
    updateitems,
    plate,
    size,
    visible,
    arms,
    gravity,
    glow,
    invulnerable,
    name,
    interactable,
    scale;

    public static ArmorStandManager.armorStandExtraActions getByName(String string) {
      ArmorStandManager.armorStandExtraActions[] armorStandExtraActionses;
      int value = (armorStandExtraActionses = values()).length;

      for (int count = 0; count < value; count++) {
        ArmorStandManager.armorStandExtraActions armorStandExtraActionsValue =
            armorStandExtraActionses[count];
        if (armorStandExtraActionsValue.name().equalsIgnoreCase(string)) {
          return armorStandExtraActionsValue;
        }
      }

      return null;
    }
  }

  public enum armorStandPoseC {
    x,
    y,
    z;
  }

  public enum armorStandSaveOptions {
    helmet,
    chest,
    offhand,
    mainhand,
    leggings,
    boots,
    name,
    body,
    plate,
    size,
    visible,
    arms,
    gravity,
    glow,
    invulnerable,
    interactable,
    scale,
    head(new Vector(-30, -50, -30), new Vector(30, 50, 30)),
    torso(new Vector(-5, -20, -5), new Vector(5, 20, 5)),
    leftArm(new Vector(-160, -35, -150), new Vector(20, 40, 5), new Vector(-10.0, 0.0, -10.0)),
    rightArm(new Vector(-160, -40, -5), new Vector(20, 35, 150), new Vector(-14.99, 0.0, 10.0)),
    leftLeg(new Vector(-85, -10, -35), new Vector(25, 10, 5), new Vector(-1.0, 0.0, -1.0)),
    rightLeg(new Vector(-85, -10, -5), new Vector(25, 10, 35), new Vector(1.0, 0.0, 1.0));

    private Vector defaults = new Vector(0.0, 0.0, 0.0);
    private Vector min = new Vector(0, 0, 0);
    private Vector max = new Vector(0, 0, 0);

    armorStandSaveOptions() {}

    armorStandSaveOptions(Vector vector, Vector vectorSecond, Vector vectorThird) {
      this.min = vector;
      this.max = vectorSecond;
      this.defaults = vectorThird;
    }

    armorStandSaveOptions(Vector vector, Vector vectorSecond) {
      this.min = vector;
      this.max = vectorSecond;
    }

    public static ArmorStandManager.armorStandSaveOptions getByName(String string) {
      ArmorStandManager.armorStandSaveOptions[] armorStandSaveOptionses;
      int value = (armorStandSaveOptionses = values()).length;

      for (int count = 0; count < value; count++) {
        ArmorStandManager.armorStandSaveOptions armorStandSaveOptionsValue =
            armorStandSaveOptionses[count];
        if (armorStandSaveOptionsValue.name().equalsIgnoreCase(string)) {
          return armorStandSaveOptionsValue;
        }
      }

      return null;
    }

    public Vector getDefault() {
      return this.defaults;
    }

    public Vector getMin() {
      return this.min;
    }

    public Vector getMax() {
      return this.max;
    }
  }
}
