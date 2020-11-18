package server;

import com.google.gson.Gson;
import server.base.service.ServerService;
import server.base.repository.JsonDataBase;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerService serverService;
        String fileName = "server/data/db.json";
        Path pathToFile = Paths.get(fileName);
        File file = new File(fileName);

        if (file.exists()) {
            try (Reader reader = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
                if (reader.ready()) {
                    Gson gson = new Gson();
                    serverService = new ServerService(gson.fromJson(reader, JsonDataBase.class));
                } else {
                    serverService = new ServerService(new JsonDataBase());
                }
            }
        } else {
            serverService = new ServerService(new JsonDataBase());
        }

        serverService.startServer();
    }

}

