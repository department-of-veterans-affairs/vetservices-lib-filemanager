package gov.va.vetservices.lib.filemanager.impl.validate;

public class ValidatorArg<T> {
	private T value;

	public ValidatorArg(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
