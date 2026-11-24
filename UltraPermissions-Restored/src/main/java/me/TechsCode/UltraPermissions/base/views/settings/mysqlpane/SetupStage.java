package me.TechsCode.UltraPermissions.base.views.settings.mysqlpane;

import me.TechsCode.UltraPermissions.base.misc.Setter;

public class SetupStage {
   private final String fieldName;
   private final String defaultValue;
   private final Setter<String> setFunction;

   public SetupStage(String text, String defaultValue, Setter<String> setter) {
      this.fieldName = text;
      this.defaultValue = defaultValue;
      this.setFunction = setter;
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public Setter<String> getSetFunction() {
      return this.setFunction;
   }
}
