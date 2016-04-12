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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hsh.healthrecordapp.R;
import com.example.hsh.healthrecordapp.model.ListData;
import com.example.hsh.healthrecordapp.model.ViewHolder;
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

    private DbAdapter dbAdapter;
    private int rowId;
    private int NAME = 0, WEIGHT = 1, SET = 2, COUNT = 3;

    //onPause()될때 데이터를 저장함
    @Override
    public void onPause() {
        super.onPause();
        ListData listData;
        for (int i = 0; i < listViewAdapter.getCount(); i++) {
            listData = (ListData) listViewAdapter.getItem(i);
            dbAdapter.update(i, listData.getNameEdit(), listData.getWeightEdit());
            Log.i(TAG, "onPause() Update : " + listData.getNameEdit() + " " + listData.getWeightEdit());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        //프레그먼트 레이아웃 정의
        View view = inflater.inflate(R.layout.fragment_layout_one, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(view.getContext());
        listView.setAdapter(listViewAdapter);
        addBtn = (Button) view.findViewById(R.id.addBtn);
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        todayDate = (TextView) view.findViewById(R.id.todayDate);
        dbAdapter = new DbAdapter(getActivity().getApplicationContext());

        //dbAdapter를 사용하기위하여 open()
        try {
            dbAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "error" + e.toString());
        }

        rowId = dbAdapter.fetchAll().getCount();

        //오늘 날짜를 알기 위한 세팅
        todayDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        todayDate.setText(todayDateString);
        //추가 버튼 클릭 시
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //listViewAdapter에 추가
                ++rowId;
                listViewAdapter.addItem("", "", 0, 0, rowId);
                //sqlite에 추가
                dbAdapter.create(rowId, "", "");
            }
        });

        //삭제 버튼 클릭 시
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listViewAdapter.getCount() != 0) {
                    dbAdapter.delete(rowId);
                    listViewAdapter.remove(--rowId);
                }
            }
        });

        //ListView에 보여주기 위한 select문으로 보면됨
        result = dbAdapter.fetchAll();
        Log.i(TAG, "Result print : " + result.toString());
        if (result.moveToFirst()) {
            while (!result.isAfterLast()) {
                int avg = getAvg();
                listViewAdapter.addItem(result.getString(1), result.getString(2), result.getInt(3) , avg , result.getShort(0));
                Log.i(TAG, "resultPrint rowId : " + result.getShort(0));
                result.moveToNext();
            }
        }

        listViewAdapter.dataChange();

        return view;
    }

    private int getAvg() {
        String countString = result.getString(4);
        String[] countStrings = countString.split("");
        int avg = 0;
        for(String string : countStrings){
            avg += Integer.valueOf(string);
        }
        avg /= countStrings.length;
        return avg;
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
            //view가 null이면
//            if (view == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_item, null);
                holder.nameEdit = (EditText) view.findViewById(R.id.nameEdit);
                holder.weightEdit = (EditText) view.findViewById(R.id.weightEdit);
                holder.setEdit = (TextView) view.findViewById(R.id.setTextView);
                holder.countEdit = (TextView) view.findViewById(R.id.countTextView);
                holder.button = (Button) view.findViewById(R.id.button);

//                holder.setEdit.addTextChangedListener(new MyWatcher(i, SET));
//                holder.countEdit.addTextChangedListener(new MyWatcher(i, COUNT));
                holder.id = i;

                Log.i(TAG, "getView() holder's i : " + holder.getId());
                holder.nameEdit.addTextChangedListener(new MyWatcher(holder.getId(), NAME));
                holder.weightEdit.addTextChangedListener(new MyWatcher(holder.getId(), WEIGHT));
//
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder) view.getTag();
//            }
            //holder를 db에 저장 시켜줘야 할듯.. 새로 시작될때마다 holder가 제대로 못받아옴



            ListData mData = arrayList.get(i);

            holder.nameEdit.setText(mData.getNameEdit());
            holder.weightEdit.setText(mData.getWeightEdit());
            holder.setEdit.setText(mData.getSetTextView());
            holder.countEdit.setText(mData.getCountTextView());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogSelectOption();
                }
            });

            return view;
        }

        public void addItem(String name, String weight, int set, int count, int id) {
            ListData addInfo = new ListData();
            addInfo.setNameEdit(name);
            addInfo.setWeightEdit(weight);
            addInfo.setSetTextView(set);
            addInfo.setCountTextView(count);
            addInfo.setId(id);

            arrayList.add(addInfo);
            dataChange();
        }

        public void remove(int position) {
            //arrayList는 index가 0부터 시작
            arrayList.remove(position);
            Log.i(TAG, "remove' position :" + position);
            dataChange();
        }

        public void dataChange() {
            listViewAdapter.notifyDataSetChanged();
            Log.i(TAG, "dataChange() rowId : " + rowId);
        }

    }

    private class MyWatcher implements TextWatcher {

        private final int type;
        private int position;

        public MyWatcher(int position, int type) {
            this.position = position;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable edit) {
            String string = edit.toString();
            ListData listData = (ListData) listViewAdapter.getItem(position);
            if (type == 0) {
                listData.setNameEdit(string);
            } else if (type == 1) {
                listData.setWeightEdit(string);
            }
        }
//        String s = edit.toString();
//        dbAdapter.update(position, type, s);
//        Log.d(TAG, "update position: " + position + " type : " + type + " String : " + s);
//        Log.i(TAG, dbAdapter.printData());
//
//        dbAdapter.update()

    }

    private void DialogSelectOption() {
        final String items[] = {"item1", "item2", "item3"};
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
