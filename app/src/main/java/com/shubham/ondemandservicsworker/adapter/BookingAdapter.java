

package com.shubham.ondemandservicsworker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.model.historyList;

import java.util.ArrayList;

public class BookingAdapter extends ArrayAdapter<historyList> {

    public BookingAdapter(@NonNull Context context, ArrayList<historyList> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_booking_list, parent, false);
        }
        historyList currentList = getItem(position);
        TextView carModelText = listItemView.findViewById(R.id.sbl_car_model_tv);
        TextView carNumberText = listItemView.findViewById(R.id.sbl_car_number_tv);
        TextView serviceText = listItemView.findViewById(R.id.sbl_wash_tv);
        TextView dateTimeText = listItemView.findViewById(R.id.sbl_date_tv);
        TextView priceText = listItemView.findViewById(R.id.sbl_price_tv);
        ImageView carImage = listItemView.findViewById(R.id.sbl_image);


        carModelText.setText(currentList.getModel());
        carNumberText.setText(currentList.getNumber());
        serviceText.setText(currentList.getService());
        dateTimeText.setText(currentList.getDateTime());
        priceText.setText("₹ " + currentList.getPrice());


        if (currentList.getCarType().equals("Mini"))
            carImage.setImageResource(R.drawable.car_mini);
        else if (currentList.getCarType().equals("Sedan"))
            carImage.setImageResource(R.drawable.car_sedan);
        else if (currentList.getCarType().equals("Suv"))
            carImage.setImageResource(R.drawable.car_suv);
        else
            carImage.setImageResource(R.drawable.car_premium);


        return listItemView;
    }
}
