//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class Domini {

	@Nullable
	public java.lang.Long id;

	@Attribute(translated = "name" )
	public java.lang.String nom;

	@Nullable
	@Attribute(translated = "externalCode" )
	public java.lang.String codiExtern;

	@Nullable
	@Attribute(translated = "description" )
	public java.lang.String descripcio;

}
