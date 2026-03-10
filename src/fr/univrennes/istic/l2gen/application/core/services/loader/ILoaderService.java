package fr.univrennes.istic.l2gen.application.core.services.loader;

import java.io.File;
import java.net.URI;
import java.util.List;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public interface ILoaderService extends IService {

    public boolean loadFolder(File folder, Character delimiter, boolean hasHeader);

    public boolean loadFile(File file, Character delimiter, boolean hasHeader);

    public boolean loadUrl(URI url, Character delimiter, boolean hasHeader);

    public List<CSVTable> getLatestLoadedTables();

    public CSVTable getTable(File file);

    public CSVTable getTableByName(String name);

    public List<File> getLoadedFiles();

    public List<String> getLoadedFileNames();
}