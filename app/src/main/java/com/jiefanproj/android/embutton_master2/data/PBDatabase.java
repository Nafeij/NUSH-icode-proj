package com.jiefanproj.android.embutton_master2.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.List;

import com.jiefanproj.android.embutton_master2.AppConstants;
import com.jiefanproj.android.embutton_master2.model.HelpPage;
import com.jiefanproj.android.embutton_master2.model.Page;
import com.jiefanproj.android.embutton_master2.model.PageAction;
import com.jiefanproj.android.embutton_master2.model.PageChecklist;
import com.jiefanproj.android.embutton_master2.model.PageItem;
import com.jiefanproj.android.embutton_master2.model.PageStatus;
import com.jiefanproj.android.embutton_master2.model.PageTimer;

/**
 * v 2.0.1.1
 */
public class PBDatabase {

    private static final String TAG = PBDatabase.class.getSimpleName();

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context mContext;

    private static final String DATABASE_NAME = "pb_db";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            PageDbManager.createTable(db);
            PageStatusDbManager.createTable(db);
            PageItemDbManager.createTable(db);
            PageActionDbManager.createTable(db);
            PageTimerDbManager.createTable(db);
            PageChecklistDbManager.createTable(db);
            HelpPageDbManager.createTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            PageDbManager.dropTable(db);
            PageStatusDbManager.dropTable(db);
            PageItemDbManager.dropTable(db);
            PageActionDbManager.dropTable(db);
            PageTimerDbManager.dropTable(db);
            PageChecklistDbManager.dropTable(db);
            HelpPageDbManager.dropTable(db);

            onCreate(db);
        }
    }

    /**
     * Constructor
     */
    public PBDatabase(Context ctx) {
        mContext = ctx;
    }

    public PBDatabase open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public void insertOrUpdatePage(Page page) {
        PageDbManager.insertOrUpdate(this.db, page);

        deletePageActions(page.getId(), page.getLang());
        deletePageItems(page.getId(), page.getLang());
        deletePageStatus(page.getId(), page.getLang());
        deletePageTimer(page.getId(), page.getLang());
        deletePageChecklist(page.getId(), page.getLang());

        if (page.getStatus() != null) {
            for (PageStatus status : page.getStatus())
                insertPageStatus(status, page.getId(), page.getLang());
        }

        if (page.getAction() != null) {
            for (PageAction action : page.getAction())
                insertPageAction(action, page.getId(), page.getLang());
        }

        if (page.getItems() != null) {
            for (PageItem item : page.getItems())
                insertPageItem(item, page.getId(), page.getLang());
        }

        if(page.getTimers() != null){
            insertPageTimer(page.getTimers(), page.getId(), page.getLang());
        }

        if (page.getChecklist() != null) {
            for (PageChecklist cl : page.getChecklist())
                insertPageChecklist(cl, page.getId(), page.getLang());
        }
    }


    public Page retrievePage(String pageId, String lang) {
        return PageDbManager.retrieve(this.db, pageId, lang);
    }

    public List<Page> retrievePages(String lang) {
        return PageDbManager.retrieve(this.db, lang);
    }


    /*
    Page-Action methods
    */
    public void insertPageAction(PageAction action, String pageId, String lang) {
        PageActionDbManager.insert(this.db, action, pageId, lang);
    }

    public List<PageAction> retrievePageAction(String pageId, String lang) {
        return PageActionDbManager.retrieve(this.db, pageId, lang);
    }

    public void deletePageActions(String pageId, String lang){
        PageActionDbManager.delete(this.db, pageId, lang);
    }


    /*
    Page-Item methods
    */
    public void insertPageItem(PageItem item, String pageId, String lang) {
        PageItemDbManager.insert(this.db, item, pageId, lang);
    }

    public List<PageItem> retrievePageItem(String pageId, String lang) {
        return PageItemDbManager.retrieve(this.db, pageId, lang);
    }

    public void deletePageItems(String pageId, String lang){
        PageItemDbManager.delete(this.db, pageId, lang);
    }


    /*
    Page-Status methods
    */
    public void insertPageStatus(PageStatus status, String pageId, String lang) {
        PageStatusDbManager.insert(this.db, status, pageId, lang);
    }

    public List<PageStatus> retrievePageStatus(String pageId, String lang) {
        return PageStatusDbManager.retrieve(this.db, pageId, lang);
    }

    public void deletePageStatus(String pageId, String lang){
        PageStatusDbManager.delete(this.db, pageId, lang);
    }



    /*
    Page-Timers methods
     */
    public void insertPageTimer(PageTimer timer, String pageId, String lang) {
        PageTimerDbManager.insert(this.db, timer, pageId, lang);
    }

    public PageTimer retrievePageTimer(String pageId, String lang) {
        return PageTimerDbManager.retrieve(this.db, pageId, lang);
    }

    public void deletePageTimer(String pageId, String lang){
        PageTimerDbManager.delete(this.db, pageId, lang);
    }


    /*
    Page-Checklist methods
    */
    public void insertPageChecklist(PageChecklist cList, String pageId, String lang) {
        PageChecklistDbManager.insert(this.db, cList, pageId, lang);
    }

    public List<PageChecklist> retrievePageChecklist(String pageId, String lang) {
        return PageChecklistDbManager.retrieve(this.db, pageId, lang);
    }

    public void deletePageChecklist(String pageId, String lang){
        PageChecklistDbManager.delete(this.db, pageId, lang);
    }


    /*
    Page-HelpPage methods
    */
    public void insertOrUpdateHelpPage(HelpPage page) {
        HelpPageDbManager.insertOrUpdate(this.db, page);

        deletePageItems(page.getId(), page.getLang());

        if (page.getItems() != null) {
            for (PageItem item : page.getItems())
                insertPageItem(item, page.getId(), page.getLang());
        }
    }

    public List<HelpPage> retrieveHelpPage(String lang) {
        return HelpPageDbManager.retrieve(this.db, lang);
    }

    public void deleteHelpPage(String pageId, String lang){
        HelpPageDbManager.delete(this.db, pageId, lang);
    }
}
