package io.lumine.mythic.core.menus;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.CrucibleSupport;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.DoubleProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.ReloadableMenu;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public abstract class MythicMenu<T> extends ReloadableMenu<T> {
   private static final String CONDITIONAL_KEY = "Conditional";
   private static final String OVERRIDES_KEY = "Overrides";
   public static final StringProp MENU_CLICK_SOUND = Property.String(Scope.CONFIG_GENERAL, "Menus.ButtonClick.Sound", "item.armor.equip_turtle");
   public static final DoubleProp MENU_CLICK_PITCH = Property.Double(Scope.CONFIG_GENERAL, "Menus.ButtonClick.Pitch", 2.0);
   public static final DoubleProp MENU_CLICK_VOLUME = Property.Double(Scope.CONFIG_GENERAL, "Menus.ButtonClick.Volume", 0.4);
   @Getter
   protected MythicBukkit plugin;
   @Getter
   protected MenuManager menuManager;
   protected final Pack menuPack;
   protected final MythicConfig menuConfig;

   public MythicMenu(MythicBukkit core, MenuManager manager, String menuFile) {
      this(core, manager, new MenuProp(core, core.getPropertyFileInternal(core.getClass().getResourceAsStream(menuFile)), "Menu", null), null, null);
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, MenuProp menuProp) {
      this(core, manager, menuProp, null, null);
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, String menuFile, boolean buildOnOpen) {
      this(
         core, manager, new MenuProp(core, core.getPropertyFileInternal(core.getClass().getResourceAsStream(menuFile)), "Menu", null), null, null, buildOnOpen
      );
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, Pack pack, MenuProp menuProp, MythicConfig menuConfig) {
      this(core, manager, menuProp, pack, menuConfig, false);
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, MenuProp menuProp, Pack pack, MythicConfig menuConfig) {
      this(core, manager, menuProp, pack, menuConfig, false);
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, Pack pack, MenuProp menuProp, MythicConfig menuConfig, boolean buildOnOpen) {
      this(core, manager, menuProp, pack, menuConfig, buildOnOpen);
   }

   public MythicMenu(MythicBukkit core, MenuManager manager, MenuProp menuProp, Pack pack, MythicConfig menuConfig, boolean buildOnOpen) {
      super(menuProp, buildOnOpen);
      this.plugin = core;
      this.menuManager = manager;
      this.menuPack = pack;
      this.menuConfig = menuConfig;
      if (menuConfig != null) {
         this.configureSharedBuildPolicy(menuConfig);
         this.registerConfiguredUiElements(pack, menuConfig);
      }
   }

   protected EditableMenuBuilder<T> applyCustomStuff(EditableMenuBuilder<T> builder) {
      builder = this.applyPlaceholderTitle(builder);
      builder.getIcons().values().forEach(icon -> {
         MythicConfigImpl config = new MythicConfigImpl(icon.getConfigurationSection());
         this.configureConfiguredIcon(icon.getBuilder(), config);
      });
      return builder;
   }

   public EditableMenuBuilder<T> addPageButtons(EditableMenuBuilder<T> builder) {
      builder.getIcon("NEXT_PAGE").ifPresent(icon -> icon.getBuilder().click((profile, player) -> {
         player.playSound(player.getLocation(), "item.book.page_turn", 1.0F, 1.0F);
         this.nextPage(player);
      }));
      builder.getIcon("PREVIOUS_PAGE").ifPresent(icon -> icon.getBuilder().click((profile, player) -> {
         player.playSound(player.getLocation(), "item.book.page_turn", 1.0F, 1.0F);
         this.previousPage(player);
      }));
      return builder;
   }

   public final void playMenuClick(Player player) {
      double volume = MENU_CLICK_VOLUME.get();
      double pitch = MENU_CLICK_PITCH.get();
      player.playSound(player.getLocation(), MENU_CLICK_SOUND.get(), (float)volume, (float)pitch);
   }

   protected final void configureSharedBuildPolicy(MythicConfig menuConfig) {
      boolean defaultValue = !this.hasConditionalOverrides(menuConfig);
      if (!menuConfig.getBoolean("Shared", defaultValue)) {
         this.setBuildOnOpen();
      }
   }

   protected final void registerConfiguredUiElements(Pack pack, MythicConfig menuConfig) {
      if (pack != null && MythicBukkit.inst().getCompatibility().getCrucible().isPresent()) {
         if (menuConfig.contains("Menu.UIElements")) {
            CrucibleSupport crucible = MythicBukkit.inst().getCompatibility().getCrucible().get();
            if (!menuConfig.isList("Menu.UIElements")) {
               String line = menuConfig.getString("Menu.UIElements");
               MythicLineConfigImpl mlc = new MythicLineConfigImpl(line);
               crucible.addFontImageToGeneration(pack, new CrucibleSupport.FontGenerationDef(mlc));
            } else {
               for (String line : menuConfig.getStringList("Menu.UIElements")) {
                  MythicLineConfigImpl mlc = new MythicLineConfigImpl(line);
                  crucible.addFontImageToGeneration(pack, new CrucibleSupport.FontGenerationDef(mlc));
               }
            }
         }
      }
   }

   protected final EditableMenuBuilder<T> applyPlaceholderTitle(EditableMenuBuilder<T> builder) {
      String display = builder.getBuilder().getTitleFunc().apply(null);
      PlaceholderString placeholder = PlaceholderString.of(display);
      if (placeholder.isStatic()) {
         String resolved = placeholder.get();
         builder.getBuilder().title(input -> resolved);
      } else {
         builder.getBuilder().title(input -> {
            PlayerData profile = this.resolvePlaceholderProfile(input);
            return profile != null ? placeholder.get(profile) : display;
         });
      }

      return builder;
   }

   protected final void applyConditionalIconOverrides(IconBuilder<T> iconBuilder, MythicConfig config, BiConsumer<IconBuilder<T>, MythicConfig> applier) {
      if (this.hasConditionalOverrides(config)) {
         this.setBuildOnOpen();

         try {
            for (MythicConfig mapConfig : this.getConditionalEntries(config)) {
               List<String> conditionLines = new ArrayList<>();
               if (mapConfig.contains("ClickConditions")) {
                  conditionLines.addAll(mapConfig.getStringList("ClickConditions"));
                  if (conditionLines.isEmpty()) {
                     String single = mapConfig.getString("ClickConditions");
                     if (single != null && !single.isBlank()) {
                        conditionLines.add(single);
                     }
                  }
               } else if (mapConfig.contains("Conditions")) {
                  conditionLines.addAll(mapConfig.getStringList("Conditions"));
                  if (conditionLines.isEmpty()) {
                     String single = mapConfig.getString("Conditions");
                     if (single != null && !single.isBlank()) {
                        conditionLines.add(single);
                     }
                  }
               }

               List<SkillCondition> conditions = MythicBukkit.inst().getSkillManager().getConditions(conditionLines);
               iconBuilder.conditionalOverride((state, player) -> {
                  for (SkillCondition condition : conditions) {
                     if (!condition.evaluateEntity(BukkitAdapter.adapt(player))) {
                        return false;
                     }
                  }

                  return true;
               }, conditionalIconBuilder -> applier.accept(conditionalIconBuilder, mapConfig));
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }

      applier.accept(iconBuilder, config);
   }

   private List<MythicConfig> getConditionalEntries(MythicConfig config) {
      ArrayList<MythicConfig> entries = new ArrayList<>();
      entries.addAll(config.getConfigurationList("Conditional"));
      entries.addAll(config.getConfigurationList("Overrides"));
      if (!entries.isEmpty()) {
         return entries;
      } else if (config.isConfigurationSection("Conditional")) {
         return List.of(config.getNestedConfig("Conditional"));
      } else {
         return config.isConfigurationSection("Overrides") ? List.of(config.getNestedConfig("Overrides")) : List.of();
      }
   }

   private boolean hasConditionalOverrides(MythicConfig config) {
      return config.isSet("Conditional") || config.isSet("Overrides");
   }

   protected void configureConfiguredIcon(IconBuilder<T> iconBuilder, MythicConfig config) {
      this.applyConditionalIconOverrides(iconBuilder, config, this::applyConfiguredIconContents);
   }

   protected void applyConfiguredIconContents(IconBuilder<T> iconBuilder, MythicConfig config) {
      this.applyConfiguredIconVisuals(iconBuilder, config, this.menuPack);
   }

   protected PlayerData resolvePlaceholderProfile(T input) {
      if (input instanceof Player player) {
         return this.getPlugin().getPlayerManager().getProfile(player);
      } else {
         return input instanceof PlayerData profile ? profile : null;
      }
   }

   protected final void applyConfiguredIconVisuals(IconBuilder<T> iconBuilder, MythicConfig config, Pack pack) {
      try {
         Material material = Material.valueOf(config.getString("Material", "PAPER").toUpperCase());
         iconBuilder.material(material);
      } catch (Throwable e) {
         iconBuilder.material(Material.PAPER);
      }

      if (config.isSet("Display")) {
         String display = config.getString("Display");
         PlaceholderString placeholder = PlaceholderString.of(display);
         if (placeholder.isStatic()) {
            String resolved = placeholder.get();
            iconBuilder.name(input -> resolved);
         } else {
            iconBuilder.name(input -> {
               PlayerData profile = this.resolvePlaceholderProfile(input);
               return profile != null ? placeholder.get(profile) : display;
            });
         }
      }

      if (config.isSet("Lore")) {
         List<String> iconLore = config.getStringList("Lore");
         int loreSize = iconLore.size();
         List<PlaceholderString> placeholderedLore = new ArrayList<>(loreSize);
         boolean allStatic = true;

         for (String line : iconLore) {
            PlaceholderString ph = PlaceholderString.of(line);
            placeholderedLore.add(ph);
            if (allStatic && !ph.isStatic()) {
               allStatic = false;
            }
         }

         if (allStatic) {
            List<String> staticLore = List.copyOf(iconLore);
            iconBuilder.lore(input -> staticLore);
         } else {
            iconBuilder.lore(input -> {
               PlayerData profile = this.resolvePlaceholderProfile(input);
               if (profile == null) {
                  return new ArrayList<>(iconLore);
               } else {
                  List<String> lore = new ArrayList<>(loreSize);

                  for (PlaceholderString linex : placeholderedLore) {
                     lore.addAll(List.of(linex.get(profile).split("\n", -1)));
                  }

                  return lore;
               }
            });
         }
      }

      if (config.isSet("Model")) {
         if (!config.isInt("Model")) {
            String model = config.getString("Model");
            if (pack != null
               && MythicBukkit.inst().getCompatibility().getCrucible().isPresent()
               && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
               CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
                  .getCompatibility()
                  .getCrucible()
                  .get()
                  .addItemToGeneration(pack, model);
               generationFeedback.getItemModel().ifPresent(iconBuilder::itemModel);
            } else {
               if (!model.contains(":")) {
                  model = "minecraft:" + model;
               }

               iconBuilder.itemModel(NamespacedKey.fromString(model));
            }
         } else {
            iconBuilder.model(config.getInt("Model", 0));
         }
      }

      List<ItemFlag> itemFlags = new ArrayList<>();
      if (config.isSet("Hide")) {
         for (String hide : config.getStringList("Hide")) {
            hide = hide.toUpperCase(Locale.ROOT);
            if (!hide.startsWith("HIDE_")) {
               hide = "HIDE_" + hide;
            }

            try {
               itemFlags.add(ItemFlag.valueOf(hide));
            } catch (IllegalArgumentException ex) {
               Log.error("Invalid Hide option ''{0}'' in menu icon config", hide);
            }
         }
      }

      iconBuilder.hideFlags(config.getBoolean("HideFlags", itemFlags.isEmpty()));
      if (!itemFlags.isEmpty()) {
         iconBuilder.flags(itemFlags);
      }

      if (config.isSet("TooltipStyle")) {
         String tooltipStyle = config.getString("TooltipStyle").toLowerCase(Locale.ROOT);
         if (!tooltipStyle.contains(":")) {
            Optional<CrucibleSupport> crucible = MythicBukkit.inst().getCompatibility().getCrucible();
            String namespace = crucible.isPresent() && crucible.get().isGeneratedTooltip(tooltipStyle) ? crucible.get().getPackNamespace() : "minecraft";
            tooltipStyle = namespace + ":" + tooltipStyle;
         }

         NamespacedKey tooltipKey = NamespacedKey.fromString(tooltipStyle);
         if (tooltipKey == null) {
            Log.error("Invalid TooltipStyle ''{0}'' in menu icon config", tooltipStyle);
         } else {
            iconBuilder.tooltipStyle(tooltipKey);
         }
      }

      if (config.isSet("Glint")) {
         boolean glint = config.getBoolean("Glint", false);
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            iconBuilder.glint(input -> glint);
         } else if (glint) {
            iconBuilder.enchant(Enchantment.VANISHING_CURSE, 1);
         }
      }

      if (config.isSet("Enchantments")) {
         for (String linex : config.getStringList("Enchantments")) {
            Entry<Enchantment, Integer> parsed = parseMenuEnchantment(linex);
            if (parsed != null) {
               iconBuilder.enchant(parsed.getKey(), parsed.getValue());
            } else {
               Log.error("Invalid enchantment ''{0}'' in menu icon config", linex);
            }
         }
      }
   }

   private static Entry<Enchantment, Integer> parseMenuEnchantment(String in) {
      String namePart = in;
      String levelPart = null;
      String[] spaced = in.split(" ");
      if (spaced.length > 1) {
         namePart = spaced[0];
         levelPart = spaced[1];
      }

      Enchantment enchantment = null;
      NamespacedKey key = NamespacedKey.fromString(namePart.toLowerCase(Locale.ROOT));
      if (key != null) {
         enchantment = Registry.ENCHANTMENT.get(key);
      }

      if (enchantment == null && namePart.contains(":")) {
         String[] split = namePart.split(":");
         enchantment = Enchantment.getByName(split[0].toUpperCase(Locale.ROOT));
         if (split.length > 1 && levelPart == null) {
            levelPart = split[1];
         }
      }

      if (enchantment == null) {
         enchantment = Enchantment.getByName(namePart.toUpperCase(Locale.ROOT));
      }

      if (enchantment == null) {
         return null;
      } else {
         int level = 1;
         if (levelPart != null) {
            try {
               level = Integer.parseInt(levelPart);
            } catch (NumberFormatException e) {
            }
         }

         return Map.entry(enchantment, level);
      }
   }
}
