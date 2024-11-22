package net.chrisrichardson.ftgo.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

public class FtgoTestUtil {
	public static <T> void assertPresent(Optional<T> value) {
		assertTrue(value.isPresent());
	  }

	  public static String getDockerHostIp() {
	    return Optional.ofNullable(System.getenv("DOCKER_HOST_IP")).orElse("localhost");
	  }
}
