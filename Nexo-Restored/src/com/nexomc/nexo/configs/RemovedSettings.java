package com.nexomc.nexo.configs;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\bX\b\u0086\u0081\u0002\u0018\u0000 \\2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\\B%\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\u0005\"\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\n\u0010[\u001a\u00020\u0003H\u0096\u0080\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0019\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\n\n\u0002\u0010Z\u001a\u0004\bX\u0010Yj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b j\u0002\b!j\u0002\b\"j\u0002\b#j\u0002\b$j\u0002\b%j\u0002\b&j\u0002\b'j\u0002\b(j\u0002\b)j\u0002\b*j\u0002\b+j\u0002\b,j\u0002\b-j\u0002\b.j\u0002\b/j\u0002\b0j\u0002\b1j\u0002\b2j\u0002\b3j\u0002\b4j\u0002\b5j\u0002\b6j\u0002\b7j\u0002\b8j\u0002\b9j\u0002\b:j\u0002\b;j\u0002\b<j\u0002\b=j\u0002\b>j\u0002\b?j\u0002\b@j\u0002\bAj\u0002\bBj\u0002\bCj\u0002\bDj\u0002\bEj\u0002\bFj\u0002\bGj\u0002\bHj\u0002\bIj\u0002\bJj\u0002\bKj\u0002\bLj\u0002\bMj\u0002\bNj\u0002\bOj\u0002\bPj\u0002\bQj\u0002\bRj\u0002\bSj\u0002\bTj\u0002\bUj\u0002\bVj\u0002\bW¨\u0006]"},
   d2 = {"Lcom/nexomc/nexo/configs/RemovedSettings;", "", "path", "", "subPaths", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;[Ljava/lang/String;)V", "getPath", "()Ljava/lang/String;", "ATTEMPT_TO_MIGRATE_DUPLICATES", "AUTOMATICALLY_SET_MODEL_DATA", "AUTOMATICALLY_SET_GLYPH_CODE", "MERGE_FONTS", "AUTO_UPDATE_ITEMS", "OVERRIDE_LORE", "UPDATE_FURNITURE", "UPDATE_FURNITURE_ON_RELOAD", "UPDATE_FURNITURE_ON_LOAD", "FURNITURE_UPDATE_DELAY", "FURNITURE_UPDATE_DELAY2", "UPDATE_FURNITURE_ON_LOAD2", "UPDATE_FURNITURE_ON_RELOAD2", "SEND_PACK_ADVANCED", "SHIELD_DISPLAY", "BOW_DISPLAY", "CROSSBOW_DISPLAY", "GENERATE_ATLAS_FILE", "EXCLUDE_MALFORMED_ATLAS", "ATLAS_GENERATION_TYPE", "ARMOR_EQUIP_EVENT_BYPASS", "UPLOAD_TYPE", "UPLOAD", "UPLOAD_OPTIONS", "POLYMATH_SERVER", "EXPERIMENTAL_FIX_BROKEN_FURNITURE", "EXPERIMENTAL_FURNITURE_TYPE_UPDATE", "SEND_JOIN_MESSAGE", "JOIN_MESSAGE_DELAY", "GENERATE_DEFAULT_ASSETS", "VERIFY_PACK_FILES", "GENERATE_MODEL_BASED_ON_TEXTURE_PATH", "COMPRESSION", "PROTECTION", "RECEIVE_ENABLED", "RECEIVE_ALLOWED_ACTIONS", "RECEIVE_LOADED_ACTIONS", "RECEIVE_FAILED_ACTIONS", "RECEIVE_DENIED_ACTIONS", "RECEIVE_FAILED_RELOAD_ACTIONS", "RECEIVE_DOWNLOADED_ACTIONS", "RECEIVE_INVALID_URL_ACTIONS", "RECEIVE_DISCARDED_ACTIONS", "BLOCK_CORRECTION", "DOWNLOAD_DEFAULT_ASSETS", "CUSTOM_ARMOR_TRIMS_SETTINGS", "CUSTOM_ARMOR_TRIMS_ASSIGN", "DISABLE_LEATHER_REPAIR_CUSTOM", "CUSTOM_ARMOR_SHADER_SETTINGS", "CUSTOM_ARMOR_COMPONENT_SETTINGS", "FORMAT_INVENTORY_TITLES", "FORMAT_TITLES", "FORMAT_SUBTITLES", "FORMAT_BYTEBUF", "PACK_GENERATE", "UPDATE_CONFIGS", "GLYPH_HANDLER", "TRIMS_ARMOR", "CUSTOM_BLOCKS", "PACK_RECEIVE", "FORCE_UNICODE", "PACK_COMMENT", "PACK_SLICER", "PACK_UPLOAD", "PACK_GENERATE_ZIP", "S3_KEY", "OVERRIDE_RENAMED_ITEMS", "PACK_IMPORT_MODEL_ENGINE", "PACK_READER_LENIENT", "WORLDEDIT_NOTEBLOCKS", "WORLDEDIT_STRINGBLOCKS", "CHAT_HANDLER", "FORMAT_PACKETS", "GLYPH_HOVER_TEXT", "GENERATE_GIF_SHADERS", "FORMAT_PACKETEVENTS", "PACK_EXCLUDE_MODEL_ENGINE_SHADERS", "DISABLE_GLYPH_HANDLER", "getSubPaths", "()[Ljava/lang/String;", "[Ljava/lang/String;", "toString", "Companion", "com.nexomc:core"}
)
public enum RemovedSettings {
   ATTEMPT_TO_MIGRATE_DUPLICATES("Pack.generation.attempt_to_migrate_duplicates", new String[0]),
   AUTOMATICALLY_SET_MODEL_DATA("ConfigsTools.automatically_set_model_data", new String[0]),
   AUTOMATICALLY_SET_GLYPH_CODE("ConfigsTools.automatically_set_glyph_code", new String[0]),
   MERGE_FONTS("Pack.import.merge_font_files", new String[0]),
   AUTO_UPDATE_ITEMS("ItemUpdater.auto_update_items", new String[0]),
   OVERRIDE_LORE("ItemUpdater.override_lore", new String[0]),
   UPDATE_FURNITURE("FurnitureUpdater.update_furniture", new String[0]),
   UPDATE_FURNITURE_ON_RELOAD("ItemUpdater.update_furniture_on_reload", new String[0]),
   UPDATE_FURNITURE_ON_LOAD("ItemUpdater.update_furniture_on_load", new String[0]),
   FURNITURE_UPDATE_DELAY("ItemUpdater.furniture_update_delay_in_seconds", new String[0]),
   FURNITURE_UPDATE_DELAY2("FurnitureUpdater.furniture_update_delay_in_seconds", new String[0]),
   UPDATE_FURNITURE_ON_LOAD2("FurnitureUpdater.update_furniture_on_load", new String[0]),
   UPDATE_FURNITURE_ON_RELOAD2("FurnitureUpdater.update_furniture_on_reload", new String[0]),
   SEND_PACK_ADVANCED("Pack.dispatch.send_pack_advanced", new String[0]),
   SHIELD_DISPLAY("Misc.shield_display", new String[0]),
   BOW_DISPLAY("Misc.bow_display", new String[0]),
   CROSSBOW_DISPLAY("Misc.crossbow_display", new String[0]),
   GENERATE_ATLAS_FILE("Pack.generation.atlas.generate", new String[0]),
   EXCLUDE_MALFORMED_ATLAS("Pack.generation.atlas.exclude_malformed_from_atlas", new String[0]),
   ATLAS_GENERATION_TYPE("Pack.generation.atlas.type", new String[0]),
   ARMOR_EQUIP_EVENT_BYPASS("Misc.armor_equip_event_bypass", new String[0]),
   UPLOAD_TYPE("Pack.upload.type", new String[0]),
   UPLOAD("Pack.upload.enabled", new String[0]),
   UPLOAD_OPTIONS("Pack.upload.options", new String[0]),
   POLYMATH_SERVER("Pack.upload.polymath.server", new String[0]),
   EXPERIMENTAL_FIX_BROKEN_FURNITURE("FurnitureUpdater.experimental_fix_broken_furniture", new String[0]),
   EXPERIMENTAL_FURNITURE_TYPE_UPDATE("FurnitureUpdater.experimental_furniture_type_update", new String[0]),
   SEND_JOIN_MESSAGE("Pack.dispatch.join_message.enabled", new String[0]),
   JOIN_MESSAGE_DELAY("Pack.dispatch.join_message.delay", new String[0]),
   GENERATE_DEFAULT_ASSETS("Plugin.generation.default_assets", new String[0]),
   VERIFY_PACK_FILES("Pack.generation.verify_pack_files", new String[0]),
   GENERATE_MODEL_BASED_ON_TEXTURE_PATH("Pack.generation.auto_generated_models_follow_texture_path", new String[0]),
   COMPRESSION("Pack.generation.compression", new String[0]),
   PROTECTION("Pack.generation.protection", new String[0]),
   RECEIVE_ENABLED("Pack.receive.enabled", new String[0]),
   RECEIVE_ALLOWED_ACTIONS("Pack.receive.accepted.actions", new String[0]),
   RECEIVE_LOADED_ACTIONS("Pack.receive.loaded.actions", new String[0]),
   RECEIVE_FAILED_ACTIONS("Pack.receive.failed_download.actions", new String[0]),
   RECEIVE_DENIED_ACTIONS("Pack.receive.denied.actions", new String[0]),
   RECEIVE_FAILED_RELOAD_ACTIONS("Pack.receive.failed_reload.actions", new String[0]),
   RECEIVE_DOWNLOADED_ACTIONS("Pack.receive.downloaded.actions", new String[0]),
   RECEIVE_INVALID_URL_ACTIONS("Pack.receive.invalid_url.actions", new String[0]),
   RECEIVE_DISCARDED_ACTIONS("Pack.receive.discarded.actions", new String[0]),
   BLOCK_CORRECTION("CustomBlocks.block_correction", new String[0]),
   DOWNLOAD_DEFAULT_ASSETS("Plugin.default_content.download_resourcepack", new String[0]),
   CUSTOM_ARMOR_TRIMS_SETTINGS("CustomArmor.trims_settings", new String[0]),
   CUSTOM_ARMOR_TRIMS_ASSIGN("CustomArmor.trims_settings.auto_assign_settings", new String[0]),
   DISABLE_LEATHER_REPAIR_CUSTOM("CustomArmor.disable_leather_repair", new String[0]),
   CUSTOM_ARMOR_SHADER_SETTINGS("CustomArmor.shader_settings", new String[0]),
   CUSTOM_ARMOR_COMPONENT_SETTINGS("CustomArmor.component_settings", new String[0]),
   FORMAT_INVENTORY_TITLES("Plugin.formatting.inventory_titles", new String[0]),
   FORMAT_TITLES("Plugin.formatting.titles", new String[0]),
   FORMAT_SUBTITLES("Plugin.formatting.subtitles", new String[0]),
   FORMAT_BYTEBUF("Plugin.formatting.bytebuf", new String[0]),
   PACK_GENERATE("Pack.generation.generate", new String[0]),
   UPDATE_CONFIGS("ConfigsTool.enable_configs_updater", new String[0]),
   GLYPH_HANDLER("Glyphs.glyph_handler", new String[0]),
   TRIMS_ARMOR("CustomArmor.trims_settings", new String[0]),
   CUSTOM_BLOCKS("CustomBlocks", new String[0]),
   PACK_RECEIVE("Pack.receive", new String[0]),
   FORCE_UNICODE("Pack.generation.fix_force_unicode_glyphs", new String[0]),
   PACK_COMMENT("Pack.generation.comment", new String[0]),
   PACK_SLICER("Pack.generation.texture_slicer", new String[0]),
   PACK_UPLOAD("Pack.upload", new String[0]),
   PACK_GENERATE_ZIP("Pack.generation.generate_zip", new String[0]),
   S3_KEY("Pack.server.s3.key", new String[0]),
   OVERRIDE_RENAMED_ITEMS("ItemUpdater.override_renamed_items", new String[0]),
   PACK_IMPORT_MODEL_ENGINE("Pack.import.modelengine", new String[0]),
   PACK_READER_LENIENT("Pack.generation.lenient", new String[0]),
   WORLDEDIT_NOTEBLOCKS("WorldEdit.noteblock_mechanic", new String[0]),
   WORLDEDIT_STRINGBLOCKS("WorldEdit.stringblock_mechanic", new String[0]),
   CHAT_HANDLER("Chat.chat_handler", new String[0]),
   FORMAT_PACKETS("Plugin.formatting.packets", new String[0]),
   GLYPH_HOVER_TEXT("Glyphs.chat_hover_text", new String[0]),
   GENERATE_GIF_SHADERS("Glyphs.generate_gif_shaders", new String[0]),
   FORMAT_PACKETEVENTS("Plugin.formatting.packetevents", new String[]{"Plugin.formatting.tablist", "Plugin.formatting.scoreboard", "Plugin.formatting.bossbar"}),
   PACK_EXCLUDE_MODEL_ENGINE_SHADERS("Pack.import.model_engine.exclude_shaders", new String[0]),
   DISABLE_GLYPH_HANDLER("Plugin.packets.disable_netty_injection", new String[0]);

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String path;
   @NotNull
   private final String[] subPaths;

   private RemovedSettings(String path, String... subPaths) {
      this.path = path;
      String[] strs = subPaths;
      Object[] result = new String[subPaths.length];
      int index = 0;
      int value = 0;

      for(int i = subPaths.length; value < i; ++value) {
         Object item = strs[value];
         int value2 = index++;
         int value3 = 0;
         String str = ((String)item).toString();
         result[value2] = str;
      }

      this.subPaths = (String[])(result);
   }

   @NotNull
   public final String getPath() {
      return this.path;
   }

   @NotNull
   public final String[] getSubPaths() {
      return this.subPaths;
   }

   @NotNull
   public String toString() {
      return this.path;
   }



   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\u0011\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00070\u0005¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/configs/RemovedSettings$Companion;", "", "<init>", "()V", "toMap", "", "", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Map<String, String[]> toMap() {
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(RemovedSettings.values());
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
         Map destination = (Map)(new LinkedHashMap(capacity));

         for(Object element : iterable) {
            RemovedSettings it = (RemovedSettings)element;
            int value = 0;
            Pair pair = TuplesKt.to(it.getPath(), it.getSubPaths());
            destination.put(pair.getFirst(), pair.getSecond());
         }

         return destination;
      }
   }
}
