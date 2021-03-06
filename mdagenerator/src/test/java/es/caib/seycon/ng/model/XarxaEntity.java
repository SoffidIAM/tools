//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_XARXES" )
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.comu.Xarxa.class,
	es.caib.seycon.ng.model.ConfiguracioEntity.class,
	es.caib.seycon.ng.model.XarxaACEntity.class,
	es.caib.seycon.ng.model.MaquinaEntity.class})
public abstract class XarxaEntity {

	@Column (name="XAR_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="XAR_CODI", length=10)
	public java.lang.String codi;

	@Column (name="XAR_ADRIP", length=25)
	public java.lang.String adreca;

	@Column (name="XAR_DESCRI", length=50)
	@Nullable
	public java.lang.String descripcio;

	@Column (name="XAR_MASIP", length=25)
	@Nullable
	public java.lang.String mascara;

	@Column (name="XAR_NORM", length=1)
	public java.lang.String normalitzada;

	@Column (name="XAR_PARDHC", length=50)
	@Nullable
	public java.lang.String dhcp;

	@ForeignKey (foreignColumn="AXA_IDXAR")
	public java.util.Collection<es.caib.seycon.ng.model.XarxaACEntity> autoritzacions;

	@ForeignKey (foreignColumn="MAQ_IDXAR")
	public java.util.Collection<es.caib.seycon.ng.model.MaquinaEntity> maquines;

	@Column (name="XAR_DHCPSUP")
	public boolean dchpSupport;

	@DaoFinder
	public es.caib.seycon.ng.model.XarxaEntity findByCodi(
		java.lang.String codi) {
	 return null;
	}
	@DaoFinder
	public java.util.List<es.caib.seycon.ng.model.XarxaEntity> findByFiltre(
		es.caib.seycon.ng.comu.XarxaSearchCriteria filtre) {
	 return null;
	}
	@DaoFinder(" - CUSTOM -")
	public java.lang.String getPrimeraIPLliure(
		java.lang.String ipXarxa, 
		java.lang.String mascara) {
	 return null;
	}
	@DaoFinder(" - CUSTOM -")
	public java.lang.Long getIPsBuides(
		java.lang.String ipXarxa, 
		java.lang.String mascara) {
	 return null;
	}
	@DaoFinder(" - CUSTOM -")
	public java.lang.Long getIPsOcupades(
		java.lang.String ipXarxa, 
		java.lang.String mascara) {
	 return null;
	}
	@DaoFinder
	public es.caib.seycon.ng.model.XarxaEntity findByAdreca(
		java.lang.String adreca) {
	 return null;
	}
}
