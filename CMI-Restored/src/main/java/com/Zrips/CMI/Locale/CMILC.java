package com.Zrips.CMI.Locale;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Messages.CMIMultiMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public enum CMILC {
  info_Same("{gcw}Can't open your own inventory for editing!"),
  info_cantLoginWithDifCap(
      "{gcw}Can't login with different name capitalization! Old name: {gcs}[oldName]{gcw}. Current:"
          + " {gcs}[currentName]"),
  info_NoCommandWhileSleeping("{gcw}Can't perform commands while sleeping!"),
  info_PurgeNotEnabled("{gcw}Purge function is not enabled in config file!"),
  info_TeamManagementDisabled(
      "{gcp}This feature will have limited functionalaty while DisableTeamManagement is set to"
          + " true!"),
  info_NoGameMode("{gcw}Please use 0/1/2/3 or Survival/Creative/Adventure/Spectator or s/c/a/sp!"),
  info_NameChange("{gcs}[playerDisplayName] {gcp}logged in, also known as: {gcs}[namelist]"),
  info_Cooldowns("{gcp}This command is on cooldown for another {gcs}[time]"),
  info_specializedCooldowns("{gcp}Cooldown in motion for this command, please wait {gcs}[time]"),
  info_specializedRunning("{gcp}Command still running, please wait {gcs}[time]"),
  info_CooldownOneTime("{gcp}This command can only be used once!"),
  info_WarmUp_canceled("{gcp}Command was cancelled due to your movement"),
  info_WarmUp_counter("!actionbar!{gcp}--> Wait {gcs}[time] {gcp}seconds <--"),
  info_WarmUp_DontMove("!title!{gcp}Teleporting!!subtitle!{gcp}Wait {gcw}[time] {gcp}seconds"),
  info_WarmUp_Boss_DontMove("{gcw}Don't move for {gcs}[autoTimeLeft] {gcw}seconds!"),
  info_WarmUp_Boss_WaitFor("{gcw}Wait for {gcs}[autoTimeLeft] {gcw}seconds!"),
  info_FailedSpawnerMine(
      "!actionbar!{gcw}Failed to mine spawner. {gcs}[percent]% {gcw}drop chance"),
  info_ClickSpawner("!actionbar!{gcs}[percent]% {gcp}Drop chance"),
  info_Elevator_created("{gcp}Created elevator sign"),
  info_CantPlaceSpawner("{gcp}Can't place spawner so close to another spawner ({gcs}[range]{gcp})"),
  info_ChunksLoading(
      "{gcp}World chunk data is still being loaded. Please wait a bit and try again."),
  info_CantUseNonEncrypted(
      "!actionbar!{gcw}Commands on this item are not encrypted. Can't use them!"),
  info_CantUseTrident("!actionbar!{gcw}Can't use trident with riptide enchant at this moment!"),
  info_CantDecode(
      "!actionbar!{gcw}Can't decode message/command. Key file contains wrong key for this task."
          + " Inform server administration about this"),
  info_CantTeleport(
      "{gcp}You can't teleport because you have too many limited items. Scroll over this line to"
          + " see the maximum amount of items allowed."),
  info_CantTeleportDistance("{gcp}Sorry you cant teleport over {gcs}[amount] {gcp}blocks away!"),
  info_BlackList("{gcp}[material] [amount] {gcs}Max: [max]"),
  info_wrongPortal("{gcw}You are in incorrect area of effect"),
  info_ItemWillBreak(
      "!actionbar!{gcp}Your item ({gcs}[itemName]{gcp}) will break soon!"
          + " {gcs}[current]{gcp}/{gcs}[max]"),
  info_ArmorWillBreak(
      "!actionbar!{gcp}Your [itemName] will break soon! {gcs}[current]{gcp}/{gcs}[max]"),
  info_flyingToHigh("{gcw}You can't fly so high, max height is {gcs}[max]{gcw}!"),
  info_specializedItemFail(
      "{gcw}Can't determine specialized item requirement by value: {gcs}[value]"),
  info_sunSpeeding("Sleeping [count] of [total] [hour] hour [speed]X speed"),
  info_sleepersRequired(
      "!actionbar!{gcs}[sleeping] {gcp}of {gcs}[required] {gcp}sleeping from required for night"
          + " time speedup"),
  info_sunSpeedingTitle("{gcs}[hour]"),
  info_skippingNight("!title!{gcsp}Skipping entire night"),
  info_sunSpeedingSubTitle("{gcs}[count]{gcp}/{gcs}[total] {gcp}({gcs}[speed]X{gcp})"),
  info_repairConfirm("{gcp}Click to confirm {gcs}[items] {gcp}item repair for {gcs}[cost]"),
  info_bookDate("{gcp}Written at {gcs}[date]"),
  info_maintenance("{gcp}Maintenance mode"),
  info_mapLimit("{gcw}Cant go beyond 30 000 000 blocks"),
  info_startedEditingPainting(
      "{gcp}You started editing painting. Click any other block to cancel."),
  info_canceledEditingPainting("{gcp}You canceled painting editing mode"),
  info_changedPainting("!actionbar!{gcp}Changed painting to {gcs}[name] {gcp}with id of {gcs}[id]"),
  info_noSpam("!title!{gcw}No spamming!"),
  info_noCmdSpam("!title!{gcw}No command spamming!"),
  info_spamConsoleInform(
      "{gcw}Player ({gcs}[playerName]{gcw}) triggered ({gcs}[rules]{gcw}) chat filter with:&r"
          + " [message]"),
  info_FirstJoin(
      "{gcp}Welcome {gcs}[playerDisplayName] {gcp}to our server!",
      "This line can have extra variables: [totalUsers] [onlinePlayers]"),
  info_LogoutCustom(
      " {gcs}[playerDisplayName] {gcp}left the game",
      "Don't forget to enable custom logout messages in config!"),
  info_LoginCustom(
      " {gcs}[playerDisplayName] {gcp}joined the game",
      "Don't forget to enable custom login messages in config!"),
  info_deathlocation(
      "{gcp}You died at x:{gcs}[x]{gcp}, y:{gcs}[y]{gcp}, z:{gcs}[z]{gcp} in {gcs}[world]"),
  info_book_exploit("{gcw}You cant create book with more than [amount] pages"),
  info_move_CantUseShulkerBox("{gcw}Can't use shulker box while moving"),
  info_combat_CostToOpen("!actionbar!{gcp}Charged for opening: [amount]"),
  info_combat_CantUseShulkerBox(
      "{gcw}Can't use shulker box while you are in combat with player. Wait: [time]"),
  info_combat_CantUseCommand(
      "!actionbar!{gcw}Can't use command while in combat mode. Wait: [time]"),
  info_combat_bossBarPvp("{gcw}Combat mode [autoTimeLeft]"),
  info_combat_bossBarPve("{gcn}Combat mode [autoTimeLeft]"),
  info_noSchedule("{gcw}Schedule by this name is not found"),
  info_totem_cooldown("{gcp}Totem cooldown: [time]"),
  info_totem_warmup("{gcp}Totem effect: [time]"),
  info_totem_cantConsume("{gcp}Totem usage was denied due to its cooldown time"),
  info_InventorySave_info("{gcp}Info: {gcn}[playerDisplayName]"),
  info_InventorySave_saved("{gcs}[time] {gcp}Inventory saved with id: {gcs}[id]"),
  info_InventorySave_NoSavedInv("{gcp}This player doesn't have any saved inventories"),
  info_InventorySave_NoEntries("{gce}File exists, but no inventories were found!"),
  info_InventorySave_CantFind("{gcp}Can't find inventory with this id"),
  info_InventorySave_TopLine(
      "{gcp}----------- {gcs}[playerDisplayName] saved inventory {gcp}-----------"),
  info_InventorySave_List("{gcp}id: {gcs}[id]{gcp}. {gcs}[time]"),
  info_InventorySave_KillerSymbol("{gcw} ☠"),
  info_InventorySave_Click("{gcp}Click to check ([id]) saved inventory"),
  info_InventorySave_IdDontExist("{gcw}This save Id doesn't exist!"),
  info_InventorySave_Deleted("{gcp}Saved inventory was successfully deleted!"),
  info_InventorySave_Restored(
      "{gcp}You have restored {gcs}[sourcename] {gcp}inventory for {gcs}[targetname] {gcp}user."),
  info_InventorySave_GotRestored(
      "{gcp}Your inventory was restored from {gcs}[sourcename] {gcp}saved inventory on"
          + " {gcs}[time]"),
  info_InventorySave_LoadForSelf("{gcp}Load this inventory for your self"),
  info_InventorySave_LoadForOwner("{gcp}Load this inventory for owner"),
  info_InventorySave_NextInventory("{gcp}Next inventory"),
  info_InventorySave_PreviousInventory("{gcp}Previous inventory"),
  info_InventorySave_Editable("{gcp}Edit mode enabled"),
  info_InventorySave_NonEditable("{gcp}Edit mode disabled"),
  info_vanishSymbolOn("{gcs}[{gcn}H{gcs}]&r"),
  info_vanishSymbolOff(""),
  info_afkSymbolOn("{gcs}[{gcn}Afk{gcs}]&r"),
  info_afkSymbolOff(""),
  info_beeinfo(
      "!actionbar!{gcp}Honey level: {gcs}[level]{gcp}/{gcs}[maxlevel] {gcp}Bees inside:"
          + " {gcs}[count]{gcp}/{gcs}[maxcount]"),
  info_decoratedpotinfo("!actionbar!{gcp}Contents: {gcs}[items]"),
  info_pvp_noGodDamage("!actionbar!{gcw}You can't damage players while being immortal"),
  info_pve_noGodDamage("!actionbar!{gcw}You can't damage mobs while being immortal"),
  info_InvEmpty_armor("{gcp}Your armor slots should be empty!"),
  info_InvEmpty_hand("{gcp}Your hand should be empty!"),
  info_InvEmpty_maininv("{gcp}Your main inventory should be empty!"),
  info_InvEmpty_maininvslots(
      "{gcp}Your main inventory should have atleast {gcs}[count] {gcp}empty slots!"),
  info_InvEmpty_inv("{gcp}Your inventory should be empty!"),
  info_InvEmpty_offhand("{gcp}Your offhand should be empty!"),
  info_InvEmpty_quickbar("{gcp}Your quick bar should be empty!"),
  info_InvEmpty_quickbarslots(
      "{gcp}Your quick bar should have atleast {gcs}[count] {gcp}empty slots!"),
  info_InvEmpty_subinv("{gcp}Your sub inventory should be empty!"),
  info_InvEmpty_subinvslots(
      "{gcp}Your sub inventory should have atleast {gcs}[count] {gcp}empty slots!"),
  info_Relog("{gcp}Relog might be needed for change to take effect"),
  info_Any("Any"),
  info_Reset("{gcp}Press {gcs}Q {gcp}to reset"),
  info_outsideWorldBorder("{gcw}Location is outside world border"),
  info_noWorldAccess("{gcw}No world access"),
  info_IncorrectColor("{gcw}Color defined incorrectly!"),
  info_IncorrectFile("{gcw}File name defined incorrectly!"),
  info_time_days(
      "{gcn}[d]{gcn}d:",
      "Mainly for placeholder playtime output",
      "Use + sign at the start of line to force include it even if specific and previous time"
          + " values are 0",
      "Use - sign at the start of line if you want to include it only if its value isn't 0. If"
          + " entire output results into empty line, then we will output seconds value"),
  info_time_hours("{gcn}[h]{gcn}h "),
  info_time_minutes("{gcn}[m]{gcn}m "),
  info_time_seconds("{gcn}[s]{gcn}s"),
  dialog_signEditor("Sign Editor"),
  dialog_update("Update"),
  dialog_save("Save"),
  dialog_close("Close"),
  dialog_line("Line [line]"),
  warp_list("{gcs}[pos]{gcp}. {gcs}[warpName] {gcn}- {gcs}[worldName] ([x]:[y]:[z])"),
  afk_off("{gcs}Playing"),
  afk_MayNotRespond(
      "{gcp}Player is AFK and may not respond",
      "Placeholders can be used for MayNotRespond and MayNotRespondStaff"),
  afk_MayNotRespondStaff(
      "{gcp}Staff member is AFK and may not respond. Try contacting us through discord"),
  BossBar_hpBar("{gcs}[victim] {gcs}[current]{gcs}/{gcs}[max] {gcs}({gcw}-[damage]{gcs})"),
  BossBar_playerHpBar(
      "{gcs}%cmi_user_display_name%"
          + " {gcs}[current]{gcs}[currentAbsorption]/{gcs}[max][maxAbsorption]"
          + " {gcs}({gcw}-[damage]{gcs})"),
  BossBar_absorption("{gcs}({gcn}[value]{gcs})"),
  Potion_Effects("{gcp}Potion effects"),
  Potion_List("{gcs}[PotionName] [PotionAmplifier] {gcp}Duration: {gcs}[LeftDuration] {gcp}sec"),
  Potion_NoPotions("{gcp}None"),
  Information_Title("{gcp}Players information"),
  Information_Health("{gcp}Health: {gcs}[Health]/[maxHealth]"),
  Information_Hunger("{gcp}Hunger: {gcs}[Hunger]"),
  Information_Saturation("{gcp}Saturation: {gcs}[Saturation]"),
  Information_Exp("{gcp}Exp: {gcs}[Exp]"),
  Information_NotEnoughExp("{gcp}Not enough exp: {gcs}[Exp]"),
  Information_NotEnoughExpNeed("{gcp}Not enough exp: {gcs}[Exp]/[need]"),
  Information_tooMuchExp("{gcp}Too much exp: {gcs}[Exp]/[need]"),
  Information_NotEnoughVotes("{gcp}Not enough votes: {gcs}[votes]"),
  Information_TooMuchVotes("{gcp}Too many votes: {gcs}[votes]"),
  Information_BadGameMode("{gcw}You can't do this in your current game mode"),
  Information_BadArea("{gcw}You can't perform this action in this area"),
  Information_GameMode("{gcp}GameMode: {gcs}[GameMode]"),
  Information_Flying("{gcp}Flying: {gcs}[Flying]"),
  Information_Uuid("{gcs}[uuid]"),
  Information_FirstConnection("{gcp}First connection: {gcs}[time]"),
  Information_Lastseen("{gcp}Last seen: {gcs}[time]"),
  Information_Onlinesince("{gcp}Online since: {gcs}[time]"),
  Information_Money("{gcp}Balance: {gcs}[money]"),
  Information_Group("{gcp}Group: {gcs}[group]"),
  econ_commandCost("{gcp}This command cost is {gcs}[cost] {gcp}repeat it or click here to confirm"),
  econ_disabled("{gcw}Can't use this command while economy support is disabled"),
  Elytra_Speed("{gcp}Speed: {gcs}[speed]{gcp}km/h"),
  Elytra_SpeedBoost(" {gcs}+ "),
  Elytra_SpeedSuperBoost(" {gcn}+ "),
  Elytra_CanUse("{gcw}Can't equip elytra without permission!"),
  Elytra_CantGlide("{gcw}Can't use elytra here!"),
  Elytra_Charging("{gcp}Charging {gcs}[percentage]{gcp}%"),
  NetherPortal_ToHigh("{gcw}Portal is to big, max height is {gcs}[max]{gcw}!"),
  NetherPortal_ToWide("{gcw}Portal is to wide, max width is {gcs}[max]{gcw}!"),
  NetherPortal_Creation("!actionbar!{gcp}Created [height]x[width] nether portal!"),
  NetherPortal_Disabled("{gcw}Portal creation disabled!"),
  Ender_Title("{gcp}Open ender chest"),
  Chat_localPrefix(""),
  Chat_shoutPrefix("{gcn}[S]&r"),
  Chat_LocalNoOne("!actionbar!{gcw}Nobody hear you, write ! before message for global chat"),
  Chat_shoutDeduction("!actionbar!{gcw}Deducted {gcs}[amount] {gcw}for shout"),
  Chat_publicHover(
      Arrays.asList("{gcp}Sent time: {gcs}%server_time_hh:mm:ss%"), "Use \\n to add new line"),
  Chat_privateHover(Arrays.asList("{gcp}Sent time: {gcs}%server_time_hh:mm:ss%")),
  Chat_staffHover(Arrays.asList("{gcp}Sent time: {gcs}%server_time_hh:mm:ss%")),
  Chat_helpopHover(Arrays.asList("{gcp}Sent time: {gcs}%server_time_hh:mm:ss%")),
  Chat_link("&l{gcw}[{gcn}LINK{gcw}]"),
  Chat_item("{gcn}[%cmi_iteminhand_displayname%[amount]{gcn}]"),
  Chat_itemAmount(" x[amount]"),
  Chat_itemEmpty("{gcn}[Mighty fist]"),
  Spy_Chat_msg(
      "{gcn}Spy{gcp}[{gcp}[senderDisplayName] {gcp}-> {gcp}[playerDisplayName]{gcp}]{gcs}"
          + " [message]"),
  Spy_Chat_custom(
      "{gcn}Spy{gcp}[senderDisplayName] {gcp}-> {gcw}|{gcp}[place]{gcw}|{gcs} [message]"),
  Spy_Command_msg("{gcs}C{gcn}Spy{gcp}[{gcp}[senderDisplayName]{gcp}]{gcp}: {gcs}[command]"),
  Spy_Sign_msg("{gcs}Sign{gcn}Spy{gcp}[{gcp}[senderDisplayName]{gcp}]{gcp}: {gcs}[text]"),
  info_CommandFormat("{gcw}Incorrect command format"),
  info_ServerSwitchOut(" {gcs}[playerDisplayName] {gcp}switched server to {gcs}[serverName]"),
  info_ServerSwitchIn(" {gcs}[playerDisplayName] {gcp}came from {gcs}[serverName] {gcp}server");

  private String text;
  private List<String> comments = new ArrayList<>();

  CMILC(String string) {
    this(string, "");
  }

  CMILC(String string, String... strings) {
    this.text = string;
    if (strings != null && strings.length > 0) {
      String[] argsCopy = strings;
      int value = strings.length;

      for (int argIndex = 0; argIndex < value; argIndex++) {
        String arg = argsCopy[argIndex];
        if (!arg.isEmpty()) {
          this.comments.add(arg);
        }
      }
    }
  }

  CMILC(List<String> list) {
    this(list, "");
  }

  CMILC(List<String> list, String... strings) {
    if (this.text == null) {
      this.text = "";
    }

    for (String string : list) {
      if (!this.text.isEmpty()) {
        this.text = this.text + " /n";
      }

      this.text = this.text + string;
    }

    if (strings != null && strings.length > 0) {
      String[] argsCopy = strings;
      int value = strings.length;

      for (int argIndex = 0; argIndex < value; argIndex++) {
        String arg = argsCopy[argIndex];
        if (!arg.isEmpty()) {
          this.comments.add(arg);
        }
      }
    }
  }

  public String getText() {
    return this.text;
  }

  public String getPt() {
    return this.name().replace("_", ".");
  }

  public List<String> getComments() {
    return this.comments;
  }

  public String getLocale(Object... objects) {
    return getMsg(this, objects);
  }

  public void sendMessage(Object object, Object... objects) {
    if (object instanceof CMIUser) {
      CMIUser cmiUser = (CMIUser) object;
      if (!cmiUser.isOnline()) {
        return;
      }

      object = cmiUser.getPlayer();
    }

    String string = this.getLocale(objects);
    if (CMI.getInstance().getConfigManager().isCleanRconCommands()
        && object instanceof RemoteConsoleCommandSender) {
      string = CMIChatColor.stripColor(string);
    }

    CMIMessages.sendMessage(object, string);
  }

  private static String getMsg(CMILC cmilc, Object... objects) {
    CMILC localeEntry = null;
    if (objects.length > 0 && objects[0] instanceof LC) {
      localeEntry = cmilc;
      cmilc = (CMILC) objects[0];
      objects = Arrays.copyOfRange(objects, 1, objects.length);
    }

    boolean flag = false;
    if (cmilc == null) {
      return "";
    }

    if (localeEntry == null) {
      flag = CMI.getInstance().getLM().isList(cmilc.getPt());
      if (!flag) {
        return CMI.getInstance().getLM().getMessage(cmilc.getPt(), objects);
      }

      var list = CMI.getInstance().getLM().getMessageList(cmilc.getPt(), objects);
      StringBuilder stringBuilder = new StringBuilder();

      for (int value = 0; value < list.size(); value++) {
        stringBuilder.append((String) list.get(value));
        if (value < list.size()) {
          stringBuilder.append(" /n ");
        }
      }

      return stringBuilder.toString();
    } else {
      flag = CMI.getInstance().getLM().isList(localeEntry.getPt());
      if (!flag) {
        return CMI.getInstance().getLM().getMessage(localeEntry.getPt())
            + CMI.getInstance().getLM().getMessage(cmilc.getPt(), objects);
      }

      var messageList = CMI.getInstance().getLM().getMessageList(localeEntry.getPt(), objects);
      StringBuilder builder = new StringBuilder();

      for (int lineIndex = 0; lineIndex < messageList.size(); lineIndex++) {
        builder.append((String) messageList.get(lineIndex));
        if (lineIndex < messageList.size()) {
          builder.append(" /n ");
        }
      }

      return CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(builder.toString());
    }
  }

  public static void info(Class<?> source, CMIUser cmiUser, String string, Object... objects) {
    if (cmiUser.isOnline()) {
      info(source.getSimpleName(), cmiUser.getPlayer(), string, objects);
    }
  }

  public static void info(Object object, CMIUser cmiUser, String string, Object... objects) {
    if (cmiUser.isOnline()) {
      if (object instanceof String) {
        info((String) object, cmiUser.getPlayer(false), string, objects);
      } else {
        info(object, cmiUser.getPlayer(false), string, objects);
      }
    }
  }

  public static void info(
      Class<?> source, CommandSender commandSender, String string, Object... objects) {
    info(source.getSimpleName(), commandSender, string, objects);
  }

  public static void info(
      Object object, CommandSender commandSender, String string, Object... objects) {
    info(object.getClass().getSimpleName(), commandSender, string, objects);
  }

  public static void info(
      String string, CommandSender commandSender, String key, Object... objects) {
    if (commandSender != null) {
      String message =
          CMI.getInstance().getLM().getMessage("command." + string + ".info." + key, objects);
      if (!message.isEmpty()) {
        sendMessage(commandSender, message, false);
      }
    }
  }

  public static void info(String string, Player player, String key, Object... objects) {
    if (player != null) {
      String message =
          CMI.getInstance().getLM().getMessage("command." + string + ".info." + key, objects);
      if (!message.isEmpty()) {
        sendMessage(player, message, false);
      }
    }
  }

  public static String getIM(Class<?> clazz, String string, Object... objects) {
    return getIM(clazz.getSimpleName(), string, objects);
  }

  public static String getIM(Object object, String string, Object... objects) {
    return getIM(object.getClass().getSimpleName(), string, objects);
  }

  public static String getIM(String string, String key, Object... objects) {
    return CMI.getInstance().getLM().getMessage("command." + string + ".info." + key, objects);
  }

  public static List<String> getIML(String string, String key, Object... objects) {
    return CMI.getInstance().getLM().getMessageList("command." + string + ".info." + key, objects);
  }

  public static List<String> getIML(Object object, String string, Object... objects) {
    return getIML(object.getClass().getSimpleName(), string, objects);
  }

  public static List<String> getIML(Class<?> clazz, String string, Object... objects) {
    return getIML(clazz.getSimpleName(), string, objects);
  }

  public static void sendMessageByPath(Object object, String string, Object... objects) {
    sendMessageByPath(null, object, string, objects);
  }

  public static void sendMessageByPath(
      String string, Object object, String key, Object... objects) {
    sendMessage(object, CMI.getInstance().getLM().getLMessage(string, key, objects));
  }

  public static void sendMessage(Object object, String string) {
    sendMessage(object, string, true, true, true, true);
  }

  public static void sendMessage(Object object, String string, boolean flag) {
    sendMessage(object, string, flag, true, true, true);
  }

  public static void sendMessage(
      Object object, String string, boolean flag, boolean translateColors) {
    sendMessage(object, string, flag, translateColors, true, true);
  }

  public static void sendMessage(
      Object object,
      String string,
      boolean flag,
      boolean translateColors,
      boolean translatePlaceholders) {
    sendMessage(object, string, flag, translateColors, translatePlaceholders, true);
  }

  public static void sendMessage(
      Object object,
      String string,
      boolean flag,
      boolean translateColors,
      boolean translatePlaceholders,
      boolean filterNewLine) {
    if (object != null && string != null && !string.isEmpty()) {
      if (CMI.getInstance().getConfigManager().isCleanRconCommands()
          && object instanceof RemoteConsoleCommandSender) {
        string = CMIChatColor.stripColor(string);
      }

      if (filterNewLine) {
        string = CMI.getInstance().getLM().filterNewLine(string);
      }

      CMIMultiMessage cmiMultiMessage = new CMIMultiMessage(string);
      cmiMultiMessage.setTranslateColors(translateColors);
      cmiMultiMessage.setUpdateSnd(flag);
      cmiMultiMessage.setTranslatePlaceholders(translatePlaceholders);

      try {
        cmiMultiMessage.setFilterNewLine(filterNewLine);
      } catch (Throwable throwable) {
      }

      if (object instanceof Player) {
        cmiMultiMessage.show((Player) object);
      } else if (object instanceof CMIUser) {
        cmiMultiMessage.show(((CMIUser) object).getPlayer(false));
      } else {
        if (Config.monochromeConsole) {
          string = CMIChatColor.stripColor(string);
        }

        cmiMultiMessage = new CMIMultiMessage(string);
        cmiMultiMessage.setTranslateColors(translateColors);
        cmiMultiMessage.setUpdateSnd(flag);
        cmiMultiMessage.setTranslatePlaceholders(translatePlaceholders);
        cmiMultiMessage.show((CommandSender) object);
      }
    }
  }

  public static int broadcastMessage(String string) {
    return broadcastMessage(null, string, false, null, null);
  }

  public static int broadcastMessage(CommandSender commandSender, String string) {
    return broadcastMessage(commandSender, string, true, null, null);
  }

  public static int broadcastMessage(
      CommandSender commandSender, PermissionsManager.CMIPerm cmiPerm, String string) {
    return broadcastMessage(commandSender, string, true, cmiPerm, null);
  }

  public static int broadcastMessage(CommandSender commandSender, String string, boolean flag) {
    return broadcastMessage(commandSender, string, flag, null, null);
  }

  public static int broadcastMessage(
      CommandSender commandSender, String string, boolean flag, Set<Player> set) {
    return broadcastMessage(commandSender, string, flag, null, set);
  }

  public static int broadcastMessage(
      CommandSender commandSender,
      String string,
      boolean flag,
      PermissionsManager.CMIPerm cmiPerm,
      Set<Player> set) {
    if (string != null && !string.isEmpty()) {
      string = CMIChatColor.translate(string);
      int value = 0;
      string = CMI.getInstance().getLM().filterNewLine(string);

      for (Player player : Bukkit.getOnlinePlayers()) {
        if ((commandSender == null || !commandSender.getName().equals(player.getName()) || flag)
            && (set == null || !set.contains(player))
            && (cmiPerm != null && cmiPerm.hasPermission(player) || cmiPerm == null)) {
          value++;
          CMIMultiMessage cmiMultiMessage = new CMIMultiMessage(string);
          cmiMultiMessage.show(player);
        }
      }

      if (commandSender == null
          || !commandSender.getName().equalsIgnoreCase(Bukkit.getConsoleSender().getName())) {
        CMIMultiMessage consoleMessage = new CMIMultiMessage(string);
        consoleMessage.show(Bukkit.getConsoleSender());
      }

      return value + 1;
    } else {
      return 0;
    }
  }

  public static List<String> updateSnd(List<String> list, Snd snd) {
    ArrayList arrayList = new ArrayList(list);
    int value = 0;

    for (int listSize = arrayList.size(); value < listSize; value++) {
      arrayList.set(value, CMI.getInstance().getLM().updateSnd(snd, (String) arrayList.get(value)));
    }

    return arrayList;
  }

  public static String updateSnd(Snd snd, String string) {
    return CMI.getInstance().getLM().updateSnd(snd, string);
  }

  public static String getOffOn(Player player, Player otherPlayer) {
    return player != null && player.isOnline() && otherPlayer != null && otherPlayer.isOnline()
        ? getOffOn(otherPlayer.canSee(player))
        : getOffOn(player != null ? player.isOnline() : false);
  }

  public static String getOffOn(Player player) {
    return player == null ? getOffOn(false) : getOffOn(player.isOnline());
  }

  public static String getOffOn(boolean flag) {
    return flag
        ? LC.info_variables_Online.getLocale(new Object[0])
        : LC.info_variables_Offline.getLocale(new Object[0]);
  }
}
