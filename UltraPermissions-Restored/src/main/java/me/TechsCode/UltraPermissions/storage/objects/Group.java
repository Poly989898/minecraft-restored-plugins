package me.TechsCode.UltraPermissions.storage.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.base.visual.Text;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.TechsCode.UltraPermissions.storage.PermissionCreator;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.visual.VisualType;

public class Group extends Storable implements PermissionHolder {
   private StorageController controller;
   private String id;
   private String name;
   private String world;
   private String server;
   private int priority;
   private Set<Stored<Group>> inheritedGroups;
   private boolean isDefault;
   private String prefix;
   private String suffix;
   private String icon;
   private int damage;
   private int modelId;
   private Map<Stored<Group>, Long> grantGroups;
   private String chatFormat;

   public Group(
      String text,
      String name,
      String world,
      String server,
      int count,
      Set<Stored<Group>> storeds,
      boolean flag,
      String prefix,
      String suffix,
      String icon,
      Map<Stored<Group>, Long> map
   ) {
      this.id = text;
      this.name = name;
      this.world = world;
      this.server = server;
      this.priority = count;
      this.inheritedGroups = storeds;
      this.isDefault = flag;
      this.prefix = prefix;
      this.suffix = suffix;
      this.icon = icon;
      this.damage = 0;
      this.modelId = 0;
      this.chatFormat = "none";
      this.grantGroups = map;
   }

   public Group(JsonObject jsonObject) {
      this.name = jsonObject.get("name").getAsString();
      this.world = jsonObject.has("world") ? (jsonObject.get("world").isJsonNull() ? null : jsonObject.get("world").getAsString()) : null;
      this.server = jsonObject.has("serverId") ? (jsonObject.get("serverId").isJsonNull() ? null : jsonObject.get("serverId").getAsString()) : null;
      this.priority = jsonObject.get("priority").getAsInt();
      if (jsonObject.has("inherits")) {
         this.inheritedGroups = Arrays.stream(jsonObject.get("inherits").getAsString().split(";"))
            .filter(text -> text.length() != 0)
            .map(text -> new Stored<Group>(text, this.controller::getGroupStorage))
            .collect(Collectors.toSet());
      } else {
         this.inheritedGroups = new HashSet<>();
      }

      this.isDefault = jsonObject.get("default").getAsBoolean();
      this.prefix = jsonObject.has("prefix") ? Text.color(jsonObject.get("prefix").getAsString()) : null;
      this.suffix = jsonObject.has("suffix") ? Text.color(jsonObject.get("suffix").getAsString()) : null;
      this.icon = jsonObject.get("icon").getAsString();
      if (jsonObject.get("damage") == null || jsonObject.get("modelId") == null) {
         jsonObject.addProperty("damage", this.damage);
         jsonObject.addProperty("modelId", this.modelId);
      }

      this.damage = jsonObject.get("damage").getAsInt();
      this.modelId = jsonObject.get("modelId").getAsInt();
      this.chatFormat = jsonObject.get("chatFormat") == null ? "none" : jsonObject.get("chatFormat").getAsString();
      this.grantGroups = new HashMap<>();
      if (jsonObject.get("grantGroups") == null) {
         JsonObject grantGroupsJson = new JsonObject();
         jsonObject.add("grantGroups", grantGroupsJson);
      }

      if (jsonObject.has("grantGroups")) {
         jsonObject.get("grantGroups")
            .getAsJsonObject()
            .entrySet()
            .forEach(
               entry -> this.grantGroups
                  .put(new Stored<>((String)entry.getKey(), this.controller::getGroupStorage), ((JsonElement)entry.getValue()).getAsLong())
            );
      }
   }

   public List<UserRankup> getRankups() {
      return this.controller
         .getUserStorage()
         .getUsers()
         .stream()
         .flatMap(user -> user.getRankups().stream())
         .filter(userRankup -> userRankup.getGroup().equals(this.toStored()))
         .collect(Collectors.toList());
   }

   @Override
   public String getName() {
      return this.name;
   }

   public void setName(String text) {
      this.name = text;
      this.sync();
   }

   public Optional<String> getWorld() {
      return Optional.ofNullable(this.world);
   }

   public void setWorld(String text) {
      this.world = text;
      this.sync();
   }

   public Optional<String> getServer() {
      return Optional.ofNullable(this.server);
   }

   public void setServer(String text) {
      this.server = text;
      this.sync();
   }

   public int getPriority() {
      return this.priority;
   }

   public void setPriority(int count) {
      this.priority = count;
      this.sync();
   }

   public Set<Stored<Group>> getInheritedGroups() {
      return this.inheritedGroups;
   }

   public GroupList getActiveInheritedGroups() {
      return this.inheritedGroups.stream().map(stored -> stored.get().orElse(null)).filter(Objects::nonNull).collect(Collectors.toCollection(GroupList::new));
   }

   public void addInheritance(Group group) {
      this.inheritedGroups.add(group.toStored());
      this.sync();
   }

   public void removeInheritance(Group group) {
      this.inheritedGroups.remove(group.toStored());
      this.sync();
   }

   public boolean isDefault() {
      return this.isDefault;
   }

   public void setDefault(boolean flag) {
      this.isDefault = flag;
      this.sync();
   }

   @Override
   public Optional<String> getPrefix() {
      return Optional.ofNullable(this.prefix);
   }

   @Override
   public void setPrefix(String text) {
      this.prefix = text != null ? Text.color(text) : null;
      this.sync();
   }

   @Override
   public Optional<String> getSuffix() {
      return Optional.ofNullable(this.suffix);
   }

   @Override
   public void setSuffix(String text) {
      this.suffix = text != null ? Text.color(text) : null;
      this.sync();
   }

   public XMaterial getIcon() {
      return XMaterial.valueOf(this.icon);
   }

   public void setIcon(XMaterial xMaterial) {
      this.icon = xMaterial.name();
      this.sync();
   }

   @Override
   public PermissionList getPermissions() {
      return this.controller.getPermissionStorage().getPermissions().holder(Holder.fromGroup(this));
   }

   @Override
   public PermissionCreator newPermission(String text) {
      return this.controller.getPermissionStorage().newPermission(text, Holder.fromGroup(this));
   }

   public UserList getUsers() {
      return this.controller.getUserStorage().getUsers().usersInGroup(this.toStored());
   }

   public void remove() {
      this.controller.getGroupStorage().getGroups().forEach(group -> group.removeInheritance(this));
      this.getUsers().forEach(user -> user.removeGroup(this));
      this.getPermissions().forEach(Permission::remove);
      this.destroy();
   }

   public PermissionList getInheritedPermissions() {
      return this.controller.getPermissionStorage().getPermissions().holder(this.getActiveInheritedGroups().getWithRecursiveInherits().holders());
   }

   @Override
   public PermissionList getAdditionalPermissions() {
      return this.getInheritedPermissions();
   }

   public Group createCopy() {
      return new Group(
         this.id,
         this.name,
         this.world,
         this.server,
         this.priority,
         this.inheritedGroups,
         this.isDefault,
         this.prefix,
         this.suffix,
         this.icon,
         this.grantGroups
      );
   }

   @Override
   public String getKey() {
      return this.id + "";
   }

   @Override
   public void setKey(String text) {
      this.id = String.valueOf(text);
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.name);
      if (this.world != null) {
         jsonObject.addProperty("world", this.world);
      }

      if (this.server != null) {
         jsonObject.addProperty("serverId", this.server);
      }

      jsonObject.addProperty("priority", this.priority);
      if (this.inheritedGroups.size() != 0) {
         String text = this.inheritedGroups.stream().map(Stored::getKey).collect(Collectors.joining(";"));
         jsonObject.addProperty("inherits", text);
      }

      jsonObject.addProperty("default", this.isDefault);
      if (this.prefix != null) {
         jsonObject.addProperty("prefix", this.prefix);
      }

      if (this.suffix != null) {
         jsonObject.addProperty("suffix", this.suffix);
      }

      if (this.icon != null) {
         jsonObject.addProperty("icon", this.icon);
      }

      jsonObject.addProperty("damage", this.damage);
      jsonObject.addProperty("modelId", this.modelId);
      jsonObject.addProperty("chatFormat", this.chatFormat);
      JsonObject grantGroupsJson = new JsonObject();
      this.grantGroups.forEach((stored, value) -> grantGroupsJson.addProperty(stored.getKey(), value));
      jsonObject.add("grantGroups", grantGroupsJson);
      return jsonObject;
   }

   @Override
   public void setState(JsonObject jsonObject, TechPlugin techPlugin) {
      StorageController storageController = (StorageController)techPlugin;
      this.name = jsonObject.get("name").getAsString();
      this.world = jsonObject.has("world") ? (jsonObject.get("world").isJsonNull() ? null : jsonObject.get("world").getAsString()) : null;
      this.server = jsonObject.has("serverId") ? (jsonObject.get("serverId").isJsonNull() ? null : jsonObject.get("serverId").getAsString()) : null;
      this.priority = jsonObject.get("priority").getAsInt();
      if (jsonObject.has("inherits")) {
         this.inheritedGroups = Arrays.stream(jsonObject.get("inherits").getAsString().split(";"))
            .filter(text -> text.length() != 0)
            .map(text -> new Stored<Group>(text, storageController::getGroupStorage))
            .collect(Collectors.toSet());
      } else {
         this.inheritedGroups = new HashSet<>();
      }

      this.isDefault = jsonObject.get("default").getAsBoolean();
      this.prefix = jsonObject.has("prefix") ? Text.color(jsonObject.get("prefix").getAsString()) : null;
      this.suffix = jsonObject.has("suffix") ? Text.color(jsonObject.get("suffix").getAsString()) : null;
      this.icon = jsonObject.get("icon").getAsString();
      if (jsonObject.get("damage") == null || jsonObject.get("modelId") == null) {
         jsonObject.addProperty("damage", this.damage);
         jsonObject.addProperty("modelId", this.modelId);
      }

      this.damage = jsonObject.get("damage").getAsInt();
      this.modelId = jsonObject.get("modelId").getAsInt();
      this.chatFormat = jsonObject.get("chatFormat") == null ? "none" : jsonObject.get("chatFormat").getAsString();
      this.grantGroups = new HashMap<>();
      if (jsonObject.get("grantGroups") == null) {
         JsonObject grantGroupsJson = new JsonObject();
         jsonObject.add("grantGroups", grantGroupsJson);
      }

      if (jsonObject.has("grantGroups")) {
         jsonObject.get("grantGroups")
            .getAsJsonObject()
            .entrySet()
            .forEach(entry -> this.grantGroups.put(new Stored<>((String)entry.getKey(), storageController::getGroupStorage), ((JsonElement)entry.getValue()).getAsLong()));
      }
   }

   @Override
   public void onMount(TechPlugin techPlugin) {
      this.controller = (StorageController)techPlugin;
   }

   public void clean() {
      for (Stored stored : new ArrayList<>(this.inheritedGroups)) {
         if (!stored.isPresent()) {
            this.inheritedGroups.remove(stored);
            this.sync();
         }
      }
   }

   public Stored<Group> toStored() {
      return new Stored<>(this.getKey(), () -> (Storage<Group>)this.storage);
   }

   public void _justDestroy() {
      this.destroy();
   }

   public void setFormat(String text) {
      this.chatFormat = text;
      this.sync();
   }

   public String getFormat() {
      return this.chatFormat.equalsIgnoreCase("none") ? null : this.chatFormat;
   }

   public void resetFormat() {
      this.chatFormat = VisualType.CHAT.getDefaultFormat();
      this.sync();
   }

   public Map<Stored<Group>, Long> getGrantGroups() {
      return this.grantGroups;
   }

   public void setGrantGroups(Map<Stored<Group>, Long> map) {
      this.grantGroups = map;
      this.sync();
   }

   public void addGrantGroup(Group group) {
      this.grantGroups.put(group.toStored(), 0L);
      this.sync();
   }

   public void addGrantGroup(Group group, long value) {
      this.grantGroups.put(group.toStored(), value);
      this.sync();
   }

   public void removeGrantGroup(Group group) {
      this.grantGroups.remove(group.toStored());
      this.sync();
   }

   public void setDamage(int count) {
      this.damage = count;
      this.sync();
   }

   public void setModelId(int count) {
      this.modelId = count;
      this.sync();
   }

   public int getDamage() {
      return this.damage;
   }

   public int getModelId() {
      return this.modelId;
   }
}
