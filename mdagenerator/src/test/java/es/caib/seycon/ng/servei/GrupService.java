//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( ) 
@Depends ({es.caib.seycon.ng.model.RolsGrupEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.MaquinaEntity.class,
	es.caib.seycon.ng.model.UsuariGrupEntity.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.ConfiguracioEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class})

public abstract class GrupService {

	@Operation ( grantees={Roles.group_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup create(
		es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getGroups")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getGrups()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class},
	translated="findGroupByGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup findGrupByCodiGrup(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class,Roles.lopd_query.class},
	translated="findSubgroupsByGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findSubGrupsByCodiGrup(
		java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="removeGroupFormUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void removeGrupFromUsuari(
		java.lang.String codiUsuari, 
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findGroupsByGroupsType")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsByTipusGrup(
		java.lang.String tipus)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="setSuperGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setSuperGrup(
		java.lang.String codiSubGrup, 
		java.lang.String codiSuperGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="getOfficeServer")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Maquina getServidorOfimatic(
		@Nullable es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.group_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup update(
		es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findGroupsByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsByFiltre(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String pare, 
		@Nullable java.lang.String unitatOfimatica, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String tipus, 
		@Nullable java.lang.String obsolet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findGroupsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="addGroupToUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void addGrupToUsuari(
		java.lang.String codiUsuari, 
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findPrimaryGroupByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup findGrupPrimariByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findGroupsFromUsersByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsFromUsuarisByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findGroupsFromRolesByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsFromRolsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_group_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariGrup create(
		es.caib.seycon.ng.comu.UsuariGrup usuariGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_group_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.UsuariGrup usuariGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findUserGroupByUserCodeAndGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariGrup findUsuariGrupByCodiUsuariAndCodiGrup(
		java.lang.String codiUsuari, 
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.lopd_query.class},
	translated="getSuperGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup getSuperGrup(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_query.class},
	translated="findUsersGroupByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariGrup> findUsuariGrupsByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUsersRolesWithGroupByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolAccount> findRolsUsuarisAmbGrupByCodiUsuari(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.lopd_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getConselleriesAmbDireccionsGenerals()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class},
	translated="getParentList")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> getLlistaDePares(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class},
	translated="findGroupById")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Grup findGrupById(
		java.lang.Long grupId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class},
	translated="getRolesFromGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Rol> getRolsFromGrup(
		es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_user_query.class},
	translated="findUsersBelongtoGroupByGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariGrup> findUsuarisPertanyenAlGrupByCodiGrup(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getRolesFromGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolsGrup> getRolsFromGrup(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_role_query.class},
	translated="findUsersRolesDomainTypeAndUserGroups")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolAccount> findRolsUsuarisTipusDominiGrupsAndGrupsUsuari(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_query.class},
	translated="findGroupsByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Grup> findGrupsByFiltre(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String pare, 
		@Nullable java.lang.String unitatOfimatica, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String tipus, 
		@Nullable java.lang.String obsolet, 
		@Nullable java.lang.String servidorOfimatic, 
		@Nullable java.lang.String seccioPressupostaria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_role_query.class},
	translated="getRolesFromGroupAndParentGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolsGrup> getRolsFromGrupYParesGrup(
		es.caib.seycon.ng.comu.Grup grup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void propagateRolsChangesToDispatcher(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
