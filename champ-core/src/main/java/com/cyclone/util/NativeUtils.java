package com.cyclone.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for loading native libraries from JAR files.
 * <p>
 * The appropriate native library will be extracted for the current platform on
 * which the JVM is running.
 * <p>
 * Library paths must be defined within a xml file that matches the library
 * name, located at the root of the native library folder. For example:
 * <p>
 * <pre>{@code
 * <libraries>
 * 	<library name="abclib">
 * 		<os name="windows">
 * 			<arch type="32">/native/abclib/windows/x86_32/abclib.dll</arch>
 * 			<arch type="64">/native/abclib/windows/x86_64/abclib.dll</arch>
 * 		</os>
 * 		<os name="linux">
 * 			<arch type="32">/native/abclib/linux/x86_32/libabclib.so</arch>
 * 			<arch type="64">/native/abclib/linux/x86_64/libabclib.so</arch>
 * 			<arch type="arm">/native/abclib/linux/arm/libabclib.so</arch>
 * 		</os>
 * 	</library>
 * </libraries>
 * }
 * </pre>
 *
 * @author dan.willis
 */
public final class NativeUtils {

    static class ExtractedLibraryInfo {
        private ExtractedLibraryInfo(File extractedFile, String libraryName) {
            this.extractedFile = extractedFile;
            this.libraryName = libraryName;
        }

        final File extractedFile;
        final String libraryName;
    }

    private static Map<String, ExtractedLibraryInfo> extractedLibraries = new HashMap<>();

    private static File copyLocation =
            new File(ConfigFactory.load().getString("cyclone.native.extractDirectoryName"));

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }

    /**
     * Extracts the platform specific native library to
     * copyLocation.get() and then uses <code>System.load</code> to
     * load the extracted file. There is no need for the path to be on the
     * java.library.path provided that no subsequent attempt is made to load the
     * library using <code>System.loadLibrary</code>.
     *
     * @param a_library the file name of the native library (without extension)
     * @param a_class   A class within the jar file from where you wish to load
     *                  library
     */
    public static void loadLibraryFromJar(final String a_library,
                                          final Class<?> a_class) throws IOException {
        libraryFromJar(a_library, a_class, true);
    }

    /**
     * Extracts the platform specific native library to
     * copyLocation.get(). This path must be on the java.library.path
     *
     * @param a_library the file name of the native library (without extension)
     * @param a_class   A class within the jar file from where you wish to extract
     *                  library
     */
    public static void extractLibraryFromJar(final String a_library,
                                             final Class<?> a_class) throws IOException {
        libraryFromJar(a_library, a_class, false);
    }

    private static synchronized void libraryFromJar(final String a_library,
                                                    final Class<?> a_class, final boolean a_loadLibrary)
            throws IOException {

        ExtractedLibraryInfo info = extractLibrary(a_library, a_class);

        if (a_loadLibrary) {
            System.load(info.extractedFile.getAbsolutePath());
        }
    }

    private static ExtractedLibraryInfo extractLibrary(final String a_library,
                                                       final Class<?> a_class) throws IOException {
        if (!extractedLibraries.containsKey(a_library)) {
            final String jarResourcePath = locateJarResource(a_library, a_class);
            final File extractedFile = prepForCopy(new File(jarResourcePath));

            try (InputStream copyFrom = getJarResource(jarResourcePath, a_class);
                 OutputStream copyTo = new FileOutputStream(extractedFile)) {
                ByteStreams.copy(copyFrom, copyTo);
                updateFilePermissions(extractedFile);

                ExtractedLibraryInfo info = new ExtractedLibraryInfo(
                        extractedFile, a_library);
                extractedLibraries.put(a_library, info);

                return info;
            }
        } else
            return extractedLibraries.get(a_library);
    }

    /**
     * Set the location where library files will be extracted
     */
    public static void setCopyLocation(final File a_copyLocation) {
        copyLocation = a_copyLocation;
    }

    /**
     * Create file placeholder to where library will be copied.
     */
    private static File prepForCopy(final File a_file) throws IOException {

        final File file = new File(copyLocation,
                a_file.getName());

        Files.createParentDirs(file);
        file.createNewFile();

        return file;
    }

    /**
     * Find the path of a resource within a jar file
     */
    private static String locateJarResource(final String a_library,
                                            final Class<?> a_class) throws IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        String path;

        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document document = builder.parse(a_class
                    .getResourceAsStream("/native/" + a_library + "/"
                            + a_library + ".xml"));

            document.getDocumentElement().normalize();

            final String expression = "/libraries/library[@name='" + a_library
                    + "']/os[@name='" + OSUtil.getOSName()
                    + "']/arch[@type='" + OSUtil.getArch() + "']";

            final XPath xPath = XPathFactory.newInstance().newXPath();
            path = xPath.compile(expression).evaluate(document);
        } catch (ParserConfigurationException | SAXException
                | XPathExpressionException e) {
            throw new IOException("Unable to parse XML file '"
                    + a_library
                    + ".xml' within jar: "
                    + a_class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath() + " \n", e);

        }
        return path;
    }

    /**
     * Get an inputput stream for a resource within a jar file
     */
    private static InputStream getJarResource(final String a_path,
                                              final Class<?> a_class) throws IOException {
        final InputStream inputStream = a_class.getResourceAsStream(a_path);

        if (inputStream == null) {
            throw new IOException("Unable to find native library\n"

                    + "\nOS: "
                    + SystemUtils.OS_NAME
                    + "\nLocation: "
                    + a_path
                    + "\nClass: "
                    + a_class.getName()
                    + "\nJAR"
                    + a_class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath() + "\n");
        }
        return inputStream;
    }

    /**
     * Set file permissions for linux
     */
    private static void updateFilePermissions(final File a_resourceLocation)
            throws IOException {
        if (SystemUtils.IS_OS_UNIX) {
            final Set<PosixFilePermission> permissions = new HashSet<>();

            // add owners permissions
            permissions.add(PosixFilePermission.OWNER_READ);
            permissions.add(PosixFilePermission.OWNER_WRITE);
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
            // add group permissions
            permissions.add(PosixFilePermission.GROUP_READ);
            permissions.add(PosixFilePermission.GROUP_WRITE);
            permissions.add(PosixFilePermission.GROUP_EXECUTE);

            java.nio.file.Files.setPosixFilePermissions(
                    Paths.get(a_resourceLocation.getAbsolutePath()),
                    permissions);
        }
    }

    /**
     * Holder for operating system and architecture information
     *
     * @author dan.willis
     */
    private static class OSUtil {
        static String getArch() {
            if (SystemUtils.OS_ARCH.contains("arm")) {
                return "arm";
            }

            return SystemUtils.OS_ARCH.contains("64") ? "64" : "32";
        }

        static String getOSName() {
            if (SystemUtils.IS_OS_WINDOWS) {
                return "windows";
            }

            if (SystemUtils.IS_OS_LINUX) {
                return "linux";
            }

            if (SystemUtils.IS_OS_MAC_OSX) {
                return "osx";
            }

            return "";
        }
    }
}
