package com.Zrips.CMI.Modules.BlueMap;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.markers.POIMarker.Builder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.World;

public class BlueMapManager {
  private CMI plugin;
  private BlueMapAPI blueMapAPI = null;
  private HashMap<World, MarkerSet> markerSetMap = new HashMap<>();
  private boolean warpsEnabled = true;
  private boolean hiddenByDefault = true;
  private String warpsLabel = "CMIWarps";
  private String warpsIcon = "https://www.zrips.net/ICON_Warp.png";
  private int iconAnchor = 16;
  private List<String> blackList = new ArrayList<>();

  public BlueMapManager(CMI cmi) {
    this.plugin = cmi;
    this.init();
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "BlueMap.Warps.Enabled",
        new String[] {
          "Do you want to show warps in BlueMap?",
          "For most settings to take effect you need to restart the server"
        });
    this.warpsEnabled = configReader.get("BlueMap.Warps.Enabled", true);
    configReader.addComment(
        "BlueMap.Warps.ToggledOffByDefault",
        new String[] {"Should warp points be toggled off by default in BlueMap"});
    this.hiddenByDefault = configReader.get("BlueMap.Warps.ToggledOffDefault", false);
    configReader.addComment("BlueMap.Warps.Label", new String[] {"Name of the markers in BlueMap"});
    this.warpsLabel = configReader.get("BlueMap.Warps.Label", "CMIWarps");
    configReader.addComment(
        "BlueMap.Warps.Icon",
        new String[] {
          "Path to an icon image. Make sure to have correctly sized icon which normally should be"
              + " around 32x32 pixels"
        });
    this.warpsIcon = configReader.get("BlueMap.Warps.Icon", "https://www.zrips.net/ICON_Warp.png");
    configReader.addComment(
        "BlueMap.Warps.IconAnchor",
        new String[] {"This should generally be half the size of icon's size"});
    this.iconAnchor = configReader.get("BlueMap.Warps.IconAnchor", 16);
    configReader.addComment(
        "BlueMap.Warps.BlackList",
        new String[] {
          "List of warp names we should not show. Keep in mind that hidden warps are not shown by"
              + " default already"
        });
    this.blackList = configReader.get("BlueMap.Warps.BlackList", this.blackList);
    CMIList.toLowerCase(this.blackList);

    for (String string : this.blackList) {
      this.removeWarp(this.plugin.getWarpManager().getWarp(string));
    }
  }

  private void init() {
    if (CMIHookType.BlueMap.isPresent()) {
      BlueMapAPI.onEnable(
          value -> {
            this.blueMapAPI = (BlueMapAPI) value;
            if (this.warpsEnabled) {
              CMIScheduler.runTaskLater(this.plugin, () -> this.addAllWarpMarkers(), 600L);
              this.plugin
                  .getServer()
                  .getPluginManager()
                  .registerEvents(new BlueMapListener(this.plugin), this.plugin);
            }
          });
    }
  }

  public void addAllWarpMarkers() {
    for (CmiWarp cmiWarp : this.plugin.getWarpManager().getWarps().values()) {
      this.addWarp(cmiWarp);
    }
  }

  public void addWarp(CmiWarp cmiWarp) {
    if (cmiWarp != null) {
      this.addWarp(cmiWarp, cmiWarp.getLoc());

      for (CMILocation cmiLocation : cmiWarp.getLocations()) {
        this.addWarp(cmiWarp, cmiLocation);
      }
    }
  }

  public void removeWarp(CmiWarp cmiWarp) {
    if (cmiWarp != null) {
      this.removeWarp(cmiWarp, cmiWarp.getLoc());

      for (CMILocation cmiLocation : cmiWarp.getLocations()) {
        this.removeWarp(cmiWarp, cmiLocation);
      }
    }
  }

  public void removeWarp(CmiWarp cmiWarp, Location location) {
    if (cmiWarp != null && !cmiWarp.isHidden()) {
      MarkerSet value = this.markerSetMap.get(location.getWorld());
      if (value != null) {
        value.getMarkers().remove(cmiWarp.getName());
      }
    }
  }

  public void addWarp(CmiWarp cmiWarp, CMILocation cmiLocation) {
    if (cmiWarp != null
        && this.warpsEnabled
        && !cmiWarp.isHidden()
        && !this.blackList.contains(cmiWarp.getName().toLowerCase())) {
      if (cmiLocation.getWorld() != null) {
        Builder poiBuilder =
            (Builder)
                (Object)
                    new Builder()
                        .position(cmiLocation.getX(), cmiLocation.getY(), cmiLocation.getZ());
        poiBuilder = (Builder) (Object) poiBuilder.label(cmiWarp.getName());
        poiBuilder = (Builder) (Object) poiBuilder.maxDistance(1000.0);
        POIMarker value = poiBuilder.icon(this.warpsIcon, this.iconAnchor, this.iconAnchor).build();
        MarkerSet markerSet =
            this.markerSetMap.computeIfAbsent(
                cmiLocation.getWorld(),
                world ->
                    MarkerSet.builder()
                        .label(this.warpsLabel)
                        .defaultHidden(this.hiddenByDefault)
                        .build());
        markerSet.getMarkers().put(cmiWarp.getName(), value);
        this.blueMapAPI
            .getWorld(cmiLocation.getWorld())
            .ifPresent(
                blueMapInstance -> {
                  for (BlueMapMap bmMap :
                      (java.util.Collection<BlueMapMap>)
                          ((BlueMapWorld) blueMapInstance).getMaps()) {
                    bmMap.getMarkerSets().put(this.warpsLabel, markerSet);
                  }
                });
      }
    }
  }
}
