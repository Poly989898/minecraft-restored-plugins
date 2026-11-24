package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class PluginPermissionListView extends PermissionListView {
   private final PermissionHolder holder;
   private final PermissionEditorMainView.PluginEntry sourcePlugin;
   private static final Phrase TITLE = Phrase.create("pluginPermissionListView.title", "%plugin% > Permissions");
   private static final Phrase ALL_PERMISSIONS_TITLE = Phrase.create("pluginPermissionListView.allPermissions.title", "All Permissions");
   private static final Phrase ALL_PERMISSIONS_ADD_ACTION = Phrase.create(
      "pluginPermissionListView.allPermissions.addAction", "**Left Click** to **add** all Permissions", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase ALL_PERMISSIONS_REMOVE_ACTION = Phrase.create(
      "pluginPermissionListView.allPermissions.removeAction", "**Right Click** to **remove** all Permissions", Colors.GRAY, Colors.AQUA, Colors.RED
   );

   public PluginPermissionListView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder, PermissionEditorMainView.PluginEntry pluginEntry) {
      super(player, ultraPermissions, permissionHolder);
      this.holder = permissionHolder;
      this.sourcePlugin = pluginEntry;
   }

   @Override
   public void reopen() {
      super.reopen();
      this.sourcePlugin.retrieveData();
   }

   public PermissionWithInfo[] getObjects() {
      return this.sourcePlugin
         .permissionsFromDatabase
         .stream()
         .map(permissionInfo -> new PermissionWithInfo(permissionInfo.getPermission(), permissionInfo))
         .distinct()
         .toArray(PermissionWithInfo[]::new);
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%plugin%", this.sourcePlugin.name);
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(this.getRightOptionSlot(), this::AllPermissionsButton);
   }

   private void AllPermissionsButton(Button button) {
      button.material(XMaterial.BUCKET)
         .name(Animation.wave(ALL_PERMISSIONS_TITLE.get(), Colors.Green, Colors.WHITE))
         .lore(ALL_PERMISSIONS_ADD_ACTION.get(), ALL_PERMISSIONS_REMOVE_ACTION.get());
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            PermissionList permissions = this.holder.getPermissions();
            PermissionList additionalPermissions = this.holder.getAdditionalPermissions();

            for (PermissionWithInfo permissionWithInfo : this.getObjects()) {
               if (permissions.name(permissionWithInfo.getName()).isEmpty() && additionalPermissions.name(permissionWithInfo.getName()).isEmpty()) {
                  this.holder.newPermission(permissionWithInfo.getName()).create();
               }
            }
         }

         if (actionType == ActionType.RIGHT) {
            PermissionList permissions = this.holder.getPermissions();

            for (PermissionWithInfo permissionWithInfo : this.getObjects()) {
               permissions.name(permissionWithInfo.getName()).forEach(Permission::remove);
            }
         }
      });
   }
}
