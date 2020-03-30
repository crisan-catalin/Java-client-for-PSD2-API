import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main {

  public static void main(String[] args) throws Exception {

    final TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                         String authType) throws CertificateException {
          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                         String authType) throws CertificateException {
          }

          @Override
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };

    String keyStoreType = KeyStore.getDefaultType();
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);

    final X509Certificate cert = readCertificate("example_eidas_client_tls.cer");
    keyStore.setCertificateEntry("cert", cert);

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
    tmf.init(keyStore);

    final SSLContext sslContext = SSLContext.getInstance("SSL");

    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .sslSocketFactory(sslContext.getSocketFactory())
        .hostnameVerifier((hostname, session) -> true);

    OkHttpClient client = builder.build();
    Request request = new Request.Builder()
        .url("https://api.sandbox.ing.com/oauth2/token")
        .method("POST", new FormBody.Builder().add("grant_type", "client_credentials").build())

        .addHeader("x-request-id", "30fb2676-8c2e-11e9-b683-526af7764f64")
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Digest", "SHA-256=w0mymuL8aCrbJmmabs1pytZhon8lQucTuJMUtuKr+uw=")
        .addHeader("Date", "Mon, 30 Mar 2020 13:09:26 GMT")
        .addHeader("TPP-Signature-Certificate",
                   "-----BEGIN CERTIFICATE-----MIIENjCCAx6gAwIBAgIEXkKZvjANBgkqhkiG9w0BAQsFADByMR8wHQYDVQQDDBZBcHBDZXJ0aWZpY2F0ZU1lYW5zQVBJMQwwCgYDVQQLDANJTkcxDDAKBgNVBAoMA0lORzESMBAGA1UEBwwJQW1zdGVyZGFtMRIwEAYDVQQIDAlBbXN0ZXJkYW0xCzAJBgNVBAYTAk5MMB4XDTIwMDIxMDEyMTAzOFoXDTIzMDIxMTEyMTAzOFowPjEdMBsGA1UECwwUc2FuZGJveF9laWRhc19xc2VhbGMxHTAbBgNVBGEMFFBTRE5MLVNCWC0xMjM0NTEyMzQ1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkJltvbEo4/SFcvtGiRCar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0JVOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5XylqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8VCfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwYDVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgwCgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGUFNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRpttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rWRcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKSsTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq+s=-----END CERTIFICATE-----")
        .addHeader("authorization",
                   "Signature keyId=\"SN=5E4299BE\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"TfwuRawj0TRrpCIwM4BUwKNYPZvchGpGfM0budabtEpMm/1aUMfyq9o5iWq/HOzUc5UDcYUjqwBvFLMJQmR/nNFcNjbcpcO1Se3KNJn26yxVGfYXbz+c1gMweh+vxeQOIXT1wkpq3AHzSchWFzQRYY25iXPVPqug74D+mciBh2QBAqRGgnt18zCe7H7wPhHJ+eMjGddzhfmtttwpyXJNe7s0qe+OSx1YO2j4vze5ake/9udBzwVz/QiFv5wochCTCNYKzC8Ejje3I713AZQSrsWGw+hkWFFsI4W24z2n58pDc6gCWpt2MmSWKGa1N1mM0irc8L91p3G1dw482VK0+g==\"/SFcvtGiRCar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0JVOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5XylqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8VCfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwYDVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgwCgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGUFNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRpttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rWRcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKSsTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq+s=-----END CERTIFICATE-----")
        .build();

    Response response = client.newCall(request).execute();

    String jsonResponse = response.body().string();
    System.out.println(jsonResponse);

  }

  private static X509Certificate readCertificate(String filename) throws Exception {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    return (X509Certificate) certificateFactory.generateCertificate(
        Main.class.getClassLoader().getResourceAsStream(filename));
  }

}
