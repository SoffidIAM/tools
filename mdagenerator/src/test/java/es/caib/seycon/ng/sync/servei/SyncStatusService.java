//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.sync.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( serverOnly=true,
	 serverPath="SEU/SyncStatusService",
	 serverRole="SEU_CONSOLE")
@Depends ({es.caib.seycon.ng.sync.servei.TaskGenerator.class,
	es.caib.seycon.ng.sync.servei.TaskQueue.class,
	es.caib.seycon.ng.sync.servei.SecretStoreService.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	com.soffid.iam.model.MetaAccountEntity.class,
	es.caib.seycon.ng.sync.servei.ServerService.class})
public abstract class SyncStatusService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AgentStatusInfo> getSeyconAgentsInfo()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SeyconServerInfo getSeyconServerStatus()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SeyconServerInfo getSeyconServerInfo()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getDBConnectionStatus()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.SeyconServerInfo> getServerAgentHostsURL()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.FileNotFoundException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String resetAllServer()
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String resetServerAgents(
		java.lang.String server)
		throws es.caib.seycon.ng.exception.InternalErrorException, java.io.IOException, es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password getAccountPassword(
		java.lang.String user, 
		java.lang.Long accountId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public byte[] getMazingerRules(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
