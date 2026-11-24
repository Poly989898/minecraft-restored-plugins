package me.TechsCode.UltraPermissions.base.addons.gui;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.addons.InstalledAddon;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudAddon;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class InstalledAddonsView extends PageableGUI<InstalledAddon> {
   protected final SpigotTechPlugin plugin;
   private static final Phrase GUI_TITLE = Phrase.create("InstalledAddonsView.gui.title", "Addons > Installed");
   private static final Phrase BUTTON_LORE_PRESS_Q = Phrase.create(
      "InstalledAddonsView.lore.pressQ", "**Press Q** to **delete**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase BUTTON_LORE_ADDON_DELETED = Phrase.create("InstalledAddonsView.lore.addon.deleted", "This addon has been deleted!", Colors.RED);
   private static final Phrase BUTTON_LORE_ADDON_INSTALLED = Phrase.create(
      "InstalledAddonsView.lore.addon.installed", "This addon is **installed** and is currently **running**", Colors.GRAY, Colors.YELLOW, Colors.GREEN
   );
   private static final Phrase BUTTON_LORE_ADDON_REQUIREMENT = Phrase.create(
      "InstalledAddonsView.lore.addon.requirement", "This addon requires **%requirement%** to load", Colors.GRAY, Colors.GOLD
   );
   private static final Phrase BUTTON_LORE_ADDON_ERROR_LINE_1 = Phrase.create(
      "InstalledAddonsView.lore.addon.error.line1", "An error occurred while trying to load this addon,", Colors.RED
   );
   private static final Phrase BUTTON_LORE_ADDON_ERROR_LINE_2 = Phrase.create(
      "InstalledAddonsView.lore.addon.error.line2", "Please contact the addon author!", Colors.YELLOW
   );
   private static final Phrase BUTTON_LORE_ADDON_DESCRIPTION = Phrase.create("InstalledAddonsView.lore.addon.description", "Description:", Colors.GRAY);
   private static final Phrase BUTTON_LORE_ADDON_INSTALLED_FROM_CLOUD = Phrase.create(
      "InstalledAddonsView.lore.addon.installedFromCloud", "This addon was installed from the Cloud", Colors.YELLOW
   );
   private static final Phrase BUTTON_LORE_PRESS_NOT_IN_CLOUD = Phrase.create(
      "InstalledAddonsView.lore.addon.notFoundInCloud", "This addon could not be found in the Cloud", Colors.GRAY
   );

   public InstalledAddonsView(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
      this.plugin = spigotTechPlugin;
   }

   @Override
   public abstract void onBack();

   @Override
   public String getTitle() {
      return GUI_TITLE.get();
   }

   @Override
   public SearchFeature<InstalledAddon> getSearch() {
      return new BasicSearch<InstalledAddon>() {
         public String[] getSearchableText(InstalledAddon installedAddon) {
            return new String[]{installedAddon.getName()};
         }
      };
   }

   public InstalledAddon[] getObjects() {
      return this.plugin.getAddonManager().getInstalledAddons().toArray(new InstalledAddon[0]);
   }

   public void construct(Button button, InstalledAddon installedAddon) {
      XMaterial xMaterial = XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE;
      button.material(installedAddon.isLoaded() ? xMaterial : XMaterial.ORANGE_STAINED_GLASS_PANE)
         .name(Animation.wave(installedAddon.getName(), installedAddon.isLoaded() ? Colors.GREEN : Colors.Orange, Colors.WHITE) + " \u00a77v" + installedAddon.getVersion());
      File addonFile = installedAddon.getAddonFile();
      if (addonFile.exists()) {
         button.item().appendLore(BUTTON_LORE_PRESS_Q.get(), "");
      }

      if (!addonFile.exists()) {
         button.item().appendLore(BUTTON_LORE_ADDON_DELETED.get());
      } else if (installedAddon.isLoaded()) {
         button.item().addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 3);
         button.item().appendLore(BUTTON_LORE_ADDON_INSTALLED.get());
      } else if (installedAddon.getDepend() != null) {
         button.item().appendLore(BUTTON_LORE_ADDON_REQUIREMENT.get().replace("%requirement%", installedAddon.getDepend()));
      } else {
         button.item().appendLore(BUTTON_LORE_ADDON_ERROR_LINE_1.get());
         button.item().appendLore(BUTTON_LORE_ADDON_ERROR_LINE_2.get());
      }

      Optional cloudAddon = installedAddon.getCloudAddon();
      if (cloudAddon.isPresent()) {
         CloudAddon foundCloudAddon = (CloudAddon)cloudAddon.get();
         if (foundCloudAddon.getDescription() != null) {
            button.item().appendLore("");
            button.item().appendLore(BUTTON_LORE_ADDON_DESCRIPTION.get());
            Arrays.stream(Tools.lineSplitter(foundCloudAddon.getDescription(), 45)).forEach(text -> button.item().appendLore("\u00a7f" + text));
            button.item().appendLore("");
         }

         button.item().appendLore(BUTTON_LORE_ADDON_INSTALLED_FROM_CLOUD.get());
      } else {
         button.item().appendLore(BUTTON_LORE_PRESS_NOT_IN_CLOUD.get());
      }

      button.action(actionType -> {
         if (actionType == ActionType.Q) {
            installedAddon.uninstall();
         }
      });
   }
}
