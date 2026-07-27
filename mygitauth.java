import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class mygitauth {
    public static void main(String[] args) {
        try {
            // טעינת ה-Client ID מתוך קובץ ה-config.yml או שימוש בברירת מחדל
            String clientId = "YOUR_CLIENT_ID";
            try {
                List<String> lines = Files.readAllLines(Paths.get("config.yml"));
                for (String line : lines) {
                    if (line.trim().startsWith("client_id")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            clientId = parts[1].trim().replace("\"", "").replace("'", "");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ קובץ config.yml לא נמצא, משתמש בברירת מחדל.");
            }

            // הפעלת שרת מקומי בפורט 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            final String finalClientId = clientId;

            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = exchange.getRequestURI().getQuery();
                    String response;

                    // בדיקה אם קיבלנו קוד אישור חזרה מ-GitHub
                    if (query != null && query.contains("code=")) {
                        response = "<h1>Authentication Successful! 🎉</h1><p>MyGit is connected to GitHub successfully. You can close this window now.</p>";
                    } else {
                        // הפניה אוטומטית לעמוד ההתחברות הרשמי של GitHub עם ה-Client ID
                        String authUrl = "https://github.com/login/oauth/authorize?client_id=" + finalClientId;
                        exchange.getResponseHeaders().set("Location", authUrl);
                        exchange.sendResponseHeaders(302, -1);
                        return;
                    }

                    exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes("UTF-8"));
                    os.close();
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("🚀 השרת רץ בפורט 8080! פותח את הדפדפן להתחברות ל-GitHub...");

            // פתיחת הדפדפן אוטומטית
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
