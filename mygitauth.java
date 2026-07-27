import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.awt.Desktop;
import java.net.URI;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;

public class mygitauth {
    public static void main(String[] args) {
        try {
            // הרמת שרת HTTP מקומי בפורט 8080 לקליטת החזרה מגיטהאב 🌐
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/callback", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = exchange.getRequestURI().getQuery();
                    System.out.println("נתונים התקבלו בהצלחה מהדפדפן! 🚀");
                    
                    String responseHtml = "<html><body dir='rtl'><h2 style='font-family: Arial;'>ההתחברות הצליחה! אתה יכול לסגור את החלון הזה ולחזור לפרויקט. 🎉</h2></body></html>";
                    exchange.sendResponseHeaders(200, responseHtml.getBytes("UTF-8").length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseHtml.getBytes("UTF-8"));
                    os.close();
                    
                    // עצירת השרת לאחר קבלת הקוד ברקע בצורה נקייה 🛑
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            server.stop(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
            server.setExecutor(null);
            server.start();
            System.out.println("שרת ההזדהות רץ ברקע וממתין לחיבור ב-port 8080... 🖥️");

            // פתיחת דפדפן ברירת המחדל של ווינדוס לכתובת ההתחברות של גיטהאב 🌍
            String githubLoginUrl = "https://github.com/login/oauth/authorize?client_id=YOUR_CLIENT_ID";
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(githubLoginUrl));
                System.out.println("הדפדפן נפתח בהצלחה מול ווינדוס! ✨");
            } else {
                System.out.println("לא ניתן לפתוח את הדפדפן אוטומטית. אנא היכנס ידנית לקישור: " + githubLoginUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}