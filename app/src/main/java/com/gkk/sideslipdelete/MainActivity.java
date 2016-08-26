package com.gkk.sideslipdelete;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gkk.sideslipdelete.SideslipView.OnSideslipViewListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {

    private ListView listView;
    private List<String> datas;
    private List<SideslipView> sideslipViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_dates);
        datas = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            datas.add("条目--" + i);
        }
        listView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (datas != null) {
                return datas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (datas != null) {
                return datas.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = View.inflate(MainActivity.this, R.layout.item, null);
                holder = new ViewHolder();
                holder.sv = (SideslipView) view.findViewById(R.id.item_sv);
                holder.leftTv = (TextView) view.findViewById(R.id.item_tv_content);
                holder.rightTv = (TextView) view.findViewById(R.id.item_tv_delete);

                holder.sv.setOnSideslipViewListener(new OnSideslipViewListener() {
                    @Override
                    public void onSideslipViewChanged(SideslipView view, boolean isOpened) {
                        if (isOpened) {
                            if (!sideslipViews.contains(view)) {
                                sideslipViews.add(view);
                            }
                        } else {
                            sideslipViews.remove(view);
                        }
                    }
                });
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final String data = datas.get(i);
            holder.leftTv.setText(data);
            holder.rightTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    datas.remove(data);
                    closeAll();
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    class ViewHolder {
        SideslipView sv;
        TextView leftTv;
        TextView rightTv;
    }

    public void closeAll() {
        ListIterator<SideslipView> iterator = sideslipViews.listIterator();
        while (iterator.hasNext()) {
            SideslipView view = iterator.next();
            view.close();
        }
    }
}
