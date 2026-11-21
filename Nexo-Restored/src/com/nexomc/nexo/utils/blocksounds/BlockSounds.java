package com.nexomc.nexo.utils.blocksounds;

import com.nexomc.nexo.utils.NexoYamlKt;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0007\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0006\u0018\u0000 .2\u00020\u0001:\u0001.B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010(\u001a\u00020)J\u0006\u0010*\u001a\u00020)J\u0006\u0010+\u001a\u00020)J\u0006\u0010,\u001a\u00020)J\u0006\u0010-\u001a\u00020)R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\tR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\tR\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\u0018\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0011\u0010\u001a\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0011\u0010\u001c\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0015R\u0011\u0010\u001e\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0015R\u0011\u0010 \u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015R\u0011\u0010\"\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0015R\u0011\u0010$\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0015R\u0011\u0010&\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b'\u0010\u0015¨\u0006/"},
   d2 = {"Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "placeSound", "", "getPlaceSound", "()Ljava/lang/String;", "breakSound", "getBreakSound", "stepSound", "getStepSound", "hitSound", "getHitSound", "fallSound", "getFallSound", "placeVolume", "", "getPlaceVolume", "()F", "breakVolume", "getBreakVolume", "stepVolume", "getStepVolume", "hitVolume", "getHitVolume", "fallVolume", "getFallVolume", "placePitch", "getPlacePitch", "breakPitch", "getBreakPitch", "stepPitch", "getStepPitch", "hitPitch", "getHitPitch", "fallPitch", "getFallPitch", "hasPlaceSound", "", "hasBreakSound", "hasStepSound", "hasHitSound", "hasFallSound", "Companion", "com.nexomc:core"}
)
public final class BlockSounds {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final String placeSound;
   @Nullable
   private final String breakSound;
   @Nullable
   private final String stepSound;
   @Nullable
   private final String hitSound;
   @Nullable
   private final String fallSound;
   private final float placeVolume;
   private final float breakVolume;
   private final float stepVolume;
   private final float hitVolume;
   private final float fallVolume;
   private final float placePitch;
   private final float breakPitch;
   private final float stepPitch;
   private final float hitPitch;
   private final float fallPitch;
   @NotNull
   public static final String VANILLA_STONE_PLACE = "nexo:block.stone.place";
   @NotNull
   public static final String VANILLA_STONE_BREAK = "nexo:block.stone.break";
   @NotNull
   public static final String VANILLA_STONE_HIT = "nexo:block.stone.hit";
   @NotNull
   public static final String VANILLA_STONE_STEP = "nexo:block.stone.step";
   @NotNull
   public static final String VANILLA_STONE_FALL = "nexo:block.stone.fall";
   @NotNull
   private static final List<String> NEXO_STONE_SOUNDS;
   @NotNull
   public static final String VANILLA_WOOD_PLACE = "nexo:block.wood.place";
   @NotNull
   public static final String VANILLA_WOOD_BREAK = "nexo:block.wood.break";
   @NotNull
   public static final String VANILLA_WOOD_HIT = "nexo:block.wood.hit";
   @NotNull
   public static final String VANILLA_WOOD_STEP = "nexo:block.wood.step";
   @NotNull
   public static final String VANILLA_WOOD_FALL = "nexo:block.wood.fall";
   @NotNull
   private static final List<String> NEXO_WOOD_SOUNDS;
   public static final float VANILLA_PLACE_VOLUME = 1.0F;
   public static final float VANILLA_PLACE_PITCH = 0.8F;
   public static final float VANILLA_BREAK_VOLUME = 1.0F;
   public static final float VANILLA_BREAK_PITCH = 0.8F;
   public static final float VANILLA_HIT_VOLUME = 0.25F;
   public static final float VANILLA_HIT_PITCH = 0.5F;
   public static final float VANILLA_STEP_VOLUME = 0.15F;
   public static final float VANILLA_STEP_PITCH = 1.0F;
   public static final float VANILLA_FALL_VOLUME = 0.5F;
   public static final float VANILLA_FALL_PITCH = 0.75F;
   @NotNull
   private static final List<SoundEntry> STONE_DIG_ENTRIES;
   @NotNull
   private static final List<SoundEntry> STONE_STEP_ENTRIES;
   @NotNull
   private static final List<SoundEntry> WOOD_DIG_ENTRIES;
   @NotNull
   private static final List<SoundEntry> WOOD_STEP_ENTRIES;
   @NotNull
   private static final List<String> VANILLA_STONE_SOUNDS;
   @JvmField
   @NotNull
   public static final SoundRegistry VANILLA_STONE_SOUND_REGISTRY;
   @NotNull
   private static final List<String> VANILLA_WOOD_SOUNDS;
   @JvmField
   @NotNull
   public static final SoundRegistry VANILLA_WOOD_SOUND_REGISTRY;
   @JvmField
   @NotNull
   public static final SoundRegistry NEXO_STONE_SOUND_REGISTRY;
   @JvmField
   @NotNull
   public static final SoundRegistry NEXO_WOOD_SOUND_REGISTRY;

   public BlockSounds(@NotNull ConfigurationSection section) {
      super();
      this.placeSound = section.getString("place_sound", section.getString("place.sound"));
      this.breakSound = section.getString("break_sound", section.getString("break.sound"));
      this.stepSound = section.getString("step_sound", section.getString("step.sound"));
      this.hitSound = section.getString("hit_sound", section.getString("hit.sound"));
      this.fallSound = section.getString("fall_sound", section.getString("fall.sound"));
      this.placeVolume = NexoYamlKt.float_(section, "place.volume", 1.0F);
      this.breakVolume = NexoYamlKt.float_(section, "break.volume", 1.0F);
      this.stepVolume = NexoYamlKt.float_(section, "step.volume", 0.15F);
      this.hitVolume = NexoYamlKt.float_(section, "hit.volume", 0.25F);
      this.fallVolume = NexoYamlKt.float_(section, "fall.volume", 0.5F);
      this.placePitch = NexoYamlKt.float_(section, "place.pitch", 0.8F);
      this.breakPitch = NexoYamlKt.float_(section, "break.pitch", 0.8F);
      this.stepPitch = NexoYamlKt.float_(section, "step.pitch", 1.0F);
      this.hitPitch = NexoYamlKt.float_(section, "hit.pitch", 0.5F);
      this.fallPitch = NexoYamlKt.float_(section, "fall.pitch", 0.75F);
   }

   @Nullable
   public final String getPlaceSound() {
      return this.placeSound;
   }

   @Nullable
   public final String getBreakSound() {
      return this.breakSound;
   }

   @Nullable
   public final String getStepSound() {
      return this.stepSound;
   }

   @Nullable
   public final String getHitSound() {
      return this.hitSound;
   }

   @Nullable
   public final String getFallSound() {
      return this.fallSound;
   }

   public final float getPlaceVolume() {
      return this.placeVolume;
   }

   public final float getBreakVolume() {
      return this.breakVolume;
   }

   public final float getStepVolume() {
      return this.stepVolume;
   }

   public final float getHitVolume() {
      return this.hitVolume;
   }

   public final float getFallVolume() {
      return this.fallVolume;
   }

   public final float getPlacePitch() {
      return this.placePitch;
   }

   public final float getBreakPitch() {
      return this.breakPitch;
   }

   public final float getStepPitch() {
      return this.stepPitch;
   }

   public final float getHitPitch() {
      return this.hitPitch;
   }

   public final float getFallPitch() {
      return this.fallPitch;
   }

   public final boolean hasPlaceSound() {
      return this.placeSound != null;
   }

   public final boolean hasBreakSound() {
      return this.breakSound != null;
   }

   public final boolean hasStepSound() {
      return this.stepSound != null;
   }

   public final boolean hasHitSound() {
      return this.hitSound != null;
   }

   public final boolean hasFallSound() {
      return this.fallSound != null;
   }

   static {
      String[] strs = new String[]{"nexo:block.stone.place", "nexo:block.stone.break", "nexo:block.stone.hit", "nexo:block.stone.step", "nexo:block.stone.fall"};
      NEXO_STONE_SOUNDS = (List)(new ObjectArrayList((Collection)CollectionsKt.listOf(strs)));
      strs = new String[]{"nexo:block.wood.place", "nexo:block.wood.break", "nexo:block.wood.hit", "nexo:block.wood.step", "nexo:block.wood.fall"};
      NEXO_WOOD_SOUNDS = (List)(new ObjectArrayList((Collection)CollectionsKt.listOf(strs)));
      SoundEntry[] soundEntries = new SoundEntry[]{SoundEntry.soundEntry().key(Key.key("dig/stone1")).build(), SoundEntry.soundEntry().key(Key.key("dig/stone2")).build(), SoundEntry.soundEntry().key(Key.key("dig/stone3")).build(), SoundEntry.soundEntry().key(Key.key("dig/stone4")).build()};
      STONE_DIG_ENTRIES = CollectionsKt.listOf(soundEntries);
      soundEntries = new SoundEntry[]{SoundEntry.soundEntry().key(Key.key("step/stone1")).build(), SoundEntry.soundEntry().key(Key.key("step/stone2")).build(), SoundEntry.soundEntry().key(Key.key("step/stone3")).build(), SoundEntry.soundEntry().key(Key.key("step/stone4")).build(), SoundEntry.soundEntry().key(Key.key("step/stone5")).build(), SoundEntry.soundEntry().key(Key.key("step/stone6")).build()};
      STONE_STEP_ENTRIES = CollectionsKt.listOf(soundEntries);
      soundEntries = new SoundEntry[]{SoundEntry.soundEntry().key(Key.key("dig/wood1")).build(), SoundEntry.soundEntry().key(Key.key("dig/wood2")).build(), SoundEntry.soundEntry().key(Key.key("dig/wood3")).build(), SoundEntry.soundEntry().key(Key.key("dig/wood4")).build()};
      WOOD_DIG_ENTRIES = CollectionsKt.listOf(soundEntries);
      soundEntries = new SoundEntry[]{SoundEntry.soundEntry().key(Key.key("step/wood1")).build(), SoundEntry.soundEntry().key(Key.key("step/wood2")).build(), SoundEntry.soundEntry().key(Key.key("step/wood3")).build(), SoundEntry.soundEntry().key(Key.key("step/wood4")).build(), SoundEntry.soundEntry().key(Key.key("step/wood5")).build(), SoundEntry.soundEntry().key(Key.key("step/wood6")).build()};
      WOOD_STEP_ENTRIES = CollectionsKt.listOf(soundEntries);
      String[] strs2 = new String[]{"minecraft:block.stone.place", "minecraft:block.stone.break", "minecraft:block.stone.hit", "minecraft:block.stone.fall", "minecraft:block.stone.step"};
      VANILLA_STONE_SOUNDS = (List)(new ObjectArrayList((Collection)CollectionsKt.listOf(strs2)));
      Iterable iterable = (Iterable)VANILLA_STONE_SOUNDS;
      String str = "minecraft";
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         destination.add(SoundEvent.soundEvent(Key.key(it), true, (String)null, (List)(new ArrayList())));
      }

      SoundRegistry soundRegistry = SoundRegistry.soundRegistry(str, (Collection)((List)destination));
      VANILLA_STONE_SOUND_REGISTRY = soundRegistry;
      strs2 = new String[]{"minecraft:block.wood.place", "minecraft:block.wood.break", "minecraft:block.wood.hit", "minecraft:block.wood.fall", "minecraft:block.wood.step"};
      VANILLA_WOOD_SOUNDS = (List)(new ObjectArrayList((Collection)CollectionsKt.listOf(strs2)));
      iterable = (Iterable)VANILLA_WOOD_SOUNDS;
      str = "minecraft";
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj : iterable) {
         String it = (String)obj;
         int value2 = 0;
         destination.add(SoundEvent.soundEvent(Key.key(it), true, (String)null, (List)(new ArrayList())));
      }

      soundRegistry = SoundRegistry.soundRegistry(str, (Collection)((List)destination));
      VANILLA_WOOD_SOUND_REGISTRY = soundRegistry;
      SoundEvent[] soundEvents = new SoundEvent[]{SoundEvent.soundEvent(Key.key("nexo:block.stone.place"), false, "subtitles.block.generic.place", STONE_DIG_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.stone.break"), false, "subtitles.block.generic.break", STONE_DIG_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.stone.hit"), false, "subtitles.block.generic.hit", STONE_STEP_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.stone.fall"), false, (String)null, STONE_STEP_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.stone.step"), false, "subtitles.block.generic.footsteps", STONE_STEP_ENTRIES)};
      soundRegistry = SoundRegistry.soundRegistry("nexo", (Collection)CollectionsKt.listOf(soundEvents));
      NEXO_STONE_SOUND_REGISTRY = soundRegistry;
      soundEvents = new SoundEvent[]{SoundEvent.soundEvent(Key.key("nexo:block.wood.place"), false, "subtitles.block.generic.place", WOOD_DIG_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.wood.break"), false, "subtitles.block.generic.break", WOOD_DIG_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.wood.hit"), false, "subtitles.block.generic.hit", WOOD_STEP_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.wood.fall"), false, (String)null, WOOD_STEP_ENTRIES), SoundEvent.soundEvent(Key.key("nexo:block.wood.step"), false, "subtitles.block.generic.footsteps", WOOD_STEP_ENTRIES)};
      soundRegistry = SoundRegistry.soundRegistry("nexo", (Collection)CollectionsKt.listOf(soundEvents));
      NEXO_WOOD_SOUND_REGISTRY = soundRegistry;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0010\u0007\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\rR\u000e\u0010\u0015\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0016X\u0086T¢\u0006\u0002\n\u0000R\u001e\u0010 \u001a\u0012\u0012\u000e\u0012\f0!¢\u0006\u0002\b\"¢\u0006\u0002\b#0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010$\u001a\u0012\u0012\u000e\u0012\f0!¢\u0006\u0002\b\"¢\u0006\u0002\b#0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010%\u001a\u0012\u0012\u000e\u0012\f0!¢\u0006\u0002\b\"¢\u0006\u0002\b#0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010&\u001a\u0012\u0012\u000e\u0012\f0!¢\u0006\u0002\b\"¢\u0006\u0002\b#0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\rR\u001a\u0010)\u001a\u00070*¢\u0006\u0002\b\"8\u0006X\u0087\u0004\u0092\u0002\u0002\b+¢\u0006\u0002\n\u0000R\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\b\n\u0000\u001a\u0004\b-\u0010\rR\u001a\u0010.\u001a\u00070*¢\u0006\u0002\b\"8\u0006X\u0087\u0004\u0092\u0002\u0002\b+¢\u0006\u0002\n\u0000R\u001a\u0010/\u001a\u00070*¢\u0006\u0002\b\"8\u0006X\u0087\u0004\u0092\u0002\u0002\b+¢\u0006\u0002\n\u0000R\u001a\u00100\u001a\u00070*¢\u0006\u0002\b\"8\u0006X\u0087\u0004\u0092\u0002\u0002\b+¢\u0006\u0002\n\u0000¨\u00061"},
      d2 = {"Lcom/nexomc/nexo/utils/blocksounds/BlockSounds$Companion;", "", "<init>", "()V", "VANILLA_STONE_PLACE", "", "VANILLA_STONE_BREAK", "VANILLA_STONE_HIT", "VANILLA_STONE_STEP", "VANILLA_STONE_FALL", "NEXO_STONE_SOUNDS", "", "getNEXO_STONE_SOUNDS", "()Ljava/util/List;", "VANILLA_WOOD_PLACE", "VANILLA_WOOD_BREAK", "VANILLA_WOOD_HIT", "VANILLA_WOOD_STEP", "VANILLA_WOOD_FALL", "NEXO_WOOD_SOUNDS", "getNEXO_WOOD_SOUNDS", "VANILLA_PLACE_VOLUME", "", "VANILLA_PLACE_PITCH", "VANILLA_BREAK_VOLUME", "VANILLA_BREAK_PITCH", "VANILLA_HIT_VOLUME", "VANILLA_HIT_PITCH", "VANILLA_STEP_VOLUME", "VANILLA_STEP_PITCH", "VANILLA_FALL_VOLUME", "VANILLA_FALL_PITCH", "STONE_DIG_ENTRIES", "Lteam/unnamed/creative/sound/SoundEntry;", "Lorg/jetbrains/annotations/NotNull;", "Lkotlin/jvm/internal/EnhancedNullability;", "STONE_STEP_ENTRIES", "WOOD_DIG_ENTRIES", "WOOD_STEP_ENTRIES", "VANILLA_STONE_SOUNDS", "getVANILLA_STONE_SOUNDS", "VANILLA_STONE_SOUND_REGISTRY", "Lteam/unnamed/creative/sound/SoundRegistry;", "Lkotlin/jvm/JvmField;", "VANILLA_WOOD_SOUNDS", "getVANILLA_WOOD_SOUNDS", "VANILLA_WOOD_SOUND_REGISTRY", "NEXO_STONE_SOUND_REGISTRY", "NEXO_WOOD_SOUND_REGISTRY", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<String> getNEXO_STONE_SOUNDS() {
         return BlockSounds.NEXO_STONE_SOUNDS;
      }

      @NotNull
      public final List<String> getNEXO_WOOD_SOUNDS() {
         return BlockSounds.NEXO_WOOD_SOUNDS;
      }

      @NotNull
      public final List<String> getVANILLA_STONE_SOUNDS() {
         return BlockSounds.VANILLA_STONE_SOUNDS;
      }

      @NotNull
      public final List<String> getVANILLA_WOOD_SOUNDS() {
         return BlockSounds.VANILLA_WOOD_SOUNDS;
      }
   }
}
