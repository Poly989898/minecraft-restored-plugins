package io.lumine.mythic.lib.api;

import io.lumine.mythic.lib.util.configobject.JsonWrapper;

public class MMOLineConfig extends JsonWrapper {
   private final String key;
   private final String[] args;

   public MMOLineConfig(String value) {
      super(value);
      if (value.contains("{") && value.contains("}")) {
         int begin = value.indexOf("{");
         int end = value.lastIndexOf("}") + 1;
         this.key = value.substring(0, begin);
         String format = value.substring(Math.min(value.length(), end + 1));
         this.args = format.isEmpty() ? new String[0] : format.split(" ");
      } else {
         String[] split = value.split(" ");
         this.key = split[0];
         this.args = split.length > 1 ? value.replace(this.key + " ", "").split(" ") : new String[0];
      }
   }

   public String[] args() {
      return this.args;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   @Deprecated
   public void validate(String... paths) {
      this.validateKeys(paths);
   }

   @Override
   public String toString() {
      return this.key + this.object.toString();
   }
}
