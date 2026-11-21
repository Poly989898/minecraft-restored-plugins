package io.lumine.mythic.core.menus;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMenusPreLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.ReloadableMenu;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.menus.config.ConfigMenu;
import io.lumine.mythic.core.menus.custom.CustomMenu;
import io.lumine.mythic.core.menus.custom.CustomMenuBindingFunction;
import io.lumine.mythic.core.menus.custom.CustomMenuButtonFunction;
import io.lumine.mythic.core.menus.custom.CustomPlayerMenu;
import io.lumine.mythic.core.menus.items.ItemBrowseMenu;
import io.lumine.mythic.core.menus.items.ItemEditorMenu;
import io.lumine.mythic.core.menus.items.editor.components.AttackRangeComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.ConsumableComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.EquippableComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.FoodComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.PiercingWeaponComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.UseCooldownComponentMenu;
import io.lumine.mythic.core.menus.items.editor.components.WeaponComponentMenu;
import io.lumine.mythic.core.menus.mobs.MobBrowseMenu;
import io.lumine.mythic.core.menus.mobs.MobEditorMenu;
import io.lumine.mythic.core.menus.spawners.SpawnerBrowseMenu;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorMenu;
import io.lumine.mythic.core.packs.PackExecutor;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;

public class MenuManager extends ReloadableModule<MythicBukkit> {
   @Getter
   private MainMenu mainMenu;
   @Getter
   private ConfigMenu configMenu;
   @Getter
   private ColorPickerMenu colorPickerMenu;
   @Getter
   private ItemBrowseMenu itemBrowseMenu;
   @Getter
   private ItemEditorMenu itemEditorMenu;
   @Getter
   private PiercingWeaponComponentMenu piercingWeaponComponentMenu;
   @Getter
   private AttackRangeComponentMenu attackRangeComponentMenu;
   @Getter
   private ConsumableComponentMenu consumableComponentMenu;
   @Getter
   private FoodComponentMenu foodComponentMenu;
   @Getter
   private UseCooldownComponentMenu useCooldownComponentMenu;
   @Getter
   private EquippableComponentMenu equippableComponentMenu;
   @Getter
   private WeaponComponentMenu weaponComponentMenu;
   @Getter
   private MobBrowseMenu mobBrowseMenu;
   @Getter
   private MobEditorMenu mobEditorMenu;
   @Getter
   private SpawnerBrowseMenu spawnerBrowseMenu;
   @Getter
   private SpawnerEditorMenu spawnerEditorMenu;
   private Map<String, CustomMenu> customMenus = Maps.newConcurrentMap();
   private Map<String, ItemFactory> customButtons = Maps.newConcurrentMap();
   private Map<String, Function<MythicConfig, CustomMenuBindingFunction>> customMenuFunctions = Maps.newConcurrentMap();
   private Map<String, Function<MythicConfig, CustomMenuButtonFunction>> customButtonFunctions = Maps.newConcurrentMap();

   public MenuManager(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      if (this.mainMenu == null) {
         this.mainMenu = new MainMenu(plugin, this);
         this.configMenu = new ConfigMenu(plugin, this);
         this.itemBrowseMenu = new ItemBrowseMenu(plugin, this);
         this.itemEditorMenu = new ItemEditorMenu(plugin, this);
         this.piercingWeaponComponentMenu = new PiercingWeaponComponentMenu(plugin, this);
         this.attackRangeComponentMenu = new AttackRangeComponentMenu(plugin, this);
         this.consumableComponentMenu = new ConsumableComponentMenu(plugin, this);
         this.foodComponentMenu = new FoodComponentMenu(plugin, this);
         this.useCooldownComponentMenu = new UseCooldownComponentMenu(plugin, this);
         this.equippableComponentMenu = new EquippableComponentMenu(plugin, this);
         this.weaponComponentMenu = new WeaponComponentMenu(plugin, this);
         this.mobBrowseMenu = new MobBrowseMenu(plugin, this);
         this.mobEditorMenu = new MobEditorMenu(plugin, this);
         this.spawnerBrowseMenu = new SpawnerBrowseMenu(plugin, this);
         this.spawnerEditorMenu = new SpawnerEditorMenu(plugin, this);
      }

      this.mainMenu.reload();
      this.configMenu.reload();
      this.itemBrowseMenu.reload();
      this.itemEditorMenu.reload();
      this.piercingWeaponComponentMenu.reload();
      this.attackRangeComponentMenu.reload();
      this.consumableComponentMenu.reload();
      this.foodComponentMenu.reload();
      this.useCooldownComponentMenu.reload();
      this.equippableComponentMenu.reload();
      this.weaponComponentMenu.reload();
      this.mobBrowseMenu.reload();
      this.mobEditorMenu.reload();
      this.spawnerBrowseMenu.reload();
      this.spawnerEditorMenu.reload();
      this.getPlugin().getClock().queueAfterLoad(() -> {
         this.colorPickerMenu = new ColorPickerMenu();
         this.loadCustomMenus();
      });
   }

   @Override
   public void unload() {
   }

   private void loadCustomMenus() {
      this.customMenus.clear();
      Events.call(new MythicMenusPreLoadEvent(this.plugin, this));

      for (PackExecutor.ConfigFile configFile : this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.MENU)) {
         for (String key : Property.NodeList(this.plugin, configFile.file(), "").get()) {
            this.registerCustomMenu(configFile.pack(), configFile.file(), key);
         }
      }

      this.customMenus.values().forEach(ReloadableMenu::reload);
   }

   private void registerCustomMenu(Pack pack, File file, String key) {
      if (!this.customMenus.containsKey(key)) {
         MenuProp prop = new MenuProp(this.plugin, file, key, null);
         CustomPlayerMenu customMenu = new CustomPlayerMenu(this.plugin, this, pack, file, key, prop);
         this.customMenus.put(key, customMenu);
      }
   }

   public Collection<String> getAvailableMenuNames() {
      return this.customMenus.keySet();
   }

   public Optional<CustomMenu> getMenu(String key) {
      return Optional.ofNullable(this.customMenus.get(key));
   }

   public Optional<CustomMenuBindingFunction> getMenuBindingFunction(String key, MythicConfig config) {
      Function<MythicConfig, CustomMenuBindingFunction> function = this.customMenuFunctions.get(key);
      return function == null ? Optional.empty() : Optional.ofNullable(function.apply(config));
   }

   public Optional<CustomMenuButtonFunction> getMenuButtonFunction(String key, MythicConfig config) {
      Function<MythicConfig, CustomMenuButtonFunction> function = this.customButtonFunctions.get(key);
      return function == null ? Optional.empty() : Optional.ofNullable(function.apply(config));
   }

   public void registerCustomMenuFunction(String[] keys, Function<MythicConfig, CustomMenuBindingFunction> function) {
      for (String key : keys) {
         this.customMenuFunctions.put(key, function);
      }
   }

   public void registerCustomMenuFunction(String key, Function<MythicConfig, CustomMenuBindingFunction> function) {
      this.customMenuFunctions.put(key, function);
   }

   public void registerCustomButtonFunction(String[] keys, Function<MythicConfig, CustomMenuButtonFunction> function) {
      for (String key : keys) {
         this.customButtonFunctions.put(key, function);
      }
   }

   public void registerCustomButtonFunction(String key, Function<MythicConfig, CustomMenuButtonFunction> function) {
      this.customButtonFunctions.put(key, function);
   }
}
