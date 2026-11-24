package me.TechsCode.UltraPermissions.storage.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.legacy.SkinTexture;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.base.visual.Text;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.TechsCode.UltraPermissions.storage.PermissionCreator;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.RankupList;

public class User extends Storable implements PermissionHolder {
   private StorageController controller;
   private UUID uuid;
   private String playerName;
   private boolean superadmin;
   private Map<Stored<Group>, Long> rankups;
   private String prefix;
   private String suffix;
   private SkinTexture skinTexture;

   public User(UUID uuid, String text, boolean flag, Map<Stored<Group>, Long> map, String prefix, String suffix, SkinTexture skinTexture) {
      this.uuid = uuid;
      this.playerName = text;
      this.superadmin = flag;
      this.rankups = map;
      this.prefix = prefix;
      this.suffix = suffix;
      this.skinTexture = skinTexture;
   }

   public RankupList getRankups() {
      return this.rankups
         .entrySet()
         .stream()
         .map(entry -> new UserRankup(this, entry.getKey(), entry.getValue()))
         .collect(Collectors.toCollection(RankupList::new))
         .bestToWorst();
   }

   public UUID getUuid() {
      return this.uuid;
   }

   @Override
   public String getName() {
      return this.playerName;
   }

   public void setPlayerName(String text) {
      this.playerName = text;
      this.sync();
   }

   public boolean isSuperadmin() {
      return this.superadmin;
   }

   public void setSuperadmin(boolean flag) {
      this.superadmin = flag;
      this.sync();
   }

   public void addGroup(Group group) {
      this.rankups.put(group.toStored(), 0L);
      this.sync();
   }

   public void addGroup(Group group, long value) {
      this.rankups.put(group.toStored(), value);
      this.sync();
   }

   public long getGroupExpiry(Stored<Group> stored) {
      return this.rankups.get(stored);
   }

   public Set<Stored<Group>> getGroups() {
      return this.rankups.keySet();
   }

   public GroupList getActiveGroups() {
      return this.rankups
         .keySet()
         .stream()
         .map(stored -> stored.get().orElse(null))
         .filter(Objects::nonNull)
         .collect(Collectors.toCollection(GroupList::new))
         .bestToWorst();
   }

   public Optional<SkinTexture> getSkinTexture() {
      return Optional.ofNullable(this.skinTexture);
   }

   public void setSkinTexture(SkinTexture skinTexture) {
      if (skinTexture != null && (this.skinTexture == null || !this.skinTexture.getUrl().equalsIgnoreCase(skinTexture.getUrl()))) {
         this.skinTexture = skinTexture;
         this.sync();
      }
   }

   public void removeGroup(Group group) {
      if (this.rankups.containsKey(group.toStored())) {
         this.rankups.remove(group.toStored());
         this.sync();
      }
   }

   public boolean hasPrefix() {
      return this.getPrefix().isPresent();
   }

   @Override
   public Optional<String> getPrefix() {
      return Optional.ofNullable(this.prefix);
   }

   public boolean hasSuffix() {
      return this.getSuffix().isPresent();
   }

   @Override
   public Optional<String> getSuffix() {
      return Optional.ofNullable(this.suffix);
   }

   @Override
   public void setPrefix(String text) {
      this.prefix = text != null ? Text.color(text) : null;
      this.sync();
   }

   @Override
   public void setSuffix(String text) {
      this.suffix = text != null ? Text.color(text) : null;
      this.sync();
   }

   @Override
   public PermissionCreator newPermission(String text) {
      return this.controller.getPermissionStorage().newPermission(text, Holder.fromUser(this));
   }

   @Override
   public PermissionList getPermissions() {
      return this.controller.getPermissionStorage().getPermissions().holder(Holder.fromUser(this));
   }

   public void remove() {
      this.getPermissions().forEach(Permission::remove);
      this.destroy();
   }

   @Override
   public PermissionList getAdditionalPermissions() {
      return this.controller.getPermissionStorage().getPermissions().holder(this.getActiveGroups().getWithRecursiveInherits().holders());
   }

   public User createCopy() {
      return new User(this.uuid, this.playerName, this.superadmin, this.rankups, this.prefix, this.suffix, this.skinTexture);
   }

   @Override
   public String getKey() {
      return this.uuid.toString();
   }

   @Override
   public void setKey(String text) {
      this.uuid = UUID.fromString(text);
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.playerName);
      jsonObject.addProperty("superadmin", this.superadmin);
      JsonObject groupsJson = new JsonObject();
      this.rankups.forEach((stored, value) -> groupsJson.addProperty(stored.getKey(), value));
      if (this.rankups.size() != 0) {
         jsonObject.add("groups", groupsJson);
      }

      if (this.prefix != null) {
         jsonObject.addProperty("prefix", this.prefix);
      }

      if (this.suffix != null) {
         jsonObject.addProperty("suffix", this.suffix);
      }

      if (this.skinTexture != null) {
         jsonObject.addProperty("skull", this.skinTexture.toString());
      }

      return jsonObject;
   }

   @Override
   public void setState(JsonObject jsonObject, TechPlugin techPlugin) {
      StorageController storageController = (StorageController)techPlugin;
      this.playerName = jsonObject.get("name").getAsString();
      this.superadmin = jsonObject.get("superadmin").getAsBoolean();
      this.rankups = new HashMap<>();
      if (jsonObject.has("groups")) {
         jsonObject.get("groups")
            .getAsJsonObject()
            .entrySet()
            .forEach(entry -> this.rankups.put(new Stored<>((String)entry.getKey(), storageController::getGroupStorage), ((JsonElement)entry.getValue()).getAsLong()));
      }

      this.prefix = jsonObject.has("prefix") ? Text.color(jsonObject.get("prefix").getAsString()) : null;
      this.suffix = jsonObject.has("suffix") ? Text.color(jsonObject.get("suffix").getAsString()) : null;
      this.skinTexture = jsonObject.has("skull") && !jsonObject.get("skull").getAsString().contains("null:split:")
         ? new SkinTexture(jsonObject.get("skull").getAsString())
         : null;
   }

   @Override
   public void onMount(TechPlugin techPlugin) {
      this.controller = (StorageController)techPlugin;
   }

   public void clean() {
      for (Stored stored : new ArrayList<>(this.rankups.keySet())) {
         if (stored != null & !stored.isPresent()) {
            this.rankups.remove(stored);
            this.sync();
         }
      }
   }

   public Stored<User> toStored() {
      return new Stored<>(this.getKey(), () -> (Storage<User>)this.storage);
   }

   public void _justDestroy() {
      this.destroy();
   }
}
