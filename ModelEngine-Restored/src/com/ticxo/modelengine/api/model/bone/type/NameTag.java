package com.ticxo.modelengine.api.model.bone.type;

import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface NameTag {
   Vector3f getLocation();

   void setString(String name);

   void setComponent(Component component);

   String getJsonString();

   void setJsonString(String json);

   void setComponentSupplier(@Nullable Supplier<Component> component);

   @Nullable
   Supplier<String> getJsonStringSupplier();

   void setJsonStringSupplier(@Nullable Supplier<String> jsonStringSupplier);

   boolean isVisible();

   void setVisible(boolean visible);

   void setLineWidth(int lineWidth);

   int getLineWidth();

   void setBackgroundColor(int backgroundColor);

   int getBackgroundColor();

   void setUseDefaultBackgroundColor(boolean bool);

   boolean isUseDefaultBackgroundColor();

   void setAlignment(TextAlignment alignment);

   TextAlignment getAlignment();

   void setTextOpacity(byte textOpacity);

   byte getTextOpacity();

   void setShadow(boolean shadow);

   boolean isShadow();

   void setSeeThrough(boolean seeThrough);

   boolean isSeeThrough();

   void setBillboard(Billboard billboard);

   Billboard getBillboard();

   void setScale(Vector3f scale);

   Vector3f getScale();

   void setPosition(Vector3f position);

   Vector3f getPosition();
}
