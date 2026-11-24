package me.TechsCode.UltraPermissions.gui;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class GroupStorageSettingsView extends GUI {
   private final UltraPermissions plugin;
   private final Group group;
   private static final Phrase WORLD_TITLE = Phrase.create("groupStorageSettingsView.world.title", "World");
   private static final Phrase WORLD_ALL_WORLDS_ACTION = Phrase.create(
      "groupStorageSettingsView.world.allWorldsAction", "Click to allow this Group on all Worlds", Colors.GRAY
   );
   private static final Phrase WORLD_SPECIFIC_WORLDS_ACTION = Phrase.create(
      "groupStorageSettingsView.world.specificWorldAction", "Click to restrict this Group to a certain World", Colors.GRAY
   );
   private static final Phrase WORLD_ATTRIBUTE = Phrase.create("groupStorageSettingsView.world.attribute", "World: **%world%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase SERVER_TITLE = Phrase.create("groupStorageSettingsView.server.title", "Server");
   private static final Phrase SERVER_DISABLED_NO_NETWORK = Phrase.create(
      "groupStorageSettingsView.server.disabled", "This feature is only available for networks", Colors.GRAY
   );
   private static final Phrase SERVER_ALL_SERVERS_ACTION = Phrase.create(
      "groupStorageSettingsView.server.allServersAction", "Click to allow this Group on all Servers", Colors.GRAY
   );
   private static final Phrase SERVER_SPECIFIC_SERVERS_ACTION = Phrase.create(
      "groupStorageSettingsView.server.specificServersAction", "Click to restrict this Group to a certain Server", Colors.GRAY
   );
   private static final Phrase SERVER_ATTRIBUTE = Phrase.create("groupStorageSettingsView.server.attribute", "Server: **%server%**", Colors.GRAY, Colors.BLUE);

   public GroupStorageSettingsView(Player player, UltraPermissions ultraPermissions, Group group) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.group = group;
   }

   public abstract void onBack();

   @Override
   public int getCurrentSlots() {
      return 36;
   }

   @Override
   public String getCurrentTitle() {
      return this.group.getName() + " > Storage Settings";
   }

   @Override
   protected void construct(Model model) {
      model.button(12, this::WorldButton);
      model.button(16, this::ServerButton);
      model.button(32, button -> Common.BackButton(button, actionType -> this.onBack()));
   }

   private void WorldButton(Button button) {
      Optional<String> world = this.group.getWorld();
      button.material(world.isPresent() ? XMaterial.YELLOW_STAINED_GLASS : XMaterial.GRAY_STAINED_GLASS_PANE)
         .name(Animation.wave(WORLD_TITLE.get(), Colors.YELLOW, Colors.WHITE))
         .lore((world.isPresent() ? WORLD_ALL_WORLDS_ACTION : WORLD_SPECIFIC_WORLDS_ACTION).get());
      world.ifPresent(text -> button.item().appendLore("", WORLD_ATTRIBUTE.get().replace("%world%", text)));
      button.action(actionType -> {
         if (world.isPresent()) {
            this.group.setWorld(null);
         } else {
            new WorldPickerView(this.p, this.plugin) {
               @Override
               public void onSelect(World selectedWorld) {
                  GroupStorageSettingsView.this.group.setWorld(selectedWorld.getName());
                  this.onBack();
               }

               @Override
               public String getTitle() {
                  return GroupStorageSettingsView.this.group.getName() + " > World";
               }

               @Override
               public void onBack() {
                  GroupStorageSettingsView.this.reopen();
               }
            };
         }
      });
   }

   private void ServerButton(Button button) {
      if (!this.plugin.isConnectedToNetwork()) {
         button.material(XMaterial.RED_STAINED_GLASS_PANE)
            .name(Animation.wave(SERVER_TITLE.get(), Colors.Red, Colors.WHITE))
            .lore(SERVER_DISABLED_NO_NETWORK.get());
      } else {
         Optional<String> server = this.group.getServer();
         button.material(server.isPresent() ? XMaterial.BLUE_STAINED_GLASS : XMaterial.GRAY_STAINED_GLASS_PANE)
            .name(Animation.wave(SERVER_TITLE.get(), Colors.DarkBlue, Colors.WHITE))
            .lore((server.isPresent() ? SERVER_ALL_SERVERS_ACTION : SERVER_SPECIFIC_SERVERS_ACTION).get());
         server.ifPresent(text -> button.item().appendLore("", SERVER_ATTRIBUTE.get().replace("%server%", text)));
         button.action(actionType -> {
            if (server.isPresent()) {
               this.group.setServer(null);
            } else {
               new ServerPickerView(this.p, this.plugin) {
                  @Override
                  public void onSelect(NServer nServer) {
                     GroupStorageSettingsView.this.group.setServer(nServer.getName());
                     this.onBack();
                  }

                  @Override
                  public String getTitle() {
                     return GroupStorageSettingsView.this.group.getName() + " > Server";
                  }

                  @Override
                  public void onBack() {
                     GroupStorageSettingsView.this.reopen();
                  }
               };
            }
         });
      }
   }
}
