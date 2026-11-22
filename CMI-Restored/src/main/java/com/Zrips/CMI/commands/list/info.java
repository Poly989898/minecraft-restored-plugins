package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIBanEntry;
import com.Zrips.CMI.Containers.CMIExperience;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.GeoIP.IPLocation;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import com.Zrips.CMI.utils.UUIDFetcher;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class info implements Cmd {

    Pattern patern = Pattern.compile("(\\[)([a-zA-Z^s]+)(\\])");

    public void getExtra(ConfigReader configReader) {
        configReader.get("feedback.uuid", "{gcp}Left click to paste uuid into chat box");
        configReader.get("feedback.ouuid", "{gcp}Shift + left click to paste online uuid into chat box");
        configReader.get("newName", "{gcs}[playerName]");
        configReader.get("newBalance", "{gcp}({gcs}[balance]{gcp}) ");
        configReader.get("newDisplayName", "{gcp}Display name: &r[playerDisplayName]");
        configReader.get("newNameHover", Arrays.asList("[playerDisplayName]", "[nick]", "[group]", "[title]", "[onlineoffline]", "[playtime]"));
        configReader.get("newIp", "{gcp}IP ");
        configReader.get("newIpHover", Arrays.asList("[ip][country]", "[ping]", "[firstLogin]"));
        configReader.get("newNameHoverNick", "{gcp}Nick: {gcs}[nick]");
        configReader.get("newNameHoverGroup", "{gcp}Group: {gcs}[group]");
        configReader.get("newNameHoverTitle", "{gcp}Prefix: {gcs}[prefix] {gcp}Suffix: {gcs}[suffix]");
        configReader.get("newNameHoverIp", "{gcp}Ip: {gcs}[ip]");
        configReader.get("newNameHoverPing", "{gcp}Ping: {gcs}[ping]");
        configReader.get("newNameHoverCountry", " {gcs}[country] {gcp}({gcs}[countryCode]{gcp}) {gcs}[city]");
        configReader.get("newFirstLogin", "{gcp}First login: {gcs}[firstLogin]");
        configReader.get("newPlaytime", "{gcp}PlayTime: {gcs}[time]");
        configReader.get("newOnline", "[state] for: {gcs}[logoff]");
        configReader.get("newOnlineStateOn", "{gcu}Online");
        configReader.get("newOnlineStateOff", "{gcn}Offline");
        configReader.get("newUUID", "{gcp}UUID ");
        configReader.get("newUUIDHover", "{gcp}UUID: {gcs}[UUID]");
        configReader.get("newOUUIDHover", "{gcp}OnlineUUID: {gcs}[OUUID]");
        configReader.get("newTurnedOnColor", "{gcu}");
        configReader.get("newTurnedOffColor", "{gcn}");
        configReader.get("newOp", "OP ");
        configReader.get("newGod", "God ");
        configReader.get("newFly", "CanFly{gcs}([flying]{gcs}) ");
        configReader.get("newFlying", "Flying");
        configReader.get("newFlyHover", "{gcp}Fly speed: {gcs}[flyspeed]");
        configReader.get("newFlyTemp", "{gcp}Temporary fly: {gcs}[time]");
        configReader.get("newFlyCharge", "{gcp}Flight charge: {gcs}[charge]");
        configReader.get("newElytraFlyHover", "{gcp}Flying with elytra");
        configReader.get("newGodHover", "{gcp}TempGod for: {gcs}[time]");
        configReader.get("newNoDamageHover", "{gcp}No Damage Ticks for: {gcs}[time]");
        configReader.get("newSneaking", "Sneaking");
        configReader.get("newSprint", "Sprinting");
        configReader.get("newWalkSpeed", "{gcp}Walk speed: {gcs}[walkspeed]");
        configReader.get("newGameMode", "{gcp}Mode: {gcs}[gameMode] ");
        configReader.get("newGameModeHover", Arrays.asList("[sprint]", "[sneaking]", "[walkspeed]", "[hp]", "[air]", "[hunger]", "[saturation]", "[exp]"));
        configReader.get("newHp", "{gcp}HP: {gcs}[hp]{gcp}/{gcs}[maxhp]");
        configReader.get("newAir", "{gcp}Air: {gcs}[leftAir]{gcp}/{gcs}[totalAir]");
        configReader.get("newHunger", "{gcp}Hunger: {gcs}[food]{gcp}/{gcs}20");
        configReader.get("newSaturation", "{gcp}Saturation +{gcs}[saturation]");
        configReader.get("newExp", "{gcp}Exp: {gcs}[totalexp]{gcp}exp -> {gcs}[currentexp]{gcp}/{gcs}[needexp] {gcp}({gcs}[level]{gcp}lvl{gcp})");
        configReader.get("newPos", "{gcp}Pos ");
        configReader.get("newPosHover", "{gcs}[world] {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
        configReader.get("newAfk", "AFK ");
        configReader.get("newAfkHover", "{gcp}Afk for last: {gcs}[time]");
        configReader.get("AfkInHover", "{gcp}Afk in: {gcs}[time]");
        configReader.get("newBed", "{gcp}Bed ");
        configReader.get("newBedHover", "{gcs}[world] {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
        configReader.get("newDeath", "{gcp}Death ");
        configReader.get("newDeathHover", "{gcs}[world] {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}{gcp}]{gcp})");
        configReader.get("newBack", "{gcp}Back ");
        configReader.get("newBackHover", "{gcs}[world] {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
        configReader.get("newRespawn", "{gcp}Respawn ");
        configReader.get("newRespawnHover", "{gcs}[world] {gcp}({gcs}[x]{gcp}:{gcs}[y]{gcp}:{gcs}[z]{gcp})");
        configReader.get("inv", "{gcp}Inv ");
        configReader.get("ender", "{gcp}Ender ");
        configReader.get("newSee", "◕‿◕ ");
        configReader.get("newSeeHover", "{gcp}Can see you");
        configReader.get("newSeeHoverNot", "{gcu}Can't see you");
        configReader.get("vanished", "(V) ");
        configReader.get("vanishedHover", "{gcp}Vanished");
        configReader.get("newBanned", "Banned ");
        configReader.get("newBannedHover", Arrays.asList("{gcp}Banned by: {gcs}[name]", "{gcp}Left: {gcs}[left]", "{gcp}For: {gcs}[reason]"));
        configReader.get("newMuted", "Muted ");
        configReader.get("newMutedHover", Arrays.asList("{gcp}Left: {gcs}[left]", "{gcp}For: {gcs}[reason]"));
        configReader.get("newShadowMutedHover", "{gcd}Shadow mute");
        configReader.get("newCuffed", "Cuffed ");
        configReader.get("newJailed", "Jailed ");
        configReader.get("newJailedHover", Arrays.asList("{gcp}Jail: {gcs}[jail]", "{gcp}Cell: {gcs}[id]", "{gcp}Left: {gcs}[time]", "{gcp}Reason: {gcs}[reason]"));
        configReader.get("newHomes", "{gcp}Homes: {gcs}[count] ");
        configReader.get("newMail", "{gcp}Mail: {gcs}[count] ");
        configReader.get("newWarnings", "{gcp}Warnings: {gcs}[count] ");
        configReader.get("newAlert", "Alert ");
        configReader.get("newAlertHover", Arrays.asList("{gcp}Left: {gcs}[left]", "{gcp}For: {gcs}[reason]"));
        configReader.get("newChatRoom", "ChatRoom ");
        configReader.get("newChatRoomHover", Arrays.asList("{gcp}ChatRoom: {gcs}[name]", "{gcp}Users in: {gcs}[count]"));
        configReader.get("List", Arrays.asList("{gcp}------------- [enableColor]Enabled [disableColor]Disabled{gcp} -------------", "{gcp}| [name][balance][UUID][ip]", "{gcp}| [OP][god][fly][gameMode][afk][see]", "{gcp}| [banned][muted][cuffed][jailed][alert][chatroom]", "{gcp}| [pos][bed][respawn][death][back][ender][inv]", "{gcp}| [homes][mail][warnings]", "{gcp}-----------------------------------------"));
    }

    private static String getColor(boolean on) {
        return on ? CMILC.getIM(info.class, "newTurnedOnColor") : CMILC.getIM(info.class, "newTurnedOffColor");
    }

    @CAnnotation(info = "Show players information", args = "[playerName/uuid]", tab = {"playername"}, regVar = {-66}, consoleVar = {-100}, customAlias = {"whois"}, others = true)
    public Boolean perform(CMI cmi, CommandSender sender, String[] args) {
        String requested = null;
        if (args.length > 0) {
            requested = args[0];
        }
        Player player = cmi.getTarget(sender, requested, (Object) this);
        String targetName = player == null ? requested : player.getName();

        CMIScheduler.runTaskAsynchronously(cmi, () -> {
            UUID fetchedUuid = null;
            if (!Bukkit.getServer().getOnlineMode()) {
                fetchedUuid = UUIDFetcher.getUUID(targetName);
                if (fetchedUuid != null) {
                    CMI.getInstance().preFetchUUIDS().put(targetName, fetchedUuid);
                } else {
                    fetchedUuid = CMI.getInstance().preFetchUUIDS().get(targetName);
                }
            }
            final UUID onlineUuid = fetchedUuid;

            CMIUser user = player != null ? CMIUser.getUser(player) : CMIUser.getUser(targetName);
            if (user == null) {
                LC.info_NoPlayer.sendMessage(sender, new Object[0]);
                return;
            }

            String ip = null;
            IPLocation ipLocation = null;
            boolean isSelf = user.getName().equalsIgnoreCase(sender.getName());
            if (PermissionsManager.CMIPerm.command_info_ip.hasPermission(sender) || isSelf) {
                ip = player != null && player.isOnline() ? player.getAddress().getAddress().getHostAddress() : user.getLastIp();
                try {
                    ipLocation = CMI.getInstance().getLookupService().getLocation(ip);
                } catch (java.io.IOException exception) {
                    exception.printStackTrace();
                }
            }
            final IPLocation geo = ipLocation;
            final String ipDisplay = ip == null || ip.isEmpty() ? "-" : ip;

            CMIScheduler.runTask(cmi, () -> {
                Location bedSpawn = null;
                try {
                    if (player != null) {
                        bedSpawn = player.getBedSpawnLocation();
                    }
                } catch (Throwable ignored) {
                    bedSpawn = null;
                }
                Location respawn = cmi.getHomeManager().getReSpawnLocation(player);

                String uuidText = user.getUniqueId().toString();
                String gameModeText = player == null
                    ? CMILib.getInstance().getLM().getMessage("info.variables.survival", new Object[0])
                    : CMILib.getInstance().getLM().getMessage("info.variables." + (player.getGameMode() == null ? "Unknown" : player.getGameMode().name()).toLowerCase(), new Object[0]);

                double health = player == null ? 20.0 : player.getHealth();
                health = (double) ((int) (health * 100.0)) / 100.0;
                double maxHealth = player == null ? 20.0 : player.getMaxHealth();
                float saturation = player == null ? 0.0f : player.getSaturation();
                long totalExp = user.getTotalExp();
                int level = player == null ? 0 : player.getLevel();
                long expForCurrentLevel = user.getExpForCurrentLevel();
                long expToNextLevel = expForCurrentLevel + CMIExperience.getTotalExpToLevel(level + 1);
                String flySpeedText = player == null ? "0" : String.valueOf((int) ((double) Math.round(player.getFlySpeed() / 5.0f * 10000.0f) / 200.0));
                String walkSpeedText = player == null ? "0" : String.valueOf((int) ((double) Math.round(player.getWalkSpeed() * 1000.0f) / 200.0));
                String firstLogin = DateFormat.MiliToDate(player != null ? player.getFirstPlayed() : 0L);

                boolean targetOnline = player != null && player.isOnline();
                boolean targetCanSeeSender = false;
                if (sender instanceof Player) {
                    targetCanSeeSender = player != null && player.canSee((Player) sender);
                    if (player != null && targetOnline && !((Player) sender).canSee(player)) {
                        targetOnline = false;
                    }
                }
                boolean senderCanSeeTarget = false;
                if (user.isOnline() && sender instanceof Player) {
                    senderCanSeeTarget = player != null && ((Player) sender).canSee(player);
                }

                Location location = !targetOnline ? user.getLogOutLocation() : (player == null ? null : player.getLocation());
                if (location == null && player != null) {
                    location = player.getLocation();
                }
                String worldName = "Unknown";
                double x = 0.0;
                double y = 0.0;
                double z = 0.0;
                if (location != null) {
                    if (location.getWorld() != null) {
                        worldName = location.getWorld().getName();
                    }
                    x = (double) ((int) (location.getX() * 100.0)) / 100.0;
                    y = (double) ((int) (location.getY() * 100.0)) / 100.0;
                    z = (double) ((int) (location.getZ() * 100.0)) / 100.0;
                }

                long sinceLastChange = System.currentTimeMillis() - (targetOnline ? user.getLastLogin(true) : user.getLastLogoff(true));
                String lastSeenText = CMITimeManager.to24hourShort(sinceLastChange);
                String mainGroup = CMI.getInstance().getPermissionsManager().getMainGroup(player);
                List<String> lines = CMILC.getIML(info.class.getSimpleName(), "List");
                boolean canTeleportToPos = PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{tppos.class.getSimpleName()});

                StringBuilder plainText = new StringBuilder();
                plainText.append("\n");
                plainText.append(LC.info_Spliter.getLocale(new Object[0]));

                for (String line : lines) {
                    Matcher matcher = this.patern.matcher(line);
                    RawMessage rawMessage = new RawMessage();
                    boolean handled = false;
                    while (matcher.find()) {
                        String[] parts = line.split("\\[" + matcher.group(2) + "\\]");
                        if (parts.length > 0) {
                            String prefix = parts[0];
                            if (!prefix.isEmpty()) {
                                rawMessage.addText(prefix);
                            }
                            if (line.length() > prefix.length() + 2 + matcher.group(2).length()) {
                                line = line.substring(prefix.length() + 2 + matcher.group(2).length());
                            }
                        }
                        try {
                            StringBuilder hover = new StringBuilder();
                            switch (matcher.group(2).toLowerCase()) {
                                case "enablecolor": {
                                    rawMessage.addText(getColor(true));
                                    plainText.append(getColor(true));
                                    break;
                                }
                                case "disablecolor": {
                                    rawMessage.addText(getColor(false));
                                    plainText.append(getColor(false));
                                    break;
                                }
                                case "banned": {
                                    if (user.isBanned()) {
                                        CMIBanEntry banEntry = user.getBanEntry();
                                        plainText.append("\n");
                                        plainText.append(getColor(user.isBanned()) + CMILC.getIM(info.class, "newBanned") + " ");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newBannedHover")) {
                                            entry = entry.replace("[name]", banEntry.getSource());
                                            entry = entry.replace("[left]", CMITimeManager.to24hourShort(banEntry.getLeftTime()));
                                            entry = entry.replace("[reason]", banEntry.getReason() == null || banEntry.getReason().isEmpty() ? "-" : banEntry.getReason());
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                    }
                                    rawMessage.addText(getColor(user.isBanned()) + CMILC.getIM(info.class, "newBanned")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "jailed": {
                                    if (user.isJailed()) {
                                        CMIJailCell cell = user.getCell();
                                        plainText.append("\n");
                                        plainText.append(getColor(user.isJailed()) + CMILC.getIM(info.class, "newJailed") + " ");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newJailedHover")) {
                                            entry = entry.replace("[jail]", cell.getJail().getName());
                                            entry = entry.replace("[id]", String.valueOf(cell.getId()));
                                            entry = entry.replace("[time]", CMITimeManager.to24hourShort(user.getJailedForTime()));
                                            entry = entry.replace("[reason]", user.getJailedReason() == null || user.getJailedReason().isEmpty() ? "-" : user.getJailedReason());
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                    }
                                    rawMessage.addText(getColor(user.isJailed()) + CMILC.getIM(info.class, "newJailed")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "muted": {
                                    if (user.isMuted()) {
                                        long mutedUntil = user.isShadowMuted() ? user.getShadowMutedUntil() : user.getMutedUntil().longValue();
                                        plainText.append("\n");
                                        plainText.append(getColor(user.isMuted()) + CMILC.getIM(info.class, "newMuted") + " ");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newMutedHover")) {
                                            entry = entry.replace("[left]", CMITimeManager.to24hourShort(mutedUntil - System.currentTimeMillis()));
                                            entry = entry.replace("[reason]", user.getMutedReason() == null ? "-" : user.getMutedReason());
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                        if (user.isShadowMuted()) {
                                            hover.append("\n");
                                            hover.append(CMILC.getIM(info.class, "newShadowMutedHover"));
                                            plainText.append(CMILC.getIM(info.class, "newShadowMutedHover"));
                                        }
                                    }
                                    rawMessage.addText(getColor(user.isMuted()) + CMILC.getIM(info.class, "newMuted")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "name": {
                                    plainText.append("\n");
                                    plainText.append(CMILC.getIM(info.class, "newName", "[playerName]", user.getName()) + " ");
                                    for (String entry : CMILC.getIML(info.class.getSimpleName(), "newNameHover")) {
                                        entry = entry.replace("[playerDisplayName]", user.getDisplayName().equals(user.getName()) ? "" : CMILC.getIM(info.class, "newDisplayName", "[playerDisplayName]", user.getDisplayName()));
                                        entry = entry.replace("[nick]", user.getNickName() == null || user.getNickName().equals(user.getName()) ? "" : CMILC.getIM(info.class, "newNameHoverNick", "[nick]", user.getNickName()));
                                        entry = entry.replace("[group]", mainGroup == null || mainGroup.isEmpty() ? "" : CMILC.getIM(info.class, "newNameHoverGroup", "[group]", mainGroup));
                                        entry = entry.replace("[title]", user.getPrefix() == null && user.getSuffix() == null ? "" : CMILC.getIM(info.class, "newNameHoverTitle", "[prefix]", user.getPrefix() == null ? "" : user.getPrefix(), "[suffix]", user.getSuffix() == null ? "" : user.getSuffix()));
                                        entry = entry.replace("[knownAs]", "");
                                        entry = entry.replace("[onlineoffline]", CMILC.getIM(info.class, "newOnline", "[state]", targetOnline ? CMILC.getIM(info.class, "newOnlineStateOn") : CMILC.getIM(info.class, "newOnlineStateOff"), "[logoff]", lastSeenText));
                                        entry = entry.replace("[playtime]", (isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{playtime.class.getSimpleName()}))
                                            || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{playtime.class.getSimpleName()}))
                                            ? CMILC.getIM(info.class, "newPlaytime", "[time]", CMITimeManager.to24hourShort(user.getTotalPlayTime(true)))
                                            : "");
                                        if (entry.isEmpty()) {
                                            continue;
                                        }
                                        if (!hover.toString().isEmpty()) {
                                            hover.append("\n");
                                        }
                                        hover.append(entry);
                                        plainText.append(entry + " ");
                                    }
                                    rawMessage.addText(CMILC.getIM(info.class, "newName", "[playerName]", user.getName()))
                                        .addHover(hover.toString().isEmpty() ? null : hover.toString())
                                        .addCommand(PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{checkaccount.class.getSimpleName()}) ? "cmi " + checkaccount.class.getSimpleName() + " " + user.getName() : null);
                                    handled = true;
                                    break;
                                }
                                case "gamemode": {
                                    plainText.append("\n");
                                    plainText.append(CMILC.getIM(info.class, "newGameMode", "[gameMode]", gameModeText) + " ");
                                    for (String entry : CMILC.getIML(info.class.getSimpleName(), "newGameModeHover")) {
                                        boolean inlineOnly = false;
                                        switch (entry.replace(" ", "")) {
                                            case "[sprint]": {
                                                entry = entry.replace("[sprint]", getColor(player != null && player.isSprinting()) + CMILC.getIM(info.class, "newSprint", "[sprint]"));
                                                inlineOnly = true;
                                                break;
                                            }
                                            case "[sneaking]": {
                                                entry = entry.replace("[sneaking]", getColor(player != null && player.isSneaking()) + CMILC.getIM(info.class, "newSneaking"));
                                                inlineOnly = true;
                                                break;
                                            }
                                            case "[walkspeed]": {
                                                entry = entry.replace("[walkspeed]", CMILC.getIM(info.class, "newWalkSpeed", "[walkspeed]", walkSpeedText));
                                                break;
                                            }
                                            case "[hp]": {
                                                entry = entry.replace("[hp]", CMILC.getIM(info.class, "newHp", "[hp]", health, "[maxhp]", maxHealth));
                                                break;
                                            }
                                            case "[air]": {
                                                entry = entry.replace("[air]", CMILC.getIM(info.class, "newAir", "[leftAir]", player == null ? 0 : player.getRemainingAir(), "[totalAir]", player == null ? 0 : player.getMaximumAir()));
                                                break;
                                            }
                                            case "[hunger]": {
                                                entry = entry.replace("[hunger]", CMILC.getIM(info.class, "newHunger", "[food]", player == null ? 0 : player.getFoodLevel()));
                                                break;
                                            }
                                            case "[saturation]": {
                                                entry = entry.replace("[saturation]", CMILC.getIM(info.class, "newSaturation", "[saturation]", Float.valueOf(saturation)));
                                                break;
                                            }
                                            case "[exp]": {
                                                entry = entry.replace("[exp]", CMILC.getIM(info.class, "newExp", "[totalexp]", totalExp, "[currentexp]", expForCurrentLevel, "[needexp]", expToNextLevel, "[level]", level));
                                                break;
                                            }
                                            default:
                                                break;
                                        }
                                        if (entry.isEmpty()) {
                                            continue;
                                        }
                                        if (!hover.toString().isEmpty()) {
                                            hover.append("\n");
                                        }
                                        hover.append(entry);
                                        if (inlineOnly) {
                                            continue;
                                        }
                                        plainText.append(entry + " ");
                                    }
                                    rawMessage.addText(CMILC.getIM(info.class, "newGameMode", "[gameMode]", gameModeText)).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "pos": {
                                    plainText.append("\n");
                                    plainText.append(CMILC.getIM(info.class, "newPos") + " ");
                                    plainText.append(CMILC.getIM(info.class, "newPosHover", "[world]", worldName, "[x]", x, "[y]", y, "[z]", z) + " ");
                                    if (PermissionsManager.CMIPerm.command_info_pos.hasPermission(sender) || isSelf) {
                                        hover.append(CMILC.getIM(info.class, "newPosHover", "[world]", worldName, "[x]", x, "[y]", y, "[z]", z));
                                        rawMessage.addText(CMILC.getIM(info.class, "newPos")).addHover(hover.toString().isEmpty() ? null : hover.toString()).addSuggestion(info.toTppos(canTeleportToPos, worldName, x, y, z));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "bed": {
                                    if (bedSpawn != null && bedSpawn.getWorld().getName() != null && (PermissionsManager.CMIPerm.command_info_bed.hasPermission(sender) || isSelf)) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newBed") + " ");
                                        plainText.append(CMILC.getIM(info.class, "newBedHover", "[world]", bedSpawn) + " ");
                                        hover.append(CMILC.getIM(info.class, "newBedHover", "[world]", bedSpawn));
                                        rawMessage.addText(CMILC.getIM(info.class, "newBed")).addHover(hover.toString().isEmpty() ? null : hover.toString()).addSuggestion(info.toTppos(canTeleportToPos, bedSpawn));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "back": {
                                    Location lastTeleport = user.getLastTeleportLocation();
                                    if (lastTeleport != null && lastTeleport.getWorld() != null && lastTeleport.getWorld().getName() != null && (PermissionsManager.CMIPerm.command_info_back.hasPermission(sender) || isSelf)) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newBack") + " ");
                                        plainText.append(CMILC.getIM(info.class, "newBackHover", "[world]", lastTeleport) + " ");
                                        hover.append(CMILC.getIM(info.class, "newBackHover", "[world]", lastTeleport));
                                        rawMessage.add(CMILC.getIM(info.class, "newBack"), hover.toString().isEmpty() ? null : hover.toString(), null, info.toTppos(canTeleportToPos, lastTeleport));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "death": {
                                    Location deathLocation = user.getDeathLoc();
                                    if (deathLocation != null && deathLocation.getWorld() != null && deathLocation.getWorld().getName() != null && (PermissionsManager.CMIPerm.command_info_deathlocation.hasPermission(sender) || isSelf)) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newDeath") + " ");
                                        plainText.append(CMILC.getIM(info.class, "newDeathHover", "[world]", deathLocation) + " ");
                                        hover.append(CMILC.getIM(info.class, "newDeathHover", "[world]", deathLocation));
                                        rawMessage.add(CMILC.getIM(info.class, "newDeath"), hover.toString().isEmpty() ? null : hover.toString(), null, info.toTppos(canTeleportToPos, deathLocation));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "inv": {
                                    if (PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{inv.class.getSimpleName()}) && !isSelf) {
                                        rawMessage.addText(CMILC.getIM(info.class, "inv"));
                                        RawMessageCommand invCommand = new RawMessageCommand() {
                                            @Override
                                            public void run(CommandSender runner) {
                                                CMI.getInstance().performCommand(runner, "cmi " + inv.class.getSimpleName() + " " + user.getName());
                                            }
                                        };
                                        invCommand.setKeep(true);
                                        rawMessage.addCommand(invCommand);
                                    }
                                    handled = true;
                                    break;
                                }
                                case "ender": {
                                    if (PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{ender.class.getSimpleName()}) || isSelf) {
                                        rawMessage.addText(CMILC.getIM(info.class, "ender"));
                                        RawMessageCommand enderCommand = new RawMessageCommand() {
                                            @Override
                                            public void run(CommandSender runner) {
                                                CMI.getInstance().performCommand(runner, "cmi " + ender.class.getSimpleName() + " " + user.getName());
                                            }
                                        };
                                        enderCommand.setKeep(true);
                                        rawMessage.addCommand(enderCommand);
                                    }
                                    handled = true;
                                    break;
                                }
                                case "respawn": {
                                    if (respawn != null && respawn.getWorld() != null && respawn.getWorld().getName() != null) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newRespawn") + " ");
                                        plainText.append(CMILC.getIM(info.class, "newRespawnHover", "[world]", respawn) + " ");
                                        hover.append(CMILC.getIM(info.class, "newRespawnHover", "[world]", respawn));
                                        rawMessage.add(CMILC.getIM(info.class, "newRespawn"), hover.toString().isEmpty() ? null : hover.toString(), null, info.toTppos(canTeleportToPos, respawn));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "afk": {
                                    if (user.isAfk()) {
                                        plainText.append("\n");
                                        plainText.append(getColor(user.isAfk()) + CMILC.getIM(info.class, "newAfk") + " ");
                                        plainText.append(CMILC.getIM(info.class, "newAfkHover", "[time]", CMITimeManager.to24hourShort(System.currentTimeMillis() - user.getAfkInfo().getAfkFrom())) + " ");
                                        hover.append(CMILC.getIM(info.class, "newAfkHover", "[time]", CMITimeManager.to24hourShort(System.currentTimeMillis() - user.getAfkInfo().getAfkFrom())));
                                    } else if (user.isOnline() && CMI.getInstance().getAfkManager().getLastAction(user) != null && senderCanSeeTarget) {
                                        hover.append(CMILC.getIM(info.class, "AfkInHover", "[time]",
                                            CMITimeManager.to24hourShort(CMI.getInstance().getAfkManager().getAwayTrigerTime() - (System.currentTimeMillis() - CMI.getInstance().getAfkManager().getLastAction(user)))));
                                    }
                                    rawMessage.addText(getColor(user.isAfk()) + CMILC.getIM(info.class, "newAfk")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "see": {
                                    if (targetCanSeeSender) {
                                        hover.append(CMILC.getIM(info.class, "newSeeHover"));
                                    } else {
                                        hover.append(CMILC.getIM(info.class, "newSeeHoverNot"));
                                    }
                                    rawMessage.addText(getColor(targetCanSeeSender) + CMILC.getIM(info.class, "newSee")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    if (user.isVanished()) {
                                        if (user.isOnline() && senderCanSeeTarget) {
                                            rawMessage.addText(getColor(true) + CMILC.getIM(info.class, "vanished")).addHover(CMILC.getIM(info.class, "vanishedHover"));
                                        }
                                        plainText.append("\n");
                                        plainText.append(getColor(true) + CMILC.getIM(info.class, "vanishedHover"));
                                    }
                                    handled = true;
                                    break;
                                }
                                case "uuid": {
                                    plainText.append("\n");
                                    plainText.append(CMILC.getIM(info.class, "newUUIDHover", "[UUID]", uuidText) + " ");
                                    if (onlineUuid != null) {
                                        plainText.append(CMILC.getIM(info.class, "newOUUIDHover", "[OUUID]", onlineUuid.toString()) + " ");
                                    }
                                    hover.append(CMILC.getIM(info.class, "newUUIDHover", "[UUID]", uuidText));
                                    hover.append("\n");
                                    hover.append(CMILC.getIM(info.class, "feedback.uuid"));
                                    if (onlineUuid != null) {
                                        hover.append("\n");
                                        hover.append(CMILC.getIM(info.class, "newOUUIDHover", "[OUUID]", onlineUuid.toString()));
                                        hover.append("\n");
                                        hover.append(CMILC.getIM(info.class, "feedback.ouuid"));
                                    }
                                    rawMessage.addText(CMILC.getIM(info.class, "newUUID")).addHover(hover.toString().isEmpty() ? null : hover.toString()).addSuggestion(uuidText).addInsertion(onlineUuid == null ? null : onlineUuid.toString());
                                    handled = true;
                                    break;
                                }
                                case "ip": {
                                    if (PermissionsManager.CMIPerm.command_info_ip.hasPermission(sender) || isSelf) {
                                        plainText.append("\n");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newIpHover")) {
                                            entry = entry.replace("[ip]", CMILC.getIM(info.class, "newNameHoverIp", "[ip]", Config.IPRecord ? ipDisplay : "-"));
                                            entry = entry.replace("[country]", CMILC.getIM(info.class, "newNameHoverCountry",
                                                "[city]", geo == null ? "-" : geo.getCity(),
                                                "[country]", geo == null ? "-" : geo.getCountryName(),
                                                "[countryCode]", geo == null ? "-" : geo.getCountryCode()));
                                            entry = entry.replace("[firstLogin]", CMILC.getIM(info.class, "newFirstLogin", "[firstLogin]", firstLogin));
                                            entry = entry.replace("[ping]", user.isOnline()
                                                && (isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{ping.class.getSimpleName()})
                                                || !isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{ping.class.getSimpleName()}))
                                                ? CMILC.getIM(info.class, "newNameHoverPing", "[ping]", CMI.getInstance().getReflectionManager().getPing(player))
                                                : "");
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                        rawMessage.addText(CMILC.getIM(info.class, "newIp")).addHover(hover.toString().isEmpty() ? null : hover.toString()).addSuggestion(Config.IPRecord ? ipDisplay : "-");
                                    }
                                    handled = true;
                                    break;
                                }
                                case "fly": {
                                    boolean flying = player != null && player.isFlying();
                                    flying = Version.isCurrentEqualOrHigher(Version.v1_9_R1) && player != null && player.isGliding() ? true : flying;
                                    String flyText = getColor(player != null && player.getAllowFlight()) + CMILC.getIM(info.class, "newFly", "[flying]", getColor(flying) + CMILC.getIM(info.class, "newFlying"));
                                    plainText.append("\n");
                                    plainText.append(flyText + " ");
                                    plainText.append(CMILC.getIM(info.class, "newFlyHover", "[flyspeed]", flySpeedText) + " ");
                                    hover.append(CMILC.getIM(info.class, "newFlyHover", "[flyspeed]", flySpeedText));
                                    if (user.getTfly() > System.currentTimeMillis()) {
                                        plainText.append(CMILC.getIM(info.class, "newFlyTemp", "[time]", CMITimeManager.to24hourShort(user.getTfly() - System.currentTimeMillis())) + " ");
                                        hover.append("\n");
                                        hover.append(CMILC.getIM(info.class, "newFlyTemp", "[time]", CMITimeManager.to24hourShort(user.getTfly() - System.currentTimeMillis())));
                                    }
                                    if (user.getFlightCharge().getCharge() > 0.0) {
                                        plainText.append(CMILC.getIM(info.class, "newFlyCharge", "[charge]", (double) ((long) (user.getFlightCharge().getCharge() * 100.0)) / 100.0) + " ");
                                        hover.append("\n");
                                        hover.append(CMILC.getIM(info.class, "newFlyCharge", "[charge]", (double) ((long) (user.getFlightCharge().getCharge() * 100.0)) / 100.0));
                                    }
                                    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && player != null && player.isGliding()) {
                                        plainText.append(CMILC.getIM(info.class, "newElytraFlyHover") + " ");
                                        hover.append("\n");
                                        hover.append(CMILC.getIM(info.class, "newElytraFlyHover"));
                                    }
                                    RawMessageCommand flyCommand = new RawMessageCommand() {
                                        @Override
                                        public void run(CommandSender runner) {
                                            CMI.getInstance().performCommand(runner, "cmi " + fly.class.getSimpleName() + " " + user.getName());
                                            CMI.getInstance().performCommand(runner, "cmi " + info.class.getSimpleName() + " " + user.getName());
                                        }
                                    };
                                    rawMessage.addText(flyText).addHover(hover.toString().isEmpty() ? null : hover.toString())
                                        .addCommand((isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{fly.class.getSimpleName()}))
                                            || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{fly.class.getSimpleName()}))
                                            ? flyCommand.getCommand() : null);
                                    handled = true;
                                    break;
                                }
                                case "balance": {
                                    plainText.append(CMILC.getIM(info.class, "newBalance", "[balance]", user.getFormatedBalance()) + " ");
                                    rawMessage.addText(CMILC.getIM(info.class, "newBalance", "[balance]", user.getFormatedBalance()));
                                    handled = true;
                                    break;
                                }
                                case "god": {
                                    RawMessageCommand godCommand = new RawMessageCommand() {
                                        @Override
                                        public void run(CommandSender runner) {
                                            CMI.getInstance().performCommand(runner, "cmi " + god.class.getSimpleName() + " " + user.getName());
                                            CMI.getInstance().performCommand(runner, "cmi " + info.class.getSimpleName() + " " + user.getName());
                                        }
                                    };
                                    boolean godActive = user.isGod() || user.getTgod() > System.currentTimeMillis();
                                    if (godActive) {
                                        plainText.append(getColor(godActive) + CMILC.getIM(info.class, "newGod") + " ");
                                    }
                                    rawMessage.addText(getColor(godActive || user.getTgod() == -1L) + CMILC.getIM(info.class, "newGod"));
                                    if (player != null && player.getNoDamageTicks() > 0) {
                                        rawMessage.addHover(CMILC.getIM(info.class, "newNoDamageHover", "[time]", CMITimeManager.to24hourShort((long) player.getNoDamageTicks() * 50L)));
                                    }
                                    rawMessage.addCommand((isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{god.class.getSimpleName()}))
                                        || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{god.class.getSimpleName()}))
                                        ? godCommand.getCommand() : null);
                                    handled = true;
                                    break;
                                }
                                case "homes": {
                                    if (!user.getHomes().isEmpty()) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newHomes", "[count]", user.getHomes().size()) + " ");
                                    }
                                    if ((isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{homes.class.getSimpleName()}))
                                        || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{homes.class.getSimpleName()}))) {
                                        rawMessage.addText(CMILC.getIM(info.class, "newHomes", "[count]", user.getHomes().size())).addHover(LC.info_Click)
                                            .addCommand(String.valueOf(CommandsHandler.getLabel()) + " " + homes.class.getSimpleName() + " " + user.getName());
                                    }
                                    handled = true;
                                    break;
                                }
                                case "mail": {
                                    if (!user.getMails().isEmpty()) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newMail", "[count]", user.getMails().size()) + " ");
                                    }
                                    if ((isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{mail.class.getSimpleName()}))
                                        || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{mail.class.getSimpleName()}))) {
                                        rawMessage.addText(CMILC.getIM(info.class, "newMail", "[count]", user.getMails().size())).addHover(LC.info_Click)
                                            .addCommand(String.valueOf(CommandsHandler.getLabel()) + " " + mail.class.getSimpleName() + " read " + user.getName());
                                    }
                                    handled = true;
                                    break;
                                }
                                case "warnings": {
                                    if (user.getWarnings() != null && !user.getWarnings().isEmpty()) {
                                        plainText.append("\n");
                                        plainText.append(CMILC.getIM(info.class, "newWarnings", "[count]", user.getWarnings().size()) + " ");
                                    }
                                    if ((isSelf && PermissionsManager.CMIPerm.command_$1.hasPermission(sender, new String[]{warnings.class.getSimpleName()}))
                                        || (!isSelf && PermissionsManager.CMIPerm.command_$1_others.hasPermission(sender, new String[]{warnings.class.getSimpleName()}))) {
                                        rawMessage.addText(CMILC.getIM(info.class, "newWarnings", "[count]", user.getWarnings().size())).addHover(LC.info_Click)
                                            .addCommand(String.valueOf(CommandsHandler.getLabel()) + " " + warnings.class.getSimpleName() + " " + user.getName());
                                    }
                                    handled = true;
                                    break;
                                }
                                case "alert": {
                                    if (user.getAlertUntil() > System.currentTimeMillis()) {
                                        plainText.append("\n");
                                        plainText.append(getColor(true) + CMILC.getIM(info.class, "newAlert") + " ");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newAlertHover")) {
                                            entry = entry.replace("[left]", CMITimeManager.to24hourShort(user.getAlertUntil() - System.currentTimeMillis()));
                                            entry = entry.replace("[reason]", user.getAlertReason() == null || user.getAlertReason().isEmpty() ? "-" : user.getAlertReason());
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                    }
                                    rawMessage.addText(getColor(user.getAlertUntil() > System.currentTimeMillis()) + CMILC.getIM(info.class, "newAlert")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "chatroom": {
                                    if (user.getChatRoom() != null) {
                                        var chatRoom = user.getChatRoom();
                                        plainText.append("\n");
                                        plainText.append(getColor(true) + CMILC.getIM(info.class, "newChatRoom") + " ");
                                        for (String entry : CMILC.getIML(info.class.getSimpleName(), "newChatRoomHover")) {
                                            entry = entry.replace("[name]", chatRoom.getChatName());
                                            entry = entry.replace("[count]", String.valueOf(chatRoom.getUsers().size()));
                                            if (entry.isEmpty()) {
                                                continue;
                                            }
                                            if (!hover.toString().isEmpty()) {
                                                hover.append("\n");
                                            }
                                            hover.append(entry);
                                            plainText.append(entry + " ");
                                        }
                                    }
                                    rawMessage.addText(getColor(user.getChatRoom() != null) + CMILC.getIM(info.class, "newChatRoom")).addHover(hover.toString().isEmpty() ? null : hover.toString());
                                    handled = true;
                                    break;
                                }
                                case "cuffed": {
                                    if (user.isCuffed()) {
                                        plainText.append(getColor(user.isCuffed()) + CMILC.getIM(info.class, "newCuffed") + " ");
                                    }
                                    rawMessage.addText(getColor(user.isCuffed()) + CMILC.getIM(info.class, "newCuffed"));
                                    handled = true;
                                    break;
                                }
                                case "op": {
                                    if (player != null && player.isOp()) {
                                        plainText.append(getColor(player.isOp()) + CMILC.getIM(info.class, "newOp") + " ");
                                    }
                                    rawMessage.addText(getColor(player != null && player.isOp()) + CMILC.getIM(info.class, "newOp"));
                                    handled = true;
                                    break;
                                }
                                default:
                                    break;
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    if (!handled) {
                        rawMessage.addText(line);
                    }
                    if (!(sender instanceof Player)) {
                        continue;
                    }
                    rawMessage.show(sender);
                }

                if (!(sender instanceof Player)) {
                    plainText.append("\n");
                    plainText.append(LC.info_Spliter.getLocale(new Object[0]));
                    CMILC.sendMessage(sender, plainText.toString());
                }
            });
        });
        return true;
    }

    private static String toTppos(boolean canTeleport, Location location) {
        if (location == null || !canTeleport) {
            return null;
        }
        return info.toTppos(canTeleport, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    private static String toTppos(boolean canTeleport, String worldName, double x, double y, double z) {
        if (!canTeleport) {
            return null;
        }
        CommandAlias alias = CMI.getInstance().getAliasManager().getAliasForCommand(tppos.class.getSimpleName());
        String labelPrefix = alias == null || !alias.getType().equals(CommandAliasType.base) ? String.valueOf(CommandsHandler.getLabel()) + " " : "";
        return "/" + labelPrefix + tppos.class.getSimpleName() + " " + ((double) ((int) x) + 0.5) + " " + y + " " + ((double) ((int) z) + 0.5) + " " + worldName;
    }

    private static String replace(String text, String target, Object value) {
        return text.replace(target, String.valueOf(value));
    }
}
