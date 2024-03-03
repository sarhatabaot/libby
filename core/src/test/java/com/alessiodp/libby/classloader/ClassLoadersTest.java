package com.alessiodp.libby.classloader;

import com.alessiodp.libby.LibraryManagerMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertThrows;
 class ClassLoadersTest {

    @BeforeEach
    void setUp() {
        System.clearProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        System.clearProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        System.clearProperty(ClassLoaderHelper.ENV_VAR_DISABLE_UNSAFE);
        System.clearProperty(ClassLoaderHelper.ENV_VAR_DISABLE_JAVA_AGENT);
    }

    @Test
    void testURLClassLoaderHelper() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        new URLClassLoaderHelper(new URLClassLoader(new URL[0]), libraryManager);
    }

    @Test
    void testURLClassLoaderHelperNoUnsafe() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        new URLClassLoaderHelper(new URLClassLoader(new URL[0]), libraryManager);
    }

    @Test
    void testURLClassLoaderHelperNoJavaAgent() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        new URLClassLoaderHelper(new URLClassLoader(new URL[0]), libraryManager);
    }

    @Test
    @EnabledForJreRange(max = JRE.JAVA_15)
    void testURLClassLoaderHelperNotFailing() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        new URLClassLoaderHelper(new URLClassLoader(new URL[0]), libraryManager);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_16)
    void testURLClassLoaderHelperFailing() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        assertThrows(RuntimeException.class, () -> new URLClassLoaderHelper(new URLClassLoader(new URL[0]), libraryManager));
    }

    @Test
    void testSystemClassLoaderHelper() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        new SystemClassLoaderHelper(ClassLoader.getSystemClassLoader(), libraryManager);
    }

    @Test
    void testSystemClassLoaderHelperNoUnsafe() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        new SystemClassLoaderHelper(ClassLoader.getSystemClassLoader(), libraryManager);
    }

    @Test
    void testSystemClassLoaderHelperNoJavaAgent() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        new SystemClassLoaderHelper(ClassLoader.getSystemClassLoader(), libraryManager);
    }

    @Test
    @EnabledForJreRange(max = JRE.JAVA_8)
    void testSystemClassLoaderHelperNotFailing() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        new SystemClassLoaderHelper(ClassLoader.getSystemClassLoader(), libraryManager);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void testSystemClassLoaderHelperFailing() throws IOException {
        LibraryManagerMock libraryManager = new LibraryManagerMock();

        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_UNSAFE);
        setProperty(ClassLoaderHelper.SYSTEM_PROPERTY_DISABLE_JAVA_AGENT);
        assertThrows(RuntimeException.class, () -> new SystemClassLoaderHelper(ClassLoader.getSystemClassLoader(), libraryManager));
    }

    private void setProperty(String property) {
        System.setProperty(property, "true");
    }
}
