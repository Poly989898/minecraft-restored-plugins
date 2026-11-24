package me.TechsCode.UltraPermissions.gui;

import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.entity.Player;

public abstract class GrantGroupsListView extends PageableGUI<Group> {
   private final Player p;
   private final UltraPermissions plugin;
   private final Group group;
   private static final Phrase TITLE = Phrase.create("GrantGroupsListView.title", "%group% > Groups");
   private static final Phrase ENTRY_ACTION = Phrase.create("GrantGroupsListView.entry.action", "Click to **remove** group", Colors.GRAY, Colors.RED);
   private static final Phrase ENTRY_TIME_TITLE = Phrase.create("GrantGroupsListView.entry.timeTitle", "Max. duration:", Colors.GRAY);
   private static final Phrase ENTRY_PERMANENT = Phrase.create("GrantGroupsListView.entry.permanent", "Permanent", Colors.RED);
   private static final Phrase ADD_TITLE = Phrase.create("GrantGroupsListView.add.title", "Add");
   private static final Phrase ADD_ACTION = Phrase.create("GrantGroupsListView.add.action", "Click to add group", Colors.GRAY);

   public GrantGroupsListView(Player player, UltraPermissions ultraPermissions, Group group) {
      super(player, ultraPermissions);
      this.p = player;
      this.plugin = ultraPermissions;
      this.group = group;
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%group%", this.group.getName());
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(this::AddButton, this.getRightOptionSlot());
   }

   public void construct(Button button, Group group) {
      long expiry = this.group.getGrantGroups().get(group.toStored());
      button.material(group.getIcon(), group.getModelId())
         .name(Animation.wave(group.getName(), Colors.YELLOW, Colors.WHITE))
         .lore(
            ENTRY_ACTION.get(),
            "",
            ENTRY_TIME_TITLE.get() + (expiry == 0L ? " " + ENTRY_PERMANENT.get() : " \u00a7e" + Tools.getTimeString(expiry, TimeUnit.SECONDS))
         )
         .setDamage(group.getDamage());
      button.action(actionType -> this.group.removeGrantGroup(group));
   }

   public Group[] getObjects() {
      GroupList list = new GroupList();
      this.group.getGrantGroups().keySet().stream().filter(stored -> !list.contains(stored.get().get())).forEach(stored -> list.add(stored.get().get()));
      return list.toArray(new Group[0]);
   }

   @Override
   public SearchFeature<Group> getSearch() {
      return new BasicSearch<Group>() {
         public String[] getSearchableText(Group group) {
            return new String[]{group.getName()};
         }
      };
   }

   private void AddButton(Button button) {
      button.material(XMaterial.ANVIL).name(Animation.fading(ADD_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(ADD_ACTION.get());
      button.action(actionType -> new GrantGroupsAddView(this.p, this.plugin, this.group) {
         @Override
         public void onBack() {
            GrantGroupsListView.this.reopen();
         }
      });
   }
}
