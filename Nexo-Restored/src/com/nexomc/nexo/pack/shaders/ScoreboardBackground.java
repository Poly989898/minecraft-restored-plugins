package com.nexomc.nexo.pack.shaders;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.NexoOverlay;
import com.nexomc.nexo.utils.StringUtilsKt;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.overlay.Overlay;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\f\u001a\u00020\rJ\u001c\u0010\u000e\u001a\u00020\r*\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/pack/shaders/ScoreboardBackground;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "classLoader", "Ljava/lang/ClassLoader;", "kotlin.jvm.PlatformType", "scoreboard", "", "tablist", "generateFiles", "", "writables", "Lteam/unnamed/creative/overlay/Overlay;", "version", "", "fileName", "com.nexomc:core"}
)
public final class ScoreboardBackground {
   @NotNull
   private final ResourcePack resourcePack;
   private final ClassLoader classLoader;
   private final boolean scoreboard;
   private final boolean tablist;

   public ScoreboardBackground(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.classLoader = NexoPlugin.Companion.instance().getClass().getClassLoader();
      this.scoreboard = Settings.HIDE_SCOREBOARD_BACKGROUND.toBool();
      this.tablist = Settings.HIDE_TABLIST_BACKGROUND.toBool();
   }

   public final void generateFiles() {
      if (this.scoreboard || this.tablist) {
         this.writables(NexoOverlay.V1_21_1.getOverlay(), "v1_21_1", "rendertype_gui.vsh");
         this.writables(NexoOverlay.V1_21_3.getOverlay(), "v1_21_1", "rendertype_gui.vsh");
         this.writables(NexoOverlay.V1_21_4.getOverlay(), "v1_21_1", "rendertype_gui.vsh");
         this.writables(NexoOverlay.V1_21_5.getOverlay(), "v1_21_1", "rendertype_gui.vsh");
         this.writables(NexoOverlay.V1_21_6.getOverlay(), "v1_21_6", "gui.vsh");
         this.writables(NexoOverlay.V1_21_11.getOverlay(), "v1_21_6", "gui.vsh");
         this.writables(NexoOverlay.V26_1.getOverlay(), "v1_21_6", "gui.vsh");
      }

   }

   private final void writables(Overlay overlay, String version, String fileName) {
      try {
      String toUTF8String = Writable.resource(this.classLoader, "shaders/score_tab/" + version + "/" + fileName).toUTF8String();
      boolean condition = this.scoreboard;
      String str;
      if (condition) {
         int value = 0;
         str = StringUtilsKt.remove(toUTF8String, "//SCOREBOARD ");
      } else {
         str = toUTF8String;
      }

      toUTF8String = str;
      condition = this.tablist;
      if (condition) {
         int value2 = 0;
         str = StringUtilsKt.remove(toUTF8String, "//TABLIST ");
      } else {
         str = toUTF8String;
      }

      String shader = str;
      overlay.unknownFile("assets/minecraft/shaders/core/" + fileName, Writable.stringUtf8(shader));
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }
}
