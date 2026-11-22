package com.Zrips.CMI.Modules.PlayerMeta;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerMeta {
  private static ConcurrentHashMap<UUID, PlayerMeta> cacheMap = new ConcurrentHashMap<>();
  private HashMap<String, PlayerMeta.metaValues> map = new HashMap<>();

  public static PlayerMeta getMeta(UUID uuid) {
    return cacheMap.computeIfAbsent(uuid, key -> new PlayerMeta());
  }

  public static PlayerMeta getMetaRaw(UUID uuid) {
    return cacheMap.get(uuid);
  }

  @Deprecated
  public void add(String string, String metaValue, String unusedValue) {
    this.add(string, metaValue);
  }

  public void add(String string, String metaValue) {
    this.map
        .computeIfAbsent(string.toLowerCase(), key -> new PlayerMeta.metaValues())
        .setValue(metaValue);
  }

  public void remove(String string) {
    this.map.remove(string.toLowerCase());
  }

  public String getValue(String string) {
    PlayerMeta.metaValues foundValues = this.map.get(string.toLowerCase());
    return foundValues == null ? "" : foundValues.getValue();
  }

  public boolean containsValues() {
    return !this.map.isEmpty();
  }

  public HashMap<String, PlayerMeta.metaValues> getMap() {
    return this.map;
  }

  public HashMap<String, String> getMapForSave() {
    return this.map.entrySet().stream()
        .collect(
            Collectors.toMap(
                entry -> ((String) entry.getKey()).toLowerCase(),
                entry -> ((PlayerMeta.metaValues) entry.getValue()).getValue(),
                (string, second) -> string,
                HashMap::new));
  }

  public class metaValues {
    private String value;

    public metaValues() {}

    public metaValues(String string) {
      this.value = string;
    }

    public String getValue() {
      return this.value;
    }

    public void setValue(String string) {
      this.value = string;
    }
  }
}
