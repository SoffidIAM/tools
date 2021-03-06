//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.ServerPluginModuleEntity.class,
	es.caib.seycon.ng.model.DefaultAttributeMappingEntity.class,
	es.caib.seycon.ng.model.DefaultObjectMappingEntity.class,
	es.caib.seycon.ng.model.DefaultObjectMappingPropertyEntity.class,
	es.caib.seycon.ng.model.ServerPluginEntity.class,
	es.caib.seycon.ng.model.AgentDescriptorEntity.class})
public abstract class ServerPluginService {

	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void deployPlugin(
		byte[] i)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.servei.DuplicatedClassException {
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void enablePlugin(
		es.caib.seycon.ng.comu.ServerPlugin plugin, 
		boolean status)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.plugins_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AgentDescriptor getAgentDescriptor(
		java.lang.String className)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.plugins_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AgentDescriptor> getAgentDescriptors()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AgentDescriptor> getPluginAgentDescriptors(
		es.caib.seycon.ng.comu.ServerPlugin plugin)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ServerPlugin> listServerPlugins()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getServerVersion()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void deletePlugin(
		es.caib.seycon.ng.comu.ServerPlugin plugin)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.plugins_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AgentDescriptor> getAllAgentDescriptorsInfo()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
