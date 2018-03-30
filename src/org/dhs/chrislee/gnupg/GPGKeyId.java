package org.dhs.chrislee.gnupg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GPGKeyId {
	static final int KEYTYPE_PUBLIC = 0;
	static final int KEYTYPE_SECRET = 1;
	static final int KEYALGO_RSA = 2;
	static final int KEYALGO_DSA = 3;
	
	int keyType;
	int keyBits;
	int keyAlgo;
	String keyId;
	Date expiration;
	ArrayList<String> uids;
	
	public GPGKeyId(int keyType, int keyBits, int keyAlgo, String keyId, Date expiration) {
		this.keyType = keyType;
		this.keyBits = keyBits;
		this.keyAlgo = keyAlgo;
		this.keyId = keyId;
		this.expiration = expiration;
		this.uids = new ArrayList<String>();
	}
	
	public GPGKeyId(String gpgLine) {
		String[] parts = gpgLine.split(" +");
		if(parts[0].startsWith("sec"))
			keyType = KEYTYPE_SECRET;
		else if(parts[0].startsWith("pub"))
			keyType = KEYTYPE_PUBLIC;
		else
			throw new IllegalArgumentException("Could not parse the key type from line.");
		if(parts[1].contains("/")) {
			String[] keyAlgoId = parts[1].split("/");
			keyId = keyAlgoId[1];
			keyBits = Integer.parseInt(keyAlgoId[0].substring(0, keyAlgoId[0].length()-1));
			if(keyAlgoId[0].endsWith("R"))
				keyAlgo = GPGKeyId.KEYALGO_RSA;
			else if(keyAlgoId[0].endsWith("D"))
				keyAlgo = GPGKeyId.KEYALGO_DSA;
			else
				throw new IllegalArgumentException("Could not parse the key algorithm from line.");
		} else { // For MacGPG
			if(parts[1].contains("rsa"))
				keyAlgo = GPGKeyId.KEYALGO_RSA;
			else if(parts[1].contains("dsa"))
				keyAlgo = GPGKeyId.KEYALGO_DSA;
			else
				throw new IllegalArgumentException("Could not parse the key algorithm from line.");
			keyBits = Integer.parseInt(parts[1].substring(3, parts[1].length()));
			keyId = null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			expiration = df.parse(parts[2]);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date: "+parts[2]);
		}
		// keyId is on the next line 
		this.uids = new ArrayList<String>();
	}

	public void addUid(String uid) {
		uids.add(uid);
	}
	
	public boolean isSecret() {
		return (keyType == KEYTYPE_SECRET);
	}
	
	public boolean isPublic() {
		return (keyType == KEYTYPE_PUBLIC);
	}
	
	public int getKeyType() {
		return keyType;
	}
	
	public void setKeyType(int keyType) {
		if(keyType == KEYTYPE_SECRET || keyType == KEYTYPE_PUBLIC) {
			this.keyType = keyType;
		} else {
			throw new java.lang.IllegalArgumentException("Key type must either be KEYTYPE_SECRET or KEYTYPE_PUBLIC");
		}
	}
	
	public boolean isRSA() {
		return(keyAlgo == KEYALGO_RSA);
	}
	
	public boolean isDSA() {
		return(keyAlgo == KEYALGO_DSA);
	}
	
	public boolean isExpired() {
		Date today = new Date();
		return(today.getTime() > expiration.getTime());
	}
	
	public Date getExpiration() {
		return expiration;
	}
	
	public void setExpiration(Date exp) {
		expiration = exp;
	}
	
	public int getKeyBits() {
		return keyBits;
	}
	
	public void setKeyBits(int keyBits) {
		if(keyBits < 768 || keyBits > 8192) {
			throw new java.lang.IllegalArgumentException("Key length should be 768 bits up to 8192 bits");
		} else {
			this.keyBits = keyBits;
		}
	}
	
	public int getKeyAlgo() {
		return keyAlgo;
	}
	
	public void setKeyAlgo(int keyAlgo) {
		if(keyAlgo == KEYALGO_DSA || keyAlgo == KEYALGO_RSA) {
			this.keyAlgo = keyAlgo;
		} else {
			throw new IllegalArgumentException("Key algorithm should be one of KEYALGO_DSA or KEYALGO_RSA");
		}
	}
	
	public String getKeyId() {
		return keyId;
	}
	
	public void setKeyId(String keyId) {
		if(keyId == null || (keyId.length() != 8 && keyId.length() != 40))
			throw new IllegalArgumentException("Key IDs should not be null and be exactly 8 or 40 characters long.");
		if(keyId.toUpperCase().matches("[0-9A-F]{8}") || keyId.toUpperCase().matches("[0-9A-F]{40}"))
			this.keyId = keyId.toUpperCase();
		else
			throw new IllegalArgumentException("The key ID must be an 8 or 40-character hex string.");
	}
	
	public ArrayList<String> getUids() {
		return uids;
	}
	
	public String[] getUidArray() {
		String[] t = new String[1];
		return uids.toArray(t);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(isSecret())
			sb.append("sec ");
		else
			sb.append("pub ");
		sb.append(keyBits);
		if(isRSA())
			sb.append("R");
		else if(isDSA())
			sb.append("D");
		else
			sb.append("?");
		sb.append("/");
		sb.append(keyId);
		for(String uid : getUids()) {
			sb.append("\t");
			sb.append(uid);
		}
		return sb.toString();
	}
}
