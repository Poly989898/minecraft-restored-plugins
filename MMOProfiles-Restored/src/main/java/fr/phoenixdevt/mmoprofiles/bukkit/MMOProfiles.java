package fr.phoenixdevt.mmoprofiles.bukkit;

import fr.phoenixdevt.mmoprofiles.bukkit.command.MMOProfilesCommandTreeRoot;
import fr.phoenixdevt.mmoprofiles.bukkit.compat.ProfilesPlaceholders;
import fr.phoenixdevt.mmoprofiles.bukkit.compat.VaultSupport;
import fr.phoenixdevt.mmoprofiles.bukkit.compat.permission.LuckPermsModule;
import fr.phoenixdevt.mmoprofiles.bukkit.compat.permission.PermissionModule;
import fr.phoenixdevt.mmoprofiles.bukkit.listener.PlayerListener;
import fr.phoenixdevt.mmoprofiles.bukkit.listener.TeleportListener;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.ConfigManager;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.data.PlayerDataManager;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql.SQLDataHandler;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.data.json.WrappedLocationAdapter;
import fr.phoenixdevt.mmoprofiles.bukkit.proxy.ProxyBasedProfiles;
import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileProvider;
import fr.phoenixdevt.profiles.placeholder.PlaceholderProcessor;
import fr.phoenixdevt.profiles.util.WrappedLocation;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.metrics.bukkit.Metrics;
import io.lumine.mythic.lib.util.MMOPlugin;
import io.lumine.mythic.lib.util.gson.MythicLibGson;
import io.lumine.mythic.lib.version.SpigotPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

public class MMOProfiles extends MMOPlugin implements ProfileProvider<ProfileListImpl> {
   public static MMOProfiles plugin;
   public final PlayerDataManager playerDataManager;
   private final Map<String, ProfileDataModule> dataHolders = new HashMap<>();
   private final Map<String, PlaceholderProcessor> placeholderProcessors = new HashMap<>();
   public VaultSupport vaultSupport;
   public ConfigManager configManager = new ConfigManager();
   public PermissionModule permissionModule;

   public MMOProfiles() {
      plugin = this;
      this.playerDataManager = new PlayerDataManager();
   }

   public void onLoad() {
      MythicLibGson.getBuilder().registerTypeAdapter(WrappedLocation.class, new WrappedLocationAdapter());
      this.getLogger().log(Level.INFO, "Plugin file is called '" + this.getFile().getName() + "'");
   }

   public void onEnable() {
      Bukkit.getServicesManager().register(ProfileProvider.class, this, this, ServicePriority.Normal);
      this.saveDefaultConfig();
      this.configManager.reload();
      if (this.configManager.proxyBasedProfiles && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
         MythicLib.plugin.useProxyProfiles();
         this.getLogger().log(Level.INFO, "Hooked onto ProtocolLib, enabling proxy-based profiles...");
         ProxyBasedProfiles.getInstance().initialize();
      } else {
         MythicLib.plugin.useLegacyProfiles();
      }

      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         new ProfilesPlaceholders().register();
         this.getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
      }

      if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
         this.vaultSupport = new VaultSupport();
      }

      if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
         try {
            this.permissionModule = new LuckPermsModule();
         } catch (Exception exception) {
            this.getLogger().log(Level.WARNING, "Could not hook onto LuckPerms: " + exception.getMessage());
         }
      }

      if (this.getConfig().isConfigurationSection("mysql") && this.getConfig().getBoolean("mysql.enabled")) {
         SQLDataSource dataSource = new SQLDataSource(this);
         this.playerDataManager.setDataHandler(new SQLDataHandler(dataSource));
      }

      this.playerDataManager.initialize(EventPriority.LOW, EventPriority.HIGH);
      new SpigotPlugin(109942, this).checkForUpdate();
      new Metrics(this);
      this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
      this.getServer().getPluginManager().registerEvents(new fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.GuiListener(), this);
      this.getServer().getPluginManager().registerEvents(new TeleportListener(), this);
      MMOProfilesCommandTreeRoot mainCommand = new MMOProfilesCommandTreeRoot();
      this.getCommand("mmoprofiles").setExecutor(mainCommand);
      this.getCommand("mmoprofiles").setTabCompleter(mainCommand);
   }

   public void onDisable() {
      this.playerDataManager.close();
   }

   @Override
   public Collection<ProfileDataModule> getModules() {
      return new ArrayList<>(this.dataHolders.values());
   }

   @Override
   public Collection<PlaceholderProcessor> getPlaceholders() {
      return new ArrayList<>(this.placeholderProcessors.values());
   }

   public ProfileListImpl getPlayerData(UUID playerId) {
      return (ProfileListImpl)this.playerDataManager.get(playerId);
   }

   @Override
   public void registerModule(ProfileDataModule module) {
      Validate.notNull(module, "Module cannot be null");
      Validate.isTrue(!this.dataHolders.containsKey(module.getIdentifier()), "A module with the same identifier already exists");
      this.dataHolders.put(module.getIdentifier(), module);
      Bukkit.getPluginManager().registerEvents(module, module.getOwningPlugin());
   }

   @Override
   public void registerPlaceholders(@NotNull PlaceholderProcessor module) {
      Validate.notNull(module, "Module cannot be null");
      Validate.isTrue(
         !this.placeholderProcessors.containsKey(module.getDataModule().getIdentifier()), "A placeholder module with the same identifier already exists"
      );
      this.placeholderProcessors.put(module.getDataModule().getIdentifier(), module);
   }

   public boolean hasEconomy() {
      return this.vaultSupport != null && this.vaultSupport.economy != null;
   }

   public boolean hasProfiles() {
      return true;
   }
}
