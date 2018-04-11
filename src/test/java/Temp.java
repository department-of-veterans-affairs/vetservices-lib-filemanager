import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class Temp {

	@Test
	public void test() {
		String test1 = "this.is.a.test";
		System.out.println(test1);
		System.out.println(StringUtils.truncate(test1, test1.lastIndexOf(".")));
		System.out.println(StringUtils.substring(test1, test1.lastIndexOf(".") + 1));
		System.out.println("");

		String test2 = ".test";
		System.out.println(test2);
		System.out.println(StringUtils.truncate(test2, test2.lastIndexOf(".")));
		System.out.println(StringUtils.substring(test2, test2.lastIndexOf(".") + 1));
		System.out.println("");

		String test3 = "this.is.a.";
		System.out.println(test3);
		System.out.println(StringUtils.truncate(test3, test3.lastIndexOf(".")));
		System.out.println(StringUtils.substring(test3, test3.lastIndexOf(".") + 1));
		System.out.println("");
	}

}
