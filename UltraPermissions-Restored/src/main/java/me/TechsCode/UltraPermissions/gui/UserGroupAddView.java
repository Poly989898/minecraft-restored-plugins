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
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public abstract class UserGroupAddView extends PageableGUI<Group> {
   private final UltraPermissions plugin;
   private final User user;
   private static final Phrase TITLE = Phrase.create("userGroupAddView.title", "%user% > Add Group");
   private static final Phrase ENTRY_ACTION = Phrase.create(
      "userGroupAddView.entry.action", "Click to **add** to **%user%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );

   public UserGroupAddView(Player player, UltraPermissions ultraPermissions, User user) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.user = user;
   }

   public Group[] getObjects() {
      return this.plugin.getGroups().stream().filter(group -> !this.user.getGroups().contains(group.toStored())).toArray(Group[]::new);
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%user%", this.user.getName());
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.Green, Colors.WHITE))
         .lore(ENTRY_ACTION.get().replace("%user%", this.user.getName()))
         .setDamage(group.getDamage());
      button.action(actionType -> new TimePickerView(this.p, this.plugin, "Duration >", "Permanent", true, 0L) {
         @Override
         public void onResult(long seconds) {
            if (seconds == 0L) {
               UserGroupAddView.this.user.addGroup(group);
            } else {
               long expiry = System.currentTimeMillis() + seconds * 1000L;
               UserGroupAddView.this.user.addGroup(group, expiry);
            }

            UserGroupAddView.this.onBack();
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
