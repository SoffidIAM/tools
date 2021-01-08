package com.soffid.iam.service;

import java.util.List;

import com.soffid.mda.annotation.Operation;
import com.soffid.mda.annotation.Service;

@Service
public class CrudRegistryService {
	public void registerHandler ( List handler) {}
	
	public void registerDefaultHandlers ( ) {}

	public<E> List<E> getHandler(Class<E> cl) {return null;}
}
