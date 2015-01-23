package test;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MatcherTest {

	public static void main(final String[] args) {
		try {
			run();
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

	private static void run() throws Exception {

		try(final DirectoryStream<Path> ds=Files.newDirectoryStream(Paths.get("."), "[h.][sic]*")) {
			for (final Path path : ds) {
				System.out.println(""+path);
			}
		}

	}
}
