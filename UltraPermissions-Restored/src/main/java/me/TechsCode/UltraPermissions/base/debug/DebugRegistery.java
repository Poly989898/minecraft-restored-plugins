package me.TechsCode.UltraPermissions.base.debug;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.Generated;
import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class DebugRegistery extends RegistryStorable {
   private ArrayList<String> debuggers;
   private final ArrayList<String> availableDebuggers = new ArrayList<>();

   public DebugRegistery() {
      super("debuggers");
      this.debuggers = new ArrayList<>();
   }

   @Override
   public void setState(JsonObject jsonObject) {
      String asString = jsonObject.get("enabled").getAsString();
      String text = asString.substring(1, asString.length() - 1);
      String[] texts = text.split(", ");
      this.debuggers = new ArrayList<>(Arrays.asList(texts));
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", this.debuggers.toString());
      return jsonObject;
   }

   public boolean isDebugging(String text) {
      for (String debugger : this.debuggers) {
         if (debugger.equalsIgnoreCase(text)) {
            return true;
         }
      }

      return false;
   }

   public void addDebugger(String text) {
      this.debuggers.add(text);
      this.sync();
   }

   public void removeDebugger(String text) {
      this.debuggers.remove(text);
      this.sync();
   }

   public void addAvailableDebugger(String text) {
      this.availableDebuggers.add(text);
   }

   public void addAvailableDebugger(String... texts) {
      this.availableDebuggers.addAll(Arrays.asList(texts));
   }

   @Generated
   public ArrayList<String> getDebuggers() {
      return this.debuggers;
   }

   @Generated
   public ArrayList<String> getAvailableDebuggers() {
      return this.availableDebuggers;
   }
}
