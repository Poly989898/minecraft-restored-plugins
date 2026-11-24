package me.TechsCode.UltraPermissions.base.update.customRequests;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpGetWithBody extends HttpEntityEnclosingRequestBase {
   public static final String GET_METHOD = "GET";

   public HttpGetWithBody(URI uri) {
      this.setURI(uri);
   }

   public HttpGetWithBody(String text) {
      this.setURI(URI.create(text));
   }

   public String getMethod() {
      return "GET";
   }
}
