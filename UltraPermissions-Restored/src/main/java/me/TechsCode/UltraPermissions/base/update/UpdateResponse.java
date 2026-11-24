package me.TechsCode.UltraPermissions.base.update;

import java.io.File;

public class UpdateResponse {
   private ResponseType type;
   private File file;

   public UpdateResponse(ResponseType responseType, File file) {
      this.type = responseType;
      this.file = file;
   }

   public ResponseType getType() {
      return this.type;
   }

   public File getFile() {
      return this.file;
   }
}
