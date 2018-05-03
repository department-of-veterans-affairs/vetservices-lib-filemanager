package gov.va.vetservices.lib.filemanager.impl.dto;

/**
 * A Data Transfer Object used by {@link ImplDto} for transporting data across the layer boundaries between the API and the
 * impelmentation.
 *
 * @author aburkholder
 *
 * @param <T> the type of object that will be passed to the validator.
 */
public class ImplArgDto<T> {
	private T value;

	/**
	 * Instantiate the argument with the type of object declared for the class.
	 *
	 * @param value the value to pass into a validator
	 */
	public ImplArgDto(T value) {
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
