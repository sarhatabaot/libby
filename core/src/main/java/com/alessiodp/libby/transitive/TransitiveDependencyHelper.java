package com.alessiodp.libby.transitive;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.classloader.IsolatedClassLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.alessiodp.libby.Util.replaceWithDots;
import static java.util.Objects.requireNonNull;

/**
 * A reflection-based helper for resolving transitive dependencies. It automatically
 * downloads Maven Resolver Supplier, Maven Resolver Provider and their transitive dependencies to resolve transitive dependencies.
 *
 * @see <a href="https://github.com/apache/maven-resolver">Apache Maven Artifact Resolver</a>
 */
public class TransitiveDependencyHelper {

    /**
     * org.eclipse.aether.artifact.Artifact class name for reflections
     */
    private static final String ARTIFACT_CLASS = replaceWithDots("org.eclipse.aether.artifact.Artifact");

    /**
     * TransitiveDependencyCollector class instance, used in {@link #findTransitiveLibraries(Library)}
     */
    private final Object transitiveDependencyCollectorObject;

    /**
     * Reflected method for resolving transitive dependencies
     */
    private final Method resolveTransitiveDependenciesMethod;

    /**
     * Reflected getter methods of Artifact class
     */
    private final Method artifactGetGroupIdMethod, artifactGetArtifactIdMethod, artifactGetVersionMethod, artifactGetClassifierMethod;

    /**
     * LibraryManager instance, used in {@link #findTransitiveLibraries(Library)}
     */
    private final LibraryManager libraryManager;

    /**
     * Creates a new transitive dependency helper using the provided library manager to
     * download the dependencies required for transitive dependency resolution in runtime.
     *
     * @param libraryManager the library manager used to download dependencies
     * @param saveDirectory  the directory where all transitive dependencies would be saved
     */
    public TransitiveDependencyHelper(@NotNull LibraryManager libraryManager, @NotNull Path saveDirectory) throws IOException {
        requireNonNull(libraryManager, "libraryManager");
        this.libraryManager = libraryManager;

        try (IsolatedClassLoader classLoader = new IsolatedClassLoader()) {
            String collectorClassName = "com.alessiodp.libby.transitive.TransitiveDependencyCollector";
            String collectorClassPath = '/' + collectorClassName.replace('.', '/') + ".class";

            for (TransitiveLibraryResolutionDependency dependency : TransitiveLibraryResolutionDependency.values()) {
                classLoader.addPath(libraryManager.downloadLibrary(dependency.toLibrary()));
            }

            final Class<?> transitiveDependencyCollectorClass;
            try {
                transitiveDependencyCollectorClass = classLoader.defineClass(collectorClassName, requireNonNull(getClass().getResourceAsStream(collectorClassPath), "resourceCollectorClass"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Class<?> artifactClass = classLoader.loadClass(ARTIFACT_CLASS);

                // com.alessiodp.libby.TransitiveDependencyCollector(Path)
                Constructor<?> constructor = transitiveDependencyCollectorClass.getConstructor(Path.class);
                constructor.setAccessible(true);
                transitiveDependencyCollectorObject = constructor.newInstance(saveDirectory);
                // com.alessiodp.libby.TransitiveDependencyCollector#findTransitiveDependencies(String, String, String, String, Stream<String>)
                resolveTransitiveDependenciesMethod = transitiveDependencyCollectorClass.getMethod(
                        "findTransitiveDependencies",
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        Stream.class
                );
                resolveTransitiveDependenciesMethod.setAccessible(true);
                // org.eclipse.aether.artifact.Artifact#getGroupId()
                artifactGetGroupIdMethod = artifactClass.getMethod("getGroupId");
                // org.eclipse.aether.artifact.Artifact#getArtifactId()
                artifactGetArtifactIdMethod = artifactClass.getMethod("getArtifactId");
                // org.eclipse.aether.artifact.Artifact#getVersion()
                artifactGetVersionMethod = artifactClass.getMethod("getVersion");
                // org.eclipse.aether.artifact.Artifact#getClassifier()
                artifactGetClassifierMethod = artifactClass.getMethod("getClassifier");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Finds and returns a collection of transitive libraries for a given library.
     * <p>
     * This method fetches the transitive dependencies of the provided library using reflection-based
     * interaction with the underlying transitive dependency collector. The method ensures to filter out
     * any excluded transitive dependencies as specified by the provided library.
     * </p>
     * <p>
     * Note: The method merges the repositories from both the library manager and the given library
     * for dependency resolution. And clones all relocations into transitive libraries.
     * </p>
     *
     * @param library The primary library for which transitive dependencies need to be found.
     * @return A collection of {@link Library} objects representing the transitive libraries
     * excluding the ones marked as excluded in the provided library.
     * @throws RuntimeException If there's any exception during the reflection-based operations.
     */
    @NotNull
    public Collection<Library> findTransitiveLibraries(@NotNull Library library) {
        List<Library> transitiveLibraries = new ArrayList<>();
        Set<ExcludedDependency> excludedDependencies = new HashSet<>(library.getExcludedTransitiveDependencies());

        Collection<String> globalRepositories = libraryManager.getRepositories();
        Collection<String> libraryRepositories = library.getRepositories();
        if (globalRepositories.isEmpty() && libraryRepositories.isEmpty()) {
            throw new IllegalArgumentException("No repositories have been added before resolving transitive dependencies");
        }

        Stream<String> repositories = Stream.of(globalRepositories, libraryRepositories).flatMap(Collection::stream);
        try {
            Collection<?> artifacts = (Collection<?>) resolveTransitiveDependenciesMethod.invoke(transitiveDependencyCollectorObject,
                library.getGroupId(),
                library.getArtifactId(),
                library.getVersion(),
                library.getClassifier(),
                repositories);
            for (Object artifact : artifacts) {
                String groupId = (String) artifactGetGroupIdMethod.invoke(artifact);
                String artifactId = (String) artifactGetArtifactIdMethod.invoke(artifact);
                String version = (String) artifactGetVersionMethod.invoke(artifact);
                String classifier = (String) artifactGetClassifierMethod.invoke(artifact);

                if (library.getGroupId().equals(groupId) && library.getArtifactId().equals(artifactId))
                    continue;

                if (excludedDependencies.contains(new ExcludedDependency(groupId, artifactId)))
                    continue;

                Library.Builder libraryBuilder = Library.builder()
                                                        .groupId(groupId)
                                                        .artifactId(artifactId)
                                                        .version(version)
                                                        .isolatedLoad(library.isIsolatedLoad())
                                                        .loaderId(library.getLoaderId());

                if (classifier != null && !classifier.isEmpty()) {
                    libraryBuilder.classifier(classifier);
                }

                library.getRelocations().forEach(libraryBuilder::relocate);
                library.getRepositories().forEach(libraryBuilder::repository);

                transitiveLibraries.add(libraryBuilder.build());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return Collections.unmodifiableCollection(transitiveLibraries);
    }
}
