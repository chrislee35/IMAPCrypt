package org.dhs.chrislee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLCertificateHelper {
	static KeyStore ks;
	
	public static X509Certificate getCertificate(String host, int port) {
		SavingTrustManager tm = null;
		try {
			if(ks == null) {
				File file = new File("jssecacerts");
		        if (file.isFile() == false) {
		            char SEP = File.separatorChar;
		            File dir = new File(System.getProperty("java.home") + SEP
		                    + "lib" + SEP + "security");
		            file = new File(dir, "jssecacerts");
		            if (file.isFile() == false) {
		                file = new File(dir, "cacerts");
		            }
		        }
		        
		        System.out.println("Loading KeyStore " + file + "...");
		        InputStream in = new FileInputStream(file);
		        ks = KeyStore.getInstance(KeyStore.getDefaultType());
		        ks.load(in, "changeit".toCharArray());
		        in.close();
			}
	        TrustManagerFactory tmf =
	                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        tmf.init(ks);
	        
	        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
	        tm = new SavingTrustManager(defaultTrustManager);
			SSLContext context = SSLContext.getInstance("TLS");
	        context.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory factory = context.getSocketFactory();
	        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
	        socket.setSoTimeout(2000);
            socket.startHandshake();
            socket.close();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		if(tm != null) {
			X509Certificate[] chain = tm.getChain();
			if(chain.length > 0)
				return chain[0];
		}
		return null;
	}
	
	public static KeyStore getCurrentKeystore() {
		return ks;
	}

	public static SSLSocketFactory getSSLSocketFactory() {
		TrustManagerFactory tmf;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
			return ctx.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void addCertToKeyStore(X509Certificate c) {
		String alias = c.getSubjectDN().toString();
		try {
			ks.setCertificateEntry(alias, c);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	public static String certificateSummary(X509Certificate certificate) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
	        MessageDigest md5 = MessageDigest.getInstance("MD5");
	        sha1.update(certificate.getEncoded());
	        md5.update(certificate.getEncoded());
	        sb.append("Subject: ");
	        sb.append(certificate.getSubjectDN());
	        sb.append("\nIssuer: ");
	        sb.append(certificate.getIssuerDN());
	        sb.append("\nSHA1: ");
	        sb.append(toHexString(sha1.digest()));
	        sb.append("\nMD5: ");
	        sb.append(toHexString(md5.digest()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }
    
}
