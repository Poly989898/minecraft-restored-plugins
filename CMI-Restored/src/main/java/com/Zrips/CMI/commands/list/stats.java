package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class stats implements Cmd {
  HashMap<String, Long> most = new HashMap<>();
  static NumberFormat formatter = new DecimalFormat("#0.00");

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("distanceCM", "{gcs}[value]cm");
    configReader.get("distanceM", "{gcs}[value]m");
    configReader.get("distanceKm", "{gcs}[value]km");
    configReader.get("list", "{gcs}[name]:{gcs}[amount]");
    configReader.get("listCover", "{gcp}({gcs}[list]{gcp})");
    configReader.get("Quit", "{gcp}Games Quit: {gcs}[value]");
    configReader.get("PlayTime", "{gcp}Time Played: {gcs}[value]");
    configReader.get("SneakTime", "{gcp}Sneak time: {gcs}[value]");
    configReader.get("Sleep", "{gcp}Sleep in bed: {gcs}[value]");
    configReader.get("Joined", "{gcp}Joined on: {gcs}[time]");
    configReader.get("Jumps", "{gcp}Jumps: {gcs}[value]");
    configReader.get("SinceDeath", "{gcp}Since Last Death: {gcs}[value]");
    configReader.get("Deaths", "{gcp}Number of Deaths: {gcs}[value]");
    configReader.get("DeathsByMonsters", "{gcp}Killed by monsters: {gcs}[value]");
    configReader.get("TotalDistance", "{gcp}Total distance traveled: {gcs}[value]");
    configReader.get("Walk", "{gcp}Walked: {gcs}[value]");
    configReader.get("Crouch", "{gcp}Crouched: {gcs}[value]");
    configReader.get("Sprint", "{gcp}Sprinted: {gcs}[value]");
    configReader.get("Swim", "{gcp}Swum: {gcs}[value]");
    configReader.get("Fall", "{gcp}Fallen: {gcs}[value]");
    configReader.get("Climb", "{gcp}Climbed: {gcs}[value]");
    configReader.get("Fly", "{gcp}Flown: {gcs}[value]");
    configReader.get("Dive", "{gcp}Dove: {gcs}[value]");
    configReader.get("Minecart", "{gcp}Minecart: {gcs}[value]");
    configReader.get("Boat", "{gcp}Boat: {gcs}[value]");
    configReader.get("Pig", "{gcp}Pig: {gcs}[value]");
    configReader.get("Horse", "{gcp}Horse: {gcs}[value]");
    configReader.get("Elytra", "{gcp}Elytra: {gcs}[value]");
    configReader.get("Damage", "{gcp}Damage {gcs}[dealt] [taken]");
    configReader.get("DamageDealt", "{gcp}Dealt: {gcs}[value]");
    configReader.get("DamageTaken", "{gcp}Taken: {gcs}[value]");
    configReader.get("JUMP", "{gcp}Jumps: {gcs}[value]");
    configReader.get("Kills", "{gcp}Kills {gcs}");
    configReader.get("KillsMob", "{gcp}Mob: {gcs}[value]");
    configReader.get("KillsPlayer", " {gcp}Player: {gcs}[value]");
    configReader.get("Blocks", "{gcp}Blocks {gcs}");
    configReader.get("BlocksMined", "{gcp}Mined: {gcs}[value]");
    configReader.get("BlocksPlaced", " {gcp}Placed: {gcs}[value]");
    configReader.get("Items", "{gcp}Items {gcs}");
    configReader.get("ItemsUsed", "{gcp}Use:{gcs}[value]");
    configReader.get("ItemsDrop", "{gcp}Drop:{gcs}[value]");
    configReader.get("ItemsPicked", "{gcp}Pick:{gcs}[value]");
    configReader.get("ItemsBroken", "{gcp}Broke:{gcs}[value]");
    configReader.get("ItemsCrafted", " {gcp}Craft:{gcs}[value]");
    configReader.get("ItemsEnchanted", " {gcp}Enchant:{gcs}[value]");
    configReader.get("Opened", "{gcp}Opened {gcs}[list]");
    configReader.get("OpenChest", "{gcp}Chest:{gcs}[value]");
    configReader.get("OpenEnder", "{gcp}EnderChest:{gcs}[value]");
    configReader.get("OpenShulker", "{gcp}ShulkerBox:{gcs}[value]");
    configReader.get("Interacted", "{gcp}Interacted {gcs}[list]");
    configReader.get("InteractBeacon", "{gcp}Beacon:{gcs}[value]");
    configReader.get("InteractBrewingStand", "{gcp}BrewStand:{gcs}[value]");
    configReader.get("InteractCraftTable", "{gcp}CraftTable:{gcs}[value]");
    configReader.get("InteractFurnace", "{gcp}Furnace:{gcs}[value]");
    configReader.get("Inspected", "{gcp}Inspected {gcs}[list]");
    configReader.get("InspectDispenser", "{gcp}Dispenser:{gcs}[value]");
    configReader.get("InspectDropper", "{gcp}Dropper:{gcs}[value]");
    configReader.get("InspectHopper", "{gcp}Hopper:{gcs}[value]");
    for (StatsManager.CMIStatistic cmiStatistic : StatsManager.CMIStatistic.values()) {
      configReader.get("Statistics." + cmiStatistic.name(), cmiStatistic.getName());
    }
  }

  @Override
  @CAnnotation(
      info = "Check players stats",
      args = "(playerName)",
      tab = {"playername"},
      regVar = {0, 1},
      consoleVar = {1},
      alias = true,
      others = true)
  public Boolean perform(CMI cmi, CommandSender sender, String[] args) {
    String playerName = null;
    if (args.length > 0) {
      playerName = args[0];
    }
    CMIUser user = cmi.getUser(sender, playerName, this);
    if (user == null || user.getPlayer() == null) {
      return null;
    }
    Player player = user.getPlayer();

    LC.info_Spliter.sendMessage((Object) sender, new Object[0]);
    for (StatsManager.CMIStatistic cmiStatistic : StatsManager.CMIStatistic.values()) {
      Statistic statistic = cmi.getStatsManager().getStatistic(cmiStatistic);
      if (statistic == null) {
        continue;
      }
      RawMessage rawMessage = new RawMessage();
      String timeText = "";
      String secondaryText = "";

      switch (statistic.name()) {
        case "PLAY_ONE_TICK":
        case "PLAY_ONE_MINUTE":
          {
            String playtimeText = CMITimeManager.to24hourShort(user.getTotalPlayTime(false));
            timeText = CMILC.getIM(this, "PlayTime", "[value]", playtimeText);

            long quitCount = player.getStatistic(Statistic.LEAVE_GAME);
            secondaryText = CMILC.getIM(this, "Quit", "[value]", quitCount);
            secondaryText =
                secondaryText
                    + " \n"
                    + CMILC.getIM(
                        this, "Joined", "[time]", DateFormat.MiliToDate(player.getFirstPlayed()));

            try {
              long sneakTicks = player.getStatistic(Statistic.SNEAK_TIME);
              String sneakText = CMITimeManager.to24hourShort(sneakTicks * 50L);
              secondaryText =
                  secondaryText + " \n" + CMILC.getIM(this, "SneakTime", "[value]", sneakText);
            } catch (NoSuchFieldError ignored) {
            }
            try {
              long sleepCount = player.getStatistic(Statistic.SLEEP_IN_BED);
              if (sleepCount != 0L) {
                secondaryText =
                    secondaryText + " \n" + CMILC.getIM(this, "Sleep", "[value]", sleepCount);
              }
            } catch (NoSuchFieldError ignored) {
            }
            long jumpCount = player.getStatistic(Statistic.JUMP);
            if (jumpCount != 0L) {
              secondaryText =
                  secondaryText + " \n" + CMILC.getIM(this, "Jumps", "[value]", jumpCount);
            }

            rawMessage.add(timeText, secondaryText);
            rawMessage.show(sender);
            break;
          }
        default:
          {
            // Statistics without output (DAMAGE_TAKEN, ANIMALS_BRED, ...) fall through to nothing.
            switch (statistic) {
              case TIME_SINCE_DEATH:
                {
                  long timeSinceDeath = player.getStatistic(Statistic.TIME_SINCE_DEATH);
                  String sinceDeathText = CMITimeManager.to24hourShort(timeSinceDeath * 50L);
                  timeText = CMILC.getIM(this, "SinceDeath", "[value]", sinceDeathText);

                  long deaths = player.getStatistic(Statistic.DEATHS);
                  if (deaths != 0L) {
                    secondaryText = CMILC.getIM(this, "Deaths", "[value]", deaths);
                  }
                  long killedByMonsters = getTotal(Statistic.ENTITY_KILLED_BY, player);
                  if (killedByMonsters != 0L) {
                    secondaryText =
                        secondaryText
                            + " \n"
                            + CMILC.getIM(this, "DeathsByMonsters", "[value]", killedByMonsters);
                    secondaryText = secondaryText + " \n" + getList();
                  }

                  rawMessage.add(timeText, secondaryText);
                  rawMessage.show(sender);
                  break;
                }
              case WALK_ONE_CM:
                {
                  long totalDistance = user.getStatistic(StatsManager.CMIStatistic.TRAVEL);
                  try {
                    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                      totalDistance +=
                          player.getStatistic(Statistic.valueOf("WALK_ON_WATER_ONE_CM"));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  timeText =
                      CMILC.getIM(this, "TotalDistance", "[value]", convertDistance(totalDistance));

                  try {
                    long walkDistance = player.getStatistic(Statistic.WALK_ONE_CM);
                    secondaryText = "";
                    if (walkDistance != 0L) {
                      secondaryText =
                          CMILC.getIM(this, "Walk", "[value]", convertDistance(walkDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long sprintDistance = player.getStatistic(Statistic.SPRINT_ONE_CM);
                    if (sprintDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Sprint", "[value]", convertDistance(sprintDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long swimDistance = player.getStatistic(Statistic.SWIM_ONE_CM);
                    if (swimDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Swim", "[value]", convertDistance(swimDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long fallDistance = player.getStatistic(Statistic.FALL_ONE_CM);
                    if (fallDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Fall", "[value]", convertDistance(fallDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long crouchDistance = player.getStatistic(Statistic.CROUCH_ONE_CM);
                    if (crouchDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Crouch", "[value]", convertDistance(crouchDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long climbDistance = player.getStatistic(Statistic.CLIMB_ONE_CM);
                    if (climbDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Climb", "[value]", convertDistance(climbDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long diveDistance =
                        Version.isCurrentEqualOrHigher(Version.v1_13_R1)
                            ? player.getStatistic(Statistic.valueOf("WALK_UNDER_WATER_ONE_CM"))
                            : player.getStatistic(Statistic.valueOf("DIVE_ONE_CM"));
                    if (diveDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Dive", "[value]", convertDistance(diveDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long boatDistance = player.getStatistic(Statistic.BOAT_ONE_CM);
                    if (boatDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Boat", "[value]", convertDistance(boatDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long flyDistance = player.getStatistic(Statistic.FLY_ONE_CM);
                    if (flyDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Fly", "[value]", convertDistance(flyDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long horseDistance = player.getStatistic(Statistic.HORSE_ONE_CM);
                    if (horseDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Horse", "[value]", convertDistance(horseDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long minecartDistance = player.getStatistic(Statistic.MINECART_ONE_CM);
                    if (minecartDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Minecart", "[value]", convertDistance(minecartDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long pigDistance = player.getStatistic(Statistic.PIG_ONE_CM);
                    if (pigDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(this, "Pig", "[value]", convertDistance(pigDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  try {
                    long elytraDistance = player.getStatistic(Statistic.AVIATE_ONE_CM);
                    if (elytraDistance != 0L) {
                      secondaryText =
                          secondaryText
                              + " \n"
                              + CMILC.getIM(
                                  this, "Elytra", "[value]", convertDistance(elytraDistance));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }

                  rawMessage.add(timeText, secondaryText);
                  rawMessage.show(sender);
                  break;
                }
              case DAMAGE_DEALT:
                {
                  long dealt = player.getStatistic(Statistic.DAMAGE_DEALT);
                  String dealtText =
                      CMILC.getIM(
                          this, "DamageDealt", "[value]", formatter.format((double) dealt / 10.0));
                  long taken = player.getStatistic(Statistic.DAMAGE_TAKEN);
                  String takenText =
                      CMILC.getIM(
                          this, "DamageTaken", "[value]", formatter.format((double) taken / 10.0));

                  timeText =
                      CMILC.getIM(this, "Damage", "[dealt]", dealtText, "[taken]", takenText);
                  rawMessage.addText(timeText);
                  rawMessage.show(sender);
                  break;
                }
              case MOB_KILLS:
                {
                  timeText = CMILC.getIM(this, "Kills");
                  rawMessage.addText(timeText);

                  long mobKills = getTotal(Statistic.MOB_KILLS, player);
                  String mobKillsText = CMILC.getIM(this, "KillsMob", "[value]", mobKills);
                  // Fills 'most' with entity kills; the hover list below is built from it.
                  getTotal(Statistic.KILL_ENTITY, player);
                  rawMessage.add(mobKillsText, getList(5));

                  long playerKills = getTotal(Statistic.PLAYER_KILLS, player);
                  String playerKillsText = CMILC.getIM(this, "KillsPlayer", "[value]", playerKills);
                  rawMessage.addText(playerKillsText);
                  rawMessage.show(sender);
                  break;
                }
              case MINE_BLOCK:
                {
                  timeText = CMILC.getIM(this, "Blocks");
                  rawMessage.addText(timeText);

                  long mined = getTotal(Statistic.MINE_BLOCK, player, Statistic.Type.BLOCK);
                  String minedText = CMILC.getIM(this, "BlocksMined", "[value]", mined);
                  rawMessage.addText(minedText).addHover(getList(5));

                  long placed = getTotal(Statistic.USE_ITEM, player, Statistic.Type.BLOCK);
                  String placedText = CMILC.getIM(this, "BlocksPlaced", "[value]", placed);
                  rawMessage.addText(placedText).addHover(getList(6));
                  rawMessage.show(sender);
                  break;
                }
              case DROP:
                {
                  timeText = CMILC.getIM(this, "Items");
                  rawMessage.add(timeText);

                  long used = getTotal(Statistic.USE_ITEM, player, Statistic.Type.ITEM);
                  String usedText = CMILC.getIM(this, "ItemsUsed", "[value]", used) + " ";
                  if (used != 0L) {
                    rawMessage.add(usedText, getList(3));
                  }

                  long dropped = getTotal(Statistic.DROP, player);
                  String droppedText = CMILC.getIM(this, "ItemsDrop", "[value]", dropped) + " ";
                  if (dropped != 0L) {
                    rawMessage.add(droppedText, getList(3));
                  }

                  try {
                    long picked = getTotal(Statistic.PICKUP, player);
                    String pickedText = CMILC.getIM(this, "ItemsPicked", "[value]", picked) + " ";
                    if (picked != 0L) {
                      rawMessage.add(pickedText, getList(3));
                    }
                  } catch (NoSuchFieldError ignored) {
                  }

                  long broken = getTotal(Statistic.BREAK_ITEM, player);
                  String brokenText = CMILC.getIM(this, "ItemsBroken", "[value]", broken) + " ";
                  if (broken != 0L) {
                    rawMessage.add(brokenText, getList(3));
                  }

                  long crafted = getTotal(Statistic.CRAFT_ITEM, player);
                  String craftedText = CMILC.getIM(this, "ItemsCrafted", "[value]", crafted) + " ";
                  if (crafted != 0L) {
                    rawMessage.add(craftedText, getList(3));
                  }

                  long enchanted = getTotal(Statistic.ITEM_ENCHANTED, player);
                  String enchantedText =
                      CMILC.getIM(this, "ItemsEnchanted", "[value]", enchanted) + " ";
                  if (enchanted != 0L) {
                    rawMessage.add(enchantedText, getList(3));
                  }
                  rawMessage.show(sender);
                  break;
                }
              case CHEST_OPENED:
                {
                  long chests = player.getStatistic(Statistic.CHEST_OPENED);
                  if (chests != 0L) {
                    timeText = timeText + CMILC.getIM(this, "OpenChest", "[value]", chests) + " ";
                  }
                  long enderChests = getTotal(Statistic.ENDERCHEST_OPENED, player);
                  if (enderChests != 0L) {
                    timeText =
                        timeText + CMILC.getIM(this, "OpenEnder", "[value]", enderChests) + " ";
                  }
                  try {
                    long shulkers = getTotal(Statistic.SHULKER_BOX_OPENED, player);
                    if (shulkers != 0L) {
                      timeText =
                          timeText + CMILC.getIM(this, "OpenShulker", "[value]", shulkers) + " ";
                    }
                  } catch (NoSuchFieldError ignored) {
                  }
                  if (timeText.isEmpty()) {
                    break;
                  }
                  timeText = CMILC.getIM(this, "Opened", "[list]", timeText);
                  CMILC.sendMessage(sender, timeText);
                  break;
                }
              case BEACON_INTERACTION:
                {
                  long beacons = player.getStatistic(Statistic.BEACON_INTERACTION);
                  if (beacons != 0L) {
                    timeText =
                        timeText + CMILC.getIM(this, "InteractBeacon", "[value]", beacons) + " ";
                  }
                  long brewingStands = getTotal(Statistic.BREWINGSTAND_INTERACTION, player);
                  if (brewingStands != 0L) {
                    timeText =
                        timeText
                            + CMILC.getIM(this, "InteractBrewingStand", "[value]", brewingStands)
                            + " ";
                  }
                  long craftingTables = getTotal(Statistic.CRAFTING_TABLE_INTERACTION, player);
                  if (craftingTables != 0L) {
                    timeText =
                        timeText
                            + CMILC.getIM(this, "InteractCraftTable", "[value]", craftingTables)
                            + " ";
                  }
                  long furnaces = getTotal(Statistic.FURNACE_INTERACTION, player);
                  if (furnaces != 0L) {
                    timeText =
                        timeText + CMILC.getIM(this, "InteractFurnace", "[value]", furnaces) + " ";
                  }
                  if (timeText.isEmpty()) {
                    break;
                  }
                  timeText = CMILC.getIM(this, "Interacted", "[list]", timeText);
                  CMILC.sendMessage(sender, timeText);
                  break;
                }
              case DISPENSER_INSPECTED:
                {
                  long dispensers = player.getStatistic(Statistic.DISPENSER_INSPECTED);
                  if (dispensers != 0L) {
                    timeText =
                        timeText
                            + CMILC.getIM(this, "InspectDispenser", "[value]", dispensers)
                            + " ";
                  }
                  long droppers = getTotal(Statistic.DROPPER_INSPECTED, player);
                  if (droppers != 0L) {
                    timeText =
                        timeText + CMILC.getIM(this, "InspectDropper", "[value]", droppers) + " ";
                  }
                  long hoppers = getTotal(Statistic.HOPPER_INSPECTED, player);
                  if (hoppers != 0L) {
                    timeText =
                        timeText + CMILC.getIM(this, "InspectHopper", "[value]", hoppers) + " ";
                  }
                  if (timeText.isEmpty()) {
                    break;
                  }
                  timeText = CMILC.getIM(this, "Inspected", "[list]", timeText);
                  CMILC.sendMessage(sender, timeText);
                  break;
                }
              default:
                break;
            }
          }
      }
    }
    LC.info_Spliter.sendMessage((Object) sender, new Object[0]);
    return true;
  }

  public static String convertDistance(long distance) {
    if (distance < 100L) {
      return CMILC.getIM(stats.class, "distanceCM", "[value]", formatter.format(distance));
    }
    if (distance / 100L < 1000L) {
      return CMILC.getIM(
          stats.class, "distanceM", "[value]", formatter.format((double) distance / 100.0));
    }
    return CMILC.getIM(
        stats.class, "distanceKm", "[value]", formatter.format((double) distance / 100000.0));
  }

  private String getList() {
    return getList(1);
  }

  /**
   * Builds the hover list from 'most' (sorted by value, zero entries skipped), at most five entries
   * per row and {@code rows} rows, and clears 'most'.
   */
  private String getList(int rows) {
    String result = "";
    this.most = CMI.getInstance().getSortingManager().sortByValueLong(this.most);

    int entryInRow = 0;
    int row = 1;
    for (Map.Entry<String, Long> entry : this.most.entrySet()) {
      if (entry.getValue() == 0L) {
        break;
      }
      if (++entryInRow > 5) {
        entryInRow = 1;
        if (rows >= ++row) {
          result = result + " \n";
        }
      }
      if (row > rows) {
        break;
      }
      if (!result.isEmpty()) {
        result = result + " ";
      }
      result =
          result
              + CMILC.getIM(this, "list", "[name]", entry.getKey(), "[amount]", entry.getValue());
    }
    this.most.clear();
    return result.isEmpty() ? "" : CMILC.getIM(this, "listCover", "[list]", result);
  }

  private int getTotal(Statistic statistic, Player player) {
    return getTotal(statistic, player, null);
  }

  /**
   * Sums the statistic over all materials/entities/items of its type (for BLOCK, ENTITY and ITEM
   * types, also fills 'most' with per-name values). Untyped statistics return the player's value.
   */
  private int getTotal(Statistic statistic, Player player, Statistic.Type type) {
    int total = 0;
    this.most.clear();
    if (type == null) {
      type = statistic.getType();
    }

    switch (type) {
      case BLOCK:
        for (Material material : Material.values()) {
          if (material.toString().startsWith("LEGACY_")) {
            continue;
          }
          try {
            long amount = player.getStatistic(statistic, material);
            if (amount == 0L) {
              continue;
            }
            CMIMaterial cmiMaterial = CMIMaterial.get(material);
            if (cmiMaterial == null) {
              this.most.put(material.name(), amount);
            } else {
              if (!cmiMaterial.isBlock()
                  || Version.isCurrentEqualOrLower(Version.v1_12_R1)
                      && (cmiMaterial.equals(CMIMaterial.GRASS_BLOCK)
                          || cmiMaterial.equals(CMIMaterial.FARMLAND))) {
                continue;
              }
              this.most.put(cmiMaterial.getName(), amount);
            }
            total = (int) (total + amount);
          } catch (Exception ignored) {
          }
        }
        break;
      case ENTITY:
        for (EntityType entityType : EntityType.values()) {
          try {
            long amount = player.getStatistic(statistic, entityType);
            total = (int) (total + amount);
            CMIEntityType cmiEntityType = CMIEntityType.getByType(entityType);
            String name =
                cmiEntityType != null
                    ? cmiEntityType.getTranslatedName()
                    : entityType.name().toLowerCase().replace("_", " ");
            this.most.put(name, amount);
          } catch (Exception ignored) {
          }
        }
        break;
      case ITEM:
        for (Material material : Material.values()) {
          if (material.toString().startsWith("LEGACY_")) {
            continue;
          }
          try {
            long amount = player.getStatistic(statistic, material);
            total = (int) (total + amount);
            CMIItemStack item = CMILib.getInstance().getItemManager().getItem(material);
            if (item != null) {
              this.most.put(item.getRealName(), amount);
            } else {
              this.most.put(material.name().toLowerCase(), amount);
            }
          } catch (Exception ignored) {
          }
        }
        break;
      default:
        total = player.getStatistic(statistic);
        break;
    }
    return total;
  }
}
