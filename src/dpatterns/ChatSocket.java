package dpatterns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.List;

import decorators.DBold;
import decorators.DColor;
import decorators.DItalic;

public class ChatSocket {

  private MulticastSocket socket;
  private InetAddress group;
  private SocketReceiver receiver;
  private String name = generateName((int) (Math.random() * 4 + 3)) + " " + generateName((int) (Math.random() * 4 + 4));
  private String color = generateColor();

  public ChatSocket(int port, String address) {
    try {
      this.group = InetAddress.getByName(address);
      socket = new MulticastSocket(port);
      socket.joinGroup(group);
      socket.setReuseAddress(true);
      receiver = new SocketReceiver(socket, group);
      receiver.start();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void send(String message) {
    String text = new DBold(new DColor(name + ": ", color)) + message;
    DatagramPacket p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void shout(String message) {
    String text = "" + new DItalic(new DBold(new DColor(name, color)) + " shouts: " + message);
    DatagramPacket p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String receive() {
    return receiver.receive();
  }

  public void changeName(String name) {
    String text = "" + new DItalic(new DBold(new DColor(this.name, color)) + " changed name to " + new DBold(new DColor(name, color)) + ".");
    DatagramPacket p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.name = name;
  }

  public void changeColor(String color) {
    String text = "" + new DItalic(new DBold(new DColor(this.name, color)) + " changed their user color.");
    DatagramPacket p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.color = color;
  }
  
  public void changeGroup(String ip) {
    String text = "" + new DItalic(new DBold(new DColor(this.name, color)) + " left the group " + group + ".");
    DatagramPacket p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      socket.leaveGroup(group);
      this.group = InetAddress.getByName(ip);
      socket.joinGroup(this.group);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    text = "" + new DItalic(new DBold(new DColor(this.name, color)) + " joined the group " + group + ".");
    p = new DatagramPacket(text.getBytes(), text.length(), group, socket.getLocalPort());
    try {
      socket.send(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static String generateName(int length) {
    String vowels = "aeiouy";
    String consonants = "bcdfghjklmnpqrstvwxz";
    List<String> combinations = Arrays.asList("st", "ch");

    String name = "";
    for (int i = 0; i < length; i++) {
      while (true) {
        char letter = (vowels + consonants).charAt((int) Math.floor((Math.random() * (vowels + consonants).length())));
        if ((name.equals("") || combinations.contains("" + name.charAt(name.length() - 1) + letter))
            || vowels.contains("" + letter) != vowels.contains("" + name.charAt(name.length() - 1))
            || (vowels.contains("" + letter) && vowels.contains("" + name.charAt(name.length() - 1))
                && (name.length() < 2 || !vowels.contains("" + name.charAt(name.length() - 2))))) {
          name += letter;
          break;
        }
      }
    }
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  public static String generateColor() {
    return "#" + Integer.toHexString((int) (Math.random() * 0xcccccc));
  }

}
