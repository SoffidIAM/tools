//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_TIPUSUO" )
@Depends ({es.caib.seycon.ng.comu.TipusUnitatOrganitzativa.class,
	es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class})
public abstract class TipusUnitatOrganitzativaEntity {

	@Column (name="TUO_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="TUO_CODI", length=20)
	public java.lang.String codi;

	@Column (name="TUO_DESC", length=50)
	@Nullable
	public java.lang.String descripcio;

	@Column (name="TUO_PARE")
	@Nullable
	public es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity pare;

	@ForeignKey (foreignColumn="TUO_PARE")
	public java.util.Collection<es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity> fills;

	@ForeignKey (foreignColumn="GRU_TIPUS")
	public java.util.Collection<es.caib.seycon.ng.model.GrupEntity> grupEntities;

	@DaoFinder
	public es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity findByCodi(
		java.lang.String codi) {
	 return null;
	}
	@DaoFinder("select uo from es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity uo where (:codi is null or (:codi is not null and uo.codi like :codi)) and (:descripcio is null or (:descripcio is not null and uo.descripcio like :descripcio))")
	public java.util.List<es.caib.seycon.ng.model.TipusUnitatOrganitzativaEntity> findByFiltre(
		@Nullable java.lang.String codi, 
		@Nullable String descripcio) {
	 return null;
	}
}
