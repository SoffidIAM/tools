//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.sync.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( internal=true,
	 serverOnly=true)
@Depends ({es.caib.seycon.ng.model.SessioEntity.class})
public abstract class ChangePasswordNotificationQueue {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public void sendNotification(
		es.caib.seycon.ng.sync.engine.ChangePasswordNotification n)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void addNotification(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.sync.engine.ChangePasswordNotification peekNotification()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void endNotificationThread()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
