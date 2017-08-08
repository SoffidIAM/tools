package com.soffid.iam.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Service;

import es.caib.signatura.api.Signature;

@Service( stateful=true, internal=false)
public class DocumentService  
{
	
	public void createDocument(String mimeType, String externalName, String application) {}
	
	public void closeDocument () {};
	
	public String getMimeType() {return null;}

	
	public String getExternalName() {return null;}

	public String getFsPath() {return null;}
		
	
}
