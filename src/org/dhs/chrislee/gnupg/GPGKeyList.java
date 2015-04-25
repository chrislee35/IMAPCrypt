package org.dhs.chrislee.gnupg;

import java.util.ArrayList;

/*
 * I decided not to call this a keyring, since it doesn't actually store keying material.
 */
public class GPGKeyList {
	ArrayList<GPGKeyId> keyIDs;
	
	public GPGKeyList() {
		keyIDs = new ArrayList<GPGKeyId>();
	}
	
	public void addKeyId(GPGKeyId keyId) {
		keyIDs.add(keyId);
	}
	
	protected static GPGKeyId[] arrayListToArray(ArrayList<GPGKeyId> ids) {
		GPGKeyId[] arr = new GPGKeyId[1];
		return ids.toArray(arr);
		
	}
	public GPGKeyId[] getKeyIDArray() {
		return arrayListToArray(keyIDs);
	}
	
	public GPGKeyId[] getPublicKeyIds() {
		ArrayList<GPGKeyId> myIds = new ArrayList<GPGKeyId>();
		for(GPGKeyId id : keyIDs) {
			if(id.isPublic())
				myIds.add(id);
		}
		return arrayListToArray(myIds);
	}
	
	public GPGKeyId[] getSecretKeyIds() {
		ArrayList<GPGKeyId> myIds = new ArrayList<GPGKeyId>();
		for(GPGKeyId id : keyIDs) {
			if(id.isSecret())
				myIds.add(id);
		}
		return arrayListToArray(myIds);		
	}
	
	public GPGKeyId[] getRSAKeyIds() {
		ArrayList<GPGKeyId> myIds = new ArrayList<GPGKeyId>();
		for(GPGKeyId id : keyIDs) {
			if(id.isRSA())
				myIds.add(id);
		}
		return arrayListToArray(myIds);		
	}

	public GPGKeyId[] getDSAKeyIds() {
		ArrayList<GPGKeyId> myIds = new ArrayList<GPGKeyId>();
		for(GPGKeyId id : keyIDs) {
			if(id.isDSA())
				myIds.add(id);
		}
		return arrayListToArray(myIds);		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(GPGKeyId id : keyIDs) {
			sb.append(id.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
