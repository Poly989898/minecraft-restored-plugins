package com.Zrips.CMI.Modules.Placeholders;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIExperience;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.RandomTeleport;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayTimeFormat;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.Modules.Sheduler.Schedule;
import com.Zrips.CMI.Modules.SilentChest.silentChestListener;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import com.Zrips.CMI.commands.list.rt;
import com.Zrips.CMI.commands.list.stats;
import com.Zrips.CMI.commands.list.weather;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class Placeholder {
  private CMI plugin;
  Pattern placeholderKeepPatern = Pattern.compile("(\\%\\%)([^\\\"^\\%^ ]+)(\\%\\%)(\\B)");
  Pattern placeholderOthersKeepPatern =
      Pattern.compile(
          "(?i)(\\%parseother_)((\\{)([^\\\"^\\%^ ^\\{^\\}]+)(\\}))([^(\\}\\%).]+)(\\}\\%)");
  Pattern placeholderPatern = Pattern.compile("(%)([^% \"]+)(%)");
  Pattern placeholderPatern2 = Pattern.compile("(\\{)([^\"^%^ ^{^}]+)(\\})");
  private static ChatFilterRule numericalRule = new ChatFilterRule().setPattern("(\\$)(\\d)");
  private static Random random = new Random(System.nanoTime());
  private static final String checkItem = "%checkitem_";
  private int maxDepth = 4;
  private HashMap<String, String> randomCache = new HashMap<>();
  private Player lastRandomPlayer = null;
  private HashMap<Integer, Player> randomPlayers = new HashMap<>();

  public Placeholder(CMI cmi) {
    this.plugin = cmi;
  }

  public List<String> updatePlaceHolders(Player player, List<String> list) {
    if (list == null) {
      return null;
    }

    ArrayList arrayList = new ArrayList(list);
    int index = 0;

    for (int size = list.size(); index < size; index++) {
      arrayList.set(index, this.updatePlaceHolders(player, (String) list.get(index)));
    }

    return arrayList;
  }

  public Placeholder.CMIPlaceholderType getPlaceHolderType(Player player, String string) {
    if (string == null) {
      return null;
    }

    if (string.contains("%") && !string.equals(this.translateOwnPlaceHolder(player, string))) {
      return Placeholder.CMIPlaceholderType.CMI;
    }

    if (string.contains("{") && !string.equals(this.translateOwnPlaceHolder(player, string))) {
      return Placeholder.CMIPlaceholderType.CMI;
    }

    if (CMIHookType.PlaceholderAPI.isEnabled()) {
      try {
        if (string.contains("%") && PlaceholderAPI.containsPlaceholders(string)) {
          return Placeholder.CMIPlaceholderType.PAPI;
        }
      } catch (Throwable throwable) {
      }
    }

    if (CMIHookType.MVdWPlaceholderAPI.isEnabled()) {
      try {
        if (string.contains("{")
            && CMIChatColor.stripHexColor(string).equals(string)
            && !string.equals(
                be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string))) {
          return Placeholder.CMIPlaceholderType.MVdW;
        }
      } catch (Throwable throwable2) {
      }
    }

    return null;
  }

  private static ItemStack getHeldItemOrAir(Player player) {
    ItemStack item = CMIItemStack.getItemInMainHand(player);
    if (item == null) {
      item = new ItemStack(Material.AIR);
    }

    return item.clone();
  }

  private static void reportIssue() {
    CMIMessages.consoleMessage(
        "&c[CMI] Placeholder got blocked due to security concerns (%checkitem_...%)");
  }

  public String updatePlaceHolders(UUID uuid, String string) {
    if (string == null) {
      return null;
    }

    if (string.contains("{")) {
      string = this.translateOwnPlaceHolder(uuid, string);
    }

    if (string.contains("%")) {
      string = this.translateOwnPlaceHolder(uuid, string);
    }

    if (CMIHookType.MVdWPlaceholderAPI.isEnabled() && string.contains("{")) {
      Player player = CMIUser.getOnlinePlayer(uuid);
      if (CMIChatColor.stripHexColor(string).equals(string)) {
        string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
      }
    }

    if (CMIHookType.PlaceholderAPI.isEnabled()) {
      try {
        if (string.contains("%")) {
          if (string.toLowerCase().contains("%checkitem_")
              && this.plugin.getConfigManager().isExploitPatcherCheckItem()) {
            reportIssue();
          } else {
            string = PlaceholderAPI.setPlaceholders(CMIUser.getOnlinePlayer(uuid), string);
          }
        }
      } catch (Exception exception) {
      }
    }

    return string;
  }

  public String updatePlaceHolders(String string) {
    return this.updatePlaceHolders((UUID) null, string);
  }

  public String updatePlaceHolders(Player player, String string) {
    if (string == null) {
      return null;
    }

    HashMap<String, String> protectedParts = new HashMap<>();
    if (string.contains("{")) {
      Matcher matcher = this.placeholderOthersKeepPatern.matcher(string);

      while (matcher.find()) {
        try {
          if (!string.contains("%")) {
            break;
          }

          String matched = matcher.group();
          int randomId = random.nextInt(Integer.MAX_VALUE);
          String token = "|" + randomId + "|";
          protectedParts.put(token, matched);
          string = string.replaceFirst(Pattern.quote(matched), Matcher.quoteReplacement(token));
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      string = this.translateOwnPlaceHolder(player, string);
      if (string.contains("%")) {
        string = this.translateOwnPlaceHolder(player, string);
      }

      for (Entry entry : protectedParts.entrySet()) {
        string =
            string.replace(
                (CharSequence) entry.getKey(), Matcher.quoteReplacement((String) entry.getValue()));
      }
    } else if (string.contains("%")) {
      string = this.translateOwnPlaceHolder(player, string);
    }

    if (CMIHookType.MVdWPlaceholderAPI.isEnabled() && string.contains("{")) {
      try {
        if (CMIChatColor.stripHexColor(string).equals(string)) {
          string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
        }
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    }

    if (CMIHookType.PlaceholderAPI.isEnabled()) {
      try {
        if (string.contains("%")) {
          if (string.toLowerCase().contains("%checkitem_")
              && this.plugin.getConfigManager().isExploitPatcherCheckItem()) {
            reportIssue();
          } else {
            string = PlaceholderAPI.setPlaceholders(player, string);
          }
        }
      } catch (Throwable throwable3) {
      }
    }

    return string;
  }

  public boolean hasMetaValue(CMIUser cmiUser, String string) {
    return cmiUser.getMeta().getMap().containsKey(string.toLowerCase());
  }

  public boolean containsPlaceHolder(String string) {
    Matcher matcher = this.placeholderPatern.matcher(string);
    if (matcher.find()) {
      return true;
    }

    matcher = this.placeholderPatern2.matcher(string);
    return matcher.find();
  }

  public String flatenPlaceHolder(String string) {
    if (!string.contains("%") && !string.contains("{")) {
      return string;
    }

    Matcher matcher = this.placeholderPatern.matcher(string);

    while (matcher.find()) {
      string =
          string.replace(
              matcher.group(), matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3));
    }

    if (CMIHookType.MVdWPlaceholderAPI.isPresent()) {
      matcher = this.placeholderPatern2.matcher(string);

      while (matcher.find()) {
        if (!matcher.group().startsWith("{#")) {
          string =
              string.replace(
                  matcher.group(),
                  matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3));
        }
      }
    }

    return string;
  }

  public String replacePlaceHolders(String string, String replacement) {
    Matcher matcher = this.placeholderPatern.matcher(string);

    while (matcher.find()) {
      String found = matcher.group();
      string = string.replaceFirst(Pattern.quote(found), Matcher.quoteReplacement(replacement));
    }

    matcher = this.placeholderPatern2.matcher(string);

    while (matcher.find()) {
      String found = matcher.group();
      string = string.replaceFirst(Pattern.quote(found), Matcher.quoteReplacement(replacement));
    }

    return string;
  }

  public String coolDownPlaceHolders(String string, Player player) {
    if (!string.toLowerCase().contains("parseother")) {
      return string;
    }

    Matcher matcher = this.placeholderPatern.matcher(string);

    while (matcher.find()) {
      String match = matcher.group();
      if (!match.equalsIgnoreCase(
          this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, match))) {
        string =
            string.replaceFirst(
                Pattern.quote(match),
                "%|" + Matcher.quoteReplacement(match.substring(1, match.length() - 1)) + "|%");
      }
    }

    matcher = this.placeholderPatern2.matcher(string);

    while (matcher.find()) {
      String match = matcher.group();
      if (!match.equalsIgnoreCase(
          this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, match))) {
        string =
            string.replaceFirst(
                Pattern.quote(match),
                "{|" + Matcher.quoteReplacement(match.substring(1, match.length() - 1)) + "|}");
      }
    }

    return string;
  }

  public String coolDownPlaceHolders(String string) {
    Matcher matcher = this.placeholderPatern.matcher(string);

    while (matcher.find()) {
      String match = matcher.group();
      string =
          string.replaceFirst(
              Pattern.quote(match),
              "%|" + Matcher.quoteReplacement(match.substring(1, match.length() - 1)) + "|%");
    }

    matcher = this.placeholderPatern2.matcher(string);

    while (matcher.find()) {
      String match = matcher.group();
      string =
          string.replaceFirst(
              Pattern.quote(match),
              "{|" + Matcher.quoteReplacement(match.substring(1, match.length() - 1)) + "|}");
    }

    return string;
  }

  public String translatePAPIPlaceHolder(
      Player player, Placeholder.CMIPlaceHolders cmiPlaceHolders, String string) {
    UUID uuid = null;
    if (player != null) {
      uuid = player.getUniqueId();
    }

    return this.translatePAPIPlaceHolder(uuid, cmiPlaceHolders, string);
  }

  public String translatePAPIPlaceHolder(
      UUID uuid, Placeholder.CMIPlaceHolders cmiPlaceHolders, String string) {
    if (string.contains("{")) {
      return this.translateOwnPlaceHolder(uuid, string);
    }

    if (cmiPlaceHolders == null) {
      return null;
    }

    Object cachedValue = cmiPlaceHolders.getCachedValue(uuid);
    String result = null;
    if (cachedValue != null) {
      result = (String) cachedValue;
    } else {
      result = this.getValue(uuid, cmiPlaceHolders, string);
    }

    result = CTextManager.clean(result);
    if (cmiPlaceHolders.isCache()
        && cachedValue == null
        && cmiPlaceHolders.getCachedValue(uuid) == null) {
      cmiPlaceHolders.addCachedValue(uuid, result, 50);
    }

    return result == null ? "" : result;
  }

  public String translateOwnPlaceHolder(Player player, String string) {
    return this.translateOwnPlaceHolder(player == null ? null : player.getUniqueId(), string);
  }

  private String matchInception(UUID uuid, String string, int depth) {
    if (string.contains("{")) {
      Matcher matcher = this.placeholderPatern.matcher(string);
      int matchCount = 0;

      try {
        while (matcher.find() && ++matchCount <= 10) {
          String placeholderName = matcher.group(2);
          if (!string.contains("{")) {
            break;
          }

          Placeholder.CMIPlaceHolders cmiPlaceHolders =
              Placeholder.CMIPlaceHolders.getByNameExact(placeholderName);
          if (cmiPlaceHolders != null) {
            String placeholderText = matcher.group();
            String replacement = this.getValue(uuid, cmiPlaceHolders, placeholderText);
            if (replacement == null) {
              replacement = "";
            }

            string =
                string.replaceFirst(
                    Pattern.quote(placeholderText), Matcher.quoteReplacement(replacement));
          } else if (CMIHookType.PlaceholderAPI.isEnabled()) {
            try {
              Player player = CMIUser.getOnlinePlayer(uuid);
              String fullMatch = matcher.group();
              if (!fullMatch.startsWith("{#")) {
                if (placeholderName.toLowerCase().contains("%checkitem_")
                    && this.plugin.getConfigManager().isExploitPatcherCheckItem()) {
                  reportIssue();
                } else {
                  String papiValue =
                      PlaceholderAPI.setPlaceholders(player, "%" + placeholderName + "%");
                  if (papiValue == null) {
                    papiValue = "";
                  }

                  if (!papiValue.equalsIgnoreCase("%" + placeholderName + "%")) {
                    string =
                        string.replaceFirst(
                            Pattern.quote(fullMatch), Matcher.quoteReplacement(papiValue));
                  }
                }
              }
            } catch (Exception exception) {
            }
          }
        }
      } catch (Throwable throwable) {
      }
    }

    return string;
  }

  public String translateOwnPlaceHolder(UUID uuid, String text) {
    if (text == null) {
      return null;
    }

    if (text.contains("{")) {
      HashMap<String, String> protectedParts = new HashMap();
      Matcher matcher = this.placeholderOthersKeepPatern.matcher(text);

      while (matcher.find()) {
        try {
          if (!text.contains("%")) {
            break;
          }

          String matched = matcher.group();
          int randomId = random.nextInt(Integer.MAX_VALUE);
          String token = "|" + randomId + "|";
          protectedParts.put(token, matched);
          text = text.replaceFirst(Pattern.quote(matched), Matcher.quoteReplacement(token));
        } catch (Throwable throwable) {
        }
      }

      text = this.matchInception(uuid, text, 0);

      for (Entry entry : protectedParts.entrySet()) {
        text = text.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
      }
    }

    if (text.contains("%")) {
      HashMap<String, String> unwrapped = new HashMap();
      Matcher keyMatcher = this.placeholderKeepPatern.matcher(text);

      while (keyMatcher.find()) {
        try {
          if (!text.contains("%")) {
            break;
          }

          String keyMatch = keyMatcher.group();
          int randomId = random.nextInt(Integer.MAX_VALUE);
          String token = "|" + randomId + "|";
          unwrapped.put(token, keyMatch.substring(1, keyMatch.length() - 1));
          text = text.replaceFirst(Pattern.quote(keyMatch), Matcher.quoteReplacement(token));
        } catch (Throwable throwable2) {
        }
      }

      Matcher placeholderMatcher = this.placeholderPatern.matcher(text);

      while (placeholderMatcher.find()) {
        try {
          String placeholderName = placeholderMatcher.group(2);
          if (!text.contains("%")) {
            break;
          }

          Placeholder.CMIPlaceHolders cmiPlaceHolders =
              Placeholder.CMIPlaceHolders.getByNameExact(placeholderName);
          if (cmiPlaceHolders != null) {
            String placeholderText = placeholderMatcher.group();
            Object cachedValue = cmiPlaceHolders.getCachedValue(uuid);
            String placeholderValue = null;
            if (cachedValue != null) {
              placeholderValue = (String) cachedValue;
            } else {
              placeholderValue = this.getValue(uuid, cmiPlaceHolders, placeholderText);
            }

            if (cmiPlaceHolders.isCache()
                && cachedValue == null
                && cmiPlaceHolders.getCachedValue(uuid) == null) {
              cmiPlaceHolders.addCachedValue(uuid, placeholderValue, 50);
            }

            if (placeholderValue == null) {
              placeholderValue = "";
            }

            placeholderValue = CTextManager.clean(placeholderValue);
            text =
                text.replaceFirst(
                    Pattern.quote(placeholderText), Matcher.quoteReplacement(placeholderValue));
          }
        } catch (Throwable throwable3) {
          throwable3.printStackTrace();
        }
      }

      for (Entry entry : unwrapped.entrySet()) {
        text = text.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
      }
    }

    return text;
  }

  public String getValue(Player player, Placeholder.CMIPlaceHolders cmiPlaceHolders) {
    return this.getValue(player, cmiPlaceHolders, null);
  }

  public String getValue(
      Player player, Placeholder.CMIPlaceHolders cmiPlaceHolders, String string) {
    return this.getValue(player != null ? player.getUniqueId() : null, cmiPlaceHolders, string);
  }

  public String getValue(UUID uuid, Placeholder.CMIPlaceHolders cmiPlaceHolders, String string) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
    Player player = null;
    if (cmiUser != null && cmiUser.isOnline()) {
      player = cmiUser.getPlayer();
    }

    if (cmiPlaceHolders == null) {
      return null;
    }

    if (cmiPlaceHolders.getUserRedirect() != null) {
      List<String> list = cmiPlaceHolders.getComplexValues(string);
      if (list.isEmpty()) {
        return "";
      }

      cmiUser = CMIUser.getUser((String) list.get(0));
      if (cmiUser == null) {
        return "";
      }

      cmiPlaceHolders = cmiPlaceHolders.getUserRedirect();
    }

    if (cmiUser != null) {
      switch (cmiPlaceHolders) {
        case cmi_user_charges_left:
          return String.valueOf(cmiUser.getPCharge().getCharges());
        case cmi_user_charges_max:
          return String.valueOf(cmiUser.getPCharge().getMaxCharges());
        case cmi_user_charges_time:
          return String.valueOf(cmiUser.getPCharge().getLeftTime());
        case cmi_user_charges_cooldown:
          return String.valueOf(cmiUser.getPCharge().getCooldown());
        case cmi_user_display_name:
          return cmiUser.getDisplayName();
        case cmi_p_$1_display_name:
        case cmi_p_$1_name:
        case cmi_user_uuid_$1:
        case cmi_p_$1_cuffed:
        case cmi_p_$1_muted:
        case cmi_p_$1_god:
        case cmi_user_sneaking:
        case cmi_p_$1_banned:
        case cmi_user_maxhomes:
        case cmi_user_ping:
        case cmi_user_gamemode:
        case cmi_user_op:
        case cmi_user_canfly:
        case cmi_user_flying:
        case cmi_p_$1_jailed:
        case cmi_p_$1_jailname:
        case cmi_p_$1_jailcell:
        case cmi_p_$1_jailtime:
        case cmi_p_$1_jailreason:
        case cmi_p_$1_jailedby:
        case cmi_p_$1_online:
        case cmi_user_itemcount_$1:
        case cmi_user_maxperm_$1_$2:
        case cmi_equation_$1:
        case cmi_equationint_$1:
        case cmi_color_$1:
        case cmi_stripcolor_$1:
        case cmi_iteminhand_displayname:
        case cmi_iteminhand_realname:
        case cmi_iteminhand_type:
        case cmi_iteminhand_itemdata:
        case cmi_iteminhand_amount:
        case cmi_iteminhand_durability:
        case cmi_iteminhand_maxdurability:
        case cmi_iteminhand_custommodeldata:
        case cmi_iteminhand_worth:
        case cmi_iteminhand_worth_one:
        case cmi_iteminhand_worthc:
        case cmi_iteminhand_worthc_one:
        case cmi_material_realname_$1:
        case cmi_schedule_nextin_$1:
        case cmi_schedule_endat_$1:
        case cmi_baltop_cname_$1:
        case cmi_baltop_name_$1:
        case cmi_baltop_money_$1:
        case cmi_baltop_shortmoney_$1:
        case cmi_playtimetop_cname_$1:
        case cmi_playtimetop_name_$1:
        case cmi_playtimetop_time_$1:
        case cmi_votetop_$1:
        case cmi_votetopcount_$1:
        case cmi_worth_buy_$1:
        case cmi_worth_sell_$1:
        case cmi_worthc_buy_$1:
        case cmi_worthc_sell_$1:
        case cmi_bungee_total_$1:
        case cmi_bungee_current_$1:
        case cmi_bungee_motd_$1:
        case cmi_bungee_onlinestatus_$1:
        case cmi_tps_1:
        case cmi_tps_60:
        case cmi_tps_300:
        case cmi_tps_$1_colored:
        case cmi_tps_$1:
        case cmi_random_player_name:
        case cmi_lastrandom_player_name:
        case cmi_random_$1_$2:
        case cmi_lastrandom_$1:
        case cmi_chatmute_time:
        case cmi_chatmute_reason:
        case cmi_jail_time_$1_$2:
        case cmi_jail_username_$1_$2:
        case cmi_jail_reason_$1_$2:
        case cmi_weather_$1:
        case cmi_weatherduration_$1:
        case cmi_afk_count:
        case cmi_maintenance_state:
        case cmi_maintenance_message:
        case cmi_chat_range:
        case cmi_server_uptime:
        case cmi_server_uptime_seconds:
        case cmi_server_worlds:
        case cmi_server_vanished:
        case cmi_server_users:
        case player_world:
        case cmi_player_world:
        case player_x:
        case cmi_player_x:
        case player_y:
        case cmi_player_y:
        case player_z:
        case cmi_player_z:
        case player_biome:
        case cmi_player_biome:
        default:
          break;
        case cmi_user_cleannickname:
          return cmiUser.getNickName() == null
              ? cmiUser.getName()
              : CMIChatColor.stripColor(cmiUser.getNickName());
        case cmi_user_nickname:
          return cmiUser.getNickName() == null ? cmiUser.getName() : cmiUser.getNickName();
        case cmi_user_name:
          return cmiUser.getName();
        case cmi_user_uuid:
          if (cmiUser.getUniqueId() == null) {
            return "";
          }

          return cmiUser.getUniqueId().toString();
        case cmi_user_deathloc:
          Location location = cmiUser.getDeathLoc();
          if (location != null && location.getWorld() != null) {
            return LC.Location_Full.getLocale(new Object[] {location});
          }

          return null;
        case cmi_user_backloc:
          Location backLocation = cmiUser.getLastTeleportLocation();
          if (backLocation != null && backLocation.getWorld() != null) {
            return LC.Location_Full.getLocale(new Object[] {backLocation});
          }

          return null;
        case cmi_user_cuffed:
          return variable(cmiUser.isCuffed());
        case cmi_user_muted:
          return variable(cmiUser.isMuted());
        case cmi_user_inpvp:
          return variable(cmiUser.isInCombatWithPlayer());
        case cmi_user_god:
          return variable(cmiUser.isGod());
        case cmi_user_mail_count:
          return String.valueOf(cmiUser.getMails().size());
        case cmi_user_warning_count:
          return String.valueOf(cmiUser.getWarnings() == null ? 0 : cmiUser.getWarnings().size());
        case cmi_user_warning_points:
          return String.valueOf(cmiUser.getWarningPoints());
        case cmi_user_afk:
          return variable(cmiUser.isAfk());
        case cmi_user_afk_symbol:
          return cmiUser.isAfk()
              ? CMILC.info_afkSymbolOn.getLocale()
              : CMILC.info_afkSymbolOff.getLocale();
        case cmi_user_afk_msg:
          if (!cmiUser.isAfk()) {
            return "";
          }

          return cmiUser.getAfkReason() == null ? "" : cmiUser.getAfkReason();
        case cmi_user_afk_for:
          return !cmiUser.isAfk()
              ? ""
              : CMITimeManager.to24hourShort(
                  System.currentTimeMillis() - cmiUser.getAfkInfo().getAfkFrom());
        case cmi_user_afk_in:
          if (this.plugin.getAfkManager().getLastAction(cmiUser) == null) {
            return CMITimeManager.to24hourShort(this.plugin.getAfkManager().getAwayTrigerTime());
          }

          return cmiUser.isAfk()
              ? ""
              : CMITimeManager.to24hourShort(
                  this.plugin.getAfkManager().getAwayTrigerTime()
                      - (System.currentTimeMillis()
                          - this.plugin.getAfkManager().getLastAction(cmiUser)));
        case cmi_user_spy:
          return variable(cmiUser.getOptionState(PlayerOption.chatSpy));
        case cmi_user_cmdspy:
          return variable(cmiUser.getOptionState(PlayerOption.cmdSpy));
        case cmi_user_signspy:
          return variable(cmiUser.getOptionState(PlayerOption.signSpy));
        case cmi_user_joinedcounter:
          return variable(cmiUser.isJoinedCounter());
        case cmi_user_banned:
          return variable(cmiUser.isBanned());
        case cmi_user_homeamount:
          return String.valueOf(cmiUser.getValidHomeCount());
        case cmi_user_homelist:
          return cmiUser.getHomes().values().stream()
              .map(CmiHome::getName)
              .collect(Collectors.joining(LC.info_ListSpliter.getLocale(new Object[0])));
        case cmi_user_missingexp:
          return String.valueOf(cmiUser.getMissingExp());
        case cmi_user_missingexpp:
          return String.valueOf(
              (int)
                      (cmiUser.getMissingExp()
                          * 100.0
                          / (CMIExperience.getTotalExpToLevel(cmiUser.getLevel() + 1)
                              - CMIExperience.getTotalExpToLevel(cmiUser.getLevel()))
                          * 100.0)
                  / 100.0);
        case cmi_user_exp:
          return String.valueOf(cmiUser.getExpForCurrentLevel());
        case cmi_user_expp:
          return String.valueOf(
              (int)
                      (cmiUser.getExpForCurrentLevel()
                          * 100.0
                          / (CMIExperience.getTotalExpToLevel(cmiUser.getLevel() + 1)
                              - CMIExperience.getTotalExpToLevel(cmiUser.getLevel()))
                          * 100.0)
                  / 100.0);
        case cmi_user_totalexp:
          return String.valueOf(cmiUser.getTotalExp());
        case cmi_user_level:
          return String.valueOf(cmiUser.getLevel());
        case cmi_user_pweather:
          if (!cmiUser.isOnline()) {
            return "";
          }

          WeatherType weatherType = cmiUser.getPlayer().getPlayerWeather();
          return weatherType != null
              ? (weatherType.equals(WeatherType.DOWNFALL)
                  ? CMILC.getIM(weather.class, "rainy")
                  : CMILC.getIM(weather.class, "sunny"))
              : LC.info_notSet.getLocale(new Object[0]);
        case cmi_user_weather:
          if (!cmiUser.isOnline()) {
            return "";
          }

          String weatherText =
              cmiUser.getWorld().hasStorm()
                  ? CMILC.getIM(weather.class, "rainy")
                  : CMILC.getIM(weather.class, "sunny");
          if (cmiUser.getWorld().hasStorm() && cmiUser.getWorld().isThundering()) {
            weatherText = CMILC.getIM(weather.class, "thundering");
          }

          return weatherText;
        case cmi_user_weatherduration:
          if (!cmiUser.isOnline()) {
            return "";
          }

          World world = cmiUser.getWorld();
          long longValue =
              !world.isThundering() ? world.getWeatherDuration() : world.getThunderDuration();
          if (world.isThundering()) {
            longValue = world.getThunderDuration();
          }

          return CMITimeManager.to24hourShort(longValue * 50L);
        case cmi_user_vanished_symbol:
          return cmiUser.isVanished()
              ? CMILC.info_vanishSymbolOn.getLocale()
              : CMILC.info_vanishSymbolOff.getLocale();
        case cmi_user_balance_formated:
        case cmi_user_balance_formatted:
        case vault_eco_balance_formatted:
          return String.valueOf(cmiUser.getFormatedBalance());
        case cmi_user_balance:
          return String.format("%.2f", cmiUser.getBalance());
        case cmi_user_prefix:
          return CMIChatColor.translate(cmiUser.getPrefix());
        case cmi_user_suffix:
          return CMIChatColor.translate(cmiUser.getSuffix());
        case cmi_user_group:
          return cmiUser.getGroupName();
        case cmi_user_nameplate_prefix:
          return CMIChatColor.translate(cmiUser.getNamePlatePrefix());
        case cmi_user_nameplate_suffix:
          return CMIChatColor.translate(cmiUser.getNamePlateSuffix());
        case cmi_user_tfly:
          return String.valueOf(
              cmiUser.getTfly() == 0L
                  ? 0L
                  : (cmiUser.getTfly() - System.currentTimeMillis()) / 1000L);
        case cmi_user_tfly_formated:
        case cmi_user_tfly_formatted:
          return String.valueOf(
              cmiUser.getTfly() == 0L
                  ? CMITimeManager.to24hourShort(0L)
                  : CMITimeManager.to24hourShort(cmiUser.getTfly() - System.currentTimeMillis()));
        case cmi_user_flightcharge:
          return String.valueOf(
              cmiUser.getFlightCharge() != null && !(cmiUser.getFlightCharge().getCharge() <= 0.0)
                  ? (int) (cmiUser.getFlightCharge().getCharge() * 100.0) / 100.0
                  : 0.0);
        case cmi_user_tgod:
          return String.valueOf(
              cmiUser.getTgod() == 0L
                  ? 0L
                  : (cmiUser.getTgod() - System.currentTimeMillis()) / 1000L);
        case cmi_user_tgod_formated:
        case cmi_user_tgod_formatted:
          return String.valueOf(
              cmiUser.getTgod() == 0L
                  ? CMITimeManager.to24hourShort(0L)
                  : CMITimeManager.to24hourShort(cmiUser.getTgod() - System.currentTimeMillis()));
        case cmi_user_votecount:
          return String.valueOf(cmiUser.getVotifierVotes());
        case cmi_user_dailyvotecount:
          return String.valueOf(cmiUser.getDailyVoteCount());
        case cmi_user_chatcolor:
          return String.valueOf(
              cmiUser.getChatMessageFormat() == null
                  ? ""
                  : cmiUser.getChatMessageFormat().toString());
        case cmi_user_rank:
        case cmi_user_rank_displayname:
          CMIRank cmiRank = cmiUser.getRank();
          return cmiRank == null ? "" : cmiRank.getDisplayName();
        case cmi_user_rank_name:
          CMIRank namedRank = cmiUser.getRank();
          return namedRank == null ? "" : namedRank.getName();
        case cmi_user_nextranks:
          CMIRank currentRank = cmiUser.getRank();
          String nextRankNames = "";
          if (currentRank == null) {
            return "";
          }

          for (CMIRank nextRank : currentRank.getNextRanks()) {
            if (!nextRankNames.isEmpty()) {
              nextRankNames =
                  nextRankNames + CMIChatColor.RESET + LC.info_ListSpliter.getLocale(new Object[0]);
            }

            nextRankNames = nextRankNames + CMIChatColor.WHITE + nextRank.getDisplayName();
          }

          return nextRankNames;
        case cmi_user_nextrankpercent:
          if (cmiUser.getRank() == null) {
            return "";
          }

          List<CMIRank> nextRanks = cmiUser.getRank().getNextRanks();
          if (nextRanks.isEmpty()) {
            return "";
          }

          CMIRank firstNextRank = (CMIRank) nextRanks.get(0);
          if (firstNextRank == null) {
            return "";
          }

          return String.valueOf(
              (int)
                      (this.plugin.getRankManager().getOverallDonePercentage(cmiUser, firstNextRank)
                          * 100.0)
                  / 100.0);
        case cmi_user_nextvalidranks:
          CMIRank playerRank = cmiUser.getRank();
          if (playerRank == null) {
            return "";
          }

          String validRankNames = "";

          for (CMIRank validRankUp : playerRank.getNextValidRankUps(cmiUser)) {
            if (!validRankNames.isEmpty()) {
              validRankNames =
                  validRankNames
                      + CMIChatColor.RESET
                      + LC.info_ListSpliter.getLocale(new Object[0]);
            }

            validRankNames = validRankNames + CMIChatColor.WHITE + validRankUp.getDisplayName();
          }

          return validRankNames;
        case cmi_user_canrankup:
          CMIRank playerRankForRankUp = cmiUser.getRank();
          if (playerRankForRankUp == null) {
            return "";
          }

          List<CMIRank> rankUps = playerRankForRankUp.getNextValidRankUps(cmiUser);
          if (rankUps == null) {
            return "";
          }

          return variable(!rankUps.isEmpty());
        case cmi_user_country:
          return cmiUser.getCountry();
        case cmi_user_country_code:
          return cmiUser.getCountryCode();
        case cmi_user_city:
          return cmiUser.getCity();
        case cmi_user_name_colorcode:
          return cmiUser.getNamePlateNameColor() == null
              ? ""
              : CMIChatColor.getColor(cmiUser.getNamePlateNameColor().toString())
                  .getBukkitColorCode();
        case cmi_user_glow_code:
          if (cmiUser.getGlow() == null) {
            return "";
          }

          if (cmiUser.isOnline()) {
            Team team = CMI.getInstance().getSB().getPlayerTeam(cmiUser.getPlayer());
            if (team != null && team.getColor() != null) {
              return CMIChatColor.getColor(team.getColor().toString()).getBukkitColorCode();
            }

            return "";
          }

          return CMIChatColor.getColor(cmiUser.getGlow().toString()).getBukkitColorCode();
        case cmi_user_glow_name:
          if (cmiUser.getGlow() == null) {
            return "";
          }

          if (cmiUser.isOnline()) {
            Team glowTeam = CMI.getInstance().getSB().getPlayerTeam(cmiUser.getPlayer());
            if (glowTeam != null && glowTeam.getColor() != null) {
              return CMIText.firstToUpperCase(
                  CMIChatColor.getColor(glowTeam.getColor().toString()).getCleanName());
            }

            return "";
          }

          return CMIText.firstToUpperCase(
              CMIChatColor.getColor(cmiUser.getGlow().toString()).toString());
        case cmi_user_jailed:
          return variable(cmiUser.isJailed());
        case cmi_user_jailname:
          return cmiUser.getCell() == null ? "" : cmiUser.getCell().getJail().getName();
        case cmi_user_jailcell:
          return cmiUser.getCell() == null ? "" : String.valueOf(cmiUser.getCell().getId());
        case cmi_user_jailtime:
          return cmiUser.isJailed()
              ? (cmiUser.getJailedUntil() != null
                      && cmiUser.getJailedUntil() >= System.currentTimeMillis()
                  ? CMITimeManager.to24hourShort(
                      cmiUser.getJailedUntil() - System.currentTimeMillis())
                  : "")
              : null;
        case cmi_user_jailreason:
          return cmiUser.getJailedUntil() != null
                  && cmiUser.getJailedUntil() >= System.currentTimeMillis()
                  && cmiUser.getJailedReason() != null
              ? CMIChatColor.translate(cmiUser.getJailedReason())
              : "";
        case cmi_user_jailedby:
          return cmiUser.getJailedUntil() != null
                  && cmiUser.getJailedUntil() >= System.currentTimeMillis()
              ? (cmiUser.getJailedBy() == null
                  ? LC.info_Console.getLocale(new Object[0])
                  : (CMIUser.getUser(cmiUser.getJailedBy()) != null
                      ? CMIUser.getUser(cmiUser.getJailedBy()).getDisplayName(false)
                      : "NA"))
              : "";
        case cmi_user_riding:
          Entity entity = cmiUser.getPlayer().getVehicle();
          if (entity instanceof Slime
              && entity.getCustomName() != null
              && entity.getCustomName().equalsIgnoreCase("CMIFakeSlime")) {
            if (!this.plugin.getAnimationManager().isRiding(cmiUser.getUniqueId())) {
              return "";
            }

            UUID ridingUuid =
                this.plugin.getAnimationManager().getRidingList().get(cmiUser.getUniqueId());
            Player ridingPlayer = CMIUser.getOnlinePlayer(ridingUuid);
            if (ridingPlayer != null) {
              return ridingPlayer.getName();
            }

            Entity ridingEntity = Bukkit.getEntity(ridingUuid);
            return ridingEntity == null
                ? ""
                : CMIEntityType.getRealNameByType(ridingEntity.getType());
          }

          return entity == null ? "" : CMIEntityType.getRealNameByType(entity.getType());
        case cmi_user_beingriddenby:
          if (!this.plugin.getAnimationManager().isBeingRiden(cmiUser.getUniqueId())) {
            return "";
          }

          UUID riderUuid = this.plugin.getAnimationManager().getBeingRiden(cmiUser.getUniqueId());
          Player riderPlayer = CMIUser.getOnlinePlayer(riderUuid);
          return riderPlayer == null ? "" : riderPlayer.getName();
        case cmi_user_bungeeserver:
          if (!this.plugin.getBungeeCordManager().isBungeeCord()) {
            return "";
          }

          BungeeCordServer bungeeCordServer =
              this.plugin.getBungeeCordManager().getPlayerServer(cmiUser.getUniqueId());
          return bungeeCordServer == null ? "" : bungeeCordServer.getName();
        case cmi_user_rt_cooldown:
          Long usedAt = rt.getUsedAt(cmiUser.getUniqueId(), cmiUser.getWorld().getName());
          if (usedAt == null) {
            return "-";
          }

          RandomTeleport randomTeleport =
              this.plugin
                  .getRandomTeleportationManager()
                  .getRandomTeleport(cmiUser.getWorld().getName());
          int cooldownSeconds =
              randomTeleport == null
                  ? this.plugin.getRandomTeleportationManager().getRandomTeleportCooldown()
                  : randomTeleport.getCooldownWithDefault();
          if (usedAt + cooldownSeconds * 1000L < System.currentTimeMillis()) {
            return "-";
          }

          return CMITimeManager.to24hourShort(
              cooldownSeconds * 1000L - (System.currentTimeMillis() - usedAt) + 1000L);
        case cmi_user_rt_cooldown_$1:
          List<String> rtArgs = cmiPlaceHolders.getComplexValues(string);
          if (rtArgs.isEmpty()) {
            return "";
          }

          Long usedAtValue = rt.getUsedAt(cmiUser.getUniqueId(), (String) rtArgs.get(0));
          if (usedAtValue == null) {
            return "-";
          }

          RandomTeleport randomTeleportValue =
              this.plugin.getRandomTeleportationManager().getRandomTeleport((String) rtArgs.get(0));
          int cooldownSecondsValue =
              randomTeleportValue == null
                  ? this.plugin.getRandomTeleportationManager().getRandomTeleportCooldown()
                  : randomTeleportValue.getCooldownWithDefault();
          if (usedAtValue + cooldownSecondsValue * 1000L < System.currentTimeMillis()) {
            return "-";
          }

          return CMITimeManager.to24hourShort(
              cooldownSecondsValue * 1000L - (System.currentTimeMillis() - usedAtValue) + 1000L);
        case cmi_user_playtime_formatted:
          return CMITimeManager.to24hourShort(cmiUser.getTotalPlayTime(false));
        case cmi_user_playtime_days:
          return String.valueOf(cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60L / 24L);
        case cmi_user_playtime_dayst:
          return String.valueOf(
              (int) (100.0 * (cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60.0 / 24.0))
                  / 100.0);
        case cmi_user_playtime_hours:
          return String.valueOf(cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60L % 24L);
        case cmi_user_playtime_hoursf:
          return String.valueOf(cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60L);
        case cmi_user_playtime_hourst:
          return String.valueOf(
              (int) (100.0 * (cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60.0)) / 100.0);
        case cmi_user_playtime_minutes:
          long wholeHoursMs = cmiUser.getTotalPlayTime(false) / 1000L / 60L / 60L;
          wholeHoursMs = wholeHoursMs * 1000L * 60L * 60L;
          return String.valueOf((cmiUser.getTotalPlayTime(false) - wholeHoursMs) / 1000L / 60L);
        case cmi_user_playtime_minutest:
          return String.valueOf(cmiUser.getTotalPlayTime(false) / 1000L / 60L);
        case cmi_user_playtime_seconds:
          long wholeMinutesMs = cmiUser.getTotalPlayTime(false) / 1000L / 60L;
          wholeMinutesMs = wholeMinutesMs * 1000L * 60L;
          return String.valueOf((cmiUser.getTotalPlayTime(false) - wholeMinutesMs) / 1000L);
        case cmi_user_playtime_secondst:
          return String.valueOf(cmiUser.getTotalPlayTime(false) / 1000L);
        case cmi_user_prewards_count:
          return String.valueOf(
              this.plugin.getPlayTimeRewardManager().getClaimableRewardCount(cmiUser));
        case cmi_user_world:
          return cmiUser.getWorld().getName();
        case cmi_user_world_formatted:
          return CMIWorld.getWorldNameFormatted(cmiUser.getWorld());
        case cmi_user_online:
          return variable(cmiUser.isOnline());
        case cmi_user_toggle_$1:
        case cmi_user_togglename_$1:
          List<String> toggleArgs = cmiPlaceHolders.getComplexValues(string);
          if (toggleArgs.isEmpty()) {
            return "";
          }

          Boolean toggleState = null;
          PlayerOption playerOption = PlayerOption.get((String) toggleArgs.get(0));
          if (playerOption != null) {
            toggleState = cmiUser.getOptionState(playerOption);
          }

          String toggleName;
          switch ((toggleName = ((String) toggleArgs.get(0)).toLowerCase()).hashCode()) {
            case -907987346:
              if (toggleName.equals("schest")) {
                toggleState =
                    silentChestListener.toggledSilentChest.containsKey(cmiUser.getUniqueId());
              }
              break;
            case 1809638968:
              if (toggleName.equals("staffchat")) {
                toggleState =
                    this.plugin
                        .getChatFormatManager()
                        .getStaffChats()
                        .contains(cmiUser.getUniqueId());
              }
              break;
            case 1869959814:
              if (toggleName.equals("autoflightrecharge")) {
                toggleState = cmiUser.getFlightCharge().isAutoRecharge();
              }
          }

          if (toggleState == null) {
            return "";
          }

          if (cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_user_togglename_$1)) {
            return variable(toggleState);
          }

          return toggleState ? String.valueOf(1) : String.valueOf(0);
        case cmi_user_holo_page_$1:
          List<String> holoArgs = cmiPlaceHolders.getComplexValues(string);
          if (holoArgs.isEmpty()) {
            return "";
          }

          CMIHologram cmiHologram =
              this.plugin.getHologramManager().getByName((String) holoArgs.get(0));
          if (cmiHologram == null) {
            return "";
          }

          return String.valueOf(cmiHologram.getPlayerPage(cmiUser.getUniqueId()));
        case cmi_user_rank_percent_$1:
          List<String> rankArgs = cmiPlaceHolders.getComplexValues(string);
          if (rankArgs.isEmpty()) {
            return "";
          }

          CMIRank rankForPercent = this.plugin.getRankManager().getRank((String) rankArgs.get(0));
          if (rankForPercent == null) {
            return "";
          }

          return String.valueOf(
              (int)
                      (this.plugin
                              .getRankManager()
                              .getOverallDonePercentage(cmiUser, rankForPercent)
                          * 100.0)
                  / 100.0);
        case cmi_user_meta_$1:
          List<String> metaArgs = cmiPlaceHolders.getComplexValues(string);
          if (metaArgs.isEmpty()) {
            return "";
          }

          return cmiUser.getMeta().getValue((String) metaArgs.get(0));
        case cmi_user_metaint_$1:
          List<String> metaIntArgs = cmiPlaceHolders.getComplexValues(string);
          if (metaIntArgs.isEmpty()) {
            return "";
          }

          try {
            return String.valueOf(
                (int) Double.parseDouble(cmiUser.getMeta().getValue((String) metaIntArgs.get(0))));
          } catch (Exception exception) {
            return cmiUser.getMeta().getValue((String) metaIntArgs.get(0));
          }
        case cmi_user_baltop:
          return String.valueOf(
              this.plugin.getEconomyManager().getLeaderboard().getRank(cmiUser.getUniqueId()));
        case cmi_user_playtimetop:
          return String.valueOf(
              this.plugin.getStatsManager().getLeaderboard().getRank(cmiUser.getUniqueId()));
        case cmi_user_stats_$1:
          List<String> statArgs = cmiPlaceHolders.getComplexValues(string);
          if (statArgs.isEmpty()) {
            return "";
          }

          String statName = (String) statArgs.get(0);
          String statQualifier = null;
          if (statName.contains(":")) {
            String[] parts = statName.split(":", 2);
            statQualifier = parts[1];
            statName = parts[0];
          }

          StatsManager.CMIStatistic cmiStatistic = StatsManager.CMIStatistic.getByName(statName);
          if (cmiStatistic == null) {
            return "";
          }

          Long statValue = cmiUser.getStatistic(cmiStatistic, statQualifier);
          switch (cmiStatistic.getType()) {
            case Time:
              return CMITimeManager.to24hourShort(statValue);
            case Distance:
              return stats.convertDistance(statValue);
            case Damage:
            case Number:
              return String.valueOf(statValue);
            default:
              return String.valueOf(statValue);
          }
        case cmi_user_kitcd_$1:
          List<String> kitCdArgs = cmiPlaceHolders.getComplexValues(string);
          if (kitCdArgs.isEmpty()) {
            return "";
          }

          Kit cooldownKit =
              this.plugin
                  .getKitsManager()
                  .getKit(cmiUser.getPlayer(), (String) kitCdArgs.get(0), true);
          string = "-";
          if (cooldownKit != null) {
            Long kitUseTime = cmiUser.getKitUseTimeIn(cooldownKit);
            if (kitUseTime != null && kitUseTime != 0L) {
              string = CMITimeManager.to24hourShort(cmiUser.getKitUseTimeIn(cooldownKit));
            }
          }

          return string;
        case cmi_user_kit_available:
          if (player == null) {
            return "";
          }

          Object cachedKitCount = cmiPlaceHolders.getCachedValue(player.getUniqueId());
          if (cachedKitCount != null) {
            return String.valueOf(cachedKitCount);
          }

          var validKits = this.plugin.getKitsManager().getValidKitsForPlayer(player, false);
          if (validKits == null) {
            return String.valueOf(0);
          }

          int totalKitCount = 0;

          for (Entry entry : validKits.entrySet()) {
            totalKitCount += ((List) entry.getValue()).size();
          }

          cmiPlaceHolders.addCachedValue(player.getUniqueId(), String.valueOf(totalKitCount), 5000);
          return String.valueOf(totalKitCount);
        case cmi_user_kit_available_$1:
          List<String> kitAvailableArgs = cmiPlaceHolders.getComplexValues(string);
          if (kitAvailableArgs.isEmpty()) {
            return "";
          }

          Kit availableKit =
              this.plugin
                  .getKitsManager()
                  .getKit(cmiUser.getPlayer(), (String) kitAvailableArgs.get(0), true);
          if (availableKit != null) {
            if (cmiUser.getLeftKitUseTimes(availableKit) == 0
                && !PermissionsManager.CMIPerm.kit_bypass_onetimeuse.hasPermission(player, 5000L)) {
              return variable(false);
            }

            return variable(cmiUser.canUseKit(availableKit));
          }

          return variable(false);
        case cmi_user_kit_hasaccess_$1:
          List<String> kitAccessArgs = cmiPlaceHolders.getComplexValues(string);
          if (kitAccessArgs.isEmpty()) {
            return "";
          }

          Kit accessKit =
              this.plugin
                  .getKitsManager()
                  .getKit(cmiUser.getPlayer(), (String) kitAccessArgs.get(0), false);
          return variable(accessKit != null);
      }
    }

    if (player != null) {
      switch (cmiPlaceHolders) {
        case cmi_user_sneaking:
          return variable(player.isSneaking());
        case cmi_user_maxhomes:
          return String.valueOf(this.plugin.getHomeManager().getMaxHomes(player));
        case cmi_user_ping:
          return String.valueOf(this.plugin.getReflectionManager().getPing(player));
        case cmi_user_gamemode:
          return CMIText.firstToUpperCase(player.getGameMode().toString());
        case cmi_user_op:
          return variable(player.isOp());
        case cmi_user_canfly:
          return variable(player.getAllowFlight());
        case cmi_user_flying:
          return variable(player.isFlying());
        case cmi_user_itemcount_$1:
          List<String> itemCountArgs = cmiPlaceHolders.getComplexValues(string);
          if (itemCountArgs.isEmpty()) {
            return "";
          }

          CMIMaterial targetMaterial = CMIMaterial.get((String) itemCountArgs.get(0));
          int itemCount = 0;
          ItemStack[] itemStacks;
          int slotCount = (itemStacks = player.getInventory().getContents()).length;

          for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
            ItemStack itemStack = itemStacks[slotIndex];
            if (itemStack != null
                && (targetMaterial == CMIMaterial.NONE
                    || CMIMaterial.get(itemStack) == targetMaterial)) {
              itemCount += itemStack.getAmount();
            }
          }

          return String.valueOf(itemCount);
        case cmi_user_maxperm_$1_$2:
          List<String> maxPermArgs = cmiPlaceHolders.getComplexValues(string);
          if (maxPermArgs.size() < 2) {
            return "";
          }

          try {
            String permissionName = (String) maxPermArgs.get(0);
            Integer configuredValue = Integer.parseInt((String) maxPermArgs.get(1));
            Integer effectiveMax = configuredValue;
            PermissionInfo permissionInfo =
                this.plugin.getPermissionsManager().getPermissionInfo(player, permissionName);
            if (permissionInfo.getMaxValue() != null) {
              effectiveMax =
                  permissionInfo.getMaxValue().intValue() > effectiveMax
                      ? permissionInfo.getMaxValue().intValue()
                      : effectiveMax;
            }

            return String.valueOf(effectiveMax);
          } catch (Exception exception2) {
            exception2.printStackTrace();
            return "";
          }
        case cmi_iteminhand_displayname:
          CMIItemStack handDisplayItem =
              CMILib.getInstance().getItemManager().getItem(getHeldItemOrAir(player));
          return handDisplayItem != null ? handDisplayItem.getDisplayName() : "N/A";
        case cmi_iteminhand_realname:
          CMIItemStack handRealItem =
              CMILib.getInstance().getItemManager().getItem(getHeldItemOrAir(player));
          return handRealItem != null ? CMIChatColor.translate(handRealItem.getRealName()) : "N/A";
        case cmi_iteminhand_type:
          CMIItemStack handTypeItem =
              CMILib.getInstance().getItemManager().getItem(getHeldItemOrAir(player));
          return handTypeItem != null ? handTypeItem.getType().name() : "AIR";
        case cmi_iteminhand_itemdata:
          CMIItemStack handDataItem =
              CMILib.getInstance().getItemManager().getItem(getHeldItemOrAir(player));
          return handDataItem != null ? String.valueOf(handDataItem.getData()) : "";
        case cmi_iteminhand_amount:
          ItemStack heldAmountItem = CMIItemStack.getItemInMainHand(player);
          if (heldAmountItem == null) {
            return String.valueOf(0);
          }

          heldAmountItem = heldAmountItem.clone();
          return String.valueOf(heldAmountItem.getAmount());
        case cmi_iteminhand_durability:
          CMIItemStack durabilityItem =
              CMILib.getInstance().getItemManager().getItem(CMIItemStack.getItemInMainHand(player));
          if (durabilityItem == null) {
            return String.valueOf(0);
          }

          return String.valueOf(durabilityItem.getMaxDurability() - durabilityItem.getDurability());
        case cmi_iteminhand_maxdurability:
          CMIItemStack maxDurabilityItem =
              CMILib.getInstance().getItemManager().getItem(CMIItemStack.getItemInMainHand(player));
          if (maxDurabilityItem == null) {
            return String.valueOf(0);
          }

          return String.valueOf(maxDurabilityItem.getMaxDurability());
        case cmi_iteminhand_custommodeldata:
          CMIItemStack customModelItem =
              CMILib.getInstance().getItemManager().getItem(CMIItemStack.getItemInMainHand(player));
          if (customModelItem == null) {
            return String.valueOf(0);
          }

          Integer customModelData = customModelItem.getCustomModelData();
          return String.valueOf(customModelData == null ? "" : customModelData);
        case cmi_iteminhand_worth:
          ItemStack heldWorthItem = getHeldItemOrAir(player);
          WorthItem worthItem = this.plugin.getWorthManager().getWorth(heldWorthItem);
          return worthItem == null
              ? CMILC.getIM("worth", "worthless")
              : this.plugin
                  .getEconomyManager()
                  .format(worthItem.getSellPrice() * heldWorthItem.getAmount());
        case cmi_iteminhand_worth_one:
          WorthItem worthOneItem = this.plugin.getWorthManager().getWorth(getHeldItemOrAir(player));
          return worthOneItem == null
              ? CMILC.getIM("worth", "worthless")
              : this.plugin.getEconomyManager().format(worthOneItem.getSellPrice());
        case cmi_iteminhand_worthc:
          ItemStack heldWorthcItem = getHeldItemOrAir(player);
          WorthItem worthcItem = this.plugin.getWorthManager().getWorth(heldWorthcItem);
          return worthcItem == null
              ? String.valueOf(0)
              : String.valueOf(worthcItem.getSellPrice() * heldWorthcItem.getAmount());
        case cmi_iteminhand_worthc_one:
          WorthItem worthcOneItem =
              this.plugin.getWorthManager().getWorth(getHeldItemOrAir(player));
          return worthcOneItem == null
              ? String.valueOf(0)
              : String.valueOf(worthcOneItem.getSellPrice());
        case player_world:
        case cmi_player_world:
          return player.getWorld().getName();
        case player_x:
        case cmi_player_x:
          return String.valueOf(player.getLocation().getBlockX());
        case player_y:
        case cmi_player_y:
          return String.valueOf(player.getLocation().getBlockY());
        case player_z:
        case cmi_player_z:
          return String.valueOf(player.getLocation().getBlockZ());
        case player_biome:
        case cmi_player_biome:
          return String.valueOf(CMIBiome.getName(player.getLocation().getBlock().getBiome()));
      }
    }

    switch (cmiPlaceHolders) {
      case cmi_equation_$1:
        List<String> equationArgs = cmiPlaceHolders.getComplexValues(string);
        if (equationArgs.isEmpty()) {
          return "";
        } else {
          try {
            Parser equationParser = new Parser((String) equationArgs.get(0));
            return String.valueOf((int) (equationParser.getValue() * 100.0) / 100.0);
          } catch (Throwable throwable) {
            CMIMessages.consoleMessage(
                "&e[CMI] Invalid equation placeholder  "
                    + cmiPlaceHolders.getFull()
                    + " -> "
                    + (String) equationArgs.get(0));
            return "";
          }
        }
      case cmi_equationint_$1:
        List<String> equationIntArgs = cmiPlaceHolders.getComplexValues(string);
        if (equationIntArgs.isEmpty()) {
          return "";
        } else {
          try {
            Parser equationIntParser = new Parser((String) equationIntArgs.get(0));
            return String.valueOf((int) equationIntParser.getValue());
          } catch (Throwable throwable2) {
            CMIMessages.consoleMessage(
                "&e[CMI] Invalid equation placeholder  "
                    + cmiPlaceHolders.getFull()
                    + " -> "
                    + (String) equationIntArgs.get(0));
            throwable2.printStackTrace();
            return "";
          }
        }
      case cmi_color_$1:
        List<String> colorArgs = cmiPlaceHolders.getComplexValues(string);
        if (colorArgs.isEmpty()) {
          return "";
        } else {
          try {
            return CMIChatColor.translate(
                CMIText.replaceUnderScoreSpace((String) colorArgs.get(0)));
          } catch (Exception exception3) {
            exception3.printStackTrace();
            return "";
          }
        }
      case cmi_stripcolor_$1:
        List<String> stripArgs = cmiPlaceHolders.getComplexValues(string);
        if (stripArgs.isEmpty()) {
          return "";
        } else {
          try {
            return CMIChatColor.stripColor((String) stripArgs.get(0));
          } catch (Exception exception4) {
            exception4.printStackTrace();
            return "";
          }
        }
      case cmi_iteminhand_displayname:
      case cmi_iteminhand_realname:
      case cmi_iteminhand_type:
      case cmi_iteminhand_itemdata:
      case cmi_iteminhand_amount:
      case cmi_iteminhand_durability:
      case cmi_iteminhand_maxdurability:
      case cmi_iteminhand_custommodeldata:
      case cmi_iteminhand_worth:
      case cmi_iteminhand_worth_one:
      case cmi_iteminhand_worthc:
      case cmi_iteminhand_worthc_one:
      case cmi_user_rank_percent_$1:
      case cmi_user_meta_$1:
      case cmi_user_metaint_$1:
      case cmi_user_baltop:
      case cmi_user_playtimetop:
      case cmi_user_stats_$1:
      case cmi_user_kitcd_$1:
      case cmi_user_kit_available:
      case cmi_user_kit_available_$1:
      case cmi_user_kit_hasaccess_$1:
      case player_world:
      case cmi_player_world:
      case player_x:
      case cmi_player_x:
      case player_y:
      case cmi_player_y:
      case player_z:
      case cmi_player_z:
      case player_biome:
      case cmi_player_biome:
      case vault_eco_balance_formatted:
      default:
        return null;
      case cmi_material_realname_$1:
        List<String> materialArgs = cmiPlaceHolders.getComplexValues(string);
        if (materialArgs.isEmpty()) {
          return "";
        } else {
          CMIMaterial realNameMaterial = CMIMaterial.get((String) materialArgs.get(0));
          if (realNameMaterial == null) {
            return "N/A";
          }

          return realNameMaterial.getTranslatedName();
        }
      case cmi_schedule_nextin_$1:
        List<String> nextInArgs = cmiPlaceHolders.getComplexValues(string);
        if (nextInArgs.isEmpty()) {
          return "";
        } else {
          try {
            Schedule schedule =
                this.plugin.getSchedulerManager().getSchedule((String) nextInArgs.get(0), false);
            if (schedule == null) {
              return "";
            } else {
              long millisLeft = schedule.getPerformOn() - System.currentTimeMillis();
              if (millisLeft < 0L) {
                return "";
              }

              return CMITimeManager.to24hourShort(millisLeft);
            }
          } catch (Exception exception5) {
            exception5.printStackTrace();
            return "";
          }
        }
      case cmi_schedule_endat_$1:
        List<String> endAtArgs = cmiPlaceHolders.getComplexValues(string);
        if (endAtArgs.isEmpty()) {
          return "";
        } else {
          try {
            Schedule endSchedule =
                this.plugin.getSchedulerManager().getSchedule((String) endAtArgs.get(0), false);
            if (endSchedule == null) {
              return "";
            } else {
              long remainingMillis = endSchedule.getPerformOn() - System.currentTimeMillis();
              if (remainingMillis < 0L) {
                return "";
              } else {
                double remainingExact =
                    endSchedule.getLastPerformedOn().longValue()
                        + endSchedule.getTotalCommandDelay() * 1000.0
                        - System.currentTimeMillis();
                if (remainingExact < 0.0) {
                  return "";
                }

                remainingMillis = (long) remainingExact;
                return CMITimeManager.to24hourShort(remainingMillis);
              }
            }
          } catch (Exception exception6) {
            exception6.printStackTrace();
            return "";
          }
        }
      case cmi_baltop_cname_$1:
      case cmi_baltop_name_$1:
        List<String> baltopNameArgs = cmiPlaceHolders.getComplexValues(string);
        if (baltopNameArgs.isEmpty()) {
          return "";
        } else {
          try {
            int leaderboardPlace = Integer.parseInt((String) baltopNameArgs.get(0));
            var placeEntry =
                this.plugin.getEconomyManager().getLeaderboard().getByPlace(leaderboardPlace);
            if (this.plugin.getEconomyManager().isRecalculatingTop() == null
                || placeEntry == null) {
              return "";
            }

            CMIUser placeUser = this.plugin.getPlayerManager().getUser((UUID) placeEntry.getKey());
            if (placeUser != null) {
              return cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_baltop_name_$1)
                  ? placeUser.getDisplayName(false)
                  : placeUser.getName();
            }
          } catch (Exception exception7) {
            exception7.printStackTrace();
          }

          return "";
        }
      case cmi_baltop_money_$1:
      case cmi_baltop_shortmoney_$1:
        List<String> baltopMoneyArgs = cmiPlaceHolders.getComplexValues(string);
        if (baltopMoneyArgs.isEmpty()) {
          return "0";
        } else {
          try {
            int leaderboardPlaceValue = Integer.parseInt((String) baltopMoneyArgs.get(0));
            var moneyEntry =
                this.plugin.getEconomyManager().getLeaderboard().getByPlace(leaderboardPlaceValue);
            if (this.plugin.getEconomyManager().isRecalculatingTop() != null
                && moneyEntry != null) {
              cmiUser = CMIUser.getUser((UUID) moneyEntry.getKey());
              if (cmiPlaceHolders == Placeholder.CMIPlaceHolders.cmi_baltop_shortmoney_$1) {
                return cmiUser.getEconomyAccount().getFormatedBalance(null, true);
              }

              return this.plugin.getEconomyManager().format((Double) moneyEntry.getValue());
            }

            return "0";
          } catch (Exception exception8) {
            this.informFailed(string);
            return "";
          }
        }
      case cmi_playtimetop_cname_$1:
      case cmi_playtimetop_name_$1:
        List<String> playtimeArgs = cmiPlaceHolders.getComplexValues(string);
        if (playtimeArgs.isEmpty()) {
          return "";
        } else {
          try {
            if (!this.plugin.getStatsManager().isDataLoaded()) {
              this.plugin.getStatsManager().loadStats();
              return "";
            } else {
              int playtimePlace = Integer.parseInt((String) playtimeArgs.get(0));
              var playtimeEntry =
                  this.plugin.getStatsManager().getLeaderboard().getByPlace(playtimePlace);
              if (playtimeEntry == null) {
                return "";
              } else {
                CMIUser playtimeUser = CMIUser.getUser((UUID) playtimeEntry.getKey());
                if (playtimeUser == null) {
                  return "";
                }

                return cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_playtimetop_name_$1)
                    ? playtimeUser.getDisplayName(false)
                    : playtimeUser.getName();
              }
            }
          } catch (Exception exception9) {
            exception9.printStackTrace();
            return "";
          }
        }
      case cmi_playtimetop_time_$1:
        List<String> playtimeTimeArgs = cmiPlaceHolders.getComplexValues(string);
        if (playtimeTimeArgs.isEmpty()) {
          return "";
        } else if (!this.plugin.getStatsManager().isDataLoaded()) {
          this.plugin.getStatsManager().loadStats();
          return "";
        } else {
          try {
            int playtimePlaceValue = Integer.parseInt((String) playtimeTimeArgs.get(0));
            var playtimeTimeEntry =
                this.plugin.getStatsManager().getLeaderboard().getByPlace(playtimePlaceValue);
            if (playtimeTimeEntry == null) {
              return "";
            } else {
              Long playtimeMillis = (Long) playtimeTimeEntry.getValue();
              if (playtimeMillis == null) {
                return "";
              }

              return CMIPlayTimeFormat.formatPlaytimePlaceholder(playtimeMillis);
            }
          } catch (Exception exception10) {
            this.informFailed(string);
            exception10.printStackTrace();
            return "";
          }
        }
      case cmi_votetop_$1:
        List<String> votetopArgs = cmiPlaceHolders.getComplexValues(string);
        if (votetopArgs.isEmpty()) {
          return "";
        } else {
          try {
            int votePlace = Integer.parseInt((String) votetopArgs.get(0));
            var sortedVotes = new TreeMap<>(this.plugin.getVotifierManager().getTop());
            int voteRank = 0;

            for (Entry voteEntry : sortedVotes.entrySet()) {
              if (((CMIUser) voteEntry.getValue()).getName(false) != null) {
                if (++voteRank == votePlace) {
                  return ((CMIUser) voteEntry.getValue()).getDisplayName(false);
                }

                if (voteRank > votePlace) {
                  break;
                }
              }
            }
          } catch (Exception exception11) {
            this.informFailed(string);
          }

          return "";
        }
      case cmi_votetopcount_$1:
        List<String> votetopCountArgs = cmiPlaceHolders.getComplexValues(string);
        if (votetopCountArgs.isEmpty()) {
          return "";
        } else {
          try {
            int votePlaceValue = Integer.parseInt((String) votetopCountArgs.get(0));
            var sortedVotesValue = new TreeMap<>(this.plugin.getVotifierManager().getTop());
            int voteRankValue = 0;

            for (Entry voteCountEntry : sortedVotesValue.entrySet()) {
              if (((CMIUser) voteCountEntry.getValue()).getName(false) != null) {
                if (++voteRankValue == votePlaceValue) {
                  return String.valueOf(((Double) voteCountEntry.getKey()).intValue());
                }

                if (voteRankValue > votePlaceValue) {
                  break;
                }
              }
            }
          } catch (Exception exception12) {
            this.informFailed(string);
          }

          return "";
        }
      case cmi_worth_buy_$1:
      case cmi_worthc_buy_$1:
        List<String> worthBuyArgs = cmiPlaceHolders.getComplexValues(string);
        if (worthBuyArgs.isEmpty()) {
          return "";
        } else {
          try {
            CMIItemStack buyItem = CMIItemStack.deserialize((String) worthBuyArgs.get(0));
            if (buyItem == null) {
              return "";
            } else {
              WorthItem buyWorth = this.plugin.getWorthManager().getWorth(buyItem.getItemStack());
              if (buyWorth == null) {
                return String.valueOf(0);
              } else {
                if (cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_worthc_buy_$1)) {
                  return String.valueOf(buyWorth.getBuyPrice() * buyItem.getAmount());
                }

                return this.plugin
                    .getEconomyManager()
                    .format(buyWorth.getBuyPrice() * buyItem.getAmount());
              }
            }
          } catch (Exception exception13) {
            CMIMessages.consoleMessage(
                "&e[CMI] Invalid equation placeholder  "
                    + cmiPlaceHolders.getFull()
                    + " -> "
                    + (String) worthBuyArgs.get(0));
            exception13.printStackTrace();
            return "";
          }
        }
      case cmi_worth_sell_$1:
      case cmi_worthc_sell_$1:
        List<String> worthSellArgs = cmiPlaceHolders.getComplexValues(string);
        if (worthSellArgs.size() < 1) {
          return "";
        } else {
          try {
            CMIItemStack sellItem = CMIItemStack.deserialize((String) worthSellArgs.get(0));
            if (sellItem == null) {
              return "";
            } else {
              WorthItem sellWorth = this.plugin.getWorthManager().getWorth(sellItem.getItemStack());
              if (sellWorth == null) {
                return "";
              } else {
                if (cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_worthc_sell_$1)) {
                  return String.valueOf(sellWorth.getSellPrice() * sellItem.getAmount());
                }

                return this.plugin
                    .getEconomyManager()
                    .format(sellWorth.getSellPrice() * sellItem.getAmount());
              }
            }
          } catch (Exception exception14) {
            exception14.printStackTrace();
            return "";
          }
        }
      case cmi_bungee_total_$1:
        List<String> bungeeTotalArgs = cmiPlaceHolders.getComplexValues(string);
        if (bungeeTotalArgs.isEmpty()) {
          return "0";
        } else {
          try {
            String serverName = (String) bungeeTotalArgs.get(0);
            BungeeCordServer totalServer = this.plugin.getBungeeCordManager().getServer(serverName);
            if (totalServer == null) {
              return "0";
            }

            totalServer.update();
            return String.valueOf(totalServer.getMaxPlayers());
          } catch (Exception exception15) {
            this.informFailed(string);
            return "0";
          }
        }
      case cmi_bungee_current_$1:
        List<String> bungeeCurrentArgs = cmiPlaceHolders.getComplexValues(string);
        if (bungeeCurrentArgs.size() < 1) {
          return "0";
        } else {
          try {
            String serverNameValue = (String) bungeeCurrentArgs.get(0);
            BungeeCordServer currentServer =
                this.plugin.getBungeeCordManager().getServer(serverNameValue);
            if (currentServer == null) {
              return "0";
            } else {
              currentServer.update();
              if (!currentServer.isOnline()) {
                return "0";
              }

              return String.valueOf(currentServer.getCurrentPlayers());
            }
          } catch (Exception exception16) {
            exception16.printStackTrace();
            return "0";
          }
        }
      case cmi_bungee_motd_$1:
        List<String> bungeeMotdArgs = cmiPlaceHolders.getComplexValues(string);
        if (bungeeMotdArgs.size() < 1) {
          return "";
        } else {
          try {
            String serverNameSecond = (String) bungeeMotdArgs.get(0);
            BungeeCordServer motdServer =
                this.plugin.getBungeeCordManager().getServer(serverNameSecond);
            if (motdServer == null) {
              return "";
            }

            motdServer.update();
            return motdServer.getMotd();
          } catch (Exception exception17) {
            exception17.printStackTrace();
            return "";
          }
        }
      case cmi_bungee_onlinestatus_$1:
        List<String> bungeeStatusArgs = cmiPlaceHolders.getComplexValues(string);
        if (bungeeStatusArgs.size() < 1) {
          return "";
        } else {
          try {
            String serverNameThird = (String) bungeeStatusArgs.get(0);
            BungeeCordServer statusServer =
                this.plugin.getBungeeCordManager().getServer(serverNameThird);
            if (statusServer == null) {
              return "";
            }

            statusServer.update();
            return statusServer.isOnline()
                ? LC.info_bungee_Online.getLocale(new Object[0])
                : LC.info_bungee_Offline.getLocale(new Object[0]);
          } catch (Exception exception18) {
            exception18.printStackTrace();
            return "";
          }
        }
      case cmi_tps_1:
        return String.valueOf((int) (this.plugin.getLagMeter().getTPS(20) * 10.0) / 10.0);
      case cmi_tps_60:
        return String.valueOf((int) (this.plugin.getLagMeter().getTPS(1200) * 10.0) / 10.0);
      case cmi_tps_300:
        return String.valueOf((int) (this.plugin.getLagMeter().getTPS(12000) * 10.0) / 10.0);
      case cmi_tps_$1_colored:
      case cmi_tps_$1:
        List<String> tpsArgs = cmiPlaceHolders.getComplexValues(string);
        if (tpsArgs.isEmpty()) {
          return "";
        } else {
          try {
            Integer tpsSeconds = Integer.parseInt((String) tpsArgs.get(0));
            double tpsValue = this.plugin.getLagMeter().getTPS(tpsSeconds * 20);
            if (cmiPlaceHolders.equals(Placeholder.CMIPlaceHolders.cmi_tps_$1_colored)) {
              return this.plugin.getUtilManager().getTpsColor(tpsValue).getBukkitColorCode()
                  + String.valueOf((int) (tpsValue * 10.0) / 10.0);
            }

            return String.valueOf((int) (tpsValue * 10.0) / 10.0);
          } catch (Exception exception19) {
            exception19.printStackTrace();
            return "";
          }
        }
      case cmi_random_player_name:
        Player randomPlayer = this.plugin.getUtilManager().getRandomOnlinePlayer();
        this.lastRandomPlayer = randomPlayer;
        return randomPlayer == null ? "" : randomPlayer.getName();
      case cmi_lastrandom_player_name:
        return this.lastRandomPlayer == null ? "" : this.lastRandomPlayer.getName();
      case cmi_random_$1_$2:
        List<String> randomRangeArgs = cmiPlaceHolders.getComplexValues(string);
        if (randomRangeArgs.size() < 2) {
          return "";
        } else {
          try {
            int rangeMin = Integer.parseInt((String) randomRangeArgs.get(0));
            int rangeMax = Integer.parseInt((String) randomRangeArgs.get(1));
            int rangeSize = rangeMax - rangeMin + 1;
            rangeSize = rangeSize < 0 ? -rangeSize : rangeSize;
            int randomOffset = random.nextInt(rangeSize);
            String randomResult = String.valueOf(randomOffset + rangeMin);
            this.randomCache.put(
                cmiUser != null && cmiUser.getName(false) != null
                    ? cmiUser.getName(false).toLowerCase()
                    : "server",
                randomResult);
            return String.valueOf(randomResult);
          } catch (Exception exception20) {
            return "";
          }
        }
      case cmi_lastrandom_$1:
        List<String> lastRandomArgs = cmiPlaceHolders.getComplexValues(string);
        if (lastRandomArgs.size() < 1) {
          return "";
        }

        String lastRandomValue =
            this.randomCache.get(((String) lastRandomArgs.get(0)).toLowerCase());
        return lastRandomValue == null ? "" : lastRandomValue;
      case cmi_chatmute_time:
        if (this.plugin.getChatFormatManager().getChatMutedUntil() == null) {
          return "";
        } else {
          long muteMillisLeft =
              this.plugin.getChatFormatManager().getChatMutedUntil() - System.currentTimeMillis();
          if (muteMillisLeft < 0L) {
            return "";
          }

          return CMITimeManager.to24hourShort(muteMillisLeft);
        }
      case cmi_chatmute_reason:
        if (this.plugin.getChatFormatManager().getChatMutedUntil() == null) {
          return "";
        } else {
          long reasonMillisLeft =
              this.plugin.getChatFormatManager().getChatMutedUntil() - System.currentTimeMillis();
          if (reasonMillisLeft < 0L) {
            return "";
          } else {
            String muteReason = this.plugin.getChatFormatManager().getChatMutedReason();
            if (muteReason == null) {
              return "";
            }

            return muteReason;
          }
        }
      case cmi_jail_time_$1_$2:
        List<String> jailTimeArgs = cmiPlaceHolders.getComplexValues(string);
        if (jailTimeArgs.size() < 2) {
          return "";
        } else {
          String jailName = (String) jailTimeArgs.get(0);
          String jailCellText = (String) jailTimeArgs.get(1);
          CMIJail jail = this.plugin.getJailManager().getByName(jailName);
          if (jail == null) {
            return "";
          } else {
            int cellId = 0;

            try {
              cellId = Integer.parseInt(jailCellText);
            } catch (Exception exception21) {
              return "";
            }

            CMIJailCell jailCell = jail.getCells().get(cellId);
            if (jailCell != null && !jailCell.getJailed().isEmpty()) {
              UUID jailedUuid = null;
              if (this.plugin.getJailManager().anyJailedOnline()) {
                for (UUID candidateUuid : jailCell.getJailed()) {
                  cmiUser = CMIUser.getUser(candidateUuid);
                  if (this.plugin.getJailManager().isOnlineAndInJail(cmiUser)) {
                    jailedUuid = candidateUuid;
                  }
                }
              }

              if (jailedUuid == null) {
                return "";
              }

              CMIUser jailedUser = this.plugin.getPlayerManager().getUser(jailedUuid);
              if (!jailedUser.isJailed()) {
                return "";
              }

              return CMITimeManager.to24hourShort(
                  jailedUser.getJailedUntil() - System.currentTimeMillis());
            }

            return "";
          }
        }
      case cmi_jail_username_$1_$2:
        List<String> jailUserArgs = cmiPlaceHolders.getComplexValues(string);
        if (jailUserArgs.size() < 2) {
          return "";
        } else {
          String jailNameValue = (String) jailUserArgs.get(0);
          String jailCellTextValue = (String) jailUserArgs.get(1);
          CMIJail jailValue = this.plugin.getJailManager().getByName(jailNameValue);
          if (jailValue == null) {
            return "";
          } else {
            int cellIdValue = 0;

            try {
              cellIdValue = Integer.parseInt(jailCellTextValue);
            } catch (Exception exception22) {
              return "";
            }

            CMIJailCell jailCellValue = jailValue.getCells().get(cellIdValue);
            if (jailCellValue != null && !jailCellValue.getJailed().isEmpty()) {
              UUID jailedUuidValue = null;
              if (this.plugin.getJailManager().anyJailedOnline()) {
                for (UUID candidateUuidValue : jailCellValue.getJailed()) {
                  cmiUser = CMIUser.getUser(candidateUuidValue);
                  if (this.plugin.getJailManager().isOnlineAndInJail(cmiUser)) {
                    jailedUuidValue = candidateUuidValue;
                  }
                }
              }

              if (jailedUuidValue == null) {
                return "";
              }

              CMIUser jailedUserValue = this.plugin.getPlayerManager().getUser(jailedUuidValue);
              if (!jailedUserValue.isJailed()) {
                return "";
              }

              return jailedUserValue.getDisplayName(false);
            }

            return "";
          }
        }
      case cmi_jail_reason_$1_$2:
        List<String> jailReasonArgs = cmiPlaceHolders.getComplexValues(string);
        if (jailReasonArgs.size() < 2) {
          return "";
        } else {
          String jailNameSecond = (String) jailReasonArgs.get(0);
          String jailCellTextSecond = (String) jailReasonArgs.get(1);
          CMIJail jailSecond = this.plugin.getJailManager().getByName(jailNameSecond);
          if (jailSecond == null) {
            return "";
          } else {
            int cellIdSecond = 0;

            try {
              cellIdSecond = Integer.parseInt(jailCellTextSecond);
            } catch (Exception exception23) {
              return "";
            }

            CMIJailCell jailCellSecond = jailSecond.getCells().get(cellIdSecond);
            if (jailCellSecond != null && !jailCellSecond.getJailed().isEmpty()) {
              UUID jailedUuidSecond = null;
              if (this.plugin.getJailManager().anyJailedOnline()) {
                for (UUID candidateUuidSecond : jailCellSecond.getJailed()) {
                  cmiUser = CMIUser.getUser(candidateUuidSecond);
                  if (this.plugin.getJailManager().isOnlineAndInJail(cmiUser)) {
                    jailedUuidSecond = candidateUuidSecond;
                  }
                }
              }

              if (jailedUuidSecond == null) {
                return "";
              }

              CMIUser jailedUserSecond = this.plugin.getPlayerManager().getUser(jailedUuidSecond);
              if (!jailedUserSecond.isJailed()) {
                return "";
              }

              return jailedUserSecond.getJailedReason() == null
                  ? ""
                  : jailedUserSecond.getJailedReason();
            }

            return "";
          }
        }
      case cmi_weather_$1:
        List<String> weatherArgs = cmiPlaceHolders.getComplexValues(string);
        if (weatherArgs.isEmpty()) {
          return "";
        } else {
          World weatherWorld = this.plugin.getUtilManager().getWorld((String) weatherArgs.get(0));
          if (weatherWorld == null) {
            return "";
          }

          String weatherText =
              weatherWorld.hasStorm()
                  ? CMILC.getIM(weather.class, "rainy")
                  : CMILC.getIM(weather.class, "sunny");
          if (weatherWorld.isThundering()) {
            weatherText = CMILC.getIM(weather.class, "thundering");
          }

          return weatherText;
        }
      case cmi_weatherduration_$1:
        List<String> weatherDurationArgs = cmiPlaceHolders.getComplexValues(string);
        if (weatherDurationArgs.isEmpty()) {
          return "";
        } else {
          World durationWorld =
              this.plugin.getUtilManager().getWorld((String) weatherDurationArgs.get(0));
          if (durationWorld == null) {
            return "";
          }

          long durationTicks = durationWorld.getWeatherDuration();
          if (durationWorld.isThundering()) {
            durationTicks = durationWorld.getThunderDuration();
          }

          return CMITimeManager.to24hourShort(durationTicks * 50L);
        }
      case cmi_afk_count:
        return String.valueOf(this.plugin.getAfkManager().getAfkPlayerCount());
      case cmi_maintenance_state:
        return variable(this.plugin.getConfigManager().isMaintenance());
      case cmi_maintenance_message:
        return this.plugin.getConfigManager().getMaintenanceMessage() == null
            ? ""
            : CMIChatColor.translate(this.plugin.getConfigManager().getMaintenanceMessage());
      case cmi_chat_range:
        int chatRange = this.plugin.getChatFormatManager().getChatGeneralRange(null);
        if (chatRange == 0) {
          return "9999+";
        } else {
          if (chatRange < 0) {
            return "-";
          }

          return String.valueOf(chatRange);
        }
      case cmi_server_uptime:
        return CMITimeManager.to24hourShort(
            System.currentTimeMillis() - CMI.getInstance().getServerStartupTime());
      case cmi_server_uptime_seconds:
        return String.valueOf(
            (System.currentTimeMillis() - CMI.getInstance().getServerStartupTime()) / 1000L);
      case cmi_server_worlds:
        StringBuilder worldNames = new StringBuilder();

        for (World serverWorld : Bukkit.getWorlds()) {
          if (!worldNames.toString().isEmpty()) {
            worldNames.append(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          worldNames.append(serverWorld.getName());
        }

        return worldNames.toString();
      case cmi_server_vanished:
        return String.valueOf(this.plugin.getVanishManager().getVanishedOnlineList().size());
      case cmi_server_users:
        return String.valueOf(this.plugin.getPlayerManager().getTotalUserCount());
      case server_online:
      case cmi_server_online:
        int onlineCount = 0;

        for (Player visiblePlayer : Bukkit.getOnlinePlayers()) {
          if ((player == null || player.canSee(visiblePlayer))
              && !this.plugin.getPlayerManager().getUser(visiblePlayer).isVanished()) {
            onlineCount++;
          }
        }

        return String.valueOf(onlineCount);
      case server_max_players:
      case cmi_server_max_players:
        return String.valueOf(Bukkit.getMaxPlayers());
      case server_online_$1:
      case cmi_server_online_$1:
        int worldOnlineCount = 0;
        List<String> worldOnlineArgs = cmiPlaceHolders.getComplexValues(string);
        if (worldOnlineArgs.isEmpty()) {
          return "";
        } else {
          String worldName = (String) worldOnlineArgs.get(0);

          for (Player worldPlayer : Bukkit.getOnlinePlayers()) {
            if ((player == null || player.canSee(worldPlayer))
                && !this.plugin.getPlayerManager().getUser(worldPlayer).isVanished()
                && worldPlayer.getWorld().getName().replace("_", "").equalsIgnoreCase(worldName)) {
              worldOnlineCount++;
            }
          }

          return String.valueOf(worldOnlineCount);
        }
      case server_unique_joins:
      case cmi_server_unique_joins:
        return String.valueOf(Bukkit.getOfflinePlayers().length);
      case onlineplayers_names:
      case cmi_onlineplayers_names:
        StringBuilder onlineNames = new StringBuilder();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          if ((player == null || player.canSee(onlinePlayer))
              && !CMIUser.getUser(onlinePlayer).isVanished()) {
            if (!onlineNames.toString().isEmpty()) {
              onlineNames.append(LC.info_ListSpliter.getLocale(new Object[0]));
            }

            onlineNames.append(onlinePlayer.getName());
          }
        }

        return onlineNames.toString();
      case onlineplayers_displaynames:
      case cmi_onlineplayers_displaynames:
        StringBuilder displayNames = new StringBuilder();

        for (Player displayPlayer : Bukkit.getOnlinePlayers()) {
          if ((player == null || player.canSee(displayPlayer))
              && !this.plugin.getPlayerManager().getUser(displayPlayer).isVanished()) {
            if (!displayNames.toString().isEmpty()) {
              displayNames.append(
                  CMIChatColor.RESET + LC.info_ListSpliter.getLocale(new Object[0]));
            }

            if (displayNames.toString().isEmpty()) {
              displayNames.append(CMIChatColor.RESET);
            }

            CMIUser displayUser = CMIUser.getUser(displayPlayer);
            displayNames.append(displayUser.getDisplayName(false));
          }
        }

        return displayNames.toString();
      case server_time_$1_$2:
      case cmi_server_time_$1_$2:
        List<String> timeArgs = cmiPlaceHolders.getComplexValues(string);
        if (timeArgs.isEmpty()) {
          return "";
        } else if (timeArgs.size() < 2) {
          return "";
        } else {
          try {
            TimeZone timeZone = TimeZone.getTimeZone((String) timeArgs.get(1));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat((String) timeArgs.get(0));
            simpleDateFormat.setTimeZone(timeZone);
            return simpleDateFormat.format(Calendar.getInstance().getTime());
          } catch (Throwable throwable3) {
            throwable3.printStackTrace();
            return "";
          }
        }
      case server_time_$1:
      case cmi_server_time_$1:
        List<String> serverTimeArgs = cmiPlaceHolders.getComplexValues(string);
        if (serverTimeArgs.isEmpty()) {
          return "";
        }

        return new SimpleDateFormat((String) serverTimeArgs.get(0))
            .format(Calendar.getInstance().getTime());
      case world_time12_$1:
      case cmi_world_time12_$1:
        List<String> worldTime12Args = cmiPlaceHolders.getComplexValues(string);
        if (worldTime12Args.isEmpty()) {
          return "";
        } else {
          World worldTime = this.plugin.getUtilManager().getWorld((String) worldTime12Args.get(0));
          if (worldTime == null) {
            return "";
          }

          return CMITimeManager.to12hour(worldTime.getTime());
        }
      case world_time24_$1:
      case cmi_world_time24_$1:
        List<String> worldTime24Args = cmiPlaceHolders.getComplexValues(string);
        if (worldTime24Args.isEmpty()) {
          return "";
        } else {
          World worldTimeSecond =
              this.plugin.getUtilManager().getWorld((String) worldTime24Args.get(0));
          return worldTimeSecond == null ? "" : CMITimeManager.to24hour(worldTimeSecond.getTime());
        }
    }
  }

  private void informFailed(String text) {
    this.plugin.consoleMessage("&cInccorrect placeholder format for " + text);
  }

  private static String variable(Boolean flag) {
    return flag
        ? LC.info_variables_True.getLocale(new Object[0])
        : LC.info_variables_False.getLocale(new Object[0]);
  }

  public enum CMIPlaceHolders {
    cmi_user_charges_left("Remaining count of spawner charges"),
    cmi_user_charges_max("Maximum allowed amount of spawner charges"),
    cmi_user_charges_time("Time until next spawner charge"),
    cmi_user_charges_cooldown("Spawner charge cooldown"),
    cmi_user_display_name("Formatted players display name"),
    cmi_p_$1_display_name(cmi_user_display_name),
    cmi_user_cleannickname("Players nick name if set or players name otherwise. No color codes"),
    cmi_user_nickname("Players nick name if set or players name otherwise"),
    cmi_user_name("Original players name"),
    cmi_p_$1_name(cmi_user_name),
    cmi_user_uuid("Player uuid"),
    cmi_user_uuid_$1(cmi_user_uuid),
    cmi_user_deathloc("Players last death location"),
    cmi_user_backloc("Players back location"),
    cmi_user_cuffed("Identification if player is cuffed"),
    cmi_p_$1_cuffed(cmi_user_cuffed),
    cmi_user_muted("Identification if player is muted"),
    cmi_p_$1_muted(cmi_user_muted),
    cmi_user_inpvp("Identification if player is in pvp mode"),
    cmi_user_god("Identification if player has god mode enabled"),
    cmi_p_$1_god(cmi_user_god),
    cmi_user_sneaking("Identification if player is sneaking"),
    cmi_user_mail_count("Amount of mails player have"),
    cmi_user_warning_count("Amount of warnings player have"),
    cmi_user_warning_points("Amount of warning points player have"),
    cmi_user_afk("Afk state"),
    cmi_user_afk_symbol("Afk symbol"),
    cmi_user_afk_msg("Afk message if present"),
    cmi_user_afk_for("Time for how long player is in afk mode"),
    cmi_user_afk_in("Time when player enters auto afk mode"),
    cmi_user_spy("Spy state"),
    cmi_user_cmdspy("Command spy state"),
    cmi_user_signspy("Sign spy state"),
    cmi_user_joinedcounter("Indication if player is joined counter"),
    cmi_user_banned("Indication if player is banned"),
    cmi_p_$1_banned(cmi_user_banned),
    cmi_user_maxhomes("Max amount of homes player can have"),
    cmi_user_homeamount("Amount of homes player has"),
    cmi_user_homelist("List of players homes"),
    cmi_user_missingexp("Missing exp amount until next level"),
    cmi_user_missingexpp("Missing exp in percentage until next level"),
    cmi_user_exp("Current exp amount for current level"),
    cmi_user_expp("Current exp in percentage for current level"),
    cmi_user_totalexp("Total amount of exp player have"),
    cmi_user_level("Players level"),
    cmi_user_ping("Ping"),
    cmi_user_gamemode("Game mode"),
    cmi_user_op("OP state"),
    cmi_user_pweather("Player weather"),
    cmi_user_weather("Weather at players world"),
    cmi_user_weatherduration("Weather duration at players world"),
    cmi_user_canfly("Players ability to fly"),
    cmi_user_flying("Is player currently flying"),
    cmi_user_vanished_symbol("Vanish symbol"),
    cmi_user_balance_formated(true),
    cmi_user_balance_formatted("Formatted users balance"),
    cmi_user_balance("Clean users balance"),
    cmi_user_prefix("Players prefix set by permission plugin"),
    cmi_user_suffix("Players suffix set by permission plugin"),
    cmi_user_group("Players main permission group name"),
    cmi_user_nameplate_prefix("Players nameplate prefix"),
    cmi_user_nameplate_suffix("Players nameplate suffix"),
    cmi_user_tfly("Left temp fly amount in seconds"),
    cmi_user_tfly_formated(true),
    cmi_user_tfly_formatted("Formatted temp fly amount"),
    cmi_user_flightcharge("Flight charge amount"),
    cmi_user_tgod("Time in seconds for temp god mode"),
    cmi_user_tgod_formated(true),
    cmi_user_tgod_formatted("Formatted time for temp god mode"),
    cmi_user_votecount("Amount of votes"),
    cmi_user_dailyvotecount("Daily vote count"),
    cmi_user_chatcolor("Player chatcolor"),
    cmi_user_rank(true),
    cmi_user_rank_displayname("Current rank display name"),
    cmi_user_rank_name("Current rank name"),
    cmi_user_nextranks("List of next ranks"),
    cmi_user_nextrankpercent("Percentage done for next rank"),
    cmi_user_nextvalidranks("Rank list to which player can rank up to"),
    cmi_user_canrankup("Returns true if player can rank up"),
    cmi_user_country("Users country from geoip feature. Example: Germany"),
    cmi_user_country_code("Users country code from geoip feature. Example: UK"),
    cmi_user_city("Users city name from geoip feature"),
    cmi_user_name_colorcode("Bukkit color code from nameplate command and -c: variable"),
    cmi_user_glow_code("Bukkit color code from glow command."),
    cmi_user_glow_name("Color name from glow command. Example: Red"),
    cmi_user_jailed("True or false if player is jailed"),
    cmi_p_$1_jailed(cmi_user_jailed),
    cmi_user_jailname("Jail name user currently is in. Example: Prison"),
    cmi_p_$1_jailname(cmi_user_jailname),
    cmi_user_jailcell("Jail cell id user currently is in. Example: 1"),
    cmi_p_$1_jailcell(cmi_user_jailcell),
    cmi_user_jailtime("Left jail time. Example: 1hour 5minutes"),
    cmi_p_$1_jailtime(cmi_user_jailtime),
    cmi_user_jailreason("Jailed reason"),
    cmi_p_$1_jailreason(cmi_user_jailreason),
    cmi_user_jailedby("Jailer name"),
    cmi_p_$1_jailedby(cmi_user_jailedby),
    cmi_user_riding("Entity name player is riding"),
    cmi_user_beingriddenby("Player name who is riding user"),
    cmi_user_bungeeserver("Bungee server name"),
    cmi_user_rt_cooldown("Remaining cooldown on random teleportation"),
    cmi_user_rt_cooldown_$1("Remaining cooldown on random teleportation by world", "worldName"),
    cmi_user_playtime_formatted("Formatted playtime"),
    cmi_user_playtime_days("Playtime in days"),
    cmi_user_playtime_dayst("Playtime in days with fraction"),
    cmi_user_playtime_hours("Playtime in hours"),
    cmi_user_playtime_hoursf("Total playtime in hours"),
    cmi_user_playtime_hourst("Total playtime in hours with fraction"),
    cmi_user_playtime_minutes("Playtime in minutes"),
    cmi_user_playtime_minutest("Total playtime in minutes"),
    cmi_user_playtime_seconds("Playtime in minutes"),
    cmi_user_playtime_secondst("Total playtime in minutes"),
    cmi_user_prewards_count("Number of claimable prewards"),
    cmi_user_world("Current players world name"),
    cmi_user_world_formatted("Current players world name by using custom identification"),
    cmi_user_online("Returns player online status"),
    cmi_p_$1_online(cmi_user_online),
    cmi_user_itemcount_$1(
        "Number of items in players inventory by provided material", "itemIdName(:data)"),
    cmi_user_maxperm_$1_$2(
        "Maximum value by provided permission node, and if it doesn't exist, returns default value",
        "corePerm",
        "defaultValue"),
    cmi_user_toggle_$1(
        "Outputs 1 or 0 if defined feature is toggled on or off",
        "holograms|shiftedit|totembar|compass|tagsound|chatspy|cmdspy|signspy|msg|tp|pay|chatbubble|pmsound|rideme|pvenumbers|pvpnumbers|durability|receivepets|deathmessages|notarget|schest|staffchat|autoflightrecharge"),
    cmi_user_togglename_$1(
        "Outputs formatted True or False if defined feature is toggled on or off",
        "holograms|shiftedit|totembar|compass|tagsound|chatspy|cmdspy|signspy|msg|tp|pay|chatbubble|pmsound|rideme|pvenumbers|pvpnumbers|durability|receivepets|deathmessages|notarget|schest|staffchat|autoflightrecharge"),
    cmi_user_holo_page_$1(
        "Outputs page number of hologram player is in at the moment", "hologramName"),
    cmi_equation_$1("Result of provided mathematical equation with fraction", "equation"),
    cmi_equationint_$1("Result of provided mathematical equation without fraction", "equation"),
    cmi_color_$1(
        "Colorizes text, replace spaces with _, underscore can be added by doubling it like __",
        "text"),
    cmi_stripcolor_$1("Removes color codes from text", "text"),
    cmi_iteminhand_displayname("Items in main hand display name or formatted material name"),
    cmi_iteminhand_realname("Items in main hand formatted material name"),
    cmi_iteminhand_type("Items in main hand material name"),
    cmi_iteminhand_itemdata("Items in main hand data value. As of 1.13+ returns 0"),
    cmi_iteminhand_amount("Amount of items in main hand"),
    cmi_iteminhand_durability("Items in main hand left durability"),
    cmi_iteminhand_maxdurability("Items in main hand max durability"),
    cmi_iteminhand_custommodeldata("Items in main hand custom model data"),
    cmi_iteminhand_worth("Returns total worth value of items in main hand"),
    cmi_iteminhand_worth_one("Returns worth value of one item from main hand"),
    cmi_iteminhand_worthc("Returns total worth value of items in main hand without formatting"),
    cmi_iteminhand_worthc_one("Returns worth value of one item from main hand without formatting"),
    cmi_material_realname_$1("Items in main hand formatted material name"),
    cmi_schedule_nextin_$1("Left time until next schedule trigger", "schedName"),
    cmi_schedule_endat_$1("Left time until scheduler triggers last command", "schedName"),
    cmi_baltop_cname_$1("Clean name of player from a provided place in a list", "1-10"),
    cmi_baltop_name_$1("Name of player from a provided place in a list", "1-10"),
    cmi_baltop_money_$1("Balance of player from a provided place in a list", "1-10"),
    cmi_baltop_shortmoney_$1("Balance of player from a provided place in a list", "1-10"),
    cmi_playtimetop_cname_$1("Clean name of player from a provided place in a list", "1-10"),
    cmi_playtimetop_name_$1("Name of player from a provided place in a list", "1-10"),
    cmi_playtimetop_time_$1("Playtime of player from a provided place in a list", "1-10"),
    cmi_votetop_$1("Name of player from a provided place in a list", "1-10"),
    cmi_votetopcount_$1("Vote count of player from a provided place in a list", "1-10"),
    cmi_worth_buy_$1("Value of the item", "itemIdName(:data)"),
    cmi_worth_sell_$1("Sell value of the item", "itemIdName(:data)"),
    cmi_worthc_buy_$1("Value of the item without formatting", "itemIdName(:data)"),
    cmi_worthc_sell_$1("Sell value of the item without formatting", "itemIdName(:data)"),
    cmi_bungee_total_$1("Total allowed amount of players in defined server", "serverName"),
    cmi_bungee_current_$1("Current amount of players in defined server", "serverName"),
    cmi_bungee_motd_$1("Motd of defined server", "serverName"),
    cmi_bungee_onlinestatus_$1("True/false of servers online status", "serverName"),
    cmi_tps_1("Ttps from last 1 second"),
    cmi_tps_60("Tps from last 60 seconds"),
    cmi_tps_300("Tps from last 5 minutes"),
    cmi_tps_$1_colored("Tps from defined range", "range"),
    cmi_tps_$1("Tps from defined range", "range"),
    cmi_random_player_name("Returns random online player name", false),
    cmi_lastrandom_player_name("Returns last random online player name", false),
    cmi_random_$1_$2("Random number from defined range", false, "from", "to"),
    cmi_lastrandom_$1(
        "Last random number assigned to player from random placeholder", false, "playerName"),
    cmi_user_rank_percent_$1("Percentage of defined rank rankup progress", "rankName"),
    cmi_user_meta_$1("Players metadate by defined key", "key"),
    cmi_user_metaint_$1("Players metadate by defined key as number", "key"),
    cmi_chatmute_time("Provides left time for global chat mute"),
    cmi_chatmute_reason("Provides reason for global chat mute"),
    cmi_user_baltop("Player position in baltop"),
    cmi_user_playtimetop("Player position in playtimetop"),
    cmi_user_stats_$1("Player statistics", "mainStat(:optionalSubStat)"),
    cmi_user_kitcd_$1("Cooldown of defined kit", "kitName"),
    cmi_user_kit_available("Amount of kits you can claim"),
    cmi_user_kit_available_$1("True/false if kit is available", "kitName"),
    cmi_user_kit_hasaccess_$1("True/false if user can use this kit", "kitName"),
    cmi_jail_time_$1_$2("Time of a jail cell", "jailName", "cellId"),
    cmi_jail_username_$1_$2("Name of user who is jailed in particular cell", "jailName", "cellId"),
    cmi_jail_reason_$1_$2("Reason of particular jail cell", "jailName", "cellId"),
    cmi_weather_$1("World weather", "worldName"),
    cmi_weatherduration_$1("World weather duration", "worldName"),
    cmi_afk_count("Number of afk players"),
    cmi_maintenance_state("Maintenance state"),
    cmi_maintenance_message("Maintenance message"),
    cmi_chat_range("Range for chat messages"),
    cmi_server_uptime("How long server is running"),
    cmi_server_uptime_seconds("How long server is running in seconds"),
    cmi_server_worlds("List of all existing worlds on server in a list format"),
    cmi_server_vanished("Online vanished player count"),
    cmi_server_users("Recorder user count"),
    player_world("Current players world name", true),
    cmi_player_world("Current players world name"),
    player_x("Current players x position", true),
    cmi_player_x("Current players x position"),
    player_y("Current players y position", true),
    cmi_player_y("Current players y position"),
    player_z("Current players z position", true),
    cmi_player_z("Current players z position"),
    player_biome("Current players biome name", true),
    cmi_player_biome("Current players biome name"),
    vault_eco_balance_formatted("Formatted players balance. Deprecated", true),
    server_online("Online player amount", true),
    cmi_server_online("Online player count"),
    server_max_players("Max allowed players", true),
    cmi_server_max_players("Max allowed players"),
    server_online_$1(
        "Online amount in particular world. Don't use _ in world name", true, true, "worldName"),
    cmi_server_online_$1(
        "Online amount in particular world. Don't use _ in world name", "worldName"),
    server_unique_joins("Unique player joins to the server", true),
    cmi_server_unique_joins("Unique player joins to the server"),
    onlineplayers_names("Formatted list of online players", true),
    cmi_onlineplayers_names("Formatted list of online players"),
    onlineplayers_displaynames(
        "Formatted list of online players by using their display names if possible", true),
    cmi_onlineplayers_displaynames(
        "Formatted list of online players by using their display names if possible"),
    server_time_$1_$2(
        "Time of server by defined format and timezone", true, true, "timeFormat", "timeZone"),
    cmi_server_time_$1_$2(
        "Time of server by defined format and timezone", "timeFormat", "timeZone"),
    server_time_$1("Local server time", true, true, "timeFormat"),
    cmi_server_time_$1("Local server time", "timeFormat"),
    world_time12_$1("World time in 12 hour format", true, true, "worldName"),
    cmi_world_time12_$1("World time in 12 hour format", "worldName"),
    world_time24_$1("World time in 24 hour format", true, true, "worldName"),
    cmi_world_time24_$1("World time in 24 hour format", "worldName");

    static LinkedHashMap<String, Placeholder.CMIPlaceHolders> byNameStatic = new LinkedHashMap<>();
    static LinkedHashMap<String, LinkedHashSet<Placeholder.CMIPlaceHolders>> byNameComplex =
        new LinkedHashMap<>();
    private static int count = 0;
    private int MAX_ENTRIES = 20;
    LinkedHashMap<UUID, PlaceholderCache> map =
        new LinkedHashMap<UUID, PlaceholderCache>(this.MAX_ENTRIES + 1, 0.75F, false) {
          @Override
          protected boolean removeEldestEntry(Entry<UUID, PlaceholderCache> entry) {
            return this.size() > CMIPlaceHolders.this.MAX_ENTRIES;
          }
        };
    private String[] vars;
    private List<Integer> groups = new ArrayList<>();
    private ChatFilterRule rule = null;
    private boolean hidden = false;
    private boolean cache = true;
    private String desc = null;
    private Placeholder.CMIPlaceHolders userRedirect = null;

    static {
      Placeholder.CMIPlaceHolders[] constants;
      int constantCount = (constants = values()).length;

      for (int i = 0; i < constantCount; i++) {
        Placeholder.CMIPlaceHolders constant = constants[i];
        if (!constant.isHidden()) {
          count++;
        }

        String constantName = constant.toString();
        if (!constant.isComplex()) {
          byNameStatic.put(constantName.toLowerCase(), constant);
        } else {
          String[] parts = constantName.split("_");
          String prefix = parts[0] + "_" + parts[1];
          var group = byNameComplex.getOrDefault(prefix, new LinkedHashSet<>());
          group.add(constant);
          byNameComplex.put(prefix, group);
        }
      }
    }

    CMIPlaceHolders() {}

    CMIPlaceHolders(String description, boolean cache, String... vars) {
      this(description, false, cache, vars);
    }

    CMIPlaceHolders(boolean hidden) {
      this(null, hidden, true);
    }

    CMIPlaceHolders(String description, String... vars) {
      this(description, false, true, vars);
    }

    CMIPlaceHolders(Placeholder.CMIPlaceHolders redirect) {
      this(redirect.getDescription(), redirect, false, true, "playerName");
    }

    CMIPlaceHolders(String description, boolean hidden, boolean cache, String... vars) {
      this(description, null, hidden, cache, vars);
    }

    CMIPlaceHolders(
        String description,
        Placeholder.CMIPlaceHolders redirect,
        boolean hidden,
        boolean cache,
        String... vars) {
      this.desc = description;
      this.vars = vars;
      this.hidden = hidden;
      this.cache = cache;
      this.userRedirect = redirect;

      try {
        Matcher matcher = Placeholder.numericalRule.getMatcher(this.toString());
        if (matcher != null) {
          this.rule = new ChatFilterRule();
          ArrayList<String> patterns = new ArrayList();
          patterns.add("(%)" + this.toString().replaceAll("\\$\\d", "([^\"%]*)") + "(%)");
          patterns.add("(\\{)" + this.toString().replaceAll("\\$\\d", "([^\"%]*)") + "(\\})");
          this.rule.setPattern(patterns);

          while (matcher.find()) {
            try {
              int groupNumber = Integer.parseInt(matcher.group(2));
              this.groups.add(groupNumber);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    public static int getCount() {
      return count;
    }

    public Object getCachedValue(UUID uuid) {
      if (this.isCache() && !this.isComplex() && uuid != null) {
        PlaceholderCache placeholderCache = this.map.get(uuid);
        return placeholderCache != null
                && System.currentTimeMillis() <= placeholderCache.getValidUntil()
            ? placeholderCache.getValue()
            : null;
      } else {
        return null;
      }
    }

    public void addCachedValue(UUID uuid, String text, int durationMillis) {
      PlaceholderCache placeholderCache = this.map.get(uuid);
      if (placeholderCache == null) {
        placeholderCache = new PlaceholderCache();
      }

      placeholderCache.setValue(text);
      placeholderCache.setValidUntil(System.currentTimeMillis() + durationMillis);
      this.map.put(uuid, placeholderCache);
    }

    public static Placeholder.CMIPlaceHolders getByName(String name) {
      if (name.startsWith("%") || name.startsWith("{")) {
        name = name.replace("%", "").replace("{", "").replace("}", "");
      }

      String fullName = name;
      Placeholder.CMIPlaceHolders cmiPlaceHolders = byNameStatic.get(name);
      if (cmiPlaceHolders != null) {
        return cmiPlaceHolders;
      }

      String[] parts = name.split("_");
      if (parts.length < 3) {
        return null;
      }

      String prefix = parts[0] + "_" + parts[1];
      var candidates = byNameComplex.get(prefix);
      if (candidates == null) {
        return null;
      }

      for (Placeholder.CMIPlaceHolders candidate : candidates) {
        if (!candidate.getComplexRegexMatchers(fullName).isEmpty()) {
          return candidate;
        }
      }

      return null;
    }

    public static Placeholder.CMIPlaceHolders getByNameExact(String name) {
      return getByName(name);
    }

    public String getFull() {
      if (this.isComplex()) {
        String result = this.name();
        int varIndex = 0;
        String[] parts;
        int partCount = (parts = this.name().split("_")).length;

        for (int partIndex = 0; partIndex < partCount; partIndex++) {
          String part = parts[partIndex];
          if (part.startsWith("$")) {
            if (varIndex < this.vars.length) {
              result = result.replace(part, "[" + this.vars[varIndex] + "]");
            }

            varIndex++;
          }
        }

        return "%" + result + "%";
      } else {
        return "%" + this.name() + "%";
      }
    }

    public String getMVdW() {
      if (this.isComplex()) {
        String result = this.name();
        int varIndex = 0;
        String[] parts;
        int partCount = (parts = this.name().split("_")).length;

        for (int partIndex = 0; partIndex < partCount; partIndex++) {
          String part = parts[partIndex];
          if (part.startsWith("$")) {
            if (this.vars.length >= varIndex - 1) {
              result = result.replace(part, "*");
            }

            varIndex++;
          }
        }

        return result;
      } else {
        return this.name();
      }
    }

    public List<String> getComplexRegexMatchers(String text) {
      ArrayList<String> matches = new ArrayList();
      if (!this.isComplex()) {
        return matches;
      }

      if (!text.startsWith("%") && !text.endsWith("%")) {
        text = "%" + text + "%";
      }

      Matcher matcher = this.getRule().getMatcher(text);
      if (matcher == null) {
        return matches;
      }

      while (matcher.find()) {
        matches.add(matcher.group());
      }

      return matches;
    }

    public List<String> getComplexValues(String text) {
      ArrayList<String> values = new ArrayList();
      if (this.isComplex() && text != null) {
        if (!text.startsWith("%") && !text.endsWith("%")) {
          text = "%" + text + "%";
        }

        Matcher matcher = this.getRule().getMatcher(text);
        if (matcher == null) {
          return values;
        }

        if (matcher.find()) {
          try {
            for (Integer group : this.groups) {
              values.add(matcher.group(group + 1));
            }
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }

        return values;
      } else {
        return values;
      }
    }

    public boolean isComplex() {
      return this.rule != null;
    }

    public ChatFilterRule getRule() {
      return this.rule;
    }

    public void setRule(ChatFilterRule chatFilterRule) {
      this.rule = chatFilterRule;
    }

    public boolean isHidden() {
      return this.hidden;
    }

    public String getDescription() {
      return this.desc;
    }

    public boolean isCache() {
      return this.cache;
    }

    public Placeholder.CMIPlaceHolders getUserRedirect() {
      return this.userRedirect;
    }
  }

  public enum CMIPlaceholderType {
    CMI,
    PAPI,
    MVdW;
  }
}
