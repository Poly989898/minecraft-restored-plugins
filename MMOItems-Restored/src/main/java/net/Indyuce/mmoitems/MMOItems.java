package net.Indyuce.mmoitems;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.util.MMOPlugin;
import io.lumine.mythic.lib.version.SpigotPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.api.DeathItemsHandler;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import net.Indyuce.mmoitems.comp.MMOItemsMetrics;
import net.Indyuce.mmoitems.comp.MMOItemsRewardTypes;
import net.Indyuce.mmoitems.comp.McMMONonRPGHook;
import net.Indyuce.mmoitems.comp.WorldEditSupport;
import net.Indyuce.mmoitems.comp.eco.VaultSupport;
import net.Indyuce.mmoitems.comp.enchants.CrazyEnchantsStat;
import net.Indyuce.mmoitems.comp.enchants.EnchantPlugin;
import net.Indyuce.mmoitems.comp.enchants.MythicEnchantsSupport;
import net.Indyuce.mmoitems.comp.enchants.advanced_enchants.AdvancedEnchantmentsHook;
import net.Indyuce.mmoitems.comp.inventory.DefaultPlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.OrnamentPlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventoryHandler;
import net.Indyuce.mmoitems.comp.inventory.RPGInventoryHook;
import net.Indyuce.mmoitems.comp.mmocore.MMOCoreMMOLoader;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.gui.edition.recipe.RecipeTypeListGUI;
import net.Indyuce.mmoitems.comp.mmoinventory.MMOInventorySupport;
import net.Indyuce.mmoitems.comp.mythicmobs.LootsplosionListener;
import net.Indyuce.mmoitems.comp.mythicmobs.MythicMobsCompatibility;
import net.Indyuce.mmoitems.comp.placeholders.MMOItemsPlaceholders;
import net.Indyuce.mmoitems.comp.rpg.DefaultHook;
import net.Indyuce.mmoitems.comp.rpg.HeroesHook;
import net.Indyuce.mmoitems.comp.rpg.McMMOHook;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.manager.BlockManager;
import net.Indyuce.mmoitems.manager.ConfigManager;
import net.Indyuce.mmoitems.manager.CraftingManager;
import net.Indyuce.mmoitems.manager.DropTableManager;
import net.Indyuce.mmoitems.manager.ItemManager;
import net.Indyuce.mmoitems.manager.LayoutManager;
import net.Indyuce.mmoitems.manager.LoreFormatManager;
import net.Indyuce.mmoitems.manager.PluginUpdateManager;
import net.Indyuce.mmoitems.manager.RecipeManager;
import net.Indyuce.mmoitems.manager.SetManager;
import net.Indyuce.mmoitems.manager.SkillManager;
import net.Indyuce.mmoitems.manager.StatManager;
import net.Indyuce.mmoitems.manager.TemplateManager;
import net.Indyuce.mmoitems.manager.TierManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.manager.UpgradeManager;
import net.Indyuce.mmoitems.manager.WorldGenManager;
import net.Indyuce.mmoitems.manager.data.PlayerDataManager;
import net.Indyuce.mmoitems.util.PluginUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitRunnable;
import net.Indyuce.mmoitems.comp.MMOItemsRewardTypes;
import org.jetbrains.annotations.NotNull;

public class MMOItems extends MMOPlugin {
   public static MMOItems plugin;
   private final PluginUpdateManager pluginUpdateManager = new PluginUpdateManager();
   private final CraftingManager stationRecipeManager = new CraftingManager();
   private final LoreFormatManager loreManager = new LoreFormatManager();
   private final TemplateManager templateManager = new TemplateManager();
   private final SkillManager skillManager = new SkillManager();
   private final RecipeManager recipeManager = new RecipeManager();
   private final LayoutManager layoutManager = new LayoutManager();
   private final TypeManager typeManager = new TypeManager();
   private final ItemManager itemManager = new ItemManager();
   private final PlayerInventoryHandler inventory = new PlayerInventoryHandler();
   private final List<EnchantPlugin<?>> enchantPlugins = new ArrayList<>();
   private final StatManager statManager = new StatManager();
   private PlayerDataManager playerDataManager;
   private DropTableManager dropTableManager;
   private WorldGenManager worldGenManager;
   private UpgradeManager upgradeManager;
   private ConfigManager configManager;
   private BlockManager blockManager;
   private TierManager tierManager;
   private SetManager setManager;
   private VaultSupport vaultSupport;
   private final List<RPGHandler> rpgPlugins = new ArrayList<>();
   private boolean hasLoadedSuccessfully;
   private static final String BLACK_MINECRAFT_85456 = "https://black-minecraft.com/resources/1185/";

   public MMOItems() {
      plugin = this;
   }

   public void onLoad() {
      this.getLogger().log(Level.INFO, "Plugin file is called '" + this.getFile().getName() + "'");
      PluginUtils.isDependencyPresent("WorldEdit", var1 -> {
         try {
            new WorldEditSupport();
            this.getLogger().log(Level.INFO, "Hooked onto WorldEdit");
         } catch (Exception var3) {
            this.getLogger().log(Level.SEVERE, "Could not initialize support with WorldEdit 7: ", var3);
         }
      });
      this.saveDefaultConfig();
      this.configManager = new ConfigManager();
      this.statManager.loadBuiltins();
      this.typeManager.reload(false);
      this.templateManager.preloadObjects();
      PluginUtils.isDependencyPresent("MMOCore", var0 -> new MMOCoreMMOLoader());
      PluginUtils.isDependencyPresent("mcMMO", var1 -> this.statManager.register(McMMOHook.disableMcMMORepair));
      PluginUtils.hookDependencyIfPresent("AdvancedEnchantments", var1 -> {
         this.statManager.register(AdvancedEnchantmentsHook.ADVANCED_ENCHANTMENTS);
         this.statManager.register(AdvancedEnchantmentsHook.DISABLE_ADVANCED_ENCHANTMENTS);
      });
      PluginUtils.hookDependencyIfPresent("MythicEnchants", var1 -> this.enchantPlugins.add(new MythicEnchantsSupport()));
      PluginUtils.isDependencyPresent("Heroes", var1 -> {
         this.statManager.register(HeroesHook.MAX_STAMINA);
         this.statManager.register(HeroesHook.REQUIRED_SECONDARY_HERO_LEVEL);
      });
   }

   public void onEnable() {
      new SpigotPlugin(39267, this).checkForUpdate();
      new MMOItemsMetrics();
      MMOItemUIFilter.register();
      RecipeTypeListGUI.registerNativeRecipes();
      this.typeManager.postload();
      this.skillManager.initialize(false);
      int configVersion = this.getConfig().contains("config-version", true) ? this.getConfig().getInt("config-version") : -1;
      int expectedVersion = this.getConfig().getDefaults().getInt("config-version");
      if (configVersion != expectedVersion) {
         this.getLogger().warning("You may be using an outdated config.yml!");
         this.getLogger().warning("(Your config version: '" + configVersion + "' | Expected config version: '" + expectedVersion + "')");
      }

      PluginUtils.hookDependencyIfPresent("MythicMobs", var1x -> {
         new MythicMobsCompatibility();
         if (this.getConfig().getBoolean("lootsplosion.enabled")) {
            Bukkit.getPluginManager().registerEvents(new LootsplosionListener(), this);
         }
      });
      PluginUtils.hookDependencyIfPresent("MMOInventory", var0 -> new MMOInventorySupport());
      this.findRpgPlugins();
      this.statManager.loadElements();
      this.loreManager.reload();
      this.tierManager = new TierManager();
      this.setManager = new SetManager();
      this.upgradeManager = new UpgradeManager();
      this.templateManager.postloadObjects();
      this.dropTableManager = new DropTableManager();
      this.worldGenManager = new WorldGenManager();
      this.blockManager = new BlockManager();
      this.statManager.reload(false);
      PluginUtils.hookDependencyIfPresent("Vault", var1x -> this.vaultSupport = new VaultSupport());
      this.getLogger().log(Level.INFO, "Loading crafting stations, please wait..");
      this.layoutManager.reload();
      this.stationRecipeManager.reload();
      MMOItemReforger.reload();
      Bukkit.getPluginManager().registerEvents(this.dropTableManager, this);

      try {
         Class.forName("net.Indyuce.mmoitems.MMOItemsBukkit").getConstructor(MMOItems.class).newInstance(this);
      } catch (Throwable var1338) {
         throw new RuntimeException("Cannot run an API build on Spigot!");
      }

      Runnable inventoryUpdate = () -> {
         for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData.get(player).getInventory().updateCheck();
         }
      };
      Bukkit.getScheduler().runTaskTimer(this, inventoryUpdate, 100L, this.getConfig().getInt("inventory-update-delay"));
      PluginUtils.isDependencyPresent("mcMMO", var1x -> Bukkit.getPluginManager().registerEvents(new McMMONonRPGHook(), this));
      this.getInventory().register(new DefaultPlayerInventory());
      PluginUtils.hookDependencyIfPresent("RPGInventory", var1x -> this.getInventory().register(new RPGInventoryHook()));
      if (plugin.getConfig().getBoolean("iterate-whole-inventory")) {
         this.getInventory().register(new OrnamentPlayerInventory());
      }

      PluginUtils.hookDependencyIfPresent("CrazyEnchantments", var1x -> this.getStats().register(new CrazyEnchantsStat()));
      PluginUtils.hookDependencyIfPresent("AdvancedEnchantments", var1x -> Bukkit.getPluginManager().registerEvents(new AdvancedEnchantmentsHook(), this));
      PluginUtils.hookDependencyIfPresent("PlaceholderAPI", var0 -> new MMOItemsPlaceholders().register());
      if (Bukkit.getPluginManager().getPlugin("BossShopPro") != null) {
         this.getLogger().log(Level.INFO, "Hooked onto BossShopPro");
         new BukkitRunnable() {
            public void run() {
               try {
                  new MMOItemsRewardTypes().register();
               } catch (NullPointerException exception) {
                  MMOItems.this.getLogger().log(Level.SEVERE, "Could not Hook onto BossShopPro");
               }
            }
         }.runTaskLater(this, 1L);
      }

      this.playerDataManager = new PlayerDataManager();
      this.playerDataManager.initialize(EventPriority.NORMAL, EventPriority.HIGHEST);
      this.getLogger().log(Level.INFO, "Loading recipes, please wait...");
      this.recipeManager.loadRecipes();
      MMOItemsCommandTreeRoot commandRoot = new MMOItemsCommandTreeRoot();
      this.getCommand("mmoitems").setExecutor(commandRoot);
      this.getCommand("mmoitems").setTabCompleter(commandRoot);
      this.hasLoadedSuccessfully = true;
   }

   public void onDisable() {
      if (this.hasLoadedSuccessfully) {
         this.playerDataManager.close();
         DeathItemsHandler.getActive().forEach(DeathItemsHandler::dropItems);
         UtilityMethods.closeOpenViewsOfType(PluginInventory.class);
         this.worldGenManager.unload();
      }
   }

   public String getPrefix() {
      return ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MMOItems" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
   }

   public File getJarFile() {
      return plugin.getFile();
   }

   public CraftingManager getCrafting() {
      return this.stationRecipeManager;
   }

   public LayoutManager getLayouts() {
      return this.layoutManager;
   }

   public SetManager getSets() {
      return this.setManager;
   }

   @Deprecated
   public RPGHandler getRPG() {
      return this.getMainRPG();
   }

   @Nullable
   public RPGHandler getMainRPG() {
      Validate.isTrue(!this.rpgPlugins.isEmpty(), "No RPG plugin was found");
      return this.rpgPlugins.get(0);
   }

   public List<RPGHandler> getRPGs() {
      return this.rpgPlugins;
   }

   public void findRpgPlugins() {
      Validate.isTrue(this.rpgPlugins.isEmpty(), "RPG hooks have already been computed");
      this.rpgPlugins.add(new DefaultHook());
      String var1 = plugin.getConfig().getString("preferred-rpg-provider");

      for (RPGHandler.PluginEnum var5 : RPGHandler.PluginEnum.values()) {
         if (Bukkit.getPluginManager().getPlugin(var5.getName()) != null) {
            try {
               RPGHandler var6 = var5.load();
               this.rpgPlugins.add(var6);
               this.getLogger().log(Level.INFO, "Hooked onto " + var5.getName());
               if (var1.equalsIgnoreCase(var5.name())) {
                  Collections.swap(this.rpgPlugins, 0, this.rpgPlugins.size() - 1);
                  this.getLogger().log(Level.INFO, "Now using " + var5.getName() + " as RPG core plugin");
               }
            } catch (Exception var7) {
               plugin.getLogger().log(Level.SEVERE, "Could not initialize RPG plugin compatibility with " + var5.getName() + ":");
               var7.printStackTrace();
            }
         }
      }

      RPGHandler var8 = this.rpgPlugins.get(0);
      if (this.rpgPlugins.get(0) instanceof Listener) {
         Bukkit.getPluginManager().registerEvents((Listener)var8, this);
      }
   }

   public void setRPG(@NotNull RPGHandler var1) {
      Validate.notNull(var1, "RPGHandler cannot be null");
      if (this.getMainRPG() instanceof Listener && this.isEnabled()) {
         HandlerList.unregisterAll((Plugin)this.getMainRPG());
      }

      this.rpgPlugins.add(0, var1);
      this.getLogger().log(Level.INFO, "Now using " + var1.getClass().getSimpleName() + " as RPG provider");
      if (var1 instanceof Listener && this.isEnabled()) {
         Bukkit.getPluginManager().registerEvents((Listener)var1, this);
      }
   }

   public PluginUpdateManager getUpdates() {
      return this.pluginUpdateManager;
   }

   public PlayerInventoryHandler getInventory() {
      return this.inventory;
   }

   public void registerPlayerInventory(PlayerInventory var1) {
      this.getInventory().register(var1);
   }

   @Deprecated
   public void setPlayerInventory(PlayerInventory var1) {
      this.getInventory().unregisterAll();
      this.getInventory().register(var1);
   }

   public void registerEnchantPlugin(EnchantPlugin var1) {
      Validate.notNull(var1, "Enchant plugin cannot be null");
      this.enchantPlugins.add(var1);
   }

   public PlayerDataManager getPlayerDataManager() {
      return this.playerDataManager;
   }

   public StatManager getStats() {
      return this.statManager;
   }

   public TierManager getTiers() {
      return this.tierManager;
   }

   public DropTableManager getDropTables() {
      return this.dropTableManager;
   }

   public SkillManager getSkills() {
      return this.skillManager;
   }

   public BlockManager getCustomBlocks() {
      return this.blockManager;
   }

   public WorldGenManager getWorldGen() {
      return this.worldGenManager;
   }

   public RecipeManager getRecipes() {
      return this.recipeManager;
   }

   public ConfigManager getLanguage() {
      return this.configManager;
   }

   public TypeManager getTypes() {
      return this.typeManager;
   }

   public UpgradeManager getUpgrades() {
      return this.upgradeManager;
   }

   public TemplateManager getTemplates() {
      return this.templateManager;
   }

   public LoreFormatManager getLore() {
      return this.loreManager;
   }

   @Deprecated
   public LoreFormatManager getFormats() {
      return this.getLore();
   }

   @Deprecated
   public ItemManager getItems() {
      return this.itemManager;
   }

   public boolean hasPermissions() {
      return this.vaultSupport != null && this.vaultSupport.getPermissions() != null;
   }

   public List<EnchantPlugin<?>> getEnchantPlugins() {
      return this.enchantPlugins;
   }

   public boolean hasEconomy() {
      return this.vaultSupport != null && this.vaultSupport.getEconomy() != null;
   }

   public VaultSupport getVault() {
      return this.vaultSupport;
   }

   @Nullable
   public MMOItem getMMOItem(@Nullable Type var1, @Nullable String var2, @Nullable PlayerData var3) {
      if (var1 != null && var2 != null) {
         MMOItemTemplate var4 = this.getTemplates().getTemplate(var1, var2);
         return var4 == null ? null : var4.newBuilder(var3).build();
      } else {
         return null;
      }
   }

   @Nullable
   public ItemStack getItem(@Nullable Type var1, @Nullable String var2, @NotNull PlayerData var3) {
      MMOItem var4 = this.getMMOItem(var1, var2, var3);
      return var4 == null ? null : var4.newBuilder().build();
   }

   @Nullable
   public MMOItem getMMOItem(@Nullable Type var1, @Nullable String var2, int var3, @Nullable ItemTier var4) {
      if (var1 != null && var2 != null) {
         MMOItemTemplate var5 = this.getTemplates().getTemplate(var1, var2);
         return var5 == null ? null : var5.newBuilder(var3, var4).build();
      } else {
         return null;
      }
   }

   @Nullable
   public ItemStack getItem(@Nullable Type var1, @Nullable String var2, int var3, @Nullable ItemTier var4) {
      MMOItem var5 = this.getMMOItem(var1, var2, var3, var4);
      return var5 == null ? null : var5.newBuilder().build();
   }

   @Nullable
   public MMOItem getMMOItem(@Nullable Type var1, @Nullable String var2) {
      return this.getMMOItem(var1, var2, 0, null);
   }

   @Nullable
   public ItemStack getItem(@Nullable String var1, @Nullable String var2) {
      return var1 != null && var2 != null ? this.getItem(this.getTypes().get(var1), var2) : null;
   }

   @Nullable
   public ItemStack getItem(@Nullable Type var1, @Nullable String var2) {
      if (var1 != null && var2 != null) {
         MMOItem var3 = this.getMMOItem(var1, var2);
         return var3 == null ? null : var3.newBuilder().build();
      } else {
         return null;
      }
   }

   @Nullable
   public static Type getType(@Nullable ItemStack var0) {
      return getType(NBTItem.get(var0));
   }

   @Nullable
   public static Type getType(@Nullable NBTItem var0) {
      return plugin.getTypes().get(getTypeName(var0));
   }

   @Nullable
   public static String getTypeName(@Nullable ItemStack var0) {
      return getTypeName(NBTItem.get(var0));
   }

   @Nullable
   public static String getTypeName(@Nullable NBTItem var0) {
      if (var0 == null) {
         return null;
      } else {
         return !var0.hasType() ? null : var0.getType();
      }
   }

   @Nullable
   public static String getID(@Nullable ItemStack var0) {
      return getID(NBTItem.get(var0));
   }

   @Nullable
   public static String getID(@Nullable NBTItem var0) {
      return var0 == null ? null : var0.getString("MMOITEMS_ITEM_ID");
   }

   public static void print(@Nullable Level var0, @Nullable String var1, @Nullable String var2, @NotNull String... var3) {
      if (var1 == null) {
         var1 = "< null >";
      }

      if (var0 != null) {
         plugin.getLogger().log(var0, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), var1, var3));
      } else {
         FriendlyFeedbackMessage var4 = new FriendlyFeedbackMessage("", var2);
         FriendlyFeedbackMessage var5 = FriendlyFeedbackProvider.generateMessage(var4, var1, var3);
         getConsole().sendMessage(var5.forConsole(FFPMMOItems.get()));
      }
   }

   public static void log(@Nullable String var0, @NotNull String... var1) {
      print(null, var0, null, var1);
   }

   @NotNull
   public static ConsoleCommandSender getConsole() {
      return plugin.getServer().getConsoleSender();
   }

}