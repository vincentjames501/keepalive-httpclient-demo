import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http2.Http2;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        String helpText = "Valid commands are: native, okhttp or apache";
        if (args.length == 0) {
            System.out.println("Command required. " + helpText);
            System.exit(1);
        }
        String cmd = args[0];
        if ("native".equalsIgnoreCase(cmd)) {
            nativeClientTest();
        } else if ("okhttp".equalsIgnoreCase(cmd)) {
            okHttpClientTest();
        } else if ("apache".equalsIgnoreCase(cmd)) {
            apacheTest();
        } else {
            System.out.println("Invalid command. " + helpText);
        }
    }

    private static void nativeClientTest() throws InterruptedException, IOException {
        System.setProperty("jdk.httpclient.HttpClient.log", "all");

        HttpClient client = HttpClient.newBuilder().build();

        System.out.println(Instant.now() + " - Making first request");
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml")).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(Instant.now() + " - First request complete. Sleeping for 6 minutes.");
        Thread.sleep(TimeUnit.MINUTES.toMillis(6));

        System.out.println(Instant.now() + " - Making second request");
        HttpRequest request2 = HttpRequest.newBuilder(URI.create("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml")).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        System.out.println(Instant.now() + " - Second request complete.");
    }

    private static void okHttpClientTest() throws IOException, InterruptedException {
        DebugLogging.enable(Http2.class);
        DebugLogging.enable(TaskRunner.class);

        OkHttpClient client = new OkHttpClient();

        System.out.println(Instant.now() + " - Making first request");

        Request request = new Request.Builder().url("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml").build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(Instant.now() + " - First request complete. Sleeping for 6 minutes.");
        }
        Thread.sleep(TimeUnit.MINUTES.toMillis(6));

        System.out.println(Instant.now() + " - Making second request");
        Request request2 = new Request.Builder().url("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml").build();
        try (Response response2 = client.newCall(request2).execute()) {
            System.out.println(Instant.now() + " - Second request complete.");
        }
    }

    private static void apacheTest() throws InterruptedException, ExecutionException, IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createHttp2Default();
        httpclient.start();
        System.out.println(Instant.now() + " - Making first request");

        Future<SimpleHttpResponse> response = httpclient.execute(new SimpleHttpRequest("GET", "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml"), new FutureCallback<>() {
            @Override
            public void completed(SimpleHttpResponse result) {
            }

            @Override
            public void failed(Exception ex) {
            }

            @Override
            public void cancelled() {
            }
        });
        response.get();

        System.out.println(Instant.now() + " - First request complete. Sleeping for 6 minutes.");
        Thread.sleep(TimeUnit.MINUTES.toMillis(6));

        System.out.println(Instant.now() + " - Making second request");
        Future<SimpleHttpResponse> response2 = httpclient.execute(new SimpleHttpRequest("GET", "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml"), new FutureCallback<>() {
            @Override
            public void completed(SimpleHttpResponse result) {
            }

            @Override
            public void failed(Exception ex) {
            }

            @Override
            public void cancelled() {
            }
        });
        response2.get();
        System.out.println(Instant.now() + " - Second complete.");
        httpclient.close();
    }
}
