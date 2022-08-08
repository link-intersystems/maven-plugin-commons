package com.link_intersystems.maven.plugin.test.extensions;

import com.link_intersystems.maven.logging.PrintWriterLog;
import com.link_intersystems.maven.plugin.test.AbstractMojoTest;
import com.link_intersystems.maven.plugin.test.MavenTestProject;
import com.link_intersystems.maven.plugin.test.TestMojo;
import org.apache.maven.plugin.Mojo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MojoTestContext extends AbstractMojoTest {

    private Path tempDirectory;
    private MavenTestProject mavenTestProject;

    @Override
    protected Optional<MavenTestProject> getMavenTestProjectAnnotation() {
        return Optional.of(mavenTestProject);
    }

    public void setUp(MavenTestProject mavenTestProject) throws Exception {
        this.mavenTestProject = mavenTestProject;

        tempDirectory = Files.createTempDirectory("MavenTestProject");
        setUp(tempDirectory);

    }

    public Mojo getMojo(TestMojo testMojo) throws Exception {
        String gaol = testMojo.gaol();
        boolean debugEnabled = testMojo.debugEnabled();
        Mojo mojo = lookupConfiguredMojo(gaol, debugEnabled);
        PrintWriterLog printWriterLog = new PrintWriterLog(new PrintWriter(System.out));
        printWriterLog.setDebugEnabled(testMojo.debugEnabled());
        mojo.setLog(printWriterLog);
        return mojo;
    }

    public void tearDown() throws Exception {
        super.tearDown();
        deleteAllFilesAndDirectories(tempDirectory);
    }

    private SortedMap<Path, IOException> deleteAllFilesAndDirectories(Path pathToDelete) throws IOException {
        if (Files.notExists(pathToDelete)) {
            return Collections.emptySortedMap();
        }

        SortedMap<Path, IOException> failures = new TreeMap<>();
        resetPermissions(pathToDelete);
        Files.walkFileTree(pathToDelete, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (!dir.equals(pathToDelete)) {
                    resetPermissions(dir);
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // IOException includes `AccessDeniedException` thrown by non-readable or non-executable flags
                resetPermissionsAndTryToDeleteAgain(file, exc);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                return deleteAndContinue(file);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return deleteAndContinue(dir);
            }

            private FileVisitResult deleteAndContinue(Path path) {
                try {
                    Files.delete(path);
                } catch (NoSuchFileException ignore) {
                    // ignore
                } catch (DirectoryNotEmptyException exception) {
                    failures.put(path, exception);
                } catch (IOException exception) {
                    // IOException includes `AccessDeniedException` thrown by non-readable or non-executable flags
                    resetPermissionsAndTryToDeleteAgain(path, exception);
                }
                return CONTINUE;
            }

            private void resetPermissionsAndTryToDeleteAgain(Path path, IOException exception) {
                try {
                    resetPermissions(path);
                    if (Files.isDirectory(path)) {
                        Files.walkFileTree(path, this);
                    } else {
                        Files.delete(path);
                    }
                } catch (Exception suppressed) {
                    exception.addSuppressed(suppressed);
                    failures.put(path, exception);
                }
            }
        });
        return failures;
    }

    private static void resetPermissions(Path path) {
        File file = path.toFile();
        file.setReadable(true);
        file.setWritable(true);
        if (Files.isDirectory(path)) {
            file.setExecutable(true);
        }
    }


}
