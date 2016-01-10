package com.scp.indexablerecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

/**
 * 适配器
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> implements SectionIndexer {
    private Context context;//上下文
    private List<String> names;//数据源
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";//右侧导航字符

    public ContentAdapter(Context context, List<String> names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        //将每个Section作为单个数组元素放到sections中
        for (int i = 0; i < mSections.length(); i++) {
            //从Sections中获取每一个字符
            sections[i] = String.valueOf(mSections.charAt(i));
        }
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        //从当前的section往前查，直到遇到第一个有对应item的为止，否则不进行定位
        for (int i = sectionIndex; i > 0; i--) {
            for (int j = 0; j < getItemCount(); j++) {
                //查询数字
                if (i == 0) {
                    for (int k = 0; k < 9; k++) {
                        //value是item的首字母
                        if (StringMatcher.match(String.valueOf(names.get(j).charAt(0)), String.valueOf(k))) {
                            return j;
                        }
                    }
                }
                //查询字母
                else {
                    //value是item的首字母
                    if (StringMatcher.match(String.valueOf(names.get(j).charAt(0)), String.valueOf(mSections.charAt(i)))) {
                        return j;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(R.id.item_name_tv);
        }
    }
}
