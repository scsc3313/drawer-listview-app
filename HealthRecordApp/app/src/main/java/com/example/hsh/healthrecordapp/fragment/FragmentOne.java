package com.example.hsh.healthrecordapp.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsh.healthrecordapp.R;
import com.example.hsh.healthrecordapp.model.ListData;
import com.example.hsh.healthrecordapp.sql.DbAdapter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {

    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private Button addBtn, deleteBtn;
    private Cursor result;
    private TextView todayDate;

    public static final String IMAGE_RESOURCE_ID = "iconResourceID";
    public static final String ITEM_NAME = "itemName";
    private static final String TAG = "FragmentOne";

    private String todayDateString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_one, container,
                false);
        listView = (ListView) view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(view.getContext());
        listView.setAdapter(listViewAdapter);
        addBtn = (Button) view.findViewById(R.id.addBtn);
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        todayDate = (TextView) view.findViewById(R.id.todayDate);

        final DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());

        try {
            dbAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "error" + e.toString());
        }
        todayDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        todayDate.setText(todayDateString);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewAdapter.addItem("","","","");
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.moveToLast();
                listViewAdapter.remove(listViewAdapter.getCount()-1);
                dbAdapter.delete(result.getShort(0));
            }
        });
//        ivIcon = (ImageView) view.findViewById(R.id.frag1_icon);
//        tvItemName = (TextView) view.findViewById(R.id.frag1_text);
//
//        tvItemName.setText(getArguments().getString(ITEM_NAME));
//        ivIcon.setImageDrawable(view.getResources().getDrawable(
//                getArguments().getInt(IMAGE_RESOURCE_ID)));
//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(TAG, "fab is Onclicked!!!");
//            }
//        });

        //ListView
        dbAdapter.create("스쿼트", "40");
        result = dbAdapter.fetchAll();
        result.moveToFirst();

        while(!result.isAfterLast()){
            listViewAdapter.addItem(result.getString(1), result.getString(2), "", "");
            result.moveToNext();
        }
        if(result.getCount() < 5)
            for(int i = 0 ; i < 3; i++)
                listViewAdapter.addItem("","","","");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListData data = (ListData) listViewAdapter.getItem(i);
                Toast.makeText(view.getContext(), data.nameEdit.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class ViewHolder{
        public EditText nameEdit, weightEdit, setEdit, countEdit;

        public Button button;
    }

    private class ListViewAdapter extends BaseAdapter {

        private Context context = null;
        private ArrayList<ListData> arrayList = new ArrayList<>();

        public ListViewAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_item, null);

                holder.nameEdit = (EditText) view.findViewById(R.id.nameEdit);
                holder.weightEdit = (EditText) view.findViewById(R.id.weightEdit);
                holder.setEdit = (EditText) view.findViewById(R.id.setEdit);
                holder.countEdit = (EditText) view.findViewById(R.id.countEdit);

                holder.button = (Button) view.findViewById(R.id.button);

//                holder.nameEdit.addTextChangedListener(new MyWatcher(holder.nameEdit));
//                holder.weightEdit.addTextChangedListener(new MyWatcher(holder.weightEdit));
//                holder.setEdit.addTextChangedListener(new MyWatcher(holder.setEdit));
//                holder.countEdit.addTextChangedListener(new MyWatcher(holder.countEdit));

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }

            ListData mData = arrayList.get(i);

            holder.nameEdit.setText(mData.nameEdit);
            holder.weightEdit.setText(mData.weightEdit);
            holder.setEdit.setText(mData.setEdit);
            holder.countEdit.setText(mData.countEdit);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogSelectOption();
                }
            });

            return view;
        }

        public void addItem(String name,String weight,String set,String count){
            ListData addInfo = new ListData();
            addInfo.nameEdit = name;
            addInfo.weightEdit = weight;
            addInfo.setEdit = set;
            addInfo.countEdit = count;

            arrayList.add(addInfo);
            dataChange();
        }

        public void remove(int position){
            arrayList.remove(position);
            dataChange();
        }

        public void dataChange(){
            listViewAdapter.notifyDataSetChanged();
        }

    }

    private class MyWatcher implements TextWatcher {

        private EditText et;
        private ListData listData;

        public MyWatcher(EditText et) {
            this.et = et;
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
            Log.d(TAG, "onTextChanged: " + c);
            listData = (ListData) et.getTag();
            if (listData != null) {
                listData.nameEdit = c.toString();
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void DialogSelectOption() {
        final String items[] = { "item1", "item2", "item3" };
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("Title");
        ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 각 리스트를 선택했을때
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancel 버튼 클릭시
            }
        });
        ab.show();
    }
}
