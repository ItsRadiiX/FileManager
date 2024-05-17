package nl.bryansuk.foundationapi.handlers;

import nl.bryansuk.foundationapi.FileManager;

import java.io.File;

@SuppressWarnings("unused")
public abstract class Handler {

    private final String path;

    private volatile long LastModified;
    private boolean isAutoReloading;

    public abstract boolean onReload();

    public Handler(String path, boolean isAutoReloading) {
        this.path = path;
        this.LastModified = 0;
        setAutoReloading(isAutoReloading);
    }

    /**
     * Gets the data folder path.
     *
     * @return the data folder path
     */
    protected String getDataFolder(){
        return FileManager.getPlugin().getDataFolder().getPath() + getFolderSeparator();
    }

    /**
     * Gets the full file path.
     *
     * @return the full file path
     */
    protected String getFilePath(){
        return getDataFolder() + path;
    }

    /**
     * Gets the system's folder separator.
     *
     * @return the system's folder separator
     */
    protected String getFolderSeparator() {
        return File.separator;
    }

    public String getPath() {
        return path;
    }

    /**
     * Checks if the file exists.
     *
     * @return true if the file exists, false otherwise
     */
    public boolean doesFileExist(){
        return getFile().exists();
    }

    public boolean isFileEmpty(){
        return getFile().length() == 0;
    }

    /**
     * Gets the file object.
     *
     * @return the file object
     */
    public File getFile(){
        return new File(getFilePath());
    }

    protected void updateLastModified() {
        LastModified = getFile().lastModified();
    }

    /**
     * Checks if a new version of the file is available.
     *
     * @return true if a new version is available, false otherwise
     */
    public boolean isNewVersionAvailable(){
        return LastModified != getFile().lastModified();
    }

    public void setAutoReloading(boolean autoReloading) {
        if (autoReloading) {
            if (isAutoReloading) return;
            isAutoReloading = true;
            FileManager.addHandler(this);
        } else {
            if (!isAutoReloading) return;
            isAutoReloading = false;
            FileManager.removeHandler(this);
        }
    }

    public boolean isFolder(){
        return getFile().isDirectory();
    }
}
