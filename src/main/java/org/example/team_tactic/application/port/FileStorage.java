package org.example.team_tactic.application.port;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Port for file storage. Abstracts filesystem vs cloud (e.g. S3) implementations.
 */
public interface FileStorage {

    /**
     * Stores a file and returns the path used for retrieval.
     *
     * @param inputStream  file content
     * @param relativePath path relative to storage root (e.g. "attachments/uuid-filename.ext")
     * @return the stored path (same as relativePath or adjusted)
     */
    String store(InputStream inputStream, String relativePath);

    /**
     * Reads a file into the given output stream.
     *
     * @param storedPath path returned by store()
     * @param output     stream to write content to
     */
    void read(String storedPath, OutputStream output);

    /**
     * Deletes a file by path.
     *
     * @param storedPath path returned by store()
     */
    void delete(String storedPath);

    /**
     * Checks if a file exists at the given path.
     */
    boolean exists(String storedPath);
}
