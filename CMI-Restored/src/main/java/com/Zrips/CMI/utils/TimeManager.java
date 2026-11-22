package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGameRule;
import com.Zrips.CMI.Containers.CMISunMover;
import com.Zrips.CMI.Containers.CMITimeRate;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.SleepStats;
import com.Zrips.CMI.Locale.CMILC;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.TimeInfo;
import net.Zrips.CMILib.Time.timeState;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Teleporters.CMITeleporter;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TimeManager {

    double tPHour = 1000.0;
    double tPMin = 16.666666666666668;
    double tPSec = 0.2777777777777778;

    private HashMap<String, CMITask> daySchedID = new HashMap<>();
    private HashMap<String, HashMap<timeState, CMITimeRate>> dayTimeDurations = new HashMap<>();
    private Set<World> froozenWorlds = new HashSet<>();
    private CMI plugin;

    static int dayTime = 600;
    static int sunriseTime = 90;
    static int sunsetTime = 90;
    static int nightTime = 420;

    private List<String> worlds = new ArrayList<>();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public static HashMap<World, CMISunMover> movingmap = new HashMap<>();
    private static long showTimer = 0L;

    public TimeManager(CMI plugin) {
        this.plugin = plugin;
    }

    public List<String> getWorlds() {
        return this.worlds;
    }

    public void runTimer() {
        if (this.getWorlds().isEmpty()) {
            return;
        }
        CMIScheduler.runTaskLater(this.plugin, () -> {
            String currentTime = dateFormat.format(Calendar.getInstance().getTime());
            List<String> worldNames = new ArrayList<>(this.worlds);
            for (String worldName : worldNames) {
                World world = CMIWorld.getWorld(worldName);
                if (world == null) {
                    this.worlds.remove(worldName);
                    continue;
                }
                CMIGameRule.ADVANCE_TIME.set(world, false);
                this.setTime(world, currentTime, false);
            }
            if (!this.worlds.isEmpty()) {
                this.runTimer();
            }
        }, (long) this.plugin.getConfigManager().AutoTimeInterval * 20L);
    }

    public void loadConfig() {
        ConfigReader config = this.plugin.getConfigManager().getConfig();
        if (!this.daySchedID.isEmpty()) {
            for (Map.Entry<String, CMITask> entry : new HashMap<>(this.daySchedID).entrySet()) {
                this.stopDayTimer(entry.getKey());
            }
        }
        this.dayTimeDurations.clear();

        ConfigurationSection timeSpeedSection = config.getC().getConfigurationSection("Time.TimeSpeed");
        Set<String> speedWorlds = new HashSet<>();
        if (timeSpeedSection != null && timeSpeedSection.getKeys(false) != null) {
            speedWorlds.addAll(timeSpeedSection.getKeys(false));
        }
        if (speedWorlds.isEmpty()) {
            speedWorlds.add(Bukkit.getWorlds().get(0).getName());
        }

        boolean commentsAdded = false;
        for (String worldKey : speedWorlds) {
            if (!commentsAdded) {
                config.addComment("Time.TimeSpeed", new String[]{"Allows you to change vanilla time speed to your own liking and needs"});
            }
            boolean enabled = config.get("Time.TimeSpeed." + worldKey + ".Enabled", Boolean.valueOf(false));
            if (!commentsAdded) {
                config.addComment("Time.TimeSpeed." + worldKey, new String[]{"Time is defined in seconds. Vanilla 24 hour ingame duration is 1200 seconds of real time"});
            }

            for (timeState state : timeState.values()) {
                if (!commentsAdded) {
                    config.addComment("Time.TimeSpeed." + worldKey + "." + state.toString(), new String[]{"Default value: " + state.getDefaultDuration() + " Starts from tick: " + state.getFrom() + " Ends at tick: " + state.getUntil()});
                }
                int durationSeconds = config.get("Time.TimeSpeed." + worldKey + "." + state.toString(), state.getDefaultDuration());
                if (durationSeconds < 1) {
                    durationSeconds = 1;
                }
                if (enabled) {
                    HashMap<timeState, CMITimeRate> rates = this.dayTimeDurations.computeIfAbsent(worldKey.toLowerCase(), key -> new HashMap<>());
                    int stateTicks = state.getUntil() - state.getFrom();
                    double ticksPerSecond = durationSeconds * 1000 / 50;
                    Double rate = ticksPerSecond / (double) stateTicks;
                    int addAmount = 1;
                    if (rate < 1.0) {
                        double baseRate = rate;
                        while (rate < 1.0) {
                            rate = baseRate * (double) (++addAmount);
                        }
                    }
                    rates.put(state, new CMITimeRate(state, rate.intValue(), addAmount));
                }
            }
            if (enabled) {
                CMIScheduler.runTask(CMI.getInstance(), () -> this.runDayTimer(worldKey));
            }
            commentsAdded = true;
        }
    }

    public void stopDayTimer(World world) {
        if (world == null) {
            return;
        }
        this.stopDayTimer(world.getName());
    }

    public void stopDayTimer(String worldName) {
        if (worldName == null) {
            return;
        }
        CMITask task = this.daySchedID.remove(worldName.toLowerCase());
        if (task != null) {
            task.cancel();
        }
    }

    private void runDayTimer(World world) {
        if (world == null) {
            return;
        }
        this.runDayTimer(world.getName());
    }

    private void runDayTimer(String worldName) {
        if (worldName == null) {
            return;
        }
        World world = CMIWorld.getWorld(worldName);
        if (world == null || world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        this.stopDayTimer(world);
        if (Bukkit.getWorld(world.getUID()) == null) {
            return;
        }
        timeState currentState = timeState.getTimeState(world);
        HashMap<timeState, CMITimeRate> rates = this.dayTimeDurations.get(world.getName().toLowerCase());
        if (rates == null) {
            return;
        }
        CMITimeRate rate = rates.get(currentState);
        if (rate == null) {
            return;
        }
        CMITimeRate timeRate = rate;
        this.daySchedID.put(world.getName().toLowerCase(), CMIScheduler.scheduleSyncRepeatingTask(this.plugin, () -> {
            if (timeState.getTimeState(world) != timeRate.getState()) {
                this.runDayTimer(world);
            }
            long newTime = world.getTime() + (long) timeRate.getAddAmount();
            if (world.getEnvironment().equals(World.Environment.NORMAL) && world.getTime() > 12009L && world.getTime() < 23992L) {
                SleepStats sleepStats = new SleepStats(world);
                if (sleepStats.getPercent() >= 1.0) {
                    timeRate.setTurnCycleOff(true);
                    if (sleepStats.getSleeping() == sleepStats.getTotal()) {
                        CMIGameRule.ADVANCE_TIME.set(world, true);
                    } else if (movingmap.containsKey(world)) {
                        CMIGameRule.ADVANCE_TIME.set(world, movingmap.get(world).getMoving());
                    }
                    return;
                }
            }
            if (timeRate.isTurnCycleOff()) {
                if (movingmap.containsKey(world)) {
                    CMIGameRule.ADVANCE_TIME.set(world, movingmap.get(world).getMoving());
                }
                timeRate.setTurnCycleOff(false);
            }
            if (newTime > 24000L) {
                newTime -= 24000L;
            }
            if (newTime > 24000L) {
                newTime = 0L;
            }
            if (this.isFroozenWorld(world)) {
                return;
            }
            world.setTime(newTime);
        }, 0L, (long) timeRate.getEveryTicks()));
    }

    public long setTime(World world, String time, boolean smooth) {
        TimeInfo timeInfo = CMITimeManager.stringToTimeInfo(time);
        return this.setTime(world, timeInfo, smooth);
    }

    public long setPTime(Player player, String time, boolean smooth) {
        TimeInfo timeInfo = CMITimeManager.stringToTimeInfo(time);
        return this.setPTime(player, timeInfo, smooth);
    }

    public void cancelSunMove(World world) {
        this.cancelSunMove(world, true);
    }

    public void cancelSunMove(World world, boolean restoreAdvanceTime) {
        CMISunMover sunMover = movingmap.get(world);
        if (sunMover != null) {
            CMITask moverTask = sunMover.getTimeMoverTask();
            if (moverTask != null) {
                moverTask.cancel();
            }
            if (restoreAdvanceTime) {
                CMIScheduler.runTask(this.plugin, () -> CMIGameRule.ADVANCE_TIME.set(world, movingmap.get(world).getMoving()));
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!world.equals(player.getWorld())) {
                continue;
            }
            this.plugin.getPlayerManager().getUser(player).removeBossBar("CMISunSpeeding");
        }
    }

    public void moveSun(World world, int speed, int period, int targetTime, boolean showInfo) {
        CMIScheduler.runAtLocation(this.plugin, new Location(world, 0.0, 0.0, 0.0), () -> {
            int normalizedTarget = targetTime > 24000 ? 24000 - targetTime % 24000 : targetTime;
            int wrappedTarget = normalizedTarget < 0 ? 24000 - normalizedTarget : normalizedTarget;
            CMISunMover sunMover = movingmap.get(world);
            if (sunMover != null) {
                if (sunMover.getTimeMoverTask() != null) {
                    sunMover.getTimeMoverTask().cancel();
                }
            } else {
                sunMover = new CMISunMover();
                movingmap.put(world, sunMover);
            }
            if (sunMover.getTimeMoverTask() != null) {
                sunMover.getTimeMoverTask().cancel();
                sunMover.setTimeMoverTask(null);
            }
            if (speed < 1) {
                return;
            }
            sunMover.setMoving(CMIGameRule.ADVANCE_TIME.get(world).booleanValue());
            long currentTime = world.getTime();
            long distance = (long) wrappedTarget - currentTime;
            distance = distance < 0L ? -distance : distance;
            if (distance <= (long) speed) {
                if (Version.isFolia()) {
                    int finalTarget = normalizedTarget;
                    CMIScheduler.runTask(this.plugin, () -> world.setTime((long) finalTarget));
                } else {
                    world.setTime((long) normalizedTarget);
                }
                return;
            }
            long firstStep = world.getTime() + (long) speed + 1L;
            if (firstStep > 24000L) {
                firstStep = 24000L - firstStep;
            }
            if (Version.isFolia()) {
                long finalStep = firstStep;
                CMIScheduler.runTask(this.plugin, () -> world.setTime(finalStep));
            } else {
                world.setTime(firstStep);
            }
            sunMover.setTimeMoverTask(CMIScheduler.scheduleSyncRepeatingTask(this.plugin, () -> {
                if (!CMI.getInstance().isEnabled()) {
                    return;
                }
                long now = world.getTime();
                long remaining = Math.abs((long) wrappedTarget - now);
                if (remaining <= (long) speed || remaining > 24000L) {
                    world.setTime((long) wrappedTarget);
                    if (movingmap.containsKey(world)) {
                        CMIGameRule.ADVANCE_TIME.set(world, movingmap.get(world).getMoving());
                    }
                    if (world.hasStorm() || world.isThundering()) {
                        SleepStats sleepStats = new SleepStats(world);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!world.equals(player.getWorld()) || !player.isSleeping()) {
                                continue;
                            }
                            CMITeleporter.teleportAsync(player, player.getLocation());
                        }
                        world.setThundering(false);
                        world.setWeatherDuration((int) ((double) world.getWeatherDuration() * (1.0 - sleepStats.getPercent())));
                    }
                    CMISunMover finishedMover = movingmap.get(world);
                    if (finishedMover != null && finishedMover.getTimeMoverTask() != null) {
                        finishedMover.getTimeMoverTask().cancel();
                    }
                    return;
                }
                if (showInfo && Version.isCurrentHigher(Version.v1_8_R2) && showTimer + 500L < System.currentTimeMillis()) {
                    showTimer = System.currentTimeMillis();
                    CMIScheduler.runTaskAsynchronously(this.plugin, () -> this.showSunSpeedInfo(world, now));
                }
                long stepTarget = world.getTime() + (long) speed;
                if (world.getTime() < (long) wrappedTarget && stepTarget > (long) wrappedTarget) {
                    world.setTime((long) wrappedTarget);
                } else {
                    world.setTime(stepTarget);
                }
            }, (long) period, (long) period));
        });
    }

    private void showSunSpeedInfo(World world, long time) {
        SleepStats sleepStats = new SleepStats(world);
        switch (SleepStats.SleepingSpeedupInfoType) {
            case "bossbar": {
                String message = CMILC.info_sunSpeeding.getLocale(new Object[]{
                    "[count]", (int) sleepStats.getSleeping(),
                    "[total]", (int) sleepStats.getTotal(),
                    "[hour]", CMITimeManager.to24hour(world.getTime()),
                    "[speed]", sleepStats.getSpeed()});
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!world.equals(player.getWorld())) {
                        continue;
                    }
                    if (!player.isSleeping()) {
                        this.plugin.getPlayerManager().getUser(player).removeBossBar("CMISunSpeeding");
                        continue;
                    }
                    CMIUser user = this.plugin.getPlayerManager().getUser(player);
                    BossBarInfo bar = user.getBossBar("CMISunSpeeding");
                    if (bar == null) {
                        bar = new BossBarInfo(user.getPlayer(), "CMISunSpeeding");
                        bar.setColor(BarColor.WHITE);
                    }
                    bar.setKeepForTicks(20);
                    bar.setTitleOfBar(message);
                    bar.setPercentage(10984.0, (double) time - 12516.0);
                    user.addBossBar(bar);
                }
                break;
            }
            case "title": {
                String title = CMILC.info_sunSpeedingTitle.getLocale(new Object[]{
                    "[count]", (int) sleepStats.getSleeping(),
                    "[total]", (int) sleepStats.getTotal(),
                    "[hour]", CMITimeManager.to24hour(world.getTime()),
                    "[speed]", sleepStats.getSpeed()});
                String subTitle = CMILC.info_sunSpeedingSubTitle.getLocale(new Object[]{
                    "[count]", (int) sleepStats.getSleeping(),
                    "[total]", (int) sleepStats.getTotal(),
                    "[hour]", CMITimeManager.to24hour(world.getTime()),
                    "[speed]", sleepStats.getSpeed()});
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (world.equals(player.getWorld()) && player.isSleeping()) {
                        CMITitleMessage.send(player, title, subTitle);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public long setTime(World world, TimeInfo timeInfo, boolean smooth) {
        if ((long) timeInfo.getHours() < 0L && (long) timeInfo.getTicks() < 0L) {
            return -1L;
        }
        if ((long) timeInfo.getTicks() < 0L) {
            CMITimeManager.convertToTicks(timeInfo);
        }
        long ticks = timeInfo.getTicks();
        if (smooth) {
            this.moveSun(world, this.plugin.getConfigManager().getAutoTimeSmoothSpeed(), 1, (int) ticks, false);
        } else if (Version.isFolia()) {
            CMIScheduler.runTask(this.plugin, () -> world.setTime(ticks));
        } else {
            world.setTime(ticks);
        }
        return ticks;
    }

    public long setPTime(Player player, TimeInfo timeInfo, boolean smooth) {
        if ((long) timeInfo.getHours() < 0L && (long) timeInfo.getTicks() < 0L) {
            return -1L;
        }
        if ((long) timeInfo.getTicks() < 0L) {
            CMITimeManager.convertToTicks(timeInfo);
        }
        long ticks = timeInfo.getTicks();
        player.setPlayerTime(ticks, false);
        CMIUser.getUser(player).setPlayerTime(ticks, false);
        this.plugin.save(player);
        return ticks;
    }

    public boolean isFroozenWorld(World world) {
        if (CMIGameRule.ADVANCE_TIME.get(world).booleanValue()) {
            return false;
        }
        return this.froozenWorlds.contains(world);
    }

    public void addFroozenWorlds(World world) {
        this.froozenWorlds.add(world);
        CMISunMover sunMover = movingmap.get(world);
        if (sunMover != null) {
            sunMover.setMoving(false);
        }
    }

    public void removeFroozenWorlds(World world) {
        this.froozenWorlds.add(world);
        CMISunMover sunMover = movingmap.get(world);
        if (sunMover != null) {
            sunMover.setMoving(true);
        }
    }

    public static String to24hourAproximateShort(Long ticks, boolean shortForm) {
        long ms = ticks;
        long years = ms / 1000L / 60L / 60L / 24L / 365L;
        if (years > 0L) {
            ms = ms / 1000L / 60L / 60L / 24L * 1000L * 60L * 60L * 24L;
        }
        ms = ms - years * 1000L * 60L * 60L * 24L * 365L;

        int months = 0;
        int weeks = 0;
        long days = ms / 1000L / 60L / 60L / 24L;
        if (days > 30L) {
            months = (int) (days / 30L);
            days -= (long) (months * 30);
        }
        if (days > 7L) {
            weeks = (int) (days / 7L);
            days -= (long) (weeks * 7);
        }
        if (months > 0) {
            ms = ms / 1000L / 60L / 60L * 1000L * 60L * 60L;
        }
        if (weeks > 0) {
            ms = ms / 1000L / 60L * 1000L * 60L;
        }
        ms = ms - (long) months * 1000L * 60L * 60L * 24L * 30L;
        ms = ms - (long) weeks * 1000L * 60L * 60L * 24L * 7L;
        ms = ms - days * 1000L * 60L * 60L * 24L;

        long hours = ms / 1000L / 60L / 60L;
        ms = ms - hours * 1000L * 60L * 60L;
        long minutes = ms / 1000L / 60L;
        ms = ms - minutes * 1000L * 60L;
        long seconds = ms / 1000L;
        ms = ms - seconds * 1000L;

        String result = "";
        if (years > 0L) {
            result = result + (years == 1L ? LC.info_oneYear.getLocale(new Object[]{"[years]", years}) : LC.info_years.getLocale(new Object[]{"[years]", years}));
        }
        if (months > 0 || (!shortForm && years > 0L)) {
            result = result + (months == 1 ? LC.info_oneMonth.getLocale(new Object[]{"[months]", months}) : LC.info_months.getLocale(new Object[]{"[months]", months}));
        }
        if (weeks > 0 || (!shortForm && (months > 0 || years > 0L))) {
            result = result + (weeks == 1 ? LC.info_oneWeek.getLocale(new Object[]{"[weeks]", weeks}) : LC.info_weeks.getLocale(new Object[]{"[weeks]", weeks}));
        }
        if ((days > 0L && years == 0L) || (!shortForm && (weeks > 0 || months > 0))) {
            result = result + (days == 1L ? LC.info_oneDay.getLocale(new Object[]{"[days]", days}) : LC.info_day.getLocale(new Object[]{"[days]", days}));
        }
        if (hours > 0L || (!shortForm && (days > 0L || weeks > 0 || months > 0 || years > 0L))) {
            result = result + (hours == 1L ? LC.info_oneHour.getLocale(new Object[]{"[hours]", hours}) : LC.info_hour.getLocale(new Object[]{"[hours]", hours}));
        }
        if (minutes > 0L || (!shortForm && (hours > 0L || days > 0L || weeks > 0 || months > 0 || years > 0L))) {
            result = result + LC.info_min.getLocale(new Object[]{"[mins]", minutes});
        }
        if (seconds > 0L || (!shortForm && (minutes > 0L || hours > 0L || days > 0L || weeks > 0 || months > 0 || years > 0L))) {
            result = result + LC.info_sec.getLocale(new Object[]{"[secs]", seconds});
        }
        if (result.isEmpty()) {
            result = result + LC.info_sec.getLocale(new Object[]{"[secs]", 0});
        }
        return result;
    }
}
