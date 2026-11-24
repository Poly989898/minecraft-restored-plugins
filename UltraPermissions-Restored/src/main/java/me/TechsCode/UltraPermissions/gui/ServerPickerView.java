package me.TechsCode.UltraPermissions.gui;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.networking.NPlayer;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.networking.NetworkData;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class ServerPickerView extends PageableGUI<NServer> {
   private final UltraPermissions plugin;
   private final NServer thisServer;
   private static final Phrase ENTRY_ACTION = Phrase.create("serverPickerView.entry.action", "Click to select", Colors.GRAY);
   private static final Phrase ENTRY_CURRENT_INDICATOR = Phrase.create(
      "serverPickerView.entry.currentIndicator", "You are currently on this Server", Colors.GREEN
   );
   private static final Phrase ENTRY_ADDRESS_ATTRIBUTE = Phrase.create(
      "serverPickerView.entry.addressAttribute", "Address: **%ip%**:**%port%**", Colors.GRAY, Colors.YELLOW, Colors.YELLOW
   );
   private static final Phrase ENTRY_ADDRESS_ONLINE_PLAYERS_TITLE = Phrase.create("serverPickerView.entry.onlinePlayers.title", "Online Players:", Colors.GRAY);
   private static final Phrase ENTRY_ADDRESS_ONLINE_PLAYERS_ENTRY = Phrase.create(
      "serverPickerView.entry.onlinePlayers.entry", "- **%player%**", Colors.GRAY, Colors.YELLOW
   );

   public ServerPickerView(Player player, UltraPermissions ultraPermissions) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.thisServer = ultraPermissions.getThisServer().orElse(null);
   }

   public abstract void onSelect(NServer nServer);

   @Override
   public SearchFeature<NServer> getSearch() {
      return new BasicSearch<NServer>() {
         public String[] getSearchableText(NServer nServer) {
            return new String[]{nServer.getName()};
         }
      };
   }

   public NServer[] getObjects() {
      Optional<NetworkData> data = this.plugin.getNetworkManager().getData();
      return data.<NServer[]>map(networkData -> networkData.getServerList().toArray(new NServer[0])).orElseGet(() -> new NServer[0]);
   }

   public void construct(Button button, NServer nServer) {
      boolean flag = this.thisServer != null && this.thisServer.getName().equals(nServer.getName());
      button.material(XMaterial.BLUE_STAINED_GLASS).name(Animation.wave(nServer.getName(), Colors.DarkBlue, Colors.WHITE)).lore(ENTRY_ACTION.get());
      if (flag) {
         button.item().appendLore("", ENTRY_CURRENT_INDICATOR.get());
      }

      button.item()
         .appendLore(
            "",
            ENTRY_ADDRESS_ATTRIBUTE.get().replace("%ip%", nServer.getIp()).replace("%port%", Integer.toString(nServer.getPort())),
            "",
            ENTRY_ADDRESS_ONLINE_PLAYERS_TITLE.get()
         );

      for (NPlayer player : nServer.getPlayers()) {
         button.item().appendLore(ENTRY_ADDRESS_ONLINE_PLAYERS_ENTRY.get().replace("%player%", player.getName()));
      }

      if (nServer.getPlayers().isEmpty()) {
         button.item().appendLore(Colors.RED + Words.NONE.get());
      }

      button.action(actionType -> this.onSelect(nServer));
   }
}
