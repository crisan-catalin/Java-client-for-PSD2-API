import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main {

  public static void main(String[] args) throws Exception {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .sslSocketFactory(getFactory(new String[]{"ing_key.p12", "bcr_key.p12"}, "1234"));

    OkHttpClient client = builder.build();
//    MediaType mediaType = MediaType.parse("application/json,text/plain");
//    RequestBody body = RequestBody.create(mediaType,
//                                          "{\n  \"access\": {\n    \"allPsd2\": \"allAccounts\"\n  },\n  \"recurringIndicator\": true,\n  \"validUntil\": \"2020-03-31\",\n  \"frequencyPerDay\": 4,\n  \"combinedServiceIndicator\": false\n}");
//    Request request = new Request.Builder()
//        .url("https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-corporate-aisp-demo/v1/consents")
//        .method("POST", body)
//        .addHeader("Accept", "application/json")
//        .addHeader("Content-Type", "application/json")
//        .addHeader("PSU-ID", "31111111")
//        .addHeader("PSU-IP-Address", "192.168.8.78")
//        .addHeader("X-Request-ID", "99391c7e-ad88-49ec-a2ad-99ddcb1f7721")
//        .build();

    Request request = ingRequest();
//    Request request = bcrRequest();

    Response response = client.newCall(request).execute();
    String jsonResponse = response.body().string();
    System.out.println(jsonResponse);
  }

  private static Request bcrRequest() {
    return new Request.Builder()
        .url("https://webapi.developers.erstegroup.com/api/bcr/sandbox/v1/aisp/v1/accounts")
        .method("GET", null)
        .addHeader("x-request-id", "30fb2676-8c2e-11e9-b683-526af7764f64")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("web-api-key", "a863b914-e8f9-47ae-b49d-1ca8af1628c4")
        .addHeader("Authorization",
                   "Bearer ewogICJ0eXBlIjogInRva2VuIiwKICAibmFtZSI6ICI4OGM3YTFjYS01YTAzLTRlZmQtODZiZS0zZWMxNGNiY2JjYTYiLAogICJzZXNzaW9uVVVJRCI6ICI4MGY5MGRjMS01ZTk2LTRmM2ItODA3OC01OGI2Yjc2NmM0MDkiLAogICJzY29wZXMiOiBbCiAgICAiQUlTUCIsCiAgICAiUElJU1AiLAogICAgIlBJU1AiCiAgXSwKICAiY29uc2VudCI6IFsKICAgIHsKICAgICAgImlkIjogIjg4YzdhMWNhLTVhMDMtNGVmZC04NmJlLTNlYzE0Y2JjYmNhNiIsCiAgICAgICJjb250ZW50IjogImZ1bGwiCiAgICB9CiAgXSwKICAibGltaXRzIjogewogICAgImFjY2Vzc1NlY29uZHMiOiAzNjAwLAogICAgInJlZnJlc2hTZWNvbmRzIjogNzc3NjAwMAogIH0sCiAgImFjY2Vzc1R5cGUiOiAib25saW5lIiwKICAiZXhwaXJhdGlvbiI6ICIyMDIwLTAzLTI0VDE3OjQzOjA1LjM5M1oiCn0%3D")
        .build();
  }

  private static Request ingRequest() {
    return new Request.Builder()
        .url("https://api.sandbox.ing.com/oauth2/token")
        .method("POST", new FormBody.Builder().add("grant_type", "client_credentials").build())

        .addHeader("x-request-id", "30fb2676-8c2e-11e9-b683-526af7764f64")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Digest", "SHA-256=w0mymuL8aCrbJmmabs1pytZhon8lQucTuJMUtuKr+uw=")
        .addHeader("Date", "Tue, 31 Mar 2020 12:39:01 GMT")
        .addHeader("TPP-Signature-Certificate",
                   "-----BEGIN CERTIFICATE-----MIIENjCCAx6gAwIBAgIEXkKZvjANBgkqhkiG9w0BAQsFADByMR8wHQYDVQQDDBZBcHBDZXJ0aWZpY2F0ZU1lYW5zQVBJMQwwCgYDVQQLDANJTkcxDDAKBgNVBAoMA0lORzESMBAGA1UEBwwJQW1zdGVyZGFtMRIwEAYDVQQIDAlBbXN0ZXJkYW0xCzAJBgNVBAYTAk5MMB4XDTIwMDIxMDEyMTAzOFoXDTIzMDIxMTEyMTAzOFowPjEdMBsGA1UECwwUc2FuZGJveF9laWRhc19xc2VhbGMxHTAbBgNVBGEMFFBTRE5MLVNCWC0xMjM0NTEyMzQ1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkJltvbEo4/SFcvtGiRCar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0JVOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5XylqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8VCfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwYDVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgwCgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGUFNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRpttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rWRcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKSsTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq+s=-----END CERTIFICATE-----")
        .addHeader("authorization",
                   "Signature keyId=\"SN=5E4299BE\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"RligtmeoOOIjx6hz2nqfo2iTqT5EpOHQGq7cU4z1V5v23tjIr6PFpajc3Be3C/spN28JGqDdggam7fIDgvEvdOrrctb5Rj7OnAOnM09Na0mwgiXqE9sM+bOZc3Ym4DGQo+lqVXZvnPKsacs9VdbryCIjBncn030Iyj7BQMzyzawOx/RuUPeiXlj7mR/w1hUJX6xlAaK+ITlDcJ2USbP7CWNlDno0zEwVtWl0bCeKQS1oMut3nv4jG8EHhS2uZo5Vso3kkNlrHoRbcu+PTF+8vCqUgDdGYVY22M1YjfrPFxdZwyO7yJ6aA8WEvAPOmRYoNWTwr2cAt8RT9EzWPDmDlA==\"")
        .build();
  }

  private static SSLSocketFactory getFactory(String[] fileNames, String password) throws Exception {
    List<X509KeyManager> keyManagerList = new ArrayList<>();
    for (String fileName : fileNames) {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(Main.class.getClassLoader().getResourceAsStream(fileName), password.toCharArray());
      keyManagerList.add(getKeyManager("SunX509", keyStore, "1234".toCharArray()));
    }

    X509KeyManager jvmKeyManager = getKeyManager(KeyManagerFactory.getDefaultAlgorithm(), null, null);
    keyManagerList.add(jvmKeyManager);

    KeyManager[] keyManagers = {new CompositeX509KeyManager(keyManagerList)};

    SSLContext context = SSLContext.getInstance("SSL");
    context.init(
        keyManagers,
        getTrustManagers(),
        new SecureRandom()
    );
    return context.getSocketFactory();
  }

  private static X509KeyManager getKeyManager(String algorithm, KeyStore keystore, char[] password)
      throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
    KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
    factory.init(keystore, password);

    return (X509KeyManager) Arrays.stream(factory.getKeyManagers())
        .filter(keyManager -> keyManager instanceof X509KeyManager)
        .findFirst()
        .orElse(null);
  }

  private static TrustManager[] getTrustManagers() {
    return new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };
  }
}
