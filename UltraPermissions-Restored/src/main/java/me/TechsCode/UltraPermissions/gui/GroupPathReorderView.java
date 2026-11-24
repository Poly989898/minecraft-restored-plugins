package me.TechsCode.UltraPermissions.gui;

import java.util.Comparator;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import org.bukkit.entity.Player;

public abstract class GroupPathReorderView extends PageableGUI<GroupPath> {
   private final UltraPermissions plugin;
   private GroupPath selected;
   private static final Phrase ENTRY_DESELECT_ACTION = Phrase.create(
      "groupPathReorderView.entry.deselectAction", "Click to **deselect** this group", Colors.GRAY, Colors.RED
   );
   private static final Phrase ENTRY_SWAP_ACTION = Phrase.create(
      "groupPathReorderView.entry.swapAction", "Click to swap with **%group%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase ENTRY_GRAB_ACTION = Phrase.create(
      "groupPathReorderView.entry.grabAction", "Click to **grab** this group", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase TITLE = Phrase.create("groupPathReorderView.title", "Group Path Sorting");

   public GroupPathReorderView(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      int count = 1;

      for (GroupPath groupPath : ultraPermissions.getGroupPaths()) {
         groupPath.setPriority(count);
         count++;
      }
   }

   public void construct(Button button, GroupPath groupPath) {
      button.material(groupPath.getIcon().getType()).name(Animation.wave(groupPath.getName(), Colors.Blue, Colors.WHITE)).setDamage(groupPath.getIcon().getDurability());
      if (this.selected != null) {
         if (this.selected.equals(groupPath)) {
            button.item().appendLore(ENTRY_DESELECT_ACTION.get());
            button.item().addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 1);
         } else {
            button.item().appendLore(ENTRY_SWAP_ACTION.get().replace("%group%", this.selected.getName()));
         }
      } else {
         button.item().appendLore(ENTRY_GRAB_ACTION.get());
      }

      if (groupPath.getDescription().length() > 0) {
         button.item().appendLore("", "\u00a7f" + groupPath.getDescription());
      }

      button.action(actionType -> {
         if (this.selected != null) {
            if (!this.selected.equals(groupPath)) {
               int priority = this.selected.getPriority();
               this.selected.setPriority(groupPath.getPriority());
               groupPath.setPriority(priority);
            }

            this.selected = null;
         } else {
            this.selected = groupPath;
         }
      });
   }

   public GroupPath[] getObjects() {
      return this.plugin.getGroupPaths().stream().sorted(Comparator.comparingInt(GroupPath::getPriority)).toArray(GroupPath[]::new);
   }

   @Override
   public String getTitle() {
      return TITLE.get();
   }

   @Override
   public SearchFeature<GroupPath> getSearch() {
      return null;
   }
}
