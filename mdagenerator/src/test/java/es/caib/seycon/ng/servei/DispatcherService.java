//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.ControlAccessEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.model.GrupDispatcherEntity.class,
	es.caib.seycon.ng.model.TipusUsuariDispatcherEntity.class,
	es.caib.seycon.ng.model.TipusUsuariEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.ServerEntity.class,
	es.caib.seycon.ng.model.ReplicaDatabaseEntity.class,
	es.caib.seycon.ng.model.AttributeMappingEntity.class,
	es.caib.seycon.ng.model.AgentDescriptorEntity.class,
	es.caib.seycon.ng.model.ObjectMappingEntity.class,
	es.caib.seycon.ng.model.ObjectMappingPropertyEntity.class})
public abstract class DispatcherService {

	@Operation ( grantees={Roles.agent_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Dispatcher create(
		es.caib.seycon.ng.comu.Dispatcher dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class,Roles.agent_accessControl_set.class,Roles.agent_accessControl_delete.class,Roles.agent_accessControl_create.class,Roles.agent_accessControl_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Dispatcher update(
		es.caib.seycon.ng.comu.Dispatcher dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Dispatcher dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_query.class,Roles.application_update.class},
	translated="findDispatchersByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Dispatcher> findDispatchersByFiltre(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String nomCla, 
		@Nullable java.lang.String url, 
		@Nullable java.lang.String basRol, 
		@Nullable java.lang.String segur, 
		@Nullable java.lang.Boolean actiu)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_query.class},
	translated="findDispatcherByCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Dispatcher findDispatcherByCodi(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_accessControl_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ControlAcces create(
		es.caib.seycon.ng.comu.ControlAcces controlAcces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_delete.class,Roles.agent_accessControl_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.ControlAcces controlAcces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_query.class},
	translated="findAccessControlByDispatcherCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ControlAcces> findControlAccesByCodiAgent(
		java.lang.String codiAgent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_refreshUsers.class},
	translated="porpagateUsersDispatcher")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void propagateUsuarisDispatcher(
		java.lang.String codiAgent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_refreshRoles.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void propagateRolsDispatcher(
		java.lang.String codiAgent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_accessControl_update.class,Roles.agent_accessControl_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ControlAcces update(
		es.caib.seycon.ng.comu.ControlAcces controlAcces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_create.class,Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.TipusUsuariDispatcher create(
		es.caib.seycon.ng.comu.TipusUsuariDispatcher tipusUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_delete.class,Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.TipusUsuariDispatcher tipusUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_create.class,Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.GrupDispatcher create(
		es.caib.seycon.ng.comu.GrupDispatcher grupDispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_delete.class,Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.GrupDispatcher grupDispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.TipusUsuariDispatcher update(
		es.caib.seycon.ng.comu.TipusUsuariDispatcher tipusUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.GrupDispatcher update(
		es.caib.seycon.ng.comu.GrupDispatcher grupDispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_query.class},
	translated="getAccessControl")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ControlAcces> getControlAcces(
		es.caib.seycon.ng.comu.Dispatcher agent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_query.class},
	translated="getDispatcherGroups")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.GrupDispatcher> getGrupsDispatcher(
		es.caib.seycon.ng.comu.Dispatcher agent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_query.class},
	translated="getDispatcherUserTypes")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.TipusUsuariDispatcher> getTipusUsuariDispatcher(
		es.caib.seycon.ng.comu.Dispatcher agent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isUserAllowed(
		es.caib.seycon.ng.comu.Dispatcher dispatcher, 
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation ( grantees={Roles.user_role_create.class,Roles.agent_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Dispatcher> findAllActiveDispatchers()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isGroupAllowed(
		es.caib.seycon.ng.comu.Dispatcher dispatcher, 
		java.lang.String group)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation ( grantees={Roles.server_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ReplicaDatabase> findAllDatabases()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ReplicaDatabase update(
		es.caib.seycon.ng.comu.ReplicaDatabase database)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ReplicaDatabase create(
		es.caib.seycon.ng.comu.ReplicaDatabase database)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.server_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Server> findAllServers()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Server update(
		es.caib.seycon.ng.comu.Server server)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Server server)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.ReplicaDatabase database)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.server_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Server create(
		es.caib.seycon.ng.comu.Server server)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ReplicaDatabase findReplicaDatabase(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AttributeMapping create(
		es.caib.seycon.ng.comu.AttributeMapping mapping)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AttributeMapping update(
		es.caib.seycon.ng.comu.AttributeMapping mapping)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.AttributeMapping mapping)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AttributeMapping> findAttributeMappingsByObject(
		java.lang.Long dispatcherId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setDefaultMappingsByDispatcher(
		java.lang.Long dispatcherId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ObjectMapping create(
		es.caib.seycon.ng.comu.ObjectMapping om)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ObjectMapping update(
		es.caib.seycon.ng.comu.ObjectMapping om)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.ObjectMapping om)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ObjectMappingProperty create(
		es.caib.seycon.ng.comu.ObjectMappingProperty omp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ObjectMappingProperty update(
		es.caib.seycon.ng.comu.ObjectMappingProperty om)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.ObjectMappingProperty omp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ObjectMapping> findObjectMappingsByDispatcher(
		java.lang.Long dispatcherId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.agent_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ObjectMappingProperty> findObjectMappingPropertiesByObject(
		java.lang.Long objectId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
