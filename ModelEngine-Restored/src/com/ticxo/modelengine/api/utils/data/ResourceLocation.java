package com.ticxo.modelengine.api.utils.data;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceLocation {
   private final String namespace;
   private final String path;

   public ResourceLocation(String full) {
      full = full.toLowerCase(Locale.ENGLISH);
      String[] split = full.split(":", 2);
      if (split.length <= 1) {
         this.namespace = "minecraft";
         this.path = full;
      } else {
         this.namespace = split[0];
         this.path = split[1];
      }
   }

   @Override
   public String toString() {
      return !this.namespace.isBlank() && !"minecraft".equals(this.namespace) ? this.namespace + ":" + this.path : this.path;
   }

   public boolean isValid() {
      return isValidNamespace(this.namespace) && isValidPath(this.path);
   }

   public static boolean isValidPath(String path) {
      for (int i = 0; i < path.length(); i++) {
         if (!validPathChar(path.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidNamespace(String namespace) {
      for (int i = 0; i < namespace.length(); i++) {
         if (!validNamespaceChar(namespace.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static boolean validPathChar(char character) {
      return character == '_'
         || character == '-'
         || character >= 'a' && character <= 'z'
         || character >= '0' && character <= '9'
         || character == '/'
         || character == '.';
   }

   private static boolean validNamespaceChar(char character) {
      return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
   }
}
