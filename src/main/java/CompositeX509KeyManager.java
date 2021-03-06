
import com.sun.istack.internal.Nullable;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.X509KeyManager;

public class CompositeX509KeyManager implements X509KeyManager {

  private final List<X509KeyManager> keyManagers;

  /**
   * Creates a new {@link CompositeX509KeyManager}.
   *
   * @param keyManagers the X509 key managers, ordered with the most-preferred managers first.
   */
  public CompositeX509KeyManager(List<X509KeyManager> keyManagers) {
    this.keyManagers = Collections.unmodifiableList(keyManagers);
  }

  /**
   * Chooses the first non-null client alias returned or {@code null} if there are no matches.
   */
  @Override
  public @Nullable
  String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
    for (X509KeyManager keyManager : keyManagers) {
      String alias = keyManager.chooseClientAlias(keyType, issuers, socket);
      if (alias != null) {
        return alias;
      }
    }
    return null;
  }

  /**
   * Chooses the first non-null server alias returned or {@code null} if there are no matches.
   */
  @Override
  public @Nullable
  String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
    for (X509KeyManager keyManager : keyManagers) {
      String alias = keyManager.chooseServerAlias(keyType, issuers, socket);
      if (alias != null) {
        return alias;
      }
    }
    return null;
  }

  /**
   * Returns the first non-null private key associated with the
   * given alias, or {@code null} if the alias can't be found.
   */
  @Override
  public @Nullable
  PrivateKey getPrivateKey(String alias) {
    for (X509KeyManager keyManager : keyManagers) {
      PrivateKey privateKey = keyManager.getPrivateKey(alias);
      if (privateKey != null) {
        return privateKey;
      }
    }
    return null;
  }

  /**
   * Returns the first non-null certificate chain associated with the
   * given alias, or {@code null} if the alias can't be found.
   */
  @Override
  public @Nullable
  X509Certificate[] getCertificateChain(String alias) {
    for (X509KeyManager keyManager : keyManagers) {
      X509Certificate[] chain = keyManager.getCertificateChain(alias);
      if (chain != null && chain.length > 0) {
        return chain;
      }
    }
    return null;
  }

  /**
   * Get all matching aliases for authenticating the client side of a
   * secure socket, or {@code null} if there are no matches.
   */
  @Override
  public @Nullable
  String[] getClientAliases(String keyType, Principal[] issuers) {
    List<String> aliases = new ArrayList<>();
    for (X509KeyManager keyManager : keyManagers) {
      aliases.addAll(Arrays.asList(keyManager.getClientAliases(keyType, issuers)));
    }
    return (String[]) emptyToNull(aliases.toArray());
  }

  /**
   * Get all matching aliases for authenticating the server side of a
   * secure socket, or {@code null} if there are no matches.
   */
  @Override
  public @Nullable
  String[] getServerAliases(String keyType, Principal[] issuers) {
    List<String> aliases = new ArrayList<>();
    for (X509KeyManager keyManager : keyManagers) {
      aliases.addAll(Arrays.asList(keyManager.getServerAliases(keyType, issuers)));
    }
    return (String[]) emptyToNull(aliases.toArray());
  }

  @Nullable
  private static <T> T[] emptyToNull(T[] arr) {
    return (arr.length == 0) ? null : arr;
  }

}

