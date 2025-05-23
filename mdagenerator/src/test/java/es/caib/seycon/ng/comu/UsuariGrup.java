//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class UsuariGrup {

	@Attribute(translated = "user" )
	public java.lang.String codiUsuari;

	@Attribute(translated = "group" )
	public java.lang.String codiGrup;

	@Nullable
	@Attribute(translated = "groupDescription" )
	public java.lang.String descripcioGrup;

	@Nullable
	public java.lang.Long id;

	@Attribute(translated = "fullName" )
	public java.lang.String nomComplet;

	@Nullable
	public java.lang.String info;

}
