package com.nexomc.nexo.configs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.glyphs.AnimatedGlyph;
import com.nexomc.nexo.pack.PackObfuscator;
import com.nexomc.nexo.pack.TextureMerger;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\bt\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u0000 ±\u00012\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0002±\u0001B\u0011\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u001b\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u0004\u0010\bB!\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\t¢\u0006\u0004\b\u0004\u0010\nB/\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\f\"\u00020\u0003¢\u0006\u0004\b\u0004\u0010\rB=\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e\u0012\u0012\u0010\u000f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\f\"\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0010B#\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u0011\u001a\u00020\u0012¢\u0006\u0004\b\u0004\u0010\u0013J\u001d\u0010\u008f\u0001\u001a\u00030\u0091\u00012\t\u0010\u008c\u0001\u001a\u0004\u0018\u00010\u00072\b\u0010\u0092\u0001\u001a\u00030\u0093\u0001J\u000b\u0010\u0094\u0001\u001a\u00020\u0003H\u0096\u0080\u0004J\u0010\u0010\u0094\u0001\u001a\u00020\u00032\u0007\u0010\u0095\u0001\u001a\u00020\u0003J\u000e\u0010\u0096\u0001\u001a\t0\u0097\u0001¢\u0006\u0003\b\u0098\u0001J\u0018\u0010\u0096\u0001\u001a\t0\u0097\u0001¢\u0006\u0003\b\u0098\u00012\b\u0010\u0095\u0001\u001a\u00030\u0097\u0001J:\u0010\u0099\u0001\u001a\u0003H\u009a\u0001\"\u0010\b\u0000\u0010\u009a\u0001*\t\u0012\u0005\u0012\u0003H\u009a\u00010\u00012\u000f\u0010\u009b\u0001\u001a\n\u0012\u0005\u0012\u0003H\u009a\u00010\u009c\u00012\u0007\u0010\u0006\u001a\u0003H\u009a\u0001¢\u0006\u0003\u0010\u009d\u0001JH\u0010\u009e\u0001\u001a\u0003H\u009a\u0001\"\u0010\b\u0000\u0010\u009a\u0001*\t\u0012\u0005\u0012\u0003H\u009a\u00010\u00012\u000f\u0010\u009b\u0001\u001a\n\u0012\u0005\u0012\u0003H\u009a\u00010\u009c\u00012\u0015\u0010\u009f\u0001\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0005\u0012\u0003H\u009a\u00010 \u0001¢\u0006\u0003\u0010¡\u0001J\r\u0010¢\u0001\u001a\b0\u0012¢\u0006\u0003\b\u0098\u0001J\b\u0010£\u0001\u001a\u00030¤\u0001J\u0012\u0010£\u0001\u001a\u00030¤\u00012\b\u0010\u0095\u0001\u001a\u00030¤\u0001J\u0011\u0010¥\u0001\u001a\u00030\u0093\u00012\u0007\u0010\u0006\u001a\u00030\u0093\u0001J\b\u0010¥\u0001\u001a\u00030\u0093\u0001J9\u0010¦\u0001\u001a4\u0012\r\u0012\u000b ¨\u0001*\u0004\u0018\u00010\u00030\u0003 ¨\u0001*\u0017\u0012\r\u0012\u000b ¨\u0001*\u0004\u0018\u00010\u00030\u00030\u000e¢\u0006\u0003\b\u0098\u00010§\u0001¢\u0006\u0003\b\u0098\u0001J\r\u0010©\u0001\u001a\b\u0012\u0004\u0012\u00020\u00030\u000eJ#\u0010©\u0001\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e2\u0014\u0010ª\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030 \u0001J\n\u0010«\u0001\u001a\u0005\u0018\u00010¬\u0001J\u0011\u0010\u00ad\u0001\u001a\u00030®\u0001¢\u0006\u0006\b¯\u0001\u0010°\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0000\u001a\u0006\b\u0085\u0001\u0010\u0086\u0001R\u001c\u0010\u0006\u001a\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0087\u0001¢\u0006\n\n\u0000\u001a\u0006\b\u0088\u0001\u0010\u0089\u0001R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00030\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u008a\u0001\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R.\u0010\u008c\u0001\u001a\u0004\u0018\u00010\u00072\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010\u00078F@FX\u0086\u000e¢\u0006\u0010\u001a\u0006\b\u008d\u0001\u0010\u008e\u0001\"\u0006\b\u008f\u0001\u0010\u0090\u0001j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b j\u0002\b!j\u0002\b\"j\u0002\b#j\u0002\b$j\u0002\b%j\u0002\b&j\u0002\b'j\u0002\b(j\u0002\b)j\u0002\b*j\u0002\b+j\u0002\b,j\u0002\b-j\u0002\b.j\u0002\b/j\u0002\b0j\u0002\b1j\u0002\b2j\u0002\b3j\u0002\b4j\u0002\b5j\u0002\b6j\u0002\b7j\u0002\b8j\u0002\b9j\u0002\b:j\u0002\b;j\u0002\b<j\u0002\b=j\u0002\b>j\u0002\b?j\u0002\b@j\u0002\bAj\u0002\bBj\u0002\bCj\u0002\bDj\u0002\bEj\u0002\bFj\u0002\bGj\u0002\bHj\u0002\bIj\u0002\bJj\u0002\bKj\u0002\bLj\u0002\bMj\u0002\bNj\u0002\bOj\u0002\bPj\u0002\bQj\u0002\bRj\u0002\bSj\u0002\bTj\u0002\bUj\u0002\bVj\u0002\bWj\u0002\bXj\u0002\bYj\u0002\bZj\u0002\b[j\u0002\b\\j\u0002\b]j\u0002\b^j\u0002\b_j\u0002\b`j\u0002\baj\u0002\bbj\u0002\bcj\u0002\bdj\u0002\bej\u0002\bfj\u0002\bgj\u0002\bhj\u0002\bij\u0002\bjj\u0002\bkj\u0002\blj\u0002\bmj\u0002\bnj\u0002\boj\u0002\bpj\u0002\bqj\u0002\brj\u0002\bsj\u0002\btj\u0002\buj\u0002\bvj\u0002\bwj\u0002\bxj\u0002\byj\u0002\bzj\u0002\b{j\u0002\b|j\u0002\b}j\u0002\b~j\u0002\b\u007fj\u0003\b\u0080\u0001j\u0003\b\u0081\u0001j\u0003\b\u0082\u0001j\u0003\b\u0083\u0001j\u0003\b\u0084\u0001¨\u0006²\u0001"},
   d2 = {"Lcom/nexomc/nexo/configs/Settings;", "", "path", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "defaultValue", "", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;)V", "Lkotlin/Function0;", "(Ljava/lang/String;ILjava/lang/String;Lkotlin/jvm/functions/Function0;)V", "comments", "", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;[Ljava/lang/String;)V", "", "inlineComments", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;Ljava/util/List;[Ljava/lang/String;)V", "richComment", "Lnet/kyori/adventure/text/Component;", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;Lnet/kyori/adventure/text/Component;)V", "DEBUG", "PLUGIN_LANGUAGE", "KEEP_UP_TO_DATE", "FORMAT_ANVIL", "FORMAT_SIGNS", "FORMAT_PLAYER_CHAT", "FORMAT_SYSTEM_CHAT", "FORMAT_CHAT_STANDARD_TAGS", "FORMAT_TRANSLATABLE", "FORMAT_BOOKS", "FORMAT_TABLIST", "FORMAT_BOSSBAR", "FORMAT_SCOREBOARD", "FORMAT_ITEMS", "FORMAT_CURSOR", "FORMAT_VILLAGERS", "FORMAT_PACKET_EVENTS", "FORMAT_ENTITY_DATA", "FORMAT_DIALOG", "FORMAT_ACTION_BAR", "DISABLE_NETTY_INJECT", "FAWE_HOOK", "WORLDEDIT_CUSTOM_BLOCKS", "WORLDEDIT_FURNITURE", "SHOW_PERMISSION_EMOJIS", "UNICODE_COMPLETIONS", "EMOJI_BOOK_SIZE", "GLYPH_DEFAULT_PERMISSION", "GLYPH_DEFAULT_FONT", "GLYPH_DEFAULT_SHADOW_COLOR", "GLYPH_HOVER_TEXT", "SHIFT_FONT", "DEFAULT_GIF_TYPE", "GENERATE_DEFAULT_CONFIGS", "DISABLE_AUTOMATIC_MODEL_DATA", "DISABLE_AUTOMATIC_GLYPH_CODE", "INITIAL_CUSTOM_MODEL_DATA", "SKIPPED_MODEL_DATA_NUMBERS", "ERROR_ITEM", "REMOVE_INVALID_FURNITURE", "ALLOWED_GAMEMODES_FOR_ROTATION", "INVALID_FURNITURE_ITEM", "SPARTAN_FURNITURE_HOOK", "VULCAN_FURNITURE_HOOK", "CUSTOM_ARMOR_TYPE", "CUSTOM_ARMOR_ASSIGN", "UPDATE_ITEMS", "UPDATE_ITEMS_ON_RELOAD", "UPDATE_TILE_ENTITY_CONTENTS", "UPDATE_ENTITY_CONTENTS", "OVERRIDE_ITEM_LORE", "HIDE_SCOREBOARD_NUMBERS", "HIDE_SCOREBOARD_BACKGROUND", "HIDE_TABLIST_BACKGROUND", "BLOCK_OTHER_RESOURCEPACKS", "DEFAULT_LANGUAGE_KEY", "RESET_RECIPES", "ADD_RECIPES_TO_BOOK", "DEFAULT_ALLOW_IN_VANILLA_RECIPES", "RECIPES_REQUIRE_PLAYER_IF_PERMISSION", "RECIPES_KEEP_TRACK_OF_PLAYER_FURNACE", "RECIPES_FURNACE_COOLDOWN_DURATION", "PACK_GENERATE_PACK", "PACK_OUTPUT_PATH", "PACK_PREFER_ITEMMODELS", "PACK_MINIMIZE_JSON", "PACK_READER_LENIENT", "PACK_LEGACY_ATLAS", "PACK_FORCE_OVERSIZED_IN_GUI", "PACK_OBFUSCATION_TYPE", "PACK_CACHE_OBFUSCATION", "PACK_MERGE_TEXTURES", "PACK_IMPORT_DEFAULT", "PACK_IMPORT_EXTERNAL", "PACK_IMPORT_FROM_LOCATION", "PACK_IMPORT_FROM_URL", "PACK_IMPORT_EXTERNAL_PACK_ORDER", "PACK_IMPORT_MODEL_ENGINE", "PACK_EXCLUDED_FILE_EXTENSIONS", "PACK_SKIP_UNSUPPORTED_OVERLAYS", "PACK_USE_PACKSQUASH", "PACKSQUASH_EXEC_PATH", "PACKSQUASH_SETTINGS_PATH", "PACKSQUASH_CACHE_SIZE", "PACK_VALIDATE_MODELS", "PACK_VALIDATE_FONTS", "PACK_VALIDATE_ATLAS", "PACK_VALIDATE_LANGUAGES", "PACK_VALIDATE_MIPMAP", "PACK_SERVER_TYPE", "SELFHOST_PACK_SERVER_PORT", "SELFHOST_PUBLIC_ADDRESS", "SELFHOST_DISPATCH_THREADS", "POLYMATH_SERVER", "POLYMATH_SECRET", "HERMES_SERVER", "LOBFILE_API_KEY", "S3_PUBLIC_URL", "S3_ENDPOINT_URL", "S3_REGION", "S3_BUCKET_NAME", "S3_UNIQUE_KEY", "S3_SECRET_KEY", "S3_ACCESS_KEY", "S3_URL_EXPIRATION", "S3_PATH_STYLE", "S3_CHUNKED_ENCODING", "PACK_SEND_PRE_JOIN", "PACK_SEND_ON_JOIN", "PACK_SEND_RELOAD", "PACK_SEND_DELAY", "PACK_SEND_MANDATORY", "PACK_SEND_PROMPT", "getPath", "()Ljava/lang/String;", "Lkotlin/Lazy;", "getDefaultValue", "()Lkotlin/Lazy;", "_value", "newValue", "value", "getValue", "()Ljava/lang/Object;", "setValue", "(Ljava/lang/Object;)V", "", "save", "", "toString", "optionalDefault", "toKey", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "toEnum", "E", "enumClass", "Ljava/lang/Class;", "(Ljava/lang/Class;Ljava/lang/Enum;)Ljava/lang/Enum;", "toEnumOrGet", "fallback", "Lkotlin/Function1;", "(Ljava/lang/Class;Lkotlin/jvm/functions/Function1;)Ljava/lang/Enum;", "toComponent", "toInt", "", "toBool", "toStringList", "", "kotlin.jvm.PlatformType", "toStringListOrSingle", "transform", "toConfigSection", "Lorg/bukkit/configuration/ConfigurationSection;", "toDuration", "Lkotlin/time/Duration;", "toDuration-UwyO8pc", "()J", "Companion", "com.nexomc:core"}
)
public enum Settings {
   DEBUG("debug", false, new String[]{"test"}),
   PLUGIN_LANGUAGE("Plugin.language", "english"),
   KEEP_UP_TO_DATE("Plugin.keep_this_up_to_date", true),
   FORMAT_ANVIL("Plugin.formatting.anvil", true),
   FORMAT_SIGNS("Plugin.formatting.signs", true),
   FORMAT_PLAYER_CHAT("Plugin.formatting.chats.player_chat", true),
   FORMAT_SYSTEM_CHAT("Plugin.formatting.chats.system_chat", true),
   FORMAT_CHAT_STANDARD_TAGS("Plugin.formatting.chats.standard_tags", false),
   FORMAT_TRANSLATABLE("Plugin.formatting.translatable", false),
   FORMAT_BOOKS("Plugin.formatting.books", true),
   FORMAT_TABLIST("Plugin.formatting.tablist", Settings::_init_$lambda$0),
   FORMAT_BOSSBAR("Plugin.formatting.bossbar", Settings::_init_$lambda$1),
   FORMAT_SCOREBOARD("Plugin.formatting.scoreboard", Settings::_init_$lambda$2),
   FORMAT_ITEMS("Plugin.formatting.items", true),
   FORMAT_CURSOR("Plugin.formatting.cursor", false),
   FORMAT_VILLAGERS("Plugin.formatting.villagers", true),
   FORMAT_PACKET_EVENTS("Plugin.formatting.packet_events", Settings::_init_$lambda$3),
   FORMAT_ENTITY_DATA("Plugin.formatting.entity_data", true),
   FORMAT_DIALOG("Plugin.formatting.dialog", true),
   FORMAT_ACTION_BAR("Plugin.formatting.action_bar", true),
   DISABLE_NETTY_INJECT("Experimental.packets.disable_netty_injection", false, new String[]{"This should not be enabled unless you know what you are doing.."}),
   FAWE_HOOK("WorldEdit.fawe_hook", false),
   WORLDEDIT_CUSTOM_BLOCKS("WorldEdit.customblock_mechanic", false),
   WORLDEDIT_FURNITURE("WorldEdit.furniture_mechanic", false),
   SHOW_PERMISSION_EMOJIS("Glyphs.emoji_list_permission_only", true),
   UNICODE_COMPLETIONS("Glyphs.unicode_completions", false),
   EMOJI_BOOK_SIZE("Glyphs.emoji_book_size", 255),
   GLYPH_DEFAULT_PERMISSION("Glyphs.default_permission", "nexo.glyphs.<glyphid>"),
   GLYPH_DEFAULT_FONT("Glyphs.default_font", "nexo:default"),
   GLYPH_DEFAULT_SHADOW_COLOR("Glyphs.default_shadow_color", "#00000000"),
   GLYPH_HOVER_TEXT("Glyphs.hover_text", ""),
   SHIFT_FONT("Glyphs.shift_font", "nexo:shift"),
   DEFAULT_GIF_TYPE("Glyphs.default_gif_type", Settings::_init_$lambda$4),
   GENERATE_DEFAULT_CONFIGS("ConfigTools.generate_default_configs", true),
   DISABLE_AUTOMATIC_MODEL_DATA("ConfigTools.disable_automatic_model_data", false),
   DISABLE_AUTOMATIC_GLYPH_CODE("ConfigTools.disable_automatic_glyph_code", false),
   INITIAL_CUSTOM_MODEL_DATA("ConfigTools.initial_custom_model_data", 1000),
   SKIPPED_MODEL_DATA_NUMBERS("ConfigTools.skipped_model_data_numbers", CollectionsKt.emptyList()),
   ERROR_ITEM("ConfigTools.error_item", MapsKt.mapOf(new Pair[]{TuplesKt.to("material", "PODZOL"), TuplesKt.to("injectId", false)})),
   REMOVE_INVALID_FURNITURE("Furniture.remove_invalid_furniture", false),
   ALLOWED_GAMEMODES_FOR_ROTATION("Furniture.allowed_gamemodes_for_rotation", CollectionsKt.listOf(new String[]{"SURVIVAL", "CREATIVE"})),
   INVALID_FURNITURE_ITEM("Furniture.invalid_furniture_item", MapsKt.mapOf(TuplesKt.to("material", "BARRIER"))),
   SPARTAN_FURNITURE_HOOK("Furniture.anti_cheat.spartan_hook", Settings::_init_$lambda$5),
   VULCAN_FURNITURE_HOOK("Furniture.anti_cheat.vulcan_hook", Settings::_init_$lambda$6),
   CUSTOM_ARMOR_TYPE("CustomArmor.type", CustomArmorType.COMPONENT),
   CUSTOM_ARMOR_ASSIGN("CustomArmor.auto_assign_settings", true),
   UPDATE_ITEMS("ItemUpdater.update_items", true),
   UPDATE_ITEMS_ON_RELOAD("ItemUpdater.update_items_on_reload", true),
   UPDATE_TILE_ENTITY_CONTENTS("ItemUpdater.update_tile_entity_contents", true),
   UPDATE_ENTITY_CONTENTS("ItemUpdater.update_entity_contents", true),
   OVERRIDE_ITEM_LORE("ItemUpdater.override_item_lore", false),
   HIDE_SCOREBOARD_NUMBERS("Misc.hide_scoreboard_numbers", false),
   HIDE_SCOREBOARD_BACKGROUND("Misc.hide_scoreboard_background", false),
   HIDE_TABLIST_BACKGROUND("Misc.hide_tablist_background", false),
   BLOCK_OTHER_RESOURCEPACKS("Misc.block_other_resourcepacks", false),
   DEFAULT_LANGUAGE_KEY("Misc.default_language_key", "en_us"),
   RESET_RECIPES("Recipes.reset_recipes", true),
   ADD_RECIPES_TO_BOOK("Recipes.add_recipes_to_book", true),
   DEFAULT_ALLOW_IN_VANILLA_RECIPES("Recipes.default_allow_in_vanilla_recipes", false),
   RECIPES_REQUIRE_PLAYER_IF_PERMISSION("Recipes.require_player_if_permission", true),
   RECIPES_KEEP_TRACK_OF_PLAYER_FURNACE("Recipes.keep_track_of_player_furnace", false),
   RECIPES_FURNACE_COOLDOWN_DURATION("Recipes.furnace_cooldown_duration", "1s"),
   PACK_GENERATE_PACK("Pack.generation.generate_pack", true),
   PACK_OUTPUT_PATH("Pack.generation.output_path", "plugins/Nexo/pack/pack.zip"),
   PACK_PREFER_ITEMMODELS("Pack.generation.prefer_item_models", true),
   PACK_MINIMIZE_JSON("Pack.generation.minimize_json", true),
   PACK_READER_LENIENT("Pack.generation.read_lenient", true),
   PACK_LEGACY_ATLAS("Pack.generation.legacy_atlas", true),
   PACK_FORCE_OVERSIZED_IN_GUI("Pack.generation.force_oversized_in_gui", false),
   PACK_OBFUSCATION_TYPE("Pack.obfuscation.type", PackObfuscator.Type.SIMPLE),
   PACK_CACHE_OBFUSCATION("Pack.obfuscation.cache", true),
   PACK_MERGE_TEXTURES("Pack.generation.merge_textures", TextureMerger.Type.NONE),
   PACK_IMPORT_DEFAULT("Pack.import.default_assets", true),
   PACK_IMPORT_EXTERNAL("Pack.import.external_packs", true),
   PACK_IMPORT_FROM_LOCATION("Pack.import.from_location", CollectionsKt.emptyList()),
   PACK_IMPORT_FROM_URL("Pack.import.from_url", CollectionsKt.emptyList()),
   PACK_IMPORT_EXTERNAL_PACK_ORDER("Pack.import.external_pack_order", CollectionsKt.emptyList()),
   PACK_IMPORT_MODEL_ENGINE("Pack.import.model_engine", true),
   PACK_EXCLUDED_FILE_EXTENSIONS("Pack.generation.excluded_file_extensions", CollectionsKt.listOf(new String[]{".zip", ".tar.gz"})),
   PACK_SKIP_UNSUPPORTED_OVERLAYS("Pack.generation.skip_unsupported_overlays", false),
   PACK_USE_PACKSQUASH("Pack.generation.packsquash.enabled", false),
   PACKSQUASH_EXEC_PATH("Pack.generation.packsquash.executable_path", "plugins/Nexo/pack/packsquash/packsquash"),
   PACKSQUASH_SETTINGS_PATH("Pack.generation.packsquash.settings_path", "plugins/Nexo/pack/packsquash/packsquash.toml"),
   PACKSQUASH_CACHE_SIZE("Pack.generation.packsquash.cache_size", 5),
   PACK_VALIDATE_MODELS("Pack.validate.models", true),
   PACK_VALIDATE_FONTS("Pack.validate.fonts", true),
   PACK_VALIDATE_ATLAS("Pack.validate.atlas", true),
   PACK_VALIDATE_LANGUAGES("Pack.validate.languages", true),
   PACK_VALIDATE_MIPMAP("Pack.validate.texture_mipnap", true),
   PACK_SERVER_TYPE("Pack.server.type", "HERMES"),
   SELFHOST_PACK_SERVER_PORT("Pack.server.selfhost.port", 8082),
   SELFHOST_PUBLIC_ADDRESS("Pack.server.selfhost.public_address", ""),
   SELFHOST_DISPATCH_THREADS("Pack.server.selfhost.dispatch_threads", 10),
   POLYMATH_SERVER("Pack.server.polymath.server", "atlas.nexomc.com"),
   POLYMATH_SECRET("Pack.server.polymath.secret", "nexomc"),
   HERMES_SERVER("Pack.server.hermes.server", "hermes.nexomc.com"),
   LOBFILE_API_KEY("Pack.server.lobfile.api_key", "API-KEY"),
   S3_PUBLIC_URL("Pack.server.s3.public_url", "https://fsn1.your-objectstorage.com"),
   S3_ENDPOINT_URL("Pack.server.s3.endpoint_url", "https://fsn1.your-objectstorage.com"),
   S3_REGION("Pack.server.s3.region", "eu-central-1"),
   S3_BUCKET_NAME("Pack.server.s3.bucket", "nexo-storage"),
   S3_UNIQUE_KEY("Pack.server.s3.unique_key"),
   S3_SECRET_KEY("Pack.server.s3.secret_key", "SECRET"),
   S3_ACCESS_KEY("Pack.server.s3.access_key", "ACCESS"),
   S3_URL_EXPIRATION("Pack.server.s3.url_expiration", "7d"),
   S3_PATH_STYLE("Pack.server.s3.path_style", false),
   S3_CHUNKED_ENCODING("Pack.server.s3.chunked_encoding", true),
   PACK_SEND_PRE_JOIN("Pack.dispatch.send_pre_join", true),
   PACK_SEND_ON_JOIN("Pack.dispatch.send_on_join", false),
   PACK_SEND_RELOAD("Pack.dispatch.send_on_reload", true),
   PACK_SEND_DELAY("Pack.dispatch.delay", -1),
   PACK_SEND_MANDATORY("Pack.dispatch.mandatory", true),
   PACK_SEND_PROMPT("Pack.dispatch.prompt", "<#fa4943>Accept the ResourcePack for the optimal <b><gradient:#46C392:#7FC794>Nexo</b></gradient> experience");

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String path;
   @NotNull
   private final Lazy<Object> defaultValue;
   @NotNull
   private List<String> comments = CollectionsKt.emptyList();
   @NotNull
   private List<String> inlineComments = CollectionsKt.emptyList();
   @NotNull
   private Component richComment;
   @Nullable
   private Object _value;
   private static File dataFolder;
   @Nullable
   private static YamlConfiguration _settings;

   @NotNull
   public final String getPath() {
      return this.path;
   }

   @NotNull
   public final Lazy<Object> getDefaultValue() {
      return this.defaultValue;
   }

   private Settings(String path) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazyOf((Object)null);
   }

   private Settings(String path, Object defaultValue) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazyOf(defaultValue);
   }

   private Settings(String path, Function0<? extends Object> defaultValue) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazy(defaultValue);
   }

   private Settings(String path, Object defaultValue, String... comments) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazyOf(defaultValue);
      this.comments = CollectionsKt.listOf(Arrays.copyOf(comments, comments.length));
   }

   private Settings(String path, Object defaultValue, List<String> comments, String... inlineComments) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazyOf(defaultValue);
      this.comments = comments;
      this.inlineComments = CollectionsKt.listOf(Arrays.copyOf(inlineComments, inlineComments.length));
   }

   private Settings(String path, Object defaultValue, Component richComment) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = LazyKt.lazyOf(defaultValue);
      this.richComment = richComment;
   }

   @Nullable
   public final Object getValue() {
      if (this._value == null) {
         this._value = Companion.settings().get(this.path);
      }

      return this._value;
   }

   public final void setValue(@Nullable Object newValue) {
      this.setValue(newValue, true);
   }

   public final void setValue(@Nullable Object value, boolean save) {
      YamlConfiguration settingFile = Companion.settings();
      Object oldValue = settingFile.get(this.path);
      settingFile.set(this.path, value);
      Settings settings = this;

      Object obj;
      try {
         Settings settings2 = settings;
         int value2 = 0;
         if (save && !Intrinsics.areEqual(oldValue, value)) {
            settingFile.save(Companion.getSettingsFile());
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      if (ResultShim.exceptionOrNull(obj) != null) {
         int value3 = 0;
         Logs.logError("Failed to apply changes to settings.yml", false);
      }

   }

   @NotNull
   public String toString() {
      return String.valueOf(this.getValue());
   }

   @NotNull
   public final String toString(@NotNull String optionalDefault) {
      Object value = this.getValue();
      String str = value instanceof String ? (String)value : null;
      if ((value instanceof String ? (String)value : null) == null) {
         str = optionalDefault;
      }

      return str;
   }

   @NotNull
   public final Key toKey() {
      Settings settings = this;

      Object obj3;
      try {
         Settings settings2 = settings;
         int value = 0;
         obj3 = ResultShim.ok(Key.key(settings2.toString()));
      } catch (Throwable e) {
         obj3 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj3;
      Key key = Key.key("minecraft:default");
      Object obj2 = ResultShim.isFailure(obj) ? key : obj;
      return (Key)obj2;
   }

   @NotNull
   public final Key toKey(@NotNull Key optionalDefault) {
      Settings settings = this;

      Object obj2;
      try {
         Settings settings2 = settings;
         int value = 0;
         obj2 = ResultShim.ok(Key.key(settings2.toString()));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = ResultShim.isFailure(obj2) ? optionalDefault : obj2;
      return (Key)obj;
   }

   @NotNull
   public final <E extends Enum<E>> E toEnum(@NotNull Class<E> enumClass, @NotNull E defaultValue) {
      String str = this.toString().toUpperCase(Locale.ROOT);
      Enum enumValue2 = EnumUtils.getEnum(enumClass, str, defaultValue);
      return (E)enumValue2;
   }

   @NotNull
   public final <E extends Enum<E>> E toEnumOrGet(@NotNull Class<E> enumClass, @NotNull Function1<? super String, ? extends E> fallback) {
      return com.nexomc.nexo.utils.EnumUtils.INSTANCE.toEnumOrElse(this.toString(), enumClass, fallback);
   }

   @NotNull
   public final Component toComponent() {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(String.valueOf(this.getValue()));
      return component;
   }

   public final int toInt() {
      return this.toInt(-1);
   }

   public final int toInt(int optionalDefault) {
      Settings settings = this;

      Object obj;
      try {
         Settings settings2 = settings;
         int value = 0;
         obj = ResultShim.ok(StringsKt.toIntOrNull(String.valueOf(settings2.getValue())));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Integer number = (Integer)(ResultShim.isFailure(obj) ? null : obj);
      return number != null ? number : optionalDefault;
   }

   public final boolean toBool(boolean defaultValue) {
      Object value = this.getValue();
      return (value instanceof Boolean ? (Boolean)value : null) != null ? value instanceof Boolean ? (Boolean)value : null : defaultValue;
   }

   public final boolean toBool() {
      Object value = this.getValue();
      return (value instanceof Boolean ? (Boolean)value : null) != null ? value instanceof Boolean ? (Boolean)value : null : false;
   }

   @NotNull
   public final List<String> toStringList() {
      List stringList = Companion.settings().getStringList(this.path);
      return stringList;
   }

   @NotNull
   public final List<String> toStringListOrSingle() {
      return NexoYamlKt.stringListOrSingle((ConfigurationSection)Companion.settings(), this.path, (String)null);
   }

   @NotNull
   public final List<String> toStringListOrSingle(@NotNull Function1<? super String, String> transform) {
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle((ConfigurationSection)Companion.settings(), this.path, (String)null);
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         destination.add(((Function1)transform).invoke(item));
      }

      return (List)destination;
   }

   @Nullable
   public final ConfigurationSection toConfigSection() {
      return NexoYamlKt.section((ConfigurationSection)Companion.settings(), this.path);
   }

   public final long toDuration_UwyO8pc/* $FF was: toDuration-UwyO8pc*/() {
      String string = Companion.settings().getString(this.path);
      long value;
      if (string != null) {
         Duration duration = DurationUtilsKt.toDuration(string, (DurationUnit)null);
         if (duration != null) {
            value = duration.unbox_impl();
            return value;
         }
      }

      value = Duration.Companion.getINFINITE_UwyO8pc();
      return value;
   }


   private static final Object _init_$lambda$0() {
      return !PluginUtils.INSTANCE.isTABEnabled();
   }

   private static final Object _init_$lambda$1() {
      return !PluginUtils.INSTANCE.isMythicHUDEnabled() && !PluginUtils.INSTANCE.isBetterHUDEnabled();
   }

   private static final Object _init_$lambda$2() {
      return !PluginUtils.INSTANCE.isTABEnabled();
   }

   private static final Object _init_$lambda$3() {
      return !PluginUtils.INSTANCE.isTABEnabled();
   }

   private static final Object _init_$lambda$4() {
      return VersionUtil.atleast("1.21.10") ? AnimatedGlyph.Type.SPRITE : AnimatedGlyph.Type.SHADER;
   }

   private static final Object _init_$lambda$5() {
      return PluginUtils.INSTANCE.isSpartanEnabled();
   }

   private static final Object _init_$lambda$6() {
      return PluginUtils.INSTANCE.isVulcanEnabled();
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0006\u001a\u00020\u0005J\u0006\u0010\u000f\u001a\u00020\nJ\u0006\u0010\u0010\u001a\u00020\u000eJ\u0006\u0010\u0011\u001a\u00020\u000eJ\u0006\u0010\u0012\u001a\u00020\nJ\b\u0010\u0013\u001a\u00020\nH\u0002R\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086.¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\f\u0010\b¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/configs/Settings$Companion;", "", "<init>", "()V", "value", "Ljava/io/File;", "dataFolder", "getDataFolder", "()Ljava/io/File;", "_settings", "Lorg/bukkit/configuration/file/YamlConfiguration;", "settingsFile", "getSettingsFile", "initialize", "", "settings", "save", "reload", "validateSettings", "defaultSettings", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final File getDataFolder() {
         File file = Settings.dataFolder;
         if (file != null) {
            return file;
         } else {
            Intrinsics.throwUninitializedPropertyAccessException("dataFolder");
            return null;
         }
      }

      @NotNull
      public final File getSettingsFile() {
         return FilesKt.resolve(this.getDataFolder(), "settings.yml");
      }

      public final void initialize(@NotNull File dataFolder) {
         Settings.dataFolder = dataFolder;
      }

      @NotNull
      public final YamlConfiguration settings() {
         if (Settings._settings == null) {
            Settings._settings = this.validateSettings();
         }

         YamlConfiguration yamlConfiguration = Settings._settings;
         return yamlConfiguration;
      }

      public final void save() {
         try {
         YamlConfiguration yamlConfiguration = Settings._settings;
         if (yamlConfiguration != null) {
            yamlConfiguration.save(this.getSettingsFile());
         }

         } catch (Exception e) {
            throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
         }
      }

      public final void reload() {
         Settings._settings = this.validateSettings();
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(Settings.values());

         for(Object element : iterable) {
            Settings it = (Settings)element;
            int value = 0;
            it._value = null;
         }

      }

      @NotNull
      public final YamlConfiguration validateSettings() {
         File settingsFile = this.getSettingsFile();
         YamlConfiguration settings = settingsFile.exists() ? NexoYaml.Companion.loadConfiguration(settingsFile) : new YamlConfiguration();
         settings.options().copyDefaults(true).indent(2).parseComments(true);
         settings.addDefaults((Configuration)this.defaultSettings());
         NexoConverter.INSTANCE.processSettings(settings);
         Companion companion = this;

         Object obj;
         try {
            Companion companion2 = companion;
            int value = 0;
            settingsFile.createNewFile();
            settings.save(settingsFile);
            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            if (settings.getBoolean("debug")) {
               it.printStackTrace();
            }
         }

         return settings;
      }

      private final YamlConfiguration defaultSettings() {
         YamlConfiguration yamlConfiguration = new YamlConfiguration();
         YamlConfiguration yamlConfiguration2 = yamlConfiguration;
         int value = 0;
         yamlConfiguration.options().copyDefaults(true).indent(4).parseComments(true);
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(Settings.values());

         for(Object element : iterable) {
            Settings setting;
            Object obj;
            String str;
            block: {
               setting = (Settings)element;
               int value2 = 0;
               Object obj2 = setting.getDefaultValue().getValue();
               str = setting.getPath();
               Enum enumValue2 = obj2 instanceof Enum ? (Enum)obj2 : null;
               if ((obj2 instanceof Enum ? (Enum)obj2 : null) != null) {
                  obj = enumValue2.name();
                  if (obj != null) {
                     break block;
                  }
               }

               obj = obj2;
            }

            yamlConfiguration2.set(str, obj);
            yamlConfiguration2.setComments(setting.getPath(), setting.comments);
            yamlConfiguration2.setInlineComments(setting.getPath(), setting.inlineComments);
         }

         return yamlConfiguration;
      }
   }
}
