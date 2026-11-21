package com.ticxo.modelengine.api.error;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class WarnBadNamespace extends IError.Warn {
   private final String namespace;

   @Override
   public String getErrorMessage() {
      return "Warning: Namespace contains characters other than a-z_0-9. Using default namespace.[" + this.namespace + "]";
   }
}
