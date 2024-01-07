// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

public class ZipExtractor {

  public static void extractZip(Path zipFile, Path extractionDirectory) throws IOException {
    try (InputStream fileInputStream = new FileInputStream(zipFile.toFile());
        InputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(bufferedInputStream)) {
      extractZip(zipInputStream, extractionDirectory);
    }
  }

  /**
   * Extracts the contents of a zip archive.
   *
   * @param zis            The ZipArchiveInputStream representing the zip archive
   * @param extractionPath The path where the contents of the zip archive should be extracted to
   * @throws IOException If an I/O error occurs during the extraction process
   */
  private static void extractZip(ZipArchiveInputStream zis, Path extractionPath)
      throws IOException {
    ZipArchiveEntry entry;
    while ((entry = zis.getNextZipEntry()) != null) {
      var localLocation = extractionPath.resolve(entry.getName());
      if (entry.isDirectory()) {
        Files.createDirectories(localLocation);
      } else {
        try (OutputStream outputFileStream = new FileOutputStream(localLocation.toFile())) {
          IOUtils.copy(zis, outputFileStream);
        }
      }
    }
  }
}
