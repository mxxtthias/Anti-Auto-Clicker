package de.luzifer.core.utils.loader;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class AntiACClassLoader {

    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> findClasses(
            @NonNull final ClassLoader classLoader,
            @NonNull final File file,
            @NonNull final Class<T> clazz,
            @NonNull final Predicate<Class<T>> predicate
    ) {

        return findClasses(classLoader, file, clazz).stream()
                .filter(aClass -> predicate.test((Class<T>) aClass))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> findClasses(
            @NonNull final ClassLoader classLoader,
            @NonNull final File file,
            @NonNull final Class<T> clazz
    ) {

        return findClasses(classLoader, file, clazz::isAssignableFrom).stream()
                .map(aClass -> (Class<T>) aClass.asSubclass(clazz))
                .collect(Collectors.toList());
    }

    public List<Class<?>> findClasses(
            @NonNull final ClassLoader classLoader,
            @NonNull final File file,
            @NonNull final Predicate<Class<?>> predicate
    ) {

        if (!file.exists()) {
            return new ArrayList<>();
        }

        if (file.isDirectory()) {
            return findClassesInSourceRoot(classLoader, file, predicate);
        } else if (!file.getName().endsWith(".jar")) {
            return new ArrayList<>();
        }

        final List<Class<?>> classes = new ArrayList<>();

        try {

            final URL jar = file.toURI().toURL();

            final List<String> matches = new ArrayList<>();

            try (final JarInputStream stream = new JarInputStream(
                    jar.openStream()); final URLClassLoader loader = new URLClassLoader(new URL[]{jar},
                    classLoader)
            ) {
                JarEntry entry;
                while ((entry = stream.getNextJarEntry()) != null) {
                    final String name = entry.getName();
                    if (name == null || name.isEmpty() || !name.endsWith(".class")) {
                        continue;
                    }

                    matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
                }

                for (final String match : matches) {
                    try {
                        final Class<?> loaded = loader.loadClass(match);
                        if (predicate.test(loaded)) {
                            classes.add(loaded);
                        }
                    } catch (final NoClassDefFoundError ignored) {
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private List<Class<?>> findClassesInSourceRoot(
            @NonNull final ClassLoader classLoader,
            @NonNull final File sourceRoot,
            @NonNull final Predicate<Class<?>> predicate
    ) {
        File[] files = sourceRoot.listFiles();

        if (files == null) return new ArrayList<>();

        ArrayList<Class<?>> classes = new ArrayList<>();

        List<String> classNames = getClassNames("", sourceRoot, sourceRoot);
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{sourceRoot.toURI().toURL()}, classLoader);

            for (String className : classNames) {
                try {
                    Class<?> aClass = loader.loadClass(className);
                    if (predicate.test(aClass)) {
                        classes.add(aClass);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private List<String> getClassNames(String basePath, File sourceRoot, File file) {

        ArrayList<String> classNames = new ArrayList<>();
        if (file.isFile() && file.getName().endsWith(".class")) {
            classNames.add(basePath + file.getName().replace(".class", ""));
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    classNames.addAll(getClassNames(file.equals(sourceRoot) ? "" : basePath + file.getName() + ".", sourceRoot, subFile));
                }
            }
        }

        return classNames;
    }

}
