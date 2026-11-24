package me.TechsCode.UltraPermissions.storage.objects;

import java.util.Optional;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import com.google.gson.JsonObject;
import me.TechsCode.UltraPermissions.internal.Node;

public class Permission extends Storable {
   private int id;
   private Holder holder;
   private String world;
   private String server;
   private String permission;
   private boolean positive;
   private long expiration;

   public Permission(int count, Holder holder, String text, String server, String permission, boolean flag, long value) {
      this.id = count;
      this.holder = holder;
      this.world = text;
      this.server = server;
      this.permission = permission;
      this.positive = flag;
      this.expiration = value;
   }

   public int getId() {
      return this.id;
   }

   public Optional<String> getServer() {
      return Optional.ofNullable(this.server);
   }

   public Optional<String> getWorld() {
      return Optional.ofNullable(this.world);
   }

   public Holder getHolder() {
      return this.holder;
   }

   public String getName() {
      return this.permission;
   }

   public void setName(String text) {
      this.permission = text;
      this.sync();
   }

   public void setWorld(String text) {
      this.world = text;
      this.sync();
   }

   public void setServer(String text) {
      this.server = text;
      this.sync();
   }

   public String getDisplayName() {
      return (this.isPositive() ? "" : "-") + this.getName();
   }

   public boolean isPositive() {
      return this.positive;
   }

   public boolean isPermanent() {
      return this.expiration == 0L;
   }

   public void setPositive(boolean flag) {
      this.positive = flag;
      this.sync();
   }

   public long getExpiration() {
      return this.expiration;
   }

   public void setExpiration(long value) {
      this.expiration = value;
      this.sync();
   }

   public void remove() {
      this.destroy();
   }

   public boolean isBungeePermission() {
      return this.server != null && this.server.equals("BungeeCord");
   }

   public boolean isExpired() {
      return this.expiration != 0L && this.expiration < System.currentTimeMillis();
   }

   public void copyTo(PermissionHolder permissionHolder) {
      permissionHolder.newPermission(this.permission).setServer(this.server).setExpiration(this.expiration).setWorld(this.world).setPositive(this.positive).create();
   }

   public Node asNode() {
      return new Node(this.permission);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof Permission permission) ? false : permission.id == this.id;
   }

   public Permission createCopy() {
      return new Permission(this.id, this.holder, this.world, this.server, this.permission, this.positive, this.expiration);
   }

   @Override
   public String getKey() {
      return this.id + "";
   }

   @Override
   public void setKey(String text) {
      this.id = Integer.parseInt(text);
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("holder", this.holder.toStored().getKey());
      if (this.world != null) {
         jsonObject.addProperty("world", this.world);
      }

      if (this.server != null) {
         jsonObject.addProperty("serverId", this.server);
      }

      jsonObject.addProperty("permission", this.permission);
      jsonObject.addProperty("positive", this.positive);
      jsonObject.addProperty("expiry", this.expiration);
      return jsonObject;
   }

   @Override
   public void setState(JsonObject jsonObject, TechPlugin techPlugin) {
      this.holder = Holder.fromKey(jsonObject.get("holder"), techPlugin);
      this.world = jsonObject.has("world") ? jsonObject.get("world").getAsString() : null;
      this.server = jsonObject.has("serverId") && !jsonObject.get("serverId").isJsonNull() ? jsonObject.get("serverId").getAsString() : null;
      this.permission = jsonObject.get("permission").getAsString();
      this.positive = jsonObject.get("positive").getAsBoolean();
      this.expiration = jsonObject.get("expiry").getAsLong();
   }

   @Override
   public void onMount(TechPlugin techPlugin) {
   }

   public void _justDestroy() {
      this.destroy();
   }
}
