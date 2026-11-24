package me.TechsCode.UltraPermissions.base.addons.cloud;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public abstract class CloudDataFetcher extends Thread {
   private LinkedTreeMap<String, CloudAddon> addons;
   private boolean running;
   private long lastFetch = -1L;
   private boolean online;

   public CloudDataFetcher() {
      this.addons = new LinkedTreeMap();
      this.running = true;
      this.online = false;
      this.start();
   }

   public abstract String getUrl();

   public abstract CloudFileType getFileType();

   @Override
   public void run() {
      while (this.running) {
         this.fetch();

         try {
            sleep(TimeUnit.MINUTES.toMillis(45L));
         } catch (InterruptedException exception) {
            exception.printStackTrace();
         }
      }
   }

   public void onDisable() {
      this.running = false;
   }

   private void fetch() {
      try {
         this.addons = (LinkedTreeMap<String, CloudAddon>)new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .fromJson(IOUtils.toString(new URL(this.getUrl()), StandardCharsets.UTF_8), (new TypeToken<Map<String, CloudAddon>>() {}).getType());
         this.lastFetch = System.currentTimeMillis();
         this.online = true;
      } catch (Exception exception) {
         this.online = false;
      }
   }

   public boolean isOnline() {
      return this.online;
   }

   public long getLastFetch() {
      return this.lastFetch;
   }

   public List<CloudAddon> getAddons() {
      return new ArrayList<>(this.addons.values());
   }
}
