DO NOT DELETE THIS FILE - IT IS USED IN FileLoader TESTS!!!

The /src/test/resources/files directory contains files that are used in testing
various file operations in this project. The directory structure reflects the
MIME type of files that are of interest.

For example, the TXT MimeType is "text/plain", therefore there is a directory
that mirrors the MimeType files/text/plain.

The testing infrastructure is set up to take advantage of this structure.
ANY directories under /src/test/resources/files/** MUST reflect a MimeType,
and files in those directories must be of that MimeType, or tests will fail.

Tests that do not rely on MimeTypes can utilize the root folder.
