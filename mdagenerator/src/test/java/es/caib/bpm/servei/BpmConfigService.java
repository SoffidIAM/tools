//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.bpm.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( grantees={Roles.Tothom.class})
public abstract class BpmConfigService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.bpm.vo.ConfigParameterVO> findAll()
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.bpm.vo.ConfigParameterVO> findById(
		long id)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.bpm.vo.ConfigParameterVO> findByAppKey(
		java.lang.String app, 
		java.lang.String key)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	 return null;
	}
	@Operation ( grantees={Roles.anonymous.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.bpm.vo.ConfigParameterVO findFirstByAppKey(
		java.lang.String app, 
		java.lang.String key)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void updateAll(
		java.util.Collection<es.caib.bpm.vo.ConfigParameterVO> config)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		es.caib.bpm.vo.ConfigParameterVO config)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void create(
		es.caib.bpm.vo.ConfigParameterVO config)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.bpm.vo.ConfigParameterVO config)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.bpm.exception.BPMException {
	}
}
