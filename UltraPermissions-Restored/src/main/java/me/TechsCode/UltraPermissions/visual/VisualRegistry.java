package me.TechsCode.UltraPermissions.visual;

import java.util.HashMap;
import java.util.Map;
import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import me.TechsCode.UltraPermissions.base.registry.Switchable;
import me.TechsCode.UltraPermissions.base.visual.Text;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class VisualRegistry extends RegistryStorable implements Switchable {
   private boolean switchedTo = false;
   private Map<VisualType, String> formats = new HashMap<>();
   private boolean editingNametags = true;

   public VisualRegistry() {
      super("visual");

      for (VisualType visualType : VisualType.values()) {
         this.formats.put(visualType, visualType.getDefaultFormat());
      }
   }

   @Override
   public void setState(JsonObject jsonObject) {
      this.switchedTo = jsonObject.get("switchedTo").getAsBoolean();
      this.formats = new HashMap<>();
      JsonObject asJsonObject = jsonObject.getAsJsonObject("formats");
      asJsonObject.entrySet().forEach(entry -> this.formats.put(VisualType.valueOf((String)entry.getKey()), Text.color(((JsonElement)entry.getValue()).getAsString())));
      this.editingNametags = !jsonObject.has("editingNametags") || jsonObject.get("editingNametags").getAsBoolean();
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("switchedTo", this.switchedTo);
      JsonObject formatsObject = new JsonObject();
      this.formats.forEach((visualType, text) -> formatsObject.addProperty(visualType.name(), text));
      jsonObject.add("formats", formatsObject);
      jsonObject.addProperty("editingNametags", this.editingNametags);
      return jsonObject;
   }

   @Override
   public boolean isSwitchedTo() {
      return this.switchedTo;
   }

   @Override
   public void setSwitchedTo(boolean flag) {
      this.switchedTo = flag;
      this.sync();
   }

   public void setFormat(VisualType visualType, String text) {
      this.formats.put(visualType, Text.color(text));
      this.sync();
   }

   public String getFormat(VisualType visualType) {
      String text = this.formats.get(visualType);
      return text.equalsIgnoreCase("none") ? null : text;
   }

   public void resetFormat(VisualType visualType) {
      this.setFormat(visualType, visualType.getDefaultFormat());
   }

   public boolean isEditingNametags() {
      return this.editingNametags;
   }

   public void setEditingNametags(boolean flag) {
      this.editingNametags = flag;
      this.sync();
   }
}
