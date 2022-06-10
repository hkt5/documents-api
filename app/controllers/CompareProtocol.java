package controllers;

import java.io.File;

public class CompareProtocol {

    public static class FilesContainer {
        public final File originalFile;
        public final File compareFile;

        public FilesContainer(File originalFile, File compareFile) {
            this.originalFile = originalFile;
            this.compareFile = compareFile;
        }
    }

}