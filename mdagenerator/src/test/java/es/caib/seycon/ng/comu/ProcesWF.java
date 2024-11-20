//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class ProcesWF {

	@Attribute(translated = "processName" )
	public java.lang.String nomProcess;

	@Attribute(translated = "description" )
	public java.lang.String descripcio;

}
