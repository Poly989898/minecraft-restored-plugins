package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.animation.AnimationFrame;
import team.unnamed.creative.metadata.animation.AnimationMeta;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

final class AnimationMetaCodec implements MetadataPartCodec<AnimationMeta> {
   static final AnimationMetaCodec INSTANCE = new AnimationMetaCodec();

   private AnimationMetaCodec() {
   }

   public @NotNull Class<AnimationMeta> type() {
      return AnimationMeta.class;
   }

   public @NotNull String name() {
      return "animation";
   }

   public @NotNull AnimationMeta read(@NotNull JsonObject node) {
      AnimationMeta.Builder animation = AnimationMeta.animation().interpolate(GsonUtil.getBoolean(node, "interpolate", false)).width(GsonUtil.getInt(node, "width", -1)).height(GsonUtil.getInt(node, "height", -1)).frameTime(GsonUtil.getInt(node, "frametime", 1));
      if (node.has("frames")) {
         List<AnimationFrame> frames = new ArrayList();

         for(JsonElement frameNode : node.get("frames").getAsJsonArray()) {
            if (frameNode.isJsonObject()) {
               JsonObject frameObject = frameNode.getAsJsonObject();
               int time = GsonUtil.getInt(frameObject, "time", -1);
               int index = frameObject.get("index").getAsInt();
               frames.add(AnimationFrame.frame(index, time));
            } else {
               int index = frameNode.getAsInt();
               frames.add(AnimationFrame.frame(index));
            }
         }

         animation.frames(frames);
      }

      return animation.build();
   }

   public void write(@NotNull JsonWriter writer, @NotNull AnimationMeta animation) throws IOException {
      writer.beginObject();
      boolean interpolate = animation.interpolate();
      if (interpolate) {
         writer.name("interpolate").value(interpolate);
      }

      int width = animation.width();
      if (width != -1) {
         writer.name("width").value((long)width);
      }

      int height = animation.height();
      if (height != -1) {
         writer.name("height").value((long)height);
      }

      int frameTime = animation.frameTime();
      if (frameTime != 1) {
         writer.name("frametime").value((long)frameTime);
      }

      List<AnimationFrame> frames = animation.frames();
      if (!frames.isEmpty()) {
         writer.name("frames").beginArray();

         for(AnimationFrame frame : frames) {
            int index = frame.index();
            int time = frame.frameTime();
            if (time != frameTime && time != -1) {
               writer.beginObject().name("index").value((long)index).name("time").value((long)time).endObject();
            } else {
               writer.value((long)index);
            }
         }

         writer.endArray();
      }

      writer.endObject();
   }
}
