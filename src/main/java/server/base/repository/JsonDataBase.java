package server.base.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class JsonDataBase {

    private final JsonObject data;
    private final String fileName;
    private final transient Semaphore writeLock = new Semaphore(1);
    private final transient Semaphore readLock = new Semaphore(1);

    public JsonDataBase() {
        this(new JsonObject(), "srs/server/data/db.json");
    }

    public JsonDataBase(JsonObject data) {
        this(data, "srs/server/data/db.json");
    }

    public JsonDataBase(JsonObject data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }

    public void set(JsonArray path, JsonElement element) {
        try {
            writeLock.acquire();
            readLock.acquire();

            JsonObject head = data;
            JsonElement key;
            for (int i = 0; i < path.size() - 1; i++) {
                key = path.get(i);
                if (head.has(key.getAsString()) && head.get(key.getAsString()).isJsonObject()) {
                    head = head.get(key.getAsString()).getAsJsonObject();
                } else {
                    JsonObject newObj = new JsonObject();
                    head.add(key.getAsString(), newObj);
                    head = newObj;
                }
            }
            String k = parseKey(path.get(path.size() - 1).getAsString());
            head.add(k, element);
            System.out.println(data.toString());
            commitDb();

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } finally {
            writeLock.release();
            readLock.release();
        }
    }

    public Optional<JsonElement> get(JsonArray path) {
        try {
            if (path.size() == 0) {
                return Optional.empty();
            }
            readLock.acquire();
            // Get location of a node inside json tree
            JsonObject head = iterateToGetOrDelete(data, path);
            // Parse key from rubbish which might be there
            String key = parseKey(path.get(path.size() - 1).getAsString());
            if (!head.has(key)) {
                return Optional.empty();
            }
            return Optional.ofNullable(head.get(key));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.release();
        }
        return Optional.empty();
    }

    public Optional<JsonElement> delete(JsonArray path) {
        try {
            writeLock.acquire();
            readLock.acquire();
            // Get location of a node inside json tree
            JsonObject head = iterateToGetOrDelete(data, path);
            // Parse key from rubbish which might be there
            String key = parseKey(path.get(path.size() - 1).getAsString());
            if (!head.has(key)) {
                return Optional.empty();
            }
            Optional<JsonElement> deleted = Optional.ofNullable(head.remove(key));
            commitDb();
            return deleted;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.release();
            readLock.release();
        }
        return Optional.empty();
    }

    private void commitDb() {
        /* Commits any changes to database */
        Path path = Paths.get(fileName);

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            JsonElement tree = gson.toJsonTree(this);
            gson.toJson(tree, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject iterateToGetOrDelete(JsonObject head, JsonArray path) {
        JsonElement key;
        for (int i = 0; i < path.size() - 1; i++) {
            key = path.get(i);
            if (head.has(key.getAsString()) && head.get(key.getAsString()).isJsonObject()) {
                head = head.get(key.getAsString()).getAsJsonObject();
            } else {
                return head;
            }
        }
        return head;
    }

    private String parseKey(String path) {
        if (path.startsWith("\"")) {
            path = path.substring(1, path.length() - 1);
        }
        return path;
    }
}
