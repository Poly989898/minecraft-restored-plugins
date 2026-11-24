package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;

public abstract class PlayerSelectorView extends PageableGUI<User> {
   private final Player p;
   private final Group group;
   private final UltraPermissions plugin;
   private static final Phrase GUI_TITLE = Phrase.create("PlayerSelectorView.title", "Users > Select Player");
   private static final Phrase GUI_LORE = Phrase.create("PlayerSelectorView.lore", "Click to **select** player", Colors.GRAY, Colors.GREEN);

   public PlayerSelectorView(Player player, Group group, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.p = player;
      this.group = group;
      this.plugin = ultraPermissions;
   }

   public abstract void select(User user);

   @Override
   public String getTitle() {
      return GUI_TITLE.get();
   }

   @Override
   public SearchFeature<User> getSearch() {
      return new BasicSearch<User>() {
         public String[] getSearchableText(User user) {
            return new String[]{user.getName()};
         }
      };
   }

   public User[] getObjects() {
      return this.plugin.getUsers().stream().filter(user -> !user.getActiveGroups().contains(this.group)).toArray(User[]::new);
   }

   public void construct(Button button, User user) {
      if (user.getSkinTexture().isPresent()) {
         button.material(XMaterial.PLAYER_HEAD).setSkullTexture(user.getSkinTexture().get());
      } else {
         button.material(XMaterial.GRAY_STAINED_GLASS_PANE);
      }

      button.item().name(Animation.fading(user.getName(), Colors.DarkCyan, Colors.Cyan)).appendLore(GUI_LORE.get());
      button.action(actionType -> {
         user.addGroup(this.group);
         this.select(user);
      });
   }
}
