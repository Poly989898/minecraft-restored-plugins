package net.Indyuce.inventory;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.version.SpigotPlugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.Indyuce.inventory.command.MMOInventoryCommand;
import net.Indyuce.inventory.command.MMOInventoryCompletion;
import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.InventoryUpdater;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.compat.ModuleType;
import net.Indyuce.inventory.compat.OraxenIntegration;
import net.Indyuce.inventory.compat.list.DefaultHook;
import net.Indyuce.inventory.compat.mmoitems.MMOItemsCompatibility;
import net.Indyuce.inventory.compat.mmoitems.TypeRestriction;
import net.Indyuce.inventory.compat.mmoitems.UniqueRestriction;
import net.Indyuce.inventory.compat.mmoitems.UseRestriction;
import net.Indyuce.inventory.compat.placeholder.DefaultParser;
import net.Indyuce.inventory.compat.placeholder.PlaceholderAPIParser;
import net.Indyuce.inventory.compat.placeholder.PlaceholderParser;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.inventory.SimpleInventoryHandler;
import net.Indyuce.inventory.listener.GuiListener;
import net.Indyuce.inventory.listener.option.DeathDrops;
import net.Indyuce.inventory.listener.option.InventoryButtonListener;
import net.Indyuce.inventory.listener.option.NoCustomInventory;
import net.Indyuce.inventory.listener.option.ResourcePack;
import net.Indyuce.inventory.manager.SlotManager;
import net.Indyuce.inventory.manager.data.PlayerDataManager;
import net.Indyuce.inventory.manager.data.sql.SQLDataHandler;
import net.Indyuce.inventory.util.ConfigFile;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.endlesscode.rpginventory.RPGInventory;

public class MMOInventory extends RPGInventory implements Listener {
   public static MMOInventory plugin;
   private final SlotManager slotManager = new SlotManager();
   private final List<InventoryUpdater> inventoryUpdaters = new ArrayList<>();
   private LevelModule levelModule;
   private ClassModule classModule;
   private final PlayerDataManager playerDataManager;
   private ConfigFile language;
   private PlaceholderParser placeholderParser = new DefaultParser();
   public int inventorySlots;
   public boolean debugMode;
   public boolean generateOraxenFiles;

   public MMOInventory() {
      plugin = this;
      this.playerDataManager = new PlayerDataManager();
   }

   public void onLoad() {
      this.getLogger().log(Level.INFO, "Plugin file is called '" + this.getFile().getName() + "'");
      if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
         this.slotManager.registerRestriction(TypeRestriction::new, "mmoitemstype", "mmoitemtype", "mitype");
         this.slotManager.registerRestriction(config -> new UseRestriction(), "mmoitemslevel", "mmoitemlevel", "milevel");
         this.slotManager.registerRestriction(UniqueRestriction::new, "unique");
      }
   }

   public void onEnable() {
      this.saveDefaultConfig();
      this.saveDefaultFile("language");
      this.saveDefaultFile("items");
      this.reload();
      new SpigotPlugin(99445, this).checkForUpdate();
      Bukkit.getServer().getPluginManager().registerEvents(this, this);
      Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);
      if (this.getConfig().getBoolean("mysql.enabled")) {
         SQLDataSource dataSource = new SQLDataSource(this);
         this.playerDataManager.setDataHandler(new SQLDataHandler(dataSource));
      }

      try {
         String moduleName = this.getConfig().getString("class-module");
         ModuleType moduleType = ModuleType.valueOf(UtilityMethods.enumName(moduleName));
         Validate.isTrue(moduleType.findPlugin(), "Plugin '" + moduleType.name() + "'not installed");
         Object module = moduleType.getModule();
         Validate.isTrue(module instanceof ClassModule, "Plugin '" + moduleType.name() + "' does not support classes");
         this.classModule = (ClassModule)module;
      } catch (Exception exception) {
         this.getLogger().log(Level.WARNING, "Could not initialize custom class module: " + exception.getMessage());
         this.classModule = new DefaultHook();
      }

      try {
         String moduleName = this.getConfig().getString("level-module");
         ModuleType moduleType = ModuleType.valueOf(UtilityMethods.enumName(moduleName));
         Validate.isTrue(moduleType.findPlugin(), "Plugin '" + moduleType.name() + "'not installed");
         Object module = moduleType.getModule();
         Validate.isTrue(module instanceof LevelModule, "Plugin '" + moduleType.name() + "' does not support levels");
         this.levelModule = (LevelModule)module;
      } catch (Exception exception) {
         this.getLogger().log(Level.WARNING, "Could not initialize custom level module: " + exception.getMessage());
         this.levelModule = new DefaultHook();
      }

      if (this.getConfig().getBoolean("resource-pack.enabled")) {
         Bukkit.getServer().getPluginManager().registerEvents(new ResourcePack(this.getConfig().getConfigurationSection("resource-pack")), this);
      }

      if (this.getConfig().getBoolean("no-custom-inventory")) {
         this.playerDataManager.setInventoryProvider(SimpleInventoryHandler::new);

         for (Listener l : NoCustomInventory.getListeners()) {
            Bukkit.getPluginManager().registerEvents(l, this);
         }
      } else {
         if (this.getConfig().getBoolean("inventory-button.enabled")) {
            for (Listener l : InventoryButtonListener.getListeners(this.getConfig().getConfigurationSection("inventory-button"))) {
               Bukkit.getPluginManager().registerEvents(l, this);
            }
         }

         if (this.getConfig().getBoolean("drop-on-death")) {
            Bukkit.getServer().getPluginManager().registerEvents(new DeathDrops(), this);
         }
      }

      this.getCommand("mmoinventory").setExecutor(new MMOInventoryCommand());
      this.getCommand("mmoinventory").setTabCompleter(new MMOInventoryCompletion());
      this.playerDataManager.initialize(EventPriority.NORMAL, EventPriority.NORMAL);
      if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
         new MMOItemsCompatibility();
         this.getLogger().log(Level.INFO, "Hooked onto MMOItems");
      }

      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         this.placeholderParser = new PlaceholderAPIParser();
         this.getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
      }

      if (this.getServer().getPluginManager().getPlugin("Oraxen") != null) {
         new OraxenIntegration();
         this.getLogger().log(Level.INFO, "Hooked onto Oraxen");
      }
   }

   public void onDisable() {
      UtilityMethods.closeOpenViewsOfType(PlayerInventoryView.class);
      this.playerDataManager.close();
   }

   public void reload() {
      this.reloadConfig();
      this.language = new ConfigFile("language");
      this.slotManager.reload();
      this.debugMode = this.getConfig().getBoolean("debug");
      this.generateOraxenFiles = this.getConfig().getBoolean("generate-oraxen-files");
      if (this.getConfig().getBoolean("no-custom-inventory")) {
         this.getConfig().set("mysql.enabled", false);
         this.getConfig().set("auto-save.enabled", false);
      }

      try {
         this.inventorySlots = this.getConfig().getInt("inventory-slots");
         Validate.isTrue(this.inventorySlots > 0 && this.inventorySlots < 55, "Number must be greater than 9 and lower than 54");
         Validate.isTrue(this.inventorySlots % 9 == 0, "Number must be a multiple of 9");
      } catch (IllegalArgumentException exception) {
         this.inventorySlots = 36;
         this.getLogger().log(Level.WARNING, "Invalid inventory slot number: " + exception.getMessage());
      }
   }

   public PlayerDataManager getDataManager() {
      return this.playerDataManager;
   }

   public SlotManager getSlotManager() {
      return this.slotManager;
   }

   public LevelModule getLevelModule() {
      return this.levelModule;
   }

   public ClassModule getClassModule() {
      return this.classModule;
   }

   public PlaceholderParser getPlaceholderParser() {
      return this.placeholderParser;
   }

   public void registerInventoryUpdater(InventoryUpdater updater) {
      Validate.notNull(updater, "Updater cannot be null");
      this.inventoryUpdaters.add(updater);
   }

   public void updateInventory(Player player) {
      for (InventoryUpdater updater : this.inventoryUpdaters) {
         updater.updateInventory(player);
      }
   }

   public String getTranslation(String path) {
      return MythicLib.plugin.parseColors(this.language.getConfig().getString(path));
   }

   private void saveDefaultFile(String path) {
      try {
         File file = new File(this.getDataFolder(), path + ".yml");
         if (!file.exists()) {
            Files.copy(this.getResource("default/" + path + ".yml"), file.getAbsoluteFile().toPath());
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
