package com.Zrips.CMI.Modules.Particl;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Reflections;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleManager {
  private CMI plugin;
  Map<UUID, Set<CMIVisualEffect>> playerEffects =
      Collections.synchronizedMap(new ConcurrentHashMap<>());
  private static final String fileName = "ParticleEffects.yml";
  private static final String customPreset =
      "circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4";
  Pattern pattern =
      Pattern.compile("(?:effect|ef):(?:\\{([^{}]*(?:\\{[^{}]*\\}[^{}]*)*)\\}|([^;]+));?");

  public ParticleManager(CMI cmi) {
    this.plugin = cmi;
  }

  public synchronized boolean addPlayerEffect(CMIVisualEffect cmiVisualEffect) {
    Player player = cmiVisualEffect.getTargetPlayer();
    if (player == null) {
      return false;
    }

    var set = this.playerEffects.get(player.getUniqueId());
    if (set == null) {
      set = Collections.synchronizedSet(new HashSet());
      this.playerEffects.put(player.getUniqueId(), set);
    }

    set.add(cmiVisualEffect);
    return true;
  }

  public synchronized boolean removePlayerEffects(CMIVisualEffect cmiVisualEffect) {
    Player player = cmiVisualEffect.getTargetPlayer();
    if (player == null) {
      return false;
    }

    var set = this.playerEffects.get(player.getUniqueId());
    if (set == null) {
      return false;
    }

    set.remove(cmiVisualEffect);
    if (set.isEmpty()) {
      this.playerEffects.remove(player.getUniqueId());
    }

    cmiVisualEffect.stop();
    return true;
  }

  public synchronized boolean removePlayerEffects(UUID uuid) {
    if (uuid == null) {
      return false;
    }

    var set = this.playerEffects.remove(uuid);
    if (set != null) {
      for (CMIVisualEffect cmiVisualEffect : set) {
        cmiVisualEffect.stop();
      }
    }

    return true;
  }

  public synchronized boolean removeAllPlayerEffects() {
    var iterator = this.playerEffects.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry entry = (Entry) iterator.next();

      for (CMIVisualEffect cmiVisualEffect :
          ((Iterable<CMIVisualEffect>) new HashSet((Collection) entry.getValue()))) {
        cmiVisualEffect.stop();
      }

      iterator.remove();
    }

    return true;
  }

  @Deprecated
  public void drawLine(
      Player player,
      Location location,
      Location endLocation,
      CMIParticle cmiParticle,
      boolean flag) {
    this.drawLine(location, endLocation, cmiParticle, flag, Arrays.asList(player));
  }

  @Deprecated
  public void drawLine(
      Location location,
      Location endLocation,
      CMIParticle cmiParticle,
      boolean flag,
      Player player) {
    ArrayList<Player> arrayList = new ArrayList();
    arrayList.add(player);
    this.drawLine(location, endLocation, cmiParticle, flag, arrayList);
  }

  @Deprecated
  public void drawLine(
      Location location,
      Location endLocation,
      CMIParticle cmiParticle,
      boolean flag,
      List<Player> list) {
    this.drawLine(location, endLocation, cmiParticle, flag, list, 5000L);
  }

  @Deprecated
  public void drawLine(
      Location location,
      Location endLocation,
      CMIParticle cmiParticle,
      boolean flag,
      List<Player> list,
      Long durationMillis) {
    this.drawLine(
        location, endLocation, new CMIEffect(cmiParticle), flag, list, durationMillis, 0.3);
  }

  public void drawLine(
      Location location, Location endLocation, CMIEffect cmiEffect, boolean flag, Player player) {
    var arrayList = getLineLocations(location, endLocation, flag, 0.3);
    this.drawShed(arrayList, cmiEffect, new ArrayList<>(Arrays.asList(player)), 5000L);
  }

  public void drawLine(
      Location location,
      Location endLocation,
      CMIEffect cmiEffect,
      boolean flag,
      List<Player> list,
      Long durationMillis,
      Double stepLength) {
    var arrayList = getLineLocations(location, endLocation, flag, stepLength);
    this.drawShed(arrayList, cmiEffect, list, durationMillis);
  }

  private static ArrayList<Location> getLineLocations(
      Location location, Location endLocation, boolean flag, Double stepLength) {
    if (stepLength == null) {
      stepLength = 0.3;
    }

    Vector vector = location.toVector();
    Vector endVector = endLocation.toVector();
    if (flag) {
      vector =
          new Vector(
              location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
      endVector =
          new Vector(
              endLocation.getBlockX() + 0.5,
              endLocation.getBlockY() + 0.5,
              endLocation.getBlockZ() + 0.5);
    }

    Vectors vectors = shortenVectors(0.5, 32, vector, endVector, location);
    ArrayList<Location> arrayList = new ArrayList();
    if (vectors.getp1() != null && vectors.getp2() != null) {
      vector = vectors.getp1();
      endVector = vectors.getp2();

      for (Vector linePoint : remakePlotLineInRange(stepLength, 32, vector, endVector, location)) {
        arrayList.add(
            new Location(
                location.getWorld(), linePoint.getX(), linePoint.getY(), linePoint.getZ()));
      }

      return arrayList;
    } else {
      return arrayList;
    }
  }

  private void drawShed(
      ArrayList<Location> arrayList, CMIEffect cmiEffect, List<Player> list, Long durationMillis) {
    ParticleManager.taskCancel repeatingTask = new ParticleManager.taskCancel(null);
    repeatingTask.setTask(
        CMIScheduler.scheduleSyncRepeatingTask(
            this.plugin,
            () -> {
              for (int value = 0; value < 5 && !arrayList.isEmpty(); value++) {
                Location location = (Location) arrayList.remove(0);

                for (Player player : list) {
                  CMILib.getInstance()
                      .getReflectionManager()
                      .playEffect(player, location, cmiEffect);
                }

                if (arrayList.isEmpty()) {
                  this.markBlockShed(
                      location, cmiEffect, System.currentTimeMillis() + durationMillis, list);
                  break;
                }
              }

              if (arrayList.isEmpty()) {
                repeatingTask.cancel();
              }
            },
            0L,
            1L));
  }

  private void markBlockShed(
      Location location, CMIEffect cmiEffect, Long endTimeMillis, List<Player> list) {
    ParticleManager.taskCancel repeatingTask = new ParticleManager.taskCancel(null);
    repeatingTask.setTask(
        CMIScheduler.scheduleSyncRepeatingTask(
            this.plugin,
            () -> {
              if (endTimeMillis < System.currentTimeMillis()) {
                repeatingTask.cancel();
              } else {
                markBlock(location, cmiEffect, list);
              }
            },
            0L,
            10L));
  }

  private static void markBlock(Location location, CMIEffect cmiEffect, List<Player> list) {
    if (location != null) {
      Location cornerLocation = location.clone().add(-0.5, -0.5, -0.5);
      Reflections reflections = CMILib.getInstance().getReflectionManager();

      for (Player player : list) {
        reflections.playEffect(player, cornerLocation, cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(1.0, 0.0, 0.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.0, 1.0, 0.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.0, 0.0, 1.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(1.0, 1.0, 0.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(1.0, 0.0, 1.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.0, 1.0, 1.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(1.0, 1.0, 1.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.5, 0.5, 0.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.5, 0.0, 0.5), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.0, 0.5, 0.5), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(1.0, 0.5, 0.5), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.5, 0.5, 1.0), cmiEffect);
        reflections.playEffect(player, cornerLocation.clone().add(0.5, 1.0, 0.5), cmiEffect);
      }
    }
  }

  private static List<Vector> remakePlotLineInRange(
      Double stepLength, int value, Vector vector, Vector endVector, Location location) {
    ArrayList<Vector> arrayList = new ArrayList();
    int pointCount = (int) (vector.distance(endVector) / stepLength) + 1;
    double lineLength = vector.distance(endVector);
    double segmentLength = lineLength / (pointCount - 1);
    Vector stepVector = endVector.subtract(vector).normalize().multiply(segmentLength);
    int skipStep = 1;

    for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
      Vector point = vector.clone().add(stepVector.clone().multiply(pointIndex));
      double yDistance = point.getY() - location.getY();
      if (yDistance < 0.0) {
        yDistance = -yDistance;
      }

      if (!(yDistance > value)) {
        if (!inRange(
            location,
            new Location(location.getWorld(), point.getX(), point.getY(), point.getZ()),
            value)) {
          if (skipStep < 15) {
            skipStep++;
          }

          pointIndex += skipStep;
        } else {
          skipStep = 1;
          arrayList.add(point);
        }
      }
    }

    return arrayList;
  }

  private static boolean inRange(Location location, Location otherLocation, Integer integer) {
    double xDistance = otherLocation.getBlockX() - location.getBlockX();
    if (xDistance < 0.0) {
      xDistance = -xDistance;
    }

    double zDistance = otherLocation.getBlockZ() - location.getBlockZ();
    if (zDistance < 0.0) {
      zDistance = -zDistance;
    }

    double yDistance = otherLocation.getBlockY() - location.getBlockY();
    if (yDistance < 0.0) {
      yDistance = -yDistance;
    }

    return !(xDistance > integer.intValue())
        && !(yDistance > integer.intValue())
        && !(zDistance > integer.intValue());
  }

  private static Vectors shortenVectors(
      Double stepLength, Integer integer, Vector vector, Vector endVector, Location location) {
    int value = (int) (vector.distance(endVector) / stepLength) + 1;
    double lineLength = vector.distance(endVector);
    double segmentLength = lineLength / (value - 1);
    Vector stepVector = endVector.clone().subtract(vector).normalize().multiply(segmentLength);
    boolean flag = false;
    boolean endpointFound = false;
    Vectors vectors = new Vectors();
    vectors.setp1(vector);
    vectors.setp2(endVector);

    for (int pointIndex = 0; pointIndex < value; pointIndex++) {
      Vector point = vector.clone().add(stepVector.clone().multiply(pointIndex));
      if (pointIndex == 0
          && point.getBlockX() < location.getBlockX() + integer
          && point.getBlockZ() < location.getBlockZ() + integer
          && point.getBlockX() > location.getBlockX() - integer
          && point.getBlockZ() > location.getBlockZ() - integer) {
        flag = true;
      }

      if (pointIndex + 1 >= value
          && !endpointFound
          && point.getBlockX() < location.getBlockX() + integer
          && point.getBlockZ() < location.getBlockZ() + integer
          && point.getBlockX() > location.getBlockX() - integer
          && point.getBlockZ() > location.getBlockZ() - integer) {
        endpointFound = true;
      }

      if (!flag
          && point.getBlockX() < location.getBlockX() + integer
          && point.getBlockZ() < location.getBlockZ() + integer
          && point.getBlockX() > location.getBlockX() - integer
          && point.getBlockZ() > location.getBlockZ() - integer) {
        vectors.setp1(point);
        endpointFound = true;
        break;
      }

      if (flag
          && (point.getBlockX() > location.getBlockX() + integer
              || point.getBlockZ() > location.getBlockZ() + integer
              || point.getBlockX() < location.getBlockX() - integer
              || point.getBlockZ() < location.getBlockZ() - integer)) {
        vectors.setp2(point);
        endpointFound = true;
        break;
      }
    }

    if (!endpointFound) {
      vectors.setp1(null);
    }

    return vectors;
  }

  public void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Settings" + File.separator + "ParticleEffects.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader == null) {
      CMI.getInstance()
          .consoleMessage(
              "&cFailed to load Settings" + File.separator + "ParticleEffects.yml" + " file");
    } else {
      ConfigReader mainConfig = CMI.getInstance().getConfigManager().getConfig();
      if (mainConfig.getC().isConfigurationSection("Particles")) {
        configReader.getC().set("Particles", mainConfig.getC().get("Particles"));
      }

      if (mainConfig.getC().isConfigurationSection("TeleportEffects")) {
        configReader.getC().set("TeleportEffects", mainConfig.getC().get("TeleportEffects"));
      }

      configReader.addComment(
          "Particles",
          new String[] {
            "If you want to disable particular particle entirely, set it to \"\" ",
            "More information how to set up this one can be found at"
                + " https://www.zrips.net/cmi/extra/particles/"
          });
      configReader.addComment(
          "Particles.custom1",
          new String[] {
            "From custom1 to custom30 are free to use particle presets which can be utilized for"
                + " things like command warmups"
          });
      Boolean particlesEnabled = configReader.get("Particles.Enabled", true);
      ParticleManager.CMIPresetAnimations[] cmiPresetAnimationses;
      int value = (cmiPresetAnimationses = ParticleManager.CMIPresetAnimations.values()).length;

      for (int presetIndex = 0; presetIndex < value; presetIndex++) {
        ParticleManager.CMIPresetAnimations cmiPresetAnimations =
            cmiPresetAnimationses[presetIndex];
        String string =
            configReader.get(
                "Particles." + cmiPresetAnimations.toString(), cmiPresetAnimations.getAnimString());
        if (string != null && !string.isEmpty()) {
          cmiPresetAnimations.setAnimString(string);
          cmiPresetAnimations.setEnabled(particlesEnabled);
        } else {
          cmiPresetAnimations.setEnabled(false);
        }
      }

      configReader.addComment(
          "TeleportEffects",
          new String[] {
            "Defined animated particles will be shown on player teleportation on particular action"
                + " from place and to destination",
            "if you don't want it to be shown then set it to empty field",
            "Animation names should be used from Particles section from above like TpUp"
          });
      CMITeleportType[] cmiTeleportTypes;
      value = (cmiTeleportTypes = CMITeleportType.values()).length;

      for (int teleportTypeIndex = 0; teleportTypeIndex < value; teleportTypeIndex++) {
        CMITeleportType cmiTeleportType = cmiTeleportTypes[teleportTypeIndex];
        String fromPresetName =
            configReader.get("TeleportEffects." + cmiTeleportType.toString() + ".From", "");
        String toPresetName =
            configReader.get("TeleportEffects." + cmiTeleportType.toString() + ".To", "");
        if (fromPresetName != null && !fromPresetName.isEmpty()) {
          cmiTeleportType.setFrom(ParticleManager.CMIPresetAnimations.getByName(fromPresetName));
        } else {
          cmiTeleportType.setFrom(null);
        }

        if (toPresetName != null && !toPresetName.isEmpty()) {
          cmiTeleportType.setTo(ParticleManager.CMIPresetAnimations.getByName(toPresetName));
        } else {
          cmiTeleportType.setTo(null);
        }
      }

      configReader.save();
    }
  }

  public void show(String string, Player player) {
    CMIPEAnimationInterface cmipeAnimationInterface = this.getParticleAnimation(string);
    this.show(cmipeAnimationInterface, player);
  }

  public void show(CMIPEAnimationInterface cmipeAnimationInterface, Location location) {
    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          if (cmipeAnimationInterface != null) {
            CMIVisualEffect cmiVisualEffect =
                new CMIVisualEffect(location, cmipeAnimationInterface);
            cmiVisualEffect.setUntil(
                (long)
                    (System.currentTimeMillis() + cmipeAnimationInterface.getDuration() * 1000.0));
            cmiVisualEffect.show();
          }
        });
  }

  public void show(CMIPEAnimationInterface cmipeAnimationInterface, Player player) {
    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          if (cmipeAnimationInterface != null) {
            CMIVisualEffect cmiVisualEffect =
                new CMIVisualEffect(
                    cmipeAnimationInterface.getPlayerMove() == null
                        ? player
                        : cmipeAnimationInterface.getPlayerMove(),
                    cmipeAnimationInterface);
            cmiVisualEffect.setUntil(
                (long)
                    (System.currentTimeMillis() + cmipeAnimationInterface.getDuration() * 1000.0));
            cmiVisualEffect.show();
          }
        });
  }

  public CMIPEAnimationInterface getParticleAnimation(String string) {
    if (string != null && !string.isEmpty()) {
      if (string.startsWith("preset:")) {
        String presetName = string.substring("preset:".length()).split(";")[0];
        ParticleManager.CMIPresetAnimations cmiPresetAnimations =
            ParticleManager.CMIPresetAnimations.getByName(presetName);
        if (cmiPresetAnimations != null) {
          string = string.substring("preset:".length() + presetName.length());
          string = string.endsWith(";") ? string.substring(0, string.length() - 1) : string;
          string = cmiPresetAnimations.getAnimString() + ";" + string;
        }
      }

      string = string.toLowerCase();
      ArrayList<String> arrayList = new ArrayList();
      LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap();
      if (string.contains(";")) {
        arrayList.add(string.split(";", 2)[0]);
        string = string.split(";", 2)[1];
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
          String effectMatch = matcher.group();
          String effectValue = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
          if (effectValue != null && !effectValue.isEmpty()) {
            string = string.replace(effectMatch, "");
            linkedHashMap.put("effect", effectValue);
          }
        }

        arrayList.addAll(Arrays.asList(string.split(";")));
      } else {
        Matcher matcher = this.pattern.matcher(string);
        if (matcher.find()) {
          String effectMatch = matcher.group();
          String effectValue = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
          if (effectValue != null && !effectValue.isEmpty()) {
            string = string.replace(effectMatch, "");
            linkedHashMap.put("effect", effectValue);
          }
        }

        arrayList.add(string);
      }

      if (arrayList.isEmpty()) {
        return null;
      }

      String animationName = ((String) arrayList.get(0)).toLowerCase();
      if (!arrayList.isEmpty()) {
        for (String argument : arrayList.subList(1, arrayList.size())) {
          if (argument.contains(":")) {
            String[] strings = argument.split(":", 2);
            linkedHashMap.put(strings[0], strings[1]);
          } else {
            linkedHashMap.put(argument, "true");
          }
        }
      }

      String animationKey = animationName;
      switch (animationName.hashCode()) {
        case -1360216880:
          if (animationKey.equals("circle")) {
            CMIPECircle cmipeCircle = new CMIPECircle();

            for (Entry entry : linkedHashMap.entrySet()) {
              label518:
              {
                label517:
                {
                  label516:
                  {
                    label515:
                    {
                      label514:
                      {
                        label513:
                        {
                          label512:
                          {
                            label511:
                            {
                              label510:
                              {
                                label509:
                                {
                                  label508:
                                  {
                                    label507:
                                    {
                                      label506:
                                      {
                                        label505:
                                        {
                                          label504:
                                          {
                                            label503:
                                            {
                                              label502:
                                              {
                                                label555:
                                                {
                                                  String optionKey;
                                                  switch ((optionKey = (String) entry.getKey())
                                                      .hashCode()) {
                                                    case -1992012396:
                                                      if (!optionKey.equals("duration")) {
                                                        continue;
                                                      }
                                                      break label507;
                                                    case -1800314195:
                                                      if (!optionKey.equals("particles")) {
                                                        continue;
                                                      }
                                                      break label512;
                                                    case -1531374038:
                                                      if (!optionKey.equals("maxmovedistance")) {
                                                        continue;
                                                      }
                                                      break label510;
                                                    case -1473474602:
                                                      if (!optionKey.equals("hidewhenvanished")) {
                                                        continue;
                                                      }
                                                      break label516;
                                                    case -1364013995:
                                                      if (!optionKey.equals("center")) {
                                                        continue;
                                                      }
                                                      break label503;
                                                    case -1306084975:
                                                      if (!optionKey.equals("effect")) {
                                                        continue;
                                                      }
                                                      break label555;
                                                    case -1232660769:
                                                      if (!optionKey.equals("yawchange")) {
                                                        continue;
                                                      }
                                                      break label515;
                                                    case -1097452790:
                                                      if (!optionKey.equals("locked")) {
                                                        continue;
                                                      }
                                                      break label518;
                                                    case -1070642362:
                                                      if (!optionKey.equals("mmdist")) {
                                                        continue;
                                                      }
                                                      break label510;
                                                    case -1019779949:
                                                      if (!optionKey.equals("offset")) {
                                                        continue;
                                                      }
                                                      break label509;
                                                    case -987978461:
                                                      if (!optionKey.equals("pitchc")) {
                                                        continue;
                                                      }
                                                      break label514;
                                                    case -938578798:
                                                      if (!optionKey.equals("radius")) {
                                                        continue;
                                                      }
                                                      break label504;
                                                    case -925180581:
                                                      if (!optionKey.equals("rotate")) {
                                                        continue;
                                                      }
                                                      break label511;
                                                    case -892481938:
                                                      if (!optionKey.equals("static")) {
                                                        continue;
                                                      }
                                                      break label518;
                                                    case -880905839:
                                                      if (!optionKey.equals("target")) {
                                                        continue;
                                                      }
                                                      break label502;
                                                    case -576493840:
                                                      if (!optionKey.equals("pitchchange")) {
                                                        continue;
                                                      }
                                                      break label514;
                                                    case -67596961:
                                                      if (!optionKey.equals("tickinter")) {
                                                        continue;
                                                      }
                                                      break label513;
                                                    case -40300712:
                                                      if (!optionKey.equals("rotating")) {
                                                        continue;
                                                      }
                                                      break label511;
                                                    case 97:
                                                      if (!optionKey.equals("a")) {
                                                        continue;
                                                      }
                                                      break label505;
                                                    case 99:
                                                      if (!optionKey.equals("c")) {
                                                        continue;
                                                      }
                                                      break;
                                                    case 114:
                                                      if (!optionKey.equals("r")) {
                                                        continue;
                                                      }
                                                      break label504;
                                                    case 3185:
                                                      if (!optionKey.equals("ct")) {
                                                        continue;
                                                      }
                                                      break label503;
                                                    case 3233:
                                                      if (!optionKey.equals("ef")) {
                                                        continue;
                                                      }
                                                      break label555;
                                                    case 3493:
                                                      if (!optionKey.equals("mr")) {
                                                        continue;
                                                      }
                                                      break label508;
                                                    case 3633:
                                                      if (!optionKey.equals("rc")) {
                                                        continue;
                                                      }
                                                      break label506;
                                                    case 3710:
                                                      if (!optionKey.equals("tr")) {
                                                        continue;
                                                      }
                                                      break label502;
                                                    case 99841:
                                                      if (!optionKey.equals("dur")) {
                                                        continue;
                                                      }
                                                      break label507;
                                                    case 103751:
                                                      if (!optionKey.equals("hwv")) {
                                                        continue;
                                                      }
                                                      break label516;
                                                    case 109935:
                                                      if (!optionKey.equals("off")) {
                                                        continue;
                                                      }
                                                      break label509;
                                                    case 113111:
                                                      if (!optionKey.equals("rot")) {
                                                        continue;
                                                      }
                                                      break label511;
                                                    case 119407:
                                                      if (!optionKey.equals("yaw")) {
                                                        continue;
                                                      }

                                                      try {
                                                        double yawValue =
                                                            Double.parseDouble(
                                                                (String) entry.getValue());
                                                        cmipeCircle.setYaw(yawValue);
                                                      } catch (Exception exception) {
                                                        Player player =
                                                            CMIUser.getOnlinePlayer(
                                                                (String) entry.getValue());
                                                        if (player != null) {
                                                          cmipeCircle.setYaw(
                                                              player.getLocation().getYaw());
                                                          cmipeCircle.setFollowYaw(true);
                                                        }
                                                      }
                                                      continue;
                                                    case 3138151:
                                                      if (!optionKey.equals("fdir")) {
                                                        continue;
                                                      }
                                                      break label517;
                                                    case 3344256:
                                                      if (!optionKey.equals("maxd")) {
                                                        continue;
                                                      }
                                                      break label510;
                                                    case 3357649:
                                                      if (optionKey.equals("move")) {
                                                        String[] moveParts =
                                                            ((String) entry.getValue()).split(",");
                                                        if (moveParts.length == 3) {
                                                          try {
                                                            double moveX =
                                                                Double.parseDouble(moveParts[0]);
                                                            double moveY =
                                                                Double.parseDouble(moveParts[1]);
                                                            double moveZ =
                                                                Double.parseDouble(moveParts[2]);
                                                            cmipeCircle.setMove(
                                                                new Vector(moveX, moveY, moveZ));
                                                          } catch (Exception exception2) {
                                                          }
                                                        }

                                                        Double moveMultiplier = null;
                                                        String moveTargetName =
                                                            (String) entry.getValue();
                                                        if (((String) entry.getValue())
                                                            .contains(",")) {
                                                          try {
                                                            moveMultiplier =
                                                                Double.parseDouble(
                                                                    ((String) entry.getValue())
                                                                        .split(",")[1]);
                                                            moveTargetName =
                                                                ((String) entry.getValue())
                                                                    .split(",")[0];
                                                          } catch (Exception exception3) {
                                                          }
                                                        }

                                                        Player moveTarget =
                                                            CMIUser.getOnlinePlayer(moveTargetName);
                                                        if (moveTarget != null) {
                                                          Vector vector =
                                                              moveTarget
                                                                  .getLocation()
                                                                  .getDirection();
                                                          if (moveMultiplier != null) {
                                                            vector =
                                                                vector.multiply(moveMultiplier);
                                                          }

                                                          cmipeCircle.setMove(vector);
                                                        }
                                                      }
                                                      continue;
                                                    case 3433459:
                                                      if (!optionKey.equals("part")) {
                                                        continue;
                                                      }
                                                      break label512;
                                                    case 3701716:
                                                      if (!optionKey.equals("yawc")) {
                                                        continue;
                                                      }
                                                      break label515;
                                                    case 11684546:
                                                      if (!optionKey.equals("radiuschange")) {
                                                        continue;
                                                      }
                                                      break label506;
                                                    case 92960979:
                                                      if (!optionKey.equals("angle")) {
                                                        continue;
                                                      }
                                                      break label505;
                                                    case 94842723:
                                                      if (!optionKey.equals("color")) {
                                                        continue;
                                                      }
                                                      break;
                                                    case 97445748:
                                                      if (!optionKey.equals("fixed")) {
                                                        continue;
                                                      }
                                                      break label518;
                                                    case 106677056:
                                                      if (optionKey.equals("pitch")) {
                                                        try {
                                                          double pitchValue =
                                                              Double.parseDouble(
                                                                  (String) entry.getValue());
                                                          cmipeCircle.setPitch(pitchValue);
                                                        } catch (Exception exception4) {
                                                          Player pitchTarget =
                                                              CMIUser.getOnlinePlayer(
                                                                  (String) entry.getValue());
                                                          if (pitchTarget != null) {
                                                            cmipeCircle.setPitch(
                                                                pitchTarget
                                                                    .getLocation()
                                                                    .getPitch());
                                                            cmipeCircle.setFollowPitch(true);
                                                          }
                                                        }
                                                      }
                                                      continue;
                                                    case 110778151:
                                                      if (!optionKey.equals("twist")) {
                                                        continue;
                                                      }
                                                      break label511;
                                                    case 292517622:
                                                      if (!optionKey.equals("maxradius")) {
                                                        continue;
                                                      }
                                                      break label508;
                                                    case 558713186:
                                                      if (!optionKey.equals("tickinterval")) {
                                                        continue;
                                                      }
                                                      break label513;
                                                    case 570418373:
                                                      if (!optionKey.equals("interval")) {
                                                        continue;
                                                      }
                                                      break label513;
                                                    case 1359098169:
                                                      if (!optionKey.equals("tinterval")) {
                                                        continue;
                                                      }
                                                      break label513;
                                                    case 2126275310:
                                                      if (!optionKey.equals("followdirection")) {
                                                        continue;
                                                      }
                                                      break label517;
                                                    default:
                                                      continue;
                                                  }

                                                  if (((String) entry.getValue()).contains(",")) {
                                                    String[] colorParts =
                                                        ((String) entry.getValue()).split(",");
                                                    if (colorParts.length == 3) {
                                                      try {
                                                        int red = Integer.parseInt(colorParts[0]);
                                                        int green = Integer.parseInt(colorParts[1]);
                                                        int blue = Integer.parseInt(colorParts[2]);
                                                        cmipeCircle.setColor(
                                                            Color.fromRGB(red, green, blue));
                                                        continue;
                                                      } catch (Exception exception5) {
                                                      }
                                                    }
                                                  }

                                                  if (((String) entry.getValue()).contains("{#")
                                                      && ((String) entry.getValue())
                                                          .contains("}")) {
                                                    CMIChatColor cmiChatColor =
                                                        CMIChatColor.getColor(
                                                            (String) entry.getValue());
                                                    if (cmiChatColor != null) {
                                                      cmipeCircle.setColor(cmiChatColor);
                                                      continue;
                                                    }
                                                  }

                                                  CMIChatColor customColor =
                                                      CMIChatColor.getByCustomName(
                                                          (String) entry.getValue());
                                                  if (customColor != null) {
                                                    cmipeCircle.setColor(customColor);
                                                    continue;
                                                  }

                                                  label468:
                                                  {
                                                    label467:
                                                    {
                                                      String rainbowMode;
                                                      switch ((rainbowMode =
                                                              (String) entry.getValue())
                                                          .hashCode()) {
                                                        case -1936000066:
                                                          if (!rainbowMode.equals(
                                                              "raindbowfromstart")) {
                                                            continue;
                                                          }
                                                          break;
                                                        case -1310498968:
                                                          if (!rainbowMode.equals(
                                                              "raindbowstatic")) {
                                                            continue;
                                                          }
                                                          break label467;
                                                        case 114:
                                                          if (!rainbowMode.equals("r")) {
                                                            continue;
                                                          }
                                                          break label468;
                                                        case 3649:
                                                          if (!rainbowMode.equals("rs")) {
                                                            continue;
                                                          }
                                                          break label467;
                                                        case 112831:
                                                          if (!rainbowMode.equals("rfs")) {
                                                            continue;
                                                          }
                                                          break;
                                                        case 116151418:
                                                          if (!rainbowMode.equals("raindbow")) {
                                                            continue;
                                                          }
                                                          break label468;
                                                        default:
                                                          continue;
                                                      }

                                                      cmipeCircle.setRaindbowColorFromStart(true);
                                                      continue;
                                                    }

                                                    cmipeCircle.setRaindbowColorStatic(true);
                                                    continue;
                                                  }

                                                  cmipeCircle.setRaindbowColor(true);
                                                  continue;
                                                }

                                                CMIEffect cmiEffect =
                                                    CMIParticle.getCMIEffect(
                                                        (String) entry.getValue());
                                                if (cmiEffect != null) {
                                                  cmipeCircle.setCMIEffect(cmiEffect);
                                                }
                                                continue;
                                              }

                                              Player movePlayer =
                                                  CMIUser.getOnlinePlayer(
                                                      (String) entry.getValue());
                                              if (movePlayer != null) {
                                                cmipeCircle.setPlayerMove(movePlayer);
                                              }
                                              continue;
                                            }

                                            String[] centerParts =
                                                ((String) entry.getValue()).split(",");
                                            if (centerParts.length != 4) {
                                              Player centerPlayer =
                                                  CMIUser.getOnlinePlayer(
                                                      (String) entry.getValue());
                                              if (centerPlayer != null) {
                                                cmipeCircle.setCenter(centerPlayer.getLocation());
                                              }
                                            } else {
                                              try {
                                                World world =
                                                    this.plugin
                                                        .getUtilManager()
                                                        .getWorld(centerParts[0]);
                                                double centerX = Double.parseDouble(centerParts[1]);
                                                double centerY = Double.parseDouble(centerParts[2]);
                                                double centerZ = Double.parseDouble(centerParts[3]);
                                                if (world != null) {
                                                  cmipeCircle.setCenter(
                                                      new Location(
                                                          world, centerX, centerY, centerZ));
                                                }
                                              } catch (Exception exception6) {
                                              }
                                            }
                                            continue;
                                          }

                                          try {
                                            double radiusValue =
                                                Double.parseDouble((String) entry.getValue());
                                            cmipeCircle.setRadius(radiusValue);
                                          } catch (Exception exception7) {
                                          }
                                          continue;
                                        }

                                        try {
                                          double startingAngleValue =
                                              Double.parseDouble((String) entry.getValue());
                                          cmipeCircle.setStartingAngle((short) startingAngleValue);
                                        } catch (Exception exception8) {
                                        }
                                        continue;
                                      }

                                      try {
                                        double radiusChangeValue =
                                            Double.parseDouble((String) entry.getValue());
                                        cmipeCircle.setRadiusChange(radiusChangeValue);
                                      } catch (Exception exception9) {
                                      }
                                      continue;
                                    }

                                    try {
                                      double durationValue =
                                          Double.parseDouble((String) entry.getValue());
                                      cmipeCircle.setDuration(durationValue);
                                    } catch (Exception exception10) {
                                    }
                                    continue;
                                  }

                                  try {
                                    double maxRadiusValue =
                                        Double.parseDouble((String) entry.getValue());
                                    cmipeCircle.setMaxRadius(maxRadiusValue);
                                  } catch (Exception exception11) {
                                  }
                                  continue;
                                }

                                String[] offsetParts = ((String) entry.getValue()).split(",");
                                if (offsetParts.length == 3) {
                                  try {
                                    double offsetX = Double.parseDouble(offsetParts[0]);
                                    double offsetY = Double.parseDouble(offsetParts[1]);
                                    double offsetZ = Double.parseDouble(offsetParts[2]);
                                    cmipeCircle.setOffset(new Vector(offsetX, offsetY, offsetZ));
                                  } catch (Exception exception12) {
                                  }
                                }
                                continue;
                              }

                              try {
                                double maxMoveDistanceValue =
                                    Double.parseDouble((String) entry.getValue());
                                cmipeCircle.setMaxMoveDistance(maxMoveDistanceValue);
                              } catch (Exception exception13) {
                              }
                              continue;
                            }

                            try {
                              cmipeCircle.setRotating(true);
                            } catch (Exception exception14) {
                            }
                            continue;
                          }

                          try {
                            int timesValue = Integer.parseInt((String) entry.getValue());
                            cmipeCircle.setTimes(timesValue);
                          } catch (Exception exception15) {
                          }
                          continue;
                        }

                        try {
                          int intervalValue = Integer.parseInt((String) entry.getValue());
                          if (intervalValue < 1) {
                            intervalValue = 1;
                          }

                          cmipeCircle.setInterval(intervalValue);
                        } catch (Exception exception16) {
                        }
                        continue;
                      }

                      try {
                        double pitchAdjustValue = Double.parseDouble((String) entry.getValue());
                        cmipeCircle.setPitchAdjust(pitchAdjustValue);
                      } catch (Exception exception17) {
                      }
                      continue;
                    }

                    try {
                      double yawAdjustValue = Double.parseDouble((String) entry.getValue());
                      cmipeCircle.setYawAdjust(yawAdjustValue);
                    } catch (Exception exception18) {
                    }
                    continue;
                  }

                  cmipeCircle.setHideWithVanish(true);
                  continue;
                }

                cmipeCircle.setFollowDirection(true);
                continue;
              }

              cmipeCircle.setFixedLocation(true);
            }

            return cmipeCircle;
          }
          break;
        case -562711993:
          if (animationKey.equals("firework")) {
            CMIPEFireWork cmipeFireWork = new CMIPEFireWork();

            label400:
            for (Entry fireworkEntry : linkedHashMap.entrySet()) {
              label552:
              {
                String fireworkKey;
                switch ((fireworkKey = (String) fireworkEntry.getKey()).hashCode()) {
                  case -1364013995:
                    if (fireworkKey.equals("center")) {
                      String[] fireworkCenterParts = ((String) fireworkEntry.getValue()).split(",");
                      if (fireworkCenterParts.length != 4) {
                        Player fireworkCenterPlayer =
                            CMIUser.getOnlinePlayer((String) fireworkEntry.getValue());
                        if (fireworkCenterPlayer != null) {
                          cmipeFireWork.setCenter(fireworkCenterPlayer.getLocation());
                        }
                      } else {
                        try {
                          World centerWorld =
                              this.plugin.getUtilManager().getWorld(fireworkCenterParts[0]);
                          double centerX = Double.parseDouble(fireworkCenterParts[1]);
                          double centerY = Double.parseDouble(fireworkCenterParts[2]);
                          double centerZ = Double.parseDouble(fireworkCenterParts[3]);
                          if (centerWorld != null) {
                            cmipeFireWork.setCenter(
                                new Location(centerWorld, centerX, centerY, centerZ));
                          }
                        } catch (Exception exception19) {
                        }
                      }
                    }
                    continue;
                  case -1019779949:
                    if (!fireworkKey.equals("offset")) {
                      continue;
                    }
                    break label552;
                  case -771931656:
                    if (fireworkKey.equals("flicker")) {
                      cmipeFireWork.setFlicker(true);
                    }
                    continue;
                  case 116:
                    if (!fireworkKey.equals("t")) {
                      continue;
                    }
                    break;
                  case 3118:
                    if (fireworkKey.equals("c1")) {
                      CMIChatColor fadeFromColor =
                          CMIChatColor.getColor((String) fireworkEntry.getValue());
                      if (fadeFromColor != null) {
                        cmipeFireWork.setFadeFrom(fadeFromColor.getRGBColor());
                      }
                    }
                    continue;
                  case 3119:
                    if (fireworkKey.equals("c2")) {
                      CMIChatColor fadeToColor =
                          CMIChatColor.getColor((String) fireworkEntry.getValue());
                      if (fadeToColor != null) {
                        cmipeFireWork.setFadeTo(fadeToColor.getRGBColor());
                      }
                    }
                    continue;
                  case 109935:
                    if (!fireworkKey.equals("off")) {
                      continue;
                    }
                    break label552;
                  case 3575610:
                    if (!fireworkKey.equals("type")) {
                      continue;
                    }
                    break;
                  case 110621190:
                    if (fireworkKey.equals("trail")) {
                      cmipeFireWork.setTrail(true);
                    }
                  default:
                    continue;
                }

                Type[] types;
                int typeCount = (types = Type.values()).length;

                for (int typeIndex = 0; typeIndex < typeCount; typeIndex++) {
                  Type type = types[typeIndex];
                  if (type.toString()
                      .replace("_", "")
                      .equalsIgnoreCase((String) fireworkEntry.getValue())) {
                    cmipeFireWork.setType(type);
                    continue label400;
                  }
                }
                continue;
              }

              String[] fireworkOffsetParts = ((String) fireworkEntry.getValue()).split(",");
              if (fireworkOffsetParts.length == 3) {
                try {
                  double offsetX = Double.parseDouble(fireworkOffsetParts[0]);
                  double offsetY = Double.parseDouble(fireworkOffsetParts[1]);
                  double offsetZ = Double.parseDouble(fireworkOffsetParts[2]);
                  cmipeFireWork.setOffset(new Vector(offsetX, offsetY, offsetZ));
                } catch (Exception exception20) {
                }
              }
            }

            return cmipeFireWork;
          }
      }

      return null;
    } else {
      return null;
    }
  }

  public enum CMIPresetAnimations {
    TotemHalo(
        "circle;effect:reddust;c:255,255,10;twist;part:3;offset:0,2,0;pitch:90;radius:0.3;interval:2"),
    Healing("circle;effect:heart;dur:0.1;part:1;offset:0,1.7,0;radius:0.3"),
    GlyphHead(
        "circle;effect:flying_glyph;dur:5;pitchc:15;part:10;offset:0,1.7,0;radius:0.5;yawc:12;color:rs;pitch:90"),
    tpaWarmup(
        "circle;effect:flying_glyph;dur:5;pitchc:15;part:10;offset:0,1.7,0;radius:0.5;yawc:12;color:rs;pitch:90"),
    GColumn("circle;c:0,255,0;twist;part:5;r:0.75;pitch:90;move:0,0.1,0;rc:-0.02"),
    SmallBoop(
        "circle;effect:flying_glyph;yaw:[playerName];pitch:[playerName];r:0.1;part:3;rc:0.03;mr:0.3;twist"),
    HologramInteraction(
        "circle;effect:flying_glyph;yaw:[playerName];pitch:[playerName];r:0.1;part:3;rc:0.03;mr:0.3;twist"),
    HologramNewInteraction(
        "circle;effect:reddust;r:0;part:2;rc:0.2;mr:1;color:rs;yaw:[playerName];a:90"),
    HologramHover("circle;effect:crit;r:0;part:1;"),
    TpUp("circle;c:200,50,210;twist;part:5;r:0.5;pitch:90;move:0,0.33,0;offset:0,-0.2,0"),
    TpDown("circle;c:150,50,10;part:5;r:0.5;pitch:90;move:0,-0.33,0;offset:0,2.2,0"),
    custom1("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom2("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom3("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom4("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom5("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom6("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom7("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom8("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom9("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom10("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom11("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom12("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom13("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom14("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom15("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom16("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom17("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom18("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom19("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom20("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom21("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom22("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom23("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom24("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom25("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom26("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom27("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom28("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom29("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4"),
    custom30("circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:4");

    private String string = "";
    private boolean enabled = true;

    CMIPresetAnimations(String animString) {
      this.string = animString;
    }

    public CMIPEAnimationInterface getInter() {
      return CMI.getInstance().getParticleManager().getParticleAnimation(this.string);
    }

    public CMIPEAnimationInterface getInter(Player player) {
      Snd snd = new Snd(player, player);
      String resolvedAnim = CMILC.updateSnd(snd, this.string);
      return CMI.getInstance()
          .getParticleManager()
          .getParticleAnimation(this.isEnabled() ? resolvedAnim : "");
    }

    public void show(Location location) {
      CMI.getInstance()
          .getParticleManager()
          .show(CMI.getInstance().getParticleManager().getParticleAnimation(this.string), location);
    }

    public void show(Player player) {
      if (player != null && player.isOnline()) {
        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
        if (cmiUser != null && !cmiUser.isVanished()) {
          CMI.getInstance()
              .getParticleManager()
              .show(
                  CMI.getInstance().getParticleManager().getParticleAnimation(this.string), player);
        }
      }
    }

    public static ParticleManager.CMIPresetAnimations getByName(String name) {
      ParticleManager.CMIPresetAnimations[] cmiPresetAnimationses;
      int value = (cmiPresetAnimationses = values()).length;

      for (int index = 0; index < value; index++) {
        ParticleManager.CMIPresetAnimations cmiPresetAnimations = cmiPresetAnimationses[index];
        if (cmiPresetAnimations.toString().equalsIgnoreCase(name)) {
          return cmiPresetAnimations;
        }
      }

      return null;
    }

    public void setAnimString(String animString) {
      this.string = animString;
    }

    public String getAnimString() {
      return this.string;
    }

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean flag) {
      this.enabled = flag;
    }
  }

  private class taskCancel {
    private CMITask task = null;

    taskCancel(CMITask cmiTask) {
      this.task = cmiTask;
    }

    public void setTask(CMITask cmiTask) {
      this.task = cmiTask;
    }

    public void cancel() {
      if (this.task != null) {
        this.task.cancel();
      }
    }
  }
}
