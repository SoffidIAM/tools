//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.AliasMaquinaEntity.class,
	es.caib.seycon.ng.model.AutoritzacioAccesHostComAdministradorEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.model.RegistreAccesEntity.class,
	es.caib.seycon.ng.model.SessioEntity.class,
	es.caib.seycon.ng.model.MaquinaEntity.class,
	es.caib.seycon.ng.model.OsTypeEntity.class,
	es.caib.seycon.ng.model.XarxaEntity.class,
	es.caib.seycon.ng.model.XarxaACEntity.class})
public abstract class XarxaService {

	@Operation ( grantees={Roles.host_all_query.class},
	translated="getNetworks")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Xarxa> getXarxes()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Xarxa create(
		es.caib.seycon.ng.comu.Xarxa xarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		es.caib.seycon.ng.comu.Xarxa xarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.network_all_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Xarxa xarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> getACL(
		es.caib.seycon.ng.comu.Xarxa xarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.Tothom.class},
	translated="findNetworkByCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Xarxa findXarxaByCodi(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_create.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina create(
		es.caib.seycon.ng.comu.Maquina maquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_update.class,Roles.host_update_os.class,Roles.host_all_query.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		es.caib.seycon.ng.comu.Maquina maquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.host_all_delete.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Maquina maquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.agent_query.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="findHostByName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findMaquinaByNom(
		java.lang.String nom)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="findHostByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> findMaquinaByFiltre(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String sistemaOperatiu, 
		@Nullable java.lang.String adreca, 
		@Nullable java.lang.String dhcp, 
		@Nullable java.lang.String correu, 
		@Nullable java.lang.String ofimatica, 
		@Nullable java.lang.String alias, 
		@Nullable java.lang.String mac, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String xarxa, 
		@Nullable java.lang.String usuari, 
		java.lang.Boolean restringeixCerca)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="getHosts")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> getMaquines()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="findHostsByNetwork")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> findMaquinesByXarxa(
		es.caib.seycon.ng.comu.Xarxa xarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="getMailServers")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> getServidorsCorreu()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="getProfileServers")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> getServidorsPerfil()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="getHomeServers")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> getServidorsHome()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.parameter_update.class,Roles.Tothom.class},
	translated="findNetworkByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Xarxa> findXarxaByFiltre(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String adreca, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String mascara, 
		@Nullable java.lang.String normalitzada, 
		@Nullable java.lang.String dhcp, 
		@Nullable java.lang.String maquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_create.class,Roles.network_all_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.NetworkAuthorization create(
		es.caib.seycon.ng.comu.NetworkAuthorization accessList)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_update.class,Roles.network_all_create.class,Roles.network_all_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.NetworkAuthorization accessList)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.network_all_create.class,Roles.network_all_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.NetworkAuthorization update(
		es.caib.seycon.ng.comu.NetworkAuthorization accessList)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class},
	translated="findIdentitiesByCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Identitat> findIdentitatsByCodi(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class},
	translated="findIdentityByCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Identitat findIdentitatByCodi(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class},
	translated="findNetworkAuthorizationsByNetworkCodeAndIdentityCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.NetworkAuthorization findNetworkAuthorizationByCodiXarxaAndCodiIdentitat(
		java.lang.String codiXarxa, 
		java.lang.String codiIdentitat)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class},
	translated="findNetworkAuthorizationsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> findNetworkAuthorizationsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class},
	translated="findNetworkAuthorizationsByRoleName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> findNetworkAuthorizationsByNomRol(
		java.lang.String nomRol)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class},
	translated="findNetworkAuthorizationsByGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> findNetworkAuthorizationsByCodiGrup(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class},
	translated="hasNetworkAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean teAccesAXarxa(
		java.lang.String codiUsuari, 
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_create.class,Roles.host_all_update.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="getFirstAvailableIP")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getPrimeraIPLliure(
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_create.class,Roles.network_all_update.class,Roles.host_all_update.class,Roles.host_all_create.class,Roles.host_all_query.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="getAvailableIPs")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Long getIPsBuides(
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_create.class,Roles.network_all_update.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="getNotAvailableIPs")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Long getIPsOcupades(
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="findHostById")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findMaquinaById(
		java.lang.Long idMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.network_all_query.class,Roles.Tothom.class},
	translated="findHostByIp")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findMaquinaByIp(
		java.lang.String ip)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="findAccessLevelByHostNameAndNetworkCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Long findNivellAccesByNomMaquinaAndCodiXarxa(
		java.lang.String nomMaquina, 
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="hasManagedNetwork")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean teXarxaAdministrada()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.network_all_query.class,Roles.host_all_query.class},
	translated="isManaged")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean esXarxaAdministrada(
		java.lang.String codiXarxa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="findSessionsByHostName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Sessio> findSessionsByNomMaquina(
		java.lang.String codiMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="getTasks")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getTasques(
		java.lang.String nomMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class,Roles.Tothom.class},
	translated="findAliasByHostName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AliasMaquina> findAliasByNomMaquina(
		java.lang.String nomMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_create.class,Roles.host_all_update.class,Roles.host_all_query.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AliasMaquina create(
		es.caib.seycon.ng.comu.AliasMaquina aliasMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_update.class,Roles.host_all_query.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		es.caib.seycon.ng.comu.AliasMaquina aliasMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.host_all_delete.class,Roles.host_all_update.class,Roles.host_all_create.class,Roles.host_all_query.class,Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.AliasMaquina aliasMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findHostByFilterUnrestricted")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> findMaquinaByFiltreSenseRestriccions(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String sistemaOperatiu, 
		@Nullable java.lang.String adreca, 
		@Nullable java.lang.String dhcp, 
		@Nullable java.lang.String correu, 
		@Nullable java.lang.String ofimatica, 
		@Nullable java.lang.String alias, 
		@Nullable java.lang.String mac, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String xarxa, 
		@Nullable java.lang.String usuari, 
		java.lang.Boolean restringeixCerca)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AutoritzacioAccesHostComAdministrador create(
		es.caib.seycon.ng.comu.AutoritzacioAccesHostComAdministrador autoritzacioAccesComAdministrador)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findAuthorizationsToAccessHostWithAdministratorRigthsByHostsAndRequestDate")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioAccesHostComAdministrador> findAutoritzacionsAccesMaquinaComAdministradorByHostAndDataPeticio(
		java.lang.String nomHost, 
		@Nullable java.lang.String dataPeticio, 
		@Nullable java.lang.String dataCaducitat)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_admin_query.class,Roles.Tothom.class},
	translated="getHostAdminUserAndPassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getUsuariAndContrasenyaAdministradorHost(
		java.lang.String nomMaquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_create.class,Roles.user_update.class,Roles.group_create.class,Roles.group_update.class,Roles.printer_query.class,Roles.host_all_query.class,Roles.Tothom.class},
	translated="findOfficeHostUserByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Maquina> findMaquinaOfirmaticaUsuariByFiltre(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String sistemaOperatiu, 
		@Nullable java.lang.String adreca, 
		@Nullable java.lang.String dhcp, 
		@Nullable java.lang.String correu, 
		@Nullable java.lang.String ofimatica, 
		@Nullable java.lang.String alias, 
		@Nullable java.lang.String mac, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String xarxa, 
		@Nullable java.lang.String usuari, 
		java.lang.Boolean restringeixCerca, 
		@Nullable java.lang.String servidorImpressores)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean launchVNC(
		java.lang.Long sessioId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="hasAnyACLNetworks")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean hasAnyACLXarxes(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findALLNetworkAuthorizationsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> findALLNetworkAuthorizationsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_admin_query.class,Roles.Tothom.class},
	translated="revokeAdministratorAccessHost")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AutoritzacioAccesHostComAdministrador revocarAccesHostComAdministrador(
		es.caib.seycon.ng.comu.AutoritzacioAccesHostComAdministrador autoritzacioAccesComAdministrador)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="setAdministratorPassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setContrasenyaAdministrador(
		java.lang.String nomMaquina, 
		java.lang.String adminUser, 
		java.lang.String adminPass)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findHostBySerialNumber")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findMaquinaBySerialNumber(
		java.lang.String serialNumber)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRED ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"},noRollbackForClassName={"es.caib.seycon.ng.exception.UnknownNetworkException","es.caib.seycon.ng.exception.UnknownHostException"})
	public es.caib.seycon.ng.comu.Maquina registerDynamicIP(
		java.lang.String nomMaquina, 
		java.lang.String ip, 
		java.lang.String serialNumber)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.UnknownHostException, es.caib.seycon.ng.exception.UnknownNetworkException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class,Roles.host_all_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<es.caib.seycon.ng.comu.OsType> findAllOSType()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.OsType findOSTypeById(
		java.lang.Long osId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.operatingSystem_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.OsType create(
		es.caib.seycon.ng.comu.OsType osType)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.operatingSystem_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.OsType osType)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.operatingSystem_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		es.caib.seycon.ng.comu.OsType osType)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.OsType findOSTypeByName(
		java.lang.String osName)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
