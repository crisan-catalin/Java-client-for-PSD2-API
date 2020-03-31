import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main {

  public static void main(String[] args) throws Exception {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
//        .sslSocketFactory(getFactory("ing_key.p12", "1234"));
        .sslSocketFactory(getFactory("bcr_key.p12", "1234"));

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

//    Request request = ingRequest();
    Request request = bcrRequest();

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
          .addHeader("Authorization", "Bearer ewogICJ0eXBlIjogInRva2VuIiwKICAibmFtZSI6ICI4OGM3YTFjYS01YTAzLTRlZmQtODZiZS0zZWMxNGNiY2JjYTYiLAogICJzZXNzaW9uVVVJRCI6ICI4MGY5MGRjMS01ZTk2LTRmM2ItODA3OC01OGI2Yjc2NmM0MDkiLAogICJzY29wZXMiOiBbCiAgICAiQUlTUCIsCiAgICAiUElJU1AiLAogICAgIlBJU1AiCiAgXSwKICAiY29uc2VudCI6IFsKICAgIHsKICAgICAgImlkIjogIjg4YzdhMWNhLTVhMDMtNGVmZC04NmJlLTNlYzE0Y2JjYmNhNiIsCiAgICAgICJjb250ZW50IjogImZ1bGwiCiAgICB9CiAgXSwKICAibGltaXRzIjogewogICAgImFjY2Vzc1NlY29uZHMiOiAzNjAwLAogICAgInJlZnJlc2hTZWNvbmRzIjogNzc3NjAwMAogIH0sCiAgImFjY2Vzc1R5cGUiOiAib25saW5lIiwKICAiZXhwaXJhdGlvbiI6ICIyMDIwLTAzLTI0VDE3OjQzOjA1LjM5M1oiCn0%3D")
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
          .addHeader("Date", "Tue, 31 Mar 2020 08:37:27 GMT")
          .addHeader("TPP-Signature-Certificate",
                     "-----BEGIN CERTIFICATE-----MIIENjCCAx6gAwIBAgIEXkKZvjANBgkqhkiG9w0BAQsFADByMR8wHQYDVQQDDBZBcHBDZXJ0aWZpY2F0ZU1lYW5zQVBJMQwwCgYDVQQLDANJTkcxDDAKBgNVBAoMA0lORzESMBAGA1UEBwwJQW1zdGVyZGFtMRIwEAYDVQQIDAlBbXN0ZXJkYW0xCzAJBgNVBAYTAk5MMB4XDTIwMDIxMDEyMTAzOFoXDTIzMDIxMTEyMTAzOFowPjEdMBsGA1UECwwUc2FuZGJveF9laWRhc19xc2VhbGMxHTAbBgNVBGEMFFBTRE5MLVNCWC0xMjM0NTEyMzQ1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkJltvbEo4/SFcvtGiRCar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0JVOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5XylqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8VCfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwYDVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgwCgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGUFNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRpttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rWRcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKSsTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq+s=-----END CERTIFICATE-----")
          .addHeader("authorization",
                     "Signature keyId=\"SN=5E4299BE\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"XfAf2jxBGZgl1/co+wKniDMxC5Rta6V7nBgyuW4C0xJrxfL1rDvWHMnhQ+3MLn40oMrKYdp0aJck5sR+5B/NmlKuNRqjTiKyE1dcT0MicNYWAd9BcQwsuKoUMT3r4tWy/Iyh8munZ7HBcV5NJNZqlm/LngJyduesT/J5wxFkEg4hyiVAXahYq++dtjq6TXtSpyF68eLMWLKb9r61oTAd0ji1SfsJEFSA4cous3iXVdA0DFBNnoHXkGlbwK//Y2E9eIFqtRiIN0Tg+oLijcToK3FiwIrXlZI4L13tjXXY2XnIzqWEqFdW6Z4gkx/4vdE97IZKARFrI2JpzbG47EfWdQ==\"")
          .build();
  }

  private static SSLSocketFactory getFactory(String fileName, String password)
      throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException,
             KeyManagementException {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(Main.class.getClassLoader().getResourceAsStream(fileName), password.toCharArray());
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, password.toCharArray());

    SSLContext context = SSLContext.getInstance("SSL");
    context.init(
        keyManagerFactory.getKeyManagers(),
        getTrustManagers(),
        new SecureRandom()
    );
    return context.getSocketFactory();
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
