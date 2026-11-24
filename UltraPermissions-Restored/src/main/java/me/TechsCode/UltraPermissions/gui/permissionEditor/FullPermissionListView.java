package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.entity.Player;

public abstract class FullPermissionListView extends PermissionListView {
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private static final Phrase TITLE = Phrase.create("fullPermissionListView.title", "%holder% > Added Permissions");
   private static final Phrase ADD_PERMISSION_TITLE = Phrase.create("fullPermissionListView.addPermission.title", "Add Permission");
   private static final Phrase ADD_PERMISSION_ADD_ACTION = Phrase.create(
      "fullPermissionListView.addPermission.addAction", "**Left Click** to add a Permission via Chat", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase ADD_PERMISSION_ADD_BUNGEE_ACTION = Phrase.create(
      "fullPermissionListView.addPermission.addBungeeAction", "**Right Click** to add **Bungee Permission**", Colors.GRAY, Colors.AQUA, Colors.RED
   );

   public FullPermissionListView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder) {
      super(player, ultraPermissions, permissionHolder);
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
   }

   @Override
   public String getTitle() {
      return TITLE.get().replace("%holder%", this.holder.getName());
   }

   public PermissionWithInfo[] getObjects() {
      List<String> texts = this.holder.getPermissions().stream().map(Permission::getName).distinct().collect(Collectors.toList());
      List<String> additionalPermissions = this.holder.getAdditionalPermissions().stream().map(Permission::getName).distinct().collect(Collectors.toList());
      return Stream.<String>concat(texts.stream(), additionalPermissions.stream())
         .map(text -> new PermissionWithInfo(text, this.plugin.getPermissionDatabase().getInfo(text)))
         .sorted(Comparator.comparing(PermissionWithInfo::getName))
         .toArray(PermissionWithInfo[]::new);
   }

   @Override
   protected void construct(Model model) {
      super.construct(model);
      model.button(this.getRightOptionSlot(), this::AddPermissionButton);
   }

   private void AddPermissionButton(Button button) {
      button.material(XMaterial.NAME_TAG).name(Animation.wave(ADD_PERMISSION_TITLE.get(), Colors.Blue, Colors.WHITE)).lore(ADD_PERMISSION_ADD_ACTION.get());
      button.action(actionType -> {
         if (actionType == ActionType.LEFT) {
            new AddPermissionDialog(this.p, this.plugin, this.holder) {
               @Override
               public void reopen() {
                  FullPermissionListView.this.reopen();
               }
            };
         }
      });
      if (this.plugin.getMySQLManager().isEnabled() && this.plugin.isConnectedToNetwork()) {
         button.item().appendLore(ADD_PERMISSION_ADD_BUNGEE_ACTION.get());
         button.action(actionType -> {
            if (actionType == ActionType.RIGHT) {
               new AddBungeePermissionDialog(this.p, this.plugin, this.holder) {
                  @Override
                  public void reopen() {
                     FullPermissionListView.this.reopen();
                  }
               };
            }
         });
      }
   }
}
