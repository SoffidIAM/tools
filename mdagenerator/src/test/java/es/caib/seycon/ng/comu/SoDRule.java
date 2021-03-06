//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class SoDRule {

	@Nullable
	public java.lang.Long id;

	public java.lang.String name;

	public es.caib.seycon.ng.comu.SoDRisk risk;

	public java.lang.Long applicationId;

}
