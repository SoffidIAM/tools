//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class AutoritzacioPuntEntrada {

	@Nullable
	public java.lang.Long id;

	@Attribute(translated = "authorizationLevelDescription" )
	public java.lang.String descripcioNivellAutoritzacio;

	@Nullable
	@Attribute(translated = "accessTreeId" )
	public java.lang.Long idPuntEntrada;

	@Attribute(translated = "authorizationEntityType" )
	public java.lang.String tipusEntitatAutoritzada;

	@Attribute(translated = "authorizationEntityId" )
	public java.lang.Long idEntitatAutoritzada;

	@Attribute(translated = "authorizedEntityDescription" )
	public java.lang.String descripcioEntitatAutoritzada;

	@Attribute(translated = "authorizedEntityCode" )
	public java.lang.String codiEntitatAutoritzada;

}
