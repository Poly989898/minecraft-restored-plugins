package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.TechsCode.UltraPermissions.PermissionColor;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.LoreLists;
import me.TechsCode.UltraPermissions.permissionDatabase.PermissionInfoList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class PermissionEditorMainView extends GUI {
   private final int[] layout = new int[]{11, 12, 13, 14, 15, 16, 17, 20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35};
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private final long opened;
   private final List<PermissionEditorMainView.PluginEntry> entries;
   private int page;
   private static final Phrase TITLE = Phrase.create("permissionEditorMainView.title", "%holder% > Permissions");
   private static final Phrase ENTRY_ACTION = Phrase.create("permissionEditorMainView.entry.action", "Click to view Permissions", Colors.GRAY);
   private static final Phrase ENTRY_PERMISSIONS_TITLE = Phrase.create("permissionEditorMainView.entry.permissionsTitle", "Added Permissions", Colors.GRAY);
   private static final Phrase ENTRY_FOOTER = Phrase.create(
      "permissionEditorMainView.entry.footer", "Found **%count% Permissions** for this plugin in the Permissions Database", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase NEXT_TITLE = Phrase.create("permissionEditorMainView.next.title", "Next");
   private static final Phrase NEXT_ACTION = Phrase.create("permissionEditorMainView.next.action", "Click to go to the next page", Colors.GRAY);
   private static final Phrase PREVIOUS_TITLE = Phrase.create("permissionEditorMainView.previous.title", "Previous");
   private static final Phrase PREVIOUS_ACTION = Phrase.create("permissionEditorMainView.previous.action", "Click to go to the previous page", Colors.GRAY);
   private static final Phrase ALL_PERMISSIONS_TITLE = Phrase.create("permissionEditorMainView.allPermissions.title", "All Permissions");
   private static final Phrase ALL_PERMISSIONS_ACTION = Phrase.create(
      "permissionEditorMainView.allPermissions.action", "Click to show all added Permissions", Colors.GRAY
   );
   private static final Phrase ADD_PERMISSION_TITLE = Phrase.create("permissionEditorMainView.addPermission.title", "Add Permission");
   private static final Phrase ADD_PERMISSION_ADD_ACTION = Phrase.create(
      "permissionEditorMainView.addPermission.addAction", "**Left Click** to add a Permission via Chat", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase ADD_PERMISSION_ADD_BUNGEE_ACTION = Phrase.create(
      "permissionEditorMainView.addPermission.addBungeeAction", "**Right Click** to add **Bungee Permission**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase PURGE_PERMISSION_TITLE = Phrase.create("permissionEditorMainView.purgePermission.title", "Purge specific Permission");
   private static final Phrase PURGE_PPERMISSION_ACTION = Phrase.create(
      "permissionEditorMainView.purgePlayerPermission.addAction",
      "**Left Click** to purge a Permission from all **Players**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase PURGE_GPERMISSION_ACTION = Phrase.create(
      "permissionEditorMainView.purgeGroupPermission.addAction",
      "**Right Click** to purge a Permission from all **Groups**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase SEARCH_TITLE = Phrase.create("permissionEditorMainView.search.title", "Search a Permission");
   private static final Phrase SEARCH_ACTION = Phrase.create("permissionEditorMainView.search.action", "Click to search for a Permission", Colors.GRAY);

   public PermissionEditorMainView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
      this.opened = System.currentTimeMillis();
      this.entries = Stream.concat(Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName), Arrays.stream(new String[]{"Bukkit"}))
         .sorted()
         .map(text -> new PermissionEditorMainView.PluginEntry(ultraPermissions, text))
         .filter(pluginEntry -> !pluginEntry.permissionsFromDatabase.isEmpty())
         .collect(Collectors.toList());
      this.page = 0;
   }

   @Override
   public void reopen() {
      super.reopen();
      this.entries.forEach(PermissionEditorMainView.PluginEntry::retrieveData);
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(TITLE.get().replace("%holder%", this.holder.getName()));
      model.setSlots(54);
      int index = this.page * this.layout.length;

      for (int count : this.layout) {
         PermissionEditorMainView.PluginEntry pluginEntry = this.entries.size() > index ? this.entries.get(index) : null;
         model.button(count, pluginEntry != null ? button -> this.PluginWithPermissionsButton(button, pluginEntry) : this::PlaceholderButton);
         index++;
      }

      if (this.page != 0) {
         model.button(10, this::PreviousButton);
      }

      if (this.entries.size() / this.layout.length > this.page) {
         model.button(18, this::NextButton);
      }

      model.button(47, this::SearchButton);
      model.button(50, button -> Common.BackButton(button, actionType -> this.onBack()));
      model.button(52, this::AddedPermissionButton);
      model.button(53, this::TypeInButton);
      model.button(54, this::PurgePermissionButton);
   }

   private void PluginWithPermissionsButton(Button button, PermissionEditorMainView.PluginEntry pluginEntry) {
      if (pluginEntry.permissionCopies.isEmpty()) {
         button.material(XMaterial.WHITE_STAINED_GLASS_PANE);
      } else if (pluginEntry.permissionCopies.size() == 1) {
         button.material(PermissionColor.fromPermission(pluginEntry.permissionCopies.get(0)).getGlassPane());
      } else {
         button.material(PermissionColor.cycle(pluginEntry.permissionCopies).getGlassBlock());
      }

      button.item()
         .amount(Math.max(1, pluginEntry.permissionCopies.size()))
         .name(Animation.wave(pluginEntry.name, PermissionColor.ALL_SERVERS.getColor(), Colors.WHITE))
         .lore(ENTRY_ACTION.get(), "");
      if (!pluginEntry.permissionCopies.isEmpty()) {
         button.item().appendLore(ENTRY_PERMISSIONS_TITLE.get());
      }

      button.item().appendLore(LoreLists.printPermissionCopiesList(pluginEntry.permissionCopies, this.opened));
      button.item().appendLore("");
      int size = pluginEntry.permissionsFromDatabase.size();
      button.item().appendLore(Arrays.stream(ENTRY_FOOTER.split(80)).map(text -> text.replace("%count%", Integer.toString(size))).toArray(String[]::new));
      button.action(actionType -> new PluginPermissionListView(this.p, this.plugin, this.holder, pluginEntry) {
         @Override
         public void onBack() {
            PermissionEditorMainView.this.reopen();
         }
      });
   }

   private void PlaceholderButton(Button button) {
      button.material(XMaterial.GRAY_STAINED_GLASS_PANE).name();
   }

   private void NextButton(Button button) {
      button.material(XMaterial.ARROW).name(Animation.wave(NEXT_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(NEXT_ACTION.get());
      button.action(actionType -> this.page++);
   }

   private void PreviousButton(Button button) {
      button.material(XMaterial.ARROW).name(Animation.wave(PREVIOUS_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(PREVIOUS_ACTION.get());
      button.action(actionType -> this.page--);
   }

   private void AddedPermissionButton(Button button) {
      button.material(XMaterial.PAPER).name(Animation.wave(ALL_PERMISSIONS_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ALL_PERMISSIONS_ACTION.get());
      button.item().amount(Math.min(64, this.holder.getPermissions().size() + this.holder.getAdditionalPermissions().size()));
      button.action(actionType -> new FullPermissionListView(this.p, this.plugin, this.holder) {
         @Override
         public void onBack() {
            PermissionEditorMainView.this.reopen();
         }
      });
   }

   private void TypeInButton(Button button) {
      button.material(XMaterial.NAME_TAG).name(Animation.wave(ADD_PERMISSION_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ADD_PERMISSION_ADD_ACTION.get());
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new AddPermissionDialog(this.p, this.plugin, this.holder) {
               @Override
               public void reopen() {
                  PermissionEditorMainView.this.reopen();
               }
            };
         }
      });
      if (this.plugin.getMySQLManager().isEnabled() && this.plugin.isConnectedToNetwork()) {
         button.item().appendLore(ADD_PERMISSION_ADD_BUNGEE_ACTION.get());
         button.action(actionType -> {
            if (actionType == ActionType.RIGHT) {
               new AddBungeePermissionDialog(this.p, this.plugin, this.holder) {
                  @Override
                  public void reopen() {
                     PermissionEditorMainView.this.reopen();
                  }
               };
            }
         });
      }
   }

   private void PurgePermissionButton(Button button) {
      button.material(XMaterial.BARRIER)
         .name(Animation.wave(PURGE_PERMISSION_TITLE.get(), Colors.RED, Colors.AliceBlue))
         .lore(PURGE_PPERMISSION_ACTION.get(), PURGE_GPERMISSION_ACTION.get());
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new PurgePlayerPermissionDialog(this.p, this.plugin, this.holder) {
               @Override
               public void reopen() {
                  PermissionEditorMainView.this.reopen();
               }
            };
         }

         if (actionType == ActionType.RIGHT) {
            new PurgeGroupPermissionDialog(this.p, this.plugin, this.holder) {
               @Override
               public void reopen() {
                  PermissionEditorMainView.this.reopen();
               }
            };
         }
      });
   }

   private void SearchButton(Button button) {
      button.material(XMaterial.COMPASS).name(Animation.wave(SEARCH_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(SEARCH_ACTION.get());
      button.action(actionType -> new SearchPermissionDialog(this.p, this.plugin, this.holder) {
         @Override
         public void reopen() {
            PermissionEditorMainView.this.reopen();
         }
      });
   }

   public abstract void onBack();

   class PluginEntry {
      public final String name;
      public PermissionInfoList permissionsFromDatabase;
      public PermissionCopyList permissionCopies;

      public PluginEntry(UltraPermissions ultraPermissions, String text) {
         this.name = text;
         this.retrieveData();
      }

      public void retrieveData() {
         PermissionList permissions = PermissionEditorMainView.this.holder.getPermissions();
         PermissionList additionalPermissions = PermissionEditorMainView.this.holder.getAdditionalPermissions();
         this.permissionsFromDatabase = PermissionEditorMainView.this.plugin.getPermissionDatabase().getPermissionInfosFromPlugin(this.name);
         this.permissionCopies = new PermissionCopyList(permissions, additionalPermissions)
            .stream()
            .filter(permissionCopy -> this.permissionsFromDatabase.find(permissionCopy.getPermission().getName()).isPresent())
            .collect(Collectors.toCollection(PermissionCopyList::new));
      }
   }
}
