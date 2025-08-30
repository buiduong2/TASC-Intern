import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get("logs"))) {

            paths.forEachOrdered(path -> {

                try {

                    Files.delete(path);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
