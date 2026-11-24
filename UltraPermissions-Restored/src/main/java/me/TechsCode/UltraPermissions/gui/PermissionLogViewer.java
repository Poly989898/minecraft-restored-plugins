package me.TechsCode.UltraPermissions.gui;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.dialogs.TogglePluginDialog;
import me.TechsCode.UltraPermissions.permissionDatabase.PermissionInfo;
import me.TechsCode.UltraPermissions.permissionlogger.LoggedPermission;
import org.bukkit.entity.Player;

public abstract class PermissionLogViewer extends PageableGUI<LoggedPermission> {
   private static final Phrase TITLE = Phrase.create("permissionLogViewer.title", "Overview > Permission Log");
   private static final Phrase ITEM_ACTION = Phrase.create(
      "permissionLogViewer.item.action", "Click to add this permission to a **group** or **user**", Colors.GRAY, Colors.AQUA, Colors.AQUA
   );
   private static final Phrase RESULT_RESULT_ATTRIBUTE_PASSED = Phrase.create(
      "permissionLogViewer.item.passedAttribute", "Player **has** this Permission", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase RESULT_RESULT_ATTRIBUTE_FAILED = Phrase.create(
      "permissionLogViewer.item.failedAttribute", "Player does **not** have this Permission", Colors.GRAY, Colors.RED
   );
   private static final Phrase RESULT_TIME_ATTRIBUTE = Phrase.create(
      "permissionLogViewer.item.timeAttribute", "Checked **%duration%** ago", Colors.GRAY, Colors.RED
   );
   private static final Phrase SOURCE_ATTRIBUTE = Phrase.create(
      "permissionLogViewer.item.sourceAttribute", "Allowed by **%source%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase FILTER_TOGGLE_BUTTON = Phrase.create(
      "permissionLogViewer.filter.toggle", "**Left Click** to toggle Plugins", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase FILTER_TOGGLE_LORE = Phrase.create("permissionLogViewer.filter.lore", "Toggled Plugins:", Colors.GRAY);
   private final Player p;
   private final UltraPermissions plugin;

   public PermissionLogViewer(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.p = player;
      this.plugin = ultraPermissions;
   }

   @Override
   public String getTitle() {
      return TITLE.get();
   }

   public void construct(Button button, LoggedPermission loggedPermission) {
      button.material(loggedPermission.getOutcome() ? XMaterial.LIME_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)
         .name(Animation.wave(loggedPermission.getPermission(), loggedPermission.getOutcome() ? Colors.GREEN : Colors.RED, Colors.WHITE))
         .lore(ITEM_ACTION.get(), "", (loggedPermission.getOutcome() ? RESULT_RESULT_ATTRIBUTE_PASSED : RESULT_RESULT_ATTRIBUTE_FAILED).get());
      if (loggedPermission.getOutcome()) {
         button.item().appendLore("", SOURCE_ATTRIBUTE.get().replace("%source%", loggedPermission.getSource()));
      }

      PermissionInfo info = this.plugin.getPermissionDatabase().getInfo(loggedPermission.getPermission());
      if (info != null) {
         button.item().appendLore("");
         button.item().appendLore(info.asLore());
      }

      if (loggedPermission.getTime() > 1000L) {
         String timeString = Tools.getTimeString(System.currentTimeMillis() - loggedPermission.getTime(), TimeUnit.MILLISECONDS);
         button.item().appendLore("", RESULT_TIME_ATTRIBUTE.get().replace("%duration%", timeString));
      }

      button.action(actionType -> new PermissionLogAddPermission(this.p, this.plugin, loggedPermission.getPermission()) {
         @Override
         public void onBack() {
            PermissionLogViewer.this.reopen();
         }
      });
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(this.getRightOptionSlot(), button -> {
         button.material(XMaterial.REDSTONE_BLOCK).name(Animation.wave("Filter", Colors.AQUA, Colors.WHITE)).appendLore(FILTER_TOGGLE_BUTTON.get());
         if (!this.plugin.getToggledPluginsRegistry().isEmpty()) {
            button.item().appendLore("").appendLore(FILTER_TOGGLE_LORE.get());
            String lastPlugin = "";

            for (String pluginName : this.plugin.getToggledPluginsRegistry()) {
               if (!pluginName.equals(lastPlugin)) {
                  lastPlugin = pluginName;
                  button.item().appendLore("\u00a77- \u00a7c" + pluginName);
               }
            }
         }

         button.action(actionType -> {
            if (actionType == ActionType.LEFT) {
               new TogglePluginDialog(this.p, this.plugin) {
                  @Override
                  public void reopen() {
                     PermissionLogViewer.this.reopen();
                  }
               };
            }
         });
      });
   }

   public LoggedPermission[] getObjects() {
      return Arrays.stream(this.plugin.getLoggedPermissionChecks()).filter(loggedPermission -> loggedPermission.getPlayer().equals(this.p)).toArray(LoggedPermission[]::new);
   }

   @Override
   public SearchFeature<LoggedPermission> getSearch() {
      return new BasicSearch<LoggedPermission>() {
         public String[] getSearchableText(LoggedPermission value) {
            return new String[]{value.getPermission(), value.getSource()};
         }
      };
   }
}
