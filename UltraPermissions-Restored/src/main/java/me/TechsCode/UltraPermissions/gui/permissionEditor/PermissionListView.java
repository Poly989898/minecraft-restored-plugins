package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.PermissionColor;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.LoreLists;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class PermissionListView extends PageableGUI<PermissionWithInfo> {
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private final long opened;
   private static final Phrase NOT_ADDED_ACTION = Phrase.create(
      "permissionListView.notAdded.action", "Click to **Quick Add** permission", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase NOT_ADDED_PLACEHOLDERS_TITLE = Phrase.create("permissionListView.placeholders.title", "Please note that", Colors.GRAY);
   private static final Phrase NOT_ADDED_PLACEHOLDERS_ENTRY = Phrase.create(
      "permissionListView.placeholders.entry", "- **[%placeholder%]** is a placeholder", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase LORE_ALL_SERVERS = Phrase.create(
      "permissionListView.lore.allServersAttribute", "**\u2022** Permission available on all Servers", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase LORE_SPECIFIC_SERVER = Phrase.create(
      "permissionListView.lore.specificServerAttribute", "**\u2022** Only applying to Server '**%server%**'", Colors.GRAY, Colors.WHITE, Colors.WHITE
   );
   private static final Phrase LORE_ALL_WORLDS = Phrase.create(
      "permissionListView.lore.allWorldsAttribute", "**\u2022** Permission applies to every World", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase LORE_SPECIFIC_WORLD = Phrase.create(
      "permissionListView.lore.specificWorldAttribute", "**\u2022** Exclusive to the World '**%world%**'", Colors.GRAY, Colors.WHITE, Colors.WHITE
   );
   private static final Phrase LORE_TEMPORARY = Phrase.create(
      "permissionListView.lore.temporaryAttribute", "**\u2022** Will expire in **%time%**", Colors.GRAY, Colors.WHITE, Colors.RED
   );
   private static final Phrase SINGLE_EDIT_ACTION = Phrase.create(
      "permissionListView.single.editAction", "**Left Click** to edit this Permission", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase SINGLE_DELETE_ACTION = Phrase.create(
      "permissionListView.single.deleteAction", "**Right Click** to **delete**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase SINGLE_SETTINGS_TITLE = Phrase.create("permissionListView.single.settingsTitle", "Settings:", Colors.GRAY);
   private static final Phrase MULTI_TITLE = Phrase.create("permissionListView.title", "%permission% (See below)");
   private static final Phrase MULTI_ACTION = Phrase.create("permissionListView.action", "Click to view all Permission Copies", Colors.GRAY);
   private static final Phrase INHERITED_VIEW_ACTION = Phrase.create(
      "permissionListView.inherited.viewAction", "**Left Click** to view this Permission", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase INHERITED_NEGATE_ACTION = Phrase.create(
      "permissionListView.inherited.negateAction", "**Right Click** to **Quick-Negate** this permission", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase INHERITED_SOURCE_DESC = Phrase.create(
      "permissionListView.inherited.sourceDesc", "This Permission originates from **%group%**", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase INHERITED_SETTINGS_TITLE = Phrase.create("permissionListView.inherited.settingsTitle", "Settings:", Colors.GRAY);

   public PermissionListView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
      this.opened = System.currentTimeMillis();
   }

   @Override
   public SearchFeature<PermissionWithInfo> getSearch() {
      return new BasicSearch<PermissionWithInfo>() {
         public String[] getSearchableText(PermissionWithInfo permissionWithInfo) {
            return new String[]{permissionWithInfo.getName(), permissionWithInfo.hasInfo() ? permissionWithInfo.getInfo().getDescription() : "xxx"};
         }
      };
   }

   public void construct(Button button, PermissionWithInfo permissionWithInfo) {
      PermissionCopyList list = new PermissionCopyList(
         this.holder.getPermissions().name(permissionWithInfo.getName()), this.holder.getAdditionalPermissions().name(permissionWithInfo.getName())
      );
      if (list.size() == 0) {
         this.NotAddedPermission(button, permissionWithInfo);
      } else if (list.size() == 1) {
         if (list.inheritedCopies().size() == 1) {
            this.InheritedOnlyButton(button, permissionWithInfo, list.get(0));
         } else {
            this.SingleAddedPermissionButton(button, permissionWithInfo, list.get(0));
         }
      } else {
         this.MultiAddedPermissionButton(button, permissionWithInfo, list);
      }

      if (permissionWithInfo.hasInfo()) {
         button.item().appendLore("");
         button.item().appendLore(permissionWithInfo.getInfo().asLore());
      }
   }

   private void NotAddedPermission(Button button, PermissionWithInfo permissionWithInfo) {
      button.material(XMaterial.WHITE_STAINED_GLASS_PANE).name(Animation.wave(permissionWithInfo.getName(), Colors.WHITE, Colors.GRAY)).lore(NOT_ADDED_ACTION.get());
      if (permissionWithInfo.hasInfo() && permissionWithInfo.getInfo().hasPlaceholders()) {
         button.item().appendLore("", NOT_ADDED_PLACEHOLDERS_TITLE.get());

         for (String placeholder : permissionWithInfo.getInfo().getPlaceholders()) {
            button.item().appendLore(NOT_ADDED_PLACEHOLDERS_ENTRY.get().replace("%placeholder%", placeholder));
         }
      }

      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new PlaceholderFillDialog(this.p, this.plugin, permissionWithInfo.getName()) {
               @Override
               public void onBack() {
                  PermissionListView.this.reopen();
               }

               @Override
               public void onComplete(String value) {
                  PermissionListView.this.holder.newPermission(value).create();
                  PermissionListView.this.reopen();
               }
            };
         }
      });
   }

   private List<String> permissionSettingsLore(PermissionCopy permissionCopy) {
      ArrayList list = new ArrayList();
      Optional server = permissionCopy.getPermission().getServer();
      if (server.isPresent()) {
         list.add(LORE_SPECIFIC_SERVER.get().replace("%server%", permissionCopy.getColor().getColor() + (String)server.get()));
      } else {
         list.add(LORE_ALL_SERVERS.get());
      }

      if (permissionCopy.getPermission().getWorld().isPresent()) {
         list.add(LORE_SPECIFIC_WORLD.get().replace("%world%", permissionCopy.getPermission().getWorld().get()));
      } else {
         list.add(LORE_ALL_WORLDS.get());
      }

      if (permissionCopy.getPermission().getExpiration() != 0L) {
         long remaining = permissionCopy.getPermission().getExpiration() - System.currentTimeMillis();
         String timeString = Tools.getTimeString(remaining, TimeUnit.MILLISECONDS, 2);
         list.add(LORE_TEMPORARY.get().replace("%time%", timeString));
      }

      return list;
   }

   private void SingleAddedPermissionButton(Button button, PermissionWithInfo selectedInfo, PermissionCopy permissionCopy) {
      PermissionColor permissionColor = PermissionColor.fromPermission(permissionCopy);
      button.material(permissionColor.getGlassPane())
         .name((permissionCopy.getPermission().isPositive() ? "" : Colors.RED + "-") + Animation.wave(selectedInfo.getName(), permissionColor.getColor(), Colors.WHITE))
         .lore(SINGLE_EDIT_ACTION.get(), SINGLE_DELETE_ACTION.get(), "");
      button.item().appendLore(SINGLE_SETTINGS_TITLE.get());
      button.item().appendLore(this.permissionSettingsLore(permissionCopy));
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new PermissionView(this.p, this.plugin, this.holder, selectedInfo) {
               @Override
               public void onBack() {
                  PermissionListView.this.reopen();
               }
            };
         }

         if (actionType == ActionType.RIGHT) {
            permissionCopy.getPermission().remove();
            this.reopen();
         }
      });
   }

   private void MultiAddedPermissionButton(Button button, PermissionWithInfo selectedInfo, PermissionCopyList list) {
      button.material(PermissionColor.cycle(list).getGlassBlock())
         .name(Animation.wave(MULTI_TITLE.get().replace("%permission%", selectedInfo.getName()), Colors.GRAY, Colors.WHITE))
         .lore(MULTI_ACTION.get(), "")
         .amount(list.size());
      button.item().appendLore(LoreLists.printPermissionCopiesList(list, this.opened));
      button.action(actionType -> new PermissionView(this.p, this.plugin, this.holder, selectedInfo) {
         @Override
         public void onBack() {
            PermissionListView.this.reopen();
         }
      });
   }

   private void InheritedOnlyButton(Button button, PermissionWithInfo selectedInfo, PermissionCopy permissionCopy) {
      button.material(PermissionColor.INHERITED.getGlassPane())
         .name(Animation.wave(selectedInfo.getName(), Colors.YELLOW, Colors.WHITE))
         .lore(INHERITED_VIEW_ACTION.get());
      button.action(actionType -> new PermissionView(this.p, this.plugin, this.holder, selectedInfo) {
         @Override
         public void onBack() {
            PermissionListView.this.reopen();
         }
      });
      if (permissionCopy.getPermission().isPositive()) {
         button.item().appendLore(INHERITED_NEGATE_ACTION.get());
         button.action(
            actionType -> {
               if (actionType == ActionType.RIGHT) {
                  this.holder
                     .newPermission(selectedInfo.getName())
                     .setPositive(false)
                     .setServer(permissionCopy.getPermission().getServer().orElse(null))
                     .setWorld(permissionCopy.getPermission().getWorld().orElse(null))
                     .create();
                  this.reopen();
               }
            }
         );
      }

      button.item()
         .appendLore("", INHERITED_SOURCE_DESC.get().replace("%group%", permissionCopy.getPermission().getHolder().getName()), "", INHERITED_SETTINGS_TITLE.get());
      button.item().appendLore(this.permissionSettingsLore(permissionCopy));
   }
}
