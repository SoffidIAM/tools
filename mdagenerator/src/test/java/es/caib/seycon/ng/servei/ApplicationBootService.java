//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service public abstract class ApplicationBootService {

	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.SUPPORTS ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void syncServerBoot()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.SUPPORTS ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void consoleBoot()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
