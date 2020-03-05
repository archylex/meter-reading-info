package io.archylex.meterreadingsinfo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InfoServiceAdapter extends ArrayAdapter<InfoService> {
    private final Activity context;
    private List<InfoService> iservices;

    public InfoServiceAdapter(Activity context, List<InfoService> iservices) {
        super(context, R.layout.info_list_cell, iservices);
        this.context = context;
        this.iservices = iservices;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.info_list_cell, null,true);

        TextView twOrganization = (TextView) rowView.findViewById(R.id.info_org_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.info_org_icon);
        TextView twSubName = (TextView) rowView.findViewById(R.id.info_sub_name);
        TextView twAddress = (TextView) rowView.findViewById(R.id.info_sub_address);
        TextView twSubNumber = (TextView) rowView.findViewById(R.id.info_sub_number);
        TextView twSaldo = (TextView) rowView.findViewById(R.id.info_sub_saldo);
        TextView twReading = (TextView) rowView.findViewById(R.id.info_sub_reading);

        twOrganization.setText(iservices.get(position).getOrganization());

        if (iservices.get(position).getOnline())
            rowView.findViewById(R.id.info_cell).setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        else
            rowView.findViewById(R.id.info_cell).setBackgroundColor(context.getResources().getColor(R.color.colorOffline));

        Drawable[] imgs = {
                context.getResources().getDrawable(R.drawable.vodakryma),
                context.getResources().getDrawable(R.drawable.krymgazseti),
                context.getResources().getDrawable(R.drawable.krymenergo)
        };

        imageView.setImageDrawable(imgs[iservices.get(position).getImageId()]);

        if (iservices.get(position).getSubscriberNumber() == null) {
            twOrganization.setText(context.getString(R.string.err_list_service_title));
            twAddress.setText(context.getString(R.string.err_list_service_notice));
        } else {
            twSubName.setText(context.getString(R.string.owner) + ": " + iservices.get(position).getSubscriberName());

            twAddress.setText(context.getString(R.string.address) + ": " + iservices.get(position).getSubscriberAddress());

            twSubNumber.setText(context.getString(R.string.subscriber_number) + ": " + iservices.get(position).getSubscriberNumber());

            if (!iservices.get(position).getSaldo().equalsIgnoreCase("")) {
                String sign;

                if (Float.valueOf(iservices.get(position).getSaldo()) >= 0) {
                    sign = context.getString(R.string.info_service_debt);
                } else {
                    sign = context.getString(R.string.info_service_overpayment);
                    iservices.get(position).setSaldo(iservices.get(position).getSaldo().substring(1));
                }

                twSaldo.setText(sign + ": " + iservices.get(position).getSaldo() + " " + context.getString(R.string.currency));
            }

            twReading.setText(context.getString(R.string.info_service_amount) + ": " + iservices.get(position).getLastReading()
                    + " " + context.getString(R.string.info_service_on) + " " + iservices.get(position).getDate());
        }

        return rowView;
    }
}