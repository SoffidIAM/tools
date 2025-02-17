//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class TipusExecucioPuntEntrada {

	public java.lang.Long id;

	@Attribute(translated = "code" )
	public java.lang.String codi;

	@Attribute(translated = "mimeType" )
	public java.lang.String tipusMime;

	@Attribute(translated = "template" )
	public java.lang.String plantilla;

}
