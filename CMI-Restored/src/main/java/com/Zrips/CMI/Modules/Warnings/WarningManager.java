package com.Zrips.CMI.Modules.Warnings;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIJson;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.entity.Player;

public class WarningManager {
  private CMI plugin;
  private LinkedHashMap<Integer, List<String>> commands = new LinkedHashMap<>();
  private HashMap<String, CMIWarningCategory> Categories = new HashMap<>();
  private HashMap<UUID, List<CMIPlayerWarning>> warnings = new HashMap<>();

  @Nullable
  public List<CMIPlayerWarning> getWarnings(UUID uuid) {
    var list = this.warnings.get(uuid);
    if (list != null) {
      list.removeIf(
          object ->
              object.getGivenAt() + object.getCategory().getLifeTime()
                  < System.currentTimeMillis());
    }

    return list;
  }

  public List<CMIPlayerWarning> getOrCreateWarnings(UUID uuid) {
    return this.warnings.computeIfAbsent(uuid, key -> new ArrayList<>());
  }

  public void setWarnings(UUID uuid, List<CMIPlayerWarning> list) {
    if (list != null && !list.isEmpty()) {
      this.warnings.put(uuid, list);
    } else {
      this.warnings.remove(uuid);
    }
  }

  public WarningManager(CMI cmi) {
    this.plugin = cmi;
  }

  public String serializeWarnings(List<CMIPlayerWarning> list) {
    if (list == null) {
      return null;
    }

    HashMap<Long, ArrayList> hashMap = new HashMap();

    for (CMIPlayerWarning cmiPlayerWarning : list) {
      ArrayList<String> arrayList = new ArrayList();
      arrayList.add(cmiPlayerWarning.getGivenBy());
      if (cmiPlayerWarning.getCategory() != null
          && !cmiPlayerWarning
              .getCategory()
              .getName()
              .equalsIgnoreCase(this.getDefaultCategory().getName())) {
        arrayList.add(cmiPlayerWarning.getCategory().getName());
      }

      if (cmiPlayerWarning.getReason() != null) {
        arrayList.add(cmiPlayerWarning.getReason());
      }

      hashMap.put(cmiPlayerWarning.getGivenAt(), arrayList);
    }

    return CMIJson.serialize(hashMap);
  }

  public List<CMIPlayerWarning> deserializeWarnings(String string) {
    ArrayList<CMIPlayerWarning> arrayList = new ArrayList();
    var hashMap = CMIJson.deserialize(string);

    for (Entry entry : hashMap.entrySet()) {
      long longValue = 0L;
      List list = new ArrayList();

      try {
        longValue = CMIJson.getFromObject(entry.getKey(), 0L);
        list = CMIJson.getFromObject(entry.getValue(), new ArrayList());
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      if (longValue > 0L) {
        String givenBy = !list.isEmpty() ? (String) list.get(0) : null;
        String categoryName = list.size() > 1 ? (String) list.get(1) : null;
        String reason = list.size() > 2 ? (String) list.get(2) : null;
        CMIPlayerWarning cmiPlayerWarning = new CMIPlayerWarning();
        cmiPlayerWarning.setGivenAt(longValue);
        cmiPlayerWarning.setGivenBy(givenBy);
        CMIWarningCategory cmiWarningCategory =
            this.plugin.getWarningManager().getCategory(categoryName);
        cmiPlayerWarning.setCategory(
            cmiWarningCategory == null
                ? this.plugin.getWarningManager().getDefaultCategory()
                : cmiWarningCategory);
        cmiPlayerWarning.setReason(
            reason != null && !reason.isEmpty()
                ? reason
                : (cmiWarningCategory == null && categoryName != null ? categoryName : null));
        arrayList.add(cmiPlayerWarning);
      }
    }

    return arrayList;
  }

  public void loadConfig() {
    Long startTime = System.currentTimeMillis();
    this.commands.clear();
    this.Categories.clear();
    this.warnings.clear();
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    CMIWarningCategory cmiWarningCategory = new CMIWarningCategory("Default");
    configReader.addComment(
        "Warnings.Default",
        new String[] {
          "Should be always present. This will be used when category of warning isint being defined"
              + " when warning player"
        });
    configReader.addComment(
        "Warnings.Default.LifeTime",
        new String[] {"Time in seconds for how long this warning applies to player"});
    cmiWarningCategory.setLifeTime(configReader.get("Warnings.Default.LifeTime", 86400) * 1000L);
    configReader.addComment(
        "Warnings.Default.Points",
        new String[] {"Warning point amount player receives for warning"});
    cmiWarningCategory.setPoints(configReader.get("Warnings.Default.Points", 1));
    configReader.addComment(
        "Warnings.Default.DefaultReason",
        new String[] {"Default reason used when reason is not provided when warning player"});
    cmiWarningCategory.setDefaultReason(
        configReader.get("Warnings.Default.DefaultReason", "&7Violated server rules"));
    this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
    if (!configReader.getC().isConfigurationSection("Warnings.Categories")) {
      cmiWarningCategory = new CMIWarningCategory("Swear");
      cmiWarningCategory.setLifeTime(
          configReader.get("Warnings.Categories.Swear.LifeTime", 86400) * 1000L);
      cmiWarningCategory.setPoints(configReader.get("Warnings.Categories.Swear.Points", 3));
      cmiWarningCategory.setDefaultReason(
          configReader.get("Warnings.Categories.Swear.DefaultReason", "&7Swearing"));
      this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
      cmiWarningCategory = new CMIWarningCategory("Grief");
      cmiWarningCategory.setLifeTime(
          configReader.get("Warnings.Categories.Grief.LifeTime", 86400) * 1000L);
      cmiWarningCategory.setPoints(configReader.get("Warnings.Categories.Grief.Points", 10));
      cmiWarningCategory.setDefaultReason(
          configReader.get("Warnings.Categories.Grief.DefaultReason", "&7Griefing"));
      this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
      cmiWarningCategory = new CMIWarningCategory("Bug");
      cmiWarningCategory.setLifeTime(
          configReader.get("Warnings.Categories.Bug.LifeTime", 86400) * 1000L);
      cmiWarningCategory.setPoints(configReader.get("Warnings.Categories.Bug.Points", 30));
      cmiWarningCategory.setDefaultReason(
          configReader.get("Warnings.Categories.Bug.DefaultReason", "&7Using bugs"));
      this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
      cmiWarningCategory = new CMIWarningCategory("Cheat");
      cmiWarningCategory.setLifeTime(
          configReader.get("Warnings.Categories.Cheat.LifeTime", 86400) * 1000L);
      cmiWarningCategory.setPoints(configReader.get("Warnings.Categories.Cheat.Points", 50));
      cmiWarningCategory.setDefaultReason(
          configReader.get("Warnings.Categories.Cheat.DefaultReason", "&7Using cheats"));
      this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
    } else {
      for (String string :
          configReader.getC().getConfigurationSection("Warnings.Categories").getKeys(false)) {
        try {
          cmiWarningCategory = new CMIWarningCategory(string);
          cmiWarningCategory.setLifeTime(
              configReader.get("Warnings.Categories." + string + ".LifeTime", 600) * 1000L);
          cmiWarningCategory.setPoints(
              configReader.get("Warnings.Categories." + string + ".Points", 5));
          cmiWarningCategory.setDefaultReason(
              configReader.get(
                  "Warnings.Categories." + string + ".DefaultReason", "&7Violated server rules"));
          this.Categories.put(cmiWarningCategory.getName().toLowerCase(), cmiWarningCategory);
        } catch (Exception exception) {
        }
      }
    }

    this.plugin.loadMessage(
        this.Categories.size(), "warning categories", System.currentTimeMillis() - startTime);
    startTime = System.currentTimeMillis();
    if (!configReader.getC().isConfigurationSection("Warnings.Perform")) {
      this.commands.put(
          3,
          configReader.get(
              "Warnings.Perform.3",
              Arrays.asList(
                  "cmi mute [playerName] 10m",
                  "cmi msg [playerName] !&cMuted for &710 &cminutes for getting &73 &cwarnings!")));
      this.commands.put(
          5,
          configReader.get(
              "Warnings.Perform.5",
              Arrays.asList("cmi kick [playerName] &cKicked for getting 5 warnings!")));
      this.commands.put(
          10,
          configReader.get(
              "Warnings.Perform.10",
              Arrays.asList(
                  "cmi tempban [playerName] 5m &cTemporary banned for getting 10 warnings!")));
    } else {
      for (String warnsKey :
          configReader.getC().getConfigurationSection("Warnings.Perform").getKeys(false)) {
        try {
          this.commands.put(
              Integer.parseInt(warnsKey),
              configReader.get("Warnings.Perform." + warnsKey, Arrays.asList("")));
        } catch (Exception exception2) {
        }
      }
    }

    this.plugin.loadMessage(
        this.commands.size(), "warning commands", System.currentTimeMillis() - startTime);
  }

  public CMIWarningCategory getDefaultCategory() {
    return this.Categories.get("default");
  }

  public CMIWarningCategory getCategory(String string) {
    return string == null ? null : this.Categories.get(string.toLowerCase());
  }

  public List<String> getCommandsByWarns(Player player, int value, int maxWarns) {
    ArrayList arrayList = new ArrayList();
    Snd snd = new Snd();
    snd.setSource(player);
    snd.setTarget(player);
    Integer integer = 0;

    for (var entry : this.commands.entrySet()) {
      if ((Integer) entry.getKey() >= value
          && (Integer) entry.getKey() <= maxWarns
          && (Integer) entry.getKey() > integer) {
        integer = (Integer) entry.getKey();
      }
    }

    if (integer > 0 && this.commands.containsKey(integer)) {
      arrayList.addAll(
          this.plugin.getLM().updateSnd(snd, new ArrayList<>(this.commands.get(integer))));
    }

    return arrayList;
  }

  public List<String> getCommandsByWarns(int value) {
    return this.commands.get(value) != null ? new ArrayList<>(this.commands.get(value)) : null;
  }

  public HashMap<String, CMIWarningCategory> getCategories() {
    return this.Categories;
  }
}
