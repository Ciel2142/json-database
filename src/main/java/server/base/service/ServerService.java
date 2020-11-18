package server.base.service;

import server.base.handler.ClientRunner;
import server.base.repository.JsonDataBase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerService {

    private final String HOST;
    private final int PORT;
    private final JsonDataBase dataBase;
    private final int threads;

    public ServerService(JsonDataBase dataBase) {
        this("127.0.0.1", 23456, dataBase, 4);
    }

    public ServerService (String host, JsonDataBase dataBase) {
        this(host, 23456, dataBase, 4);
    }
    public ServerService(String host, int port, JsonDataBase dataBase, int threads) {
        this.HOST = host;
        this.PORT = port;
        this.dataBase = dataBase;
        this.threads = threads;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(HOST))) {
            System.out.println("Server started!");
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            while (!serverSocket.isClosed()) {
                ClientRunner clientRunner = new ClientRunner(serverSocket.accept(), dataBase, serverSocket, executorService);
                executorService.execute(clientRunner);
            }
            executorService.awaitTermination(200L, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException ignored) {
        }
    }

}
