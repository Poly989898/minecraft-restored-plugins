package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.PermissionColor;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.gui.ServerPickerView;
import me.TechsCode.UltraPermissions.gui.WorldPickerView;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class PermissionView extends GUI {
   private final UltraPermissions plugin;
   private final PermissionHolder holder;
   private final PermissionWithInfo permissionWithInfo;
   private List<PermissionCopy> permissionCopies;
   private int currentCopy;
   private static final Phrase COPY_ENTRY_SELECT_ACTION = Phrase.create(
      "permissionView.entry.selectAction", "Click to **select** this copy", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase COPY_ENTRY_SELECTED_INDICATOR = Phrase.create(
      "permissionView.entry.selectedIndicator", "This copy is currently **selected**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase COPY_ENTRY_SERVER_ATTRIBUTE = Phrase.create(
      "permissionView.entry.serverAttribute", "Server: **%server%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase COPY_ENTRY_WORLD_ATTRIBUTE = Phrase.create(
      "permissionView.entry.worldAttribute", "World: **%world%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase COPY_ENTRY_PERMANENT_DESC = Phrase.create(
      "permissionView.entry.permanentDesc", "This permission is **permanent** and cannot expire", Colors.GRAY, Colors.RED
   );
   private static final Phrase COPY_ENTRY_TEMPORARY_DESC = Phrase.create(
      "permissionView.entry.temporaryDesc", "Expiring in **%time%**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase NEW_COPY_TITLE = Phrase.create("permissionView.newCopy.title", "Create new Copy");
   private static final Phrase NEW_COPY_ACTION = Phrase.create("permissionView.newCopy.action", "Click to create a new copy of this permission", Colors.GRAY);
   private static final Phrase SERVER_TITLE = Phrase.create("permissionView.server.title", "Server");
   private static final Phrase SERVER_ALL_SERVERS_ACTION = Phrase.create(
      "permissionView.server.allServersAction", "Click to **allow** permission on all Servers", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase SERVER_SPECIFIC_SERVER_ACTION = Phrase.create(
      "permissionView.server.specificServerAction", "Click to **restrict** permission to a certain Server", Colors.GRAY, Colors.RED
   );
   private static final Phrase SERVER_ALL_SERVERS_DESC = Phrase.create(
      "permissionView.server.allServersDesc", "Permission is available to all Servers", Colors.GRAY
   );
   private static final Phrase SERVER_SPECIFIC_SERVERS_DESC = Phrase.create(
      "permissionView.server.specificServersDesc", "Permission only available on Server **%server%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase WORLD_TITLE = Phrase.create("permissionView.world.title", "World");
   private static final Phrase WORLD_ALL_WORLDS_ACTION = Phrase.create(
      "permissionView.world.allWorldsAction", "Click to **allow** permission on all Worlds", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase WORLD_SPECIFIC_WORLD_ACTION = Phrase.create(
      "permissionView.world.specificWorldAction", "Click to **restrict** permission to a certain World", Colors.GRAY, Colors.RED
   );
   private static final Phrase WORLD_ALL_WORLDS_DESC = Phrase.create("permissionView.world.allWorldsDesc", "Permission is available to all Worlds", Colors.GRAY);
   private static final Phrase WORLD_SPECIFIC_WORLD_DESC = Phrase.create(
      "permissionView.world.specificWorldDesc", "Permission only available on World **%world%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase POLARITY_TITLE = Phrase.create("permissionView.polarity.title", "Polarity");
   private static final Phrase POLARITY_NEGATIVE_ACTION = Phrase.create(
      "permissionView.polarity.negativeAction", "Click to make this Permission **negative**", Colors.GRAY, Colors.RED
   );
   private static final Phrase POLARITY_POSITIVE_ACTION = Phrase.create(
      "permissionView.polarity.positiveAction", "Click to make this a **regular** Permission", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase POLARITY_NEGATIVE_ATTRIBUTE = Phrase.create(
      "permissionView.polarity.negativeAttribute", "This Permission is currently **negated**", Colors.GRAY, Colors.RED
   );
   private static final Phrase POLARITY_POSITIVE_ATTRIBUTE = Phrase.create(
      "permissionView.polarity.positiveAttribute", "This is currently a regular, **positive** permission", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase DELETE_TITLE = Phrase.create("permissionView.delete.title", "Delete");
   private static final Phrase DELETE_ACTION = Phrase.create("permissionView.delete.action", "Click to delete this copy", Colors.GRAY);
   private static final Phrase INHERITED_TITLE = Phrase.create("permissionView.inherited.title", "Permission Inherited");
   private static final Phrase INHERITED_ACTION = Phrase.create("permissionView.inherited.action", "Click to jump to Group", Colors.GRAY);
   private static final Phrase INHERITED_DESC = Phrase.create(
      "permissionView.inherited.desc",
      "This permission is inherited from **%group%** and can be only edited when jumping to that group",
      Colors.GRAY,
      Colors.YELLOW
   );

   public PermissionView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder, PermissionWithInfo permissionWithInfo) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.holder = permissionHolder;
      this.permissionWithInfo = permissionWithInfo;
      this.reloadPermissionCopies();
   }

   private void reloadPermissionCopies() {
      this.permissionCopies = new ArrayList<>();
      this.holder
         .getPermissions()
         .stream()
         .filter(permission -> this.permissionWithInfo.isThisPermission(permission.getName()))
         .forEach(permission -> this.permissionCopies.add(new PermissionCopy(permission, false)));
      this.holder
         .getAdditionalPermissions()
         .stream()
         .filter(permission -> this.permissionWithInfo.isThisPermission(permission.getName()))
         .forEach(permission -> this.permissionCopies.add(new PermissionCopy(permission, true)));
      this.currentCopy = Math.min(this.permissionCopies.size(), this.currentCopy + 1) - 1;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(this.holder.getName() + " > " + this.permissionWithInfo.getName());
      model.setSlots(45);

      for (int i = 0; i <= 8; i++) {
         int index = i;
         model.button(
            i + 1,
            this.permissionCopies.size() > i ? button -> this.CopyEntryButton(button, this.permissionCopies.get(index), index) : this::PlaceholderTabButton
         );
      }

      if (this.permissionCopies.size() < 8) {
         model.button(9, this::CreateNewCopyButton);
      }

      if (!this.permissionCopies.isEmpty()) {
         PermissionCopy permissionCopy = this.permissionCopies.get(this.currentCopy);
         if (permissionCopy.isInherited()) {
            model.button(23, button -> this.InheritedButton(button, permissionCopy.getPermission()));
         } else {
            model.button(20, button -> this.ServerButton(button, permissionCopy.getPermission()));
            model.button(22, button -> this.WorldButton(button, permissionCopy.getPermission()));
            model.button(24, button -> this.PolarityButton(button, permissionCopy.getPermission()));
            model.button(26, button -> this.DeleteButton(button, permissionCopy.getPermission()));
         }
      }

      model.button(41, button -> Common.BackButton(button, actionType -> this.onBack()));
   }

   private void CopyEntryButton(Button button, PermissionCopy permissionCopy, int count) {
      Permission permission = permissionCopy.getPermission();
      PermissionColor permissionColor = PermissionColor.fromPermission(permissionCopy);
      boolean flag = count == this.currentCopy;
      button.material(permissionColor.getGlassPane())
         .name((permission.isPositive() ? "" : Colors.RED + "-") + Animation.wave(permission.getName(), permissionColor.getColor(), Colors.WHITE))
         .lore(
            (flag ? COPY_ENTRY_SELECTED_INDICATOR : COPY_ENTRY_SELECT_ACTION).get(),
            "",
            COPY_ENTRY_SERVER_ATTRIBUTE.get().replace("%server%", this.getServer(permission)),
            COPY_ENTRY_WORLD_ATTRIBUTE.get().replace("%world%", this.getWorld(permission)),
            ""
         );
      if (permissionCopy.getPermission().isPermanent()) {
         button.item().appendLore(COPY_ENTRY_PERMANENT_DESC.get());
      } else {
         String timeString = Tools.getTimeString(permission.getExpiration() - System.currentTimeMillis(), TimeUnit.MILLISECONDS, 2);
         button.item().appendLore(COPY_ENTRY_TEMPORARY_DESC.get().replace("%time%", timeString));
      }

      if (flag) {
         button.item().addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 3);
      }

      button.action(actionType -> this.currentCopy = count);
   }

   private void CreateNewCopyButton(Button button) {
      button.material(XMaterial.ANVIL).name(Animation.wave(NEW_COPY_TITLE.get(), Colors.GOLD, Colors.WHITE)).lore(NEW_COPY_ACTION.get());
      button.action(actionType -> {
         this.holder.newPermission(this.permissionWithInfo.getName()).create();
         this.reloadPermissionCopies();
      });
   }

   private void PlaceholderTabButton(Button button) {
      button.material(XMaterial.GRAY_STAINED_GLASS_PANE).name();
   }

   private void ServerButton(Button button, Permission permission) {
      button.material(XMaterial.ENDER_CHEST)
         .name(Animation.wave(SERVER_TITLE.get(), Colors.GOLD, Colors.WHITE))
         .lore(
            (permission.getServer().isPresent() ? SERVER_ALL_SERVERS_ACTION : SERVER_SPECIFIC_SERVER_ACTION).get(),
            "",
            permission.getServer().isPresent() ? SERVER_SPECIFIC_SERVERS_DESC.get().replace("%server%", permission.getServer().get()) : SERVER_ALL_SERVERS_DESC.get()
         );
      button.action(actionType -> {
         if (permission.getServer().isPresent()) {
            permission.setServer(null);
         } else {
            new ServerPickerView(this.p, this.plugin) {
               @Override
               public void onSelect(NServer nServer) {
                  permission.setServer(nServer.getName());
                  this.onBack();
               }

               @Override
               public String getTitle() {
                  return permission.getName() + " > Server";
               }

               @Override
               public void onBack() {
                  PermissionView.this.reopen();
               }
            };
         }
      });
   }

   private void WorldButton(Button button, Permission permission) {
      button.material(XMaterial.CHEST)
         .name(Animation.wave(WORLD_TITLE.get(), Colors.GOLD, Colors.WHITE))
         .lore(
            (permission.getWorld().isPresent() ? WORLD_ALL_WORLDS_ACTION : WORLD_SPECIFIC_WORLD_ACTION).get(),
            "",
            permission.getWorld().isPresent() ? WORLD_SPECIFIC_WORLD_DESC.get().replace("%world%", permission.getWorld().get()) : WORLD_ALL_WORLDS_DESC.get()
         );
      button.action(actionType -> {
         if (permission.getWorld().isPresent()) {
            permission.setWorld(null);
         } else {
            new WorldPickerView(this.p, this.plugin) {
               @Override
               public void onSelect(World world) {
                  permission.setWorld(world.getName());
                  this.onBack();
               }

               @Override
               public String getTitle() {
                  return permission.getName() + " > World";
               }

               @Override
               public void onBack() {
                  PermissionView.this.reopen();
               }
            };
         }
      });
   }

   private void PolarityButton(Button button, Permission permission) {
      button.material(XMaterial.REPEATER)
         .name(Animation.wave(POLARITY_TITLE.get(), Colors.GOLD, Colors.WHITE))
         .lore(
            (permission.isPositive() ? POLARITY_NEGATIVE_ACTION : POLARITY_POSITIVE_ACTION).get(),
            "",
            (permission.isPositive() ? POLARITY_POSITIVE_ATTRIBUTE : POLARITY_NEGATIVE_ATTRIBUTE).get()
         );
      button.action(actionType -> permission.setPositive(!permission.isPositive()));
   }

   private void DeleteButton(Button button, Permission permission) {
      button.material(Material.REDSTONE_BLOCK).name(Animation.wave(DELETE_TITLE.get(), Colors.Red, Colors.WHITE)).lore(DELETE_ACTION.get());
      button.action(actionType -> {
         permission.remove();
         if (this.permissionCopies.size() == 1) {
            this.onBack();
         } else {
            this.reloadPermissionCopies();
         }
      });
   }

   private void InheritedButton(Button button, Permission permission) {
      button.material(XMaterial.STICKY_PISTON).name(Animation.wave(INHERITED_TITLE.get(), Colors.GOLD, Colors.WHITE)).lore(INHERITED_ACTION.get(), "");

      for (String text : INHERITED_DESC.split(60)) {
         button.item().appendLore(text.replace("%group%", permission.getHolder().getName()));
      }

      button.action(actionType -> {
         Optional<PermissionHolder> optional = permission.getHolder().get();
         optional.ifPresent(holder -> new PermissionEditorMainView(this.p, this.plugin, holder) {
            @Override
            public void onBack() {
               PermissionView.this.reopen();
            }
         });
      });
   }

   private String getServer(Permission permission) {
      if (permission.getServer().isPresent()) {
         if (permission.getServer().get().equals("BungeeCord")) {
            return Colors.RED + "BungeeCord";
         } else {
            return permission.getServer().get().equals(this.plugin.getThisServer().map(NServer::getName).orElse(null)) ? "This Server" : permission.getServer().get();
         }
      } else {
         return "All Servers";
      }
   }

   private String getWorld(Permission permission) {
      return permission.getWorld().orElse("All Worlds");
   }

   public abstract void onBack();
}
