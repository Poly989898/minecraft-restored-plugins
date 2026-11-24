package me.TechsCode.UltraPermissions.gui;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.dialogs.CreateGroupDialog;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GroupListView extends PageableGUI<Group> {
   private static final Phrase TITLE = Phrase.create("groupListView.titleAllGroups", "Groups > Showing %count% Groups");
   private final Player p;
   private final UltraPermissions plugin;
   private boolean showAll;
   private int showing = 0;
   private static final Phrase GROUP_OPEN_ACTION = Phrase.create("groupListView.openAction", "**Left Click** to open", Colors.GRAY, Colors.AQUA);
   private static final Phrase GROUP_STORAGE_SETTINGS_ACTION = Phrase.create(
      "groupListView.storageSettingsAction", "**Press Q** to view Storage Settings", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase GROUP_STORAGE_DEFAULT_INDICATOR = Phrase.create("groupListView.defaultIndicator", "This is a default group", Colors.GREEN);
   private static final Phrase GROUP_ATTRIBUTE_SERVER = Phrase.create("groupListview.attributes.server", "Server: **%server%**", Colors.GRAY, Colors.GOLD);
   private static final Phrase GROUP_ATTRIBUTE_WORLD = Phrase.create("groupListview.attributes.world", "World: **%world%**", Colors.GRAY, Colors.GOLD);
   private static final Phrase GROUP_ATTRIBUTE_PREFIX = Phrase.create("groupListview.attributes.prefix", "Prefix: **%prefix%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase GROUP_ATTRIBUTE_SUFFIX = Phrase.create("groupListview.attributes.suffix", "Suffix: **%suffix%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase GROUP_INHERITS_TITLE = Phrase.create("groupListview.inherits.title", "Inherited Groups:", Colors.GRAY);
   private static final Phrase GROUP_INHERITS_ENTRY = Phrase.create("groupListview.inherits.entry", "- **%group%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase GROUP_INHERITS_NONE = Phrase.create("groupListview.inherits.none", "- **none**", Colors.GRAY, Colors.RED);
   private static final Phrase GROUP_PERMISSION_NODES_COUNT = Phrase.create(
      "groupListview.permissions.count", "**%count%** Permission Nodes", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase ADD_TITLE = Phrase.create("groupListView.add.title", "Create Group");
   private static final Phrase ADD_ACTION = Phrase.create("groupListView.add.action", "Click to create group", Colors.GRAY);
   private static final Phrase REORDER_TITLE = Phrase.create("groupListView.reorder.title", "Change Order");
   private static final Phrase REORDER_ACTION = Phrase.create("groupListView.reorder.action", "Click to modify the order", Colors.GRAY);
   private static final Phrase SHOW_ALL_TITLE = Phrase.create("groupListView.showAll.title", "Show all");
   private static final Phrase SHOW_ALL_ACTION = Phrase.create(
      "groupListView.showAll.action", "Click to show all groups from all Servers & Worlds", Colors.GRAY
   );

   public GroupListView(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.p = player;
      this.plugin = ultraPermissions;
      this.showAll = false;
      int length = this.getObjects().length;
      this.showAll = true;
      int lengthWithAll = this.getObjects().length;
      if (length != lengthWithAll) {
         this.showAll = false;
      }
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%count%", Integer.toString(this.showing));
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.GOLD, Colors.YELLOW, Colors.WHITE))
         .setDamage(group.getDamage());
      button.item().appendLore(GROUP_OPEN_ACTION.get()).appendLore(GROUP_STORAGE_SETTINGS_ACTION.get());
      boolean isDefault = group.isDefault();
      Optional<String> world = group.getWorld();
      Optional<String> server = group.getServer();
      if (isDefault || world.isPresent() || server.isPresent()) {
         button.item().appendLore("");
      }

      if (group.isDefault()) {
         button.item().appendLore(GROUP_STORAGE_DEFAULT_INDICATOR.get());
      }

      server.ifPresent(text -> button.item().appendLore(GROUP_ATTRIBUTE_SERVER.get().replace("%server%", text)));
      world.ifPresent(text -> button.item().appendLore(GROUP_ATTRIBUTE_WORLD.get().replace("%world%", text)));
      String text = group.getPrefix().orElse(Colors.RED + Words.NONE.get());
      String suffix = group.getSuffix().orElse(Colors.RED + Words.NONE.get());
      button.item().appendLore("", GROUP_ATTRIBUTE_PREFIX.get().replace("%prefix%", text), GROUP_ATTRIBUTE_SUFFIX.get().replace("%suffix%", suffix), "");
      button.item().appendLore(GROUP_INHERITS_TITLE.get());
      GroupList list = group.getActiveInheritedGroups().bestToWorst();
      list.forEach(inheritedGroup -> button.item().appendLore(GROUP_INHERITS_ENTRY.get().replace("%group%", inheritedGroup.getName())));
      if (list.isEmpty()) {
         button.item().appendLore(GROUP_INHERITS_NONE.get());
      }

      int size = group.getPermissions().size();
      button.item().appendLore("", GROUP_PERMISSION_NODES_COUNT.get().replace("%count%", Integer.toString(size)));
      button.action(actionType -> {
         if (actionType == ActionType.Q) {
            new GroupStorageSettingsView(this.p, this.plugin, group) {
               @Override
               public void onBack() {
                  GroupListView.this.reopen();
               }
            };
         } else {
            new GroupView(this.p, this.plugin, group) {
               @Override
               public void onBack() {
                  GroupListView.this.reopen();
               }
            };
         }
      });
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      if (!this.showAll) {
         model.button(47, this::ShowAllButton);
      }

      model.button(52, this::ReorderButton);
      model.button(53, this::AddButton);
   }

   @Override
   protected int getLeftOptionSlot() {
      return this.showAll ? 48 : super.getLeftOptionSlot();
   }

   private void AddButton(Button button) {
      button.material(XMaterial.ANVIL).name(Animation.fading(ADD_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ADD_ACTION.get());
      button.action(actionType -> new CreateGroupDialog(this.p, this.plugin) {
         @Override
         public void reopen() {
            GroupListView.this.reopen();
         }
      });
   }

   public void ReorderButton(Button button) {
      button.material(XMaterial.ITEM_FRAME).name(Animation.fading(REORDER_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(REORDER_ACTION.get());
      button.action(actionType -> new GroupReorderView(this.p, this.plugin) {
         @Override
         public void onBack() {
            GroupListView.this.reopen();
         }
      });
   }

   public void ShowAllButton(Button button) {
      button.material(XMaterial.ENDER_EYE).name(Animation.fading(SHOW_ALL_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(SHOW_ALL_ACTION.split(50));
      button.action(actionType -> this.showAll = true);
   }

   public Group[] getObjects() {
      GroupList groups = this.plugin.getGroups();
      if (!this.showAll) {
         groups = groups.servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null)).worlds(true, this.p.getWorld().getName());
      }

      this.showing = groups.size();
      return groups.toArray(new Group[0]);
   }

   @Override
   public SearchFeature<Group> getSearch() {
      return new BasicSearch<Group>() {
         public String[] getSearchableText(Group group) {
            return new String[]{group.getName()};
         }
      };
   }
}
