package me.TechsCode.UltraPermissions.base.source;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import org.apache.commons.io.FileUtils;

public class LocalRepository {
   private TechPlugin plugin;
   private File folder;

   public LocalRepository(TechPlugin plugin) {
      this.plugin = plugin;
      this.folder = new File(plugin.getPluginFolder().getAbsolutePath() + "/Maven");
   }

   public File getIfPresent(Dependency dependency) {
      File file = new File(this.folder.getAbsolutePath() + "/" + dependency.toURL());
      return file.exists() ? file : null;
   }

   public boolean load(Dependency dependency, Collection<String> collection) {
      File file = new File(this.folder.getAbsolutePath() + "/" + dependency.toURL());

      for (String text : collection) {
         String url = text + "/" + dependency.toURL();

         try {
            FileUtils.copyURLToFile(new URL(url), file);
            return true;
         } catch (IOException ex) {
         }
      }

      return false;
   }
}
