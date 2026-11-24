package me.TechsCode.UltraPermissions.base.addons.gui;

import java.util.Optional;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.addons.InstalledAddon;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudAddon;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class AddonsMarketplaceListView extends PageableGUI<CloudAddon> {
   private final SpigotTechPlugin plugin;
   private boolean loading;
   private static final Phrase LOADING = Phrase.create("AddonsMarketplaceListView.loading", "Loading...");
   private static final Phrase TITLE = Phrase.create("AddonsMarketplaceListView.gui.title", "Addons > Marketplace");
   private static final Phrase BUTTON_LORE_ADDON_INSTALL = Phrase.create(
      "AddonsMarketplaceListView.button.lore.addon.install", "Click to **install** this addon", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase BUTTON_LORE_ADDON_UPDATE = Phrase.create(
      "AddonsMarketplaceListView.button.lore.addon.update", "Click to **update** this addon", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase BUTTON_LORE_ADDON_INSTALLED = Phrase.create(
      "AddonsMarketplaceListView.button.lore.addon.installed", "This addon is **installed**", Colors.Gray, Colors.GREEN
   );
   private static final Phrase BUTTON_LORE_DESCRIPTION = Phrase.create("AddonsMarketplaceListView.button.lore.description", "Description:", Colors.GRAY);
   private static final Phrase BUTTON_LORE_CREATOR = Phrase.create(
      "AddonsMarketplaceListView.button.lore.creator", "Creator: **%creatorName%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase BUTTON_LORE_DOWNLOADS = Phrase.create(
      "AddonsMarketplaceListView.button.lore.downloads", "Downloads: **%amount%**", Colors.GRAY, Colors.YELLOW
   );

   public AddonsMarketplaceListView(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
      this.plugin = spigotTechPlugin;
   }

   @Override
   public abstract void onBack();

   @Override
   public String getTitle() {
      return this.loading ? LOADING.get() : TITLE.get();
   }

   @Override
   public SearchFeature<CloudAddon> getSearch() {
      return new BasicSearch<CloudAddon>() {
         public String[] getSearchableText(CloudAddon cloudAddon) {
            return new String[]{cloudAddon.getName()};
         }
      };
   }

   public CloudAddon[] getObjects() {
      return this.plugin.getAddonManager().getCloudAddons().toArray(new CloudAddon[0]);
   }

   public void construct(Button button, CloudAddon cloudAddon) {
      if (this.loading) {
         button.material(XMaterial.YELLOW_STAINED_GLASS_PANE).name("\u00a7f\u00a7l" + LOADING.get());
      } else {
         Optional first = this.plugin.getAddonManager().getInstalledAddons().stream().filter(installedAddon -> installedAddon.getName().equals(cloudAddon.getName())).findFirst();
         boolean isPresent = first.isPresent();
         boolean flag = first.isPresent() && ((InstalledAddon)first.get()).isOutOfDate();
         button.material(
               isPresent
                  ? (flag ? XMaterial.ORANGE_STAINED_GLASS : XMaterial.from(cloudAddon.getMaterial()).orElse(XMaterial.GREEN_STAINED_GLASS))
                  : XMaterial.WHITE_STAINED_GLASS
            )
            .name(Animation.wave(cloudAddon.getName(), isPresent ? (flag ? Colors.Orange : Colors.GREEN) : Colors.Gray, Colors.WHITE) + " \u00a7fv" + cloudAddon.getVersion())
            .lore(isPresent ? (flag ? BUTTON_LORE_ADDON_UPDATE.get() : BUTTON_LORE_ADDON_INSTALLED.get()) : BUTTON_LORE_ADDON_INSTALL.get());
         button.item().appendLore("");
         if (cloudAddon.getDescription() != null) {
            button.item().appendLore(BUTTON_LORE_DESCRIPTION.get());

            for (String text : Tools.lineSplitter(cloudAddon.getDescription(), 35)) {
               button.item().appendLore("\u00a7f" + text);
            }

            button.item().appendLore("");
         }

         button.item()
            .appendLore(
               BUTTON_LORE_CREATOR.get().replace("%creatorName%", cloudAddon.getAuthor()),
               BUTTON_LORE_DOWNLOADS.get().replace("%amount%", String.valueOf(cloudAddon.getDownloads()))
            );
         button.action(actionType -> {
            if (!isPresent || flag) {
               this.loading = true;
               this.plugin.getAddonManager().download(cloudAddon);
               this.loading = false;
            }
         });
      }
   }
}
