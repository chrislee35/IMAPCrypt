package org.dhs.chrislee;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager tm;
    private X509Certificate[] chain;

    SavingTrustManager(X509TrustManager tm) {
    	super();
        this.tm = tm;
    }

    public X509Certificate[] getAcceptedIssuers() {
    	return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        this.chain = chain;
        tm.checkServerTrusted(chain, authType);
    }
    
    public X509Certificate[] getChain() {
    	return chain;
    }
}