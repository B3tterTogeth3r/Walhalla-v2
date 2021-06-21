package de.walhalla.app2.dialog.search;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * @author B3tterTogeth3r
 * @version 1.0
 * @see ir.mirrajabi.searchdialog.core.Searchable
 * @since 2.5
 */
public class SearchModel implements Searchable {
    private String mTitle;

    public SearchModel(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }
}
