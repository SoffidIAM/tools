//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class ParaulaProhibidaPoliticaContrasenya {

	@Nullable
	public java.lang.Long id;

	@Nullable
	@Attribute(translated = "forbiddenWord" )
	public es.caib.seycon.ng.comu.ParaulaProhibida paraulaProhibida;

	@Nullable
	@Attribute(translated = "passwordDomainPolicy" )
	public es.caib.seycon.ng.comu.PoliticaContrasenya politicaContrasenyaDomini;

}
