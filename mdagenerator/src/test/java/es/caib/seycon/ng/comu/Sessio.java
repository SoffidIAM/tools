//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class Sessio {

	@Attribute(translated = "userCode" )
	public java.lang.String codiUsuari;

	@Attribute(translated = "serverHostName" )
	public java.lang.String nomMaquinaServidora;

	@Nullable
	@Attribute(translated = "clientHostName" )
	public java.lang.String nomMaquinaClient;

	@Nullable
	public java.lang.Long port;

	@Nullable
	public java.lang.Long id;

	@Nullable
	@Attribute(translated = "userFullName" )
	public java.lang.String nomComplertUsuari;

	@Attribute(translated = "startDate" )
	public java.util.Calendar dataInici;

	@Nullable
	@Attribute(translated = "key" )
	public java.lang.String clau;

	@Nullable
	public java.util.Calendar dataKeepAlive;

	@Nullable
	@Attribute(translated = "temporaryKey" )
	public java.lang.String clauTemporal;

	@Nullable
	@Attribute(translated = "accessLogId" )
	public java.lang.Long idRegistreAccess;

	@Nullable
	public java.lang.String url;

}
