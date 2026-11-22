package net.Zrips.CMILib.Skins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkinManager {
  public HashMap<UUID, CMISkin> skinCacheByUUID = new HashMap<>();
  public HashMap<String, CMISkin> skinCacheByName = new HashMap<>();
  protected HashMap<String, List<String>> preFetchNames = new HashMap<>();
  protected HashMap<String, UUID> preFetchUUIDs = new HashMap<>();
  CMILib plugin;
  private long SkinUpdateTimer = 1320L;
  private long SkinRequestFrequency = 60L;
  Long lastUpdateRequest = 0L;
  private static Method getProperties = null;
  boolean saving = false;

  public SkinManager(CMILib plugin) {
    this.plugin = plugin;
  }

  public void clearCache() {
    this.skinCacheByUUID.clear();
    this.skinCacheByName.clear();
  }

  public void loadConfig() {
    ConfigReader cfg = this.plugin.getConfigManager().getConfigFile();
    cfg.addComment(
        "Skins.SkinUpdateTimer",
        "Defines time in minutes how often we want to update skin information from online Mojang"
            + " servers",
        "Keep in mind that your server can only send 1 request every minute, so keep it at a decent"
            + " amount, hour or more",
        "So if you have this set to 1 hour, then player skin information will be updated if player"
            + " old skin information is older then 1 hour",
        "This only triggers when player joins server or changes skin manually");
    this.setSkinUpdateTimer(cfg.get("Skins.SkinUpdateTimer", 1320));
    this.setSkinUpdateTimer(this.getSkinUpdateTimer() < 5L ? 5L : this.getSkinUpdateTimer());
    cfg.addComment(
        "Skins.SkinRequestFrequency",
        "Defines time in minutes how often we want to send requests to Mojang servers",
        "This is to limit amount of requests in specific time to avoid clutter with possible"
            + " requests");
    this.SkinRequestFrequency = cfg.get("Skins.SkinRequestFrequency", 10);
    this.SkinRequestFrequency = this.SkinRequestFrequency < 1L ? 1L : this.SkinRequestFrequency;
  }

  public void removeSkinFromCache(String playerName) {
    CMISkin skin = this.skinCacheByName.remove(playerName);
    if (skin != null) {
      this.skinCacheByUUID.remove(skin.getUuid());
    }
  }

  public void removeSkinFromCache(UUID uuid) {
    CMISkin skin = this.skinCacheByUUID.remove(uuid);
    if (skin != null) {
      this.skinCacheByName.remove(skin.getName());
    }
  }

  public boolean setSkin(GameProfile profile, UUID uuid) {
    if (this.checkCache(profile, uuid)) {
      return true;
    }

    try {
      HttpsURLConnection connection =
          (HttpsURLConnection)
              new URL(
                      String.format(
                          "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                          uuid.toString().replace("-", "")))
                  .openConnection();
      if (connection.getResponseCode() == 200) {
        InputStream stream = connection.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

        try {
          String reply = buffer.readLine();
          String lines = "";
          Iterator<String> iter = buffer.lines().iterator();

          while (iter.hasNext()) {
            lines = lines + iter.next();
          }

          reply = lines.replace(" ", "");
          if (!reply.contains("name")) {
            return this.checkCache(profile, uuid);
          }

          String playerName = reply.split("\"name\":\"")[1].split("\"")[0];
          if (!reply.contains("value")) {
            return this.checkCache(profile, uuid);
          }

          String skin = reply.split("\"value\":\"")[1].split("\"")[0];
          if (!reply.contains("signature")) {
            return this.checkCache(profile, uuid);
          }

          String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
          CMISkin cmiSkin = new CMISkin(playerName, uuid, skin, signature);
          cmiSkin.setLastUpdate(System.currentTimeMillis());
          this.lastUpdateRequest = System.currentTimeMillis();
          this.skinCacheByUUID.put(uuid, cmiSkin);
          this.skinCacheByName.put(playerName, cmiSkin);
          if (profile != null) {
            PropertyMap properties = getPropertyMap(profile);
            if (properties != null) {
              properties.removeAll("textures");
              properties.put("textures", new Property("textures", skin, signature));
            }
          }
        } catch (Throwable ex) {
          ex.printStackTrace();
        } finally {
          buffer.close();
          stream.close();
          connection.disconnect();
        }

        return true;
      } else {
        if (this.checkCache(profile, uuid)) {
          return true;
        }

        CMIMessages.consoleMessage(
            "Connection could not be opened (Response code "
                + connection.getResponseCode()
                + ", "
                + connection.getResponseMessage()
                + ")");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static PropertyMap getPropertyMap(GameProfile profile) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
      if (getProperties == null) {
        try {
          getProperties = GameProfile.class.getMethod("properties");
        } catch (NoSuchMethodException | SecurityException e) {
          e.printStackTrace();
        }
      }

      if (getProperties != null) {
        try {
          return (PropertyMap) getProperties.invoke(profile);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    }

    try {
      if (getProperties == null) {
        getProperties = GameProfile.class.getMethod("getProperties");
      }

      return (PropertyMap) getProperties.invoke(profile);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private boolean checkCache(GameProfile profile, UUID uuid) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R6) && Version.isPaperBranch()) {
      return false;
    }

    if (profile == null) {
      return false;
    }

    CMISkin cache = this.skinCacheByUUID.get(uuid);
    if (cache != null
        && cache.getSkin() != null
        && cache.getSignature() != null
        && cache.getLastUpdate() + this.getSkinUpdateTimer() * 60L * 1000L
            > System.currentTimeMillis()) {
      try {
        PropertyMap properties = getPropertyMap(profile);
        if (properties != null) {
          properties.removeAll("textures");
          properties.put(
              "textures", new Property("textures", cache.getSkin(), cache.getSignature()));
          return true;
        }
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }

    return false;
  }

  public CMISkin getSkin(String name) {
    if (name == null) {
      return null;
    }

    UUID onlineUUID = null;
    if (name.length() == 36) {
      onlineUUID = UUID.fromString(name);
    } else {
      name = name.replaceAll("[^a-zA-Z0-9_]", "");
      CMISkin cache = this.skinCacheByName.get(name);
      if (cache != null) {
        return cache;
      }

      if (!this.preFetchUUIDS().containsKey(name)) {
        onlineUUID = this.getUUID(name);
        this.preFetchUUIDS().put(name, onlineUUID);
      } else {
        onlineUUID = this.preFetchUUIDS().get(name);
      }
    }

    if (onlineUUID == null) {
      return null;
    }

    CMISkin cache = this.skinCacheByUUID.get(onlineUUID);
    if (cache != null) {
      return cache;
    }

    this.setSkin(null, onlineUUID);
    cache = this.skinCacheByUUID.get(onlineUUID);
    return cache != null ? cache : null;
  }

  public UUID getUUID(String name) {
    UUID cached = this.preFetchUUIDS().get(name);
    if (cached != null) {
      return cached;
    }

    String target = "https://api.mojang.com/users/profiles/minecraft/" + name;
    StringBuilder response = new StringBuilder();
    HttpURLConnection connection = null;

    try {
      URL url = new URL(target);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setUseCaches(false);
      connection.setDoOutput(true);
      connection.connect();
      if (connection.getResponseCode() != 200) {
        return null;
      }

      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r').append('\n');
      }

      rd.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    UUID uuid = null;

    try {
      Pattern pattern = Pattern.compile("(\")([a-zA-Z0-9]{32})(\")");
      Matcher matcher = pattern.matcher(response.toString());
      if (matcher.find()) {
        uuid =
            UUID.fromString(
                matcher
                    .group(2)
                    .replaceFirst(
                        "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
                        "$1-$2-$3-$4-$5"));
      }
    } catch (Exception e) {
      try {
        String id =
            response
                .toString()
                .split("\",\"")[0]
                .split(":")[1]
                .replace("\"", "")
                .replaceFirst(
                    "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
                    "$1-$2-$3-$4-$5");
        uuid = UUID.fromString(id);
      } catch (Exception var15) {
      }
    }

    return uuid;
  }

  public void save(CMISkin skin) {
    this.saving = true;
    File file = new File(this.plugin.getDataFolder(), "skinsCache.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    YamlConfiguration k = YamlConfiguration.loadConfiguration(file);
    String uuid = skin.getUuid().toString();
    k.set(uuid + ".name", skin.getName());
    k.set(uuid + ".skin", skin.getSkin());
    k.set(uuid + ".signature", skin.getSignature());
    k.set(uuid + ".lastUpdate", skin.getLastUpdate() == 0L ? null : skin.getLastUpdate());

    try {
      k.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.saving = false;
  }

  public void load() {
    try {
      Long time = System.currentTimeMillis();
      File file = new File(this.plugin.getDataFolder(), "skinsCache.yml");
      if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }

        return;
      }

      YamlConfiguration k = YamlConfiguration.loadConfiguration(file);

      for (String key : k.getKeys(false)) {
        UUID uuid = UUID.fromString(key);
        if (uuid != null) {
          String name = k.getString(key + ".name");
          String skin = k.getString(key + ".skin");
          String signature = k.getString(key + ".signature");
          Long lastUpdate = 0L;
          if (k.isLong(key + ".lastUpdate")) {
            lastUpdate = k.getLong(key + ".lastUpdate");
          }

          CMISkin cmiSkin = new CMISkin(name, uuid, skin, signature);
          cmiSkin.setLastUpdate(lastUpdate);
          this.skinCacheByUUID.put(uuid, cmiSkin);
          this.skinCacheByName.put(name, cmiSkin);
        }
      }

      this.plugin.loadMessage(
          this.skinCacheByUUID.size(), "skin cache entries", System.currentTimeMillis() - time);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, UUID> preFetchUUIDS() {
    return this.preFetchUUIDs;
  }

  public long getSkinUpdateTimer() {
    return this.SkinUpdateTimer;
  }

  public void setSkinUpdateTimer(long skinUpdateTimer) {
    this.SkinUpdateTimer = skinUpdateTimer;
  }
}
