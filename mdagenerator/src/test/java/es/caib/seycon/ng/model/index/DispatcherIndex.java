//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model.index;
import com.soffid.mda.annotation.*;

@Index (name="DIS_UK_CODI",	unique=true,
	entity=es.caib.seycon.ng.model.DispatcherEntity.class,
	columns={"DIS_CODI"})
public abstract class DispatcherIndex {
}

