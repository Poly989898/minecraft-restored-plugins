package litebans;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class FabricMeta {
   private String modId;
   private String modName;
   private String modVersion;

   public FabricMeta(String text) {
      try {
         String text2 = "fabric.mod.json";
         JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(new File(text).toPath()));

         JarEntry jarEntry;
         while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(text2)) {
               JSONObject jsonObject = (JSONObject)new JSONParser().computeObj(new InputStreamReader(jarInputStream));
               this.modId = (String)jsonObject.get("id");
               this.modName = (String)jsonObject.get("name");
               this.modVersion = (String)jsonObject.get("version");
               break;
            }
         }

         if (this.modId == null) {
            throw new AssertionError("Missing " + text2);
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String toString() {
      return "FabricMeta(id=" + this.getModId() + ", name=" + this.getModName() + ", version=" + this.getModVersion() + ")";
   }

   public String getModId() {
      return this.modId;
   }

   public String getModName() {
      return this.modName;
   }

   public String getModVersion() {
      return this.modVersion;
   }
}
