package com.Zrips.CMI.Modules.Skin;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.utils.UUIDFetcher;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SkinManager {
  public ConcurrentHashMap<UUID, CMISkin> skinCacheByUUID = new ConcurrentHashMap<>();
  public ConcurrentHashMap<String, CMISkin> skinCacheByName = new ConcurrentHashMap<>();
  CMI plugin;
  private boolean SteveOnOff = true;
  private boolean AutoApply = false;
  private boolean RequireSpecificPerm = false;
  private long SkinUpdateTimer = 60L;
  private long SkinRequestFrequency = 60L;
  static Method getProperties = null;
  boolean saving = false;
  private String fileName = "SkinsCache.yml";

  public SkinManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Skins.AutoApply",
        new String[] {
          "Applies skin to player automatically on his login to server if he doesnt have one"
              + " already set",
          "This will always set to skin by target player name"
        });
    this.AutoApply = configReader.get("Skins.AutoApply", false);
    configReader.addComment(
        "Skins.SteveOnOff",
        new String[] {
          "Sets player sking to Steve when turning skin off and lets server to handle it",
          "If its false, then skin will be changed to online one"
        });
    this.SteveOnOff = configReader.get("Skins.SteveOnOff", true);
    configReader.addComment(
        "Skins.RequireSpecificPerm",
        new String[] {
          "Requests from player specific permission for that skin cmi.command.skin.perm.[skinName]"
        });
    this.RequireSpecificPerm = configReader.get("Skins.RequireSpecificPerm", false);
    configReader.addComment(
        "Skins.SkinUpdateTimer",
        new String[] {
          "Defines time in minutes how offten we want to update skin information from online Mojang"
              + " servers",
          "So if you have this set to 1 hour, then player skin information will be updated if"
              + " player old skin information is older then 1 hour",
          "This only triggers when player joins server or changes skin manually"
        });
    this.SkinUpdateTimer =
        CMINumber.clamp(configReader.get("Skins.SkinUpdateTimer", 1320), 5, 99999);
  }

  public void removeSkinFromCache(String string) {
    if (string != null) {
      CMISkin cmiSkin = this.skinCacheByName.remove(string);
      if (cmiSkin != null) {
        this.skinCacheByUUID.remove(cmiSkin.getUuid());
      }
    }
  }

  @Deprecated
  public boolean applySkin(Player player, String string) {
    return !CMIModule.skin.isEnabled() ? false : this.applySkinAsync(player, string).join();
  }

  public CompletableFuture<Boolean> applySkinAsync(Player player, String string) {
    if (player != null && string != null && CMIModule.skin.isEnabled()) {
      CompletableFuture<UUID> completableFuture;
      if (string.length() == 36) {
        completableFuture = CompletableFuture.completedFuture(UUID.fromString(string));
      } else {
        CMISkin cmiSkin = this.skinCacheByName.get(string);
        if (cmiSkin != null) {
          completableFuture = CompletableFuture.completedFuture(cmiSkin.getUuid());
        } else if (this.plugin.preFetchUUIDS().containsKey(string)) {
          completableFuture =
              CompletableFuture.completedFuture(this.plugin.preFetchUUIDS().get(string));
        } else {
          completableFuture = UUIDFetcher.getUUIDAsync(string);
        }
      }

      return completableFuture.thenCompose(
          object -> {
            if (object == null) {
              return CompletableFuture.completedFuture(false);
            }

            this.plugin.preFetchUUIDS().putIfAbsent(string, object);
            return this.applySkinAsync(player, object);
          });
    } else {
      return CompletableFuture.completedFuture(false);
    }
  }

  public CompletableFuture<Boolean> applySkinAsync(Player player, UUID uuid) {
    CompletableFuture completableFuture = new CompletableFuture();
    if (player != null
        && player.isOnline()
        && !Version.isCurrentEqual(Version.v1_7_R4)
        && CMIModule.skin.isEnabled()) {
      try {
        CMIScheduler.runTask(
            this.plugin,
            () -> {
              if (this.plugin.getAnimationManager().isSitting(player)) {
                this.plugin.getAnimationManager().removePlayerFromChair(player);
              }
            });
        var skinFuture =
            this.setSkinAsync(CMILib.getInstance().getReflectionManager().getProfile(player), uuid);
        skinFuture.thenAccept(
            object -> {
              if (object) {
                CMIScheduler.runTask(
                    this.plugin,
                    () -> {
                      this.plugin.getNMS().updatePlayerViaPackets(player);
                      player.updateInventory();
                      completableFuture.complete(true);
                    });
              } else {
                completableFuture.complete(false);
              }
            });
      } catch (Exception exception) {
        exception.printStackTrace();
        completableFuture.completeExceptionally(exception);
      }

      return completableFuture;
    } else {
      completableFuture.complete(false);
      return completableFuture;
    }
  }

  @Deprecated
  public boolean applySkin(Player player, UUID uuid) {
    return this.applySkinAsync(player, uuid).join();
  }

  private static CompletableFuture<CMISkin> fetchSkinAsync(UUID uuid) {
    return CompletableFuture.supplyAsync(
        () -> {
          HttpsURLConnection httpsURLConnection = null;
          InputStream inputStream = null;
          BufferedReader bufferedReader = null;

          try {
            URL url =
                new URL(
                    String.format(
                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                        uuid.toString().replace("-", "")));
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(5000);
            httpsURLConnection.setReadTimeout(5000);
            int value = httpsURLConnection.getResponseCode();
            if (value != 200) {
              CMIMessages.consoleMessage(
                  "Failed to fetch skin: " + value + " " + httpsURLConnection.getResponseMessage());
              return null;
            }

            inputStream = httpsURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String string = "";

            String line;
            while ((line = bufferedReader.readLine()) != null) {
              string = string + line;
            }

            string = string.replace(" ", "");
            if (string.contains("name")
                && string.contains("value")
                && string.contains("signature")) {
              String skinName = string.split("\"name\":\"")[1].split("\"")[0];
              String skinValue = string.split("\"value\":\"")[1].split("\"")[0];
              String skinSignature = string.split("\"signature\":\"")[1].split("\"")[0];
              CMISkin cmiSkin = new CMISkin(skinName, uuid, skinValue, skinSignature);
              cmiSkin.setLastUpdate(System.currentTimeMillis());
              return cmiSkin;
            }
          } catch (Exception exception) {
            exception.printStackTrace();
            return null;
          } finally {
            try {
              if (bufferedReader != null) {
                bufferedReader.close();
              }
            } catch (IOException ioException) {
            }

            try {
              if (inputStream != null) {
                inputStream.close();
              }
            } catch (IOException ioException2) {
            }

            if (httpsURLConnection != null) {
              httpsURLConnection.disconnect();
            }
          }

          return null;
        });
  }

  public CompletableFuture<Boolean> setSkinAsync(GameProfile gameProfile, UUID uuid) {
    return this.checkCache(gameProfile, uuid)
        ? CompletableFuture.completedFuture(true)
        : fetchSkinAsync(uuid)
            .thenCompose(
                cmiSkin -> {
                  if (cmiSkin == null) {
                    return CompletableFuture.completedFuture(false);
                  }

                  CompletableFuture completableFuture = new CompletableFuture();
                  CMIScheduler.runTask(
                      this.plugin,
                      () -> {
                        this.skinCacheByUUID.put(uuid, cmiSkin);
                        this.skinCacheByName.put(cmiSkin.getName(), cmiSkin);
                        CMIScheduler.runTaskAsynchronously(
                            this.plugin,
                            () -> {
                              if (!this.saving) {
                                this.save(cmiSkin);
                              }
                            });
                        boolean flag = this.checkCache(gameProfile, uuid);
                        completableFuture.complete(flag);
                      });
                  return completableFuture;
                });
  }

  @Deprecated
  public boolean setSkin(GameProfile gameProfile, UUID uuid) {
    return this.setSkinAsync(gameProfile, uuid).join();
  }

  private boolean checkCache(GameProfile gameProfile, UUID uuid) {
    if (gameProfile == null) {
      return false;
    }

    CMISkin cmiSkin = this.skinCacheByUUID.get(uuid);
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
      if (Version.isPaperBranch() && cmiSkin != null && cmiSkin.getSkinUrl() != null) {
        Player player = Bukkit.getPlayer(gameProfile.id());
        Entity entity = player.getVehicle();
        PlayerProfile playerProfile = player.getPlayerProfile();
        playerProfile.setProperty(
            new ProfileProperty("textures", cmiSkin.getSkin(), cmiSkin.getSignature()));
        CMIScheduler.runAtLocation(
            this.plugin,
            player.getLocation(),
            () -> {
              player.setPlayerProfile(playerProfile);
              if (entity != null && !entity.isDead()) {
                player.leaveVehicle();
                entity.addPassenger(player);
              }
            });
        return true;
      } else {
        return false;
      }
    } else if (cmiSkin != null
        && cmiSkin.getSkin() != null
        && cmiSkin.getSignature() != null
        && cmiSkin.getLastUpdate() + this.getSkinUpdateTimer() * 60L * 1000L
            > System.currentTimeMillis()) {
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            PropertyMap propertyMap = getPropertyMap(gameProfile);
            if (propertyMap != null) {
              propertyMap.removeAll("textures");
              propertyMap.put(
                  "textures", new Property("textures", cmiSkin.getSkin(), cmiSkin.getSignature()));
            }
          });
      return true;
    } else {
      return false;
    }
  }

  @Nullable
  public static PropertyMap getPropertyMap(GameProfile gameProfile) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
      if (getProperties == null) {
        try {
          getProperties = gameProfile.getClass().getMethod("properties");
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }

      if (getProperties != null) {
        try {
          return (PropertyMap) getProperties.invoke(gameProfile);
        } catch (Exception exception2) {
          exception2.printStackTrace();
          return null;
        }
      }
    }

    try {
      if (getProperties == null) {
        getProperties = gameProfile.getClass().getMethod("getProperties");
      }

      return (PropertyMap) getProperties.invoke(gameProfile);
    } catch (Exception exception3) {
      exception3.printStackTrace();
      return null;
    }
  }

  @Deprecated
  public CMISkin getSkin(String string) {
    if (string == null) {
      return null;
    }

    UUID uuid = null;
    if (string.length() == 36) {
      uuid = UUID.fromString(string);
    } else {
      CMISkin cmiSkin = this.skinCacheByName.get(string);
      if (cmiSkin != null
          && cmiSkin.getLastUpdate() + this.getSkinUpdateTimer() * 60L * 1000L
              > System.currentTimeMillis()) {
        return cmiSkin;
      }

      if (!this.plugin.preFetchUUIDS().containsKey(string)) {
        uuid = UUIDFetcher.getUUID(string);
        this.plugin.preFetchUUIDS().put(string, uuid);
      } else {
        uuid = this.plugin.preFetchUUIDS().get(string);
      }
    }

    if (uuid == null) {
      return null;
    }

    CMISkin cachedSkin = this.skinCacheByUUID.get(uuid);
    if (cachedSkin != null
        && cachedSkin.getLastUpdate() + this.getSkinUpdateTimer() * 60L * 1000L
            > System.currentTimeMillis()) {
      return cachedSkin;
    }

    this.setSkinAsync(null, uuid);
    return this.skinCacheByUUID.get(uuid);
  }

  public void save(CMISkin cmiSkin) {
    this.saving = true;
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    String string = cmiSkin.getUuid().toString();
    yamlConfiguration.set(string + ".name", cmiSkin.getName());
    yamlConfiguration.set(string + ".skin", cmiSkin.getSkin());
    yamlConfiguration.set(string + ".signature", cmiSkin.getSignature());
    yamlConfiguration.set(
        string + ".lastUpdate", cmiSkin.getLastUpdate() == 0L ? null : cmiSkin.getLastUpdate());

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException2) {
      ioException2.printStackTrace();
    }

    this.saving = false;
  }

  public void load() {
    try {
      Long startTime = System.currentTimeMillis();
      CMI.getInstance().safeRenameFile("skinsCache.yml", "Saves" + File.separator + this.fileName);
      File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
      if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }

        return;
      }

      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

      for (String string : yamlConfiguration.getKeys(false)) {
        UUID uuid = UUID.fromString(string);
        if (uuid != null) {
          String cachedName = yamlConfiguration.getString(string + ".name");
          String cachedValue = yamlConfiguration.getString(string + ".skin");
          String cachedSignature = yamlConfiguration.getString(string + ".signature");
          Long lastUpdate = 0L;
          if (yamlConfiguration.isLong(string + ".lastUpdate")) {
            lastUpdate = yamlConfiguration.getLong(string + ".lastUpdate");
          }

          CMISkin cmiSkin = new CMISkin(cachedName, uuid, cachedValue, cachedSignature);
          cmiSkin.setLastUpdate(lastUpdate);
          this.skinCacheByUUID.put(uuid, cmiSkin);
          this.skinCacheByName.put(cachedName, cmiSkin);
        }
      }

      this.plugin.loadMessage(
          this.skinCacheByUUID.size(),
          "skin cache entries",
          System.currentTimeMillis() - startTime);
    } catch (Exception exception) {
    }
  }

  public boolean isSteveOnOff() {
    return this.SteveOnOff;
  }

  public void setSteveOnOff(boolean flag) {
    this.SteveOnOff = flag;
  }

  public boolean isRequireSpecificPerm() {
    return this.RequireSpecificPerm;
  }

  public void setRequireSpecificPerm(boolean flag) {
    this.RequireSpecificPerm = flag;
  }

  public boolean isAutoApply() {
    return this.AutoApply;
  }

  public long getSkinUpdateTimer() {
    return this.SkinUpdateTimer;
  }
}
