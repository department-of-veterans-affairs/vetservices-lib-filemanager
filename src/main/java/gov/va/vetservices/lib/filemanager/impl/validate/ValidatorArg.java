package gov.va.vetservices.lib.filemanager.impl.validate;

/**
 * Use this class to pass values to implementations of the {@link Validator} interface
 * 
 * @author aburkholder
 *
 * @param <T> the type of object that will be passed to the validator.
 */
public class ValidatorArg<T> {
	private T value;

	/**
	 * Instantiate the argument with the type of object declared for the class.
	 * 
	 * @param value the value to pass into a validator
	 */
	public ValidatorArg(T value) {
		this.value = value;
	}

	/**
	 * Get the value contained in this validator argument
	 * 
	 * @return T the object stored in this argument
	 */
	public T getValue() {
		return value;
	}
}
