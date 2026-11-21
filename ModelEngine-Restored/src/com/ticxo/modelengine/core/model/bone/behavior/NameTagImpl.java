package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import java.util.function.Supplier;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NameTagImpl extends AbstractBoneBehavior<NameTagImpl> implements NameTag {
   private static final String EMPTY_STRING = "{\"text\":\"\"}";
   private final Vector3f location = new Vector3f();
   private String jsonString = "{\"text\":\"\"}";
   private Supplier<String> jsonStringSupplier;
   private boolean visible;
   private int backgroundColor = 1073741824;
   private boolean useDefaultBackgroundColor = true;
   private TextAlignment alignment = TextAlignment.CENTER;
   private int lineWidth = 200;
   private byte textOpacity = -1;
   private boolean shadow = false;
   private boolean seeThrough = true;
   private Billboard billboard = Billboard.CENTER;
   private Vector3f scale = new Vector3f(1.0F);
   private Vector3f position = new Vector3f(0.0F);

   public NameTagImpl(ModelBone bone, BoneBehaviorType<NameTagImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
   }

   @Override
   public void onApply() {
      Location baseLocation = this.bone.calculatePivotLocation();
      this.bone
         .getBlueprintBone()
         .getLocalPosition()
         .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0), this.location)
         .add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
   }

   @Override
   public void onFinalize() {
      if (this.jsonStringSupplier != null) {
         this.setJsonString(this.jsonStringSupplier.get());
      }

      Location baseLocation = this.bone.calculatePivotLocation();
      this.bone
         .getGlobalTransform()
         .mutatePosition(
            vector3f -> vector3f.rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0), this.location)
               .add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ())
         );
   }

   @Override
   public void setString(String name) {
      this.setComponent(Component.text(name));
   }

   @Override
   public void setComponent(Component component) {
      this.setJsonString(GsonComponentSerializer.gson().serialize(component));
   }

   @Override
   public void setJsonString(String json) {
      this.jsonString = json != null ? json : "{\"text\":\"\"}";
   }

   @Override
   public void setComponentSupplier(@Nullable Supplier<Component> component) {
      this.jsonStringSupplier = component == null ? null : () -> GsonComponentSerializer.gson().serialize(component.get());
   }

   @Override
   public void setUseDefaultBackgroundColor(boolean bool) {
      this.useDefaultBackgroundColor = bool;
      if (bool) {
         this.backgroundColor = 1073741824;
      }
   }

   @Generated
   @Override
   public Vector3f getLocation() {
      return this.location;
   }

   @Generated
   @Override
   public String getJsonString() {
      return this.jsonString;
   }

   @Generated
   @Override
   public Supplier<String> getJsonStringSupplier() {
      return this.jsonStringSupplier;
   }

   @Generated
   @Override
   public void setJsonStringSupplier(Supplier<String> jsonStringSupplier) {
      this.jsonStringSupplier = jsonStringSupplier;
   }

   @Generated
   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Generated
   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Generated
   @Override
   public int getBackgroundColor() {
      return this.backgroundColor;
   }

   @Generated
   @Override
   public void setBackgroundColor(int backgroundColor) {
      this.backgroundColor = backgroundColor;
   }

   @Generated
   @Override
   public boolean isUseDefaultBackgroundColor() {
      return this.useDefaultBackgroundColor;
   }

   @Generated
   @Override
   public TextAlignment getAlignment() {
      return this.alignment;
   }

   @Generated
   @Override
   public void setAlignment(TextAlignment alignment) {
      this.alignment = alignment;
   }

   @Generated
   @Override
   public int getLineWidth() {
      return this.lineWidth;
   }

   @Generated
   @Override
   public void setLineWidth(int lineWidth) {
      this.lineWidth = lineWidth;
   }

   @Generated
   @Override
   public byte getTextOpacity() {
      return this.textOpacity;
   }

   @Generated
   @Override
   public void setTextOpacity(byte textOpacity) {
      this.textOpacity = textOpacity;
   }

   @Generated
   @Override
   public boolean isShadow() {
      return this.shadow;
   }

   @Generated
   @Override
   public void setShadow(boolean shadow) {
      this.shadow = shadow;
   }

   @Generated
   @Override
   public boolean isSeeThrough() {
      return this.seeThrough;
   }

   @Generated
   @Override
   public void setSeeThrough(boolean seeThrough) {
      this.seeThrough = seeThrough;
   }

   @Generated
   @Override
   public Billboard getBillboard() {
      return this.billboard;
   }

   @Generated
   @Override
   public void setBillboard(Billboard billboard) {
      this.billboard = billboard;
   }

   @Generated
   @Override
   public Vector3f getScale() {
      return this.scale;
   }

   @Generated
   @Override
   public void setScale(Vector3f scale) {
      this.scale = scale;
   }

   @Generated
   @Override
   public Vector3f getPosition() {
      return this.position;
   }

   @Generated
   @Override
   public void setPosition(Vector3f position) {
      this.position = position;
   }
}
