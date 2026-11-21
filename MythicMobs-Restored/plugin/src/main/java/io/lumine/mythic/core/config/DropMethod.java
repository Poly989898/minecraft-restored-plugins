package io.lumine.mythic.core.config;

public enum DropMethod {
   VANILLA("VANILLA"),
   FANCY("FANCY"),
   CHEST("CHEST");

   private String name;

   private DropMethod(String name) {
      this.name = name;
   }
}
