package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.views.TimePickerView;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GrantGroupsAddView extends PageableGUI<Group> {
   private final UltraPermissions plugin;
   private final Group group;
   private static final Phrase TITLE = Phrase.create("GrantGroupsAddView.title", "%group% > Add Group");
   private static final Phrase ENTRY_ACTION = Phrase.create(
      "GrantGroupsAddView.entry.action", "Click to **add** to **%group%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );
   private static final Phrase ENTRY_ACTION_TIME_TITLE_PREFIX = Phrase.create("GrantGroupsAddView.entry.action.time.titlePrefix", "Duration >", Colors.GRAY);
   private static final Phrase ENTRY_ACTION_TIME_NO_TIME = Phrase.create("GrantGroupsAddView.entry.action.time.noTimePhrase", "Permanent", Colors.GRAY);

   public GrantGroupsAddView(Player player, UltraPermissions ultraPermissions, Group group) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.group = group;
   }

   public Group[] getObjects() {
      return this.plugin.getGroups().stream().filter(group -> !this.group.getGrantGroups().keySet().contains(group.toStored())).toArray(Group[]::new);
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%group%", this.group.getName());
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.Green, Colors.WHITE))
         .lore(ENTRY_ACTION.get().replace("%group%", this.group.getName()))
         .setDamage(group.getDamage());
      button.action(actionType -> new TimePickerView(this.p, this.plugin, ENTRY_ACTION_TIME_TITLE_PREFIX.get(), ENTRY_ACTION_TIME_NO_TIME.get(), true, 0L) {
         @Override
         public void onResult(long value) {
            if (value == 0L) {
               GrantGroupsAddView.this.group.addGrantGroup(group);
            } else {
               GrantGroupsAddView.this.group.addGrantGroup(group, value);
            }

            GrantGroupsAddView.this.onBack();
         }
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
