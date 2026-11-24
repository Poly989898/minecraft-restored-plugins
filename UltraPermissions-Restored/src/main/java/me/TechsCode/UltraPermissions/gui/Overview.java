package me.TechsCode.UltraPermissions.gui;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.ColorPalette;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.SkinTexture;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.base.visual.LoreScroller;
import me.TechsCode.UltraPermissions.gui.settings.UpermsSettingsView;
import me.TechsCode.UltraPermissions.migration.InsaneEditorDownload;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Overview extends GUI {
   private final UltraPermissions plugin;
   private final long start;
   private boolean expanded = false;
   private List<SkinTexture> skinTextures;
   private List<String> playerList;
   private List<String> groupList;
   private static final Phrase USERS_TITLE = Phrase.create("overview.users.title", "Users");
   private static final Phrase USERS_ACTION = Phrase.create("overview.users.action", "Click to view all **Users**", Colors.GRAY, Colors.GREEN);
   private static final Phrase USERS_DESC = Phrase.create("overview.users.desc", "%count% Players registered so far", Colors.DARK_GRAY);
   private static final Phrase USERS_SCROLLING_ENTRY = Phrase.create("overview.users.scrollingEntry", "- **%prefix%** %user%", Colors.GRAY, Colors.WHITE);
   private static final Phrase GROUPS_TITLE = Phrase.create("overview.groups.title", "Groups");
   private static final Phrase GROUPS_ACTION = Phrase.create("overview.groups.action", "Click to view all **Groups**", Colors.GRAY, Colors.GREEN);
   private static final Phrase GROUPS_DESC = Phrase.create("overview.groups.desc", "%count% Groups created so far", Colors.DARK_GRAY);
   private static final Phrase GROUPS_SCROLLING_ENTRY = Phrase.create(
      "overview.groups.scrollingEntry", "- **%prefix%** %group% **%suffix%**", Colors.GRAY, Colors.WHITE, Colors.WHITE
   );
   private static final Phrase GROUP_PATHS_BUTTON_NAME = Phrase.create("overview.groupPathsButton.name", "Group Paths");
   private static final Phrase GROUP_PATHS_BUTTON_ACTION = Phrase.create(
      "overview.groupPathsButton.action", "**Click** to view all Group Paths", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase GROUP_PATHS_BUTTON_COUNT = Phrase.create(
      "overview.groupPathsButton.count", "%count% Group Paths created so far", Colors.DARK_GRAY
   );
   private static final Phrase GROUP_PATHS_BUTTON_DESC1 = Phrase.create("overview.groupPathsButton.desc1", "Create customized Group Paths which", Colors.GRAY);
   private static final Phrase GROUP_PATHS_BUTTON_DESC2 = Phrase.create("overview.groupPathsButton.desc2", "a User will go through with the", Colors.GRAY);
   private static final Phrase GROUP_PATHS_BUTTON_DESC3 = Phrase.create("overview.groupPathsButton.desc3", "/promote and /demote command", Colors.GRAY);
   private static final Phrase GROUP_PATHS_BUTTON_DESC4 = Phrase.create("overview.groupPathsButton.desc4", "(e.g Supporter > Moderator > Admin)", Colors.WHITE);
   private static final Phrase PERMISSION_LOG_TITLE = Phrase.create("overview.permissionLog.title", "Permission Log");
   private static final Phrase PERMISSION_LOG_ACTION = Phrase.create(
      "overview.permissionLog.action", "**Click** to open the Permission Log", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase PERMISSION_LOG_DESC = Phrase.create(
      "overview.permissionLog.desc", "Here you can take a look at all recent permission checks", Colors.GRAY
   );
   private static final Phrase INSANE_EDITOR_BUTTON_TITLE = Phrase.create("overview.InsaneEditor.title", "Insane Editor");
   private static final Phrase INSANE_EDITOR_BUTTON_ACTION = Phrase.create(
      "overview.InsaneEditor.action", "**Click** to install **Insane Editor**", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase INSANE_EDITOR_BUTTON_ACTION_INSTALLED = Phrase.create(
      "overview.InsaneEditor.action.installed", "**Click** to display **Insane Editor's** commands", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase INSANE_EDITOR_BUTTON_DESC = Phrase.create(
      "overview.InsaneEditor.desc", "Manage Ultra Permissions through an interactive Web Editor", Colors.GRAY
   );
   private static final Phrase SETTINGS_TITLE = Phrase.create("overview.settings.title", "Settings");
   private static final Phrase SETTINGS_ACTION = Phrase.create("overview.settings.action", "**Click** to view Settings", Colors.GRAY, Colors.AQUA);
   private static final Phrase SETTINGS_DESC = Phrase.create("overview.settings.desc", "Adjust all settings of the plugin", Colors.GRAY);
   private static final Phrase REDUCE_TITLE = Phrase.create("overview.settings.reduce.title", "Reduce");
   private static final Phrase REDUCE_ACTION = Phrase.create("overview.settings.reduce.action", "**Click** to hide advanced features", Colors.GRAY, Colors.AQUA);
   private static final Phrase REDUCE_DESC = Phrase.create("overview.settings.reduce.desc", "This will hide the advanced options", Colors.GRAY);
   private static final Phrase EXPAND_TITLE = Phrase.create("overview.settings.expand.title", "Expand");
   private static final Phrase EXPAND_ACTION = Phrase.create("overview.settings.expand.action", "**Click** to show advanced features", Colors.GRAY, Colors.AQUA);
   private static final Phrase EXPAND_DESC = Phrase.create("overview.settings.expand.desc", "This will show you more advanced options", Colors.GRAY);

   public Overview(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.start = System.currentTimeMillis();
   }

   @Override
   public void reopen() {
      super.reopen();
   }

   @Override
   public void construct(Model model) {
      model.setTitle("Ultra Permissions v" + this.plugin.getVersion());
      model.setSlots(this.expanded ? 54 : 45);
      model.button(this::UsersButton, this.expanded ? 11 : 21);
      model.button(this::GroupsButton, this.expanded ? 17 : 25);
      model.button(this::GroupPathsButton, this.expanded ? 32 : 23);
      if (this.expanded) {
         model.button(this::PermissionLogButton, 30);
         model.button(this::SettingsButton, 34);
         model.button(this::ReduceButton, 50);
      } else {
         model.button(this::ExpandButton, 41);
      }
   }

   private void UsersButton(Button button) {
      button.material(XMaterial.PLAYER_HEAD).name(Animation.wave(USERS_TITLE.get(), ColorPalette.MAIN, Colors.WHITE)).lore(USERS_ACTION.get());
      button.item().appendLore("");
      if (this.playerList == null) {
         this.playerList = this.plugin.getUsers().stream().map(user -> {
            String text = user.getPrefix().orElse("");
            if (text.isEmpty()) {
               Optional first = user.getActiveGroups().bestToWorst().stream().findFirst();
               if (first.isPresent() && ((Group)first.get()).getPrefix().isPresent()) {
                  text = ((Group)first.get()).getPrefix().get() + " ";
               }
            }

            return USERS_SCROLLING_ENTRY.get().replace("%prefix%", text).replace("%user%", user.getName()).trim();
         }).collect(Collectors.toList());
      }

      button.item().appendLore(LoreScroller.scroller(this.playerList, 8, this.start));
      button.item().appendLore("", USERS_DESC.get().replace("%count%", Integer.toString(this.playerList.size())));
      if (this.skinTextures == null) {
         this.skinTextures = this.plugin
            .getUsers()
            .stream()
            .map(user -> user.getSkinTexture().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }

      if (!this.skinTextures.isEmpty()) {
         long value = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
         int index = (int)(value % this.skinTextures.size());
         button.item().setSkullTexture(this.skinTextures.get(index));
      }

      button.action(actionType -> new UserListView(this.p, this.plugin) {
         @Override
         public void onBack() {
            Overview.this.reopen();
         }
      });
   }

   private void GroupsButton(Button button) {
      button.material(XMaterial.BOOKSHELF).name(Animation.wave(GROUPS_TITLE.get(), ColorPalette.MAIN, Colors.WHITE)).lore(GROUPS_ACTION.get());
      button.item().appendLore("");
      if (this.groupList == null) {
         this.groupList = this.plugin
            .getGroups()
            .stream()
            .map(
               group -> GROUPS_SCROLLING_ENTRY.get()
                  .replace("%prefix%", group.getPrefix().orElse(""))
                  .replace("%suffix%", group.getSuffix().orElse(""))
                  .replace("%group%", group.getName())
                  .trim()
            )
            .collect(Collectors.toList());
      }

      button.item().appendLore(LoreScroller.scroller(this.groupList, 8, this.start));
      button.item().appendLore("");
      button.item().appendLore(GROUPS_DESC.get().replace("%count%", Integer.toString(this.groupList.size())));
      button.action(actionType -> new GroupListView(this.p, this.plugin) {
         @Override
         public void onBack() {
            Overview.this.reopen();
         }
      });
   }

   private void GroupPathsButton(Button button) {
      button.material(XMaterial.BOOK)
         .name(Animation.wave(GROUP_PATHS_BUTTON_NAME.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(
            GROUP_PATHS_BUTTON_ACTION.get(),
            "",
            GROUP_PATHS_BUTTON_COUNT.get().replace("%count%", Integer.toString(this.plugin.getGroupPaths().size())),
            "",
            GROUP_PATHS_BUTTON_DESC1.get(),
            GROUP_PATHS_BUTTON_DESC2.get(),
            GROUP_PATHS_BUTTON_DESC3.get(),
            GROUP_PATHS_BUTTON_DESC4.get()
         );
      button.action(actionType -> new GroupPathListView(this.p, this.plugin) {
         @Override
         public void onBack() {
            Overview.this.reopen();
         }
      });
   }

   private void PermissionLogButton(Button button) {
      button.material(XMaterial.WRITABLE_BOOK)
         .name(Animation.wave(PERMISSION_LOG_TITLE.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(PERMISSION_LOG_ACTION.get(), "", PERMISSION_LOG_DESC.get());
      button.action(actionType -> new PermissionLogViewer(this.p, this.plugin) {
         @Override
         public void onBack() {
            Overview.this.reopen();
         }
      });
   }

   private void InsaneEditorButton(Button button) {
      button.material(Bukkit.getPluginManager().getPlugin("InsaneEditor") != null ? XMaterial.NETHER_STAR : XMaterial.WHITE_STAINED_GLASS_PANE)
         .name(Animation.wave(INSANE_EDITOR_BUTTON_TITLE.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(
            Bukkit.getPluginManager().getPlugin("InsaneEditor") != null ? INSANE_EDITOR_BUTTON_ACTION_INSTALLED.get() : INSANE_EDITOR_BUTTON_ACTION.get(),
            "",
            INSANE_EDITOR_BUTTON_DESC.get()
         );
      button.action(actionType -> new InsaneEditorDownload(this.p, this.plugin) {});
   }

   private void SettingsButton(Button button) {
      button.material(XMaterial.COMMAND_BLOCK)
         .name(Animation.wave(SETTINGS_TITLE.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(SETTINGS_ACTION.get(), "", SETTINGS_DESC.get());
      button.action(actionType -> new UpermsSettingsView(this.p, this.plugin) {
         @Override
         public void onBack() {
            Overview.this.reopen();
         }
      });
   }

   private void ReduceButton(Button button) {
      button.material(XMaterial.TRIPWIRE_HOOK)
         .name(Animation.wave(REDUCE_TITLE.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(REDUCE_ACTION.get(), "", REDUCE_DESC.get());
      button.action(actionType -> this.expanded = false);
   }

   private void ExpandButton(Button button) {
      button.material(XMaterial.TRIPWIRE_HOOK)
         .name(Animation.wave(EXPAND_TITLE.get(), ColorPalette.MAIN, Colors.WHITE))
         .lore(EXPAND_ACTION.get(), "", EXPAND_DESC.get());
      button.action(actionType -> this.expanded = true);
   }
}
