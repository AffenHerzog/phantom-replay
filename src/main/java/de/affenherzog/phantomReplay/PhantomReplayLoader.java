package de.affenherzog.phantomReplay;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@SuppressWarnings("UnstableApiUsage")
public class PhantomReplayLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder(
                "central",
                "default",
                "https://maven-central.storage-download.googleapis.com/maven2/"
        ).build());

        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:7.1.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mariadb.jdbc:mariadb-java-client:3.5.9"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.google.code.gson:gson-extras:2.13.2-rc1"), null));

        classpathBuilder.addLibrary(resolver);
    }

}