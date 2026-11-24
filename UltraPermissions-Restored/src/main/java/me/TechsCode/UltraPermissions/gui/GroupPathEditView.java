package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import org.bukkit.entity.Player;

public abstract class GroupPathEditView extends PageableGUI<Group> {
   private static final Phrase TITLE = Phrase.create("groupPathEditView.titleAllGroups", "%groupPathName% > Path");
   private final Player p;
   private final UltraPermissions plugin;
   private GroupPath groupPath;
   private static final Phrase BUTTON_LORE_PRESS_Q = Phrase.create(
      "groupPathEditView.construct.lore.pressQ", "**Press Q** to **delete**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase ADD_TITLE = Phrase.create("groupPathEditView.add.title", "Add Group");
   private static final Phrase ADD_ACTION = Phrase.create("groupPathEditView.add.action", "Click to add group to path", Colors.GRAY);
   private static final Phrase REORDER_TITLE = Phrase.create("groupPathEditView.reorder.title", "Change Order");
   private static final Phrase REORDER_ACTION = Phrase.create("groupPathEditView.reorder.action", "Click to modify the order", Colors.GRAY);

   public GroupPathEditView(Player player, UltraPermissions plugin, GroupPath groupPath) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.groupPath = groupPath;
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%groupPathName%", this.groupPath.getName());
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.GOLD, Colors.YELLOW, Colors.WHITE))
         .setDamage(group.getDamage())
         .lore(BUTTON_LORE_PRESS_Q.get());
      button.action(actionType -> {
         if (actionType == ActionType.Q) {
            this.groupPath.removeGroupFromGroupPath(group);
         }
      });
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(52, this::ReorderButton);
      model.button(53, this::AddButton);
   }

   private void AddButton(Button button) {
      button.material(XMaterial.ANVIL).name(Animation.fading(ADD_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ADD_ACTION.get());
      button.action(actionType -> new GroupPathAddGroupView(this.p, this.plugin, this.groupPath) {
         @Override
         public void onBack() {
            GroupPathEditView.this.reopen();
         }
      });
   }

   public void ReorderButton(Button button) {
      button.material(XMaterial.ITEM_FRAME).name(Animation.fading(REORDER_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(REORDER_ACTION.get());
      button.action(actionType -> new GroupPathGroupsReorderView(this.p, this.plugin, this.groupPath) {
         @Override
         public void onBack() {
            GroupPathEditView.this.reopen();
         }
      });
   }

   public Group[] getObjects() {
      return this.groupPath.getGroupPath().toArray(new Group[0]);
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
