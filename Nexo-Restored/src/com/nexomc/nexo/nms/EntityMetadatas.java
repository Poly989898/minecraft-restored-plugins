package com.nexomc.nexo.nms;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0011\u0010%\u001a\u00020&¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0011\u0010)\u001a\u00020&¢\u0006\b\n\u0000\u001a\u0004\b*\u0010(R\u0011\u0010+\u001a\u00020,¢\u0006\b\n\u0000\u001a\u0004\b-\u0010.¨\u0006/"},
   d2 = {"Lcom/nexomc/nexo/nms/EntityMetadatas;", "", "<init>", "()V", "ENTITY_SHARED_DATA", "", "ENTITY_CUSTOM_NAME", "ENTITY_CUSTOM_NAME_VISIBLE", "ENTITY_SILENT", "MOB_SHARED_DATA", "SHULKER_ATTACHED_FACE", "SHULKER_SHIELD_HEIGHT", "SHULKER_COLOR", "DISPLAY_INTERPOLATION_DELAY", "DISPLAY_TRANSFORMATION_INTERPOLATION_DURATION", "DISPLAY_POS_INTERPOLATION_DURATION", "DISPLAY_TRANSLATION", "DISPLAY_SCALE_ID", "DISPLAY_QUATERNION_LEFT", "DISPLAY_QUATERNION_RIGHT", "DISPLAY_BILLBOARD_ID", "DISPLAY_BRIGHTNESS_ID", "DISPLAY_VIEW_RANGE_ID", "DISPLAY_SHADOW_RADIUS_ID", "DISPLAY_SHADOW_STRENGTH_ID", "DISPLAY_WIDTH_ID", "DISPLAY_HEIGHT_ID", "DISPLAY_GLOW_ID", "ITEM_DISPLAY_ITEM_ID", "ITEM_DISPLAY_TRANSFORM_ID", "TEXT_DISPLAY_TEXT_ID", "TEXT_DISPLAY_LINE_WIDTH_ID", "TEXT_DISPLAY_BACKGROUND_COLOR_ID", "TEXT_DISPLAY_TEXT_OPACITY", "TEXT_DISPLAY_OTHER", "INTERACTION_WIDTH_ID", "INTERACTION_HEIGHT_ID", "DEFAULT_TRANSLATION", "Lorg/joml/Vector3f;", "getDEFAULT_TRANSLATION", "()Lorg/joml/Vector3f;", "DEFAULT_SCALE", "getDEFAULT_SCALE", "DEFAULT_ROTATION", "Lorg/joml/Quaternionf;", "getDEFAULT_ROTATION", "()Lorg/joml/Quaternionf;", "com.nexomc:core"}
)
public final class EntityMetadatas {
   @NotNull
   public static final EntityMetadatas INSTANCE = new EntityMetadatas();
   public static final int ENTITY_SHARED_DATA = 0;
   public static final int ENTITY_CUSTOM_NAME = 2;
   public static final int ENTITY_CUSTOM_NAME_VISIBLE = 3;
   public static final int ENTITY_SILENT = 4;
   public static final int MOB_SHARED_DATA = 15;
   public static final int SHULKER_ATTACHED_FACE = 16;
   public static final int SHULKER_SHIELD_HEIGHT = 17;
   public static final int SHULKER_COLOR = 18;
   public static final int DISPLAY_INTERPOLATION_DELAY = 8;
   public static final int DISPLAY_TRANSFORMATION_INTERPOLATION_DURATION = 9;
   public static final int DISPLAY_POS_INTERPOLATION_DURATION = 10;
   public static final int DISPLAY_TRANSLATION = 11;
   public static final int DISPLAY_SCALE_ID = 12;
   public static final int DISPLAY_QUATERNION_LEFT = 13;
   public static final int DISPLAY_QUATERNION_RIGHT = 14;
   public static final int DISPLAY_BILLBOARD_ID = 15;
   public static final int DISPLAY_BRIGHTNESS_ID = 16;
   public static final int DISPLAY_VIEW_RANGE_ID = 17;
   public static final int DISPLAY_SHADOW_RADIUS_ID = 18;
   public static final int DISPLAY_SHADOW_STRENGTH_ID = 19;
   public static final int DISPLAY_WIDTH_ID = 20;
   public static final int DISPLAY_HEIGHT_ID = 21;
   public static final int DISPLAY_GLOW_ID = 22;
   public static final int ITEM_DISPLAY_ITEM_ID = 23;
   public static final int ITEM_DISPLAY_TRANSFORM_ID = 24;
   public static final int TEXT_DISPLAY_TEXT_ID = 23;
   public static final int TEXT_DISPLAY_LINE_WIDTH_ID = 24;
   public static final int TEXT_DISPLAY_BACKGROUND_COLOR_ID = 25;
   public static final int TEXT_DISPLAY_TEXT_OPACITY = 26;
   public static final int TEXT_DISPLAY_OTHER = 27;
   public static final int INTERACTION_WIDTH_ID = 8;
   public static final int INTERACTION_HEIGHT_ID = 9;
   @NotNull
   private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
   @NotNull
   private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
   @NotNull
   private static final Quaternionf DEFAULT_ROTATION = new Quaternionf();

   private EntityMetadatas() {
   }

   @NotNull
   public final Vector3f getDEFAULT_TRANSLATION() {
      return DEFAULT_TRANSLATION;
   }

   @NotNull
   public final Vector3f getDEFAULT_SCALE() {
      return DEFAULT_SCALE;
   }

   @NotNull
   public final Quaternionf getDEFAULT_ROTATION() {
      return DEFAULT_ROTATION;
   }
}
