package io.github.wysohn.rapidframework4.utils.rest;

import spark.Route;
import spark.Service;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

public class RESTService {
    private final String host;
    private final int port;

    private final Map<String, Route> getHandlers = new HashMap<>();
    private final Map<String, Route> postHandlers = new HashMap<>();

    private Service service;

    private RESTService(String host, int port) {
        this.host = host;
        this.port = port;

        Spark.initExceptionHandler(Throwable::printStackTrace);
    }

    private RESTService start() {
        service = Service.ignite();
        service.ipAddress(host);
        service.port(port);

        getHandlers.forEach(service::get);
        postHandlers.forEach(service::post);

        service.init();
        System.out.println("Listening on " + host + ":" + port);
        return this;
    }

    public void stop() {
        service.stop();
    }

    public static class Builder {
        private final RESTService service;

        private Builder(String host, int port) {
            service = new RESTService(host, port);
        }

        public static Builder bind(String host, int port) {
            return new Builder(host, port);
        }

        public static Builder bind(int port) {
            return bind("127.0.0.1", port);
        }

        public static Builder bind() {
            return bind("127.0.0.1", 8888);
        }

        public Builder get(String path, Route handle) {
            service.getHandlers.put(path, handle);
            return this;
        }

        public Builder post(String path, Route handle) {
            service.postHandlers.put(path, handle);
            return this;
        }

        public RESTService build() {
            return service;
        }
    }

    public static void main(String[] ar) {
        RESTService service = RESTService.Builder.bind()
                .get("/test", (req, res) -> "This is test.")
                .build()
                .start();
    }
}
