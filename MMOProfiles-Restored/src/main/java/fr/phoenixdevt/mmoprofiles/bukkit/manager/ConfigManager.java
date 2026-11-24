package fr.phoenixdevt.mmoprofiles.bukkit.manager;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.compat.permission.ProfilePermissible;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.PlayerDataType;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ConfigFile;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ProfileUtils;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.Rank;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.message.Message;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.handler.MythicLibSkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.version.VersionUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigManager {
   public Location profileSelectionLocation;
   public Location newProfileSpawnPoint;
   public List<String> permanentWorlds;
   public List<String> targetServers;
   public DateFormat dateFormat;
   public boolean uniqueProfile;
   public boolean waitForResourcePack;
   public boolean defaultProfile;
   public boolean profileNames;
   public boolean noGuiOnLogin;
   public boolean proxyBasedProfiles;
   public boolean kickIfNoProfile;
   public boolean unselectProfileOnLogin;
   public boolean permissionBlacklistInstead;
   public String defaultMainProfileName;
   public String serverIdentifier;
   public String kickIfNoProfileMessage;
   public long guiCloseTimeout;
   public long resourcePackLoadDelay;
   @Nullable
   public GameMode forcedGameMode;
   @Nullable
   public Boolean forcedInvulnerability;
   @Nullable
   public Set<PlayerDataType> syncedDataMap = new HashSet<>();
   @Nullable
   public Skill loginScript;
   @Nullable
   public Skill profileSelectScript;
   @Nullable
   public Skill profileCreateScript;
   @Nullable
   public Skill profileDeleteScript;
   public int defaultMaxSlots;
   public int maxSlots;
   public final List<Rank> ranks = new ArrayList<>();
   public Rank[] slotToRank;
   public boolean[] inventorySlotBlacklist;
   @Nullable
   public Long backToInitialServerDelay;
   public Set<String> permissionList;

   public void reload() {
      this.ranks.clear();
      FileConfiguration config = MMOProfiles.plugin.getConfig();
      this.defaultMaxSlots = config.getInt("default-slots");
      this.newProfileSpawnPoint = ProfileUtils.readLocation(config.getConfigurationSection("new-profile-spawn-point"));
      this.profileSelectionLocation = ProfileUtils.readLocation(config.getConfigurationSection("profile-selection-location"));
      this.defaultMainProfileName = config.getString("default-main-profile-name");
      this.guiCloseTimeout = Math.max(1L, config.getLong("gui-close-timeout"));
      this.uniqueProfile = config.getBoolean("unique-profile");
      this.waitForResourcePack = config.getBoolean("resource-pack-options.wait-for-resource-pack");
      this.resourcePackLoadDelay = Math.max(1L, config.getLong("resource-pack-options.resource-pack-load-delay"));
      this.dateFormat = new SimpleDateFormat(config.getString("date-format"));
      this.defaultProfile = config.getBoolean("default-profile");
      this.serverIdentifier = config.getString("server-identifier");
      this.profileNames = config.getBoolean("use-profile-names");
      this.noGuiOnLogin = config.getBoolean("no-gui-on-login");
      this.permanentWorlds = config.getStringList("permanent-worlds");
      this.forcedInvulnerability = config.getBoolean("force-invulnerability.enabled") ? config.getBoolean("force-invulnerability.state") : null;
      this.proxyBasedProfiles = config.getBoolean("proxy_based_profiles.enabled");
      this.backToInitialServerDelay = config.getBoolean("proxy_based_profiles.back_to_initial_server.enabled")
         ? config.getLong("proxy_based_profiles.back_to_initial_server.delay")
         : null;
      this.targetServers = config.getStringList("proxy_based_profiles.target_servers");
      this.kickIfNoProfile = config.getBoolean("proxy_based_profiles.kick_if_no_profile.enabled");
      this.kickIfNoProfileMessage = config.getString("proxy_based_profiles.kick_if_no_profile.kick_message");
      this.unselectProfileOnLogin = config.getBoolean("proxy_based_profiles.unselect_profile_on_login");
      this.permissionBlacklistInstead = config.getBoolean("proxy_based_profiles.shared_permissions.blacklist_instead");
      this.permissionList = new HashSet<>(config.getStringList("proxy_based_profiles.shared_permissions.list"));
      List<Integer> inventorySlotBlacklist = config.getIntegerList("inventory-slot-blacklist");
      if (inventorySlotBlacklist.isEmpty()) {
         this.inventorySlotBlacklist = null;
      } else {
         this.inventorySlotBlacklist = new boolean[41];

         for (Integer slot : inventorySlotBlacklist) {
            this.inventorySlotBlacklist[Math.max(0, Math.min(40, slot))] = true;
         }
      }

      try {
         this.forcedGameMode = config.getBoolean("force-game-mode.enabled")
            ? GameMode.valueOf(UtilityMethods.enumName(config.getString("force-game-mode.game-mode")))
            : null;
      } catch (Exception exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Invalid game-mode name: '" + config.getString("force-game-mode.game-mode") + "'");
      }

      for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
         try {
            this.ranks.add(new Rank(config.getConfigurationSection("ranks." + key)));
         } catch (Exception exception) {
            MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load rank '" + key + "': " + exception.getMessage());
         }
      }

      for (PlayerDataType dataType : PlayerDataType.values()) {
         if (config.getBoolean("synced-data." + dataType.name().toLowerCase().replace("_", "-"))) {
            this.syncedDataMap.add(dataType);
         }
      }

      this.maxSlots = this.defaultMaxSlots;

      for (Rank rank : this.ranks) {
         this.maxSlots = Math.max(this.maxSlots, rank.getMaxSlots());
      }

      this.slotToRank = new Rank[this.maxSlots];
      List<Rank> ranksOrdered = new ArrayList<>(this.ranks);
      ranksOrdered.sort(Comparator.comparingInt(Rank::getMaxSlots).reversed());

      for (Rank rank : ranksOrdered) {
         for (int i = 0; i < rank.getMaxSlots(); i++) {
            this.slotToRank[i] = rank;
         }
      }

      ConfigFile messages = new ConfigFile("/language", "messages");

      for (Message key : Message.values()) {
         try {
            String path = key.getPath();
            if (!messages.getConfig().contains(path)) {
               messages.getConfig().set(path + ".format", key.getCached());
               if (key.hasSound()) {
                  messages.getConfig().set(path + ".sound.name", key.getSound().getSound().key().asString());
                  messages.getConfig().set(path + ".sound.vol", key.getSound().getVolume());
                  messages.getConfig().set(path + ".sound.pitch", key.getSound().getPitch());
               }
            }

            key.update(messages.getConfig().getConfigurationSection(key.getPath()));
         } catch (IllegalArgumentException exception) {
            MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not reload message " + key.name() + ": " + exception.getMessage());
         }
      }

      messages.save();
      InventoryManager.reload();

      try {
         Script script = MythicLib.plugin.getSkills().loadScript(config.getConfigurationSection("script.login"));
         this.loginScript = new SimpleSkill(TriggerType.CAST, new MythicLibSkillHandler(script));
      } catch (RuntimeException exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load login script: " + exception.getMessage());
      }

      try {
         Script script = MythicLib.plugin.getSkills().loadScript(config.getConfigurationSection("script.profile-select"));
         this.profileSelectScript = new SimpleSkill(TriggerType.CAST, new MythicLibSkillHandler(script));
      } catch (RuntimeException exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load profile-select script: " + exception.getMessage());
      }

      try {
         Script script = MythicLib.plugin.getSkills().loadScript(config.getConfigurationSection("script.profile-create"));
         this.profileCreateScript = new SimpleSkill(TriggerType.CAST, new MythicLibSkillHandler(script));
      } catch (RuntimeException exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load profile-create script: " + exception.getMessage());
      }

      try {
         Script script = MythicLib.plugin.getSkills().loadScript(config.getConfigurationSection("script.profile-delete"));
         this.profileDeleteScript = new SimpleSkill(TriggerType.CAST, new MythicLibSkillHandler(script));
      } catch (RuntimeException exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load profile-delete script: " + exception.getMessage());
      }
   }

   public boolean isBlacklisted(int inventorySlot) {
      return this.inventorySlotBlacklist != null && this.inventorySlotBlacklist[inventorySlot];
   }

   @NotNull
   public List<String> getSharedPermissions(@NotNull ProfileListImpl playerData) {
      return this.getSharedPermissions(MMOProfiles.plugin.permissionModule.getPermissible(playerData));
   }

   @NotNull
   public List<String> getSharedPermissions(@NotNull ProfilePermissible permissible) {
      return permissible.getPermisions().stream().filter(this::isPermissionShared).collect(Collectors.toList());
   }

   public boolean arePermissionsSynchronized() {
      return MMOProfiles.plugin.permissionModule != null
         && MMOProfiles.plugin.configManager.proxyBasedProfiles
         && !this.syncedDataMap.contains(PlayerDataType.PERMISSIONS);
   }

   public boolean isPermissionShared(@NotNull String perm) {
      boolean insider = this.permissionList.contains(perm);
      return this.permissionBlacklistInstead ? !insider : insider;
   }

   public void loadDefaultFile(String name) {
      this.loadDefaultFile("", name);
   }

   public void loadDefaultFile(String path, String name) {
      String newPath = path.isEmpty() ? "" : "/" + path;
      File folder = new File(MMOProfiles.plugin.getDataFolder() + newPath);
      if (!folder.exists()) {
         folder.mkdir();
      }

      File file = new File(MMOProfiles.plugin.getDataFolder() + newPath, name);
      if (!file.exists()) {
         try {
            Files.copy(MMOProfiles.plugin.getResource("default/" + (path.isEmpty() ? "" : path + "/") + name), file.getAbsoluteFile().toPath());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
