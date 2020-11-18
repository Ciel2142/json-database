package server.base.handler;

import com.google.gson.*;
import server.responses.InputJson;
import server.base.repository.JsonDataBase;
import server.responses.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class ClientRunner implements Runnable {

    private final Socket socket;
    private final JsonDataBase db;
    private final ServerSocket server;
    private final static String OK = "OK";
    private final static String ERROR = "ERROR";
    private final ExecutorService executor;

    public ClientRunner(Socket socket, JsonDataBase dataBase, ServerSocket exit, ExecutorService executorService) {
        this.executor = executorService;
        this.socket = socket;
        this.db = dataBase;
        this.server = exit;
    }

    @Override
    public void run() {
        try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            try {
                final Gson gson = new Gson();
                final Response response = new Response();
                String json = dis.readUTF();
                final InputJson input = gson.fromJson(json, InputJson.class);
                final JsonArray path = getJsonElements(input);

                System.out.println("Received: " + json);

                switch (input.getType().toLowerCase()) {
                    case "exit":
                        response.setResponse(OK);
                        server.close();
                        executor.shutdown();
                        break;
                    case "get":
                        Optional<JsonElement> value = db.get(path);
                        if (value.isEmpty()) {
                            response.setResponse(ERROR);
                            response.setReason("No such key");
                        } else {
                            response.setResponse(OK);
                            response.setValue(value.get());
                        }
                        break;
                    case "delete":
                        if (db.delete(path).isEmpty()) {
                            response.setResponse(ERROR);
                            response.setReason("No such key");
                        } else {
                            response.setResponse(OK);
                        }
                        break;
                    case "set":
                        db.set(path, gson.toJsonTree(input.getValue()));
                        response.setResponse(OK);
                        break;
                }
                json = gson.toJson(response);
                dos.writeUTF(json);
                System.out.println("Sent: " + json);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonArray getJsonElements(InputJson input) {
        JsonArray path;
        if (input.getKey() == null) {
            path = null;
        } else if (input.getKey().isJsonArray()) {
            path = input.getKey().getAsJsonArray();
        } else {
            path = new JsonArray();
            path.add(input.getKey().toString());
        }
        return path;
    }

}
