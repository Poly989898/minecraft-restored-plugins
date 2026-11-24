package me.TechsCode.UltraPermissions.base.addons.cloud;

public enum CloudFileType {
   JAR("jar"),
   YML("yml");

   private final String extension;

   CloudFileType(String extension) {
      this.extension = extension;
   }

   public String getExtension() {
      return this.extension;
   }
}
