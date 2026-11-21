package litebans;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class FabricMeta {
   private String text3;
   private String text4;
   private String text5;

   public FabricMeta(String text) {
      try {
         String text2 = "fabric.mod.json";
         JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(new File(text).toPath()));

         JarEntry jarEntry;
         while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(text2)) {
               JSONObject jsonObject = (JSONObject)new JSONParser().computeObj(new InputStreamReader(jarInputStream));
               this.text3 = (String)jsonObject.get("id");
               this.text4 = (String)jsonObject.get("name");
               this.text5 = (String)jsonObject.get("version");
               break;
            }
         }

         if (this.text3 == null) {
            throw new AssertionError("Missing " + text2);
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String toString() {
      return "FabricMeta(id=" + this.getText3() + ", name=" + this.getText4() + ", version=" + this.getText5() + ")";
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public String getText5() {
      return this.text5;
   }
}
