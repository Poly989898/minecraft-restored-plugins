package com.Zrips.CMI.Modules.SpawnerCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerChargeManager {
  private LinkedHashMap<String, SCharges> map = new LinkedHashMap<>();
  private Integer total = 0;
  private HashMap<UUID, PlayerCharge> spawnerCharges = new HashMap<>();
  private boolean SpawnersSupportDisabled = false;
  private double SpawnersBreakBaseDropChance = 100.0;
  private long SpawnersStopDroppingAfter = 0L;
  private boolean SpawnersStopDroppingEnabled = true;
  private boolean SpawnersStopDroppingLinear = true;
  private boolean SpawnersXpDrop = true;
  private boolean SpawnersBreakEnabled = false;
  private boolean SpawnersCustomName = false;
  private boolean SpawnersIntoInventory = false;
  private boolean SpawnersPlaceEnabled = false;
  private boolean SpawnersPlaceRequiresPermission = false;
  private boolean SpawnersBreakRequiresExactPermission = false;
  private boolean SpawnersPlaceRequiresExactPermission = false;
  private boolean SpawnerEggInteractRequiresPermission = false;
  private boolean TrialSpawnerEggInteractRequiresPermission = false;
  private int SpawnersBreakSilkTouchLevel = 1;
  private boolean SpawnerDropFromTnt = false;
  private int SpawnerDropChanceTnt = 30;
  private boolean SpawnerDropFromCreeper = false;
  private int SpawnerDropChanceCreeper = 30;
  private boolean ChargesUse = false;
  private boolean ChargesBreakWithoutCharge = false;
  private boolean SpawnersProximityUse = false;
  private int SpawnersProximityRange = 0;

  public void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Spawners.FullDisable",
        new String[] {
          "If you experiencing issues with spawner handling, set this to true to avoid any spawner"
              + " manipulations from CMI side",
          "This will disable features like, spawner placement, spawner drops, spawner charges and"
              + " so on"
        });
    this.SpawnersSupportDisabled = configReader.get("Spawners.FullDisable", false);
    configReader.addComment(
        "Spawners.Break.Enabled",
        new String[] {
          "ATTENTION! cmi.dropspawner will be required to get spawner dropping for the player",
          "Enable or disable spawner handler for spawner break",
          "If enabled player will get spawner if using silktouch pickaxe and have cmi.dropspawner"
              + " permission node",
          "If player has cmi.dropspawner.nosilk permission node, player is not required to use silk"
              + " touch pickaxe to get droped spawner"
        });
    this.SpawnersBreakEnabled = configReader.get("Spawners.Break.Enabled", false);
    configReader.addComment(
        "Spawners.Break.CustomName",
        new String[] {"When enabled item stack will receive custom name based on spawners type"});
    this.SpawnersCustomName = configReader.get("Spawners.Break.CustomName", false);
    configReader.addComment(
        "Spawners.Break.IntoInventory",
        new String[] {
          "When enabled instead of dropping spawner on the ground, player will received this item"
              + " directly into their inventory",
          "In case inventory is full then item will be dropped on the ground"
        });
    this.SpawnersIntoInventory = configReader.get("Spawners.Break.IntoInventory", false);
    configReader.addComment(
        "Spawners.Break.RequiresExactPermission",
        new String[] {
          "If set to true, player will need to have particular permission node to break and get"
              + " particular spawner.",
          "In example: player should have cmi.dropspawner.pig to break pig spawner and get it"
              + " dropped, or cmi.dropspawner.zombie to get zombie spawner"
        });
    this.SpawnersBreakRequiresExactPermission =
        configReader.get("Spawners.Break.RequiresExactPermission", false);
    configReader.addComment(
        "Spawners.Break.DropExp",
        new String[] {
          "When set to false, exp will not be dropped from broken spawner independent if spawner it"
              + " self is being dropped",
          "When set to true exp will be dropped only if spawner is not"
        });
    this.SpawnersXpDrop = configReader.get("Spawners.Break.DropExp", false);
    configReader.addComment(
        "Spawners.Break.SilkTouchLevel",
        new String[] {"Minimal silktouch level required to get spawner back"});
    this.SpawnersBreakSilkTouchLevel = configReader.get("Spawners.Break.SilkTouchLevel", 1);
    configReader.addComment(
        "Spawners.Break.BaseDropChance",
        new String[] {
          "Number in percentage from 0 up to 100 for a spawner to be dropped when you break one"
        });
    this.SpawnersBreakBaseDropChance =
        CMINumber.clamp(configReader.get("Spawners.Break.BaseDropChance", 100.0), 0.0, 100.0);
    configReader.addComment(
        "Spawners.Break.StopDropping.Enabled",
        new String[] {
          "Enable or disable spawner dropping stopping after certain amount of spawners being mined"
        });
    this.SpawnersStopDroppingEnabled =
        configReader.get("Spawners.Break.StopDropping.Enabled", false);
    configReader.addComment(
        "Spawners.Break.StopDropping.After",
        new String[] {
          "Number of spawners player is able to actually mine before its drop chance is set to 0",
          "This is based on players statistics records of mined spawners in total, this INCLUDES"
              + " failed drops as well, so adjust acordingly",
          "Permission as "
              + PermissionsManager.CMIPerm.dropspawner_stopafter_$1.getCleanPermissionForShow()
              + " can be used to define limit with permission node instead"
        });
    this.SpawnersStopDroppingAfter =
        CMINumber.clamp(
            configReader.get("Spawners.Break.StopDropping.After", 0), 0L, Long.MAX_VALUE);
    configReader.addComment(
        "Spawners.Break.StopDropping.Linear",
        new String[] {
          "When enabled drop change will slowly drop down based on previous set value and how many"
              + " spawners player has mined",
          "This will not go lower than 1% of drop chance until previous set value is reached"
        });
    this.SpawnersStopDroppingLinear = configReader.get("Spawners.Break.StopDropping.Linear", false);
    configReader.addComment(
        "Spawners.Place.Enabled",
        new String[] {
          "Enable or disable spawner handler for spawner place",
          "If enabled player will place spawner depending from what it is by its type",
          "If disabled then spawner will be placed in normal way and it will allow other plugins to"
              + " handle its placement"
        });
    this.SpawnersPlaceEnabled = configReader.get("Spawners.Place.Enabled", true);
    configReader.addComment(
        "Spawners.Place.RequiresPermission",
        new String[] {
          "If set to true, player will need to have appropriate permission node to place spawner by"
              + " its type"
        });
    this.SpawnersPlaceRequiresPermission =
        configReader.get("Spawners.Place.RequiresPermission", false);
    configReader.addComment(
        "Spawners.Place.RequiresExactPermission",
        new String[] {
          "RequiresPermission should be set to true for this to work. If set to true, player will"
              + " need to have particular permission node to place particular spawner.",
          "In example: player should have cmi.placespawner.pig to place pig spawner, or"
              + " cmi.placespawner.zombie to place zombie spawner",
          "If set to false, then player will need to have basic cmi.placespawner permission to"
              + " place any type of spawner"
        });
    this.SpawnersPlaceRequiresExactPermission =
        configReader.get("Spawners.Place.RequiresExactPermission", false);
    configReader.addComment(
        "Spawners.Interact.Spawner.RequiresPermission",
        new String[] {
          "When set to true, players trying to change spawner with monster egg will require"
              + " appropriate permission node",
          "In example: player should have "
              + PermissionsManager.CMIPerm.egginteract_$1.getPermission("pig")
              + " to change spawner into pig, or "
              + PermissionsManager.CMIPerm.egginteract_$1.getPermission("zombie")
              + " to change into zombie spawner"
        });
    this.SpawnerEggInteractRequiresPermission =
        configReader.get(
            "Spawners.Interact.Spawner.RequiresPermission",
            configReader.getC().getBoolean("Spawners.Interact.EggRequiresPermission", false));
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      configReader.addComment(
          "Spawners.Interact.TrialSpawner.RequiresPermission",
          new String[] {
            "When set to true, players trying to change trial spawner with monster egg will require"
                + " appropriate permission node",
            "In example: player should have "
                + PermissionsManager.CMIPerm.trialegginteract_$1.getPermission("pig")
                + " to change spawner into pig, or "
                + PermissionsManager.CMIPerm.trialegginteract_$1.getPermission("zombie")
                + " to change into zombie spawner"
          });
      this.TrialSpawnerEggInteractRequiresPermission =
          configReader.get("Spawners.Interact.TrialSpawner.RequiresPermission", false);
    }

    configReader.addComment(
        "Spawners.TnTExplosionDrop",
        new String[] {
          "If set to true, spawners will have chance to be dropped when destroying with tnt"
        });
    this.SpawnerDropFromTnt = configReader.get("Spawners.TnTExplosionDrop.use", false);
    configReader.addComment(
        "Spawners.TnTExplosionDrop.Chance",
        new String[] {"Chance in percentage for spawner to drop"});
    this.SpawnerDropChanceTnt = configReader.get("Spawners.TnTExplosionDrop.Chance", 30);
    configReader.addComment(
        "Spawners.CreeperExplosionDrop",
        new String[] {
          "If set to true, spawners will have chance to be dropped when destroyed by creeper"
        });
    this.SpawnerDropFromCreeper = configReader.get("Spawners.CreeperExplosionDrop.use", false);
    this.SpawnerDropChanceCreeper = configReader.get("Spawners.CreeperExplosionDrop.Chance", 30);
    configReader.addComment(
        "Spawners.Charges.Use",
        new String[] {
          "When enabled players will be assigned to particular spawner charges group who have"
              + " cmi.spawners.charge.[groupName] permission node",
          "Players will be limited to how many spawners they can mine",
          "StartingCharge will determine how many charges they will have on first time joining"
              + " group",
          "MaxCharge will limit to how many charges you can have at one time",
          "Cooldown determines how often new charge will be given",
          "Bonus is optional and it will determine by how many seconds to lower cooldown for next"
              + " charge when placing spawner",
          "Option to bypass limitations with cmi.spawners.charge.bypass"
        });
    this.ChargesUse = configReader.get("Spawners.Charges.Use", false);
    configReader.addComment(
        "Spawners.Charges.BreakWithoutCharge",
        new String[] {
          "If set to true when player runs out of spawner charges spawner will be destroyed without"
              + " droping it"
        });
    this.ChargesBreakWithoutCharge = configReader.get("Spawners.Charges.BreakWithoutCharge", false);
    if (!configReader.getC().isConfigurationSection("Spawners.Charges.List")
        || configReader.getC().getConfigurationSection("Spawners.Charges").getKeys(false).size()
            < 2) {
      configReader.get("Spawners.Charges.List.Noob.Use", false);
      configReader.get("Spawners.Charges.List.Noob.StartingCharge", 2);
      configReader.get("Spawners.Charges.List.Noob.MaxCharge", 5);
      configReader.get("Spawners.Charges.List.Noob.Cooldown", 3600);
      configReader.get("Spawners.Charges.List.Noob.Bonus", 10);
      configReader.get("Spawners.Charges.List.Advanced.Use", false);
      configReader.get("Spawners.Charges.List.Advanced.StartingCharge", 3);
      configReader.get("Spawners.Charges.List.Advanced.MaxCharge", 6);
      configReader.get("Spawners.Charges.List.Advanced.Cooldown", 3000);
      configReader.get("Spawners.Charges.List.Advanced.Bonus", 10);
    }

    if (configReader.getC().isConfigurationSection("Spawners.Charges.List")) {
      for (String string :
          configReader.getC().getConfigurationSection("Spawners.Charges.List").getKeys(false)) {
        if (configReader.getC().isBoolean("Spawners.Charges.List." + string + ".Use")
            && configReader.getC().isInt("Spawners.Charges.List." + string + ".StartingCharge")
            && configReader.getC().isInt("Spawners.Charges.List." + string + ".MaxCharge")
            && configReader.getC().isInt("Spawners.Charges.List." + string + ".Cooldown")) {
          boolean flag =
              configReader.get(
                  "Spawners.Charges.List." + string + ".Use",
                  configReader.getC().getBoolean("Spawners.Charges.List." + string + ".Use"));
          int value =
              configReader.get(
                  "Spawners.Charges.List." + string + ".StartingCharge",
                  configReader
                      .getC()
                      .getInt("Spawners.Charges.List." + string + ".StartingCharge"));
          int count =
              configReader.get(
                  "Spawners.Charges.List." + string + ".MaxCharge",
                  configReader.getC().getInt("Spawners.Charges.List." + string + ".MaxCharge"));
          int countSecond =
              configReader.get(
                  "Spawners.Charges.List." + string + ".Cooldown",
                  configReader.getC().getInt("Spawners.Charges.List." + string + ".Cooldown"));
          int countThird = 0;
          if (configReader.getC().isInt("Spawners.Charges.List." + string + ".Bonus")) {
            countThird =
                configReader.get(
                    "Spawners.Charges.List." + string + ".Bonus",
                    configReader.getC().getInt("Spawners.Charges.List." + string + ".Bonus"));
          }

          if (flag) {
            SCharges sCharges = new SCharges(string, value, count, countSecond);
            sCharges.setBonus(countThird);
            if (this.ChargesUse) {
              CMI.getInstance().getSpawnerChargesManager().addCharge(sCharges);
            }
          }
        }
      }
    }

    configReader.addComment(
        "Spawners.Proximity.Use",
        new String[] {"Allows to limit how tight spawners can be placed from each other"});
    this.SpawnersProximityUse = configReader.get("Spawners.Proximity.Use", false);
    configReader.addComment(
        "Spawners.Proximity.Range",
        new String[] {
          "Radius in blocks from placed block. Max range is 16",
          "Can be bypassed with cmi.spawners.proximity.bypass"
        });
    this.SpawnersProximityRange =
        CMINumber.clamp(configReader.get("Spawners.Proximity.Range", 3), 0, 16);
    if (this.SpawnersProximityRange == 0) {
      this.SpawnersProximityUse = false;
    }
  }

  public PlayerCharge getPCharge(CMIUser cmiUser, boolean flag) {
    return this.spawnerCharges.computeIfAbsent(
        cmiUser.getUniqueId(), uuid -> new PlayerCharge(cmiUser, flag));
  }

  private void recalculateTotalSpawnerEntities() {
    EntityType[] entityTypes;
    int value = (entityTypes = EntityType.values()).length;

    for (int count = 0; count < value; count++) {
      EntityType entityType = entityTypes[count];
      if (entityType.name() != null && entityType.isSpawnable() && entityType.isAlive()) {
        CMIEntityType cmiEntityType = CMIEntityType.getByType(entityType);
        if (cmiEntityType != null
            && !cmiEntityType.equals(CMIEntityType.SHULKER)
            && !cmiEntityType.equals(CMIEntityType.ARMOR_STAND)
            && cmiEntityType.getType() != null) {
          this.total = this.total + 1;
        }
      }
    }
  }

  public void addCharge(SCharges sCharges) {
    this.map.put(sCharges.getName().toLowerCase(), sCharges);
  }

  public SCharges getCharge(String string) {
    return this.map.get(string.toLowerCase());
  }

  public SCharges getCharge(Player player) {
    SCharges sCharges = null;
    if (player == null) {
      return sCharges;
    }

    for (var entry : this.map.entrySet()) {
      if (PermissionsManager.CMIPerm.spawners_charge_$1.hasPermission(
          player, (String) entry.getKey())) {
        sCharges = (SCharges) entry.getValue();
      }
    }

    return sCharges;
  }

  public boolean changeSpawnerType(Block block, EntityType entityType) {
    BlockState blockState = block.getState();
    if (CMIMaterial.SPAWNER.getMaterial().equals(block.getType())) {
      CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;

      try {
        creatureSpawner.setSpawnedType(entityType);
      } catch (Exception exception) {
        return false;
      }

      creatureSpawner.update();
    } else if (CMIMaterial.TRIAL_SPAWNER.getMaterial().equals(block.getType())) {
      TrialSpawner trialSpawner = (TrialSpawner) blockState;
      trialSpawner.update();
    }

    return true;
  }

  @Deprecated
  public void openSpawnerPickerUI(Player player) {
    this.openSpawnerPickerUI(player, 1);
  }

  public void openSpawnerPickerUI(final Player player, int value) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int count) {
            SpawnerChargeManager.this.openSpawnerPickerUI(player, count);
          }
        };
    cmiGui.addLock(InvType.Gui);
    cmiGui.setTitle(CMILC.getIM("spawner", "pickOne"));
    if (this.total == 0) {
      this.recalculateTotalSpawnerEntities();
    }

    PageInfo pageInfo = new PageInfo(45, this.total, value);
    int countSecond = -1;
    var stream = Arrays.stream(EntityType.values()).map(Enum::name).sorted();
    Object[] objects;
    int countThird = (objects = stream.toArray()).length;

    for (int countFourth = 0; countFourth < countThird; countFourth++) {
      Object object = objects[countFourth];
      EntityType entityType = EntityType.valueOf((String) object);
      if (entityType.name() != null && entityType.isSpawnable() && entityType.isAlive()) {
        CMIEntityType cmiEntityType = CMIEntityType.getByType(entityType);
        if (cmiEntityType != null
            && !cmiEntityType.equals(CMIEntityType.SHULKER)
            && !cmiEntityType.equals(CMIEntityType.ARMOR_STAND)
            && cmiEntityType.getType() != null
            && pageInfo.isEntryOk()) {
          if (pageInfo.isBreak()) {
            break;
          }

          boolean flag = false;
          if (PermissionsManager.CMIPerm.command_spawner_$1.hasPermission(
              player, false, entityType.name().toLowerCase())) {
            flag = true;
          }

          countSecond++;
          CMIMaterial cmiMaterial = cmiEntityType.getSpawnEggMaterial();
          if (cmiMaterial == null) {
            cmiMaterial = CMIMaterial.PIG_SPAWN_EGG;
          }

          Material material = cmiMaterial.getMaterial();
          CMIItemStack cmiItemStack = new CMIItemStack(flag ? material : Material.STONE_BUTTON);
          if (cmiItemStack.getCMIType().isSpawnEgg()) {
            cmiItemStack.setEntityType(cmiEntityType);
          }

          cmiItemStack.setDisplayName("&r&f" + cmiEntityType.getTranslatedName());
          CMIGuiButton cmiGuiButton = new CMIGuiButton(countSecond, cmiItemStack.getItemStack());
          cmiGuiButton.addCommand("cmi spawner " + entityType.name());
          cmiGuiButton.addPermission("cmi.command.spawner." + entityType.name().toLowerCase());
          cmiGui.addButton(cmiGuiButton);
        }
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.autoResize();
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  @Deprecated
  public int getSpawnerDropChance(Player player, Block block) {
    return (int) this.getDropChance(player, block);
  }

  public double getDropChance(Player player, Block block) {
    if (!PermissionsManager.CMIPerm.dropspawner.hasPermission(player, 55L)) {
      return 0.0;
    }

    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser == null) {
      return 0.0;
    }

    ItemStack itemStack = cmiUser.getItemInHand();
    double number = this.SpawnersBreakBaseDropChance;
    PermissionInfo permissionInfo =
        CMI.getInstance()
            .getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.dropspawner_basedropchance_$1);
    if (permissionInfo != null) {
      number = permissionInfo.getMaxValue(number);
    }

    if (itemStack != null) {
      CMINBT cminbt = new CMINBT(itemStack);
      String string = cminbt.getString("PublicBukkitValues.slimefun:slimefun_item");
      if (string != null && string.equals("PICKAXE_OF_CONTAINMENT")) {
        return -1.0;
      }
    }

    try {
      if (block != null
          && this.SpawnersBreakRequiresExactPermission
          && !PermissionsManager.CMIPerm.dropspawner_$1.hasPermission(
              player, ((CreatureSpawner) block.getState()).getSpawnedType().name().toLowerCase())) {
        number = 0.0;
      }
    } catch (Exception exception) {
    }

    if (!CMI.getInstance()
            .getUtilManager()
            .hasSilkTouch(itemStack, this.SpawnersBreakSilkTouchLevel)
        && !PermissionsManager.CMIPerm.dropspawner_nosilk.hasPermission(player)) {
      number = 0.0;
    }

    if (this.SpawnersStopDroppingEnabled) {
      double amount = cmiUser.getStatistic(StatsManager.CMIStatistic.MINE_BLOCK, "spawner");
      double amountSecond = this.getSpawnersStopDroppingAfter(player);
      if (amountSecond > 0.0 && amount > amountSecond) {
        number = 0.0;
      } else if (this.SpawnersStopDroppingLinear) {
        double amountThird = amount / amountSecond;
        number = CMINumber.clamp(number - number * amountThird, 0.0, 100.0);
      }
    }

    return number;
  }

  public int getSpawnersStopDroppingAfter(Player player) {
    double number = this.SpawnersStopDroppingAfter;
    PermissionInfo permissionInfo =
        CMI.getInstance()
            .getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.dropspawner_stopafter_$1);
    if (permissionInfo != null) {
      number = permissionInfo.getMaxValue(number);
    }

    return (int) number;
  }

  public boolean isSpawnersSupportDisabled() {
    return this.SpawnersSupportDisabled;
  }

  public boolean isSpawnersXpDrop() {
    return this.SpawnersXpDrop;
  }

  public boolean isSpawnersEggInteractRequiresPermission() {
    return this.SpawnerEggInteractRequiresPermission;
  }

  public boolean isTrialSpawnersEggInteractRequiresPermission() {
    return this.TrialSpawnerEggInteractRequiresPermission;
  }

  public boolean isSpawnersPlaceRequiresExactPermission() {
    return this.SpawnersPlaceRequiresExactPermission;
  }

  public boolean isSpawnersPlaceRequiresPermission() {
    return this.SpawnersPlaceRequiresPermission;
  }

  public boolean isSpawnersPlaceEnabled() {
    return this.SpawnersPlaceEnabled;
  }

  public boolean isSpawnerDropFromCreeper() {
    return this.SpawnerDropFromCreeper;
  }

  public int getSpawnerDropChanceCreeper() {
    return this.SpawnerDropChanceCreeper;
  }

  public boolean isSpawnersBreakEnabled() {
    return this.SpawnersBreakEnabled;
  }

  public boolean isSpawnersIntoInventory() {
    return this.SpawnersIntoInventory;
  }

  public int getSpawnerDropChanceTnt() {
    return this.SpawnerDropChanceTnt;
  }

  public boolean isSpawnerDropFromTnt() {
    return this.SpawnerDropFromTnt;
  }

  public boolean isChargesUse() {
    return this.ChargesUse;
  }

  public boolean isChargesBreakWithoutCharge() {
    return this.ChargesBreakWithoutCharge;
  }

  public boolean isSpawnersProximityUse() {
    return this.SpawnersProximityUse;
  }

  public int getSpawnersProximityRange() {
    return this.SpawnersProximityRange;
  }

  public boolean isSpawnersCustomName() {
    return this.SpawnersCustomName;
  }
}
