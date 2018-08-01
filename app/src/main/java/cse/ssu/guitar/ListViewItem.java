package cse.ssu.guitar;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;
    private boolean isHeader;

    public ListViewItem(Drawable icon, String title, String descStr) {
        super();
        if(icon==null) {
            this.titleStr = title;
            isHeader = true;
        }
        else {
            this.iconDrawable = icon;
            this.titleStr = title;
            this.descStr = descStr;
            isHeader = false;
        }
    }

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public boolean getHeader(){
        return isHeader;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}

