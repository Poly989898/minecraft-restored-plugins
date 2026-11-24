package me.TechsCode.UltraPermissions.gui;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.ColorPalette;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.views.ConfirmationView;
import me.TechsCode.UltraPermissions.base.views.MaterialPickerView;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.base.visual.LoreScroller;
import me.TechsCode.UltraPermissions.gui.dialogs.CreateGroupPathDialog;
import me.TechsCode.UltraPermissions.gui.dialogs.RenameGroupPathDialog;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GroupPathListView extends PageableGUI<GroupPath> {
   private static final Phrase TITLE = Phrase.create("groupPathListView.titleAllGroups", "Group Paths");
   private final Player p;
   private final UltraPermissions plugin;
   private long start;
   private static final Phrase BUTTON_LORE_LEFT_CLICK = Phrase.create(
      "groupPathListView.construct.lore.leftClick", "**Left Click** to edit group path", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase BUTTON_LORE_RIGHT_CLICK = Phrase.create(
      "groupPathListView.construct.lore.rightClick", "**Right Click** to change name", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase BUTTON_LORE_MIDDLE_CLICK = Phrase.create(
      "groupPathListView.construct.lore.middleClick", "**Middle Click** to change description", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase BUTTON_LORE_SHIFT_LEFT_CLICK = Phrase.create(
      "groupPathListView.construct.lore.shiftLeftClick", "**Shift + Left Click** to change icon", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase BUTTON_LORE_SHIFT_RIGHT_CLICK = Phrase.create(
      "groupPathListView.construct.lore.shiftRightClick", "**Shift + Right Click** to %action% group path", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase BUTTON_LORE_PRESS_Q = Phrase.create(
      "groupPathListView.construct.lore.pressQ", "**Press Q** to **delete**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase BUTTON_ACTION_DELETION_TITLE = Phrase.create(
      "groupPathListView.construct.action.deletion.title", "deletion of group path '%pathName%'"
   );
   private static final Phrase BUTTON_LORE_DESCRIPTION = Phrase.create("groupPathListView.construct.lore.description", "Description", Colors.GRAY);
   private static final Phrase BUTTON_ACTION_MATERIAL_PICKER_TITLE = Phrase.create(
      "groupPathListView.construct.action.materialPicker.title", "%groupPathName% > Icon"
   );
   private static final Phrase BUTTON_LORE_GROUPS_INSIDE_PATH = Phrase.create(
      "groupPathListView.construct.lore.groupsInsidePath", "Groups inside Path", Colors.GRAY
   );
   private static final Phrase BUTTON_ACTION_DESCRIPTION_TITLE_MAIN = Phrase.create(
      "groupPathListView.construct.action.description.title.main", "Group Path Description", Colors.AQUA
   );
   private static final Phrase BUTTON_ACTION_DESCRIPTION_TITLE_SUB = Phrase.create(
      "groupPathListView.construct.action.description.title.sub", "Enter a description for the group path", Colors.GRAY
   );
   private static final Phrase BUTTON_ACTION_DESCRIPTION_TITLE_ACTION_BAR = Phrase.create(
      "groupPathListView.construct.action.description.title.actionBar", "Type in **none** to reset the description", Colors.GRAY, Colors.RED
   );
   private static final Phrase BUTTON_LORE_STATUS = Phrase.create("groupPathListView.construct.lore.status", "Status: %status%", Colors.GRAY);
   private static final Phrase ADD_TITLE = Phrase.create("groupPathListView.add.title", "Create Group Path");
   private static final Phrase ADD_ACTION = Phrase.create("groupPathListView.add.action", "Click to create group path", Colors.GRAY);
   private static final Phrase REORDER_TITLE = Phrase.create("groupPathListView.reorder.title", "Change Order");
   private static final Phrase REORDER_ACTION = Phrase.create("groupPathListView.reorder.action", "Click to modify the order", Colors.GRAY);

   public GroupPathListView(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.p = player;
      this.plugin = ultraPermissions;
      this.start = System.currentTimeMillis();
   }

   @Override
   public String getTitle() {
      return TITLE.get();
   }

   public void construct(Button button, GroupPath path) {
      ItemStack icon = path.getIcon();
      List<String> texts = path.getGroupPath().stream().map(group -> group.getName()).collect(Collectors.toList());
      button.material(icon.getType()).setDamage(icon.getDurability());
      if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_13_R1) && icon.hasItemMeta() && icon.getItemMeta().hasCustomModelData()) {
         button.material(icon.getType(), icon.getItemMeta().getCustomModelData()).setDamage(icon.getDurability());
      }

      button.item()
         .name(Animation.wave(path.getName(), ColorPalette.MAIN, Colors.WHITE))
         .lore(
            BUTTON_LORE_LEFT_CLICK.get(),
            BUTTON_LORE_RIGHT_CLICK.get(),
            BUTTON_LORE_MIDDLE_CLICK.get(),
            BUTTON_LORE_SHIFT_LEFT_CLICK.get(),
            BUTTON_LORE_SHIFT_RIGHT_CLICK.get()
               .replace("%action%", path.isEnabled() ? "\u00a7c" + Words.DISABLE.get() + "\u00a77" : "\u00a7a" + Words.ENABLE.get() + "\u00a77"),
            BUTTON_LORE_PRESS_Q.get(),
            "",
            BUTTON_LORE_STATUS.get().replace("%status%", path.isEnabled() ? "\u00a7a" + Words.ENABLED.get() : "\u00a7c" + Words.DISABLED.get()),
            "",
            BUTTON_LORE_GROUPS_INSIDE_PATH.get() + "\u00a77:"
         );
      if (!path.getGroupPath().isEmpty()) {
         for (String text : LoreScroller.scroller(texts, 3, this.start)) {
            button.item().appendLore("\u00a77- \u00a7e" + text);
         }
      } else {
         button.item().appendLore("\u00a77- \u00a7c" + Words.NONE.get());
      }

      if (path.getDescription().length() > 0) {
         button.item().appendLore("", BUTTON_LORE_DESCRIPTION.get() + "\u00a77:", "\u00a7f" + path.getDescription());
      }

      button.action(
         actionType -> {
            if (actionType == ActionType.LEFT) {
               new GroupPathEditView(this.p, this.plugin, path) {
                  @Override
                  public void onBack() {
                     GroupPathListView.this.reopen();
                  }
               };
            }

            if (actionType == ActionType.RIGHT) {
               new RenameGroupPathDialog(this.p, this.plugin, path) {
                  @Override
                  public void reopen() {
                     GroupPathListView.this.reopen();
                  }
               };
            }

            if (actionType == ActionType.MIDDLE) {
               new UserInput(
                  this.p,
                  this.plugin,
                  BUTTON_ACTION_DESCRIPTION_TITLE_MAIN.get(),
                  BUTTON_ACTION_DESCRIPTION_TITLE_SUB.get(),
                  BUTTON_ACTION_DESCRIPTION_TITLE_ACTION_BAR.get()
               ) {
                  @Override
                  public void onClose(Player closingPlayer) {
                     GroupPathListView.this.reopen();
                  }

                  @Override
                  public boolean onResult(String text) {
                     if (text.equalsIgnoreCase("none")) {
                        path.setDescription("");
                     } else {
                        path.setDescription(text);
                     }

                     GroupPathListView.this.reopen();
                     return true;
                  }
               };
            }

            if (actionType == ActionType.SHIFT_LEFT) {
               new MaterialPickerView(this.p, this.plugin, BUTTON_ACTION_MATERIAL_PICKER_TITLE.get().replace("%groupPathName%", path.getName())) {
                  @Override
                  public void onBack() {
                     GroupPathListView.this.reopen();
                  }

                  @Override
                  public void choose(Player choosingPlayer, XMaterial xMaterial) {
                     path.setIcon(xMaterial.parseItem());
                     GroupPathListView.this.reopen();
                  }
               };
            }

            if (actionType == ActionType.SHIFT_RIGHT) {
               path.setEnabled(!path.isEnabled());
            }

            if (actionType == ActionType.Q) {
               new ConfirmationView(this.p, this.plugin, BUTTON_ACTION_DELETION_TITLE.get().replace("%pathName%", path.getName())) {
                  @Override
                  public void choose(boolean flag) {
                     if (flag) {
                        path.delete();
                     }

                     GroupPathListView.this.reopen();
                  }
               };
            }
         }
      );
   }

   private void SelectorLore(Button button, String selected, Collection<String> texts) {
      button.item().appendLore(texts.stream().map(entry -> entry.equals(selected) ? "\u00a7a> \u00a77" + entry : "\u00a77  " + entry).toArray(String[]::new));
   }

   private <X> X next(X value, List<X> list) {
      int count = list.indexOf(value);
      return (X)list.get(count + 1 == list.size() ? 0 : count + 1);
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(52, this::ReorderButton);
      model.button(53, this::AddButton);
   }

   private void AddButton(Button button) {
      button.material(XMaterial.ANVIL).name(Animation.fading(ADD_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ADD_ACTION.get());
      button.action(actionType -> new CreateGroupPathDialog(this.p, this.plugin) {
         @Override
         public void reopen() {
            GroupPathListView.this.reopen();
         }
      });
   }

   public void ReorderButton(Button button) {
      button.material(XMaterial.ITEM_FRAME).name(Animation.fading(REORDER_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(REORDER_ACTION.get());
      button.action(actionType -> new GroupPathReorderView(this.p, this.plugin) {
         @Override
         public void onBack() {
            GroupPathListView.this.reopen();
         }
      });
   }

   public GroupPath[] getObjects() {
      return this.plugin.getGroupPaths().stream().sorted(Comparator.comparingInt(GroupPath::getPriority)).toArray(GroupPath[]::new);
   }

   @Override
   public SearchFeature<GroupPath> getSearch() {
      return new BasicSearch<GroupPath>() {
         public String[] getSearchableText(GroupPath path) {
            return new String[]{path.getName()};
         }
      };
   }
}
