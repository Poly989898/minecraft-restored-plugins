package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GroupReorderView extends PageableGUI<Group> {
   private final UltraPermissions plugin;
   private Group selected;
   private static final Phrase ENTRY_DESELECT_ACTION = Phrase.create(
      "groupReorderView.entry.deselectAction", "Click to **deselect** this group", Colors.GRAY, Colors.RED
   );
   private static final Phrase ENTRY_SWAP_ACTION = Phrase.create(
      "groupReorderView.entry.swapAction", "Click to swap with **%group%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase ENTRY_GRAB_ACTION = Phrase.create("groupReorderView.entry.grabAction", "Click to **grab** this group", Colors.GRAY, Colors.GREEN);
   private static final Phrase TITLE = Phrase.create("groupReorderView.title", "Group Sorting (High to Low)");

   public GroupReorderView(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      int count = 1;

      for (Group group : ultraPermissions.getGroups().bestToWorst()) {
         group.setPriority(count);
         count++;
      }
   }

   public void construct(Button button, Group group) {
      button.material(group.getIcon(), group.getModelId()).name(Animation.wave(group.getName(), Colors.Blue, Colors.WHITE)).setDamage(group.getDamage());
      if (this.selected != null) {
         if (this.selected.equals(group)) {
            button.item().appendLore(ENTRY_DESELECT_ACTION.get());
            button.item().addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 1);
         } else {
            button.item().appendLore(ENTRY_SWAP_ACTION.get().replace("%group%", this.selected.getName()));
         }
      } else {
         button.item().appendLore(ENTRY_GRAB_ACTION.get());
      }

      button.action(actionType -> {
         if (this.selected != null) {
            if (!this.selected.equals(group)) {
               int priority = this.selected.getPriority();
               this.selected.setPriority(group.getPriority());
               group.setPriority(priority);
            }

            this.selected = null;
         } else {
            this.selected = group;
         }
      });
   }

   public Group[] getObjects() {
      return this.plugin.getGroups().bestToWorst().toArray(new Group[0]);
   }

   @Override
   public String getTitle() {
      return TITLE.get();
   }

   @Override
   public SearchFeature<Group> getSearch() {
      return null;
   }
}
