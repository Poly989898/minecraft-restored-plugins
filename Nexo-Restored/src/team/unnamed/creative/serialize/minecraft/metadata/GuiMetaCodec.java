package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.gui.GuiBorder;
import team.unnamed.creative.metadata.gui.GuiMeta;
import team.unnamed.creative.metadata.gui.GuiScaling;
import team.unnamed.creative.metadata.gui.NineSliceGuiScaling;
import team.unnamed.creative.metadata.gui.StretchGuiScaling;
import team.unnamed.creative.metadata.gui.TileGuiScaling;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

final class GuiMetaCodec implements MetadataPartCodec<GuiMeta> {
   public @NotNull Class<GuiMeta> type() {
      return GuiMeta.class;
   }

   public @NotNull String name() {
      return "gui";
   }

   public @NotNull GuiMeta read(@NotNull JsonObject node) {
      if (!node.has("scaling")) {
         return GuiMeta.of(GuiScaling.stretch());
      } else {
         JsonObject scalingNode = node.getAsJsonObject("scaling");
         JsonElement typeNode = scalingNode.get("type");
         String typeString = typeNode != null ? typeNode.getAsString() : "stretch";
         switch (typeString.toLowerCase(Locale.ROOT)) {
            case "stretch":
               return GuiMeta.of(GuiScaling.stretch());
            case "tile":
               int width = GsonUtil.getInt(scalingNode, "width", 0);
               int height = GsonUtil.getInt(scalingNode, "height", 0);
               return GuiMeta.of(GuiScaling.tile(width, height));
            case "nine_slice":
               int nineWidth = GsonUtil.getInt(scalingNode, "width", 0);
               int nineHeight = GsonUtil.getInt(scalingNode, "height", 0);
               GuiBorder border;
               if (GsonUtil.isInt(scalingNode, "border")) {
                  border = GuiBorder.border(GsonUtil.getInt(scalingNode, "border", 0));
               } else {
                  JsonObject borderNode = scalingNode.getAsJsonObject("border");
                  if (borderNode == null) {
                     border = GuiBorder.border(0, 0, 0, 0);
                  } else {
                     int top = GsonUtil.getInt(borderNode, "top", 0);
                     int bottom = GsonUtil.getInt(borderNode, "bottom", 0);
                     int left = GsonUtil.getInt(borderNode, "left", 0);
                     int right = GsonUtil.getInt(borderNode, "right", 0);
                     border = GuiBorder.border(top, bottom, left, right);
                  }
               }

               boolean stretchInner = GsonUtil.getBoolean(scalingNode, "stretch_inner", false);
               return GuiMeta.of(GuiScaling.nineSlice(nineWidth, nineHeight, border, stretchInner));
            default:
               throw new IllegalArgumentException("Unknown gui scaling type: " + typeString);
         }
      }
   }

   public void write(@NotNull JsonWriter writer, @NotNull GuiMeta value) throws IOException {
      writer.beginObject();
      GuiScaling scaling = value.scaling();
      writer.name("scaling").beginObject();
      Objects.requireNonNull(scaling);
      byte var5 = 0;
      //$FF: var5->value
      //0->team/unnamed/creative/metadata/gui/StretchGuiScaling
      //1->team/unnamed/creative/metadata/gui/TileGuiScaling
      //2->team/unnamed/creative/metadata/gui/NineSliceGuiScaling
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(scaling, team.unnamed.creative.metadata.gui.StretchGuiScaling.class, team.unnamed.creative.metadata.gui.TileGuiScaling.class, team.unnamed.creative.metadata.gui.NineSliceGuiScaling.class)) {
         case 0:
            StretchGuiScaling ignored = (StretchGuiScaling)scaling;
            writer.name("type").value("stretch");
            break;
         case 1:
            TileGuiScaling tile = (TileGuiScaling)scaling;
            writer.name("type").value("tile");
            writer.name("width").value((long)tile.width());
            writer.name("height").value((long)tile.height());
            break;
         case 2:
            NineSliceGuiScaling nineSlice = (NineSliceGuiScaling)scaling;
            writer.name("type").value("nine_slice");
            writer.name("width").value((long)nineSlice.width());
            writer.name("height").value((long)nineSlice.height());
            GuiBorder border = nineSlice.border();
            int top = border.top();
            int bottom = border.bottom();
            int left = border.left();
            int right = border.right();
            writer.name("border");
            if (top == bottom && bottom == left && left == right) {
               writer.value((long)top);
            } else {
               writer.beginObject().name("top").value((long)top).name("bottom").value((long)bottom).name("left").value((long)left).name("right").value((long)right).endObject();
            }

            writer.name("stretch_inner").value(nineSlice.stretchInner());
      }

      writer.endObject();
      writer.endObject();
   }
}
