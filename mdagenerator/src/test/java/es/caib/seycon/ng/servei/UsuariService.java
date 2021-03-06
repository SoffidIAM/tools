//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( serverPath="/seycon/UsuariService",
	 serverRole="agent")
@Depends ({
	AplicacioService.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.ScTarget.class,
	es.caib.seycon.ng.model.UsuariSEUEntity.class,
	es.caib.seycon.ng.model.TipusUsuariEntity.class,
	es.caib.seycon.ng.model.UsuariWFProcessEntity.class,
	es.caib.seycon.ng.model.DominiUsuariEntity.class,
	es.caib.seycon.ng.model.DominiContrasenyaEntity.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	es.caib.seycon.ng.model.ContrasenyaEntity.class,
	es.caib.seycon.ng.model.RenovacioEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.UsuariGrupEntity.class,
	es.caib.seycon.ng.model.XarxaACEntity.class,
	es.caib.seycon.ng.model.ImpressoraEntity.class,
	es.caib.seycon.ng.model.TipusDadaEntity.class,
	es.caib.seycon.ng.model.DadaUsuariEntity.class,
	es.caib.seycon.ng.model.SessioEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.UsuariImpressoraEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.MaquinaEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.model.RolAssociacioRolEntity.class,
	es.caib.seycon.ng.model.RolsGrupEntity.class,
	es.caib.seycon.ng.model.ServerEntity.class})
public abstract class UsuariService {

	@Operation (translated="createUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari altaUsuari(
		byte[] peticio, 
		es.caib.signatura.api.Signature signatura)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="disableUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari baixaUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserByUserNif")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari findUsuariByNIFUsuari(
		java.lang.String nif)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.anonymous.class},
	translated="findUsersByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Usuari> findUsuarisByCodiUsuari(
		@Nullable java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="setServersToUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari setServidorsToUsuari(
		java.lang.String codiUsuari, 
		@Nullable java.lang.String nomServidorPerfil, 
		@Nullable java.lang.String nomServidorCorreu, 
		@Nullable java.lang.String nomServidorHome)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class,Roles.mail_update.class,Roles.lopd_update.class},
	translated="findUserByCriteria")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Usuari> findUsuariByCriteri(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String primerLlinatge, 
		@Nullable java.lang.String nomCurt, 
		@Nullable java.lang.String dataCreacio, 
		@Nullable java.lang.String usuariCreacio, 
		@Nullable java.lang.String actiu, 
		@Nullable java.lang.String segonLlinatge, 
		@Nullable java.lang.String multiSessio, 
		@Nullable java.lang.String comentari, 
		@Nullable java.lang.String tipusUsuari, 
		@Nullable java.lang.String servidorPerfil, 
		@Nullable java.lang.String servidorHome, 
		@Nullable java.lang.String servidorCorreu, 
		@Nullable java.lang.String codiGrupPrimari, 
		@Nullable java.lang.String dni, 
		@Nullable java.lang.String dominiCorreu, 
		@Nullable java.lang.String grupSecundari, 
		@Nullable java.lang.Boolean restringeixCerca)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_update.class,Roles.user_custom_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari update(
		es.caib.seycon.ng.comu.Usuari usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Usuari usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="findNetworksACByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.NetworkAuthorization> findXarxesACByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findProfileServerByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findServidorPerfilByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findHomeServerByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findServidorHomeByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findMailServerByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina findServidorCorreuByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findPrintersByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Impressora> findImpressoresByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="findUserDataByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.DadaUsuari> findDadesUsuariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getDataType")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.TipusDada> getTipusDades()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findDataByTypeDataCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.DadaUsuari findDadaByCodiTipusDada(
		java.lang.String codiUsuari, 
		java.lang.String codiTipusDada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_session_query.class},
	translated="findSessionByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Sessio> findSessionsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUsersByCoreData")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Usuari> findUsuarisByDadesBasiques(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String primerLlinatge, 
		@Nullable java.lang.String segonLlinatge, 
		@Nullable java.lang.String dni)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getApplicationsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Aplicacio> getAplicacionsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getApplicationRolesByuserCodeAndApplicationCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Rol> getRolsAplicacioByCodiUsuariAndCodiAplicacio(
		java.lang.String codiUsuari, 
		java.lang.String codiAplicacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="setInitialPassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String assignaPasswordInicial(
		java.lang.String codiUsuari, 
		java.lang.String codiDominiContrasenyes)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findUserByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari findUsuariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getDireccionsGeneralsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getConselleriesByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="shortNameExists")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.Boolean existeixNomCurt(
		java.lang.String nomCurt)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findByShortName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari findByNomCurt(
		java.lang.String nomCurt)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari create(
		es.caib.seycon.ng.comu.Usuari usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="findUserPrintersByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariImpressora> findUsuariImpressoresByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_refresh.class},
	translated="refreshChanges")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] refreshCanvis(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_password_update.class},
	translated="changePassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String canviPassword(
		java.lang.String codiUsuari, 
		java.lang.String codiDominiContrasenyes)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_refresh.class,Roles.user_query.class},
	translated="getTasks")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getTasques(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_create.class},
	translated="getFollowingCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getSeguentCodi()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_query.class},
	translated="findUserRolesWithApplicationRolesByApplicationCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolAccount> findRolsUsuarisAmbRolsDAplicacioByCodiAplicacio(
		java.lang.String codiAplicacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserByUserId")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari findUsuariByIdUsuari(
		java.lang.Long idUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String addUsuari(
		es.caib.signatura.api.Signature signatura, 
		java.lang.String userType)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserByDataTypeCodeAndDataTypeValue")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari findUsuariByCodiTipusDadaIValorTipusDada(
		java.lang.String codiTipusDada, 
		java.lang.String valorTipusDada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String addUsuari(
		@Nullable java.util.Collection<java.security.cert.X509Certificate> certs, 
		java.lang.String userType)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getBpmEnabledApplicationsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Aplicacio> getAplicacionsGestionablesWFByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="createPupilUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariAlumne altaUsuariAlumne(
		es.caib.seycon.ng.comu.UsuariAlumne usuariAlumne)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="createExtranetCard")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.TargetaExtranet creaTargetaExtranet(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findExtranetCardsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.TargetaExtranet> findTargetesExtranetByCodiUsuari(
		java.lang.String codiUsuari, 
		@Nullable java.lang.String activa)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.TargetaExtranet update(
		es.caib.seycon.ng.comu.TargetaExtranet targetaExtranet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findExtranetCardByUserCodeAndCardCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.TargetaExtranet findTargetaExtranetByCodiUsuariAndCodiTargeta(
		java.lang.String codiUsuari, 
		java.lang.String codiTargeta)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserRolesHierachyByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Rol> findJerarquiaRolsUsuariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserRolesHierachyByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Rol> findJerarquiaRolsUsuariByCodiUsuari(
		java.lang.String codiUsuari, 
		java.lang.Boolean incloureRolsUsuariDirectes)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_update.class,Roles.user_create.class},
	translated="checkUserIdentity")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] verificarIdentitatUsuari(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String llinatge1, 
		@Nullable java.lang.String llinatge2, 
		@Nullable java.lang.String document)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_update.class,Roles.user_create.class},
	translated="checkUserIdentity")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] verificarIdentitatUsuari(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String llinatge1, 
		@Nullable java.lang.String llinatge2, 
		@Nullable java.lang.String tipusDocument, 
		@Nullable java.lang.String document, 
		@Nullable java.lang.String numSolicitud)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariSEU update(
		es.caib.seycon.ng.comu.UsuariSEU usuariSEU)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariSEU create(
		es.caib.seycon.ng.comu.UsuariSEU usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class,Roles.anonymous.class},
	translated="findConsoleUserByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariSEU findUsuariSEUByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getPasswordsUserType")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.EstatContrasenya> getContrasenyesTipusUsuari(
		java.util.Date dataInici, 
		@Nullable java.util.Date dataFi, 
		@Nullable java.lang.String tipusUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="seeUserIdentity")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] consultarIdentitatUsuari(
		@Nullable java.lang.String document)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="seeUserIdentity")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] consultarIdentitatUsuari(
		@Nullable java.lang.String tipusDocument, 
		@Nullable java.lang.String document, 
		@Nullable java.lang.String numSolicitud)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="createHostUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari altaUsuariMaquina(
		java.lang.String nom, 
		java.lang.String descripcio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="changePasswordUserHost")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String canviPasswordUsuariMaquina(
		java.lang.String codiUsuari, 
		java.lang.String codiDominiContrasenyes)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="spreadPassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void propagaContrasenya(
		java.lang.String codiUsuari, 
		java.lang.String contrasenya)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="generateRandomPassword")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String generaPasswordRandom()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="updateUserCoreData")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari updateDadesBasiquesUsuari(
		es.caib.seycon.ng.comu.Usuari usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="createNewUserProcess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String creaNouProcesUsuari(
		java.lang.String nomProces, 
		java.lang.String codiUsuari, 
		boolean canviaAProces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="getBpmUserProcessList")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ProcesWF> obteLlistaProcessosWFUsuari()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.DadesDocent consultaDestinacionsXestibDocent(
		java.lang.String nifPersonalDocent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariWFProcess create(
		es.caib.seycon.ng.comu.UsuariWFProcess usuariWFProces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariWFProcess update(
		es.caib.seycon.ng.comu.UsuariWFProcess usuariWFProces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.UsuariWFProcess usuariWFProces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findBpmUserProcessByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariWFProcess> findProcessosWFUsuariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findBpmUserProcessByProcessId")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariWFProcess> findProcessosWFUsuariByIdProces(
		java.lang.Long idProces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findBpmUserProcessByUserNif")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariWFProcess> findProcessosWFUsuariByNIFUsuari(
		java.lang.String nifUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="findBpmUserProcessInstanceByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.bpm.vo.ProcessInstance> findProcessInstanceWFUsuariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getUserGroups(
		long userId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRED ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"},noRollbackForClassName={"UnknownUserException"})
	public es.caib.seycon.ng.comu.Usuari getUserInfo(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.UnknownUserException {
	 return null;
	}
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRED ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"},noRollbackForClassName={"UnknownUserException"})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getUserGroupsHierarchy(
		long userId)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.UnknownUserException {
	 return null;
	}
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRED ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"},noRollbackForClassName={"UnknownUserException"})
	public java.util.Collection<es.caib.seycon.ng.comu.RolGrant> getUserRoles(
		long userId)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.UnknownUserException {
	 return null;
	}
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRED ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"},noRollbackForClassName={"UnknownUserException"})
	public java.util.Collection<es.caib.seycon.ng.comu.RolGrant> getUserExplicitRoles(
		long userId)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.UnknownUserException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="getCurrentUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari getCurrentUsuari()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_role_query.class},
	translated="getESSORules")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public byte[] getMazingerRules(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Usuari> findUserByCriteria(
		es.caib.seycon.ng.comu.UsuariCriteria criteria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
