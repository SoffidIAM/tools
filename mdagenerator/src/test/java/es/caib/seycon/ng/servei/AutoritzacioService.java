//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.AutoritzacioRolEntity.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class})
public abstract class AutoritzacioService {

	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getUserAuthorization(
		java.lang.String codiAutoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getUserAuthorization(
		java.lang.String codiAutoritzacio, 
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.authorization_rol_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AutoritzacioRol create(
		es.caib.seycon.ng.comu.AutoritzacioRol autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.authorization_rol_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.AutoritzacioRol autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.Tothom.class,Roles.authorization_query.class},
	translated="getAuthorizationRoles")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getRolsAutoritzacio(
		java.lang.String autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getUserAuthorizationString(
		java.lang.String codiAutoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getUserAuthorizationString(
		java.lang.String codiAutoritzacio, 
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getUserAuthorizations()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection getUserAuthorizations(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getUserAuthorizationsString()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] getUserAuthorizationsString(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getDescriptionUserAuthorizations()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioRol> getDescriptionUserAuthorizations(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getAuthorizationInfo")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<java.lang.Object> getInformacioAutoritzacio(
		java.lang.String autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.authorization_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection findAuthorizations(
		@Nullable java.lang.String ambit, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String codi)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.authorization_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List getScopeList()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
