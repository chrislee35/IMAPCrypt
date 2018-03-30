package org.dhs.chrislee.gnupg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GPGUtil {
	
	/**
	 * detect the OS, and based on the OS, try to find a copy of gpg
	 * 
	 * @return String with the full path to the GPG binary.  null if not found.
	 */
	public static String findGPG() {
		String os = System.getProperty("os.name").toLowerCase();
		String locations[];
		if(os.indexOf("win") >= 0) {
			locations = new String[] { "c:/Program Files/GNU/GnuPG/gpg2.exe" };
		} else {
			locations = new String[] { "/usr/bin/gpg2", "/usr/local/bin/gpg2", "/opt/local/bin/gpg2", "/usr/bin/gpg", "/usr/local/bin/gpg", "/opt/local/bin/gpg" };
		}
		// iterate through the lists and, on the first hit, return the location of gpg
		for(int i=0; i<locations.length; i++) {
			File f = new File(locations[i]);
			if(f.exists()) {
				return locations[i];
			}
		}
		return null;
	}
	
	public static GPGKeyList getSecretKeyIds(String gpgLoc) {
		return getKeyIds(gpgLoc, "--list-secret-keys", "sec");
	}

	public static GPGKeyList getPublicKeyIds(String gpgLoc) {
		return getKeyIds(gpgLoc, "--list-keys", "pub");
	}
	
	protected static GPGKeyList getKeyIds(String gpgLoc, String argument, String secpub) {
		GPGKeyList keyList = new GPGKeyList();
		try {
			Process p = Runtime.getRuntime().exec(gpgLoc+" "+argument);
			InputStreamReader is = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(is);
			String line;
			GPGKeyId keyId = null;
			while((line = br.readLine()) != null) {
				if(line.startsWith(secpub)) {
					keyId = new GPGKeyId(line);
					if(keyId.keyId == null)
						keyId.setKeyId(br.readLine().trim());
				} else if(line.startsWith("uid")) {
					if(line.contains("@")) {
						if(line.contains("[ expired]") || line.contains("[ revoked]")) {
							continue;
						} else {
							line = line.replaceAll("\\[ *(unknown|expired|revoked|ultimate|full|undef) *\\]", "");
						}
						String[] parts = line.split(" +", 2);
						keyId.addUid(parts[1]);
					}
				} else {
					if(keyId != null) {
						keyList.addKeyId(keyId);
						keyId = null;
					}
				}
			}
		} catch (IOException io) {
			return null;
		}
		return keyList;		
	}
	
	public static void main(String[] args) {
		GPGKeyList secKeys = GPGUtil.getSecretKeyIds("/usr/local/bin/gpg2");
		GPGKeyList pubKeys = GPGUtil.getPublicKeyIds("/usr/local/bin/gpg2");
		System.out.println("Secret Keys");
		System.out.println(secKeys.toString());
		System.out.println("Public Keys");
		System.out.println(pubKeys.toString());
	}
}
