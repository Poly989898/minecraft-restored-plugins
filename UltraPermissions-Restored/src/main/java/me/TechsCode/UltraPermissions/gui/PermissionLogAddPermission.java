package me.TechsCode.UltraPermissions.gui;

import java.util.ArrayList;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.permissionEditor.PermissionEditorMainView;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class PermissionLogAddPermission extends PageableGUI<PermissionHolder> {
   private final Player p;
   private final UltraPermissions plugin;
   private final String permission;
   private static final Phrase TITLE = Phrase.create("permissionLogAddPermission.title", "Permission > %permission%");
   private static final Phrase ENTRY_ACTION = Phrase.create("permissionLogAddPermission.entry.action", "Click to **add** permission", Colors.GRAY, Colors.AQUA);

   public PermissionLogAddPermission(Player player, UltraPermissions ultraPermissions, String text) {
      super(player, ultraPermissions);
      this.p = player;
      this.plugin = ultraPermissions;
      this.permission = text;
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%permission%", this.permission);
   }

   public void construct(Button button, PermissionHolder holder) {
      button.material(XMaterial.WHITE_STAINED_GLASS_PANE).name(Animation.wave(holder.getName(), Colors.Green, Colors.WHITE)).lore(ENTRY_ACTION.get());
      button.action(actionType -> {
         holder.newPermission(this.permission).create();
         new PermissionEditorMainView(this.p, this.plugin, holder) {
            @Override
            public void onBack() {
               PermissionLogAddPermission.this.onBack();
            }
         };
      });
   }

   public PermissionHolder[] getObjects() {
      ArrayList<PermissionHolder> permissionHolders = new ArrayList<>();
      permissionHolders.addAll(this.plugin.getGroups());
      permissionHolders.addAll(this.plugin.getUsers());
      return permissionHolders.toArray(new PermissionHolder[0]);
   }

   @Override
   public SearchFeature<PermissionHolder> getSearch() {
      return new BasicSearch<PermissionHolder>() {
         public String[] getSearchableText(PermissionHolder holder) {
            return new String[]{holder.getName()};
         }
      };
   }
}
