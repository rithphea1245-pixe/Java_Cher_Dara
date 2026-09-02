package com.worldofwonder.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.worldofwonder.controller.AchievementController;
import com.worldofwonder.controller.AdminController;
import com.worldofwonder.controller.AuthController;
import com.worldofwonder.controller.DailyController;
import com.worldofwonder.controller.GameController;
import com.worldofwonder.controller.StatsController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class HttpServerApp {

    private final int port;

    public HttpServerApp() {
        this(8080);
    }

    public HttpServerApp(int port) {
        this.port = port;
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Register base paths WITHOUT trailing slashes so sub-paths route properly
            server.createContext("/api/auth", cors(new AuthController()));
            server.createContext("/api/users", cors(new AdminController()));

            GameController gameController = new GameController();
            server.createContext("/api/worlds", cors(gameController));
            server.createContext("/api/levels", cors(gameController));
            server.createContext("/api/questions", cors(gameController));
            server.createContext("/api/answers", cors(gameController));
            server.createContext("/api/leaderboard", cors(gameController));
            server.createContext("/api/puzzles", cors(gameController));

            // Stats & session tracking
            StatsController statsController = new StatsController();
            server.createContext("/api/stats", cors(statsController));
            server.createContext("/api/sessions", cors(statsController));

            // Achievements
            server.createContext("/api/achievements", cors(new AchievementController()));

            // Daily challenges & streaks
            DailyController dailyController = new DailyController();
            server.createContext("/api/daily", cors(dailyController));
            server.createContext("/api/streak", cors(dailyController));

            // Use a thread pool executor to handle requests concurrently without blocking connections
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("Server started on http://localhost:" + port);
        } catch (IOException e) {
            System.err.println("Failed to start server on port " + port);
            e.printStackTrace();
        }
    }

    // Centralized CORS & preflight handler wrapper
    private HttpHandler cors(HttpHandler handler) {
        return (HttpExchange exchange) -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            try {
                handler.handle(exchange);
            } catch (Throwable t) {
                t.printStackTrace();
                try {
                    byte[] response = "{\"success\":false,\"message\":\"Internal server error\"}"
                            .getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(500, response.length);
                    try (OutputStream output = exchange.getResponseBody()) {
                        output.write(response);
                        output.flush();
                    }
                } catch (IOException io) {
                    exchange.close();
                }
            }
        };
    }

    public static void main(String[] args) {
        HttpServerApp app = new HttpServerApp(8080);
        app.start();
    }
}
