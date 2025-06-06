//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.sync.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( internal=true,
	 serverOnly=true)
@Depends ({es.caib.seycon.ng.sync.servei.TaskGenerator.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DominiContrasenyaEntity.class,
	es.caib.seycon.ng.model.DominiUsuariEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.TaskLogEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.sync.servei.SecretStoreService.class,
	com.soffid.iam.model.MetaAccountEntity.class,
	es.caib.seycon.ng.sync.servei.ServerService.class,
	es.caib.seycon.ng.sync.servei.ChangePasswordNotificationQueue.class,
	es.caib.seycon.ng.sync.servei.TaskQueue.class})
@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
public abstract class TaskQueue {

	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void addTask(
		es.caib.seycon.ng.sync.engine.TaskHandler newTask)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public es.caib.seycon.ng.sync.engine.TaskHandler addTask(
		es.caib.seycon.ng.model.TasqueEntity newTask)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.NEVER ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public es.caib.seycon.ng.sync.engine.TaskHandler getPendingTask(
		es.caib.seycon.ng.sync.engine.DispatcherHandler taskDispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.NEVER ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public es.caib.seycon.ng.sync.engine.TaskHandler getNextPendingTask(
		es.caib.seycon.ng.sync.engine.DispatcherHandler taskDispatcher, 
		es.caib.seycon.ng.sync.engine.TaskHandler previousTask)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public int countTasks()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return 0;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public int countTasks(
		es.caib.seycon.ng.sync.engine.DispatcherHandler taskDispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return 0;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.SUPPORTS)
	public void expireTasks()
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void notifyTaskStatus(
		es.caib.seycon.ng.sync.engine.TaskHandler task, 
		es.caib.seycon.ng.sync.engine.DispatcherHandler taskDispatcher, 
		boolean bOK, 
		@Nullable java.lang.String sReason, 
		@Nullable java.lang.Throwable t)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isEmpty()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Iterator<es.caib.seycon.ng.sync.engine.TaskHandler> getIterator()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void cancelTask(
		long taskId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void updateTask(
		es.caib.seycon.ng.model.TasqueEntity task)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void removeTask(
		es.caib.seycon.ng.model.TasqueEntity task)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Map processOBTask(
		es.caib.seycon.ng.sync.engine.TaskHandler task)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW ,isolation=org.springframework.transaction.annotation.Isolation.READ_COMMITTED ,rollbackForClassName={"java.lang.Exception"})
	public void notifyTaskStatusNewTransaction(
		es.caib.seycon.ng.sync.engine.TaskHandler task, 
		es.caib.seycon.ng.sync.engine.DispatcherHandler taskDispatcher, 
		boolean bOK, 
		@Nullable java.lang.String sReason, 
		@Nullable java.lang.Throwable t)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void pushTaskToPersist(
		es.caib.seycon.ng.sync.engine.TaskHandler newTask)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.sync.engine.TaskHandler peekTaskToPersist()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void persistTask(
		es.caib.seycon.ng.sync.engine.TaskHandler newTask)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Description("Cola de tareas pendientes de ejecución\n\n@author $Author: u07286 $\n@version $Revision: 1.1 $\n")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.sync.engine.TaskHandler findTaskHandlerById(
		long taskId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
