package client;

import client.input.Input;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import client.input.InputJson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private final static String SERVER_ADDRESS = "127.0.0.1";
    private final static int SERVER_PORT = 23456;

    public static void main(String[] args) {
        try (Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            Input input = new Input();
            Gson gson = new Gson();
            JCommander.newBuilder().addObject(input).build().parse(args);
            InputJson inputJson = new InputJson();
            if (input.getJson() != null) {
                inputJson = gson.fromJson(Files.readString(Paths.get("src/client/data/" + input.getJson())), InputJson.class);
            } else {
                inputJson.setType(input.getType());
                inputJson.setKey(gson.toJsonTree(input.getKey()));
                inputJson.setValue(gson.toJsonTree(input.getValue()));
            }

            System.out.println("Client started!");
            String msg = gson.toJson(inputJson);

            System.out.println("Sent: " + msg);
            dos.writeUTF(msg);
            System.out.println("Received: " + dis.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
