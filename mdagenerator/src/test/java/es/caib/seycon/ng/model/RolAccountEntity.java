//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_ROLUSU" )
@Depends ({es.caib.seycon.ng.comu.RolGrant.class,
	es.caib.seycon.ng.comu.ContenidorRol.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.NotificacioEntity.class,
	es.caib.seycon.ng.comu.RolAccount.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.ValorDominiAplicacioEntity.class,
	es.caib.seycon.ng.comu.AdministracioAplicacio.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.model.MetaAccountEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	com.soffid.iam.model.RuleEntity.class})
public abstract class RolAccountEntity {

	@Column (name="RLU_ROLUSU_GRU")
	@Nullable
	public es.caib.seycon.ng.model.GrupEntity grup;

	@Column (name="RLU_IDROL")
	public es.caib.seycon.ng.model.RolEntity rol;

	@Column (name="RLU_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="RLU_VALDOM")
	@Nullable
	public es.caib.seycon.ng.model.ValorDominiAplicacioEntity valorDominiAplicacio;

	@Column (name="RLU_TIPDOM", length=20)
	@Nullable
	public java.lang.String tipusDomini;

	@Column (name="RLU_ADMAPP")
	@Nullable
	public es.caib.seycon.ng.model.AplicacioEntity aplicacioAdministrada;

	@Column (name="RLU_ACC_ID")
	public com.soffid.iam.model.MetaAccountEntity account;

	@Column (name="RLU_RUL_ID")
	@Nullable
	public com.soffid.iam.model.RuleEntity rule;

	@DaoFinder("select ra\nfrom es.caib.seycon.ng.model.RolAccountEntity ra\ninner join    ra.account as account\ninner join    account.users as users\ninner join    users.user as user\ninner join    ra.rol as rol\nwhere user.codi = :codiUsuari and rol.nom = :nomRol")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByCodiUsuariAndNomRol(
		java.lang.String codiUsuari, 
		java.lang.String nomRol) {
	 return null;
	}
	@DaoFinder("select ra\nfrom es.caib.seycon.ng.model.RolAccountEntity as ra\njoin ra.account as acc\njoin acc.users as users\njoin users.user as user\nleft join ra.rol as rol\nleft join rol.baseDeDades as dispatcher\nwhere user.codi = :codiUsuari and acc.type='U'\norder by dispatcher.codi, rol.nom\n")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByCodiUsuari(
		java.lang.String codiUsuari) {
	 return null;
	}
	@DaoFinder("select ra\nfrom es.caib.seycon.ng.model.RolAccountEntity ra\nwhere ra.rol.nom = :nomRol")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByNomRol(
		java.lang.String nomRol) {
	 return null;
	}
	public java.lang.String toString() {
	 return null;
	}
	@DaoFinder("select rolsUsuaris\nfrom es.caib.seycon.ng.model.RolAccountEntity rolsUsuaris\nwhere (rolsUsuaris.tipusDomini='GRUPS' or rolsUsuaris.tipusDomini='GRUPS_USUARI')\nand rolsUsuaris.grup.codi=:codiGrup")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByCodiGrup(
		java.lang.String codiGrup) {
	 return null;
	}
	@DaoFinder("select rolsUsuaris from es.caib.seycon.ng.model.RolAccountEntity rolsUsuaris \nleft join rolsUsuaris.rol role \nleft join rolsUsuaris.grup grup left join rolsUsuaris.aplicacioAdministrada aplicacio \nleft join rolsUsuaris.valorDominiAplicacio valorDominiAplicacio \nwhere (role.nom = :nomRol and role.baseDeDades.codi = :baseDadesRol and \nrole.aplicacio.codi = :codiAplicacioRol) and rolsUsuaris.tipusDomini=:tipusDomini and \n( ( (:tipusDomini='GRUPS' or :tipusDomini='GRUPS_USUARI') and grup is not null \nand (:codiGrupDomini is not null and grup.codi=:codiGrupDomini)) or (:tipusDomini='APLICACIONS' and \naplicacio is not null and (:codiAplicacioDomini is not null and aplicacio.codi=:codiAplicacioDomini)) \nor (:tipusDomini='DOMINI_APLICACIO' and valorDominiAplicacio is not null \nand (valorDominiAplicacio.id=:idValorDominiAplicacioDomini)) or \n( (:tipusDomini='SENSE_DOMINI' or :tipusDomini is null) and aplicacio IS NULL \nAND grup IS NULL AND valorDominiAplicacio IS NULL) )")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByRolAndValorDomini(
		java.lang.String nomRol, 
		java.lang.String baseDadesRol, 
		java.lang.String codiAplicacioRol, 
		java.lang.String tipusDomini, 
		java.lang.String codiGrupDomini, 
		java.lang.String codiAplicacioDomini, 
		java.lang.Long idValorDominiAplicacioDomini) {
	 return null;
	}
	@DaoFinder("select ra from es.caib.seycon.ng.model.RolAccountEntity ra\nleft join ra.rol role \nwhere (role.nom = :nomRol and role.baseDeDades.codi = :baseDadesRol and \nrole.aplicacio.codi = :codiAplicacioRol) and ra.tipusDomini=:tipusDomini")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByRolAndTipusDomini(
		java.lang.String nomRol, 
		java.lang.String baseDadesRol, 
		java.lang.String codiAplicacioRol, 
		java.lang.String tipusDomini) {
	 return null;
	}
	@DaoFinder("select ra\nfrom \nes.caib.seycon.ng.model.RolAccountEntity as ra\njoin ra.account as account\njoin account.users as users\njoin users.user as user\nleft join ra.rol as rol\nleft join rol.baseDeDades as dispatcher\nwhere  user.codi = :codiUsuari \norder by dispatcher.codi, rol.nom")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findAllByCodiUsuari(
		java.lang.String codiUsuari) {
	 return null;
	}
	@DaoFinder("select ra \nfrom es.caib.seycon.ng.model.RolAccountEntity as ra\nwhere ra.grup.codi=:groupName\n")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByGrupQualifier(
		java.lang.String groupName) {
	 return null;
	}
	@DaoFinder("select ra\nfrom es.caib.seycon.ng.model.RolAccountEntity as ra\njoin ra.account.users as useraccount\nwhere ra.rule.id = :ruleId and useraccount.user.id = :userId and ra.account.type='U'")
	public java.util.List<es.caib.seycon.ng.model.RolAccountEntity> findByUserAndRule(
		java.lang.Long userId, 
		java.lang.Long ruleId) {
	 return null;
	}
}
