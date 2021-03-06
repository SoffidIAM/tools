//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.RegistreAccesEntity.class,
	es.caib.seycon.ng.model.MaquinaEntity.class,
	es.caib.seycon.ng.model.ServeiEntity.class})
public abstract class RegistreAccesService {

	@Operation ( grantees={Roles.accessRegister_query.class},
	translated="findAccessEntryByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistresAccesByFiltre(
		@Nullable java.lang.String data, 
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String nomClient, 
		@Nullable java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.accessRegister_query.class},
	translated="findAccessEntryByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistresAccesByFiltre(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String dataFi, 
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String nomClient, 
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class},
	translated="findAccessEntryByHost")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistresAccesByMaquina(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String numRegistres, 
		@Nullable java.lang.String protocolAcces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class},
	translated="findAccessEntryBySSOHostAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistresAccesByMaquinaAccesSSO(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String numRegistres)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_accessRegister_query.class},
	translated="findEntryByInitialDataAndUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistreByDataIniAndCodiUsuari(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String codiUsuari, 
		@Nullable java.lang.String numRegistres)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_accessRegister_query.class},
	translated="findLastEntriesByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findDarrersRegistresByCodiUsuari(
		java.lang.String codiUsuari, 
		java.lang.String numRegistres, 
		java.lang.String codiProtocolAcces)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.accessRegister_query.class},
	translated="findAccessEntryByNewFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findRegistresAccesByFiltreNou(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String dataFi, 
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String nomClient, 
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.host_all_query.class},
	translated="findLastEntriesSSOHostAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RegistreAcces> findDarrersRegistresAccesMaquinaSSO(
		@Nullable java.lang.String nomServidor, 
		@Nullable java.lang.String numRegistres)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.RegistreAcces create(
		es.caib.seycon.ng.comu.RegistreAcces registre)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
