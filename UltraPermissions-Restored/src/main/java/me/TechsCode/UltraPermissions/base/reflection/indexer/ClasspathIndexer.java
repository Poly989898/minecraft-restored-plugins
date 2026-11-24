package me.TechsCode.UltraPermissions.base.reflection.indexer;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClasspathIndexer {
   public ClasspathIndexer() {
   }

   public static NodeList index() {
      NodeList list = new NodeList();

      try {
         CodeSource codeSource = ClasspathIndexer.class.getProtectionDomain().getCodeSource();
         if (codeSource != null) {
            URL location = codeSource.getLocation();
            ZipInputStream zipInputStream = new ZipInputStream(location.openStream());

            while (true) {
               ZipEntry nextEntry = zipInputStream.getNextEntry();
               if (nextEntry == null) {
                  break;
               }

               list.add(new Node(nextEntry.getName()));
            }
         }
      } catch (IOException exception) {
         exception.printStackTrace();
      }

      return list;
   }
}
