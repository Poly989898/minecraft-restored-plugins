package me.TechsCode.UltraPermissions.base.addons.gui;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Color;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class AddonsOverview extends GUI {
   private final SpigotTechPlugin plugin;
   private final Color color;
   private static final Phrase GUI_TITLE = Phrase.create("AddonsOverview.gui.title", "Overview > Addons");
   private static final Phrase MARKETPLACE_BUTTON_NAME = Phrase.create("AddonsOverview.marketplaceButton.name", "Marketplace");
   private static final Phrase MARKETPLACE_BUTTON_LORE_MARKETPLACE_ONLINE = Phrase.create(
      "AddonsOverview.marketplaceButton.lore.marketplace.online", "Click to browse our **marketplace**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase MARKETPLACE_BUTTON_LORE_MARKETPLACE_OFFLINE = Phrase.create(
      "AddonsOverview.marketplaceButton.lore.marketplace.offline", "Our Marketplace is currently **unavailable**", Colors.GOLD, Colors.RED
   );
   private static final Phrase MARKETPLACE_BUTTON_LORE_DESC_LINE_1 = Phrase.create(
      "AddonsOverview.marketplaceButton.lore.description.line1", "Our Marketplace provides you", Colors.GRAY
   );
   private static final Phrase MARKETPLACE_BUTTON_LORE_DESC_LINE_2 = Phrase.create(
      "AddonsOverview.marketplaceButton.lore.description.line2", "with various addons that are", Colors.GRAY
   );
   private static final Phrase MARKETPLACE_BUTTON_LORE_DESC_LINE_3 = Phrase.create(
      "AddonsOverview.marketplaceButton.lore.description.line3", "downloadable with just one click!", Colors.GRAY
   );
   private static final Phrase INSTALLED_ADDONS_BUTTON_NAME = Phrase.create("AddonsOverview.installedAddonsButton.name", "Installed");
   private static final Phrase INSTALLED_ADDONS_BUTTON_LORE_CLICK = Phrase.create(
      "AddonsOverview.installedAddonsButton.lore.click", "Click to view **installed Addons**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase INSTALLED_ADDONS_BUTTON_LORE_AMOUNT = Phrase.create(
      "AddonsOverview.installedAddonsButton.lore.amount", "Amount: **%amount%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase INSTALLED_ADDONS_BUTTON_LORE_DESC_LINE_1 = Phrase.create(
      "AddonsOverview.installedAddonsButton.lore.description.line1", "View, edit, and remove all installed addons.", Colors.GRAY
   );
   private static final Phrase INSTALLED_ADDONS_BUTTON_LORE_DESC_LINE_2 = Phrase.create(
      "AddonsOverview.installedAddonsButton.lore.description.line2", "Don't want an addon anymore? Then delete it!", Colors.GRAY
   );

   public AddonsOverview(Player player, Color color, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
      this.color = color;
      this.plugin = spigotTechPlugin;
   }

   public abstract void onBack();

   @Override
   public String getCurrentTitle() {
      return GUI_TITLE.get();
   }

   @Override
   public void construct(Model model) {
      model.setSlots(36);
      model.button(12, this::MarketplaceButton);
      model.button(16, this::InstalledAddonsButton);
      model.button(32, button -> Common.BackButton(button, actionType -> this.onBack()));
   }

   private void MarketplaceButton(Button button) {
      boolean isCloudOnline = this.plugin.getAddonManager().isCloudOnline();
      button.material(XMaterial.EMERALD)
         .name(Animation.wave(MARKETPLACE_BUTTON_NAME.get(), isCloudOnline ? this.color : Colors.RED, Colors.WHITE))
         .lore(
            isCloudOnline ? MARKETPLACE_BUTTON_LORE_MARKETPLACE_ONLINE.get() : MARKETPLACE_BUTTON_LORE_MARKETPLACE_OFFLINE.get(),
            "",
            MARKETPLACE_BUTTON_LORE_DESC_LINE_1.get(),
            MARKETPLACE_BUTTON_LORE_DESC_LINE_2.get(),
            MARKETPLACE_BUTTON_LORE_DESC_LINE_3.get()
         );
      button.action(actionType -> {
         if (isCloudOnline) {
            new AddonsMarketplaceListView(this.p, this.plugin) {
               @Override
               public void onBack() {
                  AddonsOverview.this.reopen();
               }
            };
         }
      });
   }

   private void InstalledAddonsButton(Button button) {
      button.material(XMaterial.HOPPER_MINECART)
         .name(Animation.wave(INSTALLED_ADDONS_BUTTON_NAME.get(), this.color, Colors.WHITE))
         .lore(
            INSTALLED_ADDONS_BUTTON_LORE_CLICK.get(),
            "",
            INSTALLED_ADDONS_BUTTON_LORE_AMOUNT.get().replace("%amount%", String.valueOf(this.plugin.getAddonManager().getInstalledAddons().size())),
            "",
            INSTALLED_ADDONS_BUTTON_LORE_DESC_LINE_1.get(),
            INSTALLED_ADDONS_BUTTON_LORE_DESC_LINE_2.get()
         );
      button.action(actionType -> new InstalledAddonsView(this.p, this.plugin) {
         @Override
         public void onBack() {
            AddonsOverview.this.reopen();
         }
      });
   }
}
