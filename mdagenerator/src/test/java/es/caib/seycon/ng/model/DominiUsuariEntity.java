//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_DOMUSU" )
@Depends ({es.caib.seycon.ng.model.DominiContrasenyaEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.comu.DominiUsuari.class,
	es.caib.seycon.ng.model.TipusUsuariEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class})
public abstract class DominiUsuariEntity {

	@Column (name="DOU_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="DOU_CODI", length=50)
	public java.lang.String codi;

	@Column (name="DOU_DESC", length=100)
	@Nullable
	public java.lang.String descripcio;

	@Column (name="DOU_TIPUS", length=1)
	@Nullable
	public es.caib.seycon.ng.comu.TipusDominiUsuariEnumeration tipus;

	@ForeignKey (foreignColumn="DIS_DOU_ID")
	public java.util.Collection<es.caib.seycon.ng.model.DispatcherEntity> dispatchers;

	@Column (name="DOU_EXPRES", length=1024)
	@Nullable
	public java.lang.String bshExpr;

	@Column (name="DOU_GENERA", length=64)
	@Nullable
	public java.lang.String beanGenerator;

	@DaoFinder
	public es.caib.seycon.ng.model.DominiUsuariEntity findByCodi(
		java.lang.String codi) {
	 return null;
	}
	@DaoFinder("select du\nfrom es.caib.seycon.ng.model.DispatcherEntity as dispatcher\nleft join dispatcher.dominiUsuari as du\nwhere dispatcher.codi=:dispatcher")
	public es.caib.seycon.ng.model.DominiUsuariEntity findByDispatcher(
		java.lang.String dispatcher) {
	 return null;
	}
}
