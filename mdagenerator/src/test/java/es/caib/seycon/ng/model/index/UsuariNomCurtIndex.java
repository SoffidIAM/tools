//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model.index;
import com.soffid.mda.annotation.*;

@Index (name="USU_UK_NOMCUR_IDDCO",	unique=true,
	entity=es.caib.seycon.ng.model.UsuariEntity.class,
	columns={"USU_NOMCUR", "USU_IDDOCO"})
public abstract class UsuariNomCurtIndex {
}

