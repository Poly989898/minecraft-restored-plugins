package com.Zrips.CMI.Modules.BungeeCord;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListPing {
  private InetSocketAddress host;
  private int timeout = 7000;
  private Gson gson = new Gson();

  public ServerListPing(String string, int value) {
    this.host = new InetSocketAddress(string, value);
  }

  public ServerListPing(InetSocketAddress inetSocketAddress) {
    this.host = inetSocketAddress;
  }

  public InetSocketAddress getAddress() {
    return this.host;
  }

  void setTimeout(int value) {
    this.timeout = value;
  }

  int getTimeout() {
    return this.timeout;
  }

  public int readVarInt(DataInputStream dataInputStream) throws IOException {
    int value = 0;
    int byteCount = 0;

    byte b;
    do {
      b = dataInputStream.readByte();
      value |= (b & 127) << byteCount++ * 7;
      if (byteCount > 5) {
        throw new RuntimeException("VarInt too big");
      }
    } while ((b & 128) == 128);

    return value;
  }

  public void writeVarInt(DataOutputStream dataOutputStream, int value) throws IOException {
    while ((value & -128) != 0) {
      dataOutputStream.writeByte(value & 127 | 128);
      value >>>= 7;
    }

    dataOutputStream.writeByte(value);
  }

  public ServerListPing.StatusResponse fetchData() throws IOException {
    Socket socket = new Socket();
    ServerListPing.StatusResponse statusResponse = null;

    try {
      socket.setSoTimeout(this.timeout);
      socket.connect(this.host, this.timeout);
      OutputStream outputStream = socket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = socket.getInputStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream handshakeOut = new DataOutputStream(byteArrayOutputStream);
      handshakeOut.writeByte(0);
      this.writeVarInt(handshakeOut, 4);
      this.writeVarInt(handshakeOut, this.host.getHostString().length());
      handshakeOut.writeBytes(this.host.getHostString());
      handshakeOut.writeShort(this.host.getPort());
      this.writeVarInt(handshakeOut, 1);
      this.writeVarInt(dataOutputStream, byteArrayOutputStream.size());
      dataOutputStream.write(byteArrayOutputStream.toByteArray());
      dataOutputStream.writeByte(1);
      dataOutputStream.writeByte(0);
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      int value = this.readVarInt(dataInputStream);
      int packetId = this.readVarInt(dataInputStream);
      if (packetId == -1) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Premature end of stream.");
      }

      if (packetId != 0) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Invalid packetID");
      }

      int stringLength = this.readVarInt(dataInputStream);
      if (stringLength == -1) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Premature end of stream.");
      }

      if (stringLength == 0) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Invalid string length.");
      }

      byte[] bs = new byte[stringLength];
      dataInputStream.readFully(bs);
      String string = new String(bs);
      long longValue = System.currentTimeMillis();
      dataOutputStream.writeByte(9);
      dataOutputStream.writeByte(1);
      dataOutputStream.writeLong(longValue);
      this.readVarInt(dataInputStream);
      packetId = this.readVarInt(dataInputStream);
      if (packetId == -1) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Premature end of stream.");
      }

      if (packetId != 1) {
        socket.close();
        dataInputStream.close();
        dataOutputStream.close();
        throw new IOException("Invalid packetID");
      }

      long pingStart = dataInputStream.readLong();
      if (net.Zrips.CMILib.Version.Version.isCurrentEqualOrHigher(
          net.Zrips.CMILib.Version.Version.v1_16_R1)) {
        ServerListPing.StatusResponse19 parsedResponse =
            (ServerListPing.StatusResponse19)
                this.gson.fromJson(string, ServerListPing.StatusResponse19.class);
        if (parsedResponse != null) {
          parsedResponse.setTime((int) (longValue - pingStart));
          statusResponse = new ServerListPing.StatusResponse();
          statusResponse.setDescription(parsedResponse.getDescription().toString());
          statusResponse.setFavicon(parsedResponse.getFavicon());
          statusResponse.setPlayers(parsedResponse.getPlayers());
          statusResponse.setVersion(parsedResponse.getVersion());
        }
      } else if (net.Zrips.CMILib.Version.Version.isCurrentEqualOrHigher(
          net.Zrips.CMILib.Version.Version.v1_9_R1)) {
        ServerListPing.StatusResponse19 parsedResponseAlt =
            (ServerListPing.StatusResponse19)
                this.gson.fromJson(string, ServerListPing.StatusResponse19.class);
        if (parsedResponseAlt != null) {
          parsedResponseAlt.setTime((int) (longValue - pingStart));
          statusResponse = new ServerListPing.StatusResponse();
          statusResponse.setDescription(parsedResponseAlt.getDescription().getText());
          statusResponse.setFavicon(parsedResponseAlt.getFavicon());
          statusResponse.setPlayers(parsedResponseAlt.getPlayers());
          statusResponse.setVersion(parsedResponseAlt.getVersion());
        }
      } else {
        statusResponse =
            (ServerListPing.StatusResponse)
                this.gson.fromJson(string, ServerListPing.StatusResponse.class);
        statusResponse.setTime((int) (longValue - pingStart));
      }

      dataOutputStream.close();
      outputStream.close();
      inputStreamReader.close();
      inputStream.close();
      socket.close();
    } catch (Throwable throwable) {
    } finally {
      if (!socket.isClosed()) {
        socket.close();
      }

      socket.close();
    }

    return statusResponse;
  }

  public class Description {
    private String text;

    public String getText() {
      return this.text;
    }
  }

  public class Description116 {
    private Gson text;

    public Gson getText() {
      return this.text;
    }
  }

  public class Player {
    private String name;
    private String id;

    public String getName() {
      return this.name;
    }

    public String getId() {
      return this.id;
    }
  }

  public class Players {
    private int max = 0;
    private int online = 0;
    private List<ServerListPing.Player> sample = null;

    public int getMax() {
      return this.max;
    }

    public int getOnline() {
      return this.online;
    }

    public List<ServerListPing.Player> getSample() {
      return this.sample == null ? new ArrayList<>() : this.sample;
    }
  }

  public class StatusResponse {
    private String description;
    private ServerListPing.Players players;
    private ServerListPing.Version version;
    private String favicon;
    private int time;

    public String getDescription() {
      return this.description;
    }

    public ServerListPing.Players getPlayers() {
      return this.players;
    }

    public ServerListPing.Version getVersion() {
      return this.version;
    }

    public String getFavicon() {
      return this.favicon;
    }

    public int getTime() {
      return this.time;
    }

    public void setTime(int value) {
      this.time = value;
    }

    public void setDescription(String string) {
      this.description = string;
    }

    public void setPlayers(ServerListPing.Players newPlayers) {
      this.players = newPlayers;
    }

    public void setVersion(ServerListPing.Version newVersion) {
      this.version = newVersion;
    }

    public void setFavicon(String string) {
      this.favicon = string;
    }
  }

  public class StatusResponse116 {
    private ServerListPing.Description116 description;
    private ServerListPing.Players players;
    private ServerListPing.Version version;
    private String favicon;
    private int time;

    public ServerListPing.Description116 getDescription() {
      return this.description;
    }

    public ServerListPing.Players getPlayers() {
      return this.players;
    }

    public ServerListPing.Version getVersion() {
      return this.version;
    }

    public String getFavicon() {
      return this.favicon;
    }

    public int getTime() {
      return this.time;
    }

    public void setTime(int value) {
      this.time = value;
    }
  }

  public class StatusResponse19 {
    private ServerListPing.Description description;
    private ServerListPing.Players players;
    private ServerListPing.Version version;
    private String favicon;
    private int time;

    public ServerListPing.Description getDescription() {
      return this.description;
    }

    public ServerListPing.Players getPlayers() {
      return this.players;
    }

    public ServerListPing.Version getVersion() {
      return this.version;
    }

    public String getFavicon() {
      return this.favicon;
    }

    public int getTime() {
      return this.time;
    }

    public void setTime(int value) {
      this.time = value;
    }
  }

  public class Version {
    private String name;
    private String protocol;

    public String getName() {
      return this.name;
    }

    public String getProtocol() {
      return this.protocol;
    }
  }
}
