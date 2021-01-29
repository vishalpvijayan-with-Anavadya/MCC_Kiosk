package com.lng.lngattendancesystem.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lng.lngattendancesystem.Activities.CustomerActivities.PergeActivity;
import com.lng.lngattendancesystem.Models.RestoreEmployeeDetales.EmployeeDetail;
import com.lng.lngattendancesystem.R;

import java.util.List;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeDetailsView> {

    Context mContext;
    PergeActivity pergeActivityReff;
    List<EmployeeDetail> employeeDetailsViewList;



    public EmployeeListAdapter(Context mContext, List<EmployeeDetail> employeeDetailsViewList,PergeActivity pergeActivityReff) {
        this.mContext = mContext;
        this.employeeDetailsViewList = employeeDetailsViewList;
        this.pergeActivityReff=pergeActivityReff;
    }

    @NonNull
    @Override
    public EmployeeDetailsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_employee, parent, false);
        return new EmployeeDetailsView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeDetailsView holder, int position) {
        final EmployeeDetail employeeData = employeeDetailsViewList.get(position);
        holder.empId.setText(String.valueOf(employeeData.getEmpId()));
        holder.empName.setText(employeeData.getEmpName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Test", "onClick: "+employeeData.getEmpPresistedFaceId());
                pergeActivityReff.purgeEmployeeDetails(Integer.parseInt(employeeData.getEmpPresistedFaceId()),employeeData.getEmpId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeDetailsViewList.size();
    }

    public class EmployeeDetailsView extends RecyclerView.ViewHolder {

        private TextView empName, empId;
        private LinearLayout parent;

        public EmployeeDetailsView(@NonNull View itemView) {
            super(itemView);
            empName = itemView.findViewById(R.id.emp_name);

            empId = itemView.findViewById(R.id.emp_id);
            parent = itemView.findViewById(R.id.lyt_parent);
        }
    }
}
