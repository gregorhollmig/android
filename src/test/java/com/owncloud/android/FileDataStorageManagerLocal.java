package com.owncloud.android;

import android.accounts.Account;

import com.owncloud.android.datamodel.FileDataStorageManagerInterface;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.lib.resources.shares.OCShare;
import com.owncloud.android.lib.resources.shares.ShareType;
import com.owncloud.android.lib.resources.status.OCCapability;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class FileDataStorageManagerLocal implements FileDataStorageManagerInterface {
    private Map<String, OCFile> pathToFile = new HashMap<>();
    private long currentMaxId = 0;

    public FileDataStorageManagerLocal() {
        // always have root file
        pathToFile.put("/", createOCFile("/"));
    }

    @Override
    public OCFile getFileByPath(String path) {
        return pathToFile.get(path);
    }

    @Override
    public Account getAccount() {
        throw new UnsupportedOperationException("to implement");
    }

    @Nullable
    @Override
    public OCFile getFileById(long id) {
        for (Map.Entry<String, OCFile> entry : pathToFile.entrySet()) {
            if (entry.getValue().getFileId() == id) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public void saveConflict(OCFile file, String etagInConflict) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void deleteFileInMediaScan(String path) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public boolean saveFile(OCFile file) {
        pathToFile.put(file.getRemotePath(), file);

        return true;
    }

    @Override
    public boolean fileExists(long id) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public List<OCFile> getFolderContent(OCFile f, boolean onlyOnDevice) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void saveFolder(OCFile folder, Collection<OCFile> updatedFiles, Collection<OCFile> filesToRemove) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public boolean removeFile(OCFile file, boolean removeDBData, boolean removeLocalCopy) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public boolean removeFolder(OCFile folder, boolean removeDBData, boolean removeLocalContent) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void moveLocalFile(OCFile file, String targetPath, String targetParentPath) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public boolean saveShare(OCShare share) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public List<OCShare> getSharesWithForAFile(String filePath, String accountName) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void removeShare(OCShare share) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void copyLocalFile(OCFile file, String targetPath) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public OCShare getFirstShareByPathAndType(String path, ShareType type, String shareWith) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public OCCapability saveCapabilities(OCCapability capability) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void saveSharesDB(List<OCShare> shares) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void removeSharesForFile(String remotePath) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public OCShare getShareById(long id) {
        throw new UnsupportedOperationException("to implement");
    }

    public OCFile createOCFile(String path) {
        OCFile file = new OCFile(path);

        file.setFileId(currentMaxId);
        currentMaxId++;

        return file;
    }
}