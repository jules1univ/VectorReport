package fr.univrennes.istic.l2gen.application.core.services;

public interface IService {

	default String name() {
		return this.getClass().getSimpleName();
	}
}