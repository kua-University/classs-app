package com.example.classcsschedule;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> userList;
    private LayoutInflater inflater;

    public UserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_user, parent, false);
        }

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvUserId = convertView.findViewById(R.id.tvUserId);
        Button btnStudent = convertView.findViewById(R.id.btnStudent);
        Button btnInstructor = convertView.findViewById(R.id.btnInstructor);

        final User user = userList.get(position);
        tvUsername.setText(user.getUsername());
        tvUserId.setText(user.getUserId());

        // When the Student button is clicked, assign the Student role
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof AdminManageUsersActivity) {
                    // Use the actual document ID instead of user.getId()
                    ((AdminManageUsersActivity) context).assignRole(user.getDocId(), "Student");
                }
            }
        });

        btnInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof AdminManageUsersActivity) {
                    ((AdminManageUsersActivity) context).assignRole(user.getDocId(), "Instructor");
                }
            }
        });


        return convertView;
    }
}
