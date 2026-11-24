package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import org.bukkit.entity.Player;

public abstract class GroupPathAddGroupView extends PageableGUI<Group> {
   private final UltraPermissions plugin;
   private final GroupPath groupPath;
   private static final Phrase TITLE = Phrase.create("groupPathAddGroupView.title", "%groupPath% > Add Group to Path");
   private static final Phrase ENTRY_ACTION = Phrase.create(
      "groupPathAddGroupView.entry.action", "Click to **add** to **%groupPath%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );

   public GroupPathAddGroupView(Player player, UltraPermissions ultraPermissions, GroupPath groupPath) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.groupPath = groupPath;
   }

   public Group[] getObjects() {
      return this.plugin
         .getGroups()
         .stream()
         .filter(group -> this.plugin.getGroupPaths().stream().noneMatch(groupPath -> groupPath.getGroupPath().contains(group)))
         .toArray(Group[]::new);
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%groupPath%", this.groupPath.getName());
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.Green, Colors.WHITE))
         .lore(ENTRY_ACTION.get().replace("%groupPath%", this.groupPath.getName()))
         .setDamage(group.getDamage());
      button.action(actionType -> {
         this.groupPath.addGroupToGroupPath(group);
         this.onBack();
      });
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
