//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( internal=true)
public abstract class SessionCacheService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Object getObject(
		java.lang.String tag)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void putObject(
		java.lang.String tag, 
		java.lang.Object value)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
