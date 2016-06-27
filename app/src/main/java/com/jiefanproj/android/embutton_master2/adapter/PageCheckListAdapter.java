package com.jiefanproj.android.embutton_master2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import com.jiefanproj.android.embutton_master2.R;
import com.jiefanproj.android.embutton_master2.model.PageChecklist;

/**
 * v 2.0.1.1
 */
public class PageCheckListAdapter extends ArrayAdapter<PageChecklist> {

    private Context mContext;
    LayoutInflater mInflater;


    public PageCheckListAdapter(Context context, List<PageChecklist> checklist) {
        super(context, R.layout.row_page_checklist);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        TextView tvAction;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_page_checklist, null);

            holder = new ViewHolder();
            holder.tvAction = (TextView) convertView.findViewById(R.id.tv_action);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PageChecklist item = getItem(position);

        holder.tvAction.setText(item.getTitle());

        return convertView;
    }


    public void setData(List<PageChecklist> itemList) {
        clear();
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i++) {
                add(itemList.get(i));
            }
        }
    }
}
