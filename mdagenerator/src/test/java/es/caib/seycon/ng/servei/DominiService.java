//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.DominiAplicacioEntity.class,
	es.caib.seycon.ng.model.ValorDominiAplicacioEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class})
public abstract class DominiService {

	@Operation ( grantees={Roles.application_create.class,Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini create(
		es.caib.seycon.ng.comu.Domini domini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Domini domini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.application_create.class,Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ValorDomini create(
		es.caib.seycon.ng.comu.ValorDomini valorDomini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_create.class,Roles.application_update.class,Roles.application_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.ValorDomini valorDomini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="findUserDomainGroup")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini findDominiGrupsUsuari()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findGroupsDomain")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini findDominiGrups()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findApplicationDomainByDomianNameAndApplicationCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini findDominiAplicacioByNomDominiAndCodiAplicacio(
		java.lang.String nomDomini, 
		java.lang.String codiAplicacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_query.class,Roles.user_role_query.class},
	translated="findDomainValuesByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ValorDomini> findValorsDominiByFiltre(
		es.caib.seycon.ng.comu.Domini domini, 
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String descripcio, 
		@Nullable java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findApplicationDomainByRoleName")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini findDominiAplicacioByNomRol(
		java.lang.String nomRol)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findApplicationDomainValueByDomainNameAndDomainApplicationCodeAndValue")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ValorDomini findValorDominiAplicacioByNomDominiAndCodiAplicacioDominiAndValor(
		java.lang.String nomDomini, 
		java.lang.String codiAplicacio, 
		java.lang.String valor)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_query.class},
	translated="findApplicationDomainsByApplicationCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Domini> findDominisAplicacioByCodiAplicacio(
		java.lang.String codiAplicacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Domini update(
		es.caib.seycon.ng.comu.Domini domini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_query.class},
	translated="findDomainsByApplicationCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Domini> findDominisByCodiAplicacio(
		java.lang.String codiAplicacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
