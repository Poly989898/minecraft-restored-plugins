package io.lumine.mythic.bukkit.utils.network.messaging.codec;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class GsonCodec<M> implements Codec<M> {
   private final Gson gson;
   private final TypeToken<M> type;

   public GsonCodec(Gson gson, TypeToken<M> type) {
      this.gson = gson;
      this.type = type;
   }

   public GsonCodec(TypeToken<M> type) {
      this(GsonProvider.standard(), type);
   }

   @Override
   public byte[] encode(M message) {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

      try (Writer writer = new OutputStreamWriter(byteOut, StandardCharsets.UTF_8)) {
         this.gson.toJson(message, this.type.getType(), writer);
      } catch (IOException e) {
         throw new EncodingException(e);
      }

      return byteOut.toByteArray();
   }

   @Override
   public M decode(byte[] buf) {
      ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);

      try {
         Object obj;
         try (Reader reader = new InputStreamReader(byteIn, StandardCharsets.UTF_8)) {
            obj = this.gson.fromJson(reader, this.type.getType());
         }

         return (M)obj;
      } catch (IOException e) {
         throw new EncodingException(e);
      }
   }
}
