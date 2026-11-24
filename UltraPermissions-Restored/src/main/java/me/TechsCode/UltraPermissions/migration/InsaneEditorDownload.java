package me.TechsCode.UltraPermissions.migration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

public class InsaneEditorDownload {
   private UltraPermissions plugin;
   private Player player;
   private static final String currentLocation = System.getProperty("user.dir");
   private static final Phrase INSTALL_SUCCESSFUL = Phrase.create(
      "insaneEditorDownload.install.successful", "Successfully installed **Insane Editor**", Colors.GRAY, Colors.GOLD
   );
   private static final Phrase INSTALL_UNSUCCESSFUL = Phrase.create(
      "insaneEditorDownload.install.unsuccessful", "There was a problem while downloading **Insane Editor**", Colors.GRAY, Colors.GOLD
   );
   private static final Phrase INSTRUCTIONS = Phrase.create(
      "insaneEditorDownload.instructions", "Type in **/InsaneEditor help** to start configuring the Web Editor", Colors.GRAY, Colors.GOLD
   );
   private static final Phrase WIKI = Phrase.create(
      "insaneEditorDownload.wiki", "Insane Editor Wiki: **https://wiki.insaneeditor.com**", Colors.GRAY, Colors.GREEN
   );

   public InsaneEditorDownload(Player player, UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      this.player = player;
      if (this.isInstalled()) {
         this.showCommands();
      } else {
         this.installJar(this.getDownloadLink());
      }
   }

   private boolean isInstalled() {
      return Bukkit.getPluginManager().getPlugin("InsaneEditor") != null;
   }

   private void showCommands() {
      this.player.performCommand("insaneeditor help");
   }

   private void installJar(String text) {
      try {
         File file = new File(currentLocation + "/plugins/" + text.split("/")[4]);
         FileUtils.copyURLToFile(new URL(text), file);
         Bukkit.getPluginManager().loadPlugin(file);
         Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("InsaneEditor"));
         this.player.sendMessage(this.plugin.getPrefix() + INSTALL_SUCCESSFUL.get());
         this.player.sendMessage(this.plugin.getPrefix() + INSTRUCTIONS.get());
         this.player.sendMessage(this.plugin.getPrefix() + WIKI.get());
      } catch (IOException | InvalidDescriptionException | InvalidPluginException exception) {
         exception.printStackTrace();
         this.player.sendMessage(this.plugin.getPrefix() + INSTALL_UNSUCCESSFUL.get());
      }
   }

   private String getDownloadLink() {
      try {
         Scanner scanner = new Scanner(new URL("https://download.insaneeditor.com/latest").openStream());
         StringBuffer stringBuffer = new StringBuffer();

         while (scanner.hasNext()) {
            stringBuffer.append(scanner.next());
         }

         return stringBuffer.toString();
      } catch (IOException exception) {
         exception.printStackTrace();
         return "WRONG URL";
      }
   }
}
