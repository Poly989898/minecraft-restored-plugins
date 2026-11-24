package me.TechsCode.UltraPermissions.base.addons.cloud;

import com.google.gson.annotations.Expose;

public class CloudAddon {
   @Expose
   private String name;
   @Expose
   private String author;
   @Expose
   private String description;
   @Expose
   private String version;
   @Expose
   private String url;
   @Expose
   private String material;
   @Expose
   private int id;
   @Expose
   private int downloads;
   @Expose
   private boolean verified;

   public CloudAddon() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAuthor() {
      return this.author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getMaterial() {
      return this.material;
   }

   public void setMaterial(String material) {
      this.material = material;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getDownloads() {
      return this.downloads;
   }

   public void setDownloads(int downloads) {
      this.downloads = downloads;
   }

   public boolean isVerified() {
      return this.verified;
   }

   public void setVerified(boolean verified) {
      this.verified = verified;
   }
}
