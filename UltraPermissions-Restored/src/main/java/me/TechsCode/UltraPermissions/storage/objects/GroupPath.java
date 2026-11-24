package me.TechsCode.UltraPermissions.storage.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.item.ItemSerializer;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class GroupPath extends Storable {
   private String uid = UUID.randomUUID().toString().substring(0, 6);
   private int priority = UltraPermissions.getAPI().getGroupPaths().size() + 1;
   private String name;
   private String description;
   private ArrayList<Group> groupPath;
   private ItemStack icon;
   private boolean enabled;

   public GroupPath(String name) {
      this.name = name;
      this.description = "";
      this.groupPath = new ArrayList<>();
      this.icon = XMaterial.GREEN_STAINED_GLASS_PANE.getAsItemStack().get();
      this.enabled = true;
   }

   @Override
   public String getKey() {
      return this.uid;
   }

   @Override
   public void setKey(String key) {
      this.uid = key;
      this.sync();
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.name);
      jsonObject.addProperty("description", this.description);
      JsonArray jsonArray = new JsonArray();
      this.groupPath.forEach(group -> jsonArray.add(group.getKey()));
      TechPlugin.getInstance().log("GroupPath: " + jsonArray);
      jsonObject.add("groupPath", jsonArray);
      jsonObject.addProperty("icon", ItemSerializer.itemTo64(this.icon));
      jsonObject.addProperty("enabled", this.enabled);
      return jsonObject;
   }

   @Override
   public void setState(JsonObject jsonObject, TechPlugin<?> plugin) {
      this.name = jsonObject.get("name").getAsString();
      this.description = jsonObject.get("description").getAsString();
      JsonArray jsonArray = jsonObject.has("groupPath") ? jsonObject.get("groupPath").getAsJsonArray() : new JsonArray();
      ArrayList<Group> groupPath = new ArrayList<>();
      jsonArray.forEach(element -> {
         if (element.isJsonPrimitive()) {
            String asString = element.getAsString();
            Optional<Group> optional = UltraPermissions.getAPI().getGroups().key(asString);
            optional.ifPresent(groupPath::add);
         }
      });
      this.groupPath = groupPath;

      try {
         this.icon = ItemSerializer.itemFrom64(jsonObject.get("icon").getAsString());
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }

      this.enabled = jsonObject.get("enabled").getAsBoolean();
   }

   @Override
   public void onMount(TechPlugin<?> plugin) {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
      this.sync();
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
      this.sync();
   }

   public ArrayList<Group> getGroupPath() {
      return this.groupPath;
   }

   public void setGroupPath(ArrayList<Group> groupPath) {
      this.groupPath = groupPath;
      this.sync();
   }

   public void addGroupToGroupPath(Group group) {
      this.groupPath.add(group);
      this.sync();
   }

   public void removeGroupFromGroupPath(Group group) {
      this.groupPath.remove(group);
      this.sync();
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public void setIcon(ItemStack icon) {
      this.icon = icon;
      this.sync();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      this.sync();
   }

   public int getPriority() {
      return this.priority;
   }

   public void setPriority(int priority) {
      this.priority = priority;
      this.sync();
   }

   public void delete() {
      this.destroy();
   }

   @Override
   public void sync() {
      super.sync();
   }
}
